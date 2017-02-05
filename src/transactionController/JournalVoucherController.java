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
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import model.AccountHead;
import model.JournalVoucherModel;
import retrofitAPI.JournalAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;

/**
 *
 * @author bhaumik
 */
public class JournalVoucherController extends javax.swing.JDialog {

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
    JLabel jlblTotAmt;
    DefaultTableModel dtm = null;
    HashMap itemCode = new HashMap();
    boolean flag = false;
    JournalAPI cashPRAPI = null;

    /**
     * Creates new form PurchaseController
     */
    public JournalVoucherController(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();

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
        addJtextBox();
        addJLabel();
        flag = true;
        setTitle("JV");
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

    private void addJLabel() {
        jlblTotAmt = new javax.swing.JLabel("0.00");
        jlblTotAmt.setHorizontalAlignment(SwingConstants.RIGHT);

        jlblTotAmt.setBounds(0, 0, 20, 20);
        jlblTotAmt.setVisible(true);
        jPanel4.add(jlblTotAmt);

        lb.setTable(jTable1, new JComponent[]{null, null, jlblTotAmt, null, null});
    }

    private void addJtextBox() {

        jTextField1.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                lb.setTable(jTable1, new JComponent[]{jtxtAcAlias, jtxtAcName, jTextField1, jcmbType, jtxtRemark});
                lb.setTable(jTable1, new JComponent[]{null, null, jlblTotAmt, null, null});
            }
        });

        lb.setTable(jTable1, new JComponent[]{jtxtAcAlias, jtxtAcName, jTextField1, jcmbType, jtxtRemark});
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    public void setData(JournalAPI journalAPI, String ref_no) {
        this.ref_no = ref_no;
        if (journalAPI == null) {
            journalAPI = lb.getRetrofit().create(JournalAPI.class);
        }
        this.cashPRAPI = journalAPI;

        if (!ref_no.equalsIgnoreCase("")) {
            try {
                JsonObject call = journalAPI.getJournalVoucher(ref_no, "17").execute().body();

                if (call != null) {
                    System.out.println(call.toString());
                    JsonObject object = call;

                    JsonArray array = object.get("data").getAsJsonArray();
                    try {
                        for (int i = 0; i < array.size(); i++) {
                            jtxtVoucher.setText(array.get(i).getAsJsonObject().get("REF_NO").getAsString() + "");
                            jtxtVouDate.setText(lb.ConvertDateFormetForDBForConcurrency(array.get(i).getAsJsonObject().get("VDATE").getAsString()));
                            JournalVoucherController.this.ref_no = array.get(i).getAsJsonObject().get("REF_NO").getAsString();
                            jlblUser.setText(array.get(i).getAsJsonObject().get("USER_ID").getAsString()+ "");
                            jlblEditNo.setText(array.get(i).getAsJsonObject().get("EDIT_NO").getAsDouble() + "");
                            jlblTimeStamp.setText(array.get(i).getAsJsonObject().get("TIME_STAMP").getAsString());
                            jlblVday.setText(lb.setDay(jtxtVouDate));

                            Vector row = new Vector();
                            row.add(array.get(i).getAsJsonObject().get("AC_CD").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("FNAME").getAsString());
                            row.add(lb.isNumber(array.get(i).getAsJsonObject().get("AMT").getAsString()));
                            jcmbType.setSelectedIndex((int) lb.isNumber(array.get(i).getAsJsonObject().get("DRCR").getAsString()));
                            row.add(jcmbType.getSelectedItem().toString());
                            row.add(array.get(i).getAsJsonObject().get("PART").getAsString());
                            dtm.addRow(row);

                        }
                        setTotal();
                    } catch (Exception ex) {
                        lb.printToLogFile("Exception", ex);
                    }

                }
            } catch (Exception ex) {
                lb.printToLogFile("Exception at getDataFor PurchaseBill", ex);
            }
        }
        JournalVoucherController.this.setVisible(true);
    }

    private void setAccountDetailMobile(String param_cd, String value) {
        try {
            JsonObject call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase()).execute().body();

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
                            jtxtAcAlias.setText(ac_cd);
                            jtxtAcName.setText(header.getAccountHeader().get(row).getFNAME());
                            jTextField1.requestFocusInWindow();
                        }
                    }
                } else {
                    lb.showMessageDailog(header.getCause().toString());
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void setTotal() {
        updateLabel();
    }

    private void updateLabel() {
        double dr = 0.00, cr = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            jcmbType.setSelectedItem(jTable1.getValueAt(i, 3).toString());
            if (jcmbType.getSelectedIndex() == 1) {
                dr += lb.isNumber2(jTable1.getValueAt(i, 2).toString());
            } else {
                cr += lb.isNumber2(jTable1.getValueAt(i, 2).toString());
            }
        }
        jlblTDR.setText(lb.Convert2DecFmt(dr));
        jlblTCR.setText(lb.Convert2DecFmt(cr));
        jlblDiff.setText(lb.Convert2DecFmt(Math.abs(cr - dr)));
    }

    private void clear() {
        jtxtAcAlias.setText("");
        jtxtAcName.setText("");
        jTextField1.setText("");
    }

    private void saveVoucher() {
        if (jTable1.getRowCount() > 0) {
            try {
                ArrayList<JournalVoucherModel> detail = new ArrayList<JournalVoucherModel>();
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    JournalVoucherModel model = new JournalVoucherModel();
                    model.setRef_no(ref_no);
                    model.setVdate(lb.ConvertDateFormetForDB(jtxtVouDate.getText()));
                    model.setAc_cd(jTable1.getValueAt(i, 0).toString());
                    model.setAmt(lb.isNumber2(jTable1.getValueAt(i, 2).toString()));
                    model.setUser_id(SkableHome.user_id);
                    jcmbType.setSelectedItem(jTable1.getValueAt(i, 3).toString());
                    model.setType(jcmbType.getSelectedIndex());
                    model.setPart(jTable1.getValueAt(i, 4).toString());
                    model.setTot_dr(lb.isNumber(jlblTDR));
                    model.setTot_cr(lb.isNumber(jlblTCR));
                    model.setBranch_cd(SkableHome.selected_branch.getBranch_cd());
                    detail.add(model);
                }
                String detailJson = new Gson().toJson(detail);
                JsonObject addUpdaCall = cashPRAPI.addUpdateCashVoucher(detailJson).execute().body();
                lb.addGlassPane(this);

                lb.removeGlassPane(JournalVoucherController.this);
                if (addUpdaCall != null) {
                    System.out.println(addUpdaCall.toString());
                    JsonObject object = addUpdaCall;
                    if (object.get("result").getAsInt() == 1) {
                        lb.showMessageDailog("Voucher saved successfully");
                        JournalVoucherController.this.dispose();
                    } else {
                        lb.showMessageDailog(object.get("Cause").getAsString());
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(JournalVoucherController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deleteItem() {
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            if (JOptionPane.showConfirmDialog(this, "Do you want to Delete " + jTable1.getValueAt(row, 1).toString() + " A/c Entry?", "Cash Entry", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dtm.removeRow(row);
                setTotal();
                jTable1.clearSelection();
            }
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
        jlblVday = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jtxtAcAlias = new javax.swing.JTextField();
        jtxtAcName = new javax.swing.JTextField();
        jtxtRemark = new javax.swing.JTextField();
        jcmbType = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jbtnOK = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jlblTDR = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlblTCR = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlblDiff = new javax.swing.JLabel();

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.Alignment.TRAILING)
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
                        .addGap(0, 342, Short.MAX_VALUE)))
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
                    .addComponent(jbtnAdd))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel1, jLabel24, jlblVday, jtxtVouDate, jtxtVoucher});

        jPanel2.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ac Alias", "Account Name", "Amount", "DR/CR", "Particular"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jtxtAcName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAcNameKeyPressed(evt);
            }
        });

        jtxtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRemarkFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRemarkFocusLost(evt);
            }
        });
        jtxtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyTyped(evt);
            }
        });

        jcmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CR", "DR" }));
        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtRemark)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
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

        jLabel5.setText("Total DR");

        jlblTDR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTDR.setText("0.00");
        jlblTDR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Total CR");

        jlblTCR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTCR.setText("0.00");
        jlblTCR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setText("Difference");

        jlblDiff.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblDiff.setText("0.00");
        jlblDiff.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(96, 96, 96))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jbtnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlblTCR, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlblTDR, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblDiff, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel4, jLabel5, jLabel6, jlblDiff, jlblTCR, jlblTDR});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jlblTDR))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jlblTCR))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblDiff)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnOK))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel13, jLabel15, jlblEditNo, jlblTimeStamp, jlblUser});

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
        lb.enterFocus(evt, jtxtAcName);
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
        if (validateData()) {
            saveVoucher();
        }
    }//GEN-LAST:event_jbtnOKActionPerformed

    private void jtxtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarkFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRemarkFocusGained

    private void jtxtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyPressed
        lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtRemarkKeyPressed

    private void jtxtRemarkKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtRemarkKeyTyped

    private void jtxtAcNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtAcName)) {
                setAccountDetailMobile("2", jtxtAcName.getText());
            }
        }
    }//GEN-LAST:event_jtxtAcNameKeyPressed

    private void jtxtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarkFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtRemarkFocusLost

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            evt.consume();
            int rowSel = jTable1.getSelectedRow();
            if (rowSel != -1) {
                jtxtAcAlias.setText(jTable1.getValueAt(rowSel, 0).toString());
                jtxtAcName.setText(jTable1.getValueAt(rowSel, 1).toString());
                jTextField1.setText(jTable1.getValueAt(rowSel, 2).toString());
                jcmbType.setSelectedItem(jTable1.getValueAt(rowSel, 3).toString());
                jtxtRemark.setText(jTable1.getValueAt(rowSel, 4).toString());
                jtxtAcName.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            evt.consume();
            deleteItem();
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jcmbTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTypeKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtRemark);
    }//GEN-LAST:event_jcmbTypeKeyPressed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcmbType);
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jTextField1KeyTyped

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        // TODO add your handling code here:
        int rowSel = jTable1.getSelectedRow();
        if (rowSel == -1) {
            Vector row = new Vector();
            row.add(jtxtAcAlias.getText());
            row.add(jtxtAcName.getText());
            row.add(jTextField1.getText());
            row.add(jcmbType.getSelectedItem().toString());
            row.add(jtxtRemark.getText());
            dtm.addRow(row);
        } else {
            jTable1.setValueAt(jtxtAcAlias.getText(), rowSel, 0);
            jTable1.setValueAt(jtxtAcName.getText(), rowSel, 1);
            jTable1.setValueAt(jTextField1.getText(), rowSel, 2);
            jTable1.setValueAt(jcmbType.getSelectedItem().toString(), rowSel, 3);
            jTable1.setValueAt(jtxtRemark.getText(), rowSel, 4);
        }
        clear();
        if (lb.isNumber2(jlblDiff.getText()) != 0.00) {
            jTextField1.setText(jlblDiff.getText());
        }
        jTable1.clearSelection();
        if (JOptionPane.showConfirmDialog(this, "Do you want to Add more Entry?", "Journal Entry", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            jtxtAcName.requestFocusInWindow();
        } else {
            jbtnOK.requestFocusInWindow();
        }
        setTotal();
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    private boolean validateData() {
        boolean flag = true;
        updateLabel();
        if (lb.isNumber2(jlblDiff.getText()) != 0.00) {
            lb.showMessageDailog("DR/CR not matched");
            flag = false;
        }
        if (!lb.checkDate2(jtxtVouDate)) {
            lb.showMessageDailog("Invalid Voucher Date");
            jtxtVouDate.requestFocusInWindow();
            flag = false;
        }
        if (jTable1.getRowCount() > 0) {
            flag = flag && true;
        } else {
            lb.showMessageDailog("Vouher can not be left blank");
            flag = flag && false;
        }
        return flag;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnOK;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JLabel jlblDiff;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblTCR;
    private javax.swing.JLabel jlblTDR;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JLabel jlblVday;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAcName;
    private javax.swing.JTextField jtxtRemark;
    private javax.swing.JTextField jtxtVouDate;
    private javax.swing.JTextField jtxtVoucher;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
