package com.hideakin.yanimu.xml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.hideakin.yanimu.xml.internal.Parser;

public class Document {

	protected Path _path;
	protected byte[] _contents;
	protected Element _root;

	public Document() {
	}

	public Document(Path path) {
		_path = path;
	}

	public Path path() {
		return _path;
	}

	public void setPath(Path path) {
		_path = path;
	}

	public Element root() {
		return _root;
	}

	public void load() throws Exception {
		_contents = Files.readAllBytes(_path);
		_root = null;
		Parser parser = new Parser(_contents);
		parser.run();
		_root = parser.root();
	}

	public void load(InputStream in) throws Exception {
		_contents = in.readAllBytes();
		_root = null;
		Parser parser = new Parser(_contents);
		parser.run();
		_root = parser.root();
	}

}
