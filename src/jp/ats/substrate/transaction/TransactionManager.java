package jp.ats.substrate.transaction;

import java.io.PrintStream;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class TransactionManager {

	private static final ThreadLocal<Transactions> current = new ThreadLocal<Transactions>() {

		@Override
		protected Transactions initialValue() {
			return new Transactions() {

				@Override
				public void regist(Transaction transaction) {
					throw new IllegalStateException("Shell の start が実行されていません");
				}
			};
		}
	};

	private static PrintStream stream = System.err;

	private TransactionManager() {}

	public static void start(Shell shell) throws Throwable {
		Transactions transactions = new Transactions();
		Transactions oldTransactions = changeCurrent(transactions);
		try {
			shell.prepare();
			shell.execute();
			transactions.commit();
		} catch (Throwable t) {
			try {
				transactions.rollback();
			} catch (Throwable tt) {
				tt.printStackTrace(getPrintStream());
			}
			throw t;
		} finally {
			try {
				shell.doFinally();
			} catch (Throwable t) {
				t.printStackTrace(getPrintStream());
			} finally {
				changeCurrent(oldTransactions);
			}
		}
	}

	public static void regist(Transaction transaction) {
		current.get().regist(transaction);
	}

	public static synchronized void setPrintStream(PrintStream stream) {
		TransactionManager.stream = stream;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private static Transactions changeCurrent(Transactions transactions) {
		Transactions oldTransactions = current.get();
		current.set(transactions);
		return oldTransactions;
	}

	private static synchronized PrintStream getPrintStream() {
		return stream;
	}
}
