/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skable;

import account.BajajReport;
import account.BankBook;
import account.BranchWisePendingReport;
import account.BuyBackRegister;
import account.CardChargesRegister;
import account.CreditNoteRegister;
import account.DCRegister;
import account.DailySalesStatement;
import account.DailySalesStatementDetail;
import account.DateWiseGroupSummary;
import account.EOD;
import account.GeneralLedger1;
import account.GroupSummary;
import account.InsuranceRegister;
import account.ItemWisePS;
import account.ItemWisePSSales;
import account.ItemWisePSWoRate;
import account.MarginReport;
import account.MarginReportByTag;
import account.ModelWiseMonthWiseSalesStatement;
import account.PhoneBookNew;
import account.PurchaseRateBYTag;
import account.PurchaseRegister;
import account.PurchaseRegisterDetail;
import account.PurchaseRegisterDetailAccount;
import account.PurchaseReturnRegister;
import account.PurchaseReturnRegisterDetail;
import account.PurchsaeReturnRegisterDetailAccount;
import account.SalesRegister;
import account.SalesRegisterCardWise;
import account.SalesRegisterDetail;
import account.SalesRegisterDetailAccount;
import account.SalesRegisterDetailCardWise;
import account.SalesReport;
import account.SalesReportBrijesh;
import account.SalesReturnRegister;
import account.SalesReturnRegisterDetail;
import account.SalesReturnRegisterDetailAccount;
import account.TypeWiseBrandWiseProfitStatement;
import account.TypeWiseProfitStatement;
import account.TypeWisePurchase;
import account.TypeWisePurchaseDetail;
import account.TypeWiseSales;
import account.TypeWiseSalesDetail;
import account.WithoutTagSalesReport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import inventory.DayWiseStockSummary;
import inventory.StockInOUTReport;
import inventory.StockLedger;
import inventory.StockLedgerRate;
import inventory.StockStatementDateWise;
import inventory.StockSummary;
import inventory.StockSummaryBalance;
import inventory.StockSummaryDetail;
import inventory.StockTransferPendingReport;
import inventory.StockValueIMEI;
import inventory.StockValueIMEIAccess;
import inventory.StockValueStatement;
import inventory.StockValueStatementAccess;
import inventory.StockValueStatementDateWise;
import inventory.TagTrack;
import inventory.TagTractQtyWise;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import login.Login;
import masterView.AccountMasterView;
import masterView.BrandMasterView;
import masterView.ColorMasterView;
import masterView.GroupMasterView;
import masterView.MemoryMasterView;
import masterView.ModelMasterView;
import masterView.RefMasterView;
import masterView.SalesmanMaster;
import masterView.SeriesMasterView;
import masterView.TidMasterView;
import masterView.TypeMasterView;
import masterView.UserGroupMasterView;
import masterView.UserMasterView;
import model.BranchMasterModel;
import model.UserRightsModel;
import retrofit2.Call;
import retrofitAPI.UserAPI;
import support.Library;
import support.UnCaughtException;
import support.ZoomingTableToolTip;
import transactionController.InternalCashTransfer;
import transactionView.BankPaymentReceiptView;
import transactionView.BranchWiseLimit;
import transactionView.CashPaymentReceiptView;
import transactionView.ContraVoucherView;
import transactionView.CreditNoteListReport;
import transactionView.DCView;
import transactionView.DNCNView;
import transactionView.JournalVoucherView;
import transactionView.ListBill;
import transactionView.PurchaseReturnView;
import transactionView.PurchaseView;
import transactionView.SalesReturnView;
import transactionView.SalesView;
import transactionView.StockTransferView;
import utility.BillTrack;
import utility.ChangePassword;
import utility.CreateUser;
import utility.IMEISearch;
import utility.NotesView;
import utility.UserPermission;

/**
 *
 * @author bhaumik
 */
public class SkableHome extends javax.swing.JFrame {

    public static String user_id = "1";
    public static String user_grp_cd = "1";
    public static BranchMasterModel selected_branch;
    public static BufferedWriter logFile = null;
    FileOutputStream errorFile = null;
    private PrintStream fileStream = null;
    private TrayIcon trayIcon = null;
    private SystemTray systemTray = SystemTray.getSystemTray();
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    public static String currentDirectory = "";
    public static JTabbedPane tabbedPane = new JTabbedPane();
    private Library lb = Library.getInstance();
    private HashMap<Integer, JMenuItem> hashMenu = null;
    public static HashMap<Integer, UserRightsModel> userRightsMap;
    public static ZoomingTableToolTip zoomTable = new ZoomingTableToolTip();

    /**
     * Creates new form SkableHome
     */
    public SkableHome() {
        initComponents();
        this.setLocationRelativeTo(null);
        currentDirectory = System.getProperty("user.dir");
        setExtendedState(MAXIMIZED_BOTH);
        jDesktopPane1.add(tabbedPane);
        tabbedPane.setVisible(true);
        setTrayIcon();
        openLogFile();
        createMenuList();
        setTitle(selected_branch.getBranch_name());
        if (!user_grp_cd.equalsIgnoreCase("1")) {
            jmnBranchWiseCreditLimit.setVisible(false);
        }
    }

