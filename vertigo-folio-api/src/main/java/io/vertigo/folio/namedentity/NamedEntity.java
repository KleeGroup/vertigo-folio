package io.vertigo.folio.namedentity;

import io.vertigo.lang.Assertion;

import java.util.Objects;

public final class NamedEntity {
	private final String name;
	private final String type;
	private final String url;

	public NamedEntity(final String name, final String type, final String url) {
		Assertion.checkNotNull(name);
		Assertion.checkNotNull(type);
		Assertion.checkNotNull(url);
		//----
		this.name = name;
		this.type = type;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof NamedEntity)) {
			return false;
		}
		final NamedEntity otherEntity = (NamedEntity) other;
		return ((this.name).equals(otherEntity.getName())
				&& (this.type).equals(otherEntity.getType())
				&& (this.url).equals(otherEntity.getUrl()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type, url);
	}

}
