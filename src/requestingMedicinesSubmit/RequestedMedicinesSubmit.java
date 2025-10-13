
package requestingMedicinesSubmit;

    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
import java.awt.Color;
    import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
    import java.awt.GridLayout;
    import java.awt.Window;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.FocusAdapter;
    import java.awt.event.FocusEvent;
    import java.awt.event.ItemEvent;
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
    import java.sql.Timestamp;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.Map;
    import java.util.Set;
    import javax.swing.JDialog;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;


public class RequestedMedicinesSubmit extends javax.swing.JPanel {
    


        public RequestedMedicinesSubmit() {
            initComponents();        
            
            // Disable buttons by default
            approvedreq.setEnabled(false);
            rejectedbtn.setEnabled(false);

            table_request_sts.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        int viewRow = table_request_sts.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = table_request_sts.convertRowIndexToModel(viewRow);
            DefaultTableModel model = (DefaultTableModel) table_request_sts.getModel();
            int statusColIndex = model.findColumn("Approval Status");

            if (statusColIndex == -1) {
                JOptionPane.showMessageDialog(null, "'Approval Status' column not found.");
                approvedreq.setEnabled(false);
                rejectedbtn.setEnabled(false);
                return;
            }

            String status = model.getValueAt(modelRow, statusColIndex).toString().trim();
            boolean isRequested = "Requested".equalsIgnoreCase(status);
            approvedreq.setEnabled(isRequested);
            rejectedbtn.setEnabled(isRequested);
        } else {
            approvedreq.setEnabled(false);
            rejectedbtn.setEnabled(false);
        }
    }
});


        }
    public void setRequestData(String requestId) {
    req_date.setEditable(false);
    req_name.setEditable(false);
    req_department.setEditable(false);

    req_date.setBackground(Color.WHITE);
    req_name.setBackground(Color.WHITE);
    req_department.setBackground(Color.WHITE);

    if (requestId == null) {
        req_id.setText("");
        req_date.setText(new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()));
        req_name.setText("");
        req_department.setText("");
        ((DefaultTableModel) table_request_sts.getModel()).setRowCount(0);

        approvedreq.setEnabled(false);
        rejectedbtn.setEnabled(false);
        approvedallbtn.setEnabled(false);
        rejectedall.setEnabled(false);
        return;
    }

    req_id.setText(requestId);

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        // Load request header
        String query = "SELECT request_date, department, requested_by FROM requests WHERE request_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, requestId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            req_date.setText(rs.getString("request_date"));
            req_department.setText(rs.getString("department"));
            req_name.setText(rs.getString("requested_by"));
        }

        // Load requested items with approval_status
        String itemQuery = """
            SELECT ri.GenericID, m.GenericName, m.Units, m.Description,
                   ri.quantity_requested, m.MfgDate, m.ExpDate, m.BatchNo,
                   ri.approval_status
            FROM requested_items_medicines ri
            JOIN medicines m ON ri.GenericID = m.GenericID
            WHERE ri.request_id = ?
        """;
        PreparedStatement psItems = conn.prepareStatement(itemQuery);
        psItems.setString(1, requestId);
        ResultSet rsItems = psItems.executeQuery();

        DefaultTableModel model = new DefaultTableModel(
            new Object[]{
                "GenericID", "GenericName", "Units", "Description",
                "Quantity Requested", "MfgDate", "ExpDate", "BatchNo", "Approval Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        boolean hasRequested = false;

        while (rsItems.next()) {
            String approvalStatus = rsItems.getString("approval_status");
            if ("Requested".equalsIgnoreCase(approvalStatus)) {
                hasRequested = true;
            }

            model.addRow(new Object[]{
                rsItems.getInt("GenericID"),
                rsItems.getString("GenericName"),
                rsItems.getString("Units"),
                rsItems.getString("Description"),
                rsItems.getInt("quantity_requested"),
                rsItems.getDate("MfgDate"),
                rsItems.getDate("ExpDate"),
                rsItems.getString("BatchNo"),
                approvalStatus
            });
        }

        table_request_sts.setModel(model);

        // Header styling
        JTableHeader header = table_request_sts.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);

        // GenericID column red
        DefaultTableCellRenderer genericIdRenderer = new DefaultTableCellRenderer();
        genericIdRenderer.setForeground(Color.RED);
        genericIdRenderer.setHorizontalAlignment(JLabel.CENTER);
        table_request_sts.getColumnModel().getColumn(0).setCellRenderer(genericIdRenderer);

        // Center other columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < table_request_sts.getColumnCount(); i++) {
            table_request_sts.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Enable/disable buttons based on approval status
        approvedreq.setEnabled(hasRequested);
        rejectedbtn.setEnabled(hasRequested);
        approvedallbtn.setEnabled(hasRequested);
        rejectedall.setEnabled(hasRequested);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to load request info.");
    }
}    
        

            
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_request_sts = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        approvedreq = new javax.swing.JButton();
        rejectedbtn = new javax.swing.JButton();
        req_name = new javax.swing.JTextField();
        req_department = new javax.swing.JTextField();
        req_date = new javax.swing.JTextField();
        req_id = new javax.swing.JTextField();
        approvedallbtn = new javax.swing.JButton();
        rejectedall = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Requested Name:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Department:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Request Date:");

        table_request_sts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Medicine ID", "Medicine Name", "Units", "Description", "Quantity Request", "Mfg Date", "Exp Date", "BatchNo", "Status"
            }
        ));
        jScrollPane1.setViewportView(table_request_sts);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Request ID:");

        approvedreq.setBackground(new java.awt.Color(0, 0, 0));
        approvedreq.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        approvedreq.setForeground(new java.awt.Color(255, 255, 255));
        approvedreq.setText("Approved");
        approvedreq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                approvedreqMouseClicked(evt);
            }
        });

        rejectedbtn.setBackground(new java.awt.Color(0, 0, 0));
        rejectedbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rejectedbtn.setForeground(new java.awt.Color(255, 255, 255));
        rejectedbtn.setText("Rejected");
        rejectedbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rejectedbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rejectedbtnMouseEntered(evt);
            }
        });

        approvedallbtn.setBackground(new java.awt.Color(0, 0, 0));
        approvedallbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        approvedallbtn.setForeground(new java.awt.Color(255, 255, 255));
        approvedallbtn.setText("Approve all");
        approvedallbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                approvedallbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                approvedallbtnMouseEntered(evt);
            }
        });

        rejectedall.setBackground(new java.awt.Color(0, 0, 0));
        rejectedall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rejectedall.setForeground(new java.awt.Color(255, 255, 255));
        rejectedall.setText("Rejected All");
        rejectedall.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rejectedallMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(2, 2, 2)
                        .addComponent(req_id, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(req_name)
                            .addComponent(req_department, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addComponent(jLabel3)
                        .addGap(2, 2, 2)
                        .addComponent(req_date, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(approvedallbtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rejectedall)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(approvedreq)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rejectedbtn))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(req_name, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(req_date, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(req_department, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(req_id, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rejectedbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(approvedreq, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(approvedallbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rejectedall, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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

    private void approvedreqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedreqMouseClicked
    int[] selectedRows = table_request_sts.getSelectedRows();
    if (selectedRows.length == 0) {
        JOptionPane.showMessageDialog(null, "Please select at least one row to approve.");
        return;
    }

    ApprovalRequestPanel panel = new ApprovalRequestPanel();
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    JDialog dialog = new JDialog(parentWindow, "Approval Details", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);

    panel.getConfirmButton().addActionListener(e -> {
        String approvedBy = panel.getApprovedBy();
        String approvalDateStr = panel.getApprovalDateText();

        if (approvedBy.isEmpty() || approvalDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please enter both Approved By and Approval Date.");
            return;
        }

        try {
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(approvalDateStr);
            java.sql.Date approvalDate = new java.sql.Date(utilDate.getTime());
            approveSelectedItems(selectedRows, approvedBy, approvalDate);
            dialog.dispose();
            approvedreq.setEnabled(false);
        } catch (Exception pe) {
            JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd.");
        }
    });

    dialog.setVisible(true);
    }//GEN-LAST:event_approvedreqMouseClicked
    
    private void approveSelectedItems(int[] selectedRows, String approvedBy, java.sql.Date approvalDate) {
    String requestId = req_id.getText();

    // Insert record to approved_medicines_request
    String insertQuery = """
        INSERT INTO approved_medicines_request 
        (request_id, medicine_id, approved_by, approval_date) 
        VALUES (?, ?, ?, ?)
    """;

    // Update approval and issuing status in requested_items_medicines
    String updateStatusQuery = """
        UPDATE requested_items_medicines 
        SET approval_status = ?, issuing_status = ? 
        WHERE request_id = ? AND GenericID = ?
    """;

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        conn.setAutoCommit(false);

        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateStatusQuery)) {

            DefaultTableModel model = (DefaultTableModel) table_request_sts.getModel();
            int statusColIndex = model.findColumn("Approval Status");
            int medicineIdColIndex = model.findColumn("GenericID");

            if (statusColIndex == -1 || medicineIdColIndex == -1) {
                JOptionPane.showMessageDialog(null, "'Approval Status' or 'GenericID' column missing in JTable.");
                return;
            }

            int rowsProcessed = 0;

            for (int viewRowIndex : selectedRows) {
                int modelRowIndex = table_request_sts.convertRowIndexToModel(viewRowIndex);
                String currentStatus = model.getValueAt(modelRowIndex, statusColIndex).toString().trim();

                // Only approve rows that are still "Requested"
                if ("Requested".equalsIgnoreCase(currentStatus)) {
                    String medicineId = model.getValueAt(modelRowIndex, medicineIdColIndex).toString();

                    // ✅ Insert into approved_medicines_request table
                    insertStmt.setString(1, requestId);
                    insertStmt.setString(2, medicineId);
                    insertStmt.setString(3, approvedBy);
                    insertStmt.setDate(4, approvalDate);
                    insertStmt.addBatch();

                    // ✅ Update both approval_status and issuing_status
                    updateStmt.setString(1, "Approved");
                    updateStmt.setString(2, "Pending Issued"); // <--- automatic issued status
                    updateStmt.setString(3, requestId);
                    updateStmt.setString(4, medicineId);
                    updateStmt.addBatch();

                    // ✅ Update JTable
                    model.setValueAt("Approved", modelRowIndex, statusColIndex);
                    rowsProcessed++;
                }
            }

            if (rowsProcessed == 0) {
                JOptionPane.showMessageDialog(null, "No 'Requested' rows selected for approval.");
                return;
            }

            // Execute all updates and inserts
            insertStmt.executeBatch();
            updateStmt.executeBatch();

            // Update the main request header status
            updateRequestHeaderStatus(conn, requestId);

            conn.commit();
            JOptionPane.showMessageDialog(null, "Successfully approved " + rowsProcessed + " item(s).");

        } catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Approval failed: " + e.getMessage());
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Database connection failed: " + ex.getMessage());
    }
}

   private void updateRequestHeaderStatus(Connection conn, String requestId) throws SQLException {
    String sql = "SELECT approval_status, COUNT(*) AS cnt FROM requested_items_medicines WHERE request_id = ? GROUP BY approval_status";
    boolean hasRequested = false;
    boolean hasApproved = false;
    boolean hasRejected = false;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, requestId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String s = rs.getString("approval_status");
                if ("Requested".equalsIgnoreCase(s)) hasRequested = true;
                if ("Approved".equalsIgnoreCase(s)) hasApproved = true;
                if ("Rejected".equalsIgnoreCase(s)) hasRejected = true;
            }
        }
    }

    String newStatus;
    if (hasApproved && !hasRequested && !hasRejected) {
        newStatus = "Approved";
    } else if (hasRejected && !hasRequested && !hasApproved) {
        newStatus = "Rejected";
    } else if ((hasApproved && hasRejected) || (hasApproved && hasRequested)) {
        newStatus = "Partially Approved";
    } else if (hasRejected && hasRequested) {
        newStatus = "Partially Processed";
    } else {
        newStatus = "Requested";
    }

    String updateSql = "UPDATE requests SET request_status = ? WHERE request_id = ?";
    try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
        ps2.setString(1, newStatus);
        ps2.setString(2, requestId);
        int affected = ps2.executeUpdate();
        System.out.println("Request header updated: " + affected + " row(s)");
    }
}

    private void rejectedbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedbtnMouseClicked
     int[] selectedRows = table_request_sts.getSelectedRows();
    if (selectedRows.length == 0) {
        JOptionPane.showMessageDialog(null, "Please select at least one row to reject.");
        return;
    }

    RequestedRejectionPanel panel = new RequestedRejectionPanel();
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    JDialog dialog = new JDialog(parentWindow, "Rejection Details", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);

    panel.getConfirmButton().addActionListener(e -> {
        String rejectedBy = panel.getRejectedBy();
        String rejectedDateStr = panel.getRejectedDate();
        String rejectionReason = panel.getRejectionReason();

        if (rejectedBy.isEmpty() || rejectedDateStr.isEmpty() || rejectionReason.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please fill all fields (Rejected By, Date, Reason).");
            return;
        }

        java.sql.Date rejectedDate;
        try {
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(rejectedDateStr);
            rejectedDate = new java.sql.Date(utilDate.getTime());
        } catch (Exception pe) {
            JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        rejectSelectedItems(selectedRows, rejectedBy, rejectedDate, rejectionReason);
        dialog.dispose();
        rejectedbtn.setEnabled(false);
    });

    dialog.setVisible(true);
    }//GEN-LAST:event_rejectedbtnMouseClicked
   
    private void rejectSelectedItems(int[] selectedRows, String rejectedBy, java.sql.Date rejectedDate, String rejectionReason) {
    String requestId = req_id.getText();

    String insertQuery = "INSERT INTO rejected_medicines_request (request_id, medicine_id, rejection_reason, rejected_by, rejected_date) VALUES (?, ?, ?, ?, ?)";
    String updateStatusQuery = "UPDATE requested_items_medicines SET approval_status = ? WHERE request_id = ? AND GenericID = ?";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        conn.setAutoCommit(false);

        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateStatusQuery)) {

            DefaultTableModel model = (DefaultTableModel) table_request_sts.getModel();
            int statusColIndex = model.findColumn("Approval Status");
            int medicineIdColIndex = model.findColumn("GenericID");

            if (statusColIndex == -1 || medicineIdColIndex == -1) {
                JOptionPane.showMessageDialog(null, "'Approval Status' or 'GenericID' column missing in JTable.");
                return;
            }

            int rowsProcessed = 0;

            for (int viewRowIndex : selectedRows) {
                int modelRowIndex = table_request_sts.convertRowIndexToModel(viewRowIndex);
                String currentStatus = model.getValueAt(modelRowIndex, statusColIndex).toString().trim();

                if ("Requested".equalsIgnoreCase(currentStatus)) {
                    String medicineId = model.getValueAt(modelRowIndex, medicineIdColIndex).toString();

                    insertStmt.setString(1, requestId);
                    insertStmt.setString(2, medicineId);
                    insertStmt.setString(3, rejectionReason);
                    insertStmt.setString(4, rejectedBy);
                    insertStmt.setDate(5, rejectedDate);
                    insertStmt.addBatch();

                    updateStmt.setString(1, "Rejected");
                    updateStmt.setString(2, requestId);
                    updateStmt.setString(3, medicineId);
                    updateStmt.addBatch();

                    model.setValueAt("Rejected", modelRowIndex, statusColIndex);
                    rowsProcessed++;
                }
            }

            if (rowsProcessed == 0) {
                JOptionPane.showMessageDialog(null, "No 'Requested' rows selected for rejection.");
                return;
            }

            insertStmt.executeBatch();
            updateStmt.executeBatch();
            conn.commit();

            updateRequestHeaderStatus(conn, requestId); // optional: update overall status
            JOptionPane.showMessageDialog(null, "Successfully rejected " + rowsProcessed + " item(s).");

        } catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Rejection failed: " + e.getMessage());
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Database connection failed.");
    }
    }
    
    private void rejectedbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedbtnMouseEntered
    
    }//GEN-LAST:event_rejectedbtnMouseEntered
 
    private void approvedallbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedallbtnMouseClicked
    DefaultTableModel model = (DefaultTableModel) table_request_sts.getModel();
