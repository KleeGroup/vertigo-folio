package io.vertigo.folio.impl.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.lang.Plugin;

public interface EnhancementPlugin extends Plugin {
	MetaDataSet extract(final Document document);
}
