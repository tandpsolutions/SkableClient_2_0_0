/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import masterView.ModelMasterView;
import model.TypeMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.ModelAPI;
import retrofitAPI.StartUpAPI;
import retrofitAPI.SupportAPI;
import retrofitAPI.TypeAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.ReportTable;
import support.SelectDailog;

public class ModelMasterController extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    boolean formLoad = false;
    Library lb = Library.getInstance();
    String model_cd = "";
    ModelMasterView mmv = null;
    private ModelAPI modelAPI;
    private String type_cd = "";
    private String brand_cd = "";
    private String tax_cd = "";
    private ReportTable viewTable = null;
    private TypeAPI typeAPI;
    private ArrayList<TypeMasterModel> typeList;

    public ModelMasterController(java.awt.Frame parent, boolean modal, ModelMasterView mmv) {
        super(parent, modal);
        typeAPI = lb.getRetrofit().create(TypeAPI.class);
        initComponents();
        modelAPI = lb.getRetrofit().create(ModelAPI.class);
        addTaxCombo();
        getData();
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
        this.mmv = mmv;
        tableForView();
        jtxtModelName.requestFocusInWindow();
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    public void setData(final String model_cd1) {

        Call<JsonObject> call = modelAPI.GetModel(model_cd1);
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(ModelMasterController.this);
                if (rspns.isSuccessful()) {
                    if (rspns.body().get("result").getAsInt() == 1) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        for (int i = 0; i < array.size(); i++) {
                            model_cd = array.get(i).getAsJsonObject().get("MODEL_CD").getAsString();
                            brand_cd = array.get(i).getAsJsonObject().get("BRAND_CD").getAsString();
                            tax_cd = array.get(i).getAsJsonObject().get("TAX_NAME").getAsString();
                            type_cd = array.get(i).getAsJsonObject().get("TYPE_CD").getAsString();
                            jtxtModelName.setText(array.get(i).getAsJsonObject().get("MODEL_NAME").getAsString());
                            jtxtBrandName.setText(array.get(i).getAsJsonObject().get("BRAND_NAME").getAsString());
                            jcmbType.setSelectedItem(array.get(i).getAsJsonObject().get("TYPE_NAME").getAsString());
                            jcmbTax.setSelectedItem(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString());
                        }
                        ModelMasterController.this.setVisible(true);
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(ModelMasterController.this);
            }

        });
    }

    public void getData() {
        Call<JsonObject> call = typeAPI.getTypeMaster();
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(ModelMasterController.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        TypeToken<List<TypeMasterModel>> token = new TypeToken<List<TypeMasterModel>>() {
                        };
                        typeList = new Gson().fromJson(result.get("data"), token.getType());
                        jcmbType.removeAllItems();
                        jcmbType.addItem("Select Type");
                        for (int i = 0; i < typeList.size(); i++) {
                            jcmbType.addItem(typeList.get(i).getTYPE_NAME());
                        }
                        formLoad = true;
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(ModelMasterController.this);
            }
        });
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    private void validateVoucher() {
        if (lb.isBlank(jtxtModelName)) {
            lb.showMessageDailog("Model name can not be left blank");
            jtxtModelName.requestFocusInWindow();
            return;
        }
        if (brand_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Enter Valid Brand Name");
            return;
        }

        if (tax_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Select Valid Tax Code");
            return;
        }

        if (type_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Select valid Type Code");
            return;
        }

        if (model_cd.equalsIgnoreCase("")) {
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).validateData("modelmst", "model_cd", "model_name", jtxtModelName.getText());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    lb.removeGlassPane(ModelMasterController.this);
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("Model already exist");
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
                    lb.removeGlassPane(ModelMasterController.this);
                }
            });
        } else {
            Call<JsonObject> call = lb.getRetrofit().create(SupportAPI.class).ValidateDataEdit("modelmst", "model_cd", "model_name", jtxtModelName.getText(), "model_cd", model_cd);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    lb.removeGlassPane(ModelMasterController.this);
                    if (rspns.isSuccessful()) {
                        if (rspns.body().get("result").getAsInt() == 0) {
                            lb.showMessageDailog("Model already exist");
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
                    lb.removeGlassPane(ModelMasterController.this);
                }
            });

        }
    }

    private void saveVoucher() {

        Call<JsonObject> call = modelAPI.AppUpdateModelMaster(model_cd, jtxtModelName.getText(),
                brand_cd,
                type_cd,
                tax_cd, SkableHome.user_id);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(ModelMasterController.this);
                if (rspns.isSuccessful()) {
                    if (rspns.body().get("result").getAsInt() == 1) {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                        if (mmv != null) {
                            mmv.addRow(rspns.body().get("model_cd").getAsString(), jtxtModelName.getText(),
                                    jtxtBrandName.getText(),
                                    (jcmbType.getSelectedItem().toString()).toString(),
                                    (jcmbTax.getSelectedItem().toString()).toString());
                        }
                        ModelMasterController.this.dispose();
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(ModelMasterController.this);
            }
        });

    }

    private void addTaxCombo() {
        jcmbTax.removeAllItems();
        for (int i = 0; i < Constants.TAX.size(); i++) {
            jcmbTax.addItem(Constants.TAX.get(i).getTAXNAME());
        }
    }

    private void setBrandData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(ModelMasterController.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        if (response.body().get("result").getAsInt() == 1) {
                            final SelectDailog sa = new SelectDailog(null, true);
                            sa.setData(viewTable);
                            sa.setLocationRelativeTo(null);
                            JsonArray array = response.body().getAsJsonArray("data");
                            sa.getDtmHeader().setRowCount(0);
                            for (int i = 0; i < array.size(); i++) {
                                Vector row = new Vector();
                                row.add(array.get(i).getAsJsonObject().get("BRAND_CD").getAsString());
                                row.add(array.get(i).getAsJsonObject().get("BRAND_NAME").getAsString());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    brand_cd = viewTable.getValueAt(row, 0).toString();
                                    jtxtBrandName.setText(viewTable.getValueAt(row, 1).toString());
                                    jcmbTax.requestFocusInWindow();
                                }
                                sa.dispose();
                            }
                        } else {
                            lb.showMessageDailog(response.body().get("Cause").toString());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(ModelMasterController.this);
                }
            }
            );
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtModelName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtBrandName = new javax.swing.JTextField();
        jcmbType = new javax.swing.JComboBox();
        jcmbTax = new javax.swing.JComboBox();

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

        jLabel1.setText("Model Name");

        jtxtModelName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtModelNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtModelNameFocusLost(evt);
            }
        });
        jtxtModelName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtModelNameKeyPressed(evt);
            }
        });

        jLabel2.setText("Brand Name");

        jLabel3.setText("Tax Code");

        jLabel4.setText("Type Name");

        jtxtBrandName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBrandNameFocusGained(evt);
            }
        });
        jtxtBrandName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBrandNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtBrandNameKeyReleased(evt);
            }
        });

        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        jcmbTax.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcmbTax.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcmbTaxItemStateChanged(evt);
            }
        });
        jcmbTax.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTaxKeyPressed(evt);
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
                        .addComponent(jtxtModelName))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtBrandName))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcmbTax, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcmbType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtModelName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnSave))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtModelName});

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
        for (int i = 0; i < Constants.TAX.size(); i++) {
            if (Constants.TAX.get(i).getTAXNAME().equalsIgnoreCase(jcmbTax.getSelectedItem().toString())) {
                tax_cd = (Constants.TAX.get(i).getTAXCD());
                break;
            }
        }

        type_cd = ((jcmbType.getSelectedIndex() > 0) ? typeList.get(jcmbType.getSelectedIndex() - 1).getTYPE_CD() : "");
        validateVoucher();
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jbtnSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnSaveKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnSaveKeyPressed

    private void jtxtModelNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtModelNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtModelNameFocusGained

    private void jtxtModelNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtModelNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtModelNameFocusLost

    private void jtxtModelNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtModelNameKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtBrandName);
    }//GEN-LAST:event_jtxtModelNameKeyPressed

    private void jtxtBrandNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBrandNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBrandNameFocusGained

    private void jtxtBrandNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_N) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                BrandMasterController smc = new BrandMasterController(null, true, null, "", "");
                smc.setLocationRelativeTo(null);
                smc.setVisible(true);
            }
        }
        if (lb.isEnter(evt)) {
            setBrandData("8", jtxtBrandName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtBrandNameKeyPressed

    private void jtxtBrandNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtBrandNameKeyReleased

    private void jcmbTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTypeKeyPressed
        // TODO add your handling code here:

        if (evt.getKeyCode() == KeyEvent.VK_N) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                TypeMasterController bmc = new TypeMasterController(null, true, null, "", "");
                bmc.setLocationRelativeTo(null);
                bmc.setVisible(true);
                getData();
            }
        }
        if (lb.isEnter(evt)) {
            jbtnSave.requestFocusInWindow();
        }

    }//GEN-LAST:event_jcmbTypeKeyPressed

    private void jcmbTaxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcmbTaxItemStateChanged
        // TODO add your handling code here:
        if (formLoad) {
            for (int i = 0; i < Constants.TAX.size(); i++) {
                if (Constants.TAX.get(i).getTAXNAME().equalsIgnoreCase(jcmbTax.getSelectedItem().toString())) {
                    tax_cd = (Constants.TAX.get(i).getTAXCD());
                    break;
                } else {
                    tax_cd = "";
                }
            }
        }
    }//GEN-LAST:event_jcmbTaxItemStateChanged

    private void jcmbTaxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTaxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jcmbTaxItemStateChanged(null);
            jcmbType.requestFocusInWindow();
        }
    }//GEN-LAST:event_jcmbTaxKeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JComboBox jcmbTax;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JTextField jtxtBrandName;
    private javax.swing.JTextField jtxtModelName;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
