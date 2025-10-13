
package orderlistspanelsSYSTEM;

    import dashboardSYSTEM.ItemsManagement;
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
    import java.awt.Frame;
    import java.awt.Window;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.FocusAdapter;
    import java.awt.event.FocusEvent;
    import java.awt.event.KeyAdapter;
    import java.awt.event.KeyEvent;
    import java.awt.event.KeyListener;
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
    import javax.swing.DefaultCellEditor;
    import javax.swing.JButton;
    import javax.swing.JComboBox;
    import javax.swing.JDialog;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JTable;
    import javax.swing.JTextField;
    import javax.swing.SwingUtilities;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.TableColumn;
    import javax.swing.table.TableModel;

public class SMOrderLists extends javax.swing.JPanel {

   
    private ItemsManagement itemsPanel;
    public SMOrderLists(ItemsManagement itemsPanel) {
        this.itemsPanel = itemsPanel; // ✅ Save reference passed from caller
        initComponents();
        loadActiveSuppliersToComboBox();
         
         date.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (date.getDate() != null) {
                updatetime();
            }
        }
        });
  

         String[] unitsList = {"pcs", "boxes", "kg", "mg", "liters"};

    tableitems.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tableitems.rowAtPoint(e.getPoint());
        int col = tableitems.columnAtPoint(e.getPoint());

        if (col == 2 && row >= 0) { // Assuming column 2 is for units
            JComboBox<String> unitCombo = new JComboBox<>(unitsList);
            JLabel unitLabel = new JLabel("Select Unit:");

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new FlowLayout());
            inputPanel.add(unitLabel);
            inputPanel.add(unitCombo);

            // OK and Cancel buttons with custom styling
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            Color bgGreen = Color.decode("#4CAF50");
            Color fgWhite = Color.WHITE;

            okButton.setBackground(bgGreen);
            okButton.setForeground(fgWhite);
            cancelButton.setBackground(bgGreen);
            cancelButton.setForeground(fgWhite);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            // Dialog setup
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(tableitems), "Select Unit", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.add(inputPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(tableitems);

            // Button actions
            okButton.addActionListener(ev -> {
                String selectedUnit = (String) unitCombo.getSelectedItem();
                if (selectedUnit != null) {
                    tableitems.setValueAt(selectedUnit, row, col); // Update the cell
                }
                dialog.dispose();
            });

            cancelButton.addActionListener(ev -> dialog.dispose());

            dialog.setVisible(true);
            }
        }
    });        
    }
    
    
     
      private void updatetime() {
        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a"); 
        String currentTime = timeformat.format(new Date());
        time.setText(currentTime); 
         }
      
      public void clearSupplierFields() {
           // Reset combo boxes to "Select ..." or first option
        suppliername.setSelectedIndex(0);
        typ.setSelectedIndex(0);
        ctgry.setSelectedIndex(0);
        orderedby.setSelectedIndex(0);

        // Clear text fields
        sts.setText("");
        time.setText("");

        // Clear date chooser
        date.setDate(null);

        // Clear JTable rows
        DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
        model.setRowCount(0);
        }
   
        private List<String> supplierList = new ArrayList<>();

    private void loadActiveSuppliersToComboBox() {
            suppliername.setEditable(true);
            suppliername.setMaximumRowCount(8); // 👈 prevents scroll when items ≤ 8
            supplierList.clear();
            suppliername.removeAllItems();

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                    "admin", "yeyel2025")) {

                String sql = "SELECT supplier_name FROM suppliers WHERE status = 'Active' AND (type = 'Supplies' OR type = 'Materials')";
                try (PreparedStatement pst = conn.prepareStatement(sql);
                     ResultSet rs = pst.executeQuery()) {

                    while (rs.next()) {
                        String supplierName = rs.getString("supplier_name");
                        supplierList.add(supplierName);
                        suppliername.addItem(supplierName);
                    }

                    sts.setText("Pending");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading suppliers: " + ex.getMessage());
                ex.printStackTrace();
            }

            // Filter while typing
            JTextField editor = (JTextField) suppliername.getEditor().getEditorComponent();
            editor.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String input = editor.getText().trim();
                    if (input.isEmpty()) {
                        resetSupplierComboBox(); // Show all when input is cleared
                    } else {
                        filterSupplierComboBox(input);
                    }
                }
            });

            // Supplier selection
            suppliername.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedSupplier = (String) suppliername.getSelectedItem();
                    if (selectedSupplier == null || selectedSupplier.isEmpty()) {
                        resetTypeAndCategory();
                        return;
                    }

                    try (Connection conn = DriverManager.getConnection(
                            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                            "admin", "yeyel2025")) {

                        String sql = "SELECT type FROM suppliers WHERE supplier_name = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, selectedSupplier);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                String supplierType = rs.getString("type");
                                typ.removeAllItems();
                                typ.addItem(supplierType);
                                loadCategoriesByType(supplierType);
                            } else {
                                resetTypeAndCategory();
                            }
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error loading supplier type: " + ex.getMessage());
                    }
                }
            });
        }

