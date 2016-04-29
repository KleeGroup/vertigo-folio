package io.vertigo.knock.impl.channel.listener;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;

/**
 * Created by sbernard on 12/02/2015.
 */
public interface ChannelListener {
	// Exception may be null if there is no error.
	void onDocumentCrawled(final DocumentVersion documentVersion, final long duration, final Exception e);

	void onDocumentEnhanced(final Document document, final long duration, final Exception exception);

	void onDocumentIndexed(final Document document, final long duration);
}
