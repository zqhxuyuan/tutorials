/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.liaohuqiu.SimpleHashSet;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

public final class LruActionTracer implements Runnable {

    // final
    static final int REDUNDANT_OP_COMPACT_THRESHOLD = 2000;
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_TMP = "journal.tmp";
    static final String MAGIC = "lru-tracer";
    static final String VERSION_1 = "1";
    private static final String LOG_TAG = "simple-lru";
    private static final boolean DEBUG = true;

    private static final byte ACTION_CLEAN = 1;
    private static final byte ACTION_DIRTY = 2;
    private static final byte ACTION_DELETE = 3;
    private static final byte ACTION_READ = 4;
    private static final byte ACTION_PENDING_DELETE = 5;
    private static final byte ACTION_FLUSH = 6;

    private static final String[] sACTION_LIST = new String[]{"UN_KNOW", "CLEAN", "DIRTY", "DELETE", "READ", "DELETE_PENDING", "FLUSH"};

    // size
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private long mSize = 0;                         //缓存条目的总大小
    private static int sPoolSize = 0;               //空闲池中可用的数量
    private static final int MAX_POOL_SIZE = 50;    //空闲池最大允许的数量
    private int mRedundantOpCount;   //操作日志的数量

    // object
    private static ActionMessage sPoolHeader;       //空闲池中第一个可用的元素
    private final LinkedHashMap<String, CacheEntry> mLruEntries = new LinkedHashMap<String, CacheEntry>(0, 0.75f, true);
    //This cache uses a single background thread to evict entries.
    private final ExecutorService mExecutorService = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private Writer mJournalWriter;

    // 构造函数初始化
    private IDiskCache mDiskCache;
    private final File mJournalFile;
    private final File mJournalFileTmp;
    private File mDirectory;
    private int mAppVersion;
    private long mCapacity;     //容量, 每次写数据都会增加mSize. 如果超过容量, 则要截断
    private SimpleHashSet mNewCreateList;
    private ConcurrentLinkedQueue<ActionMessage> mActionQueue;  //Action的队列, 因为Action操作是比较耗时且数量很多.

    // state
    private static final byte[] sPoolSync = new byte[0];  //空闲池同步块
    private Object mLock = new Object();
    private boolean mIsRunning = false;

    public LruActionTracer(IDiskCache diskCache, File directory, int appVersion, long capacity) {
        mDiskCache = diskCache;
        mJournalFile = new File(directory, JOURNAL_FILE);
        mJournalFileTmp = new File(directory, JOURNAL_FILE_TMP);

        mDirectory = directory;
        mAppVersion = appVersion;
        mCapacity = capacity;
        mNewCreateList = new SimpleHashSet();
        mActionQueue = new ConcurrentLinkedQueue<ActionMessage>();
    }

    private static void validateKey(String key) {
        if (key.contains(" ") || key.contains("\n") || key.contains("\r")) {
            throw new IllegalArgumentException(
                    "keys must not contain spaces or newlines: \"" + key + "\"");
        }
    }

    /**
     * try to resume last status when we got off 恢复最近的状态
     *
     * @throws java.io.IOException
     */
    public void tryToResume() throws IOException {
        if (mJournalFile.exists()) {
            // journal file记录了操作日志, 恢复状态时从这个文件中读取数据
            try {
                readJournal();
                processJournal();
                //处理完毕后, 创建一个新的journal file writer, 准备记录接下来的操作日志
                mJournalWriter = new BufferedWriter(new FileWriter(mJournalFile, true), IO_BUFFER_SIZE);
                if (DEBUG) {
                    CLog.d(LOG_TAG, "open success");
                }
            } catch (IOException journalIsCorrupt) {
                journalIsCorrupt.printStackTrace();
                if (DEBUG) {
                    CLog.d(LOG_TAG, "clear old cache");
                }
                clear();
            }
        } else {
            // 没有操作日志, 没有需要恢复的数据
            if (DEBUG) {
                CLog.d(LOG_TAG, "create new cache");
            }

            // create a new empty cache
            if (mDirectory.exists()) {
                mDirectory.delete();
            }
            mDirectory.mkdirs();
            rebuildJournal();
        }
    }

