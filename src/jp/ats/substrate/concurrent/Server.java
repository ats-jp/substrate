package jp.ats.substrate.concurrent;

import jp.ats.substrate.U;
import jp.ats.substrate.concurrent.Acceptor.ShutdownNotice;
import jp.ats.substrate.io.IOStream;

/**
 * サービスを行う主体となるクラスです。
 *
 * @author 千葉 哲嗣
 */
public class Server {

	private final Channel channel;

	private final Acceptor acceptor;

	private final Object lock = new Object();

	private boolean acceptorShutdowned = false;

	private IOStreamServiceHandlerFactory factory;

	/**
	 * 起動-停止スレッドが使用
	 */
	public Server(Acceptor acceptor) {
		this.channel = createChannel();
		this.acceptor = acceptor;
		this.factory = new DummyServiceHandlerFactory();
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public Server(Acceptor acceptor, IOStreamServiceHandlerFactory factory) {
		this.channel = createChannel();
		this.acceptor = acceptor;
		this.factory = factory;
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public synchronized void setServiceHandlerFactory(
		IOStreamServiceHandlerFactory factory) {
		this.factory = factory;
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * 終了します。 受け付けた全てのリクエストを処理するまで、終了しません。 起動-停止スレッドが使用
	 */
	public void shutdown() {
		shutdownAcceptor();
		channel.shutdown();
	}

	/**
	 * 強制終了します。 リクエストを処理中でも、極力終了させようとします。 起動-停止スレッドが使用
	 */
	public void terminate() {
		shutdownAcceptor();
		channel.terminate();
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	public synchronized void service() {
		new ServiceThread().start();
	}

	/**
	 * {@link Acceptor} が停止しているかどうか検査します。
	 *
	 * @return 停止している場合、 true
	 */
	public boolean isAcceptorShutdowned() {
		synchronized (lock) {
			return acceptorShutdowned;
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected Channel createChannel() {
		return new Channel();
	}

	private synchronized IOStreamServiceHandlerFactory getServiceHandlerFactory() {
		return factory;
	}

	private void shutdownAcceptor() {
		acceptor.shutdown();
		synchronized (lock) {
			try {
				while (!acceptorShutdowned) {
					lock.wait();
				}
			} catch (InterruptedException e) {}
		}
	}

	private class ServiceThread extends Thread {

		/**
		 * 待受スレッドが使用
		 */
		@Override
		public void run() {
			long counter = 0;
			try {
				while (true) {
					IOStream stream = acceptor.accept();
					IOStreamServiceHandler handler = getServiceHandlerFactory().newInstance();
					handler.setId(counter);
					counter++;
					handler.setIOStream(stream);
					channel.add(handler);
				}
			} catch (ShutdownNotice notice) {} finally {
				synchronized (lock) {
					acceptorShutdowned = true;
					lock.notifyAll();
				}
			}
		}
	}

	private static final class DummyServiceHandlerFactory
		implements IOStreamServiceHandlerFactory {

		@Override
		public IOStreamServiceHandler newInstance() {
			return new IOStreamServiceHandler() {

				@Override
				protected void serviceInternal(int workerID) {}
			};
		}
	}
}
