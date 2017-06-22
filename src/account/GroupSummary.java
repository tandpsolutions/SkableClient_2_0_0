/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.data.JsonDataSource;
import retrofitAPI.AccountAPI;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;
import support.Library;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author nice
 */
public class GroupSummary extends javax.swing.JInternalFrame {

    /**
     * Creates new form salesRegisterSummary
     */
    Library lb = Library.getInstance();
    DefaultTableModel dtm = null;
    private ReportTable viewTable = null;
    private String grp_cd;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    public GroupSummary() {
        initComponents();
        setPopUp();
        dtm = (DefaultTableModel) jTable1.getModel();
        registerShortKeys();
        tableForView();
        searchOnTextFields();
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
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

    @Override
    public void dispose() {
        try {
            SkableHome.removeFromScreen(SkableHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
//            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
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

    private void jbtnViewActionPerformedRoutine() {
        try {
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class
            );
            if (jRadioButton3.isSelected()) {
                grp_cd = "";
            } else if (jRadioButton2.isSelected()) {
            }
            int mode = 0;
            if (jcbAccount.isSelected()) {
                if (jchkPayable.isSelected()) {
                    mode = 1;
                }
                if (jchkReceivable.isSelected()) {
                    mode = 2;
                }
            }
            JsonObject call = accountAPI.GetGroupSummary(grp_cd, mode, lb.isNumber(jtxtAmt)).execute().body();

            lb.addGlassPane(this);

            System.out.println(call.toString());
            lb.removeGlassPane(GroupSummary.this);
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = result.getAsJsonArray("data");
                    dtm.setRowCount(0);
                    double opb = 0.00, pur = 0.00, sal = 0.00, stock = 0.00;
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("fname").getAsString());
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("opb").getAsDouble()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("dr").getAsDouble()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("cr").getAsDouble()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("bal").getAsDouble()));
                        row.add(array.get(i).getAsJsonObject().get("ac_cd").getAsString());
                        dtm.addRow(row);
                        opb += array.get(i).getAsJsonObject().get("opb").getAsDouble();
                        pur += array.get(i).getAsJsonObject().get("dr").getAsDouble();
                        sal += array.get(i).getAsJsonObject().get("cr").getAsDouble();
                        stock += array.get(i).getAsJsonObject().get("bal").getAsDouble();
                    }

                    Vector row = new Vector();
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("Total");
                    row.add(lb.Convert2DecFmtForRs(opb));
                    row.add(lb.Convert2DecFmtForRs(pur));
                    row.add(lb.Convert2DecFmtForRs(sal));
                    row.add(lb.Convert2DecFmtForRs(stock));
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                } else {
                    lb.showMessageDailog(result.get("Cause").getAsString());
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void jbtnPreViewActionPerformedRoutine() throws IOException {
        AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class
        );
        if (jRadioButton3.isSelected()) {
            grp_cd = "";
        } else if (jRadioButton2.isSelected()) {
        }
        int mode = 0;
        if (jcbAccount.isSelected()) {
            if (jchkPayable.isSelected()) {
                mode = 1;
            }
            if (jchkReceivable.isSelected()) {
                mode = 2;
            }
        }
        JsonObject call = accountAPI.GetGroupSummary(grp_cd, mode, lb.isNumber(jtxtAmt)).execute().body();

        lb.addGlassPane(this);

        lb.removeGlassPane(GroupSummary.this);
        if (call != null) {
            JsonObject result = call;
            if (result.get("result").getAsInt() == 1) {
                JsonArray array = result.getAsJsonArray("data");
                if (array != null) {
                    try {
                        FileWriter file = new FileWriter(System.getProperty("user.dir") + File.separator + "file1.txt");
                        file.write(array.toString());
                        file.close();
                        File jsonFile = new File(System.getProperty("user.dir") + File.separator + "file1.txt");
                        JsonDataSource dataSource = new JsonDataSource(jsonFile);
                        HashMap params = new HashMap();
                        params.put("dir", System.getProperty("user.dir"));
                        params.put("group_name", jtxtBrandName.getText());
                        lb.reportGenerator("GroupSummary.jasper", params, dataSource, jPanel1);
                    } catch (Exception ex) {
                    }
                }

            } else {
                lb.showMessageDailog(result.get("Cause").getAsString());
            }
        }

    }

