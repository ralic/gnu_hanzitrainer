/*
 * CharacterReviewPanel.java
 *
 * Created on June 18, 2008, 10:04 PM
 * 
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
import java.util.Locale;

/**
 *
 * @author  Administrator
 */
public class CharacterReviewPanel extends javax.swing.JPanel
{
    /** Creates new form CharacterReviewPanel */
    public CharacterReviewPanel(HanziDBscore database, HanziApplicationUpdater updater)
    {
        main_database = database;
        parent_app = updater;
        initComponents();
        CharTableFiller.set_filter("");
        character_history = new ArrayList<String>();
        set_new_random_character();
    }

    public void CharacterReviewUpdateDB()
    {
        CharTableFiller.fireTableDataChanged();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CharacterLabel = new javax.swing.JLabel();
        NextCharacterButton = new javax.swing.JButton();
        CharsearchentryTextField = new javax.swing.JTextField();
        CharSearchButton = new javax.swing.JButton();
        CharPreviousButton = new javax.swing.JButton();
        CharDBScroll = new javax.swing.JScrollPane();
        CharDBTable = new javax.swing.JTable();
        CharTableFiller = new DBTableFiller(main_database);
        PinyinsTextfield = new javax.swing.JTextField();
        PinyinsLabel = new javax.swing.JLabel();
        WordListLabel = new javax.swing.JLabel();
        ScoreLabel = new javax.swing.JLabel();
        ScoreTextfield = new javax.swing.JTextField();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(hanzitrainer.HanziTrainerApp.class).getContext().getResourceMap(CharacterReviewPanel.class);
        CharacterLabel.setFont(resourceMap.getFont("CharacterLabel.font")); // NOI18N
        CharacterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CharacterLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        CharacterLabel.setName("CharacterLabel"); // NOI18N

        NextCharacterButton.setText(resourceMap.getString("NextCharacterButton.text")); // NOI18N
        NextCharacterButton.setName("NextCharacterButton"); // NOI18N
        NextCharacterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextCharacterButtonrandom_character_action(evt);
            }
        });

        CharsearchentryTextField.setName("CharsearchentryTextField"); // NOI18N
        CharsearchentryTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CharsearchentryTextFieldCharSearchButtonAction(evt);
            }
        });
        CharsearchentryTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                CharsearchentryTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                CharsearchentryTextFieldFocusLost(evt);
            }
        });

        CharSearchButton.setText(resourceMap.getString("CharSearchButton.text")); // NOI18N
        CharSearchButton.setName("CharSearchButton"); // NOI18N
        CharSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CharSearchButtonAction(evt);
            }
        });

        CharPreviousButton.setText(resourceMap.getString("CharPreviousButton.text")); // NOI18N
        CharPreviousButton.setName("CharPreviousButton"); // NOI18N
        CharPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CharPreviousButtonActionPerformed(evt);
            }
        });

        CharDBScroll.setName("CharDBScroll"); // NOI18N

        CharDBTable.setModel(CharTableFiller);
        CharDBTable.setName("CharDBTable"); // NOI18N
        CharDBTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CharDBTableMouseClicked(evt);
            }
        });
        CharDBScroll.setViewportView(CharDBTable);

        PinyinsTextfield.setEditable(false);
        PinyinsTextfield.setName("PinyinsTextfield"); // NOI18N

        PinyinsLabel.setText(resourceMap.getString("PinyinsLabel.text")); // NOI18N
        PinyinsLabel.setName("PinyinsLabel"); // NOI18N

        WordListLabel.setText(resourceMap.getString("WordListLabel.text")); // NOI18N
        WordListLabel.setName("WordListLabel"); // NOI18N

        ScoreLabel.setText(resourceMap.getString("ScoreLabel.text")); // NOI18N
        ScoreLabel.setName("ScoreLabel"); // NOI18N

        ScoreTextfield.setEditable(false);
        ScoreTextfield.setName("ScoreTextfield"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(PinyinsLabel)
                            .addComponent(WordListLabel)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(CharsearchentryTextField)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(CharSearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(NextCharacterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(CharPreviousButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CharDBScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PinyinsTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ScoreLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ScoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CharPreviousButton)
                            .addComponent(NextCharacterButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CharSearchButton)
                            .addComponent(CharsearchentryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PinyinsLabel)
                            .addComponent(PinyinsTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ScoreLabel)
                            .addComponent(ScoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CharDBScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                            .addComponent(WordListLabel))
                        .addGap(4, 4, 4)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void set_character_review(String hanzi)
    {
        ArrayList<String> pinyins = main_database.get_pinyin_for_character(hanzi);
        String pinyin_list = "";
        int score = main_database.get_character_score(main_database.get_character_id(hanzi));
        int i, j;

        if (pinyins.size() != 0)
        {
            for (i = pinyins.size() - 1; i >= 0; i--)
            {
                String pinyin_to_check = pinyins.get(i);
                int tone = pinyin_to_check.charAt(pinyin_to_check.length() - 1);
                boolean found = false;
                if ((tone < '1') || (tone > '4'))
                {
                    for (j = 0; j < pinyins.size(); j++)
                    {
                        if (i == j)
                        {
                            continue;
                        }
                        if (pinyins.get(j).startsWith(pinyin_to_check))
                        {
                            found = true;
                            break;
                        }
                    }
                    if (found)
                    {
                        pinyins.remove(i);
                    }
                }

            }

            pinyin_list = PinyinParser.convert_to_printed_version(pinyins.get(0));
            for (i = 1; i < pinyins.size(); i++)
            {
                pinyin_list += ", " + PinyinParser.convert_to_printed_version(pinyins.get(i));
            }
        }
        PinyinsTextfield.setText(pinyin_list);
        ScoreTextfield.setText("" +score);
        CharacterLabel.setText(hanzi);
        CharTableFiller.set_filter(hanzi);
        CharTableFiller.fireTableDataChanged();
    }
    
    private void set_new_random_character() {
        int num_char = main_database.get_number_characters();
        int index;
        int char_id;
        String hanzi;

        if (num_char == 0) {
            return;
        }
        do {
            index = (int) (Math.random() * num_char);
            char_id = main_database.get_character_id(index);
            hanzi = main_database.get_character_details(char_id);
            System.out.println("getting index " + index + " character :" + hanzi);
        } while (character_history.contains(hanzi));
        character_history.add(hanzi);
        if (character_history.size() > (num_char - 1) / 2) {
            character_history.remove(0);
        }
        set_character_review(hanzi);

    }

private void NextCharacterButtonrandom_character_action(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextCharacterButtonrandom_character_action
    set_new_random_character();
}//GEN-LAST:event_NextCharacterButtonrandom_character_action

private void CharsearchentryTextFieldCharSearchButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CharsearchentryTextFieldCharSearchButtonAction
    String char_to_search = CharsearchentryTextField.getText();
    int num_char = main_database.get_number_characters();

    if (char_to_search.codePointCount(0, char_to_search.length()) != 1)
    {
        return;
    }
    character_history.add(char_to_search);
    if (character_history.size() > (num_char - 1) / 2)
    {
        character_history.remove(0);
    }
    set_character_review(char_to_search);
}//GEN-LAST:event_CharsearchentryTextFieldCharSearchButtonAction

private void CharsearchentryTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CharsearchentryTextFieldFocusGained
    CharsearchentryTextField.getInputContext().selectInputMethod(Locale.CHINA);
}//GEN-LAST:event_CharsearchentryTextFieldFocusGained

private void CharsearchentryTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CharsearchentryTextFieldFocusLost
    CharsearchentryTextField.getInputContext().selectInputMethod(Locale.getDefault());
}//GEN-LAST:event_CharsearchentryTextFieldFocusLost

