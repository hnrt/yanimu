package com.hideakin.yanimu.maven.settings;

import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.start;
import static com.hideakin.yanimu.util.TestHelper.print;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.hideakin.yanimu.maven.MavenHelper;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

public class SettingsDocumentTest {

	private static MockedStatic<MavenHelper> mockedMavenHelp;

	@BeforeAll
	static void initAll() {
		start(SettingsDocumentTest.class);
	}

	@AfterAll
	static void tearDownAll() {
		finish(SettingsDocumentTest.class);
	}

	@BeforeEach
    void beforeEach(TestInfo info) {
		start(info);
		mockedMavenHelp = Mockito.mockStatic(MavenHelper.class);
		mockedMavenHelp.when(() -> MavenHelper.getMavenHome(anyString())).thenReturn("mocked");
    }

	@AfterEach
	void afterEach(TestInfo info) {
		finish(info);
		if (mockedMavenHelp != null) {
			mockedMavenHelp.close();
		}
	}

	@Test
	void test001() {
		Path path = SettingsDocument.USER_PATH;
		String pathString = path.toString();
		String userHome = System.getProperty("user.home");
		String relative = pathString.substring(userHome.length() + (userHome.endsWith(File.separator) ? 0 : 1));
		print("user.relative=%s", relative);
		assertEquals(".m2" + File.separator + "settings.xml", relative);
	}

	// -Xshare:off to supperss the warning:
	// OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
	@Test
	void test002() {
		Path path = SettingsDocument.GLOBAL_PATH;
		print("global=%s", path);
		assertEquals("mocked" + File.separator + "conf" + File.separator + "settings.xml", path.toString());
	}

	@Test
	void test101() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <localRepository/>\r\n"
				+ "  <interactiveMode/>\r\n"
				+ "  <offline/>\r\n"
				+ "  <pluginGroups/>\r\n"
				+ "  <servers/>\r\n"
				+ "  <mirrors/>\r\n"
				+ "  <proxies/>\r\n"
				+ "  <profiles/>\r\n"
				+ "  <activeProfiles/>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			assertEquals(Path.of(System.getProperty("user.home")).resolve(".m2").resolve("repository").toString(), doc.localRepository());
			assertEquals(true, doc.interactiveMode());
			assertEquals(false, doc.offline());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test102() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <localRepository>${user.home}/.m2/repository</localRepository>\r\n"
				+ "  <interactiveMode>true</interactiveMode>\r\n"
				+ "  <offline>false</offline>\r\n"
				+ "  <pluginGroups/>\r\n"
				+ "  <servers/>\r\n"
				+ "  <mirrors/>\r\n"
				+ "  <proxies/>\r\n"
				+ "  <profiles/>\r\n"
				+ "  <activeProfiles/>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			assertEquals("${user.home}/.m2/repository", doc.localRepository());
			assertEquals(true, doc.interactiveMode());
			assertEquals(false, doc.offline());
			String translated = SettingsDocument.translate(doc.localRepository());
			print("translated=%s", translated);
			assertEquals(System.getProperty("user.home") + "/.m2/repository", translated);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test103() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <localRepository></localRepository>\r\n"
				+ "  <interactiveMode>false</interactiveMode>\r\n"
				+ "  <offline>true</offline>\r\n"
				+ "  <pluginGroups/>\r\n"
				+ "  <servers/>\r\n"
				+ "  <mirrors/>\r\n"
				+ "  <proxies/>\r\n"
				+ "  <profiles/>\r\n"
				+ "  <activeProfiles/>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			assertEquals(Path.of(System.getProperty("user.home")).resolve(".m2").resolve("repository").toString(), doc.localRepository());
			assertEquals(false, doc.interactiveMode());
			assertEquals(true, doc.offline());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
