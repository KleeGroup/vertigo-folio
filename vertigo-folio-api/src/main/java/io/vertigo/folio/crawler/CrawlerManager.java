package io.vertigo.folio.crawler;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.lang.Component;

/**
 * Gestionnaires des crawler de DataSource.
 * Ce manager g�re les diff�rentes sources de donn�es et utilise un plugin adapat� leurs sp�cificit�es techniques.
 *
 * @author npiedeloup
 * @version $Id: CrawlerManager.java,v 1.5 2011/08/02 14:36:12 pchretien Exp $
 */
public interface CrawlerManager extends Component {
	/**
	 * Crawl une dataSource.
	 * @param dataSourceId Identifiant de la dataSource � parcourir
	 */
	Crawler getCrawler(final String dataSourceId);

	/**
	 * Lit le document avec ses m�ta-donn�es.
	 * @param documentVersion Identifiant de la version du document
	 * @return Document lu
	 */
	Document readDocument(final DocumentVersion documentVersion);
}
