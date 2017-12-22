package jp.ats.substrate.util;

/**
 * @author 千葉 哲嗣
 */
public interface Container<T> {

	T get(String key);
}
