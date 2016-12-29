package io.vertigo.knock;

import javax.inject.Inject;

import io.vertigo.app.App;
import io.vertigo.app.AutoCloseableApp;
import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.app.config.ModuleConfigBuilder;
import io.vertigo.core.component.di.injector.Injector;
import io.vertigo.core.param.Param;
import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.impl.file.FileManagerImpl;
import io.vertigo.folio.crawler.CrawlerManager;
import io.vertigo.folio.document.DocumentManager;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.impl.crawler.CrawlerManagerImpl;
import io.vertigo.folio.impl.document.DocumentManagerImpl;
import io.vertigo.folio.impl.metadata.MetaDataManagerImpl;
import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataManager;
import io.vertigo.folio.plugins.crawler.fs.FSCrawlerPlugin;
import io.vertigo.folio.plugins.metadata.microsoft.excel.MSExcelMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.microsoft.powerpoint.MSPowerPointMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.microsoft.word.MSWordMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.odf.ODFMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.ooxml.CommonOOXMLMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.pdf.PDFMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaDataExtractorPlugin;
import io.vertigo.folio.plugins.metadata.txt.TxtMetaDataExtractorPlugin;

public final class KnockCrawler {
	@Inject
	private CrawlerManager crawlerManager;

	//	@Inject
	//	private MetaDataManager metaDataManager;

	private KnockCrawler(final App app) {
		Injector.injectMembers(this, app.getComponentSpace());
	}

	public static void main(final String[] args) {
		System.out.println(">>> start spider");
		try (AutoCloseableApp app = new AutoCloseableApp(config())) {
			new KnockCrawler(app).crawl();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void crawl() {
		final int i = 0;
		crawlerManager
				.crawl("myFS")
				.limit(100)
				.forEach(documentVersion -> {
					System.out.println("doc[" + i + "]: " + documentVersion.getUrl());
					try {
						final Document document = crawlerManager.readDocument(documentVersion);
						System.out.println("   +--- name : " + document.getName());
						System.out.println("   +--- source");
						for (final MetaData metaData : document.getSourceMetaDataSet().getMetaDatas()) {
							System.out.println("   +------ " + metaData + " : " + document.getSourceMetaDataSet().getValue(metaData));
						}
					} catch (final Throwable e) {
						System.out.println("   +---: failed to read");
					}
				});
	}

	private static AppConfig config() {
		return new AppConfigBuilder()
				.beginBoot()
				.withLocales("fr")
				.endBoot()
				//				.beginBoot()
				//					.withLogConfig(new LogConfig("log4j.xml"))
				//				.endBoot()
				.addModule(new ModuleConfigBuilder("document")
						.addComponent(CrawlerManager.class, CrawlerManagerImpl.class)
						.addPlugin(FSCrawlerPlugin.class,
								Param.create("dataSourceId", "myFS"),
								Param.create("directory", "z:"),
								Param.create("maxFiles", "250"),
								Param.create("excludePatterns", ""))
						.addComponent(FileManager.class, FileManagerImpl.class)
						.addComponent(DocumentManager.class, DocumentManagerImpl.class)
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
