package masterView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import masterController.AccountMasterController;
import model.AccountMasterModel;
import model.GroupMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.AccountMasterAPI;
import retrofitAPI.GroupMasterAPI;
import skable.SkableHome;
import support.Library;
import support.SmallNavigation;

public class AccountMasterView extends javax.swing.JInternalFrame {

    private DefaultTableModel dtm = null;
    private SmallNavigation navLoad = null;
    private final Library lb = Library.getInstance();
    private AccountMasterAPI accountAPI;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    public ArrayList<GroupMasterModel> detail;
    private GroupMasterAPI groupAPI;
    ArrayList<AccountMasterModel> detailAc;

    public AccountMasterView(int fromCD) {
        initComponents();
        accountAPI = lb.getRetrofit().create(AccountMasterAPI.class);
        groupAPI = lb.getRetrofit().create(GroupMasterAPI.class);
        connectToNavigation();
        searchOnTextFields();
        dtm = (DefaultTableModel) jTable1.getModel();
        getData();
        if (jTable1.getRowCount() > 0) {
            jTable1.requestFocusInWindow();
            jTable1.setRowSelectionInterval(0, 0);
        }
        navLoad.setFormCd(fromCD);
    }

    public void getData() {
        Call<JsonObject> call = groupAPI.GetGroupMaster();
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(AccountMasterView.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        TypeToken<List<GroupMasterModel>> token = new TypeToken<List<GroupMasterModel>>() {
                        };
                        detail = new Gson().fromJson(result.get("data"), token.getType());
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(AccountMasterView.this);
            }
        });
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

//    public SeriesAPI getSeriesAPI() {
//        return seriesAPI;
//    }
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

    private void showDailogeAdd() {
        AccountMasterController bmc = new AccountMasterController(null, true, this);
        if (jTable1.getSelectedRow() != -1) {
            bmc.getData(detailAc.get(Integer.parseInt(jTable1.getValueAt(jTable1.getSelectedRow(), 8).toString())));
        }
        bmc.setLocationRelativeTo(null);
        bmc.setVisible(true);
    }

    private void connectToNavigation() {
        class navigation extends SmallNavigation {

            @Override
            public void callNew() {
                if (navLoad.getModel().getADDS().equalsIgnoreCase("1")) {
                    navLoad.setMode("N");
                    jTable1.clearSelection();
                    showDailogeAdd();
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
                        lb.confirmDialog("Do you want to Edit This Series?");
                        if (lb.type) {
                            showDailogeAdd();
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
                                    lb.removeGlassPane(AccountMasterView.this);
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

    public void getAccountData() {
        Call<JsonObject> call = accountAPI.getSeriesMaster(jTextField1.getText());
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(AccountMasterView.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        TypeToken<List<AccountMasterModel>> token = new TypeToken<List<AccountMasterModel>>() {
                        };
                        detailAc = new Gson().fromJson(result.get("data"), token.getType());
                        dtm.setRowCount(0);
                        for (int i = 0; i < detailAc.size(); i++) {
                            Vector row = new Vector();
                            row.add(detailAc.get(i).getAC_CD());
                            row.add(detailAc.get(i).getFNAME());
                            row.add(detailAc.get(i).getGROUP_NAME());
                            row.add(detailAc.get(i).getMOBILE1());
                            row.add(detailAc.get(i).getCST());
                            row.add(detailAc.get(i).getTIN());
                            row.add(detailAc.get(i).getADD1());
                            row.add(detailAc.get(i).getEMAIL());
                            row.add(i);
                            dtm.addRow(row);
                        }
                        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(AccountMasterView.this);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        panel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ac_cd", "Name", "Group", "Mobile", "CST", "TIN", "Address", "Email", "index"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(8).setMinWidth(0);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(8).setMaxWidth(0);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panel.setLayout(new java.awt.BorderLayout());

        jButton1.setText("View");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
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
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(76, 76, 76))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (!lb.isBlank(jTextField1)) {
            getAccountData();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jButton1KeyPressed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jButton1);
    }//GEN-LAST:event_jTextField1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
