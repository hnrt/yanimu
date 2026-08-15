package com.hideakin.yanimu.xml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.hideakin.yanimu.xml.internal.Processor;

public class Document {

	protected Path _path;
	protected List<Node> _layout;
	protected XmlDeclaration _xmlDeclaration;
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

	public List<Node> layout() {
		return _layout;
	}

	public String version() {
		return _xmlDeclaration != null ? _xmlDeclaration.version : null;
	}

	public String encoding() {
		return _xmlDeclaration != null ? _xmlDeclaration.encoding : null;
	}

	public String standalone() {
		return _xmlDeclaration != null ? _xmlDeclaration.standalone : null;
	}

	public Element root() {
		return _root;
	}

	public void load() throws Exception {
		load(Files.readAllBytes(_path));
	}

	public void load(InputStream in) throws Exception {
		load(in.readAllBytes());
	}

	public void load(byte[] content) throws Exception {
		_layout = null;
		_xmlDeclaration = null;
		_root = null;
		Processor processor = new Processor(content);
		_layout = processor.parse();
		if (_layout.get(0) instanceof XmlDeclaration xmlDeclaration) {
			_xmlDeclaration = xmlDeclaration;
		}
		for (Node node : _layout) {
			if (node instanceof Element element) {
				_root = element;
				break;
			}
		}
	}

}
