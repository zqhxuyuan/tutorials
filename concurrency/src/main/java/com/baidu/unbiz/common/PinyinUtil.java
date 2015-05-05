/**
 * 
 */
package com.baidu.unbiz.common;

import static com.baidu.unbiz.common.StringPool.Charset.GBK;

/**
 * 拼音字母工具类
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午7:14:35
 */
public abstract class PinyinUtil {

    /** 除了A-Z之外的字符标志 */
    private static final String otherType = "other_type";

    private final static int compare(String str1, String str2) {
        String s1 = Emptys.EMPTY_STRING;
        String s2 = Emptys.EMPTY_STRING;

        try {
            s1 = new String(str1.getBytes(GBK), GBK);
            s2 = new String(str2.getBytes(GBK), GBK);
        } catch (Exception e) {
            return str1.compareTo(str2);
        }

        return chineseCompareTo(s1, s2);

    }

    public final static int chineseCompareTo(String string1, String string2) {
        if (StringUtil.isAllBlank(string1, string2)) {
            return 0;
        }

        if (StringUtil.isBlank(string1)) {
            return -1;
        }

        if (StringUtil.isBlank(string2)) {
            return 1;
        }

        int len1 = string1.length();
        int len2 = string2.length();
        int n = Math.min(len1, len2);

        for (int i = 0; i < n; i++) {
            int s1Code = getCharCode(Character.toString(string1.charAt(i)));
            int s2Code = getCharCode(Character.toString(string2.charAt(i)));

            if ((s1Code * s2Code) < 0) {
                return Math.min(s1Code, s2Code);
            }

            if (s1Code != s2Code) {
                return s1Code - s2Code;
            }
        }

        return len1 - len2;
    }

    private final static int getCharCode(String string) {
        byte[] bytes = string.getBytes();
        int value = 0;

        for (int i = 0; (i < bytes.length) && (i <= 2); i++)
            value = (value * 100) + bytes[i];

        return value;
    }

    /**
     * 得到一个字符串所有汉字的首字母,
     * <p>
     * 比如: getBeginCharacter("我爱北京天安门") = "wabjtam"
     * </p>
     * 
     * @param string 字符串
     * @return 汉字的首字母
     */
    public final static String getBeginCharacter(String string) {
        if (StringUtil.isBlank(string)) {
            return null;
        }

        String a = string;
        String result = Emptys.EMPTY_STRING;

        for (int i = 0; i < a.length(); i++) {
            String current = a.substring(i, i + 1);
            if (compare(current, "\u554A") < 0) {
                result = result + current;
            }
            // FIXME
            else if ((compare(current, "\u554A") >= 0) && (compare(current, "\u5EA7") <= 0)) {
                if (compare(current, "\u531D") >= 0) {
                    result = result + "z";
                } else if (compare(current, "\u538B") >= 0) {
                    result = result + "y";
                } else if (compare(current, "\u6614") >= 0) {
                    result = result + "x";
                } else if (compare(current, "\u6316") >= 0) {
                    result = result + "w";
                } else if (compare(current, "\u584C") >= 0) {
                    result = result + "t";
                } else if (compare(current, "\u6492") >= 0) {
                    result = result + "s";
                } else if (compare(current, "\u7136") >= 0) {
                    result = result + "r";
                } else if (compare(current, "\u671F") >= 0) {
                    result = result + "q";
                } else if (compare(current, "\u556A") >= 0) {
                    result = result + "p";
                } else if (compare(current, "\u54E6") >= 0) {
                    result = result + "o";
                } else if (compare(current, "\u62FF") >= 0) {
                    result = result + "n";
                } else if (compare(current, "\u5988") >= 0) {
                    result = result + "m";
                } else if (compare(current, "\u5783") >= 0) {
                    result = result + "l";
                } else if (compare(current, "\u5580") >= 0) {
                    result = result + "k";
                } else if (compare(current, "\u51FB") > 0) {
                    result = result + "j";
                } else if (compare(current, "\u54C8") >= 0) {
                    result = result + "h";
                } else if (compare(current, "\u5676") >= 0) {
                    result = result + "g";
                } else if (compare(current, "\u53D1") >= 0) {
                    result = result + "f";
                } else if (compare(current, "\u86FE") >= 0) {
                    result = result + "e";
                } else if (compare(current, "\u642D") >= 0) {
                    result = result + "d";
                } else if (compare(current, "\u64E6") >= 0) {
                    result = result + "c";
                } else if (compare(current, "\u82AD") >= 0) {
                    result = result + "b";
                } else if (compare(current, "\u554A") >= 0) {
                    result = result + "a";
                }
            }
        }

        if (result.length() <= 0) {
            result = otherType;
        }

        return result;
    }

    /**
     * 得到字符串首字符的拼音首字母 如果首字符是数字
     * <p>
     * 返回 OTHER_TYPE,如果英文字母，则返回字母的小写
     * 
     * @param string 普通字符串
     * @return 拼音首字母或OTHER_TYPE
     */
    public final static String getFirstSpell(String string) {
        String result = null;

        if (StringUtil.isNotBlank(string)) {
            char a = string.charAt(0);

            if (Character.isDigit(a)) {
                result = otherType;
            } else if (((a >= 'a') && (a <= 'z')) || ((a >= 'A') && (a <= 'Z'))) {
                result = Character.toString(a).toLowerCase();
            } else {
                result = getBeginCharacter(Character.toString(a));
            }
        }

        return result;
    }

}
