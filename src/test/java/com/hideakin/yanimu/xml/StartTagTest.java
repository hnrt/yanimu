package com.hideakin.yanimu.xml;

import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.print;
import static com.hideakin.yanimu.util.TestHelper.start;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class StartTagTest {

	@BeforeAll
	static void initAll() {
		start(StartTagTest.class);
	}

	@AfterAll
	static void tearDownAll() {
		finish(StartTagTest.class);
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
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute("abc", "xyz");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc=\"xyz\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test002() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\n/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute("abc", "xyz");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc=\"xyz\"\r\n/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test003() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting>Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute("abc1", "xyz1");
			st.addAttribute("abc2", "xyz2");
			st.addAttribute("abc3", "xyz3");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc1=\"xyz1\" abc2=\"xyz2\" abc3=\"xyz3\">", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test004() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting  abc0=\"xyz0\"   />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute("abc1", "xyz1");
			st.addAttribute("abc2", "xyz2");
			st.addAttribute("abc3", "xyz3");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting  abc0=\"xyz0\" abc1=\"xyz1\" abc2=\"xyz2\" abc3=\"xyz3\"   />", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test005() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"   />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute(0, "abc1", "xyz1");
			st.addAttribute(1, "abc1.5", "xyz1.5");
			st.addAttribute(3, "abc8", "xyz8");
			st.addAttribute(5, "abc9", "xyz9");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc1=\"xyz1\" abc1.5=\"xyz1.5\"\r\nabc2=\"xyz2\" abc8=\"xyz8\" abc8.1=\"xyz8.1\" abc9=\"xyz9\"   />", st.toString());
			assertEquals("xyz1", st.attribute(0));
			assertEquals("xyz1.5", st.attribute(1));
			assertEquals("xyz2", st.attribute(2));
			assertEquals("xyz8", st.attribute(3));
			assertEquals("xyz8.1", st.attribute(4));
			assertEquals("xyz9", st.attribute(5));
			assertEquals("xyz1", st.attribute("abc1"));
			assertEquals("xyz1.5", st.attribute("abc1.5"));
			assertEquals("xyz2", st.attribute("abc2"));
			assertEquals("xyz8", st.attribute("abc8"));
			assertEquals("xyz8.1", st.attribute("abc8.1"));
			assertEquals("xyz9", st.attribute("abc9"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test101() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.setAttribute(0, "abc1", "xyz1");
			st.setAttribute(1, "abc1.5", "xyz1.5");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting\r\nabc1=\"xyz1\" abc1.5=\"xyz1.5\"/>", st.toString());
			assertEquals("xyz1", st.attribute(0));
			assertEquals("xyz1.5", st.attribute(1));
			assertEquals("xyz1", st.attribute("abc1"));
			assertEquals("xyz1.5", st.attribute("abc1.5"));
			assertEquals(null, st.attribute("abc2"));
			assertEquals("opq", st.attribute("abc2", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test102() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.setAttribute("abc2", "xyz22");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting\r\nabc2 = \"xyz22\" abc8.1=\"xyz8.1\"/>", st.toString());
			assertEquals("xyz22", st.attribute(0));
			assertEquals("xyz8.1", st.attribute(1));
			assertEquals("xyz22", st.attribute("abc2"));
			assertEquals("xyz8.1", st.attribute("abc8.1"));
			assertEquals(null, st.attribute("abc3"));
			assertEquals("opq", st.attribute("abc3", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test103() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.setAttribute("abc8.1", "xyzzy8.1");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyzzy8.1\"/>", st.toString());
			assertEquals("xyz2", st.attribute(0));
			assertEquals("xyzzy8.1", st.attribute(1));
			assertEquals("xyz2", st.attribute("abc2"));
			assertEquals("xyzzy8.1", st.attribute("abc8.1"));
			assertEquals(null, st.attribute("abc3"));
			assertEquals("opq", st.attribute("abc3", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test104() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.setAttribute("abc9", "xyzzy9");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting\r\nabc2 = \"xyz2\" abc8.1=\"xyz8.1\" abc9=\"xyzzy9\"/>", st.toString());
			assertEquals("xyz2", st.attribute(0));
			assertEquals("xyz8.1", st.attribute(1));
			assertEquals("xyzzy9", st.attribute(2));
			assertEquals("xyz2", st.attribute("abc2"));
			assertEquals("xyz8.1", st.attribute("abc8.1"));
			assertEquals("xyzzy9", st.attribute("abc9"));
			assertEquals(null, st.attribute(3));
			assertEquals("lmn", st.attribute(3, "lmn"));
			assertEquals(null, st.attribute("abc3"));
			assertEquals("opq", st.attribute("abc3", "opq"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test201() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.removeAllAttributes();
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test202() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"  >Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.removeAllAttributes();
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting  >", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test203() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"  />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.removeAllAttributes();
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting  />", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test204() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\">Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.removeAllAttributes();
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test301() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE  %s", st.toDebuggingString());
			Node node = st.removeAttribute(0);
			print("AFTER   %s", st.toDebuggingString());
			print("REMOVED %s", node.toDebuggingString());
			assertEquals("<greeting abc8.1=\"xyz8.1\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test302() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			Node node = st.removeAttribute(1);
			print("AFTER  %s", st.toDebuggingString());
			print("REMOVED %s", node.toDebuggingString());
			assertEquals("<greeting\r\nabc2=\"xyz2\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test303() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE  %s", st.toDebuggingString());
			Node node = st.removeAttribute(2);
			print("AFTER   %s", st.toDebuggingString());
			print("REMOVED %s", node.toDebuggingString());
			assertEquals(Node.NULL, node.type);
			assertEquals("<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test311() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE  %s", st.toDebuggingString());
			Node node = st.removeAttribute("abc2");
			print("AFTER   %s", st.toDebuggingString());
			print("REMOVED %s", node.toDebuggingString());
			assertEquals("<greeting abc8.1=\"xyz8.1\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test312() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2 = \"xyz2\" abc8.1 = \"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE  %s", st.toDebuggingString());
			Node node = st.removeAttribute("abc8.1");
			print("AFTER   %s", st.toDebuggingString());
			print("REMOVED %s", node.toDebuggingString());
			assertEquals("<greeting\r\nabc2 = \"xyz2\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test313() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE  %s", st.toDebuggingString());
			Node node = st.removeAttribute("xyz");
			print("AFTER   %s", st.toDebuggingString());
			print("REMOVED %s", node.toDebuggingString());
			assertEquals(Node.NULL, node.type);
			assertEquals("<greeting\r\nabc2=\"xyz2\" abc8.1=\"xyz8.1\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test801() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting>Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			st.set(0, Node.of(Node.S, " ".getBytes()));
			fail("An exception was expectedly to be thrown.");
		} catch (Exception e) {
			print("%s", e.getMessage());
		}
	}

	@Test
	void test802() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			st.add(Node.of(Node.S, " ".getBytes()));
			fail("An exception was expectedly to be thrown.");
		} catch (Exception e) {
			print("%s", e.getMessage());
		}
	}

	@Test
	void test803() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting>Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			st.add(0, Node.of(Node.S, " ".getBytes()));
			fail("An exception was expectedly to be thrown.");
		} catch (Exception e) {
			print("%s", e.getMessage());
		}
	}

	@Test
	void test804() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			st.removeAll();
			fail("An exception was expectedly to be thrown.");
		} catch (Exception e) {
			print("%s", e.getMessage());
		}
	}

	@Test
	void test805() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting>Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			st.remove(0);
			fail("An exception was expectedly to be thrown.");
		} catch (Exception e) {
			print("%s", e.getMessage());
		}
	}

	@Test
	void test806() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			st.remove(st.get(0));
			fail("An exception was expectedly to be thrown.");
		} catch (Exception e) {
			print("%s", e.getMessage());
		}
	}

	@Test
	void test807() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting>Hello</greeting>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			st.remove(st.get(0), 0, st.size());
			fail("An exception was expectedly to be thrown.");
		} catch (Exception e) {
			print("%s", e.getMessage());
		}
	}

	@Test
	void test901() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute("abc", "x\"y\"z");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc=\'x\"y\"z\'/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test902() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute("abc", "x\'y\'z");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc=\"x\'y\'z\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test903() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.addAttribute("abc", "\"xy\'z\"");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc=\'\"xy&apos;z\"\'/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test904() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting aaa=\'zzz\' />";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.setAttribute(0, "abc", "\"xy\'z\"");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc=\"&quot;xy\'z&quot;\" />", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test905() {
		String source = "<?xml version=\"1.0\"?>\r\n<greeting aaa=\'zzz\'/>";
		Document doc = new Document();
		try {
			doc.load(source.getBytes());
			StartTag st = doc.root().startTag();
			print("BEFORE %s", st.toDebuggingString());
			st.setAttribute(0, "abc", "The quick brown \"fox\" jumps over the \'lazy\' dog.");
			print("AFTER  %s", st.toDebuggingString());
			assertEquals("<greeting abc=\"The quick brown &quot;fox&quot; jumps over the \'lazy\' dog.\"/>", st.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
