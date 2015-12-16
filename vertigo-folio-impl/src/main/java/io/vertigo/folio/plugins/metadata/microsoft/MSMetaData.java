package io.vertigo.folio.plugins.metadata.microsoft;

import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es pour les fichiers Microsoft.
 *
 * @author pchretien
 * @version $Id: MSMetaData.java,v 1.3 2013/10/22 10:52:34 pchretien Exp $
 */
public enum MSMetaData implements MetaData {
	/** Auteur du document. */
	AUTHOR(MetaDataType.STRING),

	/** Titre du document. */
	TITLE(MetaDataType.STRING),

	/** Sujet du document. */
	SUBJECT(MetaDataType.STRING),

	/** Mots cl�s du document. */
	KEYWORDS(MetaDataType.STRING),

	/** Commentaires inclus dans le document. */
	COMMENTS(MetaDataType.STRING),

	/** Contenu textuel du document. */
	CONTENT(MetaDataType.STRING);

	//-------------------------------------------------------------------------
	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private MSMetaData(final MetaDataType metaDataType) {
		Assertion.checkNotNull(metaDataType);
		//---------------------------------------------------------
		this.metaDataType = metaDataType;
	}

	/** {@inheritDoc} */
	@Override
	public MetaDataType getType() {
		return metaDataType;
	}
}