    public synchronized void clear() throws IOException {

        // abort edit
        for (CacheEntry cacheEntry : new ArrayList<CacheEntry>(mLruEntries.values())) {
            //如果正在编辑的话, 让它停止编辑. 因为编辑了也没有用!
            if (cacheEntry.isUnderEdit()) {
                cacheEntry.abortEdit();
            }
        }
        //清除所有的条目
        mLruEntries.clear();
        //设置条目的大小=0
        mSize = 0;

        // delete current directory then rebuild
        if (DEBUG) {
            CLog.d(LOG_TAG, "delete directory");
        }

        //等待清除过程完成. 实际是等待队列中的操作完成后再关闭. 不能直接粗鲁地关闭.
        waitJobDone();

        // rebuild 重构journal文件
        Utils.deleteDirectoryQuickly(mDirectory);
        rebuildJournal();
    }

    /**
     * Returns a {@link CacheEntry} named {@code key}, or null if it doesn't exist is not currently readable.
     * If a value is returned, it is moved to the head of the LRU queue.
     * 根据key返回一个缓存的条目. 如果返回了一个值, 则这个条目会被移动到LRU队列的头部.
     * 因为根据LRU: Least Recently Used最近最少使用. 如果获得了数据,相当于使用了,就不会成为最近最少使用的了.
     */
    public synchronized CacheEntry getEntry(String key) throws IOException {
        checkNotClosed();
        validateKey(key);
        CacheEntry cacheEntry = mLruEntries.get(key);
        if (cacheEntry == null) return null;

        trimToSize();
        addActionLog(ACTION_READ, cacheEntry);
        return cacheEntry;
    }

    //开始编辑
    public synchronized CacheEntry beginEdit(String key) throws IOException {
        checkNotClosed();
        validateKey(key);

        if (DEBUG) {
            CLog.d(LOG_TAG, "beginEdit: %s", key);
        }
        CacheEntry cacheEntry = mLruEntries.get(key);
        if (cacheEntry == null) {
            //新创建一个缓存条目
            cacheEntry = new CacheEntry(mDiskCache, key);
            mNewCreateList.add(key);
            //添加到LRU缓存中!!!
            mLruEntries.put(key, cacheEntry);
        }

        //已经存在, 设置存在的哪个条目为过期的状态!
        addActionLog(ACTION_DIRTY, cacheEntry);
        return cacheEntry;
    }

    //停止编辑
    public void abortEdit(CacheEntry cacheEntry) {
        final String cacheKey = cacheEntry.getKey();
        if (DEBUG) {
            CLog.d(LOG_TAG, "abortEdit: %s", cacheKey);
        }
        if (mNewCreateList.contains(cacheKey)) {
            mLruEntries.remove(cacheKey);
            mNewCreateList.remove(cacheKey);
        }
    }

    //提交编辑
    public void commitEdit(CacheEntry cacheEntry) throws IOException {
        if (DEBUG) {
            CLog.d(LOG_TAG, "commitEdit: %s", cacheEntry.getKey());
        }
        //在开始前添加到newCreate, 在结束编辑后,从newCreate中删除
        mNewCreateList.remove(cacheEntry.getKey());
        mSize += cacheEntry.getSize() - cacheEntry.getLastSize();
        addActionLog(ACTION_CLEAN, cacheEntry);
        trimToSize();
    }

    //读取journal文件的一行
    private void readJournalLine(String line) throws IOException {
        //一行的格式是: Action Key Value
        String[] parts = line.split(" ");
        if (parts.length < 2) {
            throw new IOException("unexpected journal line: " + line);
        }
        if (parts.length != 3) {
            throw new IOException("unexpected journal line: " + line);
        }

        String key = parts[1];
        //被标记为删除,从LRU缓存中删除
        if (parts[0].equals(sACTION_LIST[ACTION_DELETE])) {
            mLruEntries.remove(key);
            return;
        }

        CacheEntry cacheEntry = mLruEntries.get(key);
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry(mDiskCache, key);
            mLruEntries.put(key, cacheEntry);
        }

