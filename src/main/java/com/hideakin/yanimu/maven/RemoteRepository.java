package com.hideakin.yanimu.maven;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class RemoteRepository {

	public static final String CENTRAL_URL_1 = "https://repo1.maven.org/maven2";
	public static final String CENTRAL_URL_2 = "https://repo.maven.apache.org/maven2";
	public static final String CENTRAL_URL = CENTRAL_URL_1;

	private static HttpClient _client = HttpClient.newBuilder()
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();

	public static String urlOfPom(String baseUrl, String groupId, String artifactId, String version) {
		if (baseUrl != null && groupId != null && groupId.indexOf('.') > 0 && artifactId != null && version != null) {
			String url = String.format(
					baseUrl.endsWith("/") ? "%s%s/%s/%s/%s-%s.pom" : "%s/%s/%s/%s/%s-%s.pom",
					baseUrl,
					groupId.replaceAll("\\.", "/"),
					artifactId,
					version,
					artifactId,
					version);
			return url;
		} else {
			return null;
		}
	}

	public static String urlOf(String baseUrl, String groupId, String artifactId, String fileName) {
		if (baseUrl != null && groupId != null && groupId.indexOf('.') > 0 && artifactId != null && fileName != null) {
			String url = String.format(
					baseUrl.endsWith("/") ? "%s%s/%s/%s" : "%s/%s/%s/%s",
					baseUrl,
					groupId.replaceAll("\\.", "/"),
					artifactId,
					fileName);
			return url;
		} else {
			return null;
		}
	}

	public static byte[] download(String url) throws Exception {
		URI uri = URI.create(url);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.build();
		HttpResponse<String> response = _client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			byte[] content = response.body().getBytes(StandardCharsets.UTF_8);
			return content;
		} else {
			throw new IOException("Failed to download: %d".formatted(response.statusCode()));
		}
	}

}
