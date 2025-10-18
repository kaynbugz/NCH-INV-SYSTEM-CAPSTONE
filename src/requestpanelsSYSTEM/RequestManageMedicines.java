
package requestpanelsSYSTEM;

    import com.toedter.calendar.JDateChooser;
    import medicinespanelsSYSTEM.IssuedMedicines;
    import medicinespanelsSYSTEM.IssuedMedicines;
    import requestingMedicinesSubmit.RequestedMedicinesSubmit;
    import requestingMedicinesSubmit.RequestedMedicinesSubmit;
    import requestingMedicinesSubmit.UpdatingAllStatusMedicines;
    import requestingMedicinesSubmit.UpdatingAllStatusMedicines;
    import dashboardSYSTEM.homepageSYSTEM;
    import static groovy.ui.text.FindReplaceUtility.dispose;
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.Component;
    import java.awt.Font;
    import java.awt.Frame;
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
    import javax.swing.DefaultComboBoxModel;
    import javax.swing.JDialog;
import javax.swing.SwingConstants;
    import javax.swing.table.DefaultTableCellRenderer;
    import javax.swing.table.JTableHeader;
    import requestingMedicinesSubmit.IssuingPanel;


public class RequestManageMedicines extends javax.swing.JPanel {

  
    public RequestManageMedicines() {
        initComponents();
        loadRequestedItemsData();
        loadApprovedMedicinesData();
        loadIssuedItems();
      
        searchidname.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
            filterRequests();
            }
        });

        startd.addPropertyChangeListener("date", e -> filterRequests());
        endd.addPropertyChangeListener("date", e -> filterRequests());
        
               // After your table is initialized:
        JTableHeader header = requested_meds.getTableHeader();

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
        JTableHeader header1 = approved_meds.getTableHeader();

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
        JTableHeader header2 = issued_items.getTableHeader();

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
        
        setupRequestFilters();
  
     }
    
        // Call this method to initialize listeners for automatic filtering
    private void setupRequestFilters() {
        // 1️⃣ Add DocumentListener to search field
        searchidname.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterRequests(); }
            @Override public void removeUpdate(DocumentEvent e) { filterRequests(); }
            @Override public void changedUpdate(DocumentEvent e) { filterRequests(); }
        });

        // 2️⃣ Add PropertyChangeListener to date choosers
        startd.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) filterRequests();
        });
        endd.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) filterRequests();
        });
    }

    public void loadApprovedMedicinesData() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[]{"Request ID", "Approval Date", "Approved By", "Total Medicines", "Total Medicines Issued", "Issuing Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    approved_meds.setModel(model);
    model.setRowCount(0);

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String sql = """
        SELECT 
            amr.request_id,
            MAX(amr.approval_date) AS approval_date,
            MAX(amr.approved_by) AS approved_by,
            COUNT(DISTINCT amr.medicine_id) AS total_items
        FROM approved_medicines_request amr
        GROUP BY amr.request_id
        ORDER BY amr.request_id
    """;

    String issuedCountQuery = """
        SELECT COUNT(*) AS issued_count
        FROM requested_items_medicines
        WHERE request_id = ? AND (UPPER(issuing_status) = 'ISSUED' OR UPPER(issuing_status) = 'PARTIALLY ISSUED')
    """;

    String statusQuery = """
        SELECT issuing_status
        FROM requested_items_medicines
        WHERE request_id = ?
    """;

    int totalItemsAll = 0;
    int totalIssuedAll = 0;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String requestId = rs.getString("request_id");
            if (requestId == null) continue;
            requestId = requestId.toUpperCase(); // ✅ Force uppercase

            Date approvalDate = rs.getDate("approval_date");
            String approvedBy = rs.getString("approved_by");
            int totalItems = rs.getInt("total_items");
            totalItemsAll += totalItems;

            int totalIssued = 0;
            try (PreparedStatement psIssued = conn.prepareStatement(issuedCountQuery)) {
                psIssued.setString(1, requestId);
                try (ResultSet rsIssued = psIssued.executeQuery()) {
                    if (rsIssued.next()) {
                        totalIssued = rsIssued.getInt("issued_count");
                    }
                }
            }
            totalIssuedAll += totalIssued;

            boolean hasPending = false;
            boolean hasIssued = false;
            boolean hasPartial = false;
            try (PreparedStatement psStatus = conn.prepareStatement(statusQuery)) {
                psStatus.setString(1, requestId);
                try (ResultSet rsStatus = psStatus.executeQuery()) {
                    while (rsStatus.next()) {
                        String status = rsStatus.getString("issuing_status");
                        if (status == null) continue;
                        switch (status.toUpperCase()) {
                            case "PENDING ISSUED" -> hasPending = true;
                            case "ISSUED" -> hasIssued = true;
                            case "PARTIALLY ISSUED" -> hasPartial = true;
                        }
                    }
                }
            }

            String issuingStatus;
            if (hasIssued && hasPending) issuingStatus = "Partially Issued";
            else if (hasPartial) issuingStatus = "Partially Issued";
            else if (hasIssued && !hasPending && !hasPartial) issuingStatus = "Issued";
            else if (hasPending && !hasIssued && !hasPartial) issuingStatus = "Pending Issued";
            else issuingStatus = "Pending Issued";

            // Format date for display
            String formattedDate = (approvalDate != null)
                ? new SimpleDateFormat("yyyy-MM-dd").format(approvalDate)
                : "";

            model.addRow(new Object[]{
                requestId,
                formattedDate,
                approvedBy != null ? approvedBy : "",
                totalItems,
                totalIssued,
                issuingStatus
            });
        }

        qtymedicines.setText(String.valueOf(totalItemsAll));
        totalmedissued.setText(String.valueOf(totalIssuedAll));

        JTableHeader header = approved_meds.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < approved_meds.getColumnCount(); i++) {
            approved_meds.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to load approved medicines data.");
    }
   }
