/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package skable;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Piyush Ramani
 */
public class CursorGlassPane extends JPanel implements MouseListener,MouseMotionListener
{    
    private String message = "Please Wait for Result.........";
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 10;
    private static final Color TEXT_COLOR = new Color(0x333333);      
    private Cursor cursor=new Cursor(Cursor.WAIT_CURSOR);
    private JLabel jl=null;
    public CursorGlassPane()
    {
        setBackground(Color.WHITE);
        setFont(new Font("Default", Font.BOLD, 32));
        ImageIcon icon = new ImageIcon("Resources/Images/loadgrey_loader[1].gif");
        jl=new JLabel(icon);
        setLayout(new BorderLayout());
        this.add(jl,BorderLayout.CENTER);
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // gets the current clipping area
        Rectangle clip = g.getClipBounds();

        // sets a 40% translucent composite
        AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.40f);
        Composite composite = g2.getComposite();
        g2.setComposite(alpha);

        // fills the background
        g2.setColor(getBackground());
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);

        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - BAR_WIDTH) / 2;
        int y = (getHeight() - BAR_HEIGHT - metrics.getDescent()) / 2;

        // draws the text
        g2.setColor(TEXT_COLOR);
        g2.drawString(message, x-100, y-50);

        g2.setComposite(composite);
        
    }

    @Override
    public void setCursor(Cursor cursor) {        
        super.setCursor(cursor);

    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void setVisible(boolean aFlag) {
        if(aFlag)
        this.requestFocusInWindow();
        super.setVisible(aFlag);
    }


    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

   

    


}
