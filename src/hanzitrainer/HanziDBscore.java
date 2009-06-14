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
    private static final int minimum_score = 0;
    private static final int maximum_score = 100;
    private static final int reset_score = 20;

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
                    st.close();

                    return;
                }
            }

            if (get_database_version() > database_ver)
            {
                System.out.println("HanziDB_open : reading file " + db_file_name + " failed, database version too high, creating a new empty one");
                shutdown();
                database_init();
                create_database();
                st.close();

                return;
            }
            st.close();
        }
        catch (SQLException e)
        {
            System.out.println("got exception " + e.getMessage());
            e.printStackTrace();
        }

        if (upgrade_database() == true)
        {
            filename = db_file_name;
            HanziDB_save();
        }

        if (upgrade_score_database() == true)
        {
            filename = db_file_name;
            HanziDB_save();
        }

        System.out.println("HanziDB_open : I think I got it right from file " + db_file_name);
        filename = db_file_name;
    }

    protected int get_score_database_version()
    {
        int res=0;
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT value FROM database_info WHERE field='score_db_version'");
            if (!rs.next())
            {
                res = 0;
            }
            else
            {
                res = rs.getInt(1);
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }

    protected boolean upgrade_score_database()
    {

        if (get_score_database_version() <= 0)
        {
            System.out.println("Upgrade score database to version 1");
            try
            {
                Statement st = conn.createStatement();
                // insert a version in the database_info
                st.executeUpdate("INSERT INTO database_info(field,value) VALUES('score_db_version', 1)");
                st.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }
        }
        if (get_score_database_version() <= 1)
        {
            System.out.println("Upgrade score database to version 2");
            try
            {
                Statement st = conn.createStatement();
                // Move word scores to a separate table
                st.executeUpdate("CREATE TABLE cword_score (" +
                        " cword_id INT NOT NULL," +
                        " score INT," +
                        " FOREIGN KEY (cword_id) REFERENCES cword(cword_id))" +
                        " AS (SELECT cword_id, score FROM cword);");
                st.executeUpdate("ALTER TABLE cword DROP COLUMN score;");

                // Move character scores to a separate table
                st.executeUpdate("CREATE TABLE character_score (" +
                        " char_id INT NOT NULL," +
                        " score INT," +
                        " FOREIGN KEY (char_id) REFERENCES character(char_id))" +
                        " AS (SELECT char_id, score FROM character);");
                st.executeUpdate("ALTER TABLE character DROP COLUMN score;");

                // create a new view
                st.executeUpdate("CREATE VIEW english_pinyin_chinese_score AS" +
                        " (SELECT cword_score.cword_id, hanzi, pinyin, translations, score" +
                        " FROM english_pinyin_chinese" +
                        " JOIN cword_score ON cword_score.cword_id = english_pinyin_chinese.cword_id)");

                // update the version
                st.executeUpdate("UPDATE database_info" +
                        " SET value=2 WHERE field='score_db_version'");

                st.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

        }
        return false;
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
            st.close();
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

    @Override
    protected synchronized void add_character(String character) throws SQLException
    {
        super.add_character(character);
        int char_id;

        try
        {
            Statement st = conn.createStatement();
            
            char_id = get_character_id(character);

            st.executeUpdate("INSERT INTO character_score(char_id, score) VALUES(" + char_id + "," + reset_score +")");
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected synchronized void delete_character(int char_id)
    {
        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("DELETE FROM character_score WHERE char_id=" + char_id);

            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        super.delete_character(char_id);
    }

    @Override public synchronized void add_translation(String english, ArrayList<String> pinyins, ArrayList<String> hanzi)
    {
        super.add_translation(english,pinyins,hanzi);
        String full_chinese_word="";
        for (int i=0; i<hanzi.size(); i++)
        {
            full_chinese_word = full_chinese_word + hanzi.get(i);
        }

        int cword_id = get_word_id(full_chinese_word);
        System.out.println("add_translation found cword_if " + cword_id + " for word " + full_chinese_word);
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT cword_id FROM cword_score WHERE cword_id="+cword_id);
            if (!rs.next())
            {
                st.executeUpdate("INSERT INTO cword_score(cword_id, score) VALUES(" + cword_id + "," + reset_score + ")");
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected synchronized void delete_cword(int cword_id)
    {
        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("DELETE FROM cword_score WHERE cword_id=" + cword_id);

            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        super.delete_cword(cword_id);
    }

    /**
     * 
     * Create a new database with all the tables and views it needs
     * 
     */
    @Override
    protected void create_database()
    {
        super.create_database();

        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("CREATE TABLE cword_score (" +
                    " cword_id INT NOT NULL," +
                    " score INT," +
                    " FOREIGN KEY (cword_id) REFERENCES cword(cword_id));");

            // Move character scores to a separate table
            st.executeUpdate("CREATE TABLE character_score (" +
                    " char_id INT NOT NULL," +
                    " score INT," +
                    " FOREIGN KEY (char_id) REFERENCES character(char_id));");

                // create a new view
                st.executeUpdate("CREATE VIEW english_pinyin_chinese_score AS" +
                        " (SELECT cword_score.cword_id, hanzi, pinyin, translations, score" +
                        " FROM english_pinyin_chinese" +
                        " JOIN cword_score ON cword_score.cword_id = english_pinyin_chinese.cword_id)");
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

    /**
     * 
     * Get details about a particular chinese word
     * 
     * @param index from 0 to the number of words - 1 
     * @return ArrayList with : Chinese word, pinyin, translations and score
     */
    @Override
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

            rs = st.executeQuery("SELECT * FROM english_pinyin_chinese_score WHERE cword_id=" + index);
            if (!rs.next())
            {
                return res;
            }
            res.add(rs.getString(2));
            res.add(rs.getString(3));
            res.add(rs.getString(4));
            res.add(rs.getString(5));
            st.close();
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
    @Override
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
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
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
        int res = 0;

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT score FROM character_score AS ch" +
                    " WHERE ch.char_id=" + id);
            if (rs.next())
            {
                res = rs.getInt(1);
            }
            st.close();
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
        int res = 0;

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT AVG(score) FROM character_score");
            rs.next();

            res = rs.getInt(1);
            st.close();
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
     * @return the average score for all words
     */
    public int get_average_word_score()
    {
        int res = 0;

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT AVG(score) FROM cword_score");
            rs.next();

            res = rs.getInt(1);
            st.close();
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

            switch (sorting_mode)
            {
                case 0:
                default:
                    rs = st.executeQuery("SELECT * FROM english_pinyin_chinese_score ORDER BY pinyin, hanzi");
                    break;
                case 1:
                    rs = st.executeQuery("SELECT * FROM english_pinyin_chinese_score ORDER BY score ASC, pinyin, hanzi");
                    break;
            }

            rs.relative(index + 1);
            res = rs.getInt(1);
            st.close();
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
        int res = 0;

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT score FROM cword_score AS cw" +
                    " WHERE cw.cword_id=" + id);
            rs.next();

            res = rs.getInt(1);
            st.close();
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

            st.executeUpdate("UPDATE character_score AS ch" +
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
    public void change_word_score(int id, boolean mode, int weight)
    {
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

            st.executeUpdate("UPDATE cword_score AS cw" +
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

    /**
     *
     * Reset the scores for all words
     */
    public void reset_word_scores()
    {
        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("UPDATE cword_score AS cw SET score=" + reset_score);
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
     * Reset the scores for all characters
     */
    public void reset_character_scores()
    {
        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("UPDATE character_score AS cw SET score=" + reset_score);
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        changed = true;
    }
}    // class Testdb