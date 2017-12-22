package jp.ats.substrate.transaction;

/**
 * @author 千葉 哲嗣
 */
public interface Transaction {

	void commit();

	void rollback();
}
