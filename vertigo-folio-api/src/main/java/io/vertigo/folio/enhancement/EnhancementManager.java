package io.vertigo.folio.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.lang.Manager;

/**
 * Created by sbernard on 28/05/2015.
 */
public interface EnhancementManager extends Manager {
	Document enhanceDocument(final Document documentToEnhance) throws Exception;
}
