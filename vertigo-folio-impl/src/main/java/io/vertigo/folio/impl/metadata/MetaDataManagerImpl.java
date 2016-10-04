package io.vertigo.folio.impl.metadata;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.metadata.MetaDataManager;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

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

	private Optional<MetaDataExtractorPlugin> getMetaDataExtractorPlugin(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		return metaDataExtractorPlugins
				.stream()
				.filter(metaDataExtractorPlugin -> metaDataExtractorPlugin.accept(file))
				.findFirst();
	}

	/** {@inheritDoc} */
	@Override
	public MetaDataSet extractMetaData(final VFile file) {
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
		final Optional<MetaDataExtractorPlugin> metaDataExtractor = getMetaDataExtractorPlugin(file);

		final MetaDataSetBuilder metaDataContainerBuilder = new MetaDataSetBuilder();
		//analyticsAgent.startProcess(fileExtension);
		//boolean ok = false;
		if (metaDataExtractor.isPresent()) {
			LOGGER.info(String.format("Start extract MetaData on %s whith %s", file.getFileName(), metaDataExtractor.get().getClass().getSimpleName()));
			metaDataContainerBuilder.addMetaDataSet(extractMetaData(metaDataExtractor.get(), file));
		} else {
			LOGGER.info(String.format("No MetaDataExtractor found for %s", file.getFileName()));
		}

		//ok = true;
		extractMetaData(metaDataContainerBuilder, file);
		return metaDataContainerBuilder.build();
	}

	private static MetaDataSet extractMetaData(final MetaDataExtractorPlugin metaDataExtractor, final VFile file) {
		try {
			return metaDataExtractor.extractMetaDataSet(file);
		} catch (final Exception e) {
			//	analyticsAgent.setValue(MEDA_SYSTEM_EXCEPTION_COUNT, 100);
			// On n'a pas r�ussi � extraire les meta donn�es
			// on cr�e un conteneur vide.
			return MetaDataSet.EMPTY_META_DATA_SET;
			//	LOGGER.warn("Impossible d'extraire les m�ta-donn�es de " + resource.getURI().toURN(), e);
		} //finally {
	}

	private static void extractMetaData(final MetaDataSetBuilder metaDataSetBuilder, final VFile kFile) {
		final String fileExtension = FileUtil.getFileExtension(kFile.getFileName());
		//			//	analyticsAgent.stopProcess();
		//		}
		// Dans le cas des fichiers on ajoute la taille
		metaDataSetBuilder//
				.addMetaData(FileInfoMetaData.SIZE, kFile.getLength())//
				.addMetaData(FileInfoMetaData.FILE_EXTENSION, fileExtension.toUpperCase())//
				// note: il y a aussi FileSystemView.getFileSystemView().getSystemIcon(file)

				// throw new KSystemException("Erreur de lecture des m�ta donn�es pour " + file.getName(), e);
				.addMetaData(FileInfoMetaData.FILE_NAME, kFile.getFileName())//
				.addMetaData(FileInfoMetaData.LAST_MODIFIED, kFile.getLastModified());
		//		mdContainer.setValue(metaDataManager.getNameSpace().getMetaData(DocumentMetaData.MEDA_LAST_MODIFIED_URN), fileInfo.getLastModified());
		// mdContainer.setValue(PATH, file.getPath());
		//	return mdContainer;
	}

}
