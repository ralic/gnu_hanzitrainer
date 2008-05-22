/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hanzitrainer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HanziDB
{

    Connection conn;

    public HanziDB()
    {
        this(new String("mem:db_" + Math.random()));
    }

    // we dont want this garbage collected until we are done
    public HanziDB(String db_file_name_prefix)
    {
        try {

        Class.forName("org.h2.Driver");

        conn = DriverManager.getConnection("jdbc:h2:" + db_file_name_prefix, // filenames
                "sa", // username
                "");                      // password

        if (check_for_empty_db())
        {

            System.out.println("database is empty");
            create_database();
            if (check_for_empty_db())
            {
                System.out.println("still...");
            }
            else
            {
                System.out.println("not anymore");
            }
        }
        else
        {
            System.out.println("database is not empty");
        }
        }
        catch (Exception e) {
        
        }
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

    public int find_chinese_word(String chinese) throws SQLException
    {
        int len = chinese.codePointCount(0, chinese.length());
        int res = -1;
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

        return res;
    }

    public ArrayList get_pinyin_from_character(String character)
    {
        ArrayList<String> res = new ArrayList<String>();

        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT (cp.pinyin+cp.tone) FROM character AS c" +
                    " JOIN character_pinyin AS cp ON c.char_id=cp.char_id" +
                    " WHERE c.hanzi='" + character + "'");

            System.out.printf("for character %s\n", character);

            for (; rs.next();)
            {
                System.out.printf("pinyin %s\n", rs.getString(1));
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
            System.out.println("trying to add pinyin for a non chinese character");
            st.close();
            return res;
        }

        // find the character
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (!rs.next())
        {
            System.out.println("Could not find the character");
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
                    " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + pinyin + "'");
            if (rs.next())
            {
                res = rs.getInt(1);
                System.out.println("combination of pinyin/character exists (with any tone) " + res);
                st.close();
            }
            else
            {
                System.out.println("cannot find combination (no tone)");
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
                System.out.println("combination of pinyin/character exists (same tone) " + res);
                st.close();
                return res;
            }


            rs = st.executeQuery("SELECT cp.pinyin FROM character_pinyin AS cp" +
                    " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + radical + "' AND cp.tone=0");
            if (rs.next())
            {
                res = rs.getInt(1);
                System.out.println("combination of pinyin/character already exists (0) " + res);
                st.close();
                return res;
            }
            System.out.println("Cannot find " + char_id + " " + radical + " " + tone + "!!");

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
            System.out.println("trying to add a non chinese character");
            st.close();
            return;
        }
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (rs.next())
        {
            System.out.println("character already exists");
            st.close();
            return;
        }
        st.executeUpdate("INSERT INTO character(hanzi) VALUES('" + character + "')");

        st.close();
    }

    private synchronized void add_pinyin(String character, String pinyin) throws SQLException
    {
        Statement st = conn.createStatement();
        ResultSet rs = null;
        int char_id;
        int tone;
        String radical = "";

        // check that we are really dealing wSith a chinese character
        // TODO : this should probably be done outside of this function
        if (!is_chinese_char(character))
        {
            System.out.println("trying to add pinyin for a non chinese character");
            st.close();
            return;
        }

        // find the character
        rs = st.executeQuery("SELECT char_id FROM character WHERE hanzi='" + character + "'");
        if (!rs.next())
        {
            System.out.println("Could not find the character");
            st.close();
            return;
        }
        char_id = rs.getInt(1);

        // tone that we want to store
        tone = pinyin.charAt(pinyin.length() - 1);
        if ((tone < '1') || (tone > '4'))
        {
            tone = 0;
            rs = st.executeQuery("SELECT cp.pinyin FROM character_pinyin AS cp" +
                    " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + pinyin + "'");
            if (rs.next())
            {
                System.out.println("combination of pinyin/character already exists (with any tone)");
                st.close();
                return;
            }
            System.out.println("Adding char " + char_id + ", pinyin " + pinyin + ", tone 0");
            st.executeUpdate("INSERT INTO character_pinyin(char_id, pinyin,tone) VALUES(" + char_id + ",'" + pinyin + "',0)");
        }
        else
        {
            tone = (int) tone - '0';
            rs = st.executeQuery("SELECT cp.pinyin FROM character_pinyin AS cp" +
                    " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + pinyin + "' AND cp.tone=" + tone);
            if (rs.next())
            {
                System.out.println("combination of pinyin/character already exists (same tone)");
                st.close();
                return;
            }

            radical = pinyin.substring(0, pinyin.length() - 1);
            rs = st.executeQuery("SELECT cp.pinyin FROM character_pinyin AS cp" +
                    " WHERE cp.char_id=" + char_id + " AND cp.pinyin='" + radical + "' AND cp.tone=0");
            if (rs.next())
            {
                System.out.println("Updating char " + char_id + ", pinyin " + radical + ", tone " + tone);
                // need to update the table to add a tone and be done with it
                st.executeUpdate("UPDATE character_pinyin SET tone=" + tone + " WHERE pinyin='" + radical + "' AND" +
                        "char_id=" + char_id + "AND tone=0");

                System.out.println("combination of pinyin/character already exists (updating tone 0 to real one");
                st.close();
                return;
            }

            System.out.println("Adding char " + char_id + ", pinyin " + radical + ", tone " + tone);
            st.executeUpdate("INSERT INTO character_pinyin(char_id, pinyin,tone) VALUES(" + char_id + ",'" + radical + "'," + tone + ")");

        }

        st.close();
    }

    public synchronized void add_translation(String english, ArrayList<String> pinyins, ArrayList<String> hanzi)
    {
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null, rs2 = null;
            int char_pinyin_id = 0;
            int i;
            String chinese = new String("");
            int found_chinese_id;
            int tone;

            if (pinyins.size() != hanzi.size())
            {
                System.out.println("size of pinyins and hanzis need to be the same");
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
                System.out.println("this chinese word already exists");
            }
            else
            {
                st.executeUpdate("INSERT INTO cword() VALUES()");
                rs = st.executeQuery("SELECT * FROM (" +
                        "SELECT cword.cword_id, SUM(pos) AS res FROM cword" +
                        " LEFT OUTER JOIN cword_pinyin_bridge AS cpb ON cword.cword_id=cpb.cword_id" +
                        " GROUP BY cword.cword_id)" +
                        " WHERE res IS NULL");
                if (!rs.next())
                {
                    System.out.println("seems like I was not able to insert a new cword ??");
                    st.close();
                    return;
                }
                found_chinese_id = rs.getInt(1);
            }


            for (i = 0; i < pinyins.size(); i++)
            {
                add_character(hanzi.get(i));
                add_pinyin(hanzi.get(i), pinyins.get(i));
                char_pinyin_id = find_existing_pinyin_character(hanzi.get(i), pinyins.get(i));
                tone = pinyins.get(i).charAt(pinyins.get(i).length() - 1);

                System.out.println("adding chinese:" + found_chinese_id + ", char_pinyin:" + char_pinyin_id + " at " + i);
                if ((tone < '1') || (tone > '4'))
                {
                    st.executeUpdate("INSERT INTO cword_pinyin_bridge(cword_id, character_pinyin_id, pos, notone) VALUES(" + found_chinese_id + "," + char_pinyin_id + "," + i + ",true)");
                }
                else
                {
                    st.executeUpdate("INSERT INTO cword_pinyin_bridge(cword_id, character_pinyin_id, pos, notone) VALUES(" + found_chinese_id + "," + char_pinyin_id + "," + i + ",false)");
                }
            }

            st.executeUpdate("INSERT INTO english(cword_id, translation) VALUES(" + found_chinese_id + ",'" + english + "')");

            st.close();

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

    }

    public void shutdown() throws SQLException
    {

        Statement st = conn.createStatement();

        // db writes out to files and performs clean shuts down
        // otherwise there will be an unclean shutdown
        // when program ends
        st.execute("SHUTDOWN");
        conn.close();    // if there are no other open connection

    }

    /**
     *  Get the number of chinese words in the database
     */
    public int get_number_words()
    {
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT COUNT(cword_id) FROM english_pinyin_chinese GROUP BY ALL");
            if (!rs.next())
                return 0;
            else
                return rs.getInt(1);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return 0;
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
        try
        {
            Statement st = conn.createStatement();
            ResultSet rs = null;

            rs = st.executeQuery("SELECT * FROM english_pinyin_chinese");
            rs.relative(index+1);
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
}    // class Testdb