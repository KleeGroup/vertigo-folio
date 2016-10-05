package io.vertigo.folio.crawler;

import javax.inject.Inject;

import org.junit.Test;

import io.vertigo.AbstractTestCaseJU4;

/**
 * Test de l'impl�mentation standard.
 *
 * @author pchretien
 * @version $Id: MetaDataManagerTest.java,v 1.5 2014/07/16 13:26:33 pchretien Exp $
 */
public final class CrawlerManagerTest extends AbstractTestCaseJU4 {
	@Inject
	private CrawlerManager crawlerManager;

	@Test
	public void testDiskC() {
		crawlerManager.crawl("myFS")
				.limit(1000)
				.forEach(documentVersion -> System.out.println("doc : " + documentVersion.getUrl()));
	}
}