public void loadRequestedItemsData() {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.addColumn("Request ID");
    model.addColumn("Request Date");
    model.addColumn("Department");
    model.addColumn("Requested By");
    model.addColumn("Total Medicines");
    model.addColumn("Total Medicines Request");
    model.addColumn("Approval Status");

    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String sql = """
        SELECT 
            r.request_id,
            r.request_date,
            r.department,
            r.requested_by,
            COUNT(DISTINCT ri.GenericID) AS total_items,
            SUM(ri.quantity_requested) AS total_quantity_requested,
            CASE
                WHEN COUNT(CASE WHEN ri.approval_status = 'Requested' THEN 1 END) = 0
                     AND COUNT(CASE WHEN ri.approval_status = 'Rejected' THEN 1 END) = 0
                     AND COUNT(CASE WHEN ri.approval_status = 'Approved' THEN 1 END) > 0
                     THEN 'Approved'
                WHEN COUNT(CASE WHEN ri.approval_status = 'Approved' THEN 1 END) > 0
                     AND COUNT(CASE WHEN ri.approval_status = 'Requested' THEN 1 END) > 0
                     THEN 'Partially Approved'
                WHEN COUNT(CASE WHEN ri.approval_status = 'Rejected' THEN 1 END) > 0
                     AND COUNT(CASE WHEN ri.approval_status = 'Requested' THEN 1 END) > 0
                     THEN 'Partially Processed'
                WHEN COUNT(CASE WHEN ri.approval_status = 'Rejected' THEN 1 END) > 0
                     AND COUNT(CASE WHEN ri.approval_status = 'Approved' THEN 1 END) = 0
                     THEN 'Rejected'
                ELSE 'Requested'
            END AS approval_status_summary
        FROM requests r
        JOIN requested_items_medicines ri ON r.request_id = ri.request_id
        GROUP BY r.request_id, r.request_date, r.department, r.requested_by
        ORDER BY r.request_id
    """;

    int totalRequests = 0;
    int totalItems = 0;
    int totalQuantity = 0;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String requestId = rs.getString("request_id"); // ✅ FIXED
            int items = rs.getInt("total_items");
            int qty = rs.getInt("total_quantity_requested");

            model.addRow(new Object[]{
                requestId,
                rs.getDate("request_date"),
                rs.getString("department"),
                rs.getString("requested_by"),
                items,
                qty,
                rs.getString("approval_status_summary")
            });

            totalRequests++;
            totalItems += items;
            totalQuantity += qty;
        }

        requested_meds.setModel(model);

        JTableHeader header = requested_meds.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < requested_meds.getColumnCount(); i++) {
            requested_meds.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        DefaultTableCellRenderer redRenderer = new DefaultTableCellRenderer();
        redRenderer.setForeground(Color.RED);
        redRenderer.setHorizontalAlignment(JLabel.CENTER);
        requested_meds.getColumnModel().getColumn(0).setCellRenderer(redRenderer);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if (!isSelected) {
                    switch (status) {
                        case "Approved" -> c.setForeground(new Color(0, 128, 0));
                        case "Rejected" -> c.setForeground(Color.RED);
                        case "Requested" -> c.setForeground(Color.BLUE);
                        case "Partially Approved" -> c.setForeground(new Color(255, 140, 0));
                        case "Partially Processed" -> c.setForeground(Color.MAGENTA);
                        default -> c.setForeground(Color.GRAY);
                    }
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        requested_meds.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);

        totalofreq.setText("" + totalRequests);
        totalofmedssreq.setText("" + totalItems);
        totalofqtyreq.setText("" + totalQuantity);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to load requested items data.");
    }
}

