package AddingStocks;


import orderlistspanelsSYSTEM.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


    // ------------------ QUANTITY CELL RENDERER ------------------
    public class QuantityCellRenderer extends JPanel implements TableCellRenderer {
        private final JLabel lbl = new JLabel();

        public QuantityCellRenderer() {
            setLayout(new BorderLayout());
            add(lbl, BorderLayout.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            lbl.setText(value != null ? value.toString() : "0");
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            lbl.setHorizontalAlignment(JLabel.CENTER);
            return this;
        }
    }