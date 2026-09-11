package com.hideakin.yanimu.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.hideakin.yanimu.util.TestHelper.*;

public class NodeTest {

	@BeforeAll
	static void initAll() {
		start(NodeTest.class);
	}

	@AfterAll
	static void tearDownAll() {
		finish(NodeTest.class);
	}

	@BeforeEach
    void beforeEach(TestInfo info) {
		start(info);
    }

	@AfterEach
	void afterEach(TestInfo info) {
		finish(info);
	}

	@Test
	void test001() {
		Node node = Node.of(Node.CHAR_DATA, "abc".getBytes());
		assertEquals(Node.CHAR_DATA, node.type);
		assertEquals(true, node instanceof TerminalNode);
		assertEquals("abc", new String(node.sequence()));
	}

	@Test
	void test002() {
		Node node = Node.of(Node.S, " ".getBytes());
		assertEquals(Node.S, node.type);
		assertEquals(true, node instanceof TerminalNode);
		assertEquals(" ", new String(node.sequence()));
	}

	@Test
	void test003() {
		Node node = Node.of(Node.ENTITY_VALUE, "'abc'".getBytes());
		assertEquals(Node.ENTITY_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("abc", ((QuotedString)node).innerText());
	}

	@Test
	void test004() {
		Node node = Node.of(Node.ENTITY_VALUE, "\"abc\"".getBytes());
		assertEquals(Node.ENTITY_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("abc", ((QuotedString)node).innerText());
	}

	@Test
	void test005() {
		Node node = Node.of(Node.ATT_VALUE, "'日本語'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("日本語", ((QuotedString)node).innerText());
	}

	@Test
	void test006() {
		Node node = Node.of(Node.SYSTEM_LITERAL, "\"日本語\"".getBytes());
		assertEquals(Node.SYSTEM_LITERAL, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("日本語", ((QuotedString)node).innerText());
	}

	@Test
	void test007() {
		Node node = Node.of(Node.PUBID_LITERAL, "'abc'".getBytes());
		assertEquals(Node.PUBID_LITERAL, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("abc", ((QuotedString)node).innerText());
	}

	@Test
	void test008() {
		Node node = Node.of(Node.COMMENT, (Comment.START + "hello" + Comment.END).getBytes());
		assertEquals(Node.COMMENT, node.type);
		assertEquals(true, node instanceof Comment);
		assertEquals("hello", ((Comment)node).innerText());
		assertEquals("<!--hello-->", new String(node.sequence()));
	}

	@Test
	void test009() {
		Node node = Node.of(Node.CD_SECT, (CDATASection.START + "hello" + CDATASection.END).getBytes());
		assertEquals(Node.CD_SECT, node.type);
		assertEquals(true, node instanceof CDATASection);
		assertEquals("hello", ((CDATASection)node).innerText());
		assertEquals("<![CDATA[hello]]>", new String(node.sequence()));
	}

	@Test
	void test010() {
		Node node = Node.of(Node.CHAR_REF, (CharRef.START + "65" + CharRef.END).getBytes());
		assertEquals(Node.CHAR_REF, node.type);
		assertEquals(true, node instanceof CharRef);
		assertEquals(65, ((CharRef)node).codepoint);
		assertEquals("&#65;", new String(node.sequence()));
	}

	@Test
	void test011() {
		Node node = Node.of(Node.ENTITY_REF, (EntityRef.START + "amp" + EntityRef.END).getBytes());
		assertEquals(Node.ENTITY_REF, node.type);
		assertEquals(true, node instanceof EntityRef);
		assertEquals("amp", ((EntityRef)node).name);
		assertEquals("&amp;", new String(node.sequence()));
	}

	@Test
	void test012() {
		Node node = Node.of(Node.PEREFERENCE, (ParameterEntityReference.START + "bogus" + ParameterEntityReference.END).getBytes());
		assertEquals(Node.PEREFERENCE, node.type);
		assertEquals(true, node instanceof ParameterEntityReference);
		assertEquals("bogus", ((ParameterEntityReference)node).name);
		assertEquals("%bogus;", new String(node.sequence()));
	}

	@Test
	void test101() {
		Node node = Node.lineSeparatorAndIndentation(Document.LF_SEQUENCE, 2, 3);
		assertEquals(Node.CHAR_DATA, node.type);
		assertEquals(true, node instanceof TerminalNode);
		assertEquals("\n      ", node.toString());
	}

	@Test
	void test102() {
		Node node = Node.lineSeparatorAndIndentation(Node.S, Document.CRLF_SEQUENCE, 3, 3);
		assertEquals(Node.S, node.type);
		assertEquals(true, node instanceof TerminalNode);
		assertEquals("\r\n         ", node.toString());
	}

	@Test
	void test201() {
		Node node1 = Node.of(Node.CHAR_DATA, "xyz".getBytes());
		assertEquals(0, node1.offset(node1));
	}

	@Test
	void test202() {
		Node node1 = Node.of(Node.CHAR_DATA, "xyz".getBytes());
		Node node2 = Node.of(Node.CHAR_DATA, "xyz".getBytes());
		assertEquals(-1, node1.offset(node2));
	}

	@Test
	void test301() {
		Node node = Node.of(Node.CHAR_DATA, "".getBytes());
		assertEquals(0, node.lineCount());
	}

	@Test
	void test302() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox jumps over the lazy dog.".getBytes());
		assertEquals(0, node.lineCount());
	}

	@Test
	void test303() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox\njumps over the lazy dog.".getBytes());
		assertEquals(1, node.lineCount());
	}

	@Test
	void test304() {
		Node node = Node.of(Node.CHAR_DATA, "\nThe quick brown fox\njumps over the lazy dog.\n".getBytes());
		assertEquals(3, node.lineCount());
	}

	@Test
	void test311() {
		Node node = Node.of(Node.CHAR_DATA, "".getBytes());
		assertEquals(0, node.lineCount(10));
	}

	@Test
	void test312() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox jumps over the lazy dog.".getBytes());
		assertEquals(0, node.lineCount(10));
	}

	@Test
	void test313() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox\njumps over the lazy dog.".getBytes());
		assertEquals(0, node.lineCount(10));
	}

	@Test
	void test314() {
		Node node = Node.of(Node.CHAR_DATA, "\nThe quick brown fox\njumps over the lazy dog.\n".getBytes());
		assertEquals(2, node.lineCount(30));
	}

	@Test
	void test401() {
		Node node = Node.of(Node.CHAR_DATA, "".getBytes());
		assertEquals(100, node.columnCount(100));
	}

	@Test
	void test402() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox jumps over the lazy dog.".getBytes());
		assertEquals(244, node.columnCount(200));
	}

