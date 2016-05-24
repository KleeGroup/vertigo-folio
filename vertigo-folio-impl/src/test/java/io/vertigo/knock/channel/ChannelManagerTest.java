package io.vertigo.knock.channel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;

import io.vertigo.AbstractTestCaseJU4;
import io.vertigo.folio.document.DocumentStore;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.folio.impl.enhancement.EnhancementPlugin;

/**
 * Test de l'impl�mentation standard.
 *
 * @author pchretien
 * @version $Id: MetaDataManagerTest.java,v 1.5 2014/07/16 13:26:33 pchretien Exp $
 */
public final class ChannelManagerTest extends AbstractTestCaseJU4 {
	@Inject
	private ChannelManager channelManager;
	//	@Inject
	//	private CrawlerManager crawlerManager;

	@Test
	public void testDiskD() {
		final EnhancementPlugin enhancementPlugin = new MockPostProcessorPlugin();
		final DocumentStore documentStore = new DocumentStore() {
			private final Map<DocumentVersion, Document> map = new HashMap<>();

			@Override
			public Iterator<Document> iterator() {
				return map.values().iterator();
			}

			@Override
			public long size() {
				return map.size();
			}

			@Override
			public boolean contains(final DocumentVersion documentVersion) {
				return map.containsKey(documentVersion);
			}

			@Override
			public void add(final Document document) {
				//				System.out.println("add doc[" + map.size() + "]: " + document.getDocumentVersion().getUrl());
				map.put(document.getDocumentVersion(), document);
			}
		};
		final ChannelDefinition channelDefinition = new ChannelDefinition(
				"CHN_MOCK",
				"test data channel",
				"d:/",
				/*	Collections.singletonList(enhancementPlugin),*/
				"");
		//-------
		channelManager.crawl(channelDefinition);
		channelManager.enhance(channelDefinition);
	}
}
