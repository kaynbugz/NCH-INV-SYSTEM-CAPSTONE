package dashboardSYSTEM;

    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Font;
    import java.awt.Window;
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
    import java.util.List;
    import javax.swing.JLabel;
    import javax.swing.JTable;
    import javax.swing.SwingUtilities;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;


public class UserManagement extends javax.swing.JPanel {
    
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel masterUserModel;
    private DefaultTableModel masterManageCategoryModel; // for cat_table_manage
    
        // Declare table models at class level
    DefaultTableModel registrationModel = new DefaultTableModel();
    DefaultTableModel listUsersModel = new DefaultTableModel();

    public UserManagement() {
        initComponents();
        populateComboBox();
        fetchRegistrationData();
        populateComboBoxUsers();
        loadUserList();
        
        
               // After your table is initialized:
        JTableHeader header = t_registration.getTableHeader();

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
        JTableHeader header1 = t_listusers.getTableHeader();

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
        
           userpane.addChangeListener(e -> {
            for (int i = 0; i < userpane.getTabCount(); i++) {
                userpane.setBackgroundAt(i, Color.decode("#FFF967")); // reset all tabs to light yellow
            }
            int selectedIndex = userpane.getSelectedIndex();
            userpane.setBackgroundAt(selectedIndex, Color.decode("#4CAF50")); // green color
        });
         


        // Add ListSelectionListener to handle row selection color change
        t_registration.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = t_registration.getSelectedRow();
                if (selectedRow != -1) {
                    // Highlight selected row
                    t_registration.setSelectionBackground(Color.decode("#4CAF50")); // Green color
                    t_registration.setSelectionForeground(Color.WHITE); // White text
                }
            }
        });
       
        
        role_u.setBackground(Color.WHITE);
        positions.setBackground(Color.WHITE);
        
          t_registration.getTableHeader().setForeground(Color.RED);
           t_listusers.getTableHeader().setForeground(Color.RED);

            // Auto-filter as user types in the search field
        searchuser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String query = searchuser.getText();
                autoFilterByDateRange();
            }
        });

        // Auto-filter as user types in the search field
        search_u.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // 1. Filter by date range first
                autoFilterByDateRangeUsers();  // You should call the method for date range filtering

                // 2. Then apply search on filtered table
                String query = search_u.getText(); // Get the search query
                filterTableBySearchUsers(query);        // Apply search filter
            }
        });

        from_u.addPropertyChangeListener("date", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            autoFilterByDateRangeUsers();
        }
        });

        to_u.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                autoFilterByDateRangeUsers();
            }
        });

            // When FROM date changes, re-filter the table
        from.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    autoFilterByDateRange();
                }
            }
        });

        // When TO date changes, re-filter the table
        to.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    autoFilterByDateRange();
                }
            }
        });


           // Show current time ONLY when date is picked
         date.addPropertyChangeListener("date", new PropertyChangeListener() {
             @Override
             public void propertyChange(PropertyChangeEvent evt) {
                 if (date.getDate() != null) {
                     SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                     String currentTime = timeFormat.format(new java.util.Date());
                     time.setText(currentTime);
                 }
             }
         });
         
           date_u.addPropertyChangeListener("date", new PropertyChangeListener() {
             @Override
             public void propertyChange(PropertyChangeEvent evt) {
                 if (date_u.getDate() != null) {
                     SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                     String currentTime = timeFormat.format(new java.util.Date());
                     time_u.setText(currentTime);
                 }
             }
         });
         

         search_userid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    String userId = search_userid.getText().trim();
                    if (!userId.isEmpty()) {
                        fetchUserDetailsById(userId);
                    }
                }
            }
        });
     }
    
    private void fetchUserDetailsById(String userId) {
    try {
        Connection conn = DriverManager.getConnection(
        "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
        "admin",
        "yeyel2025"
        );
        String sql = "SELECT fullname, username, email, address, password, cpno, role, date, time FROM user_registration WHERE user_reg_id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, userId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            fullname_u.setText(rs.getString("fullname"));
            username_u.setText(rs.getString("username"));
            email_u.setText(rs.getString("email"));
            address_u.setText(rs.getString("address"));
            password_u.setText(rs.getString("password"));
            cpno_u.setText(rs.getString("cpno"));
            role_u.setSelectedItem(rs.getString("role")); // ComboBox
            date_u.setDate(rs.getDate("date"));           // JDateChooser
            time_u.setText(formatTime(rs.getString("time")));
        } else {
            JOptionPane.showMessageDialog(null, "No user found with ID: " + userId);
            clearUserFields();
        }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching user details: " + e.getMessage());
        }
    }
    
    private String formatTime(String time24) {
    try {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        Date date = inputFormat.parse(time24);
        return outputFormat.format(date);
    } catch (Exception e) {
        return time24;
    }
}

    private void clearUserFields() {
    fullname_u.setText("");
    username_u.setText("");
    email_u.setText("");
    address_u.setText("");
    password_u.setText("");
    cpno_u.setText("");
    role_u.setSelectedIndex(-1);
    date_u.setDate(null);
    time_u.setText("");
    search_userid.setText("");
    search_u.setText(""); // ✅ clear the search field too
    from_u.setDate(null);
    to_u.setDate(null);

    // ✅ Reset the table model to the full user list
    t_listusers.setModel(masterUserModel); // show original data again
    t_listusers.setRowSorter(null);

}


     private void setCurrentTime() {
     SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
     time.setText(timeFormat.format(new Date()));
 }
     
    
    private void filterTableBySearchUsers(String query) {
    DefaultTableModel model = (DefaultTableModel) t_listusers.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    t_listusers.setRowSorter(sorter);

    if (query.trim().length() == 0) {
        sorter.setRowFilter(null); // Show all if empty
    } else {
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query)); // Case-insensitive match
    }
    }
    
    private void populateComboBox() {
        String[] roles = {
            "Medical Center Chief II", 
            "Chief Administrative Officer", 
            "Supervising Administrative Officer", 
            "Administrative Officer V", 
            "Administrative Officer I", 
            "Warehouseman III", 
            "Administrative Assistant III", 
            "Administrative Assistant II", 
            "Administrative Aide VI", 
            "Administrative Aide IV", 
            "Administrative Aide II"
        };

        for (String role : roles) {
            positions.addItem(role);
        }
    }
    
     private void populateComboBoxUsers() {
        String[] roles = {
            "Medical Center Chief II", 
            "Chief Administrative Officer", 
            "Supervising Administrative Officer", 
            "Administrative Officer V", 
            "Administrative Officer I", 
            "Warehouseman III", 
            "Administrative Assistant III", 
            "Administrative Assistant II", 
            "Administrative Aide VI", 
            "Administrative Aide IV", 
            "Administrative Aide II"
        };

        for (String role : roles) {
            role_u.addItem(role);
        }
    }
    
    

        private void clearForm() {
            fullname.setText("");
            username.setText("");
            email.setText("");
            address.setText("");
            password.setText("");
            cpno.setText("");
            positions.setSelectedIndex(0);
            date.setDate(null);
            time.setText("");
            searchuser.setText("");
            from.setDate(null);
            to.setDate(null);

            // Reset to original registration model
            t_registration.setModel(registrationModel);
            sorter = new TableRowSorter<>(registrationModel);
            t_registration.setRowSorter(sorter);

            // ✅ Re-apply the date filter (if any)
            autoFilterByDateRange();
    }
    
        
    private Date stripTime(Date date) {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(sdf.format(date));
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        userpane = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        fullname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        t_registration = new javax.swing.JTable();
        register = new javax.swing.JButton();
        clear = new javax.swing.JButton();
        email = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        address = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        positions = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cpno = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        date = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        time = new javax.swing.JTextField();
        from = new com.toedter.calendar.JDateChooser();
        to = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        searchuser = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        t_listusers = new javax.swing.JTable();
        search_userid = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        search_u = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        username_u = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        address_u = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        password_u = new javax.swing.JPasswordField();
        jLabel23 = new javax.swing.JLabel();
        cpno_u = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        date_u = new com.toedter.calendar.JDateChooser();
        time_u = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        clear1 = new javax.swing.JButton();
        to_u = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        from_u = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        remove_user = new javax.swing.JButton();
        update_user = new javax.swing.JButton();
        role_u = new javax.swing.JComboBox<>();
        email_u = new javax.swing.JTextField();
        fullname_u = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(48, 122, 55));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 249, 103));
        jLabel1.setText("USER MANAGEMENT");

        userpane.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 249, 103));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Username");

        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });

        t_registration.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Username", "Role", "Date", "Time"
            }
        ));
        t_registration.setDropMode(javax.swing.DropMode.INSERT_ROWS);
        jScrollPane1.setViewportView(t_registration);

        register.setBackground(new java.awt.Color(0, 0, 0));
        register.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        register.setForeground(new java.awt.Color(255, 255, 255));
        register.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/plus (2).png"))); // NOI18N
        register.setText("Register");
        register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerActionPerformed(evt);
            }
        });

        clear.setBackground(new java.awt.Color(0, 0, 0));
        clear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clear.setForeground(new java.awt.Color(255, 255, 255));
        clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/delete (1).png"))); // NOI18N
        clear.setText("Clear");
        clear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Email");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Date Added");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Address");

        positions.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "   " }));
        positions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionsActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("CP No.");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Password");

        date.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dateMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Role");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Time Added");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("End Date");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Starting Date");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Full Name");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/USER.png"))); // NOI18N
        jLabel15.setText("USER REGISTRATION");

        searchuser.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        searchuser.setForeground(new java.awt.Color(255, 51, 0));
        searchuser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchuserActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel14.setText("Search");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(register, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(clear, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel13))
                                .addGap(1, 1, 1)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(positions, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(password)
                                    .addComponent(username)
                                    .addComponent(email)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGap(11, 11, 11)
                                                        .addComponent(jLabel5))
                                                    .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jLabel10))
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGap(11, 11, 11)
                                                        .addComponent(time))))
                                            .addComponent(fullname, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                            .addComponent(address)
                                            .addComponent(cpno))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(27, 27, 27)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(from, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addGap(2, 2, 2)
                        .addComponent(to, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 805, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchuser, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fullname, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(username, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cpno, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(positions, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(time)
                    .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(register)
                    .addComponent(clear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(to, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(from, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        userpane.addTab("Register User", jPanel3);

        jPanel2.setBackground(new java.awt.Color(255, 249, 103));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        t_listusers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "Full Name", "User Name", "Email", "Address", "Password", "Phone Number", "Role", "Date Registered", "Time Registered"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(t_listusers);

        search_userid.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        search_userid.setForeground(new java.awt.Color(255, 51, 0));
        search_userid.setPreferredSize(new java.awt.Dimension(28, 28));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Username");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel18.setText("Search ID");

        search_u.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        search_u.setForeground(new java.awt.Color(255, 51, 0));
        search_u.setPreferredSize(new java.awt.Dimension(100, 22));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Full Name");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("Email");

        address_u.setPreferredSize(new java.awt.Dimension(28, 28));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Address");

        password_u.setPreferredSize(new java.awt.Dimension(28, 28));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setText("Password");

        cpno_u.setPreferredSize(new java.awt.Dimension(28, 28));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("CP No.");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setText("Role");

        date_u.setPreferredSize(new java.awt.Dimension(28, 28));
        date_u.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                date_uMouseClicked(evt);
            }
        });

        time_u.setPreferredSize(new java.awt.Dimension(28, 28));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("Time Added");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setText("Date Added");

        clear1.setBackground(new java.awt.Color(0, 0, 0));
        clear1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clear1.setForeground(new java.awt.Color(255, 255, 255));
        clear1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/delete (1).png"))); // NOI18N
        clear1.setText("Clear");
        clear1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clear1MouseClicked(evt);
            }
        });
        clear1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear1ActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("End Date");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setText("Starting Date");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/parcel.png"))); // NOI18N
        jLabel28.setText("USER LISTS MANAGE");

        remove_user.setBackground(new java.awt.Color(0, 0, 0));
        remove_user.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        remove_user.setForeground(new java.awt.Color(255, 255, 255));
        remove_user.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/remove (1).png"))); // NOI18N
        remove_user.setText("Remove");
        remove_user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                remove_userMouseClicked(evt);
            }
        });
        remove_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remove_userActionPerformed(evt);
            }
        });

        update_user.setBackground(new java.awt.Color(0, 0, 0));
        update_user.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        update_user.setForeground(new java.awt.Color(255, 255, 255));
        update_user.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/plus (2).png"))); // NOI18N
        update_user.setText("Update");
        update_user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                update_userMouseClicked(evt);
            }
        });
        update_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_userActionPerformed(evt);
            }
        });

        role_u.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "   " }));
        role_u.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                role_uActionPerformed(evt);
            }
        });

        email_u.setPreferredSize(new java.awt.Dimension(28, 28));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/magnifier.png"))); // NOI18N
        jLabel29.setText("Search");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(update_user, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(remove_user, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clear1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel17)
                                .addComponent(jLabel19)
                                .addComponent(jLabel20)
                                .addComponent(jLabel21)
                                .addComponent(jLabel23)
                                .addComponent(jLabel24)
                                .addComponent(jLabel25))
                            .addGap(2, 2, 2)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(fullname_u)
                                .addComponent(username_u)
                                .addComponent(email_u, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(address_u, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(password_u, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cpno_u, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(role_u, 0, 237, Short.MAX_VALUE)))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(search_userid, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel28)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(date_u, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel26)
                                    .addGap(32, 32, 32)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel22)
                                .addComponent(time_u, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(11, 11, 11))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(search_u, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(147, 147, 147)
                        .addComponent(jLabel27)
                        .addGap(2, 2, 2)
                        .addComponent(from_u, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addGap(2, 2, 2)
                        .addComponent(to_u, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 748, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel28)
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(search_userid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(fullname_u, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(username_u, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(email_u, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(address_u, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(password_u, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cpno_u, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(role_u, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(date_u, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(time_u, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(remove_user)
                            .addComponent(clear1)
                            .addComponent(update_user)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel29)
                                    .addComponent(search_u, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(7, 7, 7))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(to_u, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16)
                                            .addComponent(from_u, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        userpane.addTab("User Manage", jPanel2);

        jPanel5.setBackground(new java.awt.Color(255, 249, 103));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1142, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 499, Short.MAX_VALUE)
        );

        userpane.addTab("User History", jPanel5);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/left.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addGap(417, 417, 417)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(userpane, javax.swing.GroupLayout.PREFERRED_SIZE, 1142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(13, 13, 13)
                .addComponent(userpane, javax.swing.GroupLayout.PREFERRED_SIZE, 526, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        userpane.getAccessibleContext().setAccessibleName("USER");
    }// </editor-fold>//GEN-END:initComponents

    
        
    
   private void fetchRegistrationData() {
    // 1) Initialize the registrationModel with the column names
    registrationModel = new DefaultTableModel(new String[]{"Username", "Role", "Date", "Time"}, 0);

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025")) {

        // SQL query to fetch the registration data
        String sql = "SELECT username, role, date, time FROM user_registration";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            // Formatters for time conversion
            SimpleDateFormat dbTimeFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat displayTimeFormat = new SimpleDateFormat("h:mm a");

            while (rs.next()) {
                String u = rs.getString("username");
                String r = rs.getString("role");
                String d = (rs.getDate("date") != null) ? rs.getDate("date").toString() : "";
                String t = (rs.getString("time") != null) ? rs.getString("time") : "";

                String formattedTime = "";
                if (!t.isEmpty()) {
                    try {
                        formattedTime = displayTimeFormat.format(dbTimeFormat.parse(t));
                    } catch (ParseException e) {
                        // If parsing fails, fallback to the original value
                        formattedTime = t;
                    }
                }

                registrationModel.addRow(new Object[]{u, r, d, formattedTime});
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }

    // Set the model and create sorter once
    t_registration.setModel(registrationModel);
    sorter = new TableRowSorter<>(registrationModel);
    t_registration.setRowSorter(sorter);

    // ✅ Important: Apply filter after loading data
    autoFilterByDateRange();
}

    
    private String convertTo12HourFormat(String time) {
        try {
            // Print the input time for debugging
            System.out.println("Time before conversion: " + time);

            // Handle case when the time is null or empty
            if (time == null || time.isEmpty()) {
                return "Invalid time"; // Return an appropriate message
            }

            // Attempt to parse the 24-hour time format (HH:mm:ss)
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss"); // 24-hour format
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a"); // 12-hour format with AM/PM
            java.util.Date date = inputFormat.parse(time); // Parse the 24-hour time

            // Return the formatted 12-hour time
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Print the exception for debugging
            e.printStackTrace();
            return "Invalid time format"; // Return an error message if parsing fails
        }
        
        
    }


    private void autoFilterByDateRange() {
    Date fromDate = from.getDate();
    Date toDate = to.getDate();
    String query = searchuser.getText(); // Get text from the search field

    DefaultTableModel model = (DefaultTableModel) t_registration.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

    // Add search filter if not empty
    if (query != null && !query.trim().isEmpty()) {
        filters.add(RowFilter.regexFilter("(?i)" + query)); // Case-insensitive
    }

    // Add date filter if dates are selected
    if (fromDate != null && toDate != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromStr = sdf.format(fromDate);
        String toStr = sdf.format(toDate);

        RowFilter<DefaultTableModel, Object> dateFilter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String dateValue = entry.getStringValue(2); // Column index 2 = "Date"
                return dateValue.compareTo(fromStr) >= 0 && dateValue.compareTo(toStr) <= 0;
            }
        };

        filters.add(dateFilter);
    }

    // Combine filters if any
    if (filters.isEmpty()) {
        sorter.setRowFilter(null); // Show all
    } else {
        sorter.setRowFilter(RowFilter.andFilter(filters)); // Combine all filters
    }

    t_registration.setRowSorter(sorter);
       };

   private void autoFilterByDateRangeUsers() {
    Date fromDate = from_u.getDate();
    Date toDate = to_u.getDate();

    if (fromDate == null || toDate == null) return;

    fromDate = stripTime(fromDate);
    toDate = stripTime(toDate);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableModel filteredModel = new DefaultTableModel();

    // Copy columns from master
    for (int i = 0; i < masterUserModel.getColumnCount(); i++) {
        filteredModel.addColumn(masterUserModel.getColumnName(i));
    }

    for (int i = 0; i < masterUserModel.getRowCount(); i++) {
        try {
            String dateStr = masterUserModel.getValueAt(i, 8).toString();
            Date rowDate = stripTime(sdf.parse(dateStr));

            if ((rowDate.equals(fromDate) || rowDate.after(fromDate)) &&
                (rowDate.equals(toDate) || rowDate.before(toDate))) {

                Object[] rowData = new Object[masterUserModel.getColumnCount()];
                for (int j = 0; j < masterUserModel.getColumnCount(); j++) {
                    rowData[j] = masterUserModel.getValueAt(i, j);
                }

                filteredModel.addRow(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Apply search filter and update table
    applySearchFilterUsers(filteredModel);
    t_listusers.setModel(filteredModel); // Refresh table
    }
   
   
    private void applySearchFilterUsers(DefaultTableModel filteredModel) {
        String query = search_u.getText(); // Get search query
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(filteredModel);
        t_listusers.setRowSorter(sorter);

        if (query.trim().length() == 0) {
            sorter.setRowFilter(null); // Show all if empty
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query)); // Case-insensitive match
        }
    }
    
    

    private void applySearchFilter(DefaultTableModel filteredModel) {
        String query = searchuser.getText(); // Get search query
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(filteredModel);
        t_registration.setRowSorter(sorter);

        if (query.trim().length() == 0) {
            sorter.setRowFilter(null); // Show all if empty
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query)); // Case-insensitive match
        }
    }
    
     private void loadUserList() {
        masterUserModel = new DefaultTableModel(); // Reset master model

        masterUserModel.addColumn("User ID");
        masterUserModel.addColumn("Full Name");
        masterUserModel.addColumn("Username");
        masterUserModel.addColumn("Email");
        masterUserModel.addColumn("Address");
        masterUserModel.addColumn("Password");
        masterUserModel.addColumn("Phone Number");
        masterUserModel.addColumn("Role");
        masterUserModel.addColumn("Date Registered");
        masterUserModel.addColumn("Time Registered");

        try {
            Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin",
            "yeyel2025"
            );
            String sql = "SELECT user_reg_id, fullname, username, email, address, password, cpno, role, date, time FROM user_registration";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String userID = rs.getString("user_reg_id");
                String fullName = rs.getString("fullname");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String password = rs.getString("password");
                String phone = rs.getString("cpno");
                String role = rs.getString("role");
                String date = rs.getString("date");

                String timeRaw = rs.getString("time");
                String formattedTime = timeRaw;
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                    Date parsedTime = inputFormat.parse(timeRaw);
                    formattedTime = outputFormat.format(parsedTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                masterUserModel.addRow(new Object[]{userID, fullName, username, email, address, password, phone, role, date, formattedTime});
            }

            t_listusers.setModel(masterUserModel); // Set the table initially
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load user list: " + e.getMessage());
        }
    }


    
    // Method to convert 12-hour format (e.g., "08:18 PM") to 24-hour format (e.g., "20:18:00")
    private String convertTo24HourFormat(String time12HourFormat) {
        try {
            SimpleDateFormat sdf12Hour = new SimpleDateFormat("hh:mm a");  // 12-hour format
            SimpleDateFormat sdf24Hour = new SimpleDateFormat("HH:mm:ss");  // 24-hour format
            Date date = sdf12Hour.parse(time12HourFormat);  // Parse the 12-hour time
            return sdf24Hour.format(date);  // Convert to 24-hour format and return
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    
    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked

    Window window = SwingUtilities.getWindowAncestor(this);
    if (window != null) {
        window.dispose(); // Close the window
    }

    // Open the homepage window
    homepageSYSTEM homepage = new homepageSYSTEM();
    homepage.setVisible(true);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void role_uActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_role_uActionPerformed
        String selectedRole = (String) role_u.getSelectedItem();
        System.out.println("Selected Role: " + selectedRole);
    }//GEN-LAST:event_role_uActionPerformed

    private void update_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_userActionPerformed

    }//GEN-LAST:event_update_userActionPerformed

    private void update_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_update_userMouseClicked

        // Get the user ID from the search field
        String userId = search_userid.getText();

        // Get the data from the form fields
        String fullName = fullname_u.getText();
        String username = username_u.getText();
        String email = email_u.getText();
        String address = address_u.getText();
        String password = password_u.getText();
        String cpno = cpno_u.getText();
        String role = role_u.getSelectedItem().toString();
        Date date = date_u.getDate();
        String time12HourFormat = time_u.getText();  // Time in 12-hour format (e.g., "08:18 PM")

        // Convert 12-hour format to 24-hour format
        String time24HourFormat = convertTo24HourFormat(time12HourFormat);

        // Check if all fields are filled
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || address.isEmpty() ||
            password.isEmpty() || cpno.isEmpty() || role.isEmpty() || date == null || time24HourFormat.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all the fields.");
            return;
        }

        try {

            Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin",
                "yeyel2025"
            );

            // SQL update query
            String sql = "UPDATE user_registration SET fullname = ?, username = ?, email = ?, address = ?, password = ?, cpno = ?, role = ?, date = ?, time = ? WHERE user_reg_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Set the values to be updated
            pst.setString(1, fullName);
            pst.setString(2, username);
            pst.setString(3, email);
            pst.setString(4, address);
            pst.setString(5, password);
            pst.setString(6, cpno);
            pst.setString(7, role);
            pst.setDate(8, new java.sql.Date(date.getTime()));  // Convert java.util.Date to java.sql.Date
            pst.setString(9, time24HourFormat);  // Set the 24-hour time format
            pst.setString(10, userId);  // Use the user ID from the search field

            // Execute the update query
            int updated = pst.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "User data updated successfully.");
                loadUserList();  // Reload the user list after the update
            } else {
                JOptionPane.showMessageDialog(null, "Error updating user.");
            }

            // Close the database connection
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
        clearUserFields();
        fetchRegistrationData();
    }//GEN-LAST:event_update_userMouseClicked

    private void remove_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remove_userActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_remove_userActionPerformed

    private void remove_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_remove_userMouseClicked
        String userId = search_userid.getText(); // Get the user ID from the search field or selected row

        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please search or select a user to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return; // Cancel deletion
        }

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin",
                "yeyel2025"
            );
            String sql = "DELETE FROM user_registration WHERE user_reg_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, userId);
            int deleted = pst.executeUpdate();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(null, "User deleted successfully.");
                loadUserList();  // Reload the user list after the update

            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete user.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
        clearUserFields(); // Clear the form
        fetchRegistrationData();
    }//GEN-LAST:event_remove_userMouseClicked

    private void clear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear1ActionPerformed
        clearUserFields();
        loadUserList();
    }//GEN-LAST:event_clear1ActionPerformed

    private void clear1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clear1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_clear1MouseClicked

    private void date_uMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_date_uMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_date_uMouseClicked

    private void searchuserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchuserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchuserActionPerformed

    private void dateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dateMouseClicked
        // Get current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(new Date());

        // Set it to the time JTextField
        time.setText(currentTime);
    }//GEN-LAST:event_dateMouseClicked

    private void positionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionsActionPerformed
        String selectedRole = (String) positions.getSelectedItem();
        System.out.println("Selected Role: " + selectedRole);
    }//GEN-LAST:event_positionsActionPerformed

    private void clearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearMouseClicked
        clearForm();
    }//GEN-LAST:event_clearMouseClicked

    private void registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerActionPerformed
        String fullName = fullname.getText().trim();
        String userName = username.getText().trim();
        String emailAddress = email.getText().trim();
        String addr = address.getText().trim();
        String contactNumber = cpno.getText().trim();
        String userRole = (String) positions.getSelectedItem();
        String userPassword = new String(password.getPassword()).trim();
        String userTimeInput = time.getText().trim();

        String formattedTime = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a");
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
            java.util.Date parsedTime = inputFormat.parse(userTimeInput);
            formattedTime = outputFormat.format(parsedTime);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Please use format like '2:46 AM'.");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String userDate = (date.getDate() != null) ? dateFormat.format(date.getDate()) : "";

        // Check if fields are empty
        if (fullName.isEmpty() || userName.isEmpty() || emailAddress.isEmpty() || addr.isEmpty() ||
            contactNumber.isEmpty() || userPassword.isEmpty() || userDate.isEmpty() ||
            userRole == null || formattedTime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        // Email format check
        if (!emailAddress.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }

        // Phone number check (must be exactly 11 digits)
        if (!contactNumber.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 11 digits.");
            return;
        }

        // Password length check
        if (userPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025"
            );

            String sql = "INSERT INTO user_registration (fullname, username, email, address, password, cpno, role, date, time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, fullName);
            pst.setString(2, userName);
            pst.setString(3, emailAddress);
            pst.setString(4, addr);
            pst.setString(5, userPassword);
            pst.setString(6, contactNumber);
            pst.setString(7, userRole);
            pst.setString(8, userDate);
            pst.setString(9, formattedTime);

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "User registered successfully!");
                clearForm(); // Clear fields if needed
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register user.");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        fetchRegistrationData();
        loadUserList();
    }//GEN-LAST:event_registerActionPerformed

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed

    }//GEN-LAST:event_usernameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField address;
    private javax.swing.JTextField address_u;
    private javax.swing.JButton clear;
    private javax.swing.JButton clear1;
    private javax.swing.JTextField cpno;
    private javax.swing.JTextField cpno_u;
    private com.toedter.calendar.JDateChooser date;
    private com.toedter.calendar.JDateChooser date_u;
    private javax.swing.JTextField email;
    private javax.swing.JTextField email_u;
    private com.toedter.calendar.JDateChooser from;
    private com.toedter.calendar.JDateChooser from_u;
    private javax.swing.JTextField fullname;
    private javax.swing.JTextField fullname_u;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPasswordField password;
    private javax.swing.JPasswordField password_u;
    private javax.swing.JComboBox<String> positions;
    private javax.swing.JButton register;
    private javax.swing.JButton remove_user;
    private javax.swing.JComboBox<String> role_u;
    private javax.swing.JTextField search_u;
    private javax.swing.JTextField search_userid;
    private javax.swing.JTextField searchuser;
    private javax.swing.JTable t_listusers;
    private javax.swing.JTable t_registration;
    private javax.swing.JTextField time;
    private javax.swing.JTextField time_u;
    private com.toedter.calendar.JDateChooser to;
    private com.toedter.calendar.JDateChooser to_u;
    private javax.swing.JButton update_user;
    private javax.swing.JTextField username;
    private javax.swing.JTextField username_u;
    private javax.swing.JTabbedPane userpane;
    // End of variables declaration//GEN-END:variables
}