private void filterSupplierComboBox(String input) {
            ActionListener[] listeners = suppliername.getActionListeners();
            for (ActionListener l : listeners) suppliername.removeActionListener(l);

            suppliername.removeAllItems();

            List<String> matches = new ArrayList<>();
            for (String supplier : supplierList) {
                if (supplier.toLowerCase().contains(input.toLowerCase())) {
                    if (supplier.toLowerCase().startsWith(input.toLowerCase())) {
                        matches.add(0, supplier); // Best match at top
                    } else {
                        matches.add(supplier);
                    }
                }
            }

            for (String match : matches) {
                suppliername.addItem(match);
            }

            suppliername.setSelectedItem(input);

            for (ActionListener l : listeners) suppliername.addActionListener(l);
            SwingUtilities.invokeLater(() -> suppliername.showPopup());

            if (matches.isEmpty()) {
                resetTypeAndCategory();
            }
        }

private void resetSupplierComboBox() {
            ActionListener[] listeners = suppliername.getActionListeners();
            for (ActionListener l : listeners) suppliername.removeActionListener(l);

            suppliername.removeAllItems();
            for (String supplier : supplierList) {
                suppliername.addItem(supplier);
            }

            suppliername.setSelectedItem("");

            for (ActionListener l : listeners) suppliername.addActionListener(l);
            resetTypeAndCategory();
}

    private void resetTypeAndCategory() {
            typ.removeAllItems();
            typ.addItem("Select Type");
            ctgry.removeAllItems();
            ctgry.addItem("Select Category");
    }

     private void loadCategoriesByType(String type) {
        ctgry.removeAllItems(); // Clear existing items

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025")) {

            String sql = "SELECT cat_name FROM categories WHERE cat_type = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, type);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    ctgry.addItem(rs.getString("cat_name"));
                }

                rs.close();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading categories: " + ex.getMessage());
        }
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
                        typ.setSelectedItem(supplierType); // Automatically sets the value
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching supplier type: " + ex.getMessage());
            ex.printStackTrace();
        }
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
        ctgry = new javax.swing.JComboBox<>();
        typ = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        date = new com.toedter.calendar.JDateChooser();
        time = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        closebtn = new javax.swing.JLabel();
        sts = new javax.swing.JLabel();
        deleterows = new javax.swing.JButton();
        clearall = new javax.swing.JButton();
        orderedby = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tableitems.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tableitems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Items Name", "Quantity", "Units", "Dscrp/Purpose"
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
        jLabel2.setText("Supplies & Materials Orders");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 33, 33));
        jLabel3.setText("Supplier Name :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Category :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Type :");

        suppliername.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        ctgry.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        typ.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

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
                .addComponent(jLabel2)
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(167, 167, 167))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ctgry, javax.swing.GroupLayout.Alignment.LEADING, 0, 212, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(sts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(suppliername, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(typ, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(83, 83, 83)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7)
                            .addComponent(orderedby, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(time, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(date, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(clearall))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(suppliername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderedby, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(2, 2, 2)
                        .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addGap(2, 2, 2)
                        .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearall))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(2, 2, 2)
                        .addComponent(typ, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(2, 2, 2)
                        .addComponent(ctgry, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sts))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleterows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addrows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addrowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrowsActionPerformed
           DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
            Object[] newRow = { "", "", "" };  // Item Name, Quantity, Units
            model.addRow(newRow);
    }//GEN-LAST:event_addrowsActionPerformed

    private void submitbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitbtnActionPerformed
    String supplierName = (suppliername.getSelectedItem() != null) ? suppliername.getSelectedItem().toString() : "";
    if (supplierName.equals("Select Supplier")) supplierName = "";

    String category = (ctgry.getSelectedItem() != null) ? ctgry.getSelectedItem().toString() : "";
    if (category.equals("Select Category")) category = "";

    String type = (typ.getSelectedItem() != null) ? typ.getSelectedItem().toString() : "";
    if (type.equals("Select Type")) type = "";

    String status = sts.getText().trim();

    String orderedBy = (orderedby.getSelectedItem() != null) ? orderedby.getSelectedItem().toString() : "";
    if (orderedBy.equals("Select Ordered By")) orderedBy = "";

    Date dateOrdered = (date.getDate() != null) ? date.getDate() : null;
    String timeOrdered = time.getText().trim();

    if (supplierName.isEmpty() || orderedBy.isEmpty() || category.isEmpty() || type.isEmpty()
            || status.isEmpty() || dateOrdered == null || timeOrdered.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please fill in all required fields before submitting.");
        return;
    }

    if (tableitems.isEditing()) {
        tableitems.getCellEditor().stopCellEditing();
    }

    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    boolean hasValidItem = false;
    for (int i = 0; i < model.getRowCount(); i++) {
        Object itemNameObj = model.getValueAt(i, 0);
        Object quantityObj = model.getValueAt(i, 1);
        if (itemNameObj != null && quantityObj != null) {
            String itemName = itemNameObj.toString().trim();
            String qtyStr = quantityObj.toString().trim();
            if (!itemName.isEmpty() && !qtyStr.isEmpty()) {
                try {
                    int qty = Integer.parseInt(qtyStr);
                    if (qty > 0) {
                        hasValidItem = true;
                        break;
                    }
                } catch (NumberFormatException e) {}
            }
        }
    }

    if (!hasValidItem) {
        JOptionPane.showMessageDialog(null, "Please add at least one valid item with a quantity greater than 0.");
        return;
    }

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        conn.setAutoCommit(false);

        // Generate custom order code
        String orderCode = null;
        try (Statement seqStmt = conn.createStatement()) {
            seqStmt.executeUpdate("UPDATE order_sequence SET last_number = last_number + 1");
            try (ResultSet rs = seqStmt.executeQuery("SELECT last_number FROM order_sequence")) {
                if (rs.next()) {
                    int number = rs.getInt("last_number");
                    orderCode = String.format("ORD-%04d", number);
                }
            }
        }

        if (orderCode == null) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Failed to generate order code.");
            return;
        }

        // Insert into orders_info
        String insertOrderSQL = "INSERT INTO orders_info (order_code, supplier_name, ordered_by, category, type, status, date_ordered, time_ordered) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL)) {
            pstmt.setString(1, orderCode);
            pstmt.setString(2, supplierName);
            pstmt.setString(3, orderedBy);
            pstmt.setString(4, category);
            pstmt.setString(5, type);
            pstmt.setString(6, status);
            pstmt.setDate(7, new java.sql.Date(dateOrdered.getTime()));
            pstmt.setString(8, timeOrdered);
            pstmt.executeUpdate();
        }

        // Insert order items using order_code
        String insertItemSQL = "INSERT INTO order_items (order_code, item_name, quantity, units, dcs) VALUES (?, ?, ?, COALESCE(?, 'pcs'), ?)";
        try (PreparedStatement itemPstmt = conn.prepareStatement(insertItemSQL)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                Object itemNameObj = model.getValueAt(i, 0);
                Object quantityObj = model.getValueAt(i, 1);
                Object unitObj = model.getValueAt(i, 2);
                Object descriptionObj = model.getValueAt(i, 3);

                if (itemNameObj == null || quantityObj == null) continue;

                String itemName = itemNameObj.toString().trim();
                String qtyStr = quantityObj.toString().trim();

                if (itemName.isEmpty() || qtyStr.isEmpty()) continue;

                try {
                    int quantity = Integer.parseInt(qtyStr);
                    if (quantity <= 0) continue;

                    String unit = (unitObj != null && !unitObj.toString().isEmpty()) ? unitObj.toString() : "pcs";
                    String description = (descriptionObj != null) ? descriptionObj.toString().trim() : "";

                    itemPstmt.setString(1, orderCode);
                    itemPstmt.setString(2, itemName);
                    itemPstmt.setInt(3, quantity);
                    itemPstmt.setString(4, unit);
                    itemPstmt.setString(5, description);
                    itemPstmt.addBatch();
                } catch (NumberFormatException ex) {
                    continue;
                }
            }
            itemPstmt.executeBatch();
        }

        conn.commit();
        JOptionPane.showMessageDialog(null, "Order " + orderCode + " submitted successfully!");

        // Reload table
        String selectItemsSQL = "SELECT item_name, quantity, units, dcs FROM order_items WHERE order_code = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectItemsSQL)) {
            selectStmt.setString(1, orderCode);
            try (ResultSet rs = selectStmt.executeQuery()) {
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.setColumnIdentifiers(new String[]{
                        "Item Name", "Quantity", "Units", "Description/Purpose"
                });
                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    int quantity = rs.getInt("quantity");
                    String units = rs.getString("units");
                    String description = rs.getString("dcs");
                    tableModel.addRow(new Object[]{itemName, quantity, units, description});
                }
                tableitems.setModel(tableModel);
            }
        }

        clearall();

        if (itemsPanel != null) {
            SwingUtilities.invokeLater(() -> itemsPanel.loadOrderSummary());
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
    
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

    private void clearall(){
        suppliername.setSelectedIndex(0); // Reset to the first item (usually a placeholder "Select Supplier")
        typ.setSelectedIndex(0); // Reset to the first item (e.g., "Select Type")
        ctgry.setSelectedIndex(0); // Reset to the first item (e.g., "Select Category")
        orderedby.setSelectedIndex(0); // Clear the ordered by text field
        time.setText(""); // Clear the time field
        date.setDate(null); // Reset the date chooser to null (no date selected)
        DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
        model.setRowCount(0);  // Clears all rows in the table
        ctgry.removeAllItems();  // Ensure all items are removed from the category combo box
        typ.removeAllItems();  // Ensure all items are removed from the category combo box
    }
    
    private void clearallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearallActionPerformed
        clearall();
    }//GEN-LAST:event_clearallActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addrows;
    private javax.swing.JButton clearall;
    private javax.swing.JLabel closebtn;
    private javax.swing.JComboBox<String> ctgry;
    private com.toedter.calendar.JDateChooser date;
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
    private javax.swing.JComboBox<String> orderedby;
    private javax.swing.JLabel sts;
    private javax.swing.JButton submitbtn;
    private javax.swing.JComboBox<String> suppliername;
    private javax.swing.JTable tableitems;
    private javax.swing.JTextField time;
    private javax.swing.JComboBox<String> typ;
    // End of variables declaration//GEN-END:variables
}
