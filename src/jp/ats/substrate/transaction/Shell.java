package jp.ats.substrate.transaction;

/**
 * @author 千葉 哲嗣
 */
public interface Shell {

	void prepare();

	void execute() throws Throwable;

	void doFinally();
}
