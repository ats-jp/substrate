package jp.ats.substrate.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.ats.substrate.U;
import jp.ats.substrate.transaction.Transaction;

/**
 * @author 千葉 哲嗣
 */
public class TransactionalFileIOStream implements IOStream, Transaction {

	private final String tempFilePrefix = getClass().getName() + "-";

	private final File file;

	private File temp;

	private InputStream input;

	private OutputStream output;

	public TransactionalFileIOStream(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (input == null) input = new FileInputStream(file);
		return input;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (output == null) {
			temp = File.createTempFile(tempFilePrefix, null);
			temp.deleteOnExit();
			output = new FileOutputStream(temp);
		}
		return output;
	}

	@Override
	public void close() {
		rollback();
	}

	@Override
	public void commit() {
		try {
			if (input != null) input.close();
			if (output != null) {
				output.close();
				file.delete();
				temp.renameTo(file);
			}
			clear();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void rollback() {
		try {
			if (input != null) input.close();
			if (output != null) {
				output.close();
				temp.delete();
			}
			clear();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private void clear() {
		input = null;
		output = null;
		temp = null;
	}
}
