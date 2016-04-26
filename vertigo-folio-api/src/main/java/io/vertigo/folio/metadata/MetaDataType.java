package io.vertigo.folio.metadata;

import io.vertigo.lang.Assertion;

import java.util.Date;

/**
 * Type of metadata.
 *
 * @author  pchretien
 * @version $Id: MetaDataType.java,v 1.3 2013/10/22 10:58:44 pchretien Exp $
 */
public enum MetaDataType {
	/** text. */
	STRING(String.class),

	/** Integer. */
	INTEGER(Integer.class),

	/** Long. */
	LONG(Long.class),

	/** Date. */
	DATE(Date.class),

	/** Boolean */
	BOOLEAN(Boolean.class);
	//-----
	private final Class<?> javaClass;

	/**
	 * Constructor.
	 * @param javaClass	the java type of the metadata
	 */
	MetaDataType(final Class<?> javaClass) {
		Assertion.checkNotNull(javaClass);
		//-----
		this.javaClass = javaClass;
	}

	/**
	 * @return the type of the metadata
	 */
	public Class<?> getJavaClass() {
		return javaClass;
	}

	/**
	 * Checks if a value has the right type.
	 * @param metaDataValue	value
	 */
	public void checkValue(final Object metaDataValue) {
		//By convention null value is valid
		//Si une valeur est non null on v�rifie que son type est correct.
		if (metaDataValue != null && !javaClass.isInstance(metaDataValue)) {
			throw new IllegalStateException("La valeur assignée doit être d'un type compatible avec le type de la métadonnée");
		}
	}
}
