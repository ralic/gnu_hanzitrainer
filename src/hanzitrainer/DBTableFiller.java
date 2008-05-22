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

    public int getRowCount()
    {
        return db.get_number_words();
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
