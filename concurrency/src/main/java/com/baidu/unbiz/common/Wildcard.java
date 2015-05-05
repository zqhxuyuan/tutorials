package com.baidu.unbiz.common;

/**
 * 通配符相关的工具类。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月3日 上午4:54:36
 */
public abstract class Wildcard {

    public static boolean match(String string, String pattern) {
        return match(string, pattern, 0, 0);
    }

    public static boolean equalsOrMatch(String string, String pattern) {
        if (string == null) {
            return false;
        }

        if (string.equals(pattern)) {
            return true;
        }

        return match(string, pattern, 0, 0);
    }

    private static boolean match(String string, String pattern, int sNdx, int pNdx) {
        if (string == null || pattern == null) {
            return false;
        }
        int pLen = pattern.length();
        if (pLen == 1) {
            if (pattern.charAt(0) == '*') {
                return true;
            }
        }
        int sLen = string.length();
        boolean nextIsNotWildcard = false;

        while (true) {
            if (sNdx >= sLen) {
                while ((pNdx < pLen) && (pattern.charAt(pNdx) == '*')) {
                    pNdx++;
                }
                return pNdx >= pLen;
            }
            if (pNdx >= pLen) {
                return false;
            }
            char p = pattern.charAt(pNdx);

            if (!nextIsNotWildcard) {
                if (p == '\\') {
                    pNdx++;
                    nextIsNotWildcard = true;
                    continue;
                }
                if (p == '?') {
                    sNdx++;
                    pNdx++;
                    continue;
                }
                if (p == '*') {
                    char pNext = 0;
                    if (pNdx + 1 < pLen) {
                        pNext = pattern.charAt(pNdx + 1);
                    }
                    if (pNext == '*') {
                        pNdx++;
                        continue;
                    }
                    int i;
                    pNdx++;

                    for (i = string.length(); i >= sNdx; i--) {
                        if (match(string, pattern, i, pNdx)) {
                            return true;
                        }
                    }
                    return false;
                }
            } else {
                nextIsNotWildcard = false;
            }

            if (p != string.charAt(sNdx)) {
                return false;
            }

            sNdx++;
            pNdx++;
        }
    }

    public static int matchOne(String src, String[] patterns) {
        if (patterns == null) {
            return -1;
        }

        for (int i = 0; i < patterns.length; i++) {
            if (match(src, patterns[i])) {
                return i;
            }
        }
        return -1;
    }

    public static int matchPathOne(String path, String[] patterns) {
        if (patterns == null) {
            return -1;
        }

        for (int i = 0; i < patterns.length; i++) {
            if (matchPath(path, patterns[i])) {
                return i;
            }
        }
        return -1;
    }

    protected static final String PATH_MATCH = "**";
    protected static final String PATH_SEPARATORS = "/\\";

    public static boolean matchPath(String path, String pattern) {
        String[] pathElements = StringUtil.split(path, PATH_SEPARATORS);
        String[] patternElements = StringUtil.split(pattern, PATH_SEPARATORS);
        return matchTokens(pathElements, patternElements);
    }

    private static boolean matchTokens(String[] tokens, String[] patterns) {
        if (tokens == null || patterns == null) {
            return false;
        }
        int patNdxStart = 0;
        int patNdxEnd = patterns.length - 1;
        int tokNdxStart = 0;
        int tokNdxEnd = tokens.length - 1;

        while ((patNdxStart <= patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {
            String patDir = patterns[patNdxStart];
            if (patDir.equals(PATH_MATCH)) {
                break;
            }
            if (!match(tokens[tokNdxStart], patDir)) {
                return false;
            }
            patNdxStart++;
            tokNdxStart++;
        }
        if (tokNdxStart > tokNdxEnd) {
            for (int i = patNdxStart; i <= patNdxEnd; i++) {
                if (!patterns[i].equals(PATH_MATCH)) {
                    return false;
                }
            }
            return true;
        }
        if (patNdxStart > patNdxEnd) {
            return false;
        }

        while ((patNdxStart <= patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {

            String patDir = patterns[patNdxEnd];
            if (patDir.equals(PATH_MATCH)) {
                break;
            }
            if (!match(tokens[tokNdxEnd], patDir)) {
                return false;
            }
            patNdxEnd--;
            tokNdxEnd--;
        }
        if (tokNdxStart > tokNdxEnd) {
            for (int i = patNdxStart; i <= patNdxEnd; i++) {
                if (!patterns[i].equals(PATH_MATCH)) {
                    return false;
                }
            }
            return true;
        }

        while ((patNdxStart != patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {
            int patIdxTmp = -1;
            for (int i = patNdxStart + 1; i <= patNdxEnd; i++) {
                if (patterns[i].equals(PATH_MATCH)) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patNdxStart + 1) {
                patNdxStart++;
                continue;
            }

            int patLength = (patIdxTmp - patNdxStart - 1);
            int strLength = (tokNdxEnd - tokNdxStart + 1);
            int ndx = -1;
            strLoop: for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = patterns[patNdxStart + j + 1];
                    String subStr = tokens[tokNdxStart + i + j];
                    if (!match(subStr, subPat)) {
                        continue strLoop;
                    }
                }

                ndx = tokNdxStart + i;
                break;
            }

            if (ndx == -1) {
                return false;
            }

            patNdxStart = patIdxTmp;
            tokNdxStart = ndx + patLength;
        }

        for (int i = patNdxStart; i <= patNdxEnd; i++) {
            if (!patterns[i].equals(PATH_MATCH)) {
                return false;
            }
        }

        return true;
    }
}
