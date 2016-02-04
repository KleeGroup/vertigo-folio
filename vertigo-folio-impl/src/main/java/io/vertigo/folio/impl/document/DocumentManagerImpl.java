package io.vertigo.folio.impl.document;

import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.folio.document.DocumentManager;
import io.vertigo.folio.document.DocumentStore;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentBuilder;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.folio.impl.metadata.FileInfoMetaData;
import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.folio.metadata.MetaDataManager;
import io.vertigo.lang.Assertion;
import io.vertigo.util.ListBuilder;
import io.vertigo.util.StringUtil;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

public final class DocumentManagerImpl implements DocumentManager {
	private final MetaDataManager metaDataManager;
	private final FileManager fileManager;

	@Inject
	public DocumentManagerImpl(final MetaDataManager metaDataManager, final FileManager fileManager) {
		Assertion.checkNotNull(metaDataManager);
		Assertion.checkNotNull(fileManager);
		//=========================================================================
		this.metaDataManager = metaDataManager;
		this.fileManager = fileManager;
	}

	/** {@inheritDoc} */
	@Override
	public DocumentStore getDocumentStore(final String storeId) {
		//		final Collection<Plugin> plugins = Home.getContainer().getPlugins(CrawlerManager.class);
		return io.vertigo.app.Home.getApp().getComponentSpace().resolve(storeId, DocumentStorePlugin.class);
		//		for (final Plugin plugin : plugins) {
		//			if (plugin instanceof DocumentStorePlugin && storeId.equals(((DocumentStorePlugin) plugin).getId())) {
		//				return (DocumentStorePlugin) plugin;
		//			}
		//		}
		//		throw new KRuntimeException("Pas de DocumentStorePlugin d'id : " + storeId);
	}

	private static Document createDocument(final DocumentVersion documentVersion, final MetaDataSet extractedMdc) {
		//On cr�e le document
		//System.out.println("createDocument :" + fileDownloadUrl);
		final DocumentBuilder documentBuilder = new DocumentBuilder(documentVersion);
		populateDocument(documentBuilder, extractedMdc);
		return documentBuilder.build();
	}

	/** {@inheritDoc} */
	@Override
	public Document createDocument(final DocumentVersion documentVersion, final File file) {
		Assertion.checkNotNull(documentVersion);
		Assertion.checkNotNull(file);
		//-----
		final VFile kFile = fileManager.createFile(file);

		//On extrait les MetaDatas
		final MetaDataSet mdc = metaDataManager.extractMetaData(kFile);
		return createDocument(documentVersion, mdc);
	}

	private static void populateDocument(final DocumentBuilder documentBuilder, final MetaDataSet mdc) {
		final List<MetaData> excludedMetaData = new ListBuilder<MetaData>()
				.add(FileInfoMetaData.FILE_NAME)
				.add(FileInfoMetaData.SIZE)
				.add(FileInfoMetaData.FILE_EXTENSION)
				.add(FileInfoMetaData.LAST_MODIFIED)
				.build();

		final MetaDataSetBuilder mdcBuilder = new MetaDataSetBuilder();
		final String type = (String) mdc.getValue(FileInfoMetaData.FILE_EXTENSION);

		documentBuilder
				.withName((String) mdc.getValue(FileInfoMetaData.FILE_NAME))
				.withSize((Long) mdc.getValue(FileInfoMetaData.SIZE))
				.withType(StringUtil.isEmpty(type) ? "<aucun>" : type)
				.withContent("");//vide par defaut

		//documentBuilder.setLastModified((Date) mdc.getValue(FileInfoMetaData.LAST_MODIFIED));
		boolean contentSet = false;
		for (final MetaData metaData : mdc.getMetaDatas()) {
			if ("CONTENT".equals(metaData.toString())) {
				Assertion.checkArgument(!contentSet, "Le contenu � d�j� �t� trouv�, que faire de {0}.CONTENT ?", metaData.getClass().getName());
				documentBuilder.withContent((String) mdc.getValue(metaData));
				contentSet = true;
			} else if (!excludedMetaData.contains(metaData)) {
				mdcBuilder.addMetaData(metaData, mdc.getValue(metaData));
			}
		}
		documentBuilder.withSourceMetaDataContainer(mdcBuilder.build());
	}
	//
	//	/** {@inheritDoc} */
	//	public Document enhanceDocument(final List<DocumentPostProcessor> documentPostProcessors, final Document document) {
	//		return new DocumentEnhancer(documentPostProcessors).process(document);
	//	}
}
