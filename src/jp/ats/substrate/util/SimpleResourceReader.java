package jp.ats.substrate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public final class SimpleResourceReader {

	//インスタンス化不可
	private SimpleResourceReader() {}

	/**
	 * 指定されたクラスと同パッケージで同名のファイルをロードします。
	 * @param resourceBase テキストファイルと同パッケージで同名のクラス
	 * @param extension ロードするテキストファイルの拡張子
	 */
	public static String[] readLines(Class<?> resourceBase, String extension) {
		return readLines(resourceBase, extension, Charset.defaultCharset());
	}

	public static String[] readLines(
		Class<?> resourceBase,
		String extension,
		Charset charset) {
		return readLines(U.getResourcePath(resourceBase, extension), charset);
	}

	/**
	 * 簡単なフォーマットのテキストファイルをロードします。<br>
	 * ファイル内の"#"以降の文字がコメントとして除外されます。<br>
	 * コメントのみ、もしくは空白文字のみの行は返却されません。<br>
	 * @param url テキストファイルのURL
	 * @return コメントを除外した文字列を、行ごとに配列にセットしています。
	 * @throws IllegalArgumentException resourcePathで指定されたファイルが存在しない場合
	 * @throws IllegalStateException 予測できない入出力例外が発生した場合
	 */
	public static String[] readLines(URL url) {
		return readLines(url, Charset.defaultCharset());
	}

	public static String[] readLines(URL url, Charset charset) {
		try {
			return readLines(url.openStream(), charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * クラスパス内にある簡単なフォーマットのテキストファイルをロードします。<br>
	 * ファイル内の"#"以降の文字がコメントとして除外されます。<br>
	 * コメントのみ、もしくは空白文字のみの行は返却されません。<br>
	 * @param path テキストファイルのパス。"/"からはじまっている必要があります。
	 * @return コメントを除外した文字列を、行ごとに配列にセットしています。
	 * @throws IllegalArgumentException resourcePathで指定されたファイルが存在しない場合
	 * @throws IllegalStateException 予測できない入出力例外が発生した場合
	 */
	public static String[] readLines(String path) {
		return readLines(path, Charset.defaultCharset());
	}

	public static String[] readLines(String path, Charset charset) {
		URL url = SimpleResourceReader.class.getResource(path);
		if (url == null) {
			throw new IllegalArgumentException(path + " が見つかりませんでした");
		}

		return readLines(url, charset);
	}

	public static String[] readLines(InputStream input) {
		return readLines(input, Charset.defaultCharset());
	}

	public static String[] readLines(InputStream input, String charset) {
		return readLines(input, Charset.forName(charset));
	}

	public static String[] readLines(InputStream input, Charset charset) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				input,
				charset));

			List<String> list = new LinkedList<>();
			for (String line; (line = reader.readLine()) != null;) {
				line = line.replaceAll("#.*$", "");
				line = line.trim();
				if (line.length() == 0) continue;
				list.add(line);
			}
			return list.toArray(new String[list.size()]);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
