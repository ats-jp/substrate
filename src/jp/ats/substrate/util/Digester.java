package jp.ats.substrate.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 千葉 哲嗣
 */
public class Digester {

	public static String digest(String algorithm, String message)
		throws NoSuchAlgorithmException {
		MessageDigest digester = MessageDigest.getInstance(algorithm);
		byte[] digested = digester.digest(message.getBytes());
		StringBuilder builder = new StringBuilder();
		for (byte b : digested) {
			builder.append(Integer.toString((b & 0xf0) >> 4, 16));
			builder.append(Integer.toString(b & 0x0f, 16));
		}

		return builder.toString();
	}
}
