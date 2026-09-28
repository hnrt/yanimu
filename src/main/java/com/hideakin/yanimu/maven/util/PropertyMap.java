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

public class PropertyMap extends LinkedHashMap<String, String> {

	private static final long serialVersionUID = 4507031764576908115L;

	public static final int MAX_TRANSLATION_ITERATIONS = 10;

	private Element _properties;

	public PropertyMap() {
		super();
	}

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

	public String translate(String source) {
		if (source == null) {
			return null;
		}
		String intermediate = source;
		StringBuilder output = new StringBuilder();
		for (int attempts = 0; attempts < MAX_TRANSLATION_ITERATIONS; attempts++) {
			if (translate(intermediate, output)) {
				intermediate = output.toString();
			} else {
				break;
			}
			output.setLength(0);
		}
		return intermediate;
	}

	public boolean translate(String source, StringBuilder output) {
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
								String key = output.substring(start);
								String value = super.get(key);
								if (value == null) {
									value = System.getProperty(key);
								}
								if (value != null) {
									output.setLength(length);
									output.append(value);
									changes++;
								} else {
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
