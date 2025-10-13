
package AddingStocks;

    import requestingMedicinesSubmit.*;
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


public class ApprovalStocksPanel extends javax.swing.JPanel {

    private String searchreqid;

    public ApprovalStocksPanel(String searchreqid) {
    this.searchreqid = searchreqid;
    initComponents();
    loadApprovalStockData(searchreqid);
    }

    public void loadApprovalStockData(String requestId) {
    DefaultTableModel model = new DefaultTableModel(
        new Object[]{
            "Generic ID", "Medicine Name", "Units", "Description",
            "Quantity Requested", "Mfg Date", "Exp Date", "Batch No", "Status"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    approvalstocktable.setModel(model);

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String sql = """
        SELECT
            srm.generic_id,
            m.GenericName,
            m.Units,
            m.Description,
            srm.quantity_requested,
            m.MfgDate,
            m.ExpDate,
            m.BatchNo,
            srm.stock_status,
            srm.supplier_name,
            srm.supplier_type,
            srm.requested_by,
            srm.request_date
        FROM stock_request_medicines srm
        LEFT JOIN medicines m ON srm.generic_id = m.GenericID
        WHERE srm.request_id = ?
        ORDER BY srm.request_date DESC, srm.request_time DESC
    """;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, requestId);
        try (ResultSet rs = ps.executeQuery()) {
            boolean firstRow = true;

            while (rs.next()) {
                Object[] row = {
                    rs.getString("generic_id"),
                    rs.getString("GenericName"),
                    rs.getString("Units"),
                    rs.getString("Description"),
                    rs.getInt("quantity_requested"),
                    rs.getDate("MfgDate"),
                    rs.getDate("ExpDate"),
                    rs.getString("BatchNo"),
                    rs.getString("stock_status")
                };
                model.addRow(row);

                // Populate text fields only once (from first row)
                if (firstRow) {
                    suppliernamed.setText(rs.getString("supplier_name"));
                    suppliertyped.setText(rs.getString("supplier_type"));
                    reqby.setText(rs.getString("requested_by"));
                    reqdate.setText(rs.getString("request_date"));
                    firstRow = false;
                }
            }

        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading approval stock data: " + ex.getMessage());
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
        approvalstocktable = new javax.swing.JTable();
        approvedreq = new javax.swing.JButton();
        rejectedbtn = new javax.swing.JButton();
        reqby = new javax.swing.JTextField();
        suppliertyped = new javax.swing.JTextField();
        reqdate = new javax.swing.JTextField();
        approvedallbtn = new javax.swing.JButton();
        rejectedall = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        suppliernamed = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Requested By");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Supplier Type:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Request Date");

        approvalstocktable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Generic ID", "Medicine Name", "Units", "Description", "Quantity Request", "Mfg Date", "Exp Date", "BatchNo", "Status"
            }
        ));
        jScrollPane1.setViewportView(approvalstocktable);

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

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Supplier");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(approvedallbtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rejectedall)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(approvedreq)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rejectedbtn))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(suppliertyped, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(suppliernamed, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(reqby, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(reqdate))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(reqby, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(suppliernamed, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(suppliertyped, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(reqdate, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    
    int selectedRow = approvalstocktable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, "Please select a row to approve.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) approvalstocktable.getModel();
    String genericId = model.getValueAt(selectedRow, 0).toString(); // "Generic ID"
    String requestId = searchreqid; // already stored in your panel

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String updateSql = """
        UPDATE stock_request_medicines
        SET stock_status = 'Approved'
        WHERE request_id = ? AND generic_id = ?
    """;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement ps = conn.prepareStatement(updateSql)) {

        ps.setString(1, requestId);
        ps.setString(2, genericId);

        int updated = ps.executeUpdate();
        if (updated > 0) {
            JOptionPane.showMessageDialog(null, "Request approved successfully.");
            loadApprovalStockData(requestId); // refresh table
        } else {
            JOptionPane.showMessageDialog(null, "No matching record found to approve.");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
    }//GEN-LAST:event_approvedreqMouseClicked
    }
    private void rejectedbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedbtnMouseClicked
   
    }//GEN-LAST:event_rejectedbtnMouseClicked
   
   
    
    
    
    private void rejectedbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedbtnMouseEntered
    
    }//GEN-LAST:event_rejectedbtnMouseEntered
 
    private void approvedallbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedallbtnMouseClicked

    }//GEN-LAST:event_approvedallbtnMouseClicked

    private void approvedallbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approvedallbtnMouseEntered

    }//GEN-LAST:event_approvedallbtnMouseEntered

    private void rejectedallMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rejectedallMouseClicked
 
    }//GEN-LAST:event_rejectedallMouseClicked
    
    
  

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable approvalstocktable;
    private javax.swing.JButton approvedallbtn;
    private javax.swing.JButton approvedreq;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton rejectedall;
    private javax.swing.JButton rejectedbtn;
    private javax.swing.JTextField reqby;
    private javax.swing.JTextField reqdate;
    private javax.swing.JTextField suppliernamed;
    private javax.swing.JTextField suppliertyped;
    // End of variables declaration//GEN-END:variables

}