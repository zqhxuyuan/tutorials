/**
 * 
 */
package com.baidu.unbiz.common.logger;

import org.slf4j.Marker;

/**
 * 增加对象的直接打印，方便编码
 * 
 * @author <a href="mailto:xuc@iphonele.com">saga67</a>
 * 
 * @version create on 2013年8月13日 下午9:29:13
 */
public interface Logger extends org.slf4j.Logger {

    // ==========================================================================
    // 打印对象。
    // ==========================================================================
    void trace(Object bean);

    void info(Object bean);

    void debug(Object bean);

    void warn(Object bean);

    void error(Object bean);

    void error(String msg, Throwable t, Object...arguments);

    // ==========================================================================
    // if enabled,then logging。
    // ==========================================================================

    // ============ trace begin =============== //
    void traceIfEnabled(String msg);

    void traceIfEnabled(String format, Object...arguments);

    void traceIfEnabled(String msg, Throwable t);

    void traceIfEnabled(Marker marker, String msg);

    public void traceIfEnabled(Marker marker, String format, Object...argArray);

    void traceIfEnabled(Marker marker, String msg, Throwable t);

    void traceIfEnabled(Object bean);

    // ============ trace end =============== //

    // ============ debug begin =============== //
    void debugIfEnabled(String msg);

    void debugIfEnabled(String format, Object...arguments);

    void debugIfEnabled(String msg, Throwable t);

    void debugIfEnabled(Marker marker, String msg);

    public void debugIfEnabled(Marker marker, String format, Object...argArray);

    void debugIfEnabled(Marker marker, String msg, Throwable t);

    void debugIfEnabled(Object bean);

    // ============ debug end =============== //

    // ============ info begin =============== //
    void infoIfEnabled(String msg);

    void infoIfEnabled(String format, Object...arguments);

    void infoIfEnabled(String msg, Throwable t);

    void infoIfEnabled(Marker marker, String msg);

    public void infoIfEnabled(Marker marker, String format, Object...argArray);

    void infoIfEnabled(Marker marker, String msg, Throwable t);

    void infoIfEnabled(Object bean);

    // ============ info end =============== //

    // ============ warn begin =============== //
    void warnIfEnabled(String msg);

    void warnIfEnabled(String format, Object...arguments);

    void warnIfEnabled(String msg, Throwable t);

    void warnIfEnabled(Marker marker, String msg);

    public void warnIfEnabled(Marker marker, String format, Object...argArray);

    void warnIfEnabled(Marker marker, String msg, Throwable t);

    void warnIfEnabled(Object bean);

    // ============ warn end =============== //

    // ============ error begin =============== //
    void errorIfEnabled(String msg);

    void errorIfEnabled(String format, Object...arguments);

    void errorIfEnabled(String msg, Throwable t);

    void errorIfEnabled(Marker marker, String msg);

    public void errorIfEnabled(Marker marker, String format, Object...argArray);

    void errorIfEnabled(Marker marker, String msg, Throwable t);

    void errorIfEnabled(Object bean);
    // ============ error end =============== //
}
