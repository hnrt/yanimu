package com.hideakin.yanimu.xml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hideakin.yanimu.xml.internal.Processor;

public class Document {

	protected Path _path;
	protected byte[] _content;
	protected Token[] _layout;
	protected XmlDeclaration _xmlDeclaration;
	protected Element _root;
	protected final Map<String, String> _entities = new HashMap<>();
	protected final Map<String, String> _references = new HashMap<>();

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

	public byte[] content() {
		return _content;
	}

	public Token[] layout() {
		return _layout;
	}

	public void setLayout(List<Token> layout) {
		_layout = layout.toArray(new Token[layout.size()]);
	}

	public void setXmlDeclaration(XmlDeclaration xmlDeclaration) {
		_xmlDeclaration = xmlDeclaration;
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

	public void setRoot(Element root) {
		_root = root;
	}
	
	public void load() throws Exception {
		load(Files.readAllBytes(_path));
	}

	public void load(InputStream in) throws Exception {
		load(in.readAllBytes());
	}

	public void load(byte[] content) throws Exception {
		_content = content;
		_xmlDeclaration = null;
		_root = null;
		Processor processor = new Processor(content);
		processor.parse(this);
	}

	public String getEntity(String key) {
		return _entities.get(key);
	}

	public void putEntity(String key, String value) {
		_entities.put(key, value);
	}

	public String getReference(String key) {
		return _references.get(key);
	}

	public void putReference(String key, String value) {
		_references.put(key, value);
	}

}
