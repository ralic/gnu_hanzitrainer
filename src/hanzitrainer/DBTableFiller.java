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
public class DBTableFiller extends AbstractTableModel
{
    private enum table_mode_t { TMODE_ALL, TMODE_CHARACTER, TMODE_WORD }
    
    HanziDB db;
    String hanzi;
    ArrayList<ArrayList<String>> table_for_character;
    table_mode_t table_mode = table_mode_t.TMODE_ALL;

    public DBTableFiller(HanziDB database)
    {
        db = database;
        hanzi = new String("");
    }

    public void set_filter(String hanzi)
    {
        this.hanzi = hanzi;
        table_for_character = db.get_words_with_character(hanzi);
        if (table_for_character.size()==0)
            System.out.println("No DBTableFiller.set_character : no word for this char");
        table_mode = table_mode_t.TMODE_CHARACTER;
    }

    public String getColumnName(int column)
    {
        switch (column)
        {
        case 0:
            return "Chinese";
        case 1:
            return "Pinyin";
        case 2:
            return "Translation";
        case 3:
            return "Score";
        default:
            return "column " + column + "???";
        }
    }

    public int getRowCount()
    {
        int res;
        if (table_mode == table_mode_t.TMODE_ALL)
        {
            res = db.get_number_words();
        }
        else
        {
            res = table_for_character.size();
        }
        return res;
    }

    public int getColumnCount()
    {
        return 4;
    }

    public Object getValueAt(int row, int column)
    {
        int id;
        if (table_mode == table_mode_t.TMODE_ALL)
        {
            ArrayList<String> word_details;
            if (getRowCount() == 0)
            {
                return "";
            }
            id = db.get_word_id(row,0);
            word_details = db.get_word_details(id);
            switch (column)
            {
            case 0:
                return word_details.get(0);
            case 1:
                return PinyinParser.convert_to_printed_version(word_details.get(1));
            case 2:
                return word_details.get(2);
            case 3:
                return word_details.get(3);
            default:
                return "(" + row + "," + column + ") ?";
            }
        }
        else
        {
            String result = table_for_character.get(row).get(column);
            if (column==1)
                result = PinyinParser.convert_to_printed_version(result);
            return result;
        }
    }
}
