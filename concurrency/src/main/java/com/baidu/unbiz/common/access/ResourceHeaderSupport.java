/**
 * 
 */
package com.baidu.unbiz.common.access;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.baidu.unbiz.common.CollectionUtil;

/**
 * support for <code>AccessHeader</code>
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月27日 上午4:07:00
 */
public class ResourceHeaderSupport implements ResourceHeader, Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -7084835063414563471L;

    protected final Map<String, String> info;

    protected String encode;

    protected String ext;

    public ResourceHeaderSupport() {
        info = CollectionUtil.createHashMap();
    }

    public ResourceHeaderSupport(Map<String, String> info) {
        this.info = info;
    }

    @Override
    public String encode() {
        return (encode == null) ? Charset.defaultCharset().name() : encode;
    }

    @Override
    public String ext() {
        return ext;
    }

    public ResourceHeader encode(String encode) {
        this.encode = encode;
        info.put("encode", encode);

        return this;
    }

    public ResourceHeader ext(String ext) {
        this.ext = ext;
        info.put("ext", ext);

        return this;
    }

    @Override
    public String getAttribute(String name) {
        return info.get(name);
    }

    @Override
    public ResourceHeader setAttribute(String name, String value) {
        info.put(name, value);

        return this;
    }

    @Override
    public Set<Map.Entry<String, String>> getInfo() {
        return Collections.unmodifiableSet(info.entrySet());
    }

}
