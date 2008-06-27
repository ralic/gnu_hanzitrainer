/*
 * CharacterTest.java
 *
 * Created on June 20, 2008, 4:19 PM
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

import java.util.ArrayList;
import java.util.Locale;
import javax.swing.event.EventListenerList;
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
        ChineseWordList = new ArrayList<String>();
        ChineseWordListState = new ArrayList<Integer>();
        CurrentChineseWords = new ArrayList<String>();
        GuessPinyins = new ArrayList<String>();
        GuessChineseWords = new ArrayList<String>();

        set_new_character_to_guess();
    }

    public void CharacterReviewUpdateDB()
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


        PreviousCharacter = CurrentCharacter;
        PreviousPinyins = CurrentPinyins;
        PreviousChineseWords = CurrentChineseWords;

        guessed_pinyins = GuessPinyinTextField.getText().split("[,，]");
        GuessPinyins.clear();
        for (String item : guessed_pinyins)
        {
            GuessPinyins.add(item.trim());
        }
        for (String item : GuessPinyins)
        {
            if (!PreviousPinyins.contains(item))
            {
                bad_pinyins.add(item);
            }
            else
            {
                good_pinyins.add(item);
            }
        }
        for (String item : PreviousPinyins)
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
            GuessChineseWords.clear();
            for (String item : guessed_chinese)
            {
                GuessChineseWords.add(item.trim());
            }
            for (String item : GuessChineseWords)
            {
                if (!PreviousChineseWords.contains(item))
                {
                    bad_chinese.add(item);
                }
                else
                {
                    good_chinese.add(item);
                }
            }
        }
        for (String item : PreviousChineseWords)
        {
            if ((!good_chinese.contains(item)) && (!bad_chinese.contains(item)))
            {
                other_chinese.add(item);
            }
        }
        ChineseWordList.clear();
        ChineseWordListState.clear();
        for (String item : bad_chinese)
        {
            ChineseWordList.add(item);
            ChineseWordListState.add(0);
        }
        for (String item : good_chinese)
        {
            ChineseWordList.add(item);
            ChineseWordListState.add(1);
        }
        for (String item : other_chinese)
        {
            ChineseWordList.add(item);
            ChineseWordListState.add(2);
        }
        PreviousCharDBTable.tableChanged(t_event);

        PreviousPinyinsLabel.setText(pinyins_result);
        PreviousCharacterLabel.setText(PreviousCharacter);
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
            index = (int) (Math.random() * num_char);
            hanzi = main_database.get_character_details(index);
        }
        while (character_history.contains(hanzi));
        character_history.add(hanzi);
        if (character_history.size() > (num_char - 1) / 2)
        {
            character_history.remove(0);
        }
        CurrentChineseWords.clear();
        words = main_database.get_words_with_character(hanzi);
        for (ArrayList<String> word : words)
        {
            CurrentChineseWords.add(word.get(0));
        }
        CurrentPinyins = main_database.get_pinyin_from_character(hanzi);
        CurrentCharacter = hanzi;
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
        CharTableFiller = new DBTableFiller(main_database);
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
                .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PinyinsLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(PinyinsLabel1, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PreviouslyLabel)))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(GuessChineseTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                            .addComponent(GuessPinyinTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                            .addComponent(DoneGuessCharacterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PreviousCharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(GoodOrBadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(PreviousPinyinsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PreviousCharDBScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PinyinsLabel)
                            .addComponent(GuessPinyinTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PinyinsLabel1)
                            .addComponent(GuessChineseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DoneGuessCharacterButton)))
                .addContainerGap(88, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(111, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PreviousCharacterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GoodOrBadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PreviouslyLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PreviousPinyinsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(PreviousCharDBScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
        chinese_word = (String) ChineseWordList.get(row);

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
    private DBTableFiller CharTableFiller;
    private ArrayList<String> character_history;
    private HanziDB main_database;
    private HanziApplicationUpdater parent_app;
    private String CurrentCharacter;
    private ArrayList<String> CurrentPinyins;
    private ArrayList<String> CurrentChineseWords;
    private String PreviousCharacter;
    private ArrayList<String> PreviousPinyins;
    private ArrayList<String> PreviousChineseWords;
    private ArrayList<String> GuessPinyins;
    private ArrayList<String> GuessChineseWords;
    private ArrayList<String> ChineseWordList;
    private ArrayList<Integer> ChineseWordListState; // 0=good, 1=bad, 2=other

    public int getRowCount()
    {
        return ChineseWordList.size();
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
        state = ChineseWordListState.get(rowIndex);
        switch (state)
        {
        case 0:
            return "<HTML><FONT COLOR=\"RED\">" + ChineseWordList.get(rowIndex) + "</FONT></HTML>";
        case 1:
            return "<HTML><FONT COLOR=\"GREEN\">" + ChineseWordList.get(rowIndex) + "</FONT></HTML>";
        case 2:
            return ChineseWordList.get(rowIndex);
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
