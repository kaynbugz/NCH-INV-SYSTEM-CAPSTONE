package requestpanelsSYSTEM;

    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Window;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.FocusAdapter;
    import java.awt.event.FocusEvent;
    import java.awt.event.KeyAdapter;
    import java.awt.event.KeyEvent;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.PreparedStatement;
    import javax.swing.JOptionPane;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.beans.PropertyChangeEvent;
    import java.beans.PropertyChangeListener;
    import java.text.ParseException;
    import javax.swing.table.DefaultTableModel;
    import java.sql.ResultSet;
    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.time.format.DateTimeFormatter;
    import javax.swing.RowFilter;
    import javax.swing.table.TableRowSorter;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Calendar;
    import java.util.List;
    import java.util.Vector;
    import javax.swing.JFrame;
    import javax.swing.SwingUtilities;
    import javax.swing.table.TableModel;
    import java.sql.Time;  // For Time
    import java.util.UUID;
    import java.util.regex.Pattern;
    import javax.swing.AbstractCellEditor;
    import javax.swing.BoxLayout;
    import javax.swing.DefaultCellEditor;
    import javax.swing.JComboBox;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JScrollPane;
    import javax.swing.JTable;
    import javax.swing.JTextArea;
    import javax.swing.JTextField;
    import javax.swing.event.CellEditorListener;
    import javax.swing.event.ChangeEvent;
    import javax.swing.event.DocumentEvent;
    import javax.swing.event.DocumentListener;
    import javax.swing.event.TableModelEvent;
    import javax.swing.event.TableModelListener;
    import javax.swing.table.TableCellEditor;
    import javax.swing.table.TableColumn;
    import javax.swing.text.AttributeSet;
    import javax.swing.text.BadLocationException;
    import javax.swing.text.PlainDocument;
    import java.sql.Statement;
    import java.util.HashMap;
    import java.util.Map;
    import javax.swing.DefaultComboBoxModel;
    import javax.swing.DefaultListCellRenderer;
    import javax.swing.JList;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;

public class RequestMedicines extends javax.swing.JPanel {


    private boolean isProgrammaticChange = false;

    public RequestMedicines() {
       initComponents();
       
    // fill request date automatically
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    String todayStr = sdf.format(new java.util.Date());
    request_dates.setText(todayStr);
    request_dates.setEditable(false);

    // populate departments combo (use existing GUI combo box)
    String[] hospitalDepartments = {"Pharmacy", "Lab", "Surgery", "Administration", "Radiology"};
    departmentsCombo.removeAllItems(); // clear existing items
    for (String dept : hospitalDepartments) {
        departmentsCombo.addItem(dept);
    }

    // other initialization
    loadMedicineData();
    medschoice.addActionListener(e -> updateStockLabel());
    
    // Set combo box background and text color
    departmentsCombo.setBackground(Color.WHITE);
    departmentsCombo.setForeground(Color.BLACK);
    departmentsCombo.setOpaque(true);

    medschoice.setBackground(Color.WHITE);
    medschoice.setForeground(Color.BLACK);
    medschoice.setOpaque(true);
    
    request_dates.setBackground(Color.WHITE);
    request_dates.setForeground(Color.BLACK);
    request_dates.setOpaque(true);
    
    JTableHeader header = tablerequest.getTableHeader();
    header.setBackground(Color.BLACK);
    header.setForeground(Color.WHITE);
    ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    
    setupSearchByTextField();
                }
    
    
    private void setupSearchByTextField() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    List<String> fullMedicineList = new ArrayList<>();
    Map<String, String> batchMap = new HashMap<>();

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT GenericID, GenericName, BatchNo FROM medicines")) {

