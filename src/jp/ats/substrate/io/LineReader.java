package jp.ats.substrate.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class LineReader {

	private final Charset charset;

	private InputStream input;

	public LineReader(InputStream input) {
		this.input = input;
		this.charset = Charset.defaultCharset();
	}

	public LineReader(InputStream input, Charset charset) {
		this.input = input;
		this.charset = charset;
	}

	public String readLine(boolean includesLineSeparator) throws IOException {
		byte[] bytes = new byte[U.BUFFER_SIZE];
		int length = 0;
		for (int i = 0; i < bytes.length; i++) {
			int b = input.read();

			if (b == -1) {
				if (length == 0) return null;
				return new String(bytes, 0, length, charset);
			}

			if (b == '\r') {
				if (includesLineSeparator) {
					bytes[i] = '\r';
					length++;
				}

				input.mark(1);
				if (input.read() == '\n') {
					if (includesLineSeparator) {
						bytes[i + 1] = '\n';
						length++;
					}
				} else {
					input.reset();
				}

				return new String(bytes, 0, length, charset);
			} else if (b == '\n') {
				if (includesLineSeparator) {
					bytes[i] = '\n';
					length++;
				}

				return new String(bytes, 0, length, charset);
			}

			bytes[i] = (byte) b;
			length++;
		}

		String next = readLine(includesLineSeparator);
		return new String(bytes, 0, length, charset)
			+ (next == null ? "" : next);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
