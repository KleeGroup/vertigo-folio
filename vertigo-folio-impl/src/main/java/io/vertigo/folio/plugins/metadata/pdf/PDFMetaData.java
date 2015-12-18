package io.vertigo.folio.plugins.metadata.pdf;

import static io.vertigo.folio.metadata.MetaDataType.STRING;
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
	AUTHOR(STRING),

	/** Mots cl�s. */
	KEYWORDS(STRING),

	/** Titre du document. */
	TITLE(STRING),

	/** Sujet du document. */
	SUBJECT(STRING),

	/** Logiciel ayant g�n�r� ce PDF. */
	PRODUCER(STRING),

	/** Contenu du document. */
	CONTENT(STRING),

	/** Compatibilit� PDF/A-1b. ("true" ou "false") */
	PDFA(STRING),

	/** Compatibilit� PDF/A-1b. ("VALID" ou "INVALID : ${causes}") */
	PDFA_VALIDATION_MSG(STRING),

	THUMBNAIL_PAGE_1(STRING),
	THUMBNAIL_PAGE_2(STRING),
	THUMBNAIL_PAGE_3(STRING),
	THUMBNAIL_PAGE_4(STRING);

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
