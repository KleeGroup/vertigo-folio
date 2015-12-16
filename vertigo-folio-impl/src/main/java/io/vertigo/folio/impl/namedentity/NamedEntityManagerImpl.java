package io.vertigo.folio.impl.namedentity;

import io.vertigo.folio.namedentity.NamedEntity;
import io.vertigo.folio.namedentity.NamedEntityManager;
import io.vertigo.lang.Assertion;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public final class NamedEntityManagerImpl implements NamedEntityManager {
	private final TokenizerPlugin tokenizerPlugin;
	private final RecognizerPlugin characterizationPlugin;

	@Inject
	public NamedEntityManagerImpl(final TokenizerPlugin tokenizerPlugin, final RecognizerPlugin characterizationPlugin) {
		Assertion.checkNotNull(tokenizerPlugin);
		Assertion.checkNotNull(characterizationPlugin);
		//----
		this.tokenizerPlugin = tokenizerPlugin;
		this.characterizationPlugin = characterizationPlugin;
	}

	@Override
	public Set<NamedEntity> extractNamedEntities(final String text) {
		Assertion.checkNotNull(text);
		//----
		final List<String> tokens = tokenizerPlugin.tokenize(text);
		return characterizationPlugin.recognizeNamedEntities(tokens);
	}

}
