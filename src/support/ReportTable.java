/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package support;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrator
 */
public class ReportTable extends JTable
{
    HashMap headerName = new HashMap();
    HashMap columnSize = new HashMap();
    //HashMap columnRenderer = new HashMap();
    //JTable tableViewOnPanel = null;
    JScrollPane tableScrollPane = null;
    Object[] header = null;
    Class str[] = null;
    ArrayList<Class> classNameArray = new ArrayList<Class>();
    ArrayList<DefaultTableCellRenderer> columnRenderer = new ArrayList<DefaultTableCellRenderer>();
    ArrayList<Boolean> editFldArray = new ArrayList<Boolean>();
    JPanel reportPanel = null;
    private Library bLib = Library.getInstance();
    int iDefaultWidth = 0;
    int[] associateColumn;


    public ReportTable()
    {
        super();
    }

    public ReportTable(JPanel reportPanel)
    {
        super();
        this.reportPanel = reportPanel;
    }
    


    public void AddColumn(int iIndex,String colName,int Size,Class column,DefaultTableCellRenderer Renderer,boolean bFlag)
    {
        headerName.put(iIndex, colName);
        columnSize.put(iIndex, Size);
        classNameArray.add(column);
        columnRenderer.add(Renderer);
        editFldArray.add(bFlag);
    }

    public void setColumnValue(int[] columnNumber)
    {        
        this.associateColumn = columnNumber;
    }

    public int[] getColumnValue()
    {
        return associateColumn;
    }
    
    private void makeArray()
    {
        Vector rows = new Vector();
        for(int i=0;i<headerName.size();i++)
        {
            rows.add(headerName.get(i).toString());
        }
        header = rows.toArray();
    }

    public void SetTableProperty(JTable tableView,boolean bSorter,boolean bResize)
    {
        tableView.getTableHeader().setFont(new Font("ARIAL",1,12));
        tableView.setFont(new Font("ARIAL",1,12));
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(JLabel.CENTER);
        tableView.getTableHeader().setDefaultRenderer(dtcr);
        tableView.setAutoCreateRowSorter(bSorter);
        tableView.getTableHeader().setResizingAllowed(bResize);
    }

    public void makeDefaultSize(JPanel panel)
    {
        int width = 0;
        int widthLocal = 0;
        int iDefault = 0;
        for(int i=0;i<columnSize.size();i++)
        {
            widthLocal = Integer.valueOf(columnSize.get(i).toString());
            if(widthLocal > 0)
            {
                width += Integer.valueOf(columnSize.get(i).toString());
            }
            else
            {
                iDefault++;
            }
        }
        if(iDefault > 0)
        {
            //iDefaultWidth = ((panel.getPreferredSize().width - width -12) / iDefault);

            //In some form this value Return zero so culumn can not visible in report...
            //Ex. Pending Collection....29/09/2009
            iDefaultWidth = ((panel.getWidth() - width - 4) / iDefault);
        }
    }

    public void makeTable()
    {
        makeArray();

        setModel(new DefaultTableModel(
            new Object [][] {
                
            },
            header
        ) {
            public Class getColumnClass(int columnIndex) {
                return classNameArray.get(columnIndex);
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return editFldArray.get(columnIndex);
            }
        });

        tableScrollPane = new JScrollPane(this);
        tableScrollPane.setViewportView(this);
        tableScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        getTableHeader().setReorderingAllowed(false);

        for(int i=0;i<columnSize.size();i++)
        {
            getColumnModel().getColumn(i).setCellRenderer(columnRenderer.get(i));
        }
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        Blib.SetTableProperty(this,true,false);
    }

    public void setwidthTable()
    {
        for(int i=0;i<columnSize.size();i++)
        {
            if(Integer.valueOf(columnSize.get(i).toString()) != -1)
            {
                getColumnModel().getColumn(i).setMinWidth(Integer.valueOf(columnSize.get(i).toString()));
                getColumnModel().getColumn(i).setPreferredWidth(Integer.valueOf(columnSize.get(i).toString()));
                getColumnModel().getColumn(i).setMaxWidth(Integer.valueOf(columnSize.get(i).toString()));
            }
            else
            {
                getColumnModel().getColumn(i).setMinWidth(iDefaultWidth);
                getColumnModel().getColumn(i).setPreferredWidth(iDefaultWidth);
                getColumnModel().getColumn(i).setMaxWidth(iDefaultWidth);
            }
        }
    }

    public void addTable(JPanel panel)
    {
//        makeDefaultSize(panel);
//        setwidthTable();
        panel.add(tableScrollPane);
    }


}
