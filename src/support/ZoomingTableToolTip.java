/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package support;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

/**
 *
 * @author Mihir
    Using ZoomingTableToolTip

            final ZoomingTableToolTip zttt = new ZoomingTableToolTip();
            zttt.setToolTipOn(true);

            final JInternalFrame jif = this;

            tableViewOnPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
            zttt.zoomInToolTipForTable(tableViewOnPanel, forTableScrollPaneOnPanel, jif, evt);
            }
            });
 */
public class ZoomingTableToolTip {

        private int last_row;
        private int last_col;
        private int fontSize;
        private JToolTip tt;
        private boolean on;
        private boolean interchange;
        private Color backgroundColor;
        private Color foreGroundColor;

        public ZoomingTableToolTip()
        {
            tt = null;
            on = false;
            interchange = false;
        }
        /***************
         * Edited by Aamir..
         * @return The Status of the Zooming..
         */
        public boolean isToolTipOn()
        {
            return this.on;
        }
        
        public void setToolTipOn(boolean on_off)
        {
            on = on_off;
        }

        public void setInterchangeColor(boolean on_off)
        {
            interchange = on_off;
        }

        public void setZoomingFontSize(int fontSize)
        {
            this.fontSize = fontSize;
        }

        public void setColors(Color foreGroundColor,Color backgroundColor)
        {
            this.foreGroundColor = foreGroundColor;
            this.backgroundColor = backgroundColor;
        }


        public void zoomInToolTipForTable(JTable table,JScrollPane ScrollPane,final Component container ,MouseEvent mouseevt)
        {

            if(fontSize==0)
            {
                fontSize = table.getFont().getSize()*2;
            }

            if( on )
            {
                    Point point = new Point(mouseevt.getX(), mouseevt.getY());

                    String tooltiptext = "";

                    if(table.getValueAt( table.rowAtPoint(point) , table.columnAtPoint(point))!=null)
                    {
                        tooltiptext = table.getValueAt( table.rowAtPoint(point) , table.columnAtPoint(point)).toString();
                    }
                    else
                    {
                        tooltiptext ="";
                    }

                    if(tt!=null&&(last_row!=table.rowAtPoint(point)||last_col!=table.columnAtPoint(point)))
                    {
                        tt.setVisible(false);
                        tt = null;
                        container.repaint();
                    }

                        tt = new JToolTip();

                        tt.setComponent(table);

                        tt.setTipText(tooltiptext);


                        Color tableCellColor = table.getBackground();
                        Color tableFontColor = table.getForeground();

                        if(backgroundColor==null)
                            tableCellColor = table.getBackground();
                        else
                            tableCellColor = backgroundColor;

                        if(foreGroundColor==null)
                            tableFontColor = table.getForeground();
                        else
                            tableFontColor = foreGroundColor;

                        Color newBackGroundColor = new Color( tableCellColor.getRed() , tableCellColor.getGreen(), tableCellColor.getBlue());
                        Color newForeGroundColor = new Color( tableFontColor.getRed() , tableFontColor.getGreen(), tableFontColor.getBlue());

                        if(interchange==false)
                        {
                            tt.setForeground( newForeGroundColor );
                            tt.setBackground( newBackGroundColor );
                        }
                        else
                        {
                            tt.setForeground( newBackGroundColor  );
                            tt.setBackground( newForeGroundColor );
                        }

                        Font currentfont = table.getFont();
                        Font font  = new Font( currentfont.getName() , currentfont.getStyle(), fontSize );

                        tt.setFont(font);

                        int width = (int) tt.getPreferredSize().getWidth();
                        int height = (int) tt.getPreferredSize().getHeight();

                        Rectangle rt = table.getCellRect(table.rowAtPoint(point), table.columnAtPoint(point), true);
                        Rectangle rt1 = table.getVisibleRect();

                        int x = (int) ((int) rt.getX() - rt1.getX()) ;
                        int y = (int) ((int) rt.getY() + table.getTableHeader().getHeight() - rt1.getY()) ;

                        tt.setBounds(x, y, width, height);
                        tt.setVisible(true);

                        ScrollPane.add(tt);

                        last_row = table.rowAtPoint(point);
                        last_col = table.columnAtPoint(point);


                        table.addMouseListener(new java.awt.event.MouseAdapter() {
                                    public void mouseExited(java.awt.event.MouseEvent evt) {

                                        if(tt!=null)
                                        {
                                            tt.setVisible(false);
                                            tt = null;
                                        }

                                        container.repaint();

                                    }
                                });

                        SwingUtilities.updateComponentTreeUI(tt);

            }
        }

}
