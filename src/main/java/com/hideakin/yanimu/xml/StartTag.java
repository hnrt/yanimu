package com.hideakin.yanimu.xml;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.hideakin.yanimu.xml.Character.*;

public class StartTag extends Tag {

	public static StartTag of(String name) {
		return new StartTag(name);
	}

	public static StartTag of(List<Node> nodeList) {
		return new StartTag(nodeList);
	}

	protected static final byte[] START_SEQUENCE = {'<'};
	protected static final byte[] STAG_END_SEQUENCE = {'>'};
	protected static final byte[] EETAG_END_SEQUENCE = {'/', '>'};
	protected static final byte[] EQ_SEQUENCE = {'='};
	protected static final byte[] SP_SEQUENCE = {' '};

	protected StartTag(String name) {
		this(STAG, List.of(
				TerminalNode.of(STAG_START, START_SEQUENCE),
				TerminalNode.of(NAME, name),
				TerminalNode.of(STAG_END, STAG_END_SEQUENCE)));
	}

	protected StartTag(List<Node> nodeList) {
		this(STAG, nodeList);
	}

	protected StartTag(int type, List<Node> nodeList) {
		super(type, nodeList);
	}

	/**
	 * Returns the number of attributes in this node.
	 * @return the number of attributes
	 */
	public int attributeCount() {
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int count = 0;
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeCount:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					count++;
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeCount:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::attributeCount:: Possible corruption.");
			}
			return count;
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::attributeCount:: Possible corruption.");
		}
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
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int attrIndex = -1;
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					if (++attrIndex == index) {
						return ((Attribute)node).value;
					}
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
		}
		return defaultValue;
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
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					Attribute a = (Attribute)node;
					if (a.key.equals(key)) {
						return a.value;
					}
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::attribute:: Possible corruption.");
		}
		return defaultValue;
	}

	/**
	 * Returns all the attributes.
	 * @return the immutable list of the attributes
	 */
	public List<Attribute> attributeList() {
		List<Attribute> aa = new ArrayList<>();
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeList:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					Attribute a = (Attribute)node;
					aa.add(a);
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeList:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::attributeList:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::attributeList:: Possible corruption.");
		}
		return List.copyOf(aa);
	}

	/**
	 * Returns all the attribute keys.
	 * @return the immutable list of the attribute keys
	 */
	public List<String> attributeKeys() {
		List<String> keys = new ArrayList<>();
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeKeys:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					Attribute a = (Attribute)node;
					keys.add(a.key);
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeKeys:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::attributeKeys:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::attributeKeys:: Possible corruption.");
		}
		return List.copyOf(keys);
	}

	/**
	 * Returns all the attribute values.
	 * @return the immutable list of the attribute values
	 */
	public List<String> attributeValues() {
		List<String> values = new ArrayList<>();
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeValues:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					Attribute a = (Attribute)node;
					values.add(a.value);
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::attributeValues:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::attributeValues:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::attributeValues:: Possible corruption.");
		}
		return List.copyOf(values);
	}

	/**
	 * Sets an attribute at the specified index in this start tag.
	 * @param index the index at which the attribute to be set
	 * @param key the key of the attribute
	 * @param value the raw attribute value, not enclosed in quotes
	 */
	public void setAttribute(int index, String key, String value) {
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int attrIndex = -1;
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					if (++attrIndex == index) {
						Attribute attributeOld = (Attribute)node;
						List<Node> nodeListNew = new ArrayList<>(attributeOld.nodeList());
						Node keyNode = Node.of(NAME, key.getBytes(StandardCharsets.UTF_8));
						int quoteCharacter = attributeOld.last().sequence()[0];
						Node valueNode = Node.of(ATT_VALUE, attributeValue(value, quoteCharacter));
						nodeListNew.set(0, keyNode);
						nodeListNew.set(nodeListNew.size() - 1, valueNode);
						Attribute attributeNew = new Attribute(nodeListNew, key, value);
						_nodeList.set(nodeIndex + 1, attributeNew);
						return;
					}
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Index out of range.");
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
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
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					Attribute a = (Attribute)node;
					if (a.key.equals(key)) {
						List<Node> nodeListNew = new ArrayList<>(a.nodeList());
						Node keyNode = Node.of(NAME, key.getBytes(StandardCharsets.UTF_8));
						int quoteCharacter = a.last().sequence()[0];
						Node valueNode = Node.of(ATT_VALUE, attributeValue(value, quoteCharacter));
						nodeListNew.set(0, keyNode);
						nodeListNew.set(nodeListNew.size() - 1, valueNode);
						Attribute attributeNew = new Attribute(nodeListNew, key, value);
						_nodeList.set(nodeIndex + 1, attributeNew);
						return;
					}
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
			}
			List<Node> nodeListNew =
					List.of(Node.of(NAME, key.getBytes(StandardCharsets.UTF_8)),
					Node.of(EQ, EQ_SEQUENCE),
					Node.of(ATT_VALUE, attributeValue(value, '\"')));
			Attribute attributeNew = new Attribute(nodeListNew, key, value);
			_nodeList.add(upperBound, Node.of(S, SP_SEQUENCE));
			_nodeList.add(upperBound + 1, attributeNew);
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::setAttribute:: Possible corruption.");
		}
	}

	/**
	 * Adds a new attribute node next to the last attribute in this start tag.
	 * @param key the key of the attribute
	 * @param value the value of the attribute, not enclosed by quote characters
	 */
	public void addAttribute(String key, String value) {
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribute:: Possible corruption.");
			}
			List<Node> nodeListNew =
					List.of(Node.of(NAME, key.getBytes(StandardCharsets.UTF_8)),
					Node.of(EQ, EQ_SEQUENCE),
					Node.of(ATT_VALUE, attributeValue(value, '\"')));
			Attribute attributeNew = new Attribute(nodeListNew, key, value);
			_nodeList.add(upperBound, Node.of(S, SP_SEQUENCE));
			_nodeList.add(upperBound + 1, attributeNew);
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribute:: Possible corruption.");
		}
	}

	/**
	 * Adds a new attribute node next to the last attribute in this start tag.
	 * @param index the attribute index to which the new attribute node is to be inserted 
	 * @param key the key of the attribute
	 * @param value the value of the attribute, not enclosed by quote characters
	 */
	public void addAttribute(int index, String key, String value) {
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int attrIndex = -1;
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribute:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					if (++attrIndex == index) {
						List<Node> nodeListNew =
								List.of(Node.of(NAME, key.getBytes(StandardCharsets.UTF_8)),
										Node.of(EQ, EQ_SEQUENCE),
										Node.of(ATT_VALUE, attributeValue(value, '\"')));
						Attribute attributeNew = new Attribute(nodeListNew, key, value);
						_nodeList.add(nodeIndex, Node.of(S, SP_SEQUENCE));
						_nodeList.add(nodeIndex + 1, attributeNew);
						return;
					}
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribute:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribute:: Possible corruption.");
			}
			if (++attrIndex == index) {
				List<Node> nodeListNew =
						List.of(Node.of(NAME, key.getBytes(StandardCharsets.UTF_8)),
						Node.of(EQ, EQ_SEQUENCE),
						Node.of(ATT_VALUE, attributeValue(value, '\"')));
				Attribute attributeNew = new Attribute(nodeListNew, key, value);
				_nodeList.add(upperBound, Node.of(S, SP_SEQUENCE));
				_nodeList.add(upperBound + 1, attributeNew);
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribute:: Index out of range.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::addAttribute:: Possible corruption.");
		}
	}

	/**
	 * Removes all the attributes in this start tag.
	 */
	public void removeAllAttributes() {
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			if (upperBound == size - 1) {
				Node node0 = _nodeList.get(0);
				Node node1 = _nodeList.get(1);
				Node node2 =  _nodeList.get(size - 1);
				_nodeList.clear();
				_nodeList.add(node0);
				_nodeList.add(node1);
				_nodeList.add(node2);
			} else if (_nodeList.get(upperBound).type == S) {
				Node node0 = _nodeList.get(0);
				Node node1 = _nodeList.get(1);
				Node node2 = _nodeList.get(size - 2);
				Node node3 =  _nodeList.get(size - 1);
				_nodeList.clear();
				_nodeList.add(node0);
				_nodeList.add(node1);
				_nodeList.add(node2);
				_nodeList.add(node3);
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::removeAllAttributes:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::removeAllAttributes:: Possible corruption.");
		}
	}

	/**
	 * Removes the attribute at the specified index.
	 * @param index of the attribute to be removed
	 * @return the removed attribute node, or NullNode if the specified index is invalid
	 */
	public Node removeAttribute(int index) {
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int attrIndex = -1;
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					if (++attrIndex == index) {
						_nodeList.remove(nodeIndex + 1);
						_nodeList.remove(nodeIndex);
						return node;
					}
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
		}
		return NullNode;
	}

	/**
	 * Removes the attribute associated with the specified key.
	 * @param key of the attribute to be removed
	 * @return the removed attribute node, or NullNode if the specified index is invalid
	 */
	public Node removeAttribute(String key) {
		int size = _nodeList.size();
		if (size >= 3
				&& _nodeList.get(0).type == STAG_START
				&& _nodeList.get(1).type == NAME
				&& (_nodeList.get(size - 1).type == STAG_END || _nodeList.get(size - 1).type == EETAG_END)) {
			int upperBound = (size - 1) & ~1;
			for (int nodeIndex = 2; nodeIndex < upperBound; nodeIndex += 2) {
				Node node = _nodeList.get(nodeIndex);
				if (node.type == S) {
					// Looks good.
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
				}
				node = _nodeList.get(nodeIndex + 1);
				if (node.type == ATTRIBUTE) {
					Attribute a = (Attribute)node;
					if (a.key.equals(key)) {
						_nodeList.remove(nodeIndex + 1);
						_nodeList.remove(nodeIndex);
						return node;
					}
				} else {
					throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
				}
			}
			if (upperBound == size - 1 || _nodeList.get(upperBound).type == S) {
				// Looks good.
			} else {
				throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
			}
		} else {
			throw new RuntimeException(this.getClass().getSimpleName() + "::removeAttribute:: Possible corruption.");
		}
		return NullNode;
	}

	/**
	 * Creates a new start tag node that has the same attributes as this node.
	 * @return a newly created start tag node
	 */
	public StartTag toStartTag() {
		if (last().type == STAG_END) {
			return StartTag.of(_nodeList);
		} else {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, TerminalNode.of(STAG_END, STAG_END_SEQUENCE));
			return StartTag.of(nodeList);
		}
	}

	/**
	 * Creates a new empty element tag node that has the same attributes as this node.
	 * @return a newly created empty element tag node
	 */
	public EmptyElementTag toEmptyElementTag() {
		if (last().type == EETAG_END) {
			return EmptyElementTag.of(_nodeList);
		} else {
			List<Node> nodeList = new ArrayList<>(_nodeList);
			nodeList.set(nodeList.size() - 1, TerminalNode.of(EETAG_END, EETAG_END_SEQUENCE));
			return EmptyElementTag.of(nodeList);
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

}
