/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Container;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import masterController.CreateSalesAccount;
import masterController.SeriesMasterController;
import model.AccountHead;
import model.SalesBillDetail;
import model.SeriesHead;
import model.SeriesMaster;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.DCAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author bhaumik
 */
public class DCController extends javax.swing.JDialog {

    private Library lb = Library.getInstance();
    private String ref_no = "";
    private DCAPI dcAPI = null;
    private String ac_cd = "";
    private String sr_cd = "";
    private String item_name = "";
    private DefaultTableModel dtm = null;
    javax.swing.JTextField jtxtTag = null, jtxtIMEI = null, jtxtSerialNo = null, jtxtQty = null,
            jtxtRate = null, jtxtAmount = null, jtxtPurTagNo = null, jtxtRemark = null;
    javax.swing.JTextField jtxtItem = null;
    private ReportTable viewTable = null;
    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form SalesBillDetailController
     */
    public DCController(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        addJtextBox();
        tableForView();
        dtm = (DefaultTableModel) jTable1.getModel();
        dcAPI = lb.getRetrofit().create(DCAPI.class);

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
        setPopUp();
        SkableHome.zoomTable.setToolTipOn(true);
        final Container zoomIFrame = this;
        jTable1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                SkableHome.zoomTable.zoomInToolTipForTable(jTable1, jScrollPane1, zoomIFrame, evt);
            }
        });
    }

    private void setPopUp() {
        final JPopupMenu popup = new JPopupMenu();
        ActionListener menuListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                popup.setVisible(false);
                int row = jTable1.getSelectedRow();
                int column = jTable1.getSelectedColumn();
                if (row != -1 && column != -1) {
                    String selection = jTable1.getValueAt(row, column).toString();
                    StringSelection data = new StringSelection(selection);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(data, data);
                }
            }
        };
        final JMenuItem item;
        popup.add(item = new JMenuItem("COPY"));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
        popup.setLocation(MouseInfo.getPointerInfo().getLocation());
        jTable1.setComponentPopupMenu(popup);
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(2, "Tax Code", 0, java.lang.String.class, null, false);
        viewTable.AddColumn(3, "Tax Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    public DCController(java.awt.Frame parent, boolean modal, int vtype) {
        super(parent, modal);
        initComponents();
        addJtextBox();
        tableForView();
        dtm = (DefaultTableModel) jTable1.getModel();
        dcAPI = lb.getRetrofit().create(DCAPI.class);

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
        jcmbType.setSelectedIndex(vtype);
        jcmbType.setEnabled(false);
        lb.setDateChooserPropertyInit(jtxtVouDate);
        if (vtype == 1) {
            jtxtTag.setVisible(false);
            setTitle("DC Receipt");
        } else {
            setTitle("DC Issue");

        }
        setPopUp();
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private void addJtextBox() {
        jtxtPurTagNo = new javax.swing.JTextField();
        jtxtRemark = new javax.swing.JTextField();

        jtxtTag = new javax.swing.JTextField();
        jtxtTag.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toUpper(e);
            }
        });

        jtxtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toUpper(e);
            }
        });

        jtxtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (lb.isEnter(e)) {
                    jbtnAdd.requestFocusInWindow();
                }
            }
        });

        jtxtTag.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (lb.isEnter(e) && !lb.isBlank(jtxtTag)) {
                    try {
                        JsonObject call = dcAPI.getTagNoDetailSales("'" + jtxtTag.getText() + "'", "15", true).execute().body();

                        if (call != null) {
                            JsonArray array = call.getAsJsonArray("data");
                            if (array.size() > 0) {
                                jtxtTag.setText(array.get(0).getAsJsonObject().get("TAG_NO").getAsString());
                                jtxtItem.setText(array.get(0).getAsJsonObject().get("ITEM_NAME").getAsString());
                                jtxtPurTagNo.setText(array.get(0).getAsJsonObject().get("REF_NO").getAsString());
                                jtxtIMEI.setText(array.get(0).getAsJsonObject().get("IMEI_NO").getAsString());
                                jtxtSerialNo.setText(array.get(0).getAsJsonObject().get("SERAIL_NO").getAsString());
                                sr_cd = (array.get(0).getAsJsonObject().get("SR_CD").getAsString());
                                item_name = (array.get(0).getAsJsonObject().get("ITEM_NAME").getAsString());
                                jtxtQty.setText("1");
                                jtxtRate.requestFocusInWindow();
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        jtxtItem = new javax.swing.JTextField();
        jtxtItem.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                lb.toUpper(e);
            }
        });

        jtxtItem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_N) {
                    if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                        SeriesMasterController smc = new SeriesMasterController(null, true, null);
                        smc.setLocationRelativeTo(null);
                        smc.setVisible(true);
                    }
                }
                if (lb.isEnter(e) && !lb.isBlank(jtxtItem)) {
                    setSeriesData("3", jtxtItem.getText().toUpperCase());
                }
            }
        });

        jtxtIMEI = new javax.swing.JTextField();
        jtxtIMEI.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toUpper(e);
            }
        });

        jtxtIMEI.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jtxtSerialNo);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                lb.onlyNumber(e, 15);
            }
        });

        jtxtSerialNo = new javax.swing.JTextField();
        jtxtSerialNo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toUpper(e);
            }
        });

        jtxtSerialNo.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jtxtQty);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                lb.fixLength(e, 20);
            }
        });

        jtxtQty = new javax.swing.JTextField();
        jtxtQty.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toInteger(e);
            }
        });

        jtxtQty.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jtxtRate);
            }
        });

        jtxtRate = new javax.swing.JTextField();

        jtxtRate.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                calculation();
                lb.toDouble(e);
            }
        });

        jtxtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_D) {
//                    if (e.getModifiers() == KeyEvent.CTRL_MASK) {
//                        String tax_cd = lb.getTaxCode(jcmbTax.getSelectedItem().toString(), "C");
//                        double tax_rate = lb.isNumber2(lb.getTaxCode(tax_cd + "", "CR"));
//                        double basic_amt = lb.isNumber2(jtxtRate.getText());
//                        double add_tax_rate = lb.isNumber2(lb.getTaxCode(tax_cd + "", "CRA"));
//                        double tax = tax_rate + add_tax_rate + 100;
//                        tax = tax / 100.0;
//                        jtxtRate.setText(lb.roundOffDoubleValue(basic_amt * tax));
//                    }
//                }
                lb.enterFocus(e, jtxtRemark);
            }
        });

        jtxtAmount = new javax.swing.JTextField();

        jtxtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toDouble(e);
            }
        });

        jtxtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jbtnAdd);
            }
        });

        jtxtAmount.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                lb.setTable(jTable1, new JComponent[]{jtxtTag, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, jtxtAmount, jtxtRemark, null, null});
            }
        });

        jtxtTag.setBounds(0, 0, 20, 20);
        jtxtTag.setVisible(true);
        jPanel2.add(jtxtTag);

        jtxtItem.setBounds(0, 0, 20, 20);
        jtxtItem.setVisible(true);
        jPanel2.add(jtxtItem);

        jtxtIMEI.setBounds(0, 0, 20, 20);
        jtxtIMEI.setVisible(true);
        jPanel2.add(jtxtIMEI);

        jtxtSerialNo.setBounds(0, 0, 20, 20);
        jtxtSerialNo.setVisible(true);
        jPanel2.add(jtxtSerialNo);

        jtxtQty.setBounds(0, 0, 20, 20);
        jtxtQty.setVisible(true);
        jPanel2.add(jtxtQty);

        jtxtRate.setBounds(0, 0, 20, 20);
        jtxtRate.setVisible(true);
        jPanel2.add(jtxtRate);

        jtxtAmount.setBounds(0, 0, 20, 20);
        jtxtAmount.setVisible(true);
        jPanel2.add(jtxtAmount);

        jtxtRemark.setBounds(0, 0, 20, 20);
        jtxtRemark.setVisible(true);
        jPanel2.add(jtxtRemark);

        lb.setTable(jTable1, new JComponent[]{jtxtTag, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, jtxtAmount, jtxtRemark, null, null});
    }

    private void setSeriesData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(DCController.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        SeriesHead header = (SeriesHead) new Gson().fromJson(response.body(), SeriesHead.class);
                        if (header.getResult() == 1) {
                            final SelectDailog sa = new SelectDailog(null, true);
                            sa.setData(viewTable);
                            sa.setLocationRelativeTo(null);
                            ArrayList<SeriesMaster> series = (ArrayList<SeriesMaster>) header.getAccountHeader();
                            sa.getDtmHeader().setRowCount(0);
                            for (int i = 0; i < series.size(); i++) {
                                Vector row = new Vector();
                                row.add(series.get(i).getSRCD());
                                row.add(series.get(i).getSRNAME());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    sr_cd = viewTable.getValueAt(row, 0).toString();
                                    item_name = viewTable.getValueAt(row, 1).toString();
                                    jtxtItem.setText(viewTable.getValueAt(row, 1).toString());
                                    if (jcmbType.getSelectedIndex() == 0) {
                                        jtxtRate.requestFocusInWindow();
                                    } else {
                                        jtxtIMEI.requestFocusInWindow();
                                    }
                                }
                                sa.dispose();
                            }
                        } else {
                            lb.showMessageDailog(header.getCause().toString());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(DCController.this);
                }
            });
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void calculation() {
        double qty = lb.isNumber(jtxtQty);
        double rate = lb.isNumber(jtxtRate);
        jtxtAmount.setText(lb.Convert2DecFmtForRs(rate * qty));

    }

    public void setData(String ref_no) {
        this.ref_no = ref_no;

        if (!ref_no.equalsIgnoreCase("")) {
            try {
                Call<JsonObject> call = dcAPI.getBill(ref_no);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            System.out.println(response.body().toString());
                            JsonObject object = response.body();

                            JsonArray array = object.get("data").getAsJsonArray();
                            try {
                                for (int i = 0; i < array.size(); i++) {
                                    DCController.this.ref_no = array.get(i).getAsJsonObject().get("REF_NO").getAsString();
                                    jtxtVoucher.setText(array.get(i).getAsJsonObject().get("INV_NO").getAsInt() + "");
                                    jtxtVouDate.setText(lb.ConvertDateFormetForDBForConcurrency(array.get(i).getAsJsonObject().get("V_DATE").getAsString()));
                                    jcmbType.setSelectedIndex(array.get(i).getAsJsonObject().get("V_TYPE").getAsInt());
                                    ac_cd = array.get(i).getAsJsonObject().get("AC_CD").getAsString();
                                    jtxtAcName.setText(array.get(i).getAsJsonObject().get("FNAME").getAsString());
                                    jtxtAcAlias.setText(ac_cd);
//                                    jtxtAddress.setText(array.get(i).getAsJsonObject().get("ADD1").getAsString());
                                    jtxtMobile.setText(array.get(i).getAsJsonObject().get("MOBILE1").getAsString());
//                                    jtxtTinNum.setText(array.get(i).getAsJsonObject().get("TIN").getAsString());
//                                    jlblUser.setText(array.get(i).getAsJsonObject().get("USER_ID").getAsInt() + "");
//                                    jlblEditNo.setText(array.get(i).getAsJsonObject().get("EDIT_NO").getAsDouble() + "");
//                                    jlblTimeStamp.setText(array.get(i).getAsJsonObject().get("TIME_STAMP").getAsString());
                                    jlblVday.setText(lb.setDay(jtxtVouDate));

                                    Vector row = new Vector();
                                    row.add(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                                    row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                                    row.add(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                                    row.add(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                                    row.add(array.get(i).getAsJsonObject().get("QTY").getAsDouble());
                                    row.add(array.get(i).getAsJsonObject().get("RATE").getAsDouble());
                                    row.add(array.get(i).getAsJsonObject().get("AMT").getAsDouble());
                                    row.add(array.get(i).getAsJsonObject().get("REMARK").getAsString());
                                    row.add(array.get(i).getAsJsonObject().get("PUR_TAG_NO").getAsString());
                                    row.add(array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                                    dtm.addRow(row);

                                }
                                setTotal();
                                jtxtVouDate.requestFocusInWindow();
                            } catch (Exception ex) {
                                lb.printToLogFile("Exception", ex);
                            }
                            DCController.this.setVisible(true);
                        } else {
                            lb.showMessageDailog(response.body().get("cause").toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    }
                });
            } catch (Exception ex) {
                lb.printToLogFile("Exception at getDataFor PurchaseBill", ex);
            }
        } else {
            DCController.this.setVisible(true);
        }

    }

    private void setTotal() {
        double tot = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            tot += lb.isNumber2(jTable1.getValueAt(i, 6).toString());
        }
        jlblTotal.setText(lb.Convert2DecFmtForRs(tot));
    }

    private boolean validateVoucher() {
        if (ac_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Please select valid account");
            return false;
        }

        if (!lb.checkDate(jtxtVouDate)) {
            lb.showMessageDailog("Invalid Voucher Date");
            jtxtVouDate.requestFocusInWindow();
            return false;
        }

        if (jTable1.getRowCount() == 0) {
            lb.showMessageDailog("Please Insert Value in Voucher");
            jtxtItem.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void setAccountDetailMobile(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(DCController.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        AccountHead header = (AccountHead) new Gson().fromJson(response.body(), AccountHead.class);
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
                                    jtxtItem.requestFocusInWindow();
                                }
                            }
                        } else {
                            lb.showMessageDailog(header.getCause().toString());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(DCController.this);
                }
            });
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private boolean validateRow() {
        if (sr_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Please enter valid item");
            return false;
        }

        if (lb.isNumber(jtxtQty) < 0) {
            lb.showMessageDailog("Please enter valid qty");
            jtxtQty.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void clear() {
        jtxtTag.setText("");
        jtxtItem.setText("");
        sr_cd = "";
        item_name = "";
        jtxtIMEI.setText("");
        jtxtSerialNo.setText("");
        jtxtQty.setText("");
        jtxtRate.setText("");
        jtxtAmount.setText("");
        jtxtPurTagNo.setText("");
    }

    private void saveVoucher() {
        ArrayList<SalesBillDetail> detail = new ArrayList<>();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            SalesBillDetail row = new SalesBillDetail();
            row.setRef_no(ref_no);
            row.setAc_cd(ac_cd);
            row.setUser_id(SkableHome.user_id);
            row.setV_date(lb.ConvertDateFormetForDB(jtxtVouDate.getText()));
            row.setV_type(jcmbType.getSelectedIndex() + "");
            row.setDet_tot(lb.isNumber(jlblTotal));
            row.setTag_no(jTable1.getValueAt(i, 0).toString());
            row.setImei_no(jTable1.getValueAt(i, 2).toString());
            row.setSerial_no(jTable1.getValueAt(i, 3).toString());
            row.setQty((int) lb.isNumber(jTable1.getValueAt(i, 4).toString()));
            row.setRate(lb.isNumber(jTable1.getValueAt(i, 5).toString()));
            row.setAmt(lb.isNumber(jTable1.getValueAt(i, 6).toString()));
            row.setRemark(jTable1.getValueAt(i, 7).toString());
            row.setPur_tag_no(jTable1.getValueAt(i, 8).toString());
            row.setSr_cd(jTable1.getValueAt(i, 9).toString());
            row.setName(jtxtAcName.getText());
            row.setMobile(jtxtMobile.getText());
            detail.add(row);
        }

        String detailJson = new Gson().toJson(detail);

        Call<JsonObject> addUpdaCall = dcAPI.AddUpdateSalesBill(detailJson);
        lb.addGlassPane(this);
        addUpdaCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                lb.removeGlassPane(DCController.this);
                if (response.isSuccessful()) {
                    System.out.println(response.body().toString());
                    JsonObject object = response.body();
                    if (object.get("result").getAsInt() == 1) {
                        lb.showMessageDailog("Voucher saved successfully");
                        DCController.this.dispose();
                    } else {
                        lb.showMessageDailog(object.get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(response.body().get("Cause").toString());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(DCController.this);
            }
        });
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jtxtVouDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jcmbType = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jtxtAcAlias = new javax.swing.JTextField();
        jtxtAcName = new javax.swing.JTextField();
        jtxtAddress = new javax.swing.JTextField();
        jtxtMobile = new javax.swing.JTextField();
        jbtnAdd = new javax.swing.JButton();
        jlblVday = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jtxtCardNo = new javax.swing.JTextField();
        jtxtTin = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jlblTotal = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Voucher No.");

        jtxtVoucher.setEnabled(false);
        jtxtVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtVoucherKeyPressed(evt);
            }
        });

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

        jLabel2.setText("Type");

        jcmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Issue", "Receipt" }));
        jcmbType.setEnabled(false);
        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        jLabel23.setText("A/C Name :");

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

        jbtnAdd.setText("ADD");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });
        jbtnAdd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnAddKeyPressed(evt);
            }
        });

        jlblVday.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel27.setText("Card Number :");

        jtxtCardNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardNoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(jtxtCardNo)
                        .addGap(435, 435, 435)
                        .addComponent(jbtnAdd))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlblVday, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 333, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAcName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jtxtTin, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
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
                    .addComponent(jlblVday)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtTin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCardNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnAdd))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel1, jLabel2, jLabel24, jcmbType, jlblVday, jtxtVouDate, jtxtVoucher});

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel3.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag ", "Product Name", "IMEI", "SERIAL No", "Qty", "Rate", "Amount", "Remark", "PUR_REF_NO", "sr_cd"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(110);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(110);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(8).setMinWidth(0);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(8).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(9).setMinWidth(0);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(9).setMaxWidth(0);
        }

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton1.setMnemonic('S');
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jlblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTotal.setText("0.00");
        jlblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(139, Short.MAX_VALUE)
                .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jButton1))
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

    private void jtxtVoucherKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtVoucherKeyPressed

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
            lb.showMessageDailog("Enter Correct Date");
            jtxtVouDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtVouDateFocusLost

    private void jtxtVouDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVouDateFocusGained
        // TODO add your handling code here:
        jtxtVouDate.selectAll();
    }//GEN-LAST:event_jtxtVouDateFocusGained

    private void jtxtVouDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVouDateKeyPressed
        lb.enterFocus(evt, jtxtAcAlias);
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

    private void jcmbTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTypeKeyPressed
        lb.enterFocus(evt, jtxtAcAlias);
    }//GEN-LAST:event_jcmbTypeKeyPressed

    private void jtxtAcAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcAliasFocusGained

    private void jtxtAcAliasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusLost
    }//GEN-LAST:event_jtxtAcAliasFocusLost

    private void jtxtAcAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcAliasKeyPressed

        if (lb.isEnter(evt)) {
            if (lb.isBlank(jtxtAcAlias)) {
                lb.confirmDialog("Do you want to create new account?");
                if (lb.type) {
                    CreateSalesAccount bmc = new CreateSalesAccount(null, true);
                    bmc.setLocationRelativeTo(null);
                    bmc.setVisible(true);
                    if (bmc.getReturnStatus() == RET_OK) {
                        ac_cd = bmc.ac_cd;
                        jtxtAcAlias.setText(ac_cd);
                        jtxtAcName.setText(bmc.account.getFNAME());
                        jtxtAddress.setText(bmc.account.getADD1());
                        jtxtMobile.setText(bmc.account.getMOBILE1());
                        jtxtCardNo.setText(bmc.account.getCARD_NO());
                        jtxtTin.setText(bmc.account.getTIN());
                        jtxtItem.requestFocusInWindow();
                    }
                } else {
                    ac_cd = "";
                }
            } else {
                setAccountDetailMobile("2", jtxtAcAlias.getText());
            }
        }
    }//GEN-LAST:event_jtxtAcAliasKeyPressed

    private void jtxtAcAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcAliasKeyReleased
    }//GEN-LAST:event_jtxtAcAliasKeyReleased

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        // TODO add your handling code here:
        String tag = "";
        if (!jtxtIMEI.getText().equalsIgnoreCase("")) {
            tag = (jtxtIMEI.getText());
        } else if (!jtxtSerialNo.getText().equalsIgnoreCase("")) {
            tag = (jtxtSerialNo.getText());
        } else {
            tag = "";
        }
        if (validateRow()) {
            int index = jTable1.getSelectedRow();
            if (index == -1) {
                Vector row = new Vector();
                if (jcmbType.getSelectedIndex() == 1) {
                    if (!jtxtIMEI.getText().equalsIgnoreCase("")) {
                        row.add(jtxtIMEI.getText());
                    } else if (!jtxtSerialNo.getText().equalsIgnoreCase("")) {
                        row.add(jtxtSerialNo.getText());
                    } else {
                        row.add("");
                    }
                } else {
                    row.add(jtxtTag.getText());
                }
                row.add(item_name);
                row.add(jtxtIMEI.getText());
                row.add(jtxtSerialNo.getText());
                row.add(lb.isNumber2(jtxtQty.getText()));
                row.add(lb.isNumber2(jtxtRate.getText()));
                row.add(lb.isNumber2(jtxtAmount.getText()));
                if (jtxtRemark.getText().equalsIgnoreCase("")) {
                    row.add(jtxtIMEI.getText());
                } else {
                    row.add(jtxtRemark.getText());
                }
                row.add(jtxtPurTagNo.getText());
                row.add(sr_cd);
                dtm.addRow(row);
            } else if (index != -1) {
                jTable1.setValueAt(jtxtTag.getText(), index, 0);
                jTable1.setValueAt(item_name, index, 1);
                jTable1.setValueAt(lb.isNumber2(jtxtQty.getText()), index, 4);
                jTable1.setValueAt(lb.isNumber2(jtxtRate.getText()), index, 5);
                jTable1.setValueAt(lb.isNumber2(jtxtAmount.getText()), index, 6);
                jTable1.setValueAt(jtxtRemark.getText(), index, 7);
                jTable1.setValueAt(jtxtPurTagNo.getText(), index, 8);
                jTable1.setValueAt(sr_cd, index, 9);
            }
            clear();
            lb.confirmDialog("Do you want to add another item?");
            if (lb.type) {
                if (jcmbType.getSelectedIndex() == 0) {
                    jtxtTag.requestFocusInWindow();
                } else {
                    jtxtItem.requestFocusInWindow();
                }
            } else {
                jButton1.requestFocusInWindow();
            }
        }
        setTotal();
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jtxtCardNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardNoKeyPressed
        // TODO add your handling code here:
