/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

public class ProgrammaticCopyDemo implements ClipboardOwner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ProgrammaticCopyDemo();
            }
        });
    }

    public ProgrammaticCopyDemo() {
        JTable table = getTable();
        addCopylistenerToTable(table);

        JTextArea area = new JTextArea(3, 20);
        addPasteListenerToArea(area);

        JFrame frame = new JFrame();
        frame.add(new JScrollPane(table));
        frame.add(area, BorderLayout.PAGE_END);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private void addPasteListenerToArea(final JTextComponent component) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new AbstractAction("Paste") {
            public void actionPerformed(ActionEvent e) {
                String copiedContent = getClipboardContents();
                int caretPosition = component.getCaretPosition();
                try {
                    component.getDocument().insertString(caretPosition, copiedContent, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            private String getClipboardContents() {
                String result = "";
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable contents = clipboard.getContents(null);
                boolean hasTransferableText
                        = (contents != null)
                        && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
                if (hasTransferableText) {
                    try {
                        result = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    } catch (UnsupportedFlavorException | IOException ex) {
                        System.out.println(ex);
                        ex.printStackTrace();
                    }
                }
                return result;
            }
        });
        component.setComponentPopupMenu(menu);
    }

    private void addCopylistenerToTable(final JTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("double click");
                    Point p = e.getPoint();
                    int row = table.rowAtPoint(p);
                    int col = table.columnAtPoint(p);
                    Object value = table.getValueAt(row, col);
                    StringSelection stringSelection = new StringSelection(value.toString());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, ProgrammaticCopyDemo.this);
                }
            }
        });
    }

    private JTable getTable() {
        String[][] data = {
            {"Hello", "World"},
            {"Stack", "Overflow"},
            {"Foo", "Bar"}
        };
        String[] cols = {"Col", "Col"};
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        return new JTable(model) {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return getPreferredSize();
            }
        };
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
