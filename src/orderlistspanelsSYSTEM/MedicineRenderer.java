package orderlistspanelsSYSTEM;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class MedicineRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Center text
        setHorizontalAlignment(SwingConstants.CENTER);

        // Kung may input -> red, else black
        if (value != null && !value.toString().trim().isEmpty()) {
            c.setForeground(Color.RED);
        } else {
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}

