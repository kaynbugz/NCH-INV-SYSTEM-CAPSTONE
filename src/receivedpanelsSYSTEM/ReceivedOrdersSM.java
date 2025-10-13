
package receivedpanelsSYSTEM;

    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
import java.awt.Color;
    import java.awt.Component;
import java.awt.Font;
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
import javax.swing.table.JTableHeader;
    import javax.swing.table.TableCellEditor;
    import javax.swing.table.TableColumn;
    import javax.swing.text.AttributeSet;
    import javax.swing.text.BadLocationException;
    import javax.swing.text.PlainDocument;


public class ReceivedOrdersSM extends javax.swing.JPanel {

    
     private String receivedId;
    
    public ReceivedOrdersSM(String receivedId) { 
           initComponents();
            this.receivedId = receivedId;  // Set receivedId
            loadReceivedItemsToTable(this.receivedId);  // Pass receivedId to the method
            loadOrderDetails(this.receivedId);
            loadReceivedItemDetails(this.receivedId);
            
             // Set header background black and text white
        JTableHeader header = ordersinfo.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.PLAIN, 11)); // Optional: Customize font

        ordersinfo.setSelectionForeground(Color.BLACK);      // Optional: selected text color
        ordersinfo.setGridColor(Color.LIGHT_GRAY);            // Optional: grid line color
        ordersinfo.setFont(new Font("Arial", Font.PLAIN, 9)); // Optional: content font

        // Optional: disable column reordering
        header.setReorderingAllowed(false);

        // Optional: disable cell editing if needed
        ordersinfo.setDefaultEditor(Object.class, null);
    }       
    
    
    // Method to load received items into the table
public void loadReceivedItemsToTable(String receivedId) {
    String[] columns = {
        "Item Name", "Description", "Qty Ordered", "Units", "Qty Received",
        "Status", "Barcode", "Remarks"
    };

    DefaultTableModel model = new DefaultTableModel(columns, 0);
    ordersinfo.setModel(model);

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        String itemsQuery = "SELECT item_name, description, quantity_ordered, units, quantity_received, " +
                            "status, barcode, remarks " +  // ✅ Removed date_expiration
                            "FROM received_items WHERE received_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(itemsQuery)) {
            pstmt.setString(1, receivedId);  // Use the receivedId as the parameter
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("item_name"),
                    rs.getString("description"),  // ✅ Added description column
                    rs.getInt("quantity_ordered"),
                    rs.getString("units"),
                    rs.getInt("quantity_received"),
                    rs.getString("status"),
                    rs.getString("barcode"),
                    rs.getString("remarks")  // ✅ Added remarks column
                });
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading received items: " + e.getMessage());
    }
}