    private void setTrayIcon() {
        String path = currentDirectory + "/Resources/Images/logo.png";
        try {
            if (systemTray.isSupported()) {
                settrayImage(path);
                trayIcon.displayMessage("Skable running", "", TrayIcon.MessageType.INFO);

            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void settrayImage(String path) {
        try {
            File imageFile = new File(path);
            Image image = toolkit.getImage(imageFile.toURI().toURL());
            setIconImage(image);
            removeTrayIcon();
            trayIcon = null;
            trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            systemTray.add(trayIcon);

            trayIcon.setToolTip("Skable 1.0.0");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void removeTrayIcon() {
        if (trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }

    public static void addOnScreen(JInternalFrame inFrame, String title) {
        javax.swing.plaf.InternalFrameUI ifu = inFrame.getUI();
        ((javax.swing.plaf.basic.BasicInternalFrameUI) ifu).setNorthPane(null);
        Border b1 = new LineBorder(Color.darkGray, 5) {
        };
        tabbedPane.setBounds(0, 0, jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
        boolean flag = true;
//        if (inFrame instanceof ColourMaster || inFrame instanceof MemoryMaster || inFrame instanceof TaxMaster || inFrame instanceof GroupMaster
//                || inFrame instanceof BrandMaster || inFrame instanceof ModelMaster || inFrame instanceof CompanySetting
//                || inFrame instanceof ChangePassword || inFrame instanceof SeriesMaster || inFrame instanceof InsurenceBill
//                || inFrame instanceof TypeMaster) {
//            flag = false;
//        }
        if (flag) {
            inFrame.setLocation(0, 0);
            inFrame.setSize(jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
        }
        inFrame.setBorder(b1);
        JPanel jp = new JPanel();
        if (flag) {
            jp.setLayout(new GridLayout());
        }
        jp.add(inFrame);
        jp.setBackground(new Color(201, 212, 216));
        if (flag) {
            jp.setSize(jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
        }
        tabbedPane.addTab(title, jp);
        tabbedPane.setSelectedComponent(jp);
        inFrame.setVisible(true);
        inFrame.requestFocusInWindow();
        tabbedPane.setVisible(true);
    }

    public static void requestFocusinwindow(String jf) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equalsIgnoreCase(jf)) {
                tabbedPane.setSelectedIndex(i);
            }
        }
    }

    public static void removeFromScreen(int index) {
        tabbedPane.removeTabAt(index);
    }

    private void openLogFile() {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hh_mm_ss aaa");
            File folder = new File("LOG");
            if (!folder.exists()) {
                folder.mkdir();
            }
            File localFile = new File(folder, "logFileCatch" + "_" + sdf.format(cal.getTime()) + ".ini");
            FileWriter fw = new FileWriter(localFile);
            logFile = new BufferedWriter(fw);
            File fileName = new File(folder, "logFileUnCaught" + "_" + sdf.format(cal.getTime()) + ".ini");
            errorFile = new FileOutputStream(fileName, true);
//            start();
//            gSysLib.printToLogFile(strCurVer,null,true);
        } catch (Exception ex) {
//            gSysLib.printToLogFile("Exception at makeLogFile in MedikingDMain...", ex);
        }
    }

    private void start() {
        // Saving the orginal stream
        fileStream = new UnCaughtException(errorFile);
        //fileStream = new PrintStream(errorFile);
        // Redirecting console output to file
        System.setOut(fileStream);
        // Redirecting runtime exceptions to file
        System.setErr(fileStream);
    }

    private void createMenuList() {
        hashMenu = new HashMap<Integer, JMenuItem>();
        hashMenu.put(1, jmnGroupMst);
        hashMenu.put(2, jmnAccountMst);
        hashMenu.put(3, jmnBrandMst);
        hashMenu.put(4, jmnTypeMst);
        hashMenu.put(5, jmnModelMst);
        hashMenu.put(6, jmnColorMst);
        hashMenu.put(7, jmnMemoryMst);
        hashMenu.put(8, jmnSeriesMst);
        hashMenu.put(9, jmnTaxMst);
//
        hashMenu.put(10, jmnRDPurchase);
        hashMenu.put(11, jmnURDPurchse);
        hashMenu.put(12, jmnPurchaseReturn);
        hashMenu.put(13, jmnRetailInvoice);
        hashMenu.put(14, jmnTaxInvoice);
        hashMenu.put(15, jmnSalesReturn);
        hashMenu.put(16, jmnSalesBillNumber);
        hashMenu.put(17, jmnCashPayment);
        hashMenu.put(18, jmnCashRcpt);
        hashMenu.put(19, jmnBankPmt);
        hashMenu.put(20, jmnBankRcpt);
        hashMenu.put(21, jmnJournalEntry);
        hashMenu.put(22, jmnContra);
        hashMenu.put(23, jmnStockTransfer);
        hashMenu.put(24, jmnDcVoucher);
        hashMenu.put(25, jmnStockAdjst);
        hashMenu.put(26, jmnCreditNoteList);

        hashMenu.put(27, jmnStockLedger);
        hashMenu.put(28, jmnStockLedgerRate);
        hashMenu.put(29, jmnStockItemMonth);
        hashMenu.put(30, jmnStockSummary);
        hashMenu.put(31, jmnStockSummaryBal);
        hashMenu.put(32, jmnStockStatementIMEI);
        hashMenu.put(33, jmnStockValueStatementIMEI);
        hashMenu.put(34, jmnstockStatementAcc);
        hashMenu.put(35, jmnStockValueStmtAcc);
        hashMenu.put(36, jmnStckStmtDateWise);
        hashMenu.put(37, jmnStockStmtDateWise);
        hashMenu.put(38, jmnTagTrackTransaction);
//
        hashMenu.put(39, jmnSalesRegister);
        hashMenu.put(40, jmnSalesRegisterDetail);
        hashMenu.put(41, jmnSalesRegisterDetailACcount);
        hashMenu.put(42, jmnInsuranceRegister);
        hashMenu.put(43, jmnPurchaseRegister);
        hashMenu.put(44, jmnPurchaseRegisterDetail);
        hashMenu.put(45, jmnPurchaseRegisterDetailACcount);
        hashMenu.put(46, jmnSalesReturnRegister);
        hashMenu.put(47, jmnSalesReturnRegisterDetail);
        hashMenu.put(48, jmnSrRegAcc);
        hashMenu.put(49, jmnPurchaseReturnRegister);
        hashMenu.put(50, jmnPurchaseReturnRegisterDetail);
        hashMenu.put(51, jmnPurRetRegAcc);
        hashMenu.put(52, jmnBuyBackRegister);
        hashMenu.put(53, jmnDCRegister);
        hashMenu.put(54, jmnCreditNoteRegister);
        hashMenu.put(55, jmnStockAdjustmentRegister);
        hashMenu.put(56, jmnSalesRegisterCardWise);
        hashMenu.put(57, jmnSalesRegisterCardWiseDetail);
        hashMenu.put(58, jmnGeneralLedger);
        hashMenu.put(59, jmnGroupSummary);
        hashMenu.put(60, jmnCashBook);
        hashMenu.put(61, jmnBankBook);
        hashMenu.put(62, jmnDailySalesStatement);
        hashMenu.put(63, jmnDailySalesStatementDetail);
        hashMenu.put(64, jmnTypeWiseSalesStatement);
        hashMenu.put(65, jmnTypeWiseSalesStatementDetail);
        hashMenu.put(66, jmnTypeWisePurchase);
        hashMenu.put(67, jmnTypeWisePurchaseDetail);
        hashMenu.put(68, jmnMarginReport);
        hashMenu.put(69, jmnMarginReportByTag);
        hashMenu.put(70, jmnIMEIPSSales);
        hashMenu.put(71, jmnIMIEPSOnPurchase);
        hashMenu.put(72, jmnTypeWiseProfitStatement);
        hashMenu.put(73, jmnTypeWiseBrandWiseProfit);

        hashMenu.put(74, jmnIMEISearch);
        hashMenu.put(75, jmnTagTrack);
        hashMenu.put(76, jmnCreateUser);
        hashMenu.put(77, jmnDummyPrint);
        hashMenu.put(78, jmnUpdateUser);
        hashMenu.put(79, jmnUserRights);
        hashMenu.put(80, jmnUserGroupMaster);

        hashMenu.put(81, jmnStockInTransit);
        hashMenu.put(82, jmnStockInoutReport);
        hashMenu.put(83, jmnIMIEPSOnPurchaseWoRate);
        hashMenu.put(84, jmnPurchaseRateByTag);
        hashMenu.put(85, jmnSnapShot);

        getData();
    }

    public void getData() {
        UserAPI userAPI = lb.getRetrofit().create(UserAPI.class);
        Call<JsonObject> call = userAPI.GetUserRights("", user_grp_cd);
        try {

            JsonObject result = call.execute().body();
            if (result.get("result").getAsInt() == 1) {
                userRightsMap = new HashMap<>();
                JsonArray array = result.get("data").getAsJsonArray();
                for (int i = 0; i <= array.size(); i++) {
                    UserRightsModel model = new Gson().fromJson(array.get(i).getAsJsonObject(), UserRightsModel.class);
                    userRightsMap.put(array.get(i).getAsJsonObject().get("FORM_CD").getAsInt(), model);
                    if (hashMenu.get(array.get(i).getAsJsonObject().get("FORM_CD").getAsInt()) != null) {
                        if (model.getVIEWS().equalsIgnoreCase("1")) {
                            ((JMenuItem) hashMenu.get(array.get(i).getAsJsonObject().get("FORM_CD").getAsInt())).setVisible(true);
                        } else {
                            ((JMenuItem) hashMenu.get(array.get(i).getAsJsonObject().get("FORM_CD").getAsInt())).setVisible(false);
                        }
                    }
                }
            } else {
                lb.showMessageDailog(result.get("Cause").getAsString());
            }
        } catch (Exception ex) {

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

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jMenuBar3 = new javax.swing.JMenuBar();
        jmenuLogin = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jmnExit = new javax.swing.JMenuItem();
        jmnMaster = new javax.swing.JMenu();
        jmnGroupMst = new javax.swing.JMenuItem();
        jmnAccountMst = new javax.swing.JMenuItem();
        jmnBrandMst = new javax.swing.JMenuItem();
        jmnTypeMst = new javax.swing.JMenuItem();
        jmnModelMst = new javax.swing.JMenuItem();
        jmnColorMst = new javax.swing.JMenuItem();
        jmnMemoryMst = new javax.swing.JMenuItem();
        jmnSeriesMst = new javax.swing.JMenuItem();
        jmnTaxMst = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jmnTransaction = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jmnRDPurchase = new javax.swing.JMenuItem();
        jmnURDPurchse = new javax.swing.JMenuItem();
        jmnPurchaseReturn = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jmnRetailInvoice = new javax.swing.JMenuItem();
        jmnTaxInvoice = new javax.swing.JMenuItem();
        jmnSalesBillNumber = new javax.swing.JMenuItem();
        jmnSalesReturn = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jmnCashPayment = new javax.swing.JMenuItem();
        jmnCashRcpt = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jmnBankPmt = new javax.swing.JMenuItem();
        jmnBankRcpt = new javax.swing.JMenuItem();
        jmnJournalEntry = new javax.swing.JMenuItem();
        jmnContra = new javax.swing.JMenuItem();
        jmnStockTransfer = new javax.swing.JMenu();
        jmnsds = new javax.swing.JMenuItem();
        jMenuItem59 = new javax.swing.JMenuItem();
        jmnDcVoucher = new javax.swing.JMenu();
        jMenuItem52 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jmnStockAdjst = new javax.swing.JMenuItem();
        jmnCreditNoteList = new javax.swing.JMenuItem();
        jmnBranchWiseCreditLimit = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jmnInventory = new javax.swing.JMenu();
        jmnStockInTransit = new javax.swing.JMenuItem();
        jmnStockInoutReport = new javax.swing.JMenuItem();
        jmnStockLedger = new javax.swing.JMenuItem();
        jmnStockLedgerRate = new javax.swing.JMenuItem();
        jmnStockItemMonth = new javax.swing.JMenuItem();
        jmnStockSummary = new javax.swing.JMenuItem();
        jmnStockSummaryBal = new javax.swing.JMenuItem();
        jmnStockStatementIMEI = new javax.swing.JMenuItem();
        jmnStockValueStatementIMEI = new javax.swing.JMenuItem();
        jmnstockStatementAcc = new javax.swing.JMenuItem();
        jmnStockValueStmtAcc = new javax.swing.JMenuItem();
        jmnStckStmtDateWise = new javax.swing.JMenuItem();
        jmnStockStmtDateWise = new javax.swing.JMenuItem();
        jmnTagTrackTransaction = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jmnAccounts = new javax.swing.JMenu();
        jmnSnapShot = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jmnSalesRegister = new javax.swing.JMenuItem();
        jmnSalesRegisterDetail = new javax.swing.JMenuItem();
        jmnSalesRegisterDetailACcount = new javax.swing.JMenuItem();
        jmnInsuranceRegister = new javax.swing.JMenuItem();
        jmnPurchaseRegister = new javax.swing.JMenuItem();
        jmnPurchaseRegisterDetail = new javax.swing.JMenuItem();
        jmnPurchaseRegisterDetailACcount = new javax.swing.JMenuItem();
        jmnSalesReturnRegister = new javax.swing.JMenuItem();
        jmnSalesReturnRegisterDetail = new javax.swing.JMenuItem();
        jmnSrRegAcc = new javax.swing.JMenuItem();
        jmnPurchaseReturnRegister = new javax.swing.JMenuItem();
        jmnPurchaseReturnRegisterDetail = new javax.swing.JMenuItem();
        jmnPurRetRegAcc = new javax.swing.JMenuItem();
        jmnBuyBackRegister = new javax.swing.JMenuItem();
        jmnCreditNoteRegister = new javax.swing.JMenuItem();
        jmnDCRegister = new javax.swing.JMenuItem();
        jmnStockAdjustmentRegister = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jmnSalesReport = new javax.swing.JMenuItem();
        jmnSalesReportTax = new javax.swing.JMenuItem();
        jmnSalesReportTax1 = new javax.swing.JMenuItem();
        jmnSalesReportTax2 = new javax.swing.JMenuItem();
        jMenu11 = new javax.swing.JMenu();
        jmnSalesRegisterCardWise = new javax.swing.JMenuItem();
        jmnSalesRegisterCardWiseDetail = new javax.swing.JMenuItem();
        jmnGeneralLedger = new javax.swing.JMenuItem();
        jmnGroupSummary = new javax.swing.JMenuItem();
        jmnCashBook = new javax.swing.JMenuItem();
        jmnBankBook = new javax.swing.JMenuItem();
        asdsds = new javax.swing.JMenu();
        jmnDailySalesStatement = new javax.swing.JMenuItem();
        jmnDailySalesStatementDetail = new javax.swing.JMenuItem();
        trthg = new javax.swing.JMenu();
        jmnTypeWiseSalesStatement = new javax.swing.JMenuItem();
        jmnTypeWiseSalesStatementDetail = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jmnTypeWisePurchase = new javax.swing.JMenuItem();
        jmnTypeWisePurchaseDetail = new javax.swing.JMenuItem();
        jmnMarginReport = new javax.swing.JMenuItem();
        jmnMarginReportByTag = new javax.swing.JMenuItem();
        jmnIMIEPSOnPurchase = new javax.swing.JMenuItem();
        jmnIMIEPSOnPurchaseWoRate = new javax.swing.JMenuItem();
        jmnIMEIPSSales = new javax.swing.JMenuItem();
        jmnTypeWiseProfitStatement = new javax.swing.JMenuItem();
        jmnTypeWiseBrandWiseProfit = new javax.swing.JMenuItem();
        jmnPurchaseRateByTag = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jmnUtility = new javax.swing.JMenu();
        jMenuItem40 = new javax.swing.JMenuItem();
        jmnIMEISearch = new javax.swing.JMenuItem();
        jmnTagTrack = new javax.swing.JMenuItem();
        jmnCreateUser = new javax.swing.JMenuItem();
        jmnDummyPrint = new javax.swing.JMenuItem();
        jmnUpdateUser = new javax.swing.JMenuItem();
        jmnUserRights = new javax.swing.JMenuItem();
        jmnUserGroupMaster = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jDesktopPane1.setLayout(null);

        jmenuLogin.setMnemonic('L');
        jmenuLogin.setText("Login");

        jMenuItem3.setText("Log Off");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jmenuLogin.add(jMenuItem3);

        jmnExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        jmnExit.setMnemonic('E');
        jmnExit.setText("Exit");
        jmnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnExitActionPerformed(evt);
            }
        });
        jmenuLogin.add(jmnExit);

        jMenuBar3.add(jmenuLogin);

        jmnMaster.setMnemonic('M');
        jmnMaster.setText("Master");

        jmnGroupMst.setMnemonic('G');
        jmnGroupMst.setText("Group Master");
        jmnGroupMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnGroupMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnGroupMst);

        jmnAccountMst.setMnemonic('A');
        jmnAccountMst.setText("Account Master");
        jmnAccountMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnAccountMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnAccountMst);

        jmnBrandMst.setMnemonic('B');
        jmnBrandMst.setText("Brand Master");
        jmnBrandMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBrandMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnBrandMst);

        jmnTypeMst.setText("Type Master");
        jmnTypeMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTypeMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnTypeMst);

