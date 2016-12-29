package io.vertigo.folio.metadata;

import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.app.config.ModuleConfigBuilder;
import io.vertigo.commons.daemon.DaemonManager;
import io.vertigo.commons.impl.daemon.DaemonManagerImpl;
import io.vertigo.core.param.Param;
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

final class MetaDataConfig {

	static AppConfig build() {
		return new AppConfigBuilder()
				//				.beginModule(CommonsFeatures.class).endModule()
				.addModule(new ModuleConfigBuilder("vertigo-dynamo")
						.addComponent(DaemonManager.class, DaemonManagerImpl.class)
						.addComponent(FileManager.class, FileManagerImpl.class)
						.build())
				.addModule(new ModuleConfigBuilder("vertigo-document")
						.addComponent(MetaDataManager.class, MetaDataManagerImpl.class)
						.addPlugin(MSWordMetaDataExtractorPlugin.class)
						.addPlugin(MSPowerPointMetaDataExtractorPlugin.class)
						.addPlugin(MSExcelMetaDataExtractorPlugin.class)
						.addPlugin(PDFMetaDataExtractorPlugin.class)
						.addPlugin(TxtMetaDataExtractorPlugin.class,
								Param.create("extensions", "txt, log"))
						.addPlugin(CommonOOXMLMetaDataExtractorPlugin.class)
						.addPlugin(ODFMetaDataExtractorPlugin.class)
						.addPlugin(AutoTikaMetaDataExtractorPlugin.class)
						.build())
				.build();
	}
}
