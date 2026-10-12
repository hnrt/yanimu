package com.hideakin.yanimu.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hideakin.yanimu.xml.doctype.DocumentTypeDeclaration;
import com.hideakin.yanimu.xml.doctype.EntityDeclaration;
import com.hideakin.yanimu.xml.doctype.InternalEntityDefinition;
import com.hideakin.yanimu.xml.internal.Processor;
import com.hideakin.yanimu.xml.util.EntityMap;

import static com.hideakin.yanimu.xml.NullElement.NULL_ELEMENT;

/**
 * The XML Document.
 */
public class Document extends NodeList {

	/**
	 * The file path
	 */
	protected Path _path;

	/**
	 * The XML declaration
	 */
	protected XmlDeclaration _xml;

	/**
	 * The document type declaration
	 */
	protected DocumentTypeDeclaration _dtd;

	/**
	 * The entity map
	 */
	protected EntityMap _entities;

	/**
	 *The document root element
	 */
	protected Element _root;

	/**
	 * Initializes a newly created node with no path.
	 */
	public Document() {
		super(DOCUMENT);
		_entities = new EntityMap();
	}

	/**
	 * Initializes a newly created node with a path.
	 * @param path a file path to assign to this node.
	 */
	public Document(Path path) {
		super(DOCUMENT);
		_path = path;
		_entities = new EntityMap();
	}

	/**
	 * Returns the file path currently set.
	 * @return the file path currently set, or {@code null} if not set
	 */
	public Path path() {
		return _path;
	}

