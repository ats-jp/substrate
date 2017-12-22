package jp.ats.substrate.sql;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import jp.ats.substrate.U;

/**
 * あらかじめ用意しておいた SQL 文を実行する Proxy クラスを生成するビルダクラスです。 <br>
 * あらかじめ用意しておいた SQL 文とは、一つの SQL 文を一つのファイルに記述し、そのファイル名を <br>
 * interface-name#method-name.sql <br>
 * として、インターフェイスと同じ場所に配備したもののことです。 <br>
 * インターフェイスに定義するメソッドは、戻り値に <br> {@link ResultSet}, int, boolean, void <br>
 * を使用することができます。 <br>
 * メソッドのパラメータには、 SQL 文に記述したプレースホルダにセットする値を渡せるように定義してください。
 * 
 * @author 千葉 哲嗣
 */
public class SQLProxyBuilder {

	private final Connection connection;

	public SQLProxyBuilder(Connection connection) {
		this.connection = connection;
	}

	/**
	 * SQL 文の文字セットとして、デフォルトの {@link Charset} を使用して ProxyObject を生成します。
	 * 
	 * @param <T> 生成される ProxyObject の型
	 * @param sourceInterface 生成される ProxyObject のインターフェイス
	 * @return 生成された ProxyObject
	 */
	public <T> T buildProxyObject(Class<T> sourceInterface) {
		return buildProxyObject(sourceInterface, Charset.defaultCharset());
	}

	/**
	 * ProxyObject を生成します。
	 * 
	 * @param <T> 生成される ProxyObject の型
	 * @param sourceInterface 生成される ProxyObject のインターフェイス
	 * @param sqlCharset SQL 文の文字セット
	 * @return 生成された ProxyObject
	 */
	@SuppressWarnings("unchecked")
	public <T> T buildProxyObject(Class<T> sourceInterface, Charset sqlCharset) {
		if (!sourceInterface.isInterface()) throw new IllegalArgumentException(
			sourceInterface + " はインターフェイスではありません");

		return (T) Proxy.newProxyInstance(
			SQLProxyBuilder.class.getClassLoader(),
			new Class<?>[] { sourceInterface },
			new SQLProxyInvocationHandler(sqlCharset));
	}

	/**
	 * ProxyObject を生成します。
	 * 
	 * @param <T> 生成される ProxyObject の型
	 * @param sourceInterface 生成される ProxyObject のインターフェイス
	 * @param sqlCharset SQL 文の文字セット
	 * @return 生成された ProxyObject
	 * @see Charset#forName(String)
	 */
	public <T> T buildProxyObject(Class<T> sourceInterface, String sqlCharset) {
		return buildProxyObject(sourceInterface, Charset.forName(sqlCharset));
	}

	private class SQLProxyInvocationHandler implements InvocationHandler {

		private final Charset charset;

		private SQLProxyInvocationHandler(Charset charset) {
			this.charset = charset;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
			Class<?> proxyClass = proxy.getClass().getInterfaces()[0];

			String sqlFileName = proxyClass.getName().replaceAll(
				".+?([^\\.]+)$",
				"$1")
				+ "#"
				+ method.getName()
				+ ".sql";

			URL url = U.getResourcePathByName(proxyClass, sqlFileName);

			if (url == null) throw new IllegalStateException(sqlFileName
				+ " が見つかりません");

			String sql = new String(U.readBytes(url.openStream()), charset);

			PreparedStatement statement = connection.prepareStatement(sql);

			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < parameterTypes.length; i++) {
				int index = i + 1;
				Object x = args[i];
				Class<?> parameterType = parameterTypes[i];
				if (parameterType.equals(String.class)) {
					statement.setString(index, (String) x);
				} else if (parameterType.equals(Timestamp.class)) {
					statement.setTimestamp(index, (Timestamp) x);
				} else if (parameterType.equals(BigDecimal.class)) {
					statement.setBigDecimal(index, (BigDecimal) x);
				} else if (parameterType.equals(Integer.TYPE)) {
					statement.setInt(index, (Integer) x);
				} else if (parameterType.equals(Long.TYPE)) {
					statement.setLong(index, (Long) x);
				} else if (parameterType.equals(Double.TYPE)) {
					statement.setDouble(index, (Double) x);
				} else if (parameterType.equals(Float.TYPE)) {
					statement.setFloat(index, (Float) x);
				} else if (parameterType.equals(InputStream.class)) {
					statement.setBinaryStream(index, (InputStream) x);
				} else if (parameterType.equals(Blob.class)) {
					statement.setBlob(index, (Blob) x);
				} else if (parameterType.equals(Boolean.TYPE)) {
					statement.setBoolean(index, (Boolean) x);
				} else if (parameterType.equals(byte[].class)) {
					statement.setBytes(index, (byte[]) x);
				} else if (parameterType.equals(Reader.class)) {
					statement.setCharacterStream(index, (Reader) x);
				} else if (parameterType.equals(Clob.class)) {
					statement.setClob(index, (Clob) x);
				} else if (parameterType.equals(Object.class)) {
					statement.setObject(index, x);
				} else {
					throw new IllegalStateException("パラメータの型が不正です");
				}
			}

			Class<?> returnType = method.getReturnType();

			if (returnType.equals(ResultSet.class)) {
				return statement.executeQuery();
			} else if (returnType.equals(int.class)) {
				return statement.executeUpdate();
			} else if (returnType.equals(boolean.class)) {
				return statement.execute();
			} else if (returnType.equals(void.class)) {
				return statement.execute();
			} else {
				throw new IllegalStateException("戻り値の型が不正です");
			}
		}
	}
}
