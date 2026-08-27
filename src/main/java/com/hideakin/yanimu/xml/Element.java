package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

public class Element extends NodeList {

	public final String name;
	protected Object _parent;

	public Element(String name) {
		super(ELEMENT, new EmptyElementTag(name));
		this.name = name;
		_parent = null;
	}

	public Element(String name, String innerText) {
		super(ELEMENT, List.of(new StartTag(name), new Content(), new EndTag(name)));
		this.name = name;
		_parent = null;
		content().setInnerText(innerText);
	}

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
			throw new RuntimeException("Element::set(nodeList): Incorrect use!");
		}
	}

	@Override
	public void clearSequence() {
		_sequence = null;
		if (_parent instanceof Node node) {
			node.clearSequence();
		}
	}

	public Element parent() {
		if (_parent instanceof Element element) {
			return element;
		} else {
			return null;
		}
	}

	public void setParent(Object parent) {
		_parent = parent;
		if (_parent instanceof Node node) {
			node.clearSequence();
		}
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

	@Override
	public void add(Node node) {
		throw new RuntimeException("Element::add: Not allowed!");
	}

	@Override
	public void add(int index, Node node) {
		throw new RuntimeException("Element::add: Not allowed!");
	}

	@Override
	public Node remove(int index) {
		throw new RuntimeException("Element::remove: Not allowed!");
	}

	@Override
	public Node remove(Node node) {
		throw new RuntimeException("Element::remove: Not allowed!");
	}

	@Override
	public Node remove(Node node, int start, int end) {
		throw new RuntimeException("Element::remove: Not allowed!");
	}

	public boolean isEmptyElement() {
		return _nodeList.size() == 1;
	}

	public StartTag startTag() {
		return (StartTag)_nodeList.get(0);
	}

	public Content content() {
		return _nodeList.size() == 3 ? (Content)_nodeList.get(1) : null;
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

	public int childCount() {
		return _nodeList.size() == 3 ? ((Content)_nodeList.get(1)).count() : 0;
	}

	public List<Node> children() {
		return _nodeList.size() == 3 ? ((Content)_nodeList.get(1)).nodeList() : List.of();
	}

	public Node child(int index) {
		if (_nodeList.size() == 3) {
			Content content = (Content)_nodeList.get(1);
			return content.get(index);
		}
		return NullNode;
	}

	public boolean empty() {
		if (isEmptyElement()) {
			return true;
		} else {
			int count = childCount();
			if (count > 1) {
				return false;
			} else if (count == 1) {
				Node node = content().get(0);
				if (node.type != CHAR_DATA) {
					return false;
				}
				byte[] sequence = node.sequence();
				int length = sequence.length;
				for (int i = 0; i < length; i++) {
					int c = sequence[i];
					if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
						continue;
					} else {
						return false;
					}
				}
			}
			StartTag eetag = startTag().clone(EETAG);
			_nodeList.clear();
			_nodeList.add(eetag);
			clearSequence();
			return true;
		}
	}

	public void addChild(Node node) {
		if (isEmptyElement()) {
			StartTag stag = startTag().clone(STAG);
			_nodeList.clear();
			_nodeList.add(stag);
			_nodeList.add(new Content());
			_nodeList.add(new EndTag(stag.name));
		}
		Content content = this.content();
		if (node instanceof Element element) {
			element.setParent(this);
			Document document = this.document();
			byte[] eol = document != null ? document.endOfLineSequence() : Document.LF_SEQUENCE;
			int indentation = document != null ? document.indentation() : Document.INDENTATION_DEFAULT;
			int level = this.level();
			if (content.count() == 0) {
				content.add(Node.endOfLineAndIndentation(eol, indentation, level + 1));
				content.add(node);
				content.add(Node.endOfLineAndIndentation(eol, indentation, level));
			} else if (content.get(-1).isEndOfLineAndIndentation()) {
				content.add(-1, Node.endOfLineAndIndentation(eol, indentation, level + 1));
				content.add(-1, node);
			} else {
				content.add(node);
			}
		} else {
			content.add(node);
		}
		clearSequence();
	}

	public void addChild(int index, Node node) {
		if (isEmptyElement()) {
			StartTag stag = startTag().clone(STAG);
			_nodeList.clear();
			_nodeList.add(stag);
			_nodeList.add(new Content());
			_nodeList.add(new EndTag(name));
		}
		if (node instanceof Element element) {
			element.setParent(this);
		}
		content().add(index, node);
		clearSequence();
	}

	public void removeAllChildren() {
		Content content = this.content();
		if (content != null) {
			Node node;
			for (int i = 0; (node = content.get(i)) != null; i++) {
				if (node instanceof Element element) {
					element.setParent(null);
				}
			}
			content.removeAll();
			clearSequence();
		}
	}

	public Node removeChild(int index) {
		Content content = this.content();
		if (content != null) {
			Node node = content.remove(index);
			if (node.type != NULL) {
				clearSequence();
				return node;
			}
		}
		return null;
	}

	public Node removeChild(Node node) {
		Content content = this.content();
		if (content != null) {
			node = content.remove(node);
			if (node.type != NULL) {
				clearSequence();
				return node;
			}
		}
		return null;
	}

	public String innerText() {
		return isEmptyElement() ? null : content().innerText();
	}

	public void setInnerText(String value) {
		if (isEmptyElement()) {
			StartTag stag = startTag().clone(STAG);
			_nodeList.clear();
			_nodeList.add(stag);
			_nodeList.add(new Content());
			_nodeList.add(new EndTag(name));
		}
		content().setInnerText(value);
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
		if (isEmptyElement()) {
			return false;
		} else {
			return content().hasElement();
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
		if (isEmptyElement()) {
			return new ArrayList<>();
		} else {
			List<Element> elementList = content().getElements(names[index]);
			if (index + 1 < names.length && elementList.size() > 0) {
				List<Element> elementList2 = new ArrayList<>();
				for (Element element : elementList) {
					elementList2.addAll(element.getElements(names, index + 1, false));
				}
				elementList = elementList2;
			}
			if (isRelative) {
				content().getElementsRecursively(names, index, elementList);
			}
			return elementList;
		}
	}

	public void indent(byte[] eol, int indentation, int level) {
		if (!empty()) {
			content().indent(eol, indentation, level + 1);
			clearSequence();
		}
	}

}
