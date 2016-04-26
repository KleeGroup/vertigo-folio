package io.vertigo.folio.impl.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.enhancement.EnhancementManager;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by sbernard on 28/05/2015.
 */
public class EnhancementManagerImpl implements EnhancementManager {
	@Inject
	private List<EnhancementPlugin> enhancementPlugins;

	@Override
	public MetaDataSet enhanceDocument(final Document document) throws Exception {
		Assertion.checkNotNull(document);
		//-----
		final MetaDataSetBuilder metaDataSetBuilder = new MetaDataSetBuilder();
		for (final EnhancementPlugin enhancementPlugin : enhancementPlugins) {
			try {
				metaDataSetBuilder.addMetaDataSet(enhancementPlugin.extract(document));
			} catch (final Exception e) {
				throw (e);
			}
		}
		return metaDataSetBuilder.build();
	}
}
