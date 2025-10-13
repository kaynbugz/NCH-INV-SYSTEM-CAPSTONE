package inspectpanelsSYSTEM;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JTextArea textArea;

    public TextAreaCellEditor() {
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        textArea.setText(value == null ? "" : value.toString());
        return textArea;
    }

    @Override
    public Object getCellEditorValue() {
        return textArea.getText();
    }
}
