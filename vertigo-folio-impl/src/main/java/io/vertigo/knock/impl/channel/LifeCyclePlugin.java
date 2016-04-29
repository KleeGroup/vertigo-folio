package io.vertigo.knock.impl.channel;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.knock.channel.ChannelDefinition;
import io.vertigo.lang.Plugin;

import java.util.List;

/**
 * Created by sbernard on 25/02/2015.
 */
public interface LifeCyclePlugin extends Plugin {
	boolean isCrawled(final ChannelDefinition channelDefinition, final DocumentVersion documentVersion);

	List<Document> getDocumentsToEnhance(final ChannelDefinition channelDefinition);

	List<Document> getDocumentsToIndex(final ChannelDefinition channelDefinition);

	void resetIndexation(final ChannelDefinition channelDefinition);
}
