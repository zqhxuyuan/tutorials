package com.zqh.hadoop.nimbus.utils;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * This is a ZooKeeper Watcher designed to notify users whenever the children of
 * a ZNode changes. Used mainly by Nimbus to handle connections between
 * Cachelets.
 */
public class ChildZNodeWatcher implements Watcher {
	private boolean triggered = false;
	private boolean deleted = false;

	/**
	 * Responds to NodeDeleted and NodeChildrenChanged events.
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
	 * Resets this Watcher so users don't have to recreate this object.
	 */
	public void reset() {
		triggered = false;
		deleted = false;
	}
}
