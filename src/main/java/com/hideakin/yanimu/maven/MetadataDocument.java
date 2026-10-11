package com.hideakin.yanimu.maven;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.hideakin.yanimu.maven.util.LocalRepository;
import com.hideakin.yanimu.maven.util.RemoteRepository;
import com.hideakin.yanimu.maven.util.Repository;
import com.hideakin.yanimu.maven.util.RepositoryMap;
import com.hideakin.yanimu.xml.Document;
import com.hideakin.yanimu.xml.Element;

public class MetadataDocument extends Document {

	public static final String FILENAME = "maven-metadata.xml";
	public static final String LOCAL_FILENAME = "maven-metadata-local.xml";

	public static final long DEFAULT_GRACE_PERIOD = 3600000L;

	public static MetadataDocument of(String groupId, String artifactId) {
		return new MetadataDocument(groupId, artifactId);
	}

	private String _groupId;
	private String _artifactId;
	private String _latest;
	private String _release;
	private List<String> _versions;
	private String _lastUpdated;

	private MetadataDocument(Path path) {
		super(path);
	}

	private MetadataDocument(String groupId, String artifactId) {
		super(LocalRepository.pathOf(groupId, artifactId, LOCAL_FILENAME));
		_groupId = groupId;
		_artifactId = artifactId;
	}

	private MetadataDocument(byte[] content) {
		super();
		try {
			load(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String groupId() {
		return _groupId;
	}

	public String artifactId() {
		return _artifactId;
	}

	public String latest() {
		return _latest;
	}

	public String release() {
		return _release;
	}

	public List<String> versions() {
		return List.copyOf(_versions);
	}

	public String lastUpdated() {
		return _lastUpdated;
	}

	@Override
	protected void onLoaded() {
		if (_root == null) {
			return;
		}
		_groupId = null;
		_artifactId = null;
		_latest = null;
		_release = null;
		_versions = null;
		_lastUpdated = null;
		Map<String, Consumer<Element>> map = new HashMap<>();
		map.put("groupId", e -> _groupId = e.innerText());
		map.put("artifactId", e -> _artifactId = e.innerText());
		map.put("versioning", e -> {
			_latest = e.getElement("/latest").innerText();
			_release = e.getElement("/release").innerText();
			_lastUpdated = e.getElement("/lastUpdated").innerText();
			List<Element> elements = getElements("/versions/version");
			if (elements.size() > 0) {
				_versions = new ArrayList<>();
				for (Element element : elements) {
					_versions.add(element.innerText());
				}
			}
		});
		for (Element e : _root.getElements("/*")) {
			Consumer<Element> c = map.get(e.name);
			if (c != null) {
				c.accept(e);
			}
		}
	}

	public void load(RepositoryMap repositories) throws Exception {
		load(repositories, DEFAULT_GRACE_PERIOD);
	}

	public void load(RepositoryMap repositories, long gracePeriod) throws Exception {
		List<MetadataDocument> list = new ArrayList<>();
		for (Repository repository : repositories.values()) {
			String baseUrl = repository.url();
			try {
				MetadataDocument document = load(baseUrl, gracePeriod);
				list.add(document);
			} catch (Exception e) {
			}
		}
		if (!repositories.containsUrl(RemoteRepository.CENTRAL_URL_1)
				&& !repositories.containsUrl(RemoteRepository.CENTRAL_URL_2)) {
			try {
				MetadataDocument document = load(RemoteRepository.CENTRAL_URL, gracePeriod);
				list.add(document);
			} catch (Exception e) {
			}
		}
		MetadataDocument latest = null;
		for (MetadataDocument next : list) {
			if (latest == null || (next._lastUpdated != null && latest._lastUpdated.compareTo(next._lastUpdated) < 0)) {
				latest = next;
			}
		}
		if (latest != null) {
			setPath(latest.path());
			load(latest.sequence());
		} else {
			throw new RuntimeException("Failed to download.");
		}
	}

	private MetadataDocument load(String baseUrl, long gracePeriod) throws Exception {
		Path path = LocalRepository.pathOf(baseUrl, _groupId, _artifactId, FILENAME);
		try {
			FileTime ft = Files.getLastModifiedTime(path());
			long mt = ft.toMillis();
			if (System.currentTimeMillis() <= mt + gracePeriod) {
				MetadataDocument document = new MetadataDocument(path);
				document.load();
				return document;
			}
		} catch (Exception e) {
		}
		String url = RemoteRepository.urlOf(baseUrl, _groupId, _artifactId, FILENAME);
		byte[] content = RemoteRepository.download(url);
		MetadataDocument document = new MetadataDocument(path);
		document.load(content);
		try {
			Path directory = path.getParent();
			if (!Files.exists(directory)) {
				Files.createDirectories(directory);
			}
			Files.write(path, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

}
