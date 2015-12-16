package io.vertigo.folio.plugins.metadata.ldap;

import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Created by sbernard on 23/03/2015.
 */
public enum LDAPMetaData implements MetaData {
	NAME(MetaDataType.STRING),
	SAMACCOUNTNAME(MetaDataType.STRING),
	EMAIL(MetaDataType.STRING),
	COMPANY(MetaDataType.STRING),
	DEPARTMENT(MetaDataType.STRING),
	FIRSTNAME(MetaDataType.STRING),
	OFFICE(MetaDataType.STRING),
	PHONE(MetaDataType.STRING),
	TITLE(MetaDataType.STRING),
	MANAGER_URL(MetaDataType.STRING),
	THUMBNAIL(MetaDataType.STRING);

	//-----
	private final MetaDataType metaDataType;

	/** Constructeur.
	 * Initialise la m�tadonn�e en lui donnant un type
	 * @param metaDataType	metaDataType de la m�tadonn�e
	 */
	private LDAPMetaData(final MetaDataType metaDataType) {
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
