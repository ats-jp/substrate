package jp.ats.substrate.flf;

import static jp.ats.substrate.U.care;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author 千葉 哲嗣
 */
public class FixedLengthFieldsFormat {

	private final Charset charset;

	private final int lineLength;

	private final List<Field> fields = new LinkedList<>();

	public FixedLengthFieldsFormat(URL xml, String charset) throws IOException {
		this.charset = Charset.forName(charset);
		NodeList nodes;
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			nodes = (NodeList) xpath.evaluate(
				"/fixed-length-fields/field",
				new InputSource(xml.openStream()),
				XPathConstants.NODESET);
			int length = nodes.getLength();
			int lineLength = 0;
			for (int i = 0; i < length; i++) {
				Field field = createField(xpath, nodes.item(i));
				lineLength += field.length;
				fields.add(field);
			}
			this.lineLength = lineLength;
		} catch (XPathExpressionException e) {
			throw new Error(e);
		}
	}

	public void parseToArray(
		InputStream input,
		FixedLengthFieldsListener listener,
		boolean trim) throws IOException {
		byte[] buffer = new byte[lineLength];
		final List<String> list = new LinkedList<>();
		while (input.read(buffer) != -1) {
			parseLine(buffer, new ParseClosure() {

				@Override
				void process(String name, String value) {
					list.add(value);
				}
			}, trim);

			listener.processLine(list.toArray(new String[list.size()]));

			list.clear();
		}
	}

	public void parseToMap(
		InputStream input,
		FixedLengthFieldsListener listener,
		boolean trim) throws IOException {
		byte[] buffer = new byte[lineLength];
		final HashMap<String, String> map = new HashMap<>();
		while (input.read(buffer) != -1) {
			parseLine(buffer, new ParseClosure() {

				@Override
				void process(String name, String value) {
					map.put(name, value);
				}
			}, trim);

			listener.processLine(cast(map.clone()));

			map.clear();
		}
	}

	public void parseLine(byte[] line, final List<String> list, boolean trim) {
		parseLine(line, new ParseClosure() {

			@Override
			void process(String name, String value) {
				list.add(value);
			}
		}, trim);
	}

	public void parseLine(
		byte[] line,
		final Map<String, String> map,
		boolean trim) {
		parseLine(line, new ParseClosure() {

			@Override
			void process(String name, String value) {
				map.put(name, value);
			}
		}, trim);
	}

	public void formatArray(
		OutputStream output,
		Iterable<String[]> target,
		boolean autoflush) throws IOException {
		byte[] line = new byte[lineLength];
		for (String[] fields : target) {
			formatLine(fields, line);
			output.write(line);
			if (autoflush) output.flush();
		}
	}

	public void formatMap(
		OutputStream output,
		Iterable<Map<String, String>> target,
		boolean autoflush) throws IOException {
		byte[] line = new byte[lineLength];
		String[] buffer = new String[fields.size()];
		for (Map<String, String> fields : target) {
			formatLine(fields, buffer, line);
			output.write(line);
			if (autoflush) output.flush();
		}
	}

	public void formatLine(Map<String, String> fieldMap, byte[] line) {
		formatLine(fieldMap, new String[fields.size()], line);
	}

	public void formatLine(String[] fields, byte[] line) {
		clear(line);
		int currentPosition = 0;
		for (int i = 0; i < fields.length; i++) {
			Field field = this.fields.get(i);
			byte[] base = care(fields[i]).getBytes(charset);
			if (base.length > field.length) throw new IllegalStateException("["
				+ fields[i]
				+ "] は、第 "
				+ (i + 1)
				+ " フィールド "
				+ field.name
				+ " の規定サイズをオーバーしています");

			if (field.preSpace) {
				System.arraycopy(base, 0, line, currentPosition
					+ field.length
					- base.length, base.length);
			} else {
				System.arraycopy(base, 0, line, currentPosition, base.length);
			}

			currentPosition += field.length;
		}
	}

	/**
	 * 固定長ファイルの定義された一行の長さを返します。
	 *
	 * @return 一行の長さ
	 */
	public int getLineLength() {
		return lineLength;
	}

	/**
	 * 固定長ファイルの各列の定義を返します。
	 *
	 * @return 各列の定義
	 */
	public Field[] getFields() {
		return fields.toArray(new Field[fields.size()]);
	}

	private void parseLine(byte[] line, ParseClosure closure, boolean trim) {
		int from = 0;
		for (Field field : fields) {
			int to = from + field.length;
			String value = new String(
				Arrays.copyOfRange(line, from, to),
				charset);
			closure.process(field.name, trim ? value.trim() : value);
			from = to;
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> cast(Object target) {
		return (Map<String, String>) target;
	}

	private void formatLine(
		Map<String, String> fieldMap,
		String[] values,
		byte[] line) {
		int size = fields.size();
		for (int i = 0; i < size; i++) {
			values[i] = fieldMap.get(fields.get(i).name);
		}

		formatLine(values, line);
	}

	private static Field createField(XPath xpath, Node node)
		throws XPathExpressionException {
		Field field = new Field(
			xpath.evaluate("@name", node),
			Integer.parseInt(xpath.evaluate("@length", node)),
			//空白頭詰めの場合 true
			Boolean.parseBoolean(xpath.evaluate("@pre-space", node)));

		return field;
	}

	private static void clear(byte[] target) {
		for (int i = 0; i < target.length; i++) {
			target[i] = ' ';
		}
	}

	public static class Field {

		private final String name;

		private final int length;

		private final boolean preSpace;

		private Field(String name, int length, boolean preSpace) {
			this.name = name;
			this.length = length;
			this.preSpace = preSpace;
		}

		public String getName() {
			return name;
		}

		public int getLength() {
			return length;
		}

		public boolean isPreSpace() {
			return preSpace;
		}
	}

	private static abstract class ParseClosure {

		abstract void process(String name, String value);
	}
}