        jmnModelMst.setMnemonic('M');
        jmnModelMst.setText("Model Master");
        jmnModelMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnModelMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnModelMst);

        jmnColorMst.setMnemonic('C');
        jmnColorMst.setText("Colour Master");
        jmnColorMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnColorMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnColorMst);

        jmnMemoryMst.setMnemonic('M');
        jmnMemoryMst.setText("Memory Master");
        jmnMemoryMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnMemoryMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnMemoryMst);

        jmnSeriesMst.setText("Series Master");
        jmnSeriesMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSeriesMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnSeriesMst);

        jmnTaxMst.setMnemonic('T');
        jmnTaxMst.setText("Tax Master");
        jmnTaxMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTaxMstActionPerformed(evt);
            }
        });
        jmnMaster.add(jmnTaxMst);

        jMenuItem10.setText("Salesman Master");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jmnMaster.add(jMenuItem10);

        jMenuItem14.setText("Referal Master");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jmnMaster.add(jMenuItem14);

        jMenuItem24.setText("TID Master");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jmnMaster.add(jMenuItem24);

        jMenuBar3.add(jmnMaster);

        jmnTransaction.setMnemonic('T');
        jmnTransaction.setText("Transaction");

        jMenu7.setMnemonic('P');
        jMenu7.setText("Purchase");

        jmnRDPurchase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jmnRDPurchase.setMnemonic('R');
        jmnRDPurchase.setText("RD Purchase Bill");
        jmnRDPurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnRDPurchaseActionPerformed(evt);
            }
        });
        jMenu7.add(jmnRDPurchase);

        jmnURDPurchse.setMnemonic('U');
        jmnURDPurchse.setText("URD Purchase Bill");
        jmnURDPurchse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnURDPurchseActionPerformed(evt);
            }
        });
        jMenu7.add(jmnURDPurchse);

        jmnPurchaseReturn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jmnPurchaseReturn.setText("Purchase Return");
        jmnPurchaseReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseReturnActionPerformed(evt);
            }
        });
        jMenu7.add(jmnPurchaseReturn);

        jmnTransaction.add(jMenu7);

        jMenu6.setMnemonic('S');
        jMenu6.setText("Sales Imvoice");

        jmnRetailInvoice.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        jmnRetailInvoice.setMnemonic('R');
        jmnRetailInvoice.setText("Retail Invoice");
        jmnRetailInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnRetailInvoiceActionPerformed(evt);
            }
        });
        jMenu6.add(jmnRetailInvoice);

        jmnTaxInvoice.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        jmnTaxInvoice.setMnemonic('T');
        jmnTaxInvoice.setText("Tax Invoice");
        jmnTaxInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTaxInvoiceActionPerformed(evt);
            }
        });
        jMenu6.add(jmnTaxInvoice);

        jmnSalesBillNumber.setText("Sales Insurance Bill");
        jmnSalesBillNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesBillNumberActionPerformed(evt);
            }
        });
        jMenu6.add(jmnSalesBillNumber);

        jmnSalesReturn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jmnSalesReturn.setText("Sales Return");
        jmnSalesReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesReturnActionPerformed(evt);
            }
        });
        jMenu6.add(jmnSalesReturn);

        jmnTransaction.add(jMenu6);

        jMenu1.setText("Cash Entry");

        jmnCashPayment.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jmnCashPayment.setText("Cash Payment");
        jmnCashPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCashPaymentActionPerformed(evt);
            }
        });
        jMenu1.add(jmnCashPayment);

        jmnCashRcpt.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jmnCashRcpt.setText("Cash Receipt");
        jmnCashRcpt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCashRcptActionPerformed(evt);
            }
        });
        jMenu1.add(jmnCashRcpt);

        jmnTransaction.add(jMenu1);

        jMenu2.setText("Bank Entry");

        jmnBankPmt.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jmnBankPmt.setText("Bank Payment");
        jmnBankPmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBankPmtActionPerformed(evt);
            }
        });
        jMenu2.add(jmnBankPmt);

        jmnBankRcpt.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        jmnBankRcpt.setText("Bank Receipt");
        jmnBankRcpt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBankRcptActionPerformed(evt);
            }
        });
        jMenu2.add(jmnBankRcpt);

        jmnTransaction.add(jMenu2);

        jmnJournalEntry.setText("Journal Entry");
        jmnJournalEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnJournalEntryActionPerformed(evt);
            }
        });
        jmnTransaction.add(jmnJournalEntry);

        jmnContra.setText("Contra Entry");
        jmnContra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnContraActionPerformed(evt);
            }
        });
        jmnTransaction.add(jmnContra);

        jmnStockTransfer.setText("Stock Transfer");

        jmnsds.setText("Stock OUT");
        jmnsds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnsdsActionPerformed(evt);
            }
        });
        jmnStockTransfer.add(jmnsds);

        jMenuItem59.setText("Stock IN");
        jMenuItem59.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem59ActionPerformed(evt);
            }
        });
        jmnStockTransfer.add(jMenuItem59);

        jmnTransaction.add(jmnStockTransfer);

        jmnDcVoucher.setText("DC");

        jMenuItem52.setText("Outward");
        jMenuItem52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem52ActionPerformed(evt);
            }
        });
        jmnDcVoucher.add(jMenuItem52);

        jMenuItem5.setText("Inward");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jmnDcVoucher.add(jMenuItem5);

        jmnTransaction.add(jmnDcVoucher);

        jmnStockAdjst.setText("Stock Adjustment");
        jmnStockAdjst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockAdjstActionPerformed(evt);
            }
        });
        jmnTransaction.add(jmnStockAdjst);

        jmnCreditNoteList.setText("Credit Note Item List");
        jmnCreditNoteList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCreditNoteListActionPerformed(evt);
            }
        });
        jmnTransaction.add(jmnCreditNoteList);

        jmnBranchWiseCreditLimit.setText("Branch Wise Credit Limit");
        jmnBranchWiseCreditLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBranchWiseCreditLimitActionPerformed(evt);
            }
        });
        jmnTransaction.add(jmnBranchWiseCreditLimit);

        jMenuItem2.setText("Internal Cash Transfer");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jmnTransaction.add(jMenuItem2);

        jMenuItem8.setText("Bill Adjustment");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jmnTransaction.add(jMenuItem8);

        jMenu9.setText("DNCN");

        jMenuItem11.setText("DN Entry");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem11);

        jMenuItem12.setText("CN Entry");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem12);

        jmnTransaction.add(jMenu9);

        jMenuBar3.add(jmnTransaction);

        jmnInventory.setMnemonic('I');
        jmnInventory.setText("Inventory");

        jmnStockInTransit.setText("In transit Report");
        jmnStockInTransit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockInTransitActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockInTransit);

        jmnStockInoutReport.setText("Stock In Out Report");
        jmnStockInoutReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockInoutReportActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockInoutReport);

        jmnStockLedger.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.SHIFT_MASK));
        jmnStockLedger.setText("Item Ledger");
        jmnStockLedger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockLedgerActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockLedger);

        jmnStockLedgerRate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jmnStockLedgerRate.setText("Item Wise Ledger With Rate");
        jmnStockLedgerRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockLedgerRateActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockLedgerRate);

        jmnStockItemMonth.setText("Stock Itemwise Monthwise");
        jmnStockItemMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockItemMonthActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockItemMonth);

        jmnStockSummary.setText("Stock Summary");
        jmnStockSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockSummaryActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockSummary);

        jmnStockSummaryBal.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.SHIFT_MASK));
        jmnStockSummaryBal.setText("Stock Summary Balance");
        jmnStockSummaryBal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockSummaryBalActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockSummaryBal);

        jmnStockStatementIMEI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.SHIFT_MASK));
        jmnStockStatementIMEI.setText("Stock Statement IMEI");
        jmnStockStatementIMEI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockStatementIMEIActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockStatementIMEI);

        jmnStockValueStatementIMEI.setText("Stock Value Statement IMEI");
        jmnStockValueStatementIMEI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockValueStatementIMEIActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockValueStatementIMEI);

        jmnstockStatementAcc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.SHIFT_MASK));
        jmnstockStatementAcc.setText("Stock Statement Accessory");
        jmnstockStatementAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnstockStatementAccActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnstockStatementAcc);

        jmnStockValueStmtAcc.setText("Stock Value Statement Accessory");
        jmnStockValueStmtAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockValueStmtAccActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockValueStmtAcc);

        jmnStckStmtDateWise.setText("Stock Value Statement Date Wise");
        jmnStckStmtDateWise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStckStmtDateWiseActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStckStmtDateWise);

        jmnStockStmtDateWise.setText("Stock Statement Date Wise");
        jmnStockStmtDateWise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockStmtDateWiseActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnStockStmtDateWise);

        jmnTagTrackTransaction.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.SHIFT_MASK));
        jmnTagTrackTransaction.setText("Item Wise Transaction Wise Tag Tracking");
        jmnTagTrackTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTagTrackTransactionActionPerformed(evt);
            }
        });
        jmnInventory.add(jmnTagTrackTransaction);

        jMenuItem9.setText("Date Wise Stock Summary");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jmnInventory.add(jMenuItem9);

        jMenuBar3.add(jmnInventory);

        jmnAccounts.setMnemonic('A');
        jmnAccounts.setText("Accounts");

        jmnSnapShot.setText("Date Wise Group Summary");
        jmnSnapShot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSnapShotActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnSnapShot);

        jMenuItem1.setText("Branch Wise Pending Collection Report");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jmnAccounts.add(jMenuItem1);

        jMenu3.setText("Register");

        jmnSalesRegister.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.ALT_MASK));
        jmnSalesRegister.setText("Sales Register");
        jmnSalesRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnSalesRegister);

        jmnSalesRegisterDetail.setText("Sales Register Detail");
        jmnSalesRegisterDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesRegisterDetailActionPerformed(evt);
            }
        });
        jMenu3.add(jmnSalesRegisterDetail);

        jmnSalesRegisterDetailACcount.setText("Sales Register Detail Account");
        jmnSalesRegisterDetailACcount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesRegisterDetailACcountActionPerformed(evt);
            }
        });
        jMenu3.add(jmnSalesRegisterDetailACcount);

        jmnInsuranceRegister.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.ALT_MASK));
        jmnInsuranceRegister.setText("Insurance Register");
        jmnInsuranceRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnInsuranceRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnInsuranceRegister);

        jmnPurchaseRegister.setText("Purchase Register");
        jmnPurchaseRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnPurchaseRegister);

        jmnPurchaseRegisterDetail.setText("Purchase Register Detail");
        jmnPurchaseRegisterDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseRegisterDetailActionPerformed(evt);
            }
        });
        jMenu3.add(jmnPurchaseRegisterDetail);

        jmnPurchaseRegisterDetailACcount.setText("Purchase Register Account Wise");
        jmnPurchaseRegisterDetailACcount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseRegisterDetailACcountActionPerformed(evt);
            }
        });
        jMenu3.add(jmnPurchaseRegisterDetailACcount);

        jmnSalesReturnRegister.setText("Sales Return Register");
        jmnSalesReturnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesReturnRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnSalesReturnRegister);

        jmnSalesReturnRegisterDetail.setText("Sales Return Register Detail");
        jmnSalesReturnRegisterDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesReturnRegisterDetailActionPerformed(evt);
            }
        });
        jMenu3.add(jmnSalesReturnRegisterDetail);

        jmnSrRegAcc.setText("Sales Return Register Account Wise");
        jmnSrRegAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSrRegAccActionPerformed(evt);
            }
        });
        jMenu3.add(jmnSrRegAcc);

        jmnPurchaseReturnRegister.setText("Purchase Return Register");
        jmnPurchaseReturnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseReturnRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnPurchaseReturnRegister);

        jmnPurchaseReturnRegisterDetail.setText("Purchase Return Register Detail");
        jmnPurchaseReturnRegisterDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseReturnRegisterDetailActionPerformed(evt);
            }
        });
        jMenu3.add(jmnPurchaseReturnRegisterDetail);

        jmnPurRetRegAcc.setText("Purchase Return Register Account");
        jmnPurRetRegAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurRetRegAccActionPerformed(evt);
            }
        });
        jMenu3.add(jmnPurRetRegAcc);

        jmnBuyBackRegister.setText("Buy Back Register");
        jmnBuyBackRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBuyBackRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnBuyBackRegister);

        jmnCreditNoteRegister.setText("Credit Note Register");
        jmnCreditNoteRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCreditNoteRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnCreditNoteRegister);

        jmnDCRegister.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jmnDCRegister.setText("DC Register");
        jmnDCRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDCRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnDCRegister);

        jmnStockAdjustmentRegister.setText("Stock Adjustment Register");
        jmnStockAdjustmentRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockAdjustmentRegisterActionPerformed(evt);
            }
        });
        jMenu3.add(jmnStockAdjustmentRegister);

        jMenuItem4.setText("Bajaj Register");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuItem19.setText("Card Charges Register");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem19);

        jmnAccounts.add(jMenu3);

        jMenu4.setText("Tax report");

        jmnSalesReport.setText("Sales Report Retail");
        jmnSalesReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesReportActionPerformed(evt);
            }
        });
        jMenu4.add(jmnSalesReport);

        jmnSalesReportTax.setText("Sales Report Tax");
        jmnSalesReportTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesReportTaxActionPerformed(evt);
            }
        });
        jMenu4.add(jmnSalesReportTax);

        jmnSalesReportTax1.setText("Sales Return Report Tax");
        jmnSalesReportTax1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesReportTax1ActionPerformed(evt);
            }
        });
        jMenu4.add(jmnSalesReportTax1);

        jmnSalesReportTax2.setText("Purchase Return Report Tax");
        jmnSalesReportTax2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesReportTax2ActionPerformed(evt);
            }
        });
        jMenu4.add(jmnSalesReportTax2);

        jmnAccounts.add(jMenu4);

        jMenu11.setText("Card");

        jmnSalesRegisterCardWise.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.ALT_MASK));
        jmnSalesRegisterCardWise.setText("Sales Register Card Wise");
        jmnSalesRegisterCardWise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesRegisterCardWiseActionPerformed(evt);
            }
        });
        jMenu11.add(jmnSalesRegisterCardWise);

        jmnSalesRegisterCardWiseDetail.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.ALT_MASK));
        jmnSalesRegisterCardWiseDetail.setText("Sales Register Card Wise Detail");
        jmnSalesRegisterCardWiseDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesRegisterCardWiseDetailActionPerformed(evt);
            }
        });
        jMenu11.add(jmnSalesRegisterCardWiseDetail);

        jmnAccounts.add(jMenu11);

        jmnGeneralLedger.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, java.awt.event.InputEvent.ALT_MASK));
        jmnGeneralLedger.setText("General Ledger");
        jmnGeneralLedger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnGeneralLedgerActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnGeneralLedger);

        jmnGroupSummary.setText("Group Summary");
        jmnGroupSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnGroupSummaryActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnGroupSummary);

        jmnCashBook.setText("Cash Book");
        jmnCashBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCashBookActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnCashBook);

        jmnBankBook.setText("Bank Book");
        jmnBankBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBankBookActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnBankBook);

        asdsds.setText("Daily Sales Statement");

        jmnDailySalesStatement.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.ALT_MASK));
        jmnDailySalesStatement.setText("Daily Sales Statement Summarised");
        jmnDailySalesStatement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDailySalesStatementActionPerformed(evt);
            }
        });
        asdsds.add(jmnDailySalesStatement);

        jmnDailySalesStatementDetail.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.ALT_MASK));
        jmnDailySalesStatementDetail.setText("Daily Sales Statement Detail");
        jmnDailySalesStatementDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDailySalesStatementDetailActionPerformed(evt);
            }
        });
        asdsds.add(jmnDailySalesStatementDetail);

        jmnAccounts.add(asdsds);

        trthg.setText("Type Wise Sales Statement");

        jmnTypeWiseSalesStatement.setText("Type Wise Sales");
        jmnTypeWiseSalesStatement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTypeWiseSalesStatementActionPerformed(evt);
            }
        });
        trthg.add(jmnTypeWiseSalesStatement);

        jmnTypeWiseSalesStatementDetail.setText("Type Wise Sales Detail");
        jmnTypeWiseSalesStatementDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTypeWiseSalesStatementDetailActionPerformed(evt);
            }
        });
        trthg.add(jmnTypeWiseSalesStatementDetail);

        jMenuItem18.setText("Without Tag Sales Report");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        trthg.add(jMenuItem18);

        jmnAccounts.add(trthg);

        jMenu8.setText("Type Wise Purchase Statement");

        jmnTypeWisePurchase.setText("Type Wise Purchase");
        jmnTypeWisePurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTypeWisePurchaseActionPerformed(evt);
            }
        });
        jMenu8.add(jmnTypeWisePurchase);

        jmnTypeWisePurchaseDetail.setText("Type Wise Purchase Detail");
        jmnTypeWisePurchaseDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTypeWisePurchaseDetailActionPerformed(evt);
            }
        });
        jMenu8.add(jmnTypeWisePurchaseDetail);

        jmnAccounts.add(jMenu8);

        jmnMarginReport.setText("Margin Report");
        jmnMarginReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnMarginReportActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnMarginReport);

        jmnMarginReportByTag.setText("Margin Report By Tag");
        jmnMarginReportByTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnMarginReportByTagActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnMarginReportByTag);

        jmnIMIEPSOnPurchase.setText("Date Range Wise PS and Profit Party Wise");
        jmnIMIEPSOnPurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnIMIEPSOnPurchaseActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnIMIEPSOnPurchase);

        jmnIMIEPSOnPurchaseWoRate.setText("Purchase Party Wise Only Code");
        jmnIMIEPSOnPurchaseWoRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnIMIEPSOnPurchaseWoRateActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnIMIEPSOnPurchaseWoRate);

        jmnIMEIPSSales.setText("IMEI Wise PS On Sales");
        jmnIMEIPSSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnIMEIPSSalesActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnIMEIPSSales);

        jmnTypeWiseProfitStatement.setText("Type Wise Profit Statement");
        jmnTypeWiseProfitStatement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTypeWiseProfitStatementActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnTypeWiseProfitStatement);

        jmnTypeWiseBrandWiseProfit.setText("Type Wise Brand Wise Profit Statement");
        jmnTypeWiseBrandWiseProfit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTypeWiseBrandWiseProfitActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnTypeWiseBrandWiseProfit);

        jmnPurchaseRateByTag.setText("Purchase Rate BY Tag");
        jmnPurchaseRateByTag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseRateByTagActionPerformed(evt);
            }
        });
        jmnAccounts.add(jmnPurchaseRateByTag);

        jMenuItem6.setText("Sales Report New");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jmnAccounts.add(jMenuItem6);

        jMenuItem13.setText("ModelWise Monthwise Sales Statement");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jmnAccounts.add(jMenuItem13);

        jMenuBar3.add(jmnAccounts);

        jmnUtility.setMnemonic('U');
        jmnUtility.setText("Utility");

        jMenuItem40.setText("Change Password");
        jMenuItem40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem40ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem40);

        jmnIMEISearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_MASK));
        jmnIMEISearch.setText("IMEI Wise Search");
        jmnIMEISearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnIMEISearchActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnIMEISearch);

        jmnTagTrack.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        jmnTagTrack.setText("TAG Track");
        jmnTagTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnTagTrackActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnTagTrack);

        jmnCreateUser.setText("Create User");
        jmnCreateUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCreateUserActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnCreateUser);

        jmnDummyPrint.setText("Dummy Print");
        jmnDummyPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDummyPrintActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnDummyPrint);

        jmnUpdateUser.setText("Update User");
        jmnUpdateUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnUpdateUserActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnUpdateUser);

        jmnUserRights.setText("User Rights");
        jmnUserRights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnUserRightsActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnUserRights);

        jmnUserGroupMaster.setText("User Group Master");
        jmnUserGroupMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnUserGroupMasterActionPerformed(evt);
            }
        });
        jmnUtility.add(jmnUserGroupMaster);

        jMenuItem7.setText("Bill Track");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem7);

        jMenuItem17.setText("Phonebook");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem17);

        jMenuItem15.setText("Notes");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem15);

        jMenuItem16.setText("EOD");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jmnUtility.add(jMenuItem16);

        jMenuBar3.add(jmnUtility);

        setJMenuBar(jMenuBar3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnExitActionPerformed
        // TODO add your handling code here:
        lb.confirmDialog("Do you want to Exit from System?");
        if (lb.type) {
            System.exit(0);
        }
    }//GEN-LAST:event_jmnExitActionPerformed

    private void jmnGroupMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnGroupMstActionPerformed
        // TODO add your handling code here:
        GroupMasterView gmv = new GroupMasterView(1);
        addOnScreen(gmv, "Group Master View");
    }//GEN-LAST:event_jmnGroupMstActionPerformed

    private void jmnAccountMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnAccountMstActionPerformed
        // TODO add your handling code here:
        AccountMasterView av = new AccountMasterView(2);
        addOnScreen(av, "Account Master View");
    }//GEN-LAST:event_jmnAccountMstActionPerformed

    private void jmnBrandMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBrandMstActionPerformed
        // TODO add your handling code here:
        BrandMasterView bmv = new BrandMasterView(3);
        addOnScreen(bmv, "Brand Master");
    }//GEN-LAST:event_jmnBrandMstActionPerformed

    private void jmnTypeMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTypeMstActionPerformed
        // TODO add your handling code here:
        TypeMasterView tmv = new TypeMasterView(4);
        addOnScreen(tmv, "Type Master View");
    }//GEN-LAST:event_jmnTypeMstActionPerformed

    private void jmnModelMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnModelMstActionPerformed
        // TODO add your handling code here:
        ModelMasterView mv = new ModelMasterView(5);
        addOnScreen(mv, "Model Master View");
    }//GEN-LAST:event_jmnModelMstActionPerformed

    private void jmnColorMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnColorMstActionPerformed
        // TODO add your handling code here:
        ColorMasterView cmv = new ColorMasterView(6);
        addOnScreen(cmv, "Color Master View");
    }//GEN-LAST:event_jmnColorMstActionPerformed

    private void jmnMemoryMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnMemoryMstActionPerformed
        // TODO add your handling code here:
        MemoryMasterView mmv = new MemoryMasterView(7);
        addOnScreen(mmv, "Memory Master View");
    }//GEN-LAST:event_jmnMemoryMstActionPerformed

    private void jmnSeriesMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSeriesMstActionPerformed
        // TODO add your handling code here:
        SeriesMasterView smv = new SeriesMasterView(8);
        addOnScreen(smv, "Series Master View");
    }//GEN-LAST:event_jmnSeriesMstActionPerformed

    private void jmnTaxMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTaxMstActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jmnTaxMstActionPerformed

    private void jmnRDPurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnRDPurchaseActionPerformed
        // TODO add your handling code here:
        PurchaseView pv = new PurchaseView(0, 10);
        addOnScreen(pv, "RD Purchase View");
    }//GEN-LAST:event_jmnRDPurchaseActionPerformed

    private void jmnURDPurchseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnURDPurchseActionPerformed
        // TODO add your handling code here:
        PurchaseView pv = new PurchaseView(1, 11);
        addOnScreen(pv, "URD Purchase Bill View");
    }//GEN-LAST:event_jmnURDPurchseActionPerformed

    private void jmnPurchaseReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseReturnActionPerformed
        // TODO add your handling code here:
        PurchaseReturnView prv = new PurchaseReturnView(0, 12);
        addOnScreen(prv, "Purchase Return View");
    }//GEN-LAST:event_jmnPurchaseReturnActionPerformed

    private void jmnRetailInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnRetailInvoiceActionPerformed
        // TODO add your handling code here:
        SalesView sb = new SalesView(0, 13);
        addOnScreen(sb, "Retail Invoice View");
    }//GEN-LAST:event_jmnRetailInvoiceActionPerformed

    private void jmnTaxInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTaxInvoiceActionPerformed
        // TODO add your handling code here:
        SalesView sb = new SalesView(1, 14);
        addOnScreen(sb, "Tax Invoice View");
    }//GEN-LAST:event_jmnTaxInvoiceActionPerformed

    private void jmnSalesReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesReturnActionPerformed
        // TODO add your handling code here:
        SalesReturnView srv = new SalesReturnView(0, 15);
        addOnScreen(srv, "Sales Return View");
    }//GEN-LAST:event_jmnSalesReturnActionPerformed

    private void jmnSalesBillNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesBillNumberActionPerformed
        // TODO add your handling code here:
        SalesView sb = new SalesView(2, 16);
        addOnScreen(sb, "Retail Insurance Invoice View");
    }//GEN-LAST:event_jmnSalesBillNumberActionPerformed

    private void jmnCashPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCashPaymentActionPerformed
        // TODO add your handling code here:
        CashPaymentReceiptView cprv = new CashPaymentReceiptView(0, 17);
        addOnScreen(cprv, "Cash Payment View");
    }//GEN-LAST:event_jmnCashPaymentActionPerformed

    private void jmnCashRcptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCashRcptActionPerformed
        // TODO add your handling code here:
        CashPaymentReceiptView cprv = new CashPaymentReceiptView(1, 18);
        addOnScreen(cprv, "Cash Receipt View");
    }//GEN-LAST:event_jmnCashRcptActionPerformed

    private void jmnBankPmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBankPmtActionPerformed
        // TODO add your handling code here:
        BankPaymentReceiptView bpr = new BankPaymentReceiptView(0, 19);
        addOnScreen(bpr, "Bank Payment View");
    }//GEN-LAST:event_jmnBankPmtActionPerformed

    private void jmnBankRcptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBankRcptActionPerformed
        // TODO add your handling code here:
        BankPaymentReceiptView bpr = new BankPaymentReceiptView(1, 20);
        addOnScreen(bpr, "Bank Receipt View");
    }//GEN-LAST:event_jmnBankRcptActionPerformed

    private void jmnJournalEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnJournalEntryActionPerformed
