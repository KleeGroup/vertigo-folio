package io.vertigo.folio.plugins.metadata.microsoft;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.folio.impl.metadata.MetaDataExtractorPlugin;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

import java.io.InputStream;

import org.apache.poi.poifs.eventfilesystem.POIFSReader;

/**
 * Fabrique Abstraite des documents Lucene
 * � partir des Fichiers de la suite Open office de Microsoft.
 *
 * @author pchretien
 * @version $Id: AbstractMSMetaDataExtractorPlugin.java,v 1.5 2014/02/27 10:21:46 pchretien Exp $
 */
public abstract class AbstractMSMetaDataExtractorPlugin implements MetaDataExtractorPlugin {

	/**
	 * Extrait le contenu texte du FileInfo.
	 * @param file Document
	 */
	protected abstract String extractContent(final VFile file) throws Exception;

	/** {@inheritDoc} */
	@Override
	public MetaDataSet extractMetaDataSet(final VFile file) throws Exception {
		Assertion.checkNotNull(file);
		//----------------------------------------------------------------------
		final MetaDataSetBuilder metaDataContainerBuilder = new MetaDataSetBuilder();
		//Etape 1 : Extraction des m�tadonn�es MS
		final POIFSReader reader = new POIFSReader();
		reader.registerListener(new POIFSReaderListenerImpl(metaDataContainerBuilder), "\005SummaryInformation");

		try (final InputStream inputStream = file.createInputStream()) {
			reader.read(inputStream);
		}
		//Etape 2 : Extraction des contenus selon le format (xls, ppt, doc)
		return metaDataContainerBuilder//
				.addMetaData(MSMetaData.CONTENT, extractContent(file))//
				.build();
	}
}
