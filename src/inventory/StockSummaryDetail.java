/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package inventory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import model.SeriesHead;
import model.SeriesMaster;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.InventoryAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author nice
 */
public class StockSummaryDetail extends javax.swing.JInternalFrame {

    /**
     * Creates new form salesRegisterSummary
     */
    Library lb = null;
    DefaultTableModel dtm = null;
    private String sr_cd;
    private ReportTable viewTable = null;
    private JPopupMenu popup = new JPopupMenu();

    public StockSummaryDetail() {
        initComponents();
        lb = Library.getInstance();
        registerShortKeys();
        dtm = (DefaultTableModel) jTable1.getModel();
        setPermission();
        tableForView();
    }

    private void registerShortKeys() {

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jbtnClose.doClick();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public StockSummaryDetail(String prd_name, String sr_cd) {
        initComponents();
        lb = Library.getInstance();
        dtm = (DefaultTableModel) jTable1.getModel();
        setPermission();
        jtxtProductName.setText(prd_name);
        this.sr_cd = sr_cd;
        jbtnViewActionPerformedRoutine();
        registerShortKeys();
        tableForView();
    }

    private void setPermission() {
//        jbtnPreview.setEnabled(UserPermission.getRight("426", "PRINT"));
    }

    @Override
    public void dispose() {
        try {
            SkableHome.removeFromScreen(SkableHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
//            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    private void jbtnViewActionPerformedRoutine() {

        InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class);

        lb.addGlassPane(this);
        Call<JsonObject> call = inventoryAPI.GetStockSummaryDetail(sr_cd);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(StockSummaryDetail.this);
                if (rspns.isSuccessful()) {

                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        dtm.setRowCount(0);
                        double opb = 0.00, pur = 0.00, sal = 0.00, stock = 0.00;
                        for (int i = 0; i < array.size(); i++) {
                            Vector row = new Vector();

                            row.add(array.get(i).getAsJsonObject().get("Month").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("opening").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("purchase").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("sales").getAsDouble());
                            opb = array.get(i).getAsJsonObject().get("opening").getAsDouble();
                            pur = array.get(i).getAsJsonObject().get("purchase").getAsDouble();
                            sal = array.get(i).getAsJsonObject().get("sales").getAsDouble();
                            stock = array.get(i).getAsJsonObject().get("opening").getAsDouble() + array.get(i).getAsJsonObject().get("purchase").getAsDouble() - array.get(i).getAsJsonObject().get("sales").getAsDouble();
                            row.add(stock);
                            dtm.addRow(row);
                        }

                        Vector row = new Vector();
                        row.add(" ");
                        row.add(" ");
                        row.add(" ");
                        row.add(" ");
                        row.add(" ");
                        dtm.addRow(row);

                        row = new Vector();
                        row.add("Total");
                        row.add(opb);
                        row.add(pur);
                        row.add(sal);
                        row.add(stock);
                        dtm.addRow(row);

                        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                        lb.removeGlassPane(StockSummaryDetail.this);
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                        lb.removeGlassPane(StockSummaryDetail.this);
                    }
                } else {
                    lb.showMessageDailog(rspns.message());

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(StockSummaryDetail.this);
            }
        }
        );
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    private void setSeriesData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
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
                                    jtxtProductName.setText(viewTable.getValueAt(row, 1).toString());
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
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

        jbtnView = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jtxtProductName = new javax.swing.JTextField();

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

        jbtnPreview.setText("Preview");
        jbtnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPreviewActionPerformed(evt);
            }
        });
        jbtnPreview.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPreviewKeyPressed(evt);
            }
        });

        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });
        jbtnClose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnCloseKeyPressed(evt);
            }
        });

        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        jTable1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Month", "Opening", "Purchase", "Sales", "Balance"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        jLabel1.setText("Product Name");

        jtxtProductName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtProductNameFocusGained(evt);
            }
        });
        jtxtProductName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtProductNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtProductNameKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnView)
                    .addComponent(jbtnPreview)
                    .addComponent(jbtnClose))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jbtnClose, jbtnPreview, jbtnView, jtxtProductName});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        // TODO add your handling code here:
        jPanel1.removeAll();
        jPanel1.add(jScrollPane1);
        jScrollPane1.setVisible(true);
        jbtnViewActionPerformedRoutine();
        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnView.doClick();
        }
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        // TODO add your handling code here:
        //        generalLedgerPreview();
//        jbtnPreviewActionPerformedRoutine();
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
//            jbtnPreviewActionPerformedRoutine();
        }
    }//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
            this.dispose();
        }
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && jTable1.getSelectedRow() != -1) {
            String fromDate = "", toDate = "";
            if (jTable1.getSelectedRow() < 9) {
                fromDate = "01/" + lb.getMonth(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString(), "C") + "/" + 2015;
                toDate = "31/" + lb.getMonth(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString(), "C") + "/" + 2015;
            } else {
                fromDate = "01/" + lb.getMonth(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString(), "C") + "/" + (int) (lb.isNumber2("2015") + 1);
                toDate = "31/" + lb.getMonth(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString(), "C") + "/" + (int) (lb.isNumber2("2015") + 1);
            }
            StockLedger stk = new StockLedger(jtxtProductName.getText(), fromDate, toDate, sr_cd);
            SkableHome.addOnScreen(stk, "Item Ledger");
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

    private void jtxtProductNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtProductNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtProductNameFocusGained

    private void jtxtProductNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtProductNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            setSeriesData("3", jtxtProductName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtProductNameKeyPressed

    private void jtxtProductNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtProductNameKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_jtxtProductNameKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtProductName;
    // End of variables declaration//GEN-END:variables
}