//        // TODO add your handling code here:
        JournalVoucherView jv = new JournalVoucherView(21);
        addOnScreen(jv, "Journal Voucher View");
    }//GEN-LAST:event_jmnJournalEntryActionPerformed

    private void jmnContraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnContraActionPerformed
        // TODO add your handling code here:
        ContraVoucherView cv = new ContraVoucherView(22);
        addOnScreen(cv, "Contra Voucher");
    }//GEN-LAST:event_jmnContraActionPerformed

    private void jmnsdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnsdsActionPerformed
        // TODO add your handling code here:
        StockTransferView stk = new StockTransferView(0, 23);
        addOnScreen(stk, "Stock Transfer Out");
    }//GEN-LAST:event_jmnsdsActionPerformed

    private void jMenuItem59ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem59ActionPerformed
        // TODO add your handling code here:
        StockTransferView stk = new StockTransferView(1, 23);
        addOnScreen(stk, "Stock Transfer In");
    }//GEN-LAST:event_jMenuItem59ActionPerformed

    private void jMenuItem52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem52ActionPerformed
        // TODO add your handling code here:
        DCView dcv = new DCView(0, 24);
        addOnScreen(dcv, "DC Issue View");
    }//GEN-LAST:event_jMenuItem52ActionPerformed

    private void jmnStockAdjstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockAdjstActionPerformed
        // TODO add your handling code here:
