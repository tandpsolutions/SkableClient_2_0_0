/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author nice
 */
public class RightAlignment extends DefaultTableCellRenderer {

    public RightAlignment() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        //Get the status for the current row.
        //Return the JLabel which renders the cell.
        return l;
    }
}
