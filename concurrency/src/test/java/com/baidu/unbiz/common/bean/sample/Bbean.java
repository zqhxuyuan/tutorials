package com.baidu.unbiz.common.bean.sample;

public class Bbean extends Abean {

    public static final long serialVersionUID = 42L;

    private Long boo;

    Long getBoo() {
        return boo;
    }

    void setBoo(Long boo) {
        this.boo = boo;
    }
}
