package medicinespanelsSYSTEM;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class DateChooserCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JDateChooser dateChooser = new JDateChooser();
    private JPanel panel = new JPanel(new BorderLayout());

    public DateChooserCellEditor() {
        dateChooser.setDateFormatString("yyyy-MM-dd");
        panel.add(dateChooser, BorderLayout.CENTER);
    }

    @Override
    public Object getCellEditorValue() {
        Date selectedDate = dateChooser.getDate();
        if (selectedDate != null) {
            return new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
        }
        return "";
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        try {
            if (value != null && !value.toString().isEmpty()) {
                dateChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(value.toString()));
            } else {
                dateChooser.setDate(null);
            }
        } catch (Exception e) {
            dateChooser.setDate(null);
        }
        return panel;
    }
}
