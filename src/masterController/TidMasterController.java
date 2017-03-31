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
import masterView.TidMasterView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.TidApi;
import retrofitAPI.SupportAPI;
import skable.SkableHome;
import support.Library;

public class TidMasterController extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    boolean formLoad = false;
    private Library lb = Library.getInstance();
    private String tid_cd = "";
    private TidMasterView bmv = null;
    private TidApi tidApi;

    public TidMasterController(java.awt.Frame parent, boolean modal, TidMasterView bmv, String tid_cd, String tid_name) {
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
        this.bmv = bmv;
        tidApi = lb.getRetrofit().create(TidApi.class);
        jtxtTidNumber.setText(tid_name);
        this.tid_cd = tid_cd;
        jtxtTidNumber.requestFocusInWindow();
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void validateVoucher() {
        if (lb.isBlank(jtxtTidNumber)) {
            lb.showMessageDailog("TID name can not be left blank");
            jtxtTidNumber.requestFocusInWindow();
            return;
        }
        if (tid_cd.equalsIgnoreCase("")) {
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).validateData("tidmst", "tid_cd", "tid_name", jtxtTidNumber.getText());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("TID already exist");
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
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
        } else {
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).ValidateDataEdit("tidmst", "tid_cd", "tid_name", jtxtTidNumber.getText(), "tid_cd", tid_cd);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("TID already exist");
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
                }
            });

        }
    }

    private void saveVoucher() {
        Call<JsonObject> call = tidApi.addUpdateTidMaster(tid_cd, jtxtTidNumber.getText(), SkableHome.user_id, SkableHome.selected_year);
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(TidMasterController.this);
                if (rspns.isSuccessful()) {
                    if (rspns.body().get("result").getAsInt() == 1) {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                        if (bmv != null) {
                            bmv.addRow(rspns.body().get("tid_cd").getAsString(), jtxtTidNumber.getText());
                        }
                        TidMasterController.this.dispose();
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(TidMasterController.this);
            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtTidNumber = new javax.swing.JTextField();

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

        jLabel1.setText("TID Number");

        jtxtTidNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTidNumberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTidNumberFocusLost(evt);
            }
        });
        jtxtTidNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTidNumberKeyPressed(evt);
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
                        .addComponent(jtxtTidNumber)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtTidNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnSave))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtTidNumber});

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

    private void jtxtTidNumberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTidNumberFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTidNumberFocusGained

    private void jtxtTidNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTidNumberFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtTidNumberFocusLost

    private void jtxtTidNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTidNumberKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnSave);
    }//GEN-LAST:event_jtxtTidNumberKeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JTextField jtxtTidNumber;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
