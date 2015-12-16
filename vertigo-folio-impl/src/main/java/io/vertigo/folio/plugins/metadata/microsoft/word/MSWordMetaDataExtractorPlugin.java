package io.vertigo.folio.plugins.metadata.microsoft.word;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.plugins.metadata.microsoft.AbstractMSMetaDataExtractorPlugin;
import io.vertigo.lang.Assertion;

import java.io.InputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;

/**
 * @author pchretien
 * @version $Id: MSWordMetaDataExtractorPlugin.java,v 1.5 2014/02/27 10:21:46 pchretien Exp $
 */
public final class MSWordMetaDataExtractorPlugin extends AbstractMSMetaDataExtractorPlugin {
	/** {@inheritDoc} */
	@Override
	protected String extractContent(final VFile file) throws Exception {
		Assertion.checkNotNull(file);
		//-----
		//Extraction des donn�es d'un fichier word
		try (final InputStream inputStream = file.createInputStream()) {
			final WordExtractor extractor = new WordExtractor(inputStream);
			return extractor.getText();
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		final String fileExtension = FileUtil.getFileExtension(file.getFileName());
		return "doc".equalsIgnoreCase(fileExtension);
	}
}
