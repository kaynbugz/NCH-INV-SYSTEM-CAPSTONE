
package dashboardSYSTEM;

    import DonationPanelMedicines.DonationPanelMedicines;
    import medicinespanelsSYSTEM.PendingOrdersPanel;
    import orderlistspanelsSYSTEM.MedicineOrderLists;
    import orderlistspanelsSYSTEM.SMOrderLists;
    import inspectpanelsSYSTEM.InspectMedicinesOrders;
    import inspectpanelsSYSTEM.InspectSMOrders;
    import receivedpanelsSYSTEM.ReceivedOrdersSM;
    import receivedpanelsSYSTEM.ReceivedOrdersMedicines;
    import com.toedter.calendar.JDateChooser;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Font;
    import java.awt.Frame;
    import java.awt.Window;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
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
    import java.util.regex.Pattern;
    import javax.swing.DefaultComboBoxModel;
    import javax.swing.JDialog;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JTable;
    import javax.swing.ListSelectionModel;
    import javax.swing.SwingConstants;
    import javax.swing.event.DocumentEvent;
    import javax.swing.event.DocumentListener;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;
    import java.awt.Desktop;
    import java.awt.event.MouseEvent;
    import java.io.File;
    import java.io.FileOutputStream;
    import javax.swing.JOptionPane;






    public class ItemsManagement extends javax.swing.JPanel {

    private TableRowSorter<DefaultTableModel> sorter;

    public ItemsManagement() {
    initComponents();
    loadOrderSummary();
    loadActiveSuppliersToTable();
    suptype();
    loadOrderInfo("");          // Load all records
    loadPendingOrders();
    setupStatusFilter();
 // After your table is initialized:
    JTableHeader header = sup_activelists.getTableHeader();

    header.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);  // Important to show background color
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    });

    // After your table is initialized:
    JTableHeader header2 = pending_orders.getTableHeader();

    header2.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);  // Important to show background color
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    });

        // After your table is initialized:
    JTableHeader header3 = orders_lists.getTableHeader();

    header3.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);  // Important to show background color
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    });


            // After your table is initialized:
    JTableHeader header4 = loadorderinfo.getTableHeader();

    header4.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);  // Important to show background color
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    });


     itemsjtabbedpane.addChangeListener(e -> {
        for (int i = 0; i < itemsjtabbedpane.getTabCount(); i++) {
            itemsjtabbedpane.setBackgroundAt(i, Color.decode("#FFF967")); // reset all tabs to light yellow
        }
        int selectedIndex = itemsjtabbedpane.getSelectedIndex();
        itemsjtabbedpane.setBackgroundAt(selectedIndex, Color.decode("#4CAF50")); // green color
    });

    s_type.setBackground(Color.WHITE);


    sup_activelists.getTableHeader().setForeground(Color.RED);

    // Add ListSelectionListener to handle row selection color change
    sup_activelists.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = sup_activelists.getSelectedRow();
            if (selectedRow != -1) {
                // Highlight selected row
                sup_activelists.setSelectionBackground(Color.decode("#4CAF50")); // Green color
                sup_activelists.setSelectionForeground(Color.WHITE); // White text
            }
        }
    });

      orders_lists.getTableHeader().setForeground(Color.RED);

    // Add ListSelectionListener to handle row selection color change
    orders_lists.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = orders_lists.getSelectedRow();
            if (selectedRow != -1) {
                // Highlight selected row
                orders_lists.setSelectionBackground(Color.decode("#4CAF50")); // Green color
                orders_lists.setSelectionForeground(Color.WHITE); // White text
            }
        }
    });

     loadorderinfo.getTableHeader().setForeground(Color.RED);

    // Add ListSelectionListener to handle row selection color change
    loadorderinfo.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = loadorderinfo.getSelectedRow();
            if (selectedRow != -1) {
                // Highlight selected row
                loadorderinfo.setSelectionBackground(Color.decode("#4CAF50")); // Green color
                loadorderinfo.setSelectionForeground(Color.WHITE); // White text
            }
        }
    });
    
        // Add ListSelectionListener to handle row selection color change
    pending_orders.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = pending_orders.getSelectedRow();
            if (selectedRow != -1) {
                // Highlight selected row
                pending_orders.setSelectionBackground(Color.decode("#4CAF50")); // Green color
                pending_orders.setSelectionForeground(Color.WHITE); // White text
            }
        }
    });

    
    
    // Set the table sorter
    sorter = new TableRowSorter<>((DefaultTableModel) orders_lists.getModel());
    orders_lists.setRowSorter(sorter);

    // Calculate initial totals (before filtering)
    calculateTotals();

    // Real-time text search
    searchidus.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            searchSupplier();
        }
    });

    // Filter by type dropdown
    s_type.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterByType();
        }
    });

// Real-time filter by text and date
searchidu.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { filterTable(); }
    public void removeUpdate(DocumentEvent e) { filterTable(); }
    public void changedUpdate(DocumentEvent e) { filterTable(); }
});

// Date filter listeners
PropertyChangeListener dateFilterListener = evt -> {
    // Call filterTable whenever date is changed
    filterTable();
};

fromd.getDateEditor().addPropertyChangeListener("date", dateFilterListener);
tod.getDateEditor().addPropertyChangeListener("date", dateFilterListener);

// Add the listeners for date changes and supplier ID change

f_date.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        filterTableData();
    }
});

t_date.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        filterTableData();
    }
});

s_id.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        filterTableData();
    }

@Override
public void removeUpdate(DocumentEvent e) {
    filterTableData();
}

@Override
public void changedUpdate(DocumentEvent e) {
    filterTableData();
}
});
}

