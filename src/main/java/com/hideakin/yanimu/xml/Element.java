package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

import com.hideakin.yanimu.xml.internal.Lexer;

public class Element extends NodeList {

	public final String name;
	protected Object _parent;

	/**
	 * Constructs a new Element that has an empty content.
	 * <p>
	 * The newly created Element has no parent (its parent is initialized to {@code null}).
	 * @param name the tag name
	 */
	public Element(String name) {
		super(ELEMENT, EmptyElementTag.of(name));
		this.name = name;
		_parent = null;
	}

	/**
	 * Constructs a new Element whose content is provided as a String.
	 * <p>
	 * The specified {@code innerText} may be represented as multiple nodes,
	 * such as CHAR_DATA, ENTITY_REF, or CHAR_REF, depending on its content.
	 * <p>
	 * The newly created Element has no parent (its parent is initialized to {@code null}).
	 * @param name the tag name
	 * @param innerText the textual content to be set in this element
	 */
	public Element(String name, String innerText) {
		super(ELEMENT, List.of(StartTag.of(name), Content.of(), EndTag.of(name)));
		this.name = name;
		_parent = null;
		Content content = (Content)_nodeList.get(1);
		content.setText(innerText);
	}

	/**
	 * Constructs a new Element node with the specified parent.
	 * <p>
	 * The content of this Element is not initialized at creation time;
	 * {@link Element#set} must be invoked later to populate the node list.
	 * @param name the tag name
	 * @param parent the parent Element
	 */
	public Element(String name, Element parent) {
		super(ELEMENT);
		this.name = name;
		_parent = parent;
	}

	/**
	 * Populates this Element with its actual terminal nodes.
	 * <p>
	 * This method must be invoked if the Element was created via
	 * {@link Element#Element(String, Element)},
	 * because that constructor does not initialize the node list.
	 * @param nodeList the list of nodes to be assigned to this Element
	 */
	public void set(List<Node> nodeList) {
		if (_nodeList.size() == 0 &&
			((nodeList.size() == 1 && nodeList.get(0).type == EETAG) ||
			 (nodeList.size() == 3 && nodeList.get(0).type == STAG && nodeList.get(1).type == CONTENT && nodeList.get(2).type == ETAG))) {
			_nodeList.addAll(nodeList);
		} else {
			throw new RuntimeException("Element::set: INCORRECT USE!");
		}
	}

	/**
	 * This method returns its parent element.
	 * @return parent element or null if it is not set.
	 */
	public Element parent() {
		if (_parent instanceof Element element) {
			return element;
		} else {
			return null;
		}
	}

	/**
	 * Sets the parent of this Element to the specified object.
	 * <p>
	 * If this Element is the root of the tree,
	 * a {@code Document} instance should be provided
	 * instead of an {@code Element}.
	 * @param parent the Element or Document to be assigned as the parent;
	 * {@code null} detaches this Element from its current parent
	 */
	public void setParent(Object parent) {
		_parent = parent;
	}

	/**
	 * Returns the Document to which this Element belongs.
	 * <p>
	 * A {@code Document} instance must be assigned as the parent of the
	 * root Element for this method to return a non-null value.
	 * @return the associated Document, or {@code null} if no Document has been set
	 */
	public Document document() {
		if (_parent instanceof Element parentElement) {
			return parentElement.document();
		} else if (_parent instanceof Document theDocument) {
			return theDocument;
		} else {
			return null;
		}
	}

