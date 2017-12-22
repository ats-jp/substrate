package jp.ats.substrate.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author 千葉 哲嗣
 */
public class ResultSetIterator
	implements Iterator<ResultSet>, Iterable<ResultSet> {

	private final ResultSet set;

	private int counter = 0;

	private boolean init = false;

	private boolean hasNext = false;

	public ResultSetIterator(ResultSet set) {
		this.set = set;
	}

	@Override
	public Iterator<ResultSet> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		try {
			hasNext = set.next();
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		init = true;
		return hasNext;
	}

	@Override
	public ResultSet next() {
		if (!init) hasNext();
		if (!hasNext) throw new NoSuchElementException();
		counter++;
		return set;
	}

	/**
	 * 現在の取得済み件数を返します。
	 *
	 * @return 現在の取得済み件数
	 */
	public int getCounter() {
		return counter;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
