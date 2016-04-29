package io.vertigo.folio.plugins.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.impl.enhancement.EnhancementPlugin;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.folio.namedentity.NamedEntity;
import io.vertigo.folio.namedentity.NamedEntityManager;
import io.vertigo.lang.Assertion;

import java.util.Set;

import javax.inject.Inject;

/**
 * Created by sbernard on 30/12/2014.
 */
public final class NamedEntitiesEnhancementPlugin implements EnhancementPlugin {
	private final NamedEntityManager namedEntityManager;

	@Inject
	public NamedEntitiesEnhancementPlugin(final NamedEntityManager namedEntityManager) {
		Assertion.checkNotNull(namedEntityManager);
		//-----
		this.namedEntityManager = namedEntityManager;
	}

	@Override
	public MetaDataSet extract(final Document document) {
		Assertion.checkNotNull(document);
		//-----
		final Set<NamedEntity> namedEntities = namedEntityManager.extractNamedEntities(document.getContent());

		// Concatenation des entités trouvées pour l'indexation
		final StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for (final NamedEntity namedEntity : namedEntities) {
			if (!first) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(namedEntity.getName());
			first = false;
		}
		return new MetaDataSetBuilder()
				.addMetaData(NamedEntitiesMetaData.NAMED_ENTITIES, stringBuilder.toString())
				.build();
	}
}
