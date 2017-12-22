package jp.ats.substrate.concurrent;

/**
 * Channelの現在の状態を知るためのリスナです。
 *
 * @author 千葉 哲嗣
 */
public interface ChannelListener {

	/**
	 * リクエストの最大貯留数が変更されたことを通知します。
	 * 
	 * @param size 新しい最大貯留数
	 */
	void receiveMaximumPoolSize(int size);

	/**
	 * ワーカスレッドの最大数が変更されたことを通知します。
	 * 
	 * @param count 新しいワーカスレッドの最大数
	 */
	void receiveMaximumWorkerCount(int count);

	/**
	 * 現在貯留しているリクエストの数が変更されたことを通知します。
	 * 
	 * @param size 新しい貯留リクエスト数
	 */
	void receiveCurrentPoolSize(int size);

	/**
	 * 現在のワーカスレッドの数が変更されたことを通知します。
	 * 
	 * @param count 新しいワーカスレッド数
	 */
	void receiveCurrentWorkerCount(int count);

	/**
	 * 現在実行中のワーカスレッドの数が変更されたことを通知します。
	 * 
	 * @param count 新しい現在実行中のワーカスレッド数
	 */
	void receiveRunningWorkerCount(int count);
}
