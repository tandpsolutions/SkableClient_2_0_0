/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import com.lamatek.swingextras.JDateChooser;
import java.awt.Component;
import java.text.SimpleDateFormat;
import javax.swing.JTextField;

/**
 *
 * @author nice
 */
public class OurDateChooser extends JDateChooser {

    private Component nextFocusComponent = null;
    private String format = null;
    private int OpenFlag = 0;

    @Override
    public void acceptSelection() {
        OpenFlag = 0;
        nextFocusComponent.requestFocusInWindow();
        ((JTextField) nextFocusComponent).setText(new SimpleDateFormat(format).format(getSelectedDate().getTime()));
    }

    @Override
    public void cancelSelection() {
        OpenFlag = 0;
        if (((JTextField) nextFocusComponent).getText().trim().equalsIgnoreCase("")) {
            ((JTextField) nextFocusComponent).setText("Select Date");
        }
    }

    public void setnextFocus(Component c) {
        this.nextFocusComponent = c;
    }

    public void setFormat(String d) {
        this.format = d;
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y); //To change body of generated methods, choose Tools | Templates.
    }

    public void setOpenFlag(int status) {
        this.OpenFlag = status;
    }

    public int getOpenFlag() {
        return this.OpenFlag;
    }
}