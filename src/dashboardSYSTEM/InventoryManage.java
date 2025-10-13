package dashboardSYSTEM;


    import AddingStocks.AddStocksPanelMeds;
    import requestpanelsSYSTEM.RequestMedicines;
    import com.toedter.calendar.JDateChooser;
    import dashboardSYSTEM.homepageSYSTEM;
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
    import receivedpanelsSYSTEM.DamagedItemsStaus;

public class InventoryManage extends javax.swing.JPanel {

        
    private String loggedInUser;

    public InventoryManage() {
        initComponents();
        loadsuppliesmaterials();
        tablefmeds();
        searchSuppliesMaterials();    
        
            
        // Add ListSelectionListener to handle row selection color change
    loadmeds.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = loadmeds.getSelectedRow();
            if (selectedRow != -1) {
                // Highlight selected row
                loadmeds.setSelectionBackground(Color.decode("#4CAF50")); // Green color
                loadmeds.setSelectionForeground(Color.WHITE); // White text
            }
        }
    });

        
        JTableHeader header = loadmeds.getTableHeader();

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
        
 
        JTableHeader header1 = loadsuppliesmaterialstable.getTableHeader();
        header1.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
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
        searchuserid.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        PropertyChangeListener dateListener = evt -> {
                if ("date".equals(evt.getPropertyName())) {
                applyFilters();
                filterMedicines();
                }
            };
            
            start_date.getDateEditor().addPropertyChangeListener(dateListener);
            end_date.getDateEditor().addPropertyChangeListener(dateListener);
             mfg_date.getDateEditor().addPropertyChangeListener(dateListener);
            exp_date.getDateEditor().addPropertyChangeListener(dateListener);

            type.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                applyFilters();
                }
            });


            type.addItem("All");
            type.addItem("Supplies");
            type.addItem("Materials");

            barcodeinsert.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
            });

    

            // filter on text change
            m_seardid.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { filterMedicines(); }
                public void removeUpdate(DocumentEvent e) { filterMedicines(); }
                public void changedUpdate(DocumentEvent e) { filterMedicines(); }
            });

          
            updateTotals();

        }
    
        public void updateTotals(int totalItems, int totalQty, int suppliesCount, int materialsCount) {
            titems.setText(String.valueOf(totalItems));
            tqtystocks.setText(String.valueOf(totalQty));
            suppliestotal.setText(String.valueOf(suppliesCount));
            materialstotal.setText(String.valueOf(materialsCount));
        }
     
        public void applyFilters() {
            String keyword = searchuserid.getText().trim();
            Date start = start_date.getDate();
            Date end = end_date.getDate();
            String selectedType = (String) type.getSelectedItem();
            String barcode = barcodeinsert.getText().trim();

            filterSuppliesMaterials(keyword, start, end, selectedType, barcode);
        }

    public void filterSuppliesMaterials(String keyword, Date startDate, Date endDate, String typeFilter, String barcode) {
       StringBuilder query = new StringBuilder("SELECT [supmat_id], [item_name], [description], [units], [qty_stocks], [date_receive], [type], [barcodes] FROM [nchdbase].[dbo].[supplies_materials] WHERE 1=1");

       List<Object> parameters = new ArrayList<>();

       if (!keyword.isEmpty()) {
           query.append(" AND ([supmat_id] = ? OR [item_name] LIKE ?)");
           try {
               parameters.add(Integer.parseInt(keyword));
           } catch (NumberFormatException e) {
               parameters.add(null);
           }
           parameters.add("%" + keyword + "%");
       }

       if (startDate != null && endDate != null) {
           query.append(" AND [date_receive] BETWEEN ? AND ?");
           parameters.add(new java.sql.Date(startDate.getTime()));
           parameters.add(new java.sql.Date(endDate.getTime()));
       }

       if (typeFilter != null && !typeFilter.equalsIgnoreCase("All")) {
           query.append(" AND [type] = ?");
           parameters.add(typeFilter);
       }

       if (!barcode.isEmpty()) {
           query.append(" AND [barcodes] LIKE ?");
           parameters.add("%" + barcode + "%");
       }

       try (Connection conn = DriverManager.getConnection(
               "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
               "admin", "yeyel2025");
            PreparedStatement stmt = conn.prepareStatement(query.toString())) {

           for (int i = 0; i < parameters.size(); i++) {
               Object param = parameters.get(i);
               if (param instanceof String) stmt.setString(i + 1, (String) param);
               else if (param instanceof Integer) stmt.setInt(i + 1, (Integer) param);
               else if (param instanceof java.sql.Date) stmt.setDate(i + 1, (java.sql.Date) param);
               else stmt.setNull(i + 1, java.sql.Types.NULL);
           }

           ResultSet rs = stmt.executeQuery();
           DefaultTableModel model = (DefaultTableModel) loadsuppliesmaterialstable.getModel();
           model.setRowCount(0);

           int suppliesCount = 0, materialsCount = 0, totalQty = 0;

           while (rs.next()) {
               String itemType = rs.getString("type");
               int qty = rs.getInt("qty_stocks");

               if ("Supplies".equalsIgnoreCase(itemType)) suppliesCount++;
               else if ("Materials".equalsIgnoreCase(itemType)) materialsCount++;

               totalQty += qty;

               model.addRow(new Object[]{
                   rs.getInt("supmat_id"),
                   rs.getString("item_name"),
                   rs.getString("description"),
                   rs.getString("units"),
                   qty,
                   rs.getDate("date_receive"),
                   itemType,
                   rs.getString("barcodes")
               });
           }

           rs.close();
           updateTotals(model.getRowCount(), totalQty, suppliesCount, materialsCount);
       } catch (SQLException e) {
           JOptionPane.showMessageDialog(null, "Filter error: " + e.getMessage());
       }
   }


    
    
    private void searchSuppliesMaterials() {
       String keyword = searchuserid.getText().trim();
       Date startDate = start_date.getDate();
       Date endDate = end_date.getDate();

       String query = "SELECT [supmat_id], [item_name], [description], [units], [qty_stocks], [date_receive], [type], [barcodes] " +
                      "FROM [nchdbase].[dbo].[supplies_materials] " +
                      "WHERE ([supmat_id] = ? OR [item_name] LIKE ?)";

       // If both dates are selected, add date filter
       if (startDate != null && endDate != null) {
           query += " AND [date_receive] BETWEEN ? AND ?";
       }

       try (Connection conn = DriverManager.getConnection(
               "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
               "admin", "yeyel2025");
            PreparedStatement stmt = conn.prepareStatement(query)) {

           // Bind ID and name parameters
           try {
               int id = Integer.parseInt(keyword);
               stmt.setInt(1, id);
           } catch (NumberFormatException e) {
               stmt.setNull(1, java.sql.Types.INTEGER);
           }
           stmt.setString(2, "%" + keyword + "%");

           // Bind date parameters if available
           if (startDate != null && endDate != null) {
               stmt.setDate(3, new java.sql.Date(startDate.getTime()));
               stmt.setDate(4, new java.sql.Date(endDate.getTime()));
           }

           ResultSet rs = stmt.executeQuery();
           DefaultTableModel model = (DefaultTableModel) loadsuppliesmaterialstable.getModel();
           model.setRowCount(0); // clear table

           while (rs.next()) {
               Object[] row = {
                   rs.getInt("supmat_id"),
                   rs.getString("item_name"),
                   rs.getString("description"),
                   rs.getString("units"),
                   rs.getInt("qty_stocks"),
                   rs.getDate("date_receive"),
                   rs.getString("type"),
                   rs.getString("barcodes")
               };
               model.addRow(row);
           }

           rs.close();
       } catch (SQLException e) {
           JOptionPane.showMessageDialog(null, "Error searching supplies: " + e.getMessage());
       }
   }
    
  
    
    
    public void loadsuppliesmaterials() {
        // Removed supplier_name and remarks
        String query = "SELECT TOP (1000) [supmat_id], [item_name], [description], [units], [qty_stocks], [date_receive], [type], [barcodes] FROM [nchdbase].[dbo].[supplies_materials]";

        // Updated column names
        String[] columnNames = {
            "supmat_id", "item name", "description", "units", "qty in stocks", "date received",
            "type", "barcodes"
        };

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                Object[] row = new Object[columnNames.length];
                row[0] = rs.getString("supmat_id");     // Column 1: supmat_id
                row[1] = rs.getString("item_name");     // Column 2: item name
                row[2] = rs.getString("description");   // Column 3: description
                row[3] = rs.getString("units");         // Column 4: units
                row[4] = rs.getInt("qty_stocks");       // Column 5: qty in stocks
                row[5] = rs.getDate("date_receive");    // Column 6: date received
                row[6] = rs.getString("type");          // Column 7: type
                row[7] = rs.getString("barcodes");      // Column 8: barcodes

                model.addRow(row);
            }

            SwingUtilities.invokeLater(() -> {
                loadsuppliesmaterialstable.setModel(model);
            });
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }
    
 
public void tablefmeds() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String query = "SELECT GenericID, GenericName, Units, Description, QuantityInStock, MfgDate, ExpDate, BatchNo, StockStatus, Condition FROM medicines";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        DefaultTableModel medsModel = (DefaultTableModel) loadmeds.getModel();
        String[] columns = {"GenericID", "GenericName", "Units", "Description", "Qty In Stock", "Mfg Date", "Exp Date", "Batch", "Stock Status", "Condition"};
        medsModel.setColumnIdentifiers(columns);
        medsModel.setRowCount(0);

        while (rs.next()) {
            String id = rs.getString("GenericID"); // ✅ FIXED
            String name = rs.getString("GenericName");
            String units = rs.getString("Units");
            String desc = rs.getString("Description");
            int qty = rs.getInt("QuantityInStock");
            Date mfg = rs.getDate("MfgDate");
            Date exp = rs.getDate("ExpDate");
            String batch = rs.getString("BatchNo");

            String stockStatus = (qty == 0) ? "Out of Stock" : (qty <= 10) ? "Low Stock" : "In Stock";
            String condition = rs.getString("Condition");
            if (condition == null || condition.isEmpty()) {
                condition = (exp != null && exp.before(new Date())) ? "Expired" : "Good";
            }

            medsModel.addRow(new Object[]{id, name, units, desc, qty, mfg, exp, batch, stockStatus, condition});
        }

        loadmeds.setModel(medsModel);

        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < loadmeds.getColumnCount(); i++) {
            loadmeds.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // GenericID: red text
        DefaultTableCellRenderer idRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.RED);
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        loadmeds.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        // GenericName: green text
        DefaultTableCellRenderer nameRenderer = new DefaultTableCellRenderer() {
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
        loadmeds.getColumnModel().getColumn(1).setCellRenderer(nameRenderer);

        // StockStatus: red if "Low Stock"
        DefaultTableCellRenderer stockStatusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                c.setForeground("Low Stock".equalsIgnoreCase(status) ? Color.RED : Color.BLACK);
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        loadmeds.getColumnModel().getColumn(8).setCellRenderer(stockStatusRenderer);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Failed to load medicines table: " + e.getMessage());
    }
}

