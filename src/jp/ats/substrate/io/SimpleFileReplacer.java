package jp.ats.substrate.io;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * @author 千葉 哲嗣
 */
public class SimpleFileReplacer extends SimpleFileTraverser {

	private final String regex;

	private final String replacement;

	private final Charset charset;

	public SimpleFileReplacer(String regex, String replacement) {
		this.regex = regex;
		this.replacement = replacement;
		this.charset = Charset.defaultCharset();
	}

	public SimpleFileReplacer(String regex, String replacement, Charset charset) {
		this.regex = regex;
		this.replacement = replacement;
		this.charset = charset;
	}

	@Override
	protected void execute(TransactionalFileIOStream stream) {
		Pattern pattern = Pattern.compile(regex);
		try {
			LineReader reader = new LineReader(new BufferedInputStream(
				stream.getInputStream()), charset);
			String line;
			boolean find = false;
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				stream.getOutputStream(),
				charset));
			try {
				while ((line = reader.readLine(true)) != null) {
					if (pattern.matcher(line).find()) {
						find = true;
						writer.write(line.replaceAll(regex, replacement));
					} else {
						writer.write(line);
					}
				}
				writer.flush();
				if (!find) stream.rollback();
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
