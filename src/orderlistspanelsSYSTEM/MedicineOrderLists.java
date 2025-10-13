
package orderlistspanelsSYSTEM;

    import dashboardSYSTEM.ItemsManagement;
    import dashboardSYSTEM.SessionManager;
    import java.awt.BorderLayout;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.sql.Statement;
    import java.awt.Color;
    import java.awt.Component;
    import java.awt.FlowLayout;
    import java.awt.Font;
    import java.awt.Frame;
    import java.awt.Window;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.KeyAdapter;
    import java.awt.event.KeyEvent;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.sql.Connection;
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
    import javax.swing.BorderFactory;
    import javax.swing.DefaultCellEditor;
    import javax.swing.JButton;
    import javax.swing.JComboBox;
    import javax.swing.JDialog;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JTable;
    import javax.swing.JTextField;
    import javax.swing.SwingUtilities;
    import javax.swing.border.Border;
    import javax.swing.event.DocumentEvent;
    import javax.swing.event.DocumentListener;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;
    import javax.swing.table.TableColumn;
    import javax.swing.table.TableColumnModel;
    import javax.swing.table.TableModel;

public class MedicineOrderLists extends javax.swing.JPanel {

   private ItemsManagement itemsPanel;

    public MedicineOrderLists(ItemsManagement itemsPanel) {
    this.itemsPanel = itemsPanel;
    initComponents();
    loadActiveSuppliersToComboBox();
    colorwhitebg();

        // Auto-fill logged-in user
    String loggedInUser = SessionManager.currentUserFullName; 
    setupOrderByNameField(loggedInUser);
        // Auto-fill date & time
    setupDateTimeFields();               // fill date & initial time
    startAutoTimeUpdate();               // keep time updating
    
    // 🟢 Table row height
    tableitems.setRowHeight(40);
    tableitems.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    // 🟢 Center text in selected columns
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    tableitems.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Medicine Name
    tableitems.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Quantity
    tableitems.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Units
    tableitems.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // description

    // 🟢 Description column (wrap + fixed width)
    TableColumn descColumn = tableitems.getColumnModel().getColumn(3);
    descColumn.setPreferredWidth(110);
    descColumn.setCellRenderer(new TextAreaRenderer()); // Custom renderer that wraps text

    
    String[] unitsList = {"pcs", "boxes", "kg", "mg", "liters"};

    JComboBox<String> unitComboBox = new JComboBox<>(unitsList);
    unitComboBox.setEditable(true);

    JTextField editor = (JTextField) unitComboBox.getEditor().getEditorComponent();

    editor.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            String input = editor.getText().trim().toLowerCase();

            // Save caret position
            int caretPos = editor.getCaretPosition();

            // Collect matches
            java.util.List<String> startsWith = new java.util.ArrayList<>();
            java.util.List<String> contains = new java.util.ArrayList<>();

            for (String unit : unitsList) {
                String lowerUnit = unit.toLowerCase();
                if (lowerUnit.startsWith(input)) {
                    startsWith.add(unit);
                } else if (lowerUnit.contains(input)) {
                    contains.add(unit);
                }
            }

            // Temporarily remove listeners to avoid recursion
            ActionListener[] listeners = unitComboBox.getActionListeners();
            for (ActionListener l : listeners) unitComboBox.removeActionListener(l);

            unitComboBox.hidePopup();
            unitComboBox.removeAllItems();

            for (String match : startsWith) unitComboBox.addItem(match);
            for (String match : contains) unitComboBox.addItem(match);

            unitComboBox.setSelectedItem(input);

            SwingUtilities.invokeLater(() -> {
                editor.setText(input); // ensure text stays
                editor.setCaretPosition(Math.min(caretPos, editor.getText().length()));

                if (unitComboBox.isShowing() && unitComboBox.getItemCount() > 0) {
                    unitComboBox.showPopup();
                }
            });

            for (ActionListener l : listeners) unitComboBox.addActionListener(l);
        }
    });

    tableitems.getColumnModel().getColumn(0).setCellRenderer(new MedicineRenderer());


    TableColumn unitColumn = tableitems.getColumnModel().getColumn(2);
    unitColumn.setCellEditor(new DefaultCellEditor(unitComboBox));

    // 🟢 Quantity column with + / – editor and centered text
    TableColumn qtyColumn = tableitems.getColumnModel().getColumn(1);
    qtyColumn.setCellEditor(new QuantityCellEditor());
    
    sts.setText("In Progress");
    
    typ.setBackground(Color.WHITE); // ComboBox background
    ctgry.setBackground(Color.WHITE); // ComboBox background

    
          // After your table is initialized:
        JTableHeader header = tableitems.getTableHeader();

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.BLACK);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 10));
                label.setOpaque(true);  // Important to show background color
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });
        
}

   private void setupOrderByNameField(String loggedInUser) {
        if (loggedInUser != null && !loggedInUser.trim().isEmpty()) {
            orderbynames.setText(loggedInUser.trim());
            orderbynames.setEditable(false); // hindi na pwedeng baguhin
        } else {
            orderbynames.setText("");
            orderbynames.setEditable(true);
        }
    }
    
   
