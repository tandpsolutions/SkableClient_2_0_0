/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import masterController.SeriesMasterController;
import model.PurchaseControllerDetailModel;
import model.SeriesHead;
import model.SeriesMaster;
import model.TaxMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.StartUpAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author bhaumik
 */
public class SplitTag extends javax.swing.JDialog {

    Library lb = Library.getInstance();
    private javax.swing.JTextField jtxtTag = null, jtxtIMEI = null, jtxtSerialNo = null, jtxtQty = null, jtxtRate = null, jtxtAmount = null, jtxtBasicAmt = null, jtxtTaxAmt = null, jtxtAddTaxAmt = null;
    private javax.swing.JTextField jtxtDiscPer = null, jtxtMRP = null;
    javax.swing.JTextField jtxtItem = null;
    DefaultTableModel dtm = null;
    private ReportTable viewTable = null;
    private JLabel jlblTotQty;
    private JLabel jlblTotAmt;
    private String sr_cd = "";
    private String item_name = "";
    private String tag = "";
    private boolean flag = false;
    public final ArrayList<PurchaseControllerDetailModel> detail = new ArrayList<PurchaseControllerDetailModel>();
    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form SplitTag
     */
    public SplitTag(java.awt.Frame parent, boolean modal, String tag) {
        super(parent, modal);
        this.tag = tag;
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        addJtextBox();
        addJLabel();

        this.setBounds(0, 0, GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width, this.getHeight());
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
        tableForView();
        addTaxCombo();
        flag = true;
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
                if (lb.isEnter(e)) {
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
                jcmbTaxItemStateChanged(null);
                calculation();
                lb.toDouble(e);
            }
        });

        jtxtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
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
                        jtxtRate.setText(lb.roundOffDoubleValue(basic_amt * tax));

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
                lb.setTable(jTable1, new JComponent[]{null, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, null, null, jcmbTax, jtxtBasicAmt, jtxtTaxAmt, jtxtAddTaxAmt, jtxtDiscPer, jtxtMRP, jtxtAmount, null});
                lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotQty, null, null, null, null, null, null, null, jlblTotAmt, null, null, null});
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

        jtxtMRP.setBounds(0, 0, 20, 20);
        jtxtMRP.setVisible(true);
        jPanel3.add(jtxtMRP);

        jtxtAmount.setBounds(0, 0, 20, 20);
        jtxtAmount.setVisible(true);
        jPanel3.add(jtxtAmount);

        lb.setTable(jTable1, new JComponent[]{null, jtxtItem, jtxtIMEI, jtxtSerialNo, jtxtQty, jtxtRate, null, null, jcmbTax, jtxtBasicAmt, jtxtTaxAmt, jtxtAddTaxAmt, jtxtDiscPer, jtxtMRP, jtxtAmount, null});
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

        lb.setTable(jTable1, new JComponent[]{null, null, null, null, jlblTotQty, null, null, null, null, null, null, null, jlblTotAmt, null, null, null});
    }

    private void setSeriesData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(SplitTag.this);
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
                                row.add(series.get(i).getTAXCD());
                                row.add(series.get(i).getTAXNAME());
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
                    lb.removeGlassPane(SplitTag.this);
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

    public boolean validateRow(String tag) {
        boolean flag = true;

        if (sr_cd.equalsIgnoreCase("")) {
            lb.showMessageDailog("Invalid Product Name");
            jtxtItem.requestFocusInWindow();
            flag = false;
        }

        return flag;
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
        jtxtAmount.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jcmbTax = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jbtnAdd = new javax.swing.JButton();
        jbtnOK = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel3.setPreferredSize(new java.awt.Dimension(1230, 25));

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
                .addComponent(jcmbTax, 0, 226, Short.MAX_VALUE)
                .addGap(514, 514, 514))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jcmbTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag No", "Product Name", "IMEI No", "Serial No", "Qty", "Rate", "ref_no", "IS_del", "Tax", "Basic Amt", "Tax Amt", "Add Tax Amt", "Disc", "MRP", "Amount", "SR_CD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        }

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

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

        jbtnOK.setText("OK");
        jbtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnOKActionPerformed(evt);
            }
        });

        jbtnCancel.setText("Cancel");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtnAdd, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jbtnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnOK)
                    .addComponent(jbtnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            {
                int index = jTable1.getSelectedRow();
                int is_del = Integer.parseInt(jTable1.getValueAt(index, 10).toString());
                int is_main = Integer.parseInt(jTable1.getValueAt(index, 15).toString());
                if (index != -1 && is_del == 0 && is_main == 1) {
                    evt.consume();
                    jtxtTag.setText(jTable1.getValueAt(index, 0).toString());
                    jtxtItem.setText(jTable1.getValueAt(index, 1).toString());
                    jtxtIMEI.setText(jTable1.getValueAt(index, 2).toString());
                    jtxtSerialNo.setText(jTable1.getValueAt(index, 3).toString());
                    jtxtQty.setText(jTable1.getValueAt(index, 4).toString());
                    jtxtRate.setText(jTable1.getValueAt(index, 5).toString());
                    jtxtDiscPer.setText(jTable1.getValueAt(index, 6).toString());
                    jtxtMRP.setText(jTable1.getValueAt(index, 7).toString());
                    jtxtAmount.setText(jTable1.getValueAt(index, 8).toString());
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        int index = jTable1.getSelectedRow();
        int is_del = Integer.parseInt(jTable1.getValueAt(index, 7).toString());

        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (index != -1 && is_del == 0) {
                lb.confirmDialog("Do you want to delete this row?");
                if (lb.type) {
                    dtm.removeRow(index);

                }
            }
        }

        if (evt.getKeyCode() == KeyEvent.VK_D) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                if (index != -1 && is_del == 0) {
                    lb.confirmDialog("Do you want to delete this row?");
                    if (lb.type) {
                        dtm.removeRow(index);
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
                int add_tax_rate_On = (int) lb.isNumber2(tm.getTAXONSALES());
                double taxable = (lb.isNumber2(jtxtRate.getText()) * 100) / (100 + tax_rate + add_tax_rate);
                jtxtBasicAmt.setText(lb.roundOffDoubleValue(taxable));
                jtxtTaxAmt.setText(lb.roundOffDoubleValue((tax_rate * taxable) / 100));
                double tax = lb.isNumber(jtxtTaxAmt);
                if (add_tax_rate_On == 1) {
                    jtxtAddTaxAmt.setText(lb.roundOffDoubleValue((add_tax_rate * taxable) / 100));
                } else {
                    jtxtAddTaxAmt.setText(lb.roundOffDoubleValue((add_tax_rate * tax) / 100));
                }
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
        if (validateRow(tag)) {
            int index = jTable1.getSelectedRow();
            if (index == -1) {
                for (int i = 0; i < (int) lb.isNumber2(jtxtQty.getText()); i++) {
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
                    row.add(lb.isNumber2(jtxtMRP.getText()));
                    row.add(lb.isNumber2(jtxtRate.getText()));
                    row.add(sr_cd);
                    dtm.addRow(row);
                }
            } else {
                if (!jtxtIMEI.getText().equalsIgnoreCase("")) {
                    jTable1.setValueAt(jtxtIMEI.getText(), index, 0);
                } else if (!jtxtSerialNo.getText().equalsIgnoreCase("")) {
                    jTable1.setValueAt(jtxtSerialNo.getText(), index, 0);
                } else {
                    jTable1.setValueAt("", index, 0);
                }
                jTable1.setValueAt(item_name, index, 1);
                jTable1.setValueAt(jtxtIMEI.getText(), index, 2);
                jTable1.setValueAt(jtxtSerialNo.getText(), index, 3);
                jTable1.setValueAt((int) lb.isNumber2(jtxtQty.getText()), index, 4);
                jTable1.setValueAt(lb.isNumber2(jtxtRate.getText()), index, 5);
                jTable1.setValueAt(lb.isNumber2(jtxtBasicAmt.getText()), index, 9);
                jTable1.setValueAt(lb.isNumber2(jtxtTaxAmt.getText()), index, 10);
                jTable1.setValueAt(lb.isNumber2(jtxtAddTaxAmt.getText()), index, 11);
                jTable1.setValueAt(lb.isNumber2(jtxtDiscPer.getText()), index, 12);
                jTable1.setValueAt(lb.isNumber2(jtxtMRP.getText()), index, 13);
                jTable1.setValueAt(lb.isNumber2(jtxtAmount.getText()), index, 14);
                jTable1.setValueAt(sr_cd, index, 15);
                jTable1.clearSelection();
            }
            jtxtIMEI.setText("");
            jtxtSerialNo.setText("");
            lb.confirmDialog("Do you want to add another item?");
            if (lb.type) {
                jtxtItem.requestFocusInWindow();
            } else {
                clear();
                jbtnOK.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jbtnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnOKActionPerformed
        // TODO add your handling code here:
        detail.clear();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            PurchaseControllerDetailModel model = new PurchaseControllerDetailModel();
            model.setTAG_NO(jTable1.getValueAt(i, 0).toString());
            model.setSR_NAME(jTable1.getValueAt(i, 1).toString());
            model.setIMEI_NO(jTable1.getValueAt(i, 2).toString());
            model.setSERAIL_NO(jTable1.getValueAt(i, 3).toString());
            model.setQTY((int) lb.isNumber2(jTable1.getValueAt(i, 4).toString()));
            model.setRATE(lb.isNumber2(jTable1.getValueAt(i, 5).toString()));
            model.setPUR_TAG_NO(jTable1.getValueAt(i, 6).toString());
            model.setTAX_CD(jTable1.getValueAt(i, 8).toString());
            model.setBASIC_AMT(lb.isNumber2(jTable1.getValueAt(i, 9).toString()));
            model.setTAX_AMT(lb.isNumber2(jTable1.getValueAt(i, 10).toString()));
            model.setADD_TAX_AMT(lb.isNumber2(jTable1.getValueAt(i, 11).toString()));
            model.setDISC_PER(lb.isNumber2(jTable1.getValueAt(i, 12).toString()));
            model.setMRP(lb.isNumber2(jTable1.getValueAt(i, 13).toString()));
            model.setAMT(lb.isNumber2(jTable1.getValueAt(i, 14).toString()));
            model.setSR_CD(jTable1.getValueAt(i, 15).toString());
            detail.add(model);
        }
        doClose(RET_OK);
    }//GEN-LAST:event_jbtnOKActionPerformed

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
        // TODO add your handling code here:
        doClose(RET_CANCEL);
    }//GEN-LAST:event_jbtnCancelActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnOK;
    private javax.swing.JComboBox jcmbTax;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
