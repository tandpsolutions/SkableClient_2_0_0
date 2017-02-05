/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skable;

import java.util.ArrayList;
import model.BranchMasterModel;
import model.RefModel;
import model.TaxMasterModel;

/**
 *
 * @author bhaumikc
 */
public class Constants {

    public static final String HOST1 = "182.70.127.231:8080";
    public static String HOST2 = "219.91.186.105:8080";
//    public static final String HOST1 = "127.0.0.1:8084"; 
    public static final String FOLDER = "Skable2.0.0";
    public static final String UPDATE_FOLDER = "Skable";
    public static final String UPDATE_host = "tandpsolutions.in";
    public static final String VER = "2";
    public static String BASE_URL = "http://" + HOST1 + "/" + FOLDER + "/";
    public static final String UPDATE_BASE_URL = "http://" + UPDATE_host + "/" + UPDATE_FOLDER + "/";
    public static final ArrayList<TaxMasterModel> TAX = new ArrayList<TaxMasterModel>();
    public static final ArrayList<BranchMasterModel> BRANCH = new ArrayList<BranchMasterModel>();
    public static final ArrayList<RefModel> REFERAL = new ArrayList<RefModel>();
}
