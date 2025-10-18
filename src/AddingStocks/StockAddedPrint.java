package AddingStocks;

    import receivedpanelsSYSTEM.*;
    import com.toedter.calendar.JDateChooser;
import dashboardSYSTEM.SessionManager;
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
    import java.security.Timestamp;
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

public class StockAddedPrint extends javax.swing.JPanel {

    private String receivedId;

    public StockAddedPrint(String receivedId) {
        this.receivedId = receivedId;
        initComponents();
        loadStockDetails(receivedId, SessionManager.currentUserFullName);
    }

    private void loadStockDetails(String requestId, String currentUser) {
        DefaultTableModel model = (DefaultTableModel) approvedmedstable.getModel(); // your JTable
        model.setRowCount(0);

        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
        String dbUser = "admin";
        String dbPass = "yeyel2025";

        String query = """
            SELECT 
                srm.generic_id,
                m.GenericName,
                m.Units,
                srm.quantity_requested,
                srm.stock_status,
                srm.requested_by,
                srm.request_date,
                srm.request_time,
                srm.remarks,
                srm.supplier_name,
                srm.supplier_type,
                srm.request_id
            FROM stock_request_medicines srm
            LEFT JOIN medicines m ON srm.generic_id = m.GenericID
            WHERE srm.request_id = ?
        """;

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, requestId);

            try (ResultSet rs = pst.executeQuery()) {
                boolean firstRow = true;

                while (rs.next()) {
                    Object[] row = {
                        rs.getString("generic_id"),
                        rs.getString("GenericName"),
                        rs.getString("Units"),
                        rs.getInt("quantity_requested"),
                        rs.getString("stock_status")
                    };
                    model.addRow(row);

                    if (firstRow) {
                        reqid.setText(rs.getString("request_id"));
                        reqby.setText(rs.getString("requested_by"));
                        supname.setText(rs.getString("supplier_name"));
                        type.setText(rs.getString("supplier_type"));
                        dadded.setText(rs.getDate("request_date").toString());
                        appby.setText(currentUser);

                        loadCategoryFromType(rs.getString("supplier_type"));
                        firstRow = false;
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading stock details: " + e.getMessage());
        }
    }

    private void loadCategoryFromType(String typeValue) {
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
        String dbUser = "admin";
        String dbPass = "yeyel2025";

        String query = """
            SELECT TOP 1 cat_name
            FROM categories
            WHERE cat_type = ?
            ORDER BY date DESC
        """;

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, typeValue);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    categories.setText(rs.getString("cat_name"));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading category: " + e.getMessage());
        }
    }




 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        approvedmedstable = new javax.swing.JTable();
        suptype = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        approvedby = new javax.swing.JLabel();
        reqby = new javax.swing.JTextField();
        type = new javax.swing.JTextField();
        categories = new javax.swing.JTextField();
        supname = new javax.swing.JTextField();
        dadded = new javax.swing.JTextField();
        appby = new javax.swing.JTextField();
        reqid = new javax.swing.JTextField();

        setBackground(new java.awt.Color(238, 238, 238));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/inspection.png"))); // NOI18N
        jLabel1.setText("Stock Added Panel");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Supplier Name");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Requested By");

        approvedmedstable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Generic ID", "Medicines Name", "Units", "Qty Stock Approved", "Status"
            }
        ));
        approvedmedstable.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(approvedmedstable);

        suptype.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        suptype.setText("Type");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Categories");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Date Added :");

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("PRINT");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Requested ID");

        approvedby.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        approvedby.setText("Approved By :");

        reqby.setForeground(new java.awt.Color(255, 0, 0));

        type.setForeground(new java.awt.Color(255, 0, 0));

        categories.setForeground(new java.awt.Color(255, 0, 0));
        categories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoriesActionPerformed(evt);
            }
        });

        supname.setForeground(new java.awt.Color(255, 0, 0));

        dadded.setForeground(new java.awt.Color(255, 0, 0));

        appby.setForeground(new java.awt.Color(255, 0, 0));

        reqid.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(590, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(2, 2, 2)
                                        .addComponent(reqid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(117, 117, 117))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3))
                                        .addGap(2, 2, 2)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(reqby, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(supname, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(2, 2, 2))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(suptype)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(categories, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                                    .addComponent(type)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(approvedby, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(appby, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                    .addComponent(dadded))))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reqid, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(categories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(suptype)
                            .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reqby, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(supname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(dadded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(approvedby)
                    .addComponent(appby, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void categoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoriesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoriesActionPerformed

    
    
    
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField appby;
    private javax.swing.JLabel approvedby;
    private javax.swing.JTable approvedmedstable;
    private javax.swing.JTextField categories;
    private javax.swing.JTextField dadded;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField reqby;
    private javax.swing.JTextField reqid;
    private javax.swing.JTextField supname;
    private javax.swing.JLabel suptype;
    private javax.swing.JTextField type;
    // End of variables declaration//GEN-END:variables
}
