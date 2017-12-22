package jp.ats.substrate.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 千葉 哲嗣
 */
public class AccessKeyUtilities {

	private static final Map<Character, Character> accessKeyMap = new HashMap<>();

	static {
		String[] table = SimpleResourceReader.readLines(
			AccessKeyUtilities.class,
			"table",
			Charset.forName("UTF-8"));
		for (String line : table) {
			char[] chars = line.toCharArray();
			for (char one : chars) {
				accessKeyMap.put(one, chars[0]);
			}
		}
	}

	private AccessKeyUtilities() {}

	public static String getAccessKey(String value) {
		if (value == null) return null;
		Character accessKey = accessKeyMap.get(value.toCharArray()[0]);
		if (accessKey == null) return null;
		return accessKey.toString();
	}
}
