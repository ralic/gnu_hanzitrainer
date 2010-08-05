/*
 * FontPanel.java
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

package hanzitrainer.settings;

import java.awt.Font;

public class FontPanel extends javax.swing.JPanel
{

    public FontPanel()
    {
        initComponents();
    }

    public Font get_chosen_font()
    {
        return null;
    }
    
    private void initComponents() {
        CharacterFontButton = new javax.swing.JButton();
        ChineseTextFontButton = new javax.swing.JButton();

        CharacterTextField = new javax.swing.JTextField();
        ChineseTextField = new javax.swing.JTextField();

        character_font_chooser = new hanzitrainer.settings.JFontChooser();
        chinese_text_font_chooser = new hanzitrainer.settings.JFontChooser();

        setName("FontPanel");

        CharacterFontButton.setText("Font for Chinese characters display");
        CharacterFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CharacterFontButton_action(evt);
            }
        });

        ChineseTextFontButton.setText("Font for Chinese text fields");
        ChineseTextFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChineseTextFontButton_action(evt);
            }
        });

        CharacterTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CharacterTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0)));
        CharacterTextField.setText("水");

        ChineseTextField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChineseTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0)));
        ChineseTextField.setText("水静流深");
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CharacterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ChineseTextField))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CharacterFontButton)
                    .addComponent(ChineseTextFontButton))
                );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CharacterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CharacterFontButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChineseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ChineseTextFontButton))
                );
    }

    private void CharacterFontButton_action(java.awt.event.ActionEvent evt)
    {
        int result = character_font_chooser.showDialog(this);
        if (result == hanzitrainer.settings.JFontChooser.OK_OPTION)
        {
        }
    }

    private void ChineseTextFontButton_action(java.awt.event.ActionEvent evt)
    {
        int result = chinese_text_font_chooser.showDialog(this);
        if (result == hanzitrainer.settings.JFontChooser.OK_OPTION)
        {
        }
    }

    javax.swing.JButton CharacterFontButton;
    javax.swing.JButton ChineseTextFontButton;
    javax.swing.JTextField CharacterTextField;
    javax.swing.JTextField ChineseTextField;

    hanzitrainer.settings.JFontChooser character_font_chooser;
    hanzitrainer.settings.JFontChooser chinese_text_font_chooser;
}
