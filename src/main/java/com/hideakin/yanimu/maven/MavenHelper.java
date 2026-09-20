package com.hideakin.yanimu.maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class MavenHelper {

	public static String getMavenHome(String fallback) {
		String value = System.getProperty("maven.home");
		if (value == null) {
			value = System.getenv("MAVEN_HOME");
			if (value == null) {
				String pathString = System.getenv("PATH");
				if (pathString != null) {
					String[] ss = pathString.split(File.pathSeparator);
					if (ss != null) {
						for (String s : ss) {
							Path mvnPath = Path.of(s).resolve("mvn");
							if (Files.exists(mvnPath)) {
								Path projectRootPath = Path.of(s).getParent();
								Path settingsPath = projectRootPath.resolve(Path.of("conf", "settings.xml"));
								if (Files.exists(settingsPath)) {
									value = projectRootPath.toString();
									break;
								}
							}
						}
					}
				}
			}
		}
		if (value == null) {
			// Try -Dmaven.home=/path/to/maven
			return fallback;
		}
		return value;
	}

}
