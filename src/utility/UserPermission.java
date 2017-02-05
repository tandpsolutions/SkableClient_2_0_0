/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import retrofitAPI.UserAPI;
import skable.SkableHome;
import support.Library;

/**
 *
 * @author nice
 */
public class UserPermission extends javax.swing.JInternalFrame {

    /**
     * Creates new form UserPermission
     */
    private Library lb = Library.getInstance();
    int noOfUser = 0;
    DefaultMutableTreeNode root;
    TreePath changePath;
    private UserAPI userAPI;
    private HashMap<String, String> userGroup = new HashMap<String, String>();
    private HashMap<String, String> menuMap = new HashMap<String, String>();
    private HashMap<String, String> formMap = new HashMap<String, String>();
    private ArrayList<String> menuList = new ArrayList<String>();
    private DefaultTreeModel model;

    public UserPermission() {
        initComponents();
        userAPI = lb.getRetrofit().create(UserAPI.class);
        registerShortKeys();
        getMenuMaster();
        jPanel1.setVisible(false);
    }

    private void setUserValues() {
        try {
            root = new DefaultMutableTreeNode("System", true);
            jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            jTree1.setModel(new DefaultTreeModel(root));
            model = (DefaultTreeModel) (jTree1.getModel());

            JsonObject call = userAPI.GetUserGrpMaster().execute().body();

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    for (int i = 0; i < array.size(); i++) {
                        DefaultMutableTreeNode child = new DefaultMutableTreeNode(array.get(i).getAsJsonObject().get("USER_GRP").getAsString(), true);
                        userGroup.put(array.get(i).getAsJsonObject().get("USER_GRP").getAsString(), array.get(i).getAsJsonObject().get("USER_GRP_CD").getAsString());
                        root.add(child);
                        addMenuToUser(child);
                    }
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error in add user", ex);
        }

    }

    private void getMenuMaster() {
        try {
            JsonObject call = userAPI.GetMenuMaster().execute().body();

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    for (int i = 0; i < array.size(); i++) {
                        menuMap.put(array.get(i).getAsJsonObject().get("MENU_NAME").getAsString(), array.get(i).getAsJsonObject().get("MENU_CD").getAsString());
                        menuList.add(array.get(i).getAsJsonObject().get("MENU_NAME").getAsString());
                    }
                    setUserValues();
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error in add user", ex);
        }

    }

    @Override
    public void dispose() {
        try {
            SkableHome.removeFromScreen(SkableHome.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose at codeBinding", ex);
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

    private void getFormMasterFromMenu(final DefaultMutableTreeNode node) {
        try {
            JsonObject call = userAPI.GetFormFromMenu(menuMap.get(node.toString())).execute().body();

            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    for (int i = 0; i < array.size(); i++) {
                        formMap.put(array.get(i).getAsJsonObject().get("FORM_NAME").getAsString(), array.get(i).getAsJsonObject().get("FORM_CD").getAsString());
                        DefaultMutableTreeNode child = new DefaultMutableTreeNode(array.get(i).getAsJsonObject().get("FORM_NAME").getAsString(), true);
                        model.insertNodeInto(child, node, node.getChildCount());
                    }
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error in add user", ex);
        }

    }

    private void addMenuToUser(TreeNode node) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node;
        for (int i = 0; i < menuList.size(); i++) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(menuList.get(i).toString(), true);
            parent.add(child);
            getFormMasterFromMenu(child);
        }

    }

    private void setPrivilegesValues(boolean b[]) {
        jCheckBox1.setSelected(b[0]);
        jCheckBox2.setSelected(b[1]);
        jCheckBox3.setSelected(b[2]);
        jCheckBox4.setSelected(b[3]);
        jCheckBox5.setSelected(b[4]);
    }

    private int[] getPrivilegesValues() {
        int[] b = new int[6];
        b[0] = (jCheckBox1.isSelected()) ? 1 : 0;
        b[1] = (jCheckBox2.isSelected()) ? 1 : 0;
        b[2] = (jCheckBox3.isSelected()) ? 1 : 0;
        b[3] = (jCheckBox4.isSelected()) ? 1 : 0;
        b[4] = (jCheckBox5.isSelected()) ? 1 : 0;
        return b;
    }

    private void setPrivileges(String form_cd, String user_grp_cd) {

        try {
            JsonObject call = userAPI.GetUserRights(form_cd, user_grp_cd).execute().body();

            boolean[] flag = new boolean[6];
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {
                    JsonArray array = call.getAsJsonArray("data");
                    for (int i = 0; i < array.size(); i++) {
                        flag[0] = (array.get(i).getAsJsonObject().get("VIEWS").getAsInt() == 1) ? true : false;
                        flag[1] = (array.get(i).getAsJsonObject().get("ADDS").getAsInt() == 1) ? true : false;
                        flag[2] = (array.get(i).getAsJsonObject().get("EDITS").getAsInt() == 1) ? true : false;
                        flag[3] = (array.get(i).getAsJsonObject().get("DELETES").getAsInt() == 1) ? true : false;
                        flag[4] = (array.get(i).getAsJsonObject().get("PRINTS").getAsInt() == 1) ? true : false;
                        setPrivilegesValues(flag);
                    }
                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                    setPrivilegesValues(flag);
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error in add user", ex);
        }
    }

    private void applyPreviledges(String form_cd, String user_grp_cd) {

        try {
            JsonObject call = userAPI.
                    ApplyUserRights(form_cd, user_grp_cd, getPrivilegesValues()[0], getPrivilegesValues()[1], getPrivilegesValues()[2],
                            getPrivilegesValues()[3], getPrivilegesValues()[4]).execute().body();

            boolean[] flag = new boolean[6];
            if (call != null) {
                JsonObject result = call;
                if (result.get("result").getAsInt() == 1) {

                } else {
                    lb.showMessageDailog(call.get("Cause").getAsString());
                    setPrivilegesValues(flag);
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error in add user", ex);
        }
    }

    private int getUserPathCount(String path) {
        boolean flag = false;
        ArrayList userPath = new ArrayList();
        StringTokenizer st = new StringTokenizer(path.substring(1, path.length() - 1), ",");
        int i = 0;
        while (st.hasMoreElements()) {
            i++;
            st.nextElement();
        }

        return i;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jbtnClose = new javax.swing.JToggleButton();
        jbtnApply = new javax.swing.JButton();
        jbtnOk = new javax.swing.JButton();

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("System");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        jCheckBox1.setText("View");
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });

        jCheckBox2.setText("Add");
        jCheckBox2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox2StateChanged(evt);
            }
        });

