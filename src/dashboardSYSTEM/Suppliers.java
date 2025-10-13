package dashboardSYSTEM;

    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Font;
    import java.awt.Window;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.event.KeyAdapter;
    import java.awt.event.KeyEvent;
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
import javax.swing.JLabel;
    import javax.swing.JTable;
    import javax.swing.SwingUtilities;
    import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
    import javax.swing.table.TableModel;
import java.sql.Statement;




public class Suppliers extends javax.swing.JPanel {

    
    public Suppliers() {
         initComponents();
         categorytypebox();
         suppliersstatus();
         loadSuppliersToTable();
         loadmanagetable();
         managestat();
         managecat();
         mataas();
         
        Color selectedColor = Color.decode("#4CAF50"); // green
        Color selectedTextColor = Color.WHITE;
        
        
             // After your table is initialized:
        JTableHeader header = t_suppliers.getTableHeader();

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
        JTableHeader header1 = manage_table.getTableHeader();

        header1.setDefaultRenderer(new DefaultTableCellRenderer() {
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

          tabsup.addChangeListener(e -> {
            for (int i = 0; i < tabsup.getTabCount(); i++) {
                tabsup.setBackgroundAt(i, Color.decode("#FFF967")); // reset all tabs to light yellow
            }
            int selectedIndex = tabsup.getSelectedIndex();
            tabsup.setBackgroundAt(selectedIndex, Color.decode("#4CAF50")); // green color
        });
          
        ma_type.setBackground(Color.WHITE);
        ma_stat.setBackground(Color.WHITE);
        categtype.setBackground(Color.WHITE);
        supplierstatus.setBackground(Color.WHITE);
        ma_status.setBackground(Color.WHITE);
        
        
         ma_search.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchAndFillCategoryDetails();
        }
        });
         
        ma_status.addActionListener(e -> autoFilterCatTableByDateRangeManage());
         
