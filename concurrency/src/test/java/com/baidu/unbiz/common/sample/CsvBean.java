/**
 * 
 */
package com.baidu.unbiz.common.sample;

import com.baidu.unbiz.common.apache.ToStringBuilder;
import com.baidu.unbiz.common.apache.ToStringStyle;
import com.baidu.unbiz.common.file.ProcessedField;
import com.baidu.unbiz.common.file.ProcessedType;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年8月1日 下午2:24:55
 */
@ProcessedType
public class CsvBean {

    @ProcessedField(index = 0, title = "姓名")
    private String name;

    @ProcessedField(index = 1, title = "年龄")
    private int age;

    @ProcessedField(index = 2, title = "工作")
    private String work;

    @ProcessedField(index = 3, title = "住址")
    private String home;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // 到此处类型必相同
        CsvBean other = (CsvBean) obj;
        return this.toString().equals(other.toString());
    }

}
