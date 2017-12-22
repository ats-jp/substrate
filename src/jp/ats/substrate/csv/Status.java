package jp.ats.substrate.csv;

/**
 * @author 千葉 哲嗣
 */
abstract class Status {

	abstract Status next(char c, CSVListener listener);

	abstract void end(CSVListener listener);
}
