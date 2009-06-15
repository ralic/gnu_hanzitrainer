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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 *
 * @author matthieu
 */
public class CedictParser extends HanziDB {

    public CedictParser()
    {
        super();
    }

    public CedictParser(String file)
    {
        super();
        Cedict_import(file);
    }

    public void Cedict_import(String cedict_file_name) {
        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new FileReader(cedict_file_name));
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("file " + cedict_file_name + " not found");
            return;
        }

        try
        {
            String one_line = reader.readLine();

            for (int i=0; i<80; i++)
            //while (one_line != null)
            {
                System.out.println("parsing :" + one_line);
                parse_one_line(one_line);
                one_line = reader.readLine();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void parse_one_line(String data)
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
        int space_index2 = info.indexOf(" ", space_index+1);
        String chinese = info.substring(space_index+1, space_index2);

        int open_bracket = info.indexOf("[");
        int close_bracket = info.indexOf(']');
        String pinyins = info.substring(open_bracket+1, close_bracket);

        // in pinyins : replace '5' with '0', replace 'u:' with 'v', change to lower case
        pinyins = pinyins.toLowerCase().replaceAll("u:", "v").replaceAll("5", "0");

        System.out.println("Found chinese -" + chinese + "- pinyin -" + pinyins + "-");

        // create the lists
        ArrayList<String> hanzi_list = new ArrayList<String>();
        for (int i=0; i<chinese.codePointCount(0, chinese.length()); i++)
        {
            int from = chinese.offsetByCodePoints(0, i);
            int to = chinese.offsetByCodePoints(0, i+1);
            System.out.println("Adding " + chinese.substring(from,to));
            hanzi_list.add(chinese.substring(from,to));
        }

        ArrayList<String> pinyin_list = new ArrayList<String>();
        StringTokenizer pinyin_tokenizer = new StringTokenizer(pinyins," ");
        while (pinyin_tokenizer.hasMoreTokens())
        {
            String token = pinyin_tokenizer.nextToken();
            //pinyin_list.add(pinyin_tokenizer.nextToken());
            pinyin_list.add(token);
            System.out.println("Adding " + token);
        }

        for (int i=0; i<translations.size(); i++)
        {
            add_translation(translations.get(i), pinyin_list,hanzi_list);
        }
    }

    public static void main(String[] args)
    {
        CedictParser parser = new CedictParser("/host/HanziTrainer/usr/cedict_1_0_ts_utf-8_mdbg/cedict_ts.u8");
        //parser.parse_one_line("龍港 龙港 [Long2 Gang3] /Longgang district of Huludao city 葫蘆島市|葫芦岛市, Liaoning/");
    }
}
