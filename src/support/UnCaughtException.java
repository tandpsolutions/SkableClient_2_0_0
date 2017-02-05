/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package support;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class UnCaughtException extends PrintStream {
//
    private FileOutputStream fops = null;
    private FileOutputStream timefops = null;
    private FileOutputStream selStkFops = null;
    private static FileOutputStream logClear = null;

    public UnCaughtException(FileOutputStream fops)
    {
        super(fops);
        this.fops = fops;
        try{
            logClear = new FileOutputStream(new File("LOG/Clear Locks_"+new SimpleDateFormat("ddMMyyyy_hh_mm_ss aaa").format(new Date())+".ini"));
        }catch(Exception ex)
        {
//            BlinderHome.BLib.printToLogFile("Error in UncaughtException ", ex);
        }
    }
//
//
//    @Override
//    public void println(String str) {
//        try {
//            if (str != null) {
//                if (str.trim().toUpperCase().startsWith("EXCEPTION")) {
////                fops.write(("==================================================").getBytes());
//                fops.write(13);
//                fops.write(("Time : "+BlinderHome.BLib.getCurrentDBServerTime()+" --> ").getBytes());
//                fops.write(13);
//                fops.write(str.getBytes());
//                fops.write(13);
//            }
//            else if(str.trim().toUpperCase().startsWith("AT"))
//            {
//                fops.write(str.getBytes());
//                fops.write(13);
//            }
//            else
//            {
////                fops.write(("==================================================").getBytes());
////                fops.write(13);
////                fops.write(("Time : "+BlinderHome.BLib.getCurrentDBServerTime()+" --> ").getBytes());
////                fops.write(13);
////                fops.write(str.getBytes());
////                fops.write(13);
//            }
//            fops.flush();
//            if(str.contains("SQLNonTransientConnectionException"))
//            {
//                JButton exit = new JButton("Exit");
////                JButton cancel = new JButton("Cancel");
//                JButton[] button = {exit};//,cancel};
//                exit.addActionListener(new ActionListener() {
//
//                    public void actionPerformed(ActionEvent e) {
//                        System.exit(0);
//                    }
//                });
//                JOptionPane.showOptionDialog(new BlinderHome(), "Please Restart the Application and\n Check the Database Connection", "Connection Error",
//                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null,button , exit);
//            }
//            } else {
//                fops.write(13);
//        }
//        } catch (Exception ex) {
//            BlinderHome.BLib.printToLogFile("Exception at println in UnCaughtException..:", ex);
//        }
//    }
//
//    @Override
//    public void print(String str)
//    {
//        try
//        {
//            if(str.startsWith("•"))
//            {
//                str = str.substring(1);
//                selStkFops.write(str.getBytes());
//                selStkFops.flush();
//            }else{
//                fops.write(str.getBytes());
//                fops.flush();
//            }
//        }
//        catch(Exception ex)
//        {
//            BlinderHome.BLib.printToLogFile("Exception at print in UnCaughtException..:",ex);
//        }
//    }
//
//    @Override
//    public void write(byte[] b) throws IOException {
//        try{
//            timefops.write(b);
//        }catch(Exception ex){
//            BlinderHome.BLib.printToLogFile("Exception at write in UnCaughtException..:",ex);
//        }
////        super.write(b);
//    }
//
//    public static void printForClearStk(String str)
//    {
//        try{
//            logClear.write(str.getBytes());
//        }catch(Exception ex){
//            BlinderHome.BLib.printToLogFile("Exception at write in UnCaughtException..:",ex);
//        }
//    }

}
