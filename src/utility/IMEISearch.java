/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StockLedger.java
 *
 * Created on Oct 16, 2012, 12:58:30 PM
 */
package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import inventory.TagTrack;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import retrofitAPI.InventoryAPI;
import skable.SkableHome;
import support.Library;
import support.ReportTable;

/**
 *
 * @author nice
 */
public class IMEISearch extends javax.swing.JInternalFrame {

    Library lb = Library.getInstance();
    private DefaultTableModel dtm = null;
    private TableRowSorter<TableModel> rowSorter;
    private JTextField jtfFilter = new JTextField();

    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form StockLedger
     */
    public IMEISearch() {
        initComponents();
        initOther();
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


    public IMEISearch(String prd_name, String fromDate, String toDate) {
        initComponents();
        initOther();
        jtxtTagNo.setText(prd_name);
        jbtnView.doClick();
    }

    private void initOther() {
        searchOnTextFields();
        setPopUp();
        dtm = (DefaultTableModel) jTable1.getModel();
        registerShortKeys();
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

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            l.setHorizontalAlignment(SwingConstants.RIGHT);
            Double d = null;
            try {
                d = Double.parseDouble(lb.getDeCustomFormat((l.getText().equalsIgnoreCase("") ? "0.00" : l.getText())));
            } catch (Exception ex) {
                d = 0.00;
                lb.printToLogFile("Error at getTableCell in stock ledger", ex);
            }
            l.setText(lb.Convert2DecFmt(d));
            return l;

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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtxtTagNo = new javax.swing.JTextField();
        jbtnView = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        panel = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setTitle("Stock Ledger");

        jPanel1.setLayout(new java.awt.CardLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag No", "Item"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        }

        jPanel1.add(jScrollPane1, "card2");

        jtxtTagNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTagNoFocusLost(evt);
            }
        });
        jtxtTagNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTagNoKeyPressed(evt);
            }
        });

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

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Tag Number");

        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jRadioButton1)
                .addGap(18, 18, 18)
                .addComponent(jtxtTagNo, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1085, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jbtnPreview)
                        .addComponent(jbtnClose))
                    .addComponent(jtxtTagNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton1, jtxtTagNo});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
    try {
        InventoryAPI inventoryAPI = lb.getRetrofit().create(InventoryAPI.class
        );
        lb.addGlassPane(this);
        JsonObject call = inventoryAPI.IMEISearch(jtxtTagNo.getText()).execute().body();

        if (call != null) {
            JsonObject result = call;
            if (result.get("result").getAsInt() == 1) {
                JsonArray array = call.getAsJsonArray("data");
                dtm.setRowCount(0);
                double tot = 0;
                for (int i = 0; i < array.size(); i++) {
                    Vector row = new Vector();
                    row.add(array.get(i).getAsJsonObject().get("tag_no").getAsString());
                    row.add(array.get(i).getAsJsonObject().get("sr_name").getAsString());
                    dtm.addRow(row);
                }
                lb.setColumnSizeForTable(jTable1, jPanel1.getWidth());
                lb.removeGlassPane(IMEISearch.this);
            } else {
                lb.showMessageDailog(call.get("Cause").getAsString());
                lb.removeGlassPane(IMEISearch.this);
            }
        }
    } catch (IOException ex) {
        Logger.getLogger(IMEISearch.class.getName()).log(Level.SEVERE, null, ex);
    }

}//GEN-LAST:event_jbtnViewActionPerformed

private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
// TODO add your handling code here:
    this.dispose();
}//GEN-LAST:event_jbtnCloseActionPerformed

private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
// TODO add your handling code here:
    lb.enterClick(evt);

}//GEN-LAST:event_jbtnViewKeyPressed

private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
// TODO add your handling code here:
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

        public void run() {
        }
    });
}//GEN-LAST:event_jbtnPreviewActionPerformed

private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
// TODO add your handling code here:
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
            }
        });
    }
}//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jtxtTagNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTagNoKeyPressed
        // TODO add your handling code here:
        if (lb.isEnter(evt)) {
            jbtnView.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtTagNoKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                TagTrack tt = new TagTrack(jTable1.getValueAt(row, 0).toString());
                SkableHome.addOnScreen(tt, "Tag Track");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtTagNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTagNoFocusLost
        // TODO add your handling code here:
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtTagNoFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtTagNo;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
