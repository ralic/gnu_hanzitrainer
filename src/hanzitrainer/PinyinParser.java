/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hanzitrainer;

import java.util.*;

/**
 *
 * @author Matthieu
 */
public class PinyinParser
{

    private String to_parse = "";
    private ArrayList<Pinyin> parsed;

    public PinyinParser()
    {
    }

    public PinyinParser(String input)
    {
        set_string_to_parse(input);
    }

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

    public String get_string_parsed()
    {
        return to_parse;
    }

    public void parse()
    {
        parsed = parse_string(to_parse);
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
            for (i = 0; i < Math.min(7, pinyin_string.length()+1); i++) // the maximum size with tone is 6 characters...
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

    public Pinyin get_element(int number)
    {
        if (number >= get_number_of_elements())
        {
            return new Pinyin();
        }
        return parsed.get(number);
    }

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
