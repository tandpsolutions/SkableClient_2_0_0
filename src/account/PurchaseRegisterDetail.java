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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
import net.sf.jasperreports.engine.data.JsonDataSource;
import retrofitAPI.AccountAPI;
import retrofitAPI.SalesAPI;
import retrofitAPI.StartUpAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.RightAlignment;
import transactionController.SelectAccount;
import utility.PrintPanel;

/**
 *
 * @author bhaumik
 */
public class PurchaseRegisterDetail extends javax.swing.JInternalFrame {

    /**
     * Creates new form SalesRegister
     */
    Library lb = Library.getInstance();
    DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    String ac_cd = "";

    public PurchaseRegisterDetail() {
        initComponents();
        registerShortKeys();
        setUpData();
        dtm = (DefaultTableModel) jTable1.getModel();
        lb.setDateChooserPropertyInit(jtxtToDate);
        lb.setDateChooserPropertyInit(jtxtFromDate);
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

    private void setUpData() {
        jComboBox3.removeAllItems();
        jComboBox3.addItem("All");
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox3.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
        jComboBox3.setSelectedItem(SkableHome.selected_branch.getBranch_name());
        if (SkableHome.user_grp_cd.equalsIgnoreCase("1")) {
            jComboBox3.setEnabled(true);
        } else {
            jComboBox3.setEnabled(false);
        }
    }

    private void registerShortKeys() {
        KeyStroke dateKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.ALT_MASK, false);
        Action dateKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtxtFromDate.requestFocusInWindow();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(dateKeyStroke, "Date");
        getRootPane().getActionMap().put("Date", dateKeyAction);

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
            JsonObject call = accountAPI.PurchaseRegisterDetail(jComboBox1.getSelectedIndex(), jComboBox2.getSelectedIndex(), jComboBox3.getSelectedIndex(),
                    lb.ConvertDateFormetForDB(jtxtFromDate.getText()), lb.ConvertDateFormetForDB(jtxtToDate.getText()), ac_cd).execute().body();

            lb.addGlassPane(this);
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    dtm.setRowCount(0);
                    dtm.setRowCount(0);
                    double basic_amt = 0.00, tax_amt = 0.00, add_tax_amt = 0.00, rate = 0.00, disc = 0.00, mrp = 0.00, amt = 0.00, net_amt = 0.00, bajaj = 0.00;
                    double cash = 0.00, bank = 0.00, card = 0.00,nlc=0.00;
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
                            row.add(lb.Convert2DecFmtForRs(rate));
                            row.add(lb.Convert2DecFmtForRs(basic_amt));
                            row.add(lb.Convert2DecFmtForRs(tax_amt));
                            row.add(lb.Convert2DecFmtForRs(add_tax_amt));
                            row.add(lb.Convert2DecFmtForRs(disc));
                            row.add(lb.Convert2DecFmtForRs(mrp));
                            row.add(lb.Convert2DecFmtForRs(nlc));
                            row.add(lb.Convert2DecFmtForRs(amt));
                            row.add(net_amt);
                            row.add("");
                            row.add(cash);
                            row.add(bank);
                            row.add(card);
                            row.add(bajaj);
                            row.add(lb.Convert2DecFmtForRs(net_amt - cash - bank - card - bajaj));
                            row.add("");
                            rate = 0.00;
                            basic_amt = 0.00;
                            tax_amt = 0.00;
                            add_tax_amt = 0.00;
                            disc = 0.00;
                            mrp = 0.00;
                            amt = 0.00;
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
                        row.add(array.get(i).getAsJsonObject().get("tag_no").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("IMEI_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("SERAIL_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("QTY").getAsInt());
                        row.add(array.get(i).getAsJsonObject().get("RATE").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("BASIC_AMT").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("tax_amt").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("add_tax_amt").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("DISC_RATE").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("MRP").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("NLC").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("AMT").getAsDouble());
                        row.add("");
                        row.add(array.get(i).getAsJsonObject().get("tax_name").getAsString());
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name());
                        dtm.addRow(row);
                        rate += array.get(i).getAsJsonObject().get("RATE").getAsDouble();
                        basic_amt += array.get(i).getAsJsonObject().get("BASIC_AMT").getAsDouble();
                        tax_amt += array.get(i).getAsJsonObject().get("tax_amt").getAsDouble();
                        add_tax_amt += array.get(i).getAsJsonObject().get("add_tax_amt").getAsDouble();
                        disc += array.get(i).getAsJsonObject().get("DISC_RATE").getAsDouble();
                        mrp += array.get(i).getAsJsonObject().get("MRP").getAsDouble();
                        nlc += array.get(i).getAsJsonObject().get("NLC").getAsDouble();
                        amt += array.get(i).getAsJsonObject().get("AMT").getAsDouble();
                        net_amt = array.get(i).getAsJsonObject().get("net_amt").getAsDouble();
                        cash = array.get(i).getAsJsonObject().get("CASH_AMT").getAsDouble();
                        bank = array.get(i).getAsJsonObject().get("BANK_AMT").getAsDouble();
                        card = array.get(i).getAsJsonObject().get("CARD_AMT").getAsDouble();
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
                    row.add(lb.Convert2DecFmtForRs(rate));
                    row.add(lb.Convert2DecFmtForRs(basic_amt));
                    row.add(lb.Convert2DecFmtForRs(tax_amt));
                    row.add(lb.Convert2DecFmtForRs(add_tax_amt));
                    row.add(lb.Convert2DecFmtForRs(disc));
                    row.add(lb.Convert2DecFmtForRs(mrp));
                    row.add(lb.Convert2DecFmtForRs(nlc));
                    row.add(lb.Convert2DecFmtForRs(amt));
                    row.add(net_amt);
                    row.add("");
                    row.add(cash);
                    row.add(bank);
                    row.add(card);
                    row.add(bajaj);
                    row.add(lb.Convert2DecFmtForRs(net_amt - cash - bank - card - bajaj));
                    row.add("");
                    rate = 0.00;
                    basic_amt = 0.00;
                    tax_amt = 0.00;
                    add_tax_amt = 0.00;
                    disc = 0.00;
                    mrp = 0.00;
                    amt = 0.00;
                    dtm.addRow(row);

//                    double tot_basic = 0.00, tot_tax = 0.00, tot_cash = 0.00, tot_bank = 0.00, tot_card = 0.00, tot_buyBack = 0.00, tot_deb = 0.00,
//                            tot_ins_amt = 0.00, tot_bajaj_amt = 0.00, tot_net_amt = 0.00;
//                    for (int i = 0; i < jTable1.getRowCount(); i++) {
//                        tot_basic += lb.isNumber2(jTable1.getValueAt(i, 12).toString());
//                        tot_tax += lb.isNumber2(jTable1.getValueAt(i, 13).toString());
//                        tot_net_amt += lb.isNumber2(jTable1.getValueAt(i, 14).toString());
//                        tot_cash += lb.isNumber2(jTable1.getValueAt(i, 16).toString());
//                        tot_card += lb.isNumber2(jTable1.getValueAt(i, 18).toString());
//                        tot_bajaj_amt += lb.isNumber2(jTable1.getValueAt(i, 19).toString());
//                        tot_buyBack += lb.isNumber2(jTable1.getValueAt(i, 20).toString());
//                        tot_ins_amt += lb.isNumber2(jTable1.getValueAt(i, 22).toString());
//                        tot_deb += lb.isNumber2(jTable1.getValueAt(i, 23).toString());
//                    }
//                    row = new Vector();
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add("");
//                    row.add(lb.Convert2DecFmtForRs(tot_basic));
//                    row.add(lb.Convert2DecFmtForRs(tot_tax));
//                    row.add(lb.Convert2DecFmtForRs(tot_net_amt));
//                    row.add("");
//                    row.add(lb.Convert2DecFmtForRs(tot_cash));
//                    row.add(lb.Convert2DecFmtForRs(tot_bank));
//                    row.add(lb.Convert2DecFmtForRs(tot_card));
//                    row.add(lb.Convert2DecFmtForRs(tot_bajaj_amt));
//                    row.add(lb.Convert2DecFmtForRs(tot_buyBack));
//                    row.add("");
//                    row.add(lb.Convert2DecFmtForRs(tot_ins_amt));
//                    row.add(lb.Convert2DecFmtForRs(tot_deb));
//                    dtm.addRow(row);
                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(PurchaseRegisterDetail.this);
                } else {
                    lb.removeGlassPane(PurchaseRegisterDetail.this);
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PurchaseRegisterDetail.class.getName()).log(Level.SEVERE, null, ex);
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
                row.add(jTable1.getValueAt(i, 22).toString());
                row.add(jTable1.getValueAt(i, 23).toString());
                row.add(jTable1.getValueAt(i, 24).toString());
                row.add(jTable1.getValueAt(i, 25).toString());
                row.add(jTable1.getValueAt(i, 26).toString());
                rows.add(row);
            }

            ArrayList header = new ArrayList();
            header.add("Type");
            header.add("Voucehr No");
            header.add("Date");
            header.add("Name");
            header.add("Product Code");
            header.add("Product Name");
            header.add("Tag");
            header.add("IMEI");
            header.add("Serail No");
            header.add("Qty");
            header.add("Rate");
            header.add("Basic Amt");
            header.add("Tax");
            header.add("Add Tax");
            header.add("Disc");
            header.add("MRP");
            header.add("NLC");
            header.add("Amt");
            header.add("Net");
            header.add("Tax Name");
            header.add("Cash");
            header.add("Bank");
            header.add("Card");
            header.add("Bajaj");
            header.add("Debtor");
            header.add("Branch");
            lb.exportToExcel("Purchase Register Detail", header, rows, "Purchase Register Detail");
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

    public void getSalesBillPrint(String ref_no) {
        try {
            SalesAPI salesAPI = lb.getRetrofit().create(SalesAPI.class);
            JsonObject call = salesAPI.GetSalesBillPrint(ref_no).execute().body();
            JsonObject call1 = salesAPI.GetSalesBillTaxPrint(ref_no).execute().body();

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    JsonArray array1 = call1.getAsJsonArray("data");
                    if (array != null) {
                        try {
                            FileWriter file = new FileWriter(System.getProperty("user.dir") + File.separator + "file1.txt");
                            FileWriter file2 = new FileWriter(System.getProperty("user.dir") + File.separator + "file2.txt");
                            file.write(array.toString());
                            file2.write(array1.toString());
                            file.close();
                            file2.close();
                            File jsonFile = new File(System.getProperty("user.dir") + File.separator + "file1.txt");
                            File jsonFile1 = new File(System.getProperty("user.dir") + File.separator + "file2.txt");
                            JsonDataSource dataSource = new JsonDataSource(jsonFile);
                            JsonDataSource dataSource1 = new JsonDataSource(jsonFile1);
                            HashMap params = new HashMap();
                            params.put("dir", System.getProperty("user.dir"));
                            params.put("comp_name", "APPLE N BERRY");
                            params.put("tin_no", (array.get(0).getAsJsonObject().get("COMPANY_TIN").getAsString()));
                            params.put("cst_no", (array.get(0).getAsJsonObject().get("COMPANY_CST").getAsString()));
                            params.put("add1", SkableHome.selected_branch.getAddress1());
                            params.put("add2", SkableHome.selected_branch.getAddress2());
                            params.put("add3", SkableHome.selected_branch.getAddress3());
                            params.put("email", SkableHome.selected_branch.getEmail());
                            params.put("mobile", SkableHome.selected_branch.getPhone());
                            params.put("tax_data", dataSource1);
                            lb.reportGenerator("saleinVoice.jasper", params, dataSource, jPanel1);
                        } catch (Exception ex) {
                        }
                    }
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PrintPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendSelesVoucher() {

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (!jTable1.getValueAt(i, 2).toString().trim().equalsIgnoreCase("")) {
                lb.getSalesBillPrint(jTable1.getValueAt(i, 0).toString());
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jtxtAcName = new javax.swing.JTextField();
        jbtnClose = new javax.swing.JButton();
        jtxtAcAlias = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jBillDateBtn1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jbtnView = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox();
        jtxtFromDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jbtnExcel = new javax.swing.JButton();
        jtxtToDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ref_no", "Type", "Bill No", "Date", "Name", "Code", "Name", "Tag", "IMEI", "Serail No", "Qty", "Rate", "Basic Amt", "Tax", "Add Tax", "Disc", "MRP", "NLC", "Amt", "Net Amt", "Tax Name", "Cash", "Bank", "Card", "Bajaj", "Debtor", "Branch"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(24).setResizable(false);
            jTable1.getColumnModel().getColumn(25).setResizable(false);
            jTable1.getColumnModel().getColumn(26).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jtxtAcName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcNameFocusGained(evt);
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

        jCheckBox1.setText("Name");

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        jLabel4.setText("To Date");

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

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "RD Purchase", "URD Purchase", "All" }));
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
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

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

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

        jLabel3.setText("From Date");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cash", "Credit", "All" }));
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jComboBox3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox3KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnExcel, jbtnView});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnExcel)
                        .addComponent(jbtnClose))
                    .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jtxtAcAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jBillDateBtn1, jComboBox1, jComboBox2, jLabel3, jLabel4, jbtnClose, jbtnExcel, jtxtFromDate, jtxtToDate});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jCheckBox1, jtxtAcAlias, jtxtAcName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                jComboBox1.requestFocusInWindow();
            }

        } catch (Exception ex) {
            //            navLoad.jlblMsg.setText("Enter Correct Date");
            jtxtToDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtToDateFocusLost

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jComboBox1.requestFocusInWindow();
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

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        // TODO add your handling code here:
        jbtnViewActionPerformedRoutine();
        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnViewKeyPressed

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

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jComboBox2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnView.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                lb.openVoucherBook(jTable1.getValueAt(row, 0).toString());
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jComboBox3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnExcel;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtAcAlias;
    private javax.swing.JTextField jtxtAcName;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
