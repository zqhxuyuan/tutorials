/**
 * 
 */
package com.baidu.unbiz.common.diagnostic;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.Emptys;
import com.baidu.unbiz.common.ObjectUtil;
import com.baidu.unbiz.common.StringUtil;

/**
 * 用来测试并统计线程执行时间的工具。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月15日 下午2:48:00
 */
public final class Profiler {
    private static final ThreadLocal<Entry> entryStack = new ThreadLocal<Entry>();

    /**
     * 开始一个新的entry，并计时，如果当前存entry，则纳入当前entry的子entry。
     */
    public static void start() {
        start((String) null);
    }

    /**
     * 开始一个新的entry，并计时，如果当前存entry，则纳入当前entry的子entry。
     * 
     * @param message 计时信息
     */
    public static void start(String message) {
        Entry currentEntry = getCurrentEntry();

        if (currentEntry != null) {
            currentEntry.enterSubEntry(message);
        } else {
            entryStack.set(new Entry(message, null, null));
        }
    }

    /**
     * 开始一个新的entry，并计时，如果当前存entry，则纳入当前entry的子entry。
     * 
     * @param message 计时信息
     */
    public static void start(Message message) {
        Entry currentEntry = getCurrentEntry();

        if (currentEntry != null) {
            currentEntry.enterSubEntry(message);
        } else {
            entryStack.set(new Entry(message, null, null));
        }
    }

    /**
     * 清除计时器。
     * 
     * <p>
     * 清除以后必须再次调用<code>start</code>方可重新计时。
     * </p>
     */
    public static void reset() {
        entryStack.set(null);
    }

    /**
     * 结束最近的一个entry，记录结束时间。
     */
    public static void release() {
        Entry currentEntry = getCurrentEntry();

        if (currentEntry != null) {
            currentEntry.release();
        }
    }

    /**
     * 取得耗费的总时间。
     * 
     * @return 耗费的总时间，如果未开始计时，则返回<code>-1</code>
     */
    public static long getDuration() {
        Entry entry = entryStack.get();

        if (entry != null) {
            return entry.getDuration();
        }
        return -1;
    }

    /**
     * 列出所有的entry。
     * 
     * @return 列出所有entry，并统计各自所占用的时间
     */
    public static String dump() {
        return dump("", "");
    }

    /**
     * 列出所有的entry。
     * 
     * @param prefix 前缀
     * 
     * @return 列出所有entry，并统计各自所占用的时间
     */
    public static String dump(String prefix) {
        return dump(prefix, prefix);
    }

    /**
     * 列出所有的entry。
     * 
     * @param prefix1 首行前缀
     * @param prefix2 后续行前缀
     * 
     * @return 列出所有entry，并统计各自所占用的时间
     */
    public static String dump(String prefix1, String prefix2) {
        Entry entry = entryStack.get();

        if (entry != null) {
            return entry.toString(prefix1, prefix2);
        }
        return Emptys.EMPTY_STRING;
    }

    public static boolean isStart() {
        return entryStack.get() != null;
    }

    public static boolean isEmpty() {
        return entryStack.get() == null;
    }

    public static boolean isEnd() {
        Entry firstEntry = entryStack.get();

        if (firstEntry == null) {
            return false;
        }
        Entry currentEntry = getCurrentEntry();

        return (currentEntry == firstEntry) && firstEntry.isReleased();
    }

    public static String dumpIfFirst(String prefix1, String prefix2) {
        Entry firstEntry = entryStack.get();
        if (firstEntry == null) {
            return null;
        }
        Entry currentEntry = getCurrentEntry();
        if (currentEntry == firstEntry) {
            String result = dump(prefix1, prefix2);
            reset();
            return result;

        }
        return null;
    }

    public static String dumpIfFirst(String prefix) {
        return dumpIfFirst(prefix, prefix);
    }

    public static String dumpIfFirst() {
        return dumpIfFirst("", "");
    }

    /**
     * 取得第一个entry。
     * 
     * @return 第一个entry，如果不存在，则返回<code>null</code>
     */
    public static Entry getEntry() {
        return entryStack.get();
    }

    /**
     * 取得最近的一个entry。
     * 
     * @return 最近的一个entry，如果不存在，则返回<code>null</code>
     */
    private static Entry getCurrentEntry() {
        Entry subEntry = entryStack.get();
        Entry entry = null;

        if (subEntry != null) {
            do {
                entry = subEntry;
                subEntry = entry.getUnreleasedEntry();
            } while (subEntry != null);
        }

        return entry;
    }

