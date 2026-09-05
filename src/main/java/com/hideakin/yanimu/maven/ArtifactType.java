package com.hideakin.yanimu.maven;

import java.util.HashMap;
import java.util.Map;

public class ArtifactType {

	public static final String POM = "pom";
	public static final String JAR = "jar";
	public static final String TEST_JAR = "test-jar";
	public static final String MAVEN_PLUGIN = "maven-plugin";
	public static final String EJB = "ejb";
	public static final String EJB_CLIENT = "ejb-client";
	public static final String WAR = "war";
	public static final String EAR = "ear";
	public static final String RAR = "rar";
	public static final String JAVA_SOURCE = "java-source";
	public static final String JAVADOC = "javadoc";
	public static final String DEFAULT = JAR;

	private static final Map<String, String> MAP;

	static {
		Map<String, String> map = new HashMap<>();
		map.put(POM, POM);
		map.put(JAR, JAR);
		map.put(TEST_JAR, TEST_JAR);
		map.put(MAVEN_PLUGIN, MAVEN_PLUGIN);
		map.put(EJB, EJB);
		map.put(EJB_CLIENT, EJB_CLIENT);
		map.put(WAR, WAR);
		map.put(EAR, EAR);
		map.put(RAR, RAR);
		map.put(JAVA_SOURCE, JAVA_SOURCE);
		map.put(JAVADOC, JAVADOC);
		MAP = Map.copyOf(map);
	}

	public static ArtifactType of(String key) {
		return new ArtifactType(key);
	}

	public final String value;

	private ArtifactType(String key) {
		String value = MAP.get(key);
		this.value = value != null ? value : key;
	}

}
