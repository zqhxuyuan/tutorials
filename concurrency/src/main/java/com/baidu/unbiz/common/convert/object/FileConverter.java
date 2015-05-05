package com.baidu.unbiz.common.convert.object;

/**
 * 
 */

import java.io.File;
import java.io.IOException;

import com.baidu.unbiz.common.FileUtil;
import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.convert.ConvertException;
import com.baidu.unbiz.common.convert.TypeConverter;
import com.baidu.unbiz.common.io.StreamUtil;
import com.baidu.unbiz.common.io.WriterUtil;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 下午3:24:16
 */
public class FileConverter extends ObjectConverter<File> implements TypeConverter<File> {

    public FileConverter() {
        register(File.class);
    }

    @Override
    public File toConvert(String value) {
        return convert(value);
    }

    @Override
    public String fromConvert(File value) {
        return value.getName();
    }

    public File toConvert(Object value) {
        if (value instanceof File) {
            return (File) value;
        }

        Class<?> type = value.getClass();
        if (type == byte[].class) {
            try {
                File tempFile = FileUtil.createTempFile();
                StreamUtil.writeBytes((byte[]) value, tempFile, true);

                return tempFile;
            } catch (IOException e) {
                throw new ConvertException(e);
            }
        }
        if (type == String.class) {
            return convert(value.toString());
        }

        throw new ConvertException(value);
    }

    private File convert(String value) {
        try {
            File tempFile = FileUtil.createTempFile();
            WriterUtil.writeLinesAndClose(tempFile, value);
            return tempFile;
        } catch (IOException e) {
            throw new ConvertException(e);
        }
    }

}
