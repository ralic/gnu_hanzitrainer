/*
 * ChineseWordTestPanel.java
 *
 * Created on July 1, 2008, 11:33 AM
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author  Administrator
 */
public class ChineseWordTestPanel extends javax.swing.JPanel
    implements hanzitrainer.internals.HanziTab
{

    /** Creates new form ChineseWordTestPanel */
    public ChineseWordTestPanel(HanziDBscore database, HanziApplicationUpdater updater)
    {
        main_database = database;
        parent_app = updater;
        initComponents();
        chinese_word_history = new ArrayList<String>();
        set_new_word_guess();
    }

    public void FontPreferenceChanged(java.awt.Font character_font, java.awt.Font chinese_font)
    {
    }
    public void DatabaseChanged()
    {
        set_new_word_guess();
    }
    public void CedictDatabaseChanged() {}

    private void set_new_word_guess()
    {
        int num_words = main_database.get_number_words();
        int index, id;
        ArrayList<String> word_information;

        if (num_words == 0)
        {
            return;
        }
        
        do {
            index = (int) (SettingsDialog.random_low() * num_words);
            id = main_database.get_word_id(index);
            word_information = main_database.get_word_details(id);
        } while (chinese_word_history.contains(word_information.get(0)));

        chinese_word_history.add(word_information.get(0));
        if (chinese_word_history.size() > num_words / 2)
        {
            chinese_word_history.remove(0);
        }
        current_translation = word_information.get(2);
        current_pinyin = word_information.get(1);
        current_chinese = word_information.get(0);

        TranslationLabel.setText(current_translation);
        ChineseGuessTextField.setText("");
    }

    private void check_old_data()
    {
        int id;

        previous_correct_chinese = current_chinese;
        previous_pinyin = current_pinyin;
        previous_translation = current_translation;
        previous_chinese_guess = ChineseGuessTextField.getText();

        PreviousTranslationLabel.setText(previous_translation);
        PreviousChineseLabel.setText(previous_correct_chinese);
        PreviouspinyinLabel.setText(PinyinParser.convert_to_printed_version(previous_pinyin));
        GuessLabel.setText(previous_chinese_guess);
        if (previous_chinese_guess.equals(previous_correct_chinese))
        {
            GuessLabel.setForeground(Color.green);
            PreviousMeaningLabel.setText("");
            
            // increase score for the correctly guessed word
            main_database.change_word_score(main_database.get_word_id(previous_correct_chinese),
                    true, 1);
        }
        else
        {
            GuessLabel.setForeground(Color.red);
            
            // decrease score for the missed guess
            main_database.change_word_score(main_database.get_word_id(previous_correct_chinese),
                    false, 2);
            id = main_database.get_word_id(previous_chinese_guess);
            if (id != -1)
            {
                PreviousMeaningLabel.setText(main_database.get_word_details(id).get(2));
                
                // if you thought it was something else, decrease also that one
                main_database.change_word_score(id, false, 2);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        EnglishLabel = new javax.swing.JLabel();
        ChineseGuessLabel = new javax.swing.JLabel();
        ChineseGuessTextField = new javax.swing.JTextField();
        TranslationLabel = new javax.swing.JLabel();
        DoneButton = new javax.swing.JButton();
        PreviousLabel = new javax.swing.JLabel();
        PreviousTranslationLabel = new javax.swing.JLabel();
        CorrectChineseLabel = new javax.swing.JLabel();
        PreviousChineseLabel = new javax.swing.JLabel();
        YourguessLabel = new javax.swing.JLabel();
        GuessLabel = new javax.swing.JLabel();
        MeaningLabel = new javax.swing.JLabel();
        PreviousMeaningLabel = new javax.swing.JLabel();
        PinyinLabel = new javax.swing.JLabel();
        PreviouspinyinLabel = new javax.swing.JLabel();
        EditPreviousWordButton = new javax.swing.JButton();
        EditGuess = new javax.swing.JButton();

        setName("Form"); // NOI18N

        EnglishLabel.setText("English translation :"); // NOI18N
        EnglishLabel.setName("EnglishLabel"); // NOI18N

        ChineseGuessLabel.setText("Chinese guess :"); // NOI18N
        ChineseGuessLabel.setName("ChineseGuessLabel"); // NOI18N

        ChineseGuessTextField.setText(""); // NOI18N
        ChineseGuessTextField.setName("ChineseGuessTextField"); // NOI18N
        ChineseGuessTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DoneButtonActionPerformed(evt);
            }
        });
        ChineseGuessTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ChineseGuessTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ChineseGuessTextFieldFocusLost(evt);
            }
        });

        TranslationLabel.setText(""); // NOI18N
        TranslationLabel.setName("TranslationLabel"); // NOI18N

        DoneButton.setText("Done"); // NOI18N
        DoneButton.setName("DoneButton"); // NOI18N
        DoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DoneButtonActionPerformed(evt);
            }
        });

        PreviousLabel.setText("Previous :"); // NOI18N
        PreviousLabel.setName("PreviousLabel"); // NOI18N

        PreviousTranslationLabel.setText(""); // NOI18N
        PreviousTranslationLabel.setName("PreviousTranslationLabel"); // NOI18N

        CorrectChineseLabel.setText("Correct chinese :"); // NOI18N
        CorrectChineseLabel.setName("CorrectChineseLabel"); // NOI18N

        PreviousChineseLabel.setText(""); // NOI18N
        PreviousChineseLabel.setName("PreviousChineseLabel"); // NOI18N

        YourguessLabel.setText("Your guess :"); // NOI18N
        YourguessLabel.setName("YourguessLabel"); // NOI18N

        GuessLabel.setText(""); // NOI18N
        GuessLabel.setName("GuessLabel"); // NOI18N

        MeaningLabel.setText(""); // NOI18N
        MeaningLabel.setName("MeaningLabel"); // NOI18N

        PreviousMeaningLabel.setText(""); // NOI18N
        PreviousMeaningLabel.setName("PreviousMeaningLabel"); // NOI18N

        PinyinLabel.setText("Pinyin :"); // NOI18N
        PinyinLabel.setName("PinyinLabel"); // NOI18N

        PreviouspinyinLabel.setText(""); // NOI18N
        PreviouspinyinLabel.setName("PreviouspinyinLabel"); // NOI18N

        EditPreviousWordButton.setText("Edit previous word"); // NOI18N
        EditPreviousWordButton.setName("EditPreviousWordButton"); // NOI18N
        EditPreviousWordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditPreviousWordButtonActionPerformed(evt);
            }
        });

        EditGuess.setText("Edit my guess"); // NOI18N
        EditGuess.setName("EditGuess"); // NOI18N
        EditGuess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditGuessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChineseGuessLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(EnglishLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TranslationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(MeaningLabel)
                    .addComponent(YourguessLabel)
                    .addComponent(PinyinLabel)
                    .addComponent(CorrectChineseLabel)
                    .addComponent(PreviousLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(EditPreviousWordButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EditGuess))
                    .addComponent(PreviousTranslationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(ChineseGuessTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(PreviousChineseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(PreviouspinyinLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(GuessLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addComponent(PreviousMeaningLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(210, 210, 210)
                .addComponent(DoneButton)
                .addContainerGap(242, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EnglishLabel)
                    .addComponent(TranslationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ChineseGuessLabel)
                    .addComponent(ChineseGuessTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DoneButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(PreviousTranslationLabel)
                    .addComponent(PreviousLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(PreviousChineseLabel)
                    .addComponent(CorrectChineseLabel))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(PreviouspinyinLabel)
                    .addComponent(PinyinLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(GuessLabel)
                    .addComponent(YourguessLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(PreviousMeaningLabel)
                    .addComponent(MeaningLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EditPreviousWordButton)
                    .addComponent(EditGuess))
                .addContainerGap(41, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void ChineseGuessTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ChineseGuessTextFieldFocusGained
    ChineseGuessTextField.getInputContext().selectInputMethod(Locale.CHINA);
}//GEN-LAST:event_ChineseGuessTextFieldFocusGained

private void ChineseGuessTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ChineseGuessTextFieldFocusLost
    ChineseGuessTextField.getInputContext().selectInputMethod(Locale.getDefault());
}//GEN-LAST:event_ChineseGuessTextFieldFocusLost

private void DoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DoneButtonActionPerformed
    check_old_data();
    set_new_word_guess();
}//GEN-LAST:event_DoneButtonActionPerformed

private void EditGuessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditGuessActionPerformed
    parent_app.edit_word(previous_chinese_guess);
}//GEN-LAST:event_EditGuessActionPerformed

private void EditPreviousWordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditPreviousWordButtonActionPerformed
    parent_app.edit_word(previous_correct_chinese);
}//GEN-LAST:event_EditPreviousWordButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ChineseGuessLabel;
    private javax.swing.JTextField ChineseGuessTextField;
    private javax.swing.JLabel CorrectChineseLabel;
    private javax.swing.JButton DoneButton;
    private javax.swing.JButton EditGuess;
    private javax.swing.JButton EditPreviousWordButton;
    private javax.swing.JLabel EnglishLabel;
    private javax.swing.JLabel GuessLabel;
    private javax.swing.JLabel MeaningLabel;
    private javax.swing.JLabel PinyinLabel;
    private javax.swing.JLabel PreviousChineseLabel;
    private javax.swing.JLabel PreviousLabel;
    private javax.swing.JLabel PreviousMeaningLabel;
    private javax.swing.JLabel PreviousTranslationLabel;
    private javax.swing.JLabel PreviouspinyinLabel;
    private javax.swing.JLabel TranslationLabel;
    private javax.swing.JLabel YourguessLabel;
    // End of variables declaration//GEN-END:variables
    private HanziDBscore main_database;
    private HanziApplicationUpdater parent_app;
    private ArrayList<String> chinese_word_history;
    private String current_translation;
    private String current_pinyin;
    private String current_chinese;
    private String previous_chinese_guess;
    private String previous_correct_chinese;
    private String previous_pinyin;
    private String previous_translation;
    private String previous_meaning_from_guess;
}
