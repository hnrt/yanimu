package com.hideakin.yanimu.maven;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;
import com.hideakin.yanimu.xml.internal.Lexer;

public class PropertyManager extends LinkedHashMap<String, String> {

	private static final long serialVersionUID = 3445726735357959153L;

	public PropertyManager() {
		super();
	}

	public void load(Element root, Path path) {
		super.clear();
		super.put("project.packaging", "jar");
		super.put("project.basedir", path.toAbsolutePath().getParent().toString());
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
		for (Node child : root.children()) {
			if (child instanceof Element childElement) {
				if (!childElement.hasElement()) {
					super.put(root.name + "." + childElement.name, childElement.innerText());
				}
			}
		}
		for (Element element : root.getElements("/properties")) {
			for (Node node : element.children()) {
				if (node instanceof Element child) {
					super.put(child.name, child.innerText());
				}
			}
		}
	}

	public List<Property> getList() {
		List<Property> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : super.entrySet()) {
			list.add(new Property(entry.getKey(), entry.getValue()));
		}
		return List.copyOf(list);
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
				if (Lexer.isNameStartChar(c)) {
					buffer2.setLength(0);
					buffer2.append((char)c);
					c = r.read();
				} else {
					buffer.append("${");
					continue;
				}
				while (Lexer.isNameChar(c)) {
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
