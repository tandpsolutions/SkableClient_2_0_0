/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package inventory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import model.TypeMasterModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.InventoryAPI;
import retrofitAPI.StartUpAPI;
import retrofitAPI.TypeAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;
import support.SelectDailog;
import transactionController.SelectAccount;

/**
 *
 * @author LENOVO
 */
public class DayWiseStockSummary extends javax.swing.JInternalFrame {

    /**
     * Creates new form StockValueStatement
     */
    Library lb = null;
    DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    private ReportTable viewTable = null;
    private String code;
    private TypeAPI typeAPI;
    private ArrayList<TypeMasterModel> typeList;

    public DayWiseStockSummary() {
        initComponents();
        lb = Library.getInstance();
        typeAPI = lb.getRetrofit().create(TypeAPI.class);
        getData();
        registerShortKeys();
        dtm = (DefaultTableModel) jTable1.getModel();
        searchOnTextFields();
        tableForView();
        setUpData();
        setPopUp();
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

    private void setUpData() {
        jComboBox2.removeAllItems();
        jComboBox2.addItem("All");
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox2.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
    }

    public void getData() {
        Call<JsonObject> call = typeAPI.getTypeMaster();
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(DayWiseStockSummary.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        TypeToken<List<TypeMasterModel>> token = new TypeToken<List<TypeMasterModel>>() {
                        };
                        typeList = new Gson().fromJson(result.get("data"), token.getType());
                        jcmbType.removeAllItems();
                        jcmbType.addItem("ALL");
                        for (int i = 0; i < typeList.size(); i++) {
                            jcmbType.addItem(typeList.get(i).getTYPE_NAME());
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
                lb.removeGlassPane(DayWiseStockSummary.this);
            }
        });
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

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Name", 120, java.lang.String.class, null, false);
        viewTable.makeTable();
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

    @Override
    public void dispose() {
        try {
            SkableHome.removeFromScreen(SkableHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
//            lb.printToLogFile("Exception at dispose at codeBinding", ex);
        }
    }

    public void callExcel() {
        try {
            ArrayList rows = new ArrayList();
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                ArrayList row = new ArrayList();
                row.add(jTable1.getValueAt(i, 0).toString());
                row.add(jTable1.getValueAt(i, 1).toString());
                row.add(jTable1.getValueAt(i, 2).toString());
                row.add(jTable1.getValueAt(i, 3).toString());
                row.add(jTable1.getValueAt(i, 4).toString());
                row.add(jTable1.getValueAt(i, 5).toString());
                row.add(jTable1.getValueAt(i, 6).toString());
                rows.add(row);
            }

            ArrayList header = new ArrayList();
            header.add("SR NO");
            header.add("Item Name");
            header.add("15");
            header.add("45");
            header.add("90");
            header.add("100");
            header.add("Branch Name");
            lb.exportToExcel("Day Wise Stock Summary", header, rows, "Day Wise Stock Summary");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
        }

    }

    private void setSeriesData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(DayWiseStockSummary.this);
                    if (response.isSuccessful()) {
                        System.out.println(response.body().toString());
                        SeriesHead header = (SeriesHead) new Gson().fromJson(response.body(), SeriesHead.class);
                        if (header.getResult() == 1) {
                            final SelectDailog sa = new SelectDailog(null, true);
                            sa.setData(viewTable);
                            sa.setLocationRelativeTo(null);
                            ArrayList<SeriesMaster> series = (ArrayList<SeriesMaster>) header.getAccountHeader();
                            sa.getDtmHeader().setRowCount(0);
                            for (int i = 0; i < series.size(); i++) {
                                Vector row = new Vector();
                                row.add(series.get(i).getSRCD());
                                row.add(series.get(i).getSRNAME());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    code = viewTable.getValueAt(row, 0).toString();
                                    jtxtSeriesName.setText(viewTable.getValueAt(row, 1).toString());
                                    jtxtFromDate.requestFocusInWindow();
                                }
                                sa.dispose();
                            }
                        } else {
                            lb.showMessageDailog(header.getCause().toString());
                        }
                    } else {
                        // handle request errors yourself
                        lb.showMessageDailog(response.message());
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                    lb.removeGlassPane(DayWiseStockSummary.this);
                }
            }
            );
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void setBrandData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(DayWiseStockSummary.this);
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
                                row.add(array.get(i).getAsJsonObject().get("BRAND_CD").getAsString());
                                row.add(array.get(i).getAsJsonObject().get("BRAND_NAME").getAsString());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    code = viewTable.getValueAt(row, 0).toString();
                                    if (jRadioButton2.isSelected()) {
                                        jtxtBrandName.setText(viewTable.getValueAt(row, 1).toString());
                                        jButton1.requestFocusInWindow();
                                    }
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
                    lb.removeGlassPane(DayWiseStockSummary.this);
                }
            }
            );
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void setModelData(String param_cd, String value) {
        try {
            Call<JsonObject> call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase());
            lb.addGlassPane(this);
            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    lb.removeGlassPane(DayWiseStockSummary.this);
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
                                row.add(array.get(i).getAsJsonObject().get("MODEL_CD").getAsString());
                                row.add(array.get(i).getAsJsonObject().get("MODEL_NAME").getAsString());
                                sa.getDtmHeader().addRow(row);
                            }
                            lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                            sa.setVisible(true);
                            if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                                int row = viewTable.getSelectedRow();
                                if (row != -1) {
                                    code = viewTable.getValueAt(row, 0).toString();
                                    jtxtModelName.setText(viewTable.getValueAt(row, 1).toString());
                                    jButton1.requestFocusInWindow();
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
                    lb.removeGlassPane(DayWiseStockSummary.this);
                }
            }
            );
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
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        panel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jtxtBrandName = new javax.swing.JTextField();
        jbtnClose = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jtxtSeriesName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jcmbType = new javax.swing.JComboBox();
        jRadioButton4 = new javax.swing.JRadioButton();
        jtxtModelName = new javax.swing.JTextField();
        jRadioButton5 = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jtxtFromDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jtxtFromDate1 = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jtxtRate = new javax.swing.JTextField();
        jRadioButton10 = new javax.swing.JRadioButton();
        jtxtRate2 = new javax.swing.JTextField();
        jRadioButton11 = new javax.swing.JRadioButton();
        jtxtFromDate2 = new javax.swing.JTextField();
        jBillDateBtn2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SR", "Item Name", "15", "45", "90", "100", "Branch"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panel.setLayout(new java.awt.BorderLayout());

        jButton1.setText("View ");
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

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Brand Wise");
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Item Name");
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });

        jtxtBrandName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBrandNameKeyPressed(evt);
            }
        });

        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        jButton4.setText("Excel");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jtxtSeriesName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSeriesNameKeyPressed(evt);
            }
        });

        jLabel5.setText("Type");

        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("Model Name");

        jtxtModelName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtModelNameFocusLost(evt);
            }
        });
        jtxtModelName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtModelNameKeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("ALL");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Godown", "Shop", "All" }));

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("Date");

        buttonGroup2.add(jRadioButton6);
        jRadioButton6.setText("<");

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

        buttonGroup2.add(jRadioButton7);
        jRadioButton7.setText(">");

        jtxtFromDate1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFromDate1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFromDate1FocusLost(evt);
            }
        });
        jtxtFromDate1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDate1KeyPressed(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton8);
        jRadioButton8.setSelected(true);
        jRadioButton8.setText("Rate");

        buttonGroup3.add(jRadioButton9);
        jRadioButton9.setText("<");

        jtxtRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRateFocusLost(evt);
            }
        });
        jtxtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRateKeyPressed(evt);
            }
        });

        buttonGroup3.add(jRadioButton10);
        jRadioButton10.setText(">=");

        jtxtRate2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRate2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRate2FocusLost(evt);
            }
        });
        jtxtRate2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRate2KeyPressed(evt);
            }
        });

        buttonGroup2.add(jRadioButton11);
        jRadioButton11.setText("=");

        jtxtFromDate2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFromDate2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFromDate2FocusLost(evt);
            }
        });
        jtxtFromDate2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDate2KeyPressed(evt);
            }
        });

        jBillDateBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Branch");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(jRadioButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbType, 0, 233, Short.MAX_VALUE)
                    .addComponent(jtxtModelName)
                    .addComponent(jtxtSeriesName, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addComponent(jtxtBrandName, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jRadioButton3)
                                            .addComponent(jRadioButton8))
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jRadioButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                            .addComponent(jRadioButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jRadioButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jtxtRate2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jRadioButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jtxtFromDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtFromDate2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jBillDateBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jRadioButton1, jRadioButton2, jRadioButton4});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jcmbType, jtxtBrandName, jtxtModelName, jtxtSeriesName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jRadioButton3, jRadioButton8});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtFromDate, jtxtRate});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtFromDate1, jtxtRate2});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton4)
                        .addComponent(jbtnClose))
                    .addComponent(jRadioButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtSeriesName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton11)
                    .addComponent(jtxtFromDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jRadioButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton5))
                            .addComponent(jtxtModelName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton3)
                                    .addComponent(jRadioButton6)
                                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton7)
                                    .addComponent(jtxtFromDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton10)
                                    .addComponent(jtxtRate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton8)
                                    .addComponent(jRadioButton9)
                                    .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton2, jtxtSeriesName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton1, jtxtBrandName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jBillDateBtn1, jRadioButton3, jRadioButton6, jRadioButton7, jtxtFromDate, jtxtFromDate1});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton10, jRadioButton8, jRadioButton9, jtxtRate, jtxtRate2});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox2, jLabel3, jLabel5, jcmbType});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        // TODO add your handling code here:
        jtxtBrandName.setEnabled(jRadioButton1.isSelected());
        jtxtSeriesName.setEnabled(jRadioButton2.isSelected());
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
        // TODO add your handling code here:
        jtxtBrandName.setEnabled(jRadioButton1.isSelected());
        jtxtSeriesName.setEnabled(jRadioButton2.isSelected());
    }//GEN-LAST:event_jRadioButton2ItemStateChanged

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class);
            String mode = "";
            if (jRadioButton1.isSelected()) {
                mode = "brand";
                jtxtModelName.setText("");
                jtxtSeriesName.setText("");
            } else if (jRadioButton2.isSelected()) {
                mode = "series";
                jtxtBrandName.setText("");
                jtxtModelName.setText("");
            } else if (jRadioButton4.isSelected()) {
                mode = "model";
                jtxtBrandName.setText("");
                jtxtSeriesName.setText("");
            }
            if (code == null) {
                code = "";
            }
            String type_CD = "";
            if (jcmbType.getSelectedIndex() != 0) {
                type_CD = typeList.get(jcmbType.getSelectedIndex() - 1).getTYPE_CD();
            }
            String before_date = lb.ConvertDateFormetForDB(jtxtFromDate.getText());
            String after_date = lb.ConvertDateFormetForDB(jtxtFromDate1.getText());
            String equal_date = lb.ConvertDateFormetForDB(jtxtFromDate2.getText());

            String date_mode = "";
            if (jRadioButton3.isSelected()) {
                date_mode = "1";
            }
            if (jRadioButton6.isSelected()) {
                date_mode = "2";
            }
            if (jRadioButton7.isSelected()) {
                date_mode = "3";
            }
            if (jRadioButton11.isSelected()) {
                date_mode = "4";
            }

            String before_rate = lb.Convert2DecFmtForRs(lb.isNumber(jtxtRate.getText()));
            String after_rate = lb.Convert2DecFmtForRs(lb.isNumber(jtxtRate2.getText()));

            String rate_mode = "";
            if (jRadioButton8.isSelected()) {
                rate_mode = "1";
            }
            if (jRadioButton9.isSelected()) {
                rate_mode = "2";
            }
            if (jRadioButton10.isSelected()) {
                rate_mode = "3";
            }
            lb.addGlassPane(this);
            JsonObject call = inventoryAPI.GetDayWiseStockSummary(code, mode, type_CD,
                    jComboBox1.getSelectedIndex() + "", before_date, after_date, equal_date,
                    date_mode, before_rate, after_rate, rate_mode,
                    ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_cd() : ""), "").execute().body();

            lb.removeGlassPane(DayWiseStockSummary.this);
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    dtm.setRowCount(0);
                    double tot15 = 0;
                    double tot45 = 0;
                    double tot90 = 0;
                    double tot100 = 0;
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(i + 1);
                        row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("day_15").getAsInt()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("day_45").getAsInt()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("day_90").getAsInt()));
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("day_100").getAsInt()));
                        row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name());
                        dtm.addRow(row);
                    }

                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        tot15 += lb.isNumber(jTable1.getValueAt(i, 2).toString());
                        tot45 += lb.isNumber(jTable1.getValueAt(i, 3).toString());
                        tot90 += lb.isNumber(jTable1.getValueAt(i, 4).toString());
                        tot100 += lb.isNumber(jTable1.getValueAt(i, 5).toString());
                    }

                    Vector row = new Vector();
                    row.add("");
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(tot15));
                    row.add(lb.Convert2DecFmtForRs(tot45));
                    row.add(lb.Convert2DecFmtForRs(tot90));
                    row.add(lb.Convert2DecFmtForRs(tot100));
                    row.add("");
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(DayWiseStockSummary.this);
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                    lb.removeGlassPane(DayWiseStockSummary.this);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DayWiseStockSummary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            // TODO add your handling code here:
            InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class);
            String mode = "";
            if (jRadioButton1.isSelected()) {
                mode = "brand";
                jtxtModelName.setText("");
                jtxtSeriesName.setText("");
            } else if (jRadioButton2.isSelected()) {
                mode = "series";
                jtxtBrandName.setText("");
                jtxtModelName.setText("");
            } else if (jRadioButton4.isSelected()) {
                mode = "model";
                jtxtBrandName.setText("");
                jtxtSeriesName.setText("");
            }
            if (code == null) {
                code = "";
            }
            String type_CD = "";
            if (jcmbType.getSelectedIndex() != 0) {
                type_CD = typeList.get(jcmbType.getSelectedIndex() - 1).getTYPE_CD();
            }
            String before_date = lb.ConvertDateFormetForDB(jtxtFromDate.getText());
            String after_date = lb.ConvertDateFormetForDB(jtxtFromDate1.getText());
            String equal_date = lb.ConvertDateFormetForDB(jtxtFromDate2.getText());

            String date_mode = "";
            if (jRadioButton3.isSelected()) {
                date_mode = "1";
            }
            if (jRadioButton6.isSelected()) {
                date_mode = "2";
            }
            if (jRadioButton7.isSelected()) {
                date_mode = "3";
            }
            if (jRadioButton11.isSelected()) {
                date_mode = "4";
            }

            String before_rate = lb.Convert2DecFmtForRs(lb.isNumber(jtxtRate.getText()));
            String after_rate = lb.Convert2DecFmtForRs(lb.isNumber(jtxtRate2.getText()));

            String rate_mode = "";
            if (jRadioButton8.isSelected()) {
                rate_mode = "1";
            }
            if (jRadioButton9.isSelected()) {
                rate_mode = "2";
            }
            if (jRadioButton10.isSelected()) {
                rate_mode = "3";
            }
            lb.addGlassPane(this);
            JsonObject call = inventoryAPI.GetDayWiseStockSummary(code, mode, type_CD,
                    jComboBox1.getSelectedIndex() + "", before_date, after_date, equal_date,
                    date_mode, before_rate, after_rate, rate_mode,
                    ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_cd() : ""), "").execute().body();

            lb.removeGlassPane(DayWiseStockSummary.this);
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    dtm.setRowCount(0);
                    double tot15 = 0;
                    double tot45 = 0;
                    double tot90 = 0;
                    double tot100 = 0;
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(i + 1);
                        row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("day_15").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("day_45").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("day_90").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("day_100").getAsString());
                        row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name());
                        dtm.addRow(row);
                    }

                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        tot15 += lb.isNumber(jTable1.getValueAt(i, 2).toString());
                        tot45 += lb.isNumber(jTable1.getValueAt(i, 3).toString());
                        tot90 += lb.isNumber(jTable1.getValueAt(i, 4).toString());
                        tot100 += lb.isNumber(jTable1.getValueAt(i, 5).toString());
                    }

                    Vector row = new Vector();
                    row.add("");
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(tot15));
                    row.add(lb.Convert2DecFmtForRs(tot45));
                    row.add(lb.Convert2DecFmtForRs(tot90));
                    row.add(lb.Convert2DecFmtForRs(tot100));
                    row.add("");
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(DayWiseStockSummary.this);
                    callExcel();
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                    lb.removeGlassPane(DayWiseStockSummary.this);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DayWiseStockSummary.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jtxtSeriesNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSeriesNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            setSeriesData("3", jtxtSeriesName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtSeriesNameKeyPressed

    private void jtxtBrandNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt) && jRadioButton1.isSelected()) {
            setBrandData("8", jtxtBrandName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtBrandNameKeyPressed

    private void jcmbTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTypeKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jcmbTypeKeyPressed

    private void jtxtModelNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtModelNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtModelNameFocusLost

    private void jtxtModelNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtModelNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            setModelData("12", jtxtModelName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtModelNameKeyPressed

    private void jtxtFromDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusGained
        // TODO add your handling code here:
        jtxtFromDate.selectAll();
    }//GEN-LAST:event_jtxtFromDateFocusGained

    private void jtxtFromDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusLost
        lb.checkDate(jtxtFromDate);
    }//GEN-LAST:event_jtxtFromDateFocusLost

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (lb.checkDate(jtxtFromDate)) {
                jButton1.requestFocusInWindow();
            } else {

                jtxtFromDate.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFromDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFromDate.getX(), jtxtFromDate.getY() + 125, jtxtFromDate.getX() + odc.getWidth(), jtxtFromDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jtxtFromDate1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDate1FocusGained
        // TODO add your handling code here:
        jtxtFromDate.selectAll();
    }//GEN-LAST:event_jtxtFromDate1FocusGained

    private void jtxtFromDate1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDate1FocusLost
        // TODO add your handling code here:
        lb.checkDate(jtxtFromDate1);
    }//GEN-LAST:event_jtxtFromDate1FocusLost

    private void jtxtFromDate1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDate1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (lb.checkDate(jtxtFromDate1)) {
                jButton1.requestFocusInWindow();
            } else {

                jtxtFromDate1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtFromDate1KeyPressed

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFromDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFromDate1.getX(), jtxtFromDate1.getY() + 125, jtxtFromDate1.getX() + odc.getWidth(), jtxtFromDate1.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        // TODO add your handling code here:
        lb.enterClick(evt);
    }//GEN-LAST:event_jButton1KeyPressed

    private void jtxtRateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateFocusGained

    private void jtxtRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtRateFocusLost

    private void jtxtRateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateKeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jButton1);
    }//GEN-LAST:event_jtxtRateKeyPressed

    private void jtxtRate2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRate2FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRate2FocusGained

    private void jtxtRate2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRate2FocusLost
        // TODO add your handling code here:
        lb.toDouble(evt);
    }//GEN-LAST:event_jtxtRate2FocusLost

    private void jtxtRate2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRate2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jButton1);
    }//GEN-LAST:event_jtxtRate2KeyPressed

    private void jtxtFromDate2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDate2FocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFromDate2FocusGained

    private void jtxtFromDate2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDate2FocusLost
        // TODO add your handling code here:
        lb.checkDate(jtxtFromDate2);
    }//GEN-LAST:event_jtxtFromDate2FocusLost

    private void jtxtFromDate2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDate2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (lb.checkDate(jtxtFromDate2)) {
                jButton1.requestFocusInWindow();
            } else {

                jtxtFromDate2.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtFromDate2KeyPressed

    private void jBillDateBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn2ActionPerformed
        // TODO add your handling code here:
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFromDate2);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFromDate2.getX(), jtxtFromDate2.getY() + 125, jtxtFromDate2.getX() + odc.getWidth(), jtxtFromDate2.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, "Select Date");
    }//GEN-LAST:event_jBillDateBtn2ActionPerformed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jButton1);
    }//GEN-LAST:event_jComboBox2KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JButton jBillDateBtn2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JTextField jtxtBrandName;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtFromDate1;
    private javax.swing.JTextField jtxtFromDate2;
    private javax.swing.JTextField jtxtModelName;
    private javax.swing.JTextField jtxtRate;
    private javax.swing.JTextField jtxtRate2;
    private javax.swing.JTextField jtxtSeriesName;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
