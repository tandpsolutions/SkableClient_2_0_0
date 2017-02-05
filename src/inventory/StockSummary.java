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
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
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
import model.SeriesHead;
import model.SeriesMaster;
import model.TypeMasterModel;
import net.sf.jasperreports.engine.data.JsonDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofitAPI.InventoryAPI;
import retrofitAPI.StartUpAPI;
import retrofitAPI.TypeAPI;
import skable.Constants;
import skable.SkableHome;
import support.Library;
import support.ReportTable;
import support.SelectDailog;

/**
 *
 * @author nice
 */
public class StockSummary extends javax.swing.JInternalFrame {

    /**
     * Creates new form salesRegisterSummary
     */
    private Library lb = Library.getInstance();
    private DefaultTableModel dtm = null;
    private ReportTable viewTable = null;
    private String sr_cd;
    private String code;
    private String model_cd = "";
    private ArrayList<TypeMasterModel> typeList;
    private TypeAPI typeAPI;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();
    private JPopupMenu popup = new JPopupMenu();

    public StockSummary() {
        initComponents();
        typeAPI = lb.getRetrofit().create(TypeAPI.class);
        getData();
        dtm = (DefaultTableModel) jTable1.getModel();
        registerShortKeys();
        setUpData();
//        jTable1.getColumnModel().getColumn(1).setCellRenderer(new StatusColumnCellRenderer(0, 2));
//        jTable1.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer(1, 0));
//        jTable1.getColumnModel().getColumn(3).setCellRenderer(new StatusColumnCellRenderer(2, 1));
//        jTable1.getColumnModel().getColumn(4).setCellRenderer(new StatusColumnCellRenderer(3, 2));
        tableForView();
        searchOnTextFields();
    }

