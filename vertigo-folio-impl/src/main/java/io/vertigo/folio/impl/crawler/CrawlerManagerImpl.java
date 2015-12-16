package io.vertigo.folio.impl.crawler;

import io.vertigo.folio.crawler.CrawlerManager;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.lang.Assertion;

import java.util.List;

import javax.inject.Inject;

/**
 * Impl�mentation standard du Manager de crawling de document.
 * Ce manager g�re les diff�rentes sources de donn�es, et utilise un plugin adapat� leurs sp�cificit�es techniques.
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

	/** {@inheritDoc} */
	@Override
	public CrawlerPlugin getCrawler(final String dataSourceId) {
		for (final CrawlerPlugin crawler : crawlerPlugins) {
			if (crawler.accept(dataSourceId)) {
				return crawler;
			}
		}
		throw new RuntimeException("Pas de crawler pour la dataSource " + dataSourceId);
	}
}
