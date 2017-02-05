/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.BorderLayout;
import java.io.IOException;
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
import retrofitAPI.AccountAPI;
import skable.SkableHome;
import support.Library;

/**
 *
 * @author bhaumik
 */
public class SalesRegisterCardWise extends javax.swing.JInternalFrame {

    /**
     * Creates new form SalesRegister
     */
    Library lb = Library.getInstance();
    DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    public SalesRegisterCardWise() {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        searchOnTextFields();
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

            JsonObject call = accountAPI.SalesRegisterCardWise(jCheckBox1.isSelected()).execute().body();

            lb.addGlassPane(this);

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    dtm.setRowCount(0);
                    double net = 0.00;
                    for (int i = 0; i < array.size(); i++) {
                        try {
                            Vector row = new Vector();
                            row.add(i + 1);
                            row.add((array.get(i).getAsJsonObject().get("AC_CD").getAsString()));
                            row.add((array.get(i).getAsJsonObject().get("FNAME") == null) ? "" : array.get(i).getAsJsonObject().get("FNAME").getAsString());
                            row.add((array.get(i).getAsJsonObject().get("CARD_NO").getAsString()));
                            row.add((array.get(i).getAsJsonObject().get("MOBILE1").getAsString()));
                            row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("NET_AMT").getAsDouble()));
                            dtm.addRow(row);
                            net += array.get(i).getAsJsonObject().get("NET_AMT").getAsDouble();
                        } catch (Exception e) {

                        }
                    }

                    Vector row = new Vector();
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    row.add(" ");
                    dtm.addRow(row);

                    row = new Vector();
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(net));
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(SalesRegisterCardWise.this);
                } else {
                    lb.removeGlassPane(SalesRegisterCardWise.this);
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SalesRegisterCardWise.class.getName()).log(Level.SEVERE, null, ex);
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

        jbtnView = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        panel = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();

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
                "Sr No", "ac_cd", "Name", "Card", "Mobile", "Net Amt"
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
            jTable1.getColumnModel().getColumn(1).setMinWidth(0);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(1).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panel.setLayout(new java.awt.BorderLayout());

        jCheckBox1.setText("Without Card No");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnView});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtnClose)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jCheckBox1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String ac_cd = jTable1.getValueAt(row, 1).toString();
                String ac_name = jTable1.getValueAt(row, 2).toString();
                String card_no = jTable1.getValueAt(row, 3).toString();
                SalesRegisterDetailCardWise sd = new SalesRegisterDetailCardWise();
                sd.ac_cd = ac_cd;
                sd.jtxtAcAlias.setText(ac_cd);
                sd.jtxtAcName.setText(ac_name);
                sd.jtxtCardNo.setText(card_no);
                sd.jCheckBox1.setSelected(true);
                sd.jbtnView.doClick();
                SkableHome.addOnScreen(sd, "Sales Register Detail Card Wise");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnView;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
