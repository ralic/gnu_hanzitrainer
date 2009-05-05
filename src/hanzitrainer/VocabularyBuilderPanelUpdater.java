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
public class VocabularyBuilderPanelUpdater implements javax.swing.event.DocumentListener {

    private VocabularyBuilderPanel parent_app;
    private HanziDB main_database;
    
    public VocabularyBuilderPanelUpdater(VocabularyBuilderPanel parent, HanziDB database)
    {
        parent_app = parent;
        main_database = database;
    }
    private void dataChanged()
    {
        String chinese_text = parent_app.getChineseTextField();
        ArrayList<String> translations = main_database.get_chinese_word_translation(chinese_text);
        parent_app.setEnglishTranslationsList(translations);
    }
    public void insertUpdate(DocumentEvent e) {
        dataChanged();
    }

    public void removeUpdate(DocumentEvent e) {
        dataChanged();
    }

    public void changedUpdate(DocumentEvent e) {
        dataChanged();
    }

}
