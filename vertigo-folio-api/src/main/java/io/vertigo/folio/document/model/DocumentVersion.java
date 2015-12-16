package io.vertigo.folio.document.model;

import io.vertigo.lang.Assertion;

import java.io.Serializable;
import java.util.Date;

/**
 * DocumentVersion.
 * @author npiedeloup
 * @version $Id: DocumentVersion.java,v 1.2 2011/08/02 08:30:31 npiedeloup Exp $
 */
public final class DocumentVersion implements Serializable {
	private static final long serialVersionUID = -7740007080741391655L;

	//Key
	private final String key;

	//URL
	private final String sourceUrl;
	private final String dataSourceId;//L'id du doc == dataSourceId + source dans cette DataSource

	//Version
	private final Date lastModified;

	/**
	 * Constructeur.
	 * @param dataSourceId Nom de la dataSource (not null)
	 * @param sourceUrl Url du document (not null)
	 * @param lastModified Date de derni�re modification (not null)
	 */
	DocumentVersion(final String dataSourceId, final String sourceUrl, final Date lastModified) {
		Assertion.checkArgNotEmpty(sourceUrl);
		Assertion.checkArgNotEmpty(dataSourceId);
		Assertion.checkNotNull(lastModified);
		//=========================================================================
		this.sourceUrl = sourceUrl;
		this.dataSourceId = dataSourceId;
		this.lastModified = lastModified;
		key = dataSourceId + ":" + sourceUrl;
	}

	public String getKey() {
		return key;
	}

	public String getUrl() {
		return sourceUrl;
	}

	//Identification de la donnee source
	public String getDataSourceId() {
		return dataSourceId;
	}

	public Date getLastModified() {
		return lastModified;
	}
}
