package com.hideakin.yanimu.maven;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

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
	public void load(byte[] content) throws Exception {
		super.load(content);
		initialize();
	}

	private void initialize() {
		if (super.root() == null) {
			return;
		}
		List<Element> elements = getElements("/metadata/groupId");
		_groupId = elements.size() > 0 ? elements.get(0).innerText() : null;
		elements = getElements("/metadata/artifactId");
		_artifactId = elements.size() > 0 ? elements.get(0).innerText() : null;
		elements = getElements("/metadata/versioning/latest");
		_latest = elements.size() > 0 ? elements.get(0).innerText() : null;
		elements = getElements("/metadata/versioning/release");
		_release = elements.size() > 0 ? elements.get(0).innerText() : null;
		elements = getElements("/metadata/versioning/versions/version");
		if (elements.size() > 0) {
			_versions = new ArrayList<>();
			for (Element element : elements) {
				_versions.add(element.innerText());
			}
		} else {
			_versions = null;
		}
		elements = getElements("/metadata/versioning/lastUpdated");
		_lastUpdated = elements.size() > 0 ? elements.get(0).innerText() : null;
	}

	public void load(RepositoryCollection repositories) throws Exception {
		load(repositories, DEFAULT_GRACE_PERIOD);
	}

	public void load(RepositoryCollection repositories, long gracePeriod) throws Exception {
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
			Files.write(path, content);
		} catch (Exception e) {
		}
		return document;
	}

}
