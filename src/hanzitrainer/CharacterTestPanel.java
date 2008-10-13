/*
 * CharacterTest.java
 *
 * Created on June 20, 2008, 4:19 PM
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author  Administrator
 */
public class CharacterTestPanel extends javax.swing.JPanel implements TableModel
{

    /** Creates new form CharacterTest */
    public CharacterTestPanel(HanziDB database, HanziApplicationUpdater updater)
    {
        main_database = database;
        parent_app = updater;
        initComponents();
        character_history = new ArrayList<String>();
        chinese_word_list = new ArrayList<String>();
        chinese_word_list_state = new ArrayList<Integer>();
        current_chinese_words = new ArrayList<String>();
        guess_pinyins = new ArrayList<String>();
        guess_chinese_words = new ArrayList<String>();

        set_new_character_to_guess();
    }

    public void CharacterTestUpdateDB()
    {
        set_new_character_to_guess();
    }

    private void check_old_data()
    {
        String[] guessed_pinyins, guessed_chinese;
        ArrayList<String> good_pinyins = new ArrayList<String>();
        ArrayList<String> bad_pinyins = new ArrayList<String>();
        ArrayList<String> other_pinyins = new ArrayList<String>();
        ArrayList<String> good_chinese = new ArrayList<String>();
        ArrayList<String> bad_chinese = new ArrayList<String>();
        ArrayList<String> other_chinese = new ArrayList<String>();
        String pinyins_result;
        TableModelEvent t_event = new TableModelEvent(this);


        previous_character = current_character;
        previous_pinyins = current_pinyins;
        previous_chinese_words = current_chinese_words;

        guessed_pinyins = GuessPinyinTextField.getText().split("[,，]");
        guess_pinyins.clear();
        for (String item : guessed_pinyins)
        {
            guess_pinyins.add(item.trim());
        }
        for (String item : guess_pinyins)
        {
            if (!previous_pinyins.contains(item))
            {
                bad_pinyins.add(item);
            }
            else
            {
                good_pinyins.add(item);
            }
        }
        for (String item : previous_pinyins)
        {
            if ((!good_pinyins.contains(item)) && (!bad_pinyins.contains(item)))
            {
                other_pinyins.add(item);
            }
        }
        pinyins_result = "<HTML><FONT COLOR=\"RED\">";
        for (String item : bad_pinyins)
        {
            pinyins_result += item + " ";
        }
        pinyins_result += "</FONT><FONT COLOR=\"GREEN\">";
        for (String item : good_pinyins)
        {
            pinyins_result += item + " ";
        }
        pinyins_result += "</FONT><FONT COLOR=\"BLACK\">";
        for (String item : other_pinyins)
        {
            pinyins_result += item + " ";
        }
        pinyins_result += "</HTML>";

        if (!GuessChineseTextField.getText().equals(""))
        {
            guessed_chinese = GuessChineseTextField.getText().split("[,，]");
            guess_chinese_words.clear();
            for (String item : guessed_chinese)
            {
                guess_chinese_words.add(item.trim());
            }
            for (String item : guess_chinese_words)
            {
                if (!previous_chinese_words.contains(item))
                {
                    bad_chinese.add(item);
                }
                else
                {
                    good_chinese.add(item);
                }
            }
        }
        for (String item : previous_chinese_words)
        {
            if ((!good_chinese.contains(item)) && (!bad_chinese.contains(item)))
            {
                other_chinese.add(item);
            }
        }
        chinese_word_list.clear();
        chinese_word_list_state.clear();
        for (String item : bad_chinese)
        {
            chinese_word_list.add(item);
            chinese_word_list_state.add(0);
        }
        for (String item : good_chinese)
        {
            chinese_word_list.add(item);
            chinese_word_list_state.add(1);
        }
        for (String item : other_chinese)
        {
            chinese_word_list.add(item);
            chinese_word_list_state.add(2);
        }
        PreviousCharDBTable.tableChanged(t_event);

        PreviousPinyinsLabel.setText(pinyins_result);
        PreviousCharacterLabel.setText(previous_character);
    }

