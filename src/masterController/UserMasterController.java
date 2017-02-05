/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import masterView.UserMasterView;
import model.UserGrpMstModel;
import retrofitAPI.SupportAPI;
import retrofitAPI.UserAPI;
import skable.Constants;
import support.Library;

public class UserMasterController extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    boolean formLoad = false;
    Library lb = Library.getInstance();
    String user_id = "";
    UserMasterView umv = null;
    HashMap<String, String> typeMap = new HashMap<>();
    private UserAPI userAPI;
    private ArrayList<UserGrpMstModel> userGrpList;

    public UserMasterController(java.awt.Frame parent, boolean modal, UserMasterView mmv) {
        super(parent, modal);
        initComponents();
        userAPI = lb.getRetrofit().create(UserAPI.class);
        getData();
        setUpData();
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
        this.umv = mmv;
        jtxtUserName.requestFocusInWindow();
    }

    private void setUpData() {
        jComboBox1.removeAllItems();
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox1.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void getData() {
        try {
            JsonObject call = userAPI.GetUserGrpMaster().execute().body();

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    TypeToken<List<UserGrpMstModel>> token = new TypeToken<List<UserGrpMstModel>>() {
                    };
                    userGrpList = new Gson().fromJson(result.get("data"), token.getType());
                    jcmbType.removeAllItems();
                    for (int i = 0; i < userGrpList.size(); i++) {
                        jcmbType.addItem(userGrpList.get(i).getUSER_GRP());
                        typeMap.put(userGrpList.get(i).getUSER_GRP(), userGrpList.get(i).getUSER_GRP_CD());
                    }
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setData(String user_id, String user_name, String user_grp,String branch_name) {
        jtxtUserName.setText(user_name);
        this.user_id = user_id;
        jcmbType.setSelectedItem(user_grp);
        jComboBox1.setSelectedItem(branch_name);
    }

    private void validateVoucher() {

        if (jcmbType.getItemCount() == 0) {
            lb.showMessageDailog("Type not loaded");
            return;
        }
        if (lb.isBlank(jtxtUserName)) {
            lb.showMessageDailog("Type name can not be left blank");
            jtxtUserName.requestFocusInWindow();
            return;
        }
        if (user_id.equalsIgnoreCase("")) {
            try {
                JsonObject call = lb.getRetrofit().create(SupportAPI.class).
                        validateData("modelmst", "model_cd", "model_name", jtxtUserName.getText()).execute().body();

                if (call != null) {
                    if (call.get("result").getAsInt() == 0) {
                        lb.showMessageDailog("Model already exist");
                        return;
                    } else {
                        saveVoucher();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                JsonObject call = lb.getRetrofit().create(SupportAPI.class)
                        .ValidateDataEdit("modelmst", "model_cd", "model_name", jtxtUserName.getText(), "model_cd", user_id).execute().body();

                if (call != null) {
                    if (call.get("result").getAsInt() == 0) {
                        lb.showMessageDailog("Model already exist");
                        return;
                    } else {
                        saveVoucher();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void saveVoucher() {
        try {
            JsonObject call = userAPI.addUpdateUserMsater(user_id, jtxtUserName.getText(),
                    typeMap.get(jcmbType.getSelectedItem().toString()).toString(), Constants.BRANCH.get(jComboBox1.getSelectedIndex()).getBranch_cd()).execute().body();

            if (call != null) {
                if (call.get("result").getAsInt() == 1) {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                    if (umv != null) {
                        umv.getData();
                    }
                    UserMasterController.this.dispose();
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtUserName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jcmbType = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

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

        jLabel1.setText("User Name");

        jtxtUserName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtUserNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtUserNameFocusLost(evt);
            }
        });
        jtxtUserName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtUserNameKeyPressed(evt);
            }
        });

        jLabel4.setText("User Group");

        jcmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        jLabel5.setText("Branch");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
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
                        .addComponent(jtxtUserName))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcmbType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnSave))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtUserName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4, jcmbType});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel5});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed


    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
//        try {
//            if (validateVoucher()) {
//                List<BasicNameValuePair> ls = new LinkedList<BasicNameValuePair>();
//                ls.add(new BasicNameValuePair("BRAND_CD", brand_cd));
//                ls.add(new BasicNameValuePair("BRAND_NAME", jtxtBrandName.getText()));
//                ls.add(new BasicNameValuePair("USER_ID", MaisHome.user_id + ""));
//                JSONObject jsonResult = lb.getDataFromServer("/" + MAIS101.folder + "/brand/addUpdateBrandName.php?", ls);
//                if (jsonResult != null) {
//                    if (jsonResult.getInt("status") == 1) {
//                        this.dispose();
//                        bmv.getData();
//                    } else {
//                        lb.showMessageDailog("Brand name is not saved on server");
//                        jtxtBrandName.requestFocusInWindow();
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            lb.printToLogFile("Exception at saveVoucher at Brand Controller", ex);
//            this.dispose();
//        }
        validateVoucher();
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jbtnSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnSaveKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnSaveKeyPressed

    private void jtxtUserNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtUserNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtUserNameFocusGained

    private void jtxtUserNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtUserNameFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_jtxtUserNameFocusLost

    private void jtxtUserNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtUserNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcmbType);
    }//GEN-LAST:event_jtxtUserNameKeyPressed

    private void jcmbTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTypeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnSave);
    }//GEN-LAST:event_jcmbTypeKeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JTextField jtxtUserName;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
