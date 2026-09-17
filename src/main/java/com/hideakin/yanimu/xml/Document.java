package com.hideakin.yanimu.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.internal.Processor;

public class Document extends NodeList {

	protected Path _path;
	protected XmlDeclaration _xml;
	protected DocumentTypeDeclaration _dtd;
	protected Element _root;

	public Document() {
		super(DOCUMENT);
	}

	public Document(Path path) {
		super(DOCUMENT);
		_path = path;
	}

	/**
	 * Returns the file path currently set.
	 * @return the file path currently set.
	 */
	public Path path() {
		return _path;
	}

	/**
	 * Sets the file path.
	 * @param path the file path to assign
	 */
	public void setPath(Path path) {
		_path = path;
	}

	/**
	 * Returns the XML declaration.
	 * @return the XML declaration, or {@code null} if not set
	 */
	public XmlDeclaration xml() {
		return _xml;
	}

	/**
	 * Sets the XML declaration of this {@code Document}.
	 * <p>
	 * If {@code xml} is {@code null}, the current XML declaration is removed from this {@code Document}.
	 * @param xml the XML declaration to assign; {@code null} clears the current XML declaration
	 */
	public void setXml(XmlDeclaration xml) {
		if (get(0).type == XML_DECL) {
			if (xml != null) {
				set(0, xml);
			} else {
				remove(0);
				if (get(0).type == S) {
					remove(0);
				}
			}
		} else if (xml != null) {
			add(0, xml);
		}
		_xml = xml;
	}

	/**
	 * Returns the document type declaration.
	 * @return the document type declaration, or {@code null} if not set
	 */
	public DocumentTypeDeclaration dtd() {
		return _dtd;
	}

	/**
	 * Sets the document type declaration of this {@code Document}.
	 * <p>
	 * If {@code dtd} is {@code null}, the current document type declaration is removed from this {@code Document}.
	 * @param dtd the document type declaration to assign; {@code null} clears the current document type declaration
	 */
	public void setDtd(DocumentTypeDeclaration dtd) {
		for (int i = 0; ; i++) {
			switch (get(i).type) {
			case DOCTYPE_DECL:
				if (dtd != null) {
					set(i, dtd);
				} else {
					remove(i);
					if (get(i).type == S) {
						remove(i);
					}
				}
				_dtd = dtd;
				return;
			case ELEMENT:
			case NULL:
				if (dtd != null) {
					add(i, dtd);
				}
				_dtd = dtd;
				return;
			default:
				break;
			}
		}
	}

	/**
	 * Returns the root {@code Element} of this {@code Document}.
	 * @return the root {@code Element}, or {@code null} if not set
	 */
	public Element root() {
		return _root;
	}

	/**
	 * Sets the root {@code Element} of this {@code Document}.
	 * <p>
	 * If {@code root} is {@code null}, the current root {@code Element} is removed from this {@code Document}.
	 * @param root the {@code Element} to assign as the root; {@code null} clears the current root
	 */
	public void setRoot(Element root) {
		for (int i = 0; ; i++) {
			switch (get(i).type) {
			case ELEMENT:
				if (root != null) {
					set(i, root);
				} else {
					remove(i);
					if (get(i).type == S) {
						remove(i);
					}
				}
				_root = root;
				return;
			case NULL:
				if (root != null) {
					add(i, root);
				}
				_root = root;
				return;
			default:
				break;
			}
		}
	}

	/**
	 * Reads the byte sequence from a file specified by the path and parses them as an XML document.
	 * @throws Exception
	 */
	public void load() throws Exception {
		load(Files.readAllBytes(_path), new ParseResult());
	}

	/**
	 * Reads the byte sequence from a file specified by the path and parses them as an XML document.
	 * @param result the information caught while parsing the document
	 * @throws Exception
	 */
	public void load(ParseResult result) throws Exception {
		load(Files.readAllBytes(_path), result);
	}

