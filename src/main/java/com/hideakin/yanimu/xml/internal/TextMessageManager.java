package com.hideakin.yanimu.xml.internal;

import java.util.ArrayList;
import java.util.List;

public class TextMessageManager {

	private final List<String> _warnings = new ArrayList<>();

	public TextMessageManager() {
	}

	public String[] warnings() {
		return _warnings.toArray(new String[_warnings.size()]);
	}

	public void addWarning(String format, Object...args) {
		String message = String.format(format, args);
		if (!_warnings.contains(message)) {
			_warnings.add(message);
		}
	}

}
