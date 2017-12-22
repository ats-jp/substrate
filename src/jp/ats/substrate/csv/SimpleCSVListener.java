package jp.ats.substrate.csv;

import java.util.LinkedList;
import java.util.List;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public abstract class SimpleCSVListener implements CSVListener {

	private final List<String> line = new LinkedList<>();

	private final StringBuilder column = new StringBuilder();

	private int lineNo;

	private int charPosition = 0;

	private char current;

	protected SimpleCSVListener(int startLineNo) {
		lineNo = startLineNo;
	}

	protected SimpleCSVListener() {
		lineNo = 0;
	}

	@Override
	public void receiveColumnBody(char next) {
		column.append(next);
	}

	@Override
	public void receiveEndCSV() {
		if (charPosition > 0) receiveNewLine(null);
	}

	@Override
	public void receiveEndColumn() {
		line.add(column.toString());
		column.delete(0, column.length());
	}

	@Override
	public void receiveError(String message) {
		throw new ParseException(message);
	}

	@Override
	public void receiveNewLine(String separator) {
		lineNo++;
		processLine(line.toArray(new String[line.size()]));
		line.clear();
		charPosition = 0;
	}

	@Override
	public void receiveNextChar(char next) {
		current = next;
		charPosition++;
	}

	@Override
	public void receiveStartColumn() {}

	public int getLineNumber() {
		return lineNo;
	}

	public int getCharPosition() {
		return charPosition;
	}

	public char getCurrentChar() {
		return current;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected abstract void processLine(String[] columns);

	public class ParseException extends RuntimeException {

		private static final long serialVersionUID = 5624656496278613481L;

		private ParseException(String message) {
			super(message);
		}

		public int getLineNumber() {
			return lineNo;
		}

		public int getCharPosition() {
			return charPosition;
		}

		public char getCauseChar() {
			return current;
		}

		public String[] getParsed() {
			List<String> copy = new LinkedList<>(line);
			copy.add(column.toString());
			return copy.toArray(new String[copy.size()]);
		}
	}
}