public void loadIssuedItems() {
    String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true";
    String dbUser = "admin";
    String dbPass = "yeyel2025";

    String sql = """
        SELECT 
            iim.request_id,
            COUNT(DISTINCT iim.medicine_id) AS total_items,
            SUM(iim.quantity_requested) AS total_quantity_requested,
            SUM(iim.quantity_issued) AS total_quantity_issued,
            MAX(iim.issued_by) AS issued_by,
            MAX(iim.issued_date) AS issued_date,
            MAX(iim.issued_time) AS issued_time,
            MAX(iim.issuing_status) AS issuing_status
        FROM issued_items_medicines iim
        GROUP BY iim.request_id
        ORDER BY MAX(iim.issued_date) DESC, MAX(iim.issued_time) DESC
    """;

    try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
            "Request ID", "Total Medicines", "Total Quantity Requested", "Total Quantity Issued",
            "Issued By", "Issued Date", "Issued Time", "Issuing Status"
        });

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int totalRequests = 0;
        int totalItems = 0;
        int totalQtyRequested = 0;
        int totalQtyIssued = 0;

        while (rs.next()) {
            int items = rs.getInt("total_items");
            int qtyRequested = rs.getInt("total_quantity_requested");
            int qtyIssued = rs.getInt("total_quantity_issued");

            java.sql.Date sqlDate = rs.getDate("issued_date");
            java.sql.Time sqlTime = rs.getTime("issued_time");

            String formattedDate = (sqlDate != null) ? dateFormat.format(sqlDate) : "";
            String formattedTime = (sqlTime != null) ? timeFormat.format(sqlTime) : "";

            String requestId = rs.getString("request_id");
            if (requestId != null) requestId = requestId.toUpperCase(); // ✅ Ensure uppercase

            model.addRow(new Object[]{
                requestId,
                items,
                qtyRequested,
                qtyIssued,
                rs.getString("issued_by"),
                formattedDate,
                formattedTime,
                rs.getString("issuing_status")
            });

            totalRequests++;
            totalItems += items;
            totalQtyRequested += qtyRequested;
            totalQtyIssued += qtyIssued;
        }

        issued_items.setModel(model);
        issued_items.repaint();

        // 🔹 Table styling
        JTableHeader header = issued_items.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 10));

        issued_items.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        issued_items.setRowHeight(30);

        // Center all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < issued_items.getColumnCount(); i++) {
            issued_items.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Status color renderer for Issuing Status column
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if (!isSelected) {
                    switch (status) {
                        case "Issued" -> c.setForeground(new Color(0, 128, 0));
                        case "Pending Issued" -> c.setForeground(Color.BLUE);
                        case "Rejected" -> c.setForeground(Color.RED);
                        default -> c.setForeground(Color.GRAY);
                    }
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        issued_items.getColumnModel().getColumn(7).setCellRenderer(statusRenderer);

        totalmeds.setText(String.valueOf(totalItems));
        totalqtyreq.setText(String.valueOf(totalQtyRequested));
        totalqtyissued.setText(String.valueOf(totalQtyIssued));

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to load issued medicines data.");
    }
}
                
