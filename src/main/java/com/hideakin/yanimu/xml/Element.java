package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

import com.hideakin.yanimu.xml.internal.Lexer;

public class Element extends NodeList {

	public final String name;
	protected Object _parent;

	public Element(String name) {
		super(ELEMENT, EmptyElementTag.of(name));
		this.name = name;
		_parent = null;
	}

	public Element(String name, String innerText) {
		super(ELEMENT, List.of(StartTag.of(name), Content.of(), EndTag.of(name)));
		this.name = name;
		_parent = null;
		Content content = (Content)_nodeList.get(1);
		content.setText(innerText);
	}

	// Note that set must be called later to fill _nodeList with the real nodes.
	public Element(String name, Element parent) {
		super(ELEMENT);
		this.name = name;
		_parent = parent;
	}

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

	public void setParent(Object parent) {
		_parent = parent;
	}

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
	 * This method returns the child nodes in this element.
	 * @return List of the child nodes
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
	 * This method returns the number of the child nodes in this element.
	 * @return Number of the child nodes
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
	 * This method returns the first child node in this element.
	 * @return The first child node if it exists or NullNode if this element has no child nodes
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
	 * This method returns the last child node in this element.
	 * @return The last child node if it exists or NullNode if this element has no child nodes
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
	 * This method returns the index number of the last child node in this element.
	 * @return The index number of the last child node if it exist, or -1 if this element has no child nodes
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

	public StartTag startTag() {
		return (StartTag)_nodeList.get(0);
	}

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

	/***
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

	public String innerText() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.text();
		} else {
			return null;
		}
	}

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

	public int level() {
		int n = 0;
		Object parent = _parent;
		while (parent instanceof Element element) {
			n++;
			parent = element.parent();
		}
		return n;
	}

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
		Document d = document();
		if (d != null) {
			byte[] eol = d.lineSeparator();
			int i = d.indentation();
			int l = level();
			indent(eol, i, l);
		}
	}

	public void indent(byte[] eol, int indentation, int level) {
		if (!empty()) {
			Content content = (Content)_nodeList.get(1);
			content.indent(eol, indentation, level + 1);
		}
	}

}
