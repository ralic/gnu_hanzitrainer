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

import java.util.*;

/**
 * Class that can handle full word or sentence of pinyins for parsing
 * 
 * @author Matthieu
 */
public class PinyinParser
{

    /**
     * Class creator without any initialization 
     */
    public PinyinParser()
    {
    }

    static public ArrayList<Pinyin> parse_string(String pinyin_string)
    {
        int strophe_index;
        ArrayList<Pinyin> result = new ArrayList<Pinyin>();
        ArrayList<Pinyin> temp;
        int i;

        strophe_index = pinyin_string.indexOf("'");
        if (strophe_index != -1)
        {
            if (strophe_index == 0)
            {
                return parse_string(pinyin_string.substring(1, pinyin_string.length()));
            }
            if (strophe_index == pinyin_string.length())
            {
                return parse_string(pinyin_string.substring(0, pinyin_string.length() - 1));
            }
            temp = parse_string(pinyin_string.substring(0, strophe_index - 1));
            if (temp.size() == 0)
            {
                result.clear();
                return result;
            }
            result = temp;

            temp = parse_string(pinyin_string.substring(strophe_index + 1, pinyin_string.length()));
            if (temp.size() == 0)
            {
                result.clear();
                return result;
            }
            result.addAll(temp);

            return result;
        }
        else
        {
            for (i = Math.min(6, pinyin_string.length()); i >= 0; i--) // the maximum size with tone is 6 characters...
                // start with the maximum size to avoid breaking down unnecessary (that would have a "'")
            {
                String try_pinyin = pinyin_string.substring(0, i);
                if (Pinyin.verify_pinyin(try_pinyin))
                {
                    if (i < pinyin_string.length())
                    {
                        String next_step=pinyin_string.substring(i, pinyin_string.length());
                        temp = parse_string(next_step);
                        //temp = parse_string(pinyin_string.substring(i + 1, pinyin_string.length()));
                        if (temp.size() == 0)
                        {
                            continue;
                        }
                        result = temp;
                    }
                    String to_add = try_pinyin;
                    result.add(0, new Pinyin(to_add));
                    //result.add(0, new Pinyin(pinyin_string.substring(0, i)));
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * 
     * @param input String of pinyins with tones as numbers
     * @return String with accents correctly marked
     */
    public static String convert_to_printed_version(String input)
    {
        int i;
        String result = "";
        ArrayList<Pinyin> parsed = parse_string(input);
        for (i = 0; i < parsed.size(); i++)
        {
            result += parsed.get(i).get_print_version();
        }
        return result;
    }
}
