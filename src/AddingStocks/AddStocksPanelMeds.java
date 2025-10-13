package AddingStocks;

    import requestpanelsSYSTEM.*;
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


public class AddStocksPanelMeds extends javax.swing.JPanel {
        private boolean isProgrammaticChange = false;

        private Map<String, String> supplierTypeMap = new HashMap<>(); // name → type


        public AddStocksPanelMeds() {
           initComponents();

        // fill request date automatically
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String todayStr = sdf.format(new java.util.Date());
        request_dates.setText(todayStr);
        request_dates.setEditable(false);

        // other initialization
        loadMedicineData();

        // Set combo box background and text color
        suppliercombo.setBackground(Color.WHITE);
        suppliercombo.setForeground(Color.BLACK);
        suppliercombo.setOpaque(true);

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

        loadSupplierComboWithTypes();     // Load supplier names and types
        setupSupplierCategoryAutoFill();     // Attach listener for auto-fill
      }

    private void setupSupplierCategoryAutoFill() {
    suppliercombo.addActionListener(e -> {
        String selectedSupplier = (String) suppliercombo.getSelectedItem();
        if (selectedSupplier != null && supplierTypeMap.containsKey(selectedSupplier)) {
            String supplierType = supplierTypeMap.get(selectedSupplier);

            // Now query the categories table for matching cat_type
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                    "admin", "yeyel2025");
                 PreparedStatement pst = conn.prepareStatement(
                     "SELECT cat_name FROM categories WHERE cat_type = ?")) {

                pst.setString(1, supplierType);
                try (ResultSet rs = pst.executeQuery()) {
                    categtype.removeAllItems(); // Clear previous categories

                    boolean found = false;
                    while (rs.next()) {
                        String catName = rs.getString("cat_name");
                        categtype.addItem(catName);
                        found = true;
                    }

                    if (!found) {
                        categtype.addItem("No category found");
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading category: " + ex.getMessage());
            }
        }
    });
}
 