//        StockAdjustmentView stkvAdjustmentView = new StockAdjustmentView(0, 25);
//        addOnScreen(stkvAdjustmentView, "Stock Adjustment View");
    }//GEN-LAST:event_jmnStockAdjstActionPerformed

    private void jmnCreditNoteListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCreditNoteListActionPerformed
        // TODO add your handling code here:
        CreditNoteListReport cn = new CreditNoteListReport();
        addOnScreen(cn, "Credit Note List Report");
    }//GEN-LAST:event_jmnCreditNoteListActionPerformed

    private void jmnStockLedgerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockLedgerActionPerformed
        // TODO add your handling code here:
        StockLedger stk = new StockLedger();
        addOnScreen(stk, "Item Ledger");
    }//GEN-LAST:event_jmnStockLedgerActionPerformed

    private void jmnStockLedgerRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockLedgerRateActionPerformed
        // TODO add your handling code here:
        StockLedgerRate stk = new StockLedgerRate();
        addOnScreen(stk, "Item Ledger Rate");
    }//GEN-LAST:event_jmnStockLedgerRateActionPerformed

    private void jmnStockItemMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockItemMonthActionPerformed
        // TODO add your handling code here:
        StockSummaryDetail sd = new StockSummaryDetail();
        addOnScreen(sd, "Stock Itemwise Monthwise");
    }//GEN-LAST:event_jmnStockItemMonthActionPerformed

    private void jmnStockSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockSummaryActionPerformed
        // TODO add your handling code here:
        StockSummary ss = new StockSummary();
        addOnScreen(ss, "Stock Summary");
    }//GEN-LAST:event_jmnStockSummaryActionPerformed

    private void jmnStockSummaryBalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockSummaryBalActionPerformed
        // TODO add your handling code here:
        StockSummaryBalance ssb = new StockSummaryBalance();
        addOnScreen(ssb, "Stock Summary Balance");
    }//GEN-LAST:event_jmnStockSummaryBalActionPerformed

    private void jmnStockStatementIMEIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockStatementIMEIActionPerformed
        // TODO add your handling code here:
        StockValueIMEI st = new StockValueIMEI();
        addOnScreen(st, "Stock Statement IMEI");
    }//GEN-LAST:event_jmnStockStatementIMEIActionPerformed

    private void jmnStockValueStatementIMEIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockValueStatementIMEIActionPerformed
        // TODO add your handling code here:
        StockValueStatement st = new StockValueStatement();
        addOnScreen(st, "Stock Value Statement IMEI");
    }//GEN-LAST:event_jmnStockValueStatementIMEIActionPerformed

    private void jmnstockStatementAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnstockStatementAccActionPerformed
        // TODO add your handling code here:
        StockValueIMEIAccess ssa = new StockValueIMEIAccess();
        addOnScreen(ssa, "Stock Statement Accessory");
    }//GEN-LAST:event_jmnstockStatementAccActionPerformed

    private void jmnStockValueStmtAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockValueStmtAccActionPerformed
        // TODO add your handling code here:
        StockValueStatementAccess ssa = new StockValueStatementAccess();
        addOnScreen(ssa, "Stock Value Statement Accessory");
    }//GEN-LAST:event_jmnStockValueStmtAccActionPerformed

    private void jmnStckStmtDateWiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStckStmtDateWiseActionPerformed
        // TODO add your handling code here:
        StockValueStatementDateWise st = new StockValueStatementDateWise();
        addOnScreen(st, "Stock Value Statement Date Wise");
    }//GEN-LAST:event_jmnStckStmtDateWiseActionPerformed

    private void jmnStockStmtDateWiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockStmtDateWiseActionPerformed
        // TODO add your handling code here:
        StockStatementDateWise st = new StockStatementDateWise();
        addOnScreen(st, "Stock Statement Date Wise");
    }//GEN-LAST:event_jmnStockStmtDateWiseActionPerformed

    private void jmnTagTrackTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTagTrackTransactionActionPerformed
        // TODO add your handling code here:
        TagTractQtyWise ttq = new TagTractQtyWise();
        addOnScreen(ttq, "Tag Track Transaction wise");
    }//GEN-LAST:event_jmnTagTrackTransactionActionPerformed

    private void jmnSalesRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesRegisterActionPerformed
        // TODO add your handling code here:
        SalesRegister sr = new SalesRegister();
        addOnScreen(sr, "Sales Register");
    }//GEN-LAST:event_jmnSalesRegisterActionPerformed

    private void jmnSalesRegisterDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesRegisterDetailActionPerformed
        // TODO add your handling code here:
        SalesRegisterDetail srd = new SalesRegisterDetail();
        addOnScreen(srd, "Sales Register Detail");
    }//GEN-LAST:event_jmnSalesRegisterDetailActionPerformed

    private void jmnSalesRegisterDetailACcountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesRegisterDetailACcountActionPerformed
        // TODO add your handling code here:
        SalesRegisterDetailAccount sda = new SalesRegisterDetailAccount();
        addOnScreen(sda, "Sales Register Detail Account");
    }//GEN-LAST:event_jmnSalesRegisterDetailACcountActionPerformed

    private void jmnInsuranceRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnInsuranceRegisterActionPerformed
        // TODO add your handling code here:
        InsuranceRegister inr = new InsuranceRegister();
        addOnScreen(inr, "Insurance Register");
    }//GEN-LAST:event_jmnInsuranceRegisterActionPerformed

    private void jmnPurchaseRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseRegisterActionPerformed
        // TODO add your handling code here:
        PurchaseRegister pr = new PurchaseRegister();
        addOnScreen(pr, "Purchase Register");
    }//GEN-LAST:event_jmnPurchaseRegisterActionPerformed

    private void jmnPurchaseRegisterDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseRegisterDetailActionPerformed
        // TODO add your handling code here:
        PurchaseRegisterDetail pr = new PurchaseRegisterDetail();
        addOnScreen(pr, "Purchase Register Detail");
    }//GEN-LAST:event_jmnPurchaseRegisterDetailActionPerformed

    private void jmnPurchaseRegisterDetailACcountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseRegisterDetailACcountActionPerformed
        // TODO add your handling code here:
        PurchaseRegisterDetailAccount prda = new PurchaseRegisterDetailAccount();
        addOnScreen(prda, "Purchase Register Detail Account");
    }//GEN-LAST:event_jmnPurchaseRegisterDetailACcountActionPerformed

    private void jmnSalesReturnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesReturnRegisterActionPerformed
        // TODO add your handling code here:
        SalesReturnRegister srs = new SalesReturnRegister();
        addOnScreen(srs, "Sales Return Register");
    }//GEN-LAST:event_jmnSalesReturnRegisterActionPerformed

    private void jmnSalesReturnRegisterDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesReturnRegisterDetailActionPerformed
        // TODO add your handling code here:
        SalesReturnRegisterDetail srs = new SalesReturnRegisterDetail();
        addOnScreen(srs, "Sales Return Register Detail");
    }//GEN-LAST:event_jmnSalesReturnRegisterDetailActionPerformed

    private void jmnSrRegAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSrRegAccActionPerformed
        // TODO add your handling code here:
        SalesReturnRegisterDetailAccount sra = new SalesReturnRegisterDetailAccount();
        addOnScreen(sra, "Sales Return Register Account Wise");
    }//GEN-LAST:event_jmnSrRegAccActionPerformed

    private void jmnPurchaseReturnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseReturnRegisterActionPerformed
        // TODO add your handling code here:
        PurchaseReturnRegister pr = new PurchaseReturnRegister();
        addOnScreen(pr, "Purcahse Return Register");
    }//GEN-LAST:event_jmnPurchaseReturnRegisterActionPerformed

    private void jmnPurchaseReturnRegisterDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseReturnRegisterDetailActionPerformed
        // TODO add your handling code here:
        PurchaseReturnRegisterDetail pr = new PurchaseReturnRegisterDetail();
        addOnScreen(pr, "Purcahse Return Register Detail");
    }//GEN-LAST:event_jmnPurchaseReturnRegisterDetailActionPerformed

    private void jmnPurRetRegAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurRetRegAccActionPerformed
        // TODO add your handling code here:
        PurchsaeReturnRegisterDetailAccount pra = new PurchsaeReturnRegisterDetailAccount();
        addOnScreen(pra, "Purchase Return Register Account");
    }//GEN-LAST:event_jmnPurRetRegAccActionPerformed

    private void jmnBuyBackRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBuyBackRegisterActionPerformed
        // TODO add your handling code here:
        BuyBackRegister bbr = new BuyBackRegister();
        addOnScreen(bbr, "Buy Back Register");
    }//GEN-LAST:event_jmnBuyBackRegisterActionPerformed

    private void jmnCreditNoteRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCreditNoteRegisterActionPerformed
        // TODO add your handling code here:
        CreditNoteRegister cnr = new CreditNoteRegister();
        addOnScreen(cnr, "Cred Note Register");
    }//GEN-LAST:event_jmnCreditNoteRegisterActionPerformed

    private void jmnDCRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDCRegisterActionPerformed
        // TODO add your handling code here:
        DCRegister dcr = new DCRegister();
        addOnScreen(dcr, "DC Outward Register");
    }//GEN-LAST:event_jmnDCRegisterActionPerformed

    private void jmnStockAdjustmentRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockAdjustmentRegisterActionPerformed
        // TODO add your handling code here:
//        StockAdjustmentRegister stkReg = new StockAdjustmentRegister();
//        addOnScreen(stkReg, "Stock Adjustment Register");
    }//GEN-LAST:event_jmnStockAdjustmentRegisterActionPerformed

    private void jmnSalesRegisterCardWiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesRegisterCardWiseActionPerformed
        // TODO add your handling code here:
        SalesRegisterCardWise srcd = new SalesRegisterCardWise();
        addOnScreen(srcd, "Sales Register Card Wise");
    }//GEN-LAST:event_jmnSalesRegisterCardWiseActionPerformed

    private void jmnSalesRegisterCardWiseDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesRegisterCardWiseDetailActionPerformed
        // TODO add your handling code here:
        SalesRegisterDetailCardWise srcd = new SalesRegisterDetailCardWise();
        addOnScreen(srcd, "Sales Register Detail Card Wise");
    }//GEN-LAST:event_jmnSalesRegisterCardWiseDetailActionPerformed

    private void jmnGeneralLedgerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnGeneralLedgerActionPerformed
        GeneralLedger1 gl = new GeneralLedger1();
        addOnScreen(gl, "General Ledger");
    }//GEN-LAST:event_jmnGeneralLedgerActionPerformed

    private void jmnGroupSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnGroupSummaryActionPerformed
        GroupSummary gs = new GroupSummary();
        addOnScreen(gs, "Group Summary");
    }//GEN-LAST:event_jmnGroupSummaryActionPerformed

    private void jmnCashBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCashBookActionPerformed
        GeneralLedger1 gl = new GeneralLedger1();
        addOnScreen(gl, "Cash Book");
    }//GEN-LAST:event_jmnCashBookActionPerformed

    private void jmnBankBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBankBookActionPerformed
        BankBook gl = new BankBook();
        addOnScreen(gl, "Bank Book");
    }//GEN-LAST:event_jmnBankBookActionPerformed

    private void jmnDailySalesStatementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDailySalesStatementActionPerformed
        // TODO add your handling code here:
        DailySalesStatement dss = new DailySalesStatement();
        addOnScreen(dss, "Daily Sales Statement Summarised");
    }//GEN-LAST:event_jmnDailySalesStatementActionPerformed

    private void jmnDailySalesStatementDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDailySalesStatementDetailActionPerformed
        // TODO add your handling code here:
        DailySalesStatementDetail dsd = new DailySalesStatementDetail();
        addOnScreen(dsd, "Daily Sales Satement Detail");
    }//GEN-LAST:event_jmnDailySalesStatementDetailActionPerformed

    private void jmnTypeWiseSalesStatementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTypeWiseSalesStatementActionPerformed
        // TODO add your handling code here:
        TypeWiseSales tws = new TypeWiseSales();
        addOnScreen(tws, "Type Wise Sales");
    }//GEN-LAST:event_jmnTypeWiseSalesStatementActionPerformed

    private void jmnTypeWiseSalesStatementDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTypeWiseSalesStatementDetailActionPerformed
        // TODO add your handling code here:
        TypeWiseSalesDetail tws = new TypeWiseSalesDetail();
        addOnScreen(tws, "Type Wise Sales Detail");
    }//GEN-LAST:event_jmnTypeWiseSalesStatementDetailActionPerformed

    private void jmnTypeWisePurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTypeWisePurchaseActionPerformed
        // TODO add your handling code here:
        TypeWisePurchase tws = new TypeWisePurchase();
        addOnScreen(tws, "Type Wise Purchase");
    }//GEN-LAST:event_jmnTypeWisePurchaseActionPerformed

    private void jmnTypeWisePurchaseDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTypeWisePurchaseDetailActionPerformed
        // TODO add your handling code here:
        TypeWisePurchaseDetail tws = new TypeWisePurchaseDetail();
        addOnScreen(tws, "Type Wise Purcahse Detail");
    }//GEN-LAST:event_jmnTypeWisePurchaseDetailActionPerformed

    private void jmnMarginReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnMarginReportActionPerformed
        // TODO add your handling code here:
        MarginReport mr = new MarginReport();
        addOnScreen(mr, "Margin Report");
    }//GEN-LAST:event_jmnMarginReportActionPerformed

    private void jmnMarginReportByTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnMarginReportByTagActionPerformed
        // TODO add your handling code here:
        MarginReportByTag tagp = new MarginReportByTag();
        addOnScreen(tagp, "Margin Report By Tag");
    }//GEN-LAST:event_jmnMarginReportByTagActionPerformed

    private void jmnIMIEPSOnPurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnIMIEPSOnPurchaseActionPerformed
        // TODO add your handling code here:
        ItemWisePS ips = new ItemWisePS();
        addOnScreen(ips, "Date Range Wise PS and Profit Party Wise");
    }//GEN-LAST:event_jmnIMIEPSOnPurchaseActionPerformed

    private void jmnIMIEPSOnPurchaseWoRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnIMIEPSOnPurchaseWoRateActionPerformed
        // TODO add your handling code here:
        ItemWisePSWoRate ips = new ItemWisePSWoRate();
        addOnScreen(ips, "Purchase Party Wise Only Code");
    }//GEN-LAST:event_jmnIMIEPSOnPurchaseWoRateActionPerformed

    private void jmnIMEIPSSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnIMEIPSSalesActionPerformed
        // TODO add your handling code here:
        ItemWisePSSales ips = new ItemWisePSSales();
        addOnScreen(ips, "IMEI Wise PS Sales");
    }//GEN-LAST:event_jmnIMEIPSSalesActionPerformed

    private void jmnTypeWiseProfitStatementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTypeWiseProfitStatementActionPerformed
        // TODO add your handling code here:
        TypeWiseProfitStatement tps = new TypeWiseProfitStatement();
        addOnScreen(tps, "Type Wise Profit Statement");
    }//GEN-LAST:event_jmnTypeWiseProfitStatementActionPerformed

    private void jmnTypeWiseBrandWiseProfitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTypeWiseBrandWiseProfitActionPerformed
        // TODO add your handling code here:
        TypeWiseBrandWiseProfitStatement tps = new TypeWiseBrandWiseProfitStatement();
        addOnScreen(tps, "Type Wise Brandwise Profit Statement");
    }//GEN-LAST:event_jmnTypeWiseBrandWiseProfitActionPerformed

    private void jmnPurchaseRateByTagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseRateByTagActionPerformed
        // TODO add your handling code here:
        PurchaseRateBYTag prt = new PurchaseRateBYTag();
        addOnScreen(prt, "Purchase Rate By Tag");
    }//GEN-LAST:event_jmnPurchaseRateByTagActionPerformed

    private void jmnSnapShotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSnapShotActionPerformed
        // TODO add your handling code here:
        DateWiseGroupSummary ss = new DateWiseGroupSummary();
        addOnScreen(ss, "Date Wise Group Summary");
    }//GEN-LAST:event_jmnSnapShotActionPerformed

    private void jMenuItem40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem40ActionPerformed
        // TODO add your handling code here:
        ChangePassword cp = new ChangePassword();
        addOnScreen(cp, "Change Password");
    }//GEN-LAST:event_jMenuItem40ActionPerformed

    private void jmnIMEISearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnIMEISearchActionPerformed
        // TODO add your handling code here:
        IMEISearch iSearch = new IMEISearch();
        addOnScreen(iSearch, "IMEI Search");
    }//GEN-LAST:event_jmnIMEISearchActionPerformed

    private void jmnTagTrackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnTagTrackActionPerformed
        // TODO add your handling code here:
        TagTrack tt = new TagTrack();
        addOnScreen(tt, "Tag Track");
    }//GEN-LAST:event_jmnTagTrackActionPerformed

    private void jmnCreateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCreateUserActionPerformed
        // TODO add your handling code here:
        CreateUser cu = new CreateUser();
        addOnScreen(cu, "Create User");
    }//GEN-LAST:event_jmnCreateUserActionPerformed

    private void jmnDummyPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDummyPrintActionPerformed
        // TODO add your handling code here:
