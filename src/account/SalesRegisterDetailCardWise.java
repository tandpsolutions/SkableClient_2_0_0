/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.AccountHead;
import retrofitAPI.AccountAPI;
import retrofitAPI.SalesAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import support.RightAlignment;
import transactionController.SalesController;
import transactionController.SelectAccount;

/**
 *
 * @author bhaumik
 */
public class SalesRegisterDetailCardWise extends javax.swing.JInternalFrame {

    /**
     * Creates new form SalesRegister
     */
    private Library lb = Library.getInstance();
    private DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    String ac_cd = "";

    public SalesRegisterDetailCardWise() {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        jTable1.getColumnModel().getColumn(9).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(10).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(11).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(12).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(13).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(14).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(15).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(16).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(17).setCellRenderer(new RightAlignment());
        jTable1.getColumnModel().getColumn(18).setCellRenderer(new RightAlignment());
        searchOnTextFields();
    }

    private void searchOnTextFields() {
        this.rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jPanel3.add(new JLabel("Specify a word to match:"),
                BorderLayout.WEST);
        jPanel3.add(jtfFilter, BorderLayout.CENTER);

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

            if (ac_cd == null) {
                ac_cd = "";
            }
            if (!jCheckBox1.isSelected()) {
                ac_cd = "";
            }
            JsonObject call = accountAPI.CardWiseSalesDetailStatement(ac_cd).execute().body();

            lb.addGlassPane(this);

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    dtm.setRowCount(0);
                    dtm.setRowCount(0);
                    double basic_amt = 0.00, tax_amt = 0.00, net_amt = 0.00, buy_back_amt = 0.00, ins_amt = 0.00, bajaj = 0.00;
                    double cash = 0.00, bank = 0.00, card = 0.00;
                    String tax_name = "";
                    String buy_back_model = "";
                    String old_ref_no = "";
                    for (int i = 0; i < array.size(); i++) {
                        if (!old_ref_no.equalsIgnoreCase("") && !old_ref_no.equalsIgnoreCase(array.get(i).getAsJsonObject().get("ref_no").getAsString())) {
                            Vector row = new Vector();
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
                            row.add("");
                            row.add(basic_amt);
                            row.add(lb.Convert2DecFmtForRs(tax_amt));
                            row.add(net_amt);
                            row.add(tax_name);
                            row.add(cash);
                            row.add(bank);
                            row.add(card);
                            row.add(bajaj);
                            row.add(buy_back_amt);
                            row.add(buy_back_model);
                            row.add(ins_amt);
                            row.add(lb.Convert2DecFmtForRs(basic_amt + tax_amt - cash - bank - card - buy_back_amt + ins_amt - bajaj));
                            basic_amt = 0.00;
                            dtm.addRow(row);
                        }
                        Vector row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("ref_no").getAsString());
                        if (basic_amt == 0.00) {
                            row.add(array.get(i).getAsJsonObject().get("v_type").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("inv_no").getAsInt());
                            row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("v_date").getAsString()));
                            row.add((array.get(i).getAsJsonObject().get("fname") == null) ? "" : array.get(i).getAsJsonObject().get("fname").getAsString());
                        } else {
                            row.add("");
                            row.add("");
                            row.add("");
                            row.add("");
                        }
                        row.add(array.get(i).getAsJsonObject().get("SR_ALIAS").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("QTY").getAsInt());
                        row.add(array.get(i).getAsJsonObject().get("RATE").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("AMT").getAsDouble());
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
                        row.add("");
                        dtm.addRow(row);
                        basic_amt = array.get(i).getAsJsonObject().get("det_tot").getAsDouble();
                        tax_amt = array.get(i).getAsJsonObject().get("tax_amt").getAsDouble() + array.get(i).getAsJsonObject().get("add_tax_amt").getAsDouble();
                        net_amt = array.get(i).getAsJsonObject().get("net_amt").getAsDouble();
                        cash = array.get(i).getAsJsonObject().get("CASH_AMT").getAsDouble();
                        bank = array.get(i).getAsJsonObject().get("BANK_AMT").getAsDouble();
                        card = array.get(i).getAsJsonObject().get("CARD_AMT").getAsDouble();
                        tax_name = array.get(i).getAsJsonObject().get("tax_name").getAsString();
                        buy_back_model = array.get(i).getAsJsonObject().get("buy_back_model").getAsString();
                        buy_back_amt = array.get(i).getAsJsonObject().get("buy_back_amt").getAsDouble();
                        ins_amt = array.get(i).getAsJsonObject().get("ins_amt").getAsDouble();
                        bajaj = array.get(i).getAsJsonObject().get("bajaj_amt").getAsDouble();
                        old_ref_no = array.get(i).getAsJsonObject().get("ref_no").getAsString();
                    }
                    Vector row = new Vector();
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
                    row.add("");
                    row.add(basic_amt);
                    row.add(lb.Convert2DecFmtForRs(tax_amt));
                    row.add(net_amt);
                    row.add(tax_name);
                    row.add(cash);
                    row.add(bank);
                    row.add(card);
                    row.add(bajaj);
                    row.add(buy_back_amt);
                    row.add(buy_back_model);
                    row.add(ins_amt);
                    row.add(lb.Convert2DecFmtForRs(basic_amt + tax_amt + ins_amt - cash - bank - card - buy_back_amt - bajaj));
                    basic_amt = 0.00;
                    dtm.addRow(row);

                    double tot_basic = 0.00, tot_tax = 0.00, tot_cash = 0.00, tot_bank = 0.00, tot_card = 0.00, tot_buyBack = 0.00, tot_deb = 0.00,
                            tot_ins_amt = 0.00, tot_bajaj_amt = 0.00, tot_net_amt = 0.00;
                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        tot_basic += lb.isNumber2(jTable1.getValueAt(i, 12).toString());
                        tot_tax += lb.isNumber2(jTable1.getValueAt(i, 13).toString());
                        tot_net_amt += lb.isNumber2(jTable1.getValueAt(i, 14).toString());
                        tot_cash += lb.isNumber2(jTable1.getValueAt(i, 16).toString());
                        tot_card += lb.isNumber2(jTable1.getValueAt(i, 18).toString());
                        tot_bajaj_amt += lb.isNumber2(jTable1.getValueAt(i, 19).toString());
                        tot_buyBack += lb.isNumber2(jTable1.getValueAt(i, 20).toString());
                        tot_ins_amt += lb.isNumber2(jTable1.getValueAt(i, 22).toString());
                        tot_deb += lb.isNumber2(jTable1.getValueAt(i, 23).toString());
                    }
                    row = new Vector();
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
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(tot_basic));
                    row.add(lb.Convert2DecFmtForRs(tot_tax));
                    row.add(lb.Convert2DecFmtForRs(tot_net_amt));
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(tot_cash));
                    row.add(lb.Convert2DecFmtForRs(tot_bank));
                    row.add(lb.Convert2DecFmtForRs(tot_card));
                    row.add(lb.Convert2DecFmtForRs(tot_bajaj_amt));
                    row.add(lb.Convert2DecFmtForRs(tot_buyBack));
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(tot_ins_amt));
                    row.add(lb.Convert2DecFmtForRs(tot_deb));
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(SalesRegisterDetailCardWise.this);
                } else {
                    lb.removeGlassPane(SalesRegisterDetailCardWise.this);
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SalesRegisterDetailCardWise.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void callExcel() {

        try {
            ArrayList rows = new ArrayList();
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                ArrayList row = new ArrayList();
                row.add(jTable1.getValueAt(i, 1).toString());
                row.add(jTable1.getValueAt(i, 2).toString());
                row.add(jTable1.getValueAt(i, 3).toString());
                row.add(jTable1.getValueAt(i, 4).toString());
                row.add(jTable1.getValueAt(i, 5).toString());
                row.add(jTable1.getValueAt(i, 6).toString());
                row.add(jTable1.getValueAt(i, 7).toString());
                row.add(jTable1.getValueAt(i, 8).toString());
                row.add(jTable1.getValueAt(i, 9).toString());
                row.add(jTable1.getValueAt(i, 10).toString());
                row.add(jTable1.getValueAt(i, 11).toString());
                row.add(jTable1.getValueAt(i, 12).toString());
                row.add(jTable1.getValueAt(i, 13).toString());
                row.add(jTable1.getValueAt(i, 14).toString());
                row.add(jTable1.getValueAt(i, 15).toString());
                row.add(jTable1.getValueAt(i, 16).toString());
                row.add(jTable1.getValueAt(i, 17).toString());
                row.add(jTable1.getValueAt(i, 18).toString());
                row.add(jTable1.getValueAt(i, 19).toString());
                row.add(jTable1.getValueAt(i, 20).toString());
                row.add(jTable1.getValueAt(i, 21).toString());
                rows.add(row);
            }

            ArrayList header = new ArrayList();
            header.add("Type");
            header.add("Voucehr No");
            header.add("Date");
            header.add("Name");
            header.add("Product Code");
            header.add("Product Name");
            header.add("IMEI");
            header.add("Serail No");
            header.add("Qty");
            header.add("Rate");
            header.add("Amt");
            header.add("Basic");
            header.add("Tax");
            header.add("Net");
            header.add("Tax Name");
            header.add("Cash");
            header.add("Bank");
            header.add("Card");
            header.add("Buy Back Amt");
            header.add("Buy Back Model");
            header.add("Debtor");
            lb.exportToExcel("Sales Register", header, rows, "DCR");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
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
                            jbtnView.requestFocusInWindow();
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
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jbtnExcel = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jtxtAcName = new javax.swing.JTextField();
        jbtnPreview = new javax.swing.JButton();
        jtxtAcAlias = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jbtnClose = new javax.swing.JButton();
        jtxtCardNo = new javax.swing.JTextField();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ref_no", "Type", "Bill No", "Date", "Name", "Code", "Name", "IMEI", "Serail No", "Qty", "Rate", "Amt", "Basic Amt", "Tax", "Net Amt", "Tax Name", "Cash", "Bank", "Card", "Bajaj", "Buy Back Amt", "Buy Back Model", "INS", "Debtor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setResizable(false);
            jTable1.getColumnModel().getColumn(10).setResizable(false);
            jTable1.getColumnModel().getColumn(11).setResizable(false);
            jTable1.getColumnModel().getColumn(12).setResizable(false);
            jTable1.getColumnModel().getColumn(13).setResizable(false);
            jTable1.getColumnModel().getColumn(14).setResizable(false);
            jTable1.getColumnModel().getColumn(15).setResizable(false);
            jTable1.getColumnModel().getColumn(16).setResizable(false);
            jTable1.getColumnModel().getColumn(17).setResizable(false);
            jTable1.getColumnModel().getColumn(18).setResizable(false);
            jTable1.getColumnModel().getColumn(19).setResizable(false);
            jTable1.getColumnModel().getColumn(20).setResizable(false);
            jTable1.getColumnModel().getColumn(21).setResizable(false);
            jTable1.getColumnModel().getColumn(22).setResizable(false);
            jTable1.getColumnModel().getColumn(23).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jbtnExcel.setText("Excel");
        jbtnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExcelActionPerformed(evt);
            }
        });
        jbtnExcel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnExcelKeyPressed(evt);
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

        jCheckBox1.setText("Card No");

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

        jtxtCardNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardNoKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtCardNo, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                        .addGap(315, 315, 315)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAcName)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jCheckBox1)
                        .addComponent(jtxtCardNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtnPreview)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnExcel)
                        .addComponent(jbtnClose)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClose, jbtnExcel, jbtnPreview, jbtnView});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jCheckBox1, jtxtAcAlias, jtxtAcName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1041, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        // TODO add your handling code here:
        jbtnViewActionPerformedRoutine();
        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        // TODO add your handling code here:
//            generalLedgerPreview();
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jbtnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExcelActionPerformed
        // TODO add your handling code here:
        callExcel();
    }//GEN-LAST:event_jbtnExcelActionPerformed

    private void jbtnExcelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnExcelKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnExcelKeyPressed

    private void jtxtAcAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcAliasFocusGained

    private void jtxtAcAliasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcAliasFocusLost
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtAcAliasFocusLost

    private void jtxtAcAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcAliasKeyPressed
        if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtAcAlias)) {
                setAccountDetailMobile("2", jtxtAcAlias.getText());
            }
        }
    }//GEN-LAST:event_jtxtAcAliasKeyPressed

    private void jtxtAcAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcAliasKeyReleased

    }//GEN-LAST:event_jtxtAcAliasKeyReleased

    private void jtxtAcNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcNameFocusGained

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                SalesController sd = new SalesController(null, true);
                sd.setLocationRelativeTo(null);
                sd.setData(jTable1.getValueAt(row, 0).toString());
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtCardNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardNoKeyPressed
        // TODO add your handling code here:

        if (lb.isEnter(evt) && !lb.isBlank(jtxtCardNo)) {
            try {
                lb.addGlassPane(this);
                JsonObject call = lb.getRetrofit().create(SalesAPI.class).GetDataFromServer(jtxtCardNo.getText(), "22").execute().body();
                lb.removeGlassPane(SalesRegisterDetailCardWise.this);
                if (call != null) {
                    JsonArray array = call.getAsJsonArray("data");
                    if (array.size() > 0) {
                        ac_cd = (array.get(0).getAsJsonObject().get("AC_CD").getAsString());
                        jtxtAcAlias.setText(ac_cd);
                        jtxtAcName.setText(array.get(0).getAsJsonObject().get("FNAME").getAsString());
                        jtxtCardNo.setText(array.get(0).getAsJsonObject().get("CARD_NO").getAsString());
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(SalesRegisterDetailCardWise.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jtxtCardNoKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnExcel;
    private javax.swing.JButton jbtnPreview;
    javax.swing.JButton jbtnView;
    javax.swing.JTextField jtxtAcAlias;
    javax.swing.JTextField jtxtAcName;
    javax.swing.JTextField jtxtCardNo;
    // End of variables declaration//GEN-END:variables
}
