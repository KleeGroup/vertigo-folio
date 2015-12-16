package io.vertigo.folio.plugins.namedentity.recognizer.freebase;

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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by sbernard on 17/12/2014.
 */
public class FreebaseRecognizerPlugin implements RecognizerPlugin {
	private final String FREEBASE_PREFIX = "https://www.googleapis.com/freebase/v1/search";
	private final String FREEBASE_API_KEY;
	private final Option<Proxy> proxy;

	@Inject
	public FreebaseRecognizerPlugin(final @Named("apiKey") String apiKey, final @Named("proxyHost") Option<String> proxyHost, @Named("proxyPort") final Option<String> proxyPort) {
		Assertion.checkNotNull(apiKey);
		Assertion.checkNotNull(proxyHost);
		Assertion.checkNotNull(proxyPort);
		Assertion.checkArgument(proxyHost.isDefined() && proxyPort.isDefined() || proxyHost.isEmpty() && proxyPort.isEmpty(), "les deux paramètres host et port doivent être tous les deux remplis ou vides");
		//----
		if (proxyHost.isDefined()) {
			proxy = Option.some(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost.get(), Integer.parseInt(proxyPort.get()))));
		} else {
			proxy = Option.none();
		}
		FREEBASE_API_KEY = apiKey;
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
				urlString = FREEBASE_PREFIX + "?query=" + URLEncoder.encode(token, "UTF-8") + "&key=" + FREEBASE_API_KEY + "&limit=5&lang=fr";
			} catch (final UnsupportedEncodingException e) {
				throw new RuntimeException("Erreur lors de l'encodage de l'adresse", e);
			}
			final URL url;
			try {
				url = new URL(urlString);
			} catch (final MalformedURLException e) {
				throw new RuntimeException("Erreur lors de la creation de l'URL", e);
			}
			final HttpURLConnection connection = createConnection(url);
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

			final JSONArray results = (JSONArray) response.get("result");

			if (results.size() > 0) {
				try {
					final Iterator<Object> jsonObjectIterator = results.iterator();
					while (jsonObjectIterator.hasNext()) {
						final JSONObject nameEntity = (JSONObject) jsonObjectIterator.next();
						final String name = nameEntity.get("name").toString();
						final String entityUrl = "http://www.freebase.com" + nameEntity.get("mid").toString();
						final JSONObject notable = (JSONObject) nameEntity.get("notable");
						final String type = notable.get("name").toString();
						namedEntities.add(new NamedEntity(name, type, entityUrl));
					}
				} catch (final Exception e) {
					System.err.println("Error characterizing token : " + token);
				}

			}
		}
		return namedEntities;
	}

	private HttpURLConnection createConnection(final URL url) {
		Assertion.checkNotNull(url);
		//----
		try {
			return doCreateConnection(url);
		} catch (final IOException e) {
			throw new RuntimeException("Erreur de connexion au service (HTTP)", e);
		}
	}

	private HttpURLConnection doCreateConnection(final URL url) throws IOException {
		Assertion.checkNotNull(url);
		//---------------------------------------------------------------------------
		HttpURLConnection connection;
		if (proxy.isDefined()) {
			connection = (HttpURLConnection) url.openConnection(proxy.get());
		} else {
			connection = (HttpURLConnection) url.openConnection();
		}
		connection.setDoOutput(true);
		return connection;
	}
}
