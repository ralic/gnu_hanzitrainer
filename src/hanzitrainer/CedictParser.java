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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 *
 * @author matthieu
 */
public class CedictParser extends HanziDB
{

    /**
     *
     */
    public CedictParser(java.awt.Frame parent)
    {
        super();

        Preferences my_preferences = Preferences.userNodeForPackage(HanziTrainerApp.class);
        init_other_components(parent);
        filename = my_preferences.get("cedict temp db :", "");
        if (!filename.equals(""))
        {
            System.out.print("Cedict parser init, opened db "+filename);
            HanziDB_open(filename);
        }
    }

    /**
     * Instantiate a new database from a Cedict file
     *
     * @param file the file name (and path) of the Cedict file to import
     */
    public CedictParser(java.awt.Frame parent, String file)
    {
        super();
        init_other_components(parent);
        Cedict_import(file);
    }

    /**
     * Get current Cedict file parsed
     *
     */
    public String Cedict_getfile()
    {
        return cedict_file;
    }

    private void init_other_components(java.awt.Frame parent)
    {
        parent_frame = parent;
    }

    public void abort()
    {
        abort = true;
    }

    private class Cedict_importer extends SwingWorker<Integer, Integer>
    {

        private BufferedReader reader;
        private ProgressMonitor progress_monitor;
        private String cedict_file;
        int status;

        public Cedict_importer(String cedict_file_name, ProgressMonitor progress_monitor)
        {
            try
            {
                this.reader = new BufferedReader(new FileReader(cedict_file_name));
            }
            catch (FileNotFoundException ex)
            {
                System.out.println("file " + cedict_file_name + " not found");
            }
            this.cedict_file = cedict_file_name;
            this.progress_monitor = progress_monitor;
        }

        @Override
        protected void done()
        {
            Preferences my_preferences;
            String md5;

            // store file name used
            my_preferences = Preferences.userNodeForPackage(HanziTrainerApp.class);

            my_preferences.put("cedict file :", this.cedict_file);
            try
            {
                // save in a temporary database
                File temp_db = File.createTempFile("hanzitrainer", "cedict");
                HanziDB_set_filename(temp_db.getAbsolutePath());
                HanziDB_save();

                // save temporary database name
                my_preferences.put("cedict temp db :", filename);

                // compute and store md5 checksum
                md5 = hanzitrainer.md5.MD5.asHex(hanzitrainer.md5.MD5.getHash(new File(this.cedict_file)));

                my_preferences.put("cedict file md5 :", md5);
            }
            catch (IOException ex)
            {
                System.out.println("Cannot store Cedict data into temp database\n" + ex);
            }
        }

        @Override
        protected Integer doInBackground() throws Exception
        {
            String one_line;
            int line = 0;
            status = 0;

            try
            {
                one_line = reader.readLine();
                one_line = new String(one_line.getBytes(), "UTF-8");

                while ((one_line != null) && (!progress_monitor.isCanceled()))
                {
                    line++;
                    progress_monitor.setProgress(line);

                    System.out.println("parsing :" + one_line);
                    try
                    {
                        parse_one_line(one_line);
                    }
                    catch (ParseException e)
                    {
                        System.out.println("The faulty line is " + e.getMessage());
                        status = -1;
                        return status;
                    }
                    one_line = reader.readLine();
                    one_line = new String(one_line.getBytes(), "UTF-8");
                }
                if (!progress_monitor.isCanceled())
                {
                    status = 0;
                }
                else
                {
                    status = -1;
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                status = -1;
            }
            return status;
        }
    }

