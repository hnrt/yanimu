package com.hideakin.yanimu.xml.internal;

import java.util.ArrayList;
import java.util.List;

public class TextMessageManager {

	private final List<String> _warnings = new ArrayList<>();
	private final List<String> _information = new ArrayList<>();

	public TextMessageManager() {
	}

	public String[] warnings() {
		return _warnings.toArray(new String[_warnings.size()]);
	}

	public String[] information() {
		return _information.toArray(new String[_information.size()]);
	}

	public void addWarning(String format, Object...args) {
		String message = String.format(format, args);
		if (!_warnings.contains(message)) {
			_warnings.add(message);
		}
	}

	public void addInformation(String format, Object...args) {
		String message = String.format(format, args);
		if (!_information.contains(message)) {
			_information.add(message);
		}
	}

}
