package jp.ats.substrate.flf;

import java.util.Map;

/**
 * @author 千葉 哲嗣
 */
public interface FixedLengthFieldsListener {

	void processLine(String[] fields);

	void processLine(Map<String, String> fieldMap);
}
