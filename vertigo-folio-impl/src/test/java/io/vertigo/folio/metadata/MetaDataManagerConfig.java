package io.vertigo.folio.metadata;

import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.impl.file.FileManagerImpl;
import io.vertigo.folio.impl.metadata.MetaDataManagerImpl;
import io.vertigo.folio.plugins.metadata.microsoft.excel.MSExcelMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.microsoft.powerpoint.MSPowerPointMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.microsoft.word.MSWordMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.odf.ODFMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.ooxml.CommonOOXMLMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.pdf.PDFMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.txt.TxtMetaDataExtractorPlugin;

final class MetaDataManagerConfig {

	static AppConfig build() {
		//@formatter:off
		return new AppConfigBuilder()
//				.beginModule(CommonsFeatures.class).endModule()
				.beginModule("vertigo-dynamo")
					.addComponent(FileManager.class, FileManagerImpl.class)
				.endModule()
				.beginModule("vertigo-document")
					.addComponent(MetaDataManager.class, MetaDataManagerImpl.class)
					.addPlugin(MSWordMetaDataExtractorPlugin.class)
					.addPlugin(MSPowerPointMetaDataExtractorPlugin.class)
					.addPlugin(MSExcelMetaDataExtractorPlugin.class)
					.addPlugin(PDFMetaDataExtractorPlugin.class)
					.beginPlugin(TxtMetaDataExtractorPlugin.class)
						.addParam("extensions", "txt, log")
					.endPlugin()
					.addPlugin(CommonOOXMLMetaDataExtractorPlugin.class)
					.addPlugin(ODFMetaDataExtractorPlugin.class)
					.addPlugin(AutoTikaMetaDataExtractorPlugin.class)
				.endModule()
				.build();
		//@formatter:on
	}
}
