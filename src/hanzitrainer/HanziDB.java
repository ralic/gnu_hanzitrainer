/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * HanziTrainer to help you learn Mandarin
 * Copyright (C) 2008  Matthieu Jeanson
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hanzitrainer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class HanziDB
{

    private Connection conn;
    private boolean initialized = false;
    private String filename = "";
    private boolean changed = false;

    public HanziDB()
    {
        database_init();
        try
        {
            create_database();
            changed = false;
        }
        catch (Exception e)
        {
        }
        System.out.println("HanziDB : Created a new empty database");
    }

    // we dont want this garbage collected until we are done
    public void HanziDB_open(String db_file_name)
    {
        try
        {
            Statement st;

            shutdown();
            database_init();

            st = conn.createStatement();
            st.execute("RUNSCRIPT FROM '" + db_file_name + "' CIPHER AES PASSWORD 'ILoveChinese'");
            if (check_for_empty_db())
            {
                System.out.println("HanziDB_open : reading file " + db_file_name + " failed, creating a new empty one");
                create_database();
            }
            else
            {
                System.out.println("HanziDB_open : I think I got it right from file " + db_file_name);
                filename = db_file_name;
            }
            changed = false;
        }
        catch (Exception e)
        {
        }
    }

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
            st.execute("SCRIPT TO '" + filename + "' CIPHER AES PASSWORD 'ILoveChinese'");
        }
        catch (Exception e)
        {
        }
    }

    public void HanziDB_close()
    {
        shutdown();
        HanziDB_set_filename("");
        database_init();
        try
        {
            create_database();
        }
        catch (Exception e)
        {
        }
    }

    public void HanziDB_set_filename(String new_filename)
    {
        filename = new_filename;
    }

    public String HanziDB_get_filename()
    {
        return filename;
    }

    private Boolean check_for_empty_db() throws SQLException
    {
        Statement st = conn.createStatement();
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet rs = dbm.getTables(null, null, null, new String[]
                {
                    "TABLE"
                });
        if (rs.next())
        {
            st.close();
            return false;
        }
        else
        {
            st.close();
            return true;
        }
    }

    private int find_chinese_word(String chinese)
    {
        int res = -1;
        try
        {
            int len = chinese.codePointCount(0, chinese.length());

            Statement st = conn.createStatement();
            ResultSet rs = null;

            if (len == 0)
            {
                st.close();
                return res;
            }

            rs = st.executeQuery("SELECT cword_id FROM english_pinyin_chinese WHERE hanzi='" + chinese + "'");
            if (rs.next())
            {
                res = rs.getInt(1);
            }
            else
            {
                res = -1;
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return res;
    }

    public ArrayList<String> get_chinese_word_translation(String chinese)
    {
        ArrayList<String> res = new ArrayList<String>();
        int len = chinese.codePointCount(0, chinese.length());

        if (len == 0)
        {
            return res;
        }

        if (!initialized)
        {
            return res;
        }

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT e.translation FROM english AS e " +
                    "JOIN english_pinyin_chinese AS epc ON e.cword_id=epc.cword_id " +
                    "WHERE epc.hanzi='" + chinese + "'");
            for (; rs.next();)
            {
                res.add(rs.getString(1));
            }

            st.close();
            return res;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }

    public ArrayList<String> get_pinyin_from_character(String character)
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

            rs = st.executeQuery("SELECT CONCAT(cp.pinyin,cp.tone) FROM character AS c" +
                    " JOIN character_pinyin AS cp ON c.char_id=cp.char_id" +
                    " WHERE c.hanzi='" + character + "'");

            for (; rs.next();)
            {
                res.add(rs.getString(1));
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return res;
    }

    private int find_existing_pinyin_character(String character, String pinyin) throws SQLException
    {
        Statement st = conn.createStatement();
        ResultSet rs = null;
        int char_id;
        int tone;
        String radical = "";
        int res = -1;

        // check that we are really dealing wSith a chinese character
        // TODO : this should probably be done outside of this function
        if (!is_chinese_char(character))
        {
            System.out.println("find_existing_pinyin_character : trying to add pinyin for a non chinese character");
            st.close();
            return res;
        }

        // find the character
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (!rs.next())
        {
            System.out.println("find_existing_pinyin_character : Could not find the character");
            st.close();
            return res;
        }
        char_id = rs.getInt(1);

        // tone that we want to store
        tone = pinyin.charAt(pinyin.length() - 1);
        if ((tone < '1') || (tone > '4'))
        {
            tone = 0;
            rs = st.executeQuery("SELECT cp.character_pinyin_id FROM character_pinyin AS cp" +
                    " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + pinyin + "' AND cp.tone IS NULL");
            if (rs.next())
            {
                res = rs.getInt(1);
                System.out.println("find_existing_pinyin_character : combination of pinyin/character exists (with no tone) " + res);
                st.close();
            }
            else
            {
                System.out.println("find_existing_pinyin_character : cannot find combination (no tone)");
            }
            return res;
        }
        else
        {
            tone = (int) tone - '0';
            radical = pinyin.substring(0, pinyin.length() - 1);
            rs = st.executeQuery("SELECT cp.character_pinyin_id FROM character_pinyin AS cp" +
                    " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + radical + "' AND cp.tone=" + tone);
            if (rs.next())
            {
                res = rs.getInt(1);
                System.out.println("find_existing_pinyin_character : combination of pinyin/character exists (same tone) " + res);
                st.close();
                return res;
            }

            System.out.println("find_existing_pinyin_character : Cannot find " + char_id + " " + radical + " " + tone + "!!");

        }

        st.close();
        return res;
    }

    private Boolean is_chinese_char(String input)
    {
        int entry;
        if (input.codePointCount(0, input.length()) != 1)
        {
            return false;
        }
        else
        {
            entry = input.codePointAt(0);
            if ((entry >= 0x4E00) && (entry <= 0x9fff))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    private synchronized void add_character(String character) throws SQLException
    {
        Statement st = conn.createStatement();
        ResultSet rs = null;

        if (!is_chinese_char(character))
        {
            System.out.println("add_character : trying to add a non chinese character");
            st.close();
            return;
        }
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (rs.next())
        {
            System.out.println("add_character : character already exists");
            st.close();
            return;
        }
        st.executeUpdate("INSERT INTO character(hanzi) VALUES('" + character + "')");

        st.close();
        changed = true;
    }

    private synchronized void add_pinyin(String character, String pinyin) throws SQLException
    {
        Statement st = conn.createStatement();
        ResultSet rs = null;
        int char_id, res;
        int tone;
        String radical = "";

        // check that we are really dealing wSith a chinese character
        // TODO : this should probably be done outside of this function
        if (!is_chinese_char(character))
        {
            System.out.println("add_pinyin : trying to add pinyin for a non chinese character");
            st.close();
            return;
        }

        // look if this character/pinyin is already there
        res = find_existing_pinyin_character(character, pinyin);
        if (res != -1)
        {
            System.out.println("add_pinyin : this combination of character/pinyin already exists");
            st.close();
            return;
        }

        // find the character
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (!rs.next())
        {
            System.out.println("add_pinyin : Could not find the character");
            st.close();
            return;
        }
        char_id = rs.getInt(1);

        // tone that we want to store
        tone = pinyin.charAt(pinyin.length() - 1);
        if ((tone < '1') || (tone > '4'))
        {
            System.out.println("add_pinyin : Adding char " + char_id + ", pinyin " + pinyin + ", tone 0");
            st.executeUpdate("INSERT INTO character_pinyin(char_id, pinyin,tone) VALUES(" + char_id + ",'" + pinyin + "',NULL)");
        }
        else
        {
            tone = (int) tone - '0';
            radical = pinyin.substring(0, pinyin.length() - 1);
            System.out.println("add_pinyin : Adding char " + char_id + ", pinyin " + pinyin + ", tone " + tone);
            st.executeUpdate("INSERT INTO character_pinyin(char_id, pinyin,tone) VALUES(" + char_id + ",'" + radical + "'," + tone + ")");

        }

        st.close();
        changed = true;
    }

    /*
     * adds a new chinese word with its translation
     * 
     * 
     */
    public synchronized void add_translation(String english, ArrayList<String> pinyins, ArrayList<String> hanzi)
    {
        if (!initialized)
        {
            return;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null, rs2 = null;
            int char_pinyin_id = 0;
            int i;
            String chinese = "";
            int found_chinese_id;
            int tone;

            if (pinyins.size() != hanzi.size())
            {
                System.out.println("add_translation : size of pinyins and hanzis need to be the same");
                st.close();
                return;
            }

            for (i = 0; i < hanzi.size(); i++)
            {
                chinese += hanzi.get(i);
            }
            found_chinese_id = find_chinese_word(chinese);
            if (found_chinese_id != -1)
            {
                System.out.println("add_translation : this chinese word already exists");
            }
            else
            {
                // TODO check the validity of the pinyins

                st.executeUpdate("INSERT INTO cword() VALUES()");
                rs = st.executeQuery("SELECT * FROM (" +
                        "SELECT cword.cword_id, SUM(pos) AS res FROM cword" +
                        " LEFT OUTER JOIN cword_pinyin_bridge AS cpb ON cword.cword_id=cpb.cword_id" +
                        " GROUP BY cword.cword_id)" +
                        " WHERE res IS NULL");
                if (!rs.next())
                {
                    System.out.println("add_translation : seems like I was not able to insert a new cword ??");
                    st.close();
                    return;
                }
                found_chinese_id = rs.getInt(1);



                for (i = 0; i < pinyins.size(); i++)
                {
                    add_character(hanzi.get(i));
                    add_pinyin(hanzi.get(i), pinyins.get(i));
                    char_pinyin_id = find_existing_pinyin_character(hanzi.get(i), pinyins.get(i));
                    tone = pinyins.get(i).charAt(pinyins.get(i).length() - 1);

                    System.out.println("add_translation : adding chinese:" + found_chinese_id + ", char_pinyin:" + char_pinyin_id + " at " + i);
                    if ((tone < '1') || (tone > '4'))
                    {
                        st.executeUpdate("INSERT INTO cword_pinyin_bridge(cword_id, character_pinyin_id, pos, notone) VALUES(" + found_chinese_id + "," + char_pinyin_id + "," + i + ",true)");
                    }
                    else
                    {
                        st.executeUpdate("INSERT INTO cword_pinyin_bridge(cword_id, character_pinyin_id, pos, notone) VALUES(" + found_chinese_id + "," + char_pinyin_id + "," + i + ",false)");
                    }
                }
            }            // TODO handle if there is "'" in the english string
            {
                StringTokenizer english_tokens = new StringTokenizer(english, ",");

                while (english_tokens.hasMoreTokens())
                {
                    String current_token = english_tokens.nextToken().trim();
                    
                    current_token = current_token.replace("'", "''");

                    rs = st.executeQuery("SELECT eng_id FROM english " +
                            " WHERE cword_id =" + found_chinese_id +
                            " AND translation='" + current_token + "'");
                    if (rs.next())
                    {
                        continue;
                    }
                    st.executeUpdate("INSERT INTO english(cword_id, translation) VALUES(" + found_chinese_id + ",'" + current_token + "')");
                }
            }

            st.close();
            changed = true;

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public synchronized void delete_translation(String english, ArrayList<String> hanzi)
    {
        if (!initialized)
        {
            return;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null, rs2 = null;
            int char_pinyin_id = 0, char_id = 0;
            int i;
            String chinese = "";
            int found_chinese_id;

            System.out.println("delete_translation: removing [" + english + "]");
            for (i = 0; i < hanzi.size(); i++)
            {
                chinese += hanzi.get(i);
            }
            found_chinese_id = find_chinese_word(chinese);
            st.executeUpdate("DELETE FROM english" +
                    " WHERE cword_id=" + found_chinese_id +
                    " AND translation='" + english + "'");

            // now look if this chinese word is still in use
            rs = st.executeQuery("SELECT eng_id FROM english" +
                    " WHERE cword_id=" + found_chinese_id);
            if (rs.next())
            {
                System.out.println("delete_translation: the word has other translations");
                st.close();
                st.close();
                return;
            }
            rs = st.executeQuery("SELECT character_pinyin_id FROM cword_pinyin_bridge" +
                    " WHERE cword_id=" + found_chinese_id);
            while (rs.next())
            {
                char_pinyin_id = rs.getInt(1);

                // remove that pair from this word
                st.executeUpdate("DELETE FROM cword_pinyin_bridge" +
                        " WHERE cword_id=" + found_chinese_id +
                        " AND character_pinyin_id=" + char_pinyin_id);

                // check if that pair of pinyin/character is used somewhere else
                rs2 = st.executeQuery("SELECT cword_id FROM cword_pinyin_bridge" +
                        " WHERE cword_id<>" + found_chinese_id +
                        " AND character_pinyin_id=" + char_pinyin_id);

                if (!rs2.next())
                {
                    // check if that caracter is used
                    rs2 = st.executeQuery("SELECT char_id FROM character_pinyin" +
                            " WHERE character_pinyin_id=" + char_pinyin_id);
                    rs2.next();
                    char_id = rs2.getInt(1);

                    System.out.println("delete_translation : remove that character_pinyin");
                    st.executeUpdate("DELETE FROM character_pinyin" +
                            " WHERE character_pinyin_id=" + char_pinyin_id);

                    rs2 = st.executeQuery("SELECT character_pinyin_id FROM character_pinyin" +
                            " WHERE char_id=" + char_id);
                    if (!rs2.next())
                    {
                        System.out.println("delete_translation : removing that character");
                        st.executeUpdate("DELETE FROM character " +
                                " WHERE char_id=" + char_id);
                    }

                }
                else
                {
                    System.out.println("delete_translation : That character pinyin is still in use");
                }
                rs = st.executeQuery("SELECT character_pinyin_id FROM cword_pinyin_bridge" +
                        " WHERE cword_id=" + found_chinese_id);
            }

            System.out.println("delete_translation : removing the chinese word from DB");
            // finally delete the word
            st.executeUpdate("DELETE FROM cword" +
                    " WHERE cword_id=" + found_chinese_id);

            st.close();
            changed = true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

    /**
     *  Shuts down the database
     */
    public void shutdown()
    {
        try
        {
            Statement st = conn.createStatement();
            if (!initialized)
            {
                return;

            // db writes out to files and performs clean shuts down
            // otherwise there will be an unclean shutdown
            // when program ends
            }
            st.execute("SHUTDOWN");
            conn.close();    // if there are no other open connection

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        initialized = false;
    }

    /**
     *  Initializes a new database
     * 
     * @return nothing
     */
    private void database_init()
    {
        try
        {

            Class.forName("org.h2.Driver");

            conn = DriverManager.getConnection("jdbc:h2:mem:db_" + Math.random(), // filenames
                    "sa", // username
                    "");                      // password

        }
        catch (Exception e)
        {
        }
        System.out.println("HanziDB : Done with database initialization");
        initialized = true;
    }

    /*
     * Create a new database with all the tables and views it needs
     * 
     * @return nothing
     */
    private void create_database() throws SQLException
    {
        Statement st = conn.createStatement();

        st.executeUpdate("CREATE TABLE database_info (" +
                " field VARCHAR(50), value VARCHAR(50))");
        st.executeUpdate("CREATE TABLE character (" +
                " char_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                " hanzi VARCHAR(2))");
        st.executeUpdate("CREATE TABLE character_pinyin (" +
                " character_pinyin_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                " char_id INTEGER," +
                " pinyin VARCHAR(10)," +
                " tone INTEGER," +
                " FOREIGN KEY (char_id) REFERENCES character(char_id))");
        st.executeUpdate("CREATE TABLE cword (" +
                " cword_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY)");
        st.executeUpdate("CREATE TABLE cword_pinyin_bridge (" +
                " cword_id INTEGER," +
                " character_pinyin_id INTEGER," +
                " pos INTEGER," +
                " notone BOOLEAN," +
                " FOREIGN KEY (cword_id) REFERENCES cword(cword_id)," +
                " FOREIGN KEY (character_pinyin_id) REFERENCES character_pinyin(character_pinyin_id))");
        st.executeUpdate("CREATE TABLE english (" +
                " eng_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                " cword_id INTEGER," +
                " translation VARCHAR(50)," +
                " FOREIGN KEY (cword_id) REFERENCES cword(cword_id))");
        st.executeUpdate("CREATE VIEW english_pinyin_chinese AS" +
                " (SELECT c_words.cword_id, c_words.hanzi, c_words.pinyin," +
                " GROUP_CONCAT(DISTINCT e.translation SEPARATOR ', ') AS translations" +
                " FROM (SELECT cpb.cword_id," +
                " GROUP_CONCAT(ch.hanzi ORDER BY cpb.pos ASC SEPARATOR '') AS hanzi," +
                " GROUP_CONCAT(CONCAT(cp.pinyin,cp.tone) ORDER BY cpb.pos ASC SEPARATOR '') AS pinyin" +
                " FROM cword AS cw" +
                " JOIN cword_pinyin_bridge AS cpb ON cpb.cword_id=cw.cword_id" +
                " JOIN character_pinyin AS cp ON cp.character_pinyin_id=cpb.character_pinyin_id" +
                " JOIN character AS ch ON ch.char_id=cp.char_id" +
                " GROUP BY cpb.cword_id) AS c_words" +
                " JOIN english AS e ON e.cword_id=c_words.cword_id " +
                " GROUP BY e.cword_id )");

        st.executeUpdate("INSERT INTO database_info(field, value) VALUES('version', '1.0')");
        st.executeUpdate("INSERT INTO database_info(field, value) VALUES('minimum_prog_version', '0.0')");

        st.close();
    }

    /**
     *  Get the number of chinese words in the database
     * 
     * @return int number of chinese words
     */
    public int get_number_words()
    {
        if (!initialized)
        {
            return 0;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT COUNT(cword_id) FROM english_pinyin_chinese GROUP BY TRUE");
            if (!rs.next())
            {
                return 0;
            }
            else
            {
                return rs.getInt(1);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     *  Get the number of chinese characters in the database
     * 
     * @return int number of chinese characters
     */
    public int get_number_characters()
    {
        if (!initialized)
        {
            return 0;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT COUNT(char_id) FROM character GROUP BY TRUE");
            if (!rs.next())
            {
                return 0;
            }
            else
            {
                return rs.getInt(1);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    public ArrayList<ArrayList<String>> get_words_with_character(String hanzi)
    {
        ArrayList<String> temp;
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT epc.cword_id, epc.hanzi, epc.pinyin, epc.translations FROM " +
                    " (SELECT cword_id FROM" +
                    " character AS ch" +
                    " JOIN character_pinyin AS cp ON ch.char_id=cp.char_id" +
                    " JOIN cword_pinyin_bridge AS cpb ON cp.character_pinyin_id=cpb.character_pinyin_id" +
                    " WHERE ch.hanzi='" + hanzi + "'" +
                    " GROUP BY cpb.cword_id) AS selected_words" +
                    " JOIN english_pinyin_chinese AS epc ON epc.cword_id=selected_words.cword_id" +
                    " ORDER BY epc.pinyin");
            for (; rs.next();)
            {
                temp = new ArrayList<String>();
                temp.clear();
                temp.add(rs.getString(2));
                temp.add(rs.getString(3));
                temp.add(rs.getString(4));
                res.add(temp);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;

    }

    /**
     * Get details about a particular chinese word
     * 
     * @param index from 0 to the number of words - 1 
     * @see get_number_words
     * @return ArrayList with : Chinese words, pinyin and translations
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

            rs = st.executeQuery("SELECT * FROM english_pinyin_chinese ORDER BY pinyin, hanzi");
            rs.relative(index + 1);
            res.add(rs.getString(2));
            res.add(rs.getString(3));
            res.add(rs.getString(4));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }

    public String get_character_details(int index)
    {
        String res="";
        if (!initialized)
        {
            return res;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT hanzi FROM character");
            rs.relative(index + 1);
            res = rs.getString(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }

    public ArrayList<String> get_pinyin_for_character(String hanzi)
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

            rs = st.executeQuery("SELECT pinyin FROM character_pinyin AS cp" +
                    " JOIN character AS ch ON ch.char_id=cp.char_id" +
                    " WHERE ch.hanzi='" + hanzi + "'");
            for (; rs.next();)
            {
                res.add(rs.getString(1));
            }
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
}    // class Testdb