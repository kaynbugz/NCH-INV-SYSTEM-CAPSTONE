package AddingStocks;


import orderlistspanelsSYSTEM.*;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JTextField txtQty = new JTextField("0", 3);
    private final JButton btnPlus;
    private final JButton btnMinus;

    public QuantityCellEditor() {
        txtQty.setHorizontalAlignment(JTextField.CENTER); // ✅ center input text

        // Load plus/minus icons
        btnPlus = new JButton(new ImageIcon(getClass().getResource("/nch/addpo.png")));
        btnMinus = new JButton(new ImageIcon(getClass().getResource("/nch/minuspo.png")));

        // Set white background and remove borders
        btnPlus.setBackground(Color.WHITE);
        btnMinus.setBackground(Color.WHITE);
        btnPlus.setOpaque(true);
        btnMinus.setOpaque(true);
        btnPlus.setBorderPainted(false);
        btnMinus.setBorderPainted(false);

        // Optional: size adjustment
        Dimension btnSize = new Dimension(20, 25);
        btnPlus.setPreferredSize(btnSize);
        btnMinus.setPreferredSize(btnSize);

        // Group buttons in a panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 2, 0));
        buttonPanel.add(btnMinus);
        buttonPanel.add(btnPlus);

        panel.setLayout(new BorderLayout(2, 0));
        panel.add(txtQty, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        // Button logic
        btnPlus.addActionListener(e -> {
            try {
                int value = Integer.parseInt(txtQty.getText().trim());
                txtQty.setText(String.valueOf(value + 1));
            } catch (NumberFormatException ignored) {
                txtQty.setText("1");
            }
        });

        btnMinus.addActionListener(e -> {
            try {
                int value = Integer.parseInt(txtQty.getText().trim());
                if (value > 0) txtQty.setText(String.valueOf(value - 1));
            } catch (NumberFormatException ignored) {
                txtQty.setText("0");
            }
        });

        // Stop editing when Enter pressed or focus lost
        txtQty.addActionListener(e -> fireEditingStopped());
        txtQty.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        txtQty.setText(value != null ? value.toString() : "0");
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return txtQty.getText().trim();
    }
}