public void loadOrderInfo(String filter) {
    DefaultTableModel model = (DefaultTableModel) loadorderinfo.getModel();
    model.setRowCount(0);

    int totalItems = 0;
    int totalOrdered = 0;
    int totalReceived = 0;

    String sql = """
        SELECT ri.received_id,
               ri.order_id,
               COUNT(DISTINCT ri.item_name) AS items,
               SUM(oi.quantity) AS quantity_ordered,
               SUM(ri.quantity_received) AS quantity_received,
               CONVERT(VARCHAR, MAX(ri.date_received), 101) AS date_received
        FROM received_items ri
        INNER JOIN order_items oi
             ON ri.order_id = oi.order_id
            AND ri.item_name = oi.item_name
        WHERE ri.received_id LIKE ?
        GROUP BY ri.received_id, ri.order_id
        ORDER BY CAST(SUBSTRING(ri.received_id, 5, LEN(ri.received_id)) AS INT) DESC
    """;

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // 🔍 Apply filter safely
        String searchPattern = (filter == null || filter.isEmpty()) ? "%" : "%" + filter + "%";
        stmt.setString(1, searchPattern);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String receivedId = rs.getString("received_id");
                String orderId = rs.getString("order_id");
                int items = rs.getInt("items");
                int qtyOrdered = rs.getInt("quantity_ordered");
                int qtyReceived = rs.getInt("quantity_received");
                String dateReceived = rs.getString("date_received");

                model.addRow(new Object[]{
                    receivedId,
                    orderId,
                    items,
                    qtyOrdered,
                    qtyReceived,
                    dateReceived
                });

                totalItems += items;
                totalOrdered += qtyOrdered;
                totalReceived += qtyReceived;
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error filtering data: " + e.getMessage());
    }

    // 🔄 Refresh table
    model.fireTableDataChanged();
    loadorderinfo.repaint();

    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    loadorderinfo.setRowSorter(sorter);
    sorter.setSortsOnUpdates(true);
    loadorderinfo.setAutoCreateRowSorter(true);

    loadorderinfo.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(JLabel.CENTER);

            if (column == 0) {
                c.setForeground(Color.RED);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else if (column == 1) {
                c.setForeground(new Color(0, 128, 0));
            } else {
                c.setForeground(Color.BLACK);
                c.setFont(c.getFont().deriveFont(Font.PLAIN));
            }

            return c;
        }
    });

    itemst.setText(String.valueOf(totalItems));
    ordered_t.setText(String.valueOf(totalOrdered));
    received_t.setText(String.valueOf(totalReceived));
}

// 🔹 call this inside your constructor after initComponents()
private void setupStatusFilter() {
    statusreveal.setModel(new DefaultComboBoxModel<>(new String[]{
        "All", "Pending Approval", "Partially Approved", "Approved", "Rejected"
    }));
    statusreveal.setBackground(Color.WHITE);
    statusreveal.addActionListener(e -> loadPendingOrders());

    // ✅ date filters (JDateChooser ka gamit)
    fromd1.getDateEditor().addPropertyChangeListener("date", evt -> loadPendingOrders());
    tod1.getDateEditor().addPropertyChangeListener("date", evt -> loadPendingOrders());

    // ✅ search filter (real-time habang nagta-type)
    searchidu1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) { loadPendingOrders(); }
        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) { loadPendingOrders(); }
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) { loadPendingOrders(); }
    });

    // initial load (show all)
    loadPendingOrders();
}


public void loadPendingOrders() {
  {
    String selectedStatus = (String) statusreveal.getSelectedItem();
    java.util.Date fromDate = fromd1.getDate();
    java.util.Date toDate = tod1.getDate();
    String searchText = searchidu1.getText().trim();

    DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new String[]{
        "Order ID", "Ordered By", "Supplier Name", "Total Items", "Total Quantities",
        "Date Ordered", "Approval Status"
    });

    try (Connection conn = DriverManager.getConnection(
        "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
        "admin", "yeyel2025")) {

        StringBuilder sql = new StringBuilder("""
            SELECT o.order_id, o.ordered_by, o.supplier_name,
                   COUNT(oi.item_name) AS total_items,
                   SUM(oi.quantity) AS total_quantities,
                   o.date_ordered, o.approval_status
            FROM orders_info o
            JOIN order_items oi ON o.order_id = oi.order_id
            WHERE o.approval_status IN ('Pending Approval', 'Partially Approved', 'Approved', 'Rejected')
        """);

        List<Object> params = new ArrayList<>();

        if (selectedStatus != null && !"All".equalsIgnoreCase(selectedStatus)) {
            sql.append(" AND o.approval_status = ? ");
            params.add(selectedStatus);
        }

        if (fromDate != null && toDate != null) {
            sql.append(" AND o.date_ordered BETWEEN ? AND ? ");
            params.add(new java.sql.Date(fromDate.getTime()));
            params.add(new java.sql.Date(toDate.getTime()));
        }

        if (!searchText.isEmpty()) {
            sql.append(" AND (o.order_id LIKE ? OR o.ordered_by LIKE ? OR o.supplier_name LIKE ?) ");
            String like = "%" + searchText + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        sql.append("""
            GROUP BY o.order_id, o.ordered_by, o.supplier_name, o.date_ordered, o.approval_status
            ORDER BY o.order_id DESC
        """);

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                int totalOrders = 0;
                int totalItems = 0;
                int totalQuantities = 0;

                while (rs.next()) {
                    String orderId = rs.getString("order_id"); // ✅ fixed
                    String orderedBy = rs.getString("ordered_by");
                    String supplier = rs.getString("supplier_name");
                    int items = rs.getInt("total_items");
                    int quantities = rs.getInt("total_quantities");
                    Date dateOrdered = rs.getDate("date_ordered");
                    String approvalStatus = rs.getString("approval_status");

                    model.addRow(new Object[]{
                        orderId, orderedBy, supplier, items, quantities,
                        dateOrdered, approvalStatus
                    });

                    totalOrders++;
                    totalItems += items;
                    totalQuantities += quantities;
                }

                totaloforders1.setText(String.valueOf(totalOrders));
                totalofitems1.setText(String.valueOf(totalItems));
                totalofquantites1.setText(String.valueOf(totalQuantities));
            }
        }

        pending_orders.setModel(model);

        // -------------------------
        // Column renderers
        // -------------------------

        DefaultTableCellRenderer idRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.RED);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        pending_orders.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        DefaultTableCellRenderer orderedByRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(new Color(0, 128, 0));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        pending_orders.getColumnModel().getColumn(1).setCellRenderer(orderedByRenderer);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    String status = value != null ? value.toString() : "";
                    switch (status) {
                        case "Pending Approval" -> c.setBackground(new Color(255, 223, 186));
                        case "Partially Approved" -> c.setBackground(new Color(186, 225, 255));
                        case "Approved" -> c.setBackground(new Color(200, 255, 200));
                        case "Rejected" -> c.setBackground(new Color(255, 200, 200));
                        default -> c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        };
        pending_orders.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < pending_orders.getColumnCount() - 1; i++) {
            pending_orders.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading pending orders: " + e.getMessage());
    }
}
}

