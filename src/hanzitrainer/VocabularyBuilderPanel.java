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

import java.awt.Component;
import java.awt.Font;
import java.util.Locale;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.FlowLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 *
 * @author  matthieu
 */
public class VocabularyBuilderPanel extends javax.swing.JPanel
    implements hanzitrainer.internals.HanziTab
{

    /** Creates new form VocabularyBuilderPanel */
    public VocabularyBuilderPanel(HanziDBscore database, HanziApplicationUpdater updater)
    {
        main_database = database;
        parent_app = updater;
        initComponents();
    }

    /** Creates new form VocabularyBuilderPanel and include the Cedict parser */
    public VocabularyBuilderPanel(HanziDBscore database, CedictParser cedict, HanziApplicationUpdater updater)
    {
        main_database = database;
        cedict_database = cedict;
        parent_app = updater;
        initComponents();
    }

    public void FontPreferenceChanged(java.awt.Font character_font, java.awt.Font chinese_font)
    {
        ChineseTextField.setFont(chinese_font);
    }

    public void DatabaseChanged()
    {
        VocabularyBuilderUpdater.dataChanged();
    }

    public void CedictDatabaseChanged()
    {
        VocabularyBuilderUpdater.dataChanged();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {

        ChineseLabel = new javax.swing.JLabel();
        PinyinLabel = new javax.swing.JLabel();
        EnglishLabel = new javax.swing.JLabel();
        ChineseTextField = new javax.swing.JTextField();
        AddButton = new javax.swing.JButton();
        ResetButton = new javax.swing.JButton();
        PinyinScroll = new javax.swing.JScrollPane();
        EnglishTranslationsScroll = new javax.swing.JScrollPane();
        ETTableModel = new EnglishTranslationModel();
        EnglishTranslationsTable = new javax.swing.JTable();

        setName("Form");

        ChineseLabel.setText("Chinese");
        ChineseLabel.setName("ChineseLabel");

        PinyinLabel.setText("Pinyin");
        PinyinLabel.setName("PinyinLabel");

        EnglishLabel.setText("English");
        EnglishLabel.setName("EnglishLabel");

        ChineseTextField.setName("ChineseTextField");
        if (cedict_database != null)
        {
            PinyinChooser = new PinyinChooserFrame(PinyinScroll,main_database, cedict_database);
            VocabularyBuilderUpdater = new VocabularyBuilderPanelUpdater(this, main_database, cedict_database);
        }
        else
        {
            PinyinChooser = new PinyinChooserFrame(PinyinScroll,main_database);
            VocabularyBuilderUpdater = new VocabularyBuilderPanelUpdater(this, main_database);
        }
        this.ChineseTextField.getDocument().addDocumentListener(PinyinChooser);
        this.ChineseTextField.getDocument().addDocumentListener(VocabularyBuilderUpdater);
        ChineseTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ChineseTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ChineseTextFieldFocusLost(evt);
            }
        });

        AddButton.setText("Add");
        AddButton.setName("AddButton");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });

        ResetButton.setText("Reset");
        ResetButton.setDefaultCapable(false);
        ResetButton.setName("ResetButton");
        ResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetButtonActionPerformed(evt);
            }
        });

        PinyinScroll.setName("PinyinScroll");

        EnglishTranslationsScroll.setName("EnglishTranslationsScroll");

        EnglishTranslationsTable.setModel(ETTableModel);
        EnglishTranslationsTable.setTableHeader(null);
        EnglishTranslationsTable.setName("EnglishTranslationsTable");
        EnglishTranslationsScroll.setViewportView(EnglishTranslationsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChineseLabel)
                    .addComponent(PinyinLabel)
                    .addComponent(EnglishLabel))
                .addGap(javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup()
                    .addComponent(ChineseTextField)
                    .addComponent(PinyinScroll)
                    .addComponent(EnglishTranslationsScroll)
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                        .addComponent(ResetButton)
                        .addComponent(AddButton)))
                );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ChineseLabel)
                    .addComponent(ChineseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PinyinLabel)
                    .addComponent(PinyinScroll, 68, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(EnglishTranslationsScroll)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ResetButton)
                            .addComponent(AddButton)))
                    .addComponent(EnglishLabel))
                );
    }

    public enum origin_t
    {
        DB_WORD,
        CEDICT_WORD,
        NEW_WORD,
        EMPTY_WORD
    }

    private class EnglishTranslationItem
    {

        private String word;
        private String original_word;
        private origin_t origin;
        private Boolean enabled;
        private Boolean modified;

        public EnglishTranslationItem(String w, origin_t o, Boolean e)
        {
            word = w;
            original_word = w;
            origin = o;
            enabled = e;
            modified = false;
        }

        public void set_word(String w)
        {
            if ((origin == origin_t.DB_WORD) && (!word.equals(w)))
                modified = true;
            word = w;
        }

        public void set_origin(origin_t o)
        {
            origin = o;
        }

        public void set_enabled(Boolean e)
        {
            enabled = e;
        }

        public String get_word()
        {
            return word;
        }
        
        public origin_t get_origin()
        {
            return origin;
        }
        
        public Boolean get_enabled()
        {
            return enabled;
        }

        public Boolean get_modified()
        {
            return modified;
        }

        public String get_original_word()
        {
            return original_word;
        }

        @Override
        public String toString()
        {
            return word;
        }
    }

    private class EnglishTranslationModel extends DefaultTableModel
    {
        private ArrayList<String> db_words;
        private ArrayList<String> cedict_words;
        private ArrayList<EnglishTranslationItem> words_list;

        public EnglishTranslationModel()
        {
            super();
            db_words = new ArrayList<String>();
            cedict_words = new ArrayList<String>();
            words_list = new ArrayList<EnglishTranslationItem>();
            fireTableDataChanged();
        }

        public EnglishTranslationModel(ArrayList<String> db, ArrayList<String> cedict)
        {
            super();
            db_words = db;
            cedict_words = cedict;
            words_list = new ArrayList<EnglishTranslationItem>();
            prepare_list();
            fireTableDataChanged();
        }

        public void change_content(ArrayList<String> db, ArrayList<String> cedict)
        {
            db_words = db;
            cedict_words.clear();
            if (cedict != null)
                cedict_words.addAll(cedict);
            words_list.clear();
            prepare_list();
            fireTableDataChanged();
        }

        public void clear_content()
        {
            db_words.clear();
            cedict_words.clear();
            words_list.clear();
            fireTableDataChanged();
        }

        private void prepare_list()
        {
            System.out.println("Prepare list with " + db_words.size() + " words in DB and " + cedict_words.size() + " words in cedict");
            int i;
            EnglishTranslationItem item;

            words_list.clear();
            for (i=0; i<db_words.size(); i++)
            {
                item = new EnglishTranslationItem(db_words.get(i), origin_t.DB_WORD, true);
                words_list.add(item);
            }
            item = new EnglishTranslationItem("", origin_t.EMPTY_WORD, false);
            words_list.add(item);
            for (i=0; i<cedict_words.size(); i++)
            {
                if (!db_words.contains(cedict_words.get(i)))
                {
                    item = new EnglishTranslationItem(cedict_words.get(i), origin_t.CEDICT_WORD, false);
                    words_list.add(item);
                }
            }
            System.out.println("Now I have " + words_list.size() + " elements in the list");
            fireTableDataChanged();
        }

        @Override
        public Class getColumnClass(int c)
        {
            switch(c)
            {
                case 0:
                    return Boolean.class;
                case 1:
                default:
                    return String.class;
            }
        }

        @Override
        public int getColumnCount()
        {
            return 2;
        }

        @Override
        public int getRowCount()
        {
            if (words_list == null)
                return 0;
            return words_list.size();
        }

        @Override
        public void setValueAt(Object value, int row, int col)
        {
            EnglishTranslationItem item = words_list.get(row);
            switch(col)
            {
                case 0 :
                    if (item.get_origin() != origin_t.NEW_WORD)
                    {
                        item.set_enabled((Boolean)value);
                        fireTableRowsUpdated(row, row);
                        System.out.println("updated row " + row);
                    }
                    break;
                case 1:
                    switch (item.get_origin())
                    {
                        case DB_WORD:
                        case NEW_WORD:
                            item.set_word((String)value);
                            fireTableRowsUpdated(row, row);
                            System.out.println("updated row " + row);
                            break;
                        case EMPTY_WORD:
                            item.set_word((String)value);
                            item.set_enabled(true);
                            item.set_origin(origin_t.NEW_WORD);
                            fireTableRowsUpdated(row, row);
                            System.out.println("updated row " + row);

                            EnglishTranslationItem new_item = new EnglishTranslationItem("", origin_t.EMPTY_WORD, false);
                            words_list.add(row+1 , new_item);
                            fireTableRowsInserted(row+1,row+1);
                            System.out.println("inserted row " + row +1);
                            break;
                        case CEDICT_WORD:
                        default:
                            break;
                    }
            }

            System.out.println("Trying to change the content at " + row + ", " + col);
            System.out.println("Now I have " + words_list.size() + " elements in the list");

        }

        @Override
        public boolean isCellEditable(int r, int c)
        {
            switch (c)
            {
                case 0:
                    return true;
                case 1:
                    EnglishTranslationItem item = words_list.get(r);
                    if (item.get_origin() != origin_t.CEDICT_WORD)
                        return true;
            }
            return false;
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            EnglishTranslationItem item = words_list.get(row);
            switch(col)
            {
                case 0:
                    return item.get_enabled();
                case 1:
                    String word = item.get_word();
                    switch (item.get_origin())
                    {
                        case DB_WORD:
                        case EMPTY_WORD:
                            return word;
                        case NEW_WORD:
                            return "<html><b>" + word + "</b></html>";
                        case CEDICT_WORD:
                            return "<html><i>" + word + "</i></html>";
                    }
                default:
                    return null;
            }
        }

        public ArrayList<EnglishTranslationItem> getCurrentList()
        {
            return new ArrayList<EnglishTranslationItem>(words_list);
        }
    }

    private void ChineseTextFieldFocusGained(java.awt.event.FocusEvent evt) {
        ChineseTextField.getInputContext().selectInputMethod(Locale.CHINA);
    }

    protected String getChineseTextField()
    {
        return ChineseTextField.getText();
    }

    protected void setEnglishTranslationsList(ArrayList<String> db_words)
    {
        int i;

        ETTableModel.change_content(db_words, null);
    }

    protected void setEnglishTranslationsList(ArrayList<String> db_words, ArrayList<String> cedict_words)
    {
        ETTableModel.change_content(db_words, cedict_words);
    }

    private void SaveAction()
    {
        int i;
        ArrayList<EnglishTranslationItem> state = ETTableModel.getCurrentList();
        ArrayList<String> pinyin = PinyinChooser.get_pinyins();
        String hanzi_string = ChineseTextField.getText();
        ArrayList<String> hanzi = new ArrayList<String>();

        for (i = 0; i < pinyin.size(); i++)
        {
            if (!Pinyin.verify_pinyin(pinyin.get(i)))
            {
                return;
            }
        }
        for (i = 0; i < hanzi_string.codePointCount(0, hanzi_string.length()); i++)
        {
            int from = hanzi_string.offsetByCodePoints(0, i);
            int to = hanzi_string.offsetByCodePoints(0, i + 1);

            hanzi.add(hanzi_string.substring(from, to));
        }


        for (i=0; i<state.size(); i++)
        {
            EnglishTranslationItem item = state.get(i);
            switch (item.get_origin())
            {
                case DB_WORD:
                    if ((item.get_modified()) && (item.get_enabled()))
                    {
                        main_database.add_translation(item.get_word(), pinyin, hanzi);
                        main_database.delete_translation(item.get_original_word(), hanzi);
                    }
                    if (!item.get_enabled())
                    {
                        main_database.delete_translation(item.get_original_word(), hanzi);
                    }
                case NEW_WORD:
                    main_database.add_translation(item.get_word(), pinyin, hanzi);
                    break;
                case CEDICT_WORD:
                    if (item.get_enabled())
                    {
                        main_database.add_translation(item.get_word(), pinyin, hanzi);
                    }
                    break;
                case EMPTY_WORD:
                default:
                    break;
            }
        }
        parent_app.update_database();

        ETTableModel.clear_content();
        ChineseTextField.setText("");
        ChineseTextField.requestFocus();
    }

    private void EnglishTextFieldSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        SaveAction();
    }

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        SaveAction();
    }

    private void ResetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        ChineseTextField.setText("");
    }

    private void ChineseTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        // TODO add your handling code here:
        ChineseTextField.getInputContext().selectInputMethod(Locale.US);
    }

    public void edit_word(String to_edit)
    {
        ChineseTextField.setText(to_edit);
    }
    // Variables declaration - do not modify
    private javax.swing.JLabel ChineseLabel;
    private javax.swing.JTextField ChineseTextField;
    private javax.swing.JLabel EnglishLabel;
    private javax.swing.JTable EnglishTranslationsTable;
    private javax.swing.JLabel PinyinLabel;
    private javax.swing.JScrollPane PinyinScroll;
    private javax.swing.JButton ResetButton;
    private javax.swing.JButton AddButton;
    private javax.swing.JScrollPane EnglishTranslationsScroll;
    // End of variables declaration
    private EnglishTranslationModel ETTableModel;
    private HanziDBscore main_database;
    private HanziApplicationUpdater parent_app;
    private CedictParser cedict_database=null;
    private PinyinChooserFrame PinyinChooser;
    private VocabularyBuilderPanelUpdater VocabularyBuilderUpdater;
}
