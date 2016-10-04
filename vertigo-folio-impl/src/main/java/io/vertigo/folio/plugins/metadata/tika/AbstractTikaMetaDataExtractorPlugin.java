package io.vertigo.folio.plugins.metadata.tika;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.folio.impl.metadata.MetaDataExtractorPlugin;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.folio.metadata.MetaDataType;
import io.vertigo.lang.Assertion;

/**
 * Extraction des m�tadonn�es via Tika.
 * Doit �tre surcharg� pour extraire les m�tadonn�es de chaque format
 * @author epaumier
 * @version $Id: AbstractTikaMetaDataExtractorPlugin.java,v 1.6 2014/02/27 10:22:04 pchretien Exp $
 * @param <M> Type de m�tadonn�e
 */
public abstract class AbstractTikaMetaDataExtractorPlugin<M extends TikaMetaData> implements MetaDataExtractorPlugin {
	private final Parser parser;
	private final M contentMetaData;
	private final Map<String, M> metaDataMap;

	/**
	 * Constructeur.
	 * Cr�e l'extracteur wrappant un parseur Tika
	 * @param parser Parseur tika � wrapper
	 * @param contentMetaData	Identifiant de m�tadonn�e du contenu
	 */

	protected AbstractTikaMetaDataExtractorPlugin(final Parser parser, final M contentMetaData) {
		Assertion.checkNotNull(parser);
		Assertion.checkNotNull(contentMetaData);
		//-----
		this.parser = parser;
		this.contentMetaData = contentMetaData;
		this.metaDataMap = new HashMap<>();
	}

	/**
	 * Lie le nom d'une m�tadonn�e Tika � un identifiant de m�tadonn�e.
	 * @param metaData Identifiant de m�tadonn�e Kasper
	 * @param tikaMetaData Nom de m�tadonn�e Tika
	 */
	protected final void bindMetaData(final M metaData, final String tikaMetaData) {
		Assertion.checkNotNull(metaData);
		Assertion.checkNotNull(tikaMetaData);
		//-----
		final Object previous = metaDataMap.put(tikaMetaData, metaData);
		Assertion.checkState(previous == null, "Binding d�ja effectu� pour {0}", tikaMetaData);
	}

	/**
	 * Permet de savoir si une m�tadonn�e Tika a d�j� �t� li�e � un identifiant de m�tadonn�e.
	 * @param tikaMetaData Nom de m�tadonn�e Tika
	 */
	protected final boolean isMetaDataBinded(final String tikaMetaData) {
		Assertion.checkNotNull(tikaMetaData);
		//-----
		return metaDataMap.containsKey(tikaMetaData);
	}

	/** {@inheritDoc} */
	@Override
	public final MetaDataSet extractMetaDataSet(final VFile file) throws Exception {
		Assertion.checkNotNull(file);
		//-----
		final MetaDataSetBuilder metaDataSetBuilder = new MetaDataSetBuilder();
		final org.apache.tika.metadata.Metadata tikaMetaData = new org.apache.tika.metadata.Metadata();

		try (final InputStream inputStream = file.createInputStream()) {
			// Cr�ation du contexte
			final ParseContext context = new ParseContext();
			context.set(Parser.class, parser);

			// Cr�ation des sorties
			final WriteOutContentHandler handler = new WriteOutContentHandler();

			tikaMetaData.add(org.apache.tika.metadata.TikaMetadataKeys.RESOURCE_NAME_KEY, file.getFileName());

			// Lancement du parsing
			parser.parse(inputStream, new BodyContentHandler(handler), tikaMetaData, context);

			metaDataSetBuilder.addMetaData(contentMetaData, handler.toString());
		}

		// Parcours des m�tadonn�es et ajout au conteneur
		for (final String name : tikaMetaData.names()) {
			if (metaDataMap.containsKey(name)) {
				final M metaData = metaDataMap.get(name);
				final String value = tikaMetaData.get(name);

				metaDataSetBuilder.addMetaData(metaData, stringToValue(metaData.getType(), value));
			}
		}

		return metaDataSetBuilder.build();
	}

	/**
	 * Conversion de String en valeur typ�e.
	 * @param stringValue Valeur textuelle
	 * @return Valeur typ�e conforme au tyope java.
	 */
	private static Object stringToValue(final MetaDataType metaDataType, final String stringValue) {
		if (stringValue == null) {
			return null;
		}

		switch (metaDataType) {
			case INTEGER:
				return Integer.parseInt(stringValue);

			case DATE:
				try {
					return DateFormat.getDateInstance().parse(stringValue);
				} catch (final ParseException e) {
					try {
						return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(stringValue);
					} catch (final ParseException e2) {
						return null;
					}
				}

			case STRING:
				return stringValue;
			case BOOLEAN:
				return Boolean.parseBoolean(stringValue);
			default:
				throw new IllegalStateException("Type non reconu" + metaDataType.name());
		}
	}

	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		// On cr�e un conteneur de m�tadonn�es Tika, avec le nom de la resource
		final org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
		metadata.set(org.apache.tika.metadata.TikaMetadataKeys.RESOURCE_NAME_KEY, file.getFileName());

		// On obtient le d�tecteur par d�faut de Tika
		final org.apache.tika.detect.Detector detector = TikaConfig.getDefaultConfig().getMimeRepository();

		// On ouvre un flux sur la resource
		try (final InputStream inputStream = file.createInputStream()) {
			// On renvoit le r�sultat de la d�tection du type MIME.
			if (inputStream.markSupported()) {
				detector.detect(inputStream, metadata);
				return true;
			}
			detector.detect(new BufferedInputStream(inputStream), metadata).toString();
			return true;
		} catch (final IOException e) {
			//en cas d'erreur, une exception stop le Work d'extraction
			//throw new KRuntimeException("Erreur lors de la recherche du Type Mime", e);

			//on pr�f�re indiquer que le mimeType n'est pas connu :
			return false;
		}
	}
}
