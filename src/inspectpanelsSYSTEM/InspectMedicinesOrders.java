package inspectpanelsSYSTEM;


    import com.toedter.calendar.JDateChooser;
    import dashboardSYSTEM.ItemsManagement;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import inspectpanelsSYSTEM.JTextAreaRenderer;
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Dimension;
    import java.awt.Font;
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
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;
    import java.sql.Types;
    import javax.swing.ImageIcon;
    import javax.swing.JButton;
    import javax.swing.JDialog;
    import javax.swing.SwingConstants;
    import javax.swing.table.TableCellRenderer;
    import orderlistspanelsSYSTEM.QuantityCellRenderer;
    import orderlistspanelsSYSTEM.TextAreaRenderer;


    
public class InspectMedicinesOrders extends javax.swing.JPanel {
    
    private ItemsManagement itemsManagementPanel;
    private final String inspectedOrderId;
    private String currentReceivingStatus="";


    public InspectMedicinesOrders(ItemsManagement itemsPanel, String orderId) {
        this.itemsManagementPanel = itemsPanel;
        this.inspectedOrderId = orderId;
        initComponents();
      JTableHeader header = ordersinfo.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.BLACK);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        ordersinfo.setRowHeight(30);
        TableColumn mfgDateColumn = ordersinfo.getColumnModel().getColumn(3);
        TableColumn expDateColumn = ordersinfo.getColumnModel().getColumn(4);
        mfgDateColumn.setCellEditor(new DateChooserCellEditor());
        expDateColumn.setCellEditor(new DateChooserCellEditor());
    }

    private void handleQuantityReceivedInput(int row) {
        String qtyOrderedStr = String.valueOf(ordersinfo.getValueAt(row, 1)).trim();
        try {
            int qtyOrdered = Integer.parseInt(qtyOrderedStr);
            JTextField inputField = new JTextField();
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Enter quantity received (max: " + qtyOrdered + "):"), BorderLayout.NORTH);
            panel.add(inputField, BorderLayout.CENTER);

            int opt = JOptionPane.showConfirmDialog(null, panel, "Quantity Received Input", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                int qtyReceived = Integer.parseInt(inputField.getText().trim());
                if (qtyReceived > qtyOrdered) {
                    JOptionPane.showMessageDialog(null, "Quantity received cannot exceed quantity ordered.");
                } else {
                    ordersinfo.setValueAt(qtyReceived, row, 2);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid quantity.");
        }
    }

    private void handleTextInput(int row, int col, String message) {
        String current = String.valueOf(ordersinfo.getValueAt(row, col));
        String input = JOptionPane.showInputDialog(null, message, current);
        if (input != null) ordersinfo.setValueAt(input.trim(), row, col);
    }

    public void updateOrderDetails(String orderedBy, String supplierName, String orderid,
                               String category, String type, String status,
                               String dateOrdered, String timeOrdered) {

    Color whiteBG = Color.WHITE;

    orderedby.setText(orderedBy);
    orderedby.setEditable(false);
    orderedby.setBackground(whiteBG);


    supname.setText(supplierName);
    supname.setEditable(false);
    supname.setBackground(whiteBG);


    orid.setText(orderid);
    orid.setEditable(false);
    orid.setBackground(whiteBG);


    categories.setText(category);
    categories.setEditable(false);
    categories.setBackground(whiteBG);


    types.setText(type);
    types.setEditable(false);
    types.setBackground(whiteBG);


    statuss.setText("Pending Inspection");
    statuss.setEditable(false);
    statuss.setBackground(whiteBG);

    dateordered.setText(dateOrdered);
    dateordered.setEditable(false);
    dateordered.setBackground(whiteBG);

    timeordered.setText(timeOrdered);
    timeordered.setEditable(false);
    timeordered.setBackground(whiteBG);

}
    
  public class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel = new JPanel(new BorderLayout(2, 0));
    private final JTextField txtQty = new JTextField("0", 3);
    private final JButton btnPlus;
    private final JButton btnMinus;
    private int maxValue = Integer.MAX_VALUE; // max allowed
    private int minValue = 0; // min allowed = current received
    private boolean incrementOnly = false; // always increment-only

    public QuantityCellEditor() {
        txtQty.setHorizontalAlignment(JTextField.CENTER);
        txtQty.setPreferredSize(new Dimension(60, 24));

        btnPlus = new JButton(new ImageIcon(getClass().getResource("/nch/addpo.png")));
        btnMinus = new JButton(new ImageIcon(getClass().getResource("/nch/minuspo.png")));

        btnPlus.setBackground(Color.WHITE);
        btnMinus.setBackground(Color.WHITE);
        btnPlus.setOpaque(true);
        btnMinus.setOpaque(true);
        btnPlus.setBorderPainted(false);
        btnMinus.setBorderPainted(false);
        btnPlus.setFocusPainted(false);
        btnMinus.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 2, 0));
        btnPlus.setPreferredSize(new Dimension(24, 24));
        btnMinus.setPreferredSize(new Dimension(24, 24));
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
        if (incrementOnly) return; // cannot decrement
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
     int qtyReceived = 0;

     try {
         qtyOrdered = Integer.parseInt(table.getValueAt(row, 1).toString()); // Qty Ordered
         qtyReceived = value != null ? Integer.parseInt(value.toString()) : 0;
     } catch (Exception ignored) {}

     minValue = 0;              // allow decrement down to 0
     maxValue = qtyOrdered;     // cannot exceed ordered quantity

     txtQty.setText(String.valueOf(qtyReceived));
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
}
    
    public class DateChooserCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final JDateChooser dateChooser;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public DateChooserCellEditor() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
    }

    @Override
    public Object getCellEditorValue() {
        Date selectedDate = dateChooser.getDate();
        if (selectedDate != null) {
            return sdf.format(selectedDate);
        } else {
            return null; // para blank → NULL sa DB
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (value == null) {
            dateChooser.setDate(null);
        } else if (value instanceof Date) {
            dateChooser.setDate((Date) value);
        } else if (value instanceof String) {
            String val = (String) value;
            if (val.trim().isEmpty()) {
                dateChooser.setDate(null);
            } else {
                try {
                    Date parsedDate = sdf.parse(val);
                    dateChooser.setDate(parsedDate);
                } catch (Exception e) {
                    dateChooser.setDate(null);
                }
            }
        } else {
            dateChooser.setDate(null);
        }

        return dateChooser;
    }
}
    
       public void loadOrderItemsToTable(String orderId) throws SQLException {
           String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";
    String currentReceivingStatus = null;

    // Get current receiving_status
    try (Connection conn = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
         PreparedStatement pst = conn.prepareStatement(
                 "SELECT receiving_status FROM orders_info WHERE order_id = ?")) {
        pst.setString(1, orderId);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) currentReceivingStatus = rs.getString("receiving_status");
        }
    }
    final String statusFinal = currentReceivingStatus;

    // Setup table model
    DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Only Qty Received, Mfg Date, Exp Date, Remarks editable if pending > 0
            int pending = (int) getValueAt(row, 3);
            return (pending > 0) && (column == 2 || column == 4 || column == 5 || column == 8);
        }
    };

    tableModel.setColumnIdentifiers(new String[]{
            "Generic Name", "Qty Ordered", "Qty Received", "Qty Pending",
            "Mfg Date", "Exp Date", "Units", "Description", "Remarks"
    });

    String sql = """
        SELECT 
            oi.item_id,
            oi.item_name,
            oi.quantity,
            oi.units,
            oi.dcs,
            oi.approval_status,
            COALESCE(SUM(ri.quantity_received), 0) AS quantity_received,
            COALESCE(ri.remarks, '') AS remarks,
            MAX(ri.MfgDate) AS MfgDate,
            MAX(ri.ExpDate) AS ExpDate
        FROM order_items oi
        LEFT JOIN received_items ri
            ON oi.order_id = ri.order_id AND oi.item_name = ri.item_name
        WHERE oi.order_id = ? AND oi.approval_status = 'Approved'
        GROUP BY oi.item_id, oi.item_name, oi.quantity, oi.units, oi.dcs, oi.approval_status, ri.remarks
    """;

    try (Connection conn = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
         PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, orderId);
        try (ResultSet rs = pst.executeQuery()) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                String itemName = rs.getString("item_name");
                String description = rs.getString("dcs");
                int qtyOrdered = rs.getInt("quantity");
                int qtyReceived = rs.getInt("quantity_received");
                int qtyPending = Math.max(qtyOrdered - qtyReceived, 0);
                String units = rs.getString("units");
                if (units == null || units.trim().isEmpty()) units = "PCS";
                String remarks = rs.getString("remarks");
                java.sql.Date mfgDateObj = rs.getDate("MfgDate");
                java.sql.Date expDateObj = rs.getDate("ExpDate");
                String mfgDate = (mfgDateObj != null) ? new SimpleDateFormat("yyyy-MM-dd").format(mfgDateObj) : "";
                String expDate = (expDateObj != null) ? new SimpleDateFormat("yyyy-MM-dd").format(expDateObj) : "";

                tableModel.addRow(new Object[]{
                        itemName, qtyOrdered, qtyReceived, qtyPending,
                        mfgDate, expDate, units, description, remarks
                });
            }
            ordersinfo.setModel(tableModel);
            ord_rec.setEnabled(!"completed".equalsIgnoreCase(statusFinal));

            // === Renderers / Editors ===
            int qtyReceivedCol = 2;
            int mfgDateCol = 4;
            int expDateCol = 5;

            QuantityCellEditor qtyEditor = new QuantityCellEditor() {
    @Override
    public boolean stopCellEditing() {
        boolean stopped = super.stopCellEditing();
        int row = ordersinfo.getEditingRow();
        if (stopped && row >= 0) {
            int ordered = (int) ordersinfo.getValueAt(row, 1);
            int currentInput = 0;
            try { currentInput = Integer.parseInt(getCellEditorValue().toString()); } catch (Exception ignored) {}

            int previousReceived = (int) ordersinfo.getValueAt(row, 2);

            // 🔹 Increment-only logic
            int newReceived = Math.max(previousReceived, currentInput);
            newReceived = Math.min(newReceived, ordered); // cap at Qty Ordered

            ordersinfo.setValueAt(newReceived, row, 2);
            ordersinfo.setValueAt(ordered - newReceived, row, 3);

            updateReceivingStatusLive();
        }
        return stopped;
    }

    private void updateReceivingStatusLive() {
        boolean allCompleted = true;
        boolean anyReceived = false;
        for (int r = 0; r < ordersinfo.getRowCount(); r++) {
            int qtyOrd = (int) ordersinfo.getValueAt(r, 1);
            int qtyRec = 0;
            try { qtyRec = Integer.parseInt(ordersinfo.getValueAt(r, 2).toString()); } catch (Exception ignored) {}
            int pending = qtyOrd - qtyRec;
            ordersinfo.setValueAt(pending, r, 3);
            if (pending > 0) allCompleted = false;
            if (qtyRec > 0) anyReceived = true;
        }
        String newStatus = ordersinfo.getRowCount() == 0 ? "Pending Inspection"
                : allCompleted ? "Completed"
                : anyReceived ? "Partially Received"
                : "Pending Inspection";

        ord_rec.setEnabled(!"Completed".equalsIgnoreCase(newStatus));
        ordersinfo.repaint();
    }
};

            ordersinfo.getColumnModel().getColumn(qtyReceivedCol).setCellRenderer(new QuantityCellRenderer());
            ordersinfo.getColumnModel().getColumn(qtyReceivedCol).setCellEditor(qtyEditor);
            ordersinfo.getColumnModel().getColumn(mfgDateCol).setCellEditor(new DateChooserCellEditor());
            ordersinfo.getColumnModel().getColumn(expDateCol).setCellEditor(new DateChooserCellEditor());

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < ordersinfo.getColumnCount(); i++) {
                if (i != 7 && i != 8) ordersinfo.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            ordersinfo.getColumnModel().getColumn(7).setCellRenderer(new JTextAreaRenderer());
            ordersinfo.getColumnModel().getColumn(7).setCellEditor(new TextAreaCellEditor());
            ordersinfo.getColumnModel().getColumn(8).setCellRenderer(new JTextAreaRenderer());
            ordersinfo.getColumnModel().getColumn(8).setCellEditor(new TextAreaCellEditor());

            int fixedHeight = ordersinfo.getFontMetrics(ordersinfo.getFont()).getHeight() * 4 + 8;
            for (int row = 0; row < ordersinfo.getRowCount(); row++)
                ordersinfo.setRowHeight(row, fixedHeight);
        }
    }
    }
       

