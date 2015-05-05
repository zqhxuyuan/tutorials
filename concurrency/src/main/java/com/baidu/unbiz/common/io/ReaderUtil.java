/**
 * 
 */
package com.baidu.unbiz.common.io;

import static com.baidu.unbiz.common.StringPool.Charset.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.StringUtil;
import com.baidu.unbiz.common.able.Transformer;

/**
 * FIXME 编码格式
 * <p>
 * 有关<code>Reader</code>的工具类。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月27日 下午12:34:29
 */
public abstract class ReaderUtil {

    /**
     * 获取所有行内容并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String> readLinesAndClose(InputStream inputStream) throws IOException {
        return readLinesAndClose(UTF_8, inputStream);
    }

    public static <T> List<T> readLinesAndClose(InputStream inputStream, Transformer<List<String>, List<T>> transformer)
            throws IOException {
        List<String> lines = readLinesAndClose(UTF_8, inputStream);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param filePath 文件路径
     * @param charset 编码格式
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String> readLinesAndClose(String filePath, Charset charset) throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return readLinesAndClose(charset.name(), inputStream);
    }

    public static <T> List<T> readLinesAndClose(String filePath, Charset charset,
            Transformer<List<String>, List<T>> transformer) throws IOException {
        List<String> lines = readLinesAndClose(filePath, charset);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String> readLinesAndClose(String charsetName, InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        BufferedReader reader = getBufferedReader(inputStream, charsetName);
        List<String> result = CollectionUtil.createArrayList();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.add(line.trim());
            }
            return result;
        } finally {
            StreamUtil.close(reader);
        }
    }

    public static <T> List<T> readLinesAndClose(String charsetName, InputStream inputStream,
            Transformer<List<String>, List<T>> transformer) throws IOException {
        List<String> lines = readLinesAndClose(charsetName, inputStream);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @param separatorChars 分隔符集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String[]> readLinesAndClose(InputStream inputStream, String separatorChars) throws IOException {
        return readLinesAndClose(inputStream, separatorChars, UTF_8);
    }

    public static <T> List<T> readLinesAndClose(InputStream inputStream, String separatorChars,
            Transformer<List<String[]>, List<T>> transformer) throws IOException {
        List<String[]> lines = readLinesAndClose(inputStream, separatorChars);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @param separatorChars 分隔符集
     * @param charsetName 编码格式
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String[]> readLinesAndClose(InputStream inputStream, String separatorChars, String charsetName)
            throws IOException {
        if (inputStream == null) {
            return null;
        }

        BufferedReader reader = getBufferedReader(inputStream, charsetName);
        List<String[]> result = CollectionUtil.createArrayList();
        try {
            String[] strings;
            String line = null;
            while ((line = reader.readLine()) != null) {
                strings = StringUtil.split(line.trim(), separatorChars);
                result.add(strings);
            }
            return result;
        } finally {
            StreamUtil.close(reader);
        }
    }

    public static <T> List<T> readLinesAndClose(InputStream inputStream, String separatorChars, String charsetName,
            Transformer<List<String[]>, List<T>> transformer) throws IOException {
        List<String[]> lines = readLinesAndClose(inputStream, separatorChars, charsetName);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @param separatorChars 分隔符集
     * @param charsetName 编码格式
     * @param keys <code>Map</code>的key集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<Map<String, String>> readLinesAndClose(InputStream inputStream, String separatorChars,
            String[] keys) throws IOException {
        return readLinesAndClose(inputStream, separatorChars, UTF_8, keys);
    }

    public static <T> List<T> readLinesAndClose(InputStream inputStream, String separatorChars, String[] keys,
            Transformer<List<Map<String, String>>, List<T>> transformer) throws IOException {
        List<Map<String, String>> lines = readLinesAndClose(inputStream, separatorChars, keys);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @param separatorChars 分隔符集
     * @param charsetName 编码格式
     * @param keys <code>Map</code>的key集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<Map<String, String>> readLinesAndClose(InputStream inputStream, String separatorChars,
            String charsetName, String[] keys) throws IOException {
        if (inputStream == null || keys == null) {
            return null;
        }

        BufferedReader reader = getBufferedReader(inputStream, charsetName);
        List<Map<String, String>> result = CollectionUtil.createArrayList();
        try {
            String[] strings;
            Map<String, String> map;
            int length = keys.length;
            String line = null;
            while ((line = reader.readLine()) != null) {
                strings = StringUtil.split(line.trim(), separatorChars);
                map = CollectionUtil.createHashMap();
                for (int i = 0; i < length; i++) {
                    map.put(keys[i], strings[i]);
                }
                result.add(map);
            }
            return result;
        } finally {
            StreamUtil.close(reader);
        }
    }

    public static <T> List<T> readLinesAndClose(InputStream inputStream, String separatorChars, String charsetName,
            String[] keys, Transformer<List<Map<String, String>>, List<T>> transformer) throws IOException {
        List<Map<String, String>> lines = readLinesAndClose(inputStream, separatorChars, charsetName, keys);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param filePath 文件路径
     * @param separatorChars 分隔符集
     * @param keys <code>Map</code>的key集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<Map<String, String>> readLinesAndClose(String filePath, String separatorChars, String[] keys)
            throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return readLinesAndClose(inputStream, separatorChars, keys);
    }

    public static <T> List<T> readLinesAndClose(String filePath, String separatorChars, String[] keys,
            Transformer<List<Map<String, String>>, List<T>> transformer) throws IOException {
        List<Map<String, String>> lines = readLinesAndClose(filePath, separatorChars, keys);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param file 文件对象 @see File
     * @param separatorChars 分隔符集
     * @param keys <code>Map</code>的key集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<Map<String, String>> readLinesAndClose(File file, String separatorChars, String[] keys)
            throws IOException {
        InputStream inputStream = getInputStream(file);

        return readLinesAndClose(inputStream, separatorChars, keys);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param filePath 文件路径
     * @param separatorChars 分隔符集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String[]> readLinesAndClose(String filePath, String separatorChars) throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return readLinesAndClose(inputStream, separatorChars);
    }

    public static <T> List<T> readLinesAndClose(String filePath, String separatorChars,
            Transformer<List<String[]>, List<T>> transformer) throws IOException {
        List<String[]> lines = readLinesAndClose(filePath, separatorChars);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param filePath 文件路径
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String> readLinesAndClose(String filePath) throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return readLinesAndClose(inputStream);
    }

    public static <T> List<T> readLinesAndClose(String filePath, Transformer<List<String>, List<T>> transformer)
            throws IOException {
        List<String> lines = readLinesAndClose(filePath);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param file 文件对象 @see File
     * @param separatorChars 分隔符集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String[]> readLinesAndClose(File file, String separatorChars) throws IOException {
        InputStream inputStream = getInputStream(file);

        return readLinesAndClose(inputStream, separatorChars);
    }

    public static <T> List<T> readLinesAndClose(File file, String separatorChars,
            Transformer<List<String[]>, List<T>> transformer) throws IOException {
        List<String[]> lines = readLinesAndClose(file, separatorChars);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param file 文件对象 @see File
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String> readLinesAndClose(File file) throws IOException {
        InputStream inputStream = getInputStream(file);

        return readLinesAndClose(inputStream);
    }

    public static <T> List<T> readLinesAndClose(File file, Transformer<List<String>, List<T>> transformer)
            throws IOException {
        List<String> lines = readLinesAndClose(file);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param charsetName 编码格式
     * @param file 文件对象 @see File
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String> readLinesAndClose(String charsetName, File file) throws IOException {
        InputStream inputStream = getInputStream(file);

        return readLinesAndClose(charsetName, inputStream);
    }

    public static <T> List<T> readLinesAndClose(String charsetName, File file,
            Transformer<List<String>, List<T>> transformer) throws IOException {
        List<String> lines = readLinesAndClose(charsetName, file);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取总记录数并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @return 总记录数
     * @throws IOException
     */
    public static int getCountAndClose(InputStream inputStream) throws IOException {
        return getCountAndClose(inputStream, UTF_8);
    }

