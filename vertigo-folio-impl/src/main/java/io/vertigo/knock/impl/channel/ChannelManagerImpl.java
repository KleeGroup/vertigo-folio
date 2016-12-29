package io.vertigo.knock.impl.channel;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import io.vertigo.app.Home;
import io.vertigo.dynamo.kvstore.KVStoreManager;
import io.vertigo.folio.crawler.CrawlerManager;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentBuilder;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.folio.enhancement.EnhancementManager;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.knock.channel.ChannelDefinition;
import io.vertigo.knock.channel.ChannelInfo;
import io.vertigo.knock.channel.ChannelManager;
import io.vertigo.knock.impl.channel.listener.ChannelListener;
import io.vertigo.knock.impl.channel.listener.ChannelListenerImpl;
import io.vertigo.knock.indexation.IndexationManager;
import io.vertigo.util.ListBuilder;

/**
 * Created by sbernard on 04/02/2015.
 */
public final class ChannelManagerImpl implements ChannelManager {
	private static final Logger LOGGER = Logger.getLogger(ChannelManagerImpl.class);
	private final ChannelListener channelListener;

	@Inject
	private CrawlerManager crawlerManager;
	@Inject
	private LifeCyclePlugin lifeCyclePlugin;
	@Inject
	private KVStoreManager kvStoreManager;
	//	@Inject
	//	private UserManager userManager;
	@Inject
	private EnhancementManager enhancementManager;
	@Inject
	private IndexationManager indexationManager;

	public ChannelManagerImpl() {
		channelListener = new ChannelListenerImpl();
	}

	@Override
	public void crawl(final ChannelDefinition channelDefinition) {
		this.doCrawl(channelDefinition);
	}

	@Override
	public void enhance(final ChannelDefinition channelDefinition) {
		this.doEnhance(channelDefinition);
	}

	@Override
	public void index(final ChannelDefinition channelDefinition) {
		doIndex(channelDefinition);
	}

	@Override
	public void crawlAll() {
		for (final ChannelDefinition channelDefinition : ChannelManagerImpl.getAllChannelDefinitions()) {
			crawl(channelDefinition);
		}
	}

	@Override
	public void enhanceAll() {
		for (final ChannelDefinition channelDefinition : ChannelManagerImpl.getAllChannelDefinitions()) {
			enhance(channelDefinition);
		}

	}

	@Override
	public void indexAll() {
		for (final ChannelDefinition channelDefinition : ChannelManagerImpl.getAllChannelDefinitions()) {
			index(channelDefinition);
		}

	}

	@Override
	public void drop() {

		for (final ChannelDefinition channelDefinition : ChannelManagerImpl.getAllChannelDefinitions()) {
			// On vide le kvDataStore
			final List<Document> documents = kvStoreManager.findAll(channelDefinition.getName(), 0, null, Document.class);
			int counter = 0;
			int displayCounter = 0;
			final int threshold = (int) (documents.size() / 10.0);
			for (final Document document : documents) {
				if (counter % threshold == 0) {
					ChannelManagerImpl.LOGGER.info("Dropping datastore for " + channelDefinition.getLabel() + "... " + displayCounter + "% done");
					displayCounter += 10;
				}
				kvStoreManager.remove(channelDefinition.getName(), document.getDocumentVersion().getKey());
				counter++;
			}

			// On vide l'index
			indexationManager.dropIndex(channelDefinition);
		}

	}