public void filterMedicines() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String query = "SELECT GenericID, GenericName, Units, Description, QuantityInStock, MfgDate, ExpDate, BatchNo, StockStatus, Condition FROM medicines";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        DefaultTableModel medsModel = (DefaultTableModel) loadmeds.getModel();
        String[] columns = {"GenericID", "GenericName", "Units", "Description", "Qty In Stock", "Mfg Date", "Exp Date", "Batch", "Stock Status", "Condition"};
        medsModel.setColumnIdentifiers(columns);
        medsModel.setRowCount(0);

        while (rs.next()) {
            String id = rs.getString("GenericID"); // ✅ FIXED
            String name = rs.getString("GenericName");
            String units = rs.getString("Units");
            String desc = rs.getString("Description");
            int qty = rs.getInt("QuantityInStock");
            Date mfg = rs.getDate("MfgDate");
            Date exp = rs.getDate("ExpDate");
            String batch = rs.getString("BatchNo");

            String stockStatus = rs.getString("StockStatus");
            if (stockStatus == null || stockStatus.isEmpty()) {
                stockStatus = (qty > 0) ? "In Stock" : "Out of Stock";
            }

            String condition = rs.getString("Condition");
            if (condition == null || condition.isEmpty()) {
                condition = (exp != null && exp.before(new Date())) ? "Expired" : "Good";
            }

            medsModel.addRow(new Object[]{id, name, units, desc, qty, mfg, exp, batch, stockStatus, condition});
        }

        // GenericID: bold red
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
        loadmeds.getColumnModel().getColumn(0).setCellRenderer(idRenderer);

        // Center all other columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < loadmeds.getColumnCount(); i++) {
            loadmeds.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Failed to load medicines table: " + e.getMessage());
    }
}


