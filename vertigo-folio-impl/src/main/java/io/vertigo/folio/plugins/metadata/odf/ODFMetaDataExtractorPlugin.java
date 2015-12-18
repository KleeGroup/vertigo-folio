package io.vertigo.folio.plugins.metadata.odf;

import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.CHARACTER_COUNT;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.CREATION_DATE;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.CREATOR;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.DESCRIPTION;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.EDITING_CYCLES;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.EDITING_DURATION;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.GENERATOR;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.IMAGE_COUNT;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.INITIAL_CREATOR;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.KEYWORD;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.LANGUAGE;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.OBJECT_COUNT;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.PAGE_COUNT;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.PARAGRAPH_COUNT;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.SUBJECT;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.TABLE_COUNT;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.TITLE;
import static io.vertigo.folio.plugins.metadata.odf.ODFMetaData.WORD_COUNT;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.plugins.metadata.tika.AbstractTikaMetaDataExtractorPlugin;
import io.vertigo.lang.Assertion;

import org.apache.tika.parser.odf.OpenDocumentParser;

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

		bindMetaData(CREATION_DATE, "creation-Date");
		bindMetaData(ODFMetaData.MODIFICATION_DATE, "modification-date");
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
