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
import java.util.List;
import java.lang.Integer;
import javax.swing.SwingWorker;

/**
 *
 * @author Matthieu
 */
public class CDBTableFiller extends AbstractTableModel
{
    HanziDBscore db;
    ArrayList<ArrayList<String>> cache_table;
    filler_worker filler=null;
 
    public CDBTableFiller(HanziDBscore database)
    {
        db = database;
        cache_table = new ArrayList<ArrayList<String>>();
        fill_character_table();
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
        default:
            return "column " + column + "???";
        }
    }

    public int getRowCount()
    {
        return cache_table.size();
    }

    public int getColumnCount()
    {
        return 3;
    }

    public void database_updated()
    {
        fill_character_table();
        this.fireTableDataChanged();
    }

    private class filler_worker extends SwingWorker<Integer, Integer>
    {
        private int last_index_published = 0;
        private AbstractTableModel table;

        public filler_worker(AbstractTableModel table)
        {
            this.table=table;
        }

        @Override
        protected Integer doInBackground()
        {
            int i, j, character_count;
            String character;
            ArrayList<String> temp;

            ArrayList<String> pinyins;
            String pinyin_list = "";

            ArrayList<Integer> cwords;
            ArrayList<String> cword_details;
            String cword_list = "";

            cache_table.clear();
            table.fireTableDataChanged();

            character_count = db.get_number_characters();
            for (i=0; i<character_count; i++)
            {
                character = db.get_character(i);
                temp = new ArrayList<String>();

                // the character comes first
                temp.add(character);

                // the pinyins next
                pinyins = db.get_pinyin_for_character(character);
                pinyin_list = PinyinParser.convert_to_printed_version(pinyins.get(0));
                for (j = 1; j < pinyins.size(); j++)
                {
                    pinyin_list += ", " + PinyinParser.convert_to_printed_version(pinyins.get(j));
                }
                temp.add(pinyin_list);

                // the chinese words
                cwords = db.get_words_with_character(character);
                cword_details = db.get_word_details(cwords.get(0));
                cword_list = cword_details.get(0);
                for (j = 1; j < cwords.size(); j++)
                {
                    cword_details = db.get_word_details(cwords.get(j));
                    cword_list += ", " + cword_details.get(0);
                }
                temp.add(cword_list);

                // the score
                temp.add("" +db.get_character_score(character));

                cache_table.add(temp);

                if ((i>0) && (i%100 == 0))
                {
                    publish(i);
                }

                if (isCancelled())
                    return i;
            }
            return character_count;
        }
        @Override
        public void done()
        {
            table.fireTableDataChanged();
        }

        @Override
        protected void process(List<Integer> chunks)
        {
            int maximum_index = 0, i;

            for (i=0; i<chunks.size(); i++)
            {
                if (chunks.get(i) > maximum_index)
                    maximum_index = chunks.get(i);
            }
            if (maximum_index > last_index_published)
            {
                table.fireTableRowsInserted(last_index_published, maximum_index);
                last_index_published = maximum_index;
            }
        }

    }

    private void fill_character_table()
    {
        if ((filler != null) && (!filler.isDone()))
        {
            filler.cancel(true);
        }
        filler = new filler_worker(this);
        filler.execute();
    }

    public Object getValueAt(int row, int column)
    {
        ArrayList<String> temp;

        temp = cache_table.get(row);

        return temp.get(column);
    }
}
