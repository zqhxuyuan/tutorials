/**
 * 
 */
package com.github.NoahShen.jue.file;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.Checksum;

import com.github.NoahShen.jue.util.ConcurrentLRUCache;

/**
 * 以块的方式读写文件，可以设置块的大小，以及是否缓存
 * 
 * @author noah
 */
public class BlockFileChannel {

	/**
	 * 最大缓存数
	 */
	public static final int MAX_CAPACITY = 100000;

	/**
	 * 文件块的校验码的长度
	 */
	public static final int CHECKSUM_SIZE = 8;
	
	/**
	 * 校验码存储文件的后缀
	 */
	public static final String BLOCK_SUFFIX = ".bck";
	
	/**
	 * 默认的文件块大小
	 */
	public static final int DEFAULT_BLOCK_SIZE = 64 * 1024 * 1024;//64MB

	/**
	 * 文件
	 */
	private File file;
	
	/**
	 * 文件Channel对象
	 */
	private FileChannel fileChannel;
	
	/**
	 * checksum文件
	 */
	private File blockChecksumFile;
	/**
	 * 存储block块的校验码的文件
	 */
	private FileChannel blockChecksumChannel;

	/**
	 * 块大小
	 */
	private final int blockSize;

	/**
	 * 是否缓存文件块
	 */
	private final boolean blockCache;

	/**
	 * 缓存
	 */
	private ConcurrentLRUCache<Long, ByteBuffer> cache;

	/**
	 * 校验码生成器
	 */
	private ChecksumGenerator checksumGenerator;
	
	/**
	 * 构造一个BlockFileChannel
	 * 
	 * @param file
	 *            文件对象
	 * @param checksumGenerator
	 *            校验码生成器
	 * @throws FileNotFoundException
	 */
	public BlockFileChannel(File file, ChecksumGenerator checksumGenerator) {
		this(file, DEFAULT_BLOCK_SIZE, false, checksumGenerator);
	}
	
	/**
	 * 构造一个BlockFileChannel
	 * 
	 * @param file
	 *            文件对象
	 * @param blockSize
	 *            快大小
	 * @param checksumGenerator
	 *            校验码生成器
	 * @throws FileNotFoundException
	 */
	public BlockFileChannel(File file, int blockSize, ChecksumGenerator checksumGenerator) {
		this(file, blockSize, true, checksumGenerator);
	}

	/**
	 * 构造一个BlockFileChannel
	 * 
	 * @param filePath
	 *            文件路径
	 * @param blockSize
	 *            快大小
	 * @param checksumGenerator
	 *            校验码生成器
	 * @throws FileNotFoundException
	 */
	public BlockFileChannel(String filePath, int blockSize, ChecksumGenerator checksumGenerator) {
		this(filePath, blockSize, true, checksumGenerator);
	}

	/**
	 * 构造一个BlockFileChannel
	 * 
	 * @param filePath
	 *            文件路径
	 * @param blockSize
	 *            块大小
	 * @param blockCache
	 *            是否缓存块
	 * @param checksumGenerator
	 *            校验码生成器
	 * @throws FileNotFoundException
	 *             文件不存在
	 */
	public BlockFileChannel(String filePath, int blockSize, boolean blockCache, ChecksumGenerator checksumGenerator) {
		this(new File(filePath), blockSize, blockCache, checksumGenerator);
	}

	/**
	 * 构造一个BlockFileChannel
	 * 
	 * @param file
	 *            文件对象
	 * @param blockSize
	 *            块大小
	 * @param blockCache
	 *            是否缓存块
	 * @param checksumGenerator
	 *            校验码生成器
	 * @throws FileNotFoundException
	 *             文件不存在
	 */
	public BlockFileChannel(File file, int blockSize, boolean blockCache, ChecksumGenerator checksumGenerator) {
		try {
			this.file = file;
			@SuppressWarnings("resource")
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			this.fileChannel = raf.getChannel();
			
			File blockchkFile = new File(getChecksumFilename(file.getAbsolutePath()));
			this.blockChecksumFile = blockchkFile;
			@SuppressWarnings("resource")
			RandomAccessFile blockchkFileRaf = new RandomAccessFile(blockchkFile, "rw");
			this.blockChecksumChannel = blockchkFileRaf.getChannel();
		} catch (FileNotFoundException e) {
			// 不会抛出该异常
		}
			
		this.blockSize = blockSize;
		this.blockCache = blockCache;
		this.checksumGenerator = checksumGenerator;
		if (blockCache) {
			cache = new ConcurrentLRUCache<Long, ByteBuffer>(MAX_CAPACITY);
		}
	}

	public boolean isBlockCache() {
		return blockCache;
	}

	public int getBlockSize() {
		return blockSize;
	}

