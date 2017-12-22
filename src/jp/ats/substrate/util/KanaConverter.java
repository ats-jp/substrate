package jp.ats.substrate.util;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author 千葉 哲嗣
 */
public class KanaConverter {

	private static final String[][] dakuonTable;

	private static final String[][] kanaTable;

	private static final Pattern kanaPattern;

	private static final Pattern hankakuKanaPattern;

	static {
		try {
			dakuonTable = ConverterUtilities.createConvertTable("dakuon");
			kanaTable = ConverterUtilities.createConvertTable("kana");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		StringBuffer zenkakuBuffer = new StringBuffer();
		StringBuffer hankakuBuffer = new StringBuffer();
		for (int i = 0; i < kanaTable.length; i++) {
			zenkakuBuffer.append(kanaTable[i][0]);
			hankakuBuffer.append(kanaTable[i][1]);
		}
		for (int i = 0; i < dakuonTable.length; i++) {
			zenkakuBuffer.append(dakuonTable[i][0]);
		}

		kanaPattern = Pattern.compile("^["
			+ zenkakuBuffer
			+ hankakuBuffer
			+ " 　]*$");
		hankakuKanaPattern = Pattern.compile("[" + hankakuBuffer + "]");
	}

	private KanaConverter() {}

	public static String convertToHankaku(String string) {
		for (int i = 0; i < dakuonTable.length; i++) {
			string = convert(dakuonTable[i][0], dakuonTable[i][1], string);
		}
		for (int i = 0; i < kanaTable.length; i++) {
			string = convert(kanaTable[i][0], kanaTable[i][1], string);
		}
		return string;
	}

	public static String convertToZenkaku(String string) {
		for (int i = 0; i < dakuonTable.length; i++) {
			string = string.replaceAll(dakuonTable[i][1], dakuonTable[i][0]);
		}
		for (int i = 0; i < kanaTable.length; i++) {
			string = string.replaceAll(kanaTable[i][1], kanaTable[i][0]);
		}
		return string;
	}

	public static String trim(String string) {
		return string.replaceAll("^[ 　]+|[ 　]+$", "");
	}

	public static boolean isAllKana(String string) {
		trim(string);
		return kanaPattern.matcher(string).matches();
	}

	public static boolean containsHankakuKana(String string) {
		return hankakuKanaPattern.matcher(string).find();
	}

	private static String convert(String from, String to, String base) {
		return base.replaceAll(from, to);
	}
}
