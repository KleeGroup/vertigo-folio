package io.vertigo.folio.plugins.metadata.odf;

import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.folio.plugins.metadata.tika.TikaMetaData;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es renvoy�es par l'extracteur textuel bas� sur Tika.
 *
 * @author epaumier
 * @version $Id: ODFMetaData.java,v 1.3 2013/10/22 10:53:28 pchretien Exp $
 */
public enum ODFMetaData implements TikaMetaData {
	/** Contenu textuel du fichier. */
	CONTENT(MetaDataType.STRING),

	/** Title of the document. */
	TITLE(MetaDataType.STRING),

	/** Brief description of the document. */
	DESCRIPTION(MetaDataType.STRING),

	/** Subject of the document. */
	SUBJECT(MetaDataType.STRING),

	/** Keyword pertaining to the document. */
	KEYWORD(MetaDataType.STRING),

	/** Default language of the document [RFC3066]. */
	LANGUAGE(MetaDataType.STRING),

	/** Name of the person who created the document initially. */
	INITIAL_CREATOR(MetaDataType.STRING),

	/** Name of the person who last modified the document (last = primarily responsible in ODF). */
	CREATOR(MetaDataType.STRING),

	/** Date and time when the document was created. */
	CREATION_DATE(MetaDataType.DATE),

	/** Date and time when the document was last modified. */
	DATE(MetaDataType.DATE),

	/** Total time spent editing the document. */
	EDITING_DURATION(MetaDataType.STRING), //MetaDataType.DURATION),

	/** Number of editing cycles the document has been through. */
	EDITING_CYCLES(MetaDataType.INTEGER),

	/** Element contains a string that identifies the application or tool that was used to create or last modify the XML document. */
	GENERATOR(MetaDataType.STRING),

	/** Nombre de tableaux dans le document. */
	TABLE_COUNT(MetaDataType.INTEGER),

	/** Nombre d'objets dans le document. */
	OBJECT_COUNT(MetaDataType.INTEGER),

	/** Nombre d'images dans le document. */
	IMAGE_COUNT(MetaDataType.INTEGER),

	/** Nombre de pages dans le document. */
	PAGE_COUNT(MetaDataType.INTEGER),

	/** Nombre de paragraphes dans le document. */
	PARAGRAPH_COUNT(MetaDataType.INTEGER),

	/** Nombre de mots dans le document. */
	WORD_COUNT(MetaDataType.INTEGER),

	/** Nombre de caract�res dans le document. */
	CHARACTER_COUNT(MetaDataType.INTEGER);

	//-----
	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private ODFMetaData(final MetaDataType metaDataType) {
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
