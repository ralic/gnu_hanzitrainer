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

    public DBTableFiller(HanziDB database)
    {
        db = database;
    }
    
    public String getColumnName(int column)
    {
        switch (column){
        case 0:
            return "Chinese";
        case 1:
            return "Pinyin";
        case 2:
            return "Translation";
        default:
            return "column "+column+"???";
        }
    }

    public int getRowCount()
    {
        int res = db.get_number_words();
        //System.out.println("got "+res+" words...");
        return res;
    }

    public int getColumnCount()
    {
        return 3;
    }

    public Object getValueAt(int row, int column)
    {
        ArrayList<String> word_details;
        if (getRowCount()==0)
            return "";
        word_details = db.get_word_details(row);
        switch(column){
        case 0 :
            return word_details.get(0);
        case 1 :
            return word_details.get(1);
        case 2 :
            return word_details.get(2);
        default :
            return "("+row+","+column+") ?";
        }
    }
}
