/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StockLedger.java
 *
 * Created on Oct 16, 2012, 12:58:30 PM
 */
package inventory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.SeriesHead;
import model.SeriesMaster;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.InventoryAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author nice
 */
public class TagTractQtyWise extends javax.swing.JInternalFrame {

    Library lb = Library.getInstance();
    private DefaultTableModel dtm = null;
    private DefaultTableModel dtmBoth = null;
    private double clb = 0.0;
    private double clbNet = 0.0;
    private ReportTable viewTable = null;
    private String sr_cd;
//    private CachedRowSetAdapter crsa = new CachedRowSetAdapter();
//    JasperReport report = null;

    String Syspath = System.getProperty("user.dir");
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    private JPopupMenu popup = new JPopupMenu();

    /**
     * Creates new form StockLedger
     */
    public TagTractQtyWise() {
        initComponents();
        initOther();
    }

    public TagTractQtyWise(String prd_name, String fromDate, String toDate, String sr_cd) {
        initComponents();
        initOther();
        this.sr_cd = sr_cd;
        jtxtPrdName.setText(prd_name);
        jtxtFromDate.setText(fromDate);
        jtxtToDate.setText(toDate);
        jbtnView.doClick();
    }

    private void searchOnTextFields() {
        this.rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        panel.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        panel.add(jtfFilter, BorderLayout.CENTER);

//        setLayout(new BorderLayout());
//        add(panel, BorderLayout.SOUTH);
//        add(new JScrollPane(jTable1), BorderLayout.CENTER);
        jtfFilter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
    }

    private void initOther() {
        dtm = (DefaultTableModel) jTable1.getModel();
        registerShortKeys();
        lb.setDateChooserPropertyInitStart(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
        tableForView();
        searchOnTextFields();
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    @Override
    public void dispose() {
        try {
            SkableHome.removeFromScreen(SkableHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            l.setHorizontalAlignment(SwingConstants.RIGHT);
            Double d = null;
            try {
                d = Double.parseDouble(lb.getDeCustomFormat((l.getText().equalsIgnoreCase("") ? "0.00" : l.getText())));
            } catch (Exception ex) {
                d = 0.00;
                lb.printToLogFile("Error at getTableCell in stock ledger", ex);
            }
            l.setText(lb.Convert2DecFmt(d));
            return l;

        }
    }

    private void registerShortKeys() {
        KeyStroke dateKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.ALT_MASK, false);
        Action dateKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxtFromDate.requestFocusInWindow();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(dateKeyStroke, "Date");
        getRootPane().getActionMap().put("Date", dateKeyAction);

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jbtnClose.doClick();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    private boolean validateData() {
        boolean flag = true;
        if (flag) {
            flag = lb.checkFinancialDate(jtxtFromDate);
            if (!flag) {
                JOptionPane.showMessageDialog(this, "Invalid From Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtFromDate.requestFocusInWindow();
            }
        }
        if (flag) {
            flag = lb.checkFinancialDate(jtxtToDate);
            if (!flag) {
                JOptionPane.showMessageDialog(this, "Invalid To Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtToDate.requestFocusInWindow();
            }
        }
        return flag;
    }

    private void setSeriesData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(TagTractQtyWise.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        SeriesHead header = (SeriesHead) new Gson().fromJson(response.body(), SeriesHead.class);
                        if (header.getResult() == 1) {
                            final SelectDailog sa = new SelectDailog(null, true);
                            sa.setData(viewTable);
                            sa.setLocationRelativeTo(null);
                            ArrayList<SeriesMaster> series = (ArrayList<SeriesMaster>) header.getAccountHeader();
                            sa.getDtmHeader().setRowCount(0);
                            for (int i = 0; i < series.size(); i++) {
                                Vector row = new Vector();
                                row.add(series.get(i).getSRCD());
                                row.add(series.get(i).getSRNAME());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    sr_cd = viewTable.getValueAt(row, 0).toString();
                                    jtxtPrdName.setText(viewTable.getValueAt(row, 1).toString());
                                    jbtnView.requestFocusInWindow();
                                }
                                sa.dispose();
                            }
                        } else {
                            lb.showMessageDailog(header.getCause().toString());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(TagTractQtyWise.this);
                }
            }
            );
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jtxtPrdName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jbtnView = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jtxtFromDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jtxtToDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        panel = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setTitle("Stock Ledger");

        jPanel1.setLayout(new java.awt.CardLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No", "Tag No", "Qty"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
        }

        jPanel1.add(jScrollPane1, "card2");

        jLabel2.setText("Product Name");

        jtxtPrdName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPrdNameKeyPressed(evt);
            }
        });

