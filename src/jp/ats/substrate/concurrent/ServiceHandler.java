package jp.ats.substrate.concurrent;

/**
 * @author 千葉 哲嗣
 */
public interface ServiceHandler {

	/**
	 * サービスを行います。 Worker スレッドが使用
	 */
	void service(int workerID);

	/**
	 * 処理を強制終了します。 起動-停止スレッドが使用
	 */
	void terminate();
}