                 // Add PropertyChangeListener to the 'from_sup' and 'to_sup' date pickers
        ma_sup.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    autoFilterCatTableByDateRangeManage(); // Trigger the filter
                }
            }
        });

        mato_sup.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    autoFilterCatTableByDateRangeManage(); // Trigger the filter
                }
            }
        });
        
        masearch.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            autoFilterCatTableByDateRangeManage(); // Trigger filter on search text change
        }
       });
         
         
         
        date.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (date.getDate() != null) {
                updatetime();
            }
        }
        });
        
         ma_added.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ma_added.getDate() != null) {
                updatetimeManage();
            }
        }
        });
        
        
            // Add PropertyChangeListener to the 'from_sup' and 'to_sup' date pickers
        from_sup.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    autoFilterCatTableByDateRange(); // Trigger the filter
                }
            }
        });

        to_sup.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    autoFilterCatTableByDateRange(); // Trigger the filter
                }
            }
        });
        
        sup_search.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            autoFilterCatTableByDateRange(); // Trigger filter on search text change
        }
    });

    }
    
     private void searchAndFillCategoryDetails() {
         String searchID = ma_search.getText().trim();

    if (searchID.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please enter a Supplier ID to search.");
        return;
    }

    // Optional: Normalize input to SPL-xxx format if user enters just a number
    if (!searchID.startsWith("SPL-")) {
        try {
            int num = Integer.parseInt(searchID);
            searchID = String.format("SPL-%03d", num); // e.g. "5" → "SPL-005"
        } catch (NumberFormatException ignored) {
            // If not numeric, keep original input
        }
    }

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement pst = conn.prepareStatement("SELECT * FROM suppliers WHERE supplier_id = ?")) {

        pst.setString(1, searchID);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // Extract fields
                String name     = rs.getString("supplier_name");
                String email    = rs.getString("supplier_email");
                String phone    = rs.getString("supplier_pn");
                String address  = rs.getString("supplier_address");
                String category = rs.getString("type");
                String status   = rs.getString("status");
                String dateStr  = rs.getString("date");
                String timeRaw  = rs.getString("time");

                // Populate form fields
                ma_name.setText(name);
                ma_email.setText(email);
                ma_phone.setText(phone);
                ma_address.setText(address);
                ma_type.setSelectedItem(category);
                ma_stat.setSelectedItem(status);

                // Parse and set date
                try {
                    Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                    ma_added.setDate(parsedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    ma_added.setDate(null);
                }

                // Format and set time
                try {
                    Date parsedTime = new SimpleDateFormat("HH:mm:ss").parse(timeRaw);
                    String formattedTime = new SimpleDateFormat("hh:mm a").format(parsedTime);
                    time_added.setText(formattedTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                    time_added.setText(timeRaw); // fallback
                }

            } else {
                JOptionPane.showMessageDialog(null, "Supplier ID not found: " + searchID);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }	
    }
    
        private void managecat(){
        String[] type = {
            "Supplies",
            "Materials",
             "Medicines"
        };
        
        for (String role : type){
            ma_type.addItem(role);
        }
    }
       
        
     private void managestat(){
        String[] type = {
            "Active",
            "Inactive"
        };
        for (String role : type){
            ma_stat.addItem(role);
        }
        }
        
       
   
    private void categorytypebox(){
        String[] type = {
            "Supplies",
            "Materials",
             "Medicines"
        };
        
        for (String role : type){
            categtype.addItem(role);
        }
    }
       
        
     private void suppliersstatus(){
        String[] type = {
            "Active",
            "Inactive"
        };
        for (String role : type){
            supplierstatus.addItem(role);
        }
        }
     
      private void mataas(){
        String[] type = {
            "All",
            "Active",
            "Inactive"
        };
        for (String role : type){
            ma_status.addItem(role);
        }
        }

     private void updatetime() {
        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a"); 
        String currentTime = timeformat.format(new Date());
        time.setText(currentTime); 
    }
     
     private void updatetimeManage() {
        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a"); 
        String currentTime = timeformat.format(new Date());
        time_added.setText(currentTime); 
    }
     
  
  private void autoFilterCatTableByDateRange() {
    Date fromDate = from_sup.getDate();
    Date toDate = to_sup.getDate();
    String query = sup_search.getText();
    DefaultTableModel model = (DefaultTableModel) t_suppliers.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

    if (query != null && !query.trim().isEmpty()) {
        filters.add(RowFilter.regexFilter("(?i)" + query)); // Case-insensitive
    }

    if (fromDate != null && toDate != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromStr = sdf.format(fromDate);
        String toStr = sdf.format(toDate);

        RowFilter<DefaultTableModel, Object> dateFilter = new RowFilter<>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String dateValue = entry.getStringValue(5); // ✅ Column 5 is "date"
                return dateValue.compareTo(fromStr) >= 0 && dateValue.compareTo(toStr) <= 0;
            }
        };
        filters.add(dateFilter);
    }

    sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    t_suppliers.setRowSorter(sorter);
}
     
      private void autoFilterCatTableByDateRangeManage() {
      Date fromDate = ma_sup.getDate();
    Date toDate = mato_sup.getDate();
    String query = masearch.getText(); // Search text field
    String selectedStatus = (String) ma_status.getSelectedItem(); // Combo box

    DefaultTableModel model = (DefaultTableModel) manage_table.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

    // 🔍 Text Search Filter
    if (query != null && !query.trim().isEmpty()) {
        filters.add(RowFilter.regexFilter("(?i)" + query));
    }

    // 📅 Date Filter
    if (fromDate != null && toDate != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromStr = sdf.format(fromDate);
        String toStr = sdf.format(toDate);
        RowFilter<DefaultTableModel, Object> dateFilter = new RowFilter<>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String dateValue = entry.getStringValue(7); // Make sure column 7 is "date"
                return dateValue.compareTo(fromStr) >= 0 && dateValue.compareTo(toStr) <= 0;
            }
        };
        filters.add(dateFilter);
    }

    // ✅ Status Filter (Active/Inactive)
    if (selectedStatus != null && !selectedStatus.equalsIgnoreCase("All")) {
        RowFilter<DefaultTableModel, Object> statusFilter = new RowFilter<>() {
            @Override
            public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String statusValue = entry.getStringValue(6); // Make sure column 6 is "status"
                return statusValue.equalsIgnoreCase(selectedStatus);
            }
        };
        filters.add(statusFilter);
    }

    // Apply filters
    if (filters.isEmpty()) {
        sorter.setRowFilter(null);
    } else {
        sorter.setRowFilter(RowFilter.andFilter(filters));
    }
    manage_table.setRowSorter(sorter);
      }
  
   private void loadSuppliersToTable() {
    DefaultTableModel model = (DefaultTableModel) t_suppliers.getModel();
    model.setRowCount(0); // Clear existing rows

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement pst = conn.prepareStatement(
             "SELECT supplier_id, supplier_name, supplier_email, supplier_pn, supplier_address, date, time FROM suppliers");
         ResultSet rs = pst.executeQuery()) {

        SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a");

        while (rs.next()) {
            String id      = rs.getString("supplier_id");      // ✅ SPL-xxx
            String name    = rs.getString("supplier_name");
            String email   = rs.getString("supplier_email");
            String phone   = rs.getString("supplier_pn");
            String address = rs.getString("supplier_address");
            String date    = rs.getString("date");
            String timeRaw = rs.getString("time");

            String formattedTime = timeRaw;
            try {
                Date parsedTime = inputTimeFormat.parse(timeRaw);
                formattedTime = outputTimeFormat.format(parsedTime);
            } catch (Exception e) {
                e.printStackTrace();
            }

            model.addRow(new Object[]{id, name, email, phone, address, date, formattedTime});
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        t_suppliers.setRowSorter(sorter);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage());
    }
}
      
     
     private void loadmanagetable() {
     DefaultTableModel model = (DefaultTableModel) manage_table.getModel();
    model.setRowCount(0); // Clear existing rows

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement pst = conn.prepareStatement(
             "SELECT supplier_id, supplier_name, supplier_email, supplier_pn, supplier_address, type, status, date, time FROM suppliers");
         ResultSet rs = pst.executeQuery()) {

        SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a");

        while (rs.next()) {
            String id       = rs.getString("supplier_id"); // ✅ FIXED: use getString
            String name     = rs.getString("supplier_name");
            String email    = rs.getString("supplier_email");
            String phone    = rs.getString("supplier_pn");
            String address  = rs.getString("supplier_address");
            String category = rs.getString("type");
            String status   = rs.getString("status");
            String date     = rs.getString("date");
            String timeRaw  = rs.getString("time");

            String formattedTime = timeRaw;
            try {
                Date parsedTime = inputTimeFormat.parse(timeRaw);
                formattedTime = outputTimeFormat.format(parsedTime);
            } catch (Exception e) {
                e.printStackTrace();
            }

            model.addRow(new Object[]{id, name, email, phone, address, category, status, date, formattedTime});
        }

        // Apply filters after loading
        autoFilterCatTableByDateRangeManage();

        // Highlight inactive rows
        manage_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getValueAt(row, 6); // Column 6 = status

                if ("Inactive".equalsIgnoreCase(status)) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        manage_table.setRowSorter(sorter);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage());
    }
    }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        tabsup = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        supplier_address = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        t_suppliers = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        supplier_name = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        supplier_email = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        supplier_pn = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        supp_address = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        categtype = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        supplierstatus = new javax.swing.JComboBox<>();
        time = new javax.swing.JTextField();
        date = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        addsupplierbtn = new javax.swing.JButton();
        clearallfields = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        sup_search = new javax.swing.JTextField();
        to_sup = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        from_sup = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        managetab = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        manage_table = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        ma_name = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        ma_email = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        ma_phone = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        ma_address = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        ma_type = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        ma_stat = new javax.swing.JComboBox<>();
        time_added = new javax.swing.JTextField();
        ma_added = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        updatebtn = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        masearch = new javax.swing.JTextField();
        mato_sup = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        ma_sup = new com.toedter.calendar.JDateChooser();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        ma_search = new javax.swing.JTextField();
        ma_status = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        clearallfields2 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        backbtn = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(48, 122, 55));

        jPanel2.setBackground(new java.awt.Color(48, 122, 55));

        tabsup.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        supplier_address.setBackground(new java.awt.Color(255, 249, 103));

        t_suppliers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Supplier Name", "Email Address", "Phone Number", "Address", "Date ", "Time"
            }
        ));
        jScrollPane1.setViewportView(t_suppliers);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Supplier Name");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Email Address");

        supplier_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supplier_emailActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Phone Number");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Address");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Type");

        categtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Status");

        supplierstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        date.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dateMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Date Added");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Time Added");

        addsupplierbtn.setBackground(new java.awt.Color(0, 0, 0));
        addsupplierbtn.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        addsupplierbtn.setForeground(new java.awt.Color(255, 255, 255));
        addsupplierbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/plus (2).png"))); // NOI18N
        addsupplierbtn.setText("Add Supplier");
        addsupplierbtn.setPreferredSize(new java.awt.Dimension(107, 25));
        addsupplierbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addsupplierbtnMouseClicked(evt);
            }
        });

        clearallfields.setBackground(new java.awt.Color(0, 0, 0));
        clearallfields.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        clearallfields.setForeground(new java.awt.Color(255, 255, 255));
        clearallfields.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/delete (1).png"))); // NOI18N
        clearallfields.setText("Clear");
        clearallfields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearallfieldsActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel9.setText("Search");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Starting Date");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("End Date");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/parcel.png"))); // NOI18N
        jLabel25.setText("Add Suppliers");

        javax.swing.GroupLayout supplier_addressLayout = new javax.swing.GroupLayout(supplier_address);
        supplier_address.setLayout(supplier_addressLayout);
        supplier_addressLayout.setHorizontalGroup(
            supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplier_addressLayout.createSequentialGroup()
                .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, supplier_addressLayout.createSequentialGroup()
                            .addGap(17, 17, 17)
                            .addComponent(addsupplierbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(clearallfields, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(supplier_addressLayout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(supplier_email)
                                    .addComponent(supplier_pn)
                                    .addComponent(supp_address)
                                    .addComponent(supplier_name, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, supplier_addressLayout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(38, 38, 38)
                                        .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(supplierstatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                            .addComponent(time))
                                        .addGap(1, 1, 1))
                                    .addGroup(supplier_addressLayout.createSequentialGroup()
                                        .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(categtype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel6)
                                        .addGap(23, 23, 23)))
                                .addComponent(jLabel2)
                                .addComponent(jLabel1)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4))))
                    .addGroup(supplier_addressLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel25)))
                .addGap(18, 18, 18)
                .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(supplier_addressLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(2, 2, 2)
                        .addComponent(sup_search, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 294, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addGap(2, 2, 2)
                        .addComponent(from_sup, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(2, 2, 2)
                        .addComponent(to_sup, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE))
                .addGap(23, 23, 23))
        );
        supplier_addressLayout.setVerticalGroup(
            supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(supplier_addressLayout.createSequentialGroup()
                .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(supplier_addressLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel25))
                    .addGroup(supplier_addressLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(sup_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9))
                            .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel10)
                                .addComponent(to_sup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(from_sup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)))))
                .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(supplier_addressLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supplier_name, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supplier_email, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supplier_pn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supp_address, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(0, 0, 0)
                        .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(categtype, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(supplierstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(supplier_addressLayout.createSequentialGroup()
                                .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7))
                                .addGap(0, 0, 0)
                                .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(supplier_addressLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(supplier_addressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clearallfields, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addsupplierbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(supplier_addressLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(supplier_address, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(supplier_address, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabsup.addTab("Add Suppliers", jPanel1);

        jPanel3.setBackground(new java.awt.Color(255, 249, 103));

        managetab.setBackground(new java.awt.Color(255, 249, 103));

        manage_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Supplier ID", "Supplier Name", "Email Address", "Phone Number", "Address", "Category", "Status", "Date ", "Time"
            }
        ));
        jScrollPane2.setViewportView(manage_table);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Supplier Name");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Email Address");

        ma_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ma_emailActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Phone Number");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Address");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Type");

        ma_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Status");

        ma_stat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        ma_added.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ma_addedMouseClicked(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Date Added");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Time Added");

        updatebtn.setBackground(new java.awt.Color(0, 0, 0));
        updatebtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        updatebtn.setForeground(new java.awt.Color(255, 255, 255));
        updatebtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/plus (2).png"))); // NOI18N
        updatebtn.setText("Update");
        updatebtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updatebtnMouseClicked(evt);
            }
        });

        remove.setBackground(new java.awt.Color(0, 0, 0));
        remove.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        remove.setForeground(new java.awt.Color(255, 255, 255));
        remove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/remove (1).png"))); // NOI18N
        remove.setText("Remove");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel20.setText("Search");

        masearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        masearch.setForeground(new java.awt.Color(255, 51, 0));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Starting Date");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("End Date");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel23.setText("Search ID");

        ma_search.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ma_search.setForeground(new java.awt.Color(255, 51, 0));

        ma_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        ma_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ma_statusActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("Status");

        clearallfields2.setBackground(new java.awt.Color(0, 0, 0));
        clearallfields2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clearallfields2.setForeground(new java.awt.Color(255, 255, 255));
        clearallfields2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/delete (1).png"))); // NOI18N
        clearallfields2.setText("Clear");
        clearallfields2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearallfields2ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/supplier (7).png"))); // NOI18N
        jLabel26.setText("Manage Suppliers");

        javax.swing.GroupLayout managetabLayout = new javax.swing.GroupLayout(managetab);
        managetab.setLayout(managetabLayout);
        managetabLayout.setHorizontalGroup(
            managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(managetabLayout.createSequentialGroup()
                .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, managetabLayout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(managetabLayout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(jLabel18))
                                .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ma_type, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ma_added, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(45, 45, 45)
                            .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(ma_stat, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(time_added, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(managetabLayout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(jLabel19)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(managetabLayout.createSequentialGroup()
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(managetabLayout.createSequentialGroup()
                                    .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(managetabLayout.createSequentialGroup()
                                            .addComponent(jLabel17)
                                            .addGap(58, 58, 58))
                                        .addComponent(ma_address, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ma_name, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ma_email, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ma_phone, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(12, 12, 12))
                                .addGroup(managetabLayout.createSequentialGroup()
                                    .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(managetabLayout.createSequentialGroup()
                                            .addGap(6, 6, 6)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(managetabLayout.createSequentialGroup()
                                            .addComponent(jLabel23)
                                            .addGap(2, 2, 2)
                                            .addComponent(ma_search, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(119, 119, 119))
                                .addGroup(managetabLayout.createSequentialGroup()
                                    .addComponent(updatebtn)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(remove)
                                    .addGap(12, 12, 12)
                                    .addComponent(clearallfields2)
                                    .addGap(6, 6, 6)))))
                    .addGroup(managetabLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel26)
                        .addGap(122, 122, 122)))
                .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(managetabLayout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(masearch, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ma_status, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ma_sup, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mato_sup, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 805, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        managetabLayout.setVerticalGroup(
            managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(managetabLayout.createSequentialGroup()
                .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(managetabLayout.createSequentialGroup()
                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(managetabLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel26))
                            .addGroup(managetabLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(mato_sup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(ma_sup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel22))
                                        .addComponent(jLabel21))
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(ma_search, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addGap(3, 3, 3)
                        .addComponent(ma_name, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ma_email, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ma_phone, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ma_address, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addGap(0, 0, 0)
                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ma_type, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ma_stat, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(managetabLayout.createSequentialGroup()
                                .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel18))
                                .addGap(0, 0, 0)
                                .addComponent(time_added, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(managetabLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(ma_added, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clearallfields2)
                            .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(updatebtn)
                                .addComponent(remove))))
                    .addGroup(managetabLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ma_status, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(managetabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(masearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(managetab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(managetab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabsup.addTab("Manage Suppliers", jPanel3);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(tabsup, javax.swing.GroupLayout.PREFERRED_SIZE, 1148, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsup, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        backbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/left.png"))); // NOI18N
        backbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backbtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backbtnMouseEntered(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("SUPPLIERS MANAGEMENT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(backbtn)
                .addGap(429, 429, 429)
                .addComponent(jLabel27)
                .addContainerGap(481, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(26, Short.MAX_VALUE)
                        .addComponent(backbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void supplier_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supplier_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_supplier_emailActionPerformed

    private void clearUserFields() {
       
        supplier_name.setText("");
        supplier_email.setText("");
        supplier_pn.setText("");
        supp_address.setText("");
        time.setText("");
        date.setDate(null);
        categtype.setSelectedIndex(-1);
        supplierstatus.setSelectedIndex(-1);
        from_sup.setDate(null);   
        to_sup.setDate(null);
        sup_search.setText("");
        
        loadSuppliersToTable();     
        loadmanagetable();
    }
    
      private void AFTERUPDATE() {
      // Clear manage panel fields
        ma_name.setText("");
        ma_email.setText(""); 
        ma_phone.setText("");  
        ma_address.setText("");
        masearch.setText("");          
        ma_added.setDate(null);                     
        ma_type.setSelectedIndex(-1);     
        ma_stat.setSelectedIndex(-1);
        ma_status.setSelectedIndex(-1);        
        ma_sup.setDate(null);   
        mato_sup.setDate(null);   
        time_added.setText(""); 
        ma_search.setText("");

        // Clear add panel fields too
        clearUserFields();     

        loadSuppliersToTable();     
        loadmanagetable();
    }
    
    private void clearallfieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearallfieldsActionPerformed
        clearUserFields();
    }//GEN-LAST:event_clearallfieldsActionPerformed

    private void addsupplierbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addsupplierbtnMouseClicked
     // ✅ get selected date from date picker
    Date selectedDate = date.getDate();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = (selectedDate != null) ? dateFormat.format(selectedDate) : null;

    // ✅ get current time
    String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

    // ✅ get form data
    String name = supplier_name.getText().trim();
    String email = supplier_email.getText().trim();
    String phone = supplier_pn.getText().trim();
    String address = supp_address.getText().trim();
    String suptype = categtype.getSelectedItem() != null ? categtype.getSelectedItem().toString() : "";
    String supstatus = supplierstatus.getSelectedItem() != null ? supplierstatus.getSelectedItem().toString() : "";

    // ✅ input validations
    if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()
            || suptype.isEmpty() || supstatus.isEmpty() || formattedDate == null) {
        JOptionPane.showMessageDialog(this, "please fill in all required fields.", "validation error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
        JOptionPane.showMessageDialog(this, "invalid email format.", "validation error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!phone.matches("\\d{10,15}")) {
        JOptionPane.showMessageDialog(this, "phone number must contain only digits (10–15 characters).", "validation error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ✅ connect and insert supplier
    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025")) {

        // ✅ generate new supplier id
        String newSupplierID = "SPL-001";
        String idQuery = "SELECT MAX(supplier_id) AS lastID FROM suppliers";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(idQuery)) {
            if (rs.next()) {
                String lastID = rs.getString("lastID"); // e.g. SPL-009

                if (lastID != null && lastID.startsWith("SPL-")) {
                    try {
                        int num = Integer.parseInt(lastID.substring(4)); // extract 009 → 9
                        num++; // increment
                        newSupplierID = String.format("SPL-%03d", num); // SPL-010
                    } catch (NumberFormatException e) {
                        newSupplierID = "SPL-001"; // fallback if bad format
                    }
                }
            }
        }

        // ✅ insert supplier data
        String sql = """
            INSERT INTO suppliers 
            (supplier_id, supplier_name, supplier_email, supplier_pn, supplier_address, type, status, date, time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newSupplierID);
            pst.setString(2, name);
            pst.setString(3, email);
            pst.setString(4, phone);
            pst.setString(5, address);
            pst.setString(6, suptype);
            pst.setString(7, supstatus);
            pst.setString(8, formattedDate);
            pst.setString(9, currentTime);

            pst.executeUpdate();
        }

        // ✅ success message
        JOptionPane.showMessageDialog(this, "supplier added successfully! (id: " + newSupplierID + ")");

        // ✅ reload tables
        loadSuppliersToTable();
        loadmanagetable();
        AFTERUPDATE();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "database error: " + e.getMessage());
    }
    }//GEN-LAST:event_addsupplierbtnMouseClicked

    private void dateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dateMouseClicked
 
    }//GEN-LAST:event_dateMouseClicked

    private void clearallfields2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearallfields2ActionPerformed
         AFTERUPDATE();
    }//GEN-LAST:event_clearallfields2ActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
       int selectedRow = manage_table.getSelectedRow();
       String supplierId;

    if (selectedRow == -1) {
        supplierId = ma_search.getText().trim(); // Assuming you use masearch for manual search
        if (supplierId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a supplier or enter a Supplier ID to remove.");
            return;
        }
    } else {
        supplierId = manage_table.getValueAt(selectedRow, 0).toString(); // 0 = supplier_id column
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete supplier ID " + supplierId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
           Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
            );
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, supplierId);
        int rowsDeleted = pst.executeUpdate();
        conn.close();

        if (rowsDeleted > 0) {
            JOptionPane.showMessageDialog(this, "Supplier deleted successfully.");
            loadSuppliersToTable();
            loadmanagetable();
            AFTERUPDATE();
        } else {
            JOptionPane.showMessageDialog(this, "No supplier found with ID: " + supplierId);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error deleting supplier: " + e.getMessage());
    }
    }//GEN-LAST:event_removeActionPerformed

    private void updatebtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updatebtnMouseClicked
    int selectedRow = manage_table.getSelectedRow();
    String supplierId;

    // Get supplier ID from selected row or manual input
    if (selectedRow == -1) {
        supplierId = ma_search.getText().trim();
        if (supplierId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a supplier or enter a Supplier ID to update.");
            return;
        }
    } else {
        supplierId = manage_table.getValueAt(manage_table.convertRowIndexToModel(selectedRow), 0).toString();
    }

    // Get updated data from form fields
    String name = ma_name.getText().trim();
    String email = ma_email.getText().trim();
    String phone = ma_phone.getText().trim();
    String address = ma_address.getText().trim();
    String category = ma_type.getSelectedItem().toString();
    String status = ma_stat.getSelectedItem().toString();
    Date date = ma_added.getDate(); // Make sure it's a JDateChooser
    String inputTime = time_added.getText().trim();

    // Validate input
    if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()
            || category.isEmpty() || status.isEmpty() || date == null || inputTime.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please fill in all the fields.");
        return;
    }

    // Format the date and time
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = sdfDate.format(date);

    SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
    String formattedTime;
    try {
        Date parsedTime = inputFormat.parse(inputTime);
        formattedTime = outputFormat.format(parsedTime);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(null, "Invalid time format. Please use hh:mm AM/PM format.");
        return;
    }

    // Update supplier in DB
    try {
           Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
            );
        String sql = "UPDATE suppliers SET supplier_name = ?, supplier_email = ?, supplier_pn = ?, supplier_address = ?, type = ?, status = ?, date = ?, time = ? WHERE supplier_id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, name);
        pst.setString(2, email);
        pst.setString(3, phone);
        pst.setString(4, address);
        pst.setString(5, category);
        pst.setString(6, status);
        pst.setString(7, formattedDate);
        pst.setString(8, formattedTime);
        pst.setString(9, supplierId);

        int rowsAffected = pst.executeUpdate();
        conn.close();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Supplier updated successfully.");
            loadSuppliersToTable();
            loadmanagetable();
            clearUserFields();
            AFTERUPDATE();
        } else {
            JOptionPane.showMessageDialog(null, "No supplier found with ID: " + supplierId);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }

    }//GEN-LAST:event_updatebtnMouseClicked

    private void ma_addedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ma_addedMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_ma_addedMouseClicked

    private void ma_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ma_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ma_emailActionPerformed

    private void ma_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ma_statusActionPerformed
       
    }//GEN-LAST:event_ma_statusActionPerformed

    private void backbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backbtnMouseClicked
          // Get the parent window (JFrame) of this panel
    Window window = SwingUtilities.getWindowAncestor(this);
    if (window != null) {
        window.dispose(); // Close the window
    }

    // Open the homepage window
    homepageSYSTEM homepage = new homepageSYSTEM();
    homepage.setVisible(true);
    }//GEN-LAST:event_backbtnMouseClicked

    private void backbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backbtnMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_backbtnMouseEntered
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addsupplierbtn;
    private javax.swing.JLabel backbtn;
    private javax.swing.JComboBox<String> categtype;
    private javax.swing.JButton clearallfields;
    private javax.swing.JButton clearallfields2;
    private com.toedter.calendar.JDateChooser date;
    private com.toedter.calendar.JDateChooser from_sup;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser ma_added;
    private javax.swing.JTextField ma_address;
    private javax.swing.JTextField ma_email;
    private javax.swing.JTextField ma_name;
    private javax.swing.JTextField ma_phone;
    private javax.swing.JTextField ma_search;
    private javax.swing.JComboBox<String> ma_stat;
    private javax.swing.JComboBox<String> ma_status;
    private com.toedter.calendar.JDateChooser ma_sup;
    private javax.swing.JComboBox<String> ma_type;
    private javax.swing.JTable manage_table;
    private javax.swing.JPanel managetab;
    private javax.swing.JTextField masearch;
    private com.toedter.calendar.JDateChooser mato_sup;
    private javax.swing.JButton remove;
    private javax.swing.JTextField sup_search;
    private javax.swing.JTextField supp_address;
    private javax.swing.JPanel supplier_address;
    private javax.swing.JTextField supplier_email;
    private javax.swing.JTextField supplier_name;
    private javax.swing.JTextField supplier_pn;
    private javax.swing.JComboBox<String> supplierstatus;
    private javax.swing.JTable t_suppliers;
    private javax.swing.JTabbedPane tabsup;
    private javax.swing.JTextField time;
    private javax.swing.JTextField time_added;
    private com.toedter.calendar.JDateChooser to_sup;
    private javax.swing.JButton updatebtn;
    // End of variables declaration//GEN-END:variables
}
