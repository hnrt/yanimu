package com.hideakin.yanimu.maven;

import java.nio.file.Path;
import java.util.Arrays;

public class LocalRepository {

	public static final Path PATH = Path.of(System.getProperty("user.home"), ".m2", "repository");
	public static final Path CUSTOM_PATH = Path.of(System.getProperty("user.home"), ".yanimu");

	public static Path pathOfPom(Artifact artifact) {
		String groupId = artifact.groupId();
		String artifactId = artifact.artifactId();
		String version = artifact.version();
		if (groupId != null && groupId.indexOf('.') > 0 && artifactId != null && version != null) {
			String fileName = artifactId + "-" + version + ".pom";
			String[] d = groupId.split("\\.");
			Path path = PATH
					.resolve(Path.of(d[0], Arrays.copyOfRange(d, 1, d.length)))
					.resolve(artifactId)
					.resolve(version)
					.resolve(fileName);
			return path;
		} else {
			return null;
		}
	}

	public static Path pathOf(String groupId, String artifactId, String fileName) {
		if (groupId != null && groupId.indexOf('.') > 0 && artifactId != null && fileName != null) {
			String[] d = groupId.split("\\.");
			Path path = PATH
					.resolve(Path.of(d[0], Arrays.copyOfRange(d, 1, d.length)))
					.resolve(artifactId)
					.resolve(fileName);
			return path;
		} else {
			return null;
		}
	}

	public static Path pathOf(String baseUrl, String groupId, String artifactId, String fileName) {
		if (baseUrl != null && groupId != null && groupId.indexOf('.') > 0 && artifactId != null && fileName != null) {
			String[] c = baseUrl
					.replaceAll("(?i)^https?://", "")
					.replaceAll("/[/]*$", "")
					.split("/");
			String[] d = groupId
					.split("\\.");
			Path path = CUSTOM_PATH
					.resolve(c.length > 1 ? Path.of(c[0], Arrays.copyOfRange(c, 1, c.length)) : Path.of(c[0]))
					.resolve(Path.of(d[0], Arrays.copyOfRange(d, 1, d.length)))
					.resolve(artifactId)
					.resolve(fileName);
			return path;
		} else {
			return null;
		}
	}

}
