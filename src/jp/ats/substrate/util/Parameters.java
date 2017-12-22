package jp.ats.substrate.util;

import java.util.LinkedList;
import java.util.List;

import jp.ats.substrate.U;

/**
 * メソッドのリフレクション機能を使用した呼出に使用するパラメータのタイプ配列、値の配列を生成するクラスです。
 * <p>
 * 下の例では1番目のパラメータがjava.lang.String、2番目のパラメータがint、3番目のパラメータがbooleanで、名前がfooであるメソッドを呼び出しています。
 * <blockquote><pre>
 * Parameters parameters = new Parameters();
 * parameters.add("first").add(2).add(true);
 * Class[] classes = parameters.getParameterTypes();
 * Object[] values = parameters.getParameters();
 *
 * Class clazz = someObject.getClass();
 * java.lang.reflect.Method method = clazz.getMethod("foo",classes);
 * method.invoke(someObject,values);
 * </pre></blockquote>
 *
 * @author 千葉 哲嗣
 */
public class Parameters {

	private final List<Class<?>> classes = new LinkedList<>();

	private final List<Object> parameters = new LinkedList<>();

	/**
	 * Objectパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(Object value) {
		classes.add(value.getClass());
		parameters.add(value);
		return this;
	}

	/**
	 * Objectパラメータを追加します。
	 * valueのクラスがメソッド宣言のパラメータクラスのサブクラス等の場合、
	 * 明示的にtypeでクラスを指定できます。
	 * <p>
	 * @param type 
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(Class<?> type, Object value) {
		classes.add(type);
		parameters.add(value);
		return this;
	}

	/**
	 * booleanパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(boolean value) {
		classes.add(Boolean.TYPE);
		parameters.add(Boolean.valueOf(value));
		return this;
	}

	/**
	 * byteパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(byte value) {
		classes.add(Byte.TYPE);
		parameters.add(new Byte(value));
		return this;
	}

	/**
	 * charパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(char value) {
		classes.add(Character.TYPE);
		parameters.add(new Character(value));
		return this;
	}

	/**
	 * shortパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(short value) {
		classes.add(Short.TYPE);
		parameters.add(new Short(value));
		return this;
	}

	/**
	 * intパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(int value) {
		classes.add(Integer.TYPE);
		parameters.add(new Integer(value));
		return this;
	}

	/**
	 * longパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(long value) {
		classes.add(Long.TYPE);
		parameters.add(new Long(value));
		return this;
	}

	/**
	 * floatパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(float value) {
		classes.add(Float.TYPE);
		parameters.add(new Float(value));
		return this;
	}

	/**
	 * doubleパラメータを追加します。
	 * <p>
	 * @param value 実パラメータ
	 * @return このオブジェクト
	 */
	public Parameters add(double value) {
		classes.add(Double.TYPE);
		parameters.add(new Double(value));
		return this;
	}

	/**
	 * クラスからメソッドを抽出する際に、メソッドを特定するためのクラス配列を生成します。
	 * <p>
	 * @return 追加されたパラメータのクラスオブジェクト配列
	 */
	public Class<?>[] getParameterTypes() {
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * メソッドのリフレクション機能を使用した呼出の際に渡すパラメータ値の配列です。
	 * <p>
	 * @return 追加されたパラメータの値オブジェクト配列
	 */
	public Object[] getParameters() {
		return parameters.toArray();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
