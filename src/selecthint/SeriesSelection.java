/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selecthint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import support.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.SeriesHead;
import model.SeriesMaster;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.StartUpAPI;
import skable.SkableHome;

/**
 *
 * @author bhaumik
 */
public class SeriesSelection extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;
    private DefaultTableModel dtmHeader = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    /**
     * Creates new form SelectDailog
     */
    public SeriesSelection(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
        setClickListner();
        setLocationRelativeTo(null);
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

    private void setClickListner() {
        
        searchOnTextFields();
        makeViewRoutine();
        this.jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    jtfFilter.setText("");
                    int row = jTable1.getSelectedRow();
                    if (row != -1) {
                        jbtnSave.doClick();
                    }
                }
            }
        });
        this.jTable1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jtfFilter.setText("");
                    int row = jTable1.getSelectedRow();
                    if (row != -1) {
                        jbtnSave.doClick();
                    }
                }
            }
        });
    }

    public DefaultTableModel getDtmHeader() {
        return dtmHeader;
    }

    public void setDtmHeader(DefaultTableModel dtmHeader) {
        this.dtmHeader = dtmHeader;
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private void makeViewRoutine() {


        dtmHeader = (DefaultTableModel) jTable1.getModel();

        InputMap im = jTable1.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");


    }

    public JTable getjTable1() {
        return jTable1;
    }
    
    

    public void setSeriesData(String param_cd, String value) {
        
        final Library lb = Library.getInstance();
        try {

            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        SeriesHead header = (SeriesHead) new Gson().fromJson(response.body(), SeriesHead.class);
                        if (header.getResult() == 1) {
                            ArrayList<SeriesMaster> series = (ArrayList<SeriesMaster>) header.getAccountHeader();
                            getDtmHeader().setRowCount(0);
                            for (int i = 0; i < series.size(); i++) {
                                Vector row = new Vector();
                                row.add(series.get(i).getSRCD());
                                row.add(series.get(i).getSRNAME());
                                row.add(series.get(i).getTAXCD());
                                row.add(series.get(i).getTAXNAME());
                                row.add(series.get(i).getGSTCD());
                                row.add(series.get(i).getGSTNAME());
                                row.add(series.get(i).getSRALIAS());
                                row.add(series.get(i).getHSNCODE());
                                getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(jTable1, jPanelHeader.getWidth());
                            setVisible(true);
                        } else {
                            lb.showMessageDailog(header.getCause());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                }
            });
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

        jPanelHeader = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jbtnSave = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        panel = new javax.swing.JPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanelHeader.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelHeader.setPreferredSize(new java.awt.Dimension(750, 420));
        jPanelHeader.setLayout(new javax.swing.BoxLayout(jPanelHeader, javax.swing.BoxLayout.LINE_AXIS));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "sr_code", "Item Name", "Tax Code", "Tax Name", "Gst Code", "Gst Name", "SKU Code", "Hsn Code"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(2).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(3).setResizable(false);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(4).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(5).setResizable(false);
        jTable1.getColumnModel().getColumn(6).setResizable(false);
        jTable1.getColumnModel().getColumn(7).setResizable(false);

        jPanelHeader.add(jScrollPane1);

        jbtnSave.setText("OK");
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });
        jbtnSave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnSaveKeyPressed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, jbtnSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(jbtnSave))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jbtnSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnSaveKeyPressed
        Library.getInstance().enterClick(evt);
    }//GEN-LAST:event_jbtnSaveKeyPressed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    public javax.swing.JPanel jPanelHeader;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