        while (rs.next()) {
            String id = rs.getString("GenericID");
            String name = rs.getString("GenericName");
            String batch = rs.getString("BatchNo");

            String display = id + " - " + name;
            fullMedicineList.add(display);

            if (batch != null) batchMap.put(display, batch);
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Select Medicine");
        for (String med : fullMedicineList) model.addElement(med);
        medschoice.setModel(model);
        medschoice.setSelectedItem("Select Medicine");

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load medicines.");
        return;
    }

    searchkey.getDocument().addDocumentListener(new DocumentListener() {
        private void filterMedicines() {
            SwingUtilities.invokeLater(() -> {
                String text = searchkey.getText().toLowerCase().trim();
                if (text.isEmpty()) {
                    medschoice.setSelectedItem("Select Medicine");
                    return;
                }

                for (String item : fullMedicineList) {
                    String[] parts = item.split(" - ");
                    String idPart = parts[0].toLowerCase();
                    String namePart = parts.length > 1 ? parts[1].toLowerCase() : "";
                    String batchPart = batchMap.getOrDefault(item, "").toLowerCase();

                    if (idPart.contains(text) || namePart.contains(text) || batchPart.contains(text)) {
                        medschoice.setSelectedItem(item);
                        return;
                    }
                }

                medschoice.setSelectedItem("Select Medicine");
            });
        }

        @Override public void insertUpdate(DocumentEvent e) { filterMedicines(); }
        @Override public void removeUpdate(DocumentEvent e) { filterMedicines(); }
        @Override public void changedUpdate(DocumentEvent e) { filterMedicines(); }
    });
}
  
   private void updateStockLabel() {
    String selected = (String) medschoice.getSelectedItem();
    if (selected == null || selected.equals("Select Medicine")) {
        stcksmeds.setText("0");
        return;
    }

    String[] parts = selected.split(" - ");
    if (parts.length < 2) return;

    String genericId = parts[0].trim(); // now treated as String

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";
    String query = "SELECT QuantityInStock FROM medicines WHERE GenericID = ?";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, genericId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int originalStock = rs.getInt("QuantityInStock");

                DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
                int reservedQty = 0;
                for (int i = 0; i < model.getRowCount(); i++) {
                    String id = model.getValueAt(i, 0).toString();
                    if (id.equalsIgnoreCase(genericId)) {
                        reservedQty += Integer.parseInt(model.getValueAt(i, 4).toString());
                    }
                }

                int remainingStock = originalStock - reservedQty;
                stcksmeds.setText(String.valueOf(Math.max(remainingStock, 0)));
            } else {
                stcksmeds.setText("0");
            }
        }
    } catch (SQLException e) {
        stcksmeds.setText("Error");
        e.printStackTrace();
    }
}

    private void loadMedicineData() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";
    String query = "SELECT GenericID, GenericName FROM medicines";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        medschoice.removeAllItems();
        medschoice.addItem("Select Medicine");
        while (rs.next()) {
            String id = rs.getString("GenericID");
            String name = rs.getString("GenericName");
            medschoice.addItem(id + " - " + name);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to load medicine data: " + e.getMessage());
    }
}

        
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        req_name = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablerequest = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        medschoice = new javax.swing.JComboBox<>();
        addreqitems = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        submit_req = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        request_dates = new javax.swing.JTextField();
        departmentsCombo = new javax.swing.JComboBox<>();
        stcksmeds = new javax.swing.JTextField();
        searchkey = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Requested Name:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Department:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Request Date:");

        tablerequest.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Generic ID", "Generic Name", "Units", "Description", "Quantity Request", "Mfg Date", "Exp Date", "BatchNo"
            }
        ));
        jScrollPane1.setViewportView(tablerequest);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Select Medicines:");

        medschoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medschoiceActionPerformed(evt);
            }
        });

        addreqitems.setBackground(new java.awt.Color(0, 0, 0));
        addreqitems.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addreqitems.setForeground(new java.awt.Color(255, 255, 255));
        addreqitems.setText("Add Request");
        addreqitems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addreqitemsMouseClicked(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Remove Request");

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Clear Request Items");

        submit_req.setBackground(new java.awt.Color(0, 0, 0));
        submit_req.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        submit_req.setForeground(new java.awt.Color(255, 255, 255));
        submit_req.setText("Sumbit Request");
        submit_req.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                submit_reqMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Quantity In Stocks:");

        departmentsCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        stcksmeds.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        stcksmeds.setForeground(new java.awt.Color(255, 51, 51));

        searchkey.setForeground(new java.awt.Color(255, 0, 0));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Search");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(req_name)
                                    .addComponent(departmentsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(37, 37, 37)
                                .addComponent(jLabel3)
                                .addGap(3, 3, 3)
                                .addComponent(request_dates, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(medschoice, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addGap(2, 2, 2)
                                .addComponent(searchkey, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addGap(2, 2, 2)
                                .addComponent(stcksmeds)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addreqitems))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(submit_req)))
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(req_name, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(request_dates, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(departmentsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(medschoice, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(addreqitems)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(stcksmeds, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(searchkey, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(submit_req))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void updateStockLabelAfterRequest(int medId, int originalStock) {
    DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
    int reservedQty = 0;

    // Sum up quantity already requested for this medicine
    for (int i = 0; i < model.getRowCount(); i++) {
        int id = Integer.parseInt(model.getValueAt(i, 0).toString());
        if (id == medId) {
            reservedQty += Integer.parseInt(model.getValueAt(i, 4).toString());
        }
    }

    // Update stcksmeds to remaining stock
    int remainingStock = originalStock - reservedQty;
    stcksmeds.setText(String.valueOf(Math.max(0, remainingStock)));
}
    
    private void addreqitemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addreqitemsMouseClicked
     String selected = (String) medschoice.getSelectedItem();
    if (selected == null || selected.equals("Select Medicine")) {
        JOptionPane.showMessageDialog(null, "Please select a medicine.");
        return;
    }

    String[] parts = selected.split(" - ");
    String medId = parts[0].trim(); // GenericID is VARCHAR

    // Prompt quantity
    String qtyStr = JOptionPane.showInputDialog("Enter quantity to request:");
    if (qtyStr == null) return;
    int qtyReq;
    try {
        qtyReq = Integer.parseInt(qtyStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid quantity.");
        return;
    }

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";
    String query = "SELECT * FROM medicines WHERE GenericID = ?";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, medId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int originalStock = rs.getInt("QuantityInStock");

                DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
                int reservedQty = 0;
                int existingRow = -1;
                for (int i = 0; i < model.getRowCount(); i++) {
                    String id = model.getValueAt(i, 0).toString();
                    if (id.equalsIgnoreCase(medId)) {
                        reservedQty = Integer.parseInt(model.getValueAt(i, 4).toString());
                        existingRow = i;
                        break;
                    }
                }

                int remainingStock = originalStock - reservedQty;
                if (qtyReq > remainingStock) {
                    JOptionPane.showMessageDialog(null, "Requested quantity exceeds available stock (" + remainingStock + ").");
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                String mfgDateStr = rs.getDate("MfgDate") != null ? sdf.format(rs.getDate("MfgDate")) : "";
                String expDateStr = rs.getDate("ExpDate") != null ? sdf.format(rs.getDate("ExpDate")) : "";

                if (existingRow != -1) {
                    model.setValueAt(reservedQty + qtyReq, existingRow, 4);
                } else {
                    model.addRow(new Object[]{
                        medId,
                        rs.getString("GenericName"),
                        rs.getString("Units"),
                        rs.getString("Description"),
                        qtyReq,
                        mfgDateStr,
                        expDateStr,
                        rs.getString("BatchNo")
                    });
                }

                stcksmeds.setText(String.valueOf(originalStock - (reservedQty + qtyReq)));

                DefaultTableCellRenderer genericIdRenderer = new DefaultTableCellRenderer();
                genericIdRenderer.setForeground(Color.RED);
                genericIdRenderer.setHorizontalAlignment(JLabel.CENTER);
                tablerequest.getColumnModel().getColumn(0).setCellRenderer(genericIdRenderer);

                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                for (int i = 1; i < tablerequest.getColumnCount(); i++) {
                    tablerequest.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                }

                medschoice.setSelectedItem("Select Medicine");
                searchkey.setText("");
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to fetch medicine data.");
    }
    }//GEN-LAST:event_addreqitemsMouseClicked

    private void submit_reqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submit_reqMouseClicked
        String reqName = req_name.getText().trim();
    String department = (String) departmentsCombo.getSelectedItem();
    String requestDateStr = request_dates.getText().trim();

    if (reqName.isEmpty() || department == null || department.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Request Name and Department are required.");
        return;
    }

    if (requestDateStr.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please enter a request date.");
        return;
    }

    java.util.Date requestDate;
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        requestDate = sdf.parse(requestDateStr);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(null, "Invalid date format. Please use MM-dd-yyyy.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "No items added to the request.");
        return;
    }

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        String requestId = generateNextRequestId(conn); // ✅ Custom ID

        String insertRequestSQL = """
            INSERT INTO requests (request_id, request_date, department, requested_by)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement psRequest = conn.prepareStatement(insertRequestSQL)) {
            psRequest.setString(1, requestId);
            psRequest.setDate(2, new java.sql.Date(requestDate.getTime()));
            psRequest.setString(3, department);
            psRequest.setString(4, reqName);
            psRequest.executeUpdate();
        }

        String insertItemsSQL = """
            INSERT INTO requested_items_medicines
            (request_id, GenericID, quantity_requested, approval_status, issuing_status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement psItems = conn.prepareStatement(insertItemsSQL)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String genericId = model.getValueAt(i, 0).toString();
                int qty = Integer.parseInt(model.getValueAt(i, 4).toString());

                if (!medicineExists(conn, genericId)) {
                    JOptionPane.showMessageDialog(null, "Error: GenericID " + genericId + " does not exist.");
                    conn.rollback();
                    return;
                }

                psItems.setString(1, requestId);         // ✅ request_id is now VARCHAR
                psItems.setString(2, genericId);
                psItems.setInt(3, qty);
                psItems.setString(4, "Requested");
                psItems.setNull(5, java.sql.Types.VARCHAR);

                psItems.addBatch();
            }
            psItems.executeBatch();
        }

        conn.commit();

        model.setRowCount(0);
        req_name.setText("");
        departmentsCombo.setSelectedIndex(0);
        request_dates.setText(new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()));

        JOptionPane.showMessageDialog(null, "Request submitted successfully! ID: " + requestId);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to submit the request: " + ex.getMessage());
    }
    }//GEN-LAST:event_submit_reqMouseClicked
    private String generateNextRequestId(Connection conn) throws SQLException {
        String prefix = "REQS-";
        String query = "SELECT MAX(request_id) FROM requests WHERE request_id LIKE 'REQS-%'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String lastId = rs.getString(1); // e.g., REQS-009
                if (lastId != null) {
                    int num = Integer.parseInt(lastId.substring(5)); // extract 009
                    return prefix + String.format("%03d", num + 1); // REQS-010
                }
            }
        }
        return prefix + "001"; // first ID
    }

    
    private void medschoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medschoiceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_medschoiceActionPerformed

        private boolean medicineExists(Connection conn, String genericId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicines WHERE GenericID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genericId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addreqitems;
    private javax.swing.JComboBox<String> departmentsCombo;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> medschoice;
    private javax.swing.JTextField req_name;
    private javax.swing.JTextField request_dates;
    private javax.swing.JTextField searchkey;
    private javax.swing.JTextField stcksmeds;
    private javax.swing.JButton submit_req;
    private javax.swing.JTable tablerequest;
    // End of variables declaration//GEN-END:variables
}
