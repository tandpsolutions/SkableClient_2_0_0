/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import masterController.CreateSalesAccount;
import masterController.SeriesMasterController;
import model.AccountHead;
import model.SalesControllerDetailModel;
import model.SalesReturnControllerDetailModel;
import model.SalesReturnControllerHeaderModel;
import model.SeriesHead;
import model.SeriesMaster;
import model.TaxMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.SalesReturnAPI;
import retrofitAPI.StartUpAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author bhaumik
 */
public class SalesReturnController extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    Library lb = Library.getInstance();
    private String ac_cd = "";
    private String ref_no = "";
    private javax.swing.JTextField jtxtTag = null, jtxtIMEI = null, jtxtSerialNo = null, jtxtQty = null, jtxtRate = null, jtxtAmount = null, jtxtBasicAmt = null, jtxtTaxAmt = null, jtxtAddTaxAmt = null;
    private javax.swing.JTextField jtxtDiscPer = null, jtxtMRP = null;
    javax.swing.JTextField jtxtItem = null;
    JLabel jlblTotQty;
    JLabel jlblTotAmt;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    DefaultTableModel dtm = null;
    int type = -1;
    boolean flag = false;
    SalesReturnAPI salesReturnAPI = null;
    private ReportTable viewTable = null;
    private String sr_cd = "";
    private String item_name = "";
    private ArrayList<SalesControllerDetailModel> subDetail = new ArrayList<SalesControllerDetailModel>();
