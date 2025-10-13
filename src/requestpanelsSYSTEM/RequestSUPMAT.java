package requestpanelsSYSTEM;

    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
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
    import javax.swing.DefaultComboBoxModel;

public class RequestSUPMAT extends javax.swing.JPanel {
    
       private boolean isProgrammaticChange = false;
           
    public RequestSUPMAT() {
        initComponents();    
        loadSuppliesData();
        supmatchoice.addActionListener(e -> updateStockLabel());
        barcodesupmat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barcode = barcodesupmat.getText().trim();
                if (!barcode.isEmpty()) {
                    addItemByBarcode(barcode);
                    barcodesupmat.setText(""); // clear after adding
                }
            }
        });
        }
        
        private void loadSuppliesData() {
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
        String dbUser = "admin";
        String dbPass = "yeyel2025";

        String query = "SELECT TOP (1000) supmat_id, item_name FROM supplies_materials";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            supmatchoice.removeAllItems();
            supmatchoice.addItem("Select Item");

            while (rs.next()) {
                String supmatId = rs.getString("supmat_id");
                String itemName = rs.getString("item_name");
                String formatted = supmatId + " - " + itemName;
                supmatchoice.addItem(formatted);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load supplies data: " + e.getMessage());
        }
        }

        
     private void updateStockLabel() {
        String selectedItem = (String) supmatchoice.getSelectedItem();
        if (selectedItem == null || selectedItem.equals("Select Item")) {
            stcksupmats.setText("0");
            barcodesupmat.setText(""); // clear barcode
            return;
        }
        try {
            String[] parts = selectedItem.split(" - ");
            if (parts.length < 2) {
                JOptionPane.showMessageDialog(null, "Invalid item format.");
                return;
            }
            String supmatId = parts[0];
            String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
            String dbUser = "admin";
            String dbPass = "yeyel2025";
            String query = "SELECT qty_stocks, barcodes FROM supplies_materials WHERE supmat_id = ?";
            try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, supmatId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int stock = rs.getInt("qty_stocks");
                        String barcode = rs.getString("barcodes");
                        stcksupmats.setText(Integer.toString(stock));
                        barcodesupmat.setText(barcode != null ? barcode : "");
                    } else {
                        stcksupmats.setText("0");
                        barcodesupmat.setText("");
                    }
                }
            }
        } catch (Exception ex) {
            stcksupmats.setText("Error");
            barcodesupmat.setText("");
            JOptionPane.showMessageDialog(null, "Failed to fetch stock/barcode: " + ex.getMessage());
            ex.printStackTrace();
        }
        }
        
     
     private void addItemByBarcode(String barcode) {
            if (barcode == null || barcode.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Barcode is empty.");
                return;
            }

            String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
            String dbUser = "admin";
            String dbPass = "yeyel2025";

            String query = "SELECT * FROM supplies_materials WHERE barcodes = ?";

            try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, barcode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String supmatId = rs.getString("supmat_id");
                        String itemName = rs.getString("item_name");
                        int stock = rs.getInt("qty_stocks");

                        String comboText = supmatId + " - " + itemName;
                        supmatchoice.setSelectedItem(comboText);

                        stcksupmats.setText(String.valueOf(stock));

                        String qtyStr = JOptionPane.showInputDialog("Enter quantity for: " + itemName);
                        if (qtyStr == null) return;

                        int qtyReq;
                        try {
                            qtyReq = Integer.parseInt(qtyStr);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Invalid quantity.");
                            return;
                        }

                        if (qtyReq > stock) {
                            JOptionPane.showMessageDialog(null, "Requested quantity exceeds stock.");
                            return;
                        }

                        DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
                        // Add row with columns: supmat_id, item_name, description, units, qty_stocks, date_receive, supplier_name, type, remarks, barcodes, supmat
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                        String dateReceiveStr = "";
                        Date dateReceive = rs.getDate("date_receive");
                        if (dateReceive != null) {
                            dateReceiveStr = sdf.format(dateReceive);
                        }

                        model.addRow(new Object[]{
                            supmatId,
                            itemName,
                            rs.getString("description"),
                            rs.getString("units"),
                            qtyReq, // quantity requested (user input)
                            dateReceiveStr,
                            rs.getString("supplier_name"),
                            rs.getString("type"),
                            rs.getString("remarks"),
                            barcode,
                            rs.getString("supmat")
                        });

                    } else {
                        JOptionPane.showMessageDialog(null, "No item found with barcode: " + barcode);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }
        }

        private void addRequestButtonClicked(java.awt.event.MouseEvent evt) {
          String selected = (String) supmatchoice.getSelectedItem();

            if (selected == null || selected.equals("Select Item")) {
                JOptionPane.showMessageDialog(null, "Please select an item.");
                return;
            }

            String[] parts = selected.split(" - ");
            String supmatId = parts[0];

            String qtyStr = JOptionPane.showInputDialog("Enter quantity to request:");
            if (qtyStr == null) return; // User cancelled input

            int qtyReq;
            try {
                qtyReq = Integer.parseInt(qtyStr.trim());
                if (qtyReq <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantity must be greater than zero.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid quantity.");
                return;
            }

            int currentStock;
            try {
                currentStock = Integer.parseInt(stcksupmats.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid or empty stock value.");
                return;
            }

            if (qtyReq > currentStock) {
                JOptionPane.showMessageDialog(null, "Requested quantity exceeds available stock.");
                return;
            }

            String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
            String dbUser = "admin";
            String dbPass = "yeyel2025";

            String query = "SELECT supmat_id, item_name, description, units, qty_stocks, date_receive, supplier_name, type, remarks, barcodes, supmat FROM supplies_materials WHERE supmat_id = ?";

            try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, supmatId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                        String dateReceiveStr = "";
                        Date dateReceive = rs.getDate("date_receive");
                        if (dateReceive != null) {
                            dateReceiveStr = sdf.format(dateReceive);
                        }

                        DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
                        model.addRow(new Object[]{
                            rs.getString("supmat_id"),
                            rs.getString("item_name"),
                            rs.getString("description"),
                            rs.getString("units"),
                            qtyReq, // user requested quantity
                            rs.getInt("qty_stocks"),
                            dateReceiveStr,
                            rs.getString("supplier_name"),
                            rs.getString("type"),
                            rs.getString("remarks"),
                            rs.getString("barcodes")
                        });

                        supmatchoice.setSelectedItem("Select Item");
                        stcksupmats.setText("0");
                        barcodesupmat.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Selected item not found in database.");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to fetch item data.");
            }
        }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        req_name = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        departments = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        request_dates = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablerequest = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        supmatchoice = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        barcodesupmat = new javax.swing.JTextField();
        addreqitems = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        submit_req = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        stcksupmats = new javax.swing.JLabel();

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
                "SUPMAT_ID", "Item Name", "Description", "Units", "Qty Stocks", "Supplier Name", "Type", "Remarks", "Barcode"
            }
        ));
        jScrollPane1.setViewportView(tablerequest);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Select Items:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Barcode:");

        addreqitems.setText("Add Request");
        addreqitems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addreqitemsMouseClicked(evt);
            }
        });

        jButton2.setText("Remove Request");

        jButton3.setText("Clear Request Items");

        submit_req.setBackground(new java.awt.Color(0, 0, 0));
        submit_req.setForeground(new java.awt.Color(255, 255, 255));
        submit_req.setText("Sumbit Request");
        submit_req.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                submit_reqMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Quantity In Stocks:");

        stcksupmats.setOpaque(true);
        stcksupmats.setPreferredSize(new java.awt.Dimension(64, 22));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(req_name)
                    .addComponent(departments, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(request_dates, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supmatchoice, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barcodesupmat, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stcksupmats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(addreqitems))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jButton2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(submit_req))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(request_dates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(req_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(departments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stcksupmats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(supmatchoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(barcodesupmat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton3)
                            .addComponent(submit_req)))
                    .addComponent(addreqitems))
                .addGap(21, 21, 21))
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
       String selected = (String) supmatchoice.getSelectedItem();
        if (selected == null || selected.equals("Select Item")) {
            JOptionPane.showMessageDialog(null, "Please select an item.");
            return;
        }

        String[] parts = selected.split(" - ");
        String supmatId = parts[0];

        String qtyStr = JOptionPane.showInputDialog("Enter quantity to request:");
        if (qtyStr == null) return;

        int qtyReq;
        try {
            qtyReq = Integer.parseInt(qtyStr.trim());
            if (qtyReq <= 0) {
                JOptionPane.showMessageDialog(null, "Quantity must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid quantity.");
            return;
        }

        int currentStock;
        try {
            currentStock = Integer.parseInt(stcksupmats.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid or empty stock value.");
            return;
        }

        if (qtyReq > currentStock) {
            JOptionPane.showMessageDialog(null, "Requested quantity exceeds available stock.");
            return;
        }

        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
        String dbUser = "admin";
        String dbPass = "yeyel2025";
        String query = "SELECT supmat_id, item_name, description, units, supplier_name, type, remarks, barcodes, qty_stocks FROM supplies_materials WHERE supmat_id = ?";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, supmatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DefaultTableModel model = (DefaultTableModel) tablerequest.getModel();
                    model.addRow(new Object[]{
                        rs.getString("supmat_id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getString("units"),
                        qtyReq, // quantity requested by user
                        rs.getString("supplier_name"),
                        rs.getString("type"),
                        rs.getString("remarks"),
                        rs.getString("barcodes")
                    });

                    // reset fields
                    supmatchoice.setSelectedItem("Select Item");
                    stcksupmats.setText("0");
                    barcodesupmat.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Selected item not found in database.");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to fetch item data.");
        }
    }//GEN-LAST:event_addreqitemsMouseClicked

    private void submit_reqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submit_reqMouseClicked
        
        }

        private boolean supplyExists(Connection conn, String supmatId) throws SQLException {
            String query = "SELECT 1 FROM supplies_materials WHERE supmat_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, supmatId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
    }//GEN-LAST:event_submit_reqMouseClicked



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addreqitems;
    private javax.swing.JTextField barcodesupmat;
    private javax.swing.JTextField departments;
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
    private javax.swing.JTextField req_name;
    private com.toedter.calendar.JDateChooser request_dates;
    private javax.swing.JLabel stcksupmats;
    private javax.swing.JButton submit_req;
    private javax.swing.JComboBox<String> supmatchoice;
    private javax.swing.JTable tablerequest;
    // End of variables declaration//GEN-END:variables
}
