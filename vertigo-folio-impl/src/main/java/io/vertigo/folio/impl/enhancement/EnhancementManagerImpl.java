package io.vertigo.folio.impl.enhancement;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.enhancement.EnhancementManager;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

/**
 * Created by sbernard on 28/05/2015.
 */
public final class EnhancementManagerImpl implements EnhancementManager {
	@Inject
	private List<EnhancementPlugin> enhancementPlugins;

	@Override
	public MetaDataSet enhanceDocument(final Document document) throws Exception {
		Assertion.checkNotNull(document);
		//-----
		final MetaDataSetBuilder metaDataSetBuilder = new MetaDataSetBuilder();
		for (final EnhancementPlugin enhancementPlugin : enhancementPlugins) {
			metaDataSetBuilder.addMetaDataSet(enhancementPlugin.extract(document));
		}
		return metaDataSetBuilder.build();
	}
}
