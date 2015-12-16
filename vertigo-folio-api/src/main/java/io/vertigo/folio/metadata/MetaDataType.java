package io.vertigo.folio.metadata;

import io.vertigo.lang.Assertion;

import java.util.Date;

/**
 * Type des m�tadonn�es.
 *
 * @author  pchretien
 * @version $Id: MetaDataType.java,v 1.3 2013/10/22 10:58:44 pchretien Exp $
 */
public enum MetaDataType {
	/** M�tadonn�e textuelle. */
	STRING(String.class),

	/** Entier. */
	INTEGER(Integer.class),

	/** Entier Long. */
	LONG(Long.class),

	/** M�tadonn�e repr�sentant une date et heure simple. */
	DATE(Date.class),

	BOOLEAN(Boolean.class);

	/** Date ou masque de date; */
	//CALENDAR(Calendar.class);

	/** Dur�e. Stock�e en Millisecondes. */
	//DURATION(Long.class),

	/** Type de m�tadonn�e g�n�rique stock�e sous forme de String. */
	//UNKNOWN(String.class);

	//-----
	private final Class<?> javaClass;

	/**
	 * Cr�e un type de m�tadonn�e, en l'associant � une classe Java.
	 * @param javaClass	Classe Java associ�e
	 */
	MetaDataType(final Class<?> javaClass) {
		Assertion.checkNotNull(javaClass);
		//-----
		this.javaClass = javaClass;
	}

	/**
	 * @return Classe java encapsul�/wrapp�e par le type
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