	//	@Override
	//	public void updateDocumentUserMetadata(final String sourceId, final String documentKey, final Map<UserMetaData, Object> metadata) {
	//		String channelName = "";
	//		for (final ChannelDefinition channelDefinition : getAllChannelDefinitions()) {
	//			if (channelDefinition.getDataSourceName().equals(sourceId)) {
	//				channelName = channelDefinition.getName();
	//				break;
	//			}
	//		}
	//		final Option<Document> documentOption = kvStoreManager.find(channelName, documentKey, Document.class);
	//		if (documentOption.isDefined()) {
	//			final MetaDataContainerBuilder metaDataContainerBuilder = new MetaDataContainerBuilder();
	//			for (final Entry<UserMetaData, Object> userMetaData : metadata.entrySet()) {
	//				metaDataContainerBuilder.withMetaData(userMetaData.getKey(), userMetaData.getValue());
	//			}
	//			final Document document = new DocumentBuilder(documentOption.get())
	//					.withUserDefinedMetaDataContainer(metaDataContainerBuilder.build())
	//					.build();
	//			indexationManager.pushDocument(document);
	//			final Document indexedDocument = new DocumentBuilder(document)
	//					.withIndexed(true)
	//					.build();
	//			kvStoreManager.put(channelName, indexedDocument.getDocumentVersion().getKey(), indexedDocument);
	//		}
	//	}

	@Override
	public List<ChannelInfo> getChannelInfos() {
		final ListBuilder<ChannelInfo> channelStatisticsListBuilder = new ListBuilder<>();
		for (final ChannelDefinition channelDefinition : ChannelManagerImpl.getAllChannelDefinitions()) {
			final long documentsCount = kvStoreManager.findAll(channelDefinition.getName(), 0, null, Document.class).size();
			channelStatisticsListBuilder.add(new ChannelInfo(channelDefinition, documentsCount));
		}
		return channelStatisticsListBuilder.build();
	}

	//-----------------

	private static Collection<ChannelDefinition> getAllChannelDefinitions() {
		return Home.getApp().getDefinitionSpace().getAll(ChannelDefinition.class);
	}

	private void doCrawl(final ChannelDefinition channelDefinition) {
		crawlerManager
				.crawl(channelDefinition.getDataSourceName())
				.filter(documentVersion -> lifeCyclePlugin.isCrawled(channelDefinition, documentVersion))
				.forEach(documentVersion -> this.doCrawl(channelDefinition, documentVersion));
	}

	private void doCrawl(final ChannelDefinition channelDefinition, final DocumentVersion documentVersion) {
		final long start = System.currentTimeMillis();
		Exception ex = null;
		try {
			final Document document = crawlerManager.readDocument(documentVersion);
			kvStoreManager.put(channelDefinition.getName(), document.getDocumentVersion().getKey(), document);
		} catch (final Exception e) {
			ex = e;
		} finally {
			final long duration = System.currentTimeMillis() - start;
			channelListener.onDocumentCrawled(documentVersion, duration, ex);
		}
	}

	private void doEnhance(final ChannelDefinition channelDefinition) {
		for (final Document documentToEnhance : lifeCyclePlugin.getDocumentsToEnhance(channelDefinition)) {
			this.doEnhance(channelDefinition, documentToEnhance);
		}
	}

	private void doEnhance(final ChannelDefinition channelDefinition, final Document document) {
		final long start = System.currentTimeMillis();
		Exception ex = null;
		try {
			final MetaDataSet enhancedMetaDataSet = enhancementManager.enhanceDocument(document);
			final Document enhancedDocument = new DocumentBuilder(document).withEnhancedMetaDataSet(enhancedMetaDataSet).build();
			kvStoreManager.put(channelDefinition.getName(), enhancedDocument.getDocumentVersion().getKey(), enhancedDocument);
		} catch (final Exception e) {
			ex = e;
		} finally {
			final long duration = System.currentTimeMillis() - start;
			channelListener.onDocumentEnhanced(document, duration, ex);
		}
	}

	private void doIndex(final ChannelDefinition channelDefinition) {
		for (final Document documentToIndex : lifeCyclePlugin.getDocumentsToIndex(channelDefinition)) {
			final long start = System.currentTimeMillis();
			indexationManager.pushDocument(documentToIndex);
			final Document indexedDocument = new DocumentBuilder(documentToIndex)
					.withIndexed(true)
					.build();
			kvStoreManager.put(channelDefinition.getName(), indexedDocument.getDocumentVersion().getKey(), indexedDocument);
			final long duration = System.currentTimeMillis() - start;
			channelListener.onDocumentIndexed(documentToIndex, duration);
		}
	}
}
