package orderlistspanelsSYSTEM;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Arrays;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class UnitCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JComboBox<String> comboBox;
    private final JTextField editor;
    private final String[] unitsList;

    public UnitCellEditor(String[] units) {
        this.unitsList = units;
        comboBox = new JComboBox<>(unitsList);
        comboBox.setEditable(true);

        editor = (JTextField) comboBox.getEditor().getEditorComponent();

        editor.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String input = editor.getText().trim();
                if (input.isEmpty()) {
                    resetComboBox();
                    return;
                }

                comboBox.hidePopup();
                comboBox.removeAllItems();

                // Filter and re-add matches
                Arrays.stream(unitsList)
                        .filter(unit -> unit.toLowerCase().contains(input.toLowerCase()))
                        .forEach(comboBox::addItem);

                comboBox.setSelectedItem(input);

                // Show popup safely
                SwingUtilities.invokeLater(() -> {
                    if (comboBox.isShowing() && comboBox.getItemCount() > 0) {
                        comboBox.showPopup();
                    }
                });
            }

            private void resetComboBox() {
                comboBox.removeAllItems();
                for (String unit : unitsList) {
                    comboBox.addItem(unit);
                }
                comboBox.setSelectedItem("");
            }

            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        comboBox.setSelectedItem(value != null ? value.toString() : "");
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
}