private void filterTableData() {
    DefaultTableModel model = (DefaultTableModel) loadorderinfo.getModel();
    model.setRowCount(0); // Clear the existing table rows

    // Get the filter criteria from the fields
    java.util.Date startDate = f_date.getDate();
    java.util.Date endDate = t_date.getDate();
    String supplierId = s_id.getText().trim();

    // Variables to calculate totals
    int totalItems = 0;
    int totalOrdered = 0;
    int totalReceived = 0;

    StringBuilder query = new StringBuilder("""
        SELECT received_id,
               COUNT(DISTINCT item_name) AS items,
               SUM(quantity_ordered) AS quantity_ordered,
               SUM(quantity_received) AS quantity_received,
               CONVERT(VARCHAR, MAX(date_received), 101) AS date_received
        FROM received_items
    """);

    boolean hasWhereClause = false;

    if (startDate != null && endDate != null) {
        query.append("WHERE date_received BETWEEN ? AND ? ");
        hasWhereClause = true;
    }

    if (!supplierId.isEmpty()) {
        if (hasWhereClause) {
            query.append("AND received_id LIKE ? ");
        } else {
            query.append("WHERE received_id LIKE ? ");
            hasWhereClause = true;
        }
    }

    query.append("GROUP BY received_id ");
    query.append("ORDER BY CAST(SUBSTRING(received_id, 5, LEN(received_id)) AS INT) DESC");

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement stmt = conn.prepareStatement(query.toString())) {

        int index = 1;

        // Set the parameters for the query
        if (startDate != null && endDate != null) {
            stmt.setDate(index++, new java.sql.Date(startDate.getTime()));
            stmt.setDate(index++, new java.sql.Date(endDate.getTime()));
        }

        if (!supplierId.isEmpty()) {
            stmt.setString(index++, "%" + supplierId + "%"); // ✅ Use LIKE for partial match
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String receivedId = rs.getString("received_id"); // ✅ FIXED: use getString
                int items = rs.getInt("items");
                int qtyOrdered = rs.getInt("quantity_ordered");
                int qtyReceived = rs.getInt("quantity_received");
                String dateReceived = rs.getString("date_received");

                model.addRow(new Object[]{
                    receivedId,
                    items,
                    qtyOrdered,
                    qtyReceived,
                    dateReceived
                });

                totalItems += items;
                totalOrdered += qtyOrdered;
                totalReceived += qtyReceived;
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error filtering data: " + e.getMessage());
    }

    // Update the labels with the calculated totals
    itemst.setText(String.valueOf(totalItems));
    ordered_t.setText(String.valueOf(totalOrdered));
    received_t.setText(String.valueOf(totalReceived));

    // 🔄 Refresh table visuals
    model.fireTableDataChanged();
    loadorderinfo.repaint();
}

private void filterTable() {
if (sorter == null) return;

String searchText = searchidu.getText().trim().toLowerCase();
Date fromDate = fromd.getDate();
Date toDate = tod.getDate();

List<RowFilter<Object, Object>> filters = new ArrayList<>();

// Filter by Order ID or Supplier Name (columns 0 and 1)
if (!searchText.isEmpty()) {
    filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(searchText), 0, 1));
}

// Filter by Date Ordered (column 7)
if (fromDate != null && toDate != null) {
    filters.add(new RowFilter<Object, Object>() {
        @Override
        public boolean include(Entry<? extends Object, ? extends Object> entry) {
            Object dateObj = entry.getValue(7); // Column 7 = Date Ordered
            if (dateObj instanceof java.util.Date) {
                Date rowDate = (Date) dateObj;
                return !rowDate.before(fromDate) && !rowDate.after(toDate);
            }
            return false;
        }
    });
}

// Apply combined filters (search + date)
RowFilter<Object, Object> combinedFilter = filters.isEmpty()
        ? null
        : RowFilter.andFilter(filters);
sorter.setRowFilter(combinedFilter);

// Recalculate the totals based on the filtered data
calculateTotals();
}


