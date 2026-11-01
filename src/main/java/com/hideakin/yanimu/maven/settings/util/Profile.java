package com.hideakin.yanimu.maven.settings.util;

import com.hideakin.yanimu.maven.util.PropertyMap;
import com.hideakin.yanimu.maven.util.RepositoryMap;
import com.hideakin.yanimu.xml.Element;
import com.hideakin.yanimu.xml.Node;

public class Profile extends SimpleSetting {

	private static final long serialVersionUID = -6197013045378235602L;

	public static Profile of() {
		return new Profile();
	}

	public static Profile of(Element element) {
		return new Profile(element);
	}

	private Element _activation;
	private final PropertyMap _properties;
	private final RepositoryMap _repositories;
	private final RepositoryMap _pluginRepositories;

	public Profile() {
		super("profile");
		_properties = new PropertyMap();
		_repositories = new RepositoryMap();
		_pluginRepositories = new RepositoryMap();
	}

	public Profile(Element element) {
		super(element);
		_properties = new PropertyMap();
		_repositories = new RepositoryMap();
		_pluginRepositories = new RepositoryMap();
		initialize();
	}

	private void initialize() {
		Element id = element.getElement("id");
		if (id != null) {
			super.put("id", id);
		}
		_activation = element.getElement("activation");
		_properties.load(element.getElement("/properties"));
		_repositories.load(element.getElement("/repositories"), element.getElements("/repositories/repository"));
		_pluginRepositories.load(element.getElement("/pluginRepositories"), element.getElements("/pluginRepositories/pluginRepository"));
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
			for (int i = 0; i < element.size(); i++) {
				Node node = element.get(i);
				if (node.type == Node.ELEMENT && "activation".equals(((Element)node).name)) {
					if (activation != null) {
						_activation = activation.element;
						element.set(i, activation.element);
					} else {
						_activation = null;
						element.remove(i);
					}
					break;
				}
			}
		} else if (activation != null) {
			_activation = activation.element;
			element.add(activation.element);
		}
	}

	public PropertyMap properties() {
		return _properties;
	}

	public String property(String key) {
		return _properties.get(key);
	}

	public void setProperty(String key, String value) {
		_properties.put(key, value);
	}

	public String translate(String text) {
		return _properties.translate(text);
	}
	
	public RepositoryMap repositories() {
		return _repositories;
	}
	
	public RepositoryMap pluginRepositories() {
		return _pluginRepositories;
	}

}