	/**
	 * 关闭文件
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		fileChannel.close();
		blockChecksumChannel.close();
	}

	/**
	 * 删除文件
	 * @throws IOException
	 */
	public void deleteFiles() throws IOException {
		this.file.delete();
		this.blockChecksumFile.delete();
	}
	/**
	 * 返回文件大小
	 * 
	 * @return
	 * @throws IOException
	 */
	public long size() throws IOException {
		return fileChannel.size();
	}

	/**
	 * 读取数据到字节数组中
	 * 
	 * @param buffer
	 *            需要存储的数据缓存
	 * @param position
	 *            读取位置
	 * @param checksum
	 *            是否要校验数据
	 * @return 返回读取的数据长度
	 * @throws IOException
	 *             文件读取异常
	 * @throws ChecksumException
	 *             校验错误抛出的异常
	 */
	public int read(ByteBuffer buffer, long position, boolean checksum)
			throws IOException, ChecksumException {
		// 文件为空
		if (size() == 0) {
			return -1;
		}
		// 最大读取的数据量
		int maxSize = buffer.remaining();
		// 读取的数据长度
		int count = 0;
		// 获取需要读取的数据对应的文件块的位置
		long[] blockIndexes = getReadBlockIndexes(position, maxSize);
		for (int i = 0; i < blockIndexes.length; ++i) {
			ByteBuffer blockDataBuffer = getBlockData(blockIndexes[i], checksum);
			int oldPos = blockDataBuffer.position();
			int oldLimit = blockDataBuffer.limit();
			if (i == 0) {
				// 要从数据块中读取的起始位置
				blockDataBuffer.position((int) (position % blockSize));
				// 设置第一个block块的读书数据大小限制
				if (maxSize < blockDataBuffer.remaining()) {
					blockDataBuffer.limit(blockDataBuffer.position() + maxSize);
				}
			} else if (i == blockIndexes.length - 1) {// 最后一块文件块，未必需要读取全部
			// 需要读取的剩余大小
				int s = maxSize - count;
				if (s < blockDataBuffer.remaining()) {
					blockDataBuffer.limit(s);
				}
			}
			// 更新已经读取的数据量
			count += blockDataBuffer.remaining();
			buffer.put(blockDataBuffer);
			blockDataBuffer.position(oldPos);
			blockDataBuffer.limit(oldLimit);

		}
		return count;
	}

	/**
	 * 读取相应的文件块
	 * 
	 * @param blockIndex
	 *            文件块位置
	 * @param checksum
	 *            是否需要校验数据
	 * @return
	 * @throws IOException
	 * @throws ChecksumException
	 */
	private ByteBuffer getBlockData(long blockIndex, boolean checksum)
			throws IOException, ChecksumException {
		ByteBuffer dataBuffer = null;
		// 从缓存中获取该文件块
		if (blockCache) {
			dataBuffer = cache.get(blockIndex);
		}
		// 缓存中不存在该块
		if (dataBuffer == null) {
			// 创建缓冲区
			dataBuffer = ByteBuffer.allocate(blockSize);
			// 已经读取的大小
			int ct = 0;
			do {
				// 读取数据
				int n = fileChannel.read(dataBuffer, blockIndex * blockSize + ct);
				// 读到末尾
				if (n == -1) {
					break;
				}
				ct += n;
			} while (ct < blockSize);// 直到读满一个文件块的容量
			// 未读取到任何数据
			if (ct == 0) {
				return null;
			}
			dataBuffer.flip();
			// 是否需要校验
			if (checksum) {
				// 读取校验码
				ByteBuffer chksumBuffer = ByteBuffer.allocate(CHECKSUM_SIZE);
				int c = 0;
				do {
					// 读取数据
					int n = blockChecksumChannel.read(chksumBuffer, blockIndex * CHECKSUM_SIZE + c);
					// 读到末尾
					if (n == -1) {
						break;
					}
					c += n;
				} while (c < CHECKSUM_SIZE);// 直到读满一个校验码
				// 读取不到校验码
				if (c == 0) {
					throw new ChecksumException("can not read checksum");
				}
				chksumBuffer.flip();
				long chksum = chksumBuffer.getLong();
				// 计算数据的校验码
				byte[] b = dataBuffer.array();
				Checksum cksum = checksumGenerator.createChecksum();
				cksum.update(b, 0, b.length);
				long chksum2 = cksum.getValue();
				// 校验码错误
				if (chksum != chksum2) {
					throw new ChecksumException();
				}
			}
			if (blockCache) {
				// 存入缓存
				cache.put(blockIndex, dataBuffer);
			}
		}
		dataBuffer.rewind();
		return dataBuffer;
	}