public void updateTotals() {
    DefaultTableModel model = (DefaultTableModel) loadmeds.getModel();

    int totalMeds = model.getRowCount();
    int totalStocks = 0;

    for (int i = 0; i < totalMeds; i++) {
        Object qtyObj = model.getValueAt(i, 4); // QuantityInStock
        if (qtyObj instanceof Integer) {
            totalStocks += (Integer) qtyObj;
        } else if (qtyObj != null) {
            try {
                totalStocks += Integer.parseInt(qtyObj.toString());
            } catch (NumberFormatException e) { }
        }
    }

    t_medicines.setText(String.valueOf(totalMeds));
    t_stocks.setText(String.valueOf(totalStocks));
}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        loadmeds = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        m_seardid = new javax.swing.JTextField();
        refreshmeds = new javax.swing.JButton();
        sd1 = new javax.swing.JLabel();
        mfg_date = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        reqmedicines = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        t_medicines = new javax.swing.JLabel();
        t_stocks = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        genid = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        viewgenid = new javax.swing.JButton();
        exp_date = new com.toedter.calendar.JDateChooser();
        addstockbtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        loadsuppliesmaterialstable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        searchuserid = new javax.swing.JTextField();
        start_date = new com.toedter.calendar.JDateChooser();
        end_date = new com.toedter.calendar.JDateChooser();
        sd = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        type = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        titems = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        REFRESH = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        barcodeinsert = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        suppliestotal = new javax.swing.JLabel();
        materialstotal = new javax.swing.JLabel();
        tqtystocks = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        stocktablemedicines = new javax.swing.JTable();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jButton2 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(48, 122, 55));
        jPanel1.setForeground(new java.awt.Color(255, 51, 0));
        jPanel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(48, 122, 55));

        jPanel5.setBackground(new java.awt.Color(255, 249, 103));

        loadmeds.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "GenericID", "Medicines Name", "Units", "Description", "Quantity In Stock", "Mfg Date", "Exp Date", "Batch NO."
            }
        ));
        jScrollPane1.setViewportView(loadmeds);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Search ID");

        refreshmeds.setBackground(new java.awt.Color(0, 0, 0));
        refreshmeds.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        refreshmeds.setForeground(new java.awt.Color(255, 255, 255));
        refreshmeds.setText("Refresh");
        refreshmeds.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refreshmedsMouseClicked(evt);
            }
        });

        sd1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sd1.setText("Exp Date");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Mfg Date");

        reqmedicines.setBackground(new java.awt.Color(0, 0, 0));
        reqmedicines.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        reqmedicines.setForeground(new java.awt.Color(255, 255, 255));
        reqmedicines.setText("Medicines Request");
        reqmedicines.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reqmedicinesMouseClicked(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Total qty of medicines");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Total qty of stocks");

        t_medicines.setBackground(new java.awt.Color(0, 0, 0));
        t_medicines.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        t_medicines.setForeground(new java.awt.Color(255, 255, 255));
        t_medicines.setText(" ");
        t_medicines.setOpaque(true);

        t_stocks.setBackground(new java.awt.Color(0, 0, 0));
        t_stocks.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        t_stocks.setForeground(new java.awt.Color(255, 255, 255));
        t_stocks.setText(" ");
        t_stocks.setOpaque(true);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Enter Generic ID :");

        viewgenid.setBackground(new java.awt.Color(0, 0, 0));
        viewgenid.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewgenid.setForeground(new java.awt.Color(255, 255, 255));
        viewgenid.setText("View");
        viewgenid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewgenidMouseClicked(evt);
            }
        });

        addstockbtn.setBackground(new java.awt.Color(0, 0, 0));
        addstockbtn.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        addstockbtn.setForeground(new java.awt.Color(255, 255, 255));
        addstockbtn.setText("Stock Request");
        addstockbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addstockbtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(2, 2, 2)
                        .addComponent(m_seardid, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshmeds)
                        .addGap(2, 2, 2)
                        .addComponent(addstockbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(2, 2, 2)
                        .addComponent(reqmedicines)
                        .addGap(236, 236, 236)
                        .addComponent(jLabel9)
                        .addGap(2, 2, 2)
                        .addComponent(mfg_date, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(sd1)
                        .addGap(2, 2, 2)
                        .addComponent(exp_date, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(t_medicines, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(t_stocks, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel18)
                        .addGap(2, 2, 2)
                        .addComponent(genid, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(viewgenid)
                .addGap(43, 43, 43))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_seardid, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(reqmedicines)
                        .addComponent(addstockbtn)
                        .addComponent(refreshmeds)
                        .addComponent(jLabel9))
                    .addComponent(sd1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfg_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exp_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(t_medicines, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel16)
                    .addComponent(t_stocks, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(viewgenid)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Medicines Inventory Lists", jPanel4);

        jPanel3.setBackground(new java.awt.Color(255, 249, 103));

        loadsuppliesmaterialstable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Item Name", "Description", "Units", "Quantity In Stocks", "Date Received", "Supplier Name", "Type", "Barcodes", "Remarks"
            }
        ));
        jScrollPane2.setViewportView(loadsuppliesmaterialstable);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel1.setText("Search ID");

        sd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sd.setText("End Date");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Starting Date");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Type");

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("CLICK");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Reques Items");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Total Items ");

        titems.setBackground(new java.awt.Color(0, 0, 0));
        titems.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        titems.setForeground(new java.awt.Color(255, 255, 255));
        titems.setText(" ");
        titems.setOpaque(true);
        titems.setPreferredSize(new java.awt.Dimension(4, 20));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Total Of Qty Stocks ");

        REFRESH.setBackground(new java.awt.Color(0, 0, 0));
        REFRESH.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        REFRESH.setForeground(new java.awt.Color(255, 255, 255));
        REFRESH.setText("Refresh");
        REFRESH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                REFRESHMouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Barcode");

        barcodeinsert.setForeground(new java.awt.Color(255, 0, 0));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Supplies Type Total ");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Materials Type Total ");

        suppliestotal.setBackground(new java.awt.Color(0, 0, 0));
        suppliestotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        suppliestotal.setForeground(new java.awt.Color(255, 255, 255));
        suppliestotal.setText(" ");
        suppliestotal.setOpaque(true);

        materialstotal.setBackground(new java.awt.Color(0, 0, 0));
        materialstotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        materialstotal.setForeground(new java.awt.Color(255, 255, 255));
        materialstotal.setText(" ");
        materialstotal.setOpaque(true);

        tqtystocks.setBackground(new java.awt.Color(0, 0, 0));
        tqtystocks.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tqtystocks.setForeground(new java.awt.Color(255, 255, 255));
        tqtystocks.setText(" ");
        tqtystocks.setOpaque(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(start_date, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(sd)
                        .addGap(2, 2, 2)
                        .addComponent(end_date, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(tqtystocks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(titems, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(71, 71, 71)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addGap(2, 2, 2)
                                        .addComponent(materialstotal, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel13)
                                        .addGap(2, 2, 2)
                                        .addComponent(suppliestotal, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(2, 2, 2)
                                .addComponent(searchuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(barcodeinsert, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(REFRESH)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 321, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addGap(2, 2, 2)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(19, 19, 19))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(start_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(end_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(searchuserid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(barcodeinsert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(REFRESH))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel2)
                    .addComponent(suppliestotal))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel14)
                    .addComponent(materialstotal)
                    .addComponent(tqtystocks))
                .addGap(82, 82, 82))
        );

        jTabbedPane1.addTab("Supplies & Materials Invetory Lists", jPanel3);

        jPanel6.setBackground(new java.awt.Color(255, 249, 103));
        jPanel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        stocktablemedicines.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Request ID", "Generic ID", "Quantity Request", "Approved By", "Approval Date", "Remarks", "Supplier Name"
            }
        ));
        jScrollPane3.setViewportView(stocktablemedicines);

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("PRINT");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("Search");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setText("To Date:");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setText("FROM DATE:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22)
                        .addGap(2, 2, 2)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addGap(2, 2, 2)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Stock Request", jPanel6);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 22, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 538, Short.MAX_VALUE))
        );

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/left.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("INVENTORY MANAGE");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addGap(452, 452, 452)
                        .addComponent(jLabel17))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // Get the parent window (JFrame) of this panel
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose(); // Close the window
        }
        // Open the homepage window
        homepageSYSTEM homepage = new homepageSYSTEM();
        homepage.setVisible(true);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void REFRESHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_REFRESHMouseClicked
        searchuserid.setText("");
        start_date.setDate(null);
        end_date.setDate(null);
        type.setSelectedIndex(0);

        loadsuppliesmaterials(); // make sure this updates the table model

        // compute totals manually if needed
        DefaultTableModel model = (DefaultTableModel) loadsuppliesmaterialstable.getModel();
        int totalItems = model.getRowCount();
        int totalQty = 0;
        int suppliesCount = 0;
        int materialsCount = 0;

        for (int i = 0; i < totalItems; i++) {
            Object qtyObj = model.getValueAt(i, 4);
            String typeVal = model.getValueAt(i, 6).toString(); // type is column 6

            try {
                totalQty += Integer.parseInt(qtyObj.toString());
            } catch (NumberFormatException e) {
                // ignore invalid quantity
            }

            if ("Supplies".equalsIgnoreCase(typeVal)) suppliesCount++;
            else if ("Materials".equalsIgnoreCase(typeVal)) materialsCount++;
        }

        updateTotals(totalItems, totalQty, suppliesCount, materialsCount);
    }//GEN-LAST:event_REFRESHMouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setUndecorated(true); // removes title bar and window controls

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        dispose(); // optional: close homepageSYSTEM
    }//GEN-LAST:event_jButton1MouseClicked

    private void addstockbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addstockbtnMouseClicked
        {
            // Launch AddStocksPanelMeds directly
            javax.swing.JFrame frame = new javax.swing.JFrame();
            frame.setUndecorated(false);

            AddStocksPanelMeds panel = new AddStocksPanelMeds();
            frame.getContentPane().add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
    }//GEN-LAST:event_addstockbtnMouseClicked
 }
    private void viewgenidMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewgenidMouseClicked
        String genIdText = genid.getText().trim(); // get value from JTextField
        if (genIdText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a GenericID.");
            return;
        }

        int genericId;
        try {
            genericId = Integer.parseInt(genIdText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid GenericID. It must be a number.");
            return;
        }

        // Create the panel with the generic ID
        DamagedItemsStaus damagedPanel = new DamagedItemsStaus(genericId);

        // Open in a new JFrame
        JFrame frame = new JFrame("Damaged Items Status");
        frame.setContentPane(damagedPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }//GEN-LAST:event_viewgenidMouseClicked

    private void reqmedicinesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reqmedicinesMouseClicked
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setUndecorated(true); // removes title bar and window controls
        frame.getContentPane().add(new RequestMedicines());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        dispose(); // optional: close homepageSYSTEM
    }//GEN-LAST:event_reqmedicinesMouseClicked

    private void refreshmedsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshmedsMouseClicked
        // TODO add your handling code here:// Clear search fields (e.g., MedicineID and Barcode)
        m_seardid.setText("");  // Clear the MedicineID search input

        // Clear date filters (e.g., Manufacturing Date and Expiration Date)
        mfg_date.setDate(null);  // Clear Manufacturing Date filter
        exp_date.setDate(null);  // Clear Expiration Date filter

        // Reset the "type" combo box (if applicable, for category filters)
        type.setSelectedIndex(0); // Assuming index 0 is for "All" or empty state

        // Reload the table data without any filters
        tablefmeds();  // This method reloads the entire data

        // Update totals (if you have total labels like "Total Medicines" or "Total Stocks")
        updateTotals();  // Make sure to call this to update the total label values

        // Optionally, if you have any other UI components that need to be reset, you can add that here
    }//GEN-LAST:event_refreshmedsMouseClicked
  



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton REFRESH;
    private javax.swing.JButton addstockbtn;
    private javax.swing.JTextField barcodeinsert;
    private com.toedter.calendar.JDateChooser end_date;
    private com.toedter.calendar.JDateChooser exp_date;
    private javax.swing.JTextField genid;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable loadmeds;
    private javax.swing.JTable loadsuppliesmaterialstable;
    private javax.swing.JTextField m_seardid;
    private javax.swing.JLabel materialstotal;
    private com.toedter.calendar.JDateChooser mfg_date;
    private javax.swing.JButton refreshmeds;
    private javax.swing.JButton reqmedicines;
    private javax.swing.JLabel sd;
    private javax.swing.JLabel sd1;
    private javax.swing.JTextField searchuserid;
    private com.toedter.calendar.JDateChooser start_date;
    private javax.swing.JTable stocktablemedicines;
    private javax.swing.JLabel suppliestotal;
    private javax.swing.JLabel t_medicines;
    private javax.swing.JLabel t_stocks;
    private javax.swing.JLabel titems;
    private javax.swing.JLabel tqtystocks;
    private javax.swing.JComboBox<String> type;
    private javax.swing.JButton viewgenid;
    // End of variables declaration//GEN-END:variables
}