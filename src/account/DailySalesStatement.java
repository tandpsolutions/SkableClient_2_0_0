/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import retrofitAPI.AccountAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;

/**
 *
 * @author LENOVO
 */
public class DailySalesStatement extends javax.swing.JInternalFrame {

    /**
     * Creates new form StockValueStatement
     */
    Library lb = Library.getInstance();
    DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    public DailySalesStatement() {
        initComponents();
        setTypeCombo();
        setUpData();
        dtm = (DefaultTableModel) jTable1.getModel();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
    }

    private void setTypeCombo() {

        jComboBox1.setSelectedIndex(2);
    }

    private void setUpData() {
        jComboBox2.removeAllItems();
        jComboBox2.addItem("All");
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox2.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
        jComboBox2.setSelectedItem(SkableHome.selected_branch.getBranch_name());
        if (SkableHome.user_grp_cd.equalsIgnoreCase("1")) {
            jComboBox2.setEnabled(true);
        } else {
            jComboBox2.setEnabled(false);
        }
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

    private void makeQuery() {
        try {
            jPanel1.removeAll();
            jPanel1.add(jScrollPane1);
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class
            );

            JsonObject call = accountAPI.DailySalesStatement(jComboBox1.getSelectedIndex(), lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                    lb.ConvertDateFormetForDB(jtxtToDate.getText()), ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_cd() : "0")).execute().body();

            lb.addGlassPane(this);

            JsonObject result = call;
            if (result.get("result").getAsInt() == 1) {
                JsonArray array = call.getAsJsonArray("data");
                dtm.setRowCount(0);
                for (int i = 0; i < array.size(); i++) {
                    Vector row = new Vector();
                    row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name());
                    row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("v_date").getAsString()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("net_amt").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("ins").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("buy_back").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("cash").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("card").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bank").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bajaj").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("net_amt").getAsDouble()
                            - array.get(i).getAsJsonObject().get("cash").getAsDouble()
                            - array.get(i).getAsJsonObject().get("card").getAsDouble()
                            - array.get(i).getAsJsonObject().get("bajaj").getAsDouble()
                            - array.get(i).getAsJsonObject().get("bank").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("adjst").getAsDouble()));
                    row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("MOP").getAsDouble()));
                    dtm.addRow(row);
                }

                if (jCheckBox1.isSelected()) {
                    call = accountAPI.DailyCashStatement(lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                            lb.ConvertDateFormetForDB(jtxtToDate.getText()), jComboBox2.getSelectedIndex()).execute().body();
                    if (call != null) {
                        result = call;
                        if (result.get("result").getAsInt() == 1) {
                            array = call.getAsJsonArray("data");
                            for (int i = 0; i < array.size(); i++) {
                                Vector row = new Vector();
                                row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name() + " Cash");
                                row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("v_date").getAsString()));
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bal").getAsDouble()));
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                dtm.addRow(row);
                            }
                        }

                    }
                }

                if (jCheckBox2.isSelected()) {
                    call = accountAPI.DailyBankSummary(lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                            lb.ConvertDateFormetForDB(jtxtToDate.getText()), jComboBox2.getSelectedIndex()).execute().body();
                    if (call != null) {
                        result = call;
                        if (result.get("result").getAsInt() == 1) {
                            array = call.getAsJsonArray("data");
                            for (int i = 0; i < array.size(); i++) {
                                Vector row = new Vector();
                                row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name() + " Cheque");
                                row.add(lb.ConvertDateFormetForDisplay(array.get(i).getAsJsonObject().get("v_date").getAsString()));
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bal").getAsDouble()));
                                row.add("");
                                row.add("");
                                row.add("");
                                row.add("");
                                dtm.addRow(row);
                            }
                        }

                    }
                }

                double net = 0.00, insurance = 0.00, buy_back = 0.00, cash = 0.00, card = 0.00, bank = 0.00, debtors = 0.00, bajaj = 0.00,
                        discount = 0.00, mop = 0.00;
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    net += lb.isNumber2(jTable1.getValueAt(i, 2).toString());
                    insurance += lb.isNumber2(jTable1.getValueAt(i, 3).toString());
                    buy_back += lb.isNumber2(jTable1.getValueAt(i, 4).toString());
                    cash += lb.isNumber2(jTable1.getValueAt(i, 5).toString());
                    card += lb.isNumber2(jTable1.getValueAt(i, 6).toString());
                    bank += lb.isNumber2(jTable1.getValueAt(i, 7).toString());
                    bajaj += lb.isNumber2(jTable1.getValueAt(i, 8).toString());
                    debtors += lb.isNumber2(jTable1.getValueAt(i, 9).toString());
                    discount += lb.isNumber2(jTable1.getValueAt(i, 10).toString());
                    mop += lb.isNumber2(jTable1.getValueAt(i, 11).toString());
                }

                Vector row = new Vector();
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                row.add(" ");
                dtm.addRow(row);

                row = new Vector();
                row.add("Total");
                row.add(" ");
                row.add(lb.Convert2DecFmtForRs(net));
                row.add(lb.Convert2DecFmtForRs(insurance));
                row.add(lb.Convert2DecFmtForRs(buy_back));
                row.add(lb.Convert2DecFmtForRs(cash));
                row.add(lb.Convert2DecFmtForRs(card));
                row.add(lb.Convert2DecFmtForRs(bank));
                row.add(lb.Convert2DecFmtForRs(bajaj));
                row.add(lb.Convert2DecFmtForRs(debtors));
                row.add(lb.Convert2DecFmtForRs(discount));
                row.add(lb.Convert2DecFmtForRs(mop));
                dtm.addRow(row);

                lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                lb.removeGlassPane(DailySalesStatement.this);
            } else {
                lb.removeGlassPane(DailySalesStatement.this);
                lb.showMessageDailog(call.get("Cause").getAsString());
            }

        } catch (Exception ex) {
            lb.printToLogFile("Exception at stockValue Statement", ex);
            lb.removeGlassPane(DailySalesStatement.this);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jtxtFromDate = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jBillDateBtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jBillDateBtn1 = new javax.swing.JButton();
        jtxtToDate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Branch", "Date", "Net Amt", "Insurance Amt", "Buy Back", "Cash", "Card", "Bank", "Bajaj", "Debtors", "Discount", "MOP"
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
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setMinWidth(0);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(2).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(3).setMinWidth(0);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(3).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setResizable(false);
            jTable1.getColumnModel().getColumn(10).setMinWidth(0);
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(10).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(11).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Retail", "Tax", "All" }));

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jLabel5.setText("Voucher Type");

        jLabel4.setText("To Date");

        jButton4.setText("Excel");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel3.setText("From Date");

        jButton3.setText("Close");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jbtnView.setText("View ");
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

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
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

        jLabel6.setText("Branch");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jCheckBox1.setText("Include Cash Detail");

        jCheckBox2.setText("Include Bank Detail");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jButton3)
                        .addComponent(jButton4))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCheckBox1)
                        .addComponent(jCheckBox2))
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jComboBox2, jLabel5, jLabel6});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        // TODO add your handling code here:
        makeQuery();
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

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
        if (lb.isEnter(evt)) {
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
                jbtnView.requestFocusInWindow();
            }

        } catch (Exception ex) {
            //            navLoad.jlblMsg.setText("Enter Correct Date");
            jtxtToDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtToDateFocusLost

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
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

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                DailySalesStatementDetail dsd = new DailySalesStatementDetail(jTable1.getValueAt(row, 1).toString(),
                        jTable1.getValueAt(row, 1).toString(), jComboBox1.getSelectedIndex(), jTable1.getValueAt(row, 0).toString());
                SkableHome.addOnScreen(dsd, "Daily Sales Statement Detail");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnView);
    }//GEN-LAST:event_jComboBox2KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtToDate;
    // End of variables declaration//GEN-END:variables
}
