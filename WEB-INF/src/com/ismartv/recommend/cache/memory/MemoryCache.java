package com.ismartv.recommend.cache.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 采用LRU算法的一个Cache
 * 
 * @Title: MFP_SERVER
 * @FileName: MemoryCache.java
 * @Description:
 * @Copyright: Copyright (c) 2008
 * @Company:
 * @author Sun Jue
 * @Create Date: 2011-7-25
 */
public class MemoryCache<K, V> {
	public static final int DEFAULT_CACHE_MILLIS = 60000;

	public MemoryCache(int maxObjects) {
		this.maxObjects = maxObjects;
		mapCache = new ConcurrentHashMap<K, CacheValue>(this.maxObjects, 1.0f);
	}

	/** if the max is less than 0, there is no limit! */
	private int maxObjects;

	/** record size to avoid having to iterate */
	private int size = 0;

	private Map<K, CacheValue> mapCache = null;// new ConcurrentHashMap<K,
												// CacheValue>();

	/** LRU double linked list head node */
	private CacheValue first;

	/** LRU double linked list tail node */
	private CacheValue last;

	public void addElement(K key, V value) {

		CacheValue node = new CacheValue(key, value);

		synchronized (this) {

			this.addFirst(node);

			CacheValue old = mapCache.put(key, node);

			if (old != null && (this.getFirst()).key.equals(old.key)) {
				this.remove(old);
			}
		}

		int size = mapCache.size();

		if (this.maxObjects >= 0 && size > this.maxObjects) {
			int chunkSizeCorrected = size - this.maxObjects;
			for (int i = 0; i < chunkSizeCorrected; i++) {
				synchronized (this) {
					if (this.last != null) {
						CacheValue l = this.removeLast();
						mapCache.remove(l.key);
					}
				}
			}
		}
	}

	public void addElement(K key, V value, int cacheMillis) {
		CacheValue node = new CacheValue(key, value, cacheMillis);

		synchronized (this) {

			this.addFirst(node);

			CacheValue old = mapCache.put(key, node);

			if (old != null && (this.getFirst()).key.equals(old.key)) {
				this.remove(old);
			}
		}

		int size = mapCache.size();

		if (this.maxObjects >= 0 && size > this.maxObjects) {
			int chunkSizeCorrected = size - this.maxObjects;
			for (int i = 0; i < chunkSizeCorrected; i++) {
				synchronized (this) {
					if (this.last != null) {
						CacheValue l = this.removeLast();
						mapCache.remove(l.key);
					}
				}
			}
		}
	}

	public V getElement(K key) {
		CacheValue cv = mapCache.get(key);
		if (cv != null) {
			if (cv.time >= System.currentTimeMillis()) {
				this.makeFirst(cv);
				return cv.value;
			} else {
				removeElement(key);
			}
		}
		return null;
	}

	public V removeElement(K key) {
		CacheValue cv = mapCache.remove(key);
		if (cv != null) {
			this.remove(cv);
			if (cv.time >= System.currentTimeMillis()) {
				return cv.value;
			}
		}
		return null;
	}

	/**
	 * This removes all the items. It clears the map and the double linked list.
	 * <p>
	 * 
	 * @see java.util.Map#clear()
	 */
	public synchronized void clear() {
		removeAll();
		mapCache.clear();
		this.size = mapCache.size();
	}

	/**
	 * Adds a new node to the end of the link list.
	 * <p>
	 * 
	 * @param me
	 *            The feature to be added to the Last
	 */
	public synchronized void addLast(CacheValue me) {
		if (first == null) {
			// empty list.
			first = me;
		} else {
			last.next = me;
			me.prev = last;
		}
		last = me;
		size++;
	}

	/**
	 * Adds a new node to the start of the link list.
	 * <p>
	 * 
	 * @param me
	 *            The feature to be added to the First
	 */
	public synchronized void addFirst(CacheValue me) {
		if (last == null) {
			// empty list.
			last = me;
		} else {
			first.prev = me;
			me.next = first;
		}
		first = me;
		size++;
		return;
	}

	/**
	 * Returns the last node from the link list, if there are any nodes.
	 * <p>
	 * 
	 * @return The last node.
	 */
	public synchronized CacheValue getLast() {
		return last;
	}

