package com.hideakin.yanimu.maven;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.hideakin.yanimu.maven.util.Artifact;
import com.hideakin.yanimu.maven.util.DependencyMap;
import com.hideakin.yanimu.maven.util.LocalRepository;
import com.hideakin.yanimu.maven.util.PluginMap;
import com.hideakin.yanimu.maven.util.Property;
import com.hideakin.yanimu.maven.util.PropertyMap;
import com.hideakin.yanimu.maven.util.RemoteRepository;
import com.hideakin.yanimu.maven.util.Repository;
import com.hideakin.yanimu.maven.util.RepositoryMap;
import com.hideakin.yanimu.maven.util.SimpleArtifact;
import com.hideakin.yanimu.xml.Document;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.ParseResult;

@SuppressWarnings("unused")
public class PomDocument extends Document implements Artifact {

	public static PomDocument of(Path path) {
		return new PomDocument(path);
	}

	public static PomDocument of(String groupId, String artifactId, String version) {
		return new PomDocument(groupId, artifactId, version);
	}

	private String _modelVersion;
	private String _groupId;
	private String _artifactId;
	private String _version;
	private final PropertyMap _properties = new PropertyMap();
	private final PluginMap _pluginManagement = new PluginMap();
	private final PluginMap _plugins = new PluginMap();
	private final DependencyMap _dependencyManagement = new DependencyMap();
	private final DependencyMap _dependencies = new DependencyMap();
	private final RepositoryMap _repositories = new RepositoryMap();
	private final RepositoryMap _pluginRepositories = new RepositoryMap();

	private PomDocument(Path path) {
		super(path);
	}

	private PomDocument(String groupId, String artifactId, String version) {
		super(LocalRepository.pathOfPom(groupId, artifactId, version));
		_groupId = groupId;
		_artifactId = artifactId;
		_version = version;
	}

	public String modelVersion() {
		return _modelVersion;
	}

	@Override
	public String groupId() {
		return _groupId;
	}

	@Override
	public String artifactId() {
		return _artifactId;
	}

	@Override
	public String version() {
		return _version;
	}

	@Override
	public String ga() {
		String g = groupId();
		String a = artifactId();
		return SimpleArtifact.ga(g, a);
	}

	public PropertyMap properties() {
		return _properties;
	}

	public String property(String key) {
		return _properties.get(key);
	}

	public void setProperty(String key, String value) {
		_properties.put(key, value);
	}

	public String translate(String text) {
		return _properties.translate(text);
	}

	public PluginMap pluginManagement() {
		return _pluginManagement;
	}

	public PluginMap plugins() {
		return _plugins;
	}

	public DependencyMap dependencyManagement() {
		return _dependencyManagement;
	}

	public DependencyMap dependencies() {
		return _dependencies;
	}
	
	public RepositoryMap repositories() {
		return _repositories;
	}
	
	public RepositoryMap pluginRepositories() {
		return _pluginRepositories;
	}

	@Override
	protected void onLoaded() {
		if (_root == null) {
			return;
		}
		Map<String, Consumer<Element>> map = new HashMap<>();
		map.put("modelVersion", e -> _modelVersion = e.innerText());
		map.put("groupId", e -> _groupId = e.innerText());
		map.put("artifactId", e -> _artifactId = e.innerText());
		map.put("version", e -> _version = e.innerText());
		for (Element e : _root.getElements("/*")) {
			Consumer<Element> c = map.get(e.name);
			if (c != null) {
				c.accept(e);
			}
		}
		_properties.load(_root.getElement("/properties"));
		_repositories.load(_root.getElement("/repositories"), _root.getElements("/repositories/repository"));
		_pluginRepositories.load(_root.getElement("/pluginRepositories"), _root.getElements("/pluginRepositories/pluginRepository"));
		_pluginManagement.load(_root.getElement("/build/pluginManagement/plugins"));
		_plugins.load(_root.getElement("/build/plugins"));
		_dependencyManagement.load(_root.getElement("/dependencyManagement/dependencies"), _repositories, x -> _properties.translate(x));
		_dependencies.load(_root.getElement("/dependencies"));
	}

	public void load(RepositoryMap repositories) throws Exception {
		for (Repository repository : repositories.values()) {
			try {
				load(repository.url());
				return;
			} catch (Exception e) {
			}
		}
		if (!repositories.containsUrl(RemoteRepository.CENTRAL_URL_1)
				&& !repositories.containsUrl(RemoteRepository.CENTRAL_URL_2)) {
			try {
				load(RemoteRepository.CENTRAL_URL);
				return;
			} catch (Exception e) {
			}
		}
		throw new IOException("Failed to download.");
	}

	public void load(String baseUrl) throws Exception {
		String url = RemoteRepository.urlOfPom(baseUrl, _groupId, _artifactId, _version);
		byte[] content = RemoteRepository.download(url);
		load(content);
		try {
			Path directory = _path.getParent();
			if (!Files.exists(directory)) {
				Files.createDirectories(directory);
			}
			Files.write(_path, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final String PROPERTY_REFERENCE_PATTERN = "^\\$\\{[^${}]*\\}$";

	public String referencingPropertyKey(String value) {
		if (value.matches(PROPERTY_REFERENCE_PATTERN)) {
			String key;
			do {
				key = value.substring(2, value.length() - 1);
				value = property(key);
				if (value == null) {
					break;
				}
			} while(value.matches(PROPERTY_REFERENCE_PATTERN));
			return key;
		} else {
			return null;
		}
	}

}
