package io.vertigo.folio.metadata;

import io.vertigo.lang.Assertion;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Conteneur de m�ta-donn�es.
 * Un conteneur est non modifiable (ValueObject).
 * Il est n�cessaire de passer par le builder associ�.
 *
 * @author pchretien
 * @version $Id: MetaDataContainer.java,v 1.3 2013/10/22 10:58:44 pchretien Exp $
 */
public final class MetaDataContainer implements Serializable {
	public static final MetaDataContainer EMPTY_META_DATA_CONTAINER = new MetaDataContainer();

	/**Identifiant de s�rialisation. */
	private static final long serialVersionUID = 8518904186883185597L;

	private final Map<MetaData, Object> metadatas;

	/**
	 * Constructeur.
	 */
	private MetaDataContainer() {
		metadatas = Collections.emptyMap();
	}

	/**
	 * Constructeur ferm� (utilisable par le seul builder.
	 */
	MetaDataContainer(final Map<MetaData, Object> metadatas) {
		Assertion.checkNotNull(metadatas);
		//-----
		this.metadatas = Collections.unmodifiableMap(metadatas);
	}

	/**
	 * @return Set des m�ta donn�es.
	 */
	public Set<MetaData> getMetaDataSet() {
		return metadatas.keySet();
	}

	/**
	 * Retourne la valeur d'une m�ta-donn�e.
	 * Le type de la valeur correspond au type de la m�tadonn�e.
	 * Dans le cas de m�tadonn�e multivalu�e ; le type retourn� est une List typ�e. (Par le type de la m�tadonn�e)
	 * @param metaData m�tadonn�e
	 * @return Valeur de la m�tadonn�e, null si la m�ta-donn�e n'a pas �t� fix�e.
	 */
	public Object getValue(final MetaData metaData) {
		Assertion.checkNotNull(metaData);
		//-----
		return metadatas.get(metaData);
	}
}
