package AddingStocks;

    import requestpanelsSYSTEM.*;
    import com.toedter.calendar.JDateChooser;
import dashboardSYSTEM.homepageSYSTEM;
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
    import javax.swing.DefaultListCellRenderer;
    import javax.swing.JList;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;


    

    public class StockRequestPanel extends javax.swing.JPanel {

    public StockRequestPanel() {
        initComponents();
        loadStockRequestedTable();
        loadStockHistorydTable();

        // reload requested table when search changes
        searchfield.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadStockRequestedTable(); }
            public void removeUpdate(DocumentEvent e) { loadStockRequestedTable(); }
            public void changedUpdate(DocumentEvent e) { loadStockRequestedTable(); }
        });

        // reload requested table when date changes
        PropertyChangeListener requestedDateListener = evt -> {
            if ("date".equals(evt.getPropertyName())) {
                loadStockRequestedTable();
            }
        };
        fromdate.addPropertyChangeListener(requestedDateListener);
        todate.addPropertyChangeListener(requestedDateListener);

        // reload history table when search changes
        searchfieldh.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadStockHistorydTable(); }
            public void removeUpdate(DocumentEvent e) { loadStockHistorydTable(); }
            public void changedUpdate(DocumentEvent e) { loadStockHistorydTable(); }
        });

        // reload history table when date changes
        PropertyChangeListener historyDateListener = evt -> {
            if ("date".equals(evt.getPropertyName())) {
                loadStockHistorydTable();
            }
        };
        fdateh.addPropertyChangeListener(historyDateListener);
        todateh.addPropertyChangeListener(historyDateListener);
    }

  private void loadStockTable(DefaultTableModel model, JTextField searchField, JDateChooser fromDate, JDateChooser toDate,
                            JTextField totalReqField, JTextField totalQtyField, JTextField totalMedsField,
                            String statusFilter) {

    model.setRowCount(0);

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    java.util.Date from = fromDate.getDate();
    java.util.Date to = toDate.getDate();
    String search = searchField.getText().trim();

    StringBuilder query = new StringBuilder("""
        SELECT 
            request_id,
            requested_by,
            request_date,
            supplier_name,
            supplier_type,
            stock_status,
            COUNT(DISTINCT generic_id) AS total_medicines,
            SUM(quantity_requested) AS total_quantity_requested
        FROM stock_request_medicines
        WHERE stock_status = ?
    """);

    if (from != null && to != null) {
        query.append(" AND request_date BETWEEN ? AND ? ");
    }
    if (!search.isEmpty()) {
        query.append("""
            AND (
                request_id LIKE ? OR
                requested_by LIKE ? OR
                supplier_name LIKE ? OR
                supplier_type LIKE ? OR
                stock_status LIKE ?
            )
        """);
    }

    query.append("""
        GROUP BY 
            request_id, requested_by, request_date, supplier_name, supplier_type, stock_status
        ORDER BY request_date DESC
    """);

    int totalRequests = 0;
    int grandTotalQty = 0;
    int grandTotalMeds = 0;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement pst = conn.prepareStatement(query.toString())) {

        int paramIndex = 1;
        pst.setString(paramIndex++, statusFilter); // filter by status

        if (from != null && to != null) {
            pst.setDate(paramIndex++, new java.sql.Date(from.getTime()));
            pst.setDate(paramIndex++, new java.sql.Date(to.getTime()));
        }

        if (!search.isEmpty()) {
            String like = "%" + search + "%";
            for (int i = 0; i < 5; i++) pst.setString(paramIndex++, like);
        }

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int totalMeds = rs.getInt("total_medicines");
                int totalQty = rs.getInt("total_quantity_requested");

                Object[] row = {
                    rs.getString("request_id"),
                    rs.getString("requested_by"),
                    rs.getString("request_date"),
                    totalMeds,
                    totalQty,
                    rs.getString("supplier_name"),
                    rs.getString("supplier_type"),
                    rs.getString("stock_status")
                };

                model.addRow(row);

                totalRequests++;
                grandTotalQty += totalQty;
                grandTotalMeds += totalMeds;
            }
        }

        totalReqField.setText(String.valueOf(totalRequests));
        totalQtyField.setText(String.valueOf(grandTotalQty));
        totalMedsField.setText(String.valueOf(grandTotalMeds));

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
    }
}
 
    
    public void loadStockRequestedTable() {
        loadStockTable(
            (DefaultTableModel) stockreqtable.getModel(),
            searchfield, fromdate, todate,
            trequested, totalqtyrequested, totalmedicines,
            "Pending Stocks"
        );
    }


    public void loadStockHistorydTable() {
        loadStockTable(
            (DefaultTableModel) stockhistorytable.getModel(),
            searchfieldh, fdateh, todateh,
            trequestedh, totalqtyrequestedh, totalmedicinesh,
            "Approved"
        );
    }

    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stockreqtable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        searchfield = new javax.swing.JTextField();
        fromdate = new com.toedter.calendar.JDateChooser();
        todate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        refresh = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        trequested = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        totalqtyrequested = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        searchreqid = new javax.swing.JTextField();
        view = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        totalmedicines = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        stockhistorytable = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        searchfieldh = new javax.swing.JTextField();
        fdateh = new com.toedter.calendar.JDateChooser();
        todateh = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        refresh1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        trequestedh = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        totalqtyrequestedh = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        searchreqidh = new javax.swing.JTextField();
        viewbtn = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        totalmedicinesh = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(48, 122, 55));
        jPanel1.setPreferredSize(new java.awt.Dimension(120, 510));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Stock Approval Panel");

        jTabbedPane2.setBackground(new java.awt.Color(255, 249, 103));

        jPanel2.setBackground(new java.awt.Color(255, 249, 103));

        stockreqtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Requested ID", "Requested By", "Requested Date", "Quantity Medicines", "Quantity Requested", "Supplier Name", "Supplier Type", "Stock Status"
            }
        ));
        jScrollPane1.setViewportView(stockreqtable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("To Date");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Search");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("From Date");

        refresh.setBackground(new java.awt.Color(0, 0, 0));
        refresh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        refresh.setForeground(new java.awt.Color(255, 255, 255));
        refresh.setText("Refresh");
        refresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refreshMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Total Of Request");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Total Of Quantity Requested");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Enter a ID");

        view.setBackground(new java.awt.Color(0, 0, 0));
        view.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        view.setForeground(new java.awt.Color(255, 255, 255));
        view.setText("View");
        view.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Total Of Medicines");

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Print");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(2, 2, 2)
                        .addComponent(searchfield, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(2, 2, 2)
                        .addComponent(fromdate, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(todate, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(2, 2, 2)
                            .addComponent(trequested, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(2, 2, 2)
                            .addComponent(jLabel6)
                            .addGap(2, 2, 2)
                            .addComponent(totalqtyrequested, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(2, 2, 2)
                            .addComponent(jLabel8)
                            .addGap(2, 2, 2)
                            .addComponent(totalmedicines, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7)
                            .addGap(2, 2, 2)
                            .addComponent(searchreqid, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(view)
                .addGap(40, 40, 40))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(refresh))
                    .addComponent(fromdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(todate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jButton3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(trequested, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(totalqtyrequested, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(searchreqid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(totalmedicines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(view)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Stock Requested", jPanel2);

        jPanel4.setBackground(new java.awt.Color(255, 249, 103));

        stockhistorytable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Requested ID", "Requested By", "Requested Date", "Quantity Medicines", "Quantity Requested", "Supplier Name", "Supplier Type", "Stock Status"
            }
        ));
        jScrollPane2.setViewportView(stockhistorytable);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("To Date");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Search");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("From Date");

        refresh1.setBackground(new java.awt.Color(0, 0, 0));
        refresh1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        refresh1.setForeground(new java.awt.Color(255, 255, 255));
        refresh1.setText("Refresh");
        refresh1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refresh1MouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Total Of Request");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Total Of Quantity Requested");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Enter a ID");

        viewbtn.setBackground(new java.awt.Color(0, 0, 0));
        viewbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewbtn.setForeground(new java.awt.Color(255, 255, 255));
        viewbtn.setText("View");
        viewbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewbtnMouseClicked(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("Total Of Medicines");

        jButton4.setBackground(new java.awt.Color(0, 0, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Print");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(2, 2, 2)
                        .addComponent(searchfieldh, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refresh1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addGap(2, 2, 2)
                        .addComponent(fdateh, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(todateh, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addGap(2, 2, 2)
                            .addComponent(trequestedh, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(2, 2, 2)
                            .addComponent(jLabel13)
                            .addGap(2, 2, 2)
                            .addComponent(totalqtyrequestedh, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(2, 2, 2)
                            .addComponent(jLabel15)
                            .addGap(2, 2, 2)
                            .addComponent(totalmedicinesh, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14)
                            .addGap(2, 2, 2)
                            .addComponent(searchreqidh, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(viewbtn)
                .addGap(40, 40, 40))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel11)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchfieldh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(refresh1)))
                    .addComponent(fdateh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(todateh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(jButton4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(trequestedh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(totalqtyrequestedh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(searchreqidh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(totalmedicinesh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(viewbtn)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1152, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 482, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("Stock Added History", jPanel3);

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/left.png"))); // NOI18N
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16)
                        .addGap(436, 436, 436)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1202, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refresh1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refresh1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_refresh1MouseClicked

    private void viewbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewbtnMouseClicked
        String requestId = searchreqidh.getText().trim(); // Get request ID from input field

        if (requestId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a valid Request ID.");
            return;
        }

        // Create and show the approval panel
        StockAddedPrint panel = new StockAddedPrint(requestId);
        JFrame frame = new JFrame("Approved Stocks for " + requestId);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }//GEN-LAST:event_viewbtnMouseClicked

    private void viewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewMouseClicked
        String requestId = searchreqid.getText().trim(); // Get request ID from input field

        if (requestId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a valid Request ID.");
            return;
        }

        // Create and show the approval panel
        ApprovalStocksPanel panel = new ApprovalStocksPanel(requestId);
        JFrame frame = new JFrame("Approved Stocks for " + requestId);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }//GEN-LAST:event_viewMouseClicked

    private void refreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshMouseClicked
        // clear filters
        fromdate.setDate(null);
        todate.setDate(null);
        searchfield.setText("");
        // clear total labels/textfields
        trequested.setText("0");
        totalqtyrequested.setText("0");
        totalmedicines.setText("0");
        // reload full table data
        loadStockRequestedTable();
    }//GEN-LAST:event_refreshMouseClicked

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        // Get the parent window (JFrame) of this panel
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose(); // Close the window
        }

        // Open the homepage window
        homepageSYSTEM homepage = new homepageSYSTEM();
        homepage.setVisible(true);
    }//GEN-LAST:event_jLabel16MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser fdateh;
    private com.toedter.calendar.JDateChooser fromdate;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JButton refresh;
    private javax.swing.JButton refresh1;
    private javax.swing.JTextField searchfield;
    private javax.swing.JTextField searchfieldh;
    private javax.swing.JTextField searchreqid;
    private javax.swing.JTextField searchreqidh;
    private javax.swing.JTable stockhistorytable;
    private javax.swing.JTable stockreqtable;
    private com.toedter.calendar.JDateChooser todate;
    private com.toedter.calendar.JDateChooser todateh;
    private javax.swing.JTextField totalmedicines;
    private javax.swing.JTextField totalmedicinesh;
    private javax.swing.JTextField totalqtyrequested;
    private javax.swing.JTextField totalqtyrequestedh;
    private javax.swing.JTextField trequested;
    private javax.swing.JTextField trequestedh;
    private javax.swing.JButton view;
    private javax.swing.JButton viewbtn;
    // End of variables declaration//GEN-END:variables
}
