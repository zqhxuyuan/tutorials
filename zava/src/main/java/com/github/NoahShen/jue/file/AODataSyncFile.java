/**
 * 
 */
package com.github.NoahShen.jue.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.github.NoahShen.jue.util.ByteDynamicArray;
import com.github.NoahShen.jue.util.ByteUtil;
import com.github.NoahShen.jue.util.ConcurrentLRUCache;

/**
 * Append-only data sync file class
 * @author noah
 *
 */
public class AODataSyncFile {
	/**
	 * 默认最大缓存数
	 */
	public static final int DEFAULT_MAX_CACHE_CAPACITY = 64;
	
	/**
	 * 默认的文件块大小
	 */
	public static final int DEFAULT_BLOCK_SIZE = 64 * 1024 * 1024; //64MB
	
	/**
	 * 默认新数据的缓冲区大小
	 */
	public static final int DEFAULT_MAX_DATA_BUFFER_SIZE = 128 * 1024 * 1024; //128MB
	
	/**
	 * 文件
	 */
	private File file;
	
	/**
	 * 文件Channel对象
	 */
	private FileChannel fileChannel;
	

	/**
	 * 块大小
	 */
	private final int blockSize;

	/**
	 * 是否缓存文件块
	 */
	private final boolean blockCache;
	
	/**
	 * 缓存最大值
	 */
	private int maxCacheCapacity;
	
	/**
	 * 缓存，使用ByteDynamicArray，而不是ByteBuffer,因为ByteBuffer线程不安全
	 */
	private ConcurrentLRUCache<Long, ByteDynamicArray> cache;
	
	/**
	 * 最大缓冲区的大小限制
	 */
	private int maxDataBufferSize;
	
	/**
	 * 新写入数据的缓存
	 */
	private ByteDynamicArray dataBufferDArray;
	
	/**
	 * 头文件的缓存
	 */
	private ByteDynamicArray headerBufferDArray;

	/**
	 * 缓存锁
	 */
	private final ReentrantReadWriteLock bufferLock = new ReentrantReadWriteLock();
	
	/**
	 * 缓存读锁
	 */
	private final ReadLock readBufferLock = bufferLock.readLock();
	
	/**
	 * 缓存写锁
	 */
	private final WriteLock writeBufferLock = bufferLock.writeLock();
	
	/**
	 * 每次写数据都先写入缓存
	 */
	public static final int BUFFER_STRATEGY = 1;
	
	/**
	 * 每次写入数据都写入到磁盘
	 */
	public static final int ALWAYS_STRATEGY = 2;
	
	/**
	 * 每秒将缓存中的数据写入磁盘
	 */
	public static final int EVERY_SEC_STRATEGY = 3;
	/**
	 * 写文件的策略
	 */
	private SyncStrategy syncStrategy;


	/**
	 * 创建AODataSyncFile
	 * @param file
	 * @param syncStrategy
	 * @throws IOException
	 */
	public AODataSyncFile(File file, int syncStrategy) throws IOException {
		this(file, DEFAULT_BLOCK_SIZE, true, DEFAULT_MAX_CACHE_CAPACITY, DEFAULT_MAX_DATA_BUFFER_SIZE, syncStrategy);
	}
	/**
	 * 创建AODataSyncFile
	 * @param file
	 * @param blockSize
	 * @param blockCache
	 * @param maxCacheCapacity
	 * @param newDataBufferSize
	 * @throws IOException
	 */
	public AODataSyncFile(File file, int blockSize, boolean blockCache, int maxCacheCapacity, int maxDataBufferSize, int syncStrategy) throws IOException {
		this.file = file;
		boolean newFile = !file.exists();
		@SuppressWarnings("resource")
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		if (newFile) {
			byte[] emptyHeader = new byte[FileHeader.HEADER_SIZE];
			raf.write(emptyHeader);
		}
		this.fileChannel = raf.getChannel();
		this.blockSize = blockSize;
		this.blockCache = blockCache;
		this.maxCacheCapacity = maxCacheCapacity;
		if (blockCache) {
			cache = new ConcurrentLRUCache<Long, ByteDynamicArray>(maxCacheCapacity);
		}
		this.maxDataBufferSize = maxDataBufferSize;
		this.dataBufferDArray = new ByteDynamicArray(maxDataBufferSize);
		this.headerBufferDArray = new ByteDynamicArray(FileHeader.HEADER_SIZE);
		
		if (syncStrategy == BUFFER_STRATEGY) {
			this.syncStrategy = new SyncBuffterStrategy();
		} else if (syncStrategy == ALWAYS_STRATEGY) {
			this.syncStrategy = new SyncAlwaysStrategy();
		} else if (syncStrategy == EVERY_SEC_STRATEGY) {
			this.syncStrategy = new SyncEverySecStrategy();
		} else {
			throw new IllegalArgumentException("Invalid strategy!");
		}
	}

