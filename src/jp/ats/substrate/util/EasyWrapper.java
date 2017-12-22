package jp.ats.substrate.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.ats.substrate.U;

/**
 * リフレクション機能を使用したクラス、オブジェクト操作ユーティリティです。
 * 対象インスタンス内のprivateフィールド、メソッドを操作可能にします。
 *
 * @author 千葉 哲嗣
 */
public class EasyWrapper<T> {

	private final Object targetObject;

	private final Class<T> targetClass;

	private boolean accessible;

	/**
	 * 渡されたインスタンスをラッピングします。
	 * <p>
	 * このコンストラクタで生成された場合は、操作するインスタンスのクラスフィールド、インスタンスフィールド、クラスメソッド、インスタンスメソッドに対してアクセスします。
	 * <p>
	 * @param target 操作対象となるインスタンス
	 * @param accessible trueの場合はprivateを含む全フィールド、メソッドにアクセス可能
	 * falseの場合はpublicのみ
	 */
	@SuppressWarnings("unchecked")
	public EasyWrapper(Object target, boolean accessible) {
		targetObject = target;
		targetClass = (Class<T>) target.getClass();
		this.accessible = accessible;
	}

	/**
	 * 渡されたクラスをラッピングします。
	 * <p>
	 * このコンストラクタで生成された場合は、渡されたクラスのクラスフィールド、クラスメソッドに対してアクセスします。
	 * <p>
	 * @param target 操作対象となるクラス
	 * @param accessible trueの場合はprivateを含む全staticフィールド、staticメソッドにアクセス可能
	 * falseの場合はpublicのみ
	 */
	public EasyWrapper(Class<T> target, boolean accessible) {
		targetObject = null;
		targetClass = target;
		this.accessible = accessible;
	}

	/**
	 * 渡されたクラスのインスタンスを、渡されたパラメータを持つコンストラクタを使用して生成し、ラッピングします。
	 * <p>
	 * このコンストラクタで生成された場合は、生成されたインスタンスのクラスフィールド、インスタンスフィールド、クラスメソッド、インスタンスメソッドに対してアクセスします。
	 * <p>
	 * @param target 操作対象となるクラス
	 * @param parameters targetクラス内のコンストラクタに渡すパラメータ
	 * @param accessible trueの場合はprivateを含む全コンストラクタ、全フィールド、メソッドにアクセス可能
	 * falseの場合はpublicのみ
	 * @exception java.lang.IllegalArgumentException 渡されたパラメータのコンストラクタが見つからない、対象クラスがabstractである、accessibleがfalseの場合、不可視のコンストラクタを使用しようとした場合発生
	 * @exception java.lang.SecurityException アクセス権がない場合
	 * @exception java.lang.reflect.InvocationTargetException コンストラクタが例外をスローする場合
	 */
	public EasyWrapper(
		Class<T> target,
		Parameters parameters,
		boolean accessible) throws InvocationTargetException {
		targetClass = target;
		this.accessible = accessible;
		Constructor<T> constructor = null;
		try {
			constructor = target.getDeclaredConstructor(parameters.getParameterTypes());
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("コンストラクタが見つかりませんでした。");
		}
		constructor.setAccessible(accessible);
		try {
			targetObject = constructor.newInstance(parameters.getParameters());
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("対象クラスはabstractです。");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("コンストラクタにアクセスできません。");
		}
	}

	/**
	 * ラップしたクラスもしくはインスタンス内のフィールドに値をセットします。
	 * <p>
	 * @param name 値をセットするフィールドの名前
	 * @param value セットする値
	 * @exception java.lang.IllegalArgumentException フィールドがfinalの場合、フィールドにアクセスできない場合、プリミティブ型のラップ解除変換に失敗した場合
	 * @exception java.lang.SecurityException アクセス権がない場合
	 */
	public void setFieldValue(String name, Object value) {
		Field f = getField(targetClass, name);
		f.setAccessible(accessible);
		try {
			f.set(targetObject, value);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("セットしようとしたフィールド"
				+ name
				+ "がfinal、もしくはフィールドにアクセスできません。");
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("プリミティブ型のラップ解除変換ができません。");
		}
	}

	/**
	 * ラップしたクラスもしくはインスタンス内のフィールドの値を取得します。
	 * <p>
	 * @param name 値をセットするフィールドの名前
	 * @return フィールドの値
	 * フィールドがプリミティブ型の場合は自動的にプリミティブ型ラッピングクラスのインスタンスとなります。
	 * @exception java.lang.IllegalArgumentException フィールドにアクセスできない場合
	 * @exception java.lang.SecurityException アクセス権がない場合
	 */
	public Object getFieldValue(String name) {
		Field f = getField(targetClass, name);
		f.setAccessible(accessible);
		try {
			return f.get(targetObject);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("取得しようとしたフィールド"
				+ name
				+ "から値が取得できません。");
		}
	}

	/**
	 * ラップしたクラスもしくはインスタンス内のメソッドを実行します。
	 * <p>
	 * 下の例ではsomeObject内にある、1番目のパラメータがjava.lang.String、2番目のパラメータがint、3番目のパラメータがbooleanで、名前がfooであるメソッドを呼び出しています。
	 * <blockquote><pre>
	 * Parameters parameters = new Parameters();
	 * parameters.add("first").add(2).add(true);
	 *
	 * Wrapper wrapper = new Wrapper(someObject,true);
	 * Object value = wrapper.invokeMethod("foo",values);
	 * </pre></blockquote>
	 * <p>
	 * @param name 値をセットするフィールドの名前
	 * @param parameters メソッドに渡すパラメータ
	 * @return メソッドの戻り値<br>
	 * 戻り値がプリミティブ型の場合は自動的にプリミティブ型ラッピングクラスのインスタンスとなります。
	 * @exception java.lang.IllegalArgumentException メソッドにアクセスできない場合
	 * @exception java.lang.SecurityException アクセス権がない場合
	 * @exception java.lang.reflect.InvocationTargetException メソッドが例外をスローする場合
	 */
	public Object invokeMethod(String name, Parameters parameters)
		throws InvocationTargetException {
		Method m = getMethod(targetClass, name, parameters.getParameterTypes());
		m.setAccessible(accessible);
		try {
			return m.invoke(targetObject, parameters.getParameters());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("メソッド" + name + "にアクセスできません。");
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("パラメータに誤りがあります。");
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private Field getField(Class<?> target, String name) {
		try {
			return target.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			Class<?> parent = target.getSuperclass();
			if (parent == null) {
				throw new IllegalArgumentException("フィールド" + name + "は存在しません。");
			}
			return getField(parent, name);
		}
	}

	private Method getMethod(Class<?> target, String name, Class<?>[] types) {
		try {
			return target.getDeclaredMethod(name, types);
		} catch (NoSuchMethodException e) {
			Class<?> parent = target.getSuperclass();
			if (parent == null) {
				throw new IllegalArgumentException("メソッド" + name + "は存在しません。");
			}
			return getMethod(parent, name, types);
		}
	}
}
