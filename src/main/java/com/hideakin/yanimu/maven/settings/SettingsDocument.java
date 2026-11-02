package com.hideakin.yanimu.maven.settings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.hideakin.yanimu.maven.settings.util.MirrorMap;
import com.hideakin.yanimu.maven.settings.util.ProfileMap;
import com.hideakin.yanimu.maven.settings.util.ProxyMap;
import com.hideakin.yanimu.maven.settings.util.ServerMap;
import com.hideakin.yanimu.maven.util.MavenHelper;
import com.hideakin.yanimu.xml.Document;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.util.FormatHelper;

public class SettingsDocument extends Document {

	public static final Path GLOBAL_PATH = Path.of(MavenHelper.getMavenHome("maven"), "conf", "settings.xml");
	public static final Path USER_PATH = Path.of(System.getProperty("user.home"), ".m2", "settings.xml");

	public static final String DEFAULT_LOCAL_REPOSITORY = "${user.home}/.m2/repository";

	public static SettingsDocument of(Path path) {
		return new SettingsDocument(path);
	}

	private String _localRepository = DEFAULT_LOCAL_REPOSITORY;
	private boolean _interactiveMode = true;
	private boolean _offline = false;
	private final List<String> _pluginGroups = new ArrayList<>();
	private ServerMap _servers = new ServerMap();
	private MirrorMap _mirrors = new MirrorMap();
	private ProxyMap _proxies = new ProxyMap();
	private ProfileMap _profiles = new ProfileMap();
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

	public ServerMap servers() {
		return _servers;
	}

	public MirrorMap mirrors() {
		return _mirrors;
	}

	public ProxyMap proxies() {
		return _proxies;
	}

	public ProfileMap profiles() {
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
		_localRepository = _root.getElement("/localRepository").innerText(DEFAULT_LOCAL_REPOSITORY);
		_interactiveMode = FormatHelper.toBoolean(_root.getElement("/interactiveMode").innerText("true"), true);
		_offline = FormatHelper.toBoolean(_root.getElement("/offline").innerText("false"), false);
		_pluginGroups.clear();
		for (Element element : _root.getElements("/pluginGroups/pluginGroup")) {
			String g = element.innerText();
			if (g != null) {
				_pluginGroups.add(g);
			}
		}
		MavenHelper.addIfNotExist(_pluginGroups, "org.apache.maven.plugins");
		MavenHelper.addIfNotExist(_pluginGroups, "org.codehaus.mojo");
		_servers = new ServerMap(_root.getElement("/servers"));
		_mirrors = new MirrorMap(_root.getElement("/mirrors"));
		_proxies = new ProxyMap(_root.getElement("/proxies"));
		_profiles = new ProfileMap(_root.getElement("/profiles"));
		_activeProfiles.clear();
		for (Element element : _root.getElements("/activeProfiles/activeProfile")) {
			String p = element.innerText();
			if (p != null) {
				_activeProfiles.add(p);
			}
		}
	}

}