    /**
     * Import a Cedict file into the database
     * 
     * @param cedict_file_name the name of the file to parse for importation
     */
    public int Cedict_import(String cedict_file_name)
    {
        BufferedReader reader;
        int max_line = 0;
        abort = false;
        Preferences my_preferences;
        String old_md5, new_md5;

        // check if it is the same file we already imported before
        my_preferences = Preferences.userNodeForPackage(HanziTrainerApp.class);
        old_md5 = my_preferences.get("cedict file md5 :", "");
        try
        {
            // compute the hash of the given file
            new_md5 = hanzitrainer.md5.MD5.asHex(hanzitrainer.md5.MD5.getHash(new File(cedict_file_name)));
            if (old_md5.equals(new_md5))
            {
                // load from the temporary database
                filename = my_preferences.get("cedict temp db :", "");
                HanziDB_open(filename);
                cedict_file = cedict_file_name;
                try
                {
                    if (this.check_for_empty_db() == false)
                    {
                        my_preferences.put("cedict file :", cedict_file);
                        System.out.println("Opened temporary database for cedict, file %s" + filename);
                        return 0;
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println("Cannot check for empty database, assume it failed\n");
                }
            }
        }
        catch (IOException ex)
        {
            System.out.println("Cannot calculate new MD5 for some reason...\n");
        }


        // reinitialize everything
        this.HanziDB_close();

        try
        {
            reader = new BufferedReader(new FileReader(cedict_file_name));
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("file " + cedict_file_name + " not found");
            return -1;
        }

        try
        {
            String one_line = reader.readLine();
            while (one_line != null)
            {
                one_line = reader.readLine();
                max_line++;
            }
            System.out.println("Maximum progress is " + max_line);
            progress_monitor = new ProgressMonitor(this.parent_frame, "Cedict Parsing", "", 0, max_line);
            progress_monitor.setMillisToDecideToPopup(2000);

            Cedict_importer t = new Cedict_importer(cedict_file_name, progress_monitor);
            t.execute();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }

    private void parse_one_line(String data) throws ParseException
    {
        // ignore if the line starts with '#'
        if (data.startsWith("#"))
        {
            return;
        }

        StringTokenizer tokenizer = new StringTokenizer(data, "/");
        ArrayList<String> translations = new ArrayList<String>();

        String info = tokenizer.nextToken(); // to be parsed later

        while (tokenizer.hasMoreElements())
        {
            translations.add(tokenizer.nextToken());
        }

        int space_index = info.indexOf(" ");
        if (space_index == -1)
        {
            // generate exception, file does not seem to be well formatted
            throw new ParseException("bad Cedict format (1), data [" + data + "]", 0);
        }
        int space_index2 = info.indexOf(" ", space_index + 1);
        if (space_index2 == -1)
        {
            // generate exception, file does not seem to be well formatted
            throw new ParseException("bad Cedict format (2), data [" + data + "]", 0);
        }
        String chinese = info.substring(space_index + 1, space_index2);

        int open_bracket = info.indexOf("[");
        if (open_bracket == -1)
        {
            // generate exception, file does not seem to be well formatted
            throw new ParseException("bad Cedict format (3), data [" + data + "]", 0);
        }
        int close_bracket = info.indexOf(']');
        if (close_bracket == -1)
        {
            // generate exception, file does not seem to be well formatted
            throw new ParseException("bad Cedict format (4), data [" + data + "]", 0);
        }
        String pinyins = info.substring(open_bracket + 1, close_bracket);

        // in pinyins : replace '5' with '0', replace 'u:' with 'v', change to lower case
        pinyins = pinyins.toLowerCase().replaceAll("u:", "v").replaceAll("5", "0");

        System.out.println("CEDICT : Found chinese -" + chinese + "- pinyin -" + pinyins + "-");

        // create the lists
        ArrayList<String> hanzi_list = new ArrayList<String>();
        for (int i = 0; i < chinese.codePointCount(0, chinese.length()); i++)
        {
            int from = chinese.offsetByCodePoints(0, i);
            int to = chinese.offsetByCodePoints(0, i + 1);
            System.out.println("Adding " + chinese.substring(from, to));
            hanzi_list.add(chinese.substring(from, to));
        }

        ArrayList<String> pinyin_list = new ArrayList<String>();
        StringTokenizer pinyin_tokenizer = new StringTokenizer(pinyins, " ");
        while (pinyin_tokenizer.hasMoreTokens())
        {
            String token = pinyin_tokenizer.nextToken();
            //pinyin_list.add(pinyin_tokenizer.nextToken());
            pinyin_list.add(token);
            System.out.println("Adding " + token);
        }

        for (int i = 0; i < translations.size(); i++)
        {
            add_translation(translations.get(i), pinyin_list, hanzi_list);
        }
    }

    public static void main(String[] args)
    {
        //CedictParser parser = new CedictParser(this.getFrame(), "/host/HanziTrainer/usr/cedict_1_0_ts_utf-8_mdbg/cedict_ts.u8");
    }
    private String cedict_file = "";
    private Boolean abort = false;
    java.awt.Frame parent_frame;
    private ProgressMonitor progress_monitor;
}