    /**
     * 代表一个计时单元。
     */
    public static final class Entry {
        private final List<Entry> subEntries = CollectionUtil.createArrayList(4);
        private final Object message;
        private final Entry parentEntry;
        private final Entry firstEntry;
        private final long baseTime;
        private final long startTime;
        private long endTime;

        /**
         * 创建一个新的entry。
         * 
         * @param message entry的信息，可以是<code>null</code>
         * @param parentEntry 父entry，可以是<code>null</code>
         * @param firstEntry 第一个entry，可以是<code>null</code>
         */
        private Entry(Object message, Entry parentEntry, Entry firstEntry) {
            this.message = message;
            this.startTime = System.currentTimeMillis();
            this.parentEntry = parentEntry;
            this.firstEntry = (Entry) ObjectUtil.defaultIfNull(firstEntry, this);
            this.baseTime = (firstEntry == null) ? 0 : firstEntry.startTime;
        }

        /**
         * 取得entry的信息。
         */
        public String getMessage() {
            String messageString = null;

            if (String.class.isInstance(message)) {
                messageString = (String) message;
            } else if (Message.class.isInstance(message)) {
                Message messageObject = (Message) message;
                MessageLevel level = MessageLevel.BRIEF_MESSAGE;

                if (isReleased()) {
                    level = messageObject.getMessageLevel(this);
                }

                if (level == MessageLevel.DETAILED_MESSAGE) {
                    messageString = messageObject.getDetailedMessage();
                } else {
                    messageString = messageObject.getBriefMessage();
                }
            }

            return StringUtil.defaultIfEmpty(messageString, null);
        }

        /**
         * 取得entry相对于第一个entry的起始时间。
         * 
         * @return 相对起始时间
         */
        public long getStartTime() {
            return (baseTime > 0) ? (startTime - baseTime) : 0;
        }

        /**
         * 取得entry相对于第一个entry的结束时间。
         * 
         * @return 相对结束时间，如果entry还未结束，则返回<code>-1</code>
         */
        public long getEndTime() {
            if (endTime < baseTime) {
                return -1;
            }
            return endTime - baseTime;
        }

        /**
         * 取得entry持续的时间。
         * 
         * @return entry持续的时间，如果entry还未结束，则返回<code>-1</code>
         */
        public long getDuration() {
            if (endTime < startTime) {
                return -1;
            }
            return endTime - startTime;
        }

        /**
         * 取得entry自身所用的时间，即总时间减去所有子entry所用的时间。
         * 
         * @return entry自身所用的时间，如果entry还未结束，则返回<code>-1</code>
         */
        public long getDurationOfSelf() {
            long duration = getDuration();

            if (duration < 0) {
                return -1;
            }
            if (subEntries.isEmpty()) {
                return duration;
            }

            for (int i = 0; i < subEntries.size(); i++) {
                Entry subEntry = subEntries.get(i);

                duration -= subEntry.getDuration();
            }

            if (duration < 0) {
                return -1;
            }
            return duration;

        }

        /**
         * 取得当前entry在父entry中所占的时间百分比。
         * 
         * @return 百分比
         */
        public double getPecentage() {
            double parentDuration = 0;
            double duration = getDuration();

            if ((parentEntry != null) && parentEntry.isReleased()) {
                parentDuration = parentEntry.getDuration();
            }

            if ((duration > 0) && (parentDuration > 0)) {
                return duration / parentDuration;
            }
            return 0;
        }

        /**
         * 取得当前entry在第一个entry中所占的时间百分比。
         * 
         * @return 百分比
         */
        public double getPecentageOfAll() {
            double firstDuration = 0;
            double duration = getDuration();

            if ((firstEntry != null) && firstEntry.isReleased()) {
                firstDuration = firstEntry.getDuration();
            }

            if ((duration > 0) && (firstDuration > 0)) {
                return duration / firstDuration;
            }
            return 0;
        }

        /**
         * 取得所有子entries。
         * 
         * @return 所有子entries的列表（不可更改）
         */
        public List<Entry> getSubEntries() {
            return Collections.unmodifiableList(subEntries);
        }

        /**
         * 结束当前entry，并记录结束时间。
         */
        private void release() {
            endTime = System.currentTimeMillis();
        }

