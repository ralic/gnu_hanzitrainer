/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hanzitrainer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;


/**
 *
 * @author Administrator
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

    public void set_character(String hanzi)
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
        return 3;
    }

    public Object getValueAt(int row, int column)
    {
        if (table_mode == table_mode_t.TMODE_ALL)
        {
            ArrayList<String> word_details;
            if (getRowCount() == 0)
            {
                return "";
            }
            word_details = db.get_word_details(row);
            switch (column)
            {
            case 0:
                return word_details.get(0);
            case 1:
                return PinyinParser.convert_to_printed_version(word_details.get(1));
            case 2:
                return word_details.get(2);
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
