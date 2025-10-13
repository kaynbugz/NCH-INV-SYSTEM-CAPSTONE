package inspectpanelsSYSTEM;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class AutoHeightTextAreaRenderer extends JTextArea implements TableCellRenderer {

    private static final int MIN_ROW_HEIGHT = 60; // sakto mga 3 lines

    public AutoHeightTextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setBorder(new EmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText(value == null ? "" : value.toString());

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        // auto height calculation for multi-line
        setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
            int preferredHeight = getPreferredSize().height + 4;
        int finalHeight = Math.max(preferredHeight, MIN_ROW_HEIGHT);

        if (table.getRowHeight(row) != finalHeight) {
            table.setRowHeight(row, finalHeight);
        }

        return this;
    }
}