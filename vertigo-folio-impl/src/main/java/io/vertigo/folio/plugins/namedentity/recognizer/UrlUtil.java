package io.vertigo.folio.plugins.namedentity.recognizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.vertigo.lang.Assertion;
import io.vertigo.lang.WrappedException;

public final class UrlUtil {
	private static final JSONParser parser = new JSONParser();

	public static Proxy buildProxy(final Optional<String> proxyHost, final Optional<String> proxyPort) {
		if (proxyHost.isPresent()) {
			return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost.get(), Integer.parseInt(proxyPort.get())));
		}
		return Proxy.NO_PROXY;
	}

	private static HttpURLConnection createConnection(final Proxy proxy, final URL url) {
		Assertion.checkNotNull(url);
		//----
		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
			connection.setDoOutput(true);
			return connection;
		} catch (final IOException e) {
			throw WrappedException.wrap(e, "Error on connection (HTTP)");
		}
	}

	private static URL buildUrl(final String urlAsString) {
		try {
			return new URL(urlAsString);
		} catch (final MalformedURLException e) {
			throw new RuntimeException("Error when creating URL", e);
		}
	}

	public static String encodeUTF8(final String token) {
		try {
			return URLEncoder.encode(token, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Error when encoding address", e);
		}
	}

	private static String exec(final HttpURLConnection connection) {
		try {
			connection.connect();
			final String jsonString;
			try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				jsonString = bufferedReader.lines()
						.collect(Collectors.joining("\n"));
			}
			return jsonString;
		} catch (final IOException e) {
			throw new RuntimeException("Erreur de connexion au service", e);
		} finally {
			connection.disconnect();
		}
	}

	private static JSONObject parse(final String jsonString) {
		try {
			return (JSONObject) parser.parse(jsonString);
		} catch (final ParseException e) {
			throw new RuntimeException("Erreur de parsing de la réponse", e);
		}
	}

	public static JSONObject exec(final Proxy proxy, final String urlAsString) {
		final URL url = UrlUtil.buildUrl(urlAsString);
		final HttpURLConnection connection = createConnection(proxy, url);
		connection.setRequestProperty("Accept", "application/json");

		final String jsonResponse = exec(connection);
		return parse(jsonResponse);
	}
}
