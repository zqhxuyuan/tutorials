package com.github.kowshik.bigo.concurrency;

/**
 * Defines a com.github.kowshik.bigo.common interface for different types of read writer locks.
 */
public interface ReadWriteLock {
	void readLock();

	void readUnlock();

	void writeLock();

	void writeUnlock();
}
