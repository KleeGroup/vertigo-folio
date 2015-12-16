package io.vertigo.folio.document.model;

import io.vertigo.lang.Assertion;

import java.io.Serializable;

/**
 * Created by sbernard on 10/03/2015.
 */
public class DocumentCategory implements Serializable {
	private static final long serialVersionUID = -8309211574774156954L;

	private final String name;
	private final String url;

	public DocumentCategory(final String name, final String url) {
		Assertion.checkNotNull(name);
		Assertion.checkNotNull(url);
		//-----
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
