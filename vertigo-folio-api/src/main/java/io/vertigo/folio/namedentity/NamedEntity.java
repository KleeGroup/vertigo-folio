package io.vertigo.folio.namedentity;

import io.vertigo.lang.Assertion;

public final class NamedEntity {
	private final String label;
	//	private final String type;
	//	private final String url;

	public NamedEntity(final String label/*, final String type, final String url*/) {
		Assertion.checkNotNull(label);
		//		Assertion.checkNotNull(type);
		//		Assertion.checkNotNull(url);
		//----
		this.label = label;
		//		type = type;
		//		url = url;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label;
	}
	//	public String getType() {
	//		return type;
	//	}
	//
	//	public String getUrl() {
	//		return url;
	//	}
	//
	//	@Override
	//	public boolean equals(final Object other) {
	//		if (!(other instanceof NamedEntity)) {
	//			return false;
	//		}
	//		final NamedEntity otherEntity = (NamedEntity) other;
	//		return ((this.name).equals(otherEntity.getName())
	//				&& (this.type).equals(otherEntity.getType())
	//				&& (this.url).equals(otherEntity.getUrl()));
	//	}
	//
	//	@Override
	//	public int hashCode() {
	//		return Objects.hash(name, type, url);
	//	}

}
