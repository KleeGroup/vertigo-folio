package io.vertigo.folio.impl.metadata;

import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Liste des m�tadonn�es pour les Fichiers.
 *
 * @author pchretien
 * @version $Id: FileInfoMetaData.java,v 1.3 2013/10/22 12:08:39 pchretien Exp $
 */
public enum FileInfoMetaData implements MetaData {
	/** Taille du fichier. */
	SIZE(MetaDataType.LONG),

	/** Extension du fichier. */
	FILE_EXTENSION(MetaDataType.STRING),

	/** Nom du fichier. */
	FILE_NAME(MetaDataType.STRING),

	/** Date de modification du fichier. */
	LAST_MODIFIED(MetaDataType.DATE);

	//-----
	private final MetaDataType metaDataType;

	/**
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	Type de la m�tadonn�e
	 */
	private FileInfoMetaData(final MetaDataType metaDataType) {
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
