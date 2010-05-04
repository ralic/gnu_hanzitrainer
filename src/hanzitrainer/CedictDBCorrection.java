/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CedictDBCorrection.java
 *
 * Created on Jul 28, 2009, 7:03:29 PM
 */

package hanzitrainer;

import java.util.ArrayList;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author matthieu
 */
public class CedictDBCorrection extends javax.swing.JDialog {

    /** Creates new form CedictDBCorrection */
    public CedictDBCorrection(java.awt.Frame parent, boolean modal, HanziDB db, ArrayList<Integer> id, ArrayList<String> new_pinyin) {
        super(parent, modal);
        table_model = new DefaultTableModel();
        initComponents();
        main_database = db;
        populate_table(id, new_pinyin);
        word_id = id;
        new_pinyins = new_pinyin;
    }

    private void populate_table(ArrayList<Integer> id, ArrayList<String> new_pinyin)
    {
        String column_names[] = {"Chinese", "Current Pinyin", "New Pinyin"};
        table_model.setColumnCount(3);
        table_model.setColumnIdentifiers(column_names);
        for (int i=0; i<id.size(); i++)
        {
            String new_data[]={main_database.get_word_details(id.get(i)).get(0), main_database.get_word_details(id.get(i)).get(1), new_pinyin.get(i)};
            table_model.addRow(new_data);
        }
        CorrectionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        CorrectionTable.setModel(table_model);
        table_model.fireTableDataChanged();
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CorrectionScroll = new javax.swing.JScrollPane();
        CorrectionTable = new javax.swing.JTable();
        OKButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        CorrectionScroll.setName("CorrectionScroll"); // NOI18N

        CorrectionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        CorrectionTable.setName("CorrectionTable"); // NOI18N
        CorrectionScroll.setViewportView(CorrectionTable);

        OKButton.setText("OK"); // NOI18N
        OKButton.setName("OKButton"); // NOI18N
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel"); // NOI18N
        CancelButton.setName("CancelButton"); // NOI18N
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CorrectionScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(OKButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton)
                        .addGap(45, 45, 45))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CorrectionScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton)
                    .addComponent(CancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        int[] selected = CorrectionTable.getSelectedRows();

        for (int i=0; i<selected.length; i++)
        {
            System.out.println("Changing word id " + word_id.get(selected[i]));
            main_database.change_word_pinyin(word_id.get(selected[i]),
                    new_pinyins.get(selected[i]));
        }
        this.setVisible(false);
    }//GEN-LAST:event_OKButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_CancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private javax.swing.JScrollPane CorrectionScroll;
    private javax.swing.JTable CorrectionTable;
    private javax.swing.JButton OKButton;
    // End of variables declaration//GEN-END:variables
    private HanziDB main_database;
    private DefaultTableModel table_model;
    private ArrayList<Integer> word_id;
    private ArrayList<String> new_pinyins;

}