    private void loadSupplierComboWithTypes() {
   {
    suppliercombo.removeAllItems();
    supplierTypeMap.clear();

    String sql = """
        SELECT s.supplier_name, s.type
        FROM suppliers s
        WHERE s.type IN (
            SELECT cat_type FROM categories WHERE cat_type = 'medicines'
        )
    """;

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            String name = rs.getString("supplier_name");
            String type = rs.getString("type");

            suppliercombo.addItem(name);
            supplierTypeMap.put(name, type);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading medicine suppliers: " + e.getMessage());
    }
   }
    }
    
   private void setupSearchByTextField() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    List<String> fullMedicineList = new ArrayList<>();
    Map<String, String> batchMap = new HashMap<>();

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
             "SELECT GenericID, GenericName, BatchNo FROM medicines WHERE QuantityInStock <= 10")) {

        while (rs.next()) {
            String id = rs.getString("GenericID"); // ✅ Use VARCHAR
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
        JOptionPane.showMessageDialog(this, "Failed to load low stock medicines.");
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

    

    private void loadMedicineData() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";
    String query = "SELECT GenericID, GenericName FROM medicines WHERE QuantityInStock <= 10";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        medschoice.removeAllItems();
        medschoice.addItem("Select Medicine");
        while (rs.next()) {
            String id = rs.getString("GenericID"); // ✅ Use VARCHAR
            String name = rs.getString("GenericName");
            medschoice.addItem(id + " - " + name);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Failed to load low stock medicine data: " + e.getMessage());
    }
}

    
        
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        requested_by = new javax.swing.JTextField();
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
        request_dates = new javax.swing.JTextField();
        suppliercombo = new javax.swing.JComboBox<>();
        searchkey = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        categtype = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Requested By:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Supplier");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Requested Date:");

        tablerequest.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Generic ID", "Generic Name", "Quantity In Stock", "Quantity To Add", "BatchNo", "Mfg Date", "Exp Date", "Remarks"
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
        addreqitems.setText("Add Stock");
        addreqitems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addreqitemsMouseClicked(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Remove");

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Clear");

        submit_req.setBackground(new java.awt.Color(0, 0, 0));
        submit_req.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        submit_req.setForeground(new java.awt.Color(255, 255, 255));
        submit_req.setText("Submit Stocks");
        submit_req.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                submit_reqMouseClicked(evt);
            }
        });

        searchkey.setForeground(new java.awt.Color(255, 0, 0));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Search");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Type");

        categtype.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(suppliercombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(categtype, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(81, 81, 81)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(request_dates, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(requested_by, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(2, 2, 2)
                            .addComponent(medschoice, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel5)
                            .addGap(2, 2, 2)
                            .addComponent(searchkey, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addreqitems))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jButton2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(submit_req))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(requested_by, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(request_dates, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(suppliercombo)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(categtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(medschoice, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(addreqitems)
                    .addComponent(searchkey, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(submit_req))
                .addContainerGap(11, Short.MAX_VALUE))
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

    
    
    private void addreqitemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addreqitemsMouseClicked
    String selected = (String) medschoice.getSelectedItem();
    if (selected == null || selected.equals("Select Medicine")) {
        JOptionPane.showMessageDialog(null, "Please select a medicine.");
        return;
    }

    String[] parts = selected.split(" - ");
    String genericId = parts[0].trim(); // Use GenericID directly

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String medQuery = """
        SELECT GenericID, GenericName, QuantityInStock, BatchNo, MfgDate, ExpDate
        FROM medicines WHERE GenericID = ?
    """;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement psMed = conn.prepareStatement(medQuery)) {

        psMed.setString(1, genericId);
        try (ResultSet rs = psMed.executeQuery()) {
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Medicine not found.");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();

            // Check if already added
            for (int i = 0; i < model.getRowCount(); i++) {
                String existingId = model.getValueAt(i, 0).toString();
                if (existingId.equalsIgnoreCase(genericId)) {
                    JOptionPane.showMessageDialog(null, "Medicine already added.");
                    return;
                }
            }

            // Format dates
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String mfgDate = rs.getDate("MfgDate") != null ? sdf.format(rs.getDate("MfgDate")) : "";
            String expDate = rs.getDate("ExpDate") != null ? sdf.format(rs.getDate("ExpDate")) : "";

            // Add new row with Quantity To Add defaulted to 0
            model.addRow(new Object[]{
                rs.getString("GenericID"),         // Generic ID
                rs.getString("GenericName"),       // Generic Name
                rs.getInt("QuantityInStock"),      // Quantity In Stock
                0,                                 // Quantity To Add
                rs.getString("BatchNo"),           // Batch No
                mfgDate,                           // Mfg Date
                expDate,                           // Exp Date
                ""                                 // Remarks (blank)
            });

            // Center all columns
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < tablerequest.getColumnCount(); i++) {
                tablerequest.getColumnModel().getColumn(i).setCellRenderer(center);
            }

            // Reset selection
            medschoice.setSelectedItem("Select Medicine");
            searchkey.setText("");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
    }
    }//GEN-LAST:event_addreqitemsMouseClicked
    
    
    
    private void submit_reqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submit_reqMouseClicked
    
    
    DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "No medicine requests to submit.");
        return;
    }

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String getMaxIdSQL = "SELECT MAX(CAST(SUBSTRING(request_id, 6, LEN(request_id)) AS INT)) AS max_id FROM stock_request_medicines";
    String insertSQL = """
        INSERT INTO stock_request_medicines
        (request_id, generic_id, quantity_requested, requested_by, request_date, request_time, remarks, stock_status, supplier_name, supplier_type)
        VALUES (?, ?, ?, ?, GETDATE(), CONVERT(TIME, GETDATE()), ?, 'Pending Stocks', ?, ?)
    """;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(getMaxIdSQL);
         PreparedStatement ps = conn.prepareStatement(insertSQL)) {

        int nextId = 1;
        if (rs.next() && rs.getInt("max_id") > 0) {
            nextId = rs.getInt("max_id") + 1;
        }
        String requestId = String.format("REQS-%03d", nextId); // Shared ID for all rows

        int validCount = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String genericId = model.getValueAt(i, 0).toString();
            int quantityRequested = Integer.parseInt(model.getValueAt(i, 3).toString());
            if (quantityRequested <= 0) {
                JOptionPane.showMessageDialog(null, "Quantity must be greater than 0 for " + genericId);
                continue;
            }

            String remarks = model.getValueAt(i, 7).toString();
            String requestedBy = System.getProperty("user.name"); // Replace with actual user if needed
            String supplierName = suppliercombo.getSelectedItem().toString();
            String supplierType = supplierTypeMap.getOrDefault(supplierName, "Unknown");

            ps.setString(1, requestId);
            ps.setString(2, genericId);
            ps.setInt(3, quantityRequested);
            ps.setString(4, requestedBy);
            ps.setString(5, remarks);
            ps.setString(6, supplierName);
            ps.setString(7, supplierType);

            ps.addBatch();
            validCount++;
        }

        if (validCount > 0) {
            int[] results = ps.executeBatch();
            JOptionPane.showMessageDialog(null, results.length + " stock request(s) submitted successfully with Request ID: " + requestId);
            model.setRowCount(0);
            medschoice.setSelectedItem("Select Medicine");
            searchkey.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "No valid requests submitted.");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
    
    }//GEN-LAST:event_submit_reqMouseClicked
    
    }
    private void medschoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medschoiceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_medschoiceActionPerformed

    private boolean medicineExists(Connection conn, int genericId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicines WHERE GenericID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genericId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addreqitems;
    private javax.swing.JComboBox<String> categtype;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> medschoice;
    private javax.swing.JTextField request_dates;
    private javax.swing.JTextField requested_by;
    private javax.swing.JTextField searchkey;
    private javax.swing.JButton submit_req;
    private javax.swing.JComboBox<String> suppliercombo;
    private javax.swing.JTable tablerequest;
    // End of variables declaration//GEN-END:variables
}
