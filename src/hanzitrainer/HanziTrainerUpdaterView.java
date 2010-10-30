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

import java.awt.Dialog;
import java.io.File;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.io.FileNotFoundException;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;

/**
 * The application's main frame.
 */
public class HanziTrainerUpdaterView extends FrameView
{
    private JPanel main_panel;
    private JTextField file_field;
    private JButton browse_button;
    private JButton update_button;
    private JLabel status_label;
    private JFileChooser db_file_chooser=null;
    private GroupLayout layout;

    public HanziTrainerUpdaterView(SingleFrameApplication app) {
        super(app);

    }

    private void initComponents() {
        JPanel main_panel = new JPanel();
        JTextField file_field = new JTextField();
        JButton browse_button = new JButton();
        JButton update_button = new JButton();
        JLabel status_label = new JLabel();

        file_field.setText("");
        status_label.setText("");

        browse_button.setText("Browse...");
        browse_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_dialog();
            }
        });

        update_button.setText("Update !");
        update_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            }
        });

        GroupLayout layout = new GroupLayout(main_panel);
        main_panel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                    .addComponent(file_field)
                    .addComponent(browse_button))
                .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                    .addComponent(update_button)
                    .addComponent(status_label)
                    )
                );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(file_field)
                    .addComponent(browse_button))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(update_button)
                    .addComponent(status_label))
                );

        setComponent(main_panel);
    }

    public void HanziTrainerUpdaterViewKill()
    {
    }

    private void init_file_chooser()
    {
        if (db_file_chooser == null)
        {
            db_file_chooser = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter(
                    "Hanzi Trainer DB files", "ktdb");
            db_file_chooser.setFileFilter(filter);
        }
    }

    private int open_dialog()
    {
        init_file_chooser();
        return db_file_chooser.showOpenDialog(main_panel);
    }

    private void open_database(java.awt.event.ActionEvent evt) {
        int returnVal = open_dialog();

        if (returnVal == db_file_chooser.APPROVE_OPTION)
        {
            File file = db_file_chooser.getSelectedFile();
            System.out.println("Opening: " + file.getPath());
            //main_database.HanziDB_open(file.getPath());
            //update_panel_databases();
        }
        else
        {
            System.out.println("Open command cancelled by user.");
        }
    }
}

