package com.hideakin.yanimu.maven.util;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hideakin.yanimu.xml.Document;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;

import static com.hideakin.yanimu.xml.Character.*;

/**
 * A mutable linked hash map object for the name-value pairs.
 */
public class PropertyMap extends LinkedHashMap<String, String> {

	private static final long serialVersionUID = 4507031764576908115L;

	/**
	 * The maximum iterations for the replacement processing in {@link PropertyMap#translate(String)}.
	 */
	public static final int MAX_TRANSLATION_ITERATIONS = 10;

	private Element _properties;

	/**
	 * Constructs a new object.
	 */
	public PropertyMap() {
		super();
	}

	/**
	 * Initializes this object with an {@code Element} object of properties.
	 * @param properties an {@code Element} object to be used for initializing this object
	 */
	public void load(Element properties) {
		super.clear();
		_properties = properties;
		if (_properties != null) {
			Element parent = _properties.parent(); 
			if (parent != null && "project".equals(parent.name)) {
				super.put("project.packaging", "jar");
				Document document = _properties.document();
				if (document != null) {
					Path path = document.path();
					if (path != null) {
						String basedir =  path.toAbsolutePath().getParent().toString();
						super.put("project.basedir", basedir);
					}
				}
				super.put("project.build.directory", "target/");
				super.put("project.build.outputDirectory", "target/classes");
				super.put("project.build.testOutputDirectory", "target/test-classes");
				super.put("project.build.sourceDirectory", "src/main/java");
				super.put("project.build.testSourceDirectory", "src/test/java");
				super.put("project.build.resources", "src/main/resources");
				super.put("project.build.testResources", "src/test/resources");
				super.put("project.build.finalName", "${project.artifactId}-${project.version}");
				super.put("settings.localRepository", "${user.home}/.m2/repository");
				//super.put("maven.version", "0.0.0");
				//super.put("settings.interactiveMode", "true");
				//super.put("settings.offline", "false");
				for (Node child : parent.children()) {
					if (child instanceof Element childElement) {
						if (!childElement.hasElement()) {
							super.put(parent.name + "." + childElement.name, childElement.innerText());
						}
					}
				}
			}
			for (Element element : _properties.getElements("/*")) {
				super.put(element.name, element.innerText());
			}
		}
	}

