package com.upmc.stl.dar.server.resource.configuration;

import java.lang.reflect.Method;
import java.util.Collections;
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

import com.upmc.stl.dar.server.annotation.PATH;
import com.upmc.stl.dar.server.exceptions.ServerException;
import com.upmc.stl.dar.server.exceptions.ResourceNotFoundException;
import com.upmc.stl.dar.server.exceptions.UrlConfictException;

/**
 *  TODO
 *  <ol>	
 *    <li> &nbsp Throws an exception in case of routing conflicts. </li>
 *   <ol>
 *  <br>
 * @author Machi
 *
 */
public class ResourceConfig {
	private static final List<ClassLoader> classLoadersList;
	private Set<Class<?>> classes = new HashSet<>();
	
	static {
		classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
	}

	public ResourceConfig() {
		super();
	}

	/**
	 * register all resources located under the given packages name
	 * 
	 * @param packes
	 * @throws IllegalArgumentException
	 * @throws {@link ServerException}
	 */
	public void packages(String... packages) throws IllegalArgumentException, ServerException {
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

		Set<Class<?>> classes = resourceReflections.getTypesAnnotatedWith(PATH.class);

		if (classes.isEmpty()) {
			throw new ResourceNotFoundException();
		}
		
		this.classes.addAll(classes);
	}

	/**
	 * Register a list of supplied resource classes
	 * @throws ServerException 
	 */
	public void classes(@SuppressWarnings("unchecked") Class<? extends Object> ...classes) throws ServerException {
		if (classes == null || classes.length == 0) {
			throw new IllegalArgumentException("Can not register a null or empty class");
		}

		Boolean found = false;
		for (Class<? extends Object> clazz : classes) {
			if (clazz == null) {
				throw new IllegalArgumentException("Null class argument is not allowed");
			}
			
			if (clazz.isAnnotationPresent(PATH.class)) {
				found = true;
				this.classes.add(clazz);
			}
		}
		
		if (!found) {
			throw new ResourceNotFoundException();
		}
	}

	public void setClasses(Set<Class<?>> classes) {
		this.classes = classes;
	}

	public Set<Resource> getResources() throws ServerException {
		Map<Resource,Resource> resources = new HashMap<>();
		
		Resource resource;
		
		for (Class<?> clazz:classes) {
			for (Method method: clazz.getDeclaredMethods()){
				if (!Resource.hasLocalAnnotation(method)) {
					continue;
				}
				
				resource = new Resource(clazz, method);
				
				if (resources.containsKey(resource)) {
					throw new UrlConfictException(resource,resources.get(resource));
				}
				
				resources.put(resource, resource);
				
			}
		}
		
		return Collections.unmodifiableSet(resources.keySet());
	}
}