package io.vertigo.folio.plugins.metadata.tika;

import static io.vertigo.folio.metadata.MetaDataType.DATE;
import static io.vertigo.folio.metadata.MetaDataType.INTEGER;
import static io.vertigo.folio.metadata.MetaDataType.STRING;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es renvoy�es par l'extracteur textuel bas� sur Tika.
 *
 * @author pchretien
 * @version $Id: AutoTikaMetaData.java,v 1.3 2013/10/22 10:52:16 pchretien Exp $
 */
public enum AutoTikaMetaData implements TikaMetaData {
	/** Contenu textuel du fichier. */
	CONTENT(STRING),

	/** Title of the document. */
	TITLE(STRING),

	/** Brief description of the document. */
	DESCRIPTION(STRING),

	/** Subject of the document. */
	SUBJECT(STRING),

	/** Keyword pertaining to the document. */
	KEYWORD(STRING),

	/** Default language of the document [RFC3066]. */
	LANGUAGE(STRING),

	/** Name of the person who created the document initially. */
	INITIAL_CREATOR(STRING),

	/** Name of the person who last modified the document (last = primarily responsible in ODF). */
	CREATOR(STRING),

	/** Date and time when the document was created. */
	CREATION_DATE(DATE),

	/** Date and time when the document was last modified. */
	MODIFICATION_DATE(DATE),

	/** Element contains a string that identifies the application or tool that was used to create or last modify the XML document. */
	GENERATOR(STRING),

	/** Nombre de pages dans le document. */
	PAGE_COUNT(INTEGER),

	/** Nombre de mots dans le document. */
	WORD_COUNT(INTEGER),

	/** Nombre de caract�res dans le document. */
	CHARACTER_COUNT(INTEGER);

	//-------------------------------------------------------------------------
	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private AutoTikaMetaData(final MetaDataType metaDataType) {
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
