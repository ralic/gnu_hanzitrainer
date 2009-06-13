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
public class ToneTestTableFiller extends AbstractTableModel
{
    HanziDBscore db;
    String hanzi;
    ArrayList<ArrayList<String>> table_for_pinyin;

    public ToneTestTableFiller(HanziDBscore database)
    {
        db = database;
        hanzi = new String("");
        table_for_pinyin = new ArrayList<ArrayList<String>>();
    }

    public void set_filter(String hanzi, String pinyin)
    {
        ArrayList<Integer> words;
        int i;
        this.hanzi = hanzi;
        table_for_pinyin.clear();
        if ((hanzi.equals("")) || (pinyin.equals("")))
            return;

        words = db.get_words_with_character(hanzi, pinyin);
        for (i = 0; i < words.size(); i++) {
            ArrayList<String> less_details = db.get_word_details(words.get(i));
            less_details.remove(3);
            less_details.remove(1);
            table_for_pinyin.add(less_details);
        }
        if (table_for_pinyin.size()==0)
            System.out.println("No DBTableFiller.set_character : no word for this char");
    }

    @Override
    public String getColumnName(int column)
    {
        switch (column)
        {
        case 0:
            return "Chinese";
        case 1:
            return "Translation";
        default:
            return "column " + column + "???";
        }
    }

    public int getRowCount()
    {
        int res = 0;
        
        res = table_for_pinyin.size();
        
        return res;
    }

    public int getColumnCount()
    {
        return 2;
    }

    public Object getValueAt(int row, int column)
    {
        int id;
        
        String result = table_for_pinyin.get(row).get(column);

        return result;
    }
}
