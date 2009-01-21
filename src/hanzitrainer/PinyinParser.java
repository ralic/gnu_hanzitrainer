/*
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

import java.util.*;

/**
 * Class that can handle full word or sentence of pinyins for parsing
 * 
 * @author Matthieu
 */
public class PinyinParser
{

    private String to_parse = "";
    private ArrayList<Pinyin> parsed;

    /**
     * Class creator without any initialization 
     */
    public PinyinParser()
    {
    }

    /**
     * Class creator that initialize it with the String give as input
     * 
     * @param input String of pinyin to be parsed
     */
    public PinyinParser(String input)
    {
        set_string_to_parse(input);
    }

    /**
     * Change the String of pinyin to be parsed
     * @param input the String of pinyin
     */
    public void set_string_to_parse(String input)
    {
        to_parse = input;
        parsed = parse_string(input);
        if (parsed.size() == 0)
        {
            to_parse = "";
        }
        return;
    }

    /**
     * Return the String of pinyin that was used
     * @return The pinyin String that has been parsed
     */
    public String get_string_parsed()
    {
        return to_parse;
    }

    static private ArrayList<Pinyin> parse_string(String pinyin_string)
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
     * @return the number of pinyin elements in the String that was parsed
     */
    public int get_number_of_elements()
    {
        if (to_parse.equals(""))
        {
            return 0;
        }
        else
        {
            return parsed.size();
        }
    }

    /**
     * 
     * @param index the Pinyin element index in the String
     * @return the Pinyin queried
     */
    public Pinyin get_element(int index)
    {
        if (index >= get_number_of_elements())
        {
            return new Pinyin();
        }
        return parsed.get(index);
    }

    /**
     * 
     * @return the printed version of the whole pinyin String
     */
    public String get_print_version()
    {
        int i;
        String result = "";

        for (i = 0; i < get_number_of_elements(); i++)
        {
            result += get_element(i).get_print_version();
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
