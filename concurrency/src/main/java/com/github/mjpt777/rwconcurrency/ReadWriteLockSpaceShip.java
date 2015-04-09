package com.github.mjpt777.rwconcurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockSpaceShip implements Spaceship
{
    //读写锁
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    //读锁
    private final Lock readLock = lock.readLock();
    //写锁
    private final Lock writeLock = lock.writeLock();

    private int x;
    private int y;

    @Override
    public int readPosition(final int[] coordinates)
    {
        readLock.lock();
        try
        {
            coordinates[0] = x;
            coordinates[1] = y;
        }
        finally
        {
            readLock.unlock();
        }

        return 1;
    }

    @Override
    public int move(final int xDelta, final int yDelta)
    {
        writeLock.lock();
        try
        {
            x += xDelta;
            y += yDelta;
        }
        finally
        {
            writeLock.unlock();
        }

        return 1;
    }
}
