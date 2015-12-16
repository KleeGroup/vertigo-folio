package io.vertigo.folio.namedentity;

import io.vertigo.lang.Component;

import java.util.Set;

public interface NamedEntityManager extends Component {
	Set<NamedEntity> extractNamedEntities(final String text);
}
