package jp.ats.substrate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 千葉 哲嗣
 */
class ConverterUtilities {

	private static final String path;

	static {
		path = ConverterUtilities.class.getName().replaceAll("[^\\.]+$", "");
	}

	private ConverterUtilities() {}

	static String[][] createConvertTable(String name) throws IOException {
		URL url = ConverterUtilities.class.getResource("/"
			+ path.replace('.', '/')
			+ "resources/"
			+ name
			+ ".table");

		BufferedReader reader = null;
		List<String> linesList = new LinkedList<>();
		try {
			reader = new BufferedReader(new InputStreamReader(
				url.openStream(),
				"UTF-8"));

			for (String line; (line = reader.readLine()) != null;) {
				if (line.length() == 0) continue;
				linesList.add(line);
			}
		} finally {
			if (reader != null) reader.close();
		}
		String[] lines = linesList.toArray(new String[linesList.size()]);

		String[][] table = new String[lines.length][2];
		for (int i = 0; i < lines.length; i++) {
			String[] entries = lines[i].split("\\t", -1);
			table[i][0] = entries[0];
			table[i][1] = entries[1];
		}
		return table;
	}
}