    private void setUpData() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem("All");
        for (int i = 0; i < Constants.BRANCH.size(); i++) {
            jComboBox1.addItem(Constants.BRANCH.get(i).getBranch_name());
        }
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Item Code", 120, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Item Name", 120, java.lang.String.class, null, false);
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

    public void getData() {
        Call<JsonObject> call = typeAPI.getTypeMaster();
        lb.addGlassPane(this);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(StockSummary.this);
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
                lb.removeGlassPane(StockSummary.this);
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

    private void jbtnViewActionPerformedRoutine() {
        InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class);
        if (jRadioButton3.isSelected()) {
            sr_cd = "";
            code = "";
            model_cd = "";
            jtxtBrandName.setText("");
            jtxtModelName.setText("");
            jtxtProductName.setText("");
        } else if (jRadioButton1.isSelected()) {
            code = "";
            model_cd = "";
            jtxtBrandName.setText("");
            jtxtModelName.setText("");

        } else if (jRadioButton2.isSelected()) {
            sr_cd = "";
            model_cd = "";

            jtxtModelName.setText("");
            jtxtProductName.setText("");
        } else if (jRadioButton4.isSelected()) {
            sr_cd = "";
            code = "";
            jtxtBrandName.setText("");
            jtxtProductName.setText("");
        }
        lb.addGlassPane(this);
        Call<JsonObject> call = inventoryAPI.GetStockSummary(sr_cd, ((jcmbType.getSelectedIndex() > 0) ? typeList.get(jcmbType.getSelectedIndex() - 1).getTYPE_CD() : ""), code,
                jCheckBox1.isSelected(), model_cd, false,
                ((jComboBox1.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox1.getSelectedIndex() - 1).getBranch_cd() : ""));

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(StockSummary.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        dtm.setRowCount(0);
                        double opb = 0.00, pur = 0.00, sal = 0.00, stock = 0.00;
                        for (int i = 0; i < array.size(); i++) {
                            Vector row = new Vector();

                            row.add(array.get(i).getAsJsonObject().get("MODEL_NAME").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("OPB").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("SALES").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("SR_ALIAS").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                            row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name());
                            if (jCheckBox1.isSelected()) {
                                if ((array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble()) < 0) {
                                    dtm.addRow(row);
                                    opb += array.get(i).getAsJsonObject().get("OPB").getAsDouble();
                                    pur += array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble();
                                    sal += array.get(i).getAsJsonObject().get("SALES").getAsDouble();
                                    stock += array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble();
                                }
                            } else {
//                    if ((array.get(i).getAsJsonObject().get("OPB") + array.get(i).getAsJsonObject().get("PURCHASE") - array.get(i).getAsJsonObject().get("SALES")) != 0) {
                                dtm.addRow(row);
                                opb += array.get(i).getAsJsonObject().get("OPB").getAsDouble();
                                pur += array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble();
                                sal += array.get(i).getAsJsonObject().get("SALES").getAsDouble();
                                stock += array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble();
//                    }
                            }
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
                        dtm.addRow(row);

                        row = new Vector();
                        row.add("Total");
                        row.add(" ");
                        row.add(opb);
                        row.add(pur);
                        row.add(sal);
                        row.add(stock);
                        row.add(" ");
                        row.add(" ");
                        dtm.addRow(row);

                        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                        lb.removeGlassPane(StockSummary.this);
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                        lb.removeGlassPane(StockSummary.this);
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                    lb.removeGlassPane(StockSummary.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(StockSummary.this);
            }
        }
        );
    }

    private void jbtnPreviewActionPerformedRoutine() {
        InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class
        );
        if (jRadioButton3.isSelected()) {
            sr_cd = "";
            code = "";
            model_cd = "";
            jtxtBrandName.setText("");
            jtxtModelName.setText("");
            jtxtProductName.setText("");
        } else if (jRadioButton1.isSelected()) {
            code = "";
            model_cd = "";
            jtxtBrandName.setText("");
            jtxtModelName.setText("");

        } else if (jRadioButton2.isSelected()) {
            sr_cd = "";
            model_cd = "";

            jtxtModelName.setText("");
            jtxtProductName.setText("");
        } else if (jRadioButton4.isSelected()) {
            sr_cd = "";
            code = "";
            jtxtBrandName.setText("");
            jtxtProductName.setText("");
        }
        lb.addGlassPane(this);
        Call<JsonObject> call = inventoryAPI.GetStockSummary(sr_cd, ((jcmbType.getSelectedIndex() > 0) ? typeList.get(jcmbType.getSelectedIndex() - 1).getTYPE_CD() : ""), code,
                jCheckBox1.isSelected(), model_cd, false,
                ((jComboBox1.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox1.getSelectedIndex() - 1).getBranch_cd() : ""));
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(StockSummary.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        if (array != null) {
                            try {
                                FileWriter file = new FileWriter(System.getProperty("user.dir") + File.separator + "file1.txt");
                                file.write(array.toString());
                                file.close();
                                File jsonFile = new File(System.getProperty("user.dir") + File.separator + "file1.txt");
                                JsonDataSource dataSource = new JsonDataSource(jsonFile);
                                HashMap params = new HashMap();
                                params.put("dir", System.getProperty("user.dir"));
                                lb.reportGenerator("StockSummary.jasper", params, dataSource, jPanel1);
                                lb.removeGlassPane(StockSummary.this);
                            } catch (Exception ex) {
                                lb.removeGlassPane(StockSummary.this);
                            }
                            lb.removeGlassPane(StockSummary.this);
                        } else {
                            lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                            lb.removeGlassPane(StockSummary.this);
                        }
                    } else {
                        lb.showMessageDailog(rspns.message());
                        lb.removeGlassPane(StockSummary.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(StockSummary.this);
            }
        }
        );
    }

    private void jbtnExcelActionPerformedRoutine() {
        InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class
        );
        if (jRadioButton3.isSelected()) {
            sr_cd = "";
            code = "";
            model_cd = "";
            jtxtBrandName.setText("");
            jtxtModelName.setText("");
            jtxtProductName.setText("");
        } else if (jRadioButton1.isSelected()) {
            code = "";
            model_cd = "";
            jtxtBrandName.setText("");
            jtxtModelName.setText("");

        } else if (jRadioButton2.isSelected()) {
            sr_cd = "";
            model_cd = "";

            jtxtModelName.setText("");
            jtxtProductName.setText("");
        } else if (jRadioButton4.isSelected()) {
            sr_cd = "";
            code = "";
            jtxtBrandName.setText("");
            jtxtProductName.setText("");
        }
        lb.addGlassPane(this);
        Call<JsonObject> call = inventoryAPI.GetStockSummary(sr_cd, ((jcmbType.getSelectedIndex() > 0) ? typeList.get(jcmbType.getSelectedIndex() - 1).getTYPE_CD() : ""), code,
                jCheckBox1.isSelected(), model_cd, false,
                ((jComboBox1.getSelectedIndex() > 0) ? Constants.BRANCH.get(jComboBox1.getSelectedIndex() - 1).getBranch_cd() : ""));
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> rspns) {
                lb.removeGlassPane(StockSummary.this);
                if (rspns.isSuccessful()) {
                    JsonObject result = rspns.body();
                    if (result.get("result").getAsInt() == 1) {
                        JsonArray array = rspns.body().getAsJsonArray("data");
                        dtm.setRowCount(0);
                        double opb = 0.00, pur = 0.00, sal = 0.00, stock = 0.00;
                        for (int i = 0; i < array.size(); i++) {
                            Vector row = new Vector();

                            row.add(array.get(i).getAsJsonObject().get("MODEL_NAME").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("SR_NAME").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("OPB").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("SALES").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble());
                            row.add(array.get(i).getAsJsonObject().get("SR_ALIAS").getAsString());
                            row.add(array.get(i).getAsJsonObject().get("SR_CD").getAsString());
                            row.add(Constants.BRANCH.get(array.get(i).getAsJsonObject().get("branch_cd").getAsInt() - 1).getBranch_name());
                            if (jCheckBox1.isSelected()) {
                                if ((array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble()) < 0) {
                                    dtm.addRow(row);
                                    opb += array.get(i).getAsJsonObject().get("OPB").getAsDouble();
                                    pur += array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble();
                                    sal += array.get(i).getAsJsonObject().get("SALES").getAsDouble();
                                    stock += array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble();
                                }
                            } else {
//                    if ((array.get(i).getAsJsonObject().get("OPB") + array.get(i).getAsJsonObject().get("PURCHASE") - array.get(i).getAsJsonObject().get("SALES")) != 0) {
                                dtm.addRow(row);
                                opb += array.get(i).getAsJsonObject().get("OPB").getAsDouble();
                                pur += array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble();
                                sal += array.get(i).getAsJsonObject().get("SALES").getAsDouble();
                                stock += array.get(i).getAsJsonObject().get("OPB").getAsDouble() + array.get(i).getAsJsonObject().get("PURCHASE").getAsDouble() - array.get(i).getAsJsonObject().get("SALES").getAsDouble();
//                    }
                            }
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
                        dtm.addRow(row);

                        row = new Vector();
                        row.add("Total");
                        row.add(" ");
                        row.add(opb);
                        row.add(pur);
                        row.add(sal);
                        row.add(stock);
                        row.add(" ");
                        row.add(" ");
                        row.add(" ");
                        dtm.addRow(row);

                        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                        lb.removeGlassPane(StockSummary.this);
                        callExcel();
                    } else {
                        lb.showMessageDailog(rspns.body().get("Cause").getAsString());
                        lb.removeGlassPane(StockSummary.this);
                    }
                } else {
                    lb.showMessageDailog(rspns.message());
                    lb.removeGlassPane(StockSummary.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable thrwbl) {
                lb.removeGlassPane(StockSummary.this);
            }
        }
        );
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
                row.add(jTable1.getValueAt(i, 8).toString());
                rows.add(row);
            }

            ArrayList header = new ArrayList();
            header.add("Model Name");
            header.add("Item Name");
            header.add("Opening");
            header.add("Purchase");
            header.add("Sales");
            header.add("Balance");
            header.add("Alias");
            header.add("Branch");
            lb.exportToExcel("IMEI Statement", header, rows, "IMEI Statement");
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
                    lb.removeGlassPane(StockSummary.this);
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
                                    sr_cd = viewTable.getValueAt(row, 0).toString();
                                    jtxtProductName.setText(viewTable.getValueAt(row, 1).toString());
                                    jbtnView.requestFocusInWindow();
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
                    lb.removeGlassPane(StockSummary.this);
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
                    lb.removeGlassPane(StockSummary.this);
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
                                        jbtnView.requestFocusInWindow();
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
                    lb.removeGlassPane(StockSummary.this);
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
                    lb.removeGlassPane(StockSummary.this);
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
                                    model_cd = viewTable.getValueAt(row, 0).toString();
                                    jtxtModelName.setText(viewTable.getValueAt(row, 1).toString());
                                    jbtnView.requestFocusInWindow();
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
                    lb.removeGlassPane(StockSummary.this);
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jtxtProductName = new javax.swing.JTextField();
        jRadioButton2 = new javax.swing.JRadioButton();
        jcmbType = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jbtnView = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jtxtBrandName = new javax.swing.JTextField();
        jtxtModelName = new javax.swing.JTextField();
        jbtnPreview1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        panel = new javax.swing.JPanel();

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        jTable1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Model Name", "Product Name", "Opening", "Purchase", "Sales", "Balance", "Product Code", "sr_cd", "Branch"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
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
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setMinWidth(0);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(7).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

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

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Brand Name");

        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        jLabel5.setText("Type");

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("Model Name");

        jCheckBox1.setText("Show Only Negative Stock");

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

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Product Name");

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

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("All");

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

        jbtnPreview1.setText("Excel");
        jbtnPreview1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPreview1ActionPerformed(evt);
            }
        });
        jbtnPreview1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPreview1KeyPressed(evt);
            }
        });

        jLabel3.setText("Branch");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtModelName, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnPreview1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnPreview1, jbtnView});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtBrandName, jtxtModelName, jtxtProductName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1)
                    .addComponent(jtxtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnView)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnPreview)
                        .addComponent(jbtnClose)
                        .addComponent(jbtnPreview1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButton4)
                            .addComponent(jtxtModelName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jtxtBrandName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton3)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView, jtxtProductName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBox1, jLabel3, jLabel5, jcmbType});

        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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
        // TODO add your handling code here:
        jbtnPreviewActionPerformedRoutine();
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            evt.consume();
//            jbtnPreviewActionPerformedRoutine();
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

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && jTable1.getSelectedRow() != -1) {
            StockSummaryDetail sd = new StockSummaryDetail(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString(), jTable1.getValueAt(jTable1.getSelectedRow(), 6).toString());
            SkableHome.addOnScreen(sd, "Stock ItemWise Month Wise");
        }
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            popup.setVisible(false);
            popup.removeAll();
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
            popup.setVisible(true);
        }
    }//GEN-LAST:event_jTable1MouseClicked

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

    private void jtxtBrandNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBrandNameFocusGained
        // TODO add your handling code here:
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtBrandNameFocusGained

    private void jtxtBrandNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt) && jRadioButton2.isSelected()) {
            setBrandData("8", jtxtBrandName.getText().toUpperCase());
        }
    }//GEN-LAST:event_jtxtBrandNameKeyPressed

    private void jtxtBrandNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBrandNameKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_jtxtBrandNameKeyReleased

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

    private void jbtnPreview1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreview1ActionPerformed
        // TODO add your handling code here:
        jPanel1.removeAll();
        jPanel1.add(jScrollPane1);
        jScrollPane1.setVisible(true);
        jbtnExcelActionPerformedRoutine();
        lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
    }//GEN-LAST:event_jbtnPreview1ActionPerformed

    private void jbtnPreview1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreview1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtnPreview1KeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // TODO add your handling code here:
        lb.enterFocus(evt, jbtnView);
    }//GEN-LAST:event_jComboBox1KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnPreview1;
    private javax.swing.JButton jbtnView;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JTextField jtxtBrandName;
    private javax.swing.JTextField jtxtModelName;
    private javax.swing.JTextField jtxtProductName;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
