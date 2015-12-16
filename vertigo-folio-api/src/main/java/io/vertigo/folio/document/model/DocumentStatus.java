package io.vertigo.folio.document.model;

import java.io.Serializable;

/**
 * A document can be 
 * - indexed
 * - dirty
 * - enhanced
 * 
 * Created by sbernard on 04/03/2015.
 */
public final class DocumentStatus implements Serializable {
	private static final long serialVersionUID = -5856325775088634567L;
	private final boolean indexed;
	private final boolean dirty;
	private final boolean enhanced;

	DocumentStatus(final boolean indexed, final boolean dirty, final boolean enhanced) {
		this.indexed = indexed;
		this.dirty = dirty;
		this.enhanced = enhanced;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean isEnhanced() {
		return enhanced;
	}
}
