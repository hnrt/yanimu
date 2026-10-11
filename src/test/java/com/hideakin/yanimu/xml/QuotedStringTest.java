package com.hideakin.yanimu.xml;

import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.start;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class QuotedStringTest {

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
	void test001b() {
		Node node = Node.of(Node.ATT_VALUE, "".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\"", node.toString());
		assertEquals("", ((QuotedString)node).innerText());
	}

	@Test
	void test001s() {
		Node node = Node.of(Node.ATT_VALUE, "");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\"", node.toString());
		assertEquals("", ((QuotedString)node).innerText());
	}

	@Test
	void test002b() {
		Node node = Node.of(Node.ATT_VALUE, "\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'\"\'", node.toString());
		assertEquals("\"", ((QuotedString)node).innerText());
	}

	@Test
	void test002s() {
		Node node = Node.of(Node.ATT_VALUE, "\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'\"\'", node.toString());
		assertEquals("\"", ((QuotedString)node).innerText());
	}

	@Test
	void test003b() {
		Node node = Node.of(Node.ATT_VALUE, "\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\'\"", node.toString());
		assertEquals("\'", ((QuotedString)node).innerText());
	}

	@Test
	void test003s() {
		Node node = Node.of(Node.ATT_VALUE, "\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\'\"", node.toString());
		assertEquals("\'", ((QuotedString)node).innerText());
	}

	@Test
	void test004b() {
		Node node = Node.of(Node.ATT_VALUE, "A".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\"", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test004s() {
		Node node = Node.of(Node.ATT_VALUE, "A");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\"", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test005b() {
		Node node = Node.of(Node.ATT_VALUE, "A".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\"", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test005s() {
		Node node = Node.of(Node.ATT_VALUE, "A");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\"", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test006b() {
		Node node = Node.of(Node.ATT_VALUE, "\"A".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'\"A\'", node.toString());
		assertEquals("\"A", ((QuotedString)node).innerText());
	}

	@Test
	void test006s() {
		Node node = Node.of(Node.ATT_VALUE, "\"A");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'\"A\'", node.toString());
		assertEquals("\"A", ((QuotedString)node).innerText());
	}

	@Test
	void test007b() {
		Node node = Node.of(Node.ATT_VALUE, "A\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'A\"\'", node.toString());
		assertEquals("A\"", ((QuotedString)node).innerText());
	}

	@Test
	void test007s() {
		Node node = Node.of(Node.ATT_VALUE, "A\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'A\"\'", node.toString());
		assertEquals("A\"", ((QuotedString)node).innerText());
	}

	@Test
	void test008b() {
		Node node = Node.of(Node.ATT_VALUE, "\'A".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\'A\"", node.toString());
		assertEquals("\'A", ((QuotedString)node).innerText());
	}

	@Test
	void test008s() {
		Node node = Node.of(Node.ATT_VALUE, "\'A");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\'A\"", node.toString());
		assertEquals("\'A", ((QuotedString)node).innerText());
	}

	@Test
	void test009b() {
		Node node = Node.of(Node.ATT_VALUE, "A\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\'\"", node.toString());
		assertEquals("A\'", ((QuotedString)node).innerText());
	}

	@Test
	void test009s() {
		Node node = Node.of(Node.ATT_VALUE, "A\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\'\"", node.toString());
		assertEquals("A\'", ((QuotedString)node).innerText());
	}

	@Test
	void test010b() {
		Node node = Node.of(Node.ATT_VALUE, "\"A\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\"", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test010s() {
		Node node = Node.of(Node.ATT_VALUE, "\"A\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"A\"", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test011b() {
		Node node = Node.of(Node.ATT_VALUE, "\'A\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'A\'", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test011s() {
		Node node = Node.of(Node.ATT_VALUE, "\'A\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'A\'", node.toString());
		assertEquals("A", ((QuotedString)node).innerText());
	}

	@Test
	void test012b() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABCDEFG\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABCDEFG\"", node.toString());
		assertEquals("ABCDEFG", ((QuotedString)node).innerText());
	}

	@Test
	void test012s() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABCDEFG\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABCDEFG\"", node.toString());
		assertEquals("ABCDEFG", ((QuotedString)node).innerText());
	}

	@Test
	void test013b() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABCDEFG\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABCDEFG\'", node.toString());
		assertEquals("ABCDEFG", ((QuotedString)node).innerText());
	}

	@Test
	void test013s() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABCDEFG\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABCDEFG\'", node.toString());
		assertEquals("ABCDEFG", ((QuotedString)node).innerText());
	}

	@Test
	void test014b() {
		Node node = Node.of(Node.ATT_VALUE, "\"A\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"&quot;A\'\"", node.toString());
		assertEquals("&quot;A\'", ((QuotedString)node).innerText());
	}

	@Test
	void test014s() {
		Node node = Node.of(Node.ATT_VALUE, "\"A\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"&quot;A\'\"", node.toString());
		assertEquals("&quot;A\'", ((QuotedString)node).innerText());
	}

	@Test
	void test015b() {
		Node node = Node.of(Node.ATT_VALUE, "\'A\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\'A&quot;\"", node.toString());
		assertEquals("\'A&quot;", ((QuotedString)node).innerText());
	}

	@Test
	void test015s() {
		Node node = Node.of(Node.ATT_VALUE, "\'A\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"\'A&quot;\"", node.toString());
		assertEquals("\'A&quot;", ((QuotedString)node).innerText());
	}

	@Test
	void test016b() {
		Node node = Node.of(Node.ATT_VALUE, "\"\"A\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'\"\"A&apos;\'", node.toString());
		assertEquals("\"\"A&apos;", ((QuotedString)node).innerText());
	}

	@Test
	void test016s() {
		Node node = Node.of(Node.ATT_VALUE, "\"\"A\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'\"\"A&apos;\'", node.toString());
		assertEquals("\"\"A&apos;", ((QuotedString)node).innerText());
	}

	@Test
	void test017b() {
		Node node = Node.of(Node.ATT_VALUE, "ABC\"DEF\"GHI".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"DEF\"GHI\'", node.toString());
		assertEquals("ABC\"DEF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test017s() {
		Node node = Node.of(Node.ATT_VALUE, "ABC\"DEF\"GHI");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"DEF\"GHI\'", node.toString());
		assertEquals("ABC\"DEF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test018b() {
		Node node = Node.of(Node.ATT_VALUE, "ABC\'DEF\'GHI".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'DEF\'GHI\"", node.toString());
		assertEquals("ABC\'DEF\'GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test018s() {
		Node node = Node.of(Node.ATT_VALUE, "ABC\'DEF\'GHI");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'DEF\'GHI\"", node.toString());
		assertEquals("ABC\'DEF\'GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test019b() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABC\"DEF\"GHI\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"DEF\"GHI\'", node.toString());
		assertEquals("ABC\"DEF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test019s() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABC\"DEF\"GHI\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"DEF\"GHI\'", node.toString());
		assertEquals("ABC\"DEF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test020b() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABC\'DEF\'GHI\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'DEF\'GHI\"", node.toString());
		assertEquals("ABC\'DEF\'GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test020s() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABC\'DEF\'GHI\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'DEF\'GHI\"", node.toString());
		assertEquals("ABC\'DEF\'GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test021b() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABC\"DEF\"GHI\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"DEF\"GHI\'", node.toString());
		assertEquals("ABC\"DEF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test021s() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABC\"DEF\"GHI\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"DEF\"GHI\'", node.toString());
		assertEquals("ABC\"DEF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test022b() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABC\'DEF\'GHI\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'DEF\'GHI\"", node.toString());
		assertEquals("ABC\'DEF\'GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test022s() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABC\'DEF\'GHI\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'DEF\'GHI\"", node.toString());
		assertEquals("ABC\'DEF\'GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test023b() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABC\"D\'EF\"GHI\"".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"D&apos;EF\"GHI\'", node.toString());
		assertEquals("ABC\"D&apos;EF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test023s() {
		Node node = Node.of(Node.ATT_VALUE, "\"ABC\"D\'EF\"GHI\"");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\'ABC\"D&apos;EF\"GHI\'", node.toString());
		assertEquals("ABC\"D&apos;EF\"GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test024b() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABC\'D\"EF\'GHI\'".getBytes());
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'D&quot;EF\'GHI\"", node.toString());
		assertEquals("ABC\'D&quot;EF\'GHI", ((QuotedString)node).innerText());
	}

	@Test
	void test024s() {
		Node node = Node.of(Node.ATT_VALUE, "\'ABC\'D\"EF\'GHI\'");
		assertEquals(Node.ATT_VALUE, node.type);
		assertEquals(true, node instanceof QuotedString);
		assertEquals("\"ABC\'D&quot;EF\'GHI\"", node.toString());
		assertEquals("ABC\'D&quot;EF\'GHI", ((QuotedString)node).innerText());
	}

}
