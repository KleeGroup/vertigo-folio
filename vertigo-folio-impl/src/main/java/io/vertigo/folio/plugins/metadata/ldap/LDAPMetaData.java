package io.vertigo.folio.plugins.metadata.ldap;

import static io.vertigo.folio.metadata.MetaDataType.STRING;
import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Created by sbernard on 23/03/2015.
 */
public enum LDAPMetaData implements MetaData {
	NAME(STRING),
	SAMACCOUNTNAME(STRING),
	EMAIL(STRING),
	COMPANY(STRING),
	DEPARTMENT(STRING),
	FIRSTNAME(STRING),
	OFFICE(STRING),
	PHONE(STRING),
	TITLE(STRING),
	MANAGER_URL(STRING),
	THUMBNAIL(STRING);

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
