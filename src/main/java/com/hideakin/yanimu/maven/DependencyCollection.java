package com.hideakin.yanimu.maven;

import java.util.HashMap;
import java.util.Map;

import com.hideakin.yanimu.xml.Element;

public class DependencyCollection extends ArtifactCollection<Dependency> {

	private static final long serialVersionUID = 7653266384075314906L;

	private final Map<String, PomDocument> _poms = new HashMap<>();

	public DependencyCollection() {
		super();
	}

	public void load(Element element, PropertyManager propertyManager) {
		super.load(element, "dependency", propertyManager, e -> new Dependency(e));
	}

	public void load(Element element, RepositoryCollection repositories, PropertyManager propertyManager) {
		super.load(element, "dependency", propertyManager, e -> new Dependency(e));
		_poms.clear();
		for (Dependency dependency : super.values()) {
			if (dependency.scope() == DependencyScope.IMPORT && dependency.type() == ArtifactType.POM) {
				boolean successful = false;
				String groupId = propertyManager.translate(dependency.groupId());
				String artifactId = propertyManager.translate(dependency.artifactId());
				String version = propertyManager.translate(dependency.version());
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
