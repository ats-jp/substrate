package jp.ats.substrate.util;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class SynchronizedCache<K, V> implements Cache<K, V> {

	private final Cache<K, V> cache;

	public static <K, V> SynchronizedCache<K, V> newInstance(Cache<K, V> cache) {
		return new SynchronizedCache<K, V>(cache);
	}

	public SynchronizedCache(Cache<K, V> cache) {
		this.cache = cache;
	}

	@Override
	public synchronized void ensureCapacity(int capacity) {
		cache.ensureCapacity(capacity);
	}

	@Override
	public synchronized int getCapacity() {
		return cache.getCapacity();
	}

	@Override
	public synchronized int size() {
		return cache.size();
	}

	@Override
	public synchronized V get(K key) {
		return cache.get(key);
	}

	@Override
	public synchronized void cache(K key, V value) {
		cache.cache(key, value);
	}

	@Override
	public boolean containsKey(K key) {
		return cache.containsKey(key);
	}

	@Override
	public synchronized V remove(K key) {
		return cache.remove(key);
	}

	@Override
	public synchronized void clear() {
		cache.clear();
	}

	@Override
	public synchronized void shrink() {
		cache.shrink();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
