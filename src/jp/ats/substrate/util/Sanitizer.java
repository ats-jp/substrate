package jp.ats.substrate.util;

import static jp.ats.substrate.U.isAvailable;

public class Sanitizer {

	public static String sanitize(String target) {
		if (!isAvailable(target)) return "";
		return target.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll("\"", "&quot;")
			.replaceAll("'", "&#39;");
	}

	public static String unsanitize(String target) {
		if (!isAvailable(target)) return "";
		return target.replaceAll("&amp;", "&")
			.replaceAll("&lt;", "<")
			.replaceAll("&gt;", ">")
			.replaceAll("&quot;", "\"")
			.replaceAll("&#39;", "'");
	}
}
