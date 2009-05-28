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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HanziDBscore extends HanziDB
{

    private String filename = "";
    private static final int minimum_score=0;
    private static final int maximum_score=100;

    public HanziDBscore()
    {
        System.out.println("HanziDBscore initialized");
    }

    /**
     *  Open a database file
     * 
     * @param db_file_name filename
     */
    public void HanziDB_open(String db_file_name)
    {
        shutdown();
        database_init();
        changed = false;

        try
        {
            Statement st;

            st = conn.createStatement();
            st.execute("RUNSCRIPT FROM '" + db_file_name + "'");        
            if (check_for_empty_db())
            {
                // try the old DB with password...
                st.execute("RUNSCRIPT FROM '" + db_file_name + "' CIPHER AES PASSWORD 'ILoveChinese'");
                if (check_for_empty_db())
                {
                    System.out.println("HanziDB_open : reading file " + db_file_name + " failed, creating a new empty one");
                    create_database();
                    
                    return;
                }
            }
            
            if (get_database_version() > database_ver)
            {
                System.out.println("HanziDB_open : reading file " + db_file_name + " failed, database version too high, creating a new empty one");
                shutdown();
                database_init();
                create_database();

                return;
            }
        }
        catch (SQLException e)
        {
            System.out.println("got exception " + e.getMessage());
            e.printStackTrace();
        }
        if (get_database_version() < database_ver)
        {
            System.out.println("HanziDB_open : reading file " + db_file_name + " upgrading");
            upgrade_database();
            filename = db_file_name;
            HanziDB_save();
        }
        System.out.println("HanziDB_open : I think I got it right from file " + db_file_name);
        filename = db_file_name;
    }
    
    /**
     *  Save a database file with the same name of the file opened or previously saved
     * 
     */
    public void HanziDB_save()
    {
        changed = false;
        if (filename.equals(""))
        {
            return;
        }
        try
        {
            Statement st = conn.createStatement();
            //st.execute("SCRIPT TO '" + filename + "' CIPHER AES PASSWORD 'ILoveChinese'");
            st.execute("SCRIPT TO '" + filename + "'");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     *  Close a database file
     * 
     */
    public void HanziDB_close()
    {
        shutdown();
        HanziDB_set_filename("");
        database_init();

        create_database();

    }
    
    /**
     *  Set the database file
     * 
     * @param new_filename new file name
     * @see HanziDB_get_filename
     */
    public void HanziDB_set_filename(String new_filename)
    {
        filename = new_filename;
    }
    /**
     *  Open a database file
     * 
     * @see HanziDB_set_filename
     * @return the file name currently used for the database
     */
    public String HanziDB_get_filename()
    {
        return filename;
    }
    
     protected synchronized void add_character(String character) throws SQLException
    {
        super.add_character(character);
        // TODO : initialize score ?
    }
    
    /**
     * 
     * Create a new database with all the tables and views it needs
     * 
     * @return nothing
     */
    protected void create_database()
    {
        super.create_database();

        try {
            Statement st = conn.createStatement();

            st.executeUpdate("ALTER TABLE CHARACTER ADD score INTEGER DEFAULT 0");
            st.executeUpdate("ALTER TABLE CWORD ADD COLUMN score INTEGER DEFAULT 0");

            st.executeUpdate("DROP VIEW english_pinyin_chinese");
            st.executeUpdate("CREATE VIEW english_pinyin_chinese AS" +
                    " (SELECT c_words.cword_id, c_words.hanzi, c_words.pinyin," +
                    " GROUP_CONCAT(DISTINCT e.translation SEPARATOR ', ') AS translations, c_words.score" +
                    " FROM (SELECT cpb.cword_id," +
                    " GROUP_CONCAT(ch.hanzi ORDER BY cpb.pos ASC SEPARATOR '') AS hanzi," +
                    " GROUP_CONCAT(CONCAT(cp.pinyin,cp.tone) ORDER BY cpb.pos ASC SEPARATOR '') AS pinyin," +
                    " cw.score" +
                    " FROM cword AS cw" +
                    " JOIN cword_pinyin_bridge AS cpb ON cpb.cword_id=cw.cword_id" +
                    " JOIN character_pinyin AS cp ON cp.character_pinyin_id=cpb.character_pinyin_id" +
                    " JOIN character AS ch ON ch.char_id=cp.char_id" +
                    " GROUP BY cpb.cword_id) AS c_words" +
                    " JOIN english AS e ON e.cword_id=c_words.cword_id " +
                    " GROUP BY e.cword_id )");
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
    
    /**
     * 
     * Get details about a particular chinese word
     * 
     * @param index from 0 to the number of words - 1 
     * @see get_number_words
     * @return ArrayList with : Chinese word, pinyin, translations and score
     */
    public ArrayList<String> get_word_details(int index)
    {
        ArrayList<String> res = new ArrayList<String>();
        if (!initialized)
        {
            return res;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT * FROM english_pinyin_chinese WHERE cword_id=" + index);
            if (!rs.next())
            {
                return res;
            }
            res.add(rs.getString(2));
            res.add(rs.getString(3));
            res.add(rs.getString(4));
            res.add(rs.getString(5));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }
    
    /**
     * 
     * Get the character from its id
     * 
     * @param id id of the character
     * @return a string that only contains that character
     */
    public String get_character_details(int id)
    {
        String res = "";
        if (!initialized)
        {
            return res;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT hanzi FROM character WHERE char_id=" + id);
            if (!rs.next())
            {
                return res;
            }
            res = rs.getString(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }
    
    public boolean get_database_changed()
    {
        return changed;
    }

    
    /**
     * 
     * Get the score for a character from its id
     * 
     * @param id id of the character
     * @return the score value for that character
     */
    public int get_character_score(int id)
    {
        int res=0;
        
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT score, hanzi FROM character AS ch" +
                    " WHERE ch.char_id="+id);
            rs.next();

            res = rs.getInt(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }
    
    /**
     * 
     * Get the average score for all characters
     * 
     * @return the score value
     */
    public int get_average_character_score()
    {
        int res=0;
        
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT AVG(score) FROM character");
            rs.next();

            res = rs.getInt(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }
    
    /**
     * 
     * Get the score for a chinese word from its id
     * 
     * @param id id of the word
     * @return the score value for that word
     */
    public int get_average_word_score()
    {
        int res=0;
        
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT AVG(score) FROM cword");
            rs.next();

            res = rs.getInt(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }

        /**
     *
     * Return the ID of the nth word defining also how to sort them
     *
     * @param index position in the word list
     * @param sorting_mode define how to sort the words (0:pinyin first, 1:score first)
     * @return id of a Chinese word
     */
    public int get_word_id(int index, int sorting_mode)
    {
        int res = -1;
        if (!initialized)
        {
            return res;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            switch(sorting_mode)
            {
                case 0:
                default:
                    rs = st.executeQuery("SELECT * FROM english_pinyin_chinese ORDER BY pinyin, hanzi");
                    break;
                case 1:
                    rs = st.executeQuery("SELECT * FROM english_pinyin_chinese ORDER BY score ASC, pinyin, hanzi");
                    break;
            }

            rs.relative(index + 1);
            res = rs.getInt(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }

    /**
     * 
     * Get the score for a chinese word from its id
     * 
     * @param id id of the word
     * @return the score value for that word
     */
    public int get_word_score(int id)
    {
        int res=0;
        
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT score FROM cword AS cw" +
                    " WHERE cw.cword_id="+id);
            rs.next();

            res = rs.getInt(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }
    
    
    /**
     * 
     * Change the score for a character
     * 
     * @param id id of the character
     * @param mode whether to increase (true) or decrease (false)
     * @param weight how much weight the current score would have
     */
    public void change_character_score(int id, boolean mode, int weight)
    {
        int score = get_character_score(id);
        if (mode)
        { // increase
            score = ((weight * score) + maximum_score) / (1 + weight);
        }
        else
        { // decrease
            score = ((weight * score) + minimum_score) / (1 + weight);
        }
        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("UPDATE character AS ch" +
                    " SET score=" + score +
                    " WHERE ch.char_id=" + id);
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        changed = true;
    }

    /**
     * 
     * Change the score for a word
     * 
     * @param id id of the word
     * @param mode whether to increase (true) or decrease (false)
     * @param weight how much weight the current score would have
     */
    public void change_word_score(int id, boolean mode, int weight) {
        int score = get_word_score(id);
        if (mode)
        { // increase
            score = ((weight * score) + maximum_score) / (1 + weight);
        }
        else
        { // decrease
            score = ((weight * score) + minimum_score) / (1 + weight);
        }

        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("UPDATE cword AS cw" +
                    " SET score=" + score +
                    " WHERE cw.cword_id=" + id);
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        changed = true;
    }
    

}    // class Testdb