        if (parts[0].equals(sACTION_LIST[ACTION_CLEAN])) {
            cacheEntry.setSize(Long.parseLong(parts[2]));
        } else if (parts[0].equals(sACTION_LIST[ACTION_DIRTY])) {
            // skip
        } else if (parts[0].equals(sACTION_LIST[ACTION_READ])) {
            // this work was already done by calling mLruEntries.get()
        } else {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    /**
     * Computes the initial size and collects garbage as a part of opening the
     * cache. Dirty entries are assumed to be inconsistent and will be deleted.
     */
    private void processJournal() throws IOException {
        Utils.deleteIfExists(mJournalFileTmp);
        for (Iterator<CacheEntry> i = mLruEntries.values().iterator(); i.hasNext(); ) {
            CacheEntry cacheEntry = i.next();
            if (!cacheEntry.isUnderEdit()) {
                mSize += cacheEntry.getSize();
            } else {
                cacheEntry.delete();
                i.remove();
            }
        }
    }

    /**
     * Creates a new journal that omits redundant information.
     * This replaces the current journal if it exists.
     * 重构journal file: 创建一个新的journal,并省略了一些信息.如果已经存在journal则覆盖
     */
    private synchronized void rebuildJournal() throws IOException {
        //存在journal, 关闭它.
        if (mJournalWriter != null) {
            mJournalWriter.close();
        }

        //先写到临时文件中
        Writer writer = new BufferedWriter(new FileWriter(mJournalFileTmp), IO_BUFFER_SIZE);
        writer.write(MAGIC);
        writer.write("\n");
        writer.write(VERSION_1);
        writer.write("\n");
        writer.write(Integer.toString(mAppVersion));
        writer.write("\n");
        writer.write("\n");

        //把内存中的条目先写到文件中. 因为内存中的这部分数据还没有持久化成journal file
        //当然, 一开始的话是没有条目的. 也就不会往文件中写条目数据
        for (CacheEntry cacheEntry : mLruEntries.values()) {
            if (cacheEntry.isUnderEdit()) {
                //正在编辑, 过期的!
                writer.write(sACTION_LIST[ACTION_DIRTY] + ' ' + cacheEntry.getKey() + " " + cacheEntry.getSize() + '\n');
            } else {
                //没有在编辑, 可以清除
                writer.write(sACTION_LIST[ACTION_CLEAN] + ' ' + cacheEntry.getKey() + " " + cacheEntry.getSize() + '\n');
            }
        }

        writer.close();
        //新建, 或覆盖(如果存在的话)
        mJournalFileTmp.renameTo(mJournalFile);
        mJournalWriter = new BufferedWriter(new FileWriter(mJournalFile, true), IO_BUFFER_SIZE);
    }

    private void readJournal() throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(mJournalFile), IO_BUFFER_SIZE);
        try {
            //头部验证
            String magic = Utils.readAsciiLine(in);
            String version = Utils.readAsciiLine(in);
            String appVersionString = Utils.readAsciiLine(in);
            String blank = Utils.readAsciiLine(in);
            if (!MAGIC.equals(magic)
                    || !VERSION_1.equals(version)
                    || !Integer.toString(mAppVersion).equals(appVersionString)
                    || !"".equals(blank)) {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + blank + "]");
            }

