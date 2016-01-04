package io.vertigo.folio.plugins.metadata.ooxml;

import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * List of metadata for Office Open XML files (docx, pptx, xlsx, ...).
 *
 * @author epaumier
 * @version $Id: OOXMLOthersMetaData.java,v 1.3 2013/10/22 12:12:20 pchretien Exp $
 */
public enum OOXMLOthersMetaData implements OOXMLMetaData {
	/** Contenu textuel du fichier. */
	CONTENT(MetaDataType.STRING);

	//-----
	private final MetaDataType metaDataType;

	/**
	 * Constructor.
	 * @param metaDataType the type of the metadata
	 */
	private OOXMLOthersMetaData(final MetaDataType metaDataType) {
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
