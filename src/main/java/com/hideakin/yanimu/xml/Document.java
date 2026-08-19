package com.hideakin.yanimu.xml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.internal.Processor;

public class Document extends NodeList {

	protected Path _path;
	protected XmlDeclaration _xmlDeclaration;
	protected DocumentTypeDeclaration _dtd;
	protected Element _root;
	protected String[] _warnings;
	protected String[] _information;

	public Document() {
		super(DOCUMENT);
	}

	public Document(Path path) {
		super(DOCUMENT);
		_path = path;
	}

	public Path path() {
		return _path;
	}

	public void setPath(Path path) {
		_path = path;
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

	public DocumentTypeDeclaration dtd() {
		return _dtd;
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
		_nodeList.clear();
		_xmlDeclaration = null;
		_dtd = null;
		_root = null;
		_warnings = null;
		_information = null;
		Processor processor = new Processor(content);
		List<Node> nodeList = processor.parse();
		_nodeList.addAll(nodeList);
		if (firstNode() instanceof XmlDeclaration xmlDeclaration) {
			_xmlDeclaration = xmlDeclaration;
		}
		for (Node node : _nodeList) {
			if (node instanceof Element element) {
				_root = element;
				_root.setParent(this);
				break;
			} else if (node instanceof DocumentTypeDeclaration dtd) {
				_dtd = dtd;
			}
		}
		_warnings = processor.warnings();
		_information = processor.information();
	}

	public String[] warnings() {
		return _warnings;
	}

	public String[] information() {
		return _information;
	}

}
