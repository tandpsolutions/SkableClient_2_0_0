/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import model.AccountHead;
import retrofitAPI.AccountAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import transactionController.SelectAccount;

/**
 *
 * @author bhaumik
 */
public class ListBill extends javax.swing.JInternalFrame {

    Library lb = Library.getInstance();
    DefaultTableModel dtmDr = null;
    DefaultTableModel dtmCr = null;
    DefaultTableModel dtmAdjsted = null;
    String ac_cd = "";

    /**
     * Creates new form ListBill
     */
    public ListBill() {
        initComponents();
        dtmDr = (DefaultTableModel) jTable1.getModel();
        dtmCr = (DefaultTableModel) jTable2.getModel();
        dtmAdjsted = (DefaultTableModel) jTable3.getModel();
    }

    private void setAccountDetailMobile(String param_cd, String value) {
        try {
            JsonObject call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase()).execute().body();

            if (call != null) {
                System.out.println(call.toString());
                AccountHead header = (AccountHead) new Gson().fromJson(call, AccountHead.class);
                if (header.getResult() == 1) {
                    SelectAccount sa = new SelectAccount(null, true);
                    sa.setLocationRelativeTo(null);
                    sa.fillData((ArrayList) header.getAccountHeader());
                    sa.setVisible(true);
                    if (sa.getReturnStatus() == SelectAccount.RET_OK) {
                        int row = sa.row;
                        if (row != -1) {
                            ac_cd = header.getAccountHeader().get(row).getACCD();
                            jtxtAcAlias.setText(ac_cd);
                            jtxtAcName.setText(header.getAccountHeader().get(row).getFNAME());
                            jButton1.requestFocusInWindow();
                        }
                    }
                } else {
                    lb.showMessageDailog(header.getCause().toString());
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void jbtnViewActionPerformedRoutineCR() {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);

            JsonObject call = accountAPI.ListBills(ac_cd, false).execute().body();

            lb.addGlassPane(this);

            System.out.println(call.toString());
            lb.removeGlassPane(ListBill.this);
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = result.getAsJsonArray("data");
                    dtmCr.setRowCount(0);
                    double tot = 0.00;
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("DOC_REF_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("DOC_CD").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("INV_NO").getAsString());
                        row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("DOC_DATE").getAsString()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("TOT_AMT").getAsDouble()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("UNPAID_AMT").getAsDouble()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("SR_NO").getAsDouble()));
                        tot += array.get(i).getAsJsonObject().get("UNPAID_AMT").getAsDouble();
                        dtmCr.addRow(row);
                    }
                    Vector row = new Vector();
                    row.add(" ");
                    row.add("Total");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(lb.Convert2DecFmtForRs(tot));
                    row.add(" ");
                    dtmCr.addRow(row);
                    jLabel2.setText(lb.Convert2DecFmtForRs(result.get("balance").getAsDouble()));
                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                } else {
                    lb.showMessageDailog(result.get("Cause").getAsString());
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void jbtnViewActionPerformedRoutineDR() {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);

            JsonObject call = accountAPI.ListBills(ac_cd, true).execute().body();

            lb.addGlassPane(this);

            System.out.println(call.toString());
            lb.removeGlassPane(ListBill.this);
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = result.getAsJsonArray("data");
                    dtmDr.setRowCount(0);
                    double tot = 0.00;
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("DOC_REF_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("DOC_CD").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("INV_NO").getAsString());
                        row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("DOC_DATE").getAsString()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("TOT_AMT").getAsDouble()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("UNPAID_AMT").getAsDouble()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("SR_NO").getAsDouble()));
                        tot += array.get(i).getAsJsonObject().get("UNPAID_AMT").getAsDouble();
                        dtmDr.addRow(row);
                    }
                    Vector row = new Vector();
                    row.add(" ");
                    row.add("Total");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(lb.Convert2DecFmtForRs(tot));
                    row.add(" ");
                    dtmDr.addRow(row);
                    lb.setColumnSizeForTable(jTable2, jPanel3.getWidth());
                } else {
                    lb.showMessageDailog(result.get("Cause").getAsString());
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void jbtnViewActionPerformedRoutineAdjst() {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);

            JsonObject call = accountAPI.ListBillsAdjsted(ac_cd).execute().body();

            lb.addGlassPane(this);

            System.out.println(call.toString());
            lb.removeGlassPane(ListBill.this);
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = result.getAsJsonArray("data");
                    dtmAdjsted.setRowCount(0);
                    double tot = 0.00;
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("DOC_REF_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("DR_DOC_CD").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("DR_INV_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("CR_DOC_CD").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("CR_INV_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("TOT_AMT").getAsString());
                        tot += array.get(i).getAsJsonObject().get("TOT_AMT").getAsDouble();
                        dtmAdjsted.addRow(row);
                    }
                    Vector row = new Vector();
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(lb.Convert2DecFmtForRs(tot));
                    dtmAdjsted.addRow(row);
                    lb.setColumnSizeForTable(jTable3, jPanel2.getWidth());
                } else {
                    lb.showMessageDailog(result.get("Cause").getAsString());
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean validateRow() {
        boolean flag = true;
        if (jTable1.getSelectedRowCount() == 0) {
            lb.showMessageDailog("Please select CR voucher");
            return false;
        }
        if (jTable2.getSelectedRowCount() == 0) {
            lb.showMessageDailog("Please select DR voucher");
            return false;
        }

        if (jTable1.getSelectedRowCount() > 1 || jTable2.getSelectedRowCount() > 1) {
            lb.showMessageDailog("Both side must contain single row");
            return false;
        }

        if (jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString().isEmpty() || jTable2.getValueAt(jTable2.getSelectedRow(), 0).toString().isEmpty()) {
            lb.showMessageDailog("Total can not be selected");
            return false;
        }
        return flag;
    }

    private void adjustBill() {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);
            JsonObject addUpdaCall = accountAPI.UpdateBill(
                    jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString(), jTable2.getValueAt(jTable2.getSelectedRow(), 0).toString(),
                    jTable1.getValueAt(jTable1.getSelectedRow(), 1).toString(), jTable2.getValueAt(jTable2.getSelectedRow(), 1).toString(),
                    jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString(), jTable2.getValueAt(jTable2.getSelectedRow(), 2).toString(),
                    jTable1.getValueAt(jTable1.getSelectedRow(), 5).toString(), jTable2.getValueAt(jTable2.getSelectedRow(), 5).toString(),
                    jTable1.getValueAt(jTable1.getSelectedRow(), 6).toString(), jTable2.getValueAt(jTable2.getSelectedRow(), 6).toString(), ac_cd).execute().body();

            if (addUpdaCall != null) {
                System.out.println(addUpdaCall.toString());
                JsonObject object = addUpdaCall;
                if (object.get("result").getAsInt() == 1) {
                    lb.showMessageDailog("Voucher saved successfully");
                    jButton1.doClick();
                } else {
                    lb.showMessageDailog(object.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ListBill.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private void reverseBill(String doc_ref_no) {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);
            JsonObject addUpdaCall = accountAPI.ReverseBill(doc_ref_no).execute().body();

            if (addUpdaCall != null) {
                System.out.println(addUpdaCall.toString());
                JsonObject object = addUpdaCall;
                if (object.get("result").getAsInt() == 1) {
                    lb.showMessageDailog("Voucher saved successfully");
                    jButton1.doClick();
                } else {
                    lb.showMessageDailog(object.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ListBill.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jtxtAcName = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtAcAlias = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ref no", "Doc Code", "Inv No", "Date", "Net Amount", "Un Paid Amt", "SR_NO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Doc Ref No", "Dr Book", "Dr Inv", "CR Book", "CR Inv", "Amt"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jTable3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable3KeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setMinWidth(0);
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable3.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable3.getColumnModel().getColumn(1).setResizable(false);
            jTable3.getColumnModel().getColumn(2).setResizable(false);
            jTable3.getColumnModel().getColumn(3).setResizable(false);
            jTable3.getColumnModel().getColumn(4).setResizable(false);
            jTable3.getColumnModel().getColumn(5).setResizable(false);
        }

        jPanel2.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jtxtAcName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcNameFocusGained(evt);
            }
        });

        jButton2.setText("Adjust Bill");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("List Bill");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("A/C Name");

        jtxtAcAlias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcAliasFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAcAliasFocusLost(evt);
            }
        });
        jtxtAcAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAcAliasKeyPressed(evt);
            }
        });

        jButton3.setText("Close");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAcName)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtxtAcAlias)
                    .addComponent(jtxtAcName)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jButton3))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ref_no", "Doc Code", "Inv No", "Date", "Amount", "Un paid Amt", "SR No"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(0);
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
            jTable2.getColumnModel().getColumn(4).setResizable(false);
            jTable2.getColumnModel().getColumn(5).setResizable(false);
            jTable2.getColumnModel().getColumn(6).setMinWidth(0);
            jTable2.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable2.getColumnModel().getColumn(6).setMaxWidth(0);
        }

        jPanel4.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtAcAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcAliasFocusGained

    private void jtxtAcAliasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusLost
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAcAliasFocusLost

    private void jtxtAcAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcAliasKeyPressed
        if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtAcAlias)) {
                setAccountDetailMobile("2", jtxtAcAlias.getText());
            }
        }
    }//GEN-LAST:event_jtxtAcAliasKeyPressed

    private void jtxtAcNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcNameFocusGained

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jbtnViewActionPerformedRoutineCR();
        jbtnViewActionPerformedRoutineDR();
        jbtnViewActionPerformedRoutineAdjst();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (validateRow()) {
            adjustBill();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jTable3MouseClicked

    private void jTable3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable3KeyPressed
        // TODO add your handling code here:
        int row = jTable3.getSelectedRow();
        if (row != -1) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                lb.confirmDialog("Do you want to revese the entry?");
                if (lb.type) {
                    final String doc_ref_no = jTable3.getValueAt(row, 0).toString();
                    reverseBill(doc_ref_no);
                }
            }
        }
    }//GEN-LAST:event_jTable3KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAcName;
    // End of variables declaration//GEN-END:variables
}
