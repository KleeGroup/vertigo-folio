package io.vertigo.folio.impl.enhancement;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentBuilder;
import io.vertigo.folio.enhancement.EnhancementManager;
import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.lang.Assertion;

import javax.inject.Inject;

import java.util.List;

/**
 * Created by sbernard on 28/05/2015.
 */
public class EnhancementManagerImpl implements EnhancementManager {
	@Inject
	private List<EnhancementPlugin> enhancementPlugins;

	@Override
	public Document enhanceDocument(Document documentToEnhance) throws Exception {
		Assertion.checkNotNull(documentToEnhance);
		//-----
		DocumentBuilder documentBuilder = new DocumentBuilder(documentToEnhance);
		for (EnhancementPlugin enhancementPlugin : this.enhancementPlugins) {
			try {
				MetaDataContainer metaDataContainer = enhancementPlugin.extract(documentToEnhance);
				documentBuilder.withEnhancedMetaDataContainer(metaDataContainer);
			} catch (Exception e) {
				throw (e);
			}
		}
		return documentBuilder.build();
	}
}
