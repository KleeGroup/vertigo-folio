package io.vertigo.folio.plugins.metadata.txt;

import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es pour les fichiers textes.
 *
 * @author pchretien
 * @version $Id: TxtMetaData.java,v 1.3 2013/10/22 10:52:06 pchretien Exp $
 */
public enum TxtMetaData implements MetaData {
	/** Contenu textuel. */
	CONTENT(MetaDataType.STRING),
	MARKDOWN_CONTENT(MetaDataType.STRING);

	//-----
	private final MetaDataType metaDataType;

	/**
	 * Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private TxtMetaData(final MetaDataType metaDataType) {
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
