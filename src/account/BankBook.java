/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.AccountHead;
import retrofitAPI.AccountAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import transactionController.SelectAccount;

/**
 *
 * @author nice
 */
public class BankBook extends javax.swing.JInternalFrame {

    Library lb = Library.getInstance();
    DefaultTableModel dtm = null;
    double opbRs = 0.00;
    String ac_cd = "";
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    /**
     * Creates new form Ledger
     */
    public BankBook() {
        initComponents();
        initOther();
    }

    public BankBook(String ac_cd, String Name) {
        initComponents();
        initOther();
        this.ac_cd = ac_cd;
        jtxtAcAlias.setText(ac_cd);
        jtxtAcName.setText(Name);
        jbtnView.doClick();
    }

    private void initOther() {
        lb.setDateChooserPropertyInit(jtxtToDate);
        lb.setDateChooserPropertyInitStart(jtxtFromDate);
        dtm = (DefaultTableModel) jTable1.getModel();
        registerShortKeys();
        searchOnTextFields();
    }

    private void registerShortKeys() {

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jbtnClose.doClick();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
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

    @Override
    public void dispose() {
        try {
            SkableHome.removeFromScreen(SkableHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
//            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    private void jbtnViewActionPerformedRoutine() {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class
            );

            JsonObject call = accountAPI.BankBook(ac_cd, lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                    lb.ConvertDateFormetForDB(jtxtToDate.getText()), true).execute().body();

            lb.addGlassPane(this);
            double main_dr = 0.00, main_cr = 0.00;

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    dtm.setRowCount(0);
                    double dr = 0.00, cr = 0.00;

                    Vector row = new Vector();
                    row.add("Opening Balance");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    if ((call.getAsJsonObject().get("opb").getAsDouble()) > 0) {
                        row.add(lb.Convert2DecFmtForRs(call.getAsJsonObject().get("opb").getAsDouble()) + " DR.");
                        dr += call.getAsJsonObject().get("opb").getAsDouble();
                    } else {
                        row.add(lb.Convert2DecFmtForRs(Math.abs(call.getAsJsonObject().get("opb").getAsDouble())) + " CR.");
                        cr += Math.abs(call.getAsJsonObject().get("opb").getAsDouble());
                    }
                    row.add("");
                    row.add("");
                    dtm.addRow(row);
                    for (int i = 0; i < array.size(); i++) {
                        row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("DOC_REF_NO").getAsString());
                        row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("DOC_DATE").getAsString()));
                        row.add((array.get(i).getAsJsonObject().get("DOC_CD").getAsString()));
                        row.add((array.get(i).getAsJsonObject().get("CHQ_NO").isJsonNull()) ? "" : array.get(i).getAsJsonObject().get("CHQ_NO").getAsString());
                        if (array.get(i).getAsJsonObject().get("REC_DATE").isJsonNull()) {
                            row.add("");
                        } else {
                            row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("REC_DATE").getAsString()));

                        }
                        if (!array.get(i).getAsJsonObject().get("REC_BANK_NAME").isJsonNull()) {
                            row.add(array.get(i).getAsJsonObject().get("REC_BANK_NAME").getAsString());
                        } else {
                            row.add("");
                        }
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("DR").getAsDouble()));
                        dr += array.get(i).getAsJsonObject().get("DR").getAsDouble();
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("CR").getAsDouble()));
                        cr += array.get(i).getAsJsonObject().get("CR").getAsDouble();
                        if ((dr - cr) > 0) {
                            row.add(lb.Convert2DecFmtForRs(dr - cr) + " DR.");
                        } else {
                            row.add(lb.Convert2DecFmtForRs(Math.abs(dr - cr)) + " CR.");
                        }

                        if (array.get(i).getAsJsonObject().get("OPP_NAME").isJsonNull()) {
                            row.add("");
                        } else {
                            row.add(array.get(i).getAsJsonObject().get("OPP_NAME").getAsString());
                        }
                        row.add(array.get(i).getAsJsonObject().get("PARTICULAR").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("REF_NO").getAsString());
                        dtm.addRow(row);
                    }

                    row = new Vector();
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(dr));
                    row.add(lb.Convert2DecFmtForRs(cr));
                    row.add(lb.Convert2DecFmtForRs(dr - cr));
                    row.add("");
                    row.add("");
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("Closing Balance as per bank");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    if ((dr - cr) > 0) {
                        row.add(lb.Convert2DecFmtForRs(dr - cr) + " DR.");
                    } else {
                        row.add(lb.Convert2DecFmtForRs(Math.abs(dr - cr)) + " CR.");
                    }
                    row.add("");
                    row.add("");
                    dtm.addRow(row);

                    main_dr = dr;
                    main_cr = cr;
                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(BankBook.this);
                } else {
                    lb.removeGlassPane(BankBook.this);
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }

            Vector row = new Vector();
            row.add(" ");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            dtm.addRow(row);

            call = accountAPI.BankBook(ac_cd, lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                    lb.ConvertDateFormetForDB(jtxtToDate.getText()), false).execute().body();

            lb.addGlassPane(this);

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    double dr = 0.00, cr = 0.00;

                    row = null;
                    for (int i = 0; i < array.size(); i++) {
                        row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("DOC_REF_NO").getAsString());
                        row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("DOC_DATE").getAsString()));
                        row.add((array.get(i).getAsJsonObject().get("DOC_CD").getAsString()));
                        row.add(array.get(i).getAsJsonObject().get("CHQ_NO").getAsString());
                        if (array.get(i).getAsJsonObject().get("REC_DATE").isJsonNull()) {
                            row.add("");
                        } else {
                            row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("REC_DATE").getAsString()));

                        }
                        if (!array.get(i).getAsJsonObject().get("REC_BANK_NAME").isJsonNull()) {
                            row.add(array.get(i).getAsJsonObject().get("REC_BANK_NAME").getAsString());
                        } else {
                            row.add("");
                        }
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("DR").getAsDouble()));
                        dr += array.get(i).getAsJsonObject().get("DR").getAsDouble();
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("CR").getAsDouble()));
                        cr += array.get(i).getAsJsonObject().get("CR").getAsDouble();
                        if ((dr - cr) > 0) {
                            row.add(lb.Convert2DecFmtForRs(dr - cr) + " DR.");
                        } else {
                            row.add(lb.Convert2DecFmtForRs(Math.abs(dr - cr)) + " CR.");
                        }

                        if (array.get(i).getAsJsonObject().get("OPP_NAME").isJsonNull()) {
                            row.add("");
                        } else {
                            row.add(array.get(i).getAsJsonObject().get("OPP_NAME").getAsString());
                        }
                        row.add(array.get(i).getAsJsonObject().get("PARTICULAR").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("REF_NO").getAsString());
                        dtm.addRow(row);
                    }

                    row = new Vector();
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(dr));
                    row.add(lb.Convert2DecFmtForRs(cr));
                    row.add(lb.Convert2DecFmtForRs(dr - cr));
                    row.add("");
                    row.add("");
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("Closing Balance as per our book");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    if ((dr - cr) > 0) {
                        row.add(lb.Convert2DecFmtForRs(dr - cr) + " DR.");
                    } else {
                        row.add(lb.Convert2DecFmtForRs(Math.abs(dr - cr)) + " CR.");
                    }
                    row.add("");
                    row.add("");

                    dtm.addRow(row);
                    main_dr += dr;
                    main_cr += cr;

                    row = new Vector();
                    row.add(" ");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("Closing Balance");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    if ((main_dr - main_cr) > 0) {
                        row.add(lb.Convert2DecFmtForRs(main_dr - main_cr) + " DR.");
                    } else {
                        row.add(lb.Convert2DecFmtForRs(Math.abs(main_dr - main_cr)) + " CR.");
                    }
                    row.add("");
                    row.add("");

                    dtm.addRow(row);
                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(BankBook.this);
                } else {
                    lb.removeGlassPane(BankBook.this);
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BankBook.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                            jtxtFromDate.requestFocusInWindow();
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        panel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jtxtToDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        jtxtFromDate = new javax.swing.JTextField();
        jtxtAcAlias = new javax.swing.JTextField();
        jbtnClose = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jBillDateBtn = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jtxtAcName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jPanel1.setLayout(new java.awt.CardLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ref_no", "VDATE", "INFO", "CHQ NO", "Rec Date", "OPP Bank Name", "Debit", "Credi", "Closing", "OPP AC Name", "Remark", "ref"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
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

        jPanel1.add(jScrollPane1, "card2");

        panel.setLayout(new java.awt.BorderLayout());

        jtxtToDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtToDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtToDateFocusLost(evt);
            }
        });
        jtxtToDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtToDateKeyPressed(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        jtxtFromDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusLost(evt);
            }
        });
        jtxtFromDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDateKeyPressed(evt);
            }
        });

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
        });

        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });
        jbtnClose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnCloseKeyPressed(evt);
            }
        });

        jbtnPreview.setText("Preview");
        jbtnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPreviewActionPerformed(evt);
            }
        });
        jbtnPreview.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPreviewKeyPressed(evt);
            }
        });

        jLabel4.setText("To Date");

        jLabel3.setText("From Date");

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jbtnView.setText("View Result");
        jbtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewActionPerformed(evt);
            }
        });
        jbtnView.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnViewKeyPressed(evt);
            }
        });

        jtxtAcName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcNameFocusGained(evt);
            }
        });

        jLabel1.setText("A/C Name");

        jLabel2.setText("Press CTRL+ R for set Reconsilation Date");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAcName, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtAcAlias)
                    .addComponent(jtxtAcName)
                    .addComponent(jbtnView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jBillDateBtn1, jLabel4, jtxtFromDate, jtxtToDate});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        // TODO add your handling code here:
        if (!ac_cd.equalsIgnoreCase("")) {
            jbtnViewActionPerformedRoutine();
            lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
        }
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jtxtAcAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcAliasKeyPressed
        if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtAcAlias)) {
                setAccountDetailMobile("2", jtxtAcAlias.getText());
            }
        }
    }//GEN-LAST:event_jtxtAcAliasKeyPressed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnCloseKeyPressed


    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jtxtToDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusGained
        // TODO add your handling code here:
        jtxtToDate.selectAll();
    }//GEN-LAST:event_jtxtToDateFocusGained

    private void jtxtToDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusLost
        // TODO add your handling code here:
        try {
            if (jtxtToDate.getText().contains("/")) {
                jtxtToDate.setText(jtxtToDate.getText().replace("/", ""));
            }
            if (jtxtToDate.getText().length() == 8) {
                String temp = jtxtToDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtToDate.setText(setDate);
            }
            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtToDate.getText().trim()))) != null) {
                jbtnView.requestFocusInWindow();
            }

        } catch (Exception ex) {
            //            navLoad.jlblMsg.setText("Enter Correct Date");
            jtxtToDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtToDateFocusLost

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnView.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtToDateKeyPressed

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtToDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtToDate.getX() - 50, jtxtToDate.getY() + 125, jtxtToDate.getX() + odc.getWidth(), jtxtToDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    private void jtxtFromDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusGained
        // TODO add your handling code here:
        jtxtFromDate.selectAll();
    }//GEN-LAST:event_jtxtFromDateFocusGained

    private void jtxtFromDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusLost
        // TODO add your handling code here:
        try {
            if (jtxtFromDate.getText().contains("/")) {
                jtxtFromDate.setText(jtxtFromDate.getText().replace("/", ""));
            }
            if (jtxtFromDate.getText().length() == 8) {
                String temp = jtxtFromDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtFromDate.setText(setDate);
            }
//            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtFromDate.getText().trim()))) != null) {
//                jtxtToDate.requestFocusInWindow();
//            }

        } catch (Exception ex) {
            //            navLoad.jlblMsg.setText("Enter Correct Date");
            jtxtFromDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtFromDateFocusLost

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtToDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFromDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFromDate.getX(), jtxtToDate.getY() + 125, jtxtFromDate.getX() + odc.getWidth(), jtxtFromDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jtxtAcAliasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusLost
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAcAliasFocusLost

    private void jtxtAcAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcAliasFocusGained

    private void jtxtAcNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcNameFocusGained

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            evt.consume();
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String ref_no = jTable1.getValueAt(row, 11).toString();
                lb.openVoucherBook(ref_no);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_R) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                int row = jTable1.getSelectedRow();
                if (row != -1) {
                    String date = lb.ConvertDateFormetForDB(JOptionPane.showInputDialog("Enter Date"));
                    if (!date.equalsIgnoreCase("")) {
                        AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);
                        try {
                            JsonObject call = accountAPI.SetReconsilationDate(jTable1.getValueAt(row, 11).toString(), date).execute().body();
                            if (call != null) {
                                JsonObject result = call;
                                if (result.get("result").getAsInt() == 1) {
                                    jbtnView.doClick();
                                } else {
                                    lb.showMessageDailog(call.get("Cause").getAsString());
                                    jbtnView.doClick();
                                }
                            }
                        } catch (IOException ex) {
                        }
                    } else {
                        lb.confirmDialog("Do you want to remove reconsilation date?");
                        if (lb.type) {
                            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);
                            try {
                                JsonObject call = accountAPI.SetReconsilationDate(jTable1.getValueAt(row, 11).toString(), date).execute().body();
                                if (call != null) {
                                    JsonObject result = call;
                                    if (result.get("result").getAsInt() == 1) {
                                        jbtnView.doClick();
                                    } else {
                                        lb.showMessageDailog(call.get("Cause").getAsString());
                                        jbtnView.doClick();
                                    }
                                }
                            } catch (IOException ex) {
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAcName;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtToDate;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
