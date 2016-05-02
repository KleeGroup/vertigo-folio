package io.vertigo.folio.plugins.namedentity.recognizer.dbpedia;

import io.vertigo.folio.impl.namedentity.RecognizerPlugin;
import io.vertigo.folio.namedentity.NamedEntity;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Option;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by sbernard on 10/12/2014.
 */
public final class DbpediaRecognizerPlugin implements RecognizerPlugin {
	private static final String DBPEDIA_LOOKUP_PREFIX = "http://lookup.dbpedia.org/api/search/KeywordSearch";
	private final Option<Proxy> proxy;

	@Inject
	public DbpediaRecognizerPlugin(final @Named("proxyHost") Option<String> proxyHost, @Named("proxyPort") final Option<String> proxyPort) {
		Assertion.checkNotNull(proxyHost);
		Assertion.checkNotNull(proxyPort);
		Assertion.checkArgument((proxyHost.isPresent() && proxyPort.isPresent()) || (proxyHost.isEmpty() && proxyPort.isEmpty()), "les deux paramètres host et port doivent être tous les deux remplis ou vides");
		//----
		if (proxyHost.isPresent()) {
			proxy = Option.of(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost.get(), Integer.parseInt(proxyPort.get()))));
		} else {
			proxy = Option.empty();
		}
	}

	@Override
	public Set<NamedEntity> recognizeNamedEntities(final List<String> tokens) {
		Assertion.checkNotNull(tokens);
		//----
		final JSONParser parser = new JSONParser();
		final Set<NamedEntity> namedEntities = new HashSet<>();
		for (final String token : tokens) {
			final String urlString;
			try {
				urlString = DBPEDIA_LOOKUP_PREFIX + "?QueryString=" + URLEncoder.encode(token, "UTF-8");
			} catch (final UnsupportedEncodingException e) {
				throw new RuntimeException("Erreur lors de l'encodage de l'adresse", e);
			}
			final URL url;
			try {
				url = new URL(urlString);
			} catch (final MalformedURLException e) {
				throw new RuntimeException("Erreur lors de la creation de l'URL", e);
			}
			final HttpURLConnection connection = createConnection(proxy, url);
			connection.setRequestProperty("Accept", "application/json");

			JSONObject response = null;
			try {
				connection.connect();
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				final StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line + '\n');
				}
				final String jsonString = stringBuilder.toString();
				response = (JSONObject) parser.parse(jsonString);
			} catch (final IOException e) {
				throw new RuntimeException("Erreur de connexion au service", e);
			} catch (final ParseException e) {
				throw new RuntimeException("Erreur de parsing de la réponse", e);
			} finally {
				connection.disconnect();
			}

			try {
				final JSONArray results = (JSONArray) response.get("results");
				final JSONObject nameEntity = (JSONObject) results.get(0);
				final String name = nameEntity.get("label").toString();
				final JSONObject typeObject = ((JSONObject) ((JSONArray) nameEntity.get("classes")).get(0));
				final String type = typeObject.get("label").toString();
				final String entityUrl = nameEntity.get("url").toString();
				namedEntities.add(new NamedEntity(name, type, entityUrl));
			} catch (final Exception e) {
				System.err.println("Error characterizing token : " + token);
			}
		}
		return namedEntities;
	}

	private static HttpURLConnection createConnection(final Option<Proxy> proxy, final URL url) {
		Assertion.checkNotNull(url);
		//----
		try {
			return doCreateConnection(proxy, url);
		} catch (final IOException e) {
			throw new RuntimeException("Erreur de connexion au service (HTTP)", e);
		}
	}

	private static HttpURLConnection doCreateConnection(final Option<Proxy> proxy, final URL url) throws IOException {
		Assertion.checkNotNull(url);
		//---------------------------------------------------------------------------
		HttpURLConnection connection;
		if (proxy.isPresent()) {
			connection = (HttpURLConnection) url.openConnection(proxy.get());
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}
		connection.setDoOutput(true);
		return connection;
	}
}
