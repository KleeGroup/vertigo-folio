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
	 * Teste si une m�tadonn�e a une valeur compatible avec le type.
	 * @param metaDataValue	M�tadonn�e dont on veut tester la valeur
	 */
	public void checkValue(final Object metaDataValue) {
		//Par convention toute valeur null est valide.
		//Si une valeur est non null on v�rifie que son type est correct.
		if (metaDataValue != null && !javaClass.isInstance(metaDataValue)) {
			throw new IllegalStateException("La valeur assign�e doit �tre d'un type compatible avec le type de la m�tadonn�e");
		}
	}
}
