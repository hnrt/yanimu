package com.hideakin.yanimu.maven;

import java.util.HashMap;
import java.util.Map;

public class DependencyScope {

	public static final String COMPILE = "compile";
	public static final String PROVIDED = "provided";
	public static final String RUNTIME = "runtime";
	public static final String TEST = "test";
	public static final String SYSTEM = "system";
	public static final String IMPORT = "import";
	public static final String DEFAULT = COMPILE;

	private static final Map<String, String> MAP;

	static {
		Map<String, String> map = new HashMap<>();
		map.put(COMPILE, COMPILE);
		map.put(PROVIDED, PROVIDED);
		map.put(RUNTIME, RUNTIME);
		map.put(TEST, TEST);
		map.put(SYSTEM, SYSTEM);
		map.put(IMPORT, IMPORT);
		MAP = Map.copyOf(map);
	}

	public static DependencyScope of(String key) {
		return new DependencyScope(key);
	}

	public final String value;

	private DependencyScope(String key) {
		String value = MAP.get(key);
		this.value = value != null ? value : key;
	}

}
