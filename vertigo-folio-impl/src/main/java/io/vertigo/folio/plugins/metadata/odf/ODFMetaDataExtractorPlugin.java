package io.vertigo.folio.plugins.metadata.odf;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.plugins.metadata.tika.AbstractTikaMetaDataExtractorPlugin;
import io.vertigo.lang.Assertion;

import org.apache.tika.parser.odf.OpenDocumentParser;

import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.*;
/**
 * Extraction des m�tadonn�es ODF via Tika.
 * 
 * @author epaumier
 * @version $Id: ODFMetaDataExtractorPlugin.java,v 1.4 2014/01/28 18:49:34 pchretien Exp $
 */
public final class ODFMetaDataExtractorPlugin extends AbstractTikaMetaDataExtractorPlugin<ODFMetaData> {

	/**
	 * Constructeur.
	 * Cr�e l'extracteur wrappant le parseur de document OpenOffice de Tika.
	 */
	public ODFMetaDataExtractorPlugin() {
		super(new OpenDocumentParser(), ODFMetaData.CONTENT);

		bindMetaData(TITLE, "title");
		bindMetaData(DESCRIPTION, "description");
		bindMetaData(SUBJECT, "subject");
		bindMetaData(KEYWORD, "Keyword");
		bindMetaData(LANGUAGE, "language");

		bindMetaData(INITIAL_CREATOR, "initial-creator");
		bindMetaData(CREATOR, "creator");

		bindMetaData(CREATION_DATE, "Creation-Date");
		bindMetaData(DATE, "date");
		bindMetaData(EDITING_DURATION, "Edit-Time");
		bindMetaData(EDITING_CYCLES, "editing-cycles");

		bindMetaData(GENERATOR, "generator");

		bindMetaData(TABLE_COUNT, "nbTab");
		bindMetaData(OBJECT_COUNT, "nbObject");
		bindMetaData(IMAGE_COUNT, "nbImg");
		bindMetaData(PAGE_COUNT, "nbPage");
		bindMetaData(PARAGRAPH_COUNT, "nbPara");
		bindMetaData(WORD_COUNT, "nbWord");
		bindMetaData(CHARACTER_COUNT, "nbCharacter");
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//---------------------------------------------------------------------
		final String fileExtension = FileUtil.getFileExtension(file.getFileName());
		return "odt".equalsIgnoreCase(fileExtension);
	}
}
