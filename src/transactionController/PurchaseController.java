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
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import model.PurchaseControllerDetailModel;
import model.PurchaseControllerHeaderModel;
import model.SeriesHead;
import model.SeriesMaster;
import model.TaxMasterModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.PurchaseAPI;
import retrofitAPI.StartUpAPI;
import retrofitAPI.SupportAPI;
import selecthint.SeriesSelection;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;
import support.SelectDailog;
import transactionView.PurchaseView;

/**
 *
 * @author bhaumik
 */
public class PurchaseController extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    Library lb = Library.getInstance();
    public String ac_cd = "";
    private String ref_no = "";
    private javax.swing.JTextField jtxtTag = null, jtxtIMEI = null, jtxtSerialNo = null, jtxtQty = null, jtxtRate = null,
            jtxtAmount = null, jtxtBasicAmt = null, jtxtTaxAmt = null, jtxtAddTaxAmt = null, jtxtNlc = null;
    private javax.swing.JTextField jtxtDiscPer = null, jtxtMRP = null;
    javax.swing.JTextField jtxtItem = null;
    JLabel jlblTotQty;
    JLabel jlblTotAmt;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    DefaultTableModel dtm = null;
    int type = -1;
    boolean flag = false;
    PurchaseAPI purchaseAPI = null;
    private ReportTable viewTable = null;
    private String sr_cd = "";
    private String item_name = "";
    private int isMain = -1;
    private ArrayList<PurchaseControllerDetailModel> subDetail = new ArrayList<PurchaseControllerDetailModel>();
    private HashMap<String, String> itemCode = new HashMap<String, String>();
    private DefaultTableModel dtmTax;
    private HashMap<String, double[]> taxInfo;
    private PurchaseView pbv;
    private int tax_type;

    /**
     * Creates new form PurchaseController
     */
    public PurchaseController(java.awt.Frame parent, boolean modal, int type, PurchaseView pbv, int tax_type) {
        super(parent, modal);
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmTax = (DefaultTableModel) jTable2.getModel();
        this.pbv = pbv;
        this.tax_type = tax_type;
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

        lb.setDateChooserPropertyInit(jtxtBillDate);
        lb.setDateChooserPropertyInit(jtxtVouDate);
        addJtextBox();
        addJLabel();
        addTaxCombo();
        tableForView();
        searchOnTextFields();
        flag = true;
        jcmbType.setSelectedIndex(type);
        jcmbPmt.setSelectedIndex(1);
        setUpData();
        taxInfo = new HashMap<String, double[]>();
        setTitle("Prchase Bill");
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
        jlblTotQty.setHorizontalAlignment(SwingConstants.CENTER);
        jlblTotAmt.setHorizontalAlignment(SwingConstants.RIGHT);

        jlblTotQty.setBounds(0, 0, 20, 20);
        jlblTotQty.setVisible(true);
        jPanel4.add(jlblTotQty);

        jlblTotAmt.setBounds(0, 0, 20, 20);
        jlblTotAmt.setVisible(true);
        jPanel4.add(jlblTotAmt);

        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotQty, null, null, null, null, null, null, null, jlblTotAmt, null, null, null, null, null});
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
                jtxtItem.requestFocusInWindow();
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
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                        jbtnBulkPurchase.doClick();
                    }
                }
                if (lb.isEnter(e)) {
                    SeriesSelection ss = new SeriesSelection(null, true);
                    ss.setSeriesData("3", jtxtItem.getText().toUpperCase());
                    ss.setVisible(true);
                    if (ss.getReturnStatus() == SelectDailog.RET_OK) {
                        int row = ss.getjTable1().getSelectedRow();
                        if (row != -1) {
                            sr_cd = ss.getjTable1().getValueAt(row, 0).toString();
                            item_name = ss.getjTable1().getValueAt(row, 1).toString();
                            jtxtItem.setText(ss.getjTable1().getValueAt(row, 1).toString());
                            jtxtIMEI.requestFocusInWindow();
                            if (tax_type == 0) {
                                jcmbTax.setSelectedItem(ss.getjTable1().getValueAt(row, 3).toString());
                            } else {
                                jcmbTax.setSelectedItem(ss.getjTable1().getValueAt(row, 5).toString());
                            }
                            jcmbTaxItemStateChanged(null);
                            getLastRate();
                        }
                        ss.dispose();
                    } else {
                        jtxtItem.requestFocusInWindow();
                    }
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
                jcmbTaxItemStateChanged(null);
                calculation();
                lb.toDouble(e);
            }
        });

        jtxtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_L) {
                    if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                        String tag = "";
                        if (!jtxtIMEI.getText().equalsIgnoreCase("")) {
                            tag = (jtxtIMEI.getText());
                        } else if (!jtxtSerialNo.getText().equalsIgnoreCase("")) {
                            tag = (jtxtSerialNo.getText());
                        } else {
                            tag = "";
                        }
                        SplitTag st = new SplitTag(null, true, tag);
                        st.setVisible(true);
                        st.setLocationRelativeTo(null);
                        if (st.getReturnStatus() == SplitTag.RET_OK) {
                            subDetail = st.detail;
                        }
                    }
                }
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
                lb.enterFocus(e, jtxtDiscPer);
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
                lb.enterFocus(e, jtxtNlc);
            }
        });
        jtxtNlc = new javax.swing.JTextField();

        jtxtNlc.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                lb.selectAll(e);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lb.toDouble(e);
            }
        });

        jtxtNlc.addKeyListener(new java.awt.event.KeyAdapter() {
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
                lb.setTable(jTable1, new JComponent[]{null, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, null, null, jcmbTax, jtxtBasicAmt, jtxtTaxAmt, jtxtAddTaxAmt, jtxtDiscPer, jtxtNlc, jtxtMRP, jtxtAmount, null, null});
                lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotQty, null, null, null, null, null, null, null, jlblTotAmt, null, null, null, null, null});
            }
        });

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
        jPanel3.add(jtxtDiscPer);

        jtxtNlc.setBounds(0, 0, 20, 20);
        jtxtNlc.setVisible(true);
        jPanel3.add(jtxtNlc);

        jtxtMRP.setBounds(0, 0, 20, 20);
        jtxtMRP.setVisible(true);
        jPanel3.add(jtxtMRP);

        jtxtAmount.setBounds(0, 0, 20, 20);
        jtxtAmount.setVisible(true);
        jPanel3.add(jtxtAmount);

        lb.setTable(jTable1, new JComponent[]{null, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, null, null, jcmbTax, jtxtBasicAmt, jtxtTaxAmt, jtxtAddTaxAmt, jtxtDiscPer, jtxtNlc, jtxtMRP, jtxtAmount, null, null});
    }

    private void setSeriesData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(PurchaseController.this);
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
                                if (row != -1) {
                                    sr_cd = viewTable.getValueAt(row, 0).toString();
                                    item_name = viewTable.getValueAt(row, 1).toString();
                                    jtxtItem.setText(viewTable.getValueAt(row, 1).toString());
                                    jtxtIMEI.requestFocusInWindow();
                                    jcmbTax.setSelectedItem(viewTable.getValueAt(row, 3).toString());
                                    jcmbTaxItemStateChanged(null);
                                    getLastRate();
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
                    lb.removeGlassPane(PurchaseController.this);
                }
            });
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void getLastRate() {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).GetDataFromServer("21", sr_cd, ac_cd);
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(PurchaseController.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        JsonObject header = (JsonObject) response.body();
                        if (header.get("result").getAsInt() == 1) {
                            if (header.get("data").getAsJsonArray().size() != 0) {
                                jtxtRate.setText(header.get("data").getAsJsonArray().get(0).getAsJsonObject().get("rate").getAsString());
                            }
                            jtxtIMEI.requestFocusInWindow();
                        } else {
                            lb.showMessageDailog(response.body().get("Cause").getAsString());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(PurchaseController.this);

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

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    public void setData(PurchaseAPI purchaseAPI, String ref_no) {
        this.ref_no = ref_no;
        if (purchaseAPI == null) {
            purchaseAPI = lb.getRetrofit().create(PurchaseAPI.class);
        }
        this.purchaseAPI = purchaseAPI;

        if (!ref_no.equalsIgnoreCase("")) {
            try {
                jComboBox1.setEnabled(false);
                Call<JsonObject> call = purchaseAPI.getBill(ref_no, "4");
                lb.addGlassPane(this);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        lb.removeGlassPane(PurchaseController.this);
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
                                        jtxtBillDate.setText(lb.ConvertDateFormetForDBForConcurrency(array.get(i).getAsJsonObject().get("BILL_DATE").getAsString()));
                                        jlblVday.setText(lb.setDay(jtxtVouDate));
                                        jlblBillDay1.setText(lb.setDay(jtxtDueDate));
                                        jlblBillDay.setText(lb.setDay(jtxtBillDate));
                                        jcmbType.setSelectedIndex(array.get(i).getAsJsonObject().get("V_TYPE").getAsInt());
                                        jComboBox1.setSelectedIndex(array.get(i).getAsJsonObject().get("BRANCH_CD").getAsInt() - 1);
                                        jcmbPmt.setSelectedIndex(array.get(i).getAsJsonObject().get("PMT_MODE").getAsInt());
                                        jtxtBillNo.setText(array.get(i).getAsJsonObject().get("BILL_NO").getAsString());
                                        ac_cd = array.get(i).getAsJsonObject().get("AC_CD").getAsString();
                                        jtxtName.setText(array.get(i).getAsJsonObject().get("FNAME").getAsString());
                                        if (array.get(i).getAsJsonObject().get("TAX_TYPE") == null) {
                                            tax_type = 0;
                                        } else {
                                            tax_type = array.get(i).getAsJsonObject().get("TAX_TYPE").getAsInt();
                                        }
//                                    jtxtAddress.setText(array.get(i).getAsJsonObject().get("ADD1").getAsString());
//                                    jtxtMobile.setText(array.get(i).getAsJsonObject().get("MOBILE1").getAsString());
                                        jtxtTinNum.setText(array.get(i).getAsJsonObject().get("TIN").getAsString());
                                        PurchaseController.this.ref_no = array.get(i).getAsJsonObject().get("REF_NO").getAsString();
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
                                        jtxtFrieght.setText(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("FR_CHG").getAsDouble()));
                                        jlblVday.setText(lb.setDay(jtxtVouDate));
                                        jlblBillDay.setText(lb.setDay(jtxtBillDate));

                                        Vector row = new Vector();
                                        row.add(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("QTY").getAsInt());
                                        row.add(array.get(i).getAsJsonObject().get("RATE").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("PUR_TAG_NO").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("TAG_DEL").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("TAX_NAME").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("BASIC_AMT").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("TAX_AMT").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("ADD_TAX_AMT").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("DISC_RATE").getAsString());
                                        row.add(array.get(i).getAsJsonObject().get("NLC").getAsString());
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
                                PurchaseController.this.setVisible(true);
                            } else {
                                lb.showMessageDailog(response.body().get("Cause").toString());
                            }
                        } else {
                            lb.showMessageDailog(response.body().get("Cause").toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                        lb.removeGlassPane(PurchaseController.this);
                    }
                });
            } catch (Exception ex) {
                lb.printToLogFile("Exception at getDataFor PurchaseBill", ex);
            }
        } else {
            PurchaseController.this.setVisible(true);
        }

    }

    private void setAccountDetailMobile(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(PurchaseController.this);
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
//                                    jtxtMobile.setText(response.body().getAccountHeader().get(row).getMOBILE1());
                                    jtxtName.setText(header.getAccountHeader().get(row).getFNAME());
//                                    jtxtAddress.setText(response.body().getAccountHeader().get(row).getADD1());
                                    jtxtTinNum.setText(header.getAccountHeader().get(row).getTIN());
                                    jtxtItem.requestFocusInWindow();
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
                    lb.removeGlassPane(PurchaseController.this);
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
            tot += lb.isNumber2(jTable1.getValueAt(i, 15).toString());
            tot_basic += lb.isNumber2(jTable1.getValueAt(i, 9).toString());
            tot_tax += lb.isNumber2(jTable1.getValueAt(i, 10).toString());
            tot_add_tax += lb.isNumber2(jTable1.getValueAt(i, 11).toString());
            if (jTable1.getValueAt(i, 16).toString().equalsIgnoreCase("1")) {
                tot_qty += (int) lb.isNumber2(jTable1.getValueAt(i, 4).toString());
            }
        }
        jlblTotal.setText(lb.Convert2DecFmtForRs(tot));
        jlblBasicAmount.setText(lb.Convert2DecFmtForRs(tot_basic));
        jlblTax.setText(lb.Convert2DecFmtForRs(tot_tax));
        jlblAddTax.setText(lb.Convert2DecFmtForRs(tot_add_tax));
        jlblTotQty.setText((tot_qty) + "");
        double fr_chg = lb.isNumber(jtxtFrieght);
        jlblNet.setText(lb.Convert2DecFmtForRs(tot + fr_chg));

        dtmTax.setRowCount(0);
        double tax_amt = 0.00, add_tax_amt = 0.00;
        Iterator it = taxInfo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, double[]> pair = (Map.Entry<String, double[]>) it.next();
            double tax[] = pair.getValue();
            Vector row = new Vector();
            row.add(pair.getKey());
            row.add(lb.Convert2DecFmtForRs(tax[0]));
            row.add(lb.Convert2DecFmtForRs(tax[1]));
            row.add(lb.Convert2DecFmtForRs(tax[0] + tax[1]));
            tax_amt += tax[0];
            add_tax_amt += tax[1];
            dtmTax.addRow(row);
        }
        Vector row = new Vector();
        row.add("Total");
        row.add(lb.Convert2DecFmtForRs(tax_amt));
        row.add(lb.Convert2DecFmtForRs(add_tax_amt));
        row.add(lb.Convert2DecFmtForRs(tax_amt + add_tax_amt));
        dtmTax.addRow(row);
    }

    private void clear() {
        jtxtItem.setText("");
        sr_cd = "";
        item_name = "";
        jtxtIMEI.setText("");
        jtxtSerialNo.setText("");
        jtxtQty.setText("");
        jtxtRate.setText("");
        jtxtDiscPer.setText("");
        jtxtMRP.setText("");
        jtxtNlc.setText("");
        jtxtTaxAmt.setText("");
        jtxtAddTaxAmt.setText("");
        jtxtBasicAmt.setText("");
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

        if (lb.isNumber(jtxtQty) <= 0) {
            lb.showMessageDailog("Qty greater than 0");
            jtxtIMEI.requestFocusInWindow();
            return false;
        }

        if (lb.isNumber(jtxtAmount) <= 0) {
            lb.showMessageDailog("Amt greater than 0");
            jtxtIMEI.requestFocusInWindow();
            return false;
        }
        return flag;
    }

    public boolean validateSubRow(String tag) {
        boolean flag = true;

        if (sr_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Invalid Product Name");
            jtxtItem.requestFocusInWindow();
            flag = false;
        }
        if (!tag.equalsIgnoreCase("")) {
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                if (i != jTable1.getSelectedRow()
                        && tag.equalsIgnoreCase(jTable1.getValueAt(i, 0).toString())
                        && jTable1.getValueAt(i, 16).toString().equalsIgnoreCase("0")
                        && jTable1.getValueAt(i, 17).toString().equalsIgnoreCase(sr_cd)) {
                    lb.showMessageDailog("Item already present");
                    jtxtIMEI.requestFocusInWindow();
                    return false;
                }
            }
        }
        return flag;
    }

    private boolean validateVoucher() {
        if (lb.isBlank(jtxtTinNum)) {
            lb.showMessageDailog("Please enter tin number of party");
            return false;
        }

        if (lb.isBlank(jtxtBillNo)) {
            lb.showMessageDailog("Please enter Bill number of party");
            return false;
        }
        if (lb.ConvertDateFormetForDB(jtxtVouDate.getText()).equalsIgnoreCase("")) {
            lb.showMessageDailog("Please Enter valid Voucher Date");
            return false;
        }

        if (lb.ConvertDateFormetForDB(jtxtBillDate.getText()).equalsIgnoreCase("")) {
            lb.showMessageDailog("Please Enter valid Voucher Date");
            return false;
        }

        if (lb.ConvertDateFormetForDB(jtxtDueDate.getText()).equalsIgnoreCase("")) {
            lb.showMessageDailog("Please Enter Due Date");
            return false;
        }

        if (jTable1.getRowCount() == 0) {
            lb.showMessageDailog("Voucher can not be empty");
            return false;
        }
        return true;
    }

    private void saveVoucher() {
        final PurchaseControllerHeaderModel header = new PurchaseControllerHeaderModel();
        header.setRef_no(ref_no);
        header.setREMARK(jTextArea1.getText());
        header.setAC_CD(ac_cd);
        header.setADJST(lb.isNumber(jlblAdjst));
        header.setBILL_DATE(lb.ConvertDateFormetForDB(jtxtBillDate.getText()));
        header.setDUE_DATE(lb.ConvertDateFormetForDB(jtxtDueDate.getText()));
        header.setBILL_NO(jtxtBillNo.getText());
        header.setDET_TOT(lb.isNumber(jlblBasicAmount));
        header.setNET_AMT(lb.isNumber(jlblNet));
        header.setPMT_MODE(jcmbPmt.getSelectedIndex());
        header.setTAX_AMT(lb.isNumber(jlblTax));
        header.setADD_TAX_AMT(lb.isNumber(jlblAddTax));
        header.setFRIEGHT_CHARGES(lb.isNumber(jtxtFrieght));
        header.setAc_name(jtxtName.getText());
        header.setBRANCH_CD(jComboBox1.getSelectedIndex() + 1);
        header.setUSER_ID(SkableHome.user_id);
        header.setV_DATE(lb.ConvertDateFormetForDB(jtxtVouDate.getText()));
        header.setV_TYPE(jcmbType.getSelectedIndex());
        header.setTAX_TYPE(tax_type);

        final ArrayList<PurchaseControllerDetailModel> detail = new ArrayList<PurchaseControllerDetailModel>();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            PurchaseControllerDetailModel model = new PurchaseControllerDetailModel();
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
            model.setNLC(lb.isNumber2(jTable1.getValueAt(i, 13).toString()));
            model.setMRP(lb.isNumber2(jTable1.getValueAt(i, 14).toString()));
            model.setAMT(lb.isNumber2(jTable1.getValueAt(i, 15).toString()));
            model.setIsMain((int) lb.isNumber2(jTable1.getValueAt(i, 16).toString()));
            detail.add(model);
        }

        String headerJson = new Gson().toJson(header);
        String detailJson = new Gson().toJson(detail);
        Call<JsonObject> addUpdaCall = purchaseAPI.addUpdatePurchaseBill(headerJson, detailJson);
        lb.addGlassPane(this);
        addUpdaCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                lb.removeGlassPane(PurchaseController.this);
                if (response.isSuccessful()) {
                    System.out.println(response.body().toString());
                    JsonObject object = response.body();
                    if (object.get("result").getAsInt() == 1) {
                        lb.showMessageDailog("Voucher saved successfully");
                        if (pbv != null) {
                            pbv.setData();
                        }
                        PurchaseController.this.dispose();
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
                lb.removeGlassPane(PurchaseController.this);
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

    public boolean setBulkData(ArrayList<PurchaseControllerDetailModel> models) {
        boolean flag = true;
        for (int i = 0; i < models.size() && flag; i++) {
            PurchaseControllerDetailModel model = models.get(i);
            jtxtIMEI.setText(model.getIMEI_NO());
            jtxtSerialNo.setText(model.getSERAIL_NO());
            item_name = model.getSR_NAME();
            jtxtRate.setText(lb.Convert2DecFmtForRs(model.getRATE()));
            jtxtAmount.setText(lb.Convert2DecFmtForRs(model.getRATE()));
            jcmbTax.setSelectedItem(model.getTAX_CD());
            jtxtQty.setText("1");
            jtxtBasicAmt.setText(lb.Convert2DecFmtForRs(model.getBASIC_AMT()));
            jtxtTaxAmt.setText(lb.Convert2DecFmtForRs(model.getTAX_AMT()));
            jtxtAddTaxAmt.setText(lb.Convert2DecFmtForRs(model.getADD_TAX_AMT()));
            jtxtDiscPer.setText(lb.Convert2DecFmtForRs(model.getDISC_PER()));
            jtxtNlc.setText(lb.Convert2DecFmtForRs(model.getNLC()));
            jtxtMRP.setText(lb.Convert2DecFmtForRs(model.getMRP()));
            isMain = model.getIsMain();
            sr_cd = model.getSR_CD();

            String tag = "";
            if (!jtxtIMEI.getText().equalsIgnoreCase("")) {
                tag = (jtxtIMEI.getText());
            } else if (!jtxtSerialNo.getText().equalsIgnoreCase("")) {
                tag = (jtxtSerialNo.getText());
            } else {
                tag = "";
            }
            if (model.getIsMain() == 1) {
                if (validateRow(tag)) {
                    addRow(tag);
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                jtxtIMEI.setText("");
                jtxtSerialNo.setText("");
                if (validateSubRow(tag)) {
                    addRow(tag);
                    flag = true;
                } else {
                    flag = false;
                }
            }
            clear();
        }

        setTotal();
        return flag;
    }

    private void addRow(String tag) {
        Vector row = new Vector();
        row.add(tag);
        row.add(item_name);
        row.add(jtxtIMEI.getText());
        row.add(jtxtSerialNo.getText());
        row.add(1);
        row.add(lb.isNumber2(jtxtRate.getText()));
        row.add("");
        row.add("0");
        row.add(jcmbTax.getSelectedItem().toString());
        row.add(lb.isNumber2(jtxtBasicAmt.getText()));
        row.add(lb.isNumber2(jtxtTaxAmt.getText()));
        row.add(lb.isNumber2(jtxtAddTaxAmt.getText()));
        row.add(lb.isNumber2(jtxtDiscPer.getText()));
        row.add(lb.isNumber2(jtxtNlc.getText()));
        row.add(lb.isNumber2(jtxtMRP.getText()));
        row.add(lb.isNumber2(jtxtRate.getText()));
        row.add(isMain);
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
        jLabel2 = new javax.swing.JLabel();
        jcmbType = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jcmbPmt = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jtxtBillNo = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jtxtBillDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        jlblVday = new javax.swing.JLabel();
        jlblBillDay = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtName = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jtxtTinNum = new javax.swing.JTextField();
        jbtnAdd = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jtxtDueDate = new javax.swing.JTextField();
        jBillDateBtn2 = new javax.swing.JButton();
        jlblBillDay1 = new javax.swing.JLabel();
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
        jLabel20 = new javax.swing.JLabel();
        jtxtFrieght = new javax.swing.JTextField();
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
        jButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jbtnBulkPurchase = new javax.swing.JButton();

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

        jLabel2.setText("Purchase Type");

        jcmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "RD Purchase", "URD Purchase", "OGS Purchase", "Tax Free Purchase" }));
        jcmbType.setEnabled(false);
        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        jLabel6.setText("Payment Mode");

        jcmbPmt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cash", "Credit" }));
        jcmbPmt.setEnabled(false);
        jcmbPmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbPmtKeyPressed(evt);
            }
        });

        jLabel25.setText("Bill No");

        jtxtBillNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBillNoFocusGained(evt);
            }
        });
        jtxtBillNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBillNoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtBillNoKeyTyped(evt);
            }
        });

        jLabel26.setText("Bill Date");

        jtxtBillDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBillDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBillDateFocusLost(evt);
            }
        });
        jtxtBillDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBillDateKeyPressed(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        jlblVday.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jlblBillDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Name");

        jtxtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtNameFocusLost(evt);
            }
        });
        jtxtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtNameKeyTyped(evt);
            }
        });

        jLabel18.setText("Tin");

        jtxtTinNum.setEnabled(false);
        jtxtTinNum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTinNumFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTinNumFocusLost(evt);
            }
        });
        jtxtTinNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTinNumKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtTinNumKeyTyped(evt);
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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
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
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblPmtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtPmtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtBillDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlblVday, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlblBillDay, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBillDateBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlblBillDay1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcmbPmt, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtBillNo, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(185, 185, 185)
                .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(292, 292, 292)
                .addComponent(jbtnAdd)
                .addGap(8, 8, 8))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(885, 885, 885)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtxtTinNum)
                .addGap(8, 8, 8))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel24, jLabel26, jlblPmtDays});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnAdd)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcmbPmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jlblVday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblBillDay1)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtBillNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBillDateBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblBillDay)
                            .addComponent(jtxtBillDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtxtTinNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jtxtPmtDays)
                                .addComponent(jlblPmtDays, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jComboBox1, jLabel1, jLabel2, jLabel24, jLabel3, jLabel6, jcmbPmt, jcmbType, jlblVday, jtxtVouDate, jtxtVoucher});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn1, jLabel25, jLabel26, jlblBillDay, jtxtBillDate, jtxtBillNo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn2, jLabel27, jlblBillDay1, jtxtDueDate});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel18, jLabel4, jtxtName, jtxtTinNum});

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag No", "Product Name", "IMEI No", "Serial No", "Qty", "Rate", "ref_no", "IS_del", "Tax", "Basic Amt", "Tax Amt", "Add Tax Amt", "Disc", "NLC", "MRP", "Amount", "Main", "SR_CD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(7).setMinWidth(0);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(7).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(10).setResizable(false);
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(11).setResizable(false);
            jTable1.getColumnModel().getColumn(11).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(12).setResizable(false);
            jTable1.getColumnModel().getColumn(12).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(13).setResizable(false);
            jTable1.getColumnModel().getColumn(13).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(14).setResizable(false);
            jTable1.getColumnModel().getColumn(14).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(15).setResizable(false);
            jTable1.getColumnModel().getColumn(15).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(16).setMinWidth(0);
            jTable1.getColumnModel().getColumn(16).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(16).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(17).setMinWidth(0);
            jTable1.getColumnModel().getColumn(17).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(17).setMaxWidth(0);
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

        jLabel20.setText("Frieght Charges");

        jtxtFrieght.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtFrieght.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFrieghtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFrieghtFocusLost(evt);
            }
        });
        jtxtFrieght.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFrieghtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtFrieghtKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblAddTax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblNet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblBasicAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblAdjst, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addComponent(jtxtFrieght))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblBasicAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblTax, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblAddTax, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jlblAdjst, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtFrieght, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblNet, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel20, jtxtFrieght});

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

        jButton1.setText("Upload Excel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel6.setLayout(new java.awt.BorderLayout());

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

        jPanel6.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jbtnBulkPurchase.setText("Bulk Purchase");
        jbtnBulkPurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBulkPurchaseActionPerformed(evt);
            }
        });

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
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1245, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtnBulkPurchase, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                                    .addComponent(jScrollPane2))
                                .addGap(88, 88, 88))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1)
                                    .addComponent(jbtnBulkPurchase))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnOK))
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
        lb.enterFocus(evt, jtxtBillDate);
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
        lb.enterFocus(evt, jcmbPmt);
    }//GEN-LAST:event_jcmbTypeKeyPressed

    private void jcmbPmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbPmtKeyPressed
        lb.enterFocus(evt, jtxtBillDate);
    }//GEN-LAST:event_jcmbPmtKeyPressed

    private void jtxtBillNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBillNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBillNoFocusGained

    private void jtxtBillNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBillNoKeyPressed
        lb.enterFocus(evt, jtxtName);
    }//GEN-LAST:event_jtxtBillNoKeyPressed

    private void jtxtBillNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBillNoKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtBillNoKeyTyped

    private void jtxtBillDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBillDateFocusLost
        // TODO add your handling code here:
        try {
            if (jtxtBillDate.getText().contains("/")) {
                jtxtBillDate.setText(jtxtBillDate.getText().replace("/", ""));
            }
            if (jtxtBillDate.getText().length() == 8) {
                String temp = jtxtBillDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtBillDate.setText(setDate);
            }
            //            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtBillDate.getText().trim()))) != null) {
            //                jtxtDueDate.requestFocusInWindow();
            //            }
            jlblBillDay.setText(lb.setDay(jtxtBillDate));
        } catch (Exception ex) {
            jtxtBillDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtBillDateFocusLost

    private void jtxtBillDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBillDateFocusGained
        // TODO add your handling code here:
        jtxtBillDate.selectAll();
    }//GEN-LAST:event_jtxtBillDateFocusGained

    private void jtxtBillDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBillDateKeyPressed
        lb.enterFocus(evt, jtxtPmtDays);
    }//GEN-LAST:event_jtxtBillDateKeyPressed

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtBillDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtBillDate.getX() - 200, jPanel1.getY() + 170, jtxtBillDate.getX() + odc.getWidth(), jtxtBillDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    private void jtxtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtNameFocusGained

    private void jtxtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNameFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtNameFocusLost

    private void jtxtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (lb.isBlank(jtxtName)) {
                lb.confirmDialog("Do you want to create new account?");
                if (lb.type) {
                    CreateSalesAccount bmc = new CreateSalesAccount(null, true);
                    bmc.setLocationRelativeTo(null);

                    bmc.setVisible(true);
                    if (bmc.getReturnStatus() == RET_OK) {
                        ac_cd = bmc.ac_cd;
                        jtxtName.setText(bmc.account.getFNAME());
                        jtxtTinNum.setText(bmc.account.getTIN());
                        jtxtTag.requestFocusInWindow();
                    }
                } else {
                    ac_cd = "";
                }
            } else {
                setAccountDetailMobile("2", jtxtName.getText());
            }
        }
    }//GEN-LAST:event_jtxtNameKeyPressed

    private void jtxtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtNameKeyTyped

    private void jtxtTinNumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTinNumFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTinNumFocusGained

    private void jtxtTinNumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTinNumFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtTinNumFocusLost

    private void jtxtTinNumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTinNumKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtItem);
    }//GEN-LAST:event_jtxtTinNumKeyPressed

    private void jtxtTinNumKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTinNumKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 12);
    }//GEN-LAST:event_jtxtTinNumKeyTyped

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
                    jtxtNlc.setText(jTable1.getValueAt(index, 13).toString());
                    jtxtMRP.setText(jTable1.getValueAt(index, 14).toString());
                    jtxtAmount.setText(jTable1.getValueAt(index, 15).toString());
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        int index = jTable1.getSelectedRow();
        int is_del = (int) lb.isNumber(jTable1.getValueAt(index, 7).toString());
        int is_main = (int) lb.isNumber(jTable1.getValueAt(index, 16).toString());
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (index != -1 && is_del == 0) {
                lb.confirmDialog("Do you want to delete this row?");
                if (lb.type) {
                    deleteSubRow(jTable1.getValueAt(index, 0).toString());
                    setTotal();
                }
            }
        }

        if (evt.getKeyCode() == KeyEvent.VK_D) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                if (index != -1 && is_del == 0) {
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
                    add_tax_rate = 0;
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
                double tax = lb.isNumber(jlblTax);
//                if (add_tax_rate_On == 1) {
//                    jtxtAddTaxAmt.setText(lb.Convert2DecFmtForRs((add_tax_rate * taxable) / 100));
//                } else {
                jtxtAddTaxAmt.setText(lb.Convert2DecFmtForRs((add_tax_rate * taxable) / 100));
//                }
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
                    if (!jtxtIMEI.getText().equalsIgnoreCase("")) {
                        row.add(jtxtIMEI.getText());
                    } else if (!jtxtSerialNo.getText().equalsIgnoreCase("")) {
                        row.add(jtxtSerialNo.getText());
                    } else {
                        row.add("");
                    }
                    row.add(item_name);
                    row.add(jtxtIMEI.getText());
                    row.add(jtxtSerialNo.getText());
                    row.add(1);
                    row.add(lb.isNumber2(jtxtRate.getText()));
                    row.add("");
                    row.add("0");
                    row.add(jcmbTax.getSelectedItem().toString());
                    row.add(lb.isNumber2(jtxtBasicAmt.getText()));
                    row.add(lb.isNumber2(jtxtTaxAmt.getText()));
                    row.add(lb.isNumber2(jtxtAddTaxAmt.getText()));
                    row.add(lb.isNumber2(jtxtDiscPer.getText()));
                    row.add(lb.isNumber2(jtxtNlc.getText()));
                    row.add(lb.isNumber2(jtxtMRP.getText()));
                    row.add(lb.isNumber2(jtxtRate.getText()));
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
                    Vector row = new Vector();
                    row.add(subDetail.get(i).getTAG_NO());
                    row.add(subDetail.get(i).getSR_NAME());
                    row.add(subDetail.get(i).getIMEI_NO());
                    row.add(subDetail.get(i).getSERAIL_NO());
                    row.add(subDetail.get(i).getQTY());
                    row.add(subDetail.get(i).getRATE());
                    row.add("");
                    row.add(0);
                    row.add(subDetail.get(i).getTAX_CD());
                    row.add(subDetail.get(i).getBASIC_AMT());
                    row.add(subDetail.get(i).getTAX_AMT());
                    row.add(subDetail.get(i).getADD_TAX_AMT());
                    row.add(subDetail.get(i).getDISC_PER());
                    row.add(0.00);
                    row.add(subDetail.get(i).getMRP());
                    row.add(subDetail.get(i).getAMT());
                    row.add("0");
                    row.add(subDetail.get(i).getSR_CD());
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
                jTable1.setValueAt(lb.isNumber2(jtxtNlc.getText()), index, 13);
                jTable1.setValueAt(lb.isNumber2(jtxtMRP.getText()), index, 14);
                jTable1.setValueAt(lb.isNumber2(jtxtAmount.getText()), index, 15);
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
                saveVoucher();
            }
        }
    }//GEN-LAST:event_jbtnOKActionPerformed

    private void jtxtFrieghtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFrieghtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFrieghtFocusGained

    private void jtxtFrieghtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFrieghtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtFrieghtFocusLost

    private void jtxtFrieghtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFrieghtKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnOK);
    }//GEN-LAST:event_jtxtFrieghtKeyPressed

    private void jtxtFrieghtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFrieghtKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtFrieghtKeyTyped

    private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                jtxtFrieght.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTextArea1KeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtBillNo);
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
            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtDueDate.getText().trim()))) == null) {
                jtxtDueDate.setText(jtxtVouDate.getText());
            }
            jlblBillDay1.setText(lb.setDay(jtxtDueDate));
        } catch (Exception ex) {
            jtxtDueDate.setText(jtxtVouDate.getText());
        }
    }//GEN-LAST:event_jtxtDueDateFocusLost

    private void jtxtDueDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDueDateKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtBillNo);
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                List sheetData = new ArrayList();
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook(fis);
                HSSFSheet sheet = workbook.getSheetAt(0);
                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()) {
                    HSSFRow row = (HSSFRow) rows.next();
                    Iterator cells = row.cellIterator();

                    List data = new ArrayList();
                    while (cells.hasNext()) {
                        HSSFCell cell = (HSSFCell) cells.next();
                        data.add(cell.toString().toUpperCase().replaceAll("!", "").trim());
                    }

                    sheetData.add(data);
                }
                dtm.setRowCount(0);
                for (int i = 0; i < sheetData.size(); i++) {
                    List list = (List) sheetData.get(i);
                    String sr_cd = "";
                    if (itemCode.get(list.get(1).toString()) == null) {
                        sr_cd = lb.getRetrofit().create(SupportAPI.class).validateData("SERIESMST", "SR_CD", "SR_NAME", list.get(1).toString()).execute().body().get("data").getAsString();
                        if (sr_cd.equalsIgnoreCase("")) {
                            dtm.setRowCount(0);
                            return;
                        } else {
                            itemCode.put(list.get(1).toString(), sr_cd);
                        }
                    } else {
                        sr_cd = itemCode.get(list.get(1).toString());
                    }
                    Vector row = new Vector();
                    row.add(list.get(0).toString());
                    row.add(list.get(1).toString());
                    row.add(list.get(2).toString());
                    row.add(list.get(3).toString());
                    row.add("1");
                    row.add(list.get(5).toString());
                    row.add("");
                    row.add("0");
                    row.add(list.get(8).toString());
                    row.add(list.get(9).toString());
                    row.add(list.get(10).toString());
                    row.add(list.get(11).toString());
                    row.add("0");
                    row.add("0");
                    row.add(list.get(5).toString());
                    row.add(list.get(15).toString());
                    row.add(sr_cd);
                    dtm.addRow(row);
                }
                setTotal();
            } else {
                System.out.println("File access cancelled by user.");
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnBulkPurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBulkPurchaseActionPerformed
        // TODO add your handling code here:
        BulkPurchase bp = new BulkPurchase(null, true, this, tax_type);
        bp.setLocationRelativeTo(null);
        bp.setVisible(true);
    }//GEN-LAST:event_jbtnBulkPurchaseActionPerformed

    private void jtxtPmtDaysFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPmtDaysFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPmtDaysFocusGained

    private void jtxtPmtDaysFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPmtDaysFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
        setDueDate();
    }//GEN-LAST:event_jtxtPmtDaysFocusLost

    private void jtxtPmtDaysKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPmtDaysKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtDueDate);
    }//GEN-LAST:event_jtxtPmtDaysKeyPressed

    private void jtxtPmtDaysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPmtDaysKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, 2);
    }//GEN-LAST:event_jtxtPmtDaysKeyTyped

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
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JButton jBillDateBtn2;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnBulkPurchase;
    private javax.swing.JButton jbtnOK;
    private javax.swing.JComboBox jcmbPmt;
    private javax.swing.JComboBox jcmbTax;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JLabel jlblAddTax;
    private javax.swing.JLabel jlblAdjst;
    private javax.swing.JLabel jlblBasicAmount;
    private javax.swing.JLabel jlblBillDay;
    private javax.swing.JLabel jlblBillDay1;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblNet;
    private javax.swing.JLabel jlblPmtDays;
    private javax.swing.JLabel jlblTax;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JLabel jlblVday;
    private javax.swing.JTextField jtxtBillDate;
    private javax.swing.JTextField jtxtBillNo;
    private javax.swing.JTextField jtxtDueDate;
    private javax.swing.JTextField jtxtFrieght;
    public javax.swing.JTextField jtxtName;
    private javax.swing.JTextField jtxtPmtDays;
    public javax.swing.JTextField jtxtTinNum;
    public javax.swing.JTextField jtxtVouDate;
    private javax.swing.JTextField jtxtVoucher;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
