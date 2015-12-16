package io.vertigo.folio.impl.namedentity;

import io.vertigo.lang.Plugin;

import java.util.List;

public interface TokenizerPlugin extends Plugin {
	List<String> tokenize(final String text);
}
