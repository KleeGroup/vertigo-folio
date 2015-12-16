package io.vertigo.folio.impl.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.lang.Plugin;

public interface EnhancementPlugin extends Plugin {
	MetaDataContainer extract(final Document document) throws Exception;
}
