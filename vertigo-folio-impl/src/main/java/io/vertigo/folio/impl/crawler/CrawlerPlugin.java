package io.vertigo.folio.impl.crawler;

import java.util.stream.Stream;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.lang.Plugin;

/**
 * Plugin de gestion d'une datasource particulière identifiée par un iD.
	 * Fournit les services de :
	 * - parcours de la dataSsource.
	 * - lecture d'un document avec extraction de ses metadatas
 * @author pchretien
 * @version $Id: CrawlerPlugin.java,v 1.6 2014/02/17 17:55:57 npiedeloup Exp $
 */
public interface CrawlerPlugin extends Plugin {
	/**
	 * @return Iterator de crawling de documentVersion
	 */
	Stream<DocumentVersion> crawl();

	/**
	 * Lit le document avec ses m�ta-donn�es.
	 * @param documentVersion Identifiant de la version du document
	 * @return Document lu
	 */
	Document readDocument(final DocumentVersion documentVersion);

	/**
	 * @return Id de la datasource
	 */
	String getDataSourceId();
}
