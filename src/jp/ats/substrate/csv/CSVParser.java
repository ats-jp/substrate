package jp.ats.substrate.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class CSVParser {

	private CSVParser() {}

	public static String[][] parse(InputStream input) throws IOException {
		return parse(new InputStreamReader(input));
	}

	public static String[][] parse(InputStream input, String charset)
		throws IOException {
		return parse(new InputStreamReader(input, charset));
	}

	public static String[][] parse(Reader reader) throws IOException {
		MyCSVListener listener = new MyCSVListener();
		parse(reader, listener);
		return listener.getLines();
	}

	public static void parse(InputStream input, CSVListener listener)
		throws IOException {
		parse(new InputStreamReader(input), listener);
	}

	public static void parse(
		InputStream input,
		String charset,
		CSVListener listener) throws IOException {
		parse(new InputStreamReader(input, charset), listener);
	}

	public static void parse(Reader input, CSVListener listener)
		throws IOException {
		int readed;
		Status status = Statuses.START;
		try {
			while ((readed = input.read()) != -1) {
				char next = (char) readed;
				listener.receiveNextChar(next);
				status = status.next(next, listener);
			}

			status.end(listener);
		} finally {
			U.close(input);
		}
	}

	private static class MyCSVListener extends SimpleCSVListener {

		private final List<String[]> lines = new LinkedList<>();

		@Override
		protected void processLine(String[] columns) {
			lines.add(columns);
		}

		private String[][] getLines() {
			return lines.toArray(new String[lines.size()][]);
		}
	}
}
