package com.hideakin.yanimu.maven.settings;

import static com.hideakin.yanimu.util.TestHelper.finish;
import static com.hideakin.yanimu.util.TestHelper.start;
import static com.hideakin.yanimu.util.TestHelper.print;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\"/>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			print("localRepository=%s", doc.localRepository());
			assertEquals(SettingsDocument.DEFAULT_LOCAL_REPOSITORY, doc.localRepository());
			assertEquals(true, doc.interactiveMode());
			assertEquals(false, doc.offline());
			String translated = SettingsDocument.translate(doc.localRepository());
			print("translated=%s", translated);
			assertEquals(System.getProperty("user.home") + "/.m2/repository", translated);
			print("Path.of(translated)=%s", Path.of(translated));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test102() {
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
			print("localRepository=%s", doc.localRepository());
			assertEquals(SettingsDocument.DEFAULT_LOCAL_REPOSITORY, doc.localRepository());
			assertEquals(true, doc.interactiveMode());
			assertEquals(false, doc.offline());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test103() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <localRepository>/foo/bar/baz</localRepository>\r\n"
				+ "  <interactiveMode>true</interactiveMode>\r\n"
				+ "  <offline>false</offline>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			assertEquals("/foo/bar/baz", doc.localRepository());
			assertEquals(true, doc.interactiveMode());
			assertEquals(false, doc.offline());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test104() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <localRepository></localRepository>\r\n"
				+ "  <interactiveMode>false</interactiveMode>\r\n"
				+ "  <offline>true</offline>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			assertEquals(SettingsDocument.DEFAULT_LOCAL_REPOSITORY, doc.localRepository());
			assertEquals(false, doc.interactiveMode());
			assertEquals(true, doc.offline());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test111() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <pluginGroups>\r\n"
				+ "    <pluginGroup>org.eclipse.jetty</pluginGroup>\r\n"
				+ "  </pluginGroups>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			List<String> pg = doc.pluginGroups();
			assertEquals(3, pg.size());
			assertEquals("org.eclipse.jetty", pg.get(0));
			assertEquals("org.apache.maven.plugins", pg.get(1));
			assertEquals("org.codehaus.mojo", pg.get(2));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test121() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <servers>\r\n"
				+ "    <server>\r\n"
				+ "      <id>server001</id>\r\n"
				+ "      <username>my_login</username>\r\n"
				+ "      <password>my_password</password>\r\n"
				+ "      <privateKey>${user.home}/.ssh/id_dsa</privateKey>\r\n"
				+ "      <passphrase>some_passphrase</passphrase>\r\n"
				+ "      <filePermissions>664</filePermissions>\r\n"
				+ "      <directoryPermissions>775</directoryPermissions>\r\n"
				+ "      <configuration></configuration>\r\n"
				+ "    </server>\r\n"
				+ "  </servers>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		String expectation = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <servers>\r\n"
				+ "    <server>\r\n"
				+ "      <id>server001</id>\r\n"
				+ "      <username>john.doe</username>\r\n"
				+ "      <password>abracadabra</password>\r\n"
				+ "      <privateKey>${user.home}/.ssh/id_dsa.2</privateKey>\r\n"
				+ "      <passphrase>Wow!</passphrase>\r\n"
				+ "      <filePermissions>444</filePermissions>\r\n"
				+ "      <directoryPermissions>555</directoryPermissions>\r\n"
				+ "      <configuration></configuration>\r\n"
				+ "    </server>\r\n"
				+ "  </servers>\r\n"
				+ "</settings>";
		byte[] expectationBytes = expectation.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			ServerCollection servers = doc.servers();
			assertEquals(1, servers.size());
			Server server = servers.get("server001");
			assertEquals("server001", server.id());
			assertEquals("my_login", server.username());
			assertEquals("my_password", server.password());
			assertEquals("${user.home}/.ssh/id_dsa", server.privateKey());
			assertEquals("some_passphrase", server.passphrase());
			assertEquals("664", server.filePermissions());
			assertEquals("775", server.directoryPermissions());
			Server server2 = servers.get("server002");
			assertEquals(null, server2);
			server.setUsername("john.doe");
			server.setPassword("abracadabra");
			server.setPrivateKey("${user.home}/.ssh/id_dsa.2");
			server.setPassphrase("Wow!");
			server.setFilePermissions("444");
			server.setDirectoryPermissions("555");
			print("%s", doc.toString());
			assertArrayEquals(expectationBytes, doc.sequence());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test131() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <mirrors>\r\n"
				+ "    <mirror>\r\n"
				+ "      <id>planetmirror.com</id>\r\n"
				+ "      <name>PlanetMirror Australia</name>\r\n"
				+ "      <url>http://downloads.planetmirror.com/pub/maven2</url>\r\n"
				+ "      <mirrorOf>central</mirrorOf>\r\n"
				+ "    </mirror>\r\n"
				+ "  </mirrors>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		String expectation = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <mirrors>\r\n"
				+ "    <mirror>\r\n"
				+ "      <id>planetmirror.com</id>\r\n"
				+ "      <name>FooBar</name>\r\n"
				+ "      <url>http://www.example.com</url>\r\n"
				+ "      <mirrorOf>local</mirrorOf>\r\n"
				+ "    </mirror>\r\n"
				+ "  </mirrors>\r\n"
				+ "</settings>";
		byte[] expectationBytes = expectation.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			MirrorCollection mirrors = doc.mirrors();
			assertEquals(1, mirrors.size());
			Mirror mirror = mirrors.get("planetmirror.com");
			assertEquals("planetmirror.com", mirror.id());
			assertEquals("PlanetMirror Australia", mirror.name());
			assertEquals("http://downloads.planetmirror.com/pub/maven2", mirror.url());
			assertEquals("central", mirror.mirrorOf());
			Mirror mirror2 = mirrors.get("mirror002");
			assertEquals(null, mirror2);
			mirror.setName("FooBar");
			mirror.setUrl("http://www.example.com");
			mirror.setMirrorOf("local");
			print("%s", doc.toString());
			assertArrayEquals(expectationBytes, doc.sequence());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	void test141() {
		String source = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <proxies>\r\n"
				+ "    <proxy>\r\n"
				+ "      <id>myproxy</id>\r\n"
				+ "      <active>true</active>\r\n"
				+ "      <protocol>http</protocol>\r\n"
				+ "      <host>proxy.somewhere.com</host>\r\n"
				+ "      <port>8080</port>\r\n"
				+ "      <username>proxyuser</username>\r\n"
				+ "      <password>somepassword</password>\r\n"
				+ "      <nonProxyHosts>*.google.com|ibiblio.org</nonProxyHosts>\r\n"
				+ "    </proxy>\r\n"
				+ "  </proxies>\r\n"
				+ "</settings>";
		byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);
		String expectation = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd\">\r\n"
				+ "  <proxies>\r\n"
				+ "    <proxy>\r\n"
				+ "      <id>myproxy</id>\r\n"
				+ "      <active>false</active>\r\n"
				+ "      <protocol>https</protocol>\r\n"
				+ "      <host>proxy.example.co.jp</host>\r\n"
				+ "      <port>443</port>\r\n"
				+ "      <username>jane.doe</username>\r\n"
				+ "      <password>noway</password>\r\n"
				+ "      <nonProxyHosts>*.google.co.jp</nonProxyHosts>\r\n"
				+ "    </proxy>\r\n"
				+ "  </proxies>\r\n"
				+ "</settings>";
		byte[] expectationBytes = expectation.getBytes(StandardCharsets.UTF_8);
		SettingsDocument doc = SettingsDocument.of(SettingsDocument.USER_PATH);
		try {
			doc.load(sourceBytes);
			print("%s", doc.toString());
			ProxyCollection proxies = doc.proxies();
			assertEquals(1, proxies.size());
			Proxy proxy = proxies.get("myproxy");
			assertEquals("myproxy", proxy.id());
			assertEquals(true, proxy.active());
			assertEquals("http", proxy.protocol());
			assertEquals("proxy.somewhere.com", proxy.host());
			assertEquals(8080, proxy.port());
			assertEquals("proxyuser", proxy.username());
			assertEquals("somepassword", proxy.password());
			assertEquals("*.google.com|ibiblio.org", proxy.nonProxyHosts());
			Proxy proxy2 = proxies.get("proxy002");
			assertEquals(null, proxy2);
			proxy.setActive(Boolean.valueOf(false));
			proxy.setProtocol("https");
			proxy.setHost("proxy.example.co.jp");
			proxy.setPort(Integer.valueOf(443));
			proxy.setUsername("jane.doe");
			proxy.setPassword("noway");
			proxy.setNonProxyHosts("*.google.co.jp");
			print("%s", doc.toString());
			assertArrayEquals(expectationBytes, doc.sequence());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
