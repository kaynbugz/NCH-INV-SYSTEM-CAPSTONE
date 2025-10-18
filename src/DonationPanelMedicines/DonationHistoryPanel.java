package DonationPanelMedicines;


    import dashboardSYSTEM.*;
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
    import java.util.Date;
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

public class DonationHistoryPanel extends javax.swing.JPanel {

    private String loggedInUser;
    private DefaultTableModel historyModel;

    public DonationHistoryPanel() {
        initComponents();
        setupTable();
        loadDonationHistory();
    }

    private void setupTable() {
        // Table model setup
        String[] columns = {
            "Donation ID", "Donor Name", "Donation Status",
            "Date Donated", "Time Donated", "Total Medicines"
        };

        historyModel = new DefaultTableModel(columns, 0);
        medicinedsdhistory.setModel(historyModel);

        // Table header styling
        JTableHeader header = medicinedsdhistory.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.BLACK);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });

        // Row selection styling
        medicinedsdhistory.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = medicinedsdhistory.getSelectedRow();
                if (selectedRow != -1) {
                    medicinedsdhistory.setSelectionBackground(Color.decode("#4CAF50"));
                    medicinedsdhistory.setSelectionForeground(Color.WHITE);
                }
            }
        });
    }

    public void loadDonationHistory() {
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
        String dbUser = "admin";
        String dbPass = "yeyel2025";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT DonationID, DonorName, DonationStatus, DateDonated,
                        TimeDonated, TotalItems
                 FROM donation_records_medicines
                 ORDER BY DateEncoded DESC
             """)) {

            historyModel.setRowCount(0); // ✅ clear existing rows

            while (rs.next()) {
                Object[] row = {
                    rs.getString("DonationID"),
                    rs.getString("DonorName"),
                    rs.getString("DonationStatus"),
                    rs.getDate("DateDonated"),
                    rs.getString("TimeDonated"),
                    rs.getInt("TotalItems")
                };
                historyModel.addRow(row); // ✅ add to model, not JTable
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Error loading donation history: " + e.getMessage());
            e.printStackTrace();
        }
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
        medicinedsdhistory = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        m_seardid = new javax.swing.JTextField();
        refreshmeds = new javax.swing.JButton();
        sd1 = new javax.swing.JLabel();
        mfg_date = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        t_medicines = new javax.swing.JLabel();
        t_stocks = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        genid = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        viewgenid = new javax.swing.JButton();
        exp_date = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(48, 122, 55));
        jPanel1.setForeground(new java.awt.Color(255, 51, 0));
        jPanel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(48, 122, 55));

        jPanel5.setBackground(new java.awt.Color(255, 249, 103));

        medicinedsdhistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Donation ID", "Donor Name", "Donation Status", "Date Donated", "Time Donated", "Total Medicines"
            }
        ));
        jScrollPane1.setViewportView(medicinedsdhistory);

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

        genid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genidActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addComponent(genid, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(2, 2, 2)
                                .addComponent(m_seardid, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(refreshmeds)
                                .addGap(479, 479, 479)
                                .addComponent(jLabel9)
                                .addGap(2, 2, 2)
                                .addComponent(mfg_date, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(sd1)
                                .addGap(2, 2, 2)
                                .addComponent(exp_date, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1102, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(m_seardid, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(refreshmeds)
                        .addComponent(jLabel9))
                    .addComponent(sd1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfg_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exp_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jTabbedPane1.addTab("Medicines Donation History", jPanel4);

        jPanel3.setBackground(new java.awt.Color(255, 249, 103));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1149, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 503, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Supplies And Materials History", jPanel3);

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
                .addComponent(jTabbedPane1))
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

    private void refreshmedsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshmedsMouseClicked
    
    }//GEN-LAST:event_refreshmedsMouseClicked

    private void genidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_genidActionPerformed
  



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser exp_date;
    private javax.swing.JTextField genid;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField m_seardid;
    private javax.swing.JTable medicinedsdhistory;
    private com.toedter.calendar.JDateChooser mfg_date;
    private javax.swing.JButton refreshmeds;
    private javax.swing.JLabel sd1;
    private javax.swing.JLabel t_medicines;
    private javax.swing.JLabel t_stocks;
    private javax.swing.JButton viewgenid;
    // End of variables declaration//GEN-END:variables
}