	/**
	 * Returns the list of child nodes contained in this Element.
	 * @return the list of child nodes
	 */
	@Override
	public List<Node> nodeList() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.nodeList();
		} else {
			return List.of();
		}
	}

	/**
	 * Returns the number of the child nodes contained in this Element.
	 * @return the number of children
	 */
	@Override
	public int size() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.size();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the first child node in this Element.
	 * @return the first child node,
	 * or {@code NullNode} if this Element has no children
	 */
	@Override
	public Node first() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.first();
		} else {
			return NullNode;
		}
	}

	/**
	 * Returns the last child node in this Element.
	 * @return the last child node,
	 * or {@code NullNode} if this Element has no children
	 */
	@Override
	public Node last() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.last();
		} else {
			return NullNode;
		}
	}

	/**
	 * Returns the index of the last child node in this Element.
	 * @return the index of the last child node,
	 * or -1 if this Element has no children
	 */
	@Override
	public int lastIndex() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.lastIndex();
		} else {
			return -1;
		}
	}

	/**
	 * Returns the child node at the specified index in the content of this Element.
	 * @return the child node, or {@code NullNode} if this Element has no children
	 */
	@Override
	public Node get(int index) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.get(index);
		} else {
			return NullNode;
		}
	}

	@Override
	public void set(int index, Node node) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			content.set(index, node);
			if (node instanceof Element element) {
				element.setParent(this);
			}
		} else {
			throw new RuntimeException("Element::set: No content.");
		}
	}

	@Override
	public void add(Node node) {
		StartTag stag = (StartTag)_nodeList.get(0);
		if (stag.type == EETAG) {
			_nodeList.clear();
			_nodeList.add(stag.toStartTag());
			_nodeList.add(Content.of());
			_nodeList.add(EndTag.of(stag.name));
		}
		Content content = (Content)_nodeList.get(1);
		content.add(node);
		if (node instanceof Element element) {
			element.setParent(this);
		}
	}

	@Override
	public void add(int index, Node node) {
		StartTag stag = (StartTag)_nodeList.get(0);
		if (stag.type == EETAG) {
			_nodeList.clear();
			_nodeList.add(stag.toStartTag());
			_nodeList.add(Content.of());
			_nodeList.add(EndTag.of(name));
		}
		Content content = (Content)_nodeList.get(1);
		content.add(index, node);
		if (node instanceof Element element) {
			element.setParent(this);
		}
	}

	@Override
	public void removeAll() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			content.removeAll();
		}
	}

	@Override
	public Node remove(int index) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			Node removed = content.remove(index);
			if (removed.type != NULL) {
				return removed;
			}
		}
		return NullNode;
	}

	@Override
	public Node remove(Node node) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			Node removed = content.remove(node);
			if (removed.type != NULL) {
				return removed;
			}
		}
		return NullNode;
	}

	@Override
	public Node remove(Node node, int start, int end) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			Node removed = content.remove(node, start, end);
			if (removed.type != NULL) {
				return removed;
			}
		}
		return NullNode;
	}

	/**
	 * This method checks if this element is an empty element.
	 * @return True if this element is an empty element or false if it is not.
	 */
	public boolean isEmptyElement() {
		return _nodeList.size() == 1;
	}

	/**
	 * This method returns the start tag node of this element.
	 * @return Start tag node
	 */
	public StartTag startTag() {
		return (StartTag)_nodeList.get(0);
	}

	/**
	 * This method returns the end tag node of this element.
	 * @return End tag node
	 */
	public EndTag endTag() {
		return _nodeList.size() == 3 ? (EndTag)_nodeList.get(2) : null;
	}

	public int attributeCount() {
		return startTag().attributeCount();
	}

	public String attribute(int index) {
		return startTag().attribute(index);
	}

	public String attribute(int index, String defaultValue) {
		return startTag().attribute(index, defaultValue);
	}

	public String attribute(String key) {
		return startTag().attribute(key);
	}

	public String attribute(String key, String defaultValue) {
		return startTag().attribute(key, defaultValue);
	}

	public List<String> attributeKeys() {
		return startTag().attributeKeys();
	}

	public void setAttribute(int index, String key, String value) {
		startTag().setAttribute(index, key, value);
	}

	public void setAttribute(String key, String value) {
		startTag().setAttribute(key, value);
	}

	public void addAttribute(String key, String value) {
		startTag().addAttribute(key, value);
	}

	public void addAttribute(int index, String key, String value) {
		startTag().addAttribute(index, key, value);
	}

	public void removeAllAttributes() {
		startTag().removeAllAttributes();
	}

	public Node removeAttribute(int index) {
		return startTag().removeAttribute(index);
	}

	public Node removeAttribute(String key) {
		return startTag().removeAttribute(key);
	}

	/**
	 * This is equivalent to size method.
	 * @return number of nodes in the content
	 */
	public int childCount() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.size();
		} else {
			return 0;
		}
	}

	/**
	 * This is equivalent to nodeList method.
	 * @return List of nodes
	 */
	public List<Node> children() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.nodeList();
		} else {
			return List.of();
		}
	}

	/**
	 * This is equivalent to get method.
	 * @param index of the node to be returned
	 * @return node in the content
	 */
	public Node child(int index) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.get(index);
		} else {
			return NullNode;
		}
	}

	/**
	 * This method attempts to change itself to an empty element if possible.
	 * @return True if this element is an empty element as a result or false if not.
	 */
	public boolean empty() {
		if (isEmptyElement()) {
			return true;
		} else if (hasElement()) {
			return false;
		} else {
			Content content = (Content)_nodeList.get(1);
			String text = content.text();
			for (int i = 0; i < text.length(); i++) {
				int c = text.charAt(i);
				if (Lexer.isWhiteSpace(c)) {
					continue;
				} else {
					return false;
				}
			}
			StartTag stag = (StartTag)_nodeList.get(0);
			_nodeList.clear();
			_nodeList.add(stag.toEmptyElementTag());
			return true;
		}
	}

	/**
	 * This method returns the inner text string of this element.
	 * @return Inner text string of this element
	 */
	public String innerText() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.text();
		} else {
			return null;
		}
	}

	/**
	 * This method sets the given string to the inner text string of this element.
	 * @param value to be set
	 */
	public void setInnerText(String value) {
		if (_nodeList.size() == 1) {
			StartTag stag = startTag().toStartTag();
			_nodeList.clear();
			_nodeList.add(stag);
			_nodeList.add(Content.of());
			_nodeList.add(EndTag.of(name));
		}
		Content content = (Content)_nodeList.get(1);
		content.setText(value);
	}

	/**
	 * This method returns the nesting level of this element.
	 * For example, the nesting level of the root element is zero.
	 * The nesting level of the child elements of the root element is one.
	 * @return The nesting level of this element
	 */
	public int level() {
		int n = 0;
		Object parent = _parent;
		while (parent instanceof Element element) {
			n++;
			parent = element.parent();
		}
		return n;
	}

	/**
	 * This method checks if this element has one or more child elements.
	 * @return true if this element has one or more child elements or<br/>false if not
	 */
	public boolean hasElement() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.hasElement();
		} else {
			return false;
		}
	}

	/**
	 * This method locates Element instances that match the criteria specified by <i>name</i>.
	 * @param name the tag name pattern used to locate Element instances.
	 *             The pattern may include multiple tag names separated by slashes
	 *             to specify an Element hierarchy.
	 *             If <i>name</i> begins with a slash, the search is performed starting
	 *             from the direct children. Otherwise, the search begins from any
	 *             descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return List of Element instances
	 */
	public List<Element> getElements(String name) {
		if (name != null && name.length() > 0) {
			String[] names = name.split("/");
			boolean isRelative = names[0].length() > 0;
			return getElements(names, isRelative ? 0 : 1, isRelative);
		} else {
			return new ArrayList<>();
		}
	}

	public List<Element> getElements(String[] names, int index, boolean isRelative) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			List<Element> elementList = content.getElements(names[index]);
			if (index + 1 < names.length && elementList.size() > 0) {
				List<Element> elementList2 = new ArrayList<>();
				for (Element element : elementList) {
					elementList2.addAll(element.getElements(names, index + 1, false));
				}
				elementList = elementList2;
			}
			if (isRelative) {
				content.getElementsRecursively(names, index, elementList);
			}
			return elementList;
		} else {
			return new ArrayList<>();
		}
	}

	public Element getElement(String name) {
		List<Element> elementList = getElements(name);
		return elementList.size() > 0 ? elementList.get(0) : null;
	}

	public void indent() {
		Document document = document();
		if (document != null) {
			byte[] lineSeparator = document.lineSeparator();
			int indentation = document.indentation();
			int level = level();
			indent(lineSeparator, indentation, level);
		}
	}

	public void indent(byte[] lineSeparator, int indentation, int level) {
		if (!empty()) {
			Content content = (Content)_nodeList.get(1);
			content.indent(lineSeparator, indentation, level + 1);
		}
	}

}
