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
import masterController.TypeMasterController;
import model.TypeMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.TypeAPI;
import skable.SkableHome;
import support.Library;
import support.SmallNavigation;

public class TypeMasterView extends javax.swing.JInternalFrame {

    private DefaultTableModel dtm = null;
    private SmallNavigation navLoad = null;
    private final Library lb = Library.getInstance();
    private TypeAPI typeAPI;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    public TypeMasterView(int formCd) {
        initComponents();
        typeAPI = lb.getRetrofit().create(TypeAPI.class);
        connectToNavigation();
        searchOnTextFields();
        dtm = (DefaultTableModel) jTable1.getModel();
        getData();
        if (jTable1.getRowCount() > 0) {
            jTable1.requestFocusInWindow();
            jTable1.setRowSelectionInterval(0, 0);
        }
        navLoad.setFormCd(formCd);
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

    public TypeAPI getTypeAPI() {
        return typeAPI;
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

    private void showDailogeAdd(String type_cd, String type_name) {
        TypeMasterController bmc = new TypeMasterController(null, true, this, type_cd, type_name);
        bmc.setLocationRelativeTo(null);
        bmc.show();
    }

    public void addRow(String type_cd, String type_name) {
        int selRow = jTable1.getSelectedRow();
        if (selRow == -1) {
            Vector row = new Vector();
            row.add(type_cd);
            row.add(type_name);
            dtm.addRow(row);
        } else {
            jTable1.setValueAt(type_cd, selRow, 0);
            jTable1.setValueAt(type_name, selRow, 1);
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
                        lb.confirmDialog("Do you want to Edit This Type?");
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
                                    lb.removeGlassPane(TypeMasterView.this);
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
        Call<JsonObject> call = typeAPI.getTypeMaster();
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(TypeMasterView.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        TypeToken<List<TypeMasterModel>> token = new TypeToken<List<TypeMasterModel>>() {
                        };
                        ArrayList<TypeMasterModel> detail = new Gson().fromJson(result.get("data"), token.getType());
                        dtm.setRowCount(0);
                        for (int i = 0; i < detail.size(); i++) {
                            Vector row = new Vector();
                            row.add(detail.get(i).getTYPE_CD());
                            row.add(detail.get(i).getTYPE_NAME());
                            dtm.addRow(row);
                        }
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(TypeMasterView.this);
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

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "type_cd", "Type Name"
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
