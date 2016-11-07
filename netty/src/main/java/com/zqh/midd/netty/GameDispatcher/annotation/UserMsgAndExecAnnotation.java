package com.zqh.midd.netty.GameDispatcher.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰消息类和业务逻辑执行类
 * msgType指定对应的类型，从1开始计数
 * @author xingchencheng
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserMsgAndExecAnnotation {
	short msgType();
}