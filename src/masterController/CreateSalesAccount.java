/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.AccountMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.AccountMasterAPI;
import skable.SkableHome;
import support.Library;

/**
 *
 * @author bhaumik
 */
public class CreateSalesAccount extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    Library lb = Library.getInstance();
    public String ac_cd = "";
    private AccountMasterAPI api;
    public AccountMasterModel account;

    /**
     * Creates new form CreateAccount
     */
    public CreateSalesAccount(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        api = lb.getRetrofit().create(AccountMasterAPI.class);
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

    public void setMobileNumber(String mobile) {
        jtxtMobile.setText(mobile);
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private boolean validateForm() {
        boolean flag = true;
        if (lb.isBlank(jtxtName)) {
            jtxtName.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Customer name can not be left blank", "Customer", JOptionPane.OK_OPTION);
            return false;
        }

        if (lb.isBlank(jtxtMobile)) {
            jtxtMobile.requestFocusInWindow();
            JOptionPane.showMessageDialog(this, "Mobile nulber can not be left blank", "Customer", JOptionPane.OK_OPTION);
            return false;
        }

        if (!jtxtTin.getText().equalsIgnoreCase("")) {
            if (!lb.isBlank(jtxtTin)) {
                if (jtxtTin.getText().trim().length() != 16) {
                    JOptionPane.showMessageDialog(null, "Card number must be length of 16");
                    jtxtTin.requestFocusInWindow();
                    return false;
                }
            }
        }

        return flag;
    }

    private void saveAccount() {
        account = new AccountMasterModel();
        account.setAC_CD(ac_cd);
        account.setFNAME(jtxtName.getText());
        account.setADD1(jtxtAddress1.getText());
        account.setEMAIL(jtxtEmail.getText());
        account.setMOBILE1(jtxtMobile.getText());
        account.setGRP_CD("G000001");
        account.setCST(jtxtCST.getText());
        account.setTIN(jtxtTin.getText());
        account.setRef_by(jtxtRefBy.getText());
        account.setCARD_NO(jtxtCardNo.getText());
        account.setGST_NO(jtxtGst.getText());
        account.setOPB_AMT(0.00);
        account.setOPB_EFF(0);
        saveVoucher(account);
    }

    private void saveVoucher(AccountMasterModel acc) {
        Call<JsonObject> call = api.AddUpdateAccountMaster(new Gson().toJson(acc), SkableHome.selected_year);
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(CreateSalesAccount.this);
                if (rspns.isSuccessful()) {
                    if (rspns.body().get("result").getAsInt() == 1) {
                        lb.showMessageDailog("Account created Successfully");
                        ac_cd = rspns.body().get("ac_cd").getAsString();
                        CreateSalesAccount.this.dispose();
                    } else {
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(CreateSalesAccount.this);
            }
        });
    }

    private boolean validateVoucher() {
        if (lb.isBlank(jtxtMobile)) {
            lb.showMessageDailog("Mobile number can not be left blank");
            jtxtMobile.requestFocusInWindow();
            return false;
        }
        if (lb.isBlank(jtxtEmail)) {
            lb.showMessageDailog("Email can not be left blank");
            jtxtEmail.requestFocusInWindow();
            return false;
        }
        if (jtxtName.getText().isEmpty()) {
            jtxtName.requestFocusInWindow();
            lb.showMessageDailog("Account name can not be left blank");
            return false;
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtAddress1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtMobile = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtxtCST = new javax.swing.JTextField();
        jtxtTin = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtxtCardNo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jtxtRefBy = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jtxtGst = new javax.swing.JTextField();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Name");

        jtxtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtNameFocusLost(evt);
            }
        });
        jtxtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtNameKeyPressed(evt);
            }
        });

        jLabel2.setText("Address");

        jtxtAddress1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAddress1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAddress1FocusLost(evt);
            }
        });
        jtxtAddress1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAddress1KeyPressed(evt);
            }
        });

        jLabel3.setText("Mobile");

        jtxtMobile.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMobileFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMobileFocusLost(evt);
            }
        });
        jtxtMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyTyped(evt);
            }
        });

        jLabel4.setText("Email");

        jtxtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtEmailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtEmailFocusLost(evt);
            }
        });
        jtxtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtEmailKeyPressed(evt);
            }
        });

        jLabel5.setText("CST");

        jtxtCST.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCSTFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCSTFocusLost(evt);
            }
        });
        jtxtCST.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCSTKeyPressed(evt);
            }
        });

        jtxtTin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTinFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTinFocusLost(evt);
            }
        });
        jtxtTin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtTinKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTinKeyPressed(evt);
            }
        });

        jLabel6.setText("Tin No");

        jtxtCardNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCardNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCardNoFocusLost(evt);
            }
        });
        jtxtCardNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardNoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCardNoKeyTyped(evt);
            }
        });

        jLabel7.setText("Card No");

        jLabel8.setText("Ref By");

        jtxtRefBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRefByFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRefByFocusLost(evt);
            }
        });
        jtxtRefBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRefByKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtRefByKeyTyped(evt);
            }
        });

        jLabel9.setText("GST No");

        jtxtGst.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGstFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtGstFocusLost(evt);
            }
        });
        jtxtGst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtGstKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGstKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(250, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtEmail)
                    .addComponent(jtxtMobile)
                    .addComponent(jtxtAddress1)
                    .addComponent(jtxtName)
                    .addComponent(jtxtCST)
                    .addComponent(jtxtTin)
                    .addComponent(jtxtCardNo)
                    .addComponent(jtxtRefBy)
                    .addComponent(jtxtGst))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtAddress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtGst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtCardNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6, jtxtTin});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel9, jtxtGst});

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (validateVoucher()) {
            returnStatus = RET_OK;
            saveAccount();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jtxtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtNameFocusGained

    private void jtxtAddress1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress1FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAddress1FocusGained

    private void jtxtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtNameFocusLost

    private void jtxtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtAddress1);
    }//GEN-LAST:event_jtxtNameKeyPressed

    private void jtxtAddress1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress1FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAddress1FocusLost

    private void jtxtAddress1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jtxtAddress1KeyPressed

    private void jtxtMobileFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMobileFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMobileFocusGained

    private void jtxtMobileFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMobileFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtMobileFocusLost

    private void jtxtMobileKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtEmail);
    }//GEN-LAST:event_jtxtMobileKeyPressed

    private void jtxtEmailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmailFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtEmailFocusGained

    private void jtxtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmailFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtEmailFocusLost

    private void jtxtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtEmailKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtCST);
    }//GEN-LAST:event_jtxtEmailKeyPressed

    private void jtxtCSTFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCSTFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCSTFocusGained

    private void jtxtCSTFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCSTFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtCSTFocusLost

    private void jtxtCSTKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCSTKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtTin);
    }//GEN-LAST:event_jtxtCSTKeyPressed

    private void jtxtTinFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTinFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTinFocusGained

    private void jtxtTinFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTinFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtTinFocusLost

    private void jtxtTinKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTinKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtCardNo);
    }//GEN-LAST:event_jtxtTinKeyPressed

    private void jtxtTinKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTinKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 16);
    }//GEN-LAST:event_jtxtTinKeyTyped

    private void jtxtCardNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardNoFocusGained
        // TODO add your handling code here:.
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCardNoFocusGained

    private void jtxtCardNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardNoFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtCardNoFocusLost

    private void jtxtCardNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardNoKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtRefBy);
    }//GEN-LAST:event_jtxtCardNoKeyPressed

    private void jtxtCardNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardNoKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 16);
    }//GEN-LAST:event_jtxtCardNoKeyTyped

    private void jtxtMobileKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 25);
    }//GEN-LAST:event_jtxtMobileKeyTyped

    private void jtxtRefByFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRefByFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRefByFocusGained

    private void jtxtRefByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRefByFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtRefByFocusLost

    private void jtxtRefByKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefByKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, okButton);
    }//GEN-LAST:event_jtxtRefByKeyPressed

    private void jtxtRefByKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefByKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtRefByKeyTyped

    private void jtxtGstFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGstFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGstFocusGained

    private void jtxtGstFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGstFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGstFocusLost

    private void jtxtGstKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGstKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGstKeyTyped

    private void jtxtGstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGstKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGstKeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        if (returnStatus == RET_OK) {
            if (validateForm()) {
                saveAccount();
                setVisible(false);

            }
        } else {
            setVisible(false);
            dispose();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public javax.swing.JTextField jtxtAddress1;
    public javax.swing.JTextField jtxtCST;
    public javax.swing.JTextField jtxtCardNo;
    public javax.swing.JTextField jtxtEmail;
    public javax.swing.JTextField jtxtGst;
    public javax.swing.JTextField jtxtMobile;
    public javax.swing.JTextField jtxtName;
    public javax.swing.JTextField jtxtRefBy;
    public javax.swing.JTextField jtxtTin;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
