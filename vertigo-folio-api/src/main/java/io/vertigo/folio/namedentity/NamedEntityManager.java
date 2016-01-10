package io.vertigo.folio.namedentity;

import io.vertigo.lang.Manager;

import java.util.Set;

public interface NamedEntityManager extends Manager {
	Set<NamedEntity> extractNamedEntities(final String text);
}
