package jp.ats.substrate.revision;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 千葉 哲嗣
 */
public class SimpleRevisionRepository<K> implements RevisionRepository<K> {

	private final Map<K, Container> map = new HashMap<>();

	private boolean locked = false;

	private long current = 0;

	@Override
	public Revision get(K key) {
		synchronized (map) {
			Container container = map.get(key);
			if (container == null) {
				container = new Container(new Revision(current++));
				map.put(key, container);
			}
			return container.revision;
		}
	}

	@Override
	public void put(K key, Revision revision) {
		synchronized (map) {
			map.put(key, new Container(revision));
		}
	}

	@Override
	public Revision remove(K key) {
		synchronized (map) {
			Container container = map.remove(key);
			if (container != null) return container.revision;
			return null;
		}
	}

	@Override
	public void clear() {
		synchronized (map) {
			map.clear();
		}
	}

	@Override
	public Set<K> keySet() {
		synchronized (map) {
			return map.keySet();
		}
	}

	@Override
	public void lockAll() {
		synchronized (map) {
			while (true) {
				try {
					if (locked) {
						map.wait();
					} else {
						break;
					}
				} catch (InterruptedException e) {}
			}

			locked = true;
		}
	}

	@Override
	public void unlockAll() {
		synchronized (map) {
			map.notifyAll();
			locked = false;
		}
	}

	@Override
	public void lock(K key) {
		Container container = getContainer(key);
		if (container == null) return;
		container.lock();
	}

	@Override
	public void unlock(K key) {
		Container container = getContainer(key);
		if (container == null) return;
		container.unlock();
	}

	private Container getContainer(K key) {
		synchronized (map) {
			return map.get(key);
		}
	}

	private static class Container {

		private final Revision revision;

		private boolean locked = false;

		private Container(Revision revision) {
			this.revision = revision;
		}

		private synchronized void lock() {
			while (true) {
				try {
					if (locked) {
						wait();
					} else {
						break;
					}
				} catch (InterruptedException e) {}
			}

			locked = true;
		}

		private synchronized void unlock() {
			notifyAll();
			locked = false;
		}
	}
}
