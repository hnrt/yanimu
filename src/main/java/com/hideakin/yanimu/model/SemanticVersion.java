package com.hideakin.yanimu.model;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

// cf. https://semver.org
public class SemanticVersion {

	public static SemanticVersion of(String text) {
		return new SemanticVersion(text != null ? text : "0.0.0");
	}

	private String _original;
	private boolean _valid;
	private int _major = -1;
	private int _minor = -1;
	private int _patch = -1;
	private List<Object> _preRelease = null;
	private String _build = null;

	private SemanticVersion(String text) {
		_original = text;
		_valid = text != null ? parse(text) : false;
	}

	@Override
	public String toString() {
		return _original;
	}

	public boolean isValid() {
		return _valid;
	}

	public int major() {
		return _major;
	}

	public int minor() {
		return _minor;
	}

	public int patch() {
		return _patch;
	}

	public List<Object> preRelease() {
		return _preRelease != null ? List.copyOf(_preRelease) : null;
	}

	public String build() {
		return _build;
	}

	public int compareTo(SemanticVersion other) {
		if (_valid && other._valid) {
			int d = _major - other._major;
			if (d != 0) {
				return d;
			}
			d = _minor - other._minor;
			if (d != 0) {
				return d;
			}
			d = _patch - other._patch;
			if (d != 0) {
				return d;
			}
			if (_preRelease == null) {
				return other._preRelease == null ? 0 : 1; 
			} else if (other._preRelease == null) {
				return -1;
			}
			for (int i = 0; ; i++) {
				if (i == _preRelease.size()) {
					return i == other._preRelease.size() ? 0 : -1;
				} else if (i == other._preRelease.size()) {
					return 1;
				}
				Object o1 = _preRelease.get(i);
				Object o2 = other._preRelease.get(i);
				if (o1 instanceof Long n1) {
					if (o2 instanceof Long n2) {
						long dd = n1.longValue() - n2.longValue();
						if (dd != 0) {
							return dd > 0 ? 1 : -1;
						}
					} else {
						return -1;
					}
				} else if (o2 instanceof Long) {
					return 1;
				} else {
					d = ((String)o1).compareTo((String)o2);
					if (d != 0) {
						return d;
					}
				}
			}
		} else if (_valid) {
			return 1;
		} else if (other._valid) {
			return -1;
		} else if (_original != null && other._original != null) {
			return _original.compareTo(other._original);
		} else if (_original != null) {
			return 1;
		} else if (other._original != null) {
			return -1;
		} else {
			return 0;
		}
	}

	private boolean parse(String s) {
		try {
			StringReader r = new StringReader(s);
			int c = r.read();
			if (c == '0') {
				_major = 0;
				c = r.read();
			} else if (isPositiveDigit(c)) {
				_major = c - '0';
				c = r.read();
				while (isDigit(c)) {
					_major = _major * 10 + c - '0';
					c = r.read();
				}
			} else {
				return false;
			}
			if (c == '.') {
				c = r.read();
			} else {
				return false;
			}
			if (c == '0') {
				_minor = 0;
				c = r.read();
			} else if (isPositiveDigit(c)) {
				_minor = c - '0';
				c = r.read();
				while (isDigit(c)) {
					_minor = _minor * 10 + c - '0';
					c = r.read();
				}
			} else {
				return false;
			}
			if (c == '.') {
				c = r.read();
			} else {
				return false;
			}
			if (c == '0') {
				_patch = 0;
				c = r.read();
			} else if (isPositiveDigit(c)) {
				_patch = c - '0';
				c = r.read();
				while (isDigit(c)) {
					_patch = _patch * 10 + c - '0';
					c = r.read();
				}
			} else {
				return false;
			}
			if (c == -1) {
				return true;
			} else if (c == '-') {
				_preRelease = new ArrayList<>();
				StringBuilder b = new StringBuilder();
				do {
					c = r.read();
					if (isNonDigit(c)) {
						b.append((char)c);
						c = r.read();
						while (isIdentifierCharacter(c)) {
							b.append((char)c);
							c = r.read();
						}
						_preRelease.add(b.toString());
					} else if (isIdentifierCharacter(c)) {
						b.append((char)c);
						c = r.read();
						while (isIdentifierCharacter(c)) {
							b.append((char)c);
							c = r.read();
						}
						if (isNonDigit(c)) {
							b.append((char)c);
							c = r.read();
							while (isIdentifierCharacter(c)) {
								b.append((char)c);
								c = r.read();
							}
							_preRelease.add(b.toString());
						} else {
							String x = b.toString();
							try {
								_preRelease.add(Long.valueOf(x));
							} catch (Exception e) {
								_preRelease.add(x);
							}
						}
					} else {
						return false;
					}
					b.setLength(0);
				} while (c == '.');
			} else {
				return false;
			}
			if (c == -1) {
				return true;
			} else if (c == '+') {
				StringBuilder b = new StringBuilder();
				do {
					c = r.read();
					if (isNonDigit(c)) {
						b.append((char)c);
						c = r.read();
						while (isIdentifierCharacter(c)) {
							b.append((char)c);
							c = r.read();
						}
					} else if (isIdentifierCharacter(c)) {
						b.append((char)c);
						c = r.read();
						while (isIdentifierCharacter(c)) {
							b.append((char)c);
							c = r.read();
						}
						if (isNonDigit(c)) {
							b.append((char)c);
							c = r.read();
							while (isIdentifierCharacter(c)) {
								b.append((char)c);
								c = r.read();
							}
						}
					} else {
						return false;
					}
				} while (c == '.');
				_build = b.toString();
			} else {
				return false;
			}
			if (c == -1) {
				return true;
			} else {		
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	private static boolean isIdentifierCharacter(int c) {
		return isDigit(c) || isNonDigit(c);
	}
	
	private static boolean isNonDigit(int c) {
		return isLetter(c) || c == '-';
	}

	private static boolean isDigit(int c) {
		return c == '0' || isPositiveDigit(c);
	}

	private static boolean isPositiveDigit(int c) {
		switch (c) {
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return true;
		default:
			return false;
		}
	}

	private static boolean isLetter(int c) {
		switch (c) {
		case 'A': case 'a':
		case 'B': case 'b':
		case 'C': case 'c':
		case 'D': case 'd':
		case 'E': case 'e':
		case 'F': case 'f':
		case 'G': case 'g':
		case 'H': case 'h':
		case 'I': case 'i':
		case 'J': case 'j':
		case 'K': case 'k':
		case 'L': case 'l':
		case 'M': case 'm':
		case 'N': case 'n':
		case 'O': case 'o':
		case 'P': case 'p':
		case 'Q': case 'q':
		case 'R': case 'r':
		case 'S': case 's':
		case 'T': case 't':
		case 'U': case 'u':
		case 'V': case 'v':
		case 'W': case 'w':
		case 'X': case 'x':
		case 'Y': case 'y':
		case 'Z': case 'z':
			return true;
		default:
			return false;
		}
	}

}
