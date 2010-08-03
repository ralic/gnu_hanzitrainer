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
public class DBTableFiller extends AbstractTableModel
{
    private enum table_mode_t 
    { 
        TMODE_ALL, 
        TMODE_CHARACTER 
    }
    
    HanziDBscore db;
    String hanzi;
    ArrayList<ArrayList<String>> cache_table;
    table_mode_t table_mode = table_mode_t.TMODE_ALL;
    filler_worker filler=null;

    public DBTableFiller(HanziDBscore database)
    {
        db = database;
        hanzi = new String("");
        cache_table = new ArrayList<ArrayList<String>>();
        fill_word_table();
    }

    public DBTableFiller(HanziDBscore database, String hanzi)
    {
        db = database;
        hanzi = new String("");
        cache_table = new ArrayList<ArrayList<String>>();
        set_filter(hanzi);
    }

    public void database_updated()
    {
        fill_word_table();
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
            int word_count, i;
            ArrayList<Integer> words;
            ArrayList<String> temp;

            cache_table.clear();
            table.fireTableDataChanged();

            if (table_mode == table_mode_t.TMODE_ALL)
            {
                word_count = db.get_number_words();
                for (i=0; i<word_count; i++)
                {
                    temp = db.get_word_details(db.get_word_id(i));
                    temp.set(1, PinyinParser.convert_to_printed_version(temp.get(1)));
                    cache_table.add(temp);

                    if ((i>0) && (i%100 == 0))
                    {
                        publish(i);
                    }

                    if (isCancelled())
                        return i;
                }
                return word_count;
            }
            else
            {
                if (hanzi.equals(""))
                    return 0;

                words = db.get_words_with_character(hanzi);
                for (i = 0; i < words.size(); i++) {
                    temp = db.get_word_details(words.get(i));
                    temp.set(1, PinyinParser.convert_to_printed_version(temp.get(1)));
                    cache_table.add(temp);

                    if ((i>0) && (i%100 == 0))
                    {
                        publish(i);
                    }
                    if (isCancelled())
                        return i;
                }

                return words.size();
            }
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

    private void fill_word_table()
    {
        if ((filler != null) && (!filler.isDone()))
        {
            filler.cancel(true);
        }
        filler = new filler_worker(this);
        filler.execute();
    }

    public void set_filter(String hanzi)
    {
        int i;
        this.hanzi = hanzi;
        table_mode = table_mode_t.TMODE_CHARACTER;

        database_updated();
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
        return cache_table.size();
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
            String result = cache_table.get(row).get(column);
            if (column==1)
                result = PinyinParser.convert_to_printed_version(result);
            return result;
        }
    }
}
