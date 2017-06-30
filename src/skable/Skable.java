/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jtattoo.plaf.mcwin.McWinLookAndFeel;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import login.SelectIP;
import retrofitAPI.UpdateInterface;
import support.Library;
import utility.SwingFileDownloadHTTP;

/**
 *
 * @author bhaumik
 */
public class Skable {

    /**
     */
    public static String ver = "116";

    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Properties property = new Properties();
            property.put("logoString", "");
            McWinLookAndFeel.setTheme(property);
            javax.swing.UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, e.getCause().getMessage());
        }
        final Library lb = Library.getInstance();
        UpdateInterface update = lb.getUpdateRetrofit().create(UpdateInterface.class);

        try {
            JsonObject data = update.getUpdateVersion(ver).execute().body();
            JsonArray array = data.getAsJsonArray("update");
            if (array.size() > 0) {
                if (!array.get(0).getAsJsonObject().get("UPDATE_VER").getAsString().equalsIgnoreCase(ver)) {
                    lb.confirmDialog("New Update has been found.\n Do you want to update it now?");
                    if (lb.type) {
                        final String ver = array.get(0).getAsJsonObject().get("UPDATE_VER").getAsString();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                SwingFileDownloadHTTP sf = new SwingFileDownloadHTTP("http://www.tandpsolutions.in/updateSkable/" + ver + ".zip");
                                sf.setTitle("Downloading...  Ver " + ver);
                                sf.setVisible(true);
                                sf.buttonDownloadActionPerformed();
                            }
                        });
                    } else {
                        System.exit(0);
                    }
                }
            } else {
                startApplication();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void startApplication() {
        SelectIP lg = new SelectIP();
        lg.setLocationRelativeTo(null);
        lg.setVisible(true);
    }

}
