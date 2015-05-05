/**
 * 
 */
package com.baidu.unbiz.common.logger;

import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author <a href="mailto:xuc@iphonele.com">saga67</a>
 * 
 * @version create on 2013年8月13日 下午9:30:56
 */
class DefaultLogger implements Logger {

    private org.slf4j.Logger logger;

    DefaultLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        logger.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object...arguments) {
        logger.trace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object...argArray) {
        logger.trace(marker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object...arguments) {
        logger.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object...arguments) {
        logger.debug(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object...arguments) {
        logger.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object...arguments) {
        logger.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(format, arg);
    }

    @Override
    public void warn(String format, Object...arguments) {
        logger.warn(format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object...arguments) {
        logger.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object...arguments) {
        logger.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object...arguments) {
        logger.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(marker, msg, t);
    }

    @Override
    public void trace(Object bean) {
        logger.trace(String.valueOf(bean));
    }

    @Override
    public void info(Object bean) {
        logger.info(String.valueOf(bean));
    }

    @Override
    public void debug(Object bean) {
        logger.debug(String.valueOf(bean));
    }

    @Override
    public void warn(Object bean) {
        logger.warn(String.valueOf(bean));
    }

    @Override
    public void error(Object bean) {
        logger.error(String.valueOf(bean));
    }

    @Override
    public void traceIfEnabled(String msg) {
        if (logger.isTraceEnabled()) {
            logger.trace(msg);
        }

    }

    @Override
    public void traceIfEnabled(String format, Object...arguments) {
        if (logger.isTraceEnabled()) {
            logger.trace(format, arguments);
        }
    }

    @Override
    public void traceIfEnabled(String msg, Throwable t) {
        if (logger.isTraceEnabled()) {
            logger.trace(msg, t);
        }
    }

    @Override
    public void traceIfEnabled(Marker marker, String msg) {
        if (logger.isTraceEnabled()) {
            logger.trace(marker, msg);
        }
    }

    @Override
    public void traceIfEnabled(Marker marker, String format, Object...argArray) {
        if (logger.isTraceEnabled()) {
            logger.trace(marker, format, argArray);
        }

    }

    @Override
    public void traceIfEnabled(Marker marker, String msg, Throwable t) {
        if (logger.isTraceEnabled()) {
            logger.trace(marker, msg, t);
        }

    }

    @Override
    public void traceIfEnabled(Object bean) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(bean));
        }

    }

    @Override
    public void debugIfEnabled(String msg) {
        if (logger.isDebugEnabled()) {
            logger.debug(msg);
        }

    }

    @Override
    public void debugIfEnabled(String format, Object...arguments) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, arguments);
        }

    }

    @Override
    public void debugIfEnabled(String msg, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(msg, t);
        }

    }

    @Override
    public void debugIfEnabled(Marker marker, String msg) {
        if (logger.isDebugEnabled()) {
            logger.debug(marker, msg);
        }

    }

    @Override
    public void debugIfEnabled(Marker marker, String format, Object...argArray) {
        if (logger.isDebugEnabled()) {
            logger.debug(marker, format, argArray);
        }

    }

    @Override
    public void debugIfEnabled(Marker marker, String msg, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(marker, msg, t);
        }

    }

    @Override
    public void debugIfEnabled(Object bean) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.valueOf(bean));
        }

    }

    @Override
    public void infoIfEnabled(String msg) {
        if (logger.isInfoEnabled()) {
            logger.info(msg);
        }

    }

    @Override
    public void infoIfEnabled(String format, Object...arguments) {
        if (logger.isInfoEnabled()) {
            logger.info(format, arguments);
        }

    }

    @Override
    public void infoIfEnabled(String msg, Throwable t) {
        if (logger.isInfoEnabled()) {
            logger.info(msg, t);
        }

    }

    @Override
    public void infoIfEnabled(Marker marker, String msg) {
        if (logger.isInfoEnabled()) {
            logger.info(marker, msg);
        }

    }

    @Override
    public void infoIfEnabled(Marker marker, String format, Object...argArray) {
        if (logger.isInfoEnabled()) {
            logger.info(marker, format, argArray);
        }

    }

    @Override
    public void infoIfEnabled(Marker marker, String msg, Throwable t) {
        if (logger.isInfoEnabled()) {
            logger.info(marker, msg, t);
        }

    }

    @Override
    public void infoIfEnabled(Object bean) {
        if (logger.isInfoEnabled()) {
            logger.info(String.valueOf(bean));
        }

    }

    @Override
    public void warnIfEnabled(String msg) {
        if (logger.isWarnEnabled()) {
            logger.warn(msg);
        }

    }

    @Override
    public void warnIfEnabled(String format, Object...arguments) {
        if (logger.isWarnEnabled()) {
            logger.warn(format, arguments);
        }

    }

    @Override
    public void warnIfEnabled(String msg, Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(msg, t);
        }

    }

    @Override
    public void warnIfEnabled(Marker marker, String msg) {
        if (logger.isWarnEnabled()) {
            logger.warn(marker, msg);
        }

    }

    @Override
    public void warnIfEnabled(Marker marker, String format, Object...argArray) {
        if (logger.isWarnEnabled()) {
            logger.warn(marker, format, argArray);
        }

    }

    @Override
    public void warnIfEnabled(Marker marker, String msg, Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn(marker, msg, t);
        }

    }

    @Override
    public void warnIfEnabled(Object bean) {
        if (logger.isWarnEnabled()) {
            logger.warn(String.valueOf(bean));
        }

    }

    @Override
    public void errorIfEnabled(String msg) {
        if (logger.isErrorEnabled()) {
            logger.error(msg);
        }

    }

    @Override
    public void errorIfEnabled(String format, Object...arguments) {
        if (logger.isErrorEnabled()) {
            logger.error(format, arguments);
        }

    }

    @Override
    public void errorIfEnabled(String msg, Throwable t) {
        if (logger.isErrorEnabled()) {
            logger.error(msg, t);
        }

    }

    @Override
    public void errorIfEnabled(Marker marker, String msg) {
        if (logger.isErrorEnabled()) {
            logger.error(marker, msg);
        }

    }

    @Override
    public void errorIfEnabled(Marker marker, String format, Object...argArray) {
        if (logger.isErrorEnabled()) {
            logger.error(marker, format, argArray);
        }

    }

    @Override
    public void errorIfEnabled(Marker marker, String msg, Throwable t) {
        if (logger.isErrorEnabled()) {
            logger.error(marker, msg, t);
        }

    }

    @Override
    public void errorIfEnabled(Object bean) {
        if (logger.isErrorEnabled()) {
            logger.error(String.valueOf(bean));
        }

    }

    @Override
    public void error(String msg, Throwable t, Object...arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(msg, arguments);

        logger.error(ft.getMessage(), ft.getThrowable());
    }

}
