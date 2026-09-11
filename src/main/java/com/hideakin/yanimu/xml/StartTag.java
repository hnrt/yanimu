package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StartTag extends Tag {

	public static StartTag of(String name) {
		return new StartTag(name);
	}

	public static StartTag of(List<Node> nodeList, List<Attribute> attributeList) {
		return new StartTag(nodeList, attributeList);
	}

	protected static final byte[] START_SEQUENCE = {'<'};
	protected static final byte[] STAG_END_SEQUENCE = {'>'};
	protected static final byte[] EETAG_END_SEQUENCE = {'/', '>'};
	protected static final byte[] EQ_SEQUENCE = {'='};
	protected static final byte[] SP_SEQUENCE = {' '};

	protected final List<Attribute> _attributeList = new ArrayList<>();

	protected StartTag(String name) {
		this(STAG,
				List.of(TerminalNode.of(STAG_START, START_SEQUENCE),
						TerminalNode.of(NAME, name),
						TerminalNode.of(STAG_END, STAG_END_SEQUENCE)),
				List.of());
	}

	protected StartTag(List<Node> nodeList, List<Attribute> attributeList) {
		this(STAG, nodeList, attributeList);
	}

	protected StartTag(int type, List<Node> nodeList, List<Attribute> attributeList) {
		super(type, nodeList);
		_attributeList.addAll(attributeList);
	}

	/**
	 * Returns the number of attributes in this node.
	 * @return the number of attributes
	 */
	public int attributeCount() {
		return _attributeList.size();
	}

	/**
	 * Returns the attribute value at the specified index.
	 * @param index the index of the attribute to be retrieved
	 * @return the attribute value if it exists, or null if not
	 */
	public String attribute(int index) {
		return attribute(index, null);
	}

	/**
	 * Returns the attribute value at the specified index.
	 * @param index the index of the attribute to be retrieved
	 * @param defaultValue the value to be returned if the specified attribute doesn't exist
	 * @return the attribute value if it exists, or {@code defaultValue} if not
	 */
	public String attribute(int index, String defaultValue) {
		int size = _attributeList.size();
		if (0 <= index && index < size) {
			Attribute a = _attributeList.get(index); 
			return a.value;
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the attribute value of the specified key.
	 * @param key the key of the attribute to be retrieved
	 * @return the attribute value if it exists, or null if not
	 */
	public String attribute(String key) {
		return attribute(key, null);
	}

	/**
	 * Returns the attribute value of the specified key.
	 * @param key the key of the attribute to be retrieved
	 * @param defaultValue the value to be returned if the specified attribute doesn't exist
	 * @return the attribute value if it exists, or {@code defaultValue} if not
	 */
	public String attribute(String key, String defaultValue) {
		for (Attribute a : _attributeList) {
			if (a.key.equals(key)) {
				return a.value;
			}
		}
		return defaultValue;
	}

	/**
	 * Returns all the attributes.
	 * @return the immutable list of the attributes
	 */
	public List<Attribute> attributeList() {
		return List.copyOf(_attributeList);
	}

	/**
	 * Returns all the attribute keys.
	 * @return the immutable list of the attribute keys
	 */
	public List<String> attributeKeys() {
		List<String> keys = new ArrayList<>(_attributeList.size());
		for (Attribute a : _attributeList) {
			keys.add(a.key);
		}
		return List.copyOf(keys);
	}

	/**
	 * Returns all the attribute values.
	 * @return the immutable list of the attribute values
	 */
	public List<String> attributeValues() {
		int size = _attributeList.size();
		List<String> values = new ArrayList<>(size);
		for (Attribute a : _attributeList) {
			values.add(a.value);
		}
		return List.copyOf(values);
	}

	/**
	 * Sets an attribute to the specified index in this start tag.
	 * @param index the attribute to be inserted
	 * @param key the key of the attribute
	 * @param value the raw attribute value, not enclosed in quotes
	 */
	public void setAttribute(int index, String key, String value) {
		int size = _attributeList.size();
		if (0 <= index && index < size) {
			int nodeIndex = 2;
			int attributeIndex = -1;
			Node node;
			while (true) {
				node = _nodeList.get(nodeIndex);
				if (node.type == ATTRIBUTE) {
					attributeIndex++;
					if (attributeIndex == index) {
						break;
					}
				} else if (node.type == STAG_END || node.type == EETAG_END) {
					throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribte:: Possible corruption.");
				}
				nodeIndex++;
			}
			Attribute attributeOld = (Attribute)node;
			int quoteCharacter = attributeOld.last().sequence()[0];
			Node valueNode = Node.of(ATT_VALUE, attributeValue(value, quoteCharacter));
			List<Node> nodeListNew = new ArrayList<>(attributeOld.nodeList());
			if (!attributeOld.key.equals(key)) {
				Node keyNode = Node.of(NAME, key.getBytes(StandardCharsets.UTF_8));
				nodeListNew.set(0, keyNode);
			}
			nodeListNew.set(nodeListNew.size() - 1, valueNode);
			Attribute attributeNew = new Attribute(nodeListNew, key, value);
			_nodeList.set(nodeIndex, attributeNew);
			_attributeList.set(attributeIndex, attributeNew);
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribte:: Index out of range.");
		}
	}

	/**
	 * Replaces the existing attribute associated with the specified key with a new attribute.<br/>
	 * If the specified key is not found, a new attribute is appended after the last existing attribute.<br/>
	 * The new attribute to be created is associated with the same key and contains the specified value.<br/>
	 * The {@code value} is treated as the raw attribute value and is not expected to include any quote characters. 
	 * @param key the key of the attribute
	 * @param value the raw attribute value, not enclosed in quotes
	 */
	public void setAttribute(String key, String value) {
		int nodeIndex = 2;
		int attributeIndex = -1;
		Node node;
		while (true) {
			node = _nodeList.get(nodeIndex);
			if (node.type == ATTRIBUTE) {
				Attribute a = (Attribute)node;
				attributeIndex++;
				if (a.key.equals(key)) {
					int quoteCharacter = a.last().sequence()[0];
					Node valueNode = Node.of(ATT_VALUE, attributeValue(value, quoteCharacter));
					List<Node> nodeListNew = new ArrayList<>(a.nodeList());
					nodeListNew.set(nodeListNew.size() - 1, valueNode);
					Attribute attributeNew = new Attribute(nodeListNew, key, value);
					_nodeList.set(nodeIndex, attributeNew);
					_attributeList.set(attributeIndex, attributeNew);
					break;
				}
			} else if (node.type == STAG_END || node.type == EETAG_END) {
				attributeIndex++;
				Node nodePrevious = _nodeList.get(nodeIndex - 1);
				if (nodePrevious.type == S) {
					nodeIndex--;
				}
				_nodeList.add(nodeIndex, Node.of(S, SP_SEQUENCE));
				nodeIndex++;
				List<Node> nodeListNew =
						List.of(Node.of(NAME, key.getBytes(StandardCharsets.UTF_8)),
						Node.of(EQ, EQ_SEQUENCE),
						Node.of(ATT_VALUE, attributeValue(value, '\"')));
				Attribute attributeNew = new Attribute(nodeListNew, key, value);
				_nodeList.add(nodeIndex, attributeNew);
				_attributeList.add(attributeIndex, attributeNew);
				break;
			}
			nodeIndex++;
		}
	}

	/**
	 * Adds a new attribute node next to the last attribute in this start tag.
	 * @param key the key of the attribute
	 * @param value the value of the attribute, not enclosed by quote characters
	 */
	public void addAttribute(String key, String value) {
		int size = _attributeList.size();
		addAttribute(size, key, value);
	}

	/**
	 * Adds a new attribute node next to the last attribute in this start tag.
	 * @param index the attribute index to which the new attribute node is to be inserted 
	 * @param key the key of the attribute
	 * @param value the value of the attribute, not enclosed by quote characters
	 */
	public void addAttribute(int index, String key, String value) {
		int size = _attributeList.size();
		if (0 <= index && index <= size) {
			int nodeIndex = 2;
			int attributeIndex = -1;
			Node node;
			while (true) {
				node = _nodeList.get(nodeIndex);
				if (node.type == ATTRIBUTE) {
					attributeIndex++;
					if (attributeIndex == index) {
						break;
					}
				} else if (node.type == STAG_END || node.type == EETAG_END) {
					attributeIndex++;
					break;
				}
				nodeIndex++;
			}
			Node nodePrevious = _nodeList.get(nodeIndex - 1);
			if (nodePrevious.type == S) {
				nodeIndex--;
			}
			_nodeList.add(nodeIndex, Node.of(S, SP_SEQUENCE));
			nodeIndex++;
			List<Node> nodeListNew =
					List.of(Node.of(NAME, key.getBytes(StandardCharsets.UTF_8)),
							Node.of(EQ, EQ_SEQUENCE),
							Node.of(ATT_VALUE, attributeValue(value, '\"')));
			Attribute attributeNew = new Attribute(nodeListNew, key, value);
			_nodeList.add(nodeIndex, attributeNew);
			_attributeList.add(attributeIndex, attributeNew);
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribte:: Index out of range.");
		}
	}

	/**
	 * Creates a new byte sequence representing the value of an attribute node,
	 * enclosed in the specified quote characters.<br/>
	 * The provided {@code source} is treated as the raw attribute value and
	 * is not expected to include any quote characters.
	 * @param source the raw attribute value, not enclosed in quotes
	 * @param quoteCharacter the quote character used to enclose {@code source}
	 * @return a newly created byte array containing the quoted attribute value
	 */
	private static byte[] attributeValue(String source, int quoteCharacter) {
		return attributeValue(source, quoteCharacter, 0).getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Creates a new String representing the value of an attribute node,
	 * enclosed in the specified quote character.<br/>
	 * The provided {@code source} is treated as the raw attribute value and
	 * is not expected to include any quote characters.<br/>
	 * If the {@code source} contains the specified quote character, this
	 * method attempts to use the alternative quote character.<br/>
	 * For example, if the first attempt uses the double quotation mark,
	 * the second attempt uses the apostrophe, and vice versa.<br/>
	 * If the {@code source} contains both supported quote characters,
	 * this method replaces the quote characters occurring in the {@code source} with the corresponding entity reference.
	 * @param source the raw attribute value, not enclosed in quotes
	 * @param quoteCharacter the quote character used to enclose {@code source}
	 * @param quoteCharacterAttempted the quote character used previously
	 * @return a newly created String containing the quoted attribute value
	 */
	private static String attributeValue(String source, int quoteCharacter, int quoteCharacterAttempted) {
		int pos = source.indexOf(quoteCharacter);
		if (pos >= 0) {
			if (quoteCharacterAttempted == 0) {
				return attributeValue(source, quoteCharacter == '\"' ? '\'' : '\"' , quoteCharacter);
			} else {
				String entity = quoteCharacter == '\"' ? "&quot;" : "&apos;";
				StringBuilder buffer = new StringBuilder();
				buffer.append((char)quoteCharacter);
				int start = 0;
				do {
					if (start < pos) {
						buffer.append(source.substring(start, pos));
					}
					buffer.append(entity);
					start = pos + 1;
					pos = source.indexOf(quoteCharacter, start);
				} while (pos >= start);
				if (start < source.length()) {
					buffer.append(source.substring(start));
				}
				buffer.append((char)quoteCharacter);
				return buffer.toString();		
			}
		} else {
			StringBuilder buffer = new StringBuilder();
			buffer.append((char)quoteCharacter);
			buffer.append(source);
			buffer.append((char)quoteCharacter);
			return buffer.toString();		
		}
	}

	/**
	 * Removes all the attributes in this start tag.
	 */
	public void removeAllAttributes() {
		while (_attributeList.size() > 0) {
			Node node = removeAttribute(0);
			if (node.type == ATTRIBUTE) {
				continue;
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::removeAllAttribtes:: Possible corruption.");
			}
		}
		int size = _nodeList.size();
		if ((size == 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(2).type == STAG_END || _nodeList.get(2).type == EETAG_END))
			|| (size == 4
					&& _nodeList.get(0).type == STAG_START
					&& _nodeList.get(1).type == NAME
					&& _nodeList.get(2).type == S
					&& (_nodeList.get(3).type == STAG_END || _nodeList.get(3).type == EETAG_END))) {
			//OK
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::removeAllAttribtes:: Possible corruption.");
		}
	}

	/**
	 * Removes the attribute at the specified index.
	 * @param index of the attribute to be removed
	 * @return the removed attribute node, or NullNode if the specified index is invalid
	 */
	public Node removeAttribute(int index) {
		int size = _attributeList.size();
		if (0 <= index && index < size) {
			int nodeIndex = 2;
			int attributeIndex = -1;
			Node node;
			while (true) {
				node = _nodeList.get(nodeIndex);
				if (node.type == ATTRIBUTE) {
					attributeIndex++;
					if (attributeIndex == index) {
						break;
					}
				} else if (node.type == STAG_END || node.type == EETAG_END) {
					throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribte:: Possible corruption.");
				}
				nodeIndex++;
			}
			Node nodePrevious = _nodeList.get(nodeIndex - 1);
			if (nodePrevious.type == S) {
				_nodeList.remove(nodeIndex);
				_nodeList.remove(nodeIndex - 1);
				_attributeList.remove(node);
				return node;
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribte:: Possible corruption.");
			}
		} else {
			return NullNode;
		}
	}

	/**
	 * Removes the attribute associated with the specified key.
	 * @param key of the attribute to be removed
	 * @return the removed attribute node, or NullNode if the specified index is invalid
	 */
	public Node removeAttribute(String key) {
		int nodeIndex = 2;
		Node node;
		while (true) {
			node = _nodeList.get(nodeIndex);
			if (node.type == ATTRIBUTE) {
				Attribute a = (Attribute)node;
				if (a.key.equals(key)) {
					break;
				}
			} else if (node.type == STAG_END || node.type == EETAG_END) {
				return NullNode;
			}
			nodeIndex++;
		}
		Node nodePrevious = _nodeList.get(nodeIndex - 1);
		if (nodePrevious.type == S) {
			_nodeList.remove(nodeIndex);
			_nodeList.remove(nodeIndex - 1);
			_attributeList.remove(node);
			return node;
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribte:: Possible corruption.");
		}
	}

	/**
	 * Creates a new start tag node that has the same attributes as this node.
	 * @return a newly created start tag node
	 */
	public StartTag toStartTag() {
		if (last().type == STAG_END) {
			return StartTag.of(_nodeList, _attributeList);
		} else {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, TerminalNode.of(STAG_END, STAG_END_SEQUENCE));
			return StartTag.of(nodeList, _attributeList);
		}
	}

	/**
	 * Creates a new empty element tag node that has the same attributes as this node.
	 * @return a newly created empty element tag node
	 */
	public EmptyElementTag toEmptyElementTag() {
		if (last().type == EETAG_END) {
			return EmptyElementTag.of(_nodeList, _attributeList);
		} else {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, TerminalNode.of(EETAG_END, EETAG_END_SEQUENCE));
			return EmptyElementTag.of(nodeList, _attributeList);
		}
	}

}
