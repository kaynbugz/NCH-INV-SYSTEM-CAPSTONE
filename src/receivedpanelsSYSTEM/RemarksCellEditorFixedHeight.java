package receivedpanelsSYSTEM;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

// Fixed height Remarks editor
class RemarksCellEditorFixedHeight extends AbstractCellEditor implements TableCellEditor {
    private final JTextArea textArea = new JTextArea();
    private final int fixedLines;
    
    public RemarksCellEditorFixedHeight(int lines) {
        this.fixedLines = lines;
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(null);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textArea.setText(value != null ? value.toString() : "");
        textArea.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();
        table.setRowHeight(row, lineHeight * fixedLines);
        return textArea;
    }

    @Override
    public Object getCellEditorValue() {
        return textArea.getText();
    }
}