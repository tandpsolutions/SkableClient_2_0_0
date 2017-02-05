/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import retrofitAPI.AccountAPI;
import retrofitAPI.StartUpAPI;
import retrofitAPI.TypeAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.OurDateChooser;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author LENOVO
 */
public class MarginReport extends javax.swing.JInternalFrame {

    /**
     * Creates new form StockValueStatement
     */
    Library lb = null;
    DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    private ReportTable viewTable = null;
    private String sr_cd = "";
    private String brand_cd;
    private String model_cd = "";
    private ArrayList<TypeMasterModel> typeList;
    private TypeAPI typeAPI;

    public MarginReport() {
        initComponents();
        registerShortKeys();
        lb = Library.getInstance();
        typeAPI = lb.getRetrofit().create(TypeAPI.class);
        try {
            getData();
        } catch (IOException ex) {
            lb.printToLogFile("Exception at get Type Data", ex);
        }
        setUpData();
        dtm = (DefaultTableModel) jTable1.getModel();
        lb = Library.getInstance();
        lb.setDateChooserPropertyInit(jtxtFromDate);
        lb.setDateChooserPropertyInit(jtxtToDate);
        searchOnTextFields();
        tableForView();
    }

    private void getData() throws IOException {
        JsonObject call = typeAPI.getTypeMaster().execute().body();

        if (call != null) {
            JsonObject result = call;
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
                lb.showMessageDailog(call.get("Cause").getAsString());
            }
        }
    }

    private void setUpData() {
        jComboBox2.removeAllItems();
        jComboBox2.addItem("All");
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox2.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
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

    private void setSeriesData(String param_cd, String value) {
        try {
            JsonObject call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase()).execute().body();
            lb.addGlassPane(this);
            if (call != null) {
                System.out.println(call.toString());
                SeriesHead header = (SeriesHead) new Gson().fromJson(call, SeriesHead.class);
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
                        row.add(series.get(i).getTAXCD());
                        row.add(series.get(i).getTAXNAME());
                        sa.getDtmHeader().addRow(row);
                    }
                    lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                    sa.setVisible(true);
                    if (sa.getReturnStatus() == SelectDailog.RET_OK) {

                        int row = viewTable.getSelectedRow();
                        if (row != -1) {
                            sr_cd = viewTable.getValueAt(row, 0).toString();
                            jtxtProductName.setText(viewTable.getValueAt(row, 1).toString());
                            jbtnView.requestFocusInWindow();
                        }
                    }
                    sa.dispose();
                } else {
                    lb.showMessageDailog(header.getCause());
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setData at account master in sales invoice", ex);
        }

    }

    private void setBrandData(String param_cd, String value) {
        try {
            JsonObject call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase()).execute().body();

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
                        row.add(array.get(i).getAsJsonObject().get("BRAND_CD").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("BRAND_NAME").getAsString());
                        sa.getDtmHeader().addRow(row);
                    }
                    lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                    sa.setVisible(true);
                    if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                        int row = viewTable.getSelectedRow();
                        if (row != -1) {
                            brand_cd = viewTable.getValueAt(row, 0).toString();
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

    private void setModelData(String param_cd, String value) {
        try {
            JsonObject call = lb.getRetrofit().create(StartUpAPI.class).getDataFromServer(param_cd, value.toUpperCase()).execute().body();

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
                        row.add(array.get(i).getAsJsonObject().get("MODEL_CD").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("MODEL_NAME").getAsString());
                        sa.getDtmHeader().addRow(row);
                    }
                    lb.setColumnSizeForTable(viewTable, sa.jPanelHeader.getWidth());
                    sa.setVisible(true);
                    if (sa.getReturnStatus() == SelectDailog.RET_OK) {
                        int row = viewTable.getSelectedRow();
                        if (row != -1) {
                            model_cd = viewTable.getValueAt(row, 0).toString();
                            jtxtModelName.setText(viewTable.getValueAt(row, 1).toString());
                            jbtnView.requestFocusInWindow();
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
                row.add(jTable1.getValueAt(i, 7).toString());
                rows.add(row);
            }

            ArrayList header = new ArrayList();
            header.add("IMEI NO");
            header.add("Item Name");
            header.add("Purchase Rate");
            header.add("Sales Rate");
            header.add("Profit");
            header.add("Branch");
            lb.exportToExcel("Margin Report", header, rows, "Margin Report");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at callView as OPDPatientListDateWise", ex);
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
        jtxtToDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        jBillDateBtn = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jtxtFromDate = new javax.swing.JTextField();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jcmbType = new javax.swing.JComboBox();
        jtxtModelName = new javax.swing.JTextField();
        jtxtBrandName = new javax.swing.JTextField();
        jtxtProductName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IMEI Name", "Item Name", "Rate", "Sale Rate", "Profit", "Pur Ref", "Sale Ref", "Branch"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(6).setMinWidth(0);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panel.setLayout(new java.awt.BorderLayout());

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

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jbtnView.setText("View ");
        jbtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewActionPerformed(evt);
            }
        });

        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setSelected(true);
        jRadioButton4.setText("All");

        jLabel4.setText("To Date");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText(">0");

        jLabel3.setText("From Date");

        jButton4.setText("Excel");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

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

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("=0");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("<0");

        buttonGroup3.add(jRadioButton5);
        jRadioButton5.setText("Product Name");

        buttonGroup3.add(jRadioButton6);
        jRadioButton6.setText("Brand Name");

        buttonGroup3.add(jRadioButton7);
        jRadioButton7.setText("Model Name");

        buttonGroup3.add(jRadioButton8);
        jRadioButton8.setSelected(true);
        jRadioButton8.setText("All");

        jLabel5.setText("Type");

        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

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

        jtxtProductName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtProductNameFocusGained(evt);
            }
        });
        jtxtProductName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtProductNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtProductNameKeyReleased(evt);
            }
        });

        jLabel1.setText("Type");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Retail", "Tax" }));

        jLabel6.setText("Branch");

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
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jRadioButton4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton2)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton3))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(94, 94, 94)
                                .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtModelName, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jtxtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtBrandName, jtxtModelName, jtxtProductName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel6});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jComboBox1, jComboBox2});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jbtnClose)
                        .addComponent(jButton4))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jBillDateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jBillDateBtn1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButton5)
                    .addComponent(jtxtProductName, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton6)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButton7)
                            .addComponent(jtxtModelName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel1, jRadioButton5, jtxtProductName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        try {
            // TODO add your handling code here:
            AccountAPI accountAPI = lb.getRetrofit().create(AccountAPI.class);
            String mode = "";
            if (jRadioButton1.isSelected()) {
                mode = "1";
            }

            if (jRadioButton2.isSelected()) {
                mode = "2";
            }

            if (jRadioButton3.isSelected()) {
                mode = "3";
            }

            if (jRadioButton8.isSelected()) {
                sr_cd = "";
                brand_cd = "";
                model_cd = "";
                jtxtBrandName.setText("");
                jtxtProductName.setText("");
                jtxtModelName.setText("");
            } else if (jRadioButton5.isSelected()) {
                brand_cd = "";
                model_cd = "";
                jtxtBrandName.setText("");
                jtxtModelName.setText("");
            } else if (jRadioButton6.isSelected()) {
                sr_cd = "";
                model_cd = "";
                jtxtProductName.setText("");
                jtxtModelName.setText("");
            } else if (jRadioButton7.isSelected()) {
                sr_cd = "";
                brand_cd = "";
                jtxtBrandName.setText("");
                jtxtProductName.setText("");
            }
            JsonObject call = accountAPI.MarginReport(lb.ConvertDateFormetForDB(jtxtFromDate.getText()),
                    lb.ConvertDateFormetForDB(jtxtToDate.getText()), mode, sr_cd, brand_cd, model_cd,
                    ((jcmbType.getSelectedIndex() > 0) ? typeList.get(jcmbType.getSelectedIndex() - 1).getTYPE_CD() : ""),
                    jComboBox1.getSelectedIndex(),
                    ((jComboBox2.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox2.getSelectedIndex() - 1).getBranch_cd() : "0")).execute().body();

            lb.addGlassPane(this);

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    System.out.println(array);
                    dtm.setRowCount(0);
                    double tot = 0;
                    double pur = 0;
                    double sal = 0;
                    for (int i = 0; i < array.size(); i++) {
                        Vector row = new Vector();
                        row.add(array.get(i).getAsJsonObject().get("TAG_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("PUR_RATE").getAsDouble());
                        row.add(array.get(i).getAsJsonObject().get("SALE_RATE").getAsDouble());
                        row.add(lb.Convert2DecFmtForRs(array.get(i).getAsJsonObject().get("SALE_RATE").getAsDouble() - array.get(i).getAsJsonObject().get("PUR_RATE").getAsDouble()));
                        row.add(array.get(i).getAsJsonObject().get("PUR_REF_NO").getAsString());
                        row.add(array.get(i).getAsJsonObject().get("SALE_REF_NO").getAsString());
                        row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name());
                        dtm.addRow(row);
                    }

                    for (int i = 0; i < jTable1.getRowCount(); i++) {
                        pur += lb.isNumber(jTable1.getValueAt(i, 2).toString());
                        sal += lb.isNumber(jTable1.getValueAt(i, 3).toString());
                        tot += lb.isNumber(jTable1.getValueAt(i, 4).toString());
                    }

                    Vector row = new Vector();
                    row.add("");
                    row.add("");
                    row.add(lb.Convert2DecFmtForRs(pur));
                    row.add(lb.Convert2DecFmtForRs(sal));
                    row.add(lb.Convert2DecFmtForRs(tot));
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    dtm.addRow(row);

                    lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                    lb.removeGlassPane(MarginReport.this);
                } else {
                    lb.removeGlassPane(MarginReport.this);
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            lb.printToLogFile("Exception at get Data From Server", ex);
        }
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        callExcel();
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
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

    private void jcmbTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTypeKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jbtnView.requestFocusInWindow();
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

    private void jtxtBrandNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBrandNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBrandNameFocusGained

    private void jtxtBrandNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            setBrandData("8", jtxtBrandName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtBrandNameKeyPressed

    private void jtxtBrandNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtBrandNameKeyReleased

    private void jtxtProductNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtProductNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtProductNameFocusGained

    private void jtxtProductNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtProductNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            setSeriesData("3", jtxtProductName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtProductNameKeyPressed

    private void jtxtProductNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtProductNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtProductNameKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                int column = jTable1.getSelectedColumn();
                if (column == 2) {
                    lb.openVoucherBook(jTable1.getValueAt(row, 5).toString());
                } else if (column == 3) {
                    lb.openVoucherBook(jTable1.getValueAt(row, 6).toString());
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnView);
    }//GEN-LAST:event_jComboBox2KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnView;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JTextField jtxtBrandName;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtModelName;
    private javax.swing.JTextField jtxtProductName;
    private javax.swing.JTextField jtxtToDate;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