    /**
     * 获取总记录数并关闭流
     * 
     * @param inputStream 输入流 @see InputStream
     * @param charsetName 编码格式
     * @return 总记录数
     * @throws IOException
     */
    public static int getCountAndClose(InputStream inputStream, String charsetName) throws IOException {
        if (inputStream == null) {
            return 0;
        }

        int count = 0;
        BufferedReader reader = getBufferedReader(inputStream, charsetName);
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                // 忽略空行
                if (StringUtil.isNotBlank(line))
                    count++;
            }
            return count;
        } finally {
            StreamUtil.close(reader);
        }
    }

    /**
     * 获取总记录数并关闭流
     * 
     * @param filePath 文件路径
     * @return 总记录数
     * @throws IOException
     */
    public static int getCountAndClose(String filePath) throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return getCountAndClose(inputStream);
    }

    /**
     * 获取总记录数并关闭流
     * 
     * @param file 文件对象 @see File
     * @return 总记录数
     * @throws IOException
     */
    public static int getCountAndClose(File file) throws IOException {
        InputStream inputStream = getInputStream(file);

        return getCountAndClose(inputStream);
    }

    /**
     * 获取输入流
     * 
     * @param filePath 文件路径
     * @return <code>InputStream</code> 如果找不到。
     * @throws FileNotFoundException
     */
    private static InputStream getInputStream(String filePath) throws FileNotFoundException {
        return new FileInputStream(filePath);
    }

    /**
     * 获取输入流
     * 
     * @param file 文件对象 @see File
     * @return <code>InputStream</code> 如果<code>file</code>为<code>null</code>，返回 <code>null</code>
     * @throws FileNotFoundException
     */
    private static InputStream getInputStream(File file) throws FileNotFoundException {
        if (file == null) {
            return null;
        }

        return new FileInputStream(file);
    }

    /**
     * 获取<code>BufferedReader</code>
     * 
     * @param inputStream 输入流 @see InputStream
     * @param charsetName 编码格式
     * @return <code>BufferedReader</code>
     * @throws UnsupportedEncodingException
     */
    private static BufferedReader getBufferedReader(InputStream inputStream, String charsetName)
            throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(inputStream, charsetName));
    }

    // /**
    // * 获取<code>BufferedReader</code>
    // *
    // * @param inputStream
    // * 输入流 @see InputStream
    // * @param charset
    // * 编码格式
    // * @return <code>BufferedReader</code>
    // * @throws UnsupportedEncodingException
    // */
    // private static BufferedReader getBufferedReader(InputStream inputStream,
    // Charset charset) throws UnsupportedEncodingException {
    // return new BufferedReader(new InputStreamReader(inputStream, charset));
    // }

    /** 获取所有行内容将其转为<code>Map</code>并关闭流 */
    public static Map<String, String> read4MapAndClose(InputStream inputStream, String separatorChars)
            throws IOException {
        return read4MapAndClose(inputStream, separatorChars, UTF_8);
    }

    public static <T> T read4MapAndClose(InputStream inputStream, String separatorChars,
            Transformer<Map<String, String>, T> transformer) throws IOException {
        Map<String, String> lines = read4MapAndClose(inputStream, separatorChars);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /** 获取所有行内容将其转为<code>Map</code>并关闭流 */
    public static Map<String, String> read4MapAndClose(InputStream inputStream, String separatorChars,
            String charsetName) throws IOException {
        if (inputStream == null) {
            return null;
        }

        if (separatorChars == null) {
            separatorChars = " ";
        }

        BufferedReader reader = getBufferedReader(inputStream, charsetName);
        Map<String, String> result = CollectionUtil.createHashMap();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                // 忽略空行
                String[] strings = StringUtil.split(line.trim(), separatorChars);
                if (strings.length == 2) {
                    result.put(strings[0], strings[1]);
                }

            }
            return result;
        } finally {
            StreamUtil.close(reader);
        }

    }

    public static <T> T read4MapAndClose(InputStream inputStream, String separatorChars, String charsetName,
            Transformer<Map<String, String>, T> transformer) throws IOException {
        Map<String, String> lines = read4MapAndClose(inputStream, separatorChars, charsetName);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /** 获取所有行内容将其转为<code>Map</code>并关闭流 */
    public static Map<String, String> read4MapAndClose(String filePath, String separatorChars) throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return read4MapAndClose(inputStream, separatorChars);
    }

    public static <T> T read4MapAndClose(String filePath, String separatorChars, String charsetName,
            Transformer<Map<String, String>, T> transformer) throws IOException {
        Map<String, String> lines = read4MapAndClose(filePath, separatorChars);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /** 获取所有行内容将其转为<code>Map</code>并关闭流 */
    public static Map<String, String> read4MapAndClose(String filePath, String separatorChars, String charsetName)
            throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return read4MapAndClose(inputStream, separatorChars, charsetName);
    }

    /** 获取所有行内容将其转为<code>Map</code>并关闭流 */
    public static Map<String, String> read4MapAndClose(File file, String separatorChars) throws IOException {
        InputStream inputStream = getInputStream(file);

        return read4MapAndClose(inputStream, separatorChars);
    }

    /** 获取所有行内容将其转为<code>Map</code>并关闭流 */
    public static Map<String, String> read4MapAndClose(File file, String separatorChars, String charsetName)
            throws IOException {
        InputStream inputStream = getInputStream(file);

        return read4MapAndClose(inputStream, separatorChars, charsetName);
    }

    public static <T> T read4MapAndClose(File file, String separatorChars, String charsetName,
            Transformer<Map<String, String>, T> transformer) throws IOException {
        Map<String, String> lines = read4MapAndClose(file, separatorChars, charsetName);
        if (CollectionUtil.isEmpty(lines)) {
            return null;
        }

        return transformer.transform(lines);
    }

    /**
     * 获取所有行内容并关闭流
     * 
     * @param filePath 文件路径
     * @param separatorChars 分隔符集
     * @return 数据集合 @see List
     * @throws IOException
     */
    public static List<String[]> read4ArrayAndClose(String filePath, String separatorChars) throws IOException {
        InputStream inputStream = getInputStream(filePath);

        return readLinesAndClose(inputStream, separatorChars);
    }

    public static <T> List<T> read4ArrayAndClose(String filePath, String separatorChars,
            Transformer<List<String[]>, List<T>> transformer) throws IOException {
        InputStream inputStream = getInputStream(filePath);

        List<String[]> lines = readLinesAndClose(inputStream, separatorChars);
        return transformer.transform(lines);
    }

}