	/**
	 * 返回文件大小，包括缓存
	 * @return
	 * @throws IOException 
	 */
	public long size() throws IOException {
		return fileChannel.size() + dataBufferDArray.size();
	}
	
	public File getFile() {
		return file;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public boolean isBlockCache() {
		return blockCache;
	}
	
	public int getMaxCacheCapacity() {
		return maxCacheCapacity;
	}
	public int getMaxDataBufferSize() {
		return maxDataBufferSize;
	}
	/**
	 * 写入数据，返回写入的地址
	 * @param dataBufferDArray
	 * @return 写入的地址
	 * @throws IOException
	 */
	public long appendData(ByteBuffer newHeaderBuffer, ByteBuffer newDataBuffer) throws IOException {
		writeBufferLock.lock();
		try	{
			return syncStrategy.append(newHeaderBuffer, newDataBuffer);
		} finally {
			writeBufferLock.unlock();
		}
	}

	/**
	 * 将缓存数据写入磁盘
	 * @throws IOException 
	 */
	private void writeBufferToFile() throws IOException {
		if (dataBufferDArray.size() > 0) {
			ByteBuffer writeDataBuffer = ByteBuffer.wrap(dataBufferDArray.getOrginalBytes(), 0, dataBufferDArray.size());
			long writePos = fileChannel.size();
			fileChannel.write(writeDataBuffer, writePos);
			dataBufferDArray.clear();
		}
		
		if (headerBufferDArray.size() > 0) {
			ByteBuffer writeHeaderBuffer = ByteBuffer.wrap(headerBufferDArray.getOrginalBytes(), 0, headerBufferDArray.size());
			fileChannel.write(writeHeaderBuffer, 0);
			headerBufferDArray.clear();
		}
	}
	
	/**
	 * 将新数据写入缓存
	 * @param newHeaderBuffer
	 * @param newDataBuffer
	 * @return 返回新数据的写入地址
	 * @throws IOException
	 */
	private long writeDataToBuffer(ByteBuffer newHeaderBuffer, ByteBuffer newDataBuffer) throws IOException {
		long newDataPos = this.size();
		int size = newDataBuffer.remaining();
		if (size > 0) {
			this.dataBufferDArray.add(ByteUtil.getBytesFromBuffer(newDataBuffer));
		}
		this.headerBufferDArray = new ByteDynamicArray(ByteUtil.getBytesFromBuffer(newHeaderBuffer));
		
		if (size > 0) {
			clearBlockCache(newDataPos, size);
		}
		return newDataPos;
	}
	
	/**
	 * // 清空缓存
	 * @param pos
	 * @param size
	 */
	private void clearBlockCache(long pos, int size) {
		if (blockCache) {
			long[] blockIdxs = getBlockIndexes(pos, size);
			for (int i = 0; i < blockIdxs.length; ++i) {
				cache.remove(blockIdxs[i]);
			}
		}
	}
	
	/**
	 * 获取即将写入的数据对应的文件块的位置
	 * 
	 * @param pos
	 *            文件位置
	 * @param size
	 *            要获取的长度
	 * @return
	 */
	private long[] getBlockIndexes(long pos, int size) {
		long removeHeaderPos = pos - FileHeader.HEADER_SIZE;
		// 起始文件块的位置
		long startBlockIndex = removeHeaderPos / blockSize;
		// 确定结束的块位置
		long endBlockIndex = (removeHeaderPos + size) / blockSize;
		// 需要读取的文件块的个数
		int count = (int) (endBlockIndex - startBlockIndex + 1);
		// 获取各文件块位置
		long[] indexes = new long[count];
		for (int i = 0; i < count; ++i, ++startBlockIndex) {
			indexes[i] = startBlockIndex;
		}
		return indexes;
	}


	/**
	 * 读取数据到buffer中
	 * @param readBuffer
	 * @param position
	 * @return
	 * @throws IOException
	 */
	public int read(ByteBuffer readBuffer, long position) throws IOException {
		if (position < FileHeader.HEADER_SIZE) {
			throw new IllegalArgumentException("position must >= file header size:" + FileHeader.HEADER_SIZE + " current pos:" + position);
		}
		readBufferLock.lock();
		try	{
			// 文件大小，包括之前写入的缓存
			long allFileSize = size();
			if (allFileSize == 0) {
				return -1;
			}
			if (position >= allFileSize) {
				throw new IllegalArgumentException("position must < file size[" + allFileSize + "]!");
			}
			// 最大读取的数据量
			int readSize = readBuffer.remaining();
			// 读取的数据长度
			int count = 0;
			// 获取需要读取的数据对应的文件块的位置
			long[] blockIndexes = getBlockIndexes(position, readSize);
			for (int i = 0; i < blockIndexes.length; ++i) {
				ByteDynamicArray blockDataDArray = getBlockData(blockIndexes[i]);
				int readPos = 0;
				if (i == 0) {
					// 移除头部后的相对位置
					long removedHeaderPos = position - FileHeader.HEADER_SIZE;
					// 第一个数据块，可能只读取部分数据
					readPos = (int) (removedHeaderPos % blockSize);
				}
				count += blockDataDArray.read(readBuffer, readPos);
			}
			return count;
		} finally {
			readBufferLock.unlock();
		}
	}

	
	/**
	 * 读取相应的文件块
	 * @param blockIndex
	 * @param checksum
	 * @return
	 * @throws IOException
	 */
	private ByteDynamicArray getBlockData(long blockIndex) throws IOException {
		ByteDynamicArray readBlockData = null;
		// 从缓存中获取该文件块
		if (blockCache) {
			readBlockData = cache.get(blockIndex);
			if (readBlockData != null) {
				return readBlockData;
			}
		}
		// 缓存中不存在该块，从文件及缓冲区中读取
		ByteBuffer readBlockBuffer = ByteBuffer.allocate(blockSize);
		// 已经读取的字节数
		int ct = 0;
		do {
			long readPos = FileHeader.HEADER_SIZE + blockIndex * blockSize + ct;
			long fileSize = fileChannel.size();
			int n = 0;
			if (readPos < fileSize) {
				n = fileChannel.read(readBlockBuffer, readPos);
			} else {
				int posInBuffer = (int) (readPos - fileSize);
				n = this.dataBufferDArray.read(readBlockBuffer, posInBuffer);
				if (n == 0) {
					break;
				}
			}
			ct += n;
		} while (ct < blockSize);// 直到读满一个文件块的容量
		
		readBlockBuffer.flip();
		byte[] bytes = ByteUtil.getBytesFromBuffer(readBlockBuffer);
		readBlockData = new ByteDynamicArray(bytes);
		if (blockCache) {
			cache.put(blockIndex, readBlockData);
		}
		return readBlockData;
	}
	
	public void close() throws IOException {
		writeBufferLock.lock();
		try	{	
			syncStrategy.close();
			fileChannel.close();
			cache.clear();
			cache = null;
			dataBufferDArray.clear();
			headerBufferDArray.clear();
		} finally {
			writeBufferLock.unlock();
		}
	}
	
	/**
	 * 数据写入策略接口
	 * @author noah
	 *
	 */
	public interface SyncStrategy {
		
		/**
		 * 写入数据
		 * @param newHeaderBuffer
		 * @param newDataBuffer
		 * @return
		 * @throws IOException
		 */
		public long append(ByteBuffer newHeaderBuffer, ByteBuffer newDataBuffer) throws IOException;
		
		/**
		 * 关闭文件
		 * @throws IOException
		 */
		public void close() throws IOException;
	}
	
	/**
	 * 使用缓存策略，当写入数据时，只写入缓存，除非缓存的数据超过最大缓存时候，或者在close的时候，才会写入磁盘
	 * @author noah
	 *
	 */
	public class SyncBuffterStrategy implements SyncStrategy {
		
		@Override
		public long append(ByteBuffer newHeaderBuffer, ByteBuffer newDataBuffer) throws IOException {
			// 需要写入的数据长度
			int dataSize = newDataBuffer.remaining();
			if (dataSize > AODataSyncFile.this.maxDataBufferSize) {
				throw new IllegalArgumentException("The new data size can't be bigger than max data buffer size!");
			}
			
			// 超出缓存最大限制，先写入磁盘
			if (AODataSyncFile.this.dataBufferDArray.size() + dataSize > AODataSyncFile.this.maxDataBufferSize) {
				writeBufferToFile();
			}
			return writeDataToBuffer(newHeaderBuffer, newDataBuffer);
		}
		
		@Override
		public void close() throws IOException {
			writeBufferToFile();
		}
	}
	
	
	/**
	 * 每次写入数据，都直接写入磁盘。
	 * @author noah
	 *
	 */
	public class SyncAlwaysStrategy implements SyncStrategy {
		
		@Override
		public long append(ByteBuffer newHeaderBuffer, ByteBuffer newDataBuffer) throws IOException {
			// 需要写入的数据长度
			int dataSize = newDataBuffer.remaining();
			if (dataSize > AODataSyncFile.this.maxDataBufferSize) {
				throw new IllegalArgumentException("The new data size can't be bigger than max data buffer size!");
			}
			long pos =  writeDataToBuffer(newHeaderBuffer, newDataBuffer);
			writeBufferToFile();
			return pos;
		}

		@Override
		public void close() throws IOException {
			writeBufferToFile();
		}
	}
	
	/**
	 * 每秒写入一次数据。
	 * @author noah
	 *
	 */
	public class SyncEverySecStrategy implements SyncStrategy {
		
		private long lastWriteTime;
		
		private static final long ONE_SECOND = 1000;
		
		@Override
		public long append(ByteBuffer newHeaderBuffer, ByteBuffer newDataBuffer) throws IOException {			
			// 需要写入的数据长度
			int dataSize = newDataBuffer.remaining();
			if (dataSize > AODataSyncFile.this.maxDataBufferSize) {
				throw new IllegalArgumentException("The new data size can't be bigger than max data buffer size!");
			}
			
			long escaped = System.currentTimeMillis() - lastWriteTime;
			// 距离上次写入时间超过1秒，或者超出缓存最大限制，先写入磁盘
			if (escaped > ONE_SECOND 
					|| AODataSyncFile.this.dataBufferDArray.size() + dataSize > AODataSyncFile.this.maxDataBufferSize) {
				writeBufferToFile();
			}
			long pos =  writeDataToBuffer(newHeaderBuffer, newDataBuffer);
			lastWriteTime = System.currentTimeMillis();
			return pos;
		}

		@Override
		public void close() throws IOException {
			writeBufferToFile();
		}
	}
}
