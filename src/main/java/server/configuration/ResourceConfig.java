package server.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import server.annotation.PATH;

public class ResourceConfig {
	private static final List<ClassLoader> classLoadersList;
	private Map<String, Resource> configuration = new HashMap<>();
	private Set<Class<?>> classes;

	static {
		classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
	}

	public ResourceConfig() {
		super();
		this.classes = new HashSet<>();
	}

	public Map<String, Resource> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Resource> configuration) {
		this.configuration = configuration;
	}

	/**
	 * register all resources located under the given packages name
	 * 
	 * @param packes
	 * @throws IllegalArgumentException
	 * @throws ResourcesNotFoundException
	 */
	public void packages(String... packages) throws IllegalArgumentException, ResourcesNotFoundException {
		if (packages == null || packages.length == 0) {
			throw new IllegalArgumentException("You must supply atleast a resource package");
		}

		FilterBuilder filter = new FilterBuilder();

		for (String package_ : packages) {
			if (package_ == null) {
				throw new IllegalArgumentException("A null argument is not allowed");
			} else if (package_.isEmpty()) {
				throw new IllegalArgumentException("An empty string is not allowed");
			}

			filter.includePackage(package_);
		}

		Reflections resourceReflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner(), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(filter));

		classes = resourceReflections.getTypesAnnotatedWith(PATH.class);

		if (classes.isEmpty()) {
			throw new ResourcesNotFoundException();
		}
	}

	/**
	 * Register a list of supplied resource classes
	 */
	public void classes(@SuppressWarnings("unchecked") Class<? extends Object> ...classes) {
		if (classes == null || classes.length == 0) {
			throw new IllegalArgumentException("Can not register a null or empty class");
		}

		for (Class<? extends Object> clazz : classes) {
			if (clazz == null) {
				throw new IllegalArgumentException("Null class argument is not allowed");
			}
			
			if (clazz.isAnnotationPresent(PATH.class)) {
				this.classes.add(clazz);
			}
		}
	}

	public Set<Class<?>> getClasses() {
		return classes;
	}

	public void setClasses(Set<Class<?>> classes) {
		this.classes = classes;
	}
}