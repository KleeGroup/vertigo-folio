package io.vertigo.folio.plugins.metadata.ooxml;

import static io.vertigo.folio.metadata.MetaDataType.INTEGER;
import static io.vertigo.folio.metadata.MetaDataType.STRING;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es pour les fichiers Office Open XML (docx, pptx, xlsx, ...).
 *
 * @author epaumier
 * @version $Id: OOXMLExtendedMetaData.java,v 1.3 2013/10/22 12:12:20 pchretien Exp $
 */
public enum OOXMLExtendedMetaData implements OOXMLMetaData {
	/** Name of the application that created this document. */
	APPLICATION(STRING),

	/** Version of the application which produced this document. */
	APP_VERSION(STRING),

	/** Total number of characters in a document. */
	CHARACTERS(INTEGER),

	/** Last count of the number of characters (including spaces) in this document. */
	CHARACTERS_WITH_SPACES(INTEGER),

	/** Name of a company associated with the document. */
	COMPANY(STRING),

	/** Number of hidden slides in a presentation document. */
	HIDDEN_SLIDES(INTEGER),

	/** Total number of lines in a document when last saved by a conforming producer if applicable. */
	LINES(INTEGER),

	/** Name of a supervisor associated with the document. */
	MANAGER(STRING),

	/** Total number of sound or video clips that are present in the document. */
	MMCLIPS(INTEGER),

	/** Number of slides in a presentation containing notes. */
	NOTES(INTEGER),

	/** Total number of pages of a document if applicable. */
	PAGES(INTEGER),

	/** Total number of paragraphs found in a document if applicable. */
	PARAGRAPHS(INTEGER),

	/** Intended format for a presentation document.
	 * For example, a presentation intended to be shown on video will have PresentationFormat "Video". */
	PRESENTATION_FORMAT(STRING),

	/** Total number of slides in a presentation document. */
	SLIDES(INTEGER),

	/** Name of an external document template containing format and style information used to create the current document. */
	TEMPLATE(STRING),

	/** Total time that a document has been edited.
	 * The default time unit is minutes. */
	TOTAL_TIME(INTEGER),

	/** Total number of words contained in a document when last saved. */
	WORDS(INTEGER);

	//-----
	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private OOXMLExtendedMetaData(final MetaDataType metaDataType) {
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
