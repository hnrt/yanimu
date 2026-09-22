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
		return translate(source, 0);
	}

	private String translate(String source, int count) {
		StringBuilder buffer = new StringBuilder();
		StringBuilder buffer2 = new StringBuilder();
		try (StringReader r = new StringReader(source)) {
			int changes = 0;
			int c = r.read();
			while (c != -1) {
				if (c == '$') {
					c = r.read();
				} else {
					buffer.append((char)c);
					c = r.read();
					continue;
				}
				if (c == '{') {
					c = r.read();
				} else {
					buffer.append((char)'$');
					continue;
				}
				if (isNameStartChar(c)) {
					buffer2.setLength(0);
					buffer2.append((char)c);
					c = r.read();
				} else {
					buffer.append("${");
					continue;
				}
				while (isNameChar(c)) {
					buffer2.append((char)c);
					c = r.read();
				}
				String key = buffer2.toString();
				if (c == '}') {
					c = r.read();
				} else {
					buffer.append('$');
					buffer.append('{');
					buffer.append(key);
					continue;
				}
				String value = super.get(key);
				if (value == null) {
					value = System.getProperty(key);
				}
				if (value != null) {
					buffer.append(value);
					changes++;
				} else {
					buffer.append('$');
					buffer.append('{');
					buffer.append(key);
					buffer.append('}');
				}
			}
			if (changes > 0 && count < 100) {
				return translate(buffer.toString(), count + 1);
			} else {
				return buffer.toString();
			}
		} catch (IOException e) {
			return source;
		}
	}

}
