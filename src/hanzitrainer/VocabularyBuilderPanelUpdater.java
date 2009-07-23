/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hanzitrainer;

import java.util.ArrayList;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author matthieu
 */
public class VocabularyBuilderPanelUpdater implements javax.swing.event.DocumentListener
{

    private VocabularyBuilderPanel parent_app;
    private HanziDBscore main_database;
    private CedictParser cedict_database = null;

    public VocabularyBuilderPanelUpdater(VocabularyBuilderPanel parent, HanziDBscore database)
    {
        parent_app = parent;
        main_database = database;
    }

    public VocabularyBuilderPanelUpdater(VocabularyBuilderPanel parent, HanziDBscore database, CedictParser cedict)
    {
        parent_app = parent;
        main_database = database;
        cedict_database = cedict;
    }

    private void dataChanged()
    {
        String chinese_text = parent_app.getChineseTextField();
        ArrayList<String> translations = main_database.get_chinese_word_translation(chinese_text);
        if (cedict_database != null)
        {
            ArrayList<String> cedict_translations = cedict_database.get_chinese_word_translation(chinese_text);
            parent_app.setEnglishTranslationsList(translations, cedict_translations);
        }
        else
        {
            parent_app.setEnglishTranslationsList(translations);
        }
    }

    public void insertUpdate(DocumentEvent e)
    {
        dataChanged();
    }

    public void removeUpdate(DocumentEvent e)
    {
        dataChanged();
    }

    public void changedUpdate(DocumentEvent e)
    {
        dataChanged();
    }
}
