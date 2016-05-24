package io.vertigo.knock.channel;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.impl.enhancement.EnhancementPlugin;
import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataSet;

public final class MockPostProcessorPlugin implements EnhancementPlugin {

	/** {@inheritDoc} */
	@Override
	public MetaDataSet extract(final Document document) {
		System.out.println("I post-processed the document : " + document.getName());
		//		MetaDataContainer metaDataContainer = new MetaDataContainerBuilder()//
		//				.withMetaData(DemoDocumentMetaData.TITLE, "$" + id++)//
		//				.build();
		final MetaDataSet metaDataSet = document.getSourceMetaDataSet();
		for (final MetaData metaData : metaDataSet.getMetaDatas()) {
			System.out.println(metaData.toString());
		}
		System.out.println(document.getContent());

		return metaDataSet;
	}
}
