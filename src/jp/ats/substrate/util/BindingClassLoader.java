package jp.ats.substrate.util;

import java.net.URL;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class BindingClassLoader extends ClassLoader {

	public BindingClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
		throws ClassNotFoundException {
		if (name.startsWith("java.")) return super.loadClass(name, resolve);
		Class<?> clazz = findLoadedOrDefineClass(name);
		if (resolve) resolveClass(clazz);
		return clazz;
	}

	public synchronized Class<?> findLoadedOrDefineClass(String name)
		throws ClassNotFoundException {
		Class<?> loaded = findLoadedClass(name);
		if (loaded != null) return loaded;
		try {
			URL url = getResource(name.replace('.', '/') + ".class");
			byte[] bytes = U.readBytes(url.openStream());
			Class<?> c = defineClass(name, bytes, 0, bytes.length);
			return c;
		} catch (Throwable t) {
			throw new ClassNotFoundException(name);
		}
	}
}
