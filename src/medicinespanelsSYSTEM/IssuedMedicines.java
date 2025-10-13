
package medicinespanelsSYSTEM;

    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
    import java.awt.Component;
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
    import java.time.LocalDate;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;




public class IssuedMedicines extends javax.swing.JPanel {


    private boolean isProgrammaticChange = false;

    public IssuedMedicines() {
        initComponents();      

     }
    public void setRequestData(String requestId) {
    req_id.setText(requestId);
    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        // Load request info
        String query = "SELECT request_date, department, requested_by FROM requests WHERE request_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, requestId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            req_date.setText(rs.getString("request_date"));
            req_department.setText(rs.getString("department"));
            req_name.setText(rs.getString("requested_by"));
        }

        // Load approved items for the request (including items_requested_id)
        String itemQuery = """
            SELECT ri.items_requested_id, r.request_id, m.MedicineID, m.MedicineName, m.Dosage, m.Units, m.Description,
                   ri.quantity_requested, m.MfgDate, m.ExpDate, m.Barcode, m.BatchNo, ri.status
            FROM requested_items_medicines ri
            JOIN medicines m ON ri.medicine_id = m.MedicineID
            JOIN requests r ON ri.request_id = r.request_id
            WHERE ri.request_id = ? AND ri.status = 'Approved'
        """;
        PreparedStatement psItems = conn.prepareStatement(itemQuery);
        psItems.setString(1, requestId);
        ResultSet rsItems = psItems.executeQuery();

        DefaultTableModel model = (DefaultTableModel) table_issued.getModel();
        model.setRowCount(0);
        while (rsItems.next()) {
            model.addRow(new Object[]{
                rsItems.getString("items_requested_id"),   // index 0
                rsItems.getString("request_id"),           // index 1
                rsItems.getString("MedicineID"),           // index 2
                rsItems.getString("MedicineName"),         // index 3
                rsItems.getString("Dosage"),
                rsItems.getString("Units"),
                rsItems.getString("Description"),
                rsItems.getInt("quantity_requested"),
                rsItems.getDate("MfgDate"),
                rsItems.getDate("ExpDate"),
                rsItems.getString("Barcode"),
                rsItems.getString("BatchNo"),
                rsItems.getString("status")
            });
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to load request data.");
    }
}
 
      
        
       
       
