package jp.ats.substrate.concurrent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class Channel {

	/**
	 * リクエストの最大貯留数として指定できる最小値
	 */
	public static final int MINIMUM_POOL_SIZE = 1;

	/**
	 * ワーカスレッドの最大数として指定できる最小値
	 */
	public static final int MINIMUM_WORKER_COUNT = 1;

	private final Object lock = new Object();

	private final Set<Worker> workers = new HashSet<>();

	private final LinkedList<ServiceHandler> handlers = new LinkedList<>();

	private Thread starter;

	private int maximumPoolSize = MINIMUM_POOL_SIZE;

	private int maximumWorkerCount = MINIMUM_WORKER_COUNT;

	private int running = 0;

	private boolean closed = false;

	private boolean terminated = false;

	private ChannelListener listener = new NullChannelListener();

	public Channel() {
		startStarter();
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public void setListener(ChannelListener listener) {
		synchronized (lock) {
			this.listener = listener;
			listener.receiveMaximumPoolSize(maximumPoolSize);
			listener.receiveMaximumWorkerCount(maximumWorkerCount);
			listener.receiveCurrentPoolSize(handlers.size());
			listener.receiveCurrentWorkerCount(workers.size());
			listener.receiveRunningWorkerCount(running);
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public void addMaximumPoolSize(int addition) {
		synchronized (lock) {
			setMaximumPoolSize(maximumPoolSize + addition);
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public void addMaximumWorkerCount(int addition) {
		synchronized (lock) {
			setMaximumWorkerCount(maximumWorkerCount + addition);
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public void setMaximumPoolSize(int size) {
		if (size < MINIMUM_POOL_SIZE) size = MINIMUM_POOL_SIZE;

		synchronized (lock) {
			lock.notifyAll();
			if (maximumPoolSize == size) return;
			maximumPoolSize = size;
			listener.receiveMaximumPoolSize(size);
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public void setMaximumWorkerCount(int count) {
		if (count < MINIMUM_WORKER_COUNT) count = MINIMUM_WORKER_COUNT;

		synchronized (lock) {
			lock.notifyAll();
			if (maximumWorkerCount == count) return;
			maximumWorkerCount = count;
			listener.receiveMaximumWorkerCount(count);
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public int getMaximumWorkerCount() {
		synchronized (lock) {
			return maximumWorkerCount;
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public int getMaximumPoolSize() {
		synchronized (lock) {
			return maximumPoolSize;
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public int getCurrentPoolSize() {
		synchronized (lock) {
			return handlers.size();
		}
	}

	public boolean canAdd() {
		synchronized (lock) {
			return handlers.size() < maximumPoolSize;
		}
	}

	/**
	 * 待受スレッドが使用
	 */
	public void add(ServiceHandler handler) {
		synchronized (lock) {
			while (handlers.size() >= maximumPoolSize) {
				try {
					lock.wait();
				} catch (InterruptedException e) {}
			}

			if (terminated) {
				handler.terminate();
				return;
			}

			handlers.add(handler);
			listener.receiveCurrentPoolSize(handlers.size());
			lock.notifyAll();
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public void shutdown() {
		Worker[] workerArray;
		synchronized (lock) {
			closed = true;
			maximumWorkerCount = 0;
			workerArray = workers.toArray(new Worker[workers.size()]);
			lock.notifyAll();
		}

		try {
			for (Worker worker : workerArray)
				worker.join();
		} catch (InterruptedException e) {}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public void terminate() {
		Worker[] workerArray;
		synchronized (lock) {
			for (ServiceHandler handler : handlers)
				handler.terminate();
			handlers.clear();
			listener.receiveCurrentPoolSize(0);
			closed = true;
			terminated = true;
			maximumPoolSize = MINIMUM_POOL_SIZE;
			maximumWorkerCount = 0;
			workerArray = workers.toArray(new Worker[workers.size()]);
			lock.notifyAll();
		}

		for (Worker worker : workerArray)
			worker.terminate();

		listener.receiveCurrentWorkerCount(0);
	}

	public synchronized void startStarter() {
		if (starter != null && starter.isAlive()) return;
		starter = new Thread() {

			@Override
			public void run() {
				synchronized (lock) {
					while (!closed) {
						boolean created = false;
						while (workers.size() < maximumWorkerCount) {
							created = true;
							Worker worker = createWorker();
							workers.add(worker);
							worker.start();
						}

						if (created) listener.receiveCurrentWorkerCount(maximumWorkerCount);

						try {
							lock.wait();
						} catch (InterruptedException e) {}
					}
				}
			}
		};

		starter.start();
	}

	public void sweepDeadWorkers() {
		synchronized (lock) {
			for (Iterator<Worker> i = workers.iterator(); i.hasNext();) {
				if (!i.next().isAlive()) i.remove();
			}

			listener.receiveCurrentWorkerCount(workers.size());
			lock.notifyAll();
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected Worker createWorker() {
		return new Worker(this);
	}

	/**
	 * Workerスレッドが使用
	 */
	void prepare(Worker worker) throws ClosingNotice {
		synchronized (lock) {
			while (handlers.size() == 0) {
				if (workers.size() > maximumWorkerCount) {
					workers.remove(worker);
					listener.receiveCurrentWorkerCount(workers.size());
					throw new ClosingNotice();
				}

				try {
					lock.wait();
				} catch (InterruptedException e) {}
			}

			worker.setHandler(handlers.removeFirst());
			listener.receiveCurrentPoolSize(handlers.size());
			listener.receiveRunningWorkerCount(++running);
			lock.notifyAll();
		}
	}

	/**
	 * Workerスレッドが使用
	 */
	void decrementAndNotifyRunningWorkerCount() {
		synchronized (lock) {
			listener.receiveRunningWorkerCount(--running);
		}
	}

	@SuppressWarnings("serial")
	static class ClosingNotice extends Throwable {}

	private static class NullChannelListener implements ChannelListener {

		private NullChannelListener() {}

		@Override
		public void receiveMaximumPoolSize(int size) {}

		@Override
		public void receiveMaximumWorkerCount(int count) {}

		@Override
		public void receiveCurrentPoolSize(int size) {}

		@Override
		public void receiveCurrentWorkerCount(int count) {}

		@Override
		public void receiveRunningWorkerCount(int count) {}
	}
}