    private void setBrandData(String param_cd, String value) {
        try {
            JsonObject call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase()).execute().body();
            lb.addGlassPane(this);
            lb.removeGlassPane(GroupSummary.this);
            if (call != null) {
                System.out.println(call.toString());
                if (call.get("result").getAsInt() == 1) {
                    final SelectDailog sa = new SelectDailog(null, true);
                    sa.setData(viewTable);
                    sa.setLocationRelativeTo(null);
                    JsonArray array = call.getAsJsonArray("data");
                    sa.getDtmHeader().setRowCount(0);
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("GRP_CD").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("GROUP_NAME").getAsString());
                        sa.getDtmHeader().addRow(row);
                    }
                    lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                    sa.setVisible(true);
                    if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                        int row = viewTable.getSelectedRow();
                        if (row != -1) {
                            grp_cd = viewTable.getValueAt(row, 0).toString();
                            if (jRadioButton2.isSelected()) {
                                jtxtBrandName.setText(viewTable.getValueAt(row, 1).toString());
                                jbtnView.requestFocusInWindow();
                            }
                        }
                        sa.dispose();
                    }
                } else {
                    lb.showMessageDailog(call.get("Cause").toString());
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jbtnView = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtxtBrandName = new javax.swing.JTextField();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jcbAccount = new javax.swing.JCheckBox();
        jchkPayable = new javax.swing.JRadioButton();
        jchkReceivable = new javax.swing.JRadioButton();
        jtxtAmt = new javax.swing.JTextField();
        panel = new javax.swing.JPanel();

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

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "A/C Name", "Opening", "Dr Transaction", "Cr Transaction", "Balance", "AC_CD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setMinWidth(0);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jtxtBrandName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBrandNameFocusGained(evt);
            }
        });
        jtxtBrandName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBrandNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtBrandNameKeyReleased(evt);
            }
        });

        buttonGroup2.add(jRadioButton2);
        jRadioButton2.setText("Group Name");

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("All");

        jcbAccount.setText("Account");
        jcbAccount.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbAccountItemStateChanged(evt);
            }
        });

        buttonGroup1.add(jchkPayable);
        jchkPayable.setText("Payable");

        buttonGroup1.add(jchkReceivable);
        jchkReceivable.setText("Receivable");

        jtxtAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAmtFocusLost(evt);
            }
        });
        jtxtAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAmtKeyTyped(evt);
            }
        });

        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jcbAccount)
                                .addGap(0, 0, 0)
                                .addComponent(jchkPayable)
                                .addGap(0, 0, 0)
                                .addComponent(jchkReceivable)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .addComponent(jtxtAmt)))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtnView)
                            .addComponent(jbtnPreview)
                            .addComponent(jbtnClose)
                            .addComponent(jRadioButton2)
                            .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jchkReceivable)
                            .addComponent(jchkPayable)
                            .addComponent(jcbAccount))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jRadioButton3)
                        .addGap(283, 283, 283)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jcbAccount, jchkPayable, jchkReceivable, jtxtAmt});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        // TODO add your handling code here:
        jPanel1.removeAll();
        jPanel1.add(jScrollPane1);
        jScrollPane1.setVisible(true);
        jbtnViewActionPerformedRoutine();
        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnView.doClick();
        }
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        try {
            // TODO add your handling code here:
            jbtnPreViewActionPerformedRoutine();
        } catch (IOException ex) {
            Logger.getLogger(GroupSummary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
            try {
                jbtnPreViewActionPerformedRoutine();
            } catch (IOException ex) {
                Logger.getLogger(GroupSummary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
            this.dispose();
        }
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jtxtBrandNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBrandNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBrandNameFocusGained

    private void jtxtBrandNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt) && jRadioButton2.isSelected()) {
            setBrandData("11", jtxtBrandName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtBrandNameKeyPressed

    private void jtxtBrandNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_jtxtBrandNameKeyReleased

    private void jcbAccountItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbAccountItemStateChanged
//        setCompEnable();
    }//GEN-LAST:event_jcbAccountItemStateChanged

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (jTable1.getSelectedRow() != -1) {
            if (evt.getClickCount() == 2) {
                GeneralLedger1 gl = new GeneralLedger1(jTable1.getValueAt(jTable1.getSelectedRow(), 5).toString(), jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
                SkableHome.addOnScreen(gl, "General Ledger");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmtFocusGained

    private void jtxtAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtAmtFocusLost

    private void jtxtAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnView);
    }//GEN-LAST:event_jtxtAmtKeyPressed

    private void jtxtAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyTyped
        // TODO add your handling code here:
        lb.onlyNumber(evt, -1);
    }//GEN-LAST:event_jtxtAmtKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JCheckBox jcbAccount;
    private javax.swing.JRadioButton jchkPayable;
    private javax.swing.JRadioButton jchkReceivable;
    private javax.swing.JTextField jtxtAmt;
    private javax.swing.JTextField jtxtBrandName;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
