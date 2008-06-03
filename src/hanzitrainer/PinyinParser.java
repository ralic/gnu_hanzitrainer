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
public class PinyinParser {
    
    private String to_parse="";
    private ArrayList parsed;
    
    public PinyinParser()
    {
        parsed = new ArrayList();
    }
    
    public PinyinParser(String input)
    {
        this.set_string_to_parse(input);
    }
    
    public void set_string_to_parse(String input)
    {
        return;
    }
    
    public String get_string_parsed()
    {
        return this.to_parse;
    }
    
    public void parse()
    {
        this.parse_string(this.to_parse, parsed);
    }
    
    private Boolean parse_string(String pinyin_string, ArrayList result)
    {
        int strophe_index;
        
        strophe_index = pinyin_string.indexOf("'");
        if (strophe_index != -1)
        {
            if (!this.parse_string(pinyin_string.substring(0, strophe_index-1),
                    result))
                return false;
            if (!this.parse_string(
                    pinyin_string.substring(strophe_index+1, pinyin_string.length()),
                    result))
                return false;
        }
        else
        {
            for (String try_pinyin : allowed_pinyin)
            {
                if (pinyin_string.startsWith(try_pinyin))
                {
                    // TODO
                    if ((pinyin_string.codePointAt(try_pinyin.length()) >= '0') &&
                            (pinyin_string.codePointAt(try_pinyin.length()) <= '4'))
                    {
                        
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean verify_pinyin(String entry)
    {
        int i;
        String temp;
        for (String try_pinyin : allowed_pinyin)
        {
            if (entry.equals(try_pinyin))
                return true;
            for (i=1;i<5; i++)
            {
                temp = try_pinyin+i;
                if (entry.equals(temp))
                    return true;
            }
        }
        return false;
    }
    
    public int get_number_of_elements()
    {
        return parsed.toArray().length;
    }

    public String get_element(int number)
    {
        return (String) parsed.toArray()[number];
    }
    
    private static final String[] allowed_pinyin={
        "a", "ba", "pa", "ma", "fa", "da", "ta", "na", "la", "za", "ca", "sa",
        "zha", "cha", "sha", "ga", "ka", "ha", "o", "bo", "po", "mo", "fo",
        "me", "de", "te", "ne", "le", "ze", "ce", "se", "zhe", "che", "she",
        "re", "ge", "ke", "he", "zi", "ci", "si", "zhi", "chi", "shi", "ri",
        "er", "r", "ai", "bai", "pai", "mai", "dai", "tai", "nai", "lai",
        "zai", "cai", "sai", "zhai", "chai", "shai", "gai", "kai", "hai",
        "ei", "bei", "pei", "mei", "fei", "dei", "nei", "lei", "zei", "zhei",
        "shei", "gei", "kei", "hei", "ao", "bao", "pao", "mao", "dao", "tao", 
        "nao", "lao", "zao", "cao", "sao", "zhao", "chao", "shao", "rao", 
        "gao", "kao", "hao", "ou", "pou", "mou", "fou", "dou", "tou", "nou",
        "lou", "zou", "cou", "sou", "zhou", "chou", "shou", "rou", "gou",
        "kou", "hou", "an", "ban", "pan", "man", "fan", "dan", "tan", "nan",
        "lan", "zan", "can", "san", "zhan", "chan", "shan", "ran", "gan", 
        "kan", "han", "en", "ben", "pen", "men", "fen", "den", "nen", "zen",
        "cen", "sen", "zhen", "chen", "shen", "ren", "gen", "ken", "hen",
        "ang", "bang", "pang", "mang", "fang", "dang", "tang", "nang", "lang",
        "zang", "cang", "sang", "zhang", "chang", "shang", "rang", "gang",
        "kang", "hang", "eng", "beng", "peng", "meng", "feng", "deng", "teng",
        "neng", "leng", "zeng", "ceng", "seng", "zheng", "cheng", "sheng",
        "reng", "geng", "keng", "heng", "dong", "tong", "nong", "long", "zong",
        "cong", "song", "zhong", "chong", "rong", "gong", "kong", "hong", "yi",
        "bi", "pi", "mi", "di", "ti", "ni", "li", "ji", "qi", "xi", "ya", "lia",
        "jia", "qia", "xia", "yao", "biao", "piao", "miao", "diao", "tiao",
        "niao", "liao", "jiao", "qiao", "xiao", "ye", "bie", "pie", "mie",
        "die", "tie", "nie", "lie", "jie", "qie", "xie", "you", "miu", "diu",
        "niu", "liu", "jiu", "qiu", "xiu", "yan", "bian", "pian", "mian", 
        "dian", "tian", "nian", "lian", "jian", "qian", "xian", "yin", "bin",
        "pin", "min", "nin", "lin", "jin", "qin", "xin", "yang", "niang", 
        "liang", "jiang", "qiang", "xiang", "ying", "bing", "ping", "ming",
        "ding", "ting", "ning", "ling", "jing", "qing", "xing", "yong", 
        "jiong", "qiong", "xiong", "wu", "bu", "pu", "mu", "fu", "du", "tu",
        "nu", "lu", "zu", "cu", "su", "zhu", "chu", "shu", "ru", "gu", "ku", 
        "hu", "wa", "zhua", "chua", "shua", "rua", "gua", "kua", "hua", "wo", 
        "duo", "tuo", "nuo", "luo", "zuo", "cuo", "suo", "zhuo", "chuo", 
        "shuo", "ruo", "guo", "kuo", "huo", "wo", "duo", "tuo", "nuo", "luo",
        "zuo", "cuo", "suo", "zhuo", "chuo", "shuo", "ruo", "guo", "kuo", 
        "huo", "wai", "zhuai", "chuai", "shuai", "guai", "kuai", "huai", "wei",
        "dui", "tui", "zui", "cui", "sui", "zhui", "chui", "shui", "rui", "gui",
        "kui", "hui", "wan", "duan", "tuan", "nuan", "luan", "zuan", "cuan",
        "suan", "zhuan", "chuan", "shuan", "ruan", "guan", "kuan", "huan", 
        "wen", "dun", "tun", "lun", "zun", "cun", "sun", "zhun", "chun", "shun",
        "run", "gun", "kun", "hun", "wang", "zhuang", "chuang", "shuang", 
        "guang", "kuang", "huang", "weng", "yu", "nv", "lv", "ju", "qu", "xu",
        "yue", "nve", "lve", "jue", "que", "xue", "yuan", "juan", "quan", 
        "xuan", "yun", "jun", "qun", "xun"};
}
