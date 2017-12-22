package jp.ats.substrate.util;

/**
 * @author 千葉 哲嗣
 */
public abstract class LRUQueueElement extends QueueElement {

	private LRUQueue<LRUQueueElement> queue;

	@Override
	public void remove() {
		super.remove();
		if (queue == null) return;
		queue.decrement();
		queue = null;
	}

	public void top() {
		if (queue == null) return;
		super.remove();
		queue.addFirstInternal(this);
	}

	@SuppressWarnings("unchecked")
	void setQueue(@SuppressWarnings("rawtypes") LRUQueue queue) {
		this.queue = queue;
	}
}
