package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.xml.Element;

public class Proxy extends SimpleSetting {

	private static final long serialVersionUID = 3722021328886169135L;

	public static Proxy of() {
		return new Proxy();
	}

	public static Proxy of(Element element) {
		return new Proxy(element);
	}

	private Proxy() {
		super("proxy");
	}

	private Proxy(Element element) {
		super(element);
	}

	public Boolean active() {
		return getBoolean("active", null);
	}

	public String protocol() {
		return getString("protocol", null);
	}

	public String host() {
		return getString("host", null);
	}

	public Integer port() {
		return getInteger("port", null);
	}

	public String username() {
		return getString("username", null);
	}

	public String password() {
		return getString("password", null);
	}

	public String nonProxyHosts() {
		return getString("nonProxyHosts", null);
	}

	public void setActive(Boolean value) {
		setBoolean("active", value);
	}

	public void setProtocol(String value) {
		setString("protocol", value);
	}

	public void setHost(String value) {
		setString("host", value);
	}

	public void setPort(Integer value) {
		setInteger("port", value);
	}

	public void setUsername(String value) {
		setString("username", value);
	}

	public void setPassword(String value) {
		setString("password", value);
	}

	public void setNonProxyHosts(String value) {
		setString("nonProxyHosts", value);
	}
	
}
