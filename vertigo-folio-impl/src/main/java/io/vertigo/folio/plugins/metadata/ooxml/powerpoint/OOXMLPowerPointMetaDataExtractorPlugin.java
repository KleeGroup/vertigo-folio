package io.vertigo.folio.plugins.metadata.ooxml.powerpoint;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.plugins.metadata.ooxml.CommonOOXMLMetaDataExtractorPlugin;
import io.vertigo.lang.Assertion;

/**
 * Permet d'�tendre l'extracteur OOXML pour des traitements particuliers sur les pr�sentations PowerPoint.
 * @author epaumier
 * @version $Id: OOXMLPowerPointMetaDataExtractorPlugin.java,v 1.4 2014/01/28 18:49:34 pchretien Exp $
 */
public final class OOXMLPowerPointMetaDataExtractorPlugin extends CommonOOXMLMetaDataExtractorPlugin {
	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		final String fileExtension = FileUtil.getFileExtension(file.getFileName());
		return "pptx".equalsIgnoreCase(fileExtension);
	}
}
