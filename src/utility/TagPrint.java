/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import retrofitAPI.PurchaseAPI;
import skable.SkableHome;
import support.Library;

/**
 *
 * @author Bhaumik
 */
public class TagPrint extends javax.swing.JInternalFrame {

    /**
     * Creates new form TagPrint
     */
    JsonObject result = null;
    Library lb = Library.getInstance();
    DefaultTableModel dtm = null;
    DefaultTableModel dtmTag = null;
    PurchaseAPI purchaseAPI;

    public TagPrint(JsonObject result, PurchaseAPI purchaseAPI) {
        initComponents();
        initOtheComponents();
        this.result = result;
        this.purchaseAPI = purchaseAPI;
        loadData();
        jrbtRange.setSelected(true);
    }

    public TagPrint() {
        initComponents();
        initOtheComponents();
    }

    private void initOtheComponents() {
        dtmTag = (DefaultTableModel) jTable2.getModel();
    }

    private void loadData() {
        try {
//            dtmTag.setRowCount(0);
            JsonArray array = result.getAsJsonArray("data");
            for (int i = 0; i < array.size(); i++) {
                Vector row = new Vector();
                row.add(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                dtmTag.addRow(row);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at loadData in TagPrint", ex);
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

    private void generateQuery() {
        try {
            String tagListForRandom = "";
            if (jrbtRandom.isSelected()) {
                int[] selectedrows = jTable2.getSelectedRows();
                for (int i = 0; i < selectedrows.length; i++) {
                    tagListForRandom += ("'" + jTable2.getValueAt(selectedrows[i], 0).toString() + "',");
                }
            } else {
                for (int i = 0; i < jTable2.getRowCount(); i++) {
                    tagListForRandom += ("'" + jTable2.getValueAt(i, 0).toString() + "',");
                }
            }
            tagListForRandom = tagListForRandom.substring(0, tagListForRandom.length() - 1);

            JsonObject call = purchaseAPI.getTagNoDetail(tagListForRandom, "6", jCheckBox1.isSelected()).execute().body();
            if (call != null) {
                result = call;
                processResult();
            }
        } catch (IOException ex) {
            Logger.getLogger(TagPrint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jbtnPrintActionPerformedRoutine() {
        generateQuery();
    }

    private void processResult() {
        try {
            JsonArray array = result.getAsJsonArray("data");
            if (array != null) {
                String tag1 = "", SR_NAME1 = "";
                String nlc1 = "", RATE1 = "";
                int i = 0;
                for (; i < array.size();) {
                    if (i == 0) {
                        tag1 = array.get(i).getAsJsonObject().get("TAG_NO").getAsString();
                        SR_NAME1 = array.get(i).getAsJsonObject().get("SR_NAME").getAsString();
                        nlc1 = array.get(i).getAsJsonObject().get("NLC").getAsString();
                        RATE1 = array.get(i).getAsJsonObject().get("RATE").getAsString();
                    } else if (i % 2 == 0) {
                        tag1 = array.get(i).getAsJsonObject().get("TAG_NO").getAsString();
                        SR_NAME1 = array.get(i).getAsJsonObject().get("SR_NAME").getAsString();
                        nlc1 = array.get(i).getAsJsonObject().get("NLC").getAsString();
                        RATE1 = array.get(i).getAsJsonObject().get("RATE").getAsString();
                    }
                    if (i == 0 || ((i + 1) % 2) == 1) {
                        i++;
                        if (i >= array.size()) {
                            break;
                        }
                        lb.PrintLabel(array.get(i).getAsJsonObject().get("TAG_NO").getAsString(), array.get(i).getAsJsonObject().get("SR_NAME").getAsString(), array.get(i).getAsJsonObject().get("RATE").getAsString(), array.get(i).getAsJsonObject().get("NLC").getAsString(), tag1, SR_NAME1, RATE1, nlc1);
                        i++;
                    }
                }
                if (array.size() % 2 == 1) {
                    lb.PrintLabel(array.get(i - 1).getAsJsonObject().get("TAG_NO").getAsString(), array.get(i - 1).getAsJsonObject().get("SR_NAME").getAsString(), array.get(i - 1).getAsJsonObject().get("RATE").getAsString(), array.get(i - 1).getAsJsonObject().get("NLC").getAsString());

                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jbtnPrintActionPerformedRoutine", ex);
        } finally {
            lb.removeGlassPane(this);
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
        jPanel2 = new javax.swing.JPanel();
        jrbtRandom = new javax.swing.JRadioButton();
        jrbtRange = new javax.swing.JRadioButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        buttonGroup1.add(jrbtRandom);
        jrbtRandom.setText("Random");

        buttonGroup1.add(jrbtRange);
        jrbtRange.setSelected(true);
        jrbtRange.setText("All");

        jCheckBox1.setText("W/ O Sales");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                    .addComponent(jrbtRandom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrbtRange, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrbtRandom)
                    .addComponent(jrbtRange))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel3.setLayout(new java.awt.BorderLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag No"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable2KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
        }

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jButton1.setText("View");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Print");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Close");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2, jButton3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jbtnPrintActionPerformedRoutine();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable2KeyPressed
        // TODO add your handling code here:
        int row = jTable2.getSelectedRow();
        if (row != -1) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                dtmTag.removeRow(row);
            }
        }
    }//GEN-LAST:event_jTable2KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JRadioButton jrbtRandom;
    private javax.swing.JRadioButton jrbtRange;
    // End of variables declaration//GEN-END:variables
}