        jCheckBox3.setText("Edit");
        jCheckBox3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox3StateChanged(evt);
            }
        });

        jCheckBox4.setText("Delete");
        jCheckBox4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox4StateChanged(evt);
            }
        });

        jCheckBox5.setText("Print");
        jCheckBox5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox5StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox1))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jbtnClose.setMnemonic('C');
        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });
        jbtnClose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jbtnCloseKeyReleased(evt);
            }
        });

        jbtnApply.setMnemonic('A');
        jbtnApply.setText("Apply");
        jbtnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnApplyActionPerformed(evt);
            }
        });
        jbtnApply.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jbtnApplyKeyReleased(evt);
            }
        });

        jbtnOk.setMnemonic('O');
        jbtnOk.setText("Ok");
        jbtnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnOkActionPerformed(evt);
            }
        });
        jbtnOk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jbtnOkKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jbtnOk, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnApply, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtnClose)
                            .addComponent(jbtnApply)
                            .addComponent(jbtnOk))
                        .addGap(40, 40, 40))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed


    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        changePath = evt.getPath();
        DefaultMutableTreeNode selectedNode = new DefaultMutableTreeNode(changePath.getLastPathComponent());

        if (getUserPathCount(evt.getPath().toString()) != 0) {
            if (selectedNode.isLeaf()) {
                if (getUserPathCount(evt.getPath().toString()) == 4) {
                    String userCode = userGroup.get(changePath.getPathComponent(1).toString());
                    String formCode = formMap.get(selectedNode.toString());
                    jPanel1.setVisible(true);
                    boolean[] flag = new boolean[6];
                    setPrivilegesValues(flag);
                    setPrivileges(formCode, userCode);
                } else {
                    jPanel1.setVisible(false);
                }
            } else {
                jPanel1.setVisible(false);
            }
        } else {
            jPanel1.setVisible(false);
        }

    }//GEN-LAST:event_jTree1ValueChanged


    private void jbtnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnApplyActionPerformed
        // TODO add your handling code here:
        String userCode = userGroup.get(changePath.getPathComponent(1).toString());
        String formCode = formMap.get(changePath.getPathComponent(3).toString());
        applyPreviledges(formCode, userCode);
    }//GEN-LAST:event_jbtnApplyActionPerformed

    private void jbtnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnOkActionPerformed
        // TODO add your handling code here:
        String userCode = userGroup.get(changePath.getPathComponent(1).toString());
        String formCode = formMap.get(changePath.getPathComponent(3).toString());
        applyPreviledges(formCode, userCode);
        this.dispose();
    }//GEN-LAST:event_jbtnOkActionPerformed

    private void jbtnOkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnOkKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnOk.doClick();
        }
    }//GEN-LAST:event_jbtnOkKeyReleased

    private void jbtnApplyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnApplyKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnApply.doClick();
        }
    }//GEN-LAST:event_jbtnApplyKeyReleased

    private void jbtnCloseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnClose.doClick();
        }
    }//GEN-LAST:event_jbtnCloseKeyReleased

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jCheckBox2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox2StateChanged
        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox2StateChanged

    private void jCheckBox3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox3StateChanged
        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox3StateChanged

    private void jCheckBox4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox4StateChanged
        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox4StateChanged

    private void jCheckBox5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox5StateChanged
        jbtnApply.requestFocusInWindow();
    }//GEN-LAST:event_jCheckBox5StateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JButton jbtnApply;
    private javax.swing.JToggleButton jbtnClose;
    private javax.swing.JButton jbtnOk;
    // End of variables declaration//GEN-END:variables
}
