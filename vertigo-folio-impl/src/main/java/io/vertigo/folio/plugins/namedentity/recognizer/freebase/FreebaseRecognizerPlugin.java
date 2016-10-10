package io.vertigo.folio.plugins.namedentity.recognizer.freebase;

import java.net.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import io.vertigo.folio.impl.namedentity.RecognizerPlugin;
import io.vertigo.folio.namedentity.NamedEntity;
import io.vertigo.folio.plugins.namedentity.recognizer.UrlUtil;
import io.vertigo.lang.Assertion;

/**
 * Created by sbernard on 17/12/2014.
 */
public final class FreebaseRecognizerPlugin implements RecognizerPlugin {
	private final String FREEBASE_PREFIX = "https://www.googleapis.com/freebase/v1/search";
	private final String FREEBASE_API_KEY;
	private final Proxy proxy;

	@Inject
	public FreebaseRecognizerPlugin(final @Named("apiKey") String apiKey, final @Named("proxyHost") Optional<String> proxyHost, @Named("proxyPort") final Optional<String> proxyPort) {
		Assertion.checkNotNull(apiKey);
		Assertion.checkNotNull(proxyHost);
		Assertion.checkNotNull(proxyPort);
		Assertion.checkArgument(proxyHost.isPresent() && proxyPort.isPresent() || !proxyHost.isPresent() && !proxyPort.isPresent(), "les deux paramètres host et port doivent être tous les deux remplis ou vides");
		//----
		proxy = UrlUtil.buildProxy(proxyHost, proxyPort);

		FREEBASE_API_KEY = apiKey;
	}

	@Override
	public Set<NamedEntity> recognizeNamedEntities(final List<String> tokens) {
		Assertion.checkNotNull(tokens);
		//----
		final Set<NamedEntity> namedEntities = new HashSet<>();
		for (final String token : tokens) {
			final String urlAsString = FREEBASE_PREFIX + "?query=" + UrlUtil.encodeUTF8(token) + "&key=" + FREEBASE_API_KEY + "&limit=5&lang=fr";

			final JSONObject response = UrlUtil.exec(proxy, urlAsString);
			final JSONArray results = (JSONArray) response.get("result");
			try {
				final Iterator<Object> jsonObjectIterator = results.iterator();
				while (jsonObjectIterator.hasNext()) {
					final JSONObject nameEntity = (JSONObject) jsonObjectIterator.next();
					final String label = nameEntity.get("name").toString();
					/*final String entityUrl = "http://www.freebase.com" + nameEntity.get("mid").toString();
					final JSONObject notable = (JSONObject) nameEntity.get("notable");
					final String type = notable.get("name").toString();*/
					namedEntities.add(new NamedEntity(label/*, type, entityUrl*/));
				}
			} catch (final Exception e) {
				System.err.println("Error characterizing token : " + token);
			}

		}
		return namedEntities;
	}

}
