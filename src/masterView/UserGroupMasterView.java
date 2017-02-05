package masterView;

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
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import masterController.UserGroupMasterController;
import retrofitAPI.UserAPI;
import skable.SkableHome;
import support.Library;
import support.SmallNavigation;

public class UserGroupMasterView extends javax.swing.JInternalFrame {

    private DefaultTableModel dtm = null;
    private SmallNavigation navLoad = null;
    private final Library lb = Library.getInstance();
    private UserAPI userAPI;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    public UserGroupMasterView(int formCD) {
        initComponents();
        userAPI = lb.getRetrofit().create(UserAPI.class);
        connectToNavigation();
        searchOnTextFields();
        dtm = (DefaultTableModel) jTable1.getModel();
        getData();
        if (jTable1.getRowCount() > 0) {
            jTable1.requestFocusInWindow();
            jTable1.setRowSelectionInterval(0, 0);
        }
        navLoad.setFormCd(formCD);
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

    private void close() {
        this.dispose();
    }

    @Override
    public void dispose() {
        try {
            SkableHome.removeFromScreen(SkableHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showDailogeAdd(String user_grp_cd, String user_grp) {
        UserGroupMasterController bmc = new UserGroupMasterController(null, true, this, user_grp_cd, user_grp);
        bmc.setLocationRelativeTo(null);
        bmc.show();
    }

    public void addRow(String bran_cd, String brand_name) {
        int selRow = jTable1.getSelectedRow();
        if (selRow == -1) {
            Vector row = new Vector();
            row.add(bran_cd);
            row.add(brand_name);
            dtm.addRow(row);
        } else {
            jTable1.setValueAt(bran_cd, selRow, 0);
            jTable1.setValueAt(brand_name, selRow, 1);
        }
    }

    private void connectToNavigation() {
        class navigation extends SmallNavigation {

            @Override
            public void callNew() {
                if (navLoad.getModel().getADDS().equalsIgnoreCase("1")) {
                    navLoad.setMode("N");
                    jTable1.clearSelection();
                    showDailogeAdd("", "");
                } else {
                    lb.showMessageDailog("You don't have rights to perform this action");
                }
            }

            @Override
            public void callEdit() {
                if (navLoad.getModel().getEDITS().equalsIgnoreCase("1")) {
                    int row = jTable1.getSelectedRow();
                    if (row != -1) {
                        navLoad.setMode("E");
                        lb.confirmDialog("Do you want to Edit This Brand?");
                        if (lb.type) {
                            showDailogeAdd(jTable1.getValueAt(row, 0).toString(), jTable1.getValueAt(row, 1).toString());
                        }
                    }
                } else {
                    lb.showMessageDailog("You don't have rights to perform this action");
                }
            }

            @Override
            public void callDelete() {
                if (navLoad.getModel().getDELETES().equalsIgnoreCase("1")) {
                    SwingWorker workerForjbtnGenerate = new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            int row = jTable1.getSelectedRow();
                            if (row != -1) {
                                try {
                                } catch (Exception ex) {
                                    lb.printToLogFile("Exception at callDelete at BrandMasterView", ex);
                                } finally {
                                    lb.removeGlassPane(UserGroupMasterView.this);
                                }
                            }

                            return null;
                        }
                    };
                    workerForjbtnGenerate.execute();
                } else {
                    lb.showMessageDailog("You don't have rights to perform this action");
                }
            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void callPrint() {

            }

        }
        navLoad = new navigation();
        jPanel2.add(navLoad);
        navLoad.setVisible(true);
    }

    public void getData() {
        try {
            JsonObject call = userAPI.GetUserGrpMaster().execute().body();

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray data = result.get("data").getAsJsonArray();
                    dtm.setRowCount(0);
                    for (int i = 0; i < data.size(); i++) {
                        Vector row = new Vector();
                        row.add(data.get(i).getAsJsonObject().get("USER_GRP_CD").getAsString());
                        row.add(data.get(i).getAsJsonObject().get("USER_GRP").getAsString());
                        dtm.addRow(row);
                    }
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserGroupMasterView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        panel = new javax.swing.JPanel();

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "user_grp_cd", "User Group Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(469);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            navLoad.callEdit();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            navLoad.callEdit();
        }
    }//GEN-LAST:event_jTable1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