    private void set_new_character_to_guess()
    {
        int num_char = main_database.get_number_characters();
        int index;
        String hanzi;
        ArrayList<ArrayList<String>> words;

        if (num_char == 0)
        {
            return;
        }
        do
        {
            index = (int) (Math.random() * num_char) + 1;
            hanzi = main_database.get_character_details(index);
        }
        while (character_history.contains(hanzi));
        character_history.add(hanzi);
        if (character_history.size() > (num_char - 1) / 2)
        {
            character_history.remove(0);
        }
        current_chinese_words.clear();
        words = main_database.get_words_with_character(hanzi);
        for (ArrayList<String> word : words)
        {
            current_chinese_words.add(word.get(0));
        }
        current_pinyins = main_database.get_pinyin_from_character(hanzi);
        current_character = hanzi;
        CharacterLabel.setText(hanzi);
        GuessPinyinTextField.setText("");
        GuessChineseTextField.setText("");
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
        DoneGuessCharacterButton = new javax.swing.JButton();
        PinyinsLabel = new javax.swing.JLabel();
        PinyinsLabel1 = new javax.swing.JLabel();
        GuessPinyinTextField = new javax.swing.JTextField();
        GuessChineseTextField = new javax.swing.JTextField();
        PreviousCharacterLabel = new javax.swing.JLabel();
        PreviouslyLabel = new javax.swing.JLabel();
        GoodOrBadLabel = new javax.swing.JLabel();
        PreviousCharDBScroll = new javax.swing.JScrollPane();
        PreviousCharDBTable = new javax.swing.JTable();
        char_table_filler = new DBTableFiller(main_database);
        PreviousPinyinsLabel = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(hanzitrainer.HanziTrainerApp.class).getContext().getResourceMap(CharacterTestPanel.class);
        CharacterLabel.setFont(resourceMap.getFont("CharacterLabel.font")); // NOI18N
        CharacterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CharacterLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        CharacterLabel.setName("CharacterLabel"); // NOI18N

        DoneGuessCharacterButton.setText(resourceMap.getString("DoneGuessCharacterButton.text")); // NOI18N
        DoneGuessCharacterButton.setName("DoneGuessCharacterButton"); // NOI18N
        DoneGuessCharacterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DoneGuessCharacterButton_action(evt);
            }
        });

        PinyinsLabel.setText(resourceMap.getString("PinyinsLabel.text")); // NOI18N
        PinyinsLabel.setName("PinyinsLabel"); // NOI18N

        PinyinsLabel1.setText(resourceMap.getString("PinyinsLabel1.text")); // NOI18N
        PinyinsLabel1.setName("PinyinsLabel1"); // NOI18N

        GuessPinyinTextField.setText(resourceMap.getString("GuessPinyinTextField.text")); // NOI18N
        GuessPinyinTextField.setName("GuessPinyinTextField"); // NOI18N
        GuessPinyinTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuessPinyinTextFieldActionPerformed(evt);
            }
        });

        GuessChineseTextField.setText(resourceMap.getString("GuessChineseTextField.text")); // NOI18N
        GuessChineseTextField.setName("GuessChineseTextField"); // NOI18N
        GuessChineseTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuessChineseTextFieldActionPerformed(evt);
            }
        });
        GuessChineseTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                GuessChineseTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                GuessChineseTextFieldFocusLost(evt);
            }
        });

        PreviousCharacterLabel.setFont(resourceMap.getFont("PreviousCharacterLabel.font")); // NOI18N
        PreviousCharacterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PreviousCharacterLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PreviousCharacterLabel.setName("PreviousCharacterLabel"); // NOI18N

        PreviouslyLabel.setText(resourceMap.getString("PreviouslyLabel.text")); // NOI18N
        PreviouslyLabel.setName("PreviouslyLabel"); // NOI18N

        GoodOrBadLabel.setText(resourceMap.getString("GoodOrBadLabel.text")); // NOI18N
        GoodOrBadLabel.setName("GoodOrBadLabel"); // NOI18N

        PreviousCharDBScroll.setName("PreviousCharDBScroll"); // NOI18N
        PreviousCharDBScroll.setPreferredSize(new java.awt.Dimension(447, 402));

        PreviousCharDBTable.setModel(this);
        PreviousCharDBTable.setName("PreviousCharDBTable"); // NOI18N
        PreviousCharDBTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PreviousCharDBTableMouseClicked(evt);
            }
        });
        PreviousCharDBScroll.setViewportView(PreviousCharDBTable);

        PreviousPinyinsLabel.setText(resourceMap.getString("PreviousPinyinsLabel.text")); // NOI18N
        PreviousPinyinsLabel.setName("PreviousPinyinsLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PreviouslyLabel)
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(GoodOrBadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(PreviousCharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(8, 8, 8)
                                .addComponent(PreviousPinyinsLabel))
                            .addComponent(DoneGuessCharacterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PreviousCharDBScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PinyinsLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(PinyinsLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(GuessPinyinTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                            .addComponent(GuessChineseTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PinyinsLabel)
                            .addComponent(GuessPinyinTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PinyinsLabel1)
                            .addComponent(GuessChineseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DoneGuessCharacterButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(PreviousCharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(PreviouslyLabel)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(35, 35, 35)
                                        .addComponent(PreviousCharDBScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)))
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(77, 77, 77)
                                        .addComponent(GoodOrBadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(PreviousPinyinsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
        );
    }// </editor-fold>//GEN-END:initComponents

private void DoneGuessCharacterButton_action(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DoneGuessCharacterButton_action

    check_old_data();
    set_new_character_to_guess();
    GuessPinyinTextField.requestFocus();
}//GEN-LAST:event_DoneGuessCharacterButton_action

private void PreviousCharDBTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PreviousCharDBTableMouseClicked
    if (evt.getClickCount() == 2)
    {
        int row = PreviousCharDBTable.getSelectedRow();
        String chinese_word;
        chinese_word = (String) chinese_word_list.get(row);

        parent_app.edit_word(chinese_word);
    }
}//GEN-LAST:event_PreviousCharDBTableMouseClicked

private void GuessChineseTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_GuessChineseTextFieldFocusLost
    GuessChineseTextField.getInputContext().selectInputMethod(Locale.getDefault());
}//GEN-LAST:event_GuessChineseTextFieldFocusLost

private void GuessChineseTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_GuessChineseTextFieldFocusGained
    GuessChineseTextField.getInputContext().selectInputMethod(Locale.CHINA);
}//GEN-LAST:event_GuessChineseTextFieldFocusGained

private void GuessPinyinTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuessPinyinTextFieldActionPerformed
    DoneGuessCharacterButton_action(evt);
}//GEN-LAST:event_GuessPinyinTextFieldActionPerformed

private void GuessChineseTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuessChineseTextFieldActionPerformed
    DoneGuessCharacterButton_action(evt);
}//GEN-LAST:event_GuessChineseTextFieldActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CharacterLabel;
    private javax.swing.JButton DoneGuessCharacterButton;
    private javax.swing.JLabel GoodOrBadLabel;
    private javax.swing.JTextField GuessChineseTextField;
    private javax.swing.JTextField GuessPinyinTextField;
    private javax.swing.JLabel PinyinsLabel;
    private javax.swing.JLabel PinyinsLabel1;
    private javax.swing.JScrollPane PreviousCharDBScroll;
    private javax.swing.JTable PreviousCharDBTable;
    private javax.swing.JLabel PreviousCharacterLabel;
    private javax.swing.JLabel PreviousPinyinsLabel;
    private javax.swing.JLabel PreviouslyLabel;
    // End of variables declaration//GEN-END:variables
    private DBTableFiller char_table_filler;
    private ArrayList<String> character_history;
    private HanziDB main_database;
    private HanziApplicationUpdater parent_app;
    private String current_character;
    private ArrayList<String> current_pinyins;
    private ArrayList<String> current_chinese_words;
    private String previous_character;
    private ArrayList<String> previous_pinyins;
    private ArrayList<String> previous_chinese_words;
    private ArrayList<String> guess_pinyins;
    private ArrayList<String> guess_chinese_words;
    private ArrayList<String> chinese_word_list;
    private ArrayList<Integer> chinese_word_list_state; // 0=good, 1=bad, 2=other

    public int getRowCount()
    {
        return chinese_word_list.size();
    }

    public int getColumnCount()
    {
        return 1;
    }

    public String getColumnName(int columnIndex)
    {
        if (columnIndex != 0)
        {
            return "column " + columnIndex + " ??";
        }
        else
        {
            return "Chinese Word";
        }
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        int state;
        state = chinese_word_list_state.get(rowIndex);
        switch (state)
        {
        case 0:
            return "<HTML><FONT COLOR=\"RED\">" + chinese_word_list.get(rowIndex) + "</FONT></HTML>";
        case 1:
            return "<HTML><FONT COLOR=\"GREEN\">" + chinese_word_list.get(rowIndex) + "</FONT></HTML>";
        case 2:
            return chinese_word_list.get(rowIndex);
        }
        return "??";
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        return;
    }

    public void addTableModelListener(TableModelListener l)
    {
    }

    public void removeTableModelListener(TableModelListener l)
    {
    }
}