	/**
	 * 获取需要读取的数据对应的文件块的位置
	 * 
	 * @param pos
	 *            文件位置
	 * @param size
	 *            要获取的长度
	 * @return
	 * @throws IOException
	 */
	private long[] getReadBlockIndexes(long pos, int size) throws IOException {
		long fileSize = size();
		// 读取位置大于文件
		if (pos >= fileSize) {
			throw new IOException("out of file");
		}
		// 起始文件块的位置
		long startBlockIndex = pos / blockSize;
		// 确定结束的块位置
		long endBlockIndex = (pos + size) / blockSize;
		// 最后一个文件块的索引位置
		long lastBlockIndex = (long) Math.ceil((double) fileSize / blockSize) - 1;
		// 超出文件
		if (endBlockIndex > lastBlockIndex) {
			endBlockIndex = lastBlockIndex;
		}
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
	 * 在指定位置写入数据
	 * 
	 * @param data
	 *            数据缓冲区
	 * @param position
	 *            位置
	 * @return
	 * @throws IOException
	 */
	public int write(ByteBuffer dataBuffer, long position) throws IOException {
		// 文件大小
		long fileSize = size();
		// 写入位置超出文件尾
		if (position > fileSize) {
			throw new IOException("out of file");
		}
		// 记录缓冲区的起始位置
		int startPos = dataBuffer.position();
		// 保存缓冲区限制
		int oldLimit = dataBuffer.limit();
		// 需要写入的数据长度
		int dataSize = dataBuffer.remaining();
		// 获取需要更新的数据块索引
		long[] blockIndexes = getWriteBlockIndexes(position, dataSize);
		// 生成校验码
		long[] checksums = generateChecksum(dataBuffer, position, blockIndexes);
		// 清空缓存
		if (blockCache) {
			for (int i = 0; i < blockIndexes.length; ++i) {
				cache.remove(blockIndexes[i]);
			}
		}
		
		dataBuffer.position(startPos);
		dataBuffer.limit(oldLimit);
		// 写入的数据
		int written = fileChannel.write(dataBuffer, position);
		
		ByteBuffer chksumBuffer = ByteBuffer.allocate(CHECKSUM_SIZE * checksums.length);
		for (int i = 0; i < checksums.length; ++i) {
			chksumBuffer.putLong(checksums[i]);
		}
		chksumBuffer.flip();
		// 更新校验码
		blockChecksumChannel.write(chksumBuffer, blockIndexes[0] * CHECKSUM_SIZE);
		return written;
	}

	/**
	 * 生成校验码
	 * @param dataBuffer
	 * @param position
	 * @param blockIndexes 
	 * @return
	 * @throws IOException  
	 */
	private long[] generateChecksum(ByteBuffer dataBuffer, long position, long[] blockIndexes) throws IOException {
		// 保存缓冲区限制
		int oldLimit = dataBuffer.limit();
		long[] checksums = new long[blockIndexes.length];
		for (int i = 0; i < blockIndexes.length; ++i) {
			long blockIndex = blockIndexes[i];
			ByteBuffer buffer = null;
			try {
				buffer = getBlockDataOrEmptyBuffer(blockIndex, false);
			} catch (ChecksumException e) {
				// 不校验数据，不会抛出异常
			}
			if (i == 0) {// 第一个数据块，根据position更新部分数据
				// 第一个数据块更新位置
				int pos = (int) (position % blockSize);
				buffer.position(pos);
				if (dataBuffer.remaining() > buffer.remaining()) {
					dataBuffer.limit(buffer.remaining());
				}
			} else if (i == blockIndexes.length - 1) {// 最后一个数据块
				dataBuffer.limit(oldLimit);
			} else {
				dataBuffer.limit(dataBuffer.position() + blockSize);
			}
			buffer.put(dataBuffer);
			buffer.flip();
			
			Checksum chksum = checksumGenerator.createChecksum();
			byte[] b = buffer.array();
			chksum.update(b, 0, b.length);
			checksums[i] = chksum.getValue();
		}
		return checksums;
	}

	/**
	 * 获取对应的块数据，如果该块超出文件，那么返回空缓冲区
	 * @param blockIndex
	 * @param checksum
	 * @return
	 * @throws IOException
	 * @throws ChecksumException 
	 */
	private ByteBuffer getBlockDataOrEmptyBuffer(long blockIndex, boolean checksum) throws IOException, ChecksumException {
		if (blockIndex * blockSize < size()) {
			return getBlockData(blockIndex, checksum);
		}
		return ByteBuffer.allocate(blockSize);
	}
	
	/**
	 * 获取即将写入的数据对应的文件块的位置
	 * 
	 * @param pos
	 *            文件位置
	 * @param size
	 *            要获取的长度
	 * @return
	 * @throws IOException
	 */
	private long[] getWriteBlockIndexes(long pos, int size) throws IOException {
		// 起始文件块的位置
		long startBlockIndex = pos / blockSize;
		// 确定结束的块位置
		long endBlockIndex = (pos + size) / blockSize;
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
	 * 获取校验和文件名
	 * @param fileName
	 * @return
	 */
	public static String getChecksumFilename(String fileName) {
		return fileName + BLOCK_SUFFIX;
	}
}