	/**
	 * Removes the specified node from the link list.
	 * <p>
	 * 
	 * @return DoubleLinkedListNode, the first node.
	 */
	public synchronized CacheValue getFirst() {
		return first;
	}

	/**
	 * Moves an existing node to the start of the link list.
	 * <p>
	 * 
	 * @param ln
	 *            The node to set as the head.
	 */
	public synchronized void makeFirst(CacheValue ln) {
		if (ln.prev == null) {
			// already the first node. or not a node
			return;
		}
		ln.prev.next = ln.next;

		if (ln.next == null) {
			// last but not the first.
			last = ln.prev;
			last.next = null;
		} else {
			// neither the last nor the first.
			ln.next.prev = ln.prev;
		}
		first.prev = ln;
		ln.next = first;
		ln.prev = null;
		first = ln;
	}

	/**
	 * Moves an existing node to the end of the link list.
	 * <p>
	 * 
	 * @param ln
	 *            The node to set as the last.
	 */
	public synchronized void makeLast(CacheValue ln) {
		if (ln.next == null) {
			// already the last node. or not a node
			return;
		}
		ln.next.prev = ln.prev;

		if (ln.prev == null) {
			// first but not the last.
			first = ln.next;
			first.prev = null;
		} else {
			// neither the last nor the first.
			ln.prev.next = ln.next;
		}
		last.next = ln;
		ln.prev = last;
		ln.next = null;
		last = ln;
	}

	/**
	 * Remove all of the elements from the linked list implementation.
	 */
	public synchronized void removeAll() {
		for (CacheValue me = first; me != null;) {
			if (me.prev != null) {
				me.prev = null;
			}
			CacheValue next = me.next;
			me = next;
		}
		first = last = null;
		// make sure this will work, could be add while this is happening.
		size = 0;
	}

	/**
	 * Removes the specified node from the link list.
	 * <p>
	 * 
	 * @param me
	 *            Description of the Parameter
	 * @return true if an element was removed.
	 */
	public synchronized boolean remove(CacheValue me) {
		if (me.next == null) {
			if (me.prev == null) {
				// Make sure it really is the only node before setting head and
				// tail to null. It is possible that we will be passed a node
				// which has already been removed from the list, in which case
				// we should ignore it

				if (me == first && me == last) {
					first = last = null;
				}
			} else {
				// last but not the first.
				last = me.prev;
				last.next = null;
				me.prev = null;
			}
		} else if (me.prev == null) {
			// first but not the last.
			first = me.next;
			first.prev = null;
			me.next = null;
		} else {
			// neither the first nor the last.
			me.prev.next = me.next;
			me.next.prev = me.prev;
			me.prev = me.next = null;
		}
		size--;

		return true;
	}

	/**
	 * Removes the specified node from the link list.
	 * <p>
	 * 
	 * @return The last node if there was one to remove.
	 */
	public synchronized CacheValue removeLast() {
		CacheValue temp = last;
		if (last != null) {
			remove(last);
		}
		return temp;
	}

	/**
	 * Returns the size of the list.
	 * <p>
	 * 
	 * @return int
	 */
	public synchronized int size() {
		return size;
	}

	private class CacheValue implements Delayed {
		K key;
		V value;
		long time;
		/** Double Linked list references */
		CacheValue prev;

		/** Double Linked list references */
		CacheValue next;

		public CacheValue(K key, V value) {
			this.key = key;
			this.value = value;
			this.time = System.currentTimeMillis() + DEFAULT_CACHE_MILLIS;
		}

		public CacheValue(K key, V value, int cacheMillis) {
			this.key = key;
			this.value = value;
			this.time = System.currentTimeMillis() + cacheMillis;
		}

		public int compareTo(Delayed o) {
			return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o
					.getDelay(TimeUnit.MILLISECONDS));
			// return 0;
		}

		public long getDelay(TimeUnit unit) {
			return unit.convert((time - System.currentTimeMillis()),
					TimeUnit.MILLISECONDS);
			// return 0;
		}
	}

	public int getMaxObjects() {
		return maxObjects;
	}

	public void setMaxObjects(int maxObjects) {
		this.maxObjects = maxObjects;
	}
}