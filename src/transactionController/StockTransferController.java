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
import java.awt.event.ComponentEvent;
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
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import model.StockTransferDetail;
import retrofitAPI.StkTrAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;

/**
 *
 * @author bhaumik
 */
public class StockTransferController extends javax.swing.JDialog {

    private final Library lb = Library.getInstance();
    private String ref_no = "";
    private final StkTrAPI StkTrAPI;
    private String sr_cd = "";
    private String item_name = "";
    private DefaultTableModel dtm = null;
    javax.swing.JTextField jtxtTag = null, jtxtIMEI = null, jtxtSerialNo = null, jtxtQty = null,
            jtxtPurTagNo = null;
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
    private void setUpData() {
        jComboBox1.removeAllItems();
        jComboBox2.removeAllItems();
        jComboBox1.addItem("Please Select Branch");
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox1.addItem(Constants.BRANCH.get(i).getBranch_name());
            jComboBox2.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
        jComboBox2.setSelectedItem(SkableHome.selected_branch.getBranch_name());
        if (SkableHome.user_grp_cd.equalsIgnoreCase("1")) {
            jComboBox2.setEnabled(true);
        } else {
            jComboBox2.setEnabled(false);
        }
    }

    private void tableForView() {
        viewTable = new ReportTable();
        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(2, "Tax Code", 0, java.lang.String.class, null, false);
        viewTable.AddColumn(3, "Tax Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    public StockTransferController(java.awt.Frame parent, boolean modal, int vtype) {
        super(parent, modal);
        initComponents();
        addJtextBox();
        tableForView();
        setUpData();
        dtm = (DefaultTableModel) jTable1.getModel();
        StkTrAPI = lb.getRetrofit().create(StkTrAPI.class);
        jComboBox1.setSelectedIndex(vtype);

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
        lb.setDateChooserPropertyInit(jtxtVouDate);
        if (vtype == 1) {
            jButton1.setVisible(false);
        }
        setPopUp();
        
        setTitle("Stock Transfer");
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
                    Clipboard clipboard
                            = Toolkit.getDefaultToolkit().getSystemClipboard();
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

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private void addJtextBox() {
        jtxtPurTagNo = new javax.swing.JTextField();
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

        jtxtTag.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (lb.isEnter(e) && !lb.isBlank(jtxtTag)) {
                    try {
                        JsonObject call = StkTrAPI.getTagNoDetailSales("'" + jtxtTag.getText() + "'", "20", true, (jComboBox2.getSelectedIndex() + 1) + "").execute().body();
                        if (call != null) {
                            JsonArray array = call.getAsJsonArray("data");
                            if (array.size() > 0) {
                                for (int i = 0; i < array.size(); i++) {
                                    jtxtTag.setText(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                                    jtxtItem.setText(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                                    jtxtPurTagNo.setText(array.get(i).getAsJsonObject().get("REF_NO").getAsString());
                                    jtxtIMEI.setText(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                                    jtxtSerialNo.setText(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                                    sr_cd = (array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                                    item_name = (array.get(i).getAsJsonObject().get("ITEM_NAME").getAsString());
                                    jtxtQty.setText("1");
                                    jbtnAdd.doClick();
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(StockTransferController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });

        jtxtItem = new javax.swing.JTextField();
//        jtxtItem.addFocusListener(new java.awt.event.FocusAdapter() {
//
//            @Override
//            public void focusGained(FocusEvent e) {
//                lb.selectAll(e);
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//                lb.toUpper(e);
//            }
//
//        });
//
//        jtxtItem.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_N) {
//                    if (e.getModifiers() == KeyEvent.CTRL_MASK) {
//                        SeriesMasterController smc = new SeriesMasterController(null, true, null);
//                        smc.setLocationRelativeTo(null);
//                        smc.setVisible(true);
//                    }
//                }
//                if (lb.isEnter(e) && !lb.isBlank(jtxtItem)) {
//                    setSeriesData("3", jtxtItem.getText().toUpperCase(), "1");
//                }
//            }
//
//        });
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
                lb.enterFocus(e, jbtnAdd);
            }
        });

        jtxtQty.addComponentListener(new java.awt.event.ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                lb.setTable(jTable1, new JComponent[]{jtxtTag, null, null, null, null, null, null});
            }

        });

        jtxtTag.setBounds(0, 0, 20, 20);
        jtxtTag.setVisible(true);
        jPanel2.add(jtxtTag);

//        jtxtItem.setBounds(0, 0, 20, 20);
////        jtxtItem.setVisible(true);
//        jPanel2.add(jtxtItem);
//
//        jtxtIMEI.setBounds(0, 0, 20, 20);
////        jtxtIMEI.setVisible(true);
//        jPanel2.add(jtxtIMEI);
//
//        jtxtSerialNo.setBounds(0, 0, 20, 20);
////        jtxtSerialNo.setVisible(true);
//        jPanel2.add(jtxtSerialNo);
//
//        jtxtQty.setBounds(0, 0, 20, 20);
////        jtxtQty.setVisible(true);
//        jPanel2.add(jtxtQty);
        lb.setTable(jTable1, new JComponent[]{jtxtTag, null, null, null, null, null, null});
    }

//    private void setSeriesData(String param_cd, String value, final String mode) {
//        try {
//            Call<SeriesHead> call = lb.getStartUpAPI().getSeriesMaster(param_cd, value.toUpperCase());
//            call.enqueue(new Callback<SeriesHead>() {
//
//                @Override
//                public void onResponse(Response<SeriesHead> response, Retrofit rtrft) {
//                    if (response.isSuccess()) {
//                        System.out.println(response.body().toString());
//                        SeriesHead header = (SeriesHead) response.body();
//                        if (header.getResult() == 1) {
//                            final SelectDailog sa = new SelectDailog(null, true);
//                            sa.setData(viewTable);
//                            sa.setLocationRelativeTo(null);
//                            ArrayList<SeriesMaster> series = (ArrayList<SeriesMaster>) response.body().getAccountHeader();
//                            sa.getDtmHeader().setRowCount(0);
//                            for (int i = 0; i < series.size(); i++) {
//                                Vector row = new Vector();
//                                row.add(series.get(i).getSRCD());
//                                row.add(series.get(i).getSRNAME());
//                                row.add(series.get(i).getTAXCD());
//                                row.add(series.get(i).getTAXNAME());
//                                sa.getDtmHeader().addRow(row);
//                            }
//                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
//                            sa.setVisible(true);
//                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
//                                int row = viewTable.getSelectedRow();
//                                if (row != -1) {
//                                    if (mode.equalsIgnoreCase("1")) {
//                                        sr_cd = viewTable.getValueAt(row, 0).toString();
//                                        item_name = viewTable.getValueAt(row, 1).toString();
//                                        jtxtItem.setText(viewTable.getValueAt(row, 1).toString());
//                                        jtxtIMEI.requestFocusInWindow();
//                                    }
//                                }
//                                sa.dispose();
//                            }
//                        } else {
//                            lb.showMessageDailog(response.body().getCause().toString());
//                        }
//                    } else {
//                        // handle request errors yourself
//                        lb.showMessageDailog(response.message());
//                    }
//                }
//
//                @Override
//
//                public void onFailure(Throwable thrwbl
//                ) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                }
//            }
//            );
//        } catch (Exception ex) {
//            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
//        }
//
//    }
    public void setData(String ref_no) {
        this.ref_no = ref_no;

        if (!ref_no.equalsIgnoreCase("")) {
            try {
                JsonObject call = StkTrAPI.getBill(ref_no).execute().body();

                if (call != null) {
                    System.out.println(call.toString());
                    JsonObject object = call;

                    JsonArray array = object.get("data").getAsJsonArray();
                    try {
                        for (int i = 0; i < array.size(); i++) {
                            StockTransferController.this.ref_no = array.get(i).getAsJsonObject().get("REF_NO").getAsString();
                            jtxtVoucher.setText(array.get(i).getAsJsonObject().get("REF_NO").getAsString() + "");
                            jComboBox1.setSelectedIndex(array.get(i).getAsJsonObject().get("to_loc").getAsInt());
                            jtxtVouDate.setText(lb.ConvertDateFormetForDBForConcurrency(array.get(i).getAsJsonObject().get("V_DATE").getAsString()));
                            jlblVday.setText(lb.setDay(jtxtVouDate));
                            jTextArea1.setText(array.get(i).getAsJsonObject().get("REMARK").getAsString());

                            Vector row = new Vector();
                            row.add(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("QTY").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("PUR_TAG_NO").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                            dtm.addRow(row);
                        }
                    } catch (Exception ex) {
                        lb.printToLogFile("Exception", ex);
                    }
                    StockTransferController.this.setVisible(true);
                } else {
                    lb.showMessageDailog(call.get("cause").toString());
                }

            } catch (Exception ex) {
                lb.printToLogFile("Exception at getDataFor PurchaseBill", ex);
            }
        } else {
            StockTransferController.this.setVisible(true);
        }

    }

    private boolean validateVoucher() {

        if (lb.ConvertDateFormetForDB(jtxtVouDate.getText()).equalsIgnoreCase("")) {
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
        jtxtPurTagNo.setText("");
    }

    private void saveVoucher() throws IOException {
        ArrayList<StockTransferDetail> detail = new ArrayList<>();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            StockTransferDetail row = new StockTransferDetail();
            row.setRef_no(ref_no);
            row.setV_date(lb.ConvertDateFormetForDB(jtxtVouDate.getText()));
            row.setFrom_loc((jComboBox2.getSelectedIndex() + 1) + "");
            row.setTo_loc(Constants.BRANCH.get(jComboBox1.getSelectedIndex() - 1).getBranch_cd());
            row.setUser_id(SkableHome.user_id);
            row.setTag_no(jTable1.getValueAt(i, 5).toString());
            row.setRemark(jTextArea1.getText());
            detail.add(row);
        }

        String detailJson = new Gson().toJson(detail);

        JsonObject addUpdaCall = StkTrAPI.AddUpdateStkAdjBill(detailJson).execute().body();
        lb.addGlassPane(this);

        lb.removeGlassPane(StockTransferController.this);
        if (addUpdaCall != null) {
            System.out.println(addUpdaCall.toString());
            JsonObject object = addUpdaCall;
            if (object.get("result").getAsInt() == 1) {
                lb.showMessageDailog("Voucher saved successfully");
                StockTransferController.this.dispose();
            } else {
                lb.showMessageDailog(object.get("Cause").getAsString());
            }
        } else {
            lb.showMessageDailog(addUpdaCall.get("Cause").toString());
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jtxtVouDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jbtnAdd = new javax.swing.JButton();
        jlblVday = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

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

        jLabel2.setText("Transfer To");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Godown", "Shop" }));
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel5.setText("From");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnAdd))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblVday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnAdd))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jComboBox1, jLabel1, jLabel2, jLabel24, jlblVday, jtxtVouDate, jtxtVoucher});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox2, jLabel5});

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 921, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        jPanel3.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag ", "Product Name", "IMEI", "SERIAL No", "Qty", "PUR_REF_NO", "sr_cd"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(5).setMinWidth(0);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
        }

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton1.setMnemonic('S');
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

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
                        .addGap(0, 781, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
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
        if (SkableHome.user_grp_cd.equalsIgnoreCase("1")) {
                jComboBox2.requestFocusInWindow();
            } else {
                jComboBox1.requestFocusInWindow();
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

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        // TODO add your handling code here:
        if (validateRow()) {
            int index = jTable1.getSelectedRow();
            if (index == -1) {
                Vector row = new Vector();
                row.add(jtxtTag.getText());
                row.add(item_name);
                row.add(jtxtIMEI.getText());
                row.add(jtxtSerialNo.getText());
                row.add(lb.isNumber2(jtxtQty.getText()));
                row.add(jtxtPurTagNo.getText());
                row.add(sr_cd);
                dtm.addRow(row);
            } else if (index != -1) {
                jTable1.setValueAt(jtxtTag.getText(), index, 0);
                jTable1.setValueAt(item_name, index, 1);
                jTable1.setValueAt(jtxtIMEI.getText(), index, 2);
                jTable1.setValueAt(jtxtSerialNo.getText(), index, 3);
                jTable1.setValueAt(lb.isNumber2(jtxtQty.getText()), index, 4);
                jTable1.setValueAt(jtxtPurTagNo.getText(), index, 7);
                jTable1.setValueAt(sr_cd, index, 8);
            }
            clear();
//            lb.confirmDialog("Do you want to add another item?");
//            if (lb.type) {
            jtxtTag.requestFocusInWindow();
//            } else {
//                jButton1.requestFocusInWindow();
//            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
//        if (!navLoad.getMode().equalsIgnoreCase("")) {
//            if (evt.getClickCount() == 2) {
//                int index = jTable1.getSelectedRow();
//                evt.consume();
//                jtxtTag.setText(jTable1.getValueAt(index, 0).toString());
//                jtxtProductName.setText(jTable1.getValueAt(index, 1).toString());
//                jtxtIMEINo.setText(jTable1.getValueAt(index, 2).toString());
//                jtxtSerailNo.setText(jTable1.getValueAt(index, 3).toString());
//                jtxtQty.setText(jTable1.getValueAt(index, 4).toString());
//                jtxtRate.setText(jTable1.getValueAt(index, 5).toString());
//                jtxtAmount.setText(jTable1.getValueAt(index, 6).toString());
//                jtxtTagRefNo.setText(jTable1.getValueAt(index, 7).toString());
//            }
//        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:

        int index = jTable1.getSelectedRow();
        if (index != -1) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                lb.confirmDialog("Do you want to delete this row?");
                if (lb.type) {
                    dtm.removeRow(index);
                }
            }

            if (evt.getKeyCode() == KeyEvent.VK_D) {
                if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                    lb.confirmDialog("Do you want to delete this row?");
                    if (lb.type) {
                        dtm.removeRow(index);
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (validateVoucher()) {
            try {
                if (jComboBox1.getSelectedIndex() != 0) {
                    if (!((jComboBox2.getSelectedIndex() + 1) + "").equalsIgnoreCase(Constants.BRANCH.get(jComboBox1.getSelectedIndex() - 1).getBranch_cd())) {
                        saveVoucher();
                    } else {
                        lb.showMessageDailog("You can not transfer to your branch");
                    }
                } else {
                    lb.showMessageDailog("Please select branch");
                }
            } catch (IOException ex) {
                lb.printToLogFile("Exception at saveVoucher", ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jComboBox1);
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtTag);
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JLabel jlblVday;
    private javax.swing.JTextField jtxtVouDate;
    private javax.swing.JTextField jtxtVoucher;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