private void CharSearchButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CharSearchButtonAction
    String char_to_search = CharsearchentryTextField.getText();
    int num_char = main_database.get_number_characters();

    if (char_to_search.codePointCount(0, char_to_search.length()) != 1)
    {
        return;
    }
    character_history.add(char_to_search);
    if (character_history.size() > (num_char - 1) / 2)
    {
        character_history.remove(0);
    }
    set_character_review(char_to_search);
}//GEN-LAST:event_CharSearchButtonAction

private void CharPreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CharPreviousButtonActionPerformed
    String hanzi;

    if (character_history.size() <= 1)
    {
        return;
    }
    hanzi = character_history.get(character_history.size() - 2);
    character_history.remove(character_history.size() - 1);

    set_character_review(hanzi);
}//GEN-LAST:event_CharPreviousButtonActionPerformed

private void CharDBTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CharDBTableMouseClicked
    if (evt.getClickCount() == 2)
    {
        int row = CharDBTable.getSelectedRow();
        String chinese_word;
        chinese_word = (String) CharTableFiller.getValueAt(row, 0);
        
        parent_app.edit_word(chinese_word);
    }
}//GEN-LAST:event_CharDBTableMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane CharDBScroll;
    private javax.swing.JTable CharDBTable;
    private javax.swing.JButton CharPreviousButton;
    private javax.swing.JButton CharSearchButton;
    private javax.swing.JLabel CharacterLabel;
    private javax.swing.JTextField CharsearchentryTextField;
    private javax.swing.JButton NextCharacterButton;
    private javax.swing.JLabel PinyinsLabel;
    private javax.swing.JTextField PinyinsTextfield;
    private javax.swing.JLabel ScoreLabel;
    private javax.swing.JTextField ScoreTextfield;
    private javax.swing.JLabel WordListLabel;
    // End of variables declaration//GEN-END:variables
    private DBTableFiller CharTableFiller;
    private ArrayList<String> character_history;
    private HanziDBscore main_database;
    private HanziApplicationUpdater parent_app;

}
