package jp.ats.substrate.io;

import java.io.File;

import jp.ats.substrate.U;
import jp.ats.substrate.traverse.HaltableTraversable;
import jp.ats.substrate.traverse.TraversableNode;

/**
 * @author 千葉 哲嗣
 */
public class TraversableFile implements HaltableTraversable {

	private final File file;

	private boolean disabled = false;

	public TraversableFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
	public TraversableNode getSubNode() {
		TraversableNode node = new TraversableNode();
		if (disabled || !file.isDirectory()) return node;
		File[] files = file.listFiles();
		for (File file : files) {
			node.add(new TraversableFile(file));
		}
		return node;
	}

	@Override
	public void halt() {
		disabled = true;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