//        if (lb.isEnter(evt)) {
//            String ac_cd = lb.getData("ac_cd", "acntmst", "card_no", jtxtCardNo.getText(), 0);
//            jtxtAcAlias.setText(lb.getAcCode(ac_cd, "CA"));
//            jtxtAcName.setText(lb.getAcCode(ac_cd, "N"));
//            jtxtAddress.setText(lb.getData("ADD1", "adbkmst", "ac_cd", ac_cd, 0));
//            jtxtCardNo.setText(lb.getData("CARD_NO", "ACNTMST", "ac_cd", ac_cd, 0));
//            jtxtMobile.setText(lb.getData("mobile1", "phbkmst", "ac_cd", ac_cd, 0));
//            jtxtTag.requestFocusInWindow();
//        }
    }//GEN-LAST:event_jtxtCardNoKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int index = jTable1.getSelectedRow();
            evt.consume();
            jtxtTag.setText(jTable1.getValueAt(index, 0).toString());
            jtxtItem.setText(jTable1.getValueAt(index, 1).toString());
            item_name = (jTable1.getValueAt(index, 1).toString());
            jtxtIMEI.setText(jTable1.getValueAt(index, 2).toString());
            jtxtSerialNo.setText(jTable1.getValueAt(index, 3).toString());
            jtxtQty.setText(jTable1.getValueAt(index, 4).toString());
            jtxtRate.setText(jTable1.getValueAt(index, 5).toString());
            jtxtAmount.setText(jTable1.getValueAt(index, 6).toString());
            jtxtRemark.setText(jTable1.getValueAt(index, 7).toString());
            jtxtPurTagNo.setText(jTable1.getValueAt(index, 8).toString());
            sr_cd = (jTable1.getValueAt(index, 9).toString());
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:

        int index = jTable1.getSelectedRow();
        if (index != -1) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                lb.confirmDialog("Do you want to delete this row?");
                if (lb.type) {
                    dtm.removeRow(index);
                    setTotal();
                }
            }

            if (evt.getKeyCode() == KeyEvent.VK_D) {
                if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                    lb.confirmDialog("Do you want to delete this row?");
                    if (lb.type) {
                        dtm.removeRow(index);
                        setTotal();
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (validateVoucher()) {
            saveVoucher();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JLabel jlblVday;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAcName;
    private javax.swing.JTextField jtxtAddress;
    private javax.swing.JTextField jtxtCardNo;
    private javax.swing.JTextField jtxtMobile;
    private javax.swing.JTextField jtxtTin;
    private javax.swing.JTextField jtxtVouDate;
    private javax.swing.JTextField jtxtVoucher;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
