package io.vertigo.knock.plugins.channel.lifecycle;

import io.vertigo.dynamo.kvstore.KVStoreManager;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentBuilder;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.knock.channel.ChannelDefinition;
import io.vertigo.knock.impl.channel.LifeCyclePlugin;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Option;
import io.vertigo.util.ListBuilder;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by sbernard on 25/02/2015.
 */
public class KVDataStoreLifeCyclePlugin implements LifeCyclePlugin {
	private final KVStoreManager kvStoreManager;

	@Inject
	public KVDataStoreLifeCyclePlugin(final KVStoreManager kvStoreManager) {
		Assertion.checkNotNull(kvStoreManager);
		//-----
		this.kvStoreManager = kvStoreManager;
	}

	@Override
	public boolean isCrawled(final ChannelDefinition channelDefinition, final DocumentVersion documentVersion) {
		final Option<Document> documentOption = kvStoreManager.find(channelDefinition.getName(), documentVersion.getKey(), Document.class);
		if (documentOption.isPresent()) {
			return documentVersion.getLastModified().equals(documentOption.get().getDocumentVersion().getLastModified());
		}
		return false;
	}

	@Override
	public List<Document> getDocumentsToEnhance(final ChannelDefinition channelDefinition) {
		final ListBuilder<Document> documentsToEnhanceListBuilder = new ListBuilder<>();
		for (final Document document : kvStoreManager.findAll(channelDefinition.getName(), 0, null, Document.class)) {
			if (!document.getDocumentStatus().isEnhanced()) {
				documentsToEnhanceListBuilder.add(document);
			}
		}
		return documentsToEnhanceListBuilder.build();
	}

	@Override
	public List<Document> getDocumentsToIndex(final ChannelDefinition channelDefinition) {
		final ListBuilder<Document> documentsToIndexListBuilder = new ListBuilder<>();
		for (final Document document : kvStoreManager.findAll(channelDefinition.getName(), 0, null, Document.class)) {
			if (!document.getDocumentStatus().isIndexed() || document.getDocumentStatus().isDirty()) {
				documentsToIndexListBuilder.add(document);
			}
		}
		return documentsToIndexListBuilder.build();
	}

	@Override
	public void resetIndexation(final ChannelDefinition channelDefinition) {
		for (final Document document : kvStoreManager.findAll(channelDefinition.getName(), 0, null, Document.class)) {
			final Document newDocument = new DocumentBuilder(document)
					.withIndexed(false)
					.build();
			kvStoreManager.put(channelDefinition.getName(), newDocument.getDocumentVersion().getKey(), newDocument);
		}
	}
}
