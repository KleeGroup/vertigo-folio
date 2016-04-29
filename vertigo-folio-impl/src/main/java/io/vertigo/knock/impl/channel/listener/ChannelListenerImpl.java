package io.vertigo.knock.impl.channel.listener;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.knock.channel.ChannelManager;

import org.apache.log4j.Logger;

/**
 * Created by sbernard on 13/02/2015.
 */
public final class ChannelListenerImpl implements ChannelListener {
	private static final Logger LOGGER = Logger.getLogger(ChannelManager.class);

	@Override
	public void onDocumentCrawled(final DocumentVersion documentVersion, final long duration, final Exception e) {
		if (e != null) {
			LOGGER.debug("CRAWLING FAILED " + documentVersion.getKey() + " : " + e.getLocalizedMessage());
		} else {
			LOGGER.debug("CRAWLED " + documentVersion.getKey() + " in " + duration + "ms");
		}
	}

	@Override
	public void onDocumentEnhanced(final Document document, final long duration, final Exception exception) {
		if (exception != null) {
			LOGGER.debug("ENHANCEMENT FAILED " + document.getDocumentVersion().getKey() + " : " + exception.getLocalizedMessage());
		} else {
			LOGGER.debug("ENHANCED " + document.getDocumentVersion().getKey() + " in " + duration + "ms");
		}
	}

	@Override
	public void onDocumentIndexed(final Document document, final long duration) {
		LOGGER.debug("INDEXED " + document.getName() + " in " + duration + "ms");
	}
}