	@Test
	void test403() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox\r\njumps over the lazy dog.".getBytes());
		assertEquals(24, node.columnCount(300));
	}

	@Test
	void test404() {
		Node node = Node.of(Node.CHAR_DATA, "\nThe quick brown fox\njumps over the lazy dog.\n".getBytes());
		assertEquals(0, node.columnCount(400));
	}

	@Test
	void test411() {
		Node node = Node.of(Node.CHAR_DATA, "".getBytes());
		assertEquals(100, node.columnCount(100, 10));
	}

	@Test
	void test412() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox jumps over the lazy dog.".getBytes());
		assertEquals(210, node.columnCount(200, 10));
	}

	@Test
	void test413() {
		Node node = Node.of(Node.CHAR_DATA, "The quick brown fox\r\njumps over the lazy dog.".getBytes());
		assertEquals(9, node.columnCount(300, 30));
	}

	@Test
	void test414() {
		Node node = Node.of(Node.CHAR_DATA, "\nThe quick brown fox\njumps over the lazy dog.\n".getBytes());
		assertEquals(9, node.columnCount(400, 30));
	}

	@Test
	void test501() {
		Node node = Node.of(Node.S, " ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test502() {
		Node node = Node.of(Node.S, "  \n".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test503() {
		Node node = Node.of(Node.S, "  \r\n  ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test504() {
		Node node = Node.of(Node.S, "\n".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test505() {
		Node node = Node.of(Node.S, "\r\n".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test506() {
		Node node = Node.of(Node.S, "\n  ".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test507() {
		Node node = Node.of(Node.S, "\r\n     ".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}
	
	@Test
	void test508() {
		Node node = Node.of(Node.S, "\n\n".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test509() {
		Node node = Node.of(Node.S, "\r\n\r\n  ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test510() {
		Node node = Node.of(Node.S, "\n  \n".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test521() {
		Node node = Node.of(Node.CHAR_DATA, " ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test522() {
		Node node = Node.of(Node.CHAR_DATA, "  \n".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test523() {
		Node node = Node.of(Node.CHAR_DATA, "  \r\n  ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test524() {
		Node node = Node.of(Node.CHAR_DATA, "\n".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test525() {
		Node node = Node.of(Node.CHAR_DATA, "\r\n".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test526() {
		Node node = Node.of(Node.CHAR_DATA, "\n  ".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test527() {
		Node node = Node.of(Node.CHAR_DATA, "\r\n     ".getBytes());
		assertEquals(true, node.isLineSeparatorAndIndentation());
	}
	
	@Test
	void test528() {
		Node node = Node.of(Node.CHAR_DATA, "\n\n".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test529() {
		Node node = Node.of(Node.CHAR_DATA, "\r\n\r\n  ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test530() {
		Node node = Node.of(Node.CHAR_DATA, "\n  \n".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test531() {
		Node node = Node.of(Node.CHAR_DATA, "\r\n     XYZ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test532() {
		Node node = Node.of(Node.CHAR_DATA, "XYZ".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test541() {
		Node node = Node.of(Node.CHAR_REF, "&#10;".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

	@Test
	void test542() {
		Node node = Node.of(Node.CHAR_REF, "&#xA;".getBytes());
		assertEquals(false, node.isLineSeparatorAndIndentation());
	}

}
