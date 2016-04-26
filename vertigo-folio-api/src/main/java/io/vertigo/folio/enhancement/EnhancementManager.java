package io.vertigo.folio.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.lang.Manager;

/**
 * Created by sbernard on 28/05/2015.
 */
public interface EnhancementManager extends Manager {
	MetaDataSet enhanceDocument(final Document document) throws Exception;
}
