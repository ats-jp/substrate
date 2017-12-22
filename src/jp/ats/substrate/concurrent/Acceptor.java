package jp.ats.substrate.concurrent;

import jp.ats.substrate.io.IOStream;

/**
 * クライアントからの接続を待ち受けます。
 *
 * @author 千葉 哲嗣
 */
public interface Acceptor {

	/**
	 * クライアントからの接続を待ちます。 待受スレッドが使用
	 * 
	 * @return 新たにサービスを受けるクライアント
	 */
	IOStream accept() throws ShutdownNotice;

	/**
	 * 起動-停止スレッドが使用
	 */
	void shutdown();

	@SuppressWarnings("serial")
	public class ShutdownNotice extends Throwable {}
}
