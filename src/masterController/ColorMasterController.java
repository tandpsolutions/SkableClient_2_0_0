/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterController;

import com.google.gson.JsonObject;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import masterView.ColorMasterView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.ColorAPI;
import retrofitAPI.SupportAPI;
import skable.SkableHome;
import support.Library;

public class ColorMasterController extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    boolean formLoad = false;
    Library lb = Library.getInstance();
    String colour_cd = "";
    ColorMasterView cmv = null;
    private ColorAPI colorAPI;

    public ColorMasterController(java.awt.Frame parent, boolean modal, ColorMasterView bmv, String brand_cd, String brand_name) {
        super(parent, modal);
        initComponents();
        colorAPI = lb.getRetrofit().create(ColorAPI.class);

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
        this.cmv = bmv;
        jtxtColorName.setText(brand_name);
        this.colour_cd = brand_cd;
        jtxtColorName.requestFocusInWindow();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void validateVoucher() {
        if (lb.isBlank(jtxtColorName)) {
            lb.showMessageDailog("Color name can not be left blank");
            jtxtColorName.requestFocusInWindow();
            return;
        }
        if (colour_cd.equalsIgnoreCase("")) {
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).validateData("colourmst", "colour_cd", "colour_name", jtxtColorName.getText());
             lb.addGlassPane(ColorMasterController.this);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    lb.removeGlassPane(ColorMasterController.this);
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("Colour already exist");
                            return;
                        } else {
                            saveVoucher();
                        }
                    } else {
                        lb.showMessageDailog(rspns.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(ColorMasterController.this);
                }
            });
        } else {
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).ValidateDataEdit("colourmst", "colour_cd", "colour_name", jtxtColorName.getText(), "colour_cd", colour_cd);
            lb.addGlassPane(ColorMasterController.this);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    lb.removeGlassPane(ColorMasterController.this);
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("Colour already exist");
                            return;
                        } else {
                            saveVoucher();
                        }
                    } else {
                        lb.showMessageDailog(rspns.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(ColorMasterController.this);
                }
            });

        }
    }

    private void saveVoucher() {
        Call<JsonObject> call = colorAPI.addUpdateColorMaster(colour_cd, jtxtColorName.getText(), SkableHome.user_id, SkableHome.selected_year);
        lb.addGlassPane(ColorMasterController.this);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(ColorMasterController.this);
                if (rspns.isSuccessful()) {
                    if (rspns.body().get("result").getAsInt() == 1) {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                        if (cmv != null) {
                            cmv.addRow(rspns.body().get("colour_cd").getAsString(), jtxtColorName.getText());
                        }
                        ColorMasterController.this.dispose();
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(ColorMasterController.this);
            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtColorName = new javax.swing.JTextField();

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

        jLabel1.setText("Color Name");

        jtxtColorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtColorNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtColorNameFocusLost(evt);
            }
        });
        jtxtColorName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtColorNameKeyPressed(evt);
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
                        .addComponent(jtxtColorName)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtColorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnSave))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtColorName});

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

    private void jtxtColorNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtColorNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtColorNameFocusGained

    private void jtxtColorNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtColorNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtColorNameFocusLost

    private void jtxtColorNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtColorNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnSave);
    }//GEN-LAST:event_jtxtColorNameKeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JTextField jtxtColorName;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
