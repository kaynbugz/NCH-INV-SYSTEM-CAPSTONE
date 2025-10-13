
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

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
    type.setBackground(Color.WHITE);
    ctgry.setBackground(Color.WHITE);
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


   
    private void setupDateTimeFields() {
        // Current date in yy/MM/dd format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
        String currentDate = dateFormat.format(new Date());
        datedonated.setText(currentDate);
        datedonated.setEditable(false); // optional: make read-only

        // Current time in hh:mm a format (AM/PM)
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(new Date());
        time.setText(currentTime);
        time.setEditable(false); // optional: make read-only
    }
      
      public void clearSupplierFields() {

    // Clear text fields
    type.setText("");
    ctgry.setText("");
    sts.setText("");
    datedonated.setText("");   // since date is JTextField
    time.setText("");

    // Clear JTable
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    model.setRowCount(0);
    }
      
      public void colorwhitebg(){
           Color whiteBG = Color.WHITE;
        Border flatBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY); // para di nawawala
        
                            // For date field
                datedonated.setEditable(true);          // must be true for white background
                datedonated.setBackground(Color.WHITE); 
                datedonated.setBorder(flatBorder);
                datedonated.setOpaque(true);
                datedonated.setFocusable(false);        // prevents user from typing

                // For time field
                time.setEditable(true);          
                time.setBackground(Color.WHITE); 
                time.setBorder(flatBorder);
                time.setOpaque(true);
                time.setFocusable(false);        
        
      }
      
     private List<String> supplierList = new ArrayList<>();

     // 🟢 Call this once in your panel initialization
        private void startAutoTimeUpdate() {
            // Timer updates every 1 second (1000 ms)
            new javax.swing.Timer(1000, e -> {
                String currentTime = new SimpleDateFormat("hh:mm a").format(new Date());
                time.setText(currentTime);
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
        time = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        closebtn = new javax.swing.JLabel();
        sts = new javax.swing.JLabel();
        deleterows = new javax.swing.JButton();
        clearall = new javax.swing.JButton();
        type = new javax.swing.JTextField();
        ctgry = new javax.swing.JTextField();
        datedonated = new javax.swing.JTextField();
        donorname = new javax.swing.JTextField();

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

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 33, 33));
        jLabel3.setText("Donor Name:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Category :");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Type :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Date Donated:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Time Donated:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closebtn)
                .addGap(4, 4, 4))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addrows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleterows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(submitbtn)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(ctgry, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addComponent(type)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(donorname))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(time, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(datedonated, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(clearall))
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(closebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(donorname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ctgry, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(datedonated, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(clearall)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sts))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleterows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addrows, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addrowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrowsActionPerformed
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    Object[] newRow = {
        "",     // Item Name
        "",     // Quantity
        null,   // Mfg Date
        null,   // Exp Date
        "",     // Units
        ""      // Description
    };
    model.addRow(newRow);
    }//GEN-LAST:event_addrowsActionPerformed

    private void submitbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitbtnActionPerformed
     String donorName = donorname.getText().trim();
    String types = type.getText().trim();
    String status = sts.getText().trim();

    String dateText = datedonated.getText().trim();
    java.sql.Date dateDonated = null;
    if (!dateText.isEmpty()) {
        try {
            java.util.Date parsedDate = new SimpleDateFormat("yy/MM/dd").parse(dateText);
            dateDonated = new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use yy/MM/dd.");
            return;
        }
    }

    String timeDonated = time.getText().trim();

    if (tableitems.isEditing()) tableitems.getCellEditor().stopCellEditing();

    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    boolean hasValidItem = false;

    for (int i = 0; i < model.getRowCount(); i++) {
        Object itemNameObj = model.getValueAt(i, 0);
        Object quantityObj = model.getValueAt(i, 1);
        if (itemNameObj != null && quantityObj != null) {
            String itemName = itemNameObj.toString().trim();
            String qtyStr = quantityObj.toString().trim();
            try {
                int qty = Integer.parseInt(qtyStr);
                if (!itemName.isEmpty() && qty > 0) {
                    hasValidItem = true;
                    break;
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    if (!hasValidItem) {
        JOptionPane.showMessageDialog(null, "Please add at least one valid item with a quantity greater than 0.");
        return;
    }

    // Prompt for InspectedBy and Remarks
    JTextField inspectedByField = new JTextField(SessionManager.currentUserFullName);
    JTextArea remarksArea = new JTextArea(4, 20);
    remarksArea.setLineWrap(true);
    remarksArea.setWrapStyleWord(true);

    JPanel panel = new JPanel(new BorderLayout(5, 5));
    JPanel fields = new JPanel(new GridLayout(0, 1, 5, 5));
    fields.add(new JLabel("Inspected By:"));
    fields.add(inspectedByField);
    fields.add(new JLabel("Remarks:"));
    fields.add(new JScrollPane(remarksArea));
    panel.add(fields, BorderLayout.CENTER);

    int result = JOptionPane.showConfirmDialog(null, panel, "Inspection Details",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result != JOptionPane.OK_OPTION) {
        JOptionPane.showMessageDialog(null, "Submission cancelled.");
        return;
    }

    String inspectedBy = inspectedByField.getText().trim();
    String remarks = remarksArea.getText().trim();

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
        conn.setAutoCommit(false);

        // Get last batch number
        int lastBatchNumber = 0;
        String getLastBatchSQL = "SELECT TOP 1 BatchNo FROM medicines WHERE BatchNo LIKE 'BATCH-%' ORDER BY GenericID DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(getLastBatchSQL)) {
            if (rs.next()) {
                String lastBatch = rs.getString("BatchNo"); // e.g., "BATCH-0005"
                String numberPart = lastBatch.replace("BATCH-", "").trim();
                try {
                    lastBatchNumber = Integer.parseInt(numberPart);
                } catch (NumberFormatException ignored) {}
            }
        }

        // Insert each medicine item
        String insertMedSQL = "INSERT INTO medicines " +
            "(GenericName, QuantityInStock, Units, Description, MfgDate, ExpDate, BatchNo, StockStatus, Condition) " +
            "VALUES (?, ?, COALESCE(?, 'pcs'), ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement medPstmt = conn.prepareStatement(insertMedSQL)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String itemName = (model.getValueAt(i, 0) != null) ? model.getValueAt(i, 0).toString().trim() : "";
                String qtyStr = (model.getValueAt(i, 1) != null) ? model.getValueAt(i, 1).toString().trim() : "";
                Date mfgDateRaw = (Date) model.getValueAt(i, 2);
                Date expDateRaw = (Date) model.getValueAt(i, 3);
                String unit = (model.getValueAt(i, 4) != null) ? model.getValueAt(i, 4).toString().trim() : "pcs";
                String description = (model.getValueAt(i, 5) != null) ? model.getValueAt(i, 5).toString().trim() : "";

                if (itemName.isEmpty() || qtyStr.isEmpty()) continue;

                try {
                    int quantity = Integer.parseInt(qtyStr);
                    if (quantity <= 0) continue;

                    java.sql.Date mfgDate = (mfgDateRaw != null) ? new java.sql.Date(mfgDateRaw.getTime()) : null;
                    java.sql.Date expDate = (expDateRaw != null) ? new java.sql.Date(expDateRaw.getTime()) : null;

                    String batchNo = "BATCH-" + String.format("%04d", lastBatchNumber + i + 1);

                    medPstmt.setString(1, itemName);
                    medPstmt.setInt(2, quantity);
                    medPstmt.setString(3, unit);
                    medPstmt.setString(4, description);
                    medPstmt.setDate(5, mfgDate);
                    medPstmt.setDate(6, expDate);
                    medPstmt.setString(7, batchNo);
                    medPstmt.setString(8, "Low Stock");
                    medPstmt.setString(9, "Good");
                    medPstmt.addBatch();
                } catch (NumberFormatException ignored) {}
            }
            medPstmt.executeBatch();
        }

        // Insert donation summary
        String insertDonationSQL = "INSERT INTO donation_records_medicines " +
            "(DonorName, DonationType, DonationStatus, DateDonated, TimeDonated, TotalItems, InspectedBy, Remarks) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement donationPstmt = conn.prepareStatement(insertDonationSQL)) {
            int totalItems = model.getRowCount();

            donationPstmt.setString(1, donorName);
            donationPstmt.setString(2, types);
            donationPstmt.setString(3, status);
            donationPstmt.setDate(4, dateDonated);
            donationPstmt.setString(5, timeDonated);
            donationPstmt.setInt(6, totalItems);
            donationPstmt.setString(7, inspectedBy);
            donationPstmt.setString(8, remarks);

            donationPstmt.executeUpdate();
        }

        conn.commit();
        JOptionPane.showMessageDialog(null, "Donation and medicines successfully saved!");

        model.setRowCount(0);
        clearall();
        sts.setText("Available");

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving donation: " + e.getMessage());
    }
    }//GEN-LAST:event_submitbtnActionPerformed

    
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
            // Delete from bottom to top to prevent shifting issues
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model.removeRow(selectedRows[i]);
            }
        }
    }//GEN-LAST:event_deleterowsActionPerformed

    private void clearall() {
        // Reset supplier combo box
    // Reset type and category
    type.setText("");
    type.setEditable(false);
    type.setBackground(Color.WHITE);
    type.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    ctgry.setText("");
    ctgry.setEditable(false);
    ctgry.setBackground(Color.WHITE);
    ctgry.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    // Set current date and time
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");     // yy/MM/dd format
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");   // 12-hour format with AM/PM

    datedonated.setText(dateFormat.format(now));
    datedonated.setEditable(true); 
    datedonated.setBackground(Color.WHITE);
    datedonated.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    datedonated.setOpaque(true);
    datedonated.setFocusable(false);

    time.setText(timeFormat.format(now));
    time.setEditable(true); 
    time.setBackground(Color.WHITE);
    time.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    time.setOpaque(true);
    time.setFocusable(false);

    // Clear the table
    DefaultTableModel model = (DefaultTableModel) tableitems.getModel();
    model.setRowCount(0);
    }

    private void clearallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearallActionPerformed
        clearall();
    }//GEN-LAST:event_clearallActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addrows;
    private javax.swing.JButton clearall;
    private javax.swing.JLabel closebtn;
    private javax.swing.JTextField ctgry;
    private javax.swing.JTextField datedonated;
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
    private javax.swing.JTextField time;
    private javax.swing.JTextField type;
    // End of variables declaration//GEN-END:variables

   
}