public void saveOrderItemsUpdates(String orderId) {
    String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(connectionUrl, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        boolean allCompleted = true; // overall order status

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String mergeSQL = """
            MERGE received_items AS target
            USING (VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)) AS source
                (item_name, quantity_ordered, units, quantity_received, remarks, MfgDate, ExpDate, description, order_id, BatchNo)
            ON target.order_id = source.order_id AND target.item_name = source.item_name
            WHEN MATCHED THEN
                UPDATE SET quantity_received = source.quantity_received,
                           remarks = source.remarks,
                           MfgDate = source.MfgDate,
                           ExpDate = source.ExpDate,
                           description = source.description
            WHEN NOT MATCHED THEN
                INSERT (item_name, quantity_ordered, units, quantity_received, remarks, MfgDate, ExpDate, description, order_id, BatchNo)
                VALUES (source.item_name, source.quantity_ordered, source.units, source.quantity_received, source.remarks, source.MfgDate, source.ExpDate, source.description, source.order_id, source.BatchNo);
        """;

        try (PreparedStatement pst = conn.prepareStatement(mergeSQL)) {

            for (int row = 0; row < ordersinfo.getRowCount(); row++) {
                String itemName = ordersinfo.getValueAt(row, 0).toString();
                int qtyOrdered = Integer.parseInt(ordersinfo.getValueAt(row, 1).toString());
                int newQtyReceived = 0;
                try { newQtyReceived = Integer.parseInt(ordersinfo.getValueAt(row, 2).toString()); } catch (Exception ignored) {}
                String units = ordersinfo.getValueAt(row, 6).toString();
                String description = ordersinfo.getValueAt(row, 7).toString();
                String remarks = ordersinfo.getValueAt(row, 8).toString();
                String mfgStr = ordersinfo.getValueAt(row, 4).toString();
                String expStr = ordersinfo.getValueAt(row, 5).toString();
                String batchNo = "BATCH-" + System.currentTimeMillis();

                java.sql.Date mfgDate = null;
                java.sql.Date expDate = null;
                try {
                    if (!mfgStr.isEmpty()) mfgDate = new java.sql.Date(sdf.parse(mfgStr).getTime());
                    if (!expStr.isEmpty()) expDate = new java.sql.Date(sdf.parse(expStr).getTime());
                } catch (Exception ignored) {}

                // 🔹 Get previous received quantity from DB
                int prevReceived = 0;
                try (PreparedStatement pstPrev = conn.prepareStatement(
                        "SELECT quantity_received FROM received_items WHERE order_id=? AND item_name=?")) {
                    pstPrev.setString(1, orderId);
                    pstPrev.setString(2, itemName);
                    try (ResultSet rsPrev = pstPrev.executeQuery()) {
                        if (rsPrev.next()) prevReceived = rsPrev.getInt("quantity_received");
                    }
                }

                int totalReceived = prevReceived + newQtyReceived;

                // Validation
                if (totalReceived > qtyOrdered) {
                    JOptionPane.showMessageDialog(null,
                            "Row " + (row + 1) + ": Total quantity received cannot exceed quantity ordered (" + qtyOrdered + ").",
                            "Invalid Data", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (totalReceived < qtyOrdered) allCompleted = false; // mark overall order as not complete

                // Batch insert/update
                pst.setString(1, itemName);
                pst.setInt(2, qtyOrdered);
                pst.setString(3, units);
                pst.setInt(4, totalReceived);
                pst.setString(5, remarks);
                if (mfgDate != null) pst.setDate(6, mfgDate); else pst.setNull(6, java.sql.Types.DATE);
                if (expDate != null) pst.setDate(7, expDate); else pst.setNull(7, java.sql.Types.DATE);
                pst.setString(8, description);
                pst.setString(9, orderId);
                pst.setString(10, batchNo);
                pst.addBatch();

                // Update pending column in JTable
                ordersinfo.setValueAt(qtyOrdered - totalReceived, row, 3);
            }

            pst.executeBatch();

            // 🔹 Update overall receiving status
            String newStatus = allCompleted ? "Completed" : "Partially Received";
            try (PreparedStatement pstStatus = conn.prepareStatement(
                    "UPDATE orders_info SET receiving_status=? WHERE order_id=?")) {
                pstStatus.setString(1, newStatus);
                pstStatus.setString(2, orderId);
                pstStatus.executeUpdate();
            }

            // Disable receive button if fully completed
            ord_rec.setEnabled(!allCompleted);

            conn.commit();

            JOptionPane.showMessageDialog(null, "Order items updated successfully. Status: " + newStatus);

            // Reload table to reflect updated values
            loadOrderItemsToTable(orderId);

        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
    }
  
}

    @SuppressWarnings("unchecked")
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ordersinfo = new javax.swing.JTable();
        ord_rec = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        orderedby = new javax.swing.JTextField();
        orid = new javax.swing.JTextField();
        types = new javax.swing.JTextField();
        supname = new javax.swing.JTextField();
        categories = new javax.swing.JTextField();
        statuss = new javax.swing.JTextField();
        timeordered = new javax.swing.JTextField();
        dateordered = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/inspection.png"))); // NOI18N
        jLabel1.setText("Medicines Inspection");

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

        jScrollPane1.setForeground(new java.awt.Color(255, 51, 51));

        ordersinfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ordersinfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Generic Name", "Qty Ordered", "Qty Received", "Qty Pending", "Mfg Date", "Exp Date", "Units", "Description", "Remarks"
            }
        ));
        ordersinfo.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(ordersinfo);

        ord_rec.setBackground(new java.awt.Color(0, 0, 0));
        ord_rec.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ord_rec.setForeground(new java.awt.Color(255, 255, 255));
        ord_rec.setText("Ordered Received");
        ord_rec.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ord_recMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ord_recMouseEntered(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Cancel Order");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Type:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Categories:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Status:");

        orderedby.setForeground(new java.awt.Color(255, 51, 51));
        orderedby.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        orid.setForeground(new java.awt.Color(255, 51, 51));
        orid.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        types.setForeground(new java.awt.Color(255, 51, 51));
        types.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        supname.setForeground(new java.awt.Color(255, 51, 51));
        supname.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        categories.setForeground(new java.awt.Color(255, 51, 51));
        categories.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        statuss.setForeground(new java.awt.Color(255, 51, 51));
        statuss.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        timeordered.setForeground(new java.awt.Color(255, 51, 51));
        timeordered.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        dateordered.setForeground(new java.awt.Color(255, 51, 51));
        dateordered.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 558, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(timeordered)
                                    .addComponent(dateordered, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ord_rec)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3)))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel2)))
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(statuss, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                    .addComponent(categories)
                                    .addComponent(supname)
                                    .addComponent(orid)
                                    .addComponent(types)
                                    .addComponent(orderedby))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(timeordered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(dateordered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderedby, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(orid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(types, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(categories, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addComponent(jLabel8))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statuss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(ord_rec, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    public String getInspectedOrderId() {
           return inspectedOrderId;
       }


    public int getNextReceivedId() {
       int nextId = 1;
       String query = "SELECT MAX(received_id) FROM received_items";
       String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
       String dbUser = "admin";
       String dbPass = "yeyel2025";

       try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
           if (rs.next()) {
               nextId = rs.getInt(1) + 1;
           }
       } catch (SQLException e) {
           JOptionPane.showMessageDialog(null, "Failed to get next received ID: " + e.getMessage());
       }
       return nextId;
   }


    public String generateNextBatchNo(Connection conn) {
        String prefix = "BATCH-";
        int nextNum = 1;
        String query = "SELECT MAX(BatchNo) FROM medicines WHERE BatchNo LIKE 'BATCH-%'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next() && rs.getString(1) != null) {
                String lastBatch = rs.getString(1); // e.g., "BATCH-0021"
                String numPart = lastBatch.substring(prefix.length()); // "0021"
                nextNum = Integer.parseInt(numPart) + 1;
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Failed to generate batch number: " + e.getMessage());
        }

        return prefix + String.format("%04d", nextNum); // e.g., "BATCH-0022"
    }
 
 
    private void ord_recMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ord_recMouseClicked
    {
     String orderId = getInspectedOrderId();
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    if (ordersinfo.isEditing()) ordersinfo.getCellEditor().stopCellEditing();
    int rowCount = ordersinfo.getRowCount();
    if (rowCount == 0) {
        JOptionPane.showMessageDialog(null, "no items found in the table.");
        return;
    }

    // ✅ generate next received ID properly (RCV-001, RCV-002, ...)
    String receivedId = "";
    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        String query = """
            SELECT 
                'RCV-' + RIGHT('000' + CAST(
                    ISNULL(CAST(SUBSTRING(MAX(received_id), 5, 3) AS INT), 0) + 1 AS VARCHAR
                ), 3) AS nextId
            FROM received_items
            WHERE received_id LIKE 'RCV-%'
        """;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                receivedId = rs.getString("nextId");
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error generating received id: " + e.getMessage());
        return;
    }

    // ✅ open order received dialog
    OrderReceivedPanel orderPanel = new OrderReceivedPanel(receivedId);
    JDialog dialog = new JDialog((JFrame) null, true);
    dialog.setUndecorated(true);
    dialog.getContentPane().add(orderPanel);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    String receiveBy = orderPanel.getApprovedBy();
    String dateReceivedStr = orderPanel.getDateReceived();

    if (receiveBy.isEmpty() || dateReceivedStr.isEmpty()) {
        JOptionPane.showMessageDialog(null, "please fill in both date received and received by.");
        return;
    }

    Date dateReceived;
    try {
        dateReceived = new SimpleDateFormat("yyyy-MM-dd").parse(dateReceivedStr);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "invalid date format. use yyyy-MM-dd");
        return;
    }

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        String insertReceivedSQL = "INSERT INTO received_items " +
            "(item_name, description, quantity_ordered, units, quantity_received, remarks, date_received, receive_by, order_id, BatchNo, received_id, MfgDate, ExpDate) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String updateOrderItemSQL = "UPDATE order_items SET quantity_received = quantity_received + ?, " +
            "remarks = ?, receiving_status = ? " +
            "WHERE order_id = ? AND item_name = ? AND receiving_status != 'Completed'";

        String insertMedicineSQL = "INSERT INTO medicines " +
            "(GenericID, GenericName, Units, Description, QuantityInStock, MfgDate, ExpDate, BatchNo, StockStatus, Condition) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 🔢 generate next batch number
        int nextBatchNum = 1;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(BatchNo) FROM medicines WHERE BatchNo LIKE 'BATCH-%'")) {
            if (rs.next() && rs.getString(1) != null) {
                nextBatchNum = Integer.parseInt(rs.getString(1).replace("BATCH-", "")) + 1;
            }
        }

        // 🔢 generate next Generic ID
        int nextGenericIdNum = 1;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(GenericID, 5, LEN(GenericID)) AS INT)) AS max_id FROM medicines")) {
            if (rs.next() && rs.getInt("max_id") > 0) {
                nextGenericIdNum = rs.getInt("max_id") + 1;
            }
        }

        try (PreparedStatement pstmtReceived = conn.prepareStatement(insertReceivedSQL);
             PreparedStatement pstmtUpdateOrder = conn.prepareStatement(updateOrderItemSQL);
             PreparedStatement pstmtMedicine = conn.prepareStatement(insertMedicineSQL)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < rowCount; i++) {
                String itemName = ordersinfo.getValueAt(i, 0).toString().trim();
                int qtyOrdered = Integer.parseInt(ordersinfo.getValueAt(i, 1).toString().trim());
                int newQtyReceived = 0;
                try {
                    newQtyReceived = Integer.parseInt(ordersinfo.getValueAt(i, 2).toString().trim());
                } catch (Exception ignored) {}

                String mfgStr = ordersinfo.getValueAt(i, 4).toString().trim();
                String expStr = ordersinfo.getValueAt(i, 5).toString().trim();
                String units = ordersinfo.getValueAt(i, 6).toString().trim();
                String description = ordersinfo.getValueAt(i, 7).toString().trim();
                String remarks = ordersinfo.getValueAt(i, 8).toString().trim();

                java.sql.Date mfgDate = (!mfgStr.isEmpty() && !mfgStr.equals("-"))
                        ? new java.sql.Date(sdf.parse(mfgStr).getTime()) : null;
                java.sql.Date expDate = (!expStr.isEmpty() && !expStr.equals("-"))
                        ? new java.sql.Date(sdf.parse(expStr).getTime()) : null;

                String batchCode = "BATCH-" + String.format("%04d", nextBatchNum++);
                String genericId = String.format("GEN-%03d", nextGenericIdNum++);

                // insert into received_items
                pstmtReceived.setString(1, itemName);
                pstmtReceived.setString(2, description);
                pstmtReceived.setInt(3, qtyOrdered);
                pstmtReceived.setString(4, units);
                pstmtReceived.setInt(5, newQtyReceived);
                pstmtReceived.setString(6, remarks);
                pstmtReceived.setDate(7, new java.sql.Date(dateReceived.getTime()));
                pstmtReceived.setString(8, receiveBy);
                pstmtReceived.setString(9, orderId);
                pstmtReceived.setString(10, batchCode);
                pstmtReceived.setString(11, receivedId);
                pstmtReceived.setDate(12, mfgDate);
                pstmtReceived.setDate(13, expDate);
                pstmtReceived.addBatch();

                // update order_items
                pstmtUpdateOrder.setInt(1, newQtyReceived);
                pstmtUpdateOrder.setString(2, remarks);
                pstmtUpdateOrder.setString(3, "Completed");
                pstmtUpdateOrder.setString(4, orderId);
                pstmtUpdateOrder.setString(5, itemName);
                pstmtUpdateOrder.addBatch();

                // insert into medicines
                String stockStatus = (newQtyReceived == 0) ? "Out of Stock" :
                                     (newQtyReceived <= 10) ? "Low Stock" : "In Stock";
                String condition = (expDate != null && expDate.before(new java.sql.Date(System.currentTimeMillis())))
                                   ? "Expired" : "Good";

                pstmtMedicine.setString(1, genericId);
                pstmtMedicine.setString(2, itemName);
                pstmtMedicine.setString(3, units);
                pstmtMedicine.setString(4, description);
                pstmtMedicine.setInt(5, newQtyReceived);
                pstmtMedicine.setDate(6, mfgDate);
                pstmtMedicine.setDate(7, expDate);
                pstmtMedicine.setString(8, batchCode);
                pstmtMedicine.setString(9, stockStatus);
                pstmtMedicine.setString(10, condition);
                pstmtMedicine.addBatch();
            }

            pstmtReceived.executeBatch();
            pstmtUpdateOrder.executeBatch();
            pstmtMedicine.executeBatch();
        }

        // ✅ mark order as completed
        try (PreparedStatement psUpdateOrder = conn.prepareStatement(
                "UPDATE orders_info SET receiving_status = 'Completed' WHERE order_id = ?")) {
            psUpdateOrder.setString(1, orderId);
            psUpdateOrder.executeUpdate();
        }

        conn.commit();
        JOptionPane.showMessageDialog(null, 
            "Order " + orderId + " received successfully.\nReceived ID: " + receivedId);
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "error during receiving: " + ex.getMessage());
    }
    }//GEN-LAST:event_ord_recMouseClicked
    }
   
    
    private void ord_recMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ord_recMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_ord_recMouseEntered

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

       private void refreshOrdersTable() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    // Fetch pending orders with their received items (if any)
    String selectSQL = """
        SELECT 
            oi.order_id, 
            ri.item_name, 
            ri.quantity_ordered, 
            ri.units, 
            COALESCE(ri.quantity_received, 0) AS quantity_received,
            COALESCE(ri.receiving_status, 'Pending') AS receiving_status,
            ri.description
        FROM orders_info oi
        LEFT JOIN received_items ri ON oi.order_id = ri.order_id
        WHERE oi.receiving_status = 'Pending'
        ORDER BY oi.order_id DESC
        """;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement pst = conn.prepareStatement(selectSQL);
         ResultSet rs = pst.executeQuery()) {

        DefaultTableModel model = (DefaultTableModel) itemsManagementPanel.getOrdersTable().getModel();
        model.setRowCount(0); // clear table

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("order_id"),
                rs.getString("item_name"),
                rs.getInt("quantity_ordered"),
                rs.getString("units"),
                rs.getInt("quantity_received"),
                rs.getString("receiving_status"),
                rs.getString("description")
            });
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error refreshing table: " + e.getMessage());
        e.printStackTrace();
    }
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField categories;
    private javax.swing.JTextField dateordered;
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
    private javax.swing.JTextField orderedby;
    private javax.swing.JTable ordersinfo;
    private javax.swing.JTextField orid;
    private javax.swing.JTextField statuss;
    private javax.swing.JTextField supname;
    private javax.swing.JTextField timeordered;
    private javax.swing.JTextField types;
    // End of variables declaration//GEN-END:variables
}
