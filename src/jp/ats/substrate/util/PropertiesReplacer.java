package jp.ats.substrate.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class PropertiesReplacer {

	private static final Pattern pattern = Pattern.compile("\\[\\[([^\\[\\]]+)\\]\\]");

	private PropertiesReplacer() {}

	public static void replace(Reader in, Writer out, Properties prop)
		throws IOException {
		BufferedReader reader = new BufferedReader(in);
		BufferedWriter writer = new BufferedWriter(out);
		for (String line; (line = reader.readLine()) != null;) {
			StringBuilder builder = new StringBuilder();
			replace(line, builder, prop);
			writer.write(builder.toString());
			writer.write(U.LINE_SEPARATOR);
			writer.flush();
		}
	}

	public static String replace(String target, Properties prop) {
		StringBuilder builder = new StringBuilder();
		replace(target, builder, prop);
		return builder.toString();
	}

	private static void replace(
		String line,
		StringBuilder builder,
		Properties prop) {
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			builder.append(line.substring(0, matcher.start()));
			String key = matcher.group(1).trim();
			String replaced = prop.getProperty(key);
			if (replaced == null) {
				builder.append(matcher.group());
			} else {
				builder.append(replaced);
			}
			line = line.substring(matcher.end());
			matcher.reset(line);
		}
		builder.append(line);
	}
}
