package jp.ats.substrate.util;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class ByteList {

	private final byte[] buffer = new byte[U.BUFFER_SIZE];

	private byte[] values = new byte[0];

	private int index;

	public ByteList() {}

	public ByteList(byte[] initialValues) {
		add(initialValues);
	}

	public void add(byte b) {
		if (index > buffer.length - 1) {
			index = 0;
			values = U.push(values, buffer);
		}

		buffer[index] = b;
		index++;
	}

	public void add(byte... bytes) {
		for (byte b : bytes) {
			add(b);
		}
	}

	public byte[] getBytes() {
		if (index == 0) return values;
		return U.concatByteArray(values, values.length, buffer, index);
	}
}