//    private double pur_rate = 0.00;
    private String pur_tag_no = "";
    private SalesPaymentDialog sd = null;
    private HashMap<String, double[]> taxInfo;
    private DefaultTableModel dtmTax;
    private double sale_rate = 0.00;
    private int tax_type;

    /**
     * Creates new form PurchaseController
     */
    public SalesReturnController(java.awt.Frame parent, boolean modal, int tax_type) {
        super(parent, modal);
        initComponents();
        this.tax_type = tax_type;
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmTax = (DefaultTableModel) jTable2.getModel();

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
        this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        sd = new SalesPaymentDialog(null, true);

        lb.setDateChooserPropertyInit(jtxtVouDate);
        addJtextBox();
        addJLabel();
        addTaxCombo();
        tableForView();
        searchOnTextFields();
        flag = true;
        setUpData();
        taxInfo = new HashMap<String, double[]>();
        setPopUp();
        setTitle("Sales Return");
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

    private void setUpData() {
        jComboBox1.removeAllItems();
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox1.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
        jComboBox1.setSelectedItem(SkableHome.selected_branch.getBranch_name());
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(2, "Tax Code", 0, java.lang.String.class, null, false);
        viewTable.AddColumn(3, "Tax Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    private void addTaxCombo() {
        jcmbTax.removeAllItems();
        for (int i = 0; i < Constants.TAX.size(); i++) {
            jcmbTax.addItem(Constants.TAX.get(i).getTAXNAME());
        }
    }

    private void addJLabel() {
        jlblTotQty = new javax.swing.JLabel("0");
        jlblTotAmt = new javax.swing.JLabel("0.00");
        jlblTotQty.setHorizontalAlignment(SwingConstants.RIGHT);
        jlblTotAmt.setHorizontalAlignment(SwingConstants.RIGHT);

        jlblTotQty.setBounds(0, 0, 20, 20);
        jlblTotQty.setVisible(true);
        jPanel4.add(jlblTotQty);

        jlblTotAmt.setBounds(0, 0, 20, 20);
        jlblTotAmt.setVisible(true);
        jPanel4.add(jlblTotAmt);

        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotQty, null, null, null, null, null, null, null, jlblTotAmt, null, null, null, null});
    }

    private void searchOnTextFields() {
        this.rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        panel.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        panel.add(jtfFilter, BorderLayout.CENTER);

//        setLayout(new BorderLayout());
//        add(panel, BorderLayout.SOUTH);
//        add(new JScrollPane(jTable1), BorderLayout.CENTER);
        jtfFilter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = jtfFilter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });
    }

    private void addJtextBox() {
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
                    jtxtTag.setText(lb.checkTag(jtxtTag.getText()));
                    Call<JsonObject> call = salesReturnAPI.getTagNoDetailSales("'" + jtxtTag.getText() + "'", "15", false);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {

                            if (rspns.isSuccessful()) {
                                JsonArray array = rspns.body().getAsJsonArray("data");
                                subDetail.clear();
                                if (array.size() > 0) {
                                    for (int i = 0; i < array.size(); i++) {
                                        if (array.get(i).getAsJsonObject().get("IS_MAIN").getAsInt() == 1) {
                                            jtxtTag.setText(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                                            jtxtItem.setText(array.get(i).getAsJsonObject().get("ITEM_NAME").getAsString());
                                            pur_tag_no = (array.get(i).getAsJsonObject().get("REF_NO").getAsString());
                                            jtxtIMEI.setText(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                                            jtxtSerialNo.setText(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                                            sr_cd = (array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                                            item_name = (array.get(i).getAsJsonObject().get("ITEM_NAME").getAsString());
                                            jtxtQty.setText("1");
                                            if (tax_type == 0) {
                                                jcmbTax.setSelectedItem(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString());
                                            } else {
                                                jcmbTax.setSelectedItem(array.get(i).getAsJsonObject().get("GST_NAME").getAsString());
                                            }
                                            jtxtRate.requestFocusInWindow();
                                        } else {
                                            SalesControllerDetailModel model = new SalesControllerDetailModel();
                                            model.setTAG_NO(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                                            model.setSR_NAME(array.get(i).getAsJsonObject().get("ITEM_NAME").getAsString());
                                            model.setIMEI_NO(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                                            model.setSERAIL_NO(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                                            model.setQTY(1);
                                            model.setRATE(array.get(i).getAsJsonObject().get("PUR_RATE").getAsDouble());
                                            model.setPUR_TAG_NO(array.get(i).getAsJsonObject().get("REF_NO").getAsString());
                                            if (tax_type == 0) {
                                                model.setTAX_CD(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString());
                                            } else {
                                                model.setTAX_CD(array.get(i).getAsJsonObject().get("GST_NAME").getAsString());
                                            }
                                            model.setBASIC_AMT(0.00);
                                            model.setTAX_AMT(0.00);
                                            model.setADD_TAX_AMT(0.00);
                                            model.setDISC_PER(0.00);
                                            model.setMRP(0.00);
                                            model.setAMT(0.00);
                                            model.setSR_CD(array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                                            subDetail.add(model);
                                        }
                                    }
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }
                    });
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
                if (lb.isEnter(e)) {
                    setSeriesData("3", jtxtItem.getText().toUpperCase(), "1");
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
                lb.toDouble(e);
                int row = jTable1.getSelectedRow();
                if (row == -1) {
                    sale_rate = lb.isNumber(jtxtRate);
                    jtxtMRP.setText(lb.Convert2DecFmtForRs(lb.isNumber(jtxtRate) - getSubDetailRate()));
                    jtxtDiscPer.setText("0.00");
                    jtxtRate.setText(lb.Convert2DecFmtForRs(lb.isNumber(jtxtRate) - getSubDetailRate()));

                    jcmbTaxItemStateChanged(null);
                    calculation();
                    jbtnAdd.doClick();

                } else {
                    jtxtDiscPer.requestFocusInWindow();
                }
            }
        });

        jtxtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_L) {
//                    if (e.getModifiers() == KeyEvent.CTRL_MASK) {
//                        String tag = "";
//                        if (!jtxtIMEI.getText().equalsIgnoreCase("")) {
//                            tag = (jtxtIMEI.getText());
//                        } else if (!jtxtSerialNo.getText().equalsIgnoreCase("")) {
//                            tag = (jtxtSerialNo.getText());
//                        } else {
//                            tag = "";
//                        }
//                        SplitTag st = new SplitTag(null, true, tag);
//                        st.setVisible(true);
//                        st.setLocationRelativeTo(null);
//                        if (st.getReturnStatus() == SplitTag.RET_OK) {
//                            subDetail = st.detail;
//                        }
//                    }
//                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                        TaxMasterModel tm = null;
                        for (int i = 0; i < Constants.TAX.size(); i++) {
                            if (Constants.TAX.get(i).getTAXNAME().equalsIgnoreCase(jcmbTax.getSelectedItem().toString())) {
                                tm = Constants.TAX.get(i);
                                break;
                            }
                        }
                        double tax_rate = Double.parseDouble(tm.getTAXPER());
                        double basic_amt = lb.isNumber2(jtxtRate.getText());
                        double add_tax_rate = Double.parseDouble(tm.getADDTAXPER());
                        double tax = tax_rate + add_tax_rate + 100;
                        tax = tax / 100.0;
                        jtxtRate.setText(lb.Convert2DecFmtForRs(basic_amt * tax));

                    }
                }
                lb.enterFocus(e, jbtnAdd);
            }
        });

        jtxtBasicAmt = new javax.swing.JTextField();

        jtxtBasicAmt.addFocusListener(new java.awt.event.FocusAdapter() {
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

        jtxtBasicAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jtxtTaxAmt);
            }
        });

        jtxtTaxAmt = new javax.swing.JTextField();

        jtxtTaxAmt.addFocusListener(new java.awt.event.FocusAdapter() {
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

        jtxtTaxAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jtxtAddTaxAmt);
            }
        });

        jtxtAddTaxAmt = new javax.swing.JTextField();

        jtxtAddTaxAmt.addFocusListener(new java.awt.event.FocusAdapter() {
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

        jtxtAddTaxAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jtxtDiscPer);
            }
        });

        jtxtDiscPer = new javax.swing.JTextField();

        jtxtDiscPer.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toDouble(e);
            }
        });

        jtxtDiscPer.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jtxtMRP);
            }
        });

        jtxtMRP = new javax.swing.JTextField();

        jtxtMRP.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toDouble(e);
            }
        });

        jtxtMRP.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                lb.enterFocus(e, jbtnAdd);
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
                calculation();
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
                lb.setTable(jTable1, new JComponent[]{jtxtTag, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, null, null, jcmbTax, jtxtBasicAmt, jtxtTaxAmt, jtxtAddTaxAmt, jtxtDiscPer, jtxtMRP, jtxtAmount, null, null});
                lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotQty, null, null, null, null, null, null, null, jlblTotAmt, null, null, null, null});
            }
        });

        jtxtTag.setBounds(0, 0, 20, 20);
        jtxtTag.setVisible(true);
        jPanel3.add(jtxtTag);

        jtxtItem.setBounds(0, 0, 20, 20);
        jtxtItem.setVisible(true);
        jPanel3.add(jtxtItem);

        jtxtIMEI.setBounds(0, 0, 20, 20);
        jtxtIMEI.setVisible(true);
        jPanel3.add(jtxtIMEI);

        jtxtSerialNo.setBounds(0, 0, 20, 20);
        jtxtSerialNo.setVisible(true);
        jPanel3.add(jtxtSerialNo);

        jtxtQty.setBounds(0, 0, 20, 20);
        jtxtQty.setVisible(true);
        jPanel3.add(jtxtQty);

        jtxtRate.setBounds(0, 0, 20, 20);
        jtxtRate.setVisible(true);
        jPanel3.add(jtxtRate);

        jcmbTax.setBounds(0, 0, 20, 20);
        jcmbTax.setVisible(true);
        jPanel3.add(jcmbTax);
        jcmbTax.setEnabled(false);

        jtxtBasicAmt.setBounds(0, 0, 20, 20);
        jtxtBasicAmt.setVisible(true);
        jPanel3.add(jtxtBasicAmt);
        jtxtBasicAmt.setEditable(false);

        jtxtTaxAmt.setBounds(0, 0, 20, 20);
        jtxtTaxAmt.setVisible(true);
        jtxtTaxAmt.setEditable(false);
        jPanel3.add(jtxtTaxAmt);

        jtxtAddTaxAmt.setBounds(0, 0, 20, 20);
        jtxtAddTaxAmt.setVisible(true);
        jtxtAddTaxAmt.setEditable(false);
        jPanel3.add(jtxtAddTaxAmt);

        jtxtDiscPer.setBounds(0, 0, 20, 20);
        jtxtDiscPer.setVisible(true);
        jtxtDiscPer.setEditable(false);
        jPanel3.add(jtxtDiscPer);

        jtxtMRP.setBounds(0, 0, 20, 20);
        jtxtMRP.setVisible(true);
        jPanel3.add(jtxtMRP);
        jtxtMRP.setEditable(false);

        jtxtAmount.setBounds(0, 0, 20, 20);
        jtxtAmount.setVisible(true);
        jPanel3.add(jtxtAmount);
        jtxtAmount.setEditable(false);

        lb.setTable(jTable1, new JComponent[]{jtxtTag, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, null, null, jcmbTax, jtxtBasicAmt, jtxtTaxAmt, jtxtAddTaxAmt, jtxtDiscPer, jtxtMRP, jtxtAmount, null, null});
    }

    private double getSubDetailRate() {
        double rate = 0.00;
        for (int i = 0; i < subDetail.size(); i++) {
            rate += subDetail.get(i).getRATE();
        }
        return rate;
    }

    private void setSeriesData(String param_cd, String value, final String mode) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(SalesReturnController.this);
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
                                if (tax_type == 0) {
                                    row.add(series.get(i).getTAXCD());
                                    row.add(series.get(i).getTAXNAME());
                                } else {
                                    row.add(series.get(i).getGSTCD());
                                    row.add(series.get(i).getGSTNAME());
                                }
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {

                                int row = viewTable.getSelectedRow();
                                if (mode.equalsIgnoreCase("1")) {
                                    if (row != -1) {
                                        sr_cd = viewTable.getValueAt(row, 0).toString();
                                        item_name = viewTable.getValueAt(row, 1).toString();
                                        jtxtItem.setText(viewTable.getValueAt(row, 1).toString());
                                        jtxtIMEI.requestFocusInWindow();
                                        jcmbTax.setSelectedItem(viewTable.getValueAt(row, 3).toString());
                                        jcmbTaxItemStateChanged(null);
                                    }
                                }
                                sa.dispose();
                            }
                        } else {
                            lb.showMessageDailog(header.getCause());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(SalesReturnController.this);
                }
            }
            );
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void calculation() {
        double qty = lb.isNumber(jtxtQty);
        double rate = lb.isNumber(jtxtRate);
        jtxtAmount.setText(lb.Convert2DecFmtForRs(rate * qty));

    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    public void setData(String ref_no) {
        this.ref_no = ref_no;
        if (salesReturnAPI == null) {
            salesReturnAPI = lb.getRetrofit().create(SalesReturnAPI.class);
        }
        this.salesReturnAPI = salesReturnAPI;

        if (!ref_no.equalsIgnoreCase("")) {
            try {
                Call<JsonObject> call = salesReturnAPI.GetDataFromServer(ref_no, "25");
                lb.addGlassPane(this);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        lb.removeGlassPane(SalesReturnController.this);
                        if (response.isSuccessful()) {
                            System.out.println(response.body().toString());
                            JsonObject object = response.body();
                            if (object.get("result").getAsInt() == 1) {

                                JsonArray array = object.get("data").getAsJsonArray();
                                try {
                                    taxInfo.clear();
                                    for (int i = 0; i < array.size(); i++) {
                                        jtxtVoucher.setText(array.get(i).getAsJsonObject().get("INV_NO").getAsInt() + "");
                                        jtxtVouDate.setText(lb.ConvertDateFormetForDBForConcurrency(array.get(i).getAsJsonObject().get("V_DATE").getAsString()));
                                        jtxtDueDate.setText(lb.ConvertDateFormetForDBForConcurrency(array.get(i).getAsJsonObject().get("DUE_DATE").getAsString()));
                                        jlblVday.setText(lb.setDay(jtxtVouDate));
                                        jlblBillDay1.setText(lb.setDay(jtxtDueDate));
                                        jComboBox1.setSelectedIndex(array.get(i).getAsJsonObject().get("BRANCH_CD").getAsInt() - 1);
                                        jcmbPmt.setSelectedIndex(array.get(i).getAsJsonObject().get("PMT_MODE").getAsInt());
                                        ac_cd = array.get(i).getAsJsonObject().get("AC_CD").getAsString();
                                        tax_type = array.get(i).getAsJsonObject().get("TAX_TYPE").getAsInt();
                                        jtxtPmtDays.setText(array.get(i).getAsJsonObject().get("PMT_DAYS").getAsString());
                                        jtxtAdvance.setText(array.get(i).getAsJsonObject().get("ADVANCE_AMT").getAsString());
                                        jtxtAcAlias.setText(array.get(i).getAsJsonObject().get("AC_CD").getAsString());
                                        jtxtAcName.setText(array.get(i).getAsJsonObject().get("FNAME").getAsString());
                                        jtxtAddress.setText(array.get(i).getAsJsonObject().get("ADD1").getAsString());
                                        jtxtMobile.setText(array.get(i).getAsJsonObject().get("MOBILE1").getAsString());
                                        jtxtTin.setText(array.get(i).getAsJsonObject().get("TIN").getAsString());
                                        SalesReturnController.this.ref_no = array.get(i).getAsJsonObject().get("REF_NO").getAsString();
                                        jlblTotal.setText(array.get(i).getAsJsonObject().get("NET_AMT").getAsDouble() + "");
                                        jlblBasicAmount.setText(array.get(i).getAsJsonObject().get("DET_TOT").getAsDouble() + "");
                                        jcmbTax.setSelectedItem(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString());
                                        jlblNet.setText(array.get(i).getAsJsonObject().get("NET_AMT").getAsDouble() + "");
                                        jlblAdjst.setText(array.get(i).getAsJsonObject().get("ADJST").getAsDouble() + "");
                                        jlblUser.setText(array.get(i).getAsJsonObject().get("USER_ID").getAsString() + "");
                                        jlblEditNo.setText(array.get(i).getAsJsonObject().get("EDIT_NO").getAsDouble() + "");
                                        jlblTimeStamp.setText(array.get(i).getAsJsonObject().get("TIME_STAMP").getAsString());
                                        if (!array.get(i).getAsJsonObject().get("REMARK").isJsonNull()) {
                                            jTextArea1.setText(array.get(i).getAsJsonObject().get("REMARK").getAsString());
                                        }

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
                                        }
                                        if (!array.get(i).getAsJsonObject().get("BAJAJ_CD").isJsonNull()) {
                                            if (!sd.bajaj_cd.equalsIgnoreCase("")) {
                                                sd.jcbBajaj.setSelected(true);
                                                sd.jtxtBajajCapital.setText(array.get(i).getAsJsonObject().get("BAJAJ_NAME").getAsString());
                                                sd.jtxtBajajAmt.setText(array.get(i).getAsJsonObject().get("BAJAJ_AMT").getAsString());
                                                sd.jtxtSFID.setText(array.get(i).getAsJsonObject().get("SFID").getAsString());
                                            }
                                        } else {
                                            if (!sd.bajaj_cd.equalsIgnoreCase("")) {
                                                sd.jcbBajaj.setSelected(false);
                                                sd.jtxtBajajCapital.setText("");
                                                sd.jtxtBajajAmt.setText("");
                                            }
                                        }
                                        Vector row = new Vector();
                                        row.add(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("QTY").getAsInt());
                                        row.add(array.get(i).getAsJsonObject().get("RATE").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("PUR_TAG_NO").getAsString());
                                        if (!array.get(i).getAsJsonObject().get("TAG_DEL").isJsonNull()) {
                                            row.add(array.get(i).getAsJsonObject().get("TAG_DEL").getAsString());
                                        } else {
                                            row.add("0");
                                        }
                                        row.add(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("BASIC_AMT").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("TAX_AMT").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("ADD_TAX_AMT").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("DISC_RATE").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("MRP").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("AMT").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("IS_MAIN").getAsInt());
                                        row.add(array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                                        if (taxInfo.get(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString()) != null) {
                                            double[] tax = taxInfo.get(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString());
                                            tax[0] += array.get(i).getAsJsonObject().get("TAX_AMT").getAsDouble();
                                            tax[1] += array.get(i).getAsJsonObject().get("ADD_TAX_AMT").getAsDouble();
                                            taxInfo.put(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString(), tax);
                                        } else {
                                            double[] tax = new double[2];
                                            tax[0] += array.get(i).getAsJsonObject().get("TAX_AMT").getAsDouble();
                                            tax[1] += array.get(i).getAsJsonObject().get("ADD_TAX_AMT").getAsDouble();
                                            taxInfo.put(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString(), tax);
                                        }
                                        dtm.addRow(row);

                                    }
                                    setTotal();
                                } catch (Exception ex) {
                                    lb.printToLogFile("Exception", ex);
                                }
                                SalesReturnController.this.setVisible(true);
                            } else {
                                lb.showMessageDailog(response.body().get("Cause").toString());
                            }
                        } else {
                            lb.showMessageDailog(response.body().get("Cause").toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                        lb.removeGlassPane(SalesReturnController.this);
                    }
                });
            } catch (Exception ex) {
                lb.printToLogFile("Exception at getDataFor PurchaseBill", ex);
            }
        } else {
            SalesReturnController.this.setVisible(true);
        }

    }

    private void setAccountDetailMobile(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(SalesReturnController.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        AccountHead header = new Gson().fromJson(response.body(), AccountHead.class);
                        if (header.getResult() == 1) {
                            SelectAccount sa = new SelectAccount(null, true);
                            sa.setLocationRelativeTo(null);
                            sa.fillData((ArrayList) header.getAccountHeader());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectAccount.RET_OK) {
                                int row = sa.row;
                                if (row != -1) {
                                    ac_cd = header.getAccountHeader().get(row).getACCD();
                                    jtxtAcAlias.setText(header.getAccountHeader().get(row).getACCD());
                                    jtxtMobile.setText(header.getAccountHeader().get(row).getMOBILE1());
                                    jtxtAcName.setText(header.getAccountHeader().get(row).getFNAME());
                                    jtxtAddress.setText(header.getAccountHeader().get(row).getADD1());
                                    jtxtTin.setText(header.getAccountHeader().get(row).getTIN());
                                    jtxtTag.requestFocusInWindow();
                                }
                            }
                        } else {
                            lb.showMessageDailog(response.body().get("Cause").getAsString().toString());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(SalesReturnController.this);
                }
            });
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void setTotal() {
        double tot = 0.00;
        int tot_qty = 0;
        double tot_basic = 0;
        double tot_tax = 0;
        double tot_add_tax = 0;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            tot += lb.isNumber2(jTable1.getValueAt(i, 14).toString());
            tot_basic += lb.isNumber2(jTable1.getValueAt(i, 9).toString());
            tot_tax += lb.isNumber2(jTable1.getValueAt(i, 10).toString());
            tot_add_tax += lb.isNumber2(jTable1.getValueAt(i, 11).toString());
            tot_qty += (int) lb.isNumber2(jTable1.getValueAt(i, 4).toString());
        }
        jlblTotal.setText(lb.Convert2DecFmtForRs(tot));
        jlblBasicAmount.setText(lb.Convert2DecFmtForRs(tot_basic));
        jlblTax.setText(lb.Convert2DecFmtForRs(tot_tax));
        jlblAddTax.setText(lb.Convert2DecFmtForRs(tot_add_tax));
        jlblTotQty.setText((tot_qty) + "");
        jlblNet.setText(lb.Convert2DecFmtForRs(tot));

        dtmTax.setRowCount(0);
        double tax_amt = 0.00, add_tax_amt = 0.00;
        Iterator it = taxInfo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, double[]> pair = (Map.Entry<String, double[]>) it.next();
            double tax[] = pair.getValue();
            Vector row = new Vector();
            row.add(pair.getKey());
            row.add(tax[0]);
            row.add(tax[1]);
            row.add(tax[0] + tax[1]);
            tax_amt += tax[0];
            add_tax_amt += tax[1];
            dtmTax.addRow(row);
        }
        Vector row = new Vector();
        row.add("Total");
        row.add(tax_amt);
        row.add(add_tax_amt);
        row.add(tax_amt + add_tax_amt);
        dtmTax.addRow(row);
    }

    private void clear() {
        jtxtItem.setText("");
        jtxtTag.setText("");
        jtxtBasicAmt.setText("");
        jtxtTaxAmt.setText("");
        jtxtAddTaxAmt.setText("");
        sr_cd = "";
        item_name = "";
        jtxtIMEI.setText("");
        jtxtSerialNo.setText("");
        jtxtQty.setText("");
        jtxtRate.setText("");
        jtxtDiscPer.setText("");
        jtxtMRP.setText("");
        jtxtAmount.setText("");
    }

    public boolean validateRow(String tag) {
        boolean flag = true;

        if (sr_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Invalid Product Name");
            jtxtItem.requestFocusInWindow();
            flag = false;
        }
//        if (dtm.getRowCount() == 0) {
//            jcmbTax.setSelectedItem(lb.getTaxCode(lb.getModelCd(lb.getSR_CD(jtxtProductName.getText(), "MN"), "T"), "N"));
//        } else {
//            if (!lb.getTaxCode(lb.getModelCd(lb.getSR_CD(jtxtProductName.getText(), "MN"), "T"), "N").equalsIgnoreCase(jcmbTax.getSelectedItem().toString())) {
//                navLoad.setMessage("Invalid Product.\n Differnt tax from previous product.");
//                flag = false;
//                jtxtProductName.requestFocusInWindow();
//            }
//        }
        if (!tag.equalsIgnoreCase("")) {
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                if (i != jTable1.getSelectedRow() && tag.equalsIgnoreCase(jTable1.getValueAt(i, 0).toString())) {
                    lb.showMessageDailog("Item already present");
                    jtxtIMEI.requestFocusInWindow();
                    return false;
                }
            }
        }

        if (lb.isNumber(jtxtAmount) == 0) {
            lb.showMessageDailog("Enter valid rate");
            jtxtRate.requestFocusInWindow();
            return false;
        }
        return flag;
    }

    private boolean validateVoucher() {
        if (ac_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Please select valid account");
            return false;
        }

        if (lb.ConvertDateFormetForDB(jtxtVouDate.getText()).equalsIgnoreCase("")) {
            lb.showMessageDailog("Invalid Voucher Date");
            jtxtVouDate.requestFocusInWindow();
            return false;
        }
        if (lb.ConvertDateFormetForDB(jtxtDueDate.getText()).equalsIgnoreCase("")) {
            lb.showMessageDailog("Please Enter Due Date");
            return false;
        }

        if (jTable1.getRowCount() == 0) {
            lb.showMessageDailog("Please Insert Value in Voucher");
            jtxtItem.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private void saveVoucher() {
        final SalesReturnControllerHeaderModel header = new SalesReturnControllerHeaderModel();
        header.setRef_no(ref_no);
        header.setREMARK(jTextArea1.getText());
        header.setAC_CD(ac_cd);
        header.setADJST(lb.isNumber(jlblAdjst));
        header.setDUE_DATE(lb.ConvertDateFormetForDB(jtxtDueDate.getText()));
        header.setDET_TOT(lb.isNumber(jlblBasicAmount));
        header.setNET_AMT(lb.isNumber(jlblNet));
        header.setPMT_MODE(jcmbPmt.getSelectedIndex());
        header.setTAX_AMT(lb.isNumber(jlblTax));
        header.setADD_TAX_AMT(lb.isNumber(jlblAddTax));
        header.setAc_name(jtxtAcName.getText());
        header.setBRANCH_CD(jComboBox1.getSelectedIndex() + 1);
        header.setUSER_ID(SkableHome.user_id);
        header.setV_DATE(lb.ConvertDateFormetForDB(jtxtVouDate.getText()));
        header.setV_TYPE(0);
        header.setCASH_AMT(lb.isNumber(sd.jtxtCashAmt.getText()));
        header.setBANK_AMT(lb.isNumber(sd.jtxtChequeAmt.getText()));
        header.setCARD_AMT(lb.isNumber(sd.jtxtCardAmt.getText()));
        header.setBAJAJ_AMT(lb.isNumber(sd.jtxtBajajAmt.getText()));
        header.setBANK_CD(sd.bank_cd);
        header.setCARD_NAME(sd.card_cd);
        header.setBAJAJ_NAME(sd.bajaj_cd);
        header.setSFID(sd.jtxtSFID.getText());
        header.setBANK_NAME(sd.jtxtBankName.getText());
        header.setBANK_BRANCH(sd.jtxtBranchName.getText());
        header.setCHEQUE_NO(sd.jtxtChequeNo.getText());
        header.setPmt_days(((int) (lb.isNumber(jtxtPmtDays))) + "");
        header.setAdvance_amt(lb.isNumber(jtxtAdvance));
        header.setTax_type(tax_type);
        if (!sd.jtxtChequeDate.getText().equalsIgnoreCase("")) {
            header.setCHEQUE_DATE(lb.ConvertDateFormetForDB(sd.jtxtChequeDate.getText()));
        } else {
            header.setCHEQUE_DATE(null);
        }

        final ArrayList<SalesReturnControllerDetailModel> detail = new ArrayList<SalesReturnControllerDetailModel>();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            SalesReturnControllerDetailModel model = new SalesReturnControllerDetailModel();
            model.setTAG_NO(jTable1.getValueAt(i, 0).toString());
            model.setSR_CD(jTable1.getValueAt(i, 1).toString());
            model.setIMEI_NO(jTable1.getValueAt(i, 2).toString());
            model.setSERAIL_NO(jTable1.getValueAt(i, 3).toString());
            model.setQTY((int) lb.isNumber2(jTable1.getValueAt(i, 4).toString()));
            model.setRATE(lb.isNumber2(jTable1.getValueAt(i, 5).toString()));
            model.setPUR_TAG_NO(jTable1.getValueAt(i, 6).toString());
            jcmbTax.setSelectedItem(jTable1.getValueAt(i, 8).toString());
            for (int j = 0; j < Constants.TAX.size(); j++) {
                if (Constants.TAX.get(j).getTAXNAME().equalsIgnoreCase(jcmbTax.getSelectedItem().toString())) {
                    model.setTAX_CD(Constants.TAX.get(j).getTAXCD());
                    break;
                }
            }
            model.setBASIC_AMT(lb.isNumber2(jTable1.getValueAt(i, 9).toString()));
            model.setTAX_AMT(lb.isNumber2(jTable1.getValueAt(i, 10).toString()));
            model.setADD_TAX_AMT(lb.isNumber2(jTable1.getValueAt(i, 11).toString()));
            model.setDISC_PER(lb.isNumber2(jTable1.getValueAt(i, 12).toString()));
            model.setMRP(lb.isNumber2(jTable1.getValueAt(i, 13).toString()));
            model.setAMT(lb.isNumber2(jTable1.getValueAt(i, 14).toString()));
            model.setIsMain((int) lb.isNumber2(jTable1.getValueAt(i, 15).toString()));
            detail.add(model);
        }

        String headerJson = new Gson().toJson(header);
        String detailJson = new Gson().toJson(detail);
        Call<JsonObject> addUpdaCall = salesReturnAPI.addUpdateSalesBill(headerJson, detailJson);
        lb.addGlassPane(this);
        addUpdaCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                lb.removeGlassPane(SalesReturnController.this);
                if (response.isSuccessful()) {
                    System.out.println(response.body().toString());
                    JsonObject object = response.body();
                    if (object.get("result").getAsInt() == 1) {
                        lb.showMessageDailog("Voucher saved successfully");
                        SalesReturnController.this.dispose();
                        if (ref_no.equalsIgnoreCase("")) {
                            SwingWorker worker = new SwingWorker() {

                                @Override
                                protected Object doInBackground() throws Exception {
//                                    lb.displayPurchaseVoucherEmail(header, detail);
                                    return null;
                                }
                            };
                            worker.execute();
                        }
                    } else {
                        lb.showMessageDailog(object.get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(response.body().get("Cause").toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(SalesReturnController.this);
            }
        });
    }

    private void deleteSubRow(String tag) {
        int row = jTable1.getRowCount();
        for (int j = 0; j < row; j++) {
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                if (jTable1.getValueAt(i, 0).toString().equalsIgnoreCase(tag)) {
                    if (taxInfo.get(jTable1.getValueAt(i, 8).toString()) != null) {
                        double[] tax = taxInfo.get(jTable1.getValueAt(i, 8).toString());
                        tax[0] -= lb.isNumber2(jTable1.getValueAt(i, 10).toString());
                        tax[1] -= lb.isNumber2(jTable1.getValueAt(i, 11).toString());
                        taxInfo.put(jTable1.getValueAt(i, 8).toString(), tax);
                    }
                    dtm.removeRow(i);
                    j = 0;
                    break;
                }
            }
        }
    }

    private void updateFooter() {

        if (flag) {
            double net_amt = (lb.isNumber2(jlblTotal.getText()));
            jlblAdjst.setText(lb.Convert2DecFmtForRs(lb.isNumber(jlblTotal) - (lb.isNumber(jlblBasicAmount) + lb.isNumber(jlblTax) + lb.isNumber(jlblAddTax))));
            jlblNet.setText(lb.Convert2DecFmtForRs(net_amt));

            if (jcmbPmt.getSelectedIndex() == 0) {
                jlblRemAmt.setText("0.00");
            } else {
                jlblRemAmt.setText(lb.Convert2DecFmtForRs(lb.isNumber(jlblNet) - lb.isNumber(jtxtAdvance)));
            }
        }
    }

    private void setDueDate() {
        Date dt = lb.ConvertDateFromString(jtxtVouDate.getText());
        int numOfDays = (int) lb.isNumber(jtxtPmtDays);
        Calendar cal = Calendar.getInstance();
        if (dt != null) {
            cal.setTime(dt);
            cal.add(Calendar.DATE, numOfDays);
            jtxtDueDate.setText(((cal.get(Calendar.DATE) > 9) ? cal.get(Calendar.DATE) : "0" + cal.get(Calendar.DATE)) + "/" + (((cal.get(Calendar.MONTH) + 1) > 9) ? (cal.get(Calendar.MONTH) + 1) : "0" + (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR));
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
        jLabel6 = new javax.swing.JLabel();
        jcmbPmt = new javax.swing.JComboBox();
        jlblVday = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jtxtDueDate = new javax.swing.JTextField();
        jBillDateBtn2 = new javax.swing.JButton();
        jlblBillDay1 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jtxtMobile = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jtxtAcAlias = new javax.swing.JTextField();
        jtxtAcName = new javax.swing.JTextField();
        jtxtAddress = new javax.swing.JTextField();
        jtxtTin = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jtxtCardNo = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jtxtRefBy = new javax.swing.JTextField();
        jlblPmtDays = new javax.swing.JLabel();
        jtxtPmtDays = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jcmbTax = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        panel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jlblAddTax = new javax.swing.JLabel();
        jlblNet = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jlblAdjst = new javax.swing.JLabel();
        jlblTax = new javax.swing.JLabel();
        jlblTotal = new javax.swing.JLabel();
        jlblBasicAmount = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jlblNet1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtAdvance = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jlblRemAmt = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jbtnOK = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

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

        jLabel6.setText("Payment Mode");

        jcmbPmt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cash", "Credit" }));
        jcmbPmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbPmtKeyPressed(evt);
            }
        });

        jlblVday.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setEnabled(false);
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel3.setText("Branch");

        jLabel27.setText("Due Date");

        jtxtDueDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDueDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDueDateFocusLost(evt);
            }
        });
        jtxtDueDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDueDateKeyPressed(evt);
            }
        });

        jBillDateBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn2ActionPerformed(evt);
            }
        });

        jlblBillDay1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel28.setText("Mobile No");

        jtxtMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyPressed(evt);
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

        jtxtTin.setEnabled(false);

        jLabel29.setText("Card Number :");

        jtxtCardNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardNoKeyPressed(evt);
            }
        });

        jLabel30.setText("Ref By");

        jtxtRefBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRefByFocusGained(evt);
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

        jlblPmtDays.setText("Payment Days");

        jtxtPmtDays.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPmtDaysFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPmtDaysFocusLost(evt);
            }
        });
        jtxtPmtDays.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPmtDaysKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPmtDaysKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(325, 325, 325)
                                .addComponent(jlblPmtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jtxtPmtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jBillDateBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlblBillDay1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcmbPmt, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(86, 86, 86)
                                .addComponent(jtxtCardNo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtAcName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtAddress)
                                .addGap(18, 18, 18)
                                .addComponent(jtxtTin, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnAdd)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel23, jLabel28, jLabel29});

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
                    .addComponent(jlblVday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbPmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblBillDay1)
                    .addComponent(jtxtPmtDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblPmtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtTin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCardNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRefBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnAdd)
                .addGap(5, 5, 5))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jComboBox1, jLabel1, jLabel24, jLabel3, jLabel6, jcmbPmt, jlblVday, jtxtVouDate, jtxtVoucher});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn2, jLabel27, jlblBillDay1, jtxtDueDate});

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag No", "Product Name", "IMEI No", "Serial No", "Qty", "Rate", "ref_no", "IS_del", "Tax", "Basic Amt", "Tax Amt", "Add Tax Amt", "Disc", "MRP", "Amount", "Main", "SR_CD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(7).setMinWidth(0);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(7).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(10).setResizable(false);
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(11).setResizable(false);
            jTable1.getColumnModel().getColumn(11).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(12).setResizable(false);
            jTable1.getColumnModel().getColumn(12).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(13).setResizable(false);
            jTable1.getColumnModel().getColumn(13).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(14).setResizable(false);
            jTable1.getColumnModel().getColumn(14).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(15).setMinWidth(0);
            jTable1.getColumnModel().getColumn(15).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(15).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(16).setMinWidth(0);
            jTable1.getColumnModel().getColumn(16).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(16).setMaxWidth(0);
        }

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(1060, 25));

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(490, 490, 490)
                .addComponent(jcmbTax, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(514, 514, 514))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jcmbTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        panel.setLayout(new java.awt.BorderLayout());

        jlblAddTax.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblAddTax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblNet.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblNet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel14.setText("Net Amount");

        jLabel19.setText("Adjustment");

        jlblAdjst.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblAdjst.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblTax.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTotal.setText("0.00");
        jlblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblBasicAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblBasicAmount.setText("0.00");
        jlblBasicAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel21.setText("Taxable");

        jLabel22.setText("Tax Amt");

        jLabel25.setText("Add Tax Amt");

        jLabel18.setText("Net Amt");

        jlblNet1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblNet1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("Advance");

        jtxtAdvance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtAdvance.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAdvanceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAdvanceFocusLost(evt);
            }
        });
        jtxtAdvance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAdvanceKeyPressed(evt);
            }
        });

        jLabel20.setText("Remaining Amount");

        jlblRemAmt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblRemAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblNet, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblRemAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAdvance, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblNet1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAdjst, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAddTax, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblBasicAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTax, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel18, jLabel19, jLabel20, jLabel21, jLabel22, jLabel25, jLabel5});

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jlblAddTax, jlblAdjst, jlblBasicAmount, jlblNet, jlblNet1, jlblRemAmt, jlblTax, jlblTotal, jtxtAdvance});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jlblBasicAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jlblTax, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(jlblAddTax, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jlblAdjst, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblNet1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jtxtAdvance, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jlblRemAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblNet, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel18, jLabel19, jLabel20, jLabel21, jLabel22, jLabel25, jLabel5});

        jLabel12.setText("User :");

        jLabel13.setText("Edit NO :");

        jLabel15.setText("Last Updated : ");

        jbtnOK.setMnemonic('O');
        jbtnOK.setText("OK");
        jbtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnOKActionPerformed(evt);
            }
        });

        jLabel16.setText("Remark");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(4);
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea1KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTextArea1);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tax Name", "Tax Amt", "Add Tax Amt", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
        }

        jPanel7.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1256, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jbtnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                                    .addComponent(jlblUser, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(jbtnOK)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(184, 184, 184)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
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
        lb.enterFocus(evt, jtxtPmtDays);
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

    private void jcmbPmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbPmtKeyPressed
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jcmbPmtKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            {
                int index = jTable1.getSelectedRow();
                int is_del = Integer.parseInt(jTable1.getValueAt(index, 7).toString());
                if (index != -1 && is_del == 0) {
                    evt.consume();
                    item_name = jTable1.getValueAt(index, 1).toString();
                    sr_cd = jTable1.getValueAt(index, 16).toString();
                    jtxtItem.setText(jTable1.getValueAt(index, 1).toString());
                    jtxtQty.setText(jTable1.getValueAt(index, 4).toString());
                    jtxtRate.setText(jTable1.getValueAt(index, 5).toString());
                    jcmbTax.setSelectedItem(jTable1.getValueAt(index, 8).toString());
                    jtxtBasicAmt.setText(jTable1.getValueAt(index, 9).toString());
                    jtxtTaxAmt.setText(jTable1.getValueAt(index, 10).toString());
                    jtxtAddTaxAmt.setText(jTable1.getValueAt(index, 11).toString());
                    jtxtDiscPer.setText(jTable1.getValueAt(index, 12).toString());
                    jtxtMRP.setText(jTable1.getValueAt(index, 13).toString());
                    jtxtAmount.setText(jTable1.getValueAt(index, 14).toString());
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        int index = jTable1.getSelectedRow();
        int is_del = (int) lb.isNumber(jTable1.getValueAt(index, 7).toString());
        int is_main = (int) lb.isNumber(jTable1.getValueAt(index, 15).toString());
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (index != -1 && is_del == 0 && is_main == 1) {
                lb.confirmDialog("Do you want to delete this row?");
                if (lb.type) {
                    deleteSubRow(jTable1.getValueAt(index, 0).toString());
                    setTotal();
                }
            }
        }

        if (evt.getKeyCode() == KeyEvent.VK_D) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                if (index != -1 && is_del == 0 && is_main == 1) {
                    lb.confirmDialog("Do you want to delete this row?");
                    if (lb.type) {
                        deleteSubRow(jTable1.getValueAt(index, 0).toString());
                        setTotal();
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jcmbTaxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcmbTaxItemStateChanged
        // TODO add your handling code here:
        if (flag) {
            TaxMasterModel tm = null;
            for (int i = 0; i < Constants.TAX.size(); i++) {
                if (Constants.TAX.get(i).getTAXNAME().equalsIgnoreCase(jcmbTax.getSelectedItem().toString())) {
                    tm = Constants.TAX.get(i);
                    break;
                }
            }
            if (tm != null) {
                double tax_rate = Double.parseDouble(tm.getTAXPER());
                double add_tax_rate = Double.parseDouble(tm.getADDTAXPER());
                if (tax_type == 2) {
                    tax_rate += add_tax_rate;
                    add_tax_rate = 0.00;
                }
//                int add_tax_rate_On = (int) lb.isNumber2(tm.getTAXONSALES());
                if (tm.getTAXCD().equalsIgnoreCase("T000003")) {
                    try {
                        final Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.MONTH, Calendar.JUNE);
                        cal.set(Calendar.DATE, 1);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        java.util.Date dt = sdf.parse(jtxtVouDate.getText());
//                        add_tax_rate_On = (int) lb.isNumber2(tm.getTAXONSALES());
                        if (dt.before(sdf.parse(sdf.format(cal.getTime())))) {
                            add_tax_rate = 0.00;
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(SalesReturnController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                double taxable = (lb.isNumber2(jtxtRate.getText()) * 100) / (100 + tax_rate + add_tax_rate);
                jtxtBasicAmt.setText(lb.Convert2DecFmtForRs(taxable));
                jtxtTaxAmt.setText(lb.Convert2DecFmtForRs((tax_rate * taxable) / 100));
//                double tax = lb.isNumber(jlblTax);
                jtxtAddTaxAmt.setText(lb.Convert2DecFmtForRs(lb.isNumber(jtxtRate) - lb.isNumber(jtxtTaxAmt) - lb.isNumber(jtxtBasicAmt)));
            }
        }
    }//GEN-LAST:event_jcmbTaxItemStateChanged

    private void jcmbTaxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTaxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jcmbTaxItemStateChanged(null);
        }
    }//GEN-LAST:event_jcmbTaxKeyPressed

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
        if (validateRow(tag)) {
            int index = jTable1.getSelectedRow();
            if (index == -1) {
                for (int i = 0; i < (int) lb.isNumber2(jtxtQty.getText()); i++) {
                    Vector row = new Vector();
                    row.add(jtxtTag.getText());
                    row.add(item_name);
                    row.add(jtxtIMEI.getText());
                    row.add(jtxtSerialNo.getText());
                    row.add(1);
                    row.add(lb.isNumber2(jtxtRate.getText()));
                    row.add(pur_tag_no);
                    row.add("0");
                    row.add(jcmbTax.getSelectedItem().toString());
                    row.add(lb.isNumber2(jtxtBasicAmt.getText()));
                    row.add(lb.isNumber2(jtxtTaxAmt.getText()));
                    row.add(lb.isNumber2(jtxtAddTaxAmt.getText()));
                    row.add(lb.isNumber2(jtxtDiscPer.getText()));
                    row.add(lb.isNumber2(jtxtMRP.getText()));
                    row.add(lb.isNumber2(jtxtAmount.getText()));
                    row.add(lb.isNumber2("1"));
                    row.add(sr_cd);
                    dtm.addRow(row);
                    if (taxInfo.get(jcmbTax.getSelectedItem().toString()) != null) {
                        double[] tax = taxInfo.get(jcmbTax.getSelectedItem().toString());
                        tax[0] += lb.isNumber2(jtxtTaxAmt.getText());
                        tax[1] += lb.isNumber2(jtxtAddTaxAmt.getText());
                        taxInfo.put(jcmbTax.getSelectedItem().toString(), tax);
                    } else {
                        double[] tax = new double[2];
                        tax[0] += lb.isNumber2(jtxtTaxAmt.getText());;
                        tax[1] += lb.isNumber2(jtxtAddTaxAmt.getText());
                        taxInfo.put(jcmbTax.getSelectedItem().toString(), tax);
                    }

                }
                for (int i = 0; i < subDetail.size(); i++) {
                    jtxtTag.setText(subDetail.get(i).getTAG_NO());
                    jtxtItem.setText(subDetail.get(i).getSR_NAME());
                    pur_tag_no = (subDetail.get(i).getPUR_TAG_NO());
                    jtxtIMEI.setText(subDetail.get(i).getIMEI_NO());
                    jtxtSerialNo.setText(subDetail.get(i).getSERAIL_NO());
                    sr_cd = (subDetail.get(i).getSR_CD());
                    item_name = (subDetail.get(i).getSR_NAME());
                    double pur_rate = (subDetail.get(i).getRATE());
                    jtxtRate.setText(lb.Convert2DecFmtForRs(pur_rate));
                    jtxtQty.setText("1");
                    jcmbTax.setSelectedItem(subDetail.get(i).getTAX_CD());
                    jcmbTaxItemStateChanged(null);
                    Vector row = new Vector();
                    row.add(jtxtTag.getText());
                    row.add(item_name);
                    row.add(jtxtIMEI.getText());
                    row.add(jtxtSerialNo.getText());
                    row.add(1);
                    row.add(lb.isNumber2(jtxtRate.getText()));
                    row.add(pur_tag_no);
                    row.add("0");
                    row.add(jcmbTax.getSelectedItem().toString());
                    row.add(lb.isNumber2(jtxtBasicAmt.getText()));
                    row.add(lb.isNumber2(jtxtTaxAmt.getText()));
                    row.add(lb.isNumber2(jtxtAddTaxAmt.getText()));
                    row.add(lb.isNumber2("0.00"));
                    row.add(lb.isNumber2(jtxtRate.getText()));
                    row.add(lb.isNumber2(jtxtRate.getText()));
                    row.add(lb.isNumber2("0"));
                    row.add(sr_cd);
                    dtm.addRow(row);

                    if (taxInfo.get(jcmbTax.getSelectedItem().toString()) != null) {
                        double[] tax = taxInfo.get(jcmbTax.getSelectedItem().toString());
                        tax[0] += lb.isNumber2(jtxtTaxAmt.getText());
                        tax[1] += lb.isNumber2(jtxtAddTaxAmt.getText());
                        taxInfo.put(jcmbTax.getSelectedItem().toString(), tax);
                    } else {
                        double[] tax = new double[2];
                        tax[0] += lb.isNumber2(jtxtTaxAmt.getText());;
                        tax[1] += lb.isNumber2(jtxtAddTaxAmt.getText());
                        taxInfo.put(jcmbTax.getSelectedItem().toString(), tax);
                    }
                }
                subDetail.clear();
            } else {
                if (taxInfo.get(jTable1.getValueAt(index, 8).toString()) != null) {
                    double[] tax = taxInfo.get(jTable1.getValueAt(index, 8).toString());
                    tax[0] -= lb.isNumber2(jTable1.getValueAt(index, 10).toString());
                    tax[1] -= lb.isNumber2(jTable1.getValueAt(index, 11).toString());
                    taxInfo.put(jTable1.getValueAt(index, 8).toString(), tax);
                }
                if (taxInfo.get(jcmbTax.getSelectedItem().toString()) != null) {
                    double[] tax = taxInfo.get(jcmbTax.getSelectedItem().toString());
                    tax[0] += lb.isNumber2(jtxtTaxAmt.getText());
                    tax[1] += lb.isNumber2(jtxtAddTaxAmt.getText());
                    taxInfo.put(jcmbTax.getSelectedItem().toString(), tax);
                } else {
                    double[] tax = new double[2];
                    tax[0] += lb.isNumber2(jtxtTaxAmt.getText());;
                    tax[1] += lb.isNumber2(jtxtAddTaxAmt.getText());
                    taxInfo.put(jcmbTax.getSelectedItem().toString(), tax);
                }
                jTable1.setValueAt(item_name, index, 1);
                jTable1.setValueAt((int) lb.isNumber2(jtxtQty.getText()), index, 4);
                jTable1.setValueAt(lb.isNumber2(jtxtRate.getText()), index, 5);
                jTable1.setValueAt(lb.isNumber2(jtxtBasicAmt.getText()), index, 9);
                jTable1.setValueAt(lb.isNumber2(jtxtTaxAmt.getText()), index, 10);
                jTable1.setValueAt(lb.isNumber2(jtxtAddTaxAmt.getText()), index, 11);
                jTable1.setValueAt(lb.isNumber2(jtxtDiscPer.getText()), index, 12);
                jTable1.setValueAt(lb.isNumber2(jtxtMRP.getText()), index, 13);
                jTable1.setValueAt(lb.isNumber2(jtxtAmount.getText()), index, 14);
                jTable1.clearSelection();

            }

            jtxtIMEI.setText("");
            jtxtSerialNo.setText("");
            lb.confirmDialog("Do you want to add another item?");
            if (lb.type) {
                jtxtItem.requestFocusInWindow();
            } else {
                clear();
                jTextArea1.requestFocusInWindow();
            }
        }
        setTotal();
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jbtnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnOKActionPerformed
        // TODO add your handling code here:
        if (validateVoucher()) {
            lb.confirmDialog("Do you want to save this voucher?");
            if (lb.type) {
                if (jcmbPmt.getSelectedIndex() == 0) {
                    sd.jlblSale.setText(lb.Convert2DecFmtForRs(lb.isNumber(jlblNet)));
                } else {
                    sd.jlblSale.setText(lb.Convert2DecFmtForRs(lb.isNumber("0.00")));
                }
                sd.setLocationRelativeTo(null);
                sd.setVisible(true);
                if (sd.getReturnStatus() == RET_OK) {
                    saveVoucher();
                }
            }
        }
    }//GEN-LAST:event_jbtnOKActionPerformed

    private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                if (jcmbPmt.getSelectedIndex() == 0) {
                    jbtnOK.requestFocusInWindow();
                } else {
                    jtxtAdvance.requestFocusInWindow();
                }
            }
        }
    }//GEN-LAST:event_jTextArea1KeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jtxtDueDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDueDateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDueDateFocusGained

    private void jtxtDueDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDueDateFocusLost
        // TODO add your handling code here:
        try {
            if (jtxtDueDate.getText().contains("/")) {
                jtxtDueDate.setText(jtxtDueDate.getText().replace("/", ""));
            }
            if (jtxtDueDate.getText().length() == 8) {
                String temp = jtxtDueDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtDueDate.setText(setDate);
            }
            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtDueDate.getText().trim()))) != null) {
                jtxtDueDate.setText(jtxtVouDate.getText());
            }
            jlblBillDay1.setText(lb.setDay(jtxtDueDate));
        } catch (Exception ex) {
            jtxtDueDate.setText(jtxtVouDate.getText());
        }
    }//GEN-LAST:event_jtxtDueDateFocusLost

    private void jtxtDueDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDueDateKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jtxtDueDateKeyPressed

    private void jBillDateBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn2ActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtDueDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtDueDate.getX() - 200, jPanel1.getY() + 170, jtxtDueDate.getX() + odc.getWidth(), jtxtDueDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtn2ActionPerformed

    private void jtxtMobileKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt) && !lb.isBlank(jtxtMobile)) {
            lb.addGlassPane(this);
            Call<JsonObject> call = salesReturnAPI.GetDataFromServer(jtxtMobile.getText(), "23");
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    lb.removeGlassPane(SalesReturnController.this);
                    if (rspns.isSuccessful()) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        if (array.size() > 0) {
                            ac_cd = (array.get(0).getAsJsonObject().get("AC_CD").getAsString());
                            jtxtAcName.setText(array.get(0).getAsJsonObject().get("FNAME").getAsString());
                            jtxtAcAlias.setText(array.get(0).getAsJsonObject().get("AC_CD").getAsString());
                            jtxtCardNo.setText(array.get(0).getAsJsonObject().get("CARD_NO").getAsString());
                            jtxtTin.setText(array.get(0).getAsJsonObject().get("TIN").getAsString());
                            jtxtAddress.setText(array.get(0).getAsJsonObject().get("ADD1").getAsString());
                            jtxtMobile.setText(array.get(0).getAsJsonObject().get("MOBILE1").getAsString());
                            jtxtRefBy.setText(array.get(0).getAsJsonObject().get("REF_BY").getAsString());
                            jtxtTag.requestFocusInWindow();
                        } else {
                            ac_cd = "";
                            jtxtAcName.setText("");
                            jtxtCardNo.setText("");
                            jtxtTin.setText("");
                            jtxtAddress.setText("");
                            jtxtRefBy.setText("");
                            lb.confirmDialog("Mobile does not exist.\nDo you want to create new account?");
                            if (lb.type) {
                                CreateSalesAccount bmc = new CreateSalesAccount(null, true);
                                bmc.setLocationRelativeTo(null);
                                bmc.setMobileNumber(jtxtMobile.getText());
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
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(SalesReturnController.this);
                }
            });
        } else {
            if (lb.isEnter(evt)) {
                lb.enterFocus(evt, jtxtAcAlias);
            }
        }
    }//GEN-LAST:event_jtxtMobileKeyPressed

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
                        jtxtTag.requestFocusInWindow();
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

    private void jtxtCardNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardNoKeyPressed
        // TODO add your handling code here:

        if (lb.isEnter(evt) && !lb.isBlank(jtxtCardNo)) {
            lb.addGlassPane(this);
            Call<JsonObject> call = salesReturnAPI.GetDataFromServer(jtxtCardNo.getText(), "22");
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                    lb.removeGlassPane(SalesReturnController.this);
                    if (rspns.isSuccessful()) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        if (array.size() > 0) {
                            ac_cd = (array.get(0).getAsJsonObject().get("AC_CD").getAsString());
                            jtxtAcName.setText(array.get(0).getAsJsonObject().get("FNAME").getAsString());
                            jtxtCardNo.setText(array.get(0).getAsJsonObject().get("CARD_NO").getAsString());
                            jtxtTin.setText(array.get(0).getAsJsonObject().get("TIN").getAsString());
                            jtxtAddress.setText(array.get(0).getAsJsonObject().get("ADD1").getAsString());
                            jtxtMobile.setText(array.get(0).getAsJsonObject().get("MOBILE1").getAsString());
                            jtxtRefBy.setText(array.get(0).getAsJsonObject().get("REF_BY").getAsString());
                            jtxtTag.requestFocusInWindow();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(SalesReturnController.this);
                }
            });
        }
    }//GEN-LAST:event_jtxtCardNoKeyPressed

    private void jtxtRefByFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRefByFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRefByFocusGained

    private void jtxtRefByKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefByKeyPressed
        lb.enterFocus(evt, jtxtAcAlias);
    }//GEN-LAST:event_jtxtRefByKeyPressed

    private void jtxtRefByKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRefByKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtRefByKeyTyped

    private void jtxtAdvanceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAdvanceFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAdvanceFocusGained

    private void jtxtAdvanceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAdvanceFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
        updateFooter();
    }//GEN-LAST:event_jtxtAdvanceFocusLost

    private void jtxtAdvanceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAdvanceKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnOK.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtAdvanceKeyPressed

    private void jtxtPmtDaysFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPmtDaysFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPmtDaysFocusGained

    private void jtxtPmtDaysFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPmtDaysFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
        setDueDate();
    }//GEN-LAST:event_jtxtPmtDaysFocusLost

    private void jtxtPmtDaysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPmtDaysKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 2);
    }//GEN-LAST:event_jtxtPmtDaysKeyTyped

    private void jtxtPmtDaysKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPmtDaysKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcmbPmt);
    }//GEN-LAST:event_jtxtPmtDaysKeyPressed

    private void doClose(int retStatus) {
        lb.confirmDialog("Do you want to discard this voucher?");
        if (lb.type) {
            returnStatus = retStatus;
            setVisible(false);
            dispose();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnOK;
    private javax.swing.JComboBox jcmbPmt;
    private javax.swing.JComboBox jcmbTax;
    private javax.swing.JLabel jlblAddTax;
    private javax.swing.JLabel jlblAdjst;
    private javax.swing.JLabel jlblBasicAmount;
    private javax.swing.JLabel jlblBillDay1;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblNet;
    private javax.swing.JLabel jlblNet1;
    private javax.swing.JLabel jlblPmtDays;
    private javax.swing.JLabel jlblRemAmt;
    private javax.swing.JLabel jlblTax;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JLabel jlblVday;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAcName;
    private javax.swing.JTextField jtxtAddress;
    private javax.swing.JTextField jtxtAdvance;
    private javax.swing.JTextField jtxtCardNo;
    private javax.swing.JTextField jtxtDueDate;
    private javax.swing.JTextField jtxtMobile;
    private javax.swing.JTextField jtxtPmtDays;
    private javax.swing.JTextField jtxtRefBy;
    private javax.swing.JTextField jtxtTin;
    private javax.swing.JTextField jtxtVouDate;
    private javax.swing.JTextField jtxtVoucher;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
