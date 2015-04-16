package com.zqh.hadoop.nimbus.utils;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * A ZooKeeper Watcher class that contains functions to let a user know then an
 * {@link EventType#NodeDataChanged} event occurs. <br>
 * <br>
 * Also keeps track of the elapsed time since it received the NodeDataChanged
 * event.<br>
 * <br>
 * Used by the {@link NimbusSafetyNet} to control heartbeats from Cachelets.
 */
public class DataZNodeWatcher implements Watcher {
	private boolean triggered = false, deleted = false, notified = false;;
	private long triggerTime = 0L;

	/**
	 * Initializes a new
	 */
	public DataZNodeWatcher() {
		triggerTime = System.currentTimeMillis();
	}

	/**
	 * Sets internal booleans based on the NodeDataChanged event and NodeDeleted event.
	 */
	@Override
	public void process(WatchedEvent event) {
		if (event.getType().equals(EventType.NodeDeleted)) {
			deleted = true;
		} else {
			triggered = true;
		}
	}

	/**
	 * Gets a value based on if the event has been triggered. with an event
	 * other than {@link EventType#NodeDeleted}
	 * 
	 * @return If this event has been triggered.
	 */
	public boolean isTriggered() {
		return triggered;
	}

	/**
	 * Gets a Boolean value based on if the event has been triggered with a
	 * {@link EventType#NodeDeleted}.
	 * 
	 * @return If the ZNode this watcher was watching has been deleted.
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * Resets this instance as it if were first created.
	 */
	public void reset() {
		triggered = notified = deleted = false;
		triggerTime = System.currentTimeMillis();
	}

	/**
	 * Gets the amount of time in milliseconds that has elapsed since the most
	 * recent event.
	 * 
	 * @return The elapsed time.
	 */
	public long getElapsedTriggerTime() {
		return System.currentTimeMillis() - triggerTime;
	}

	/**
	 * Sets whether or not the Safety Net has already notified users that the
	 * Cachelet has gone stale.
	 * 
	 * @param notified
	 */
	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	/**
	 * Gets a value determined whether or not the Safety net has already
	 * notified users that a ZNode has gone stale.
	 * 
	 * @return True if notified, false otherwise.
	 */
	public boolean getNotified() {
		return notified;
	}
}