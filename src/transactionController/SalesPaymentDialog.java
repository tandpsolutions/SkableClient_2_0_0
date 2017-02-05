/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import masterController.TidMasterController;
import model.AccountHead;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.StartUpAPI;
import support.Library;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author nice
 */
public class SalesPaymentDialog extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    private Library lb = Library.getInstance();
    private JComponent focusComp = null, returnComp = null;
    private int type = -1;
    public String bank_cd = "", card_cd = "", bajaj_cd = "";
    private ReportTable viewTable = null;

    /**
     * Creates new form PaymentDialog
     */
    public SalesPaymentDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("Payment Information");
        initComponents();

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
//        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
//        ActionMap actionMap = getRootPane().getActionMap();
//        actionMap.put(cancelName, new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                doClose(RET_CANCEL);
//            }
//        });
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(screenSize.width / 2 - this.getWidth() / 2, screenSize.height / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
        setCompEnable();
        tableForView();
    }

    
    
    private void tableForView() {
        viewTable = new ReportTable();
        viewTable.AddColumn(0, "TID Code", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
    }
    
    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    public void setFocusComp(JComponent comp) {
        focusComp = comp;
    }

    public void setReturnComp(JComponent comp) {
        returnComp = comp;
    }

    public void setInitialAmt(int type) {
        this.type = type;
        if (type == 0 || type == 2) {
//            jtxtCashAmt.setText(jlblSale.getText());
            jcbCash.setSelected(true);
        } else if (type == 2) {
//            jtxtChequeAmt.setText(jlblSale.getText());
            jcbBank.setSelected(true);
        } else {
//            jtxtCashAmt.setText(jlblSale.getText());
            jcbCash.setSelected(true);
        }
    }

    public void setInitialAmtForCredit() {

        jtxtCashAmt.setText(jlblSale.getText());
        jcbCash.setSelected(true);
    }

    public void setTotal() {

        double total = 0;
        if (jcbCash.isSelected()) {
            total += lb.isNumber(jtxtCashAmt);
        }
        if (jcbBank.isSelected()) {
            total += lb.isNumber(jtxtChequeAmt);
        }
        if (jcbCard.isSelected()) {
            total += lb.isNumber(jtxtCardAmt);
        }
        if (jcbBajaj.isSelected()) {
            total += lb.isNumber(jtxtBajajAmt);
        }
        if (jcbDisc.isSelected()) {
            total += lb.isNumber(jtxtDiscount);
        }
        jlblSalePay.setText(lb.Convert2DecFmt(total));
        jlblRemainPay.setText(lb.Convert2DecFmt(lb.isNumber(jlblSale) - total));
        jlblCardChanges.setText(lb.Convert2DecFmtForRs(lb.isNumber(jtxtCardAmt) * lb.isNumber(jtxtCardPer) / 100));
        jlblBajajCharges.setText(lb.Convert2DecFmtForRs(lb.isNumber(jtxtBajajAmt) * lb.isNumber(jtxtBajajPer) / 100));
    }

    private void setCompEnable() {
        jtxtCashAmt.setEnabled(jcbCash.isSelected());
        jtxtBankAc.setEnabled(jcbBank.isSelected());
        jtxtBankName.setEnabled(jcbBank.isSelected());
        jtxtBranchName.setEnabled(jcbBank.isSelected());
        jtxtChequeNo.setEnabled(jcbBank.isSelected());
        jtxtChequeDate.setEnabled(jcbBank.isSelected());
        jtxtChequeAmt.setEnabled(jcbBank.isSelected());
        jtxtCardBank.setEnabled(jcbCard.isSelected());
        jtxtCardAmt.setEnabled(jcbCard.isSelected());
        jtxtCardNo.setEnabled(jcbCard.isSelected());
        jtxtTIDNo.setEnabled(jcbCard.isSelected());
        jtxtBajajCapital.setEnabled(jcbBajaj.isSelected());
        jtxtBajajAmt.setEnabled(jcbBajaj.isSelected());
        jtxtDiscount.setEnabled(jcbDisc.isSelected());
        setTotal();
    }

    public void reset() {
        jcbCash.setSelected(false);
        jcbBank.setSelected(false);
        jcbCard.setSelected(false);
        jcbDisc.setSelected(false);

        jtxtCashAmt.setText("");
        jtxtDiscount.setText("");
        jtxtBankAc.setText("");
        jtxtBankName.setText("");
        jtxtBranchName.setText("");
        jtxtChequeNo.setText("");
        jtxtChequeDate.setText("");
        jtxtChequeAmt.setText("");
        jtxtCardBank.setText("");
        jtxtCardAmt.setText("");
        jtxtCardNo.setText("");
        jtxtTIDNo.setText("");
        setCompEnable();
    }

    private boolean fieldValidate(JComponent comp) {
        boolean flag = true;
        if (comp == jtxtBankAc) {
            if (bank_cd.equalsIgnoreCase("")) {
                flag = false;
                JOptionPane.showMessageDialog(this, "Bank Name not exist", "", JOptionPane.WARNING_MESSAGE);
                comp.requestFocusInWindow();
            }
        }
        if (comp == jtxtCardBank) {
            if (card_cd.equalsIgnoreCase("")) {
                flag = false;
                JOptionPane.showMessageDialog(this, "Card Bank Name not exist", "", JOptionPane.WARNING_MESSAGE);
                comp.requestFocusInWindow();
            }

            if (jtxtCardNo.getText().length() < 4) {
                flag = false;
                JOptionPane.showMessageDialog(this, "Enter valid card number", "", JOptionPane.WARNING_MESSAGE);
                jtxtCardNo.requestFocusInWindow();
            }

            if (jtxtTIDNo.getText().length() != 8) {
                flag = false;
                JOptionPane.showMessageDialog(this, "Enter valid TID number", "", JOptionPane.WARNING_MESSAGE);
                jtxtTIDNo.requestFocusInWindow();
            }
        }

        if (comp == jtxtSFID) {
            if (lb.isBlank(jtxtSFID)) {
                flag = false;
                JOptionPane.showMessageDialog(this, "SFID Can not be left blank", "", JOptionPane.WARNING_MESSAGE);
                comp.requestFocusInWindow();
            }
        }

        if (comp == jtxtBajajCapital) {
            if (bajaj_cd.equalsIgnoreCase("")) {
                flag = false;
                JOptionPane.showMessageDialog(this, "Card Bank Name not exist", "", JOptionPane.WARNING_MESSAGE);
                comp.requestFocusInWindow();
            }
        }
        if (comp == jlblRemainPay) {
            if (lb.isNumber(jlblRemainPay) != 0) {
                return false;
            }
        }
        return flag;
    }

    private boolean validateData() {
        boolean flag = true;
        if (jcbBank.isSelected()) {
            flag = flag && fieldValidate(jtxtBankAc);
        }
        if (jcbCard.isSelected()) {
            flag = flag && fieldValidate(jtxtCardBank);
        }
        if (jcbBajaj.isSelected()) {
            flag = flag && fieldValidate(jtxtBajajCapital);
            flag = flag && fieldValidate(jtxtSFID);
        }
        flag = flag && fieldValidate(jlblRemainPay);
        return flag;
    }

    private void setAccountDetailMobile(String param_cd, String value, final String mode) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value);
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(SalesPaymentDialog.this);
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
                                    if (mode.equalsIgnoreCase("1")) {
                                        bank_cd = header.getAccountHeader().get(row).getACCD();
                                        jtxtBankAc.setText(header.getAccountHeader().get(row).getFNAME());
                                        jtxtBankName.requestFocusInWindow();
                                    } else if (mode.equalsIgnoreCase("2")) {
                                        card_cd = header.getAccountHeader().get(row).getACCD();
                                        jtxtCardBank.setText(header.getAccountHeader().get(row).getFNAME());
                                        jtxtCardAmt.requestFocusInWindow();
                                    } else if (mode.equalsIgnoreCase("3")) {
                                        bajaj_cd = header.getAccountHeader().get(row).getACCD();
                                        jtxtBajajCapital.setText(header.getAccountHeader().get(row).getFNAME());
                                        jtxtBajajAmt.requestFocusInWindow();
                                    }
                                }
                            } else {
                                if (mode.equalsIgnoreCase("1")) {
                                    bank_cd = "";
                                    jtxtBankAc.setText("");
                                } else if (mode.equalsIgnoreCase("2")) {
                                    card_cd = "";
                                    jtxtCardBank.setText("");
                                } else if (mode.equalsIgnoreCase("3")) {
                                    bajaj_cd = "";
                                    jtxtBajajCapital.setText("");
                                }
                            }
                        } else {
                            lb.showMessageDailog(header.getCause().toString());
                        }
                    } else {
                        lb.showMessageDailog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(SalesPaymentDialog.this);
                }
            });
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    
     private void setTIDData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(SalesPaymentDialog.this);
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
                                row.add(array.get(i).getAsJsonObject().get("TID_NAME").getAsString());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    jtxtTIDNo.setText(viewTable.getValueAt(row, 0).toString());
                                    jtxtCardPer.requestFocusInWindow();
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
                    lb.removeGlassPane(SalesPaymentDialog.this);
                }
            }
            );
        } catch (Exception ex) {
            lb.removeGlassPane(SalesPaymentDialog.this);
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

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jcbCash = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jtxtCashAmt = new javax.swing.JTextField();
        jcbBank = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jtxtBankName = new javax.swing.JTextField();
        jtxtBranchName = new javax.swing.JTextField();
        jtxtChequeNo = new javax.swing.JTextField();
        jtxtChequeDate = new javax.swing.JTextField();
        jtxtChequeAmt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jcbCard = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jtxtCardBank = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jtxtCardAmt = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jtxtBankAc = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jlblSale = new javax.swing.JLabel();
        jbtnReset = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jlblSalePay = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlblRemainPay = new javax.swing.JLabel();
        jcbBajaj = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        jtxtBajajCapital = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jtxtBajajAmt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jtxtSFID = new javax.swing.JTextField();
        jcbDisc = new javax.swing.JCheckBox();
        jtxtDiscount = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jtxtTIDNo = new javax.swing.JTextField();
        jtxtCardNo = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jtxtCardPer = new javax.swing.JTextField();
        jlblCardChanges = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jtxtBajajPer = new javax.swing.JTextField();
        jlblBajajCharges = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setMnemonic('O');
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                okButtonKeyPressed(evt);
            }
        });

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jcbCash.setText("Cash Information");
        jcbCash.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbCashItemStateChanged(evt);
            }
        });
        jcbCash.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcbCashKeyPressed(evt);
            }
        });

        jLabel1.setText("Cash Amount");

        jtxtCashAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCashAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCashAmtFocusLost(evt);
            }
        });
        jtxtCashAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCashAmtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCashAmtKeyTyped(evt);
            }
        });

        jcbBank.setText("Bank Information");
        jcbBank.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbBankItemStateChanged(evt);
            }
        });
        jcbBank.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcbBankKeyPressed(evt);
            }
        });

        jLabel2.setText("Bank Name");

        jtxtBankName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBankNameFocusGained(evt);
            }
        });
        jtxtBankName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBankNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtBankNameKeyTyped(evt);
            }
        });

        jtxtBranchName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBranchNameFocusGained(evt);
            }
        });
        jtxtBranchName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBranchNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtBranchNameKeyTyped(evt);
            }
        });

        jtxtChequeNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtChequeNoFocusGained(evt);
            }
        });
        jtxtChequeNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtChequeNoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtChequeNoKeyTyped(evt);
            }
        });

        jtxtChequeDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtChequeDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtChequeDateFocusLost(evt);
            }
        });
        jtxtChequeDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtChequeDateKeyPressed(evt);
            }
        });

        jtxtChequeAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtChequeAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtChequeAmtFocusLost(evt);
            }
        });
        jtxtChequeAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtChequeAmtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtChequeAmtKeyTyped(evt);
            }
        });

        jLabel6.setText("Cheque Amount");

        jLabel5.setText("Cheque Date.");

        jLabel4.setText("Cheque No.");

        jLabel3.setText("Bank Branch");

        jcbCard.setText("Credit Card Information");
        jcbCard.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbCardItemStateChanged(evt);
            }
        });
        jcbCard.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcbCardKeyPressed(evt);
            }
        });

        jLabel8.setText("Bank Name");

        jtxtCardBank.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCardBankFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCardBankFocusLost(evt);
            }
        });
        jtxtCardBank.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardBankKeyPressed(evt);
            }
        });

        jLabel7.setText("Card Amount");

        jtxtCardAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCardAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCardAmtFocusLost(evt);
            }
        });
        jtxtCardAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardAmtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCardAmtKeyTyped(evt);
            }
        });

        jLabel9.setText("Your Bank");

        jtxtBankAc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBankAcFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBankAcFocusLost(evt);
            }
        });
        jtxtBankAc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBankAcKeyPressed(evt);
            }
        });

        jLabel10.setText("Total Bill Amount");

        jlblSale.setText("0.00");

        jbtnReset.setMnemonic('R');
        jbtnReset.setText("Reset");
        jbtnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnResetActionPerformed(evt);
            }
        });

        jLabel11.setText("Total Purchase Pay");

        jlblSalePay.setText("0.00");

        jLabel12.setText("Remain Pay Amount");

        jlblRemainPay.setText("0.00");

        jcbBajaj.setText("Bajaj Capital");
        jcbBajaj.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbBajajItemStateChanged(evt);
            }
        });
        jcbBajaj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcbBajajKeyPressed(evt);
            }
        });

        jLabel13.setText("Bank Name");

        jtxtBajajCapital.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBajajCapitalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBajajCapitalFocusLost(evt);
            }
        });
        jtxtBajajCapital.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBajajCapitalKeyPressed(evt);
            }
        });

        jLabel14.setText("Loan Amt");

        jtxtBajajAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBajajAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBajajAmtFocusLost(evt);
            }
        });
        jtxtBajajAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBajajAmtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtBajajAmtKeyTyped(evt);
            }
        });

        jLabel15.setText("SFID");

        jtxtSFID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSFIDFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSFIDFocusLost(evt);
            }
        });
        jtxtSFID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSFIDKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtSFIDKeyTyped(evt);
            }
        });

        jcbDisc.setText("Discount");
        jcbDisc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbDiscItemStateChanged(evt);
            }
        });
        jcbDisc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcbDiscKeyPressed(evt);
            }
        });

        jtxtDiscount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDiscountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDiscountFocusLost(evt);
            }
        });
        jtxtDiscount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDiscountKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtDiscountKeyTyped(evt);
            }
        });

        jLabel16.setText("Discount");

        jLabel18.setText("Card No");

        jLabel19.setText("TID No");

        jtxtTIDNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTIDNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTIDNoFocusLost(evt);
            }
        });
        jtxtTIDNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTIDNoKeyPressed(evt);
            }
        });

        jtxtCardNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCardNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCardNoFocusLost(evt);
            }
        });
        jtxtCardNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardNoKeyPressed(evt);
            }
        });

        jLabel17.setText("%");

        jtxtCardPer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCardPerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCardPerFocusLost(evt);
            }
        });
        jtxtCardPer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCardPerKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCardPerKeyTyped(evt);
            }
        });

        jlblCardChanges.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel20.setText("%");

        jtxtBajajPer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBajajPerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBajajPerFocusLost(evt);
            }
        });
        jtxtBajajPer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBajajPerKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtBajajPerKeyTyped(evt);
            }
        });

        jlblBajajCharges.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jcbDisc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(85, 85, 85))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 237, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtBankAc)
                            .addComponent(jtxtBankName)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtChequeAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxtChequeDate, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxtChequeNo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jtxtBranchName)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbCash, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblSale, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jlblSalePay, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jlblRemainPay, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtCardNo)
                            .addComponent(jtxtTIDNo)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtCashAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jcbBank)
                            .addComponent(jcbCard)
                            .addComponent(jcbBajaj))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtCardPer, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblCardChanges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtSFID)
                            .addComponent(jtxtCardBank)
                            .addComponent(jtxtBajajCapital)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtCardAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxtBajajAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxtBajajPer, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblBajajCharges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel7, jLabel8});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jlblSale)
                    .addComponent(jbtnReset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbCash)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtCashAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbBank)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtBankAc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtBankName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtBranchName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtChequeNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtChequeDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtChequeAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jcbCard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtCardBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtCardAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jtxtCardNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jtxtTIDNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblCardChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtCardPer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)))
                .addGap(10, 10, 10)
                .addComponent(jcbBajaj)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtBajajCapital, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtBajajAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jtxtSFID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtBajajPer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20))
                    .addComponent(jlblBajajCharges, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(jcbDisc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jlblSalePay))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jlblRemainPay))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtCashAmt});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel15, jtxtSFID});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (validateData()) {
            doClose(RET_OK);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jcbCashItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbCashItemStateChanged
        setCompEnable();
    }//GEN-LAST:event_jcbCashItemStateChanged

    private void jcbBankItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbBankItemStateChanged
        setCompEnable();
    }//GEN-LAST:event_jcbBankItemStateChanged

    private void jcbCardItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbCardItemStateChanged
        setCompEnable();
    }//GEN-LAST:event_jcbCardItemStateChanged

    private void jtxtBankNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBankNameKeyPressed
        lb.enterFocus(evt, jtxtBranchName);
    }//GEN-LAST:event_jtxtBankNameKeyPressed

    private void jtxtBranchNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBranchNameKeyPressed
        lb.enterFocus(evt, jtxtChequeNo);
    }//GEN-LAST:event_jtxtBranchNameKeyPressed

    private void jtxtChequeNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtChequeNoKeyPressed
        lb.enterFocus(evt, jtxtChequeDate);
    }//GEN-LAST:event_jtxtChequeNoKeyPressed

    private void jtxtChequeDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtChequeDateKeyPressed
        lb.enterFocus(evt, jtxtChequeAmt);
    }//GEN-LAST:event_jtxtChequeDateKeyPressed

    private void jtxtCashAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCashAmtKeyTyped
        lb.onlyNumber(evt, jtxtCashAmt.getText().length() + 1);
    }//GEN-LAST:event_jtxtCashAmtKeyTyped

    private void jtxtChequeNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtChequeNoKeyTyped
        lb.onlyNumber(evt, jtxtChequeNo.getText().length() + 1);
    }//GEN-LAST:event_jtxtChequeNoKeyTyped

    private void jtxtChequeAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtChequeAmtKeyTyped
        lb.onlyNumber(evt, jtxtChequeAmt.getText().length() + 1);
    }//GEN-LAST:event_jtxtChequeAmtKeyTyped

    private void jtxtCardAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardAmtKeyTyped
        lb.onlyNumber(evt, jtxtCardAmt.getText().length() + 1);
    }//GEN-LAST:event_jtxtCardAmtKeyTyped

    private void jtxtBankNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBankNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtBankNameKeyTyped

    private void jtxtBranchNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBranchNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtBranchNameKeyTyped

    private void jbtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnResetActionPerformed
        reset();
    }//GEN-LAST:event_jbtnResetActionPerformed

    private void jtxtCashAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCashAmtFocusLost
        setTotal();
    }//GEN-LAST:event_jtxtCashAmtFocusLost

    private void jtxtChequeAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtChequeAmtFocusLost
        setTotal();
    }//GEN-LAST:event_jtxtChequeAmtFocusLost

    private void jtxtCardAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardAmtFocusLost
        setTotal();
    }//GEN-LAST:event_jtxtCardAmtFocusLost

    private void jtxtCashAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCashAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCashAmtFocusGained

    private void jtxtBankAcFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBankAcFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBankAcFocusGained

    private void jtxtBankNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBankNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBankNameFocusGained

    private void jtxtBranchNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBranchNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBranchNameFocusGained

    private void jtxtChequeNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtChequeNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtChequeNoFocusGained

    private void jtxtChequeDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtChequeDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtChequeDateFocusGained

    private void jtxtChequeAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtChequeAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtChequeAmtFocusGained

    private void jtxtCardBankFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardBankFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCardBankFocusGained

    private void jtxtCardAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCardAmtFocusGained

    private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_okButtonKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_okButtonKeyPressed

    private void jtxtCashAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCashAmtKeyPressed
        lb.enterFocus(evt, jcbBank);
    }//GEN-LAST:event_jtxtCashAmtKeyPressed

    private void jtxtChequeAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtChequeAmtKeyPressed
        lb.enterFocus(evt, jcbCard);
    }//GEN-LAST:event_jtxtChequeAmtKeyPressed

    private void jtxtCardAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardAmtKeyPressed
        lb.enterFocus(evt, jtxtCardNo);
    }//GEN-LAST:event_jtxtCardAmtKeyPressed

    private void jtxtBankAcFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBankAcFocusLost
    }//GEN-LAST:event_jtxtBankAcFocusLost

    private void jtxtCardBankFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardBankFocusLost
    }//GEN-LAST:event_jtxtCardBankFocusLost

    private void jcbCashKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcbCashKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (jcbCash.isSelected()) {
                jtxtCashAmt.requestFocusInWindow();
            } else {
                jcbBank.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jcbCashKeyPressed

    private void jcbBankKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcbBankKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (jcbBank.isSelected()) {
                jtxtBankAc.requestFocusInWindow();
            } else {
                jcbCard.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jcbBankKeyPressed

    private void jcbCardKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcbCardKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (jcbCard.isSelected()) {
                jtxtCardBank.requestFocusInWindow();
            } else {
                jcbBajaj.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jcbCardKeyPressed

    private void jcbBajajItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbBajajItemStateChanged
        // TODO add your handling code here:
        setCompEnable();
    }//GEN-LAST:event_jcbBajajItemStateChanged

    private void jcbBajajKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcbBajajKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (jcbBajaj.isSelected()) {
                jtxtBajajCapital.requestFocusInWindow();
            } else {
                jcbDisc.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jcbBajajKeyPressed

    private void jtxtBajajCapitalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBajajCapitalFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBajajCapitalFocusGained

    private void jtxtBajajCapitalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBajajCapitalFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtBajajCapitalFocusLost

    private void jtxtBajajAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBajajAmtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBajajAmtFocusGained

    private void jtxtBajajAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBajajAmtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        setTotal();
    }//GEN-LAST:event_jtxtBajajAmtFocusLost

    private void jtxtBajajAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBajajAmtKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtSFID);
    }//GEN-LAST:event_jtxtBajajAmtKeyPressed

    private void jtxtBajajAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBajajAmtKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtBajajAmtKeyTyped

    private void jtxtBankAcKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBankAcKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtBankAc)) {
                setAccountDetailMobile("2", jtxtBankAc.getText(), "1");
            }
        }
    }//GEN-LAST:event_jtxtBankAcKeyPressed

    private void jtxtCardBankKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardBankKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtCardBank)) {
                setAccountDetailMobile("2", jtxtCardBank.getText(), "2");
            }
        }
    }//GEN-LAST:event_jtxtCardBankKeyPressed

    private void jtxtBajajCapitalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBajajCapitalKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (!lb.isBlank(jtxtBajajCapital)) {
                setAccountDetailMobile("2", jtxtBajajCapital.getText(), "3");
            }
        }
    }//GEN-LAST:event_jtxtBajajCapitalKeyPressed

    private void jtxtSFIDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSFIDFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSFIDFocusGained

    private void jtxtSFIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSFIDFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtSFIDFocusLost

    private void jtxtSFIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSFIDKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtBajajPer);
    }//GEN-LAST:event_jtxtSFIDKeyPressed

    private void jtxtSFIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSFIDKeyTyped
        // TODO add your handling code here:
        lb.fixLength(evt, 25);
    }//GEN-LAST:event_jtxtSFIDKeyTyped

    private void jcbDiscItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbDiscItemStateChanged
        // TODO add your handling code here:
        setCompEnable();
    }//GEN-LAST:event_jcbDiscItemStateChanged

    private void jcbDiscKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcbDiscKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            if (jcbDisc.isSelected()) {
                jtxtDiscount.requestFocusInWindow();
            } else {
                okButton.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jcbDiscKeyPressed

    private void jtxtDiscountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscountFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDiscountFocusGained

    private void jtxtDiscountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscountFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        setTotal();
    }//GEN-LAST:event_jtxtDiscountFocusLost

    private void jtxtDiscountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscountKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, okButton);
    }//GEN-LAST:event_jtxtDiscountKeyPressed

    private void jtxtDiscountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscountKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtDiscountKeyTyped

    private void jtxtTIDNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTIDNoFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTIDNoFocusGained

    private void jtxtTIDNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTIDNoFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtTIDNoFocusLost

    private void jtxtTIDNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTIDNoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_N) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                TidMasterController tid = new TidMasterController(null, true, null, "", "");
                tid.setLocationRelativeTo(null);
                tid.setVisible(true);
            }
        }
        if (lb.isEnter(evt)) {
            setTIDData("32", jtxtTIDNo.getText().trim());
        }
    }//GEN-LAST:event_jtxtTIDNoKeyPressed

    private void jtxtCardNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardNoFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCardNoFocusGained

    private void jtxtCardNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardNoFocusLost
        // TODO add your handling code here:
        lb.toInteger(evt);
    }//GEN-LAST:event_jtxtCardNoFocusLost

    private void jtxtCardNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardNoKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jtxtTIDNo);
    }//GEN-LAST:event_jtxtCardNoKeyPressed

    private void jtxtChequeDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtChequeDateFocusLost
        // TODO add your handling code here:
        try {
            if (jtxtChequeDate.getText().contains("/")) {
                jtxtChequeDate.setText(jtxtChequeDate.getText().replace("/", ""));
            }
            if (jtxtChequeDate.getText().length() == 8) {
                String temp = jtxtChequeDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtChequeDate.setText(setDate);
            }
            //            if ((new SimpleDateFormat("dd/MM/yyyy").format(new Date(jtxtChequeDate.getText().trim()))) != null) {
            //                jtxtBillDate.requestFocusInWindow();
            //            }
        } catch (Exception ex) {
            jtxtChequeDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtChequeDateFocusLost

    private void jtxtCardPerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardPerFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCardPerFocusGained

    private void jtxtCardPerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCardPerFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        setTotal();
    }//GEN-LAST:event_jtxtCardPerFocusLost

    private void jtxtCardPerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardPerKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcbBajaj);
    }//GEN-LAST:event_jtxtCardPerKeyPressed

    private void jtxtCardPerKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCardPerKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtCardPerKeyTyped

    private void jtxtBajajPerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBajajPerFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBajajPerFocusGained

    private void jtxtBajajPerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBajajPerFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
        setTotal();
    }//GEN-LAST:event_jtxtBajajPerFocusLost

    private void jtxtBajajPerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBajajPerKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jcbDisc);
    }//GEN-LAST:event_jtxtBajajPerKeyPressed

    private void jtxtBajajPerKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBajajPerKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtBajajPerKeyTyped

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
    }

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel11;
    public javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jbtnReset;
    public javax.swing.JCheckBox jcbBajaj;
    public javax.swing.JCheckBox jcbBank;
    public javax.swing.JCheckBox jcbCard;
    public javax.swing.JCheckBox jcbCash;
    public javax.swing.JCheckBox jcbDisc;
    public javax.swing.JLabel jlblBajajCharges;
    public javax.swing.JLabel jlblCardChanges;
    public javax.swing.JLabel jlblRemainPay;
    public javax.swing.JLabel jlblSale;
    public javax.swing.JLabel jlblSalePay;
    public javax.swing.JTextField jtxtBajajAmt;
    public javax.swing.JTextField jtxtBajajCapital;
    public javax.swing.JTextField jtxtBajajPer;
    public javax.swing.JTextField jtxtBankAc;
    public javax.swing.JTextField jtxtBankName;
    public javax.swing.JTextField jtxtBranchName;
    public javax.swing.JTextField jtxtCardAmt;
    public javax.swing.JTextField jtxtCardBank;
    public javax.swing.JTextField jtxtCardNo;
    public javax.swing.JTextField jtxtCardPer;
    public javax.swing.JTextField jtxtCashAmt;
    public javax.swing.JTextField jtxtChequeAmt;
    public javax.swing.JTextField jtxtChequeDate;
    public javax.swing.JTextField jtxtChequeNo;
    public javax.swing.JTextField jtxtDiscount;
    public javax.swing.JTextField jtxtSFID;
    public javax.swing.JTextField jtxtTIDNo;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