        /**
         * 判断当前entry是否结束。
         * 
         * @return 如果entry已经结束，则返回<code>true</code>
         */
        private boolean isReleased() {
            return endTime > 0;
        }

        /**
         * 创建一个新的子entry。
         * 
         * @param message 子entry的信息
         */
        private void enterSubEntry(Object message) {
            Entry subEntry = new Entry(message, this, firstEntry);

            subEntries.add(subEntry);
        }

        /**
         * 取得未结束的子entry。
         * 
         * @return 未结束的子entry，如果没有子entry，或所有entry均已结束，则返回<code>null</code>
         */
        private Entry getUnreleasedEntry() {
            Entry subEntry = null;

            if (!subEntries.isEmpty()) {
                subEntry = subEntries.get(subEntries.size() - 1);

                if (subEntry.isReleased()) {
                    subEntry = null;
                }
            }

            return subEntry;
        }

        /**
         * 将entry转换成字符串的表示。
         * 
         * @return 字符串表示的entry
         */
        public String toString() {
            return toString("", "");
        }

        /**
         * 将entry转换成字符串的表示。
         * 
         * @param prefix1 首行前缀
         * @param prefix2 后续行前缀
         * 
         * @return 字符串表示的entry
         */
        private String toString(String prefix1, String prefix2) {
            StringBuilder builder = new StringBuilder();

            toString(builder, prefix1, prefix2);

            return builder.toString();
        }

        /**
         * 将entry转换成字符串的表示。
         * 
         * @param builder 字符串builder
         * @param prefix1 首行前缀
         * @param prefix2 后续行前缀
         */
        private void toString(StringBuilder builder, String prefix1, String prefix2) {
            builder.append(prefix1);

            String message = getMessage();
            long startTime = getStartTime();
            long duration = getDuration();
            long durationOfSelf = getDurationOfSelf();
            double selfPercent = duration == 0 ? 0 : (double) durationOfSelf / duration;
            double percent = getPecentage();
            double percentOfAll = getPecentageOfAll();

            Object[] params = new Object[] { message, // {0} - entry信息
                    Long.valueOf(startTime), // {1} - 起始时间
                    Long.valueOf(duration), // {2} - 持续总时间
                    Long.valueOf(durationOfSelf), // {3} - 自身消耗的时间
                    Double.valueOf(selfPercent), // {4} -
                    // 自身消耗在持续时间中所占的比例
                    Double.valueOf(percent), // {5} - 在父entry中所占的时间比例
                    Double.valueOf(percentOfAll) // {6} - 在总时间中所占的比例
                    };

            StringBuilder pattern = new StringBuilder("startTime: {1,number}ms ");

            if (isReleased()) {
                pattern.append(", duration: [{2,number}ms");

                if ((durationOfSelf > 0) && (durationOfSelf != duration)) {
                    pattern.append(",durationOfSelf: ({3,number}ms");
                    pattern.append(",selfPercent: {4,number,##.##%})");
                }

                if (percent > 0) {
                    pattern.append(", percent: {5,number,##.##%}");
                }

                if (percentOfAll > 0) {
                    pattern.append(", percentOfAll: {6,number,##.##%}");
                }

                pattern.append("]");
            } else {
                pattern.append("[UNRELEASED]");
            }

            if (message != null) {
                pattern.append(" - {0}");
            }

            builder.append(MessageFormat.format(pattern.toString(), params));

            for (int i = 0; i < subEntries.size(); i++) {
                Entry subEntry = subEntries.get(i);

                builder.append('\n');

                if (i == (subEntries.size() - 1)) {
                    subEntry.toString(builder, prefix2 + "`---", prefix2 + "    "); // 最后一项
                } else if (i == 0) {
                    subEntry.toString(builder, prefix2 + "+---", prefix2 + "|   "); // 第一项
                } else {
                    subEntry.toString(builder, prefix2 + " ---", prefix2 + "|   "); // 中间项
                }
            }
            // reset();
        }
    }

    /**
     * 显示消息的级别。
     */
    public static enum MessageLevel {

        NO_MESSAGE, BRIEF_MESSAGE, DETAILED_MESSAGE;

    }

    /**
     * 代表一个profiler entry的详细信息。
     */
    public interface Message {
        MessageLevel getMessageLevel(Entry entry);

        String getBriefMessage();

        String getDetailedMessage();
    }

}
