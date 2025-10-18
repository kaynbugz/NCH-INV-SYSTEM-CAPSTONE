
package DonationPanelMedicines;

import com.toedter.calendar.JDateChooser;

import dashboardSYSTEM.ItemsManagement;
import dashboardSYSTEM.SessionManager;
import orderlistspanelsSYSTEM.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

public class DonationPanelMedicines extends javax.swing.JPanel {

    private ItemsManagement itemsPanel;

    public DonationPanelMedicines(ItemsManagement itemsPanel) {
    this.itemsPanel = itemsPanel;
    initComponents();
    colorwhitebg();
    loadCategoryTypes();
    setupCategoryTypeListener();


    // Auto-fill logged-in user
    String loggedInUser = SessionManager.currentUserFullName;

    // Auto-fill date & time
    setupDateTimeFields();
    startAutoTimeUpdate();

    // Table setup
    tableitems.setRowHeight(40);
    tableitems.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    // Center-align selected columns
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < 6; i++) {
        tableitems.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    // Description column: wrap + fixed width
    TableColumn descColumn = tableitems.getColumnModel().getColumn(3);
    descColumn.setPreferredWidth(110);
    descColumn.setCellRenderer(new TextAreaRenderer());

    // Units dropdown with smart filtering
    String[] unitsList = {"pcs", "boxes", "kg", "mg", "liters"};
    JComboBox<String> unitComboBox = new JComboBox<>(unitsList);
    unitComboBox.setEditable(true);

    JTextField editor = (JTextField) unitComboBox.getEditor().getEditorComponent();
    editor.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            String input = editor.getText().trim().toLowerCase();
            int caretPos = editor.getCaretPosition();

            java.util.List<String> startsWith = new java.util.ArrayList<>();
            java.util.List<String> contains = new java.util.ArrayList<>();

            for (String unit : unitsList) {
                String lowerUnit = unit.toLowerCase();
                if (lowerUnit.startsWith(input)) startsWith.add(unit);
                else if (lowerUnit.contains(input)) contains.add(unit);
            }

            unitComboBox.hidePopup();
            unitComboBox.removeAllItems();
            for (String match : startsWith) unitComboBox.addItem(match);
            for (String match : contains) unitComboBox.addItem(match);
            unitComboBox.setSelectedItem(input);

            SwingUtilities.invokeLater(() -> {
                editor.setText(input);
                editor.setCaretPosition(Math.min(caretPos, editor.getText().length()));
                if (unitComboBox.getItemCount() > 0) unitComboBox.showPopup();
            });
        }
    });

    // Medicine name renderer
    tableitems.getColumnModel().getColumn(0).setCellRenderer(new MedicineRenderer());

    // Units column editor
    TableColumn unitColumn = tableitems.getColumnModel().getColumn(4);
    unitColumn.setCellEditor(new DefaultCellEditor(unitComboBox));

    // Quantity column editor
    TableColumn qtyColumn = tableitems.getColumnModel().getColumn(1);
    qtyColumn.setCellEditor(new QuantityCellEditor());

    // Default status
    sts.setText("In Progress");

    // Styling
    catype.setBackground(Color.WHITE);
    categories.setBackground(Color.WHITE);
    donorname.setBackground(Color.WHITE);
    donorname.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    donorname.setText("");

    // Header style
    JTableHeader header = tableitems.getTableHeader();
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    });
    
    TableColumn mfgDateCol = tableitems.getColumnModel().getColumn(2); // Mfg Date
    TableColumn expDateCol = tableitems.getColumnModel().getColumn(3); // Exp Date

    // Set custom cell editor (date picker)
    mfgDateCol.setCellEditor(new DateChooserCellEditor());
    expDateCol.setCellEditor(new DateChooserCellEditor());

    // Set custom cell renderer (formatted display)
    mfgDateCol.setCellRenderer(new DateRenderer());
    expDateCol.setCellRenderer(new DateRenderer());
}
    
    class DateRenderer extends DefaultTableCellRenderer {
    private final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Date) {
            value = formatter.format((Date) value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

    
     class DateChooserCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JDateChooser dateChooser;

    public DateChooserCellEditor() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("MM/dd/yyyy"); // 👈 Display format
    }

    @Override
    public Object getCellEditorValue() {
        return dateChooser.getDate();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        dateChooser.setDate((Date) value);
        return dateChooser;
    }           
}

     
private void loadCategoryTypes() {
    catype.removeAllItems();
    catype.setBackground(Color.WHITE);

    String sql = "SELECT DISTINCT cat_type FROM categories ORDER BY cat_type";

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            String type = rs.getString("cat_type");
            catype.addItem(type);
        }

        catype.setSelectedIndex(-1); // no selection by default

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading category types: " + e.getMessage());
    }
}


