package com.hideakin.yanimu.maven.settings;

import com.hideakin.yanimu.xml.Element;

public class Server extends SimpleSetting {

	private static final long serialVersionUID = 4462833099566632042L;

	public static Server of() {
		return new Server();
	}

	public static Server of(Element element) {
		return new Server(element);
	}

	private Server() {
		super("server");
	}

	private Server(Element element) {
		super(element);
	}

	public String username() {
		return getString("username", null);
	}

	public String password() {
		return getString("password", null);
	}

	public String privateKey() {
		return getString("privateKey", null);
	}

	public String passphrase() {
		return getString("passphrase", null);
	}

	public String filePermissions() {
		return getString("filePermissions", null);
	}

	public String directoryPermissions() {
		return getString("directoryPermissions", null);
	}

	public void setUsername(String value) {
		setString("username", value);
	}

	public void setPassword(String value) {
		setString("password", value);
	}

	public void setPrivateKey(String value) {
		setString("privateKey", value);
	}

	public void setPassphrase(String value) {
		setString("passphrase", value);
	}

	public void setFilePermissions(String value) {
		setString("filePermissions", value);
	}

	public void setDirectoryPermissions(String value) {
		setString("directoryPermissions", value);
	}

}