private void calculateTotals() {
int totalOrders = 0;
int totalItems = 0;
int totalQuantities = 0;

// Get the row count from the sorter (filtered table view)
int visibleRowCount = orders_lists.getRowCount(); 

for (int i = 0; i < visibleRowCount; i++) {
    // Convert visible row index to model index
    int modelRow = orders_lists.convertRowIndexToModel(i);

    DefaultTableModel model = (DefaultTableModel) orders_lists.getModel();

    totalOrders++; // Count each visible row as an order

    // Get total items and quantities from the model row
    int items = (int) model.getValueAt(modelRow, 5);  // Total Items
    int quantities = (int) model.getValueAt(modelRow, 6);  // Total Quantities

    totalItems += items;
    totalQuantities += quantities;
}

    // Update JTextFields with filtered totals
    totaloforders.setText(String.valueOf(totalOrders)); 
    totalofitems.setText(String.valueOf(totalItems));
    totalofquantites.setText(String.valueOf(totalQuantities));
    }

    private void suptype() {
    // clear muna para iwas duplicate items
    s_type.removeAllItems();

    // add default "All" option
    s_type.addItem("All");

    // add the rest
    String[] type = {
        "Supplies",
        "Materials",
        "Medicines"
    };

    for (String role : type) {
        s_type.addItem(role);
    }

    // style: white background
    s_type.setBackground(Color.WHITE);
    s_type.setOpaque(true);

    // default selection = All
    s_type.setSelectedItem("All");
}


  

    
        private void loadActiveSuppliersToTable() {
   DefaultTableModel model = (DefaultTableModel) sup_activelists.getModel();
    model.setRowCount(0); // Clear existing rows

    String sql = "SELECT supplier_id, supplier_name, supplier_email, supplier_pn, supplier_address, type, status " +
                 "FROM suppliers WHERE status = 'Active'";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            Vector<String> row = new Vector<>();
            row.add(rs.getString("supplier_id"));
            row.add(rs.getString("supplier_name"));
            row.add(rs.getString("supplier_email"));
            row.add(rs.getString("supplier_pn"));
            row.add(rs.getString("supplier_address"));
            row.add(rs.getString("type"));
            row.add(rs.getString("status"));
            model.addRow(row);
        }

        // Enable sorting
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(model);
        sup_activelists.setRowSorter(rowSorter);

        // Center-align all columns by default
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < sup_activelists.getColumnCount(); i++) {
            sup_activelists.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Supplier ID: bold red
        DefaultTableCellRenderer idRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                c.setForeground(Color.RED);
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        sup_activelists.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        // Supplier Name: green, not bold
        DefaultTableCellRenderer nameRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(new Color(0, 128, 0)); // green
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        sup_activelists.getColumnModel().getColumn(1).setCellRenderer(nameRenderer);

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading active suppliers: " + ex.getMessage());
        ex.printStackTrace();
    }    
}

