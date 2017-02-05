/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterController;

import com.google.gson.JsonObject;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import masterView.UserGroupMasterView;
import retrofitAPI.SupportAPI;
import retrofitAPI.UserAPI;
import support.Library;

public class UserGroupMasterController extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    boolean formLoad = false;
    private Library lb = Library.getInstance();
    private String user_grp_cd = "";
    private UserGroupMasterView ugmv = null;
    private UserAPI userAPI;

    public UserGroupMasterController(java.awt.Frame parent, boolean modal, UserGroupMasterView bmv, String user_grp_cd, String user_grp) {
        super(parent, modal);
        initComponents();

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
        this.ugmv = bmv;
        userAPI = lb.getRetrofit().create(UserAPI.class);
        jtxtUserGroupName.setText(user_grp);
        this.user_grp_cd = user_grp_cd;
        jtxtUserGroupName.requestFocusInWindow();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void validateVoucher() {
        if (lb.isBlank(jtxtUserGroupName)) {
            lb.showMessageDailog("User Group name can not be left blank");
            jtxtUserGroupName.requestFocusInWindow();
            return;
        }
        if (user_grp_cd.equalsIgnoreCase("")) {
            try {
                JsonObject call = lb.getRetrofit().create(SupportAPI.class).
                        validateData("USER_GRP_MST", "USER_GRP_CD", "USER_GRP", jtxtUserGroupName.getText()).execute().body();

                if (call != null) {
                    if (call.get("result").getAsInt() == 0) {
                        lb.showMessageDailog("User group already exist");
                        return;
                    } else {
                        saveVoucher();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(UserGroupMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                JsonObject call = lb.getRetrofit().create(SupportAPI.class)
                        .ValidateDataEdit("USER_GRP_MST", "USER_GRP_CD", "USER_GRP", jtxtUserGroupName.getText(), "USER_GRP_CD", user_grp_cd).execute().body();

                if (call != null) {
                    if (call.get("result").getAsInt() == 0) {
                        lb.showMessageDailog("User group already exist");
                        return;
                    } else {
                        saveVoucher();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(UserGroupMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void saveVoucher() {
        try {
            JsonObject call = userAPI.AddUpdateUserGroupMaster(jtxtUserGroupName.getText(), user_grp_cd).execute().body();

            if (call != null) {
                if (call.get("result").getAsInt() == 1) {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                    if (ugmv != null) {
                        ugmv.addRow(call.get("USER_GRP_CD").getAsString(), jtxtUserGroupName.getText());
                    }
                    UserGroupMasterController.this.dispose();
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserGroupMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtUserGroupName = new javax.swing.JTextField();

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

        jbtnSave.setText("Save");
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });
        jbtnSave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnSaveKeyPressed(evt);
            }
        });

        jLabel1.setText("User Group Name");

        jtxtUserGroupName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtUserGroupNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtUserGroupNameFocusLost(evt);
            }
        });
        jtxtUserGroupName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtUserGroupNameKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 192, Short.MAX_VALUE)
                        .addComponent(jbtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtUserGroupName)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtUserGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnSave))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtUserGroupName});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed


    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
        validateVoucher();
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jbtnSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnSaveKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnSaveKeyPressed

    private void jtxtUserGroupNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtUserGroupNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtUserGroupNameFocusGained

    private void jtxtUserGroupNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtUserGroupNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtUserGroupNameFocusLost

    private void jtxtUserGroupNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtUserGroupNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnSave);
    }//GEN-LAST:event_jtxtUserGroupNameKeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JTextField jtxtUserGroupName;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
