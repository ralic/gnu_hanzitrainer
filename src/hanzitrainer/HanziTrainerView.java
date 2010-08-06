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

        Settings = new SettingsDialog(this.getFrame(), cedict_parser, main_database);

    }

    public void HanziTrainerViewKill()
    {
        String database_file = main_database.HanziDB_get_filename();
        if (main_database.get_database_changed())
        {
            Object[] options =
            {
                "Save",
                "Do not save"
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
        vocabulary_builder.DatabaseChanged();
        word_database.DatabaseChanged();
        char_database.DatabaseChanged();
        char_review.DatabaseChanged();
        char_test.DatabaseChanged();
        chinese_test.DatabaseChanged();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
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

        mainPanel.setName("mainPanel");

        Tabs.setName("Tabs");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(Tabs)
                );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(Tabs)
                );

        Tabs.getAccessibleContext().setAccessibleName("Vocabulary Builder");
        Tabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        menuBar.setName("menuBar");

        fileMenu.setText("File");
        fileMenu.setName("fileMenu");
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
        OpenDBMenuItem.setText("Open Database...");
        OpenDBMenuItem.setName("OpenDBMenuItem");
        OpenDBMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_database(evt);
            }
        });
        fileMenu.add(OpenDBMenuItem);
        OpenDBMenuItem.getAccessibleContext().setAccessibleDescription("Open an existing database");

        CloseDBMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        CloseDBMenuItem.setText("Close Database");
        CloseDBMenuItem.setName("CloseDBMenuItem");
        CloseDBMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_database(evt);
            }
        });
        fileMenu.add(CloseDBMenuItem);

        SaveDBMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SaveDBMenuItem.setText("Save Database");
        SaveDBMenuItem.setName("SaveDBMenuItem");
        SaveDBMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_database(evt);
            }
        });
        fileMenu.add(SaveDBMenuItem);
        SaveDBMenuItem.getAccessibleContext().setAccessibleDescription("Save the database");

        SaveDBAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.CTRL_MASK));
        SaveDBAsMenuItem.setText("Save Database as...");
        SaveDBAsMenuItem.setName("SaveDBAsMenuItem");
        SaveDBAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_database_as(evt);
            }
        });
        fileMenu.add(SaveDBAsMenuItem);

        exitMenuItem.setAction(new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                org.jdesktop.application.Application.getInstance(hanzitrainer.HanziTrainerApp.class).quit(e);
            }
        }
        );
        exitMenuItem.setText("Quit");
        exitMenuItem.setName("exitMenuItem");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText("Help");
        helpMenu.setName("helpMenu");

        OptionsMenuItem.setText("Options...");
        OptionsMenuItem.setName("OptionsMenuItem");
        OptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OptionsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(OptionsMenuItem);

        menuBar.add(helpMenu);

        ChoicePane.setName("ChoicePane");

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }

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

    private void open_database(java.awt.event.ActionEvent evt) {
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
    }

    private void save_database_as(java.awt.event.ActionEvent evt) {
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
    }

    private void save_database(java.awt.event.ActionEvent evt) {
        main_database.HanziDB_save();
    }

    private void close_database(java.awt.event.ActionEvent evt) {
        main_database.HanziDB_close();
        update_panel_databases();
    }

    private void FileMenuSelected(javax.swing.event.MenuEvent evt) {
        if (main_database.HanziDB_get_filename().equals(""))
        {
            SaveDBMenuItem.setEnabled(false);
        }
        else
        {
            SaveDBMenuItem.setEnabled(true);
        }
    }

    private void OptionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        Settings.setVisible(true);
    }

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
