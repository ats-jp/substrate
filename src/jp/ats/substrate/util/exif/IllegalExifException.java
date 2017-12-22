package jp.ats.substrate.util.exif;

/**
 * @author 千葉 哲嗣
 */
public class IllegalExifException extends RuntimeException {

	private static final long serialVersionUID = -4669588368235683372L;

	public IllegalExifException(String message) {
		super(message);
	}
}
