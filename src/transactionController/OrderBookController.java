/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import masterController.AccountMasterController;
import masterController.ColorMasterController;
import masterController.CreateSalesAccount;
import masterController.MemoryMasterController;
import masterController.ModelMasterController;
import masterController.SeriesMasterController;
import model.AccountHead;
import model.OrderBookModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.OrderBookAPI;
import retrofitAPI.StartUpAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;
import support.SelectDailog;
import transactionView.OrderBookView;

/**
 *
 * @author bhaumik
 */
public class OrderBookController extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    private Library lb = Library.getInstance();
    private String ac_cd = "";
    private String ref_no = "";
    boolean flag = false;
    private OrderBookAPI orderAPI = null;
    private ReportTable viewTable = null;
    private ReportTable viewTable1 = null;
    private String model_cd = "";
    private String memory_cd = "";
    private String color_cd = "";
    private OrderBookView odv;
    private SalesPaymentDialog sd = null;

    /**
     * Creates new form PurchaseController
     */
    public OrderBookController(java.awt.Frame parent, boolean modal, OrderBookView odv) {
        super(parent, modal);
        initComponents();
        this.odv = odv;

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
        setUpData();
        lb.setDateChooserPropertyInit(jtxtVouDate);
        sd = new SalesPaymentDialog(null, true);
        tableForView();
        tableForViewModel();
        flag = true;
        setTitle("Order Book");
        SkableHome.zoomTable.setToolTipOn(true);

        if (SkableHome.user_grp_cd.equalsIgnoreCase("1")) {
            jcmbBranch.setEnabled(true);
        } else {
            jcmbBranch.setEnabled(false);
        }
    }

    private void setUpData() {
        jcmbBranch.removeAllItems();
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jcmbBranch.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
        jcmbBranch.setSelectedItem(SkableHome.selected_branch.getBranch_name());

    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    private void tableForViewModel() {
        viewTable1 = new ReportTable();
        viewTable1.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable1.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable1.AddColumn(2, "Brand Name", 120, java.lang.String.class, null, false);
        viewTable1.AddColumn(3, "Type Name", 120, java.lang.String.class, null, false);
        viewTable1.AddColumn(4, "Sub Type Name", 120, java.lang.String.class, null, false);
        viewTable1.AddColumn(5, "Tax Name", 120, java.lang.String.class, null, false);
        viewTable1.makeTable();
    }

    private void setModelData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(OrderBookController.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        if (response.body().get("result").getAsInt() == 1) {
                            final SelectDailog sa = new SelectDailog(null, true);
                            sa.setData(viewTable1);
                            sa.setLocationRelativeTo(null);
                            JsonArray array = response.body().getAsJsonArray("data");
                            sa.getDtmHeader().setRowCount(0);
                            for (int i = 0; i < array.size(); i++) {
                                Vector row = new Vector();
                                row.add(array.get(i).getAsJsonObject().get("MODEL_CD").getAsString());
                                row.add(array.get(i).getAsJsonObject().get("MODEL_NAME").getAsString());
                                row.add(array.get(i).getAsJsonObject().get("BRAND_NAME").getAsString());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable1, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable1.getSelectedRow();
                                if (row != -1) {
                                    model_cd = viewTable1.getValueAt(row, 0).toString();
                                    jtxtModelName.setText(viewTable1.getValueAt(row, 1).toString());
                                    jtxtMemoryName.requestFocusInWindow();
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
                    lb.removeGlassPane(OrderBookController.this);
                }
            }
            );
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void setMemoryMaster(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(OrderBookController.this);
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
                                row.add(array.get(i).getAsJsonObject().get("MEMORY_CD").getAsString());
                                row.add(array.get(i).getAsJsonObject().get("MEMORY_NAME").getAsString());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    memory_cd = viewTable.getValueAt(row, 0).toString();
                                    jtxtMemoryName.setText(viewTable.getValueAt(row, 1).toString());
                                    jtxtColorName.requestFocusInWindow();
                                }
                                sa.dispose();
                            } else {
                                jtxtMemoryName.requestFocusInWindow();
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
                    lb.removeGlassPane(OrderBookController.this);
                }
            });
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void setColorMaster(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(OrderBookController.this);
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
                                row.add(array.get(i).getAsJsonObject().get("COLOUR_CD").getAsString());
                                row.add(array.get(i).getAsJsonObject().get("COLOUR_NAME").getAsString());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    color_cd = viewTable.getValueAt(row, 0).toString();
                                    jtxtColorName.setText(viewTable.getValueAt(row, 1).toString());
                                    jtxtRemark.requestFocusInWindow();
                                }
                                sa.dispose();
                            } else {
                                jtxtColorName.requestFocusInWindow();
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
                    lb.removeGlassPane(OrderBookController.this);
                }
            });
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

    public void setData(OrderBookAPI orderAPI, String ref_no) {
        this.ref_no = ref_no;
        if (orderAPI == null) {
            orderAPI = lb.getRetrofit().create(OrderBookAPI.class);
        }
        this.orderAPI = orderAPI;

        if (!ref_no.equalsIgnoreCase("")) {
            try {
                JsonObject call;
                call = orderAPI.getOrderBookDetail(ref_no, "", SkableHome.selected_year).execute().body();
                if (call != null) {
                    System.out.println(call.toString());
                    JsonObject object = call;

                    JsonArray array = object.get("data").getAsJsonArray();
                    try {
                        for (int i = 0; i < array.size(); i++) {
                            jtxtVoucher.setText(array.get(i).getAsJsonObject().get("REF_NO").getAsString() + "");
                            jtxtVouDate.setText(lb.ConvertDateFormetForDBForConcurrency(array.get(i).getAsJsonObject().get("VDATE").getAsString()));
                            OrderBookController.this.ref_no = array.get(i).getAsJsonObject().get("REF_NO").getAsString();
                            jlblUser.setText(array.get(i).getAsJsonObject().get("USER_ID").getAsString() + "");
                            jlblEditNo.setText(array.get(i).getAsJsonObject().get("EDIT_NO").getAsDouble() + "");
                            jlblTimeStamp.setText(array.get(i).getAsJsonObject().get("TIME_STAMP").getAsString());
                            jlblVday.setText(lb.setDay(jtxtVouDate));
                            ac_cd = (array.get(i).getAsJsonObject().get("AC_CD").getAsString());
                            model_cd = (array.get(i).getAsJsonObject().get("MODEL_CD").getAsString());
                            memory_cd = (array.get(i).getAsJsonObject().get("MEMORY_CD").getAsString());
                            color_cd = (array.get(i).getAsJsonObject().get("COLOUR_CD").getAsString());
                            jtxtAcName.setText(array.get(i).getAsJsonObject().get("FNAME").getAsString());
                            jtxtModelName.setText(array.get(i).getAsJsonObject().get("MODEL_NAME").getAsString());
                            jtxtMemoryName.setText(array.get(i).getAsJsonObject().get("MEMORY_NAME").getAsString());
                            jtxtColorName.setText(array.get(i).getAsJsonObject().get("COLOUR_NAME").getAsString());
                            jtxtRemark.setText(array.get(i).getAsJsonObject().get("REMARK").getAsString());
                            jtxtAmount.setText(array.get(i).getAsJsonObject().get("BAL").getAsString());
                            jtxtMobile.setText(array.get(i).getAsJsonObject().get("MOBILE1").getAsString());

                            sd.bank_cd = array.get(i).getAsJsonObject().get("BANK_CD").getAsString();
                            sd.card_cd = array.get(i).getAsJsonObject().get("CARD_CD").getAsString();
                            if (!array.get(i).getAsJsonObject().get("BAJAJ_CD").isJsonNull()) {
                                sd.bajaj_cd = array.get(i).getAsJsonObject().get("BAJAJ_CD").getAsString();
                            } else {
                                sd.bajaj_cd = "";
                            }
                            if (lb.isNumber(array.get(i).getAsJsonObject().get("CASH_AMT").getAsString()) > 0) {
                                sd.jcbCash.setSelected(true);
                            }
                            sd.jtxtCashAmt.setText(lb.Convert2DecFmtForRs(lb.isNumber(array.get(i).getAsJsonObject().get("CASH_AMT").getAsString())));
                            if (!sd.bank_cd.equalsIgnoreCase("")) {
                                sd.jcbBank.setSelected(true);
                                sd.jtxtBankAc.setText(array.get(i).getAsJsonObject().get("OUR_BANK").getAsString());
                                sd.jtxtBankName.setText(array.get(i).getAsJsonObject().get("BANK_NAME").getAsString());
                                sd.jtxtBranchName.setText(array.get(i).getAsJsonObject().get("BANK_BRANCH").getAsString());
                                sd.jtxtChequeAmt.setText(array.get(i).getAsJsonObject().get("BANK_AMT").getAsString());
                                sd.jtxtChequeNo.setText(array.get(i).getAsJsonObject().get("CHEQUE_NO").getAsString());
                                if (!array.get(i).getAsJsonObject().get("CHEQUE_DATE").isJsonNull()) {
                                    sd.jtxtChequeDate.setText(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("CHEQUE_DATE").getAsString()));
                                }
                            }
                            if (!sd.card_cd.equalsIgnoreCase("")) {
                                sd.jcbCard.setSelected(true);
                                sd.jtxtCardBank.setText(array.get(i).getAsJsonObject().get("CARD_NAME").getAsString());
                                sd.jtxtCardAmt.setText(array.get(i).getAsJsonObject().get("CARD_AMT").getAsString());
                                sd.jtxtCardPer.setText(array.get(i).getAsJsonObject().get("CARD_PER").getAsString());
                                sd.jlblCardChanges.setText(array.get(i).getAsJsonObject().get("CARD_CHG").getAsString());
                                sd.jtxtCardNo.setText(array.get(i).getAsJsonObject().get("CARD_NO").getAsString());
                                sd.jtxtTIDNo.setText(array.get(i).getAsJsonObject().get("TID_NO").getAsString());
                            }
                            if (!array.get(i).getAsJsonObject().get("BAJAJ_CD").isJsonNull()) {
                                if (!sd.bajaj_cd.equalsIgnoreCase("")) {
                                    sd.jcbBajaj.setSelected(true);
                                    sd.jtxtBajajCapital.setText(array.get(i).getAsJsonObject().get("BAJAJ_NAME").getAsString());
                                    sd.jtxtBajajAmt.setText(array.get(i).getAsJsonObject().get("BAJAJ_AMT").getAsString());
                                    sd.jtxtBajajPer.setText(array.get(i).getAsJsonObject().get("BAJAJ_PER").getAsString());
                                    sd.jlblBajajCharges.setText(array.get(i).getAsJsonObject().get("BAJAJ_CHG").getAsString());
                                    sd.jtxtSFID.setText(array.get(i).getAsJsonObject().get("SFID").getAsString());
                                }
                            } else {
                                if (!sd.bajaj_cd.equalsIgnoreCase("")) {
                                    sd.jcbBajaj.setSelected(false);
                                    sd.jtxtBajajCapital.setText("");
                                    sd.jtxtBajajAmt.setText("");
                                }
                            }

                        }
                    } catch (Exception ex) {
                        lb.printToLogFile("Exception", ex);
                    }

                }
            } catch (Exception ex) {
                lb.printToLogFile("Exception at getDataFor PurchaseBill", ex);
            }
        }
        OrderBookController.this.setVisible(true);
    }

    private void setAccountDetailMobile(String param_cd, String value) {
        try {
            JsonObject call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase()).execute().body();
            lb.addGlassPane(this);

            lb.removeGlassPane(OrderBookController.this);
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
                            jtxtAcName.setText(header.getAccountHeader().get(row).getFNAME());
                            jtxtAmount.requestFocusInWindow();
                        }
                    } else {
                        jtxtAcName.requestFocusInWindow();
                    }
                } else {
                    lb.showMessageDailog(header.getCause().toString());
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void saveVoucher() {
        try {
            final OrderBookModel model = new OrderBookModel();
            model.setRef_no(ref_no);
            model.setVdate(lb.ConvertDateFormetForDB(jtxtVouDate.getText()));
            model.setAc_cd(ac_cd);
            model.setAmt(lb.isNumber2(jtxtAmount.getText()));
            model.setRemark(jtxtRemark.getText());
            model.setModel_cd(model_cd);
            model.setMemory_cd(memory_cd);
            model.setColor_cd(color_cd);
            model.setUser_id(SkableHome.user_id);
            model.setCASH_AMT(lb.isNumber(sd.jtxtCashAmt.getText()));
            model.setBANK_AMT(lb.isNumber(sd.jtxtChequeAmt.getText()));
            model.setCARD_AMT(lb.isNumber(sd.jtxtCardAmt.getText()));
            model.setCARD_PER(lb.isNumber(sd.jtxtCardPer.getText()));
            model.setCARD_CHG(lb.isNumber(sd.jlblCardChanges.getText()));
            model.setBAJAJ_AMT(lb.isNumber(sd.jtxtBajajAmt.getText()));
            model.setBAJAJ_PER(lb.isNumber(sd.jtxtBajajPer.getText()));
            model.setBAJAJ_CHG(lb.isNumber(sd.jlblBajajCharges.getText()));
            model.setBANK_CD(sd.bank_cd);
            model.setCARD_NAME(sd.card_cd);
            model.setBAJAJ_NAME(sd.bajaj_cd);
            model.setSFID(sd.jtxtSFID.getText());
            model.setBANK_NAME(sd.jtxtBankName.getText());
            model.setBANK_BRANCH(sd.jtxtBranchName.getText());
            model.setCard_no(sd.jtxtCardNo.getText());
            model.setTid_no(sd.jtxtTIDNo.getText());
            model.setBranch_cd(Constants.BRANCH.get(jcmbBranch.getSelectedIndex()).getBranch_cd());
            model.setCHEQUE_NO(sd.jtxtChequeNo.getText());
            if (!sd.jtxtChequeDate.getText().equalsIgnoreCase("")) {
                model.setCHEQUE_DATE(lb.ConvertDateFormetForDB(sd.jtxtChequeDate.getText()));
            } else {
                model.setCHEQUE_DATE(null);
            }
            String detailJson = new Gson().toJson(model);
            JsonObject addUpdaCall = orderAPI.AddUpdateOrderBookVoucher(detailJson, "", SkableHome.selected_year).execute().body();
            lb.addGlassPane(OrderBookController.this);

            lb.removeGlassPane(OrderBookController.this);
            if (addUpdaCall != null) {
                System.out.println(addUpdaCall.toString());
                JsonObject object = addUpdaCall;
                if (object.get("result").getAsInt() == 1) {
                    lb.showMessageDailog("Voucher saved successfully");
                    OrderBookController.this.dispose();
                    if (odv != null) {
                        odv.jButton1.doClick();
                        if (ref_no.equalsIgnoreCase("")) {
                            odv.callPrint(object.get("ref_no").getAsString());
                        }

                    }
                } else {
                    lb.showMessageDailog(object.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OrderBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean validateVoucher() {
        if (!lb.checkDate(jtxtVouDate)) {
            lb.showMessageDailog("Invalid Date");
            return false;
        }

        if (ac_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Please select valid account");
            jtxtAcName.requestFocusInWindow();
            return false;
        }
        if (model_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Enter valid Model Name");
            jtxtModelName.requestFocusInWindow();
            return false;
        }

        if (memory_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Enter valid Memory Name");
            jtxtMemoryName.requestFocusInWindow();
            return false;
        }

        if (color_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Enter valid Color Name");
            jtxtColorName.requestFocusInWindow();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jtxtVouDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jlblVday = new javax.swing.JLabel();
        jtxtAcName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtAmount = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtModelName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtxtMemoryName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxtColorName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtxtRemark = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jbtnOK = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jcmbBranch = new javax.swing.JComboBox();
        jLabel28 = new javax.swing.JLabel();
        jtxtMobile = new javax.swing.JTextField();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jLabel1.setText("Voucher No.");

        jtxtVoucher.setEnabled(false);

        jLabel24.setText("Voucher Date");

        jtxtVouDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtVouDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtVouDateFocusLost(evt);
            }
        });
        jtxtVouDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtVouDateKeyPressed(evt);
            }
        });

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jlblVday.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jtxtAcName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAcNameKeyPressed(evt);
            }
        });

        jLabel2.setText("Account ");

        jtxtAmount.setNextFocusableComponent(jtxtRemark);
        jtxtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmountFocusGained(evt);
            }
        });
        jtxtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAmountKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmountKeyPressed(evt);
            }
        });

        jLabel3.setText("Amount");

        jLabel4.setText("Remark");

        jLabel5.setText("Model Name");

        jtxtModelName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtModelNameFocusLost(evt);
            }
        });
        jtxtModelName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtModelNameKeyPressed(evt);
            }
        });

        jLabel6.setText("Memory Name");

        jtxtMemoryName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMemoryNameFocusLost(evt);
            }
        });
        jtxtMemoryName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMemoryNameKeyPressed(evt);
            }
        });

        jLabel7.setText("Colour Name");

        jtxtColorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtColorNameFocusLost(evt);
            }
        });
        jtxtColorName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtColorNameKeyPressed(evt);
            }
        });

        jtxtRemark.setColumns(20);
        jtxtRemark.setRows(5);
        jtxtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jtxtRemark);

        jLabel12.setText("User :");

        jlblUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setText("Edit NO :");

        jlblEditNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setText("Last Updated : ");

        jlblTimeStamp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jbtnOK.setMnemonic('O');
        jbtnOK.setText("OK");
        jbtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnOKActionPerformed(evt);
            }
        });
        jbtnOK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnOKKeyPressed(evt);
            }
        });

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel8.setText("Branch");

        jcmbBranch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcmbBranch.setEnabled(false);
        jcmbBranch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbBranchKeyPressed(evt);
            }
        });

        jLabel28.setText("Mobile No");

        jtxtMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbBranch, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcName)
                    .addComponent(jtxtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtColorName, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMemoryName)
                    .addComponent(jtxtModelName)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addGap(6, 6, 6))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblVday, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnOK});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel12, jLabel13, jLabel15});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblVday))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbBranch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMobile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtModelName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMemoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtColorName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnOK))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel1, jLabel24, jlblVday, jtxtVouDate, jtxtVoucher});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel13, jLabel15, jlblEditNo, jlblTimeStamp, jlblUser});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jtxtAcName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

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

    private void jtxtVouDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVouDateFocusGained
        // TODO add your handling code here:
        jtxtVouDate.selectAll();
    }//GEN-LAST:event_jtxtVouDateFocusGained

    private void jtxtVouDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVouDateFocusLost
        // TODO add your handling code here:
        try {
            if (jtxtVouDate.getText().contains("/")) {
                jtxtVouDate.setText(jtxtVouDate.getText().replace("/", ""));
            }
            if (jtxtVouDate.getText().length() == 8) {
                String temp = jtxtVouDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtVouDate.setText(setDate);
            }
            //            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtVouDate.getText().trim()))) != null) {
            //                jtxtBillDate.requestFocusInWindow();
            //            }
            jlblVday.setText(lb.setDay(jtxtVouDate));
        } catch (Exception ex) {
            jtxtVouDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtVouDateFocusLost

    private void jtxtVouDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVouDateKeyPressed
        if (jcmbBranch.isEnabled()) {
            lb.enterFocus(evt, jcmbBranch);
        } else {
            lb.enterFocus(evt, jtxtAcName);
        }
    }//GEN-LAST:event_jtxtVouDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtVouDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtVouDate.getX() - 200, jPanel1.getY() + 150, jtxtVouDate.getX() + odc.getWidth(), jtxtVouDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jbtnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnOKActionPerformed
        // TODO add your handling code here:
        if (validateVoucher()) {
            lb.confirmDialog("Do you want to save this voucher?");
            if (lb.type) {
                sd.jlblSale.setText(lb.Convert2DecFmtForRs(lb.isNumber(jtxtAmount)));
                sd.setTotal();
                sd.setLocationRelativeTo(null);
                sd.setVisible(true);
                if (sd.getReturnStatus() == RET_OK) {
                    saveVoucher();
                }
            }
        }
    }//GEN-LAST:event_jbtnOKActionPerformed

    private void jtxtAmountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmountFocusGained

    private void jtxtAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyPressed
        lb.enterFocus(evt, jtxtModelName);
    }//GEN-LAST:event_jtxtAmountKeyPressed

    private void jtxtAmountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyTyped
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtAmountKeyTyped

    private void jtxtAcNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcNameKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_N) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                lb.confirmDialog("Do you want to create new account?");
                if (lb.type) {
                    AccountMasterController bmc = new AccountMasterController(null, true, null);
                    bmc.setLocationRelativeTo(null);
                    bmc.setVisible(true);
                }
            }
        } else if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtAcName)) {
                setAccountDetailMobile("2", jtxtAcName.getText());
            }
        }
    }//GEN-LAST:event_jtxtAcNameKeyPressed

    private void jtxtModelNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtModelNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtModelNameFocusLost

    private void jtxtModelNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtModelNameKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_N) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                ModelMasterController smc = new ModelMasterController(null, true, null);
                smc.setLocationRelativeTo(null);
                smc.setData("");
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_E) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                ModelMasterController smc = new ModelMasterController(null, true, null);
                smc.setLocationRelativeTo(null);
                smc.setData(model_cd);
            }
        }
        if (lb.isEnter(evt)) {
            setModelData("12", jtxtModelName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtModelNameKeyPressed

    private void jtxtMemoryNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMemoryNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtMemoryNameFocusLost

    private void jtxtMemoryNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMemoryNameKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_N) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                MemoryMasterController smc = new MemoryMasterController(null, true, null, "", "");
                smc.setLocationRelativeTo(null);
                smc.setVisible(true);
            }
        }
        if (lb.isEnter(evt)) {
            setMemoryMaster("13", jtxtMemoryName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtMemoryNameKeyPressed

    private void jtxtColorNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtColorNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtColorNameFocusLost

    private void jtxtColorNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtColorNameKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_N) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                ColorMasterController smc = new ColorMasterController(null, true, null, "", "");
                smc.setLocationRelativeTo(null);
                smc.setVisible(true);
            }
        }
        if (lb.isEnter(evt)) {
            setColorMaster("14", jtxtColorName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtColorNameKeyPressed

    private void jbtnOKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnOKKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnOKKeyPressed

    private void jtxtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                jbtnOK.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtRemarkKeyPressed

    private void jcmbBranchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbBranchKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jcmbBranchKeyPressed

    private void jtxtMobileKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt) && !lb.isBlank(jtxtMobile)) {
            lb.addGlassPane(this);
            Call<JsonObject> call = orderAPI.GetDataFromServer(jtxtMobile.getText(), "23", "", SkableHome.selected_year);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    lb.removeGlassPane(OrderBookController.this);
                    if (rspns.isSuccessful()) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        if (array.size() > 0) {
                            ac_cd = (array.get(0).getAsJsonObject().get("AC_CD").getAsString());
                            jtxtAcName.setText(array.get(0).getAsJsonObject().get("FNAME").getAsString());
                            jtxtMobile.setText(array.get(0).getAsJsonObject().get("MOBILE1").getAsString());
                            jtxtAmount.requestFocusInWindow();
                        } else {
                            ac_cd = "";
                            jtxtAcName.setText("");
                            lb.confirmDialog("Mobile does not exist.\nDo you want to create new account?");
                            if (lb.type) {
                                CreateSalesAccount bmc = new CreateSalesAccount(null, true);
                                bmc.setLocationRelativeTo(null);
                                bmc.setMobileNumber(jtxtMobile.getText());
                                bmc.setVisible(true);
                                if (bmc.getReturnStatus() == RET_OK) {
                                    ac_cd = bmc.ac_cd;
                                    jtxtAcName.setText(bmc.account.getFNAME());
                                    jtxtMobile.setText(bmc.account.getMOBILE1());
                                    jtxtAmount.requestFocusInWindow();
                                }
                            } else {
                                ac_cd = "";
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(OrderBookController.this);
                }
            });
        } else {
            if (lb.isEnter(evt)) {
                lb.enterFocus(evt, jtxtAcName);
            }
        }
    }//GEN-LAST:event_jtxtMobileKeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnOK;
    private javax.swing.JComboBox jcmbBranch;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JLabel jlblVday;
    private javax.swing.JTextField jtxtAcName;
    private javax.swing.JTextField jtxtAmount;
    private javax.swing.JTextField jtxtColorName;
    private javax.swing.JTextField jtxtMemoryName;
    private javax.swing.JTextField jtxtMobile;
    private javax.swing.JTextField jtxtModelName;
    private javax.swing.JTextArea jtxtRemark;
    private javax.swing.JTextField jtxtVouDate;
    private javax.swing.JTextField jtxtVoucher;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
