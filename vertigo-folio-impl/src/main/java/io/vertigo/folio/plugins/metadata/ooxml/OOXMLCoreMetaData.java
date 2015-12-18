package io.vertigo.folio.plugins.metadata.ooxml;

import static io.vertigo.folio.metadata.MetaDataType.DATE;
import static io.vertigo.folio.metadata.MetaDataType.STRING;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es pour les fichiers Office Open XML (docx, pptx, xlsx, ...).
 *
 * @author epaumier
 * @version $Id: OOXMLCoreMetaData.java,v 1.3 2013/10/22 12:12:20 pchretien Exp $
 */
public enum OOXMLCoreMetaData implements OOXMLMetaData {
	/** A categorization of the content of this package. */
	CATEGORY(STRING),

	/** The type of content represented, generally defined by a specific use and intended audience. */
	CONTENT_TYPE(STRING),

	/** The status of the content. */
	CONTENT_STATUS(STRING),

	/** Date of creation of the resource. */
	CREATED(DATE),

	/** An entity primarily responsible for making the content of the resource. */
	CREATOR(STRING),

	/** An explanation of the content of the resource. */
	DESCRIPTION(STRING),

	/** An unambiguous reference to the resource within a given context. */
	IDENTIFIER(STRING),

	/** A delimited set of keywords to support searching and indexing.
	 * This is typically a list of terms that are not available elsewhere in the properties. */
	KEYWORDS(STRING),

	/** The language of the intellectual content of the resource. */
	LANGUAGE(STRING),

	/** The user who performed the last modification. The identification is environment-specific.
	 * It is recommended that this value be as concise as possible. */
	LAST_MODIFIED_BY(STRING),

	/** The date and time of the last printing. */
	LAST_PRINTED(DATE),

	/** Date on which the resource was changed. */
	MODIFIED(DATE),

	/** The revision number. */
	REVISION_NUMBER(STRING),

	/** The topic of the content of the resource. */
	SUBJECT(STRING),

	/** The name given to the resource. */
	TITLE(STRING),

	/** The version number. This value is set by the user or by the application. */
	VERSION(STRING);

	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private OOXMLCoreMetaData(final MetaDataType metaDataType) {
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
