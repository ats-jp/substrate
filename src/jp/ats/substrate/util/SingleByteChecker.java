package jp.ats.substrate.util;

/**
 * @author 千葉 哲嗣
 */
public class SingleByteChecker {

	private SingleByteChecker() {}

	public static boolean containsMultiByteCharacters(String string) {
		String[] illegals = ASCIIConverter.findAllIllegalCharacters(string);
		for (int i = 0; i < illegals.length; i++) {
			if (!KanaConverter.isAllKana(illegals[i])) return true;
		}
		return false;
	}
}
