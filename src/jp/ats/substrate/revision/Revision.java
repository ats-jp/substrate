package jp.ats.substrate.revision;

import java.io.Serializable;

/**
 * @author 千葉 哲嗣
 */
public class Revision implements Serializable {

	private static final long serialVersionUID = 7235542358537921617L;

	private final long value;

	public Revision(long value) {
		this.value = value;
	}

	Revision increment() {
		return new Revision(value + 1);
	}

	public long getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