String requestId = req_id.getText();

// approval panel for Approved By + Date
ApprovalRequestPanel panel = new ApprovalRequestPanel();
Window parentWindow = SwingUtilities.getWindowAncestor(this);
JDialog dialog = new JDialog(parentWindow, "Approval Details", Dialog.ModalityType.APPLICATION_MODAL);
dialog.setUndecorated(true);
dialog.setContentPane(panel);
dialog.pack();
dialog.setLocationRelativeTo(this);

panel.getConfirmButton().addActionListener(e -> {
    String approvedBy = panel.getApprovedBy();
    String approvalDateStr = panel.getApprovalDateText();

    if (approvedBy.isEmpty() || approvalDateStr.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Please enter both Approved By and Approval Date.");
        return;
    }

    java.sql.Date approvalDate;
    try {
        java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(approvalDateStr);
        approvalDate = new java.sql.Date(utilDate.getTime());
    } catch (Exception pe) {
        JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd.");
        return;
    }

    String insertQuery = "INSERT INTO approved_medicines_request (request_id, medicine_id, approved_by, approval_date) VALUES (?, ?, ?, ?)";
    String updateStatusQuery = "UPDATE requested_items_medicines SET approval_status = ? WHERE request_id = ? AND GenericID = ?";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        conn.setAutoCommit(false);

        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateStatusQuery)) {

            int statusColIndex = model.findColumn("Approval Status");
            int medicineIdColIndex = model.findColumn("GenericID");

            if (statusColIndex == -1 || medicineIdColIndex == -1) {
                JOptionPane.showMessageDialog(null, "'Approval Status' or 'GenericID' column not found.");
                return;
            }

            int rowsProcessed = 0;

            for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
                String currentStatus = model.getValueAt(rowIndex, statusColIndex).toString().trim();

                if ("Requested".equalsIgnoreCase(currentStatus)) {
                    String medicineId = model.getValueAt(rowIndex, medicineIdColIndex).toString();

                    insertStmt.setString(1, requestId);
                    insertStmt.setString(2, medicineId);
                    insertStmt.setString(3, approvedBy);
                    insertStmt.setDate(4, approvalDate);
                    insertStmt.addBatch();

                    updateStmt.setString(1, "Approved");
                    updateStmt.setString(2, requestId);
                    updateStmt.setString(3, medicineId);
                    updateStmt.addBatch();

                    model.setValueAt("Approved", rowIndex, statusColIndex);
                    rowsProcessed++;
                }
            }

            if (rowsProcessed == 0) {
                JOptionPane.showMessageDialog(null, "No 'Requested' rows to approve.");
                return;
            }

            insertStmt.executeBatch();
            updateStmt.executeBatch();
            conn.commit();

            updateRequestHeaderStatus(conn, requestId); // optional: update overall status
            JOptionPane.showMessageDialog(null, rowsProcessed + " item(s) approved successfully.");
            dialog.dispose();

        } catch (SQLException ex) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Approval failed: " + ex.getMessage());
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
    }
});

