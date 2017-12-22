package jp.ats.substrate.util;

import java.util.Iterator;

/**
 * @author 千葉 哲嗣
 */
public class SimpleIterableIterator<T> implements IterableIterator<T> {

	private final Iterator<T> iterator;

	public SimpleIterableIterator(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public SimpleIterableIterator(Iterable<T> iterable) {
		this.iterator = iterable.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}
}
