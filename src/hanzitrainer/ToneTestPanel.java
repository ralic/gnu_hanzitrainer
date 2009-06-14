/*
 * HanziTrainer to help you learn Mandarin
 * Copyright (c) 2008, Matthieu Jeanson ( matthieu.jeanson[at]gmail.com )
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The name its contributors may not be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY MATTHIEU JEANSON ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MATTHIEU JEANSON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hanzitrainer;

import java.util.ArrayList;

/**
 *
 * @author matthieu
 */
public class ToneTestPanel extends javax.swing.JPanel {

    /** Creates new form ToneTestPanel */
    public ToneTestPanel(HanziDBscore database, HanziApplicationUpdater updater) {
        initComponents();
        main_database = database;
        parent_app = updater;
        initComponents();
        ToneTableFiller.set_filter("", "");
        character_history = new ArrayList<String>();
        set_new_random_pinyin();
    }

    public void set_pinyin_test(String hanzi, String pinyin)
    {
        CharacterLabel.setText(hanzi);
        PinyinLabel.setText(Pinyin.pinyin_base(pinyin));
        ToneTableFiller.set_filter(hanzi, pinyin);
        ToneTableFiller.fireTableDataChanged();
    }


    private void set_new_random_pinyin() {
        int num_char = main_database.get_number_characters();
        int index;
        int char_id;
        String hanzi, pinyin;

        if (num_char == 0) {
            return;
        }
        do
        {
            index = (int) (Math.random() * num_char);
            char_id = main_database.get_character_id(index);
            hanzi = main_database.get_character_details(char_id);
            System.out.println("getting index " + index + " character :" + hanzi);
        } while (character_history.contains(hanzi));

        /*
        index = 149;
        char_id = main_database.get_character_id(index);
        hanzi = main_database.get_character_details(char_id);
        */

        character_history.add(hanzi);
        if (character_history.size() > (num_char - 1) / 2) {
            character_history.remove(0);
        }
        ArrayList<String> pinyins = main_database.get_pinyin_for_character(hanzi);
        if (pinyins.size() > 1)
        {
            index = (int) (Math.random() * pinyins.size());
            pinyin = pinyins.get(index);
        }
        else
        {
            pinyin = pinyins.get(0);
        }


        set_pinyin_test(hanzi,pinyin);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CharacterLabel = new javax.swing.JLabel();
        CharDBScroll = new javax.swing.JScrollPane();
        CharDBTable = new javax.swing.JTable();
        ToneTableFiller = new ToneTestTableFiller(main_database);
        NoToneButton = new javax.swing.JButton();
        FirstToneButton = new javax.swing.JButton();
        SecondToneButton = new javax.swing.JButton();
        ThirdToneButton = new javax.swing.JButton();
        FourthToneButton = new javax.swing.JButton();
        PinyinLabel = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(hanzitrainer.HanziTrainerApp.class).getContext().getResourceMap(ToneTestPanel.class);
        CharacterLabel.setFont(resourceMap.getFont("CharacterLabel.font")); // NOI18N
        CharacterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CharacterLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        CharacterLabel.setName("CharacterLabel"); // NOI18N

        CharDBScroll.setName("CharDBScroll"); // NOI18N

        CharDBTable.setModel(ToneTableFiller);
        CharDBTable.setName("CharDBTable"); // NOI18N
        CharDBTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CharDBTableMouseClicked(evt);
            }
        });
        CharDBScroll.setViewportView(CharDBTable);

        NoToneButton.setText(resourceMap.getString("NoToneButton.text")); // NOI18N
        NoToneButton.setName("NoToneButton"); // NOI18N
        NoToneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoToneButtonActionPerformed(evt);
            }
        });

        FirstToneButton.setText(resourceMap.getString("FirstToneButton.text")); // NOI18N
        FirstToneButton.setName("FirstToneButton"); // NOI18N

        SecondToneButton.setText(resourceMap.getString("SecondToneButton.text")); // NOI18N
        SecondToneButton.setName("SecondToneButton"); // NOI18N

        ThirdToneButton.setText(resourceMap.getString("ThirdToneButton.text")); // NOI18N
        ThirdToneButton.setName("ThirdToneButton"); // NOI18N

        FourthToneButton.setText(resourceMap.getString("FourthToneButton.text")); // NOI18N
        FourthToneButton.setName("FourthToneButton"); // NOI18N

        PinyinLabel.setText(resourceMap.getString("PinyinLabel.text")); // NOI18N
        PinyinLabel.setName("PinyinLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(NoToneButton)
                        .addGap(18, 18, 18)
                        .addComponent(FirstToneButton)
                        .addGap(18, 18, 18)
                        .addComponent(SecondToneButton)
                        .addGap(18, 18, 18)
                        .addComponent(ThirdToneButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(FourthToneButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(PinyinLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CharDBScroll, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PinyinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CharDBScroll, 0, 0, Short.MAX_VALUE))
                    .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FourthToneButton)
                    .addComponent(ThirdToneButton)
                    .addComponent(SecondToneButton)
                    .addComponent(FirstToneButton)
                    .addComponent(NoToneButton))
                .addGap(95, 95, 95))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void CharDBTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CharDBTableMouseClicked
        if (evt.getClickCount() == 2) {
            int row = CharDBTable.getSelectedRow();
            String chinese_word;
            chinese_word = (String) ToneTableFiller.getValueAt(row, 0);

            parent_app.edit_word(chinese_word);
        }
}//GEN-LAST:event_CharDBTableMouseClicked

    private void NoToneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoToneButtonActionPerformed
        set_new_random_pinyin();
        // TODO add your handling code here:
    }//GEN-LAST:event_NoToneButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane CharDBScroll;
    private javax.swing.JTable CharDBTable;
    private javax.swing.JLabel CharacterLabel;
    private javax.swing.JButton FirstToneButton;
    private javax.swing.JButton FourthToneButton;
    private javax.swing.JButton NoToneButton;
    private javax.swing.JLabel PinyinLabel;
    private javax.swing.JButton SecondToneButton;
    private javax.swing.JButton ThirdToneButton;
    // End of variables declaration//GEN-END:variables
    private ToneTestTableFiller ToneTableFiller;
    private ArrayList<String> character_history;
    private HanziDBscore main_database;
    private HanziApplicationUpdater parent_app;
}