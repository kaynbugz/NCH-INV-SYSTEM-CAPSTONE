package AddingStocks;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class MultiLineCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JTextArea textArea;
    private final JScrollPane scrollPane;

    public MultiLineCellEditor() {
        textArea = new JTextArea(4, 20); // 4 lines
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(textArea);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        textArea.setText(value != null ? value.toString() : "");
        return scrollPane;
    }

    @Override
    public Object getCellEditorValue() {
        return textArea.getText();
    }
}
