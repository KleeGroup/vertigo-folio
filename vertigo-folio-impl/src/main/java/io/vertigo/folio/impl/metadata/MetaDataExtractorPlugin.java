package io.vertigo.folio.impl.metadata;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.lang.Plugin;

/**
 * Cr�ation de la liste des couples (m�tadonn�e, valeurs) partir d'un document physique.
 *
 * @author pchretien
 * @version $Id: MetaDataExtractorPlugin.java,v 1.3 2014/01/28 18:49:34 pchretien Exp $
 */
public interface MetaDataExtractorPlugin extends Plugin {
	/**
	 * R�cup�ration des m�tadon�es d'une ressource.
	 * @param file Ressource � indexer
	 * @return M�tadonn�es.
	 * @throws Exception Erreur
	 */
	MetaDataContainer extractMetaData(VFile file) throws Exception;

	/**
	 * Pr�cise quels sont les fichiers accept�s par cet extracteur.
	 * @return Si le type mime est accept� par l'extracteur
	 */
	boolean accept(VFile file);
}
