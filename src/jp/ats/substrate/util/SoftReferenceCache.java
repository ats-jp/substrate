package jp.ats.substrate.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class SoftReferenceCache<K, V> implements Cache<K, V> {

	private final Cache<K, SoftReferenceValue> cache;

	private static final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();

	private static final Object lock = new Object();

	private static Thread autoShrinkThread;

	static {
		startRemoval();
	}

	public static void startRemoval() {
		synchronized (lock) {
			if (autoShrinkThread != null) return;
			autoShrinkThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (true) {
							@SuppressWarnings("rawtypes")
							SoftReferenceCache.SoftReferenceValue value = (SoftReferenceCache.SoftReferenceValue) referenceQueue.remove();
							value.removeSelf();
						}
					} catch (InterruptedException e) {} finally {
						synchronized (lock) {
							autoShrinkThread = null;
						}
					}
				}
			},
				SoftReferenceCache.class.getName() + ".autoShrinkThread");
			autoShrinkThread.setDaemon(true);
			autoShrinkThread.start();
		}
	}

	public static void stopRemoval() {
		synchronized (lock) {
			if (autoShrinkThread != null) autoShrinkThread.interrupt();
		}
	}

	public static <K, V> SoftReferenceCache<K, V> newInstance() {
		return new SoftReferenceCache<K, V>();
	}

	public static <K, V> SoftReferenceCache<K, V> newInstance(Cache<K, V> cache) {
		return new SoftReferenceCache<K, V>(cache);
	}

	/**
	 * パラメータのキャッシュの振る舞いに {@link SoftReference} の機能を追加したキャッシュを生成します
	 *
	 * @param cache
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SoftReferenceCache(Cache cache) {
		cache.clear();
		if (cache instanceof SynchronizedCache) {
			this.cache = cache;
		} else {
			this.cache = SynchronizedCache.newInstance(cache);
		}
	}

	/**
	 * サイズに制限のない {@link SoftReference} の機能を持つキャッシュを生成します
	 */
	public SoftReferenceCache() {
		this(new MapCache<K, V>());
	}

	@Override
	public void ensureCapacity(int capacity) {
		cache.ensureCapacity(capacity);
	}

	@Override
	public int getCapacity() {
		return cache.getCapacity();
	}

	@Override
	public int size() {
		return cache.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		SoftReferenceValue reference = cache.get(key);
		if (reference == null) return null;
		return (V) reference.get();
	}

	@Override
	public void cache(K key, V value) {
		cache.cache(key, new SoftReferenceValue(key, value));
	}

	@Override
	public boolean containsKey(K key) {
		return cache.containsKey(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public V remove(K key) {
		SoftReferenceValue reference = cache.remove(key);
		if (reference == null) return null;
		V value = (V) reference.get();
		reference.clear();
		return value;
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public void shrink() {
		cache.shrink();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private class SoftReferenceValue extends SoftReference<Object> {

		private final K key;

		private SoftReferenceValue(K key, V value) {
			super(value, referenceQueue);
			this.key = key;
		}

		private void removeSelf() {
			cache.remove(key);
		}
	}

	private static class MapCache<K, V> implements Cache<K, V> {

		private final Map<K, V> map = new HashMap<>();

		@Override
		public void cache(K key, V value) {
			map.put(key, value);
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public boolean containsKey(Object key) {
			return map.containsKey(key);
		}

		@Override
		public void ensureCapacity(int capacity) {}

		@Override
		public V get(K key) {
			return map.get(key);
		}

		@Override
		public int getCapacity() {
			return Integer.MAX_VALUE;
		}

		@Override
		public V remove(K key) {
			return map.remove(key);
		}

		@Override
		public void shrink() {}

		@Override
		public int size() {
			return map.size();
		}
	}
}