	/**
	 * Reads the byte sequence from the specified {@code InputStream} and parses them as an XML document.
	 * @param in {@code InputStream} to read the byte sequence from
	 * @throws Exception
	 */
	public void load(InputStream in) throws Exception {
		load(in.readAllBytes(), new ParseResult());
	}

	/**
	 * Reads the byte sequence from the specified {@code InputStream} and parses them as an XML document.
	 * @param in {@code InputStream} to read the byte sequence from
	 * @param result the information caught while parsing the document
	 * @throws Exception
	 */
	public void load(InputStream in, ParseResult result) throws Exception {
		load(in.readAllBytes(), result);
	}

	/**
	 * Parses the specified byte sequence as an XML document.
	 * @param content the byte sequence to parse
	 * @throws Exception
	 */
	public void load(byte[] content) throws Exception {
		load(content, new ParseResult());
	}

	/**
	 * Parses the specified byte sequence as an XML document.
	 * @param content the byte sequence to parse
	 * @param result the information caught while parsing the document
	 * @throws Exception
	 */
	public void load(byte[] content, ParseResult result) throws Exception {
		_nodeList.clear();
		_xml = null;
		_dtd = null;
		_root = null;
		Processor processor = new Processor(content, result);
		List<Node> nodeList = processor.parse();
		_nodeList.addAll(nodeList);
		if (first() instanceof XmlDeclaration xml) {
			_xml = xml;
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
	}

	public void save() throws Exception {
		if (Files.exists(_path)) {
			if (Files.isDirectory(_path)) {
				throw new IOException("Unable to write to a directory.");
			}
		} else if (!Files.exists(_path.getParent())) {
			Files.createDirectories(_path.getParent());
		}
		String fileName = _path.getFileName().toString() + "." + UUID.randomUUID().toString();
		Path path = _path.getParent().resolve(fileName);
		try {
			Files.write(path, sequence());
			Files.move(path, _path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				Files.deleteIfExists(path);
			} catch (Exception g) {
				g.printStackTrace();
			}
		}
	}

	/**
	 * Locates {@code Element} instances that match the criteria specified by {@code name}.
	 * @param name the tag name pattern used to locate {@code Element} instances.
	 *             The pattern may include multiple tag names separated by slashes
	 *             to specify an element hierarchy.
	 *             If {@code name} begins with a slash, the search is performed starting
	 *             from the root element.
	 *             Otherwise, the search begins from any descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return {@code List} of {@code Element} instances
	 */
	public List<Element> getElements(String name) {
		List<Element> elementList = new ArrayList<>();
		if (name != null && name.length() > 0 && _root != null) {
			String[] names = name.split("/");
			boolean isRelative = names[0].length() > 0;
			int index = isRelative ? 0 : 1;
			boolean anyMatch = names[index].equals("*");
			if (anyMatch || _root.name.equals(names[index])) {
				if (index + 1 < names.length) {
					elementList.addAll(_root.getElements(names, index + 1, false));
				} else {
					elementList.add(_root);
				}
			}
			if (isRelative) {
				elementList.addAll(_root.getElements(names, index, true));
			}
		}
		return elementList;
	}

	/**
	 * Locates the first {@code Element} instance that matches the criteria specified by {@code name}.
	 * @param name the tag name pattern used to locate {@code Element} instances.
	 *             The pattern may include multiple tag names separated by slashes
	 *             to specify an element hierarchy.
	 *             If {@code name} begins with a slash, the search is performed starting
	 *             from the root element.
	 *             Otherwise, the search begins from any descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return {@code Element} instance
	 */
	public Element getElement(String name) {
		List<Element> elementList = getElements(name);
		return elementList.size() > 0 ? elementList.get(0) : null;
	}

	/**
	 * Returns the line number at the specified offset.
	 * @param offset to check
	 * @return the line number
	 */
	public int toLineNumber(int offset) {
		return offset < 0 ? 0 : lineCount(offset) + 1;
	}

	/**
	 * Returns the column number at the specified offset.
	 * @param offset to check
	 * @return the column number
	 */
	public int toColumnNumber(int offset) {
		return offset < 0 ? 0 : columnCount(0, offset) + 1;
	}

}
