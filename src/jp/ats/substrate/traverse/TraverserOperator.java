package jp.ats.substrate.traverse;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public abstract class TraverserOperator {

	public static void operate(Traverser traverser, Traversable traversable) {
		traverser.execute(traversable);
		traverser.getOperator().operate(
			traverser,
			traversable.getSubNode().getTraversables());
	}

	public abstract void operate(Traverser traverser, Traversable[] traversables);

	@Override
	public String toString() {
		return U.toString(this);
	}
}
