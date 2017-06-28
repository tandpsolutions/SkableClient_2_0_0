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
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import masterView.TaxMasterView;
import model.TaxMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.SupportAPI;
import retrofitAPI.TaxMasterAPI;
import skable.SkableHome;
import support.Library;

public class TaxMasterController extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    boolean formLoad = false;
    private Library lb = Library.getInstance();
    private String tax_cd = "";
    private TaxMasterView tmv = null;
    private TaxMasterAPI taxMasterAPI;

    public TaxMasterController(java.awt.Frame parent, boolean modal, TaxMasterView tmv) {
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
        this.tmv = tmv;
        taxMasterAPI = lb.getRetrofit().create(TaxMasterAPI.class);
        jtxtTaxName.requestFocusInWindow();
    }

    public void setTaxMasterData(String tax_cd) {
        try {
            if(tax_cd.equalsIgnoreCase("")){
                setVisible(true);
                return;
            }
            JsonObject result = taxMasterAPI.GetTaxMaster(tax_cd).execute().body();
            if (result.get("result").getAsInt() == 1) {
                TypeToken<List<TaxMasterModel>> token = new TypeToken<List<TaxMasterModel>>() {
                };
                ArrayList<TaxMasterModel> detail = new Gson().fromJson(result.get("data"), token.getType());
                for (int i = 0; i < detail.size(); i++) {
                    tax_cd = detail.get(i).getTAXCD();
                    jtxtTaxName.setText(detail.get(i).getTAXNAME());
                    jtxtSgst.setText(detail.get(i).getTAXPER() + "");
                    jtxtCgst.setText(detail.get(i).getADDTAXPER() + "");
                    jtxtIgst.setText(detail.get(i).getIGST() + "");
                    jlblUser.setText(detail.get(i).getUSERID() + "");
                    jlblEditNo.setText(detail.get(i).getEDITNO() + "");
                    jlblTimeStamp.setText(detail.get(i).getTIMESTAMP());
                    this.setVisible(true);
                }
            } else {
                lb.showMessageDailog(result.get("Cause").getAsString());
            }
        } catch (IOException ex) {
            lb.printToLogFile("setTaxMaster", ex);
        }


    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void validateVoucher() {
        if (lb.isBlank(jtxtTaxName)) {
            lb.showMessageDailog("Tax name can not be left blank");
            jtxtTaxName.requestFocusInWindow();
            return;
        }
        if (tax_cd.equalsIgnoreCase("")) {
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).validateData("taxmst", "tax_cd", "tax_name", jtxtTaxName.getText());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("Tax already exist");
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
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).ValidateDataEdit("taxmst", "tax_cd", "tax_name", jtxtTaxName.getText(), "tax_cd", tax_cd);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("Tax already exist");
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
        Call<JsonObject> call = taxMasterAPI.addUpdateTaxMaster(tax_cd, jtxtTaxName.getText(), jtxtSgst.getText(), jtxtCgst.getText(), SkableHome.user_id, SkableHome.selected_year);
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(TaxMasterController.this);
                if (rspns.isSuccessful()) {
                    if (rspns.body().get("result").getAsInt() == 1) {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                        if (tmv != null) {
                            tmv.addRow(rspns.body().get("tax_cd").getAsString(), jtxtTaxName.getText());
                        }
                        TaxMasterController.this.dispose();
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(TaxMasterController.this);
            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtTaxName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtSgst = new javax.swing.JTextField();
        jtxtCgst = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtIgst = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();

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

        jLabel1.setText("Tax Name");

        jtxtTaxName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTaxNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTaxNameFocusLost(evt);
            }
        });
        jtxtTaxName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTaxNameKeyPressed(evt);
            }
        });

        jLabel2.setText("SGST");

        jtxtSgst.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSgstFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSgstFocusLost(evt);
            }
        });
        jtxtSgst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtSgstKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSgstKeyPressed(evt);
            }
        });

        jtxtCgst.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCgstFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCgstFocusLost(evt);
            }
        });
        jtxtCgst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCgstKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCgstKeyPressed(evt);
            }
        });

        jLabel3.setText("CGST");

        jtxtIgst.setEditable(false);
        jtxtIgst.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtIgstFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtIgstFocusLost(evt);
            }
        });
        jtxtIgst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtIgstKeyPressed(evt);
            }
        });

        jLabel4.setText("CGST");

        jLabel15.setText("Last Updated : ");

        jLabel13.setText("Edit NO :");

        jLabel12.setText("User :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtTaxName)
                    .addComponent(jtxtSgst)
                    .addComponent(jtxtCgst)
                    .addComponent(jtxtIgst))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(198, Short.MAX_VALUE)
                .addComponent(jbtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtTaxName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtSgst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtCgst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtIgst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnSave))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtTaxName});

        jLabel4.getAccessibleContext().setAccessibleName("IGST");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
//        
        validateVoucher();
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jbtnSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnSaveKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnSaveKeyPressed

    private void jtxtTaxNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTaxNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTaxNameFocusGained

    private void jtxtTaxNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTaxNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtTaxNameFocusLost

    private void jtxtTaxNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTaxNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtSgst);
    }//GEN-LAST:event_jtxtTaxNameKeyPressed

    private void jtxtSgstFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSgstFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSgstFocusGained

    private void jtxtSgstFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSgstFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtSgstFocusLost

    private void jtxtSgstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSgstKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtCgst);
    }//GEN-LAST:event_jtxtSgstKeyPressed

    private void jtxtCgstFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCgstFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCgstFocusGained

    private void jtxtCgstFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCgstFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtCgstFocusLost

    private void jtxtCgstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCgstKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnSave);
    }//GEN-LAST:event_jtxtCgstKeyPressed

    private void jtxtIgstFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtIgstFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtIgstFocusGained

    private void jtxtIgstFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtIgstFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtIgstFocusLost

    private void jtxtIgstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtIgstKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtIgstKeyPressed

    private void jtxtSgstKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSgstKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtSgstKeyTyped

    private void jtxtCgstKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCgstKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtCgstKeyTyped

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JTextField jtxtCgst;
    private javax.swing.JTextField jtxtIgst;
    private javax.swing.JTextField jtxtSgst;
    private javax.swing.JTextField jtxtTaxName;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
