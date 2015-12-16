package io.vertigo.folio.plugins.metadata.tika;

import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.CHARACTER_COUNT;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.CREATION_DATE;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.CREATOR;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.DATE;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.DESCRIPTION;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.GENERATOR;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.INITIAL_CREATOR;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.KEYWORD;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.LANGUAGE;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.PAGE_COUNT;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.SUBJECT;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.TITLE;
import static io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData.WORD_COUNT;

import org.apache.tika.parser.AutoDetectParser;

/**
 * Extrait les m�tadonn�es classiques via Tika.
 * Doit �tre surcharg� pour extraire les m�tadonn�es de chaque format
 * @author epaumier
 *
 */
public class AutoTikaMetaDataExtractorPlugin extends AbstractTikaMetaDataExtractorPlugin<AutoTikaMetaData> {

	/**
	 * Constructeur.
	 * Cr�e l'extracteur wrappant le parseur auto-d�tectant de Tika.
	 */
	public AutoTikaMetaDataExtractorPlugin() {
		super(new AutoDetectParser(), AutoTikaMetaData.CONTENT);

		bindMetaData(TITLE, "title");
		bindMetaData(DESCRIPTION, "description");
		bindMetaData(SUBJECT, "subject");
		bindMetaData(KEYWORD, "Keyword");
		bindMetaData(LANGUAGE, "language");

		bindMetaData(INITIAL_CREATOR, "initial-creator");
		bindMetaData(CREATOR, "creator");

		bindMetaData(CREATION_DATE, "Creation-Date");
		bindMetaData(DATE, "date");

		bindMetaData(GENERATOR, "generator");
		bindMetaData(PAGE_COUNT, "nbPage");
		bindMetaData(WORD_COUNT, "nbWord");
		bindMetaData(CHARACTER_COUNT, "nbCharacter");
	}

}
