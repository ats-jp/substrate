package jp.ats.substrate.concurrent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import jp.ats.substrate.U;
import jp.ats.substrate.io.IOStream;

/**
 * @author 千葉 哲嗣
 */
public class SocketAcceptor implements Acceptor {

	private final ServerSocket socket;

	private final int clientTimeout;

	private boolean shutdowned = false;

	public SocketAcceptor(
		String address,
		int port,
		int backlog,
		int interval,
		int clientTimeout) throws IOException {
		socket = new ServerSocket(port, backlog, InetAddress.getByName(address));
		socket.setSoTimeout(interval);
		this.clientTimeout = clientTimeout;
	}

	@Override
	public IOStream accept() throws ShutdownNotice {
		try {
			while (true) {
				try {
					Socket client = socket.accept();
					client.setSoTimeout(clientTimeout);
					return new SocketIOStream(client);
				} catch (SocketTimeoutException e) {
					if (isShutdowned()) {
						close();
						break;
					}
				}
			}
		} catch (Throwable t) {
			//OutOfMemoryError など、不測の事態のときでも
			//とりあえずソケットを閉じてみる
			try {
				socket.close();
			} catch (IOException e) {
				//t の方が障害原因なので、ここでの例外は無視する
			}
			throw new RuntimeException(t);
		}
		throw new ShutdownNotice();
	}

	@Override
	public void shutdown() {
		setShutdowned(true);
	}

	public void close() {
		U.close(socket);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private synchronized boolean isShutdowned() {
		return shutdowned;
	}

	private synchronized void setShutdowned(boolean shutdowned) {
		this.shutdowned = shutdowned;
	}
}
