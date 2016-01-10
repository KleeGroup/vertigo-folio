package io.vertigo.folio.document;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.lang.Manager;

import java.io.File;

/**
 * @author npiedeloup
 * @version $Id: DocumentManager.java,v 1.6 2011/09/13 16:58:32 pchretien Exp $
 */
public interface DocumentManager extends Manager {
	//Stockage des documents par channel
	DocumentStore getDocumentStore(final String storeId);

	//Cr�ation des documents
	//Document createDocument(DocumentVersion documentVersion, MetaDataContainer extractedMdc);

	//Extraction des m�tadonn�es internes,
	//Normalement seul le crawler est d�pendant du type de donn�es : File
	//Ceci devrait-�tre dans le Crawler
	Document createDocument(DocumentVersion documentVersion, File sourceFile);

	//Enrichissement des documents avec des m�tadonn�es externes
	//Voir ChannelManager
	//	Document enhanceDocument(List<DocumentPostProcessor> postProcessors, Document document);
}
