/**
 * adx-common#com.baidu.ub.generic.bo.BaseObject.java
 * 下午6:46:03 created by Darwin(Tianxin)
 */
package com.baidu.unbiz.common.bo;

import java.io.Serializable;

/**
 * 使用GenericDao做映射的对象，必须都继承与这个类
 * 
 * @author Darwin(Tianxin)
 */
public class BaseObject<KEY extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    public BaseObject() {
    }

    /**
     * 主键字段
     */
    protected KEY id;

    public KEY getId() {
        return id;
    }

    public void setId(KEY id) {
        this.id = id;
    }

}
