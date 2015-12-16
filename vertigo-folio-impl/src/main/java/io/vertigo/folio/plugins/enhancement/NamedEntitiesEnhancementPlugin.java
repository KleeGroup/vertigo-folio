package io.vertigo.folio.plugins.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.impl.enhancement.EnhancementPlugin;
import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.folio.metadata.MetaDataContainerBuilder;
import io.vertigo.folio.namedentity.NamedEntity;
import io.vertigo.folio.namedentity.NamedEntityManager;
import io.vertigo.lang.Assertion;

import java.util.Set;

import javax.inject.Inject;

/**
 * Created by sbernard on 30/12/2014.
 */
public class NamedEntitiesEnhancementPlugin implements EnhancementPlugin {
	private final NamedEntityManager namedEntityManager;

	@Inject
	public NamedEntitiesEnhancementPlugin(final NamedEntityManager namedEntityManager) {
		Assertion.checkNotNull(namedEntityManager);
		this.namedEntityManager = namedEntityManager;
	}

	@Override
	public MetaDataContainer extract(final Document document) {
		Assertion.checkNotNull(document);
		//-------------------------------------------------------------
		final Set<NamedEntity> namedEntities = namedEntityManager.extractNamedEntities(document.getContent());

		// Concatenation des entités trouvées pour l'indexation
		final StringBuilder stringBuilder = new StringBuilder();
		for (final NamedEntity namedEntity : namedEntities) {
			stringBuilder.append(namedEntity.getName());
			stringBuilder.append(", ");
		}
		// Suppression de la dernière virgule
		if (stringBuilder.length() > 0) {
			stringBuilder.delete(stringBuilder.lastIndexOf(","), stringBuilder.length() - 1);
		}
		return new MetaDataContainerBuilder()
				.withMetaData(NamedEntitiesMetaData.NAMED_ENTITIES, stringBuilder.toString())
				.build();
	}
}
