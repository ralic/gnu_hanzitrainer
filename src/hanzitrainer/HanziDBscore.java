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

    private static final int minimum_score = 0;
    private static final int maximum_score = 100;
    private static final int reset_score = 20;

    public HanziDBscore()
    {
        super();
        System.out.println("HanziDBscore initialized");
    }

    /**
     *  Open a database file
     * 
     * @param db_file_name filename
     */
    @Override
    public void HanziDB_open(String db_file_name)
    {
        super.HanziDB_open_no_upgrade(db_file_name);

        if (upgrade_database() == true)
        {
            filename = db_file_name;
            HanziDB_save();
        }
    }

    protected int get_score_database_version()
    {
        int res = 0;
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
                res = Integer.parseInt(rs.getString(1));
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        System.out.println("Score database version " +  res);
        return res;
    }

    @Override
    protected boolean upgrade_database()
    {
        boolean res = false;

        res |= upgrade_score_database();

        res |= super.upgrade_database();

        finish_score_database_upgrade();

        return res;
    }

    protected boolean upgrade_score_database()
    {
        boolean upgrade_change = false;

        System.out.println("Upgrade score database");

        if (get_score_database_version() <= 0)
        {
            System.out.println("Upgrade score database to version 1");
            try
            {
                Statement st = conn.createStatement();
                // insert a version in the database_info
                st.executeUpdate("INSERT INTO database_info(field,value) VALUES('score_db_version', '1')");
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
            // that means separating the scores from the original tables and create a cword_score and a character_score tables
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
                        " SET value='2' WHERE field='score_db_version'");

                st.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            upgrade_change = true;
        }
        if (get_score_database_version() <= 2)
        {
            System.out.println("Upgrade score database to version 3");
            // getting rid of the character table
            try
            {
                Statement st = conn.createStatement();
                Statement st2 = conn.createStatement();
                Statement st3 = conn.createStatement();
                Statement st4 = conn.createStatement();
                ResultSet rs = null;
                ResultSet rs2 = null;
                ResultSet rs3 = null;
               
                // some duplicates in the score database... need to fix that first
                rs = st2.executeQuery("SELECT char_id FROM character_score GROUP BY char_id HAVING COUNT(char_id)>1");
                for (;rs.next();)
                {
                    int id = rs.getInt(1);
                    st.executeUpdate("DELETE FROM character_score WHERE char_id="+id);
                    st.executeUpdate("INSERT INTO character_score (char_id, score) VALUES("+id+","+reset_score+")");
                }
                st2.close();

                // Move character scores to a separate table
                st.executeUpdate("ALTER TABLE character_score ADD hanzi VARCHAR(2)");

                rs2 = st3.executeQuery("SELECT char_id, hanzi FROM character");
                for (;rs2.next();)
                {
                    int id = rs2.getInt(1);
                    String character = rs2.getString(2);
                    st.executeUpdate("UPDATE character_score SET hanzi='"+character+"' WHERE char_id="+id);
                }
                st3.close();

                // Remove constraints on the char_id column
                rs3 = st4.executeQuery("SELECT constraint_name FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE table_name='CHARACTER_SCORE'");
                for (;rs3.next();)
                {
                    String constraint_name = rs3.getString(1);
                    st.executeUpdate("ALTER TABLE CHARACTER_SCORE DROP CONSTRAINT " + constraint_name);
                }
                st4.close();

                st.executeUpdate("ALTER TABLE character_score DROP COLUMN char_id");
                st.executeUpdate("ALTER TABLE character_score ALTER COLUMN hanzi VARCHAR(2) NOT NULL");
                st.executeUpdate("CREATE PRIMARY KEY ON character_score(hanzi)");

                // update the version
                st.executeUpdate("UPDATE database_info" +
                        " SET value='3' WHERE field='score_db_version'");

                st.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

            upgrade_change = true;
        }

        return upgrade_change;
    }

    protected void finish_score_database_upgrade()
    {
        // always refresh the view if it was changed in the master class...
        try
        {
            Statement st = conn.createStatement();

            st.executeUpdate("DROP VIEW IF EXISTS english_pinyin_chinese_score");
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

    @Override
    protected synchronized int add_pinyin(String character, String pinyin) throws SQLException
    {
        int res;
        res = super.add_pinyin(character, pinyin);
        String try_character;

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT hanzi FROM character_score WHERE hanzi NOT IN (SELECT hanzi FROM character_pinyin GROUP BY hanzi)");
            for (;rs.next();)
            {
                try_character = rs.getString(1);

                st.executeUpdate("INSERT INTO character_score(hanzi, score) VALUES(" + try_character + "," + reset_score + ")");
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }

    @Override
    protected synchronized void delete_char_pinyin(int char_pinyin_id)
    {
        super.delete_char_pinyin(char_pinyin_id);

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;
            String character;

            st.executeQuery("SELECT hanzi FROM character_score WHERE hanzi NOT IN" +
                    " (SELECT hanzi FROM character_pinyin GROUP BY hanzi)");
            for (;rs.next();)
            {
                character = rs.getString(1);
                st.executeUpdate("DELETE FROM character_score WHERE hanzi=" + character);
            }

            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public synchronized int add_translation(String english, ArrayList<String> pinyins, ArrayList<String> hanzi)
    {
        int id = super.add_translation(english, pinyins, hanzi);
        String full_chinese_word = "";
        for (int i = 0; i < hanzi.size(); i++)
        {
            full_chinese_word = full_chinese_word + hanzi.get(i);
        }

        int cword_id = get_word_id(full_chinese_word);
        System.out.println("add_translation found cword_id " + cword_id + " for word " + full_chinese_word);
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT cword_id FROM cword_score WHERE cword_id=" + cword_id);
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
        return id;
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
                    " hanzi VARCHAR(2) NOT NULL," +
                    " score INT)");


            st.executeUpdate("INSERT INTO database_info(field,value) VALUES('score_db_version', '3')");

            // create a new view
            st.executeUpdate("CREATE VIEW english_pinyin_chinese_score AS" +
                    " (SELECT cword_score.cword_id, hanzi, pinyin, translations, score" +
                    " FROM english_pinyin_chinese" +
                    " JOIN cword_score ON cword_score.cword_id = english_pinyin_chinese.cword_id)");

            st.executeUpdate("INSERT INTO database_info(field,value) VALUES('score_db_version', '2')");
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
     * Get the score for a character from its id
     * 
     * @param id id of the character
     * @return the score value for that character
     */
    public int get_character_score(String character)
    {
        int res = 0;

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT score FROM character_score AS ch" +
                    " WHERE ch.hanzi='" + character + "'");
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
     * Get the id of an indexed character
     *
     * @param index from 0 to the number of words - 1
     * @return id of the character
     */
    public String get_character_low_score(int index)
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

            rs = st.executeQuery("SELECT hanzi FROM character_score ORDER BY score,hanzi");
            rs.relative(index + 1);
            res = rs.getString(1);
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
    public void change_character_score(String character, boolean mode, int weight)
    {
        int score = get_character_score(character);
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
                    " WHERE ch.hanzi='" + character + "'");
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
