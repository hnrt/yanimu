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

import com.hideakin.yanimu.xml.Document;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.ParseResult;

@SuppressWarnings("unused")
public class PomDocument extends Document {

	public static PomDocument of(Path path) {
		return new PomDocument(path);
	}

	public static PomDocument of(Artifact artifact) {
		return new PomDocument(artifact);
	}

	private String _modelVersion;
	private String _groupId;
	private String _artifactId;
	private String _version;
	private final PropertyManager _propertyManager = new PropertyManager();
	private final PluginCollection _pluginManagement = new PluginCollection();
	private final PluginCollection _plugins = new PluginCollection();
	private final DependencyCollection _dependencyManagement = new DependencyCollection();
	private final DependencyCollection _dependencies = new DependencyCollection();
	private final RepositoryCollection _repositories = new RepositoryCollection();
	private final RepositoryCollection _pluginRepositories = new RepositoryCollection();

	private PomDocument(Path path) {
		super(path);
	}

	private PomDocument(Artifact artifact) {
		super(LocalRepository.pathOfPom(artifact));
		_groupId = artifact.groupId();
		_artifactId = artifact.artifactId();
		_version = artifact.version();
	}

	public String modelVersion() {
		return _modelVersion;
	}

	public String groupId() {
		return _groupId;
	}

	public String artifactId() {
		return _artifactId;
	}

	public String version() {
		return _version;
	}

	public List<Property> properties() {
		return _propertyManager.getList();
	}

	public String property(String key) {
		return _propertyManager.get(key);
	}

	public PluginCollection pluginManagement() {
		return _pluginManagement;
	}

	public PluginCollection plugins() {
		return _plugins;
	}

	public DependencyCollection dependencyManagement() {
		return _dependencyManagement;
	}

	public DependencyCollection dependencies() {
		return _dependencies;
	}
	
	public RepositoryCollection repositories() {
		return _repositories;
	}
	
	public RepositoryCollection pluginRepositories() {
		return _pluginRepositories;
	}

	public List<String> repositoryUrls() {
		List<String> urls = new ArrayList<>();
		for (Repository repository : _repositories.values()) {
			String url = repository.url();
			if (!urls.contains(url)) {
				urls.add(url);
			}
		}
		for (Repository repository : _pluginRepositories.values()) {
			String url = repository.url();
			if (!urls.contains(url)) {
				urls.add(url);
			}
		}
		return List.copyOf(urls);
	}

	@Override
	public void load() throws Exception {
		super.load();
		initialize();
	}

	@Override
	public void load(ParseResult result) throws Exception {
		super.load(result);
		initialize();
	}

	@Override
	public void load(InputStream in) throws Exception {
		super.load(in);
		initialize();
	}

	@Override
	public void load(InputStream in, ParseResult result) throws Exception {
		super.load(in, result);
		initialize();
	}

	@Override
	public void load(byte[] content) throws Exception {
		super.load(content);
		initialize();
	}

	@Override
	public void load(byte[] content, ParseResult result) throws Exception {
		super.load(content, result);
		initialize();
	}

	private void initialize() {
		if (super.root() == null) {
			return;
		}
		Map<String, Consumer<Element>> map = new HashMap<>();
		map.put("modelVersion", e -> _modelVersion = e.innerText());
		map.put("groupId", e -> _groupId = e.innerText());
		map.put("artifactId", e -> _artifactId = e.innerText());
		map.put("version", e -> _version = e.innerText());
		for (Element e : super.root().getElements("/*")) {
			Consumer<Element> c = map.get(e.name);
			if (c != null) {
				c.accept(e);
			}
		}
		_propertyManager.load(super.root(), super.path());
		_repositories.load(super.root().getElement("/repositories"));
		_pluginRepositories.load(super.root().getElement("/pluginRepositories"));
		_pluginManagement.load(super._root.getElement("/build/pluginManagement/plugins"));
		_plugins.load(super._root.getElement("/build/plugins"));
		_dependencyManagement.load(super.root().getElement("/dependencyManagement/dependencies"), _repositories);
		_dependencies.load(super.root().getElement("/dependencies"));
	}

	public void load(RepositoryCollection repositories) throws Exception {
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
		String url = RemoteRepository.urlOfPom(baseUrl, baseUrl, baseUrl, baseUrl);
		byte[] content = RemoteRepository.download(url);
		load(content);
		try {
			Files.write(_path, content);
		} catch (Exception e) {
		}
	}

}