// Method to load received item details
public void loadReceivedItemDetails(String receivedId) {
    String receivedItemsQuery = "SELECT date_received, received_id, receive_by, remarks " +
                                "FROM received_items " +
                                "WHERE received_id = ?";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        try (PreparedStatement pstmt = conn.prepareStatement(receivedItemsQuery)) {
            pstmt.setString(1, receivedId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp dateReceived = rs.getTimestamp("date_received");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = sdf.format(new Date(dateReceived.getTime()));

                    dt_received.setText(formattedDate);
                    idreceive.setText(rs.getString("received_id"));
                    rcv_by.setText(rs.getString("receive_by"));
                } else {
                    JOptionPane.showMessageDialog(null, "No matching received item details found.");
                }
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading received item details: " + e.getMessage());
        e.printStackTrace();
    }
}

    // Method to load order details
  public void loadOrderDetails(String receivedId) {
       // Adjust the query to ensure correct relationship
    String orderDetailsQuery = "SELECT oi.order_id, oi.supplier_name, oi.ordered_by, oi.category, oi.type, oi.status, oi.date_ordered, oi.time_ordered " +
                               "FROM orders_info oi " +
                               "JOIN received_items ri ON oi.order_id = ri.order_id " +
                               "WHERE ri.received_id = ?";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        try (PreparedStatement pstmt = conn.prepareStatement(orderDetailsQuery)) {
            pstmt.setString(1, receivedId);  // Set received_id parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Set values to your GUI fields
                    orderedby.setText(rs.getString("ordered_by"));
                    orid.setText(rs.getString("order_id"));  // Ensure this is showing order_id
                    types.setText(rs.getString("type"));
                    categories.setText(rs.getString("category"));
                    supname.setText(rs.getString("supplier_name"));
                    status.setText("Received");
                    dateo.setText(rs.getString("date_ordered"));

                    // Format time_ordered as hh:mm AM/PM
                    Time sqlTime = rs.getTime("time_ordered");
                    if (sqlTime != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        String formattedTime = sdf.format(sqlTime);
                        timeo.setText(formattedTime);
                    } else {
                        timeo.setText(""); // fallback if null
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No matching order details found for received ID: " + receivedId);
                }
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading order details: " + e.getMessage());
        e.printStackTrace();
    }
}
 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        orderedby = new javax.swing.JLabel();
        orid = new javax.swing.JLabel();
        types = new javax.swing.JLabel();
        timeo = new javax.swing.JLabel();
        dateo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ordersinfo = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        supname = new javax.swing.JLabel();
        categories = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        status = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        dt_received = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        idreceive = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        rcv_by = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/inspection.png"))); // NOI18N
        jLabel1.setText("Received Orders Materials & Supplies");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Order ID:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Supplier Name:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Ordered By:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Date Ordered:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Time Ordered:");

        orderedby.setBackground(new java.awt.Color(255, 249, 103));
        orderedby.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        orderedby.setForeground(new java.awt.Color(255, 0, 0));
        orderedby.setText(" ");

        orid.setBackground(new java.awt.Color(255, 249, 103));
        orid.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        orid.setForeground(new java.awt.Color(255, 0, 0));
        orid.setText(" ");

        types.setBackground(new java.awt.Color(255, 249, 103));
        types.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        types.setForeground(new java.awt.Color(255, 0, 0));

        timeo.setBackground(new java.awt.Color(255, 249, 103));
        timeo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        timeo.setForeground(new java.awt.Color(255, 0, 0));
        timeo.setText(" ");

        dateo.setBackground(new java.awt.Color(255, 249, 103));
        dateo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        dateo.setForeground(new java.awt.Color(255, 0, 0));
        dateo.setText(" ");

        ordersinfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ordersinfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Item Name", "Quantity Ordered", "Units", "Quantity Received", "Date Expiration", "Status", "Barcode"
            }
        ));
        ordersinfo.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(ordersinfo);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Type:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Categories:");

        supname.setBackground(new java.awt.Color(255, 249, 103));
        supname.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        supname.setForeground(new java.awt.Color(255, 0, 0));
        supname.setText(" ");

        categories.setBackground(new java.awt.Color(255, 249, 103));
        categories.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        categories.setForeground(new java.awt.Color(255, 0, 0));
        categories.setText(" ");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Status:");

        status.setBackground(new java.awt.Color(255, 249, 103));
        status.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        status.setForeground(new java.awt.Color(255, 0, 0));
        status.setText(" ");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Date Received :");

        dt_received.setBackground(new java.awt.Color(255, 255, 255));
        dt_received.setForeground(new java.awt.Color(255, 0, 0));
        dt_received.setText(" ");

        jButton1.setBackground(new java.awt.Color(48, 122, 55));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("PRINT");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Received ID:");

        idreceive.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        idreceive.setForeground(new java.awt.Color(255, 102, 0));
        idreceive.setText(" ");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Received By :");

        rcv_by.setBackground(new java.awt.Color(255, 255, 255));
        rcv_by.setForeground(new java.awt.Color(255, 0, 0));
        rcv_by.setText("    ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(53, 53, 53)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel8)))
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idreceive, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(categories, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(supname, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                                        .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(orid, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                    .addComponent(orderedby, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(types, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(timeo, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dateo, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(28, 28, 28))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rcv_by, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel11)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dt_received, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(16, 16, 16))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(dateo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(timeo)
                            .addComponent(jLabel6))
                        .addGap(97, 97, 97))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(idreceive))
                        .addGap(68, 68, 68)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(orderedby))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(orid))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(types, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(categories)
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(supname)
                            .addComponent(jLabel3))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(status)
                            .addComponent(jLabel9))))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(dt_received, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(rcv_by))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(19, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    
    
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categories;
    private javax.swing.JLabel dateo;
    private javax.swing.JLabel dt_received;
    private javax.swing.JLabel idreceive;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel orderedby;
    private javax.swing.JTable ordersinfo;
    private javax.swing.JLabel orid;
    private javax.swing.JLabel rcv_by;
    private javax.swing.JLabel status;
    private javax.swing.JLabel supname;
    private javax.swing.JLabel timeo;
    private javax.swing.JLabel types;
    // End of variables declaration//GEN-END:variables
}