private void setupCategoryTypeListener() {
    catype.addActionListener(e -> {
        String selectedType = (String) catype.getSelectedItem();
        if (selectedType == null || selectedType.isBlank()) return;

        categories.removeAllItems();
        categories.setBackground(Color.WHITE);

        String sql = "SELECT cat_name FROM categories WHERE cat_type = ? ORDER BY cat_name";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
                "admin", "yeyel2025");
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, selectedType);
            try (ResultSet rs = pst.executeQuery()) {
                boolean hasResults = false;
                while (rs.next()) {
                    String catName = rs.getString("cat_name");
                    categories.addItem(catName);
                    hasResults = true;
                }

                if (!hasResults) {
                    categories.addItem("No categories found");
                } else {
                    categories.setSelectedIndex(0); // auto-select first
                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching category names: " + ex.getMessage());
        }
    });
}

    private void setupDateTimeFields() {
        // Current date in yy/MM/dd format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
        String currentDate = dateFormat.format(new Date());
        dated.setText(currentDate);
        dated.setEditable(false); // optional: make read-only

        // Current time in hh:mm a format (AM/PM)
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(new Date());
        timed.setText(currentTime);
        timed.setEditable(false); // optional: make read-only
    }
      
      public void clearSupplierFields() {

 
    // Clear JComboBox selection
    catype.setSelectedIndex(-1); // ✅ clears selection without removing items
    // Clear JComboBox selection
    categories.setSelectedIndex(-1); // ✅ clears selection without removing items
    sts.setText("");
    dated.setText("");   // since date is JTextField
    timed.setText("");

    // Clear JTable
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    model.setRowCount(0);
    }
      
      public void colorwhitebg(){
           Color whiteBG = Color.WHITE;
        Border flatBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY); // para di nawawala
        
                            // For date field
                dated.setEditable(true);          // must be true for white background
                dated.setBackground(Color.WHITE); 
                dated.setBorder(flatBorder);
                dated.setOpaque(true);
                dated.setFocusable(false);        // prevents user from typing

                // For time field
                timed.setEditable(true);          
                timed.setBackground(Color.WHITE); 
                timed.setBorder(flatBorder);
                timed.setOpaque(true);
                timed.setFocusable(false);        
        
      }
      
     private List<String> supplierList = new ArrayList<>();

     // 🟢 Call this once in your panel initialization
        private void startAutoTimeUpdate() {
            // Timer updates every 1 second (1000 ms)
            new javax.swing.Timer(1000, e -> {
                String currentTime = new SimpleDateFormat("hh:mm a").format(new Date());
                timed.setText(currentTime);
            }).start();
        }


    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableitems = new javax.swing.JTable();
        addrows = new javax.swing.JButton();
        submitbtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        timed = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        closebtn = new javax.swing.JLabel();
        sts = new javax.swing.JLabel();
        deleterows = new javax.swing.JButton();
        clearall = new javax.swing.JButton();
        dated = new javax.swing.JTextField();
        donorname = new javax.swing.JTextField();
        catype = new javax.swing.JComboBox<>();
        categories = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tableitems.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tableitems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Generic Names", "Quantity Donated", "Mfg Date", "Exp Date", "Units", "Description"
            }
        ));
        jScrollPane1.setViewportView(tableitems);

        addrows.setBackground(new java.awt.Color(0, 0, 0));
        addrows.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addrows.setForeground(new java.awt.Color(255, 255, 255));
        addrows.setText("+ Add ");
        addrows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addrowsActionPerformed(evt);
            }
        });

        submitbtn.setBackground(new java.awt.Color(0, 0, 0));
        submitbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        submitbtn.setForeground(new java.awt.Color(255, 255, 255));
        submitbtn.setText("Submit");
        submitbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                submitbtnMouseClicked(evt);
            }
        });
        submitbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitbtnActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/school-supplies (1).png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Medicines Donation ");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 33, 33));
        jLabel3.setText("Donor Name:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Category :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Type :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Date Donated:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Time Donated:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Status :");

        closebtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/close (1).png"))); // NOI18N
        closebtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closebtnMouseClicked(evt);
            }
        });

        sts.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        sts.setText(" ");

        deleterows.setBackground(new java.awt.Color(0, 0, 0));
        deleterows.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        deleterows.setForeground(new java.awt.Color(255, 255, 255));
        deleterows.setText("- Delete ");
        deleterows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleterowsActionPerformed(evt);
            }
        });

        clearall.setBackground(new java.awt.Color(0, 0, 0));
        clearall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clearall.setForeground(new java.awt.Color(255, 255, 255));
        clearall.setText("Clear");
        clearall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearallActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closebtn))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addrows, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(deleterows, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(submitbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(donorname)
                                    .addComponent(catype, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(timed, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(dated, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(clearall, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(categories, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(donorname, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(9, 9, 9)
                        .addComponent(catype, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addGap(9, 9, 9)
                        .addComponent(categories, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sts))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dated, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timed, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearall)
                        .addGap(75, 75, 75)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleterows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addrows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addrowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrowsActionPerformed
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();

    // ✅ Validate column count
    if (model.getColumnCount() != 6) {
        JOptionPane.showMessageDialog(null, "Table must have exactly 6 columns: Item Name, Quantity, Mfg Date, Exp Date, Units, Description.");
        return;
    }

    // ✅ Add empty row with proper types
    Object[] newRow = {
        "",     // Item Name (String)
        "",     // Quantity (String or numeric input)
        null,   // Mfg Date (JDateChooser-compatible)
        null,   // Exp Date (JDateChooser-compatible)
        "",     // Units (String)
        ""      // Description (String)
    };

    model.addRow(newRow);
    }//GEN-LAST:event_addrowsActionPerformed

    private void submitbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitbtnActionPerformed
   {
    String donorName = donorname.getText().trim();
    String types = catype.getSelectedItem() != null ? catype.getSelectedItem().toString().trim() : "";
    String status = sts.getText().trim();
    String dateText = dated.getText().trim();
    String timeDonated = timed.getText().trim();

    if (donorName.isEmpty() || types.isEmpty() || status.isEmpty() || dateText.isEmpty() || timeDonated.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please fill in all donation details.");
        return;
    }

    java.sql.Date dateDonated;
    try {
        java.util.Date parsedDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateText);
        dateDonated = new java.sql.Date(parsedDate.getTime());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Invalid date format. Please use dd/MM/yyyy.");
        return;
    }

    if (tableitems.isEditing()) tableitems.getCellEditor().stopCellEditing();
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "Please add at least one donation item.");
        return;
    }

    ApprovalDonationPanel approvalPanel = new ApprovalDonationPanel();
    approvalPanel.setInspectedBy(SessionManager.currentUserFullName);
    approvalPanel.setDate(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
    approvalPanel.setRemarks("");

    JDialog dialog = new JDialog((Frame) null, "Donation Approval", true);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.getContentPane().add(approvalPanel);
    dialog.pack();
    dialog.setLocationRelativeTo(null);

    // ✅ Attach confirm listener
    approvalPanel.setConfirmListener(e -> {
        String inspectedBy = approvalPanel.getInspectedBy();
        String remarks = approvalPanel.getRemarks();

        if (inspectedBy.isEmpty() || remarks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please complete inspection details.");
            return;
        }

        dialog.dispose(); // close the dialog

        // ✅ Proceed to save donation
        saveDonationToDatabase(donorName, types, status, dateDonated, timeDonated, inspectedBy, remarks, model);
    });

    dialog.setVisible(true); // show after listener is attached   
    }//GEN-LAST:event_submitbtnActionPerformed
  }

    private void saveDonationToDatabase(String donorName, String types, String status,
                                   java.sql.Date dateDonated, String timeDonated,
                                   String inspectedBy, String remarks,
                                   DefaultTableModel model) {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        // 🔍 Get last DonationID
        String lastDonationID = "DNT-000";
        int lastDonationNum = 0;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TOP 1 DonationID FROM donation_records_medicines WHERE DonationID LIKE 'DNT-%' ORDER BY DonationID DESC")) {
            if (rs.next()) {
                String lastID = rs.getString("DonationID").replace("DNT-", "").trim();
                lastDonationNum = Integer.parseInt(lastID);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Error reading DonationID: " + e.getMessage());
            conn.rollback();
            return;
        }
        String newDonationID = String.format("DNT-%03d", lastDonationNum + 1);

        // 🔍 Get last BatchNo
        String lastBatchNo = "BATCH-0000";
        int lastBatchNum = 0;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TOP 1 BatchNo FROM medicines ORDER BY BatchNo DESC")) {
            if (rs.next()) lastBatchNo = rs.getString("BatchNo");
            lastBatchNum = Integer.parseInt(lastBatchNo.replace("BATCH-", ""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Error reading BatchNo: " + e.getMessage());
            conn.rollback();
            return;
        }

        // 🔍 Get last valid GenericID
        String lastGenericID = "GEN-000";
        int lastGenNum = 0;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT GenericID FROM medicines WHERE GenericID LIKE 'GEN-%' ORDER BY GenericID DESC")) {
            while (rs.next()) {
                String candidateID = rs.getString("GenericID");
                Matcher matcher = Pattern.compile("GEN-(\\d{3})").matcher(candidateID);
                if (matcher.matches()) {
                    lastGenNum = Integer.parseInt(matcher.group(1));
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Error reading GenericID: " + e.getMessage());
            conn.rollback();
            return;
        }

        // 🧩 Insert donation header
        String insertDonationSQL = """
            INSERT INTO donation_records_medicines 
            (DonationID, DonorName, DonationType, DonationStatus, DateDonated, TimeDonated, TotalItems, InspectedBy, Remarks, DateEncoded)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(insertDonationSQL)) {
            pstmt.setString(1, newDonationID);
            pstmt.setString(2, donorName);
            pstmt.setString(3, types);
            pstmt.setString(4, status);
            pstmt.setDate(5, dateDonated);
            pstmt.setString(6, timeDonated);
            pstmt.setInt(7, model.getRowCount());
            pstmt.setString(8, inspectedBy);
            pstmt.setString(9, remarks);
            pstmt.executeUpdate();
        }

        // 🧩 Insert medicines
        String insertMedSQL = """
            INSERT INTO medicines 
            (GenericName, Units, Description, QuantityInStock, MfgDate, ExpDate, BatchNo, StockStatus, Condition, GenericID)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement medStmt = conn.prepareStatement(insertMedSQL)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String genericName = model.getValueAt(i, 0).toString().trim(); // GenericName
                String quantityStr = model.getValueAt(i, 1).toString().trim(); // Quantity
                Object mfgObj      = model.getValueAt(i, 2);                   // MfgDate
                Object expObj      = model.getValueAt(i, 3);                   // ExpDate
                String units       = model.getValueAt(i, 4).toString().trim(); // Units
                String description = model.getValueAt(i, 5).toString().trim(); // Description
                String batchNo     = String.format("BATCH-%04d", ++lastBatchNum);

                // ✅ Validate quantity
                if (quantityStr.isEmpty() || !quantityStr.matches("\\d+")) {
                    JOptionPane.showMessageDialog(null, "❌ Invalid quantity at row " + (i + 1) + ": '" + quantityStr + "'");
                    conn.rollback();
                    return;
                }
                int quantity = Integer.parseInt(quantityStr);
                String stockStatus = (quantity <= 10) ? "Low Stock" : "In Stock";

                // ✅ Validate dates
                if (!(mfgObj instanceof Date) || !(expObj instanceof Date)) {
                    JOptionPane.showMessageDialog(null, "❌ Invalid Mfg/Exp date format at row " + (i + 1));
                    conn.rollback();
                    return;
                }

                java.sql.Date mfgDate = new java.sql.Date(((Date) mfgObj).getTime());
                java.sql.Date expDate = new java.sql.Date(((Date) expObj).getTime());

                // ✅ Generate clean, sequential GenericID
                String genericID = String.format("GEN-%03d", ++lastGenNum);

                medStmt.setString(1, genericName);
                medStmt.setString(2, units);
                medStmt.setString(3, description);
                medStmt.setInt(4, quantity);
                medStmt.setDate(5, mfgDate);
                medStmt.setDate(6, expDate);
                medStmt.setString(7, batchNo);
                medStmt.setString(8, stockStatus);
                medStmt.setString(9, "Good");
                medStmt.setString(10, genericID);
                medStmt.addBatch();
            }
            medStmt.executeBatch();
        }

        conn.commit();
        JOptionPane.showMessageDialog(null, "✅ Donation successfully confirmed and saved!\nDonation ID: " + newDonationID);
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Error saving donation: " + ex.getMessage());
    }
}
    
   
    private void submitbtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitbtnMouseClicked
    
    }//GEN-LAST:event_submitbtnMouseClicked

    private void closebtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closebtnMouseClicked
        // Get the parent window (JFrame) of this panel
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose(); // Close the window
        }
    }//GEN-LAST:event_closebtnMouseClicked

    private void deleterowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleterowsActionPerformed
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    int[] selectedRows = tableitems.getSelectedRows();

    if (selectedRows.length == 0) {
        JOptionPane.showMessageDialog(this, "Please select at least one row to delete.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the selected row(s)?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        // Validate row indices before deletion
        int rowCount = model.getRowCount();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int rowIndex = selectedRows[i];
            if (rowIndex >= 0 && rowIndex < rowCount) {
                model.removeRow(rowIndex);
            }
        }

        // Optional: clear selection after deletion
        tableitems.clearSelection();
    }  
    }//GEN-LAST:event_deleterowsActionPerformed

    private void clearall() {
        // Reset supplier combo box
    // Reset type and category
     catype.setSelectedIndex(-1); // ✅ clears selection without removing items
    catype.setEditable(false);
    catype.setBackground(Color.WHITE);
    catype.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

   // Clear JComboBox selection
    categories.setSelectedIndex(-1); // ✅ clears selection without removing items
    categories.setEditable(false);
    categories.setBackground(Color.WHITE);
    categories.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    // Set current date and time
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");     // yy/MM/dd format
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");   // 12-hour format with AM/PM

    dated.setText(dateFormat.format(now));
    dated.setEditable(true); 
    dated.setBackground(Color.WHITE);
    dated.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    dated.setOpaque(true);
    dated.setFocusable(false);

    timed.setText(timeFormat.format(now));
    timed.setEditable(true); 
    timed.setBackground(Color.WHITE);
    timed.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    timed.setOpaque(true);
    timed.setFocusable(false);

    // Clear the table
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    model.setRowCount(0);
    }

    private void clearallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearallActionPerformed
        clearall();
    }//GEN-LAST:event_clearallActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addrows;
    private javax.swing.JComboBox<String> categories;
    private javax.swing.JComboBox<String> catype;
    private javax.swing.JButton clearall;
    private javax.swing.JLabel closebtn;
    private javax.swing.JTextField dated;
    private javax.swing.JButton deleterows;
    private javax.swing.JTextField donorname;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel sts;
    private javax.swing.JButton submitbtn;
    private javax.swing.JTable tableitems;
    private javax.swing.JTextField timed;
    // End of variables declaration//GEN-END:variables

   
}
