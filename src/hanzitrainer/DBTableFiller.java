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

    HanziDB db;
    String hanzi;
    ArrayList<ArrayList<String>> table_for_character;

    public DBTableFiller(HanziDB database)
    {
        db = database;
        hanzi = new String("");
    }

    public void set_character(String hanzi)
    {
        this.hanzi = hanzi;
        table_for_character = db.get_words_with_character(hanzi);
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
        if (hanzi.equals(""))
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
        if (hanzi.equals(""))
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
                return word_details.get(1);
            case 2:
                return word_details.get(2);
            default:
                return "(" + row + "," + column + ") ?";
            }
        }
        else
        {
            return table_for_character.get(row).get(column);
        }
    }
}
