/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import model.AccountHead;
import model.NlcModel;
import retrofitAPI.AccountAPI;
import retrofitAPI.StartUpAPI;
import support.Library;

/**
 *
 * @author bhaumik
 */
public class NLCCalculation extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    private ArrayList<NlcModel> data;
    Library lb = Library.getInstance();
    String ac_cd = "";

    /**
     * Creates new form PrizeDropController
     */
    public NLCCalculation(java.awt.Frame parent, boolean modal, ArrayList<NlcModel> data) {
        super(parent, modal);
        initComponents();
        this.data = data;

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
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
                            jtxtRemark.requestFocusInWindow();
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

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private void okRoutine() {
        try {
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setDisc_per(lb.isNumber(jtxtDiscPer));
                data.get(i).setExtra_support(lb.isNumber(jtxtExtraSupport));
                data.get(i).setActivation(lb.isNumber(jtxtACtivation));
                data.get(i).setBackend(lb.isNumber(jtxtBackEnd));
                data.get(i).setPrize_drop(lb.isNumber(jtxtPrizeDrop));
                data.get(i).setCnAmount(lb.isNumber(jtxtcnAmount));
                data.get(i).setRemark(jtxtRemark.getText());
                data.get(i).setAc_cd(ac_cd);
            }
            String detailJson = new Gson().toJson(data);
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);
            JsonObject addUpdaCall = accountAPI.UpdateNlc(detailJson).execute().body();

            if (addUpdaCall != null) {
                System.out.println(addUpdaCall.toString());
                JsonObject object = addUpdaCall;
                if (object.get("result").getAsInt() == 1) {
                    lb.showMessageDailog("Voucher saved successfully");
                    this.dispose();
                } else {
                    lb.showMessageDailog(object.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NLCCalculation.class.getName()).log(Level.SEVERE, null, ex);
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

        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtDiscPer = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtExtraSupport = new javax.swing.JTextField();
        jtxtBackEnd = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtxtACtivation = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jtxtPrizeDrop = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jtxtcnAmount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxtRemark = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jtxtAcAlias = new javax.swing.JTextField();
        jtxtAcName = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Discount %");

        jtxtDiscPer.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtDiscPer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDiscPerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDiscPerFocusLost(evt);
            }
        });
        jtxtDiscPer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDiscPerKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtDiscPerKeyTyped(evt);
            }
        });

        jLabel3.setText("Extra Support");

        jtxtExtraSupport.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtExtraSupport.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtExtraSupportFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtExtraSupportFocusLost(evt);
            }
        });
        jtxtExtraSupport.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtExtraSupportKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtExtraSupportKeyTyped(evt);
            }
        });

        jtxtBackEnd.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtBackEnd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBackEndFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBackEndFocusLost(evt);
            }
        });
        jtxtBackEnd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBackEndKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtBackEndKeyTyped(evt);
            }
        });

        jLabel5.setText("Back End");

        jtxtACtivation.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtACtivation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtACtivationFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtACtivationFocusLost(evt);
            }
        });
        jtxtACtivation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtACtivationKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtACtivationKeyTyped(evt);
            }
        });

        jLabel9.setText("Activation");

        jLabel10.setText("Prize Drop");

        jtxtPrizeDrop.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtPrizeDrop.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPrizeDropFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPrizeDropFocusLost(evt);
            }
        });
        jtxtPrizeDrop.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPrizeDropKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPrizeDropKeyTyped(evt);
            }
        });

        jLabel11.setText("CN Amount");

        jtxtcnAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtcnAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtcnAmountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtcnAmountFocusLost(evt);
            }
        });
        jtxtcnAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtcnAmountKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtcnAmountKeyTyped(evt);
            }
        });

        jLabel7.setText("Remark");

        jtxtRemark.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyPressed(evt);
            }
        });

        jLabel8.setText("Name");

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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtAcAliasKeyReleased(evt);
            }
        });

        jtxtAcName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcNameFocusGained(evt);
            }
        });

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtAcName))
                            .addComponent(jtxtACtivation, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                            .addComponent(jtxtBackEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                            .addComponent(jtxtExtraSupport, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                            .addComponent(jtxtDiscPer, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                            .addComponent(jtxtPrizeDrop, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                            .addComponent(jtxtcnAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                            .addComponent(jtxtRemark))
                        .addGap(79, 79, 79))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(cancelButton)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel5, jLabel9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDiscPer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtExtraSupport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtBackEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtACtivation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtPrizeDrop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtcnAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel5, jLabel9, jtxtACtivation, jtxtBackEnd, jtxtDiscPer, jtxtExtraSupport});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel10, jtxtPrizeDrop});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jtxtcnAmount});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, jtxtAcAlias, jtxtAcName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel7, jtxtRemark});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jtxtDiscPerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscPerFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDiscPerFocusGained

    private void jtxtDiscPerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscPerFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtDiscPerFocusLost

    private void jtxtDiscPerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscPerKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtExtraSupport);
    }//GEN-LAST:event_jtxtDiscPerKeyPressed

    private void jtxtDiscPerKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscPerKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtDiscPerKeyTyped

    private void jtxtExtraSupportFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtExtraSupportFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtExtraSupportFocusGained

    private void jtxtExtraSupportFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtExtraSupportFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtExtraSupportFocusLost

    private void jtxtExtraSupportKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtExtraSupportKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtBackEnd);
    }//GEN-LAST:event_jtxtExtraSupportKeyPressed

    private void jtxtExtraSupportKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtExtraSupportKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtExtraSupportKeyTyped

    private void jtxtBackEndFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBackEndFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBackEndFocusGained

    private void jtxtBackEndFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBackEndFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtBackEndFocusLost

    private void jtxtBackEndKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBackEndKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtACtivation);
    }//GEN-LAST:event_jtxtBackEndKeyPressed

    private void jtxtBackEndKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBackEndKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtBackEndKeyTyped

    private void jtxtACtivationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACtivationFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtACtivationFocusGained

    private void jtxtACtivationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACtivationFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtACtivationFocusLost

    private void jtxtACtivationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACtivationKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtPrizeDrop);
    }//GEN-LAST:event_jtxtACtivationKeyPressed

    private void jtxtACtivationKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACtivationKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtACtivationKeyTyped

    private void jtxtPrizeDropFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPrizeDropFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPrizeDropFocusGained

    private void jtxtPrizeDropFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPrizeDropFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtPrizeDropFocusLost

    private void jtxtPrizeDropKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPrizeDropKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtcnAmount);
    }//GEN-LAST:event_jtxtPrizeDropKeyPressed

    private void jtxtPrizeDropKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPrizeDropKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtPrizeDropKeyTyped

    private void jtxtcnAmountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtcnAmountFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtcnAmountFocusGained

    private void jtxtcnAmountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtcnAmountFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtcnAmountFocusLost

    private void jtxtcnAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtcnAmountKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtAcAlias);
    }//GEN-LAST:event_jtxtcnAmountKeyPressed

    private void jtxtcnAmountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtcnAmountKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtcnAmountKeyTyped

    private void jtxtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jButton1);
    }//GEN-LAST:event_jtxtRemarkKeyPressed

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

    private void jtxtAcAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcAliasKeyReleased

    }//GEN-LAST:event_jtxtAcAliasKeyReleased

    private void jtxtAcNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcNameFocusGained

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (ac_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Please select account");
            return;
        }
        okRoutine();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public javax.swing.JTextField jtxtACtivation;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAcName;
    public javax.swing.JTextField jtxtBackEnd;
    public javax.swing.JTextField jtxtDiscPer;
    public javax.swing.JTextField jtxtExtraSupport;
    public javax.swing.JTextField jtxtPrizeDrop;
    public javax.swing.JTextField jtxtRemark;
    public javax.swing.JTextField jtxtcnAmount;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
