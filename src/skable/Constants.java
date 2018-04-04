/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skable;

import java.util.ArrayList;
import model.BranchMasterModel;
import model.DBYearModel;
import model.RefModel;
import model.TaxMasterModel;

/**
 *
 * @author bhaumikc
 */
public class Constants {

    public static String HOST1 = "";
    public static final String FOLDER = "Skable2.0.0";
//    public static final String FOLDER = "Skable2.0.0_Git";
    public static final String FOLDER_NEW_2018 = "Skable2.0.0_1_2018";
    public static final String FOLDER_NEW = "Skable2.0.0_1";
//    public static final String FOLDER_NEW = "Skable2.0.0_1_Git";
    public static final String UPDATE_FOLDER = "Skable";

    public static final String UPDATE_host = "tandpsolutions.in";
    public static final String VER = "2";
    public static String BASE_URL = "";
    public static final String UPDATE_BASE_URL = "http://" + UPDATE_host + "/" + UPDATE_FOLDER + "/";
    public static final ArrayList<TaxMasterModel> TAX = new ArrayList<TaxMasterModel>();
    public static final ArrayList<BranchMasterModel> BRANCH = new ArrayList<BranchMasterModel>();
    public static final ArrayList<RefModel> REFERAL = new ArrayList<RefModel>();
    public static final ArrayList<DBYearModel> DBYMS = new ArrayList<DBYearModel>();
}
