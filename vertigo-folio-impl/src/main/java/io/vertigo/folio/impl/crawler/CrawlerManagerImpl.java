package io.vertigo.folio.impl.crawler;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.folio.crawler.CrawlerManager;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.lang.Assertion;

/**
 * This class is the standard impl of the CrawlerManager.
 * Many dataSources can be managed.
 * Each dataSource is defined by an Id.
 *
 * @author npiedeloup
 * @version $Id: CrawlerManagerImpl.java,v 1.11 2014/02/17 17:55:57 npiedeloup Exp $
 */
public final class CrawlerManagerImpl implements CrawlerManager {
	private final List<CrawlerPlugin> crawlerPlugins;

	@Inject
	public CrawlerManagerImpl(final List<CrawlerPlugin> crawlerPlugins) {
		Assertion.checkNotNull(crawlerPlugins);
		//-----
		this.crawlerPlugins = crawlerPlugins;
	}

	/** {@inheritDoc} */
	@Override
	public Document readDocument(final DocumentVersion documentVersion) {
		return getCrawler(documentVersion.getDataSourceId()).readDocument(documentVersion);
	}

	private CrawlerPlugin getCrawler(final String dataSourceId) {
		return crawlerPlugins
				.stream()
				.filter(crawlerPlugin -> crawlerPlugin.getDataSourceId().equals(dataSourceId))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Pas de crawler pour la dataSource " + dataSourceId));
	}

	@Override
	public Iterable<DocumentVersion> crawl(final String dataSourceId) {
		Assertion.checkArgNotEmpty("dataSourceId");
		//-----
		return getCrawler(dataSourceId).crawl();
	}
}
