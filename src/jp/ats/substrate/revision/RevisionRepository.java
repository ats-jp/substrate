package jp.ats.substrate.revision;

import java.util.Set;

/**
 * @author 千葉 哲嗣
 */
public interface RevisionRepository<K> {

	Revision get(K key);

	void put(K key, Revision revision);

	Revision remove(K key);

	void clear();

	Set<K> keySet();

	void lockAll();

	void unlockAll();

	void lock(K key);

	void unlock(K key);
}