public JTable getOrdersTable() {
    return orders_lists;
}

  public void loadOrderSummary() {
    String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(connectionUrl, dbUser, dbPass)) {

        String sql = """
            SELECT oi.order_id, oi.supplier_name, oi.ordered_by, oi.category, oi.type,
                   COUNT(oi_item.item_id) AS total_items,
                   SUM(oi_item.quantity) AS total_quantities,
                   oi.date_ordered, oi.time_ordered, oi.receiving_status
            FROM nchdbase.dbo.orders_info oi
            INNER JOIN nchdbase.dbo.order_items oi_item
                ON oi.order_id = oi_item.order_id
            WHERE oi_item.approval_status = 'Approved'
            GROUP BY oi.order_id, oi.supplier_name, oi.ordered_by, oi.category, oi.type,
                     oi.date_ordered, oi.time_ordered, oi.receiving_status
            ORDER BY oi.order_id DESC
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        String[] columnNames = {
            "Order ID", "Supplier Name", "Ordered By", "Category", "Type",
            "Total Items", "Total Quantities", "Date Ordered", "Time Ordered", "Receiving Status"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        while (rs.next()) {
            String orderId = rs.getString("order_id");
            String supplierName = rs.getString("supplier_name");
            String orderedBy = rs.getString("ordered_by");
            String category = rs.getString("category");
            String type = rs.getString("type");
            int totalItems = rs.getInt("total_items");
            int totalQuantities = rs.getInt("total_quantities");
            Date dateOrdered = rs.getDate("date_ordered");
            Time timeOrdered = rs.getTime("time_ordered");
            String formattedTime = (timeOrdered != null) ? timeFormat.format(timeOrdered) : "";

            String receivingStatus = rs.getString("receiving_status");
            if (receivingStatus == null || receivingStatus.trim().equalsIgnoreCase("Not Yet Received")) {
                receivingStatus = "Pending Inspection"; // ✅ rename it before display
            }

            model.addRow(new Object[]{
                orderId, supplierName, orderedBy, category, type,
                totalItems, totalQuantities, dateOrdered, formattedTime, receivingStatus
            });
        }

        orders_lists.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        orders_lists.setRowSorter(sorter);

        // column renderers
        DefaultTableCellRenderer orderIdRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                c.setForeground(Color.RED);
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        orders_lists.getColumnModel().getColumn(0).setCellRenderer(orderIdRenderer);

        DefaultTableCellRenderer supplierRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(new Color(0, 128, 0));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        orders_lists.getColumnModel().getColumn(1).setCellRenderer(supplierRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < orders_lists.getColumnCount(); i++) {
            orders_lists.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        ((DefaultTableCellRenderer) orders_lists.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        rs.close();
        stmt.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error retrieving order summary: " + ex.getMessage());
        ex.printStackTrace();
    }   
}
        
  private void searchSupplier() {
       String keyword = searchidus.getText().trim();

    try {
        // Establish database connection
        Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
        );

        // Query to search by supplier_id or supplier_name and filter by active status
        String sql = "SELECT * FROM suppliers WHERE (supplier_id = ? OR supplier_name LIKE ?) AND status = 'active'";
        PreparedStatement stmt = conn.prepareStatement(sql);

        // Try to parse keyword as an integer for supplier_id
        try {
            int id = Integer.parseInt(keyword); // Try converting input to an integer
            stmt.setInt(1, id); // If it's a number, search by supplier_id
        } catch (NumberFormatException e) {
            stmt.setNull(1, java.sql.Types.INTEGER); // If it's not a number, search by name
        }

        // Use LIKE for supplier_name to allow partial matching
        stmt.setString(2, "%" + keyword + "%");

        ResultSet rs = stmt.executeQuery();

        // Clear previous rows before adding new ones
        DefaultTableModel model = (DefaultTableModel) sup_activelists.getModel();
        model.setRowCount(0); // Clear the table

        // Add new filtered rows to the table
        while (rs.next()) {
            int supplierId = rs.getInt("supplier_id");
            String supplierName = rs.getString("supplier_name");
            String supplierEmail = rs.getString("supplier_email");
            String supplierPhone = rs.getString("supplier_pn");
            String supplierAddress = rs.getString("supplier_address");
            String type = rs.getString("type");
            String status = rs.getString("status");

            // Add the result to the table model
            model.addRow(new Object[] {
                supplierId, supplierName, supplierEmail, supplierPhone, supplierAddress, type, status
            });
        }

        rs.close();
        stmt.close();
        conn.close();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Search error: " + e.getMessage());
    }
    }
    
    
    private void filterByType() {
     Object selected = s_type.getSelectedItem();
    if (selected == null) return; // prevent NullPointerException

    String selectedType = selected.toString();

    try {
        Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
        );

        String sql = "SELECT * FROM suppliers WHERE type = ? AND status = 'Active'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, selectedType);

        ResultSet rs = stmt.executeQuery();
        DefaultTableModel model = (DefaultTableModel) sup_activelists.getModel();
        model.setRowCount(0); // Clear table first

        while (rs.next()) {
            int supplierId = rs.getInt("supplier_id");
            String name = rs.getString("supplier_name");
            String email = rs.getString("supplier_email");
            String phone = rs.getString("supplier_pn");
            String address = rs.getString("supplier_address");
            String type = rs.getString("type");
            String status = rs.getString("status");

            model.addRow(new Object[] {
                supplierId, name, email, phone, address, type, status
            });
        }

        rs.close();
        stmt.close();
        conn.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error filtering by type: " + ex.getMessage());
    }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel20 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        itemsjtabbedpane = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sup_activelists = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnorderlists = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        searchidus = new javax.swing.JTextField();
        s_type = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        refresh = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        pending_orders = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        searchidu1 = new javax.swing.JTextField();
        fromd1 = new com.toedter.calendar.JDateChooser();
        tod1 = new com.toedter.calendar.JDateChooser();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        order_id = new javax.swing.JTextField();
        ViewPendingOrders = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        totaloforders1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        totalofitems1 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        totalofquantites1 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        statusreveal = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        orders_lists = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        searchidu = new javax.swing.JTextField();
        fromd = new com.toedter.calendar.JDateChooser();
        tod = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        enid = new javax.swing.JTextField();
        inspectid = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        totaloforders = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        totalofitems = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        totalofquantites = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        printbtn = new javax.swing.JButton();
        orderedt = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        loadorderinfo = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        s_id = new javax.swing.JTextField();
        f_date = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        t_date = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ordered_t = new javax.swing.JLabel();
        received_t = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        itemst = new javax.swing.JLabel();
        r_id = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        viewbtninfo = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 249, 103));
        jLabel20.setText("CATEGORIES");

        setBackground(new java.awt.Color(48, 122, 55));
        setForeground(new java.awt.Color(48, 122, 55));

        jPanel1.setBackground(new java.awt.Color(48, 122, 55));

        itemsjtabbedpane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        itemsjtabbedpane.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel7.setBackground(new java.awt.Color(255, 249, 103));

        sup_activelists.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        sup_activelists.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Supplier ID", "Supplier Name", "Supplier Email", "Supplier NO.", "Supplier Address", "Type", "Status"
            }
        ));
        jScrollPane2.setViewportView(sup_activelists);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Make Orders");

        btnorderlists.setBackground(new java.awt.Color(0, 0, 0));
        btnorderlists.setForeground(new java.awt.Color(255, 255, 255));
        btnorderlists.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/add (1).png"))); // NOI18N
        btnorderlists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnorderlistsActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Search ID");

        searchidus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        searchidus.setForeground(new java.awt.Color(255, 51, 0));

        s_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        s_type.setPreferredSize(new java.awt.Dimension(64, 22));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Type");

        refresh.setBackground(new java.awt.Color(0, 0, 0));
        refresh.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        refresh.setForeground(new java.awt.Color(255, 255, 255));
        refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/refresh-page-option (1).png"))); // NOI18N
        refresh.setText("Refresh");
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(48, 122, 55));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/check (1).png"))); // NOI18N
        jLabel18.setText("Supplier Active Lists");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 887, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(2, 2, 2)
                        .addComponent(searchidus, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(2, 2, 2)
                        .addComponent(s_type, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(2, 2, 2)
                        .addComponent(btnorderlists, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchidus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel4)
                        .addComponent(s_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(refresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnorderlists))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        itemsjtabbedpane.addTab("Supplier Active Lists", jPanel3);

        jPanel9.setBackground(new java.awt.Color(255, 249, 103));

        pending_orders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Ordered By", "Supplier Name", "Total Items", "Date Ordered", "Status"
            }
        ));
        jScrollPane4.setViewportView(pending_orders);

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("Search ID");

        searchidu1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        searchidu1.setForeground(new java.awt.Color(255, 51, 0));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setText("Starting Date");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setText("End Date");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setText("Enter a Order ID :");

        order_id.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        order_id.setForeground(new java.awt.Color(255, 51, 0));
        order_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                order_idActionPerformed(evt);
            }
        });

        ViewPendingOrders.setBackground(new java.awt.Color(0, 0, 0));
        ViewPendingOrders.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        ViewPendingOrders.setForeground(new java.awt.Color(255, 255, 255));
        ViewPendingOrders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/transparency.png"))); // NOI18N
        ViewPendingOrders.setText("View");
        ViewPendingOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewPendingOrdersActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setText("Total Of Orders");

        totaloforders1.setBackground(new java.awt.Color(0, 0, 0));
        totaloforders1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totaloforders1.setForeground(new java.awt.Color(255, 255, 255));
        totaloforders1.setBorder(null);
        totaloforders1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totaloforders1ActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setText("Total of items");

        totalofitems1.setBackground(new java.awt.Color(0, 0, 0));
        totalofitems1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalofitems1.setForeground(new java.awt.Color(255, 255, 255));
        totalofitems1.setBorder(null);

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel30.setText("Total of quantities");

        totalofquantites1.setBackground(new java.awt.Color(0, 0, 0));
        totalofquantites1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalofquantites1.setForeground(new java.awt.Color(255, 255, 255));
        totalofquantites1.setBorder(null);

        jButton4.setBackground(new java.awt.Color(0, 0, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/refresh-page-option (1).png"))); // NOI18N
        jButton4.setText("Refresh");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });

        jLabel31.setBackground(new java.awt.Color(48, 122, 55));
        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/proof-of-delivery.png"))); // NOI18N
        jLabel31.setText("Pending Orders");

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("PRINT");

        statusreveal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        statusreveal.setOpaque(true);

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setText("Status");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel24)
                        .addGap(0, 0, 0)
                        .addComponent(searchidu1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(statusreveal, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel25)
                        .addGap(2, 2, 2)
                        .addComponent(fromd1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addGap(2, 2, 2)
                        .addComponent(tod1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addGap(2, 2, 2)
                        .addComponent(totaloforders1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)
                        .addGap(2, 2, 2)
                        .addComponent(totalofquantites1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addGap(2, 2, 2)
                        .addComponent(totalofitems1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 310, Short.MAX_VALUE)
                        .addComponent(jLabel27)
                        .addGap(2, 2, 2)
                        .addComponent(order_id, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ViewPendingOrders)
                .addGap(15, 15, 15))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel32)
                        .addComponent(jButton4)
                        .addComponent(statusreveal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(searchidu1))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fromd1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tod1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel30)
                        .addComponent(totaloforders1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(totalofitems1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(totalofquantites1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(order_id, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ViewPendingOrders, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        itemsjtabbedpane.addTab("Pending Orders", jPanel2);

        jPanel8.setBackground(new java.awt.Color(255, 249, 103));

        orders_lists.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Supplier Name", "Ordered By", "Category", "Type", "Items", "Quantities", "Date Ordered", "Time Ordered"
            }
        ));
        jScrollPane3.setViewportView(orders_lists);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Search ID");

        searchidu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        searchidu.setForeground(new java.awt.Color(255, 51, 0));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Starting Date");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("End Date");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Enter a Order ID :");

        enid.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        enid.setForeground(new java.awt.Color(255, 51, 0));

        inspectid.setBackground(new java.awt.Color(0, 0, 0));
        inspectid.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        inspectid.setForeground(new java.awt.Color(255, 255, 255));
        inspectid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/transparency.png"))); // NOI18N
        inspectid.setText("Inspect");
        inspectid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inspectidActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Total Of Orders ");

        totaloforders.setBackground(new java.awt.Color(0, 0, 0));
        totaloforders.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totaloforders.setForeground(new java.awt.Color(255, 255, 255));
        totaloforders.setBorder(null);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Total of items ");

        totalofitems.setBackground(new java.awt.Color(0, 0, 0));
        totalofitems.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalofitems.setForeground(new java.awt.Color(255, 255, 255));
        totalofitems.setBorder(null);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Total of quantities ");

        totalofquantites.setBackground(new java.awt.Color(0, 0, 0));
        totalofquantites.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalofquantites.setForeground(new java.awt.Color(255, 255, 255));
        totalofquantites.setBorder(null);

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/refresh-page-option (1).png"))); // NOI18N
        jButton3.setText("Refresh");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/proof-of-delivery.png"))); // NOI18N
        jLabel22.setText("Lists of orders");

        printbtn.setBackground(new java.awt.Color(0, 0, 0));
        printbtn.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        printbtn.setForeground(new java.awt.Color(255, 255, 255));
        printbtn.setText("PRINT");
        printbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                printbtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(2, 2, 2)
                                .addComponent(searchidu, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addGap(2, 2, 2)
                                .addComponent(fromd, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addGap(2, 2, 2)
                                .addComponent(tod, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(printbtn))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(inspectid)
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(2, 2, 2)
                                .addComponent(totaloforders, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addGap(2, 2, 2)
                                .addComponent(totalofquantites, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel11)
                                .addGap(2, 2, 2)
                                .addComponent(totalofitems, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(2, 2, 2)
                                .addComponent(enid, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(12, 12, 12)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(searchidu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(tod, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(printbtn))
                            .addComponent(jLabel6)
                            .addComponent(fromd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totaloforders, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                            .addComponent(jLabel10)
                            .addComponent(totalofquantites, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(totalofitems, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(1, 1, 1))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(enid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inspectid)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        itemsjtabbedpane.addTab("Inspections Orders", jPanel4);

        orderedt.setBackground(new java.awt.Color(255, 249, 103));

        loadorderinfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Received ID", "Order ID", "Items", "Quantity Ordered", "Quantity Received", "Date Received"
            }
        ));
        jScrollPane1.setViewportView(loadorderinfo);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Search ID");

        s_id.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        s_id.setForeground(new java.awt.Color(255, 51, 0));
        s_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                s_idActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Starting Date");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("End Date");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Quantity Ordered");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Quantity Received");

        ordered_t.setBackground(new java.awt.Color(0, 0, 0));
        ordered_t.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ordered_t.setForeground(new java.awt.Color(255, 255, 255));
        ordered_t.setText(" ");
        ordered_t.setOpaque(true);

        received_t.setBackground(new java.awt.Color(0, 0, 0));
        received_t.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        received_t.setForeground(new java.awt.Color(255, 255, 255));
        received_t.setText(" ");
        received_t.setOpaque(true);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Total of Items");

        itemst.setBackground(new java.awt.Color(0, 0, 0));
        itemst.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        itemst.setForeground(new java.awt.Color(255, 255, 255));
        itemst.setText(" ");
        itemst.setOpaque(true);

        r_id.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        r_id.setForeground(new java.awt.Color(255, 51, 0));
        r_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                r_idActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Enter Order ID :");

        viewbtninfo.setBackground(new java.awt.Color(0, 0, 0));
        viewbtninfo.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        viewbtninfo.setForeground(new java.awt.Color(255, 255, 255));
        viewbtninfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/eye.png"))); // NOI18N
        viewbtninfo.setText("View");
        viewbtninfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewbtninfoActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/refresh-page-option (1).png"))); // NOI18N
        jButton2.setText("Refresh");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(0, 0, 0));
        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/received.png"))); // NOI18N
        jLabel23.setText("Received Orders");

        jButton6.setBackground(new java.awt.Color(0, 0, 0));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("PRINT");

        javax.swing.GroupLayout orderedtLayout = new javax.swing.GroupLayout(orderedt);
        orderedt.setLayout(orderedtLayout);
        orderedtLayout.setHorizontalGroup(
            orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderedtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(orderedtLayout.createSequentialGroup()
                        .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(2, 2, 2)
                                .addComponent(itemst, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel16)
                                .addGap(2, 2, 2)
                                .addComponent(received_t, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13)
                                .addGap(2, 2, 2)
                                .addComponent(ordered_t, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel21)
                                .addGap(2, 2, 2)
                                .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(r_id, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createSequentialGroup()
                                        .addComponent(viewbtninfo)
                                        .addGap(14, 14, 14))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(s_id, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14)
                                .addGap(2, 2, 2)
                                .addComponent(f_date, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15)
                                .addGap(2, 2, 2)
                                .addComponent(t_date, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6))
                            .addComponent(jScrollPane1))
                        .addContainerGap())
                    .addGroup(orderedtLayout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 925, Short.MAX_VALUE))))
        );
        orderedtLayout.setVerticalGroup(
            orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(7, 7, 7)
                        .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(t_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(s_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton2)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orderedtLayout.createSequentialGroup()
                        .addGap(0, 41, Short.MAX_VALUE)
                        .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(f_date, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(orderedtLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(ordered_t, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(itemst, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16)
                                .addComponent(received_t, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(orderedtLayout.createSequentialGroup()
                                .addGroup(orderedtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(r_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(viewbtninfo, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(orderedtLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        itemsjtabbedpane.addTab("Received Orders", orderedt);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(itemsjtabbedpane)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(itemsjtabbedpane, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        itemsjtabbedpane.getAccessibleContext().setAccessibleName("asd");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/left.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("ITEMS ORDERS MANAGEMENT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(439, 439, 439)
                .addComponent(jLabel17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        // Get the parent window (JFrame) of this panel
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose(); // Close the window
        }
        
        String username = "kaynz"; // or the one you got during login
        homepageSYSTEM homepage = new homepageSYSTEM();
        homepage.setVisible(true);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      // 🔄 Reset all filters and fields
    r_id.setText("");           // Clear Received ID field
    s_id.setText("");           // Clear Search ID field
    f_date.setDate(null);       // Clear 'From' date picker
    t_date.setDate(null);       // Clear 'To' date picker

    // 🔄 Reload full data (no filter)
    loadOrderInfo("");          // Load all records
    loadOrderSummary();         // Refresh summary stats (if applicable)
    }//GEN-LAST:event_jButton2ActionPerformed

    private void viewbtninfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewbtninfoActionPerformed
        String receivedId = r_id.getText().trim();

        if (!receivedId.isEmpty()) {
            try {
                String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
                String dbUser = "admin";
                String dbPass = "yeyel2025";

                try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
                    // ✅ Step 1: Get order_id from received_items
                    String orderQuery = "SELECT TOP 1 order_id FROM received_items WHERE received_id = ?";
                    String orderId = null;

                    try (PreparedStatement stmt = conn.prepareStatement(orderQuery)) {
                        stmt.setString(1, receivedId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                orderId = rs.getString("order_id");
                            } else {
                                JOptionPane.showMessageDialog(null, "No order found for Received ID.");
                                return;
                            }
                        }
                    }

                    // ✅ Step 2: Use order_id to get type from orders_info
                    String typeQuery = "SELECT type FROM orders_info WHERE order_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(typeQuery)) {
                        stmt.setString(1, orderId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                String type = rs.getString("type").trim().toLowerCase();

                                JPanel inspectionPanel;
                                if (type.equals("medicines")) {
                                    inspectionPanel = new ReceivedOrdersMedicines(receivedId);
                                } else {
                                    inspectionPanel = new ReceivedOrdersSM(receivedId);
                                }

                                // ✅ Show panel
                                javax.swing.JFrame frame = new javax.swing.JFrame();
                                frame.setUndecorated(true);
                                frame.getContentPane().add(inspectionPanel);
                                frame.pack();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);

                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Order type not found.");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a valid Received ID.");
        }
    }//GEN-LAST:event_viewbtninfoActionPerformed

    private void r_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_r_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_r_idActionPerformed

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // Clear the date pickers (JDateChooser)
        fromd.setDate(null);  // Clear the 'from' date
        tod.setDate(null);    // Clear the 'to' date

        // Clear the search JTextField
        searchidu.setText("");  // Clear the search text field

        // Clear the table (reset the model)
        DefaultTableModel model = (DefaultTableModel) orders_lists.getModel();
        model.setRowCount(0);  // Clear all rows in the table

        // Optionally, reload the table with all data (if needed)
        loadOrderSummary();  // Reload the initial data into the table
        loadActiveSuppliersToTable();
    }//GEN-LAST:event_jButton3MouseClicked


    
    private void inspectidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inspectidActionPerformed
    String orderId = enid.getText().trim(); // get the ID entered in the text field
    if (orderId.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a valid Order ID.");
        return;
    }

    String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(connectionUrl, dbUser, dbPass)) {
        String sql = """
            SELECT TOP (1) order_id, supplier_name, ordered_by, category, type,
                           approval_status, receiving_status, date_ordered, time_ordered
            FROM orders_info
            WHERE order_id = ? AND receiving_status IN ('Pending Inspection', 'Not Yet Inspected')
        """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, orderId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Order ID not found or already inspected.");
                    return;
                }

                // Extract values
                String orderedBy       = rs.getString("ordered_by");
                String supplier        = rs.getString("supplier_name");
                String category        = rs.getString("category");
                String type            = rs.getString("type");
                String approvalStatus  = rs.getString("approval_status");
                String receivingStatus = rs.getString("receiving_status");
                String dateOrd         = rs.getString("date_ordered");
                String timeOrd         = rs.getString("time_ordered");

                // Format time
                String formattedTime = timeOrd;
                if (timeOrd != null) {
                    try {
                        SimpleDateFormat inFmt = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat outFmt = new SimpleDateFormat("hh:mm a");
                        Date parsed = inFmt.parse(timeOrd);
                        formattedTime = outFmt.format(parsed);
                    } catch (Exception ex) {
                        // fallback to original
                    }
                }

                // Create items management panel
                ItemsManagement itemsPanel = new ItemsManagement();

                // Decide which inspect panel to open
                JFrame frame = new JFrame();
                frame.setUndecorated(true);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                if ("medicines".equalsIgnoreCase(type)) {
                    InspectMedicinesOrders orderPanel = new InspectMedicinesOrders(itemsPanel, orderId);
                    orderPanel.updateOrderDetails(orderedBy, supplier, orderId, category, type, approvalStatus, dateOrd, formattedTime);
                    orderPanel.loadOrderItemsToTable(orderId);
                    frame.getContentPane().add(orderPanel);
                } else if ("supplies".equalsIgnoreCase(type) || "materials".equalsIgnoreCase(type)) {
                    InspectSMOrders orderPanel = new InspectSMOrders(itemsPanel, orderId);
                    orderPanel.updateOrderDetails(orderedBy, supplier, orderId, category, type, approvalStatus, dateOrd, formattedTime);
                    orderPanel.loadOrderItemsToTable(orderId);
                    frame.getContentPane().add(orderPanel);
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown order type: " + type);
                    return;
                }

                // Show frame
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                // Close current frame
                dispose();
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "SQL error: " + ex.getMessage());
        ex.printStackTrace();
    }
    }//GEN-LAST:event_inspectidActionPerformed

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
         // clear search box
    searchidus.setText("");

    // reset s_type back to "All"
    s_type.setSelectedItem("All");

    // reload table
    loadActiveSuppliersToTable();
    }//GEN-LAST:event_refreshActionPerformed

    private void btnorderlistsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnorderlistsActionPerformed
    String[] options = {"Medicines", "Supplies & Materials", "Donation – Medicines"};
    int choice = JOptionPane.showOptionDialog(
        null,
        "What do you want to open?",
        "Choose Panel",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]
    );

    ItemsManagement itemsPanel = new ItemsManagement(); // shared dependency

    javax.swing.JFrame frame = new javax.swing.JFrame();
    frame.setUndecorated(true);

    if (choice == 0) { // Medicines selected
        MedicineOrderLists orderPanel = new MedicineOrderLists(itemsPanel);
        frame.getContentPane().add(orderPanel);
    } else if (choice == 1) { // Supplies & Materials selected
        SMOrderLists orderPanel = new SMOrderLists(itemsPanel);
        frame.getContentPane().add(orderPanel);
    } else if (choice == 2) { // Donation – Medicines selected
        DonationPanelMedicines donationPanel = new DonationPanelMedicines(itemsPanel);
        frame.getContentPane().add(donationPanel);
    } else {
        return; // Cancelled or closed
    }

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    dispose();
    }//GEN-LAST:event_btnorderlistsActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
         // reset filters
    statusreveal.setSelectedItem("All");   // reset status
    searchidu1.setText("");               // reset search box
    fromd1.setDate(null);                  // clear from date
    tod1.setDate(null);                    // clear to date

    // reload the tables
    loadOrderSummary();  
    loadPendingOrders();  
    loadActiveSuppliersToTable();  
    }//GEN-LAST:event_jButton4MouseClicked

    private void ViewPendingOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewPendingOrdersActionPerformed
    String searchReqId = order_id.getText().trim();
    if (searchReqId.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please enter a Request ID.");
        return;
    }

    try {
        PendingOrdersPanel updPanel = new PendingOrdersPanel();
        updPanel.loadPendingOrderItems(searchReqId);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Request Details", true);
        dialog.setUndecorated(true); // 🔴 Remove decorations
        dialog.setContentPane(updPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error opening update panel: " + ex.getMessage());
    }
    }//GEN-LAST:event_ViewPendingOrdersActionPerformed

    private void s_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_s_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_s_idActionPerformed

    private void order_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_order_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_order_idActionPerformed

    private void totaloforders1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totaloforders1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totaloforders1ActionPerformed

    private void printbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_printbtnMouseClicked
      
    }//GEN-LAST:event_printbtnMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ViewPendingOrders;
    private javax.swing.JButton btnorderlists;
    private javax.swing.JTextField enid;
    private com.toedter.calendar.JDateChooser f_date;
    private com.toedter.calendar.JDateChooser fromd;
    private com.toedter.calendar.JDateChooser fromd1;
    private javax.swing.JButton inspectid;
    private javax.swing.JTabbedPane itemsjtabbedpane;
    private javax.swing.JLabel itemst;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
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
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable loadorderinfo;
    private javax.swing.JTextField order_id;
    private javax.swing.JLabel ordered_t;
    private javax.swing.JPanel orderedt;
    private javax.swing.JTable orders_lists;
    private javax.swing.JTable pending_orders;
    private javax.swing.JButton printbtn;
    private javax.swing.JTextField r_id;
    private javax.swing.JLabel received_t;
    private javax.swing.JButton refresh;
    private javax.swing.JTextField s_id;
    private javax.swing.JComboBox<String> s_type;
    private javax.swing.JTextField searchidu;
    private javax.swing.JTextField searchidu1;
    private javax.swing.JTextField searchidus;
    private javax.swing.JComboBox<String> statusreveal;
    private javax.swing.JTable sup_activelists;
    private com.toedter.calendar.JDateChooser t_date;
    private com.toedter.calendar.JDateChooser tod;
    private com.toedter.calendar.JDateChooser tod1;
    private javax.swing.JTextField totalofitems;
    private javax.swing.JTextField totalofitems1;
    private javax.swing.JTextField totaloforders;
    private javax.swing.JTextField totaloforders1;
    private javax.swing.JTextField totalofquantites;
    private javax.swing.JTextField totalofquantites1;
    private javax.swing.JButton viewbtninfo;
    // End of variables declaration//GEN-END:variables
}
