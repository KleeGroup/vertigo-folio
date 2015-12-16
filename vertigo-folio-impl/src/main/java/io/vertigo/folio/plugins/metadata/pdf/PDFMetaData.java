package io.vertigo.folio.plugins.metadata.pdf;

import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es pour les PDF.
 *
 * @author pchretien
 * @version $Id: PDFMetaData.java,v 1.3 2013/10/22 10:52:26 pchretien Exp $
 */
public enum PDFMetaData implements MetaData {
	/** Auteur. */
	AUTHOR(MetaDataType.STRING),

	/** Mots cl�s. */
	KEYWORDS(MetaDataType.STRING),

	/** Titre du document. */
	TITLE(MetaDataType.STRING),

	/** Sujet du document. */
	SUBJECT(MetaDataType.STRING),

	/** Logiciel ayant g�n�r� ce PDF. */
	PRODUCER(MetaDataType.STRING),

	/** Contenu du document. */
	CONTENT(MetaDataType.STRING),

	/** Compatibilit� PDF/A-1b. ("true" ou "false") */
	PDFA(MetaDataType.STRING),

	/** Compatibilit� PDF/A-1b. ("VALID" ou "INVALID : ${causes}") */
	PDFA_VALIDATION_MSG(MetaDataType.STRING),

	THUMBNAIL_PAGE_1(MetaDataType.STRING),
	THUMBNAIL_PAGE_2(MetaDataType.STRING),
	THUMBNAIL_PAGE_3(MetaDataType.STRING),
	THUMBNAIL_PAGE_4(MetaDataType.STRING);

	//-----
	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private PDFMetaData(final MetaDataType metaDataType) {
		Assertion.checkNotNull(metaDataType);
		//-----
		this.metaDataType = metaDataType;
	}

	/** {@inheritDoc} */
	@Override
	public MetaDataType getType() {
		return metaDataType;
	}
}
