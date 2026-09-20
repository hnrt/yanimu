package com.hideakin.yanimu.xml;

public class Character {

	public static final int EOF = -1;
	public static final int NULL = 0;
	public static final int HT = 9; // HORIZONTAL TABULATION
	public static final int LF = 10; // LINE FEED
	public static final int CR = 13; // CARRIAGE RETURN
	public static final int SP = 32; // SPACE (private to avoid incorrect use)
	public static final int EQ = 61;
	public static final int TAG_END = 62;

	public static final int MIN_CODE_POINT = java.lang.Character.MIN_CODE_POINT;
	public static final int MAX_CODE_POINT = java.lang.Character.MAX_CODE_POINT;
	public static final int MIN_HIGH_SURROGATE = java.lang.Character.MIN_HIGH_SURROGATE;
	public static final int MAX_HIGH_SURROGATE = java.lang.Character.MAX_HIGH_SURROGATE;
	public static final int MIN_LOW_SURROGATE = java.lang.Character.MIN_LOW_SURROGATE;
	public static final int MAX_LOW_SURROGATE = java.lang.Character.MAX_LOW_SURROGATE;

	public static boolean isWhiteSpace(int c) {
		return c == SP || c == HT || c == LF || c == CR;
	}

	public static boolean isNameStartChar(int c) {
		return isNameStartCharL(c) || isNameStartCharH(c);
	}

	private static boolean isNameStartCharL(int c) {
		return isAlphabetic(c)
				|| c == ':'
				|| c == '_';
	}

	private static boolean isNameStartCharH(int c) {
		return (0xC0 <= c && c <= 0xD6)
				|| (0xD8 <= c && c <= 0xF6)
				|| (0xF8 <= c && c <= 0x2FF)
				|| (0x370 <= c && c <= 0x37D)
				|| (0x37F <= c && c <= 0x1FFF)
				|| (0x200C <= c && c <= 0x200D)
				|| (0x2070 <= c && c <= 0x218F)
				|| (0x2C00 <= c && c <= 0x2FEF)
				|| (0x3001 <= c && c <= 0xD7FF)
				|| (0xF900 <= c && c <= 0xFDCF)
				|| (0xFDF0 <= c && c <= 0xFFFD)
				|| (0x10000 <= c && c <= 0xEFFFF);
	}

	public static boolean isNameChar(int c) {
		return isNameStartCharL(c)
				|| isDigit(c)
				|| c == '-'
				|| c == '.'
				|| c == 0xB7
				|| (0x0300 <= c && c <= 0x036F)
				|| (0x203F <= c && c <= 0x2040)
				|| isNameStartCharH(c);
	}

	public static boolean isChar(int c) {
		return c == HT
				|| c == LF
				|| c == CR
				|| (0x20 <= c && c <= 0xD7FF)
				|| (0xE000 <= c && c <= 0xFFFD)
				|| (0x10000 <= c && c <= 0x10FFFF);
	}

	public static boolean isPubidChar(int c) {
		switch (c) {
		case SP:
		case CR:
		case LF:
		case '-':
		case '\'':
		case '(':
		case ')':
		case '+':
		case ',':
		case '.':
		case '/':
		case ':':
		case '=':
		case '?':
		case ';':
		case '!':
		case '*':
		case '#':
		case '@':
		case '$':
		case '_':
		case '%':
			return true;
		default:
			return isAlphabetic(c) || isDigit(c);
		}
	}

	public static boolean isAlphabetic(int c) {
		return isAlphabeticUppercase(c) || isAlphabeticLowercase(c);
	}

	public static boolean isAlphabeticUppercase(int c) {
		switch (c) {
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
			return true;
		default:
			return false;
		}
	}

	public static boolean isAlphabeticLowercase(int c) {
		switch (c) {
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return true;
		default:
			return false;
		}
	}

	public static boolean isDigit(int c) {
		switch (c) {
		case '0':
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

	public static boolean isHexadecimal(int c) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
			return true;
		default:
			return false;
		}
	}
	
}
