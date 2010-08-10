/*
 * VocabularyBuilderPanel.java
 *
 * Created on April 23, 2009, 2:24 AM
 */
package hanzitrainer;

import java.awt.Component;
import java.awt.Font;
import java.util.Locale;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  matthieu
 */
public class VocabularyBuilderPanel extends javax.swing.JPanel
    implements ListSelectionListener, hanzitrainer.internals.HanziTab
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
        EnglishTextField = new javax.swing.JTextField();
        AddButton = new javax.swing.JButton();
        ResetButton = new javax.swing.JButton();
        PinyinScroll = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        EnglishTranslationsListModel = new DefaultListModel();
        ETRenderer = new EnglishTranslationsRenderer();
        EnglishTranslations = new javax.swing.JList();

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

        EnglishTextField.setName("EnglishTextField");
        EnglishTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EnglishTextFieldSaveButtonActionPerformed(evt);
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

        jScrollPane1.setName("jScrollPane1");

        EnglishTranslations.setModel(EnglishTranslationsListModel);
        EnglishTranslations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        EnglishTranslations.setName("EnglishTranslations");
        EnglishTranslations.addListSelectionListener(this);
        EnglishTranslations.setCellRenderer(ETRenderer);
        jScrollPane1.setViewportView(EnglishTranslations);

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
                    .addComponent(jScrollPane1)
                    .addComponent(EnglishTextField)
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
                        .addComponent(jScrollPane1)
                        .addComponent(EnglishTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ResetButton)
                            .addComponent(AddButton)))
                    .addComponent(EnglishLabel))
                );
    }

    private class EnglishTranslationsRenderer extends JLabel implements ListCellRenderer
    {
        private int change_index;
        private DefaultListCellRenderer default_renderer;

        public EnglishTranslationsRenderer()
        {
            change_index = -1;
            default_renderer = new DefaultListCellRenderer();
        }

        public EnglishTranslationsRenderer(int index)
        {
            change_index = index;
            default_renderer = new DefaultListCellRenderer();
        }

        public void set_change_index(int index)
        {
            change_index = index;
        }

        @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                Component comp = default_renderer.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                setText(value.toString());
                if ((change_index != -1) && (index >= change_index))
                {
                    System.out.println("index " + index + ", change " + change_index + ", text " +value.toString());
                    setFont(comp.getFont().deriveFont(Font.ITALIC));
                }
                else
                {
                    setFont(comp.getFont().deriveFont(Font.PLAIN));
                }

                return this;
            }
    }

    private void ChineseTextFieldFocusGained(java.awt.event.FocusEvent evt) {
        ChineseTextField.getInputContext().selectInputMethod(Locale.CHINA);
    }

    protected String getChineseTextField()
    {
        return ChineseTextField.getText();
    }

    protected void setEnglishTranslationsList(ArrayList<String> content)
    {
        int i;

        EnglishTranslationsListModel.removeAllElements();
        EnglishTranslationsListModel.addElement("[new]");
        for (i = 0; i < content.size(); i++)
        {
            EnglishTranslationsListModel.addElement(content.get(i));
        }
        EnglishTranslations.setSelectedIndex(0);
        EnglishTranslations.ensureIndexIsVisible(0);
    }

    protected void setEnglishTranslationsList(ArrayList<String> content, ArrayList<String> secondary)
    {
        int i;

        EnglishTranslationsListModel.removeAllElements();
        EnglishTranslationsListModel.addElement("[new]");
        for (i = 0; i < content.size(); i++)
        {
            EnglishTranslationsListModel.addElement(content.get(i));
        }
        for (i = 0; i < secondary.size(); i++)
        {
            EnglishTranslationsListModel.addElement(secondary.get(i));
        }
        ETRenderer.set_change_index(content.size()+1);
        EnglishTranslations.setSelectedIndex(0);
        EnglishTranslations.ensureIndexIsVisible(0);
    }

    private void SaveAction()
    {
        String english = EnglishTextField.getText();
        ArrayList<String> pinyin = PinyinChooser.get_pinyins();
        String hanzi_string = ChineseTextField.getText();
        ArrayList<String> hanzi = new ArrayList<String>();
        int i, listIndex;

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

        listIndex = EnglishTranslations.getSelectedIndex();

        if (listIndex == 0)
        {
            if (english.length() == 0)
            {
                return;
            }
            StringTokenizer english_tokens = new StringTokenizer(english, ",");

            while (english_tokens.hasMoreTokens())
            {
                String current_token = english_tokens.nextToken().trim();

                main_database.add_translation(current_token, pinyin, hanzi);
            }
        }
        else
        {
            if (english.length() == 0)
            {
                main_database.delete_translation(
                        (String) EnglishTranslationsListModel.getElementAt(EnglishTranslations.getSelectedIndex()), hanzi);
            }
            else
            {
                if (EnglishTranslations.getSelectedIndex() == 0)
                {
                    return;
                }
                if (english.length() == 0)
                {
                    return;
                }
                main_database.delete_translation(
                        (String) EnglishTranslationsListModel.getElementAt(EnglishTranslations.getSelectedIndex()), hanzi);
                main_database.add_translation(english, pinyin, hanzi);
            }
        }

        parent_app.update_database();

        EnglishTranslationsListModel.removeAllElements();
        ChineseTextField.setText("");
        EnglishTextField.setText("");
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
        EnglishTextField.setText("");
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
    private javax.swing.JTextField EnglishTextField;
    private javax.swing.JList EnglishTranslations;
    private javax.swing.JLabel PinyinLabel;
    private javax.swing.JScrollPane PinyinScroll;
    private javax.swing.JButton ResetButton;
    private javax.swing.JButton AddButton;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration
    private DefaultListModel EnglishTranslationsListModel;
    private EnglishTranslationsRenderer ETRenderer;
    private HanziDBscore main_database;
    private HanziApplicationUpdater parent_app;
    private CedictParser cedict_database=null;
    private PinyinChooserFrame PinyinChooser;
    private VocabularyBuilderPanelUpdater VocabularyBuilderUpdater;

    public void valueChanged(ListSelectionEvent e)
    {
        int listIndex;
        String value;

        listIndex = EnglishTranslations.getSelectedIndex();
        if ((listIndex != 0) && (listIndex != -1))
        {
            value = (String) EnglishTranslationsListModel.getElementAt(EnglishTranslations.getSelectedIndex());
            EnglishTextField.setText(value);
        }
    }
}
