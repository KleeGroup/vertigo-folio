package io.vertigo.folio.impl.namedentity;

import io.vertigo.folio.namedentity.NamedEntity;
import io.vertigo.folio.namedentity.NamedEntityManager;
import io.vertigo.lang.Assertion;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public final class NamedEntityManagerImpl implements NamedEntityManager {
	private final TokenizerPlugin tokenizerPlugin;
	private final RecognizerPlugin recognizerPlugin;

	@Inject
	public NamedEntityManagerImpl(final TokenizerPlugin tokenizerPlugin, final RecognizerPlugin recognizerPlugin) {
		Assertion.checkNotNull(tokenizerPlugin);
		Assertion.checkNotNull(recognizerPlugin);
		//----
		this.tokenizerPlugin = tokenizerPlugin;
		this.recognizerPlugin = recognizerPlugin;
	}

	@Override
	public Set<NamedEntity> extractNamedEntities(final String text) {
		Assertion.checkNotNull(text);
		//----
		final List<String> tokens = tokenizerPlugin.tokenize(text);
		return recognizerPlugin.recognizeNamedEntities(tokens);
	}

}
