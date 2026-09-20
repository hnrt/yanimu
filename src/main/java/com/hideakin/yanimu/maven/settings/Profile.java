package com.hideakin.yanimu.maven.settings;

import java.util.List;

import com.hideakin.yanimu.maven.Property;
import com.hideakin.yanimu.maven.PropertyManager;
import com.hideakin.yanimu.maven.RepositoryCollection;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;

public class Profile extends SimpleSetting {

	private static final long serialVersionUID = -5214590429768494027L;

	public static Profile of() {
		return new Profile();
	}

	public static Profile of(Element element) {
		return new Profile(element);
	}

	private Element _activation;
	private final PropertyManager _propertyManager = new PropertyManager();
	private final RepositoryCollection _repositories = new RepositoryCollection();
	private final RepositoryCollection _pluginRepositories = new RepositoryCollection();

	public Profile() {
		super("profile");
	}

	public Profile(Element element) {
		super(element);
	}

	@Override
	protected void initialize() {
		Element id = _element.getElement("id");
		if (id != null) {
			super.put("id", id);
		}
		_activation = _element.getElement("activation");
		_propertyManager.load(_element);
		_repositories.load(_element.getElement("/repositories"), _element.getElements("/repositories/repository"));
		_pluginRepositories.load(_element.getElement("/pluginRepositories"), _element.getElements("/pluginRepositories/pluginRepository"));
	}

	public Activation activation() {
		if (_activation != null) {
			return new Activation(_activation);
		} else {
			return null;
		}
	}

	public void setActivation(Activation activation) {
		if (_activation != null) {
			for (int i = 0; i < _element.size(); i++) {
				Node node = _element.get(i);
				if (node.type == Node.ELEMENT && "activation".equals(((Element)node).name)) {
					if (activation != null) {
						_activation = activation.element();
						_element.set(i, activation.element());
					} else {
						_activation = null;
						_element.remove(i);
					}
					break;
				}
			}
		} else if (activation != null) {
			_activation = activation.element();
			_element.add(activation.element());
		}
	}

	public List<Property> properties() {
		return _propertyManager.getList();
	}

	public String property(String key) {
		return _propertyManager.get(key);
	}

	public void setProperty(String key, String value) {
		_propertyManager.put(key, value);
	}

	public String translate(String text) {
		return _propertyManager.translate(text);
	}
	
	public RepositoryCollection repositories() {
		return _repositories;
	}
	
	public RepositoryCollection pluginRepositories() {
		return _pluginRepositories;
	}

}
