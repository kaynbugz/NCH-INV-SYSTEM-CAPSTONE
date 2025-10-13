package inspectpanelsSYSTEM;

    import com.toedter.calendar.JDateChooser;
import dashboardSYSTEM.ItemsManagement;
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
    import javax.swing.table.JTableHeader;


public class InspectSMOrders extends javax.swing.JPanel {

    private ItemsManagement itemsManagementPanel;
    private final String inspectedOrderId; // store the orderId being inspected

    public InspectSMOrders(ItemsManagement itemsPanel, String orderId) {
        this.itemsManagementPanel = itemsPanel;
        this.inspectedOrderId = orderId;
        initComponents();
         ordersinfo.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
        int row = ordersinfo.rowAtPoint(e.getPoint());
        int col = ordersinfo.columnAtPoint(e.getPoint());

        if (col == 7 && row != -1) { 
            JTextField remarksField = new JTextField();
            JPanel remarksPanel = new JPanel(new BorderLayout());
            remarksPanel.add(new JLabel("Enter remarks:"), BorderLayout.NORTH);
            remarksPanel.add(remarksField, BorderLayout.CENTER);

            int remarksOption = JOptionPane.showConfirmDialog(null, remarksPanel, "Enter Remarks", JOptionPane.OK_CANCEL_OPTION);
            if (remarksOption == JOptionPane.OK_OPTION) {
                String remarks = remarksField.getText().trim();
                ordersinfo.setValueAt(remarks, row, col); // ✅ Update Remarks column
            }
        }

        
   
        if (col == 4 && row != -1) { 
            String qtyOrderedStr = ordersinfo.getValueAt(row, 2).toString().trim(); // ✅ Correct index for Qty Ordered

            try {
                int qtyOrdered = Integer.parseInt(qtyOrderedStr);
                JTextField inputField = new JTextField();
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JLabel("Enter quantity received (max: " + qtyOrdered + "):"), BorderLayout.NORTH);
                panel.add(inputField, BorderLayout.CENTER);

                int option = JOptionPane.showConfirmDialog(null, panel, "Quantity Received Input", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String inputText = inputField.getText().trim();

                    if (!inputText.matches("\\d+")) { // ✅ Validate input: Check if it's a number
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                        return; // ✅ Exit if the input is invalid
                    }

                    int qtyReceived = Integer.parseInt(inputText); // ✅ Now safe to convert

                    if (qtyReceived > qtyOrdered) {
                        JOptionPane.showMessageDialog(null, "Quantity received cannot exceed quantity ordered.");
                    } else {
                        ordersinfo.setValueAt(qtyReceived, row, col); // ✅ Update Quantity Received column
                        String status = (qtyReceived == qtyOrdered) ? "Completed" : "Incomplete";
                        ordersinfo.setValueAt(status, row, 5); // ✅ Update Status column

                        // ✅ Generate 12-digit barcode
                        long min = 100000000000L;
                        long max = 999999999999L;
                        long randomBarcode = min + (long)(Math.random() * (max - min + 1));
                        String barcode = String.valueOf(randomBarcode);
                        ordersinfo.setValueAt(barcode, row, 6); // ✅ Set barcode in column 7
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid numeric format in Quantity Ordered. Please check the data.");
            }
            }
        }
    });

         // Set header background black and text white
        JTableHeader header = ordersinfo.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.PLAIN, 11)); // Optional: Customize font

        ordersinfo.setSelectionForeground(Color.BLACK);      // Optional: selected text color
        ordersinfo.setGridColor(Color.LIGHT_GRAY);            // Optional: grid line color
        ordersinfo.setFont(new Font("Arial", Font.PLAIN, 10)); // Optional: content font

        // Optional: disable column reordering
        header.setReorderingAllowed(false);

        // Optional: disable cell editing if needed
        ordersinfo.setDefaultEditor(Object.class, null);
     
   }       

    
    
    // Method to update UI labels with the fetched data
    public void updateOrderDetails(String orderedBy, String supplierName, String orderid, String category, String type, String status, String dateOrdered, String timeOrdered) {
        orderedby.setText(orderedBy);  // Update ordered by label
        supname.setText(supplierName); // Update supplier name label
        orid.setText(orderid);     // Update supplier ID label
        categories.setText(category);  // Update category label
        types.setText(type);           // Update type label
        statuss.setText(status);       // Update status label
        dateordered.setText(dateOrdered); // Update date ordered label
        timeordered.setText(timeOrdered); // Update time ordered label
    }
    

    public class DateChooserCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JDateChooser dateChooser = new JDateChooser();

    public DateChooserCellEditor() {
        dateChooser.setDateFormatString("yyyy-MM-dd");
    }

    @Override
    public Object getCellEditorValue() {
        Date selectedDate = dateChooser.getDate();
        if (selectedDate != null) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
        }
        return "";
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (value instanceof String) {
            try {
                Date parsedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse((String) value);
                dateChooser.setDate(parsedDate);
            } catch (Exception e) {
                dateChooser.setDate(null);
            }
        }
        return dateChooser;
    }
}
    
    
public void loadOrderItemsToTable(String orderId) {
    DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Allow editing for: Qty Received (4), Status (5), Barcode (6), Remarks (7)
            return column == 4 || column == 5 || column == 6 || column == 7;
        }
    };

    // Updated column headers to display "Description/Purpose"
    tableModel.setColumnIdentifiers(new String[] {
        "Item Name", "Description", "Qty Ordered", "Units", "Qty Received", "Status", "Barcode", "Remarks"
    });

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        // Fetch dcs instead of "description"
        String sql = "SELECT item_name, dcs, quantity, units FROM order_items WHERE order_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, orderId);
            try (ResultSet rs = pst.executeQuery()) {
                tableModel.setRowCount(0);  // Clear existing rows

                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    String dcs = rs.getString("dcs"); // this is your description/purpose
                    int quantity = rs.getInt("quantity");
                    String units = rs.getString("units");

                    if (units == null || units.trim().isEmpty()) {
                        units = "PCS";
                    }

                    tableModel.addRow(new Object[] {
                        itemName,
                        (dcs != null ? dcs : ""), // fallback to blank if null
                        quantity,
                        units,
                        "",         // Qty Received
                        "Pending",  // Status
                        "",         // Barcode
                        ""          // Remarks
                    });
                }

                ordersinfo.setModel(tableModel);

                // Set cell editors for editable fields
                ordersinfo.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField())); // Qty Received
                ordersinfo.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JTextField())); // Status
                ordersinfo.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JTextField())); // Barcode
                ordersinfo.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JTextField())); // Remarks

                ordersinfo.repaint();  // Refresh
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error loading order items: " + ex.getMessage());
        ex.printStackTrace();
    }
}

  
  
  
    @SuppressWarnings("unchecked")
    
    private int getNextReceivedId() {
    int nextId = 1; // Default to 2 if no records exist yet

    String query = "SELECT MAX(received_id) FROM received_items";
    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                nextId = rs.getInt(1) + 1; // Increment the last received_id
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching last received_id: " + e.getMessage());
    }
    
    return nextId;
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
        dateordered = new javax.swing.JLabel();
        timeordered = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ordersinfo = new javax.swing.JTable();
        ord_rec = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        supname = new javax.swing.JLabel();
        categories = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        statuss = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/inspection.png"))); // NOI18N
        jLabel1.setText("Supplies & Materials Inspection");

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

        dateordered.setBackground(new java.awt.Color(255, 249, 103));
        dateordered.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        dateordered.setForeground(new java.awt.Color(255, 0, 0));
        dateordered.setText(" ");

        timeordered.setBackground(new java.awt.Color(255, 249, 103));
        timeordered.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        timeordered.setForeground(new java.awt.Color(255, 0, 0));
        timeordered.setText(" ");

        ordersinfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ordersinfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Item Name", "Description", "Quantity Ordered", "Units", "Quantity Received", "Status", "Barcode", "Remarks"
            }
        ));
        ordersinfo.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(ordersinfo);

        ord_rec.setBackground(new java.awt.Color(0, 0, 0));
        ord_rec.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ord_rec.setForeground(new java.awt.Color(255, 255, 255));
        ord_rec.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/plus (2).png"))); // NOI18N
        ord_rec.setText("Ordered Received");
        ord_rec.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ord_recMouseClicked(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/delete (1).png"))); // NOI18N
        jButton3.setText("Cancel Order");

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

        statuss.setBackground(new java.awt.Color(255, 249, 103));
        statuss.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        statuss.setForeground(new java.awt.Color(255, 0, 0));
        statuss.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jLabel2)
                            .addComponent(jLabel8)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(statuss, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(orid, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                            .addComponent(orderedby, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(types, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel6)))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dateordered, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(timeordered, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(28, 28, 28))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(categories, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(supname, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(ord_rec)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton3))))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(78, 78, 78)
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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(timeordered))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dateordered)
                            .addComponent(jLabel6))
                        .addGap(97, 97, 97)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(categories))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(supname))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statuss)
                            .addComponent(jLabel9))))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(ord_rec))
                .addContainerGap(17, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


 public String getInspectedOrderId() {
        return inspectedOrderId;
    }

    private void ord_recMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ord_recMouseClicked
    int rowCount = ordersinfo.getRowCount();
    if (rowCount == 0) {
        JOptionPane.showMessageDialog(null, "No items found in the table.");
        return;
    }

    // Generate new received ID (REC-001, REC-002, ...)
    String receivedId = "";
    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                "SELECT ISNULL(MAX(CAST(SUBSTRING(received_id, 5, LEN(received_id)) AS INT)), 0) + 1 AS nextId FROM received_items WHERE received_id LIKE 'REC-%'")) {
            if (rs.next()) {
                int nextId = rs.getInt("nextId");
                receivedId = String.format("REC-%03d", nextId);
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error generating received ID: " + e.getMessage());
        return;
    }

    // Input dialog for receiving info
    JDateChooser dateReceivedChooser = new JDateChooser();
    dateReceivedChooser.setDateFormatString("yyyy-MM-dd");
    JTextField receiveByField = new JTextField();

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
    inputPanel.add(new JLabel("Date Received:"));
    inputPanel.add(dateReceivedChooser);
    inputPanel.add(new JLabel("Received By:"));
    inputPanel.add(receiveByField);

    int inputOption = JOptionPane.showConfirmDialog(null, inputPanel, "Receiving Info", JOptionPane.OK_CANCEL_OPTION);
    if (inputOption != JOptionPane.OK_OPTION) return;

    Date dateReceived = dateReceivedChooser.getDate();
    String receiveBy = receiveByField.getText().trim();

    if (dateReceived == null || receiveBy.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Date and receiver must be provided.");
        return;
    }

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        conn.setAutoCommit(false);

        String orderId = getInspectedOrderId(); 
        if (orderId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No order selected.");
            return;
        }

        // Fetch supplier details
        String supplierName = "", supplierType = "";
        try (PreparedStatement ps = conn.prepareStatement("SELECT supplier_name, type FROM orders_info WHERE order_id = ?")) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    supplierName = rs.getString("supplier_name");
                    supplierType = rs.getString("type");
                }
            }
        }

        // Generate new supmat ID
        int nextSupmatId = 1;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ISNULL(MAX(CAST(SUBSTRING(supmat, 7, LEN(supmat)) AS INT)), 0) + 1 AS nextId FROM supplies_materials WHERE supmat LIKE 'SUPMAT%'")) {
            if (rs.next()) nextSupmatId = rs.getInt("nextId");
        }
        String supmat = String.format("SUPMAT%03d", nextSupmatId);

        // Prepare insert queries
        String insertReceived = "INSERT INTO received_items (received_id, item_name, description, quantity_ordered, units, quantity_received, status, barcode, remarks, date_received, receive_by, order_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertSupply = "INSERT INTO supplies_materials (supmat, item_name, description, units, qty_stocks, date_receive, supplier_name, type, barcodes, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psReceived = conn.prepareStatement(insertReceived);
             PreparedStatement psSupply = conn.prepareStatement(insertSupply)) {

            for (int i = 0; i < rowCount; i++) {
                String itemName = ordersinfo.getValueAt(i, 0).toString().trim();
                String description = ordersinfo.getValueAt(i, 1) != null ? ordersinfo.getValueAt(i, 1).toString().trim() : "N/A";
                int qtyOrdered = Integer.parseInt(ordersinfo.getValueAt(i, 2).toString().trim());
                String units = ordersinfo.getValueAt(i, 3).toString().trim();
                int qtyReceived = Integer.parseInt(ordersinfo.getValueAt(i, 4).toString().trim());
                String status = ordersinfo.getValueAt(i, 5).toString().trim();
                String barcode = ordersinfo.getValueAt(i, 6).toString().trim();
                String remarks = ordersinfo.getValueAt(i, 7) != null ? ordersinfo.getValueAt(i, 7).toString().trim() : "";

                // Add to received_items batch
                psReceived.setString(1, receivedId);
                psReceived.setString(2, itemName);
                psReceived.setString(3, description);
                psReceived.setInt(4, qtyOrdered);
                psReceived.setString(5, units);
                psReceived.setInt(6, qtyReceived);
                psReceived.setString(7, status);
                psReceived.setString(8, barcode);
                psReceived.setString(9, remarks);
                psReceived.setDate(10, new java.sql.Date(dateReceived.getTime()));
                psReceived.setString(11, receiveBy);
                psReceived.setString(12, orderId);
                psReceived.addBatch();

                // Add to supplies_materials batch
                psSupply.setString(1, supmat);
                psSupply.setString(2, itemName);
                psSupply.setString(3, description);
                psSupply.setString(4, units);
                psSupply.setInt(5, qtyReceived);
                psSupply.setDate(6, new java.sql.Date(dateReceived.getTime()));
                psSupply.setString(7, supplierName);
                psSupply.setString(8, supplierType);
                psSupply.setString(9, barcode);
                psSupply.setString(10, remarks);
                psSupply.addBatch();
            }

            psReceived.executeBatch();
            psSupply.executeBatch();

            // Update order status
            try (PreparedStatement ps = conn.prepareStatement("UPDATE orders_info SET status = 'Pending' WHERE order_id = ?")) {
                ps.setString(1, orderId);
                ps.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(null, rowCount + " item(s) received successfully.\nReceived ID: " + receivedId);

        } catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Error during save: " + e.getMessage());
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Connection error: " + e.getMessage());
    }
    }//GEN-LAST:event_ord_recMouseClicked


    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categories;
    private javax.swing.JLabel dateordered;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton ord_rec;
    private javax.swing.JLabel orderedby;
    private javax.swing.JTable ordersinfo;
    private javax.swing.JLabel orid;
    private javax.swing.JLabel statuss;
    private javax.swing.JLabel supname;
    private javax.swing.JLabel timeordered;
    private javax.swing.JLabel types;
    // End of variables declaration//GEN-END:variables
}