//        DummyPrint dp = new DummyPrint();
//        addOnScreen(dp, "Dummy Print");
    }//GEN-LAST:event_jmnDummyPrintActionPerformed

    private void jmnUpdateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnUpdateUserActionPerformed
        // TODO add your handling code here:
        UserMasterView cu = new UserMasterView(78);
        addOnScreen(cu, "user Master View");
    }//GEN-LAST:event_jmnUpdateUserActionPerformed

    private void jmnUserRightsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnUserRightsActionPerformed
        // TODO add your handling code here:
        UserPermission up = new UserPermission();
        addOnScreen(up, "User Permission");
    }//GEN-LAST:event_jmnUserRightsActionPerformed

    private void jmnUserGroupMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnUserGroupMasterActionPerformed
        // TODO add your handling code here:
        UserGroupMasterView ugmv = new UserGroupMasterView(80);
        addOnScreen(ugmv, "User Group Master View");
    }//GEN-LAST:event_jmnUserGroupMasterActionPerformed

    private void jmnStockInTransitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockInTransitActionPerformed
        // TODO add your handling code here:
        StockTransferPendingReport spr = new StockTransferPendingReport();
        addOnScreen(spr, "In Transit Report");
    }//GEN-LAST:event_jmnStockInTransitActionPerformed

    private void jmnStockInoutReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockInoutReportActionPerformed
        // TODO add your handling code here:
        StockInOUTReport sio = new StockInOUTReport();
        addOnScreen(sio, "Stock Inout Report");
    }//GEN-LAST:event_jmnStockInoutReportActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        BranchWisePendingReport brp = new BranchWisePendingReport();
        addOnScreen(brp, "Branch Wise Pending Collection Report");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jmnBranchWiseCreditLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBranchWiseCreditLimitActionPerformed
        // TODO add your handling code here:
        BranchWiseLimit bl = new BranchWiseLimit(null, true);
        bl.setVisible(true);
    }//GEN-LAST:event_jmnBranchWiseCreditLimitActionPerformed

    private void jmnSalesReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesReportActionPerformed
        // TODO add your handling code here:
        SalesReport sr = new SalesReport(0);
        addOnScreen(sr, "Sales Report");
    }//GEN-LAST:event_jmnSalesReportActionPerformed

    private void jmnSalesReportTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesReportTaxActionPerformed
        // TODO add your handling code here:
        SalesReport sr = new SalesReport(1);
        addOnScreen(sr, "Sales Report Tax");
    }//GEN-LAST:event_jmnSalesReportTaxActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        InternalCashTransfer ict = new InternalCashTransfer(null, true);
        ict.setLocationRelativeTo(null);
        ict.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        Login lg = new Login();
        lg.setLocationRelativeTo(null);
        lg.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        BajajReport br = new BajajReport();
        addOnScreen(br, "Bajaj Report");
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        DCView dcv = new DCView(1, 24);
        addOnScreen(dcv, "DC Inward");
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jmnSalesReportTax1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesReportTax1ActionPerformed
        // TODO add your handling code here:
        SalesReport sr = new SalesReport(2);
        addOnScreen(sr, "Sales Return Report");
    }//GEN-LAST:event_jmnSalesReportTax1ActionPerformed

    private void jmnSalesReportTax2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesReportTax2ActionPerformed
        // TODO add your handling code here:
        SalesReport sr = new SalesReport(3);
        addOnScreen(sr, "Purchase Return Report Tax");
    }//GEN-LAST:event_jmnSalesReportTax2ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        SalesReportBrijesh srn = new SalesReportBrijesh();
        addOnScreen(srn, "Sales Report New");
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        BillTrack br = new BillTrack(WIDTH, 13);
        addOnScreen(br, "Bill Track");
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
        ListBill lb = new ListBill();
        addOnScreen(lb, "List Bill");
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        DayWiseStockSummary dts = new DayWiseStockSummary();
        addOnScreen(dts, "Date Wise Stock Summary");
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        SalesmanMaster sm = new SalesmanMaster(3);
        addOnScreen(sm, "Salesman Master");
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
        ModelWiseMonthWiseSalesStatement mmss = new ModelWiseMonthWiseSalesStatement();
        addOnScreen(mmss, "Modelwise Monthwise Sales Statement");
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        DNCNView dncn = new DNCNView(0, 29);
        addOnScreen(dncn, "DN View");
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        DNCNView dncn = new DNCNView(1, 30);
        addOnScreen(dncn, "CN View");
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        // TODO add your handling code here:
        RefMasterView ref = new RefMasterView(3);
        addOnScreen(ref, "Refral Master");
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        // TODO add your handling code here:
        NotesView nv = new NotesView();
        addOnScreen(nv, "Notes View");
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        // TODO add your handling code here:
        EOD eod = new EOD();
        addOnScreen(eod, "EOD Report");
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        // TODO add your handling code here:
        PhoneBookNew mm = new PhoneBookNew();
        addOnScreen(mm, "Phonebook");
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // TODO add your handling code here:
        WithoutTagSalesReport ws = new WithoutTagSalesReport();
        addOnScreen(ws, "Without Tag Sales Report");
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        // TODO add your handling code here:
        CardChargesRegister crr = new CardChargesRegister();
        addOnScreen(crr, "Card Charges Register");
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        // TODO add your handling code here:
        TidMasterView tm = new TidMasterView(13);
        addOnScreen(tm, "TID Master View");
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu asdsds;
    private static javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem40;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem52;
    private javax.swing.JMenuItem jMenuItem59;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenu jmenuLogin;
    private javax.swing.JMenuItem jmnAccountMst;
    private javax.swing.JMenu jmnAccounts;
    private javax.swing.JMenuItem jmnBankBook;
    private javax.swing.JMenuItem jmnBankPmt;
    private javax.swing.JMenuItem jmnBankRcpt;
    private javax.swing.JMenuItem jmnBranchWiseCreditLimit;
    private javax.swing.JMenuItem jmnBrandMst;
    private javax.swing.JMenuItem jmnBuyBackRegister;
    private javax.swing.JMenuItem jmnCashBook;
    private javax.swing.JMenuItem jmnCashPayment;
    private javax.swing.JMenuItem jmnCashRcpt;
    private javax.swing.JMenuItem jmnColorMst;
    private javax.swing.JMenuItem jmnContra;
    private javax.swing.JMenuItem jmnCreateUser;
    private javax.swing.JMenuItem jmnCreditNoteList;
    private javax.swing.JMenuItem jmnCreditNoteRegister;
    private javax.swing.JMenuItem jmnDCRegister;
    private javax.swing.JMenuItem jmnDailySalesStatement;
    private javax.swing.JMenuItem jmnDailySalesStatementDetail;
    private javax.swing.JMenu jmnDcVoucher;
    private javax.swing.JMenuItem jmnDummyPrint;
    private javax.swing.JMenuItem jmnExit;
    private javax.swing.JMenuItem jmnGeneralLedger;
    private javax.swing.JMenuItem jmnGroupMst;
    private javax.swing.JMenuItem jmnGroupSummary;
    private javax.swing.JMenuItem jmnIMEIPSSales;
    private javax.swing.JMenuItem jmnIMEISearch;
    private javax.swing.JMenuItem jmnIMIEPSOnPurchase;
    private javax.swing.JMenuItem jmnIMIEPSOnPurchaseWoRate;
    private javax.swing.JMenuItem jmnInsuranceRegister;
    private javax.swing.JMenu jmnInventory;
    private javax.swing.JMenuItem jmnJournalEntry;
    private javax.swing.JMenuItem jmnMarginReport;
    private javax.swing.JMenuItem jmnMarginReportByTag;
    private javax.swing.JMenu jmnMaster;
    private javax.swing.JMenuItem jmnMemoryMst;
    private javax.swing.JMenuItem jmnModelMst;
    private javax.swing.JMenuItem jmnPurRetRegAcc;
    private javax.swing.JMenuItem jmnPurchaseRateByTag;
    private javax.swing.JMenuItem jmnPurchaseRegister;
    private javax.swing.JMenuItem jmnPurchaseRegisterDetail;
    private javax.swing.JMenuItem jmnPurchaseRegisterDetailACcount;
    private javax.swing.JMenuItem jmnPurchaseReturn;
    private javax.swing.JMenuItem jmnPurchaseReturnRegister;
    private javax.swing.JMenuItem jmnPurchaseReturnRegisterDetail;
    private javax.swing.JMenuItem jmnRDPurchase;
    private javax.swing.JMenuItem jmnRetailInvoice;
    private javax.swing.JMenuItem jmnSalesBillNumber;
    private javax.swing.JMenuItem jmnSalesRegister;
    private javax.swing.JMenuItem jmnSalesRegisterCardWise;
    private javax.swing.JMenuItem jmnSalesRegisterCardWiseDetail;
    private javax.swing.JMenuItem jmnSalesRegisterDetail;
    private javax.swing.JMenuItem jmnSalesRegisterDetailACcount;
    private javax.swing.JMenuItem jmnSalesReport;
    private javax.swing.JMenuItem jmnSalesReportTax;
    private javax.swing.JMenuItem jmnSalesReportTax1;
    private javax.swing.JMenuItem jmnSalesReportTax2;
    private javax.swing.JMenuItem jmnSalesReturn;
    private javax.swing.JMenuItem jmnSalesReturnRegister;
    private javax.swing.JMenuItem jmnSalesReturnRegisterDetail;
    private javax.swing.JMenuItem jmnSeriesMst;
    private javax.swing.JMenuItem jmnSnapShot;
    private javax.swing.JMenuItem jmnSrRegAcc;
    private javax.swing.JMenuItem jmnStckStmtDateWise;
    private javax.swing.JMenuItem jmnStockAdjst;
    private javax.swing.JMenuItem jmnStockAdjustmentRegister;
    private javax.swing.JMenuItem jmnStockInTransit;
    private javax.swing.JMenuItem jmnStockInoutReport;
    private javax.swing.JMenuItem jmnStockItemMonth;
    private javax.swing.JMenuItem jmnStockLedger;
    private javax.swing.JMenuItem jmnStockLedgerRate;
    private javax.swing.JMenuItem jmnStockStatementIMEI;
    private javax.swing.JMenuItem jmnStockStmtDateWise;
    private javax.swing.JMenuItem jmnStockSummary;
    private javax.swing.JMenuItem jmnStockSummaryBal;
    private javax.swing.JMenu jmnStockTransfer;
    private javax.swing.JMenuItem jmnStockValueStatementIMEI;
    private javax.swing.JMenuItem jmnStockValueStmtAcc;
    private javax.swing.JMenuItem jmnTagTrack;
    private javax.swing.JMenuItem jmnTagTrackTransaction;
    private javax.swing.JMenuItem jmnTaxInvoice;
    private javax.swing.JMenuItem jmnTaxMst;
    private javax.swing.JMenu jmnTransaction;
    private javax.swing.JMenuItem jmnTypeMst;
    private javax.swing.JMenuItem jmnTypeWiseBrandWiseProfit;
    private javax.swing.JMenuItem jmnTypeWiseProfitStatement;
    private javax.swing.JMenuItem jmnTypeWisePurchase;
    private javax.swing.JMenuItem jmnTypeWisePurchaseDetail;
    private javax.swing.JMenuItem jmnTypeWiseSalesStatement;
    private javax.swing.JMenuItem jmnTypeWiseSalesStatementDetail;
    private javax.swing.JMenuItem jmnURDPurchse;
    private javax.swing.JMenuItem jmnUpdateUser;
    private javax.swing.JMenuItem jmnUserGroupMaster;
    private javax.swing.JMenuItem jmnUserRights;
    private javax.swing.JMenu jmnUtility;
    private javax.swing.JMenuItem jmnsds;
    private javax.swing.JMenuItem jmnstockStatementAcc;
    private javax.swing.JMenu trthg;
    // End of variables declaration//GEN-END:variables
}