dialog.setVisible(true);
    }//GEN-LAST:event_approvedallbtnMouseClicked

    private void approvedallbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedallbtnMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_approvedallbtnMouseEntered

    private void rejectedallMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedallMouseClicked
    {                                         
    {
    DefaultTableModel model = (DefaultTableModel) table_request_sts.getModel();
    String requestId = req_id.getText();

    RequestedRejectionPanel panel = new RequestedRejectionPanel();
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    JDialog dialog = new JDialog(parentWindow, "Rejection Details", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(this);

    panel.getConfirmButton().addActionListener(e -> {
        String rejectedBy = panel.getRejectedBy();
        String rejectedDateStr = panel.getRejectedDate();
        String rejectionReason = panel.getRejectionReason();

        if (rejectedBy.isEmpty() || rejectedDateStr.isEmpty() || rejectionReason.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Please fill all fields (Rejected By, Date, Reason).");
            return;
        }

        java.sql.Date rejectedDate;
        try {
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(rejectedDateStr);
            rejectedDate = new java.sql.Date(utilDate.getTime());
        } catch (Exception pe) {
            JOptionPane.showMessageDialog(dialog, "Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        String insertQuery = "INSERT INTO rejected_medicines_request (request_id, medicine_id, rejection_reason, rejected_by, rejected_date) VALUES (?, ?, ?, ?, ?)";
        String updateStatusQuery = "UPDATE requested_items_medicines SET approval_status = ? WHERE request_id = ? AND GenericID = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025")) {

            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                 PreparedStatement updateStmt = conn.prepareStatement(updateStatusQuery)) {

                int statusColIndex = model.findColumn("Approval Status");
                int medicineIdColIndex = model.findColumn("GenericID");

                if (statusColIndex == -1 || medicineIdColIndex == -1) {
                    JOptionPane.showMessageDialog(null, "'Approval Status' or 'GenericID' column not found.");
                    return;
                }

                int rowsProcessed = 0;

                for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
                    String currentStatus = model.getValueAt(rowIndex, statusColIndex).toString().trim();

                    if ("Requested".equalsIgnoreCase(currentStatus)) {
                        String medicineId = model.getValueAt(rowIndex, medicineIdColIndex).toString();

                        insertStmt.setString(1, requestId);
                        insertStmt.setString(2, medicineId);
                        insertStmt.setString(3, rejectionReason);
                        insertStmt.setString(4, rejectedBy);
                        insertStmt.setDate(5, rejectedDate);
                        insertStmt.addBatch();

                        updateStmt.setString(1, "Rejected");
                        updateStmt.setString(2, requestId);
                        updateStmt.setString(3, medicineId);
                        updateStmt.addBatch();

                        model.setValueAt("Rejected", rowIndex, statusColIndex);
                        rowsProcessed++;
                    }
                }

                if (rowsProcessed == 0) {
                    JOptionPane.showMessageDialog(null, "No 'Requested' rows to reject.");
                    return;
                }

                insertStmt.executeBatch();
                updateStmt.executeBatch();
                conn.commit();

                updateRequestHeaderStatus(conn, requestId); // optional: update overall status
                JOptionPane.showMessageDialog(null, rowsProcessed + " item(s) rejected successfully.");
                dialog.dispose();

            } catch (SQLException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Rejection failed: " + ex.getMessage());
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    });

    dialog.setVisible(true);
    }//GEN-LAST:event_rejectedallMouseClicked
    }
    }
  

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton approvedallbtn;
    private javax.swing.JButton approvedreq;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton rejectedall;
    private javax.swing.JButton rejectedbtn;
    private javax.swing.JTextField req_date;
    private javax.swing.JTextField req_department;
    private javax.swing.JTextField req_id;
    private javax.swing.JTextField req_name;
    private javax.swing.JTable table_request_sts;
    // End of variables declaration//GEN-END:variables
}
