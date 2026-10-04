package com.hideakin.yanimu.xml;

import java.util.ArrayList;
import java.util.List;

import static com.hideakin.yanimu.xml.Character.isWhiteSpace;

/**
 * An XML element object.
 */
public class Element extends NodeList {

	/**
	 * The tag name of this element.
	 */
	public final String name;

	/**
	 * The parent of this element.
	 */
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
		super(ELEMENT, List.of(StartTag.of(name), Content.of(innerText), EndTag.of(name)));
		this.name = name;
		_parent = null;
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
			throw new RuntimeException(getClass().getSimpleName() + "::set: INCORRECT USE!");
		}
	}

	/**
	 * Returns the parent Element.
	 * @return the parent Element or null if not set.
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
	 * Returns an unmodifiable List containing the child nodes in this Element, in its iteration order.
	 * @return an unmodifiable List of the child nodes
	 */
	@Override
	public List<Node> copy() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.copy();
		} else {
			return List.of();
		}
	}

	/**
	 * Returns the number of the child nodes in this Element.
	 * @return the number of the child nodes
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
	 * or {@code NULL_NODE} if this Element has no children
	 */
	@Override
	public Node first() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.first();
		} else {
			return NULL_NODE;
		}
	}

	/**
	 * Returns the last child node in this Element.
	 * @return the last child node,
	 * or {@code NULL_NODE} if this Element has no children
	 */
	@Override
	public Node last() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.last();
		} else {
			return NULL_NODE;
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
	 * Returns the child node at the specified index in this Element.
	 * @param index the index of the child node to return
	 * @return the child node
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than or equal to the size)
	 */
	@Override
	public Node get(int index) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.get(index);
		} else {
			throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::get: Index out of range.");
		}
	}

	/**
	 * Returns the child node at the specified index in this Element.
	 * <p>
	 * If the index is out of range (less than 0 or greater than or equal to the size),
	 * the specified fallback is returned.
	 * @param index the index of the child node to return
	 * @param fallback the value to return if the index is out of range
	 * @return the child node at the specified index, or {@code fallback} if the index is out of range
	 */
	@Override
	public Node get(int index, Node fallback) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.get(index, fallback);
		} else {
			return fallback;
		}
	}

	/**
	 * Replaces the child node at the specified index in this Element with the specified node.
	 * @param index the index at which the node is to be replaced
	 * @param node the node with which the child node is to be replaced
	 */
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

	/**
	 * Appends the specified node to the end of the child list in this Element.
	 * @param node the node to be appended to this Element
	 * @throws NullPointerException if null is specified for the node
	 * @throws IllegalArgumentException if a NULL-node is specified
	 */
	@Override
	public void add(Node node) {
		AttributeTag tag = (AttributeTag)_nodeList.get(0);
		if (tag.type == EETAG) {
			_nodeList.clear();
			_nodeList.add(tag.toStartTag());
			_nodeList.add(Content.of());
			_nodeList.add(EndTag.of(tag.name));
		}
		Content content = (Content)_nodeList.get(1);
		content.add(node);
		if (node instanceof Element element) {
			element.setParent(this);
		}
	}

	/**
	 * Inserts the specified node at the specified index in the child list in this Element.
	 * Shifts the node currently at that position (if any)
	 * and any subsequent nodes to the right (adds one to their indices).
	 * @param index the index at which the node is to be inserted
	 * @param node the node to be inserted
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than the size)
	 * @throws NullPointerException if null is specified for the node
	 * @throws IllegalArgumentException if a NULL-node is specified
	 */
	@Override
	public void add(int index, Node node) {
		AttributeTag tag = (AttributeTag)_nodeList.get(0);
		if (tag.type == EETAG) {
			_nodeList.clear();
			_nodeList.add(tag.toStartTag());
			_nodeList.add(Content.of());
			_nodeList.add(EndTag.of(name));
		}
		Content content = (Content)_nodeList.get(1);
		content.add(index, node);
		if (node instanceof Element element) {
			element.setParent(this);
		}
	}

	/**
	 * Removes all of the child nodes from this Element.
	 * There will be no children after this call returns.
	 */
	@Override
	public void clear() {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			content.clear();
		}
	}

	/**
	 * Removes the child node at the specified index in this Element.
	 * Shifts any subsequent nodes to the left (subtracts one from their indices).
	 * Returns the node that was removed from the child list.
	 * @param index the index of the child node to be removed
	 * @return the child node previously at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range (less than 0 or greater than or equal to the size)
	 */
	@Override
	public Node remove(int index) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.remove(index);
		} else {
			throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::remove: An empty element has no children.");
		}
	}

	/**
	 * Removes the first occurrence of the specified node from this Element, if it is present.
	 * Shifts any subsequent nodes to the left (subtracts one from their indices).
	 * Returns the node that was removed from the child list.
	 * <p>
	 * If the child list in this Element does not contain the node, it is unchanged and {@code NULL_NODE} is returned.
	 * @param node the node to be removed
	 * @return the node that was removed from the list, or {@code NULL_NODE} if not found
	 */
	@Override
	public Node remove(Node node) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			return content.remove(node);
		}
		return NULL_NODE;
	}

	/**
	 * Checks if this element is an empty element.
	 * @return true if this is it, or false if not.
	 */
	public boolean isEmptyElement() {
		return _nodeList.size() == 1;
	}

	/**
	 * Returns the start tag node in this element.
	 * @return Start tag node
	 */
	public AttributeTag startTag() {
		return (AttributeTag)_nodeList.get(0);
	}

	/**
	 * Returns the end tag node in this element.
	 * @return End tag node
	 */
	public EndTag endTag() {
		return _nodeList.size() == 3 ? (EndTag)_nodeList.get(2) : null;
	}

	/**
	 * Returns the number of attributes in this node.
	 * @return the number of attributes
	 */
	public int attributeCount() {
		return startTag().attributeCount();
	}

	/**
	 * Returns an attribute value at the specified index.
	 * @param index the index of the attribute to be retrieved
	 * @return an attribute value if it exists, or null otherwise
	 */
	public String attribute(int index) {
		return attribute(index, null);
	}

	/**
	 * Returns an attribute value at the specified index.
	 * @param index the index of the attribute to be retrieved
	 * @param fallback the value to be returned if the specified attribute doesn't exist
	 * @return an attribute value if it is found, or {@code fallback} otherwise
	 */
	public String attribute(int index, String fallback) {
		Attribute a = startTag().attribute(index);
		return a != null ? a.value : fallback;
	}

	/**
	 * Returns an attribute value associated with the specified name.
	 * @param name the name of the attribute to be retrieved
	 * @return the attribute value if it exists, or null otherwise
	 */
	public String attribute(String name) {
		return attribute(name, null);
	}

	/**
	 * Returns an attribute value associated with the specified name.
	 * @param name the name of the attribute to be retrieved
	 * @param fallback the value to be returned if the specified attribute doesn't exist
	 * @return an attribute value if it is found, or {@code fallback} otherwise
	 */
	public String attribute(String name, String fallback) {
		Attribute a = startTag().attribute(name);
		return a != null ? a.value : fallback;
	}

	/**
	 * Returns all the attributes.
	 * @return the immutable list of the attributes
	 */
	public List<Attribute> attributeList() {
		List<Attribute> aa = new ArrayList<>();
		AttributeTag tag = startTag();
		int size = tag.safeSize();
		for (int nodeIndex = AttributeTag.LOWER_BOUND; nodeIndex < size - 1; nodeIndex += AttributeTag.STEP) {
			Attribute a = (Attribute)tag.get(nodeIndex);
			aa.add(a);
		}
		return List.copyOf(aa);
	}

	/**
	 * Returns all the attribute names.
	 * @return the immutable list of the attribute names
	 */
	public List<String> attributeNames() {
		List<String> names = new ArrayList<>();
		AttributeTag tag = startTag();
		int size = tag.safeSize();
		for (int nodeIndex = AttributeTag.LOWER_BOUND; nodeIndex < size - 1; nodeIndex += AttributeTag.STEP) {
			Attribute a = (Attribute)tag.get(nodeIndex);
			names.add(a.name);
		}
		return List.copyOf(names);
	}

	/**
	 * Returns all the attribute values.
	 * @return the immutable list of the attribute values
	 */
	public List<String> attributeValues() {
		List<String> values = new ArrayList<>();
		AttributeTag tag = startTag();
		int size = tag.safeSize();
		for (int nodeIndex = AttributeTag.LOWER_BOUND; nodeIndex < size - 1; nodeIndex += AttributeTag.STEP) {
			Attribute a = (Attribute)tag.get(nodeIndex);
			values.add(a.value);
		}
		return List.copyOf(values);
	}

	/**
	 * Sets an attribute at the specified index in this start tag.
	 * @param index the index at which the attribute to be set
	 * @param name the name of the attribute
	 * @param value the raw attribute value, not enclosed in quotes
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void setAttribute(int index, String name, String value) {
		AttributeTag tag = startTag();
		int nodeIndex = tag.attributeIndex(index);
		if (nodeIndex >= 0) {
			Attribute a = (Attribute)tag.get(nodeIndex);
			tag.set(nodeIndex, new Attribute(name, value, a));
		} else {
			throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::setAttribute:: Index out of range.");
		}
	}

	/**
	 * Replaces the existing attribute associated with the specified name with a new attribute.<br/>
	 * If the specified name is not found, a new attribute is appended after the last existing attribute.<br/>
	 * The new attribute to be created is associated with the same name and contains the specified value.<br/>
	 * The {@code value} is treated as the raw attribute value and is not expected to include any quote characters. 
	 * @param name the name of the attribute
	 * @param value the raw attribute value, not enclosed in quotes
	 */
	public void setAttribute(String name, String value) {
		AttributeTag tag = startTag();
		int nodeIndex = tag.attributeIndex(name);
		if (nodeIndex >= 0) {
			Attribute a = (Attribute)tag.get(nodeIndex);
			tag.set(nodeIndex, new Attribute(name, value, a));
		} else {
			int upperBound = tag.upperBound();
			tag.add(upperBound, Node.of(S, SP_SEQUENCE));
			tag.add(upperBound + 1, new Attribute(name, value));
		}
	}

	/**
	 * Adds a new attribute node next to the last attribute in this start tag.
	 * @param name the name of the attribute
	 * @param value the value of the attribute, not enclosed by quote characters
	 */
	public void addAttribute(String name, String value) {
		AttributeTag tag = startTag();
		int upperBound = tag.upperBound();
		tag.add(upperBound, Node.of(S, SP_SEQUENCE));
		tag.add(upperBound + 1, new Attribute(name, value));
	}

	/**
	 * Adds a new attribute node next to the last attribute in this start tag.
	 * @param index the attribute index to which the new attribute node is to be inserted 
	 * @param name the name of the attribute
	 * @param value the value of the attribute, not enclosed by quote characters
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void addAttribute(int index, String name, String value) {
		AttributeTag tag = startTag();
		int count = tag.attributeCount();
		if (index == count) {
			int upperBound = tag.upperBound();
			tag.add(upperBound, Node.of(S, SP_SEQUENCE));
			tag.add(upperBound + 1, new Attribute(name, value));
		} else {
			int nodeIndex = tag.attributeIndex(index);
			if (nodeIndex >= 0) {
				tag.add(nodeIndex - 1, Node.of(S, SP_SEQUENCE));
				tag.add(nodeIndex, new Attribute(name, value));
			} else {
				throw new IndexOutOfBoundsException(getClass().getSimpleName() + "::addAttribute:: Index out of range.");
			}
		}
	}

	/**
	 * Removes all the attributes in this start tag.
	 */
	public void removeAllAttributes() {
		AttributeTag tag = startTag();
		int size = tag.safeSize();
		int upperBound = tag.upperBound();
		if (upperBound < size - 1) {
			Node node0 = tag.get(0);
			Node node1 = tag.get(1);
			Node node2 = tag.get(size - 2);
			Node node3 =  tag.get(size - 1);
			tag.clear();
			tag.add(node0);
			tag.add(node1);
			tag.add(node2);
			tag.add(node3);
		} else {
			Node node0 = tag.get(0);
			Node node1 = tag.get(1);
			Node node2 =  tag.get(size - 1);
			tag.clear();
			tag.add(node0);
			tag.add(node1);
			tag.add(node2);
		}
	}

	/**
	 * Removes the attribute at the specified index.
	 * @param index of the attribute to be removed
	 * @return the removed attribute node, or {@code NULL_NODE} if the specified index is invalid
	 */
	public Node removeAttribute(int index) {
		AttributeTag tag = startTag();
		int nodeIndex = tag.attributeIndex(index);
		if (nodeIndex >= 0) {
			tag.remove(nodeIndex - 1);
			return tag.remove(nodeIndex - 1);
		} else {
			return NULL_NODE;
		}
	}

	/**
	 * Removes the attribute associated with the specified name.
	 * @param name of the attribute to be removed
	 * @return the removed attribute node, or {@code NULL_NODE} if the specified index is invalid
	 */
	public Node removeAttribute(String name) {
		AttributeTag tag = startTag();
		int nodeIndex = tag.attributeIndex(name);
		if (nodeIndex >= 0) {
			tag.remove(nodeIndex - 1);
			return tag.remove(nodeIndex - 1);
		} else {
			return NULL_NODE;
		}
	}

	/**
	 * This is equivalent to {@link Element#size}.
	 */
	public int childCount() {
		return size();
	}

	/**
	 * This is equivalent to {@link Element#copy}.
	 */
	public List<Node> children() {
		return copy();
	}

	/**
	 * This is equivalent to {@link Element#get}.
	 */
	public Node child(int index) {
		return get(index);
	}

	/**
	 * Attempts to change this Element to an empty element if no children is contained.
	 * @return true if this Element is an empty element as a result or false if not.
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
				if (isWhiteSpace(c)) {
					continue;
				} else {
					return false;
				}
			}
			AttributeTag tag = (AttributeTag)_nodeList.get(0);
			_nodeList.clear();
			_nodeList.add(tag.toEmptyElementTag());
			return true;
		}
	}

	/**
	 * Returns the inner text String of this Element.
	 * @return the inner text string of this Element, or null if this Element is an empty element
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
	 * Returns the inner text String of this Element.
	 * @param fallback the return value on error
	 * @return the inner text String of this Element,
	 * or {@code fallback} if this Element is an empty element or the inner text is empty
	 */
	public String innerText(String fallback) {
		if (_nodeList.size() > 1) {
			Content content = (Content)_nodeList.get(1);
			String text = content.text();
			return text.length() > 0 ? text : fallback;
		} else {
			return fallback;
		}
	}

	/**
	 * Sets the given String to the inner text String of this Element.
	 * @param value to be set
	 */
	public void setInnerText(String value) {
		if (_nodeList.size() == 1) {
			AttributeTag tag = startTag().toStartTag();
			_nodeList.clear();
			_nodeList.add(tag);
			_nodeList.add(Content.of());
			_nodeList.add(EndTag.of(name));
		}
		Content content = (Content)_nodeList.get(1);
		content.setText(value);
	}

	/**
	 * Returns the nesting level of this Element.
	 * <p>
	 * For example, the nesting level of the root Element is 0.
	 * The nesting level of a root's child Element is 1.
	 * @return the nesting level of this Element
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
	 * Checks if this Element has one or more child elements.
	 * @return true if this Element has one or more child elements or false otherwise
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
	 * Locates Element nodes that match the criteria specified by {@code name}.
	 * If no Element nodes are located, an empty list is returned.
	 * @param name the tag name pattern used to locate Element nodes.
	 *             The pattern may include multiple tag names separated by slashes to specify an Element hierarchy.
	 *             If {@code name} begins with a slash, the search is performed starting from the direct children.
	 *             Otherwise, the search begins from any descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return List of Element nodes
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

	/**
	 * Locates Element nodes that match the criteria specified by {@code name}
	 * and returns the first located one if one or more nodes are found.
	 * If no Element nodes are found, null is returned.
	 * @param name the tag name pattern used to locate Element nodes.
	 *             The pattern may include multiple tag names separated by slashes to specify an Element hierarchy.
	 *             If {@code name} begins with a slash, the search is performed starting from the direct children.
	 *             Otherwise, the search begins from any descendant elements.
	 *             An asterisk acts as a wildcard that matches any tag name.
	 * @return the first located node, or null if not found
	 */
	public Element getElement(String name) {
		List<Element> elementList = getElements(name);
		return elementList.size() > 0 ? elementList.get(0) : null;
	}

}
