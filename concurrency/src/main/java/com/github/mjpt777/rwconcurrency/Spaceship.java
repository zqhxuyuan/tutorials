package com.github.mjpt777.rwconcurrency;

/**
 * Interface to a concurrent representation of a ship that can move around
 * a 2 dimensional space with updates and reads performed concurrently.
 *
 * 并发接口，表示太空船可以在2维的空间中移动位置;并且同时更新读取位置
 */
public interface Spaceship
{
    /**
     * Read the position of the spaceship into the array of coordinates provided.
     * 读取太空船的位置到参数数组 coordinates 中
     *
     * @param coordinates into which the x and y coordinates should be read. 保存读取到的XY坐标
     * @return the number of attempts made to read the current state. 当前的状态
     */
    int readPosition(final int[] coordinates);

    /**
     * Move the position of the spaceship by a delta to the x and y coordinates.
     * 通过增加XY的值表示移动太空船的位置
     *
     * @param xDelta delta by which the spaceship should be moved in the x-axis. x坐标轴上移动的增量
     * @param yDelta delta by which the spaceship should be moved in the y-axis. y坐标轴上移动的增量
     * @return the number of attempts made to write the new coordinates.
     */
    int move(final int xDelta, final int yDelta);
}
