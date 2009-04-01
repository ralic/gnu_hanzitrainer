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

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 *
 * @author Matthieu
 */
public class CDBTableFiller extends AbstractTableModel
{
    HanziDB db;
 
    public CDBTableFiller(HanziDB database)
    {
        db = database;
    }

    @Override
    public String getColumnName(int column)
    {
        switch (column)
        {
        case 0:
            return "Character";
        case 1:
            return "Pinyin";
        case 2:
            return "Chinese words";
        case 3:
            return "Score";
        default:
            return "column " + column + "???";
        }
    }

    public int getRowCount()
    {
        int res;

        res = db.get_number_characters();

        return res;
    }

    public int getColumnCount()
    {
        return 4;
    }

    public Object getValueAt(int row, int column)
    {
        int id, i;
        String pinyin_list = "";
        String cword_list = "";

        ArrayList<String> pinyins;
        ArrayList<ArrayList<String>> cwords;
        if (getRowCount() == 0) {
            return "";
        }
        
        id = db.get_character_id(row);
        switch (column)
        {
            case 0:
                return db.get_character_details(id);
            case 1:
                pinyins = db.get_pinyin_for_character(db.get_character_details(id));
                pinyin_list = PinyinParser.convert_to_printed_version(pinyins.get(0));
                for (i = 1; i < pinyins.size(); i++)
                {
                    pinyin_list += ", " + PinyinParser.convert_to_printed_version(pinyins.get(i));
                }
                return pinyin_list;
            case 2:
                cwords = db.get_words_with_character(db.get_character_details(id));
                cword_list = cwords.get(0).get(0);
                for (i = 1; i < cwords.size(); i++)
                {
                    cword_list += ", " + cwords.get(i).get(0);
                }
                return cword_list;
            case 3:
                return db.get_character_score(id);
            default:
                return "(" + row + "," + column + ") ?";
        }

    }
}