private void setupDateTimeFields() {
    // Current date in yy/MM/dd format
    SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
    String currentDate = dateFormat.format(new Date());
    date.setText(currentDate);
    date.setEditable(false); // optional: make read-only

    // Current time in hh:mm a format (AM/PM)
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    String currentTime = timeFormat.format(new Date());
    time.setText(currentTime);
    time.setEditable(false); // optional: make read-only
}
      
      public void clearSupplierFields() {
    // Clear combo box
    suppliername.setSelectedIndex(-1); // keep this if suppliername is still JComboBox

    // Clear text fields
    typ.setText("");
    ctgry.setText("");
    sts.setText("");
    date.setText("");   // since date is JTextField
    time.setText("");

    // Clear JTable
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    model.setRowCount(0);
}
      
      public void colorwhitebg(){
           Color whiteBG = Color.WHITE;
        Border flatBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY); // para di nawawala
        
                            // For date field
                date.setEditable(true);          // must be true for white background
                date.setBackground(Color.WHITE); 
                date.setBorder(flatBorder);
                date.setOpaque(true);
                date.setFocusable(false);        // prevents user from typing

                // For time field
                time.setEditable(true);          
                time.setBackground(Color.WHITE); 
                time.setBorder(flatBorder);
                time.setOpaque(true);
                time.setFocusable(false);        
        
      }
      
     private List<String> supplierList = new ArrayList<>();

        private void loadActiveSuppliersToComboBox() {
            supplierList.clear();
    suppliername.removeAllItems();
    suppliername.setEditable(true);
    suppliername.addItem("Select Supplier");

    resetTypeAndCategory();

    // 🔌 Load suppliers from database
    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        String sql = "SELECT supplier_name FROM suppliers WHERE status = 'Active' AND type = 'Medicines'";
        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("supplier_name");
                supplierList.add(name);
                suppliername.addItem(name);
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading suppliers: " + ex.getMessage());
        ex.printStackTrace();
    }

    // 🎯 Supplier selection logic
    suppliername.addActionListener(e -> {
        String selected = (String) suppliername.getSelectedItem();
        if (selected == null || selected.equals("Select Supplier") || !supplierList.contains(selected)) {
            resetTypeAndCategory();
            return;
        }

        Color whiteBG = Color.WHITE;
        Border flatBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY); // para di nawawala

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025")) {

            // 🔹 Get supplier type
            String type = null;
            String sqlType = "SELECT type FROM suppliers WHERE supplier_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlType)) {
                stmt.setString(1, selected);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        type = rs.getString("type");
                    }
                }
            }

            if (type != null) {
                // Type field
                typ.setText(type);
                typ.setEditable(false);
                typ.setBackground(whiteBG);
                typ.setBorder(flatBorder);

                // Category field
                String category = null;
                String sqlCat = "SELECT TOP 1 cat_name FROM categories WHERE cat_type = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCat)) {
                    stmt.setString(1, type);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            category = rs.getString("cat_name");
                        }
                    }
                }
                ctgry.setText(category != null ? category : "No category found");
                ctgry.setEditable(false);
                ctgry.setBackground(whiteBG);
                ctgry.setBorder(flatBorder);
                
                colorwhitebg();

            
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading supplier details: " + ex.getMessage());
        }
    });
}

    private void resetTypeAndCategory() {
    Color whiteBG = Color.WHITE;

    typ.setText("");
    typ.setEditable(false);
    typ.setBackground(whiteBG);
    typ.setOpaque(true); // para sigurado mag-apply
    typ.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    ctgry.setText("");
    ctgry.setEditable(false);
    ctgry.setBackground(whiteBG);
    ctgry.setOpaque(true); // para sigurado mag-apply
    ctgry.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    
    colorwhitebg();
}

            // 🟢 Call this once in your panel initialization
        private void startAutoTimeUpdate() {
            // Timer updates every 1 second (1000 ms)
            new javax.swing.Timer(1000, e -> {
                String currentTime = new SimpleDateFormat("hh:mm a").format(new Date());
                time.setText(currentTime);
            }).start();
        }

    private void loadCategoriesByType(String type) {
    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        String sql = "SELECT cat_name FROM categories WHERE cat_type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();

            StringBuilder categoriesList = new StringBuilder();
            while (rs.next()) {
                categoriesList.append(rs.getString("cat_name")).append(", ");
            }
            if (categoriesList.length() > 0) {
                categoriesList.setLength(categoriesList.length() - 2); // remove last comma
            }

            // If ctgry is now a JTextField
            ctgry.setText(categoriesList.toString());

            rs.close();
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error loading categories: " + ex.getMessage());
    }
    colorwhitebg();
}

    private void displaySupplierType() {
    String selectedSupplier = (String) suppliername.getSelectedItem();
    if (selectedSupplier == null) return;

    try (Connection conn = DriverManager.getConnection(
        "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
        "admin",
        "yeyel2025")) {

        String sql = "SELECT type FROM suppliers WHERE supplier_name = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, selectedSupplier);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String supplierType = rs.getString("type");
                    typ.setText(supplierType); // now sets the text field
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error fetching supplier type: " + ex.getMessage());
        ex.printStackTrace();
    }
    colorwhitebg();
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableitems = new javax.swing.JTable();
        addrows = new javax.swing.JButton();
        submitbtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        suppliername = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        time = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        closebtn = new javax.swing.JLabel();
        sts = new javax.swing.JLabel();
        deleterows = new javax.swing.JButton();
        clearall = new javax.swing.JButton();
        typ = new javax.swing.JTextField();
        ctgry = new javax.swing.JTextField();
        date = new javax.swing.JTextField();
        orderbynames = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tableitems.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tableitems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Generic Names", "Quantity", "Units", "Description"
            }
        ));
        jScrollPane1.setViewportView(tableitems);

        addrows.setBackground(new java.awt.Color(0, 0, 0));
        addrows.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addrows.setForeground(new java.awt.Color(255, 255, 255));
        addrows.setText("+ Add Item");
        addrows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addrowsActionPerformed(evt);
            }
        });

        submitbtn.setBackground(new java.awt.Color(0, 0, 0));
        submitbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        submitbtn.setForeground(new java.awt.Color(255, 255, 255));
        submitbtn.setText("Submit Order");
        submitbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                submitbtnMouseClicked(evt);
            }
        });
        submitbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitbtnActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/school-supplies (1).png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Medicines Orders ");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 33, 33));
        jLabel3.setText("Supplier Name :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Category :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Type :");

        suppliername.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Date Ordered :");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Time Ordered :");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Order By :");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Status :");

        closebtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/close (1).png"))); // NOI18N
        closebtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closebtnMouseClicked(evt);
            }
        });

        sts.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sts.setText(" ");

        deleterows.setBackground(new java.awt.Color(0, 0, 0));
        deleterows.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        deleterows.setForeground(new java.awt.Color(255, 255, 255));
        deleterows.setText("- Delete Item");
        deleterows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleterowsActionPerformed(evt);
            }
        });

        clearall.setBackground(new java.awt.Color(0, 0, 0));
        clearall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clearall.setForeground(new java.awt.Color(255, 255, 255));
        clearall.setText("Clear");
        clearall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearallActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closebtn)
                .addGap(4, 4, 4))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addrows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleterows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(submitbtn)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(ctgry)
                            .addComponent(typ)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(suppliername, 0, 251, Short.MAX_VALUE))
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(orderbynames)
                            .addComponent(date)
                            .addComponent(time)
                            .addComponent(clearall, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel6))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(15, 15, 15))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(closebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(suppliername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderbynames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(typ, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ctgry, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sts)
                            .addComponent(clearall)))
                    .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleterows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addrows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addrowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrowsActionPerformed
           DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
            Object[] newRow = { "", "", "" };  // Item Name, Quantity, Units
            model.addRow(newRow);
    }//GEN-LAST:event_addrowsActionPerformed

    private void submitbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitbtnActionPerformed
    {
      String supplierName = (suppliername.getSelectedItem() != null) ? suppliername.getSelectedItem().toString().trim() : "";
    if (supplierName.equals("Select Supplier")) supplierName = "";

    String category = ctgry.getText().trim();
    if (category.equals(" ")) category = "";

    String type = typ.getText().trim();
    if (type.equals(" ")) type = "";

    String orderedBy = orderbynames.getText().trim();
    if (orderedBy.equalsIgnoreCase("Select User")) orderedBy = "";

    String dateText = date.getText().trim();
    java.sql.Date dateOrdered = null;
    if (!dateText.isEmpty()) {
        try {
            java.util.Date parsedDate = new SimpleDateFormat("yy/MM/dd").parse(dateText);
            dateOrdered = new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use yy/MM/dd.");
            return;
        }
    }

    String timeOrdered = time.getText().trim();
    String approvalStatus = "Pending Approval";
    String receivingStatus = "Pending Inspection";

    if (supplierName.isEmpty() || orderedBy.isEmpty() || category.isEmpty() || type.isEmpty()
            || dateOrdered == null || timeOrdered.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please fill in all required fields before submitting.");
        return;
    }

    if (tableitems.isEditing()) tableitems.getCellEditor().stopCellEditing();

    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    boolean hasValidItem = false;

    for (int i = 0; i < model.getRowCount(); i++) {
        Object itemNameObj = model.getValueAt(i, 0);
        Object quantityObj = model.getValueAt(i, 1);
        if (itemNameObj != null && quantityObj != null) {
            String itemName = itemNameObj.toString().trim();
            String qtyStr = quantityObj.toString().trim();
            try {
                int qty = Integer.parseInt(qtyStr);
                if (!itemName.isEmpty() && qty > 0) {
                    hasValidItem = true;
                    break;
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    if (!hasValidItem) {
        JOptionPane.showMessageDialog(null, "Please add at least one valid item with a quantity greater than 0.");
        return;
    }

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        // ✅ Generate next Order ID (ORD-001 format)
        String orderId = null;
        String getMaxSQL = "SELECT MAX(CAST(SUBSTRING(order_id, 5, LEN(order_id)) AS INT)) AS max_id FROM orders_info";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(getMaxSQL)) {
            int nextNum = 1;
            if (rs.next() && rs.getInt("max_id") > 0) {
                nextNum = rs.getInt("max_id") + 1;
            }
            orderId = String.format("ORD-%03d", nextNum); // ✅ 3-digit format (ORD-001)
        }

        // ✅ Check for duplicates
        String checkSQL = "SELECT COUNT(*) FROM orders_info WHERE order_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
            checkStmt.setString(1, orderId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "Duplicate Order ID detected. Please try again.");
                    conn.rollback();
                    return;
                }
            }
        }

        // ✅ Insert into orders_info
        String insertOrderSQL = "INSERT INTO orders_info " +
                "(order_id, supplier_name, ordered_by, category, type, approval_status, receiving_status, date_ordered, time_ordered) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL)) {
            pstmt.setString(1, orderId);
            pstmt.setString(2, supplierName);
            pstmt.setString(3, orderedBy);
            pstmt.setString(4, category);
            pstmt.setString(5, type);
            pstmt.setString(6, approvalStatus);
            pstmt.setString(7, receivingStatus);
            pstmt.setDate(8, dateOrdered);
            pstmt.setString(9, timeOrdered);
            pstmt.executeUpdate();
        }

        // ✅ Insert items into order_items
        String insertItemSQL = "INSERT INTO order_items " +
                "(order_id, item_name, quantity, units, dcs, approval_status, receiving_status) " +
                "VALUES (?, ?, ?, COALESCE(?, 'pcs'), ?, ?, ?)";
        try (PreparedStatement itemPstmt = conn.prepareStatement(insertItemSQL)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String itemName = (model.getValueAt(i, 0) != null) ? model.getValueAt(i, 0).toString().trim() : "";
                String qtyStr = (model.getValueAt(i, 1) != null) ? model.getValueAt(i, 1).toString().trim() : "";
                String unit = (model.getValueAt(i, 2) != null) ? model.getValueAt(i, 2).toString().trim() : "pcs";
                String description = (model.getValueAt(i, 3) != null) ? model.getValueAt(i, 3).toString().trim() : "";

                if (itemName.isEmpty() || qtyStr.isEmpty()) continue;

                try {
                    int quantity = Integer.parseInt(qtyStr);
                    if (quantity <= 0) continue;

                    itemPstmt.setString(1, orderId);
                    itemPstmt.setString(2, itemName);
                    itemPstmt.setInt(3, quantity);
                    itemPstmt.setString(4, unit);
                    itemPstmt.setString(5, description);
                    itemPstmt.setString(6, approvalStatus);
                    itemPstmt.setString(7, receivingStatus);
                    itemPstmt.addBatch();
                } catch (NumberFormatException ignored) {}
            }
            itemPstmt.executeBatch();
        }

        conn.commit();
        JOptionPane.showMessageDialog(null, "Order " + orderId + " submitted successfully!");
        ItemsManagement itemsPanel = new ItemsManagement();
        itemsPanel.loadPendingOrders();

        model.setRowCount(0);
        clearall();
        sts.setText("Pending Approval");

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving order: " + e.getMessage());
    }
    }//GEN-LAST:event_submitbtnActionPerformed
    }
  
    
    private void submitbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitbtnMouseClicked


    }//GEN-LAST:event_submitbtnMouseClicked
    
    
    private void closebtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closebtnMouseClicked
        // Get the parent window (JFrame) of this panel
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose(); // Close the window
        }
    }//GEN-LAST:event_closebtnMouseClicked

    private void deleterowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleterowsActionPerformed
          DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
        int[] selectedRows = tableitems.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one row to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected row(s)?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Delete from bottom to top to prevent shifting issues
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model.removeRow(selectedRows[i]);
            }
        }
    }//GEN-LAST:event_deleterowsActionPerformed

    private void clearall() {
        // Reset supplier combo box
    suppliername.setSelectedIndex(0);

    // Reset type and category
    typ.setText("");
    typ.setEditable(false);
    typ.setBackground(Color.WHITE);
    typ.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    ctgry.setText("");
    ctgry.setEditable(false);
    ctgry.setBackground(Color.WHITE);
    ctgry.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    // Set current date and time
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");     // yy/MM/dd format
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");   // 12-hour format with AM/PM

    date.setText(dateFormat.format(now));
    date.setEditable(true); 
    date.setBackground(Color.WHITE);
    date.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    date.setOpaque(true);
    date.setFocusable(false);

    time.setText(timeFormat.format(now));
    time.setEditable(true); 
    time.setBackground(Color.WHITE);
    time.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    time.setOpaque(true);
    time.setFocusable(false);

    // Clear the table
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    model.setRowCount(0);
    }

    private void clearallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearallActionPerformed
        clearall();
    }//GEN-LAST:event_clearallActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addrows;
    private javax.swing.JButton clearall;
    private javax.swing.JLabel closebtn;
    private javax.swing.JTextField ctgry;
    private javax.swing.JTextField date;
    private javax.swing.JButton deleterows;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField orderbynames;
    private javax.swing.JLabel sts;
    private javax.swing.JButton submitbtn;
    private javax.swing.JComboBox<String> suppliername;
    private javax.swing.JTable tableitems;
    private javax.swing.JTextField time;
    private javax.swing.JTextField typ;
    // End of variables declaration//GEN-END:variables

   
}
