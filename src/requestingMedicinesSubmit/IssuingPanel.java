
package requestingMedicinesSubmit;

    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Dialog;
    import java.awt.Dimension;
    import java.awt.Font;
    import java.awt.FontMetrics;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
    import javax.swing.JDialog;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;


public class IssuingPanel extends javax.swing.JPanel {
    


        public IssuingPanel() {
            initComponents();        
    
            }

    // for new request
    public void setRequestData() {
        setRequestData(null);
    }

    public void setRequestData(String requestId) {
        req_date.setEditable(false);
        req_name.setEditable(false);
        req_department.setEditable(false);
        approvedby.setEditable(false);
        approvaldate.setEditable(false);

        req_date.setBackground(Color.WHITE);
        req_name.setBackground(Color.WHITE);
        req_department.setBackground(Color.WHITE);
        approvedby.setBackground(Color.WHITE);
        approvaldate.setBackground(Color.WHITE);

        if (requestId == null) {
            req_id.setText("");
            req_date.setText(new SimpleDateFormat("MM-dd-yyyy").format(new java.util.Date()));
            req_name.setText("");
            req_department.setText("");
            approvedby.setText("");
            approvaldate.setText("");
            ((DefaultTableModel) table_issuing_approval.getModel()).setRowCount(0);
            confirmbtn.setEnabled(false);
            rejectedbtn.setEnabled(false);
            return;
        }

        req_id.setText(requestId);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025")) {

            // ==========================
            // Load request header info
            // ==========================
            String query = """
                SELECT r.request_date, r.department, r.requested_by,
                       ar.approved_by, ar.approval_date
                FROM requests r
                LEFT JOIN approved_medicines_request ar
                       ON r.request_id = ar.request_id
                WHERE r.request_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        req_date.setText(rs.getString("request_date"));
                        req_department.setText(rs.getString("department"));
                        req_name.setText(rs.getString("requested_by"));

                        String approvedBy = rs.getString("approved_by");
                        Date approvalDateVal = rs.getDate("approval_date");
                        approvedby.setText(approvedBy != null ? approvedBy : "N/A");
                        approvaldate.setText(approvalDateVal != null
                                ? new SimpleDateFormat("MM-dd-yyyy").format(approvalDateVal)
                                : "N/A");
                    }
                }
            }

            // ==========================
            // Load requested + issued items
            // ==========================
            String itemQuery = """
                SELECT 
                    ri.GenericID,
                    m.GenericName,
                    m.Units,
                    m.Description,
                    ri.quantity_requested,
                    ISNULL(iim.quantity_issued, 0) AS quantity_issued,
                    m.MfgDate,
                    m.ExpDate,
                    m.BatchNo,
                    ISNULL(iim.remarks, '') AS remarks,
                    ri.issuing_status
                FROM requested_items_medicines ri
                JOIN medicines m ON ri.GenericID = m.GenericID
                LEFT JOIN issued_items_medicines iim
                    ON ri.request_id = iim.request_id AND ri.GenericID = iim.medicine_id
                WHERE ri.request_id = ?
            """;

            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{
                        "GenericID", "GenericName", "Units", "Description",
                        "Qty Requested", "Qty Issued", "MfgDate", "ExpDate",
                        "BatchNo", "Remarks", "Issuing Status"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    Object statusObj = getValueAt(row, 10);
                    String status = (statusObj != null) ? statusObj.toString().trim() : "";

                    // lock row if already issued
                    if ("Issued".equalsIgnoreCase(status)) return false;

                    // allow editing for Qty Issued and Remarks only
                    return (column == 5 || column == 9);
                }
            };

            boolean hasPendingIssuance = false;

            try (PreparedStatement psItems = conn.prepareStatement(itemQuery)) {
                psItems.setString(1, requestId);
                try (ResultSet rsItems = psItems.executeQuery()) {
                    while (rsItems.next()) {
                        String issuingStatus = rsItems.getString("issuing_status");
                        if ("Pending Issued".equalsIgnoreCase(issuingStatus)) {
                            hasPendingIssuance = true;
                        }

                        model.addRow(new Object[]{
                            rsItems.getInt("GenericID"),
                            rsItems.getString("GenericName"),
                            rsItems.getString("Units"),
                            rsItems.getString("Description"),
                            rsItems.getInt("quantity_requested"),
                            rsItems.getInt("quantity_issued"),
                            rsItems.getDate("MfgDate"),
                            rsItems.getDate("ExpDate"),
                            rsItems.getString("BatchNo"),
                            rsItems.getString("remarks"),
                            issuingStatus != null ? issuingStatus : "Pending Issued"
                        });
                    }
                }
            }

            table_issuing_approval.setModel(model);

            // hide Issuing Status column
            TableColumn hiddenCol = table_issuing_approval.getColumnModel().getColumn(10);
            table_issuing_approval.removeColumn(hiddenCol);

            // ==========================
            // Quantity column editor
            // ==========================
            table_issuing_approval.getColumnModel().getColumn(5).setCellEditor(new QuantityCellEditor());
            table_issuing_approval.getColumnModel().getColumn(5).setCellRenderer(new QuantityCellRenderer());

            // ==========================
            // Description column renderer (like remarks)
            // ==========================
            table_issuing_approval.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    JTextArea area = new JTextArea(value != null ? value.toString() : "");
                    area.setLineWrap(true);
                    area.setWrapStyleWord(true);
                    area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    area.setEditable(false);
                    area.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    area.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                    return area;
                }
            });

            // ==========================
            // Remarks column editor/renderer
            // ==========================
            table_issuing_approval.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(new JTextField()) {
                private final JTextArea textArea = new JTextArea();

                {
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }

                @Override
                public Component getTableCellEditorComponent(JTable table, Object value,
                                                             boolean isSelected, int row, int column) {
                    textArea.setText(value != null ? value.toString() : "");
                    if (isSelected) {
                        textArea.setBackground(table.getSelectionBackground());
                        textArea.setForeground(table.getSelectionForeground());
                    } else {
                        textArea.setBackground(Color.WHITE);
                        textArea.setForeground(Color.BLACK);
                    }
                    return textArea;
                }

                @Override
                public Object getCellEditorValue() {
                    return textArea.getText();
                }
            });

            table_issuing_approval.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    JTextArea area = new JTextArea(value != null ? value.toString() : "");
                    area.setLineWrap(true);
                    area.setWrapStyleWord(true);
                    area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    area.setEditable(false);
                    area.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    area.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                    return area;
                }
            });

            // ==========================
            // Table appearance
            // ==========================
            table_issuing_approval.setRowHeight(80);
            table_issuing_approval.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            JTableHeader header = table_issuing_approval.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 10));
            header.setBackground(Color.BLACK);
            header.setForeground(Color.WHITE);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < table_issuing_approval.getColumnCount(); i++) {
                if (i != 3 && i != 5 && i != 9) {
                    table_issuing_approval.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                }
            }

            confirmbtn.setEnabled(hasPendingIssuance);
            rejectedbtn.setEnabled(hasPendingIssuance);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load request info.");
        }
    }

    // overloaded version
    public void setRequestData(int requestId) {
        setRequestData(String.valueOf(requestId));
    }


    // ==============================
    // Quantity Cell Editor (fixed for nulls)
    // ==============================
    public class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new BorderLayout(2, 0));
        private final JTextField txtQty = new JTextField("0", 3);
        private final JButton btnPlus;
        private final JButton btnMinus;
        private int maxValue = Integer.MAX_VALUE;
        private int minValue = 0;

        public QuantityCellEditor() {
            txtQty.setHorizontalAlignment(JTextField.CENTER);
            txtQty.setPreferredSize(new Dimension(60, 24));

            btnPlus = new JButton(new ImageIcon(getClass().getResource("/nch/addpo.png")));
            btnMinus = new JButton(new ImageIcon(getClass().getResource("/nch/minuspo.png")));

            for (JButton btn : new JButton[]{btnPlus, btnMinus}) {
                btn.setBackground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(24, 24));
            }

            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 2, 0));
            buttonPanel.add(btnPlus);
            buttonPanel.add(btnMinus);

            panel.add(txtQty, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.EAST);

            btnPlus.addActionListener(e -> increment());
            btnMinus.addActionListener(e -> decrement());

            txtQty.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    fireEditingStopped();
                }
            });
        }

        private void increment() {
            try {
                int val = Integer.parseInt(txtQty.getText().trim());
                if (val < maxValue) txtQty.setText(String.valueOf(val + 1));
            } catch (NumberFormatException ex) {
                txtQty.setText(String.valueOf(minValue + 1));
            }
        }

        private void decrement() {
            try {
                int val = Integer.parseInt(txtQty.getText().trim());
                if (val > minValue) txtQty.setText(String.valueOf(val - 1));
            } catch (NumberFormatException ex) {
                txtQty.setText(String.valueOf(minValue));
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            int qtyOrdered = 0;
            int qtyIssued = 0;

            try {
                Object orderedObj = table.getValueAt(row, 4);
                if (orderedObj != null && !orderedObj.toString().isEmpty()) {
                    qtyOrdered = Integer.parseInt(orderedObj.toString());
                }
                qtyIssued = (value != null && !value.toString().isEmpty()) ? Integer.parseInt(value.toString()) : 0;
            } catch (Exception ignored) {}

            minValue = 0;
            maxValue = qtyOrdered;
            txtQty.setText(String.valueOf(qtyIssued));
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            int val = minValue;
            try {
                val = Integer.parseInt(txtQty.getText().trim());
                if (val < minValue) val = minValue;
                if (val > maxValue) val = maxValue;
            } catch (Exception ignored) {}
            return val;
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }



// ==============================
// CUSTOM CELL RENDERER
// ==============================
public class QuantityCellRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        setHorizontalAlignment(JLabel.CENTER);
        setText((value == null) ? "0" : value.toString());
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
        table_issuing_approval = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        confirmbtn = new javax.swing.JButton();
        rejectedbtn = new javax.swing.JButton();
        req_name = new javax.swing.JTextField();
        req_department = new javax.swing.JTextField();
        req_date = new javax.swing.JTextField();
        req_id = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        approvedby = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        approvaldate = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Requested Name:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Department:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Request Date:");

        table_issuing_approval.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Generic ID", "Generic Name", "Units", "Description", "Quantity Request", "Quantity Issued", "Mfg Date", "Exp Date", "BatchNo", "Status", "Remarks"
            }
        ));
        jScrollPane1.setViewportView(table_issuing_approval);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Request ID:");

        confirmbtn.setBackground(new java.awt.Color(0, 0, 0));
        confirmbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        confirmbtn.setForeground(new java.awt.Color(255, 255, 255));
        confirmbtn.setText("Confirm");
        confirmbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                confirmbtnMouseClicked(evt);
            }
        });

        rejectedbtn.setBackground(new java.awt.Color(0, 0, 0));
        rejectedbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rejectedbtn.setForeground(new java.awt.Color(255, 255, 255));
        rejectedbtn.setText("Cancel");
        rejectedbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rejectedbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rejectedbtnMouseEntered(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Approved By:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Approved Date :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(req_name)
                            .addComponent(req_department, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                        .addGap(33, 33, 33)
                        .addComponent(jLabel3)
                        .addGap(2, 2, 2)
                        .addComponent(req_date, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 4, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(confirmbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rejectedbtn))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 874, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addGap(2, 2, 2)
                                    .addComponent(req_id, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel5)
                                    .addGap(2, 2, 2)
                                    .addComponent(approvedby, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel6)
                                    .addGap(2, 2, 2)
                                    .addComponent(approvaldate, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(15, 15, 15))))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(approvaldate, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(approvedby, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(req_id, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rejectedbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confirmbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void confirmbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_confirmbtnMouseClicked
                                           
    int rowCount = table_issuing_approval.getRowCount();
    if (rowCount == 0) {
        JOptionPane.showMessageDialog(null, "No items to confirm.");
        return;
    }

    String requestId = req_id.getText().trim();
    String issuedBy = System.getProperty("user.name"); // or replace with logged-in user variable
    String approvalStatus = "Approved";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        conn.setAutoCommit(false);

        // SQL queries
        String getItemIdSQL = "SELECT items_requested_id FROM requested_items_medicines WHERE request_id = ? AND GenericID = ?";
        String checkExistingSQL = "SELECT issue_id, quantity_issued FROM issued_items_medicines WHERE request_id = ? AND medicine_id = ?";
        String insertSQL = """
            INSERT INTO issued_items_medicines 
            (items_requested_id, request_id, medicine_id, issued_by, issued_date, issued_time, 
             quantity_requested, quantity_issued, remarks, approval_status, issuing_status) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        String updateIssuedSQL = """
            UPDATE issued_items_medicines 
            SET quantity_issued = ?, remarks = ?, issued_date = ?, issued_time = ?, issued_by = ?, issuing_status = ? 
            WHERE request_id = ? AND medicine_id = ?
        """;
        String updateRequestSQL = "UPDATE requested_items_medicines SET issuing_status = ? WHERE request_id = ? AND GenericID = ?";
        String updateStockSQL = "UPDATE medicines SET QuantityInStock = QuantityInStock - ? WHERE GenericID = ?";

        try (PreparedStatement psGetItemId = conn.prepareStatement(getItemIdSQL);
             PreparedStatement psCheck = conn.prepareStatement(checkExistingSQL);
             PreparedStatement psInsert = conn.prepareStatement(insertSQL);
             PreparedStatement psUpdateIssued = conn.prepareStatement(updateIssuedSQL);
             PreparedStatement psUpdateReq = conn.prepareStatement(updateRequestSQL);
             PreparedStatement psUpdateStock = conn.prepareStatement(updateStockSQL)) {

            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            java.sql.Time currentTime = new java.sql.Time(System.currentTimeMillis());

            // Find Remarks column index
            int remarksColumnIndex = -1;
            for (int c = 0; c < table_issuing_approval.getColumnCount(); c++) {
                String colName = table_issuing_approval.getColumnName(c).trim().toLowerCase();
                if (colName.contains("remark")) {
                    remarksColumnIndex = c;
                    break;
                }
            }

            for (int i = 0; i < rowCount; i++) {
                Object genIdObj = table_issuing_approval.getValueAt(i, 0); // GenericID
                Object qtyReqObj = table_issuing_approval.getValueAt(i, 4); // Quantity Requested
                Object qtyIssuedObj = table_issuing_approval.getValueAt(i, 5); // Quantity Issued
                Object remarksObj = (remarksColumnIndex != -1) ? table_issuing_approval.getValueAt(i, remarksColumnIndex) : null;

                if (genIdObj == null) continue;

                int medId = Integer.parseInt(genIdObj.toString());
                int qtyRequested = qtyReqObj != null ? Integer.parseInt(qtyReqObj.toString()) : 0;
                int qtyFromTable = qtyIssuedObj != null ? Integer.parseInt(qtyIssuedObj.toString()) : 0;
                String remarks = (remarksObj != null && !remarksObj.toString().isBlank()) ? remarksObj.toString().trim() : "";

                if (qtyFromTable <= 0) continue; // skip if nothing to issue

                // ensure totalIssued does not exceed requested
                int totalIssued = Math.min(qtyRequested, qtyFromTable);

                String itemStatus = (totalIssued >= qtyRequested) ? "Issued" :
                                    (totalIssued > 0 ? "Partially Issued" : "Pending Issued");

                // get items_requested_id
                int itemsRequestedId = -1;
                psGetItemId.setString(1, requestId);
                psGetItemId.setInt(2, medId);
                try (ResultSet rs = psGetItemId.executeQuery()) {
                    if (rs.next()) {
                        itemsRequestedId = rs.getInt("items_requested_id");
                    } else {
                        System.err.println("No items_requested_id found for request " + requestId + " and med " + medId);
                        continue;
                    }
                }

                // check if already exists in issued_items_medicines
                int issueId = -1;
                psCheck.setString(1, requestId);
                psCheck.setInt(2, medId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        issueId = rs.getInt("issue_id");
                    }
                }

                if (issueId > 0) {
                    // update existing issued item
                    psUpdateIssued.setInt(1, totalIssued);
                    psUpdateIssued.setString(2, remarks);
                    psUpdateIssued.setDate(3, currentDate);
                    psUpdateIssued.setTime(4, currentTime);
                    psUpdateIssued.setString(5, issuedBy);
                    psUpdateIssued.setString(6, itemStatus);
                    psUpdateIssued.setString(7, requestId);
                    psUpdateIssued.setInt(8, medId);
                    psUpdateIssued.addBatch();
                } else {
                    // insert new issued item
                    psInsert.setInt(1, itemsRequestedId);
                    psInsert.setString(2, requestId);
                    psInsert.setInt(3, medId);
                    psInsert.setString(4, issuedBy);
                    psInsert.setDate(5, currentDate);
                    psInsert.setTime(6, currentTime);
                    psInsert.setInt(7, qtyRequested);
                    psInsert.setInt(8, totalIssued);
                    psInsert.setString(9, remarks);
                    psInsert.setString(10, approvalStatus);
                    psInsert.setString(11, itemStatus);
                    psInsert.addBatch();
                }

                // update requested_items_medicines issuing status
                psUpdateReq.setString(1, itemStatus);
                psUpdateReq.setString(2, requestId);
                psUpdateReq.setInt(3, medId);
                psUpdateReq.addBatch();

                // update medicines stock
                psUpdateStock.setInt(1, totalIssued);
                psUpdateStock.setInt(2, medId);
                psUpdateStock.addBatch();
            }

            psInsert.executeBatch();
            psUpdateIssued.executeBatch();
            psUpdateReq.executeBatch();
            psUpdateStock.executeBatch();

            // update header status
            updateIssuingHeaderStatus(conn, requestId);

            conn.commit();
            JOptionPane.showMessageDialog(null, "Items successfully confirmed and inventory updated!");
            confirmbtn.setEnabled(false);
            setRequestData(requestId);

        } catch (SQLException ex) {
            conn.rollback();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error issuing items. Transaction rolled back.\n" + ex.getMessage());
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database connection error.\n" + ex.getMessage());
    }
    }//GEN-LAST:event_confirmbtnMouseClicked

 private void updateIssuingHeaderStatus(Connection conn, String requestId) throws SQLException {
    String sql = "SELECT issuing_status FROM requested_items_medicines WHERE request_id = ?";
    boolean hasPending = false;
    boolean hasIssued = false;
    boolean hasPartial = false;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, requestId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("issuing_status");
                if ("Pending Issued".equalsIgnoreCase(status)) hasPending = true;
                if ("Issued".equalsIgnoreCase(status)) hasIssued = true;
                if ("Partially Issued".equalsIgnoreCase(status)) hasPartial = true;
            }
        }
    }

    String newStatus;
    if (hasIssued && !hasPending && !hasPartial) {
        newStatus = "Issued";
    } else if (hasPartial || (hasIssued && hasPending)) {
        newStatus = "Partially Issued";
    } else if (hasPending && !hasIssued && !hasPartial) {
        newStatus = "Pending Issued";
    } else {
        newStatus = "Partially Processed";
    }

    String updateSql = "UPDATE requests SET request_status = ? WHERE request_id = ?";
    try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
        ps2.setString(1, newStatus);
        ps2.setString(2, requestId);
        ps2.executeUpdate();
    }
}
    private void rejectedbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedbtnMouseClicked
    
    }//GEN-LAST:event_rejectedbtnMouseClicked
   
   


    
    private void rejectedbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedbtnMouseEntered
    
    }//GEN-LAST:event_rejectedbtnMouseEntered
     
  

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField approvaldate;
    private javax.swing.JTextField approvedby;
    private javax.swing.JButton confirmbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton rejectedbtn;
    private javax.swing.JTextField req_date;
    private javax.swing.JTextField req_department;
    private javax.swing.JTextField req_id;
    private javax.swing.JTextField req_name;
    private javax.swing.JTable table_issuing_approval;
    // End of variables declaration//GEN-END:variables
}
