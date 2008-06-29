/*
 * WordDatabasePanel.java
 *
 * HanziTrainer to help you learn Mandarin
 * Copyright (C) 2008  Matthieu Jeanson
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hanzitrainer;

/**
 *
 * @author  Administrator
 */
public class WordDatabasePanel extends javax.swing.JPanel
{

    /** Creates new form WordDatabasePanel */
    public WordDatabasePanel(HanziDB database, HanziApplicationUpdater updater)
    {
        main_database = database;
        parent_app = updater;
        initComponents();
        WordDatabaseUpdateDB();
    }

    public void WordDatabaseUpdateDB()
    {
        int res;
        TableFiller.fireTableDataChanged();

        res = main_database.get_number_words();
        numWordLabel.setText("Number of words : " + res);

        res = main_database.get_number_characters();
        numCharLabel.setText("Number of characters : " + res);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        numCharLabel = new javax.swing.JLabel();
        numWordLabel = new javax.swing.JLabel();
        DBScroll = new javax.swing.JScrollPane();
        DBTable = new javax.swing.JTable();
        TableFiller = new DBTableFiller(main_database);

        setName("Form"); // NOI18N

        numCharLabel.setName("numCharLabel"); // NOI18N

        numWordLabel.setName("numWordLabel"); // NOI18N

        DBScroll.setName("DBScroll"); // NOI18N

        DBTable.setModel(TableFiller);
        DBTable.setName("DBTable"); // NOI18N
        DBTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        DBTable.getTableHeader().setReorderingAllowed(false);
        DBTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DBTableMouseClicked(evt);
            }
        });
        DBScroll.setViewportView(DBTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(numCharLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numWordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(48, 48, 48)
                    .addComponent(DBScroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(24, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 235, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numWordLabel)
                    .addComponent(numCharLabel))
                .addContainerGap(224, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addComponent(DBScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

private void DBTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DBTableMouseClicked
    if (evt.getClickCount() == 2)
    {
        int row = DBTable.getSelectedRow();
        String chinese_word;
        chinese_word = (String) TableFiller.getValueAt(row, 0);

        parent_app.edit_word(chinese_word);
    }
}//GEN-LAST:event_DBTableMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane DBScroll;
    private javax.swing.JTable DBTable;
    private javax.swing.JLabel numCharLabel;
    private javax.swing.JLabel numWordLabel;
    // End of variables declaration//GEN-END:variables
    private DBTableFiller TableFiller;
    private HanziDB main_database;
    private HanziApplicationUpdater parent_app;
}