	/**
	 * Returns an unmodifiable {@code List} object containing name-value pairs in this object in its iteration order. 
	 * @return a {@code List} object of {@code Property}
	 */
	public List<Property> list() {
		List<Property> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : super.entrySet()) {
			list.add(new Property(entry.getKey(), entry.getValue()));
		}
		return List.copyOf(list);
	}

	@Override
	public String put(String key, String value) {
		String old = super.put(key, value);
		if (_properties != null) {
			Element element = _properties.getElement("/" + key);
			if (element != null) {
				element.setInnerText(value);
			} else {
				element = new Element(key, value);
				_properties.add(element);
			}
		} else {
			Element element = new Element(key, value);
			_properties = new Element("properties");
			_properties.add(element);
		}
		return old;
	}

	/**
	 * Replaces property references in the given {@code String} with their corresponding property value text.
	 * <p>
	 * A property reference is a text sequence of "${" followed by a property name and "}".
	 * <p>
	 * The replacement is performed as follows;
	 * first searches this map object for a property value indicated by the property name in a reference.
	 * If no value is found in this map object, then attempts to look up that name in the system properties.  
	 * If no value is found in the system properties either, then that property reference is left as is.
	 * <p>
	 * The replacement process is repeated while the resulting String still contains any property reference,
	 * up to a maximum of ten iterations.
	 * This prevents infinite expansion in cases where references expand into new references.
	 * @param source the input String containing property references
	 * @return the fully expanded String, or a partially expanded String if the iteration limit is reached
	 */
	public String translate(String source) {
		return translate(source, (List<String>)null);
	}

	/**
	 * Replaces property references in the specified {@code String} with their corresponding property value text.
	 * <p>
	 * A property reference is a text sequence of "${" followed by a property name and "}".
	 * <p>
	 * The replacement is performed as follows;
	 * first searches this map object for a property value indicated by the property name in a reference.
	 * If no value is found in this map object, then attempts to look up that name in the system properties.  
	 * If no value is found in the system properties either, then that property reference is left as is.
	 * <p>
	 * The replacement process is repeated while the resulting String still contains any property reference,
	 * up to a maximum of ten iterations.
	 * This prevents infinite expansion in cases where references expand into new references.
	 * @param source the input String containing property references
	 * @param names a {@code List} to receive the property names that appear in {@code source},
	 *              excluding the existing system properties, or null if not required
	 * @return the fully expanded String, or a partially expanded String if the iteration limit is reached
	 */
	public String translate(String source, List<String> names) {
		if (source == null) {
			return null;
		}
		String intermediate = source;
		StringBuilder output = new StringBuilder();
		for (int attempts = 0; attempts < MAX_TRANSLATION_ITERATIONS; attempts++) {
			if (translate(intermediate, output, names)) {
				intermediate = output.toString();
			} else {
				break;
			}
			output.setLength(0);
		}
		return intermediate;
	}

	/**
	 * Replaces property references in the specified {@code String}
	 * with their corresponding property value
	 * and writes the expanded text to the specified {@code StringBuilder}.
	 * <p>
	 * A property reference is a text sequence of "${" followed by a property name and "}".
	 * <p>
	 * The replacement is performed as follows;
	 * first searches this map object for a property value indicated by the property name in a reference.
	 * If no value is found in this map object, then attempts to look up that name in the system properties.  
	 * If no value is found in the system properties either, then that property reference is left as is.
	 * @param source the input {@code String} containing property references
	 * @param output the output {@code StringBuilder} for the expanded text to be written to
	 * @return true if the replacement was took place one or more times, false otherwise
	 */
	public boolean translate(String source, StringBuilder output) {
		return translate(source, output, null);
	}

	/**
	 * Replaces property references in the specified {@code String}
	 * with their corresponding property value
	 * and writes the expanded text to the specified {@code StringBuilder}.
	 * <p>
	 * A property reference is a text sequence of "${" followed by a property name and "}".
	 * <p>
	 * The replacement is performed as follows;
	 * first searches this map object for a property value indicated by the property name in a reference.
	 * If no value is found in this map object, then attempts to look up that name in the system properties.  
	 * If no value is found in the system properties either, then that property reference is left as is.
	 * @param source the input {@code String} containing property references
	 * @param output the output {@code StringBuilder} for the expanded text to be written to
	 * @param names a {@code List} to receive the property names that appear in {@code source},
	 *              excluding the existing system properties, or null if not required
	 * @return true if the replacement was took place one or more times, false otherwise
	 */
	public boolean translate(String source, StringBuilder output, List<String> names) {
		try (StringReader input = new StringReader(source)) {
			int changes = 0;
			int c = input.read();
			while (c >= 0) {
				if (c == '$') {
					int length = output.length();
					output.append((char)c);
					c = input.read();
					if (c == '{') {
						output.append((char)c);
						c = input.read();
						if (isNameStartChar(c)) {
							int start = output.length();
							output.append((char)c);
							c = input.read();
							while (isNameChar(c)) {
								output.append((char)c);
								c = input.read();
							}
							if (c == '}') {
								String name = output.substring(start);
								String value = super.get(name);
								if (value != null) {
									if (names != null) {
										names.add(name);
									}
								} else {
									value = System.getProperty(name);
								}
								if (value != null) {
									output.setLength(length);
									output.append(value);
									changes++;
								} else {
									if (names != null) {
										names.add(name);
									}
									output.append((char)c);
								}
								c = input.read();
							}
						}
					}
				} else {
					output.append((char)c);
					c = input.read();
				}
			}
			return changes > 0;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
