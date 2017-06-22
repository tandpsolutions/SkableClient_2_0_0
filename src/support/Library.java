/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterState;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.swing.JRViewer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.poi.hpsf.HPSFException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofitAPI.SalesAPI;
import skable.Constants;
import skable.CursorGlassPane;
import skable.SkableHome;
import transactionController.BankPaymentController;
import transactionController.CashPaymentReceiptController;
import transactionController.ContraVoucherController;
import transactionController.DCController;
import transactionController.JournalVoucherController;
import transactionController.PurchaseController;
import transactionController.PurchaseReturnController;
import transactionController.SalesController;
import transactionController.SalesReturnController;
import utility.SendMailSSL;

/**
 *
 * @author bhaumik
 */
public class Library {

    private static Library ourInstance = new Library();
    private Retrofit retrofit;
    private Retrofit upDateRetrofit;
    public boolean type;
    public SimpleDateFormat userFormat = new SimpleDateFormat("dd/MM/yyyy");
    private CursorGlassPane glassPane = new CursorGlassPane();
    private Component oldGlass = null;

    private Library() {
        makeConnection();
    }

    public void makeConnection() {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build();

            if (!Constants.BASE_URL.isEmpty()) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            upDateRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.UPDATE_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Library getInstance() {
        return ourInstance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public Retrofit getUpdateRetrofit() {
        return upDateRetrofit;
    }

    public void confirmDialog(String message) {
        final JButton yes = new JButton("Yes");
        final JButton no = new JButton("No");
        type = false;
        JOptionPane JP = new JOptionPane();
//                b1.setMnemonic(KeyEvent.VK_Y);
//                b2.setMnemonic(KeyEvent.VK_N);

        no.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                type = false;
                no.getTopLevelAncestor().setVisible(false);
            }
        });

        yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                type = true;
                yes.getTopLevelAncestor().setVisible(false);
            }
        });

        Action yesKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yes.doClick();
            }
        };

        Action noKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                no.doClick();
            }
        };
        yes.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "Click Me Button");
        yes.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0, false), "Click Me");
        no.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0, false), "Click Me");
        no.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0, false), "Click Me Button");
        yes.getActionMap().put("Click Me Button", yesKeyAction);
        yes.getActionMap().put("Click Me", noKeyAction);
        no.getActionMap().put("Click Me Button", yesKeyAction);
        no.getActionMap().put("Click Me", noKeyAction);
        JButton[] options = {yes, no};
        JP.showOptionDialog(null, message + " \n (Press Y for Yes)  (Press N for No)", "", -1, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    public String getCustomFormat(String input) {
        String value = "";
        value = input.replace(",", "");
        return value;
    }

    public String getDeCustomFormat(String input) {
        String value = "";
        value = input.replace(",", "");
        return value;
    }

    public boolean checkFinancialDate(JTextField jtxtDate) {
        boolean flag = checkDate2(jtxtDate);
        if (flag) {
            try {
                Date dt = userFormat.parse(jtxtDate.getText());
                int year = (int) isNumber2("2015");
                Date from = new Date(year - 1900, 3, 1);
                Date to = new Date(year + 1 - 1900, 2, 31);
                if (dt.before(from) || dt.after(to)) {
                    flag = false;
                }
            } catch (Exception ex) {
                flag = false;
                printToLogFile("Error at checkFinancialDate in library", ex);
            }
        }
        return flag;
    }

    public boolean checkDate2(JTextField jtxtDate) {
        boolean flag = true;
        try {
            String[] date = new String[3];
            StringTokenizer stToken = new StringTokenizer(jtxtDate.getText(), "/");
            int i = 0;
            while (stToken.hasMoreElements()) {
                String token = stToken.nextToken().trim();
                if (!token.equalsIgnoreCase("")) {
                    date[i] = token;
                    i++;
                }
            }

            int day = 0, month = 0, year = 0;
            if (i == 3) {
                day = (int) isNumber2(date[0]);
                month = (int) isNumber2(date[1]);
                year = (int) isNumber2(date[2]);

                if (day < 0 || day > 31) {
                    flag = false;
                }
                if (month < 1 || month > 12) {
                    flag = false;
                }

                if ((year + "").length() == 4) {
                } else if ((year + "").length() == 2) {
                    year += 2000;
                } else {
                    flag = false;
                }
                if (year < 1900 || year > 2099) {
                    flag = false;
                }
            } else {
                flag = false;
            }
            Date d = null;
            if (flag) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, day);
                d = cal.getTime();
                jtxtDate.setText(userFormat.format(d));
            }

        } catch (Exception ex) {
            flag = false;
            jtxtDate.requestFocusInWindow();

        }
        if (!flag) {
            jtxtDate.setText(userFormat.format(new Date()));
        }
        return flag;
    }

    public double isNumber2(String text) {
        double ans = 0.00;
        try {
            if (text != null) {
                ans = Double.parseDouble(text);
            }
        } catch (Exception ex) {
//            printToLogFile("Error at isNumber in Library", ex);
        }
        return ans;
    }
    
    public String checkTag(String inputTag) {
        String tag[] = inputTag.split("/");
        if (tag.length == 2) {
            String antyzero = tag[0] + tag[1];
            String newTag = tag[0];
            for (int i = antyzero.length(); i < 16; i++) {
                newTag += "0";
            }
            newTag += tag[1];
            return (newTag);
        } else {
            return inputTag;
        }
    }

    public void setDateChooserPropertyInit(JDateChooser jcmbDate) {
        jcmbDate.setDateFormatString("dd/MM/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        jcmbDate.setText(sdf.format(cal.getTime()));
        Font font = new Font("Arial", 1, 12);
        jcmbDate.setFont(font);
    }

    public void setDateChooserPropertyInit(JTextField jcmbDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        jcmbDate.setText(sdf.format(cal.getTime()));
    }

    public String getDateChooserPropertyInit() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        return (sdf.format(cal.getTime()));
    }

    public void setDateChooserPropertyInitStart(JTextField jcmbDate) {
        jcmbDate.setText("01/04/2015");
    }

    public boolean checkDate(JTextField jtxtDate) {
        boolean flag = true;
        try {
            if (jtxtDate.getText().contains("/")) {
                jtxtDate.setText(jtxtDate.getText().replace("/", ""));
            }
            if (jtxtDate.getText().length() == 8) {
                String temp = jtxtDate.getText();
                String setDate = (temp.substring(0, 2)).replace(temp.substring(0, 2), temp.substring(0, 2) + "/") + (temp.substring(2, 4)).replace(temp.substring(2, 4), temp.substring(2, 4) + "/") + temp.substring(4, temp.length());
                jtxtDate.setText(setDate);
            }

        } catch (Exception ex) {
            flag = false;
            jtxtDate.requestFocusInWindow();

        }
        return flag;
    }

    public void getSalesBillPrint(final String ref_no) {
        try {
            SalesAPI salesAPI = getRetrofit().create(SalesAPI.class);
            JsonObject call = salesAPI.GetSalesBillPrint(ref_no).execute().body();
            JsonObject call1 = salesAPI.GetSalesBillTaxPrint(ref_no).execute().body();

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    JsonArray array1 = call1.getAsJsonArray("data");
                    if (array != null) {
                        try {
                            FileWriter file = new FileWriter(System.getProperty("user.dir") + File.separator + "file1.txt");
                            FileWriter file2 = new FileWriter(System.getProperty("user.dir") + File.separator + "file2.txt");
                            file.write(array.toString());
                            file2.write(array1.toString());
                            file.close();
                            file2.close();
                            File jsonFile = new File(System.getProperty("user.dir") + File.separator + "file1.txt");
                            File jsonFile1 = new File(System.getProperty("user.dir") + File.separator + "file2.txt");
                            JsonDataSource dataSource = new JsonDataSource(jsonFile);
                            JsonDataSource dataSource1 = new JsonDataSource(jsonFile1);
                            HashMap params = new HashMap();
                            params.put("dir", System.getProperty("user.dir"));
                            params.put("comp_name", "APPLE N BERRY");
                            params.put("tin_no", (array.get(0).getAsJsonObject().get("COMPANY_TIN").getAsString()));
                            params.put("cst_no", (array.get(0).getAsJsonObject().get("COMPANY_CST").getAsString()));
                            params.put("add1", SkableHome.selected_branch.getAddress1());
                            params.put("add2", SkableHome.selected_branch.getAddress2());
                            params.put("add3", SkableHome.selected_branch.getAddress3());
                            params.put("email", SkableHome.selected_branch.getEmail());
                            params.put("mobile", SkableHome.selected_branch.getPhone());
                            params.put("tax_data", dataSource1);
                            reportGeneratorEmail("saleinVoice.jasper", params, dataSource, array.get(0).getAsJsonObject().get("EMAIL").getAsString(), ref_no);
                        } catch (Exception ex) {
                        }
                    }
                } else {
                    showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JasperPrint reportGeneratorEmail(String fileName, HashMap params, JRDataSource viewDataRs, String to, String ref_no) {
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            print = JasperFillManager.fillReport(System.getProperty("user.dir") + "/Reports/" + fileName, params, viewDataRs);
            JasperExportManager.exportReportToPdfFile(print, System.getProperty("user.dir") + "/PDF/" + ref_no + ".pdf");
            SendMailSSL se = new SendMailSSL(to);
//            SendMailSSL se = new SendMailSSL("bhaumikshah1991@gmail.com");
            se.sendEmail(System.getProperty("user.dir") + "/PDF/" + ref_no + ".pdf");
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public String ConvertDateFormetForDisplay(String strOrgDate) {
        //Changed
        String strConvDate = "";
        //try
        //{
        strOrgDate = strOrgDate.trim();
        if (!strOrgDate.startsWith("/")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dt = new Date();
            try {
                dt = sdf.parse(strOrgDate);
            } catch (ParseException ex) {
            }
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            strConvDate = sdf2.format(dt);
        }
        //} catch(Exception ex){
        //printToLogFile("Error in ConvertDateFormetForDB in clSysLib...:",ex);
        //}
        return strConvDate;
    }

    public boolean isBlank(Component comp) {
        JTextField jText = (JTextField) comp;
        if (jText.getText().trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public String roundOffDoubleValue(Double strSource) {
        String str = "0";
        double temp = Math.round(strSource);
        str = Convert2DecFmt(temp);
        return str;
    }

    public String Convert2DecFmt(double strSource) {
        String str = "0";
        try {

            NumberFormat formatter = new DecimalFormat("#0.00");
            str = formatter.format(strSource);
        } catch (Exception ex) {
            printToLogFile("Exception at convertToFormat in clSysLib..", ex);
        }
        return str;
    }

    public void setTable(JPanel jPanel, JTable jTableDet, JComponent[] compHeader, JComponent[] compFooter) {
        int maxHeightHeaderComp = 0;
        int maxHeightFooterComp = 0;
        int x = 0;
        int y = 0;

        if (compHeader != null) {
            for (int i = 0; i < compHeader.length; i++) {
                if (compHeader[i] != null) {
                    if (maxHeightHeaderComp < compHeader[i].getHeight()) {
                        maxHeightHeaderComp = compHeader[i].getHeight();
                    }
                }
            }

            // SETTING HEADER
            x = jPanel.getX();
            y = jPanel.getY() - maxHeightHeaderComp;
            for (int i = 0; i < jTableDet.getColumnCount(); i++) {

                if (compHeader[i] != null) {
                    compHeader[i].setBounds(x, y, jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth() - 1, maxHeightHeaderComp);
                }
                x += jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth();
            }
        }

        if (compFooter != null) {
            for (int i = 0; i < compFooter.length; i++) {
                if (compFooter[i] != null) {
                    Dimension d = compFooter[i].getPreferredSize();
                    if (maxHeightFooterComp < d.height) {
                        maxHeightFooterComp = d.height;
                    }
                }
            }

            // SETTING FOOTER
            x = jPanel.getX();
            y = jPanel.getY() + jPanel.getHeight();
            for (int i = 0; i < jTableDet.getColumnCount(); i++) {

                if (compFooter[i] != null) {
                    compFooter[i].setBounds(x, y, jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth() - 1, maxHeightFooterComp);
                }
                x += jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth();
            }
        }
    }

    public void adjustColumnSizes(JTable table, int column, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(column);
        int width, minWidth;

        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, column);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
            int currentWidth = comp.getPreferredSize().width;
            width = Math.max(width, currentWidth);
        }
        minWidth = col.getMinWidth();
        width += 2 * margin;
        if (width < minWidth) {
            width = minWidth;
        }
        col.setPreferredWidth(width);
        col.setWidth(width);
    }

    public void setColumnSizeForTable(JTable table, int minTableWidth) {
        adjustJTableRowSizes(table);
        for (int i = 0; i < table.getColumnCount(); i++) {
            adjustColumnSizes(table, i, 2);
        }
        if (minTableWidth != 0) {
            if (table.getPreferredSize().width < minTableWidth) {
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            } else {
                table.setAutoResizeMode(0);
            }
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }
    }

    public void adjustJTableRowSizes(JTable jTable) {
        for (int row = 0; row < jTable.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < jTable.getColumnCount(); column++) {
                TableCellRenderer cellRenderer = jTable.getCellRenderer(row, column);
                Object valueAt = jTable.getValueAt(row, column);
                Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(jTable, valueAt, false, false, row, column);
                int heightPreferable = tableCellRendererComponent.getPreferredSize().height;
                maxHeight = Math.max(heightPreferable, maxHeight);
            }
            jTable.setRowHeight(row, maxHeight);
        }

    }

    public void onlyNumber(KeyEvent event, int len) {

        try {
            int keyCode = event.getKeyChar();
            JTextComponent source = (JTextComponent) event.getSource();
            if (len == -1) {
                len = source.getText().length() + 1;
            }
            if (!(keyCode < 48 || keyCode > 58) || keyCode == 46 || keyCode == 45) {
                if (event.isConsumed()) {
                    return;
                }
                if (source.getText().length() >= len && event.getKeyChar() != event.VK_BACK_SPACE && source.getSelectionStart() == source.getSelectionEnd()) {
                    source.getToolkit().beep();
                    event.consume();
                }
            } else {
                event.consume();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double isNumber(JComponent comp) {
        double ans = 0.00;
        String txt = "";
        if (comp instanceof JTextField) {
            txt = ((JTextField) comp).getText();
        } else if (comp instanceof JLabel) {
            txt = ((JLabel) comp).getText();
        }
        try {
            ans = Double.parseDouble(txt);
        } catch (Exception ex) {
//            printToLogFile("Error at isNumber in Library", ex);
        }
        return ans;
    }

    public String Convert2DecFmtForRs(double strSource) {
//        return roundOffDoubleValue(strSource);
        String str = "0";
        try {
            String digit = "";
            for (int i = 1; i <= 2; i++) {
                digit += "0";
            }
            NumberFormat formatter = new DecimalFormat("#0." + digit);
            str = formatter.format(strSource);
        } catch (Exception ex) {
            printToLogFile("Exception at convertToFormat in clSysLib..", ex);
        }
        if (str.equalsIgnoreCase("0.00")) {
            str = "";
        }
        return str;
    }

    public String setDay(JTextField jtxtDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE");
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            java.util.Date dt = sdfDate.parse(jtxtDate.getText());
            cal.setTime(dt);
            return (sdf.format(cal.getTime()));

        } catch (Exception ex) {
        }
        return "";
    }

    public void fixLength(KeyEvent event, int len) {

        try {
            if (event.isConsumed()) {
                return;
            }
            JTextComponent source = (JTextComponent) event.getSource();
            if (len == -1) {
                len = source.getText().length() + 1;
            }
            if (source.getText().length() >= len && event.getKeyChar() != event.VK_BACK_SPACE && source.getSelectionStart() == source.getSelectionEnd()) {
                source.getToolkit().beep();
                event.consume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectAll(FocusEvent evt) {
        ((JTextField) evt.getSource()).selectAll();
    }

    public void toDouble(java.awt.event.FocusEvent evt) {
        ((JTextField) evt.getSource()).setText(isNumber(((JTextField) evt.getSource()).getText()) + "");
    }

    public void showMessageDailog(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    public void setTable(JTable jTableDet, JComponent[] compHeader) {
        int maxHeightHeaderComp = 0;
        int maxHeightFooterComp = 0;
        int x = 0;
        int y = 0;

        if (compHeader != null) {
            for (int i = 0; i < compHeader.length; i++) {
                if (compHeader[i] != null) {
                    if (maxHeightHeaderComp < compHeader[i].getHeight()) {
                        maxHeightHeaderComp = compHeader[i].getHeight();
                    }
                }
            }

            // SETTING HEADER
            x = 0;
            y = 0;
            for (int i = 0; i < jTableDet.getColumnCount(); i++) {
                if (compHeader[i] != null) {
                    compHeader[i].setBounds(x, y, jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth() - 1, maxHeightHeaderComp);
                }
                x += jTableDet.getColumn(jTableDet.getColumnName(i).toString()).getWidth();
            }
        }
    }

    public void toUpper(java.awt.event.FocusEvent evt) {
        if (evt.getSource() instanceof JTextField) {
            JTextField txt = (JTextField) evt.getSource();
            txt.setText(txt.getText().trim().toUpperCase());
        } else if (evt.getSource() instanceof JTextArea) {
            JTextArea txt = (JTextArea) evt.getSource();
            txt.setText(txt.getText().trim().toUpperCase());
        }
    }

    public void enterFocus(KeyEvent evt, JComponent comp) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            comp.requestFocusInWindow();
        }
    }

    public void toInteger(java.awt.event.FocusEvent evt) {
        ((JTextField) evt.getSource()).setText((long) isNumber(((JTextField) evt.getSource()).getText()) + "");
    }

    public double isNumber(String comp) {
        double ans = 0.00;
        try {
            ans = Double.parseDouble(comp);
        } catch (Exception ex) {
//            printToLogFile("Error at isNumber in Library", ex);
        }
        return ans;
    }

    public void downFocus(KeyEvent evt, JComponent comp) {
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (evt.getModifiers() == KeyEvent.CTRL_MASK) {
                evt.consume();
                comp.requestFocusInWindow();
            }
        }
    }

    public void enterClick(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            ((JButton) evt.getSource()).doClick();
        }
    }

    public boolean isEnter(KeyEvent evt) {
        boolean flag = false;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            flag = true;
        }
        return flag;
    }

    public String ConvertDateFormetForDBForConcurrency(String strOrgDate) throws Exception {
        //Changed
        String strConvDate = "";
        //try
        //{
        strOrgDate = strOrgDate.trim();
        if (!strOrgDate.startsWith("/")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dt = sdf.parse(strOrgDate);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            strConvDate = sdf2.format(dt);
        }
        //} catch(Exception ex){
        //printToLogFile("Error in ConvertDateFormetForDB in clSysLib...:",ex);
        //}
        return strConvDate;
    }

    public String ConvertDateFormetForDB(String strOrgDate) {
        //Changed
        String strConvDate = "";
        try {
            strOrgDate = strOrgDate.trim();
            if (!strOrgDate.startsWith("/")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date dt = sdf.parse(strOrgDate);
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                strConvDate = sdf2.format(dt);
            }
        } catch (Exception ex) {
        }
        return strConvDate;
    }

    public Date ConvertDateFromString(String strOrgDate) {
        //Changed
        java.util.Date dt = null;
        try {
            strOrgDate = strOrgDate.trim();
            if (!strOrgDate.startsWith("/")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                dt = sdf.parse(strOrgDate);
            }
        } catch (Exception ex) {
        }
        return dt;
    }

    public String getMonth(String i, String tag) {
        if (tag.equalsIgnoreCase("n")) {
            if (i.equalsIgnoreCase("1")) {
                return "January";
            } else if (i.equalsIgnoreCase("2")) {
                return "February";
            } else if (i.equalsIgnoreCase("3")) {
                return "March";
            } else if (i.equalsIgnoreCase("4")) {
                return "April";
            } else if (i.equalsIgnoreCase("5")) {
                return "May";
            } else if (i.equalsIgnoreCase("6")) {
                return "June";
            } else if (i.equalsIgnoreCase("7")) {
                return "July";
            } else if (i.equalsIgnoreCase("8")) {
                return "August";
            } else if (i.equalsIgnoreCase("9")) {
                return "September";
            } else if (i.equalsIgnoreCase("10")) {
                return "October";
            } else if (i.equalsIgnoreCase("11")) {
                return "November";
            } else if (i.equalsIgnoreCase("12")) {
                return "December";
            }
        } else if (tag.equalsIgnoreCase("c")) {
            if (i.equalsIgnoreCase("January")) {
                return "01";
            } else if (i.equalsIgnoreCase("February")) {
                return "02";
            } else if (i.equalsIgnoreCase("March")) {
                return "03";
            } else if (i.equalsIgnoreCase("April")) {
                return "04";
            } else if (i.equalsIgnoreCase("May")) {
                return "05";
            } else if (i.equalsIgnoreCase("June")) {
                return "06";
            } else if (i.equalsIgnoreCase("July")) {
                return "07";
            } else if (i.equalsIgnoreCase("August")) {
                return "08";
            } else if (i.equalsIgnoreCase("September")) {
                return "09";
            } else if (i.equalsIgnoreCase("October")) {
                return "10";
            } else if (i.equalsIgnoreCase("November")) {
                return "11";
            } else if (i.equalsIgnoreCase("December")) {
                return "12";
            }
        }
        return "";
    }

    public void printToLogFile(String strMsg, Exception exType) {
        try {
            HashSet<String> hsFileName = new HashSet<String>();
            if (exType != null) {
                StackTraceElement str[] = exType.getStackTrace();
                int iIndex = 0;
                if (str.length > 10) {
                    iIndex = 10;
                } else {
                    iIndex = str.length;
                }

                for (int i = 0; i < iIndex; i++) {
                    if (str[i].getFileName() != null) {
                        hsFileName.add(str[i].getFileName());
                    }
                }

                if (!hsFileName.contains("DataFilterColumnArray.java")
                        && !hsFileName.contains("DataFilterDate.java")
                        && !hsFileName.contains("DataFilterNumber.java")
                        && !hsFileName.contains("DataFilterString.java")) {
                    SkableHome.logFile.write("Time : " + getCurrentDBServerTime());
                    SkableHome.logFile.newLine();
                    SkableHome.logFile.write("Exception From : " + strMsg.toString());
                    SkableHome.logFile.newLine();
                    SkableHome.logFile.write("Main Exception :" + exType.toString());
                    SkableHome.logFile.newLine();

                    for (int i = 0; i < iIndex; i++) {
                        SkableHome.logFile.write("          ======================           ");
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("Class Name  :" + str[i].getClassName());
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("File Name   :" + str[i].getFileName());
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("Method Name :" + str[i].getMethodName());
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("Line Number :" + str[i].getLineNumber());
                        SkableHome.logFile.newLine();
                    }

                    SkableHome.logFile.write("==================================================");
                    SkableHome.logFile.newLine();
                    SkableHome.logFile.write("==================================================");
                    SkableHome.logFile.newLine();
                }
            } else {
                SkableHome.logFile.write("Time : " + getCurrentDBServerTime());
                SkableHome.logFile.newLine();
                SkableHome.logFile.write("Message(For Information) : " + strMsg.toString());
                SkableHome.logFile.newLine();
                SkableHome.logFile.write("==================================================");
                SkableHome.logFile.newLine();
            }
            SkableHome.logFile.flush();
            if (exType instanceof java.sql.SQLNonTransientConnectionException) {
                JButton exit = new JButton("Exit");
//                    JButton cancel = new JButton("Cancel");
                JButton[] button = {exit};
                exit.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                JOptionPane.showOptionDialog(new SkableHome(), "Please Restart the Application and\n Check the Database Connection", "Connection Error",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, button, exit);
                return;
            }
        } catch (Exception ex) {
            printToLogFile(strMsg + " (And Error in PrintToLogFile : " + ex + ")", exType, true);
        }
    }

    public String getCurrentDBServerTime() {
        String strTime = "";
        try {
            Calendar cal = Calendar.getInstance();

            strTime = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(cal.getTime());
        } catch (Exception ex) {
            //Null is passed here, because it shold print the original exception,
            //If we passed this exception then on nontransientconnection exception
            //it will go for exit in printtoLogfile..
            printToLogFile("Exception at getCurrentDBServerTime in clSysLib..!!: ", null, true);
        }
        return strTime;
//       Temprority changed..
//        return DateFormat.getTimeInstance().format(new java.util.Date());
    }

    public void printToLogFile(String strMsg, Exception exType, boolean withoutTiming) {
        try {
            HashSet<String> hsFileName = new HashSet<String>();
            if (exType != null) {
                StackTraceElement str[] = exType.getStackTrace();
                int iIndex = 0;
                if (str.length > 10) {
                    iIndex = 10;
                } else {
                    iIndex = str.length;
                }

                for (int i = 0; i < iIndex; i++) {
                    if (str[i].getFileName() != null) {
                        hsFileName.add(str[i].getFileName());
                    }
                }

                if (!hsFileName.contains("DataFilterColumnArray.java")
                        && !hsFileName.contains("DataFilterDate.java")
                        && !hsFileName.contains("DataFilterNumber.java")
                        && !hsFileName.contains("DataFilterString.java")) {
                    SkableHome.logFile.write("Exception From : " + strMsg.toString());
                    SkableHome.logFile.newLine();
                    SkableHome.logFile.write("Main Exception :" + exType.toString());
                    SkableHome.logFile.newLine();

                    for (int i = 0; i < iIndex; i++) {
                        SkableHome.logFile.write("          ======================           ");
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("Class Name  :" + str[i].getClassName());
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("File Name   :" + str[i].getFileName());
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("Method Name :" + str[i].getMethodName());
                        SkableHome.logFile.newLine();
                        SkableHome.logFile.write("Line Number :" + str[i].getLineNumber());
                        SkableHome.logFile.newLine();
                    }

                    SkableHome.logFile.write("==================================================");
                    SkableHome.logFile.newLine();
                    SkableHome.logFile.write("==================================================");
                    SkableHome.logFile.newLine();
                }
                if (exType instanceof java.sql.SQLNonTransientConnectionException) {
                    SkableHome.logFile.flush();
                    JButton exit = new JButton("Exit");
//                    JButton cancel = new JButton("Cancel");
                    JButton[] button = {exit};
                    exit.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });
                    JOptionPane.showOptionDialog(new SkableHome(), "Please Restart the Application and\n Check the Database Connection", "Connection Error",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, button, exit);
                    return;
                }
            } else {
                SkableHome.logFile.write("Main Exception :" + strMsg.toString());
                SkableHome.logFile.newLine();
            }
            SkableHome.logFile.flush();
        } catch (Exception ex) {
            System.out.println("Exception at printToLogFile_withoutTiming in clSysLib..!!" + ex);
        }
    }

    public void addGlassPane(JInternalFrame navLoad) {
        if (!(((JInternalFrame) navLoad).getGlassPane() instanceof CursorGlassPane)) {
            //If antother Glass Pane is set for this form then save it to restore it after saving..
            oldGlass = ((JInternalFrame) navLoad).getGlassPane();
            ((JInternalFrame) navLoad).setGlassPane(glassPane);

        }
        ((JInternalFrame) navLoad).getGlassPane().setVisible(true);
        ((JInternalFrame) navLoad).getGlassPane().requestFocusInWindow();

        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

    }

    public void addGlassPane(javax.swing.JDialog navLoad) {
        if (!(((javax.swing.JDialog) navLoad).getGlassPane() instanceof CursorGlassPane)) {
            //If antother Glass Pane is set for this form then save it to restore it after saving..
            oldGlass = ((javax.swing.JDialog) navLoad).getGlassPane();
            ((javax.swing.JDialog) navLoad).setGlassPane(glassPane);

        }
        ((javax.swing.JDialog) navLoad).getGlassPane().setVisible(true);
        ((javax.swing.JDialog) navLoad).getGlassPane().requestFocusInWindow();

        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

    }

    public void removeGlassPane(JInternalFrame navLoad) {
        navLoad.requestFocusInWindow();
        ((JInternalFrame) navLoad).getGlassPane().setVisible(false);
        if (oldGlass != null) {
            ((JInternalFrame) navLoad).setGlassPane(oldGlass);
        }
        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    public void removeGlassPane(javax.swing.JDialog navLoad) {
        navLoad.requestFocusInWindow();
        ((javax.swing.JDialog) navLoad).getGlassPane().setVisible(false);
        if (oldGlass != null) {
            ((javax.swing.JDialog) navLoad).setGlassPane(oldGlass);
        }
        navLoad.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    public void exportToExcel(String sheetName, ArrayList headers,
            ArrayList data, String fileName) throws HPSFException {
        final JFileChooser jfc = new JFileChooser(SkableHome.currentDirectory);

        final JTextField jf = new JTextField();
        jfc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equalsIgnoreCase("ApproveSelection")) {
                    jf.setText(jfc.getSelectedFile().getAbsolutePath());
                } else {
                    jf.setText("");
                    return;
                }
            }
        });

        jfc.setCurrentDirectory(new File(SkableHome.currentDirectory));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setDialogTitle("Select Destination Folder");
        jfc.setApproveButtonText("Select");
        jfc.showOpenDialog(null);

        if (!jf.getText().isEmpty()) {
            fileName = jf.getText() + File.separatorChar + sheetName.replaceAll(".jasper", "");
            Calendar cal = Calendar.getInstance();
            fileName += cal.get(Calendar.DATE + 1) + "_" + cal.get(Calendar.MONTH + 1) + "_" + cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.HOUR) + "_" + cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND);
            File f1 = new File(fileName + ".xls");
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(sheetName);

            int rowIdx = 0;
            short cellIdx = 0;

            // Header
            HSSFRow hssfHeader = sheet.createRow(rowIdx);
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            for (Iterator cells = headers.iterator(); cells.hasNext();) {
                HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
                hssfCell.setCellStyle(cellStyle);
                hssfCell.setCellValue((String) cells.next());
            }
            // Data
            rowIdx = 1;
            for (Iterator rows = data.iterator(); rows.hasNext();) {
                ArrayList row = (ArrayList) rows.next();
                HSSFRow hssfRow = sheet.createRow(rowIdx++);
                cellIdx = 0;
                for (Iterator cells = row.iterator(); cells.hasNext();) {
                    HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
                    hssfCell.setCellValue(cells.next() + "");
                }
            }

            wb.setSheetName(0, sheetName);
            try {
                FileOutputStream outs = new FileOutputStream(f1);
                wb.write(outs);
                outs.close();
                confirmDialog(f1.getAbsolutePath() + " has been generated successfully.");
                if (type) {
                    Desktop.getDesktop().open(f1);
                }
            } catch (IOException e) {
                throw new HPSFException(e.getMessage());
            }
        }

    }

    public void PrintLabel(String tag1, String item_name1, String Rate1, String nlRate1, String tag2, String item_name2, String Rate2, String nlRate2) {

        // Prepare date to print in dd/mm/yyyy format
        // Search for an installed zebra printer...
        // is a printer with "zebra" in its name
        try {
            PrintService psZebra = null;
            String sPrinterName = null;
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (int i = 0; i < services.length; i++) {
                if ((services[i].getName().toString().equalsIgnoreCase("Zebra")
                        || services[i].getName().toString().equalsIgnoreCase("TTP-244"))) {
                    psZebra = services[i];
                    PrintServiceAttributeSet printServiceAttributes = psZebra.getAttributes();
                    PrinterState printerState = (PrinterState) printServiceAttributes.get(PrinterState.class);
                    if (printerState != null) {
                    } else {
                    }
                    break;

                }
            }

            if (psZebra == null) {
                System.out.println("Zebra printer is not found.");
                return;
            }

            System.out.println("Found printer: " + sPrinterName);
            DocPrintJob job = psZebra.createPrintJob();

            // Prepare string to send to the printer
            String s = "^XA\n"
                    + "^FO160,5^BY1\n"
                    + "^BCN,30,N,Y,N\n"
                    + "^FD" + tag1 + "^FS\n"
                    + "^CF0,23"
                    + "^FO140,47^FD" + tag1.substring(0, 6) + (int) isNumber(nlRate1) + tag1.substring(6) + "^FS"
                    + "^FO140,68^FD" + item_name1 + "^FS"
                    + "^FO140,92^FD" + Rate1 + "-ANB^FS"
                    //Second Label
                    + "^FO450,5^BY1\n"
                    + "^BCN,30,Y,Y,N\n"
                    + "^FD" + tag2 + "^FS\n"
                    + "^CF0,23"
                    + "^FO440,47^FD" + tag2.substring(0, 6) + (int) isNumber(nlRate2) + tag2.substring(6) + "^FS"
                    + "^FO440,68^FD" + item_name2 + "^FS"
                    + "^FO440,92^FD" + Rate2 + "-ANB^FS"
                    + "^XZ";   // Print content of buffer, 1 label
            byte[] by = s.getBytes();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            // MIME type = "application/octet-stream",
            // print data representation class name = "[B" (byte array).
            Doc doc = new SimpleDoc(by, flavor, null);
            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void PrintLabel(String tag1, String item_name1, String Rate1, String nlRate1) {

        // Prepare date to print in dd/mm/yyyy format
        // Search for an installed zebra printer...
        // is a printer with "zebra" in its name
        try {
            PrintService psZebra = null;
            String sPrinterName = null;
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (int i = 0; i < services.length; i++) {
                if ((services[i].getName().toString().equalsIgnoreCase("Zebra")
                        || services[i].getName().toString().equalsIgnoreCase("TTP-244"))) {
                    psZebra = services[i];
                    PrintServiceAttributeSet printServiceAttributes = psZebra.getAttributes();
                    PrinterState printerState = (PrinterState) printServiceAttributes.get(PrinterState.class);
                    if (printerState != null) {
                    } else {
                    }
                    break;

                }
            }
            if (psZebra == null) {
                System.out.println("Zebra printer is not found.");
                return;
            }

            System.out.println("Found printer: " + sPrinterName);
            DocPrintJob job = psZebra.createPrintJob();

            // Prepare string to send to the printer
            String s = "^XA\n"
                    + "^FO160,5BY1\n"
                    + "^BCN,30,N,Y,N\n"
                    + "^FD" + tag1 + "^FS\n"
                    + "^CF0,23"
                    + "^FO140,47^FD" + tag1.substring(0, 6) + (int) isNumber(nlRate1) + tag1.substring(6) + "^FS"
                    + "^FO140,68^FD" + item_name1 + "^FS"
                    + "^FO140,92^FD" + Rate1 + "-ANB^FS"
                    + "^XZ";   // Print content of buffer, 1 label
            byte[] by = s.getBytes();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            // MIME type = "application/octet-stream",
            // print data representation class name = "[B" (byte array).
            Doc doc = new SimpleDoc(by, flavor, null);
            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public JasperPrint reportGenerator(String fileName, HashMap params, JRDataSource viewDataRs, JPanel panelReport) {
//        JRResultSetDataSource dataSource = new JRResultSetDataSource(viewDataRs);
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports" + File.separatorChar + fileName, params, viewDataRs);
            panelReport.removeAll();
            JRViewer jrViewer = new JRViewer(print);
//            ((JPanel)jrViewer.getComponent(0)).remove(0);
            jrViewer.setSize(panelReport.getWidth(), panelReport.getHeight());
            panelReport.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(panelReport);
            panelReport.requestFocusInWindow();
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }

    public JasperPrint reportGeneratorPDF(String fileName, HashMap params, JRDataSource viewDataRs, String ref_no) {
//        JRResultSetDataSource dataSource = new JRResultSetDataSource(viewDataRs);
        JasperPrint print = null;
//        jScrollPane1.setVisible(false);
        try {
            if (!new File(System.getProperty("user.dir") + "/PDF").exists()) {
                new File(System.getProperty("user.dir") + "/PDF").mkdir();
            }
            print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports" + File.separatorChar + fileName, params, viewDataRs);
            JasperExportManager.exportReportToPdfFile(print, System.getProperty("user.dir") + File.separatorChar + "PDF" + File.separatorChar + ref_no + ".pdf");
            open(new File(System.getProperty("user.dir") + File.separatorChar + "PDF" + File.separatorChar + ref_no + ".pdf"));
//            ((JPanel)jrViewer.getComponent(0)).remove(0);
        } catch (Exception ex) {
            printToLogFile("Exception at reportGenerator report", ex);
        }
        return print;
    }
    private static boolean isWindows = false;
    private static boolean isLinux = false;
    private static boolean isMac = false;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        isWindows = os.contains("win");
        isLinux = os.contains("nux") || os.contains("nix");
        isMac = os.contains("mac");
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isLinux() {
        return isLinux;
    }

    public static boolean isMac() {
        return isMac;
    }

    ;

    private void open(File file) {
        try {
            if (isWindows()) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler",
                    file.getAbsolutePath()});
//            } else if (isLinux() || isMac()) {
//                Runtime.getRuntime().exec(new String[]{"/usr/bin/open",
//                    file.getAbsolutePath()});
            } else {
                // Unknown OS, try with desktop
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void openVoucherBook(String ref_no) {
        if (ref_no.startsWith("02")) {
            if (SkableHome.userRightsMap.get(13).getEDITS().equalsIgnoreCase("1")) {
                SalesController sbc = new SalesController(null, true);
                sbc.setLocationRelativeTo(null);
                sbc.setData(ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("05")) {
            if (SkableHome.userRightsMap.get(10).getEDITS().equalsIgnoreCase("1")) {
                PurchaseController sbc = new PurchaseController(null, true, 0, null);
                sbc.setLocationRelativeTo(null);
                sbc.setData(null, ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("BP")) {
            if (SkableHome.userRightsMap.get(19).getEDITS().equalsIgnoreCase("1")) {
                BankPaymentController sbc = new BankPaymentController(null, true, 0);
                sbc.setLocationRelativeTo(null);
                sbc.setData(null, ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("BR")) {
            if (SkableHome.userRightsMap.get(20).getEDITS().equalsIgnoreCase("1")) {
                BankPaymentController sbc = new BankPaymentController(null, true, 1);
                sbc.setLocationRelativeTo(null);
                sbc.setData(null, ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("CV")) {
            if (SkableHome.userRightsMap.get(22).getEDITS().equalsIgnoreCase("1")) {
                ContraVoucherController sbc = new ContraVoucherController(null, true);
                sbc.setLocationRelativeTo(null);
                sbc.setData(null, ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("CR")) {
            if (SkableHome.userRightsMap.get(18).getEDITS().equalsIgnoreCase("1")) {
                CashPaymentReceiptController sbc = new CashPaymentReceiptController(null, true, 1);
                sbc.setLocationRelativeTo(null);
                sbc.setData(null, ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("CP")) {
            if (SkableHome.userRightsMap.get(17).getEDITS().equalsIgnoreCase("1")) {
                CashPaymentReceiptController sbc = new CashPaymentReceiptController(null, true, 0);
                sbc.setLocationRelativeTo(null);
                sbc.setData(null, ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("JV")) {
            if (SkableHome.userRightsMap.get(21).getEDITS().equalsIgnoreCase("1")) {
                JournalVoucherController sbc = new JournalVoucherController(null, true);
                sbc.setLocationRelativeTo(null);
                sbc.setData(null, ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("PR")) {
            if (SkableHome.userRightsMap.get(12).getEDITS().equalsIgnoreCase("1")) {
                PurchaseReturnController sbc = new PurchaseReturnController(null, true);
                sbc.setLocationRelativeTo(null);
                sbc.setData(ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("SR")) {
            if (SkableHome.userRightsMap.get(15).getEDITS().equalsIgnoreCase("1")) {
                SalesReturnController sbc = new SalesReturnController(null, true);
                sbc.setLocationRelativeTo(null);
                sbc.setData(ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        } else if (ref_no.startsWith("DCI")) {
            if (SkableHome.userRightsMap.get(24).getEDITS().equalsIgnoreCase("1")) {
                DCController sbc = new DCController(null, true);
                sbc.setLocationRelativeTo(null);
                sbc.setData(ref_no);
            } else {
                showMessageDailog("You don't have rights to perform this action");
            }
        }
    }
}