public void loadIssuedItems(String requestId) {
    try (Connection conn = DriverManager.getConnection(
        "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
        "admin", "yeyel2025")) {

        String sql = """
            SELECT r.request_id, m.MedicineID, m.MedicineName, m.Dosage, m.Units, m.Description,
                   ri.quantity_requested, m.MfgDate, m.ExpDate, m.Barcode, m.BatchNo, ri.status
            FROM requested_items_medicines ri
            JOIN medicines m ON ri.medicine_id = m.MedicineID
            JOIN requests r ON ri.request_id = r.request_id
            WHERE ri.request_id = ? AND ri.status = 'Approved'
        """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, requestId);
        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = (DefaultTableModel) table_issued.getModel();
        model.setRowCount(0);
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("request_id"),
                rs.getString("MedicineID"),
                rs.getString("MedicineName"),
                rs.getString("Dosage"),
                rs.getString("Units"),
                rs.getString("Description"),
                rs.getInt("quantity_requested"),
                rs.getDate("MfgDate"),
                rs.getDate("ExpDate"),
                rs.getString("Barcode"),
                rs.getString("BatchNo"),
                rs.getString("status")
            });
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error reloading issued items: " + ex.getMessage());
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
        table_issued = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        req_id = new javax.swing.JLabel();
        req_name = new javax.swing.JLabel();
        req_department = new javax.swing.JLabel();
        req_date = new javax.swing.JLabel();
        issuebtn = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Requested Name:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Department:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Request Date:");

        table_issued.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request_ID", "Medicine ID", "Medicine Name", "Dosage", "Units", "Description", "Quantity Request", "Mfg Date", "Exp Date", "Barcode", "BatchNo", "Status"
            }
        ));
        jScrollPane1.setViewportView(table_issued);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Request ID:");

        req_id.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        req_id.setForeground(new java.awt.Color(255, 51, 0));
        req_id.setText(" ");

        req_name.setText("jLabel6");

        req_department.setText("jLabel7");

        req_date.setText("jLabel8");

        issuebtn.setText("Make Issued");
        issuebtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                issuebtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(req_name, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(req_department, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(36, 36, 36)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(req_date, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(req_id, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(issuebtn)
                .addGap(34, 34, 34))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(req_name)
                    .addComponent(req_date))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(req_department))
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(req_id, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(issuebtn)
                .addContainerGap(21, Short.MAX_VALUE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents





    
    private void issuebtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_issuebtnMouseClicked
 int selectedRow = table_issued.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, "Please select an approved item from the table.");
        return;
    }

    String itemsRequestedId = table_issued.getValueAt(selectedRow, 0).toString(); // items_requested_id
    String requestId = table_issued.getValueAt(selectedRow, 1).toString();        // request_id
    int quantityRequested = Integer.parseInt(table_issued.getValueAt(selectedRow, 7).toString());

    JDialog issueDialog = new JDialog((Frame) null, "Issue Medicine", true);
    issueDialog.setLayout(new BorderLayout(10, 10));

    JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
    JTextField issuedByField = new JTextField();

    com.toedter.calendar.JDateChooser issueDateChooser = new com.toedter.calendar.JDateChooser();
    issueDateChooser.setDate(new java.util.Date());

    JTextField quantityRequestedField = new JTextField(String.valueOf(quantityRequested));
    quantityRequestedField.setEditable(false);

    JTextField quantityIssuedField = new JTextField();
    JTextArea remarksArea = new JTextArea(3, 20);
    JScrollPane remarksScroll = new JScrollPane(remarksArea);

    formPanel.add(new JLabel("Issued By:"));
    formPanel.add(issuedByField);

    formPanel.add(new JLabel("Issue Date:"));
    formPanel.add(issueDateChooser);

    formPanel.add(new JLabel("Quantity Requested:"));
    formPanel.add(quantityRequestedField);

    formPanel.add(new JLabel("Quantity Issued:"));
    formPanel.add(quantityIssuedField);

    formPanel.add(new JLabel("Remarks:"));
    formPanel.add(remarksScroll);

    JButton submitIssueBtn = new JButton("Submit");

    issueDialog.add(formPanel, BorderLayout.CENTER);
    issueDialog.add(submitIssueBtn, BorderLayout.SOUTH);

    submitIssueBtn.addActionListener(e -> {
        String issuedBy = issuedByField.getText().trim();
        Date selectedDate = issueDateChooser.getDate();
        String remarks = remarksArea.getText().trim();
        String quantityIssuedStr = quantityIssuedField.getText().trim();

        if (issuedBy.isEmpty() || selectedDate == null || quantityIssuedStr.isEmpty()) {
            JOptionPane.showMessageDialog(issueDialog, "Please complete all fields.");
            return;
        }

        int quantityIssued;
        try {
            quantityIssued = Integer.parseInt(quantityIssuedStr);
            if (quantityIssued <= 0 || quantityIssued > quantityRequested) {
                JOptionPane.showMessageDialog(issueDialog, "Invalid quantity issued.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(issueDialog, "Quantity issued must be a number.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String issueDate = sdf.format(selectedDate);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025")) {

            String insertSql = """
                INSERT INTO issued_items_medicines (items_requested_id, issued_by, issued_date, quantity_issued, remarks, status)
                VALUES (?, ?, ?, ?, ?, 'Completed')
            """;
            PreparedStatement psInsert = conn.prepareStatement(insertSql);
            psInsert.setString(1, itemsRequestedId);
            psInsert.setString(2, issuedBy);
            psInsert.setString(3, issueDate);
            psInsert.setInt(4, quantityIssued);
            psInsert.setString(5, remarks);
            psInsert.executeUpdate();

            String updateSql = "UPDATE requested_items_medicines SET status = 'Completed' WHERE items_requested_id = ?";
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setString(1, itemsRequestedId);
            psUpdate.executeUpdate();

            JOptionPane.showMessageDialog(issueDialog, "Issued successfully.");
            issueDialog.dispose();

            // reload table after issuing
            loadIssuedItems(requestId);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(issueDialog, "Database error: " + ex.getMessage());
        }
    });

    issueDialog.pack();
    issueDialog.setLocationRelativeTo(null);
    issueDialog.setVisible(true);
    }//GEN-LAST:event_issuebtnMouseClicked

 


    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton issuebtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel req_date;
    private javax.swing.JLabel req_department;
    private javax.swing.JLabel req_id;
    private javax.swing.JLabel req_name;
    private javax.swing.JTable table_issued;
    // End of variables declaration//GEN-END:variables
}
