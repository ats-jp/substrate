package jp.ats.substrate.util;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 千葉 哲嗣
 */
public class MultiClassLoader {

	private MultiClassLoader() {}

	private static final BindingClassLoader loader = new BindingClassLoader(
		MultiClassLoader.class.getClassLoader());

	public static synchronized Class<?>[] getClasses(URL[] resources)
		throws ClassNotFoundException {
		return getClasses(resources, new Object().getClass());
	}

	public static synchronized Class<?>[] getClasses(
		URL[] resources,
		Class<?> assignable) throws ClassNotFoundException {
		List<Class<?>> classes = new LinkedList<>();
		for (URL resource : resources) {
			String[] classNames = SimpleResourceReader.readLines(resource);
			for (String className : classNames) {
				classes.add(loader.findLoadedOrDefineClass(className));
			}
		}

		List<Class<?>> assignableList = new LinkedList<>();
		for (Class<?> entry : classes) {
			if (assignable.isAssignableFrom(entry)) assignableList.add(entry);
		}
		return assignableList.toArray(new Class[assignableList.size()]);
	}
}
