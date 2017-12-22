package jp.ats.substrate.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import jp.ats.substrate.concurrent.Channel.ClosingNotice;

/**
 * @author 千葉 哲嗣
 */
public class Worker extends Thread {

	private static final AtomicInteger counter = new AtomicInteger();

	private final int id = counter.getAndIncrement();

	private final Channel channel;

	private ServiceHandler handler;

	protected Worker(Channel channel) {
		this.channel = channel;
	}

	@Override
	public final boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	/**
	 * Worker スレッドが使用
	 */
	@Override
	public void run() {
		try {
			while (true) {
				channel.prepare(this);
				try {
					getHandler().service(id);
				} finally {
					channel.decrementAndNotifyRunningWorkerCount();
					setHandler(null);
				}
			}
		} catch (ClosingNotice notice) {}
	}

	protected synchronized ServiceHandler getHandler() {
		return handler;
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	protected void terminate() {
		interrupt();
		ServiceHandler handler = getHandler();
		if (handler != null) handler.terminate();
		try {
			join();
		} catch (InterruptedException e) {}
	}

	synchronized void setHandler(ServiceHandler handler) {
		this.handler = handler;
	}
}
