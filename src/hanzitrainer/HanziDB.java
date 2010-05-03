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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public abstract class HanziDB
{

    protected String filename;
    protected Connection conn;
    protected boolean initialized = false;
    protected boolean changed = false;
    protected static final int database_ver = 5;

    public HanziDB()
    {
        database_init();

        create_database();
        changed = false;
        filename = "";

        System.out.println("HanziDB : Database initialized");
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
                System.out.println("HanziDB_open : reading file " + db_file_name + " failed, database version too high (" + get_database_version() + " vs. " + database_ver + "), creating a new empty one");
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
            e.printStackTrace();
        }

        if (upgrade_database() == true)
        {
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
            System.out.println("Saving to " + filename);
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
        filename = "";
        database_init();

        create_database();
    }

    public void HanziDB_set_filename(String name)
    {
        filename = name;
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

    /**
     *  Check if the currently opened database is empty
     * 
     * @return True if the database is empty
     */
    protected Boolean check_for_empty_db()
    {
        Statement st;
        try
        {
            st = conn.createStatement();

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
        catch (SQLException ex)
        {
        }
        return true;
    }

    /**
     *  Get the translation(s) for a Chinese word
     * 
     * @param chinese Chinese word
     * @return list of translations
     */
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
            int cword_id = get_word_id(chinese);

            if (cword_id == -1)
            {
                return res;
            }

            rs = st.executeQuery("SELECT e.translation FROM english AS e " +
                    "WHERE e.cword_id=" + cword_id);
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

    /**
     *  Get a list of the pinyins knowns for a Chinese character
     * 
     * @param character Chinese character
     * @return list of pinyin strings
     */
    public ArrayList<String> get_pinyin_for_character(String character)
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

    protected int find_existing_pinyin_character(String character, String pinyin) throws SQLException
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
            st.close();
            return res;
        }

        // find the character
        char_id = get_character_id(character);

        // tone that we want to store
        tone = Pinyin.pinyin_tone(pinyin);
        radical = Pinyin.pinyin_base(pinyin);

        rs = st.executeQuery("SELECT cp.character_pinyin_id FROM character_pinyin AS cp" +
                " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + radical + "' AND cp.tone=" + tone);
        if (rs.next())
        {
            res = rs.getInt(1);
            //System.out.println("find_existing_pinyin_character : combination of pinyin/character exists (same tone) " + res);
        }
        else
        {
            //System.out.println("find_existing_pinyin_character : Cannot find " + char_id + " " + radical + " " + tone + "!!");
        }

        st.close();
        return res;
    }

    /**
     *  Check if a character is Chinese (within a range of UTF codes)
     * 
     * @param input character
     * @return True if the character seems Chinese
     */
    public static Boolean is_chinese_char(String input)
    {
        int entry = input.codePointAt(0);
        if (((entry >= 0x4E00) && (entry <= 0x9fff)) // main CJK
                || ((entry >= 0x2e00) && (entry <= 0x31ff)) // additional punctuation
                || ((entry >= 0x3400) && (entry <= 0x4dbf)) // extension A
                )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected synchronized void add_character(String character) throws SQLException
    {
        Statement st = conn.createStatement();
        ResultSet rs = null;

        //System.out.println("Adding character " + character);

        if (!is_chinese_char(character))
        {
            st.close();
            return;
        }
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (rs.next())
        {
            st.close();
            return;
        }
        st.executeUpdate("INSERT INTO character(hanzi) VALUES('" + character + "')");

        st.close();
        changed = true;
    }

    protected synchronized void add_pinyin(String character, String pinyin) throws SQLException
    {
        Statement st = conn.createStatement();
        ResultSet rs = null;
        int char_id, res;
        int tone;
        String radical = "";

        //System.out.println("Adding pinyin " + pinyin + " for character " + character);

        // check that we are really dealing wSith a chinese character
        // TODO : this should probably be done outside of this function
        if (!is_chinese_char(character))
        {
            st.close();
            return;
        }

        // look if this character/pinyin is already there
        res = find_existing_pinyin_character(character, pinyin);
        if (res != -1)
        {
            st.close();
            return;
        }

        // find the character
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (!rs.next())
        {
            st.close();
            return;
        }
        char_id = rs.getInt(1);

        // tone that we want to store
        tone = Pinyin.pinyin_tone(pinyin);
        radical = Pinyin.pinyin_base(pinyin);

        //System.out.println("add_pinyin : Adding char " + char_id + ", pinyin " + radical + ", tone " + tone);
        st.executeUpdate("INSERT INTO character_pinyin(char_id, pinyin,tone) VALUES(" + char_id + ",'" + radical + "'," + tone + ")");

        st.close();
        changed = true;
    }

    /*
     * adds a new chinese word with its translation
     * 
     * 
     */
    public synchronized int add_translation(String english, ArrayList<String> pinyins, ArrayList<String> hanzi)
    {
        if (!initialized)
        {
            return -1;
        }
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;
            int char_pinyin_id = 0;
            int i;
            String chinese = "", pinyin = "";
            int found_chinese_id;
            int tone;

            if (pinyins.size() != hanzi.size())
            {
                System.out.println("add_translation : size of pinyins and hanzis need to be the same");
                st.close();
                return -1;
            }

            for (i = 0; i < hanzi.size(); i++)
            {
                chinese += hanzi.get(i);
                pinyin += pinyins.get(i);
            }

            System.out.println("add_translation : adding word [" + chinese + "] with pinyins [" + pinyin + "]" +
                    ", translation : [" + english + "]");
            found_chinese_id = get_word_id(chinese);
            if (found_chinese_id != -1)
            {
                //System.out.println("add_translation : this chinese word already exists");
            }
            else
            {
                // check the validity of the pinyins
                for (i = 0; i < pinyins.size(); i++)
                {
                    if (!Pinyin.verify_pinyin(pinyins.get(i)))
                    {
                        System.out.println("add_translation : pinyin " + pinyins.get(i) + " does not seem to be correct");
                        st.close();
                        return -1;
                    }
                }

                // First create a new Cword
                st.executeUpdate("INSERT INTO cword(chinese) VALUES('" + chinese + "')");
                rs = st.executeQuery("SELECT cword_id FROM  cword WHERE chinese='" + chinese + "'");
                if (!rs.next())
                {
                    System.out.println("add_translation : seems like I was not able to insert a new cword ??");
                    st.close();
                    return -1;
                }
                found_chinese_id = rs.getInt(1);
                //System.out.println("Cword id is " + found_chinese_id);
                st.executeUpdate("UPDATE cword SET chinese='" + chinese + "' WHERE cword_id=" + found_chinese_id);

                // Now add all characters and associated pinyins
                for (i = 0; i < pinyins.size(); i++)
                {
                    add_character(hanzi.get(i));
                    add_pinyin(hanzi.get(i), pinyins.get(i));
                    char_pinyin_id = find_existing_pinyin_character(hanzi.get(i), pinyins.get(i));
                    tone = Pinyin.pinyin_tone(pinyins.get(i));

                    //System.out.println("add_translation : adding chinese:" + found_chinese_id + ", char_pinyin:" + char_pinyin_id + " at " + i);
                    if (tone == 0)
                    {
                        st.executeUpdate("INSERT INTO cword_pinyin_bridge(cword_id, character_pinyin_id, pos, notone) VALUES(" + found_chinese_id + "," + char_pinyin_id + "," + i + ",true)");
                    }
                    else
                    {
                        st.executeUpdate("INSERT INTO cword_pinyin_bridge(cword_id, character_pinyin_id, pos, notone) VALUES(" + found_chinese_id + "," + char_pinyin_id + "," + i + ",false)");
                    }
                }
            }

            add_translation(english, found_chinese_id);

            st.close();
            changed = true;

            return found_chinese_id;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return -1;
        }

    }

    public synchronized void add_translation(String english, int id)
    {
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            english = english.substring(0, Math.min(249, english.length()));
            english = english.replaceAll("'", "''");

            //System.out.println("Add translation [" + english + "] to chinese " + found_chinese_id);

            rs = st.executeQuery("SELECT eng_id FROM english " +
                    " WHERE cword_id =" + id +
                    " AND translation='" + english + "'");
            if (!rs.next())
            {
                st.executeUpdate("INSERT INTO english(cword_id, translation) VALUES(" + id + ",'" + english + "')");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public synchronized void add_translation(ArrayList<String> english, ArrayList<String> pinyins, ArrayList<String> hanzi)
    {
        int id = add_translation(english.get(0), pinyins, hanzi);
        if (id == -1)
        {
            System.out.println("something went wrong  !!!);");
            return;
        }

        for (int i = 1; i < english.size(); i++)
        {
            add_translation(english.get(i), id);
        }
    }

    protected synchronized void change_word_pinyin(int id, String pinyin)
    {
        ArrayList<String> translations = get_chinese_word_translation(get_word_details(id).get(0));
        String chinese = get_word_details(id).get(0);
        ArrayList<String> hanzi = new ArrayList<String>();
        ArrayList<Pinyin> pinyins = PinyinParser.parse_string(pinyin);
        ArrayList<String> pinyin_strings = new ArrayList<String>();

        for (int i = 0; i < pinyins.size(); i++)
        {
            pinyin_strings.add(pinyins.get(i).get_lame_version());
        }

        for (int i = 0; i < chinese.codePointCount(0, chinese.length()); i++)
        {
            int from = chinese.offsetByCodePoints(0, i);
            int to = chinese.offsetByCodePoints(0, i + 1);

            hanzi.add(chinese.substring(from, to));
        }

        for (int i = 0; i < translations.size(); i++)
        {
            delete_translation(translations.get(i), hanzi);
        }
        add_translation(translations, pinyin_strings, hanzi);
    }

    /**
     * Deletes a character not used anymore
     *
     * @param char_id the ID of the character to delete
     */
    protected synchronized void delete_character(int char_id)
    {
        try
        {
            Statement st = conn.createStatement();

            System.out.println("delete_translation : removing that character");
            st.executeUpdate("DELETE FROM character " +
                    " WHERE char_id=" + char_id);
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Check for a potential character that does not link anywhere anymore
     *
     * @param char_id the ID of the character to potentially remove
     */
    protected synchronized void check_for_isolated_character(int char_id)
    {
        ResultSet rs = null;

        try
        {
            Statement st = conn.createStatement();

            rs = st.executeQuery("SELECT character_pinyin_id FROM character_pinyin" +
                    " WHERE char_id=" + char_id);
            if (!rs.next())
            {
                delete_character(char_id);
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Deletes a character/pinyin not used anymore
     *
     * @param char_pinyin_id the ID of the character/pinyin to delete
     */
    protected synchronized void delete_char_pinyin(int char_pinyin_id)
    {
        int char_id;
        ResultSet rs = null;

        try
        {
            Statement st = conn.createStatement();
            System.out.println("delete_translation : remove that character_pinyin");

            rs = st.executeQuery("SELECT char_id FROM character_pinyin" +
                    " WHERE character_pinyin_id=" + char_pinyin_id);
            rs.next();
            char_id = rs.getInt(1);

            st.executeUpdate("DELETE FROM character_pinyin" +
                    " WHERE character_pinyin_id=" + char_pinyin_id);
            check_for_isolated_character(char_id);

            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Check for a potential character/pinyin that does not link anywhere anymore
     *
     * @param char_pinyin_id the ID of the character/pinyin to potentially remove
     */
    protected synchronized void check_for_isolated_char_pinyin(int char_pinyin_id)
    {
        ResultSet rs = null;

        try
        {
            Statement st = conn.createStatement();

            // check if that pair of pinyin/character is used somewhere else
            rs = st.executeQuery("SELECT cword_id FROM cword_pinyin_bridge" +
                    " WHERE character_pinyin_id=" + char_pinyin_id);

            if (!rs.next())
            {
                delete_char_pinyin(char_pinyin_id);
            }
            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Delete a cword that's not to be used anymore
     *
     * @param cword_id the ID of the cword to delete
     */
    protected synchronized void delete_cword(int cword_id)
    {
        int char_pinyin_id;

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;
            rs = st.executeQuery("SELECT character_pinyin_id FROM cword_pinyin_bridge" +
                    " WHERE cword_id=" + cword_id);
            while (rs.next())
            {
                char_pinyin_id = rs.getInt(1);

                // remove that pair from this word
                st.executeUpdate("DELETE FROM cword_pinyin_bridge" +
                        " WHERE cword_id=" + cword_id +
                        " AND character_pinyin_id=" + char_pinyin_id);

                check_for_isolated_char_pinyin(char_pinyin_id);

                rs = st.executeQuery("SELECT character_pinyin_id FROM cword_pinyin_bridge" +
                        " WHERE cword_id=" + cword_id);
            }

            System.out.println("delete_translation : removing the chinese word from DB");
            // finally delete the word
            st.executeUpdate("DELETE FROM cword" +
                    " WHERE cword_id=" + cword_id);

            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Check for potential cwords not linked to anything anymore
     *
     * @param cword_id the ID of the word to potentially remove
     */
    protected synchronized void check_for_isolated_cword(int cword_id)
    {

        ResultSet rs = null;

        try
        {
            Statement st = conn.createStatement();

            // now look if this chinese word is still in use
            rs = st.executeQuery("SELECT eng_id FROM english" +
                    " WHERE cword_id=" + cword_id);
            if (!rs.next())
            {
                delete_cword(cword_id);
            }

            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Deletes an English translation from the database.
     * Cleans up any pinyin or character that would not be used anymore
     * 
     * @param english English version of the word
     * @param hanzi Chinese version of the word
     */
    public synchronized void delete_translation(String english, ArrayList<String> hanzi)
    {
        if (!initialized)
        {
            return;
        }
        try
        {
            Statement st = conn.createStatement();
            int i;
            String chinese = "";
            int found_chinese_id;

            System.out.println("delete_translation: removing [" + english + "]");
            for (i = 0; i < hanzi.size(); i++)
            {
                chinese += hanzi.get(i);
            }
            found_chinese_id = get_word_id(chinese);
            st.executeUpdate("DELETE FROM english" +
                    " WHERE cword_id=" + found_chinese_id +
                    " AND translation='" + english + "'");

            // now look if this chinese word is still in use
            check_for_isolated_cword(found_chinese_id);

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
     */
    protected void database_init()
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
            e.printStackTrace();
        }
        System.out.println("HanziDB : Done with database initialization");
        initialized = true;
    }

    /**
     * 
     * Create a new database with all the tables and views it needs
     * 
     */
    protected void create_database()
    {
        try
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
                    " cword_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    " chinese VARCHAR(30))");
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
                    " translation VARCHAR(250)," +
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

            st.executeUpdate("INSERT INTO database_info(field, value) VALUES('version', '" + database_ver + "')");
            st.executeUpdate("INSERT INTO database_info(field, value) VALUES('minimum_prog_version', '0.0')");

            st.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

    /**
     * 
     * Placeholder for upgrading the database when needed
     *
     * @return true is the database has been upgraded
     */
    protected boolean upgrade_database()
    {
        int version;
        boolean updated = false;

        version = get_database_version();

        if (version <= 2)
        {
            System.out.println("Upgrading master database to version 3");

            try
            {
                Statement st = conn.createStatement();

                // Make sure the pinyin table never has NULL
                st.executeUpdate("UPDATE character_pinyin" +
                        " SET tone=0 WHERE tone IS NULL");

                st.executeUpdate("UPDATE database_info SET value='3' WHERE field='version'");
                st.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

        }

        if (version <= 3)
        {
            System.out.println("Upgrading master database to version 4");

            try
            {
                Statement st = conn.createStatement();

                // upgrade the size for translations
                st.executeUpdate("ALTER TABLE english ALTER COLUMN translation VARCHAR(250)");
                st.executeUpdate("UPDATE database_info SET value='4' WHERE field='version'");

                st.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }
        }

        if (version <= 4)
        {
            System.out.println("Upgrading master database to version 5");

            try
            {
                Statement st = conn.createStatement();
                Statement st2 = conn.createStatement();
                ResultSet rs = null;

                // Add the chinese word into the cword table
                st.executeUpdate("ALTER TABLE cword ADD COLUMN chinese VARCHAR(30)");
                st.executeUpdate("UPDATE database_info SET value='5' WHERE field='version'");

                rs = st2.executeQuery("SELECT cword_id, hanzi FROM english_pinyin_chinese");
                for (; rs.next();)
                {
                    int id = rs.getInt(1);
                    String word = rs.getString(2);
                    st.executeUpdate("UPDATE cword SET chinese='" + word + "' WHERE cword_id=" + id);
                }
                st.close();
                st2.close();

            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }
            // this one should always be at the last update...
            updated = true;
        }
        // always recreate the view at the end...
        try
        {
            Statement st = conn.createStatement();

            // Add the chinese word into the cword table
            st.executeUpdate("DROP VIEW IF EXISTS english_pinyin_chinese");
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
            st.close();

            // this one should always move to the latest version...
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return updated;
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

            rs = st.executeQuery("SELECT COUNT(cword_id) FROM cword GROUP BY TRUE");
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

    /**
     * 
     * Returns detail information about the words that contain a particular Chinese character with a filter on a particular pinyin
     * 
     * @param hanzi Chinese character
     * @param pinyin pinyin to filter with
     * @return ArrayList<Integer> List of Chinese word ids
     */
    public ArrayList<Integer> get_words_with_character(String hanzi, String pinyin)
    {

        ArrayList<Integer> res = new ArrayList<Integer>();

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            int tone = Pinyin.pinyin_tone(pinyin);
            String pinyin_base = Pinyin.pinyin_base(pinyin);

            //System.out.println("Querying db with char :" + hanzi + " pinyin :" + pinyin_base + " tone :" + tone);

            rs = st.executeQuery("SELECT epc.cword_id FROM " +
                    " (SELECT cword_id FROM" +
                    " character AS ch" +
                    " JOIN character_pinyin AS cp ON ch.char_id=cp.char_id" +
                    " JOIN cword_pinyin_bridge AS cpb ON cp.character_pinyin_id=cpb.character_pinyin_id" +
                    " WHERE cp.character_pinyin_id = " +
                    " (SELECT character_pinyin_id FROM " +
                    " character AS ch" +
                    " JOIN character_pinyin AS cp ON ch.char_id=cp.char_id" +
                    " WHERE ch.hanzi='" + hanzi + "'" +
                    " AND cp.pinyin='" + pinyin_base + "'" +
                    " AND cp.tone=" + tone + ")" +
                    " GROUP BY cpb.cword_id) AS selected_words" +
                    " JOIN english_pinyin_chinese AS epc ON epc.cword_id=selected_words.cword_id" +
                    " ORDER BY epc.pinyin");
            for (; rs.next();)
            {
                res.add(rs.getInt(1));
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;

    }

    /**
     * 
     * Returns detail information about the words that contain a particular Chinese character
     * 
     * @param hanzi Chinese character
     * @return ArrayList<Integer> List of Chinese word ids
     */
    public ArrayList<Integer> get_words_with_character(String hanzi)
    {

        ArrayList<Integer> res = new ArrayList<Integer>();

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT epc.cword_id FROM " +
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
                res.add(rs.getInt(1));
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;

    }

    /**
     * 
     * Return the ID of the nth word
     * 
     * @param index position in the word list
     * @return id of a Chinese word
     */
    public int get_word_id(int index)
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

            rs = st.executeQuery("SELECT cword_id FROM english_pinyin_chinese ORDER BY pinyin, hanzi");

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
     * Returns the ID of a Chinese word
     * 
     * @param chinese Chinese String of the word in the lsit
     * @return int ID of the word
     */
    public int get_word_id(String chinese)
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

            rs = st.executeQuery("SELECT cword_id FROM cword WHERE chinese='" + chinese + "'");
            if (!rs.next())
            {
                return res;
            }
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
     * Get details about a particular chinese word
     * 
     * @param index from 0 to the number of words - 1 
     * @see get_number_words()
     * @return ArrayList with : Chinese word, pinyin, translations
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

            rs = st.executeQuery("SELECT cword_id, hanzi, pinyin, translations FROM english_pinyin_chinese WHERE cword_id=" + index);
            if (!rs.next())
            {
                return res;
            }
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

    /**
     * 
     * Get the id of a character
     * 
     * @param character Chinese character
     * @return id of the character
     */
    public int get_character_id(String character)
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

            rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
            if (!rs.next())
            {
                return res;
            }
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
     * Get the id of an indexed character
     * 
     * @param index from 0 to the number of words - 1 
     * @return id of the character
     */
    public int get_character_id(int index)
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

            rs = st.executeQuery("SELECT char_id FROM character ORDER BY hanzi");
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

    public int get_database_version()
    {
        int res = 0;
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT value FROM database_info AS di" +
                    " WHERE field='version'");
            rs.next();

            res = Integer.parseInt(rs.getString(1));

            System.out.println("Database version " + res);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return res;
    }
}    // class Testdb

