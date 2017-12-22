package jp.ats.substrate.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class LineIterator implements Iterator<String>, Iterable<String> {

	private final LineReader reader;

	private final boolean includesLineSeparator;

	private String next;

	public LineIterator(File file, boolean includesLineSeparator)
		throws FileNotFoundException {
		this(
			new BufferedInputStream(new FileInputStream(file)),
			includesLineSeparator);
	}

	public LineIterator(
		File file,
		boolean includesLineSeparator,
		Charset charset) throws FileNotFoundException {
		this(
			new BufferedInputStream(new FileInputStream(file)),
			includesLineSeparator,
			charset);
	}

	public LineIterator(InputStream input, boolean includesLineSeparator) {
		reader = new LineReader(input);
		this.includesLineSeparator = includesLineSeparator;
	}

	public LineIterator(
		InputStream input,
		boolean includesLineSeparator,
		Charset charset) {
		reader = new LineReader(input, charset);
		this.includesLineSeparator = includesLineSeparator;
	}

	@Override
	public boolean hasNext() {
		try {
			next = reader.readLine(includesLineSeparator);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return next != null;
	}

	@Override
	public String next() {
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> iterator() {
		return this;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
