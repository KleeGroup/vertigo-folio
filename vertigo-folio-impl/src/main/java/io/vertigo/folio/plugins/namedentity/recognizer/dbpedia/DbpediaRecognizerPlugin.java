package io.vertigo.folio.plugins.namedentity.recognizer.dbpedia;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.vertigo.folio.impl.namedentity.RecognizerPlugin;
import io.vertigo.folio.namedentity.NamedEntity;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.WrappedException;

/**
 * Created by sbernard on 10/12/2014.
 */
public final class DbpediaRecognizerPlugin implements RecognizerPlugin {
	private static final String DBPEDIA_LOOKUP_PREFIX = "http://lookup.dbpedia.org/api/search/KeywordSearch";
	private final Proxy proxy;

	@Inject
	public DbpediaRecognizerPlugin(final @Named("proxyHost") Optional<String> proxyHost, @Named("proxyPort") final Optional<String> proxyPort) {
		Assertion.checkNotNull(proxyHost);
		Assertion.checkNotNull(proxyPort);
		Assertion.checkArgument((proxyHost.isPresent() && proxyPort.isPresent()) || (!proxyHost.isPresent() && !proxyPort.isPresent()), "les deux paramètres host et port doivent être tous les deux remplis ou vides");
		//----
		proxy = buildProxy(proxyHost, proxyPort);
	}

	private static Proxy buildProxy(final Optional<String> proxyHost, final Optional<String> proxyPort) {
		if (proxyHost.isPresent()) {
			return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost.get(), Integer.parseInt(proxyPort.get())));
		}
		return Proxy.NO_PROXY;
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

			final JSONObject response;
			try {
				connection.connect();
				final String jsonString;
				try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					jsonString = bufferedReader.lines()
							.collect(Collectors.joining("\n"));
				}
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
				final JSONObject namedEntity = (JSONObject) results.get(0);
				final String label = namedEntity.get("label").toString();
				//				final JSONObject typeObject = ((JSONObject) ((JSONArray) namedEntity.get("classes")).get(0));
				//				final String type = typeObject.get("label").toString();
				//				final String entityUrl = namedEntity.get("url").toString();
				namedEntities.add(new NamedEntity(label/*, type, entityUrl*/));
			} catch (final Exception e) {
				throw new WrappedException("Error characterizing token : " + token, e);
			}
		}
		return namedEntities;
	}

	private static HttpURLConnection createConnection(final Proxy proxy, final URL url) {
		Assertion.checkNotNull(url);
		//----
		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
			connection.setDoOutput(true);
			return connection;
		} catch (final IOException e) {
			throw new WrappedException("Error on connection (HTTP)", e);
		}
	}

}