	/**
	 * Sets the file path.
	 * @param path a file path to assign to this node.
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
	 * Sets the XML declaration.
	 * <p>
	 * If {@code null} is specified, the current XML declaration is removed from this document.
	 * @param xml an XML declaration to assign; {@code null} clears the current XML declaration
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
	 * Sets the document type declaration.
	 * <p>
	 * If {@code null} is specified, the current document type declaration is removed from this document.
	 * @param dtd a document type declaration to assign; {@code null} clears the current document type declaration
	 */
	public void setDtd(DocumentTypeDeclaration dtd) {
		for (int i = 0; i < size(); i++) {
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
				_entities = populateEntityMap();
				return;
			case ELEMENT:
				if (dtd != null) {
					add(i, dtd);
				}
				_dtd = dtd;
				_entities = populateEntityMap();
				return;
			default:
				break;
			}
		}
		if (dtd != null) {
			add(dtd);
		}
		_dtd = dtd;
		_entities = populateEntityMap();
	}

	private EntityMap populateEntityMap() {
		EntityMap entityMap = new EntityMap();
		if (_dtd != null) {
			for (Object obj : _dtd.declarations) {
				if (obj instanceof EntityDeclaration entity) {
					if (entity.definition instanceof InternalEntityDefinition ied) {
						entityMap.put(ied.key, ied);
					}
				}
			}
		}
		return entityMap;
	}

	/**
	 * Returns the root element.
	 * @return the root element, or {@code null} if not set
	 */
	public Element root() {
		return _root;
	}

	/**
	 * Sets the root {@code Element}.
	 * <p>
	 * If {@code null} is specified, the current root element is removed from this document.
	 * @param root an {@code Element} to assign as the root; {@code null} clears the current root
	 */
	public void setRoot(Element root) {
		for (int i = 0; i < size(); i++) {
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
			default:
				break;
			}
		}
		if (root != null) {
			add(root);
		}
		_root = root;
	}

	/**
	 * Reads a byte sequence from a file specified by the path and parses them to construct an XML document.
	 * @throws IOException if an I/O error occurs
	 * @throws ParseException if a parse error occurs
	 */
	public void load() throws IOException {
		load(Files.readAllBytes(_path), new ParseResult());
	}

	/**
	 * Reads a byte sequence from a file specified by the path and parses them to construct an XML document.
	 * @param result an object to receive information caught while parsing the byte sequence
	 * @throws IOException if an I/O error occurs
	 * @throws ParseException if a parse error occurs
	 */
	public void load(ParseResult result) throws IOException {
		load(Files.readAllBytes(_path), result);
	}

	/**
	 * Reads a byte sequence from the specified {@code InputStream} and parses them to construct an XML document.
	 * @param in an {@code InputStream} to read a byte sequence from
	 * @throws IOException if an I/O error occurs
	 * @throws ParseException if a parse error occurs
	 */
	public void load(InputStream in) throws IOException {
		load(in.readAllBytes(), new ParseResult());
	}

	/**
	 * Reads a byte sequence from the specified {@code InputStream} and parses them to construct an XML document.
	 * @param in an {@code InputStream} to read a byte sequence from
	 * @param result an object to receive information caught while parsing the byte sequence
	 * @throws IOException if an I/O error occurs
	 * @throws ParseException if a parse error occurs
	 */
	public void load(InputStream in, ParseResult result) throws IOException {
		load(in.readAllBytes(), result);
	}

	/**
	 * Parses the specified byte sequence to construct an XML document.
	 * @param content the byte sequence to parse
	 * @throws ParseException if a parse error occurs
	 */
	public void load(byte[] content) {
		load(content, new ParseResult());
	}

	/**
	 * Parses the specified byte sequence to construct an XML document.
	 * @param content a byte sequence to parse
	 * @param result an object to receive information caught while parsing the byte sequence
	 * @throws ParseException if a parse error occurs
	 */
	public void load(byte[] content, ParseResult result) {
		_nodeList.clear();
		_xml = null;
		_dtd = null;
		_entities = new EntityMap();
		_root = null;
		Processor processor = new Processor(content, _entities, result);
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
		onLoaded();
	}

	/**
	 * Executes the object specific processing.
	 * Override this method in each subclass,
	 * instead of overriding {@link Document#load(byte[],ParseResult)},
	 * if necessary.
	 */
	protected void onLoaded() {
	}

	/**
	 * Serializes this document into a byte sequence and writes it to a file.
	 * <p>
	 * The byte sequence is generated by serializing the nodes contained in this document in their stored order.
	 * The output encoding is determined as follows:
	 * <ul>
	 * <li>If this document contains an XML declaration with an {@code encoding} attribute, that encoding is used.</li>
	 * <li>Otherwise, UTF-8 is used.</li>
	 * </ul>
	 * <p>
	 * The resulting byte sequence is written to the file specified by the {@code _path} field.
	 * @throws IOException if an I/O error occurs while writing the file
	 * @throws NullPointerException if the path is not yet set
	 */
	public void save() throws IOException {
		if (_path == null) {
			throw new NullPointerException("Path not set.");
		}
		Path parent = _path.getParent();
		if (parent == null) {
			parent = _path.toAbsolutePath().getParent();
			if (parent == null) {
				throw new IOException("Unable to locate the parent directory of " + _path);
			}
		}
		if (Files.exists(_path)) {
			if (Files.isDirectory(_path)) {
				throw new IOException("Unable to write to a directory.");
			}
		} else if (!Files.exists(parent)) {
			Files.createDirectories(parent);
		}
		String fileName = _path.getFileName().toString() + "." + UUID.randomUUID().toString();
		Path path = parent.resolve(fileName);
		try {
			byte[] bb = sequence();
			if (_xml != null && _xml.encoding != null && !_xml.encoding.toUpperCase().equals("UTF-8")) {
				String s = new String(bb, StandardCharsets.UTF_8);
				bb = s.getBytes(_xml.encoding);
			}
			Files.write(path, bb);
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
	 * Locates {@code Element} nodes that match the criteria specified by {@code name}.
	 * @param name a tag name pattern used to locate {@code Element} nodes.
	 *             The pattern may include multiple tag names separated by slashes
	 *             to specify an element hierarchy.
	 *             If {@code name} begins with a slash, the search is performed starting
	 *             from the root element.
	 *             Otherwise, the search begins from any descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return a {@code List} of {@code Element} nodes
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
	 * Locates the first {@code Element} node that matches the criteria specified by {@code name}.
	 * @param name a tag name pattern used to locate {@code Element} nodes.
	 *             The pattern may include multiple tag names separated by slashes
	 *             to specify an element hierarchy.
	 *             If {@code name} begins with a slash, the search is performed starting
	 *             from the root element.
	 *             Otherwise, the search begins from any descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return an {@code Element} node, or {@link NullElement#NULL_ELEMENT} if not found
	 */
	public Element getElement(String name) {
		List<Element> elementList = getElements(name);
		return elementList.size() > 0 ? elementList.get(0) : NULL_ELEMENT;
	}

	/**
	 * Returns the line number at the specified offset.
	 * @param offset the byte offset of interest from the beginning of this document
	 * @return the line number
	 */
	public int toLineNumber(int offset) {
		return offset < 0 ? 0 : lineCount(offset) + 1;
	}

	/**
	 * Returns the column number at the specified offset.
	 * @param offset the byte offset of interest from the beginning of this document
	 * @return the column number
	 */
	public int toColumnNumber(int offset) {
		return offset < 0 ? 0 : columnCount(0, offset) + 1;
	}

	/**
	 * Replaces entity references and character references in the given text
	 * with their corresponding replacement text.
	 * <p>
	 * Entity references are the predefined entities and the ones that appear in the entity declarations.
	 * <p>
	 * The replacement process is repeated while the resulting text still contains any entity or character reference,
	 * up to a maximum of ten iterations.
	 * This prevents infinite expansion in cases where references expand into new references.
	 * @param source an input {@code String} containing entity or character references
	 * @return a fully expanded {@code String}, or a partially expanded {@code String} if the iteration limit is reached
	 */
	public String translate(String source) {
		if (_entities != null) {
			return _entities.translate(source);
		} else {
			return source;
		}
	}

}
