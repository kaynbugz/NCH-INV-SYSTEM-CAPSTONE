package AddingStocks;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Vector;

public class SupplierCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JComboBox<String> comboBox;
    private final DefaultComboBoxModel<String> model;
    private final Vector<String> supplierList;
    private final JTextField editor;

    public SupplierCellEditor(Vector<String> suppliers) {
        this.supplierList = suppliers;

        // use DefaultComboBoxModel for easier filtering
        model = new DefaultComboBoxModel<>();
        for (String s : suppliers) {
            model.addElement(s);
        }

        comboBox = new JComboBox<>(model);
        comboBox.setEditable(true);
        editor = (JTextField) comboBox.getEditor().getEditorComponent();

        // add document listener for live search
        editor.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                SwingUtilities.invokeLater(() -> {
                    String text = editor.getText().toLowerCase();

                    model.removeAllElements(); // clear current items
                    boolean hasMatch = false;
                    for (String s : supplierList) {
                        if (text.isEmpty() || s.toLowerCase().contains(text)) {
                            model.addElement(s);
                            hasMatch = true;
                        }
                    }

                    editor.setText(text.isEmpty() ? "" : editor.getText()); // preserve text

                    comboBox.setPopupVisible(hasMatch);
                });
            }

            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }

            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }

            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // set selected value only; don't remove items
        comboBox.setSelectedItem(value);

        SwingUtilities.invokeLater(() -> comboBox.setPopupVisible(true));
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
}
