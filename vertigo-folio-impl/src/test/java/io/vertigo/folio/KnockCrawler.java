package io.vertigo.folio;

import io.vertigo.app.App;
import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.core.component.di.injector.Injector;
import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.impl.file.FileManagerImpl;
import io.vertigo.folio.crawler.CrawlerManager;
import io.vertigo.folio.document.DocumentManager;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentVersion;
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

import javax.inject.Inject;

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
		try (App app = new App(config())) {
			new KnockCrawler(app).crawl();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void crawl() {
		int i = 0;
		for (final DocumentVersion documentVersion : crawlerManager.getCrawler("myFS").crawl("")) {
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
			//			System.out.println("   +---: " + document);
			//			final Document document = metaDataManager.extractMetaData(new VFile(documentVersion.getUrl()));
			i++;
			if (i > 100) {
				break;
			}
		}
	}

	private static AppConfig config() {
		// @formatter:off
			return new AppConfigBuilder()
				.beginBootModule("fr")
				.endModule()
//				.beginBoot()
//					.withLogConfig(new LogConfig("log4j.xml"))
//				.endBoot()
				.beginModule("document")
					.addComponent(CrawlerManager.class, CrawlerManagerImpl.class)
					.beginPlugin(FSCrawlerPlugin.class)
						.addParam("dataSourceId", "myFS")
						.addParam("directory" , "z:")
						.addParam ("maxFiles" , "250")
						.addParam ("excludePatterns" , "")
					.endPlugin()
					.addComponent(FileManager.class, FileManagerImpl.class)
					.addComponent(DocumentManager.class, DocumentManagerImpl.class)
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
		// @formatter:on
	}
}
