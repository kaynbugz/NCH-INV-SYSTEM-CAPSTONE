
package dashboardSYSTEM;

    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Dimension;
    import java.awt.Font;
    import java.awt.Insets;
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
    import javax.swing.JButton;
    import javax.swing.JLabel;
    import javax.swing.JScrollBar;
    import javax.swing.JScrollPane;
    import javax.swing.JTable;
    import javax.swing.JTextArea;
    import javax.swing.SwingUtilities;
    import javax.swing.plaf.basic.BasicScrollBarUI;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;
    import javax.swing.table.TableModel;

    public class Categories extends javax.swing.JPanel {

 
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel masterCategoryModel; // for cat_table
    private DefaultTableModel masterManageCategoryModel; // for cat_table_manage

    
    public Categories() {
        initComponents();
        typebox();
        fetchCategoryData() ;
        loadCategoryList();
        categbox();
        
        Color selectedColor = Color.decode("#4CAF50"); // green
        Color selectedTextColor = Color.WHITE;

          cat_table_manage.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
           @Override
           public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int row, int column) {
               Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

               // If the row is selected, apply custom background and foreground colors
               if (isSelected) {
                   c.setBackground(selectedColor);
                   c.setForeground(selectedTextColor);
               } else {
                   // When the row is not selected, revert to default colors
                   c.setBackground(Color.WHITE);
                   c.setForeground(Color.BLACK);
               }

               return c;
           }
       });
        
         cat_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(selectedColor);
                    c.setForeground(selectedTextColor);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });
        
        cat_search.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
               autoFilterCatTableByDateRange();
            }
        });
        
         s_cat.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            String query = s_cat.getText();
            filterTableBySearchManage(query);
        }
        });
      
         
         categorymanage.addChangeListener(e -> {
            for (int i = 0; i < categorymanage.getTabCount(); i++) {
                categorymanage.setBackgroundAt(i, Color.decode("#FFF967")); // reset all tabs to light yellow
            }
            int selectedIndex = categorymanage.getSelectedIndex();
            categorymanage.setBackgroundAt(selectedIndex, Color.decode("#4CAF50")); // green color
        });
         
        catid_search.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchAndFillCategoryDetails();
        }
        });
        
        c_date.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (c_date.getDate() != null) {
                updateCurrentTime();
            }
        }
        });
        
         categ_d.getDateEditor().addPropertyChangeListener("date", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (categ_d.getDate() != null) {
                 updateCurrentTimeCategs();
            }
        }
        });

        // Listener for "From Date"
        from_c.addPropertyChangeListener("date", evt -> {
            if (evt.getNewValue() != null) {
                autoFilterCatTableByDateRange();
            }
        });

        // Listener for "To Date"
        to_c.addPropertyChangeListener("date", evt -> {
            if (evt.getNewValue() != null) {
                autoFilterCatTableByDateRange();
            }
        });

       // Listener for search field to filter automatically
       cat_search.addKeyListener(new KeyAdapter() {
           @Override
           public void keyReleased(KeyEvent e) {
               autoFilterCatTableByDateRange();
           }
       });

          date_f.addPropertyChangeListener(new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               if ("date".equals(evt.getPropertyName())) {
                   autoFilterCatTableKManageByDateRange();
               }
           }
       });
        
  
          // After your table is initialized:
        JTableHeader header = cat_table.getTableHeader();

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
        JTableHeader header2 = cat_table_manage.getTableHeader();

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
        
        
        

          
        type_combo.setBackground(Color.WHITE);
        category_box.setBackground(Color.WHITE);
        
          date_t.addPropertyChangeListener(new PropertyChangeListener() {
           @Override
           public void propertyChange(PropertyChangeEvent evt) {
               if ("date".equals(evt.getPropertyName())) {
                   autoFilterCatTableKManageByDateRange();
               }
           }
       });
       }
    
    
    
        
       private void filterTableBySearchManage(String query) {
       DefaultTableModel model = (DefaultTableModel) cat_table_manage.getModel();
       TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
       cat_table_manage.setRowSorter(sorter);

       if (query.trim().length() == 0) {
           sorter.setRowFilter(null); // Show all if empty
       } else {
           sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query)); // Case-insensitive match
       }
    }
       
    private void autoFilterCatTableByDateRange() {
    Date fromDate = from_c.getDate();
    Date toDate = to_c.getDate();
    String query = cat_search.getText(); // Get text from the search field
    DefaultTableModel model = (DefaultTableModel) cat_table.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

    // Add search filter if not empty
    if (query != null && !query.trim().isEmpty()) {
        filters.add(RowFilter.regexFilter("(?i)" + query)); // Case-insensitive
    }

    // Add date filter if both dates are selected
    if (fromDate != null && toDate != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromStr = sdf.format(fromDate);
        String toStr = sdf.format(toDate);

        RowFilter<DefaultTableModel, Object> dateFilter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String dateValue = entry.getStringValue(3); // Correct index for "Date" column
                return dateValue.compareTo(fromStr) >= 0 && dateValue.compareTo(toStr) <= 0;
            }
        };
        filters.add(dateFilter);
    }

    // Apply filters
    if (filters.isEmpty()) {
        sorter.setRowFilter(null);
    } else {
        sorter.setRowFilter(RowFilter.andFilter(filters));
    }

    cat_table.setRowSorter(sorter);
    }

    
       private void autoFilterCatTableKManageByDateRange() {
        Date fromDate = date_f.getDate();
        Date toDate = date_t.getDate();

        if (fromDate == null || toDate == null) return;

        fromDate = stripTime(fromDate);
        toDate = stripTime(toDate);

        String searchQuery = s_cat.getText().trim().toLowerCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        DefaultTableModel filteredModel = new DefaultTableModel();

        // Add column names from the masterManageCategoryModel
        for (int i = 0; i < masterManageCategoryModel.getColumnCount(); i++) {
            filteredModel.addColumn(masterManageCategoryModel.getColumnName(i));
        }

        // Loop through master data (not from UI table)
        for (int i = 0; i < masterManageCategoryModel.getRowCount(); i++) {
            try {
                // Assuming 'catId' is in column 0 for 'cat_table_kmanage'
                String catId = masterManageCategoryModel.getValueAt(i, 0).toString().toLowerCase();
                String catName = masterManageCategoryModel.getValueAt(i, 1).toString().toLowerCase();
                String type = masterManageCategoryModel.getValueAt(i, 2).toString().toLowerCase();
                String description = masterManageCategoryModel.getValueAt(i, 3).toString().toLowerCase();
                String dateStr = masterManageCategoryModel.getValueAt(i, 4).toString(); // date column
                String time = masterManageCategoryModel.getValueAt(i, 5).toString().toLowerCase();

                Date rowDate = stripTime(sdf.parse(dateStr));

                boolean matchesDate = (rowDate.equals(fromDate) || rowDate.after(fromDate)) &&
                                      (rowDate.equals(toDate) || rowDate.before(toDate));

                boolean matchesSearch = searchQuery.isEmpty() ||
                                        catId.contains(searchQuery) ||
                                        catName.contains(searchQuery) ||
                                        type.contains(searchQuery) ||
                                        description.contains(searchQuery) ||
                                        time.contains(searchQuery);

                if (matchesDate && matchesSearch) {
                    Object[] rowData = new Object[masterManageCategoryModel.getColumnCount()];
                    for (int j = 0; j < masterManageCategoryModel.getColumnCount(); j++) {
                        rowData[j] = masterManageCategoryModel.getValueAt(i, j);
                    }
                    filteredModel.addRow(rowData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Apply filtered model to cat_table_kmanage
        cat_table_manage.setModel(filteredModel);
        cat_table_manage.setRowSorter(new TableRowSorter<>(filteredModel));
    }
       
    
        private void loadCategoryList() {

         if (masterManageCategoryModel == null) {
            masterManageCategoryModel = new DefaultTableModel();
        }

        // Avoid duplicate columns
        if (masterManageCategoryModel.getColumnCount() == 0) {
            masterManageCategoryModel.addColumn("Categories ID");
            masterManageCategoryModel.addColumn("Category Name");
            masterManageCategoryModel.addColumn("Description");
            masterManageCategoryModel.addColumn("Category Type");
            masterManageCategoryModel.addColumn("Date");
            masterManageCategoryModel.addColumn("Time");
        }

        // 🔥 Clear all existing rows to avoid duplication
        masterManageCategoryModel.setRowCount(0);

        try {
               Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
            );
            String sql = "SELECT categories_id, cat_name, cat_dscrpt, cat_type, date, time FROM categories";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String categoriesID = rs.getString("categories_id");
                String catName = rs.getString("cat_name");
                String catDscrpt = rs.getString("cat_dscrpt");
                String catType = rs.getString("cat_type");
                String date = rs.getString("date");
                String timeRaw = rs.getString("time");

                // Format the time
                String formattedTime = timeRaw;
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                    Date parsedTime = inputFormat.parse(timeRaw);
                    formattedTime = outputFormat.format(parsedTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                masterManageCategoryModel.addRow(new Object[]{
                    categoriesID, catName, catDscrpt, catType, date, formattedTime
                });
            }

            cat_table_manage.setModel(masterManageCategoryModel);
            cat_table_manage.setRowSorter(new TableRowSorter<>(masterManageCategoryModel));

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load category list: " + e.getMessage());
        }
       }
    
    private void searchAndFillCategoryDetails() {
       String searchID = catid_search.getText().trim();
        if (searchID.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a Category ID to search.");
            return;
        }

        try {
               Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
            );
            String sql = "SELECT * FROM categories WHERE categories_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, searchID);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String name = rs.getString("cat_name");
                String desc = rs.getString("cat_dscrpt");
                String type = rs.getString("cat_type");
                String dateStr = rs.getString("date");
                String timeRaw = rs.getString("time");

                // Populate text fields
                categ_name.setText(name);
                categ_des.setText(desc);

                // ✅ Set selected item in combo box
                type_combo.setSelectedItem(type);

                // Date parsing
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date parsedDate = sdf.parse(dateStr);
                    categ_d.setDate(parsedDate); // For JDateChooser
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Time formatting
                SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                try {
                    Date parsedTime = inputFormat.parse(timeRaw);
                    categ_t.setText(outputFormat.format(parsedTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                    categ_t.setText(timeRaw); // fallback
                }
            } else {
                JOptionPane.showMessageDialog(null, "Category ID not found.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    
        private Date stripTime(Date date) {
        // Remove the time part (set hours, minutes, seconds to 0)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

  
    private void fetchCategoryData() {
        masterCategoryModel = new DefaultTableModel(new String[]{"Category Name", "Type", "Description", "Date", "Time"}, 0);
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin",
                "yeyel2025")) {

            String sql = "SELECT cat_name, cat_type, cat_dscrpt, date, time FROM categories";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                SimpleDateFormat dbTime = new SimpleDateFormat("HH:mm:ss");  // Database time format
                SimpleDateFormat dispTime = new SimpleDateFormat("h:mm a");  // Display time format (12-hour with AM/PM)

                while (rs.next()) {
                    String name = rs.getString("cat_name");
                    String type = rs.getString("cat_type");
                    String description = rs.getString("cat_dscrpt");
                    String date = rs.getString("date");  // Assuming it's already in yyyy-MM-dd format
                    String rawTime = rs.getString("time");

                    // Format the time from the database to display in 12-hour format with AM/PM
                    String formattedTime = dispTime.format(dbTime.parse(rawTime));

                    // Add a row to the table model
                    masterCategoryModel.addRow(new Object[]{name, type, description, date, formattedTime});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading category data: " + ex.getMessage());
        }

        // Set the table model and apply sorting
        cat_table.setModel(masterCategoryModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(masterCategoryModel);
        cat_table.setRowSorter(sorter);  
    }


    
    private void updateCurrentTime() {
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    String currentTime = timeFormat.format(new Date()); // Current system time
    c_time.setText(currentTime);
    }
    
      private void updateCurrentTimeCategs() {
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    String currentTime = timeFormat.format(new Date()); // Current system time
    categ_t.setText(currentTime);
    }
     private void typebox() {
        String[] type = {
            "Supplies", 
            "Materials",
             "Medicines"
        };

        for (String role : type) {
            category_box.addItem(role);
        }
    }
     
      private void categbox() {
        String[] type = {
            "Supplies", 
            "Materials",
             "Medicines"
        };

        for (String role : type) {
            type_combo.addItem(role);
        }
        }

 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        categorymanage = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cat_table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cat_name = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cat_dscrpt = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        category_box = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        c_date = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        c_time = new javax.swing.JTextField();
        c_clear = new javax.swing.JButton();
        c_add = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cat_search = new javax.swing.JTextField();
        from_c = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        to_c = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        cat_table_manage = new javax.swing.JTable();
        catid_search = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        categ_name = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        categ_d = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        categ_t = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        categ_des = new javax.swing.JTextArea();
        type_combo = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        s_cat = new javax.swing.JTextField();
        date_f = new com.toedter.calendar.JDateChooser();
        date_t = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        update_cat = new javax.swing.JButton();
        remove_cate = new javax.swing.JButton();
        clear2 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(48, 122, 55));

        jPanel1.setBackground(new java.awt.Color(48, 122, 55));

        categorymanage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 249, 103));

        cat_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Category Name", "Description", "Type", "Date", "Time"
            }
        ));
        jScrollPane1.setViewportView(cat_table);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Starting Date");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Category Description");

        cat_dscrpt.setColumns(20);
        cat_dscrpt.setRows(5);
        cat_dscrpt.setBorder(null);
        cat_dscrpt.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(cat_dscrpt);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Category Type");

        category_box.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        category_box.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                category_boxMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Date Added");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Time Added");

        c_time.setPreferredSize(new java.awt.Dimension(88, 22));

        c_clear.setBackground(new java.awt.Color(0, 0, 0));
        c_clear.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        c_clear.setForeground(new java.awt.Color(255, 255, 255));
        c_clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/delete (1).png"))); // NOI18N
        c_clear.setText("Clear");
        c_clear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                c_clearMouseClicked(evt);
            }
        });

        c_add.setBackground(new java.awt.Color(0, 0, 0));
        c_add.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        c_add.setForeground(new java.awt.Color(255, 255, 255));
        c_add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/plus (2).png"))); // NOI18N
        c_add.setText("Add Ctgry");
        c_add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                c_addMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Category Name");

        cat_search.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cat_search.setForeground(new java.awt.Color(255, 51, 0));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel7.setText("Category Name");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("End Date");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/school-supplies (1).png"))); // NOI18N
        jLabel21.setText("Add Categories");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(47, 47, 47)
                                        .addComponent(jLabel5))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cat_name, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(21, 26, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(c_add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(24, 24, 24)
                                .addComponent(c_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(c_date, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(c_time, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(category_box, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cat_search, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(129, 129, 129)
                        .addComponent(jLabel1)
                        .addGap(0, 0, 0)
                        .addComponent(from_c, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(to_c, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 833, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addGap(33, 33, 33)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cat_name, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(category_box, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(c_time, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(c_date, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(c_clear)
                    .addComponent(c_add))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cat_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(from_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(to_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        categorymanage.addTab("Add Catergory", jPanel2);

        jPanel5.setBackground(new java.awt.Color(255, 249, 103));

        cat_table_manage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Cat ID", "Cat Name", "Description", "Type", "Date", "Time"
            }
        ));
        jScrollPane3.setViewportView(cat_table_manage);

        catid_search.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        catid_search.setForeground(new java.awt.Color(255, 0, 0));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Category Description");

        categ_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categ_nameActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel10.setText("Search ID");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Category Name");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Category Type");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Date Added");

        categ_t.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categ_tActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Time Added");

        categ_des.setColumns(20);
        categ_des.setRows(5);
        jScrollPane4.setViewportView(categ_des);

        type_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel15.setText("Category Name");

        s_cat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        s_cat.setForeground(new java.awt.Color(255, 51, 0));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Starting Date");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("End Date");

        update_cat.setBackground(new java.awt.Color(0, 0, 0));
        update_cat.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        update_cat.setForeground(new java.awt.Color(255, 255, 255));
        update_cat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/plus (2).png"))); // NOI18N
        update_cat.setText("Update");
        update_cat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                update_catMouseClicked(evt);
            }
        });

        remove_cate.setBackground(new java.awt.Color(0, 0, 0));
        remove_cate.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        remove_cate.setForeground(new java.awt.Color(255, 255, 255));
        remove_cate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/remove (1).png"))); // NOI18N
        remove_cate.setText("Remove");
        remove_cate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                remove_cateMouseClicked(evt);
            }
        });

        clear2.setBackground(new java.awt.Color(0, 0, 0));
        clear2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        clear2.setForeground(new java.awt.Color(255, 255, 255));
        clear2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/delete (1).png"))); // NOI18N
        clear2.setText("Clear");
        clear2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clear2MouseClicked(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/categorization.png"))); // NOI18N
        jLabel18.setText("Category Lists Manage");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(catid_search, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(54, 54, 54))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(categ_name, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                            .addComponent(categ_d, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(categ_t, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                            .addComponent(type_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(141, 141, 141))))
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(update_cat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(remove_cate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clear2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)))))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(s_cat, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16)
                        .addGap(2, 2, 2)
                        .addComponent(date_f, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(date_t, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 796, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(catid_search, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(categ_name, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(type_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13))
                .addGap(2, 2, 2)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(categ_d, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(categ_t, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(update_cat)
                    .addComponent(remove_cate)
                    .addComponent(clear2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(s_cat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel17)
                        .addComponent(date_f, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(date_t, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        categorymanage.addTab("Category Manage", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(categorymanage, javax.swing.GroupLayout.PREFERRED_SIZE, 1139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(categorymanage, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("CATEGORIES");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/left.png"))); // NOI18N
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addGap(448, 448, 448)
                .addComponent(jLabel19)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void category_boxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_category_boxMouseClicked
        String selectedRole = (String) category_box.getSelectedItem();
    }//GEN-LAST:event_category_boxMouseClicked

    private void c_addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_c_addMouseClicked
        String name = cat_name.getText().trim();
        String description = cat_dscrpt.getText().trim();
        String type = category_box.getSelectedItem() != null ? category_box.getSelectedItem().toString() : "";

        // Validate name and description
        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and description cannot be empty.");
            return;
        }

        // Validate category type (category_box)
        if (category_box.getSelectedItem() == null || type.isEmpty() || type.equals("Select Type")) { // Ensure it is not null or default value
            JOptionPane.showMessageDialog(this, "Please select a valid category type.");
            return;
        }

        // Validate date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = c_date.getDate() != null ? dateFormat.format(c_date.getDate()) : null;
        if (date == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.");
            return;
        }

        // Validate time
        String time = c_time.getText().trim();
        if (time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid time.");
            return;
        }

        // Convert the 12-hour AM/PM time to 24-hour format
        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = "";

        try {
            Date parsedTime = inputFormat.parse(time);
            formattedTime = outputFormat.format(parsedTime); // Convert to 24-hour format
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Please enter a valid time (e.g., 02:30 PM).");
            return;
        }

        // Proceed with database insertion if all fields are valid
        try {
            // Establish database connection
            Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                    "admin", "yeyel2025"
            );

            // SQL query to insert category data
            String sql = "INSERT INTO categories (cat_name, cat_dscrpt, cat_type, date, time) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Set the values for the prepared statement
            pst.setString(1, name);
            pst.setString(2, description);
            pst.setString(3, type);
            pst.setString(4, date);
            pst.setString(5, formattedTime); // Insert the properly formatted time

            // Execute the query
            int rowsInserted = pst.executeUpdate();

            // Check if the insertion was successful
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Category added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add category.");
            }

            // Close resources
            pst.close();
            conn.close();

        } catch (SQLException ex) {
            // Handle any SQL exceptions
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }

        // Fetch and load category data again
        fetchCategoryData();
        loadCategoryList();
        clearUser();

    }//GEN-LAST:event_c_addMouseClicked

      private void clearUser() {
        
      cat_name.setText("");
      catid_search.setText("");         // Clear category ID search field
      cat_dscrpt.setText("");           // Clear category description field
      category_box.setSelectedIndex(0); // Reset category combo box to default index
      c_date.setDate(null);             // Clear date picker
      c_time.setText("");               // Clear time field
      cat_search.setText("");           // Clear search text field
      from_c.setDate(null);             // Clear "from" date picker
      to_c.setDate(null);   
      // Clear "to" date picker
        // ✅ Reset the table model to the full user list
      cat_table.setModel(masterCategoryModel); // show original data again
      cat_table.setRowSorter(null);

}
        
    private void c_clearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_c_clearMouseClicked
     clearUser();
    }//GEN-LAST:event_c_clearMouseClicked

    private void categ_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categ_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categ_nameActionPerformed

        private void clearUserFields() {

          categ_name.setText("");
          catid_search.setText("");         // Clear category ID search field
          categ_des.setText("");           // Clear category description field
          type_combo.setSelectedIndex(-1); // Reset category combo box to default index
          categ_d.setDate(null);             // Clear date picker
          categ_t.setText("");               // Clear time field
          s_cat.setText("");           // Clear search text field
          date_f.setDate(null);             // Clear "from" date picker
          date_t.setDate(null);               // Clear "to" date picker

            // ✅ Reset the table model to the full user list
            cat_table_manage.setModel(masterManageCategoryModel); // show original data again
            cat_table_manage.setRowSorter(null);

    }
    
    private void clear2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clear2MouseClicked
     clearUserFields();
    }//GEN-LAST:event_clear2MouseClicked

    private void remove_cateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_remove_cateMouseClicked
         int selectedRow = cat_table_manage.getSelectedRow();
        String categoryId = null;

        // Priority: Text field search (catid_search), fallback to selected row
        String searchId = catid_search.getText().trim();
        if (!searchId.isEmpty()) {
            categoryId = searchId;
        } else if (selectedRow != -1) {
            categoryId = cat_table_manage.getValueAt(selectedRow, 0).toString(); // Assuming ID is column 0
        } else {
            JOptionPane.showMessageDialog(null, "Please select a category or enter an ID to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to delete category ID: " + categoryId + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Delete from database
               Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
            );
            String sql = "DELETE FROM categories WHERE categories_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, categoryId);
            int rowsAffected = pst.executeUpdate();
            conn.close();

            if (rowsAffected > 0) {
                // Remove row from table if selected
                if (selectedRow != -1 && cat_table_manage.getValueAt(selectedRow, 0).toString().equals(categoryId)) {
                    DefaultTableModel model = (DefaultTableModel) cat_table_manage.getModel();
                    model.removeRow(selectedRow);
                }
                JOptionPane.showMessageDialog(null, "Category deleted successfully.");
                catid_search.setText(""); // Clear search field
                clearUserFields();
                loadCategoryList(); // Reload the table after update
                fetchCategoryData();
            } else {
                JOptionPane.showMessageDialog(null, "No category found with ID: " + categoryId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
        
    }//GEN-LAST:event_remove_cateMouseClicked

    private void update_catMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_update_catMouseClicked
     int selectedRow = cat_table_manage.getSelectedRow();
    String categoryId;
    
    // Get category ID from the selected row or manual input field
    if (selectedRow == -1) {
        categoryId = catid_search.getText().trim();
        if (categoryId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a category or enter a Category ID to update.");
            return;
        }
    } else {
        categoryId = cat_table_manage.getValueAt(selectedRow, 0).toString(); // Category ID from selected row
    }

    // Get updated data from input fields
    String name = categ_name.getText().trim();
    String desc = categ_des.getText().trim();
    String type = type_combo.getSelectedItem().toString();
    Date date = categ_d.getDate();
    String inputTime = categ_t.getText().trim();

    if (name.isEmpty() || desc.isEmpty() || date == null || inputTime.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please fill in all the fields.");
        return;
    }

    // Format the date
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = sdfDate.format(date);

    // Convert time from 12-hour to 24-hour format
    SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
    String time;
    try {
        Date parsedTime = inputFormat.parse(inputTime);
        time = outputFormat.format(parsedTime);
    } catch (ParseException ex) {
        JOptionPane.showMessageDialog(null, "Invalid time format. Please use hh:mm AM/PM format.");
        return;
    }

    try {
        // Update category in the database
           Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
            );
        String sql = "UPDATE categories SET cat_name = ?, cat_dscrpt = ?, cat_type = ?, date = ?, time = ? WHERE categories_id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, name);
        pst.setString(2, desc);
        pst.setString(3, type);
        pst.setString(4, formattedDate);
        pst.setString(5, time);
        pst.setString(6, categoryId);
        int rowsAffected = pst.executeUpdate();
        conn.close();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Category updated successfully.");
            clearUserFields();
            loadCategoryList(); // Reload the table after update
            fetchCategoryData();
        } else {
            JOptionPane.showMessageDialog(null, "No category found with ID: " + categoryId);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_update_catMouseClicked

    private void jLabel20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel20MouseClicked
    Window window = SwingUtilities.getWindowAncestor(this);
    if (window != null) {
        window.dispose(); // Close the window
    }

    // Open the homepage window
    homepageSYSTEM homepage = new homepageSYSTEM();
    homepage.setVisible(true);
    }//GEN-LAST:event_jLabel20MouseClicked

    private void categ_tActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categ_tActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categ_tActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton c_add;
    private javax.swing.JButton c_clear;
    private com.toedter.calendar.JDateChooser c_date;
    private javax.swing.JTextField c_time;
    private javax.swing.JTextArea cat_dscrpt;
    private javax.swing.JTextField cat_name;
    private javax.swing.JTextField cat_search;
    private javax.swing.JTable cat_table;
    private javax.swing.JTable cat_table_manage;
    private com.toedter.calendar.JDateChooser categ_d;
    private javax.swing.JTextArea categ_des;
    private javax.swing.JTextField categ_name;
    private javax.swing.JTextField categ_t;
    private javax.swing.JComboBox<String> category_box;
    private javax.swing.JTabbedPane categorymanage;
    private javax.swing.JTextField catid_search;
    private javax.swing.JButton clear2;
    private com.toedter.calendar.JDateChooser date_f;
    private com.toedter.calendar.JDateChooser date_t;
    private com.toedter.calendar.JDateChooser from_c;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton remove_cate;
    private javax.swing.JTextField s_cat;
    private com.toedter.calendar.JDateChooser to_c;
    private javax.swing.JComboBox<String> type_combo;
    private javax.swing.JButton update_cat;
    // End of variables declaration//GEN-END:variables
}
