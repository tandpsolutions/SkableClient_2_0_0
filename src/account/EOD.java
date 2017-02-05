/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import masterView.SeriesMasterView;
import retrofitAPI.AccountAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;

/**
 *
 * @author LENOVO
 */
public class EOD extends javax.swing.JInternalFrame {

    /**
     * Creates new form StockValueStatement
     */
    Library lb = Library.getInstance();
    DefaultTableModel dtm = null;
    DefaultTableModel dtmDenomation = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    public EOD() {
        initComponents();
        setTypeCombo();
        setUpData();
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmDenomation = (DefaultTableModel) jTable2.getModel();
        lb.setDateChooserPropertyInit(jtxtFromDate);

    }

    private void setTypeCombo() {

        jComboBox1.setSelectedIndex(2);
    }

    private void setUpData() {
        jComboBox2.removeAllItems();
        jComboBox2.addItem("All");
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox2.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
        jComboBox2.setSelectedItem(SkableHome.selected_branch.getBranch_name());
        if (SkableHome.user_grp_cd.equalsIgnoreCase("1")) {
            jComboBox2.setEnabled(true);
        } else {
            jComboBox2.setEnabled(false);
        }
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

    private void makeQuery() {
        try {
            jPanel1.removeAll();
            jPanel1.add(jScrollPane1);
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class
            );

            JsonObject call = accountAPI.EOD(jComboBox1.getSelectedIndex(), lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                    lb.ConvertDateFormetForDB(jtxtFromDate.getText()), ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_cd() : "0")).execute().body();

            lb.addGlassPane(this);

            JsonObject result = call;
            if (result.get("result").getAsInt() == 1) {
                JsonArray array = call.getAsJsonArray("data");
                dtm.setRowCount(0);
                for (int i = 0; i < array.size(); i++) {
                    Vector row = new Vector();

                    row.add("CASH");
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("cash").getAsDouble()));
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("BANK");
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bank").getAsDouble()));
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("CARD");
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("card").getAsDouble()));
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("BAJAJ");
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bajaj").getAsDouble()));
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("BUYBACK");
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("buy_back").getAsDouble()));
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("DEBTORS");
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("net_amt").getAsDouble()
                            - array.get(i).getAsJsonObject().get("cash").getAsDouble()
                            - array.get(i).getAsJsonObject().get("card").getAsDouble()
                            - array.get(i).getAsJsonObject().get("bajaj").getAsDouble()
                            - array.get(i).getAsJsonObject().get("bank").getAsDouble()));
                    dtm.addRow(row);
                }

                {
                    call = accountAPI.DailyCashStatement(lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                            lb.ConvertDateFormetForDB(jtxtFromDate.getText()), jComboBox2.getSelectedIndex()).execute().body();
                    if (call != null) {
                        result = call;
                        if (result.get("result").getAsInt() == 1) {
                            array = call.getAsJsonArray("data");
                            if (array.size() > 0) {
                                for (int i = 0; i < array.size(); i++) {
                                    Vector row = new Vector();
                                    row.add("CASH RECEIPT");
                                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("dr_bal").getAsDouble()));
                                    dtm.addRow(row);

                                    row = new Vector();
                                    row.add("CASH PAYMENT");
                                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("cr_bal").getAsDouble()));
                                    dtm.addRow(row);
                                }
                            } else {
                                Vector row = new Vector();
                                row.add("CASH RECEIPT");
                                row.add(" ");
                                dtm.addRow(row);

                                row = new Vector();
                                row.add("CASH PAYMENT");
                                row.add(" ");
                                dtm.addRow(row);
                            }

                        }
                    }
                }

                {
                    call = accountAPI.DailyBankSummary(lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                            lb.ConvertDateFormetForDB(jtxtFromDate.getText()), jComboBox2.getSelectedIndex()).execute().body();
                    if (call != null) {
                        result = call;
                        if (result.get("result").getAsInt() == 1) {
                            array = call.getAsJsonArray("data");
                            if (array.size() > 0) {
                                for (int i = 0; i < array.size(); i++) {
                                    Vector row = new Vector();
                                    row.add("BANK");
                                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bal").getAsDouble()));
                                    dtm.addRow(row);
                                }
                            } else {
                                Vector row = new Vector();
                                row.add("Bank");
                                row.add(" ");
                                dtm.addRow(row);
                            }
                        }

                    }
                }

                dtmDenomation.setRowCount(0);
                Vector row = new Vector();
                row.add("1000");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                row = new Vector();
                row.add("500");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                row = new Vector();
                row.add("100");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                row = new Vector();
                row.add("50");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                row = new Vector();
                row.add("20");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                row = new Vector();
                row.add("10");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                row = new Vector();
                row.add("Accessory");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                row = new Vector();
                row.add("2000");
                row.add(" ");
                row.add(" ");
                dtmDenomation.addRow(row);

                call = accountAPI.GetDenomation(lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                        ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_cd() : "0")).execute().body();
                result = call;
                if (result.get("result").getAsInt() == 1) {
                    array = call.getAsJsonArray("data");
                    for (int i = 0; i < array.size(); i++) {
                        double rate = lb.isNumber(jTable2.getValueAt(array.get(i).getAsJsonObject().get("note_cd").getAsInt(), 0).toString());
                        if (rate == 0) {
                            rate = 1;
                        }
                        double qty = lb.isNumber(array.get(i).getAsJsonObject().get("qty").getAsString());
                        jTable2.setValueAt(array.get(i).getAsJsonObject().get("qty").getAsString(), array.get(i).getAsJsonObject().get("note_cd").getAsInt(), 1);
                        jTable2.setValueAt(lb.Convert2DecFmtForRs(rate * qty), array.get(i).getAsJsonObject().get("note_cd").getAsInt(), 2);
                    }
                } else {
                    for (int i = 0; i < jTable2.getRowCount(); i++) {
                        jTable2.setValueAt("", i, 1);
                    }
                }

                double total = 0.00;
                double total_note = 0.00;
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    double qty = (lb.isNumber(jTable2.getValueAt(i, 0).toString()) == 0) ? 1 : lb.isNumber(jTable2.getValueAt(i, 0).toString());
                    total += qty * lb.isNumber(jTable2.getValueAt(i, 1).toString());
                    total_note += lb.isNumber(jTable2.getValueAt(i, 1).toString());
                }
                row = new Vector();
                row.add("Total");
                row.add(total_note);
                row.add(lb.Convert2DecFmtForRs(total));
                dtmDenomation.addRow(row);

                double total1 = 0.00;
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    total1 += lb.isNumber(jTable1.getValueAt(i, 1).toString());
                }
                row = new Vector();
                row.add("Total");
                row.add(lb.Convert2DecFmtForRs(total1));
                dtm.addRow(row);
                lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                lb.removeGlassPane(EOD.this);
            } else {
                lb.removeGlassPane(EOD.this);
                lb.showMessageDailog(call.get("Cause").getAsString());
            }

        } catch (Exception ex) {
            lb.printToLogFile("Exception at stockValue Statement", ex);
            lb.removeGlassPane(EOD.this);
        }
    }

    public void updateMOP(int row, int qty) {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class
            );
            JsonObject call = accountAPI.updateDenomation(lb.ConvertDateFormetForDB(jtxtFromDate.getText()), ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_cd() : "0"), row, qty).execute().body();
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    lb.showMessageDailog("update successfully");

                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SeriesMasterView.class.getName()).log(Level.SEVERE, null, ex);
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
        jPanel2 = new javax.swing.JPanel();
        jtxtFromDate = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jBillDateBtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jtxtFromDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusLost(evt);
            }
        });
        jtxtFromDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDateKeyPressed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Retail", "Tax", "All" }));

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jLabel5.setText("Voucher Type");

        jButton4.setText("Print");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel3.setText("From Date");

        jButton3.setText("Close");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jbtnView.setText("View ");
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

        jLabel6.setText("Branch");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(186, 186, 186)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jButton3)
                        .addComponent(jButton4))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jComboBox2, jLabel5, jLabel6});

        jPanel3.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Note", "Amount", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable2KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
        }

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        // TODO add your handling code here:
        makeQuery();
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        HashMap params = new HashMap<Object, Object>();
        params.put("cash", lb.isNumber(jTable1.getValueAt(0, 1).toString()));
        params.put("bank", lb.isNumber(jTable1.getValueAt(1, 1).toString()));
        params.put("card", lb.isNumber(jTable1.getValueAt(2, 1).toString()));
        params.put("bajaj", lb.isNumber(jTable1.getValueAt(3, 1).toString()));
        params.put("buyback", lb.isNumber(jTable1.getValueAt(4, 1).toString()));
        params.put("debtors", lb.isNumber(jTable1.getValueAt(5, 1).toString()));
        params.put("Cash_Receipt", lb.isNumber(jTable1.getValueAt(6, 1).toString()));
        params.put("Cash_Payment", lb.isNumber(jTable1.getValueAt(7, 1).toString()));
        params.put("Bank", lb.isNumber(jTable1.getValueAt(8, 1).toString()));
        params.put("Total", lb.isNumber(jTable1.getValueAt(9, 1).toString()));

        params.put("branch", ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_name() : "All Branch"));

        params.put("thousand", lb.isNumber(jTable2.getValueAt(0, 1).toString()));
        params.put("five", lb.isNumber(jTable2.getValueAt(1, 1).toString()));
        params.put("hundred", lb.isNumber(jTable2.getValueAt(2, 1).toString()));
        params.put("fifty", lb.isNumber(jTable2.getValueAt(3, 1).toString()));
        params.put("twenty", lb.isNumber(jTable2.getValueAt(4, 1).toString()));
        params.put("ten", lb.isNumber(jTable2.getValueAt(5, 1).toString()));
        params.put("acc", lb.isNumber(jTable2.getValueAt(6, 1).toString()));
        params.put("twothousand", lb.isNumber(jTable2.getValueAt(7, 1).toString()));

        params.put("thousand_1", lb.isNumber(jTable2.getValueAt(0, 2).toString()));
        params.put("five_1", lb.isNumber(jTable2.getValueAt(1, 2).toString()));
        params.put("hundred_1", lb.isNumber(jTable2.getValueAt(2, 2).toString()));
        params.put("fifty_1", lb.isNumber(jTable2.getValueAt(3, 2).toString()));
        params.put("twenty_1", lb.isNumber(jTable2.getValueAt(4, 2).toString()));
        params.put("ten_1", lb.isNumber(jTable2.getValueAt(5, 2).toString()));
        params.put("acc_1", lb.isNumber(jTable2.getValueAt(6, 2).toString()));
        params.put("two_thousand_1", lb.isNumber(jTable2.getValueAt(7, 2).toString()));

        params.put("total_1", lb.isNumber(jTable2.getValueAt(8, 2).toString()));
        params.put("dir", System.getProperty("user.dir"));
        lb.reportGeneratorPDF("denomation.jasper", params, null, "");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jtxtFromDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusGained
        // TODO add your handling code here:
        jtxtFromDate.selectAll();
    }//GEN-LAST:event_jtxtFromDateFocusGained

    private void jtxtFromDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusLost
        // TODO add your handling code here:
        try {
            if (jtxtFromDate.getText().contains("/")) {
                jtxtFromDate.setText(jtxtFromDate.getText().replace("/", ""));
            }
            if (jtxtFromDate.getText().length() == 8) {
                String temp = jtxtFromDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtFromDate.setText(setDate);
            }
            //            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtFromDate.getText().trim()))) != null) {
            //                jtxtToDate.requestFocusInWindow();
            //            }

        } catch (Exception ex) {
            //            navLoad.jlblMsg.setText("Enter Correct Date");
            jtxtFromDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtFromDateFocusLost

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jbtnView.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFromDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFromDate.getX(), jtxtFromDate.getY() + 125, jtxtFromDate.getX() + odc.getWidth(), jtxtFromDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                DailySalesStatementDetail dsd = new DailySalesStatementDetail(jtxtFromDate.getText(),
                        jtxtFromDate.getText(), jComboBox1.getSelectedIndex(), jComboBox2.getSelectedItem().toString());
                SkableHome.addOnScreen(dsd, "Daily Sales Statement Detail");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnView);
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jTable2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                int row = jTable2.getSelectedRow();
                double rate = lb.isNumber(JOptionPane.showInputDialog("Enter QTY"));
                {
                    double note = lb.isNumber(jTable2.getValueAt(row, 0).toString());
                    if (row != -1) {
                        jTable2.setValueAt(rate, row, 1);
                        jTable2.setValueAt(lb.Convert2DecFmtForRs(rate * note), row, 2);
                        updateMOP(row, (int) rate);
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable2KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtFromDate;
    // End of variables declaration//GEN-END:variables
}
