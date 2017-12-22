package jp.ats.substrate.util;

import java.util.Iterator;

/**
 * @author 千葉 哲嗣
 */
public class BreakpointFinder<T extends Comparable<T>> {

	private final Iterator<T> iterator1;

	private final Iterator<T> iterator2;

	private T element1;

	private T element2;

	public BreakpointFinder(Iterator<T> iterator1, Iterator<T> iterator2) {
		this.iterator1 = iterator1;
		this.iterator2 = iterator2;
	}

	public boolean find() {
		element1 = next(iterator1);
		element2 = next(iterator2);
		while (element1 != null && element2 != null) {
			int result = element1.compareTo(element2);
			if (result < 0) {
				element1 = skip(iterator1, element2);
				continue;
			} else if (result > 0) {
				element2 = skip(iterator2, element1);
				continue;
			} else {
				return true;
			}
		}
		return false;
	}

	public T getElement1() {
		return element1;
	}

	public T getElement2() {
		return element2;
	}

	private static <T> T next(Iterator<T> iterator) {
		if (!iterator.hasNext()) return null;
		return iterator.next();
	}

	private T skip(Iterator<T> iterator, T target) {
		while (iterator.hasNext()) {
			T current = iterator.next();
			if (current.compareTo(target) >= 0) return current;
		}

		return null;
	}
}
