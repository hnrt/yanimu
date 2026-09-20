package com.hideakin.yanimu.maven.settings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.hideakin.yanimu.maven.MavenHelper;
import com.hideakin.yanimu.maven.PropertyManager;
import com.hideakin.yanimu.xml.Document;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.util.FormatHelper;

public class SettingsDocument extends Document {

	public static final Path GLOBAL_PATH = Path.of(MavenHelper.getMavenHome("maven"), "conf", "settings.xml");
	public static final Path USER_PATH = Path.of(System.getProperty("user.home"), ".m2", "settings.xml");

	public static SettingsDocument of(Path path) {
		return new SettingsDocument(path);
	}

	private String _localRepository;
	private boolean _interactiveMode;
	private boolean _offline;
	private final List<String> _pluginGroups = new ArrayList<>();
	private final ServerCollection _servers = new ServerCollection();
	private final MirrorCollection _mirrors = new MirrorCollection();
	private final ProxyCollection _proxies = new ProxyCollection();
	private final ProfileCollection _profiles = new ProfileCollection();
	private final List<String> _activeProfiles = new ArrayList<>();
	
	private SettingsDocument(Path path) {
		super(path);
	}

	public String localRepository() {
		return _localRepository;
	}

	public boolean interactiveMode() {
		return _interactiveMode;
	}

	public boolean offline() {
		return _offline;
	}

	public List<String> pluginGroups() {
		return List.copyOf(_pluginGroups);
	}

	public ServerCollection servers() {
		return _servers;
	}

	public MirrorCollection mirrors() {
		return _mirrors;
	}

	public ProxyCollection proxies() {
		return _proxies;
	}

	public ProfileCollection profiles() {
		return _profiles;
	}

	public List<String> activeProfiles() {
		return List.copyOf(_activeProfiles);
	}

	@Override
	protected void onLoaded() {
		if (_root == null) {
			return;
		}
		_localRepository = _root.getElement("/localRepository").innerText(Path.of(System.getProperty("user.home"), ".m2", "repository").toString());
		_interactiveMode = FormatHelper.toBoolean(_root.getElement("/interactiveMode").innerText("true"), true);
		_offline = FormatHelper.toBoolean(_root.getElement("/offline").innerText("false"), false);
		for (Element element : _root.getElements("/pluginGroups/pluginGroup")) {
			String g = element.innerText();
			if (g != null) {
				_pluginGroups.add(g);
			}
		}
		addIfNotExist(_pluginGroups, "org.apache.maven.plugins");
		addIfNotExist(_pluginGroups, "org.codehaus.mojo");
		_servers.load(_root.getElement("/servers"));
		_mirrors.load(_root.getElement("/mirrors"));
		_proxies.load(_root.getElement("/proxies"));
		_profiles.load(_root.getElement("/profiles"));
		for (Element element : _root.getElements("/activeProfiles/activeProfile")) {
			String p = element.innerText();
			if (p != null) {
				_activeProfiles.add(p);
			}
		}
	}

	private static void addIfNotExist(List<String> list, String value) {
		if (!list.contains(value)) {
			list.add(value);
		}
	}

	public static String translate(String text) {
		PropertyManager pm = new PropertyManager();
		return pm.translate(text);
	}

}
