package jp.ats.substrate.util;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import jp.ats.substrate.io.Native2AsciiInputStream;

/**
 * @author 千葉 哲嗣
 */
public class EncodablePropertiesFactory {

	private EncodablePropertiesFactory() {}

	public static Properties getInstance(
		URL fileURL,
		Properties defaultValue,
		String encode) throws IOException {
		Properties prop;
		if (defaultValue == null) {
			prop = new Properties();
		} else {
			prop = new Properties(defaultValue);
		}

		prop.load(new Native2AsciiInputStream(fileURL.openStream(), encode));

		return prop;
	}

	public static Properties getInstance(URL fileURL, String encode)
		throws IOException {
		return getInstance(fileURL, null, encode);
	}

	public static Properties getInstance(URL fileURL) throws IOException {
		return getInstance(fileURL, null, Charset.defaultCharset().name());
	}
}
