package io.vertigo.folio.plugins.namedentity.recognizer.dbpedia;

import java.net.Proxy;
import java.util.HashSet;
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
import io.vertigo.lang.WrappedException;

/**
 * Created by sbernard on 10/12/2014.
 */
public final class DbpediaRecognizerPlugin implements RecognizerPlugin {
	private static final String DBPEDIA_LOOKUP_PREFIX = "http://lookup.dbpedia.org/api/search/KeywordSearch";
	private final Proxy proxy;

	@Inject
	public DbpediaRecognizerPlugin(
			final @Named("proxyHost") Optional<String> proxyHost,
			@Named("proxyPort") final Optional<String> proxyPort) {
		Assertion.checkNotNull(proxyHost);
		Assertion.checkNotNull(proxyPort);
		Assertion.checkArgument((proxyHost.isPresent() && proxyPort.isPresent()) || (!proxyHost.isPresent() && !proxyPort.isPresent()),
				"les deux paramètres host et port doivent être tous les deux remplis ou vides");
		//----
		proxy = UrlUtil.buildProxy(proxyHost, proxyPort);
	}

	@Override
	public Set<NamedEntity> recognizeNamedEntities(final List<String> tokens) {
		Assertion.checkNotNull(tokens);
		//----
		final Set<NamedEntity> namedEntities = new HashSet<>();
		for (final String token : tokens) {
			final String urlAsString = DBPEDIA_LOOKUP_PREFIX + "?QueryString=" + UrlUtil.encodeUTF8(token);

			final JSONObject response = UrlUtil.exec(proxy, urlAsString);

			try {
				final JSONArray results = (JSONArray) response.get("results");
				final JSONObject namedEntity = (JSONObject) results.get(0);
				final String label = namedEntity.get("label").toString();
				//				final JSONObject typeObject = ((JSONObject) ((JSONArray) namedEntity.get("classes")).get(0));
				//				final String type = typeObject.get("label").toString();
				//				final String entityUrl = namedEntity.get("url").toString();
				namedEntities.add(new NamedEntity(label/*, type, entityUrl*/));
			} catch (final Exception e) {
				throw WrappedException.wrap(e, "Error characterizing token : " + token);
			}
		}
		return namedEntities;
	}

}
