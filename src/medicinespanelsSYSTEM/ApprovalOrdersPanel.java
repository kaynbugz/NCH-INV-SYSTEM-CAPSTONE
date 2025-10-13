package medicinespanelsSYSTEM;


import dashboardSYSTEM.SessionManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.Connection; // ✅ Corrected import
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;


public class ApprovalOrdersPanel extends javax.swing.JPanel {

    private List<String> pendingItemIds = new ArrayList<>();
    private String itemId;
    private String orderId;

    // ===== Single-item constructor =====
    public ApprovalOrdersPanel(String itemId, String orderId) {
        this.itemId = itemId;
        this.orderId = orderId;
        pendingItemIds.add(itemId);

        initComponents();
        initDefaults();
        String fullName = SessionManager.currentUserFullName;
        if (fullName != null) setLoggedInUser(fullName);
        setupListeners();
    }

    // ===== Batch approval constructor =====
    public ApprovalOrdersPanel(String orderId) {
        this.orderId = orderId;

        initComponents();
        initDefaults();
        String fullName = SessionManager.currentUserFullName;
        if (fullName != null) setLoggedInUser(fullName);
        setupListeners();
    }

    // ===== Add pending items for batch =====
    public void addPendingItem(String itemId, String name, int qty, String status) {
        pendingItemIds.add(itemId);
    }

    // ===== Approve all pending items =====
    private void approveAllItems() {
        if (pendingItemIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending items to approve.");
            return;
        }

        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
        String dbUser = "admin";
        String dbPass = "yeyel2025";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
            conn.setAutoCommit(false);

            String sqlUpdate = "UPDATE order_items SET approval_status = 'Approved' WHERE item_id = ? AND order_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlUpdate)) {
                for (String id : pendingItemIds) {
                    pst.setString(1, id);
                    pst.setString(2, this.orderId);
                    pst.addBatch();
                }
                pst.executeBatch();
            }

            // Update overall order status
            String sqlCheck = "SELECT COUNT(*) AS totalItems, SUM(CASE WHEN approval_status = 'Approved' THEN 1 ELSE 0 END) AS approvedItems FROM order_items WHERE order_id = ?";
            int total = 0, approved = 0;
            try (PreparedStatement pstCheck = conn.prepareStatement(sqlCheck)) {
                pstCheck.setString(1, this.orderId);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    if (rs.next()) {
                        total = rs.getInt("totalItems");
                        approved = rs.getInt("approvedItems");
                    }
                }
            }

            String newStatus = (approved == total && total > 0) ? "Approved" : "Partially Approved";
            String sqlUpdateOrder = "UPDATE orders_info SET approval_status = ? WHERE order_id = ?";
            try (PreparedStatement pstOrder = conn.prepareStatement(sqlUpdateOrder)) {
                pstOrder.setString(1, newStatus);
                pstOrder.setString(2, this.orderId);
                pstOrder.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "All pending items approved successfully!");

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error approving items: " + ex.getMessage());
        }
    }

    // ===== Approve single item =====
    private void approveItem() {
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
        String dbUser = "admin";
        String dbPass = "yeyel2025";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
            String sqlUpdateItem = "UPDATE order_items SET approval_status = 'Approved' WHERE item_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlUpdateItem)) {
                pst.setString(1, this.itemId);
                pst.executeUpdate();
            }

            String sqlCheck = """
                SELECT 
                    COUNT(*) AS totalItems,
                    SUM(CASE WHEN approval_status = 'Approved' THEN 1 ELSE 0 END) AS approvedItems
                FROM order_items 
                WHERE order_id = ?
            """;

            int total = 0, approved = 0;
            try (PreparedStatement pstCheck = conn.prepareStatement(sqlCheck)) {
                pstCheck.setString(1, this.orderId);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    if (rs.next()) {
                        total = rs.getInt("totalItems");
                        approved = rs.getInt("approvedItems");
                    }
                }
            }

            String newStatus = (approved == total && total > 0) ? "Approved" : "Partially Approved";

            String sqlUpdateOrder = "UPDATE orders_info SET approval_status = ? WHERE order_id = ?";
            try (PreparedStatement pstOrder = conn.prepareStatement(sqlUpdateOrder)) {
                pstOrder.setString(1, newStatus);
                pstOrder.setString(2, this.orderId);
                pstOrder.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Item approved successfully!");

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error approving item: " + ex.getMessage());
        }
    }

    // ===== UI setup helpers =====
    private void initDefaults() {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        datetext.setText(today);
        datetext.setEditable(false);
    }

    public void setLoggedInUser(String fullName) {
        approvedtxt.setText(fullName);
        approvedtxt.setEditable(false);
    }

    public void setApprovalData(String itemId, String orderId) {
        this.itemId = itemId;
        this.orderId = orderId;
    }

    private void setupListeners() {
        apprvebtn.addActionListener(e -> {
            if (!pendingItemIds.isEmpty()) approveAllItems();
            else approveItem();
        });

        cancelbtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });
}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        approvedtxt = new javax.swing.JTextField();
        cancelbtn = new javax.swing.JButton();
        apprvebtn = new javax.swing.JButton();
        datetext = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Approved Item");

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Approved By :");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Approved Date :");

        cancelbtn.setBackground(new java.awt.Color(0, 0, 0));
        cancelbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cancelbtn.setForeground(new java.awt.Color(255, 255, 255));
        cancelbtn.setText("Cancel");

        apprvebtn.setBackground(new java.awt.Color(0, 0, 0));
        apprvebtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        apprvebtn.setForeground(new java.awt.Color(255, 255, 255));
        apprvebtn.setText("Confirm");
        apprvebtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                apprvebtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(datetext, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                            .addComponent(approvedtxt)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 196, Short.MAX_VALUE)
                        .addComponent(apprvebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(146, 146, 146))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(approvedtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datetext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(apprvebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void apprvebtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_apprvebtnMouseClicked
            
    }//GEN-LAST:event_apprvebtnMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField approvedtxt;
    private javax.swing.JButton apprvebtn;
    private javax.swing.JButton cancelbtn;
    private javax.swing.JTextField datetext;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}

