/*
 * CedictPanel.java
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

import java.util.prefs.Preferences;
import java.io.File;
import javax.swing.JFileChooser;

public class CedictPanel extends javax.swing.JPanel
{

    public CedictPanel(hanzitrainer.CedictParser cedict)
    {
        Preferences my_preferences;
        String cedict_file;

        cedict_parser = cedict;

        initComponents();

        my_preferences = Preferences.userNodeForPackage(hanzitrainer.HanziTrainerApp.class);
        cedict_file = my_preferences.get("cedict file :", "");
        CedictFileField.setText(cedict_file);
    }

    private void initComponents() {
        CedictFileBrowseButton = new javax.swing.JButton();
        CedictApplyButton = new javax.swing.JButton();
        CedictFileField = new javax.swing.JTextField();
        CedictFileLabel = new javax.swing.JLabel();
        CedictStatusLabel = new javax.swing.JLabel();

        CedictFileLabel.setText("Cedict file :");

        CedictFileField.setText("");

        CedictFileBrowseButton.setText("Browse...");
        CedictFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CedictFileBrowseButtonActionPerformed(evt);
            }
        });


        CedictApplyButton.setText("Apply");
        CedictApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CedictApplyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(CedictFileLabel)
                    .addComponent(CedictFileField)
                    .addComponent(CedictFileBrowseButton))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(CedictStatusLabel)
                    .addComponent(CedictApplyButton))
                );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CedictFileLabel)
                    .addComponent(CedictFileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CedictFileBrowseButton))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CedictStatusLabel)
                    .addComponent(CedictApplyButton))
                );
    }
    private void CedictFileBrowseButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        int returnVal;

        if (CedictFileChooser == null)
        {
            CedictFileChooser = new javax.swing.JFileChooser();
        }
        returnVal = CedictFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = CedictFileChooser.getSelectedFile();
            CedictFileField.setText(file.getPath());
        }
    }
    private void CedictApplyButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        Preferences my_preferences;
        String cedictFile;

        my_preferences = Preferences.userNodeForPackage(hanzitrainer.HanziTrainerApp.class);

        cedictFile = CedictFileField.getText();
        if (cedict_parser.Cedict_import(cedictFile) == 0)
        {
            my_preferences.put("cedict file :", cedictFile);
        }
    }

    private javax.swing.JButton CedictFileBrowseButton;
    private javax.swing.JButton CedictApplyButton;
    private javax.swing.JTextField CedictFileField;
    private javax.swing.JFileChooser CedictFileChooser;
    private javax.swing.JLabel CedictStatusLabel;
    private javax.swing.JLabel CedictFileLabel;

    private hanzitrainer.CedictParser cedict_parser;
}