            //读取文件的每一行
            while (true) {
                try {
                    readJournalLine(Utils.readAsciiLine(in));
                } catch (EOFException endOfJournal) {
                    break;
                }
            }
        } finally {
            Utils.closeQuietly(in);
        }
    }

    private void checkNotClosed() {
        if (mJournalFile == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    /**
     * Force buffered operations to the filesystem. 刷写到磁盘
     */
    public synchronized void flush() throws IOException {
        checkNotClosed();
        trimToSize();
        addActionLog(ACTION_FLUSH, null);
        waitJobDone();
    }

    /**
     * 往journal file中追加操作日志
     * @param action
     * @param cacheEntry
     * @throws IOException
     */
    private void writeActionLog(byte action, CacheEntry cacheEntry) throws IOException {
        //写入一个操作日志: Action Key Value
        mJournalWriter.write(sACTION_LIST[action] + ' ' + cacheEntry.getKey() + ' ' + cacheEntry.getSize() + '\n');
        mRedundantOpCount++;
        //如果操作日志数量超过2000个,则重建journal文件
        if (mRedundantOpCount >= REDUNDANT_OP_COMPACT_THRESHOLD && mRedundantOpCount >= mLruEntries.size()) {
            mRedundantOpCount = 0;
            rebuildJournal();
        }
    }

    private void doJob() throws IOException {
        synchronized (mLock) {
            while (!mActionQueue.isEmpty()) {
                ActionMessage message = mActionQueue.poll();
                //ActionMessage封装了CacheEntry和Action
                final CacheEntry cacheEntry = message.mCacheEntry;
                final byte action = message.mAction;
                //处理完消息,就可以回收这个消息在队列中占用的内存了. 回收后的空间可以给其他消息使用
                message.recycle();
                CLog.d(LOG_TAG, "doAction: %s,\tkey: %s", sACTION_LIST[action], cacheEntry != null ? cacheEntry.getKey() : null);

                switch (action) {
                    case ACTION_READ:
                        writeActionLog(action, cacheEntry);
                        break;

                    case ACTION_DIRTY:
                        writeActionLog(action, cacheEntry);
                        break;

                    case ACTION_CLEAN:
                        writeActionLog(action, cacheEntry);
                        break;

                    case ACTION_DELETE:
                        writeActionLog(action, cacheEntry);
                        break;

                    //正在删除, 只有mSize超过mCapacity的时候,需要截断!
                    case ACTION_PENDING_DELETE:
                        writeActionLog(action, cacheEntry);
                        if (mLruEntries.containsKey(cacheEntry.getKey())) {
                            continue;
                        }
                        cacheEntry.delete();
                        break;

                    case ACTION_FLUSH:
                        mJournalWriter.flush();
                        break;
                }
            }
            mLock.notify();
        }
    }

    private void waitJobDone() {
        if (DEBUG) {
            CLog.d(LOG_TAG, "waitJobDone");
        }
        synchronized (mLock) {
            if (mIsRunning) {
                while (!mActionQueue.isEmpty()) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (DEBUG) {
            CLog.d(LOG_TAG, "job is done");
        }
    }

    /**
     * CacheEntry    缓存条目 --> 会添加到LRU缓存中
     * ActionLog     操作日志 --> 会写到journal文件中
     * ActionMessage 操作消息 --> 会添加到ActionQueue中
     *
     *                 构造             入队           出队        追加写
     * Action+CacheEntry==>ActionMessage==>ActionQueue<==ActionLog==>journal file
     *
     * @param action
     * @param cacheEntry
     */
    private void addActionLog(byte action, CacheEntry cacheEntry) {
        mActionQueue.add(ActionMessage.obtain(action, cacheEntry));

        //启动后台队列获取线程. 因为只有往队列里开始添加操作日志后, 队列才有数据. 这时候启动线程才有意义.
        //如果一开始构造LruActionTracker对象时就启动线程, 但这个时候队列里没有数据, 仍然除于等待状态.
        if (!mIsRunning) {
            mIsRunning = true;
            mExecutorService.submit(this);
        }
    }

    public synchronized void close() throws IOException {
        if (isClosed()) {
            return; // already closed
        }
        for (CacheEntry cacheEntry : new ArrayList<CacheEntry>(mLruEntries.values())) {
            if (cacheEntry.isUnderEdit()) {
                cacheEntry.abortEdit();
            }
        }
        trimToSize();
        waitJobDone();
        rebuildJournal();
        mJournalWriter.close();
        mJournalWriter = null;
    }

    private boolean isClosed() {
        return mJournalWriter == null;
    }

    @Override
    public void run() {
        try {
            doJob();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mIsRunning = false;
    }

    /**
     * remove files from list, delete files
     *
     * @throws IOException
     */
    private void trimToSize() throws IOException {
        synchronized (this) {
            if (mSize > mCapacity) {
                if (DEBUG) {
                    CLog.d(LOG_TAG, "should trim, current is: %s", mSize);
                }
            }
            while (mSize > mCapacity) {
                Map.Entry<String, CacheEntry> toEvict = mLruEntries.entrySet().iterator().next();
                String key = toEvict.getKey();
                CacheEntry cacheEntry = toEvict.getValue();
                mLruEntries.remove(key);

                mSize -= cacheEntry.getSize();
                addActionLog(ACTION_PENDING_DELETE, cacheEntry);
                if (DEBUG) {
                    CLog.d(LOG_TAG, "pending remove: %s, size: %s, after remove total: %s", key, cacheEntry.getSize(), mSize);
                }
            }
        }
    }

    public synchronized boolean delete(String key) throws IOException {
        if (DEBUG) {
            CLog.d(LOG_TAG, "delete: %s", key);
        }
        checkNotClosed();
        validateKey(key);
        CacheEntry cacheEntry = mLruEntries.get(key);
        if (cacheEntry == null) {
            return false;
        }

        // delete at once
        cacheEntry.delete();
        mSize -= cacheEntry.getSize();
        cacheEntry.setSize(0);
        mLruEntries.remove(key);

        addActionLog(ACTION_DELETE, cacheEntry);
        return true;
    }

    public long getSize() {
        return mSize;
    }

    public long getCapacity() {
        return mCapacity;
    }

    public File getDirectory() {
        return mDirectory;
    }

    public boolean has(String key) {
        return mLruEntries.containsKey(key) && !mNewCreateList.contains(key);
    }

    //动作消息. 封装了Action和CacheEntry
    private static class ActionMessage {
        private byte mAction;
        private CacheEntry mCacheEntry;
        private ActionMessage mNext;

        public ActionMessage(byte action, CacheEntry cacheEntry) {
            mAction = action;
            mCacheEntry = cacheEntry;
        }

        public static ActionMessage obtain(byte action, CacheEntry cacheEntry) {
            synchronized (sPoolSync) {
                //如果有回收空间, 在获取时优先使用回收过的内存
                //pollHeader表示可以用的(空闲)池的第一个元素
                if (sPoolHeader != null) {
                    //使用空闲池的第一个元素
                    ActionMessage m = sPoolHeader;

                    //空闲池中下一个可用的元素是sPoolHeader的下一个元素
                    sPoolHeader = m.mNext;
                    m.mNext = null;
                    //可用的减一
                    sPoolSize--;

                    //设置最新的ActionMessage对象
                    m.mAction = action;
                    m.mCacheEntry = cacheEntry;
                    return m;
                }
            }
            //如果没有空闲元素可用, 则直接创建消息对象
            return new ActionMessage(action, cacheEntry);
        }

        //回收利用当前节点. 在从队列中消费完消息后就可以回收了.
        public void recycle() {
            mAction = 0;            //回收后,状态为UNKNOWN
            mCacheEntry = null;     //回收缓存条目
            synchronized (sPoolSync) {
                //最多回收50个
                if (sPoolSize < MAX_POOL_SIZE) {
                    //当前节点是要被回收的节点. 它的下一个next指针指向前一个回收节点. 所以当前节点会插入到链表表头
                    mNext = sPoolHeader;
                    //当前节点会成为空闲链表的表头
                    sPoolHeader = this;
                    sPoolSize++;
                }
            }
        }
    }
}
