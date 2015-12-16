package io.vertigo.folio.document.model;

import io.vertigo.lang.Builder;

import java.util.Date;

/**
 * @author npiedeloup
 * @version $Id: DocumentVersionBuilder.java,v 1.3 2012/03/26 17:06:52 pchretien Exp $
 */
public final class DocumentVersionBuilder implements Builder<DocumentVersion> {
	//URL
	private String mySourceUrl;
	private String myDataSourceId;
	private Date myLastModified;

	public DocumentVersionBuilder withSourceUrl(final String sourceUrl) {
		mySourceUrl = sourceUrl;
		return this;
	}

	public DocumentVersionBuilder withDataSourceId(final String dataSourceId) {
		myDataSourceId = dataSourceId;
		return this;
	}

	public DocumentVersionBuilder withLastModified(final Date lastModified) {
		myLastModified = lastModified;
		return this;
	}

	@Override
	public DocumentVersion build() {
		return new DocumentVersion(myDataSourceId, mySourceUrl, myLastModified);
	}
}
