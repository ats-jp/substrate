package jp.ats.substrate.concurrent;

import java.io.IOException;

import jp.ats.substrate.U;
import jp.ats.substrate.io.IOStream;

/**
 * @author 千葉 哲嗣
 */
public abstract class IOStreamServiceHandler implements ServiceHandler {

	private long id;

	private IOStream stream;

	/**
	 * Worker スレッドが使用
	 */
	protected IOStream getIOStream() {
		return stream;
	}

	/**
	 * Worker スレッドが使用
	 */
	protected long getID() {
		return id;
	}

	/**
	 * Worker スレッドが使用
	 */
	@Override
	public final void service(int workerID) {
		try {
			serviceInternal(workerID);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 起動-停止スレッドが使用
	 */
	@Override
	public void terminate() {
		try {
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected abstract void serviceInternal(int workerID);

	/**
	 * 待受スレッドが使用
	 */
	void setId(long id) {
		this.id = id;
	}

	/**
	 * 待受スレッドが使用
	 */
	void setIOStream(IOStream stream) {
		this.stream = stream;
	}
}
