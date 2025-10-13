package orderlistspanelsSYSTEM;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;

public class TextAreaRenderer extends JTextArea implements TableCellRenderer {

    public TextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setFont(new javax.swing.JLabel().getFont());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                    boolean isSelected, boolean hasFocus,
                                                    int row, int column) {
        String text = value == null ? "" : value.toString();
        setText(text);

        // Match table selection colors
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        int columnWidth = table.getColumnModel().getColumn(column).getWidth();
        setSize(columnWidth, Short.MAX_VALUE); // allow layout to compute height
        int preferredHeight = getPreferredSize().height;

        int fontHeight = getFontMetrics(getFont()).getHeight();

        // If text fits in one line, center it vertically by adding top margin
        if (preferredHeight <= fontHeight + 4) {
            int topPadding = (40 - fontHeight) / 2; // assuming rowHeight = 40
            setBorder(new EmptyBorder(topPadding, 2, 0, 2)); // top, left, bottom, right
            table.setRowHeight(row, 40); // keep standard height
        } else {
            setBorder(new EmptyBorder(4, 2, 4, 2));
            table.setRowHeight(row, preferredHeight); // auto expand for wrapped text
        }

        return this;
    }
}
