
package medicinespanelsSYSTEM;

    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Dialog;
    import java.awt.Font;
import java.awt.Frame;
    import java.awt.GridLayout;
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
import javax.swing.JDialog;
    import javax.swing.SwingConstants;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;

    
public class PendingOrdersPanel extends javax.swing.JPanel {

        private boolean isProgrammaticChange = false;
        private JPanel mainPanel;

        public PendingOrdersPanel() {
            initComponents();  

            approval_orders.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            updateApproveButtonState();
        }
    });

            
        // After your table is initialized:
        JTableHeader header = approval_orders.getTableHeader();

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.BLACK);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setOpaque(true);  // Important to show background color
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });
   }
    
    private void updateApproveButtonState() {
    int selectedRow = approval_orders.getSelectedRow();

    if (selectedRow == -1) {
        approvedbtn.setEnabled(false);
        return;
    }

    String status = approval_orders.getValueAt(selectedRow, 4).toString();
    if ("Approved".equalsIgnoreCase(status) || "Received".equalsIgnoreCase(status)) {
        approvedbtn.setEnabled(false); // disable kapag approved
    } else {
        approvedbtn.setEnabled(true); // enable lang kapag pending
    }
}
    
    

        public void loadPendingOrderItems(String searchOrderId) {
              if (searchOrderId == null || searchOrderId.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please enter a request id.");
        return;
    }

    // make text fields read-only
    Color whiteBG = Color.WHITE;
    order_id.setEditable(false); 
    order_id.setBackground(whiteBG);
    supplier_name.setEditable(false); 
    supplier_name.setBackground(whiteBG);
    d_ordered.setEditable(false); 
    d_ordered.setBackground(whiteBG);
    odered_by.setEditable(false); 
    odered_by.setBackground(whiteBG);

    // custom table model
    CustomTableModel model = new CustomTableModel(
            new String[]{"Item ID", "Generic Name", "Quantity Ordered", "Description", "Approval Status"}, 0);

    String sqlItems = """
        SELECT 
            oi.item_id,
            oi.item_name,
            oi.quantity AS total_quantity_ordered,
            oi.dcs AS description,
            oi.approval_status
        FROM order_items oi
        WHERE CAST(oi.order_id AS VARCHAR) = ?
        ORDER BY oi.approval_status DESC, oi.item_name
    """;

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement pst = conn.prepareStatement(sqlItems)) {

        pst.setString(1, searchOrderId);

        try (ResultSet rs = pst.executeQuery()) {
            boolean hasResult = false;
            boolean anyPending = false;
            boolean allApproved = true;
            boolean allRejected = true;

            while (rs.next()) {
                hasResult = true;
                String approvalStatus = rs.getString("approval_status");

                if ("Pending Approval".equalsIgnoreCase(approvalStatus)) {
                    anyPending = true;
                    allApproved = false;
                    allRejected = false;
                } else if ("Approved".equalsIgnoreCase(approvalStatus)) {
                    allRejected = false;
                } else if ("Rejected".equalsIgnoreCase(approvalStatus)) {
                    allApproved = false;
                }

                // set order info fields (only once)
                if (order_id.getText().isEmpty()) {
                    order_id.setText(searchOrderId);
                    try (PreparedStatement pstOrder = conn.prepareStatement(
                            "SELECT supplier_name, ordered_by, date_ordered FROM orders_info WHERE order_id = ?")) {
                        pstOrder.setString(1, searchOrderId);
                        try (ResultSet rsOrder = pstOrder.executeQuery()) {
                            if (rsOrder.next()) {
                                supplier_name.setText(rsOrder.getString("supplier_name"));
                                odered_by.setText(rsOrder.getString("ordered_by"));
                                Date d = rsOrder.getDate("date_ordered");
                                if (d != null) {
                                    d_ordered.setText(new SimpleDateFormat("yy/MM/dd").format(d));
                                }
                            }
                        }
                    }
                }

                model.addRow(new Object[]{
                        rs.getString("item_id"), // ✅ FIXED: use getString instead of getInt
                        rs.getString("item_name"),
                        rs.getInt("total_quantity_ordered"),
                        rs.getString("description"),
                        approvalStatus
                    });
            }

            approval_orders.setModel(model);

            if (!hasResult) {
                JOptionPane.showMessageDialog(null, "No items found for request id: " + searchOrderId);
                return;
            }

            // determine parent status
            String parentStatus;
            if (anyPending) {
                parentStatus = "Pending Approval";
            } else if (allApproved) {
                parentStatus = "Approved";
            } else if (allRejected) {
                parentStatus = "Rejected";
            } else {
                parentStatus = "Partially Approved";
            }

            // update parent table
            try (PreparedStatement pstUpdate = conn.prepareStatement(
                    "UPDATE orders_info SET approval_status = ? WHERE order_id = ?")) {
                pstUpdate.setString(1, parentStatus);
                pstUpdate.setString(2, searchOrderId);
                pstUpdate.executeUpdate();
            }

            // disable buttons if no pending
            boolean allDone = !anyPending;
            approval_orders.setEnabled(!allDone);
            approvedbtn.setEnabled(false);
            rejectbtn.setEnabled(false);
            approvedall.setEnabled(!allDone);
            rejectedall.setEnabled(!allDone);

        }

        // Renderer for approval status
        int approvalCol = 4;
        approval_orders.getColumnModel().getColumn(approvalCol).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = String.valueOf(value);
                if ("Pending Approval".equalsIgnoreCase(status)) {
                    c.setForeground(Color.RED);
                } else if ("Approved".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(0, 128, 0));
                } else if ("Rejected".equalsIgnoreCase(status)) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // center-align other columns except approval status
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < approval_orders.getColumnCount() - 1; i++) {
            approval_orders.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // listener to enable/disable approve/reject per row
        approval_orders.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && approval_orders.getSelectedRow() != -1) {
                int row = approval_orders.getSelectedRow();
                String status = approval_orders.getValueAt(row, 4).toString();
                if ("Approved".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) {
                    approvedbtn.setEnabled(false);
                    rejectbtn.setEnabled(false);
                } else {
                    approvedbtn.setEnabled(true);
                    rejectbtn.setEnabled(true);
                }
            }
        });

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
    }
                 }
    // custom table model with editable toggle
class CustomTableModel extends DefaultTableModel {
    private boolean editable = true;

    public CustomTableModel(String[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public void setEditable(boolean flag) {
        this.editable = flag;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return editable && super.isCellEditable(row, column);
    }
}



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        approval_orders = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        approvedbtn = new javax.swing.JButton();
        rejectbtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        order_id = new javax.swing.JTextField();
        supplier_name = new javax.swing.JTextField();
        d_ordered = new javax.swing.JTextField();
        odered_by = new javax.swing.JTextField();
        approvedall = new javax.swing.JButton();
        rejectedall = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Order ID :");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Supplier Name:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Date Ordered :");

        approval_orders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Medicine ID", "Generic Name", "Quantites Ordered", "Description", "Status"
            }
        ));
        jScrollPane1.setViewportView(approval_orders);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Orderd By :");

        approvedbtn.setBackground(new java.awt.Color(0, 0, 0));
        approvedbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        approvedbtn.setForeground(new java.awt.Color(255, 255, 255));
        approvedbtn.setText("Approved Orders");
        approvedbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                approvedbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                approvedbtnMouseEntered(evt);
            }
        });
        approvedbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approvedbtnActionPerformed(evt);
            }
        });

        rejectbtn.setBackground(new java.awt.Color(0, 0, 0));
        rejectbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rejectbtn.setForeground(new java.awt.Color(255, 255, 255));
        rejectbtn.setText("Reject Orders");
        rejectbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rejectbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rejectbtnMouseEntered(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/box (3).png"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Pending Orders");

        order_id.setForeground(new java.awt.Color(255, 51, 51));
        order_id.setSelectionColor(new java.awt.Color(0, 0, 0));
        order_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                order_idActionPerformed(evt);
            }
        });

        supplier_name.setForeground(new java.awt.Color(255, 51, 51));
        supplier_name.setSelectionColor(new java.awt.Color(0, 0, 0));

        d_ordered.setForeground(new java.awt.Color(255, 51, 51));

        odered_by.setForeground(new java.awt.Color(255, 51, 51));

        approvedall.setBackground(new java.awt.Color(0, 0, 0));
        approvedall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        approvedall.setForeground(new java.awt.Color(255, 255, 255));
        approvedall.setText("Approved All");
        approvedall.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                approvedallMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                approvedallMouseEntered(evt);
            }
        });

        rejectedall.setBackground(new java.awt.Color(0, 0, 0));
        rejectedall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rejectedall.setForeground(new java.awt.Color(255, 255, 255));
        rejectedall.setText("Rejeceted All");
        rejectedall.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rejectedallMouseClicked(evt);
            }
        });
        rejectedall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rejectedallActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(supplier_name, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(order_id, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(d_ordered)
                    .addComponent(odered_by, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                .addGap(23, 23, 23))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(approvedall, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rejectedall, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(approvedbtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rejectbtn))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(order_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(d_ordered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(supplier_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(odered_by, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rejectbtn)
                    .addComponent(approvedbtn)
                    .addComponent(rejectedall)
                    .addComponent(approvedall, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

  
 private void saveApprovedItem(int itemId, int orderId, String approvedBy, java.sql.Date approvedDate, String remarks) {
       String fetchTypeSql = "SELECT type FROM orders_info WHERE order_id = ?";
    String insertMedsSql = "INSERT INTO approved_orders_medicines (item_id, order_id, approved_by, approved_date, remarks) VALUES (?, ?, ?, ?, ?)";
    String insertSupMatSql = "INSERT INTO approved_orders_supmat (order_id, approved_by, approved_date, remarks) VALUES (?, ?, ?, ?)";
    String updateItemStatusSql = "UPDATE order_items SET status = 'approved' WHERE item_id = ?";
    String checkPendingItemsSql = "SELECT COUNT(*) FROM order_items WHERE order_id = ? AND status != 'approved'";
    String updateOrderStatusSql = "UPDATE orders_info SET status = 'approved' WHERE order_id = ?";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        conn.setAutoCommit(false);

        try (PreparedStatement fetchTypeStmt = conn.prepareStatement(fetchTypeSql)) {
            fetchTypeStmt.setInt(1, orderId);
            ResultSet rs = fetchTypeStmt.executeQuery();

            if (rs.next()) {
                String orderType = rs.getString("type");

                PreparedStatement insertStmt;
                int inserted = 0;

                if ("Medicines".equalsIgnoreCase(orderType)) {
                    insertStmt = conn.prepareStatement(insertMedsSql);
                    insertStmt.setInt(1, itemId);
                    insertStmt.setInt(2, orderId);
                    insertStmt.setString(3, approvedBy);
                    insertStmt.setDate(4, approvedDate);
                    insertStmt.setString(5, remarks);
                    inserted = insertStmt.executeUpdate();
                } else {
                    insertStmt = conn.prepareStatement(insertSupMatSql);
                    insertStmt.setInt(1, orderId);
                    insertStmt.setString(2, approvedBy);
                    insertStmt.setDate(3, approvedDate);
                    insertStmt.setString(4, remarks);
                    inserted = insertStmt.executeUpdate();
                }

                // Update item status
                try (PreparedStatement updateItemStatusStmt = conn.prepareStatement(updateItemStatusSql)) {
                    updateItemStatusStmt.setInt(1, itemId);
                    int updated = updateItemStatusStmt.executeUpdate();

                    // If both insert and update succeed
                    if (inserted > 0 && updated > 0) {
                        // Check for any remaining unapproved items
                        try (PreparedStatement checkPendingStmt = conn.prepareStatement(checkPendingItemsSql)) {
                            checkPendingStmt.setInt(1, orderId);
                            try (ResultSet rsPending = checkPendingStmt.executeQuery()) {
                                if (rsPending.next()) {
                                    int pendingCount = rsPending.getInt(1);
                                    if (pendingCount == 0) {
                                        try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderStatusSql)) {
                                            updateOrderStmt.setInt(1, orderId);
                                            updateOrderStmt.executeUpdate();
                                        }
                                    }
                                }
                            }
                        }

                        conn.commit();
                        JOptionPane.showMessageDialog(null, "Item approved successfully!");
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(null, "Approval failed. No records updated.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Order type not found.");
            }

        } catch (SQLException ex) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Error during approval: " + ex.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
    }
    }

    


 

    private void approvedbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedbtnMouseClicked
    int selectedRow = approval_orders.getSelectedRow();
    if (selectedRow == -1) return;

    String status = approval_orders.getValueAt(selectedRow, 4).toString();
    if ("Approved".equalsIgnoreCase(status)) {
        approvedbtn.setEnabled(false);
        rejectbtn.setEnabled(false);
        return;
    }

    // ✅ FIXED: itemId is now String
    String itemId = approval_orders.getValueAt(selectedRow, 0).toString().trim();
    String orderId = order_id.getText().trim();

    // ✅ FIXED: constructor must accept String itemId
    ApprovalOrdersPanel panel = new ApprovalOrdersPanel(itemId, orderId);

    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                                 "Approve Item",
                                 Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    loadPendingOrderItems(orderId);
    updateApproveButtonState();

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        updateOrderApprovalStatus(conn, orderId); // ✅ pass as String
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error updating order status: " + e.getMessage());
    }
    }

    private void updateOrderApprovalStatus(Connection conn, String orderId) throws SQLException {
    String query = "SELECT COUNT(*) AS totalItems, " +
                   "SUM(CASE WHEN approval_status = 'Approved' THEN 1 ELSE 0 END) AS approvedItems " +
                   "FROM order_items WHERE order_id = ?";

    try (PreparedStatement pst = conn.prepareStatement(query)) {
        pst.setString(1, orderId); // ✅ FIXED
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("totalItems");
                int approved = rs.getInt("approvedItems");

                String newStatus;
                if (approved == total && total > 0) {
                    newStatus = "Approved";
                } else if (approved > 0 && approved < total) {
                    newStatus = "Partially Approved";
                } else {
                    newStatus = "Pending Approval";
                }

                try (PreparedStatement updatePst = conn.prepareStatement(
                        "UPDATE orders_info SET approval_status = ? WHERE order_id = ?")) {
                    updatePst.setString(1, newStatus); // ✅ FIXED
                    updatePst.setString(2, orderId);   // ✅ FIXED
                    updatePst.executeUpdate();
                }
            }
        }
     }
    }//GEN-LAST:event_approvedbtnMouseClicked

    private void saveRejectedItem(String orderId, String itemId, String rejectedBy, java.sql.Date rejectedDate, String rejectionReason) {
    String insertMedsSql = "INSERT INTO rejected_orders_medicines (order_id, item_id, rejected_by, rejected_date, rejection_reason) VALUES (?, ?, ?, ?, ?)";
    String updateOrderSql = "UPDATE orders_info SET approval_status = 'Rejected', receiving_status = 'Rejected' WHERE order_id = ?";
    String updateItemSql = "UPDATE order_items SET approval_status = 'Rejected', receiving_status = 'Rejected' WHERE order_id = ? AND item_id = ?";

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        try {
            try (PreparedStatement insertStmt = conn.prepareStatement(insertMedsSql)) {
                insertStmt.setString(1, orderId);
                insertStmt.setString(2, itemId);
                insertStmt.setString(3, rejectedBy);
                insertStmt.setDate(4, rejectedDate);
                insertStmt.setString(5, rejectionReason);
                insertStmt.executeUpdate();
            }

            try (PreparedStatement updateItemStmt = conn.prepareStatement(updateItemSql)) {
                updateItemStmt.setString(1, orderId);
                updateItemStmt.setString(2, itemId);
                updateItemStmt.executeUpdate();
            }

            String checkSql = "SELECT COUNT(*) AS cnt FROM order_items WHERE order_id = ? AND approval_status <> 'Rejected'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, orderId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt("cnt") == 0) {
                    try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderSql)) {
                        updateOrderStmt.setString(1, orderId);
                        updateOrderStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(null, "Item rejected successfully.");
        } catch (Exception ex) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Error during rejection: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        ex.printStackTrace();
    }
}

    private void rejectbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectbtnMouseClicked
    {
    int selectedRow = approval_orders.getSelectedRow();
    if (selectedRow == -1) return;

    String orderId = order_id.getText().trim(); // ✅ FIXED
    if (orderId.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Invalid Order ID format.");
        return;
    }

    String itemId = approval_orders.getValueAt(selectedRow, 0).toString().trim(); // ✅ FIXED
    if (itemId.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Invalid item ID for the selected row.");
        return;
    }

    RejectedOrdersPanel panel = new RejectedOrdersPanel();
    panel.setRejectedBy(System.getProperty("user.name"));

    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    JDialog dialog = new JDialog(parentWindow, "Reject Item", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);

    panel.getConfirmBtn().addActionListener(e -> {
        String rejectedBy = panel.getRejectedBy();
        String rejectedDate = panel.getRejectedDate();
        String rejectionReason = panel.getRemarks();

        if (rejectedBy.isEmpty() || rejectedDate.isEmpty() || rejectionReason.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please fill out all required fields.");
            return;
        }

        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(rejectedDate);
            saveRejectedItem(orderId, itemId, rejectedBy, sqlDate, rejectionReason);

            loadPendingOrderItems(orderId); // ✅ no need to convert
            dialog.dispose();
        } catch (IllegalArgumentException ex2) {
            JOptionPane.showMessageDialog(dialog, "Invalid date format. Expected yyyy-MM-dd");
        }
    });

    panel.getCancelBtn().addActionListener(e -> dialog.dispose());
    dialog.setVisible(true);
    }//GEN-LAST:event_rejectbtnMouseClicked
    }
    
    private void approvedbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedbtnMouseEntered
   
    }//GEN-LAST:event_approvedbtnMouseEntered

    private void order_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_order_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_order_idActionPerformed

    private void approvedbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approvedbtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_approvedbtnActionPerformed

    private void approvedallMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedallMouseClicked
    String orderId = order_id.getText().trim(); // ✅ FIXED: treat as String

    if (orderId.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Invalid Order ID format.");
        return;
    }

    // Count only items with status "Pending Approval"
    int pendingCount = 0;
    for (int i = 0; i < approval_orders.getRowCount(); i++) {
        String status = approval_orders.getValueAt(i, 4).toString().trim();
        if ("Pending Approval".equalsIgnoreCase(status)) {
            pendingCount++;
        }
    }

    if (pendingCount == 0) {
        approvedall.setEnabled(false);
        approvedbtn.setEnabled(false);
        rejectbtn.setEnabled(false);
        return;
    }

    int choice = JOptionPane.showConfirmDialog(this,
            "There are " + pendingCount + " pending item(s). Are you sure you want to approve all?",
            "Confirm Approve All",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (choice != JOptionPane.YES_OPTION) return;

    // ✅ FIXED: use String orderId
    ApprovalOrdersPanel panel = new ApprovalOrdersPanel(orderId);

    for (int i = 0; i < approval_orders.getRowCount(); i++) {
        String status = approval_orders.getValueAt(i, 4).toString().trim();
        if ("Pending Approval".equalsIgnoreCase(status)) {
            String itemId = approval_orders.getValueAt(i, 0).toString().trim(); // ✅ FIXED
            String name = approval_orders.getValueAt(i, 1).toString();
            int qty = Integer.parseInt(approval_orders.getValueAt(i, 2).toString());
            panel.addPendingItem(itemId, name, qty, status); // ✅ FIXED
        }
    }

    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Approve Pending Items",
            Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    // ✅ Refresh table after approval
    loadPendingOrderItems(orderId);

    // ✅ Disable buttons if no pending items left
    boolean anyPending = false;
    for (int i = 0; i < approval_orders.getRowCount(); i++) {
        String rowStatus = approval_orders.getValueAt(i, 4).toString().trim();
        if ("Pending Approval".equalsIgnoreCase(rowStatus)) {
            anyPending = true;
            break;
        }
    }

    approvedall.setEnabled(anyPending);
    approvedbtn.setEnabled(false);
    rejectbtn.setEnabled(false);
    }//GEN-LAST:event_approvedallMouseClicked

    private void approvedallMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedallMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_approvedallMouseEntered

    private void rejectbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectbtnMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_rejectbtnMouseEntered

    private void rejectedallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rejectedallActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rejectedallActionPerformed

    private void updateOrderStatusRejected(String orderId) {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String sql = "UPDATE orders_info SET receiving_status = 'Rejected' WHERE order_id = ?";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, orderId); // ✅ FIXED: use setString
        pst.executeUpdate();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error updating order status: " + ex.getMessage());
    }
}
    
    private void rejectedallMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedallMouseClicked
     String orderId = order_id.getText().trim(); // ✅ FIXED: treat as String

    if (orderId.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Invalid Order ID format.");
        return;
    }

    // count only pending items
    int pendingCount = 0;
    for (int i = 0; i < approval_orders.getRowCount(); i++) {
        String status = approval_orders.getValueAt(i, 4).toString();
        if ("Pending Approval".equalsIgnoreCase(status)) {
            pendingCount++;
        }
    }

    if (pendingCount == 0) {
        rejectedall.setEnabled(false);
        return;
    }

    String previewMsg = "Are you sure you want to reject ALL " + pendingCount + " pending item(s)?";

    int confirm = JOptionPane.showConfirmDialog(
        this,
        previewMsg,
        "Confirm Reject All",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );
    if (confirm != JOptionPane.YES_OPTION) return;

    RejectedOrdersPanel panel = new RejectedOrdersPanel();
    panel.setRejectedBy(System.getProperty("user.name"));

    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    JDialog dialog = new JDialog(parentWindow, "Reject All Items", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);

    panel.getConfirmBtn().addActionListener(e -> {
        String rejectedBy = panel.getRejectedBy();
        String rejectedDate = panel.getRejectedDate();
        String rejectionReason = panel.getRemarks();

        if (rejectedBy.isEmpty() || rejectedDate.isEmpty() || rejectionReason.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please fill out all required fields.");
            return;
        }

        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(rejectedDate);
            saveRejectedAll(orderId, rejectedBy, sqlDate, rejectionReason); // ✅ FIXED
            updateOrderStatusRejected(orderId); // ✅ FIXED
            loadPendingOrderItems(orderId); // ✅ FIXED

            boolean anyPending = false;
            for (int i = 0; i < approval_orders.getRowCount(); i++) {
                if ("Pending Approval".equalsIgnoreCase(approval_orders.getValueAt(i, 4).toString())) {
                    anyPending = true;
                    break;
                }
            }

            rejectedall.setEnabled(anyPending);
            approvedall.setEnabled(anyPending);
            approvedbtn.setEnabled(false);
            rejectbtn.setEnabled(false);

            dialog.dispose();
        } catch (IllegalArgumentException ex2) {
            JOptionPane.showMessageDialog(dialog, "Invalid date format. Expected yyyy-MM-dd");
        }
    });

    panel.getCancelBtn().addActionListener(e -> dialog.dispose());
    dialog.setVisible(true);
    }//GEN-LAST:event_rejectedallMouseClicked

    private void saveRejectedAll(String orderId, String rejectedBy, java.sql.Date rejectedDate, String rejectionReason) {
    String insertMedsSql = """
        INSERT INTO rejected_orders_medicines (order_id, item_id, rejected_by, rejected_date, rejection_reason)
        SELECT oi.order_id, oi.item_id, ?, ?, ?
        FROM order_items oi
        WHERE oi.order_id = ? AND oi.approval_status = 'Pending Approval'
    """;

    String updateItemsSql = """
        UPDATE order_items
        SET approval_status = 'Rejected', receiving_status = 'Rejected'
        WHERE order_id = ? AND approval_status = 'Pending Approval'
    """;

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        try (PreparedStatement insertStmt = conn.prepareStatement(insertMedsSql)) {
            insertStmt.setString(1, rejectedBy);
            insertStmt.setDate(2, rejectedDate);
            insertStmt.setString(3, rejectionReason);
            insertStmt.setString(4, orderId); // ✅ FIXED
            int inserted = insertStmt.executeUpdate();

            try (PreparedStatement updateItemsStmt = conn.prepareStatement(updateItemsSql)) {
                updateItemsStmt.setString(1, orderId); // ✅ FIXED
                int updated = updateItemsStmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(null, "Rejected " + inserted + " pending item(s) successfully.");
        } catch (Exception ex) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Error during rejection: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        ex.printStackTrace();
    }
}
     
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable approval_orders;
    private javax.swing.JButton approvedall;
    private javax.swing.JButton approvedbtn;
    private javax.swing.JTextField d_ordered;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField odered_by;
    private javax.swing.JTextField order_id;
    private javax.swing.JButton rejectbtn;
    private javax.swing.JButton rejectedall;
    private javax.swing.JTextField supplier_name;
    // End of variables declaration//GEN-END:variables
}
