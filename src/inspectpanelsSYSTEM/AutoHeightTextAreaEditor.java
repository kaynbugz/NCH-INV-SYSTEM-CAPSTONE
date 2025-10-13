package inspectpanelsSYSTEM;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

public class AutoHeightTextAreaEditor extends AbstractCellEditor implements TableCellEditor {
    private final JTextArea textArea;
    private final JTable table;
    private int editingRow;
    private int editingColumn;

    public AutoHeightTextAreaEditor(JTable table) {
        this.table = table;

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Listen sa changes sa text
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                adjustRowHeight();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                adjustRowHeight();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                adjustRowHeight();
            }
        });
    }
    private static final int MIN_ROW_HEIGHT = 60; // 3 lines default

private void adjustRowHeight() {
    textArea.setSize(table.getColumnModel().getColumn(editingColumn).getWidth(), Short.MAX_VALUE);
    int preferredHeight = textArea.getPreferredSize().height + table.getRowMargin();

    // kung mas mataas yung preferredHeight kesa sa kasalukuyan, taasan
    int newHeight = Math.max(preferredHeight, MIN_ROW_HEIGHT);

    if (table.getRowHeight(editingRow) != newHeight) {
        table.setRowHeight(editingRow, newHeight);
    }
}
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        editingRow = row;
        editingColumn = column;
        textArea.setText(value == null ? "" : value.toString());
        SwingUtilities.invokeLater(this::adjustRowHeight);
        return textArea;
    }

    @Override
    public Object getCellEditorValue() {
        return textArea.getText();
    }
}