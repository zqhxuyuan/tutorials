/**
 * 
 */
package com.baidu.unbiz.common.file;

import java.util.List;

import com.baidu.unbiz.common.collection.ListMap;

/**
 * FIXME 感觉是北斗报表的产物
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月8日 上午1:43:57
 */
public class FileBeanInfo {

    private ListMap<String, String> headers;

    private Object[] beans;

    private List<String> fields;

    private List<String> titles;

    public ListMap<String, String> getHeaders() {
        return headers;
    }

    public FileBeanInfo setHeaders(ListMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Object[] getBeans() {
        return beans;
    }

    public FileBeanInfo setBeans(Object[] beans) {
        this.beans = beans;
        return this;
    }

    public List<String> getFields() {
        return fields;
    }

    public FileBeanInfo setFields(List<String> fields) {
        this.fields = fields;
        return this;
    }

    public List<String> getTitles() {
        return titles;
    }

    public FileBeanInfo setTitles(List<String> titles) {
        this.titles = titles;
        return this;
    }

}
