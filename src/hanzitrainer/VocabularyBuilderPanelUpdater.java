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

    public void dataChanged()
    {
        String chinese_text = parent_app.getChineseTextField();
        ArrayList<String> translations = main_database.get_chinese_word_translation(chinese_text);
        if (!cedict_database.check_for_empty_db())
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
