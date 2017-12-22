package jp.ats.substrate.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class ASCIIConverter {

	private static final Map<String, String> zenkakuMap = new HashMap<>();

	private static final Map<String, String> hankakuMap = new HashMap<>();

	static {
		String[][] table = null;
		try {
			table = ConverterUtilities.createConvertTable("ascii");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (String[] element : table) {
			zenkakuMap.put(element[0], element[1]);
			hankakuMap.put(element[1], element[0]);
		}
	}

	public static String convertToHankaku(String string) {
		return convert(zenkakuMap, string);
	}

	public static String convertToZenkaku(String string) {
		return convert(hankakuMap, string);
	}

	public static boolean isAllASCII(String string) {
		char[] chars = string.toCharArray();
		for (char element : chars) {
			if (!isBasicLatin(element)) return false;
		}
		return true;
	}

	public static String[] findAllIllegalCharacters(String string) {
		LinkedList<String> list = new LinkedList<>();
		char[] chars = string.toCharArray();
		for (char element : chars)
			if (!isBasicLatin(element)) list.add(String.valueOf(element));
		return list.toArray(new String[list.size()]);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private static boolean isBasicLatin(char c) {
		return Character.UnicodeBlock.BASIC_LATIN.equals(Character.UnicodeBlock.of(c));
	}

	private static String convert(Map<String, String> map, String string) {
		StringBuffer buffer = new StringBuffer();
		char[] chars = string.toCharArray();
		for (char element : chars) {
			String value = map.get(String.valueOf(element));
			if (value == null) {
				buffer.append(element);
			} else {
				buffer.append(value);
			}
		}
		return buffer.toString();
	}
}
