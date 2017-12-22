package jp.ats.substrate.io;

import java.io.File;

import jp.ats.substrate.U;
import jp.ats.substrate.transaction.Shell;
import jp.ats.substrate.transaction.TransactionManager;
import jp.ats.substrate.traverse.Traversable;
import jp.ats.substrate.traverse.Traverser;
import jp.ats.substrate.traverse.TraverserOperator;

/**
 * @author 千葉 哲嗣
 */
public abstract class SimpleFileTraverser implements Traverser {

	public void start(File base) {
		TraverserOperator.operate(this, new TraversableFile(base));
	}

	@Override
	public TraverserOperator getOperator() {
		return DEPTH_FIRST;
	}

	@Override
	public void execute(Traversable traversable) {
		final File file = ((TraversableFile) traversable).getFile();
		if (file.isDirectory()) {
			if (!canExecuteRecursively(file)) {
				((TraversableFile) traversable).halt();
			}
			return;
		}

		if (!canExecute(file)) return;

		try {
			TransactionManager.start(new Shell() {

				@Override
				public void prepare() {}

				@Override
				public void execute() {
					TransactionalFileIOStream stream = new TransactionalFileIOStream(
						file);
					TransactionManager.regist(stream);
					SimpleFileTraverser.this.execute(stream);
				}

				@Override
				public void doFinally() {}
			});
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected boolean canExecuteRecursively(File directory) {
		return true;
	}

	protected boolean canExecute(File file) {
		return true;
	}

	protected abstract void execute(TransactionalFileIOStream stream);
}
