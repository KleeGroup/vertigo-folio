package io.vertigo.folio.impl.metadata;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.folio.metadata.MetaDataContainerBuilder;
import io.vertigo.folio.metadata.MetaDataManager;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Option;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

/**
 * Impl�mentation de r�f�rence de l'extracteur de m�tadonn�es.
 *
 * @author npiedeloup, pchretien
 * @version $Id: MetaDataManagerImpl.java,v 1.4 2014/01/28 18:49:34 pchretien Exp $
 */
public final class MetaDataManagerImpl implements MetaDataManager {
	private static final Logger LOGGER = Logger.getLogger(MetaDataManager.class);

	@Inject
	private List<MetaDataExtractorPlugin> metaDataExtractorPlugins;

	private Option<MetaDataExtractorPlugin> getMetaDataExtractorPlugin(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		for (final MetaDataExtractorPlugin plugin : metaDataExtractorPlugins) {
			if (plugin.accept(file)) {
				return Option.some(plugin);
			}
		}
		//Si pas de metaDataExtractorPlugin associ�
		return Option.none();

	}

	/** {@inheritDoc} */
	@Override
	public MetaDataContainer extractMetaData(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		//		Assertion.notNull(extractorWork.getResource());
		//		Assertion.precondition(extractorWork.getResource() instanceof FileInfo, "seules les ressources de type FileInfo sont g�r�es.");
		//-----
		//		final FileInfo fileInfo = (FileInfo) extractorWork.getResource();
		//
		//		return extractMetaData(fileInfo);
		//	}
		//
		//	private MetaDataContainer2 extractMetaData(final Resource<?, ?> resource) throws KSystemException {
		//		Assertion.notNull(fileInfo);
		//-----
		//		final String fileExtension = FileInfoHelper.getFileExtension(resource);
		LOGGER.trace(String.format("Start extract MetaData on %s ", file.getFileName()));
		final Option<MetaDataExtractorPlugin> metaDataExtractor = getMetaDataExtractorPlugin(file);

		final MetaDataContainerBuilder metaDataContainerBuilder = new MetaDataContainerBuilder();
		//analyticsAgent.startProcess(fileExtension);
		//boolean ok = false;
		if (metaDataExtractor.isDefined()) {
			LOGGER.info(String.format("Start extract MetaData on %s whith %s", file.getFileName(), metaDataExtractor.get().getClass().getSimpleName()));
			metaDataContainerBuilder.withAllMetaDatas(extractMetaData(metaDataExtractor.get(), file));
		} else {
			LOGGER.info(String.format("No MetaDataExtractor found for %s", file.getFileName()));
		}

		//ok = true;
		extractMetaData(metaDataContainerBuilder, file);
		return metaDataContainerBuilder.build();
	}

	private static MetaDataContainer extractMetaData(final MetaDataExtractorPlugin metaDataExtractor, final VFile file) {
		try {
			return metaDataExtractor.extractMetaData(file);
		} catch (final Exception e) {
			//	analyticsAgent.setValue(MEDA_SYSTEM_EXCEPTION_COUNT, 100);
			// On n'a pas r�ussi � extraire les meta donn�es
			// on cr�e un conteneur vide.
			return MetaDataContainer.EMPTY_META_DATA_CONTAINER;
			//	LOGGER.warn("Impossible d'extraire les m�ta-donn�es de " + resource.getURI().toURN(), e);
		} //finally {
	}

	private static void extractMetaData(final MetaDataContainerBuilder metaDataContainerBuilder, final VFile kFile) {
		final String fileExtension = FileUtil.getFileExtension(kFile.getFileName());
		//			//	analyticsAgent.stopProcess();
		//		}
		// Dans le cas des fichiers on ajoute la taille
		metaDataContainerBuilder//
				.withMetaData(FileInfoMetaData.SIZE, kFile.getLength())//
				.withMetaData(FileInfoMetaData.FILE_EXTENSION, fileExtension.toUpperCase())//
				// note: il y a aussi FileSystemView.getFileSystemView().getSystemIcon(file)

				// throw new KSystemException("Erreur de lecture des m�ta donn�es pour " + file.getName(), e);
				.withMetaData(FileInfoMetaData.FILE_NAME, kFile.getFileName())//
				.withMetaData(FileInfoMetaData.LAST_MODIFIED, kFile.getLastModified());
		//		mdContainer.setValue(metaDataManager.getNameSpace().getMetaData(DocumentMetaData.MEDA_LAST_MODIFIED_URN), fileInfo.getLastModified());
		// mdContainer.setValue(PATH, file.getPath());
		//	return mdContainer;
	}

}
