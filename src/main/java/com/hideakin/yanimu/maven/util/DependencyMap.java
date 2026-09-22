package com.hideakin.yanimu.maven.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.hideakin.yanimu.maven.PomDocument;
import com.hideakin.yanimu.xml.Element;

public class DependencyMap extends ArtifactMap<Dependency> {

	private static final long serialVersionUID = -1556933944749434731L;

	private final Map<String, PomDocument> _poms = new HashMap<>();

	public DependencyMap() {
		super();
	}

	public void load(Element element) {
		super.load(element, "dependency", e -> new Dependency(e));
	}

	public void load(Element element, RepositoryMap repositories, Function<String, String> translator) {
		super.load(element, "dependency", e -> new Dependency(e));
		_poms.clear();
		for (Dependency dependency : super.values()) {
			if (dependency.scope() == DependencyScope.IMPORT && dependency.type() == ArtifactType.POM) {
				boolean successful = false;
				String groupId = translator.apply(dependency.groupId());
				String artifactId = translator.apply(dependency.artifactId());
				String version = translator.apply(dependency.version());
				PomDocument pom = PomDocument.of(groupId, artifactId, version);
				if (pom.path() == null) {
					continue;
				}
				try {
					pom.load();
					successful = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!successful) {
					try {
						pom.load(repositories);
						successful = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (successful) {
					for (Dependency d : pom.dependencyManagement().values()) {
						_poms.put(pom.translate(d.ga()), pom);
					}
				}
			}
		}
	}

	public PomDocument pomDocument(String ga) {
		return _poms.get(ga);
	}

}
