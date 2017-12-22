package jp.ats.substrate.concurrent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jp.ats.substrate.U;
import jp.ats.substrate.io.IOStream;

/**
 * @author 千葉 哲嗣
 */
public class SocketIOStream implements IOStream {

	private final Socket socket;

	public SocketIOStream(Socket socket) {
		this.socket = socket;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	public Socket getSocket() {
		return socket;
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
