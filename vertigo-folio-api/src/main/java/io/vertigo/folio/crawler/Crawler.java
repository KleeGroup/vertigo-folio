package io.vertigo.folio.crawler;

import io.vertigo.folio.document.model.DocumentVersion;

/**
 * Plugin de gestion d'une datasource particuli�re.
 * Fournit les services de :
 * - parcours de la source.
 * - lecture d'un document avec extraction de ses m�ta-donn�es
 * - d�finition de la base d'url de download
 * @author npiedeloup
 * @version $Id: Crawler.java,v 1.1 2011/08/02 14:36:12 pchretien Exp $
 */
public interface Crawler {
	/**
	 * @param startAtUrl url de d�part
	 * @return Iterator de crawling de documentVersion
	 */
	Iterable<DocumentVersion> crawl(final String startAtUrl);

	//	/**
	//	 * TODO : ceci est de la conf de dataSource, ne devrait pas �tre dans le plugin.
	//	 * @return Base de l'url de download
	//	 */
	//	String getBaseDownloadUrl();
}