public void filterRequests() {
    String searchText = searchidname.getText().trim();
    java.util.Date startDate = startd.getDate();
    java.util.Date endDate = endd.getDate();

    String sql = "SELECT r.request_id, r.request_date, r.department, r.requested_by, "
           + "COUNT(ri.GenericID) AS total_items, "
           + "SUM(ri.quantity_requested) AS total_quantity_requested, "
           + "CASE "
           + "WHEN COUNT(DISTINCT ri.approval_status) = 1 "
           + "     AND COUNT(DISTINCT ri.issuing_status) = 1 "
           + "     THEN MAX(ri.approval_status)+' / '+MAX(ri.issuing_status) "
           + "WHEN COUNT(CASE WHEN ri.approval_status='Approved' THEN 1 END) > 0 "
           + "     AND COUNT(CASE WHEN ri.approval_status='Requested' THEN 1 END) > 0 "
           + "     THEN 'Partially Approved' "
           + "WHEN COUNT(CASE WHEN ri.issuing_status='Issued' THEN 1 END) > 0 "
           + "     AND COUNT(CASE WHEN ri.issuing_status='Pending Issued' THEN 1 END) > 0 "
           + "     THEN 'Partially Issued' "
           + "ELSE 'Pending Review' END AS overall_status "
           + "FROM requests r "
           + "JOIN requested_items_medicines ri ON r.request_id=ri.request_id "
           + "WHERE 1=1 ";

    if (!searchText.isEmpty()) {
        sql += "AND (r.request_id LIKE ? OR r.requested_by LIKE ? OR r.department LIKE ?) ";
    }
    if (startDate != null && endDate != null) {
        sql += "AND r.request_date BETWEEN ? AND ? ";
    }

    sql += "GROUP BY r.request_id, r.request_date, r.department, r.requested_by "
         + "ORDER BY r.request_id";

    DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new Object[]{
        "Request ID", "Request Date", "Department", "Requested By",
        "Total Items", "Total Quantity Requested", "Overall Status"
    });

    try (Connection conn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=true;trustServerCertificate=true",
            "admin", "yeyel2025");
         PreparedStatement ps = conn.prepareStatement(sql)) {

        int idx = 1;
        if (!searchText.isEmpty()) {
            ps.setString(idx++, "%" + searchText + "%");
            ps.setString(idx++, "%" + searchText + "%");
            ps.setString(idx++, "%" + searchText + "%");
        }
        if (startDate != null && endDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ps.setString(idx++, sdf.format(startDate));
            ps.setString(idx++, sdf.format(endDate));
        }

        ResultSet rs = ps.executeQuery();

        int totalRequests = 0, totalItems = 0, totalQuantity = 0;
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("request_id"),
                rs.getDate("request_date"),
                rs.getString("department"),
                rs.getString("requested_by"),
                rs.getInt("total_items"),
                rs.getInt("total_quantity_requested"),
                rs.getString("overall_status")
            });
            totalRequests++;
            totalItems += rs.getInt("total_items");
            totalQuantity += rs.getInt("total_quantity_requested");
        }

        requested_meds.setModel(model);

        JTableHeader header = requested_meds.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer redCenterRenderer = new DefaultTableCellRenderer();
        redCenterRenderer.setForeground(Color.RED);
        redCenterRenderer.setHorizontalAlignment(JLabel.CENTER);
        requested_meds.getColumnModel().getColumn(0).setCellRenderer(redCenterRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < requested_meds.getColumnCount(); i++) {
            requested_meds.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 🔹 Status color renderer
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if (!isSelected) {
                    switch (status) {
                        case "Approved / Issued" -> c.setForeground(new Color(0, 128, 0));
                        case "Rejected / Pending Issued" -> c.setForeground(Color.RED);
                        case "Pending Review" -> c.setForeground(Color.BLUE);
                        case "Partially Approved" -> c.setForeground(new Color(255, 140, 0));
                        case "Partially Issued" -> c.setForeground(Color.MAGENTA);
                        default -> c.setForeground(Color.GRAY);
                    }
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        };
        requested_meds.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);

        totalofreq.setText("" + totalRequests);
        totalofmedssreq.setText("" + totalItems);
        totalofqtyreq.setText("" + totalQuantity);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to filter data.");
    }

              
     
          
      }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        requested_meds = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        searchidname = new javax.swing.JTextField();
        startd = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        endd = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        totalofreq = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        totalofqtyreq = new javax.swing.JLabel();
        view = new javax.swing.JButton();
        reqidsearch = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        totalofmedssreq = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        approved_meds = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        searchidname1 = new javax.swing.JTextField();
        startd1 = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        endd1 = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        qtymedicines = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        totalmedissued = new javax.swing.JLabel();
        view2 = new javax.swing.JButton();
        search_req_issue = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        searchidname2 = new javax.swing.JTextField();
        startd2 = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        endd2 = new com.toedter.calendar.JDateChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        issued_items = new javax.swing.JTable();
        view3 = new javax.swing.JButton();
        issue_id = new javax.swing.JTextField();
        totalqtyreq = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        totalqtyissued = new javax.swing.JLabel();
        totalmeds = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(48, 122, 55));

        jPanel2.setBackground(new java.awt.Color(48, 122, 55));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 249, 103));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel3MouseClicked(evt);
            }
        });

        requested_meds.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Request ID", "Request Date", "Department", "Requested By", "Total Medicines", "Total Quantity Requested", "Status"
            }
        ));
        jScrollPane1.setViewportView(requested_meds);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Search");

        searchidname.setText(" ");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Stating Date");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("End Date");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Total Request");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Total Qty Request");

        totalofreq.setBackground(new java.awt.Color(0, 0, 0));
        totalofreq.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        totalofreq.setForeground(new java.awt.Color(255, 255, 255));
        totalofreq.setOpaque(true);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Total Medicines");

        totalofqtyreq.setBackground(new java.awt.Color(0, 0, 0));
        totalofqtyreq.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        totalofqtyreq.setForeground(new java.awt.Color(255, 255, 255));
        totalofqtyreq.setOpaque(true);

        view.setBackground(new java.awt.Color(0, 0, 0));
        view.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        view.setForeground(new java.awt.Color(255, 255, 255));
        view.setText("VIEW");
        view.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewMouseClicked(evt);
            }
        });
        view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewActionPerformed(evt);
            }
        });

        reqidsearch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        reqidsearch.setForeground(new java.awt.Color(255, 0, 51));
        reqidsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reqidsearchActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Search ID");

        totalofmedssreq.setBackground(new java.awt.Color(0, 0, 0));
        totalofmedssreq.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalofmedssreq.setForeground(new java.awt.Color(255, 255, 255));
        totalofmedssreq.setOpaque(true);

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Refresh");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(2, 2, 2)
                        .addComponent(searchidname, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(2, 2, 2)
                        .addComponent(startd, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel3)
                        .addGap(2, 2, 2)
                        .addComponent(endd, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 380, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(2, 2, 2)
                        .addComponent(totalofreq, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addGap(2, 2, 2)
                        .addComponent(totalofqtyreq, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addGap(2, 2, 2)
                        .addComponent(totalofmedssreq, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addGap(2, 2, 2)
                        .addComponent(reqidsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(19, 19, 19))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(view)
                .addGap(41, 41, 41))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(searchidname, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton2)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(2, 2, 2))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startd, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(endd, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(reqidsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(totalofreq, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(jLabel6))
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(totalofqtyreq, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalofmedssreq, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(view)
                .addGap(20, 20, 20))
        );

        jTabbedPane1.addTab("Requested Items ", jPanel3);

        jPanel5.setBackground(new java.awt.Color(255, 249, 103));

        approved_meds.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Request Date", "Department", "Requested By", "Total Medicines", "Total Medicines Issued", "Status"
            }
        ));
        jScrollPane2.setViewportView(approved_meds);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Search");

        searchidname1.setText(" ");
        searchidname1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchidname1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Stating Date");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("End Date");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Total Qty Medicines");

        qtymedicines.setBackground(java.awt.Color.black);
        qtymedicines.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        qtymedicines.setForeground(java.awt.Color.white);
        qtymedicines.setOpaque(true);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Total Medicines Issued");

        totalmedissued.setBackground(new java.awt.Color(0, 0, 0));
        totalmedissued.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        totalmedissued.setForeground(new java.awt.Color(255, 255, 255));
        totalmedissued.setOpaque(true);

        view2.setBackground(new java.awt.Color(0, 0, 0));
        view2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        view2.setForeground(new java.awt.Color(255, 255, 255));
        view2.setText("VIEW");
        view2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                view2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                view2MouseEntered(evt);
            }
        });
        view2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view2ActionPerformed(evt);
            }
        });

        search_req_issue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        search_req_issue.setForeground(new java.awt.Color(255, 0, 51));
        search_req_issue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_req_issueActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Search ID");

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Refresh");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(qtymedicines, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalmedissued, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(345, 345, 345)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(2, 2, 2)
                                .addComponent(search_req_issue, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(view2)
                                .addGap(10, 10, 10))))
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(2, 2, 2)
                        .addComponent(searchidname1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addGap(0, 0, 0)
                        .addComponent(startd1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel11)
                        .addGap(2, 2, 2)
                        .addComponent(endd1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(44, 44, 44))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(endd1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(searchidname1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel11)
                        .addComponent(jLabel9)
                        .addComponent(startd1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(qtymedicines, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                            .addComponent(totalmedissued, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(search_req_issue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(view2)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1152, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 1152, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 526, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Approved Medicines", jPanel4);

        jPanel6.setBackground(new java.awt.Color(255, 249, 103));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Search");

        searchidname2.setText(" ");
        searchidname2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchidname2ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Stating Date");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("End Date");

        issued_items.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Request Date", "Department", "Requested By", "Total Items", "Total Qty Request", "Total Qty Issued", "Status"
            }
        ));
        jScrollPane3.setViewportView(issued_items);

        view3.setBackground(new java.awt.Color(0, 0, 0));
        view3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        view3.setForeground(new java.awt.Color(255, 255, 255));
        view3.setText("VIEW");
        view3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                view3MouseClicked(evt);
            }
        });
        view3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view3ActionPerformed(evt);
            }
        });

        issue_id.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        issue_id.setForeground(new java.awt.Color(255, 0, 51));
        issue_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issue_idActionPerformed(evt);
            }
        });

        totalqtyreq.setBackground(new java.awt.Color(0, 0, 0));
        totalqtyreq.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        totalqtyreq.setForeground(java.awt.Color.white);
        totalqtyreq.setOpaque(true);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Total Qty Request");

        totalqtyissued.setBackground(new java.awt.Color(0, 0, 0));
        totalqtyissued.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        totalqtyissued.setForeground(new java.awt.Color(255, 255, 255));
        totalqtyissued.setOpaque(true);

        totalmeds.setBackground(new java.awt.Color(0, 0, 0));
        totalmeds.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        totalmeds.setForeground(java.awt.Color.white);
        totalmeds.setOpaque(true);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("Total Medicines");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setText("Total Qty Issued");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("Search ID");

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Refresh");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(2, 2, 2)
                        .addComponent(searchidname2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addGap(2, 2, 2)
                        .addComponent(startd2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel18)
                        .addGap(2, 2, 2)
                        .addComponent(endd2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 367, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(2, 2, 2)
                        .addComponent(totalmeds, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel19)
                                .addGap(2, 2, 2)
                                .addComponent(totalqtyreq, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel21)
                                .addGap(2, 2, 2)
                                .addComponent(totalqtyissued, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel22)
                                .addGap(2, 2, 2)
                                .addComponent(issue_id, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(view3)
                                .addGap(28, 28, 28)))))
                .addGap(19, 19, 19))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(searchidname2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(endd2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(startd2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(issue_id, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(totalqtyreq, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(totalqtyissued, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel20)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel19)
                                    .addComponent(totalmeds, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)))))
                .addGap(7, 7, 7)
                .addComponent(view3)
                .addGap(18, 18, 18))
        );

        jTabbedPane1.addTab("Complete Medicines Issued", jPanel6);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nch/left.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
           // Get the parent window (JFrame) of this panel
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose(); // Close the window
        }

        // Open the homepage window
        homepageSYSTEM homepage = new homepageSYSTEM();
        homepage.setVisible(true);
    }//GEN-LAST:event_jLabel7MouseClicked

    private void issue_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issue_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_issue_idActionPerformed

    private void view3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_view3ActionPerformed

    private void view3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_view3MouseClicked
    String requestId = issue_id.getText().trim();
    if (requestId.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please enter a Request ID.");
        return;
    }
    UpdatingAllStatusMedicines updPanel = new UpdatingAllStatusMedicines();
    updPanel.setRequestDataByRequestId(requestId);

    JDialog dialog = new JDialog((Frame) null, "Update Request Details", true);
    dialog.setContentPane(updPanel);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    }//GEN-LAST:event_view3MouseClicked

    private void searchidname2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchidname2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchidname2ActionPerformed

    private void search_req_issueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_req_issueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_req_issueActionPerformed

    private void view2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_view2ActionPerformed

    private void view2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_view2MouseClicked
        String searchReqId = search_req_issue.getText().trim();
        if (searchReqId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a Request ID.");
            return;
        }

        IssuingPanel updPanel = new IssuingPanel();
        updPanel.setRequestData(searchReqId);

        // Show inside JDialog
        JDialog dialog = new JDialog((Frame) null, "Update Request Details", true);
        dialog.setContentPane(updPanel);
        dialog.pack(); // auto-size
        dialog.setLocationRelativeTo(null); // center on screen
        dialog.setVisible(true); // show modal
    }//GEN-LAST:event_view2MouseClicked

    private void searchidname1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchidname1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchidname1ActionPerformed

    private void reqidsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reqidsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reqidsearchActionPerformed

    private void viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewActionPerformed

    }//GEN-LAST:event_viewActionPerformed

    private void viewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewMouseClicked
    String searchReqId = reqidsearch.getText().trim();

    if (searchReqId.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Please enter a Request ID.");
        return;
    }

    try {
        // Create and configure the panel
        RequestedMedicinesSubmit updPanel = new RequestedMedicinesSubmit();
        updPanel.setRequestData(searchReqId); // ✅ Pass VARCHAR request_id

        // Create modal dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, "Requested Medicines", true);
        dialog.setUndecorated(true); // Optional: remove window borders
        dialog.setContentPane(updPanel);
        dialog.pack(); // Auto-size to panel
        dialog.setLocationRelativeTo(this); // Center relative to current component
        dialog.setVisible(true); // Show modal

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to load request details: " + e.getMessage());
    }
    }//GEN-LAST:event_viewMouseClicked

    private void view2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_view2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_view2MouseEntered

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
    loadApprovedMedicinesData();
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        loadRequestedItemsData();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel3MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
       loadApprovedMedicinesData();
    }//GEN-LAST:event_jButton3MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable approved_meds;
    private com.toedter.calendar.JDateChooser endd;
    private com.toedter.calendar.JDateChooser endd1;
    private com.toedter.calendar.JDateChooser endd2;
    private javax.swing.JTextField issue_id;
    private javax.swing.JTable issued_items;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JLabel qtymedicines;
    private javax.swing.JTextField reqidsearch;
    private javax.swing.JTable requested_meds;
    private javax.swing.JTextField search_req_issue;
    private javax.swing.JTextField searchidname;
    private javax.swing.JTextField searchidname1;
    private javax.swing.JTextField searchidname2;
    private com.toedter.calendar.JDateChooser startd;
    private com.toedter.calendar.JDateChooser startd1;
    private com.toedter.calendar.JDateChooser startd2;
    private javax.swing.JLabel totalmedissued;
    private javax.swing.JLabel totalmeds;
    private javax.swing.JLabel totalofmedssreq;
    private javax.swing.JLabel totalofqtyreq;
    private javax.swing.JLabel totalofreq;
    private javax.swing.JLabel totalqtyissued;
    private javax.swing.JLabel totalqtyreq;
    private javax.swing.JButton view;
    private javax.swing.JButton view2;
    private javax.swing.JButton view3;
    // End of variables declaration//GEN-END:variables
}