        jLabel3.setText("From Date");

        jLabel4.setText("To Date");

        jbtnView.setText("View Result");
        jbtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewActionPerformed(evt);
            }
        });
        jbtnView.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnViewKeyPressed(evt);
            }
        });

        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        jtxtFromDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusGained(evt);
            }
        });
        jtxtFromDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDateKeyPressed(evt);
            }
        });

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jtxtToDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtToDateFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtToDateFocusGained(evt);
            }
        });
        jtxtToDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtToDateKeyPressed(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 369, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxtPrdName, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 369, Short.MAX_VALUE)))
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnView});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jbtnClose))
                    .addComponent(jtxtPrdName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtPrdName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jBillDateBtn1, jLabel3, jLabel4, jtxtFromDate, jtxtToDate});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
    InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class
    );
    lb.addGlassPane(this);
    Call<JsonObject> call = inventoryAPI.TagTrackQty(sr_cd, lb.ConvertDateFormetForDB(jtxtFromDate.getText()), lb.ConvertDateFormetForDB(jtxtToDate.getText()));

    call.enqueue(new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
            lb.removeGlassPane(TagTractQtyWise.this);

            if (rspns.isSuccessful()) {
                JsonObject result = rspns.body();
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = rspns.body().getAsJsonArray("data");
                    dtm.setRowCount(0);
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(i + 1);
                        row.add(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("QTY").getAsString());
                        dtm.addRow(row);
                    }
                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(TagTractQtyWise.this);
                } else {
                    lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    lb.removeGlassPane(TagTractQtyWise.this);
                }
            } else {
                lb.showMessageDailog(rspns.message());
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
            lb.removeGlassPane(TagTractQtyWise.this);

        }
    }
    );

}//GEN-LAST:event_jbtnViewActionPerformed

private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
// TODO add your handling code here:
    this.dispose();
}//GEN-LAST:event_jbtnCloseActionPerformed

private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
// TODO add your handling code here:
    lb.enterClick(evt);

}//GEN-LAST:event_jbtnViewKeyPressed

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtToDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtToDate.getX() - 50, jtxtToDate.getY() + 125, jtxtToDate.getX() + odc.getWidth(), jtxtToDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (lb.checkDate(jtxtToDate)) {
                jbtnView.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid To Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtToDate.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtToDateKeyPressed

    private void jtxtToDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusGained
        // TODO add your handling code here:
        jtxtToDate.selectAll();
    }//GEN-LAST:event_jtxtToDateFocusGained

    private void jtxtToDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusLost
        // TODO add your handling code here:
        lb.checkDate(jtxtToDate);
    }//GEN-LAST:event_jtxtToDateFocusLost

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFromDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFromDate.getX(), jtxtToDate.getY() + 125, jtxtFromDate.getX() + odc.getWidth(), jtxtFromDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (lb.checkDate(jtxtFromDate)) {
                jtxtToDate.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid From Date", "", JOptionPane.WARNING_MESSAGE);
                jtxtFromDate.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jtxtFromDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusGained
        // TODO add your handling code here:
        jtxtFromDate.selectAll();
    }//GEN-LAST:event_jtxtFromDateFocusGained

    private void jtxtFromDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusLost
        lb.checkDate(jtxtFromDate);
    }//GEN-LAST:event_jtxtFromDateFocusLost

    private void jtxtPrdNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPrdNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            setSeriesData("3", jtxtPrdName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtPrdNameKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                TagTrack tt = new TagTrack(jTable1.getValueAt(row, 1).toString());
                SkableHome.addOnScreen(tt, "Tag Track");
            }
        }
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            popup.setVisible(false);
            popup.removeAll();
            ActionListener menuListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    popup.setVisible(false);
                    int row = jTable1.getSelectedRow();
                    int column = jTable1.getSelectedColumn();
                    if (row != -1 && column != -1) {
                        String selection = jTable1.getValueAt(row, column).toString();
                        StringSelection data = new StringSelection(selection);
                        Clipboard clipboard
                                = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(data, data);
                    }
                }
            };
            final JMenuItem item;
            popup.add(item = new JMenuItem("COPY"));
            item.setHorizontalTextPosition(JMenuItem.RIGHT);
            item.addActionListener(menuListener);
            popup.setLocation(MouseInfo.getPointerInfo().getLocation());
            popup.setVisible(true);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtPrdName;
    private javax.swing.JTextField jtxtToDate;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
