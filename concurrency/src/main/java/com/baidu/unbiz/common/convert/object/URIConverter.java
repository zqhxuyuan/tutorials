package com.baidu.unbiz.common.convert.object;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.baidu.unbiz.common.convert.ObjectConverter;
import com.baidu.unbiz.common.convert.ConvertException;
import com.baidu.unbiz.common.convert.TypeConverter;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月19日 下午7:20:55
 */
public class URIConverter extends ObjectConverter<URI> implements TypeConverter<URI> {

    public URIConverter() {
        register(URI.class);
    }

    @Override
    public URI toConvert(String value) {
        return convert(value);
    }

    @Override
    public String fromConvert(URI value) {
        return String.valueOf(value);
    }

    public URI toConvert(Object value) {
        if (value instanceof URI) {
            return (URI) value;
        }

        if (value instanceof File) {
            File file = (File) value;
            return file.toURI();
        }

        if (value instanceof URL) {
            URL url = (URL) value;
            try {
                return url.toURI();
            } catch (URISyntaxException e) {
                throw new ConvertException(value, e);
            }
        }

        return convert(value.toString());
    }

    private URI convert(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new ConvertException(value, e);
        }
    }

}
