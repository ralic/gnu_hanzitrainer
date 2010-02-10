/*
 * HanziTrainerView.java
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

import java.awt.Dialog;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.File;
import java.util.prefs.*;

/**
 * The application's main frame.
 */
public class HanziTrainerView extends FrameView implements HanziApplicationUpdater
{
    HanziDBscore main_database;
    private Preferences my_preferences;
    CharacterReviewPanel char_review;
    WordDatabasePanel word_database;
    CharacterTestPanel char_test;
    ChineseWordTestPanel chinese_test;
    CharacterDatabasePanel char_database;
    VocabularyBuilderPanel vocabulary_builder;
    ToneTestPanel tone_test;

    CedictParser cedict_parser;

    public HanziTrainerView(SingleFrameApplication app)
    {
        super(app);
        String database_file;

        //my_preferences = Preferences.userRoot();
        my_preferences = Preferences.userNodeForPackage(HanziTrainerApp.class);

        main_database = new HanziDBscore();
        database_file = my_preferences.get("database_filename", "");
        if (!database_file.equals(""))
        {
            System.out.println("Was using " + database_file + " before");
            main_database.HanziDB_open(database_file);
        }
        else
        {
            System.out.print("No previous database to reopen");
        }
        cedict_parser = new CedictParser(this.getFrame());

        initComponents();
        vocabulary_builder = new VocabularyBuilderPanel(main_database, cedict_parser, this);
        Tabs.addTab("Vocabulary Builder", vocabulary_builder);
        word_database = new WordDatabasePanel(main_database, this);
        Tabs.addTab("Word Database", word_database);
        char_database = new CharacterDatabasePanel(main_database, this);
        Tabs.addTab("Character Database", char_database);
        char_review = new CharacterReviewPanel(main_database, this);
        Tabs.addTab("Character Review", char_review);
        char_test = new CharacterTestPanel(main_database, this);
        Tabs.addTab("Character Test", char_test);
        chinese_test = new ChineseWordTestPanel(main_database, this);
        Tabs.addTab("Chinese Test", chinese_test);
        tone_test = new ToneTestPanel(main_database, this);
        Tabs.addTab("Tone Test", tone_test);
        update_panel_databases();

        Settings = new SettingsDialog(this.getFrame(), cedict_parser, main_database, true);
        
    }

    @SuppressWarnings(
                      {
                          "static-access"
                      })
    public void HanziTrainerViewKill()
    {
        String database_file = main_database.HanziDB_get_filename();
        if (main_database.get_database_changed())
        {
            Object[] options =
            {
                "Save",
                "Do no save"
            };
            if (ChoicePane.showOptionDialog(this.getFrame(),
                    "You did not save the database, do you want to save now ?", "Save?",
                    ChoicePane.YES_NO_OPTION, ChoicePane.WARNING_MESSAGE,
                    null, options, options[1]) == ChoicePane.YES_OPTION)
            {
                if (database_file.equals(""))
                {
                    save_database_as(new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, "Save on quit"));
                }
                else
                {
                    main_database.HanziDB_save();
                }
            }
        }

