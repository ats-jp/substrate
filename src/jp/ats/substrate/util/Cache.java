package jp.ats.substrate.util;

/**
 * @author 千葉 哲嗣
 */
public interface Cache<K, V> {

	void ensureCapacity(int capacity);

	int getCapacity();

	int size();

	V get(K key);

	void cache(K key, V value);

	boolean containsKey(K key);

	V remove(K key);

	void clear();

	void shrink();
}
