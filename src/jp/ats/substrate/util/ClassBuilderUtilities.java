package jp.ats.substrate.util;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class ClassBuilderUtilities {

	public static String[] pickupFromSource(String source, String key) {
		String patternBase = "/\\*==" + key + "==\\*/";
		Matcher matcher = Pattern.compile(
			patternBase + "(.+?)" + patternBase,
			Pattern.MULTILINE + Pattern.DOTALL).matcher(source);
		matcher.find();
		return new String[] { matcher.group(1), matcher.replaceAll("") };
	}

	public static String convertToTemplate(String source) {
		source = source.replaceAll("/\\*--\\*/.+?/\\*--\\*/", "");
		return source.replaceAll("/\\*\\+\\+(.+?)\\+\\+\\*/", "$1");
	}

	public static String readTemplate(Class<?> target, String charset) {
		URL templateURL = U.getResourcePathByName(
			target,
			target.getSimpleName() + ".java");
		try {
			return new String(U.readBytes(templateURL.openStream()), charset);
		} catch (IOException e) {
			throw new Error();
		}
	}
}