        my_preferences.put("database_filename", database_file);
        main_database.shutdown();
    }

    private void update_panel_databases()
    {
        vocabulary_builder.VocabularyBuilderUpdateDB();
        word_database.WordDatabaseUpdateDB();
        char_database.CharDatabaseUpdateDB();
        char_review.CharacterReviewUpdateDB();
        char_test.CharacterTestUpdateDB();
        chinese_test.ChineseWordTestUpdateDB();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        Tabs = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        OpenDBMenuItem = new javax.swing.JMenuItem();
        CloseDBMenuItem = new javax.swing.JMenuItem();
        SaveDBMenuItem = new javax.swing.JMenuItem();
        SaveDBAsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        OptionsMenuItem = new javax.swing.JMenuItem();
        vocabularyEditorButtonGroup = new javax.swing.ButtonGroup();
        ChoicePane = new javax.swing.JOptionPane();

        mainPanel.setName("mainPanel"); // NOI18N

        Tabs.setName("Tabs"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
        );

        Tabs.getAccessibleContext().setAccessibleName("Vocabulary Builder"); // NOI18N

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText("File"); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                FileMenuSelected(evt);
            }
        });

        OpenDBMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        OpenDBMenuItem.setMnemonic('O');
        OpenDBMenuItem.setText("Open Database..."); // NOI18N
        OpenDBMenuItem.setName("OpenDBMenuItem"); // NOI18N
        OpenDBMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_database(evt);
            }
        });
        fileMenu.add(OpenDBMenuItem);
        OpenDBMenuItem.getAccessibleContext().setAccessibleDescription("Open an existing database"); // NOI18N

        CloseDBMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        CloseDBMenuItem.setText("Close Database"); // NOI18N
        CloseDBMenuItem.setName("CloseDBMenuItem"); // NOI18N
        CloseDBMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_database(evt);
            }
        });
        fileMenu.add(CloseDBMenuItem);

        SaveDBMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SaveDBMenuItem.setText("Save Database"); // NOI18N
        SaveDBMenuItem.setName("SaveDBMenuItem"); // NOI18N
        SaveDBMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_database(evt);
            }
        });
        fileMenu.add(SaveDBMenuItem);
        SaveDBMenuItem.getAccessibleContext().setAccessibleDescription("Save the database"); // NOI18N

        SaveDBAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.CTRL_MASK));
        SaveDBAsMenuItem.setText("Save Database as..."); // NOI18N
        SaveDBAsMenuItem.setName("SaveDBAsMenuItem"); // NOI18N
        SaveDBAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_database_as(evt);
            }
        });
        fileMenu.add(SaveDBAsMenuItem);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(hanzitrainer.HanziTrainerApp.class).getContext().getActionMap(HanziTrainerView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText("Help"); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        OptionsMenuItem.setText("Options..."); // NOI18N
        OptionsMenuItem.setName("OptionsMenuItem"); // NOI18N
        OptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OptionsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(OptionsMenuItem);

        menuBar.add(helpMenu);

        ChoicePane.setName("ChoicePane"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void init_file_chooser()
    {
        if (DBFileChooser == null)
        {
            DBFileChooser = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter(
                    "Hanzi Trainer DB files", "ktdb");
            DBFileChooser.setFileFilter(filter);
        }
    }

    private int open_dialog()
    {
        init_file_chooser();
        return DBFileChooser.showOpenDialog(mainPanel);
    }

    private int save_dialog()
    {
        init_file_chooser();
        return DBFileChooser.showSaveDialog(mainPanel);
    }

    @SuppressWarnings("static-access")
private void open_database(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_database
        int returnVal = open_dialog();

        if (returnVal == DBFileChooser.APPROVE_OPTION)
        {
            File file = DBFileChooser.getSelectedFile();
            System.out.println("Opening: " + file.getPath());
            main_database.HanziDB_open(file.getPath());
            update_panel_databases();
        }
        else
        {
            System.out.println("Open command cancelled by user.");
        }
}//GEN-LAST:event_open_database

    @SuppressWarnings("static-access")
private void save_database_as(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_database_as
        int returnVal = save_dialog();
        if (returnVal == DBFileChooser.APPROVE_OPTION)
        {
            File file = DBFileChooser.getSelectedFile();
            System.out.println("Saving as: " + file.getPath());
            main_database.HanziDB_set_filename(file.getPath());
            main_database.HanziDB_save();
        }
        else
        {
            System.out.println("Open command cancelled by user.");
        }
}//GEN-LAST:event_save_database_as

private void save_database(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_database
    main_database.HanziDB_save();
}//GEN-LAST:event_save_database

private void close_database(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_database
    main_database.HanziDB_close();
    update_panel_databases();
}//GEN-LAST:event_close_database

private void FileMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_FileMenuSelected
    if (main_database.HanziDB_get_filename().equals(""))
    {
        SaveDBMenuItem.setEnabled(false);
    }
    else
    {
        SaveDBMenuItem.setEnabled(true);
    }
}//GEN-LAST:event_FileMenuSelected

private void OptionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OptionsMenuItemActionPerformed
    Settings.setVisible(true);
}//GEN-LAST:event_OptionsMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JOptionPane ChoicePane;
    private javax.swing.JMenuItem CloseDBMenuItem;
    private javax.swing.JMenuItem OpenDBMenuItem;
    private javax.swing.JMenuItem OptionsMenuItem;
    private javax.swing.JMenuItem SaveDBAsMenuItem;
    private javax.swing.JMenuItem SaveDBMenuItem;
    private javax.swing.JTabbedPane Tabs;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.ButtonGroup vocabularyEditorButtonGroup;
    // End of variables declaration//GEN-END:variables
    private PinyinChooserFrame PinyinChooser;
    private javax.swing.JFileChooser DBFileChooser;
    private SettingsDialog Settings;

    public void edit_word(String to_edit)
    {
        vocabulary_builder.edit_word(to_edit);
        Tabs.setSelectedIndex(0);
    }
    public void update_database()
    {
        update_panel_databases();
    }
    public void review_character(String hanzi)
    {
        char_review.set_character_review(hanzi);
        Tabs.setSelectedIndex(3);
    }
}
