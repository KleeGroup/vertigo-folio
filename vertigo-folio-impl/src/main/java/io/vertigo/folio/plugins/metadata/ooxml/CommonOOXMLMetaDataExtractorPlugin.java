package io.vertigo.folio.plugins.metadata.ooxml;

import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.CATEGORY;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.CONTENT_STATUS;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.CONTENT_TYPE;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.CREATED;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.CREATOR;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.DESCRIPTION;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.IDENTIFIER;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.KEYWORDS;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.LANGUAGE;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.LAST_MODIFIED_BY;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.LAST_PRINTED;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.MODIFIED;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.REVISION_NUMBER;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.SUBJECT;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.TITLE;
import static io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData.VERSION;

import java.io.InputStream;
import java.util.Date;

import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.util.Nullable;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.impl.metadata.MetaDataExtractorPlugin;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

/**
 * Extrait les m�tadonn�es classiques d'un document Office Open XML.
 * docx, xlsx, pptx, ...
 * Peut �tre surcharg� pour extraire les m�tadonn�es d'un format particulier
 * @author epaumier
 *
 */
public class CommonOOXMLMetaDataExtractorPlugin implements MetaDataExtractorPlugin {

	/** {@inheritDoc} */
	@Override
	public final MetaDataSet extractMetaDataSet(final VFile file) throws Exception {
		Assertion.checkNotNull(file);
		//-----
		if (file.getFileName().endsWith(".xlsx") && file.getLength() > 3.5d * 1024 * 1024) {
			throw new java.lang.UnsupportedOperationException("Fichier XLSX trop gros pour �tre trait� : " + file.getLength() / 1024 + "Ko");
		}
		try (final InputStream inputStream = file.createInputStream()) {
			//1. Cr�ation de l'extracteur
			final POIXMLTextExtractor extractor = createExtractor(inputStream);
			//2. Extraction des m�ta-donn�es
			return doExtractMetaData(extractor);
		}
	}

	private static POIXMLTextExtractor createExtractor(final InputStream inputStream) throws Exception {
		return (POIXMLTextExtractor) ExtractorFactory.createExtractor(inputStream);
	}

	/**
	 * Extrait m�tadonn�es (et texte) du document repr�sent� par l'extracteur.
	 * Surcharger pour extraire des m�tadonn�es sp�cifiques au document
	 *
	 * @param extractor Extracteur des m�tadonn�es
	 * @return Container Conteneur o� placer les m�tadonn�es
	 */
	protected static final MetaDataSet doExtractMetaData(final POIXMLTextExtractor extractor) {
		final MetaDataSetBuilder metaDataSetBuilder = new MetaDataSetBuilder();

		// Content
		metaDataSetBuilder.addMetaData(OOXMLOthersMetaData.CONTENT, extractor.getText());

		// Core Metadatas
		final PackagePropertiesPart cp = extractor.getCoreProperties().getUnderlyingProperties();
		addProperty(metaDataSetBuilder, CATEGORY, cp.getCategoryProperty());
		addProperty(metaDataSetBuilder, CONTENT_TYPE, cp.getContentTypeProperty());
		addProperty(metaDataSetBuilder, CONTENT_STATUS, cp.getContentStatusProperty());
		addDateProperty(metaDataSetBuilder, CREATED, cp.getCreatedProperty());
		addProperty(metaDataSetBuilder, CREATOR, cp.getCreatorProperty());
		addProperty(metaDataSetBuilder, DESCRIPTION, cp.getDescriptionProperty());
		addProperty(metaDataSetBuilder, IDENTIFIER, cp.getIdentifierProperty());
		addProperty(metaDataSetBuilder, KEYWORDS, cp.getKeywordsProperty());
		addProperty(metaDataSetBuilder, LANGUAGE, cp.getLanguageProperty());
		addProperty(metaDataSetBuilder, LAST_MODIFIED_BY, cp.getLastModifiedByProperty());
		addDateProperty(metaDataSetBuilder, LAST_PRINTED, cp.getLastPrintedProperty());
		addDateProperty(metaDataSetBuilder, MODIFIED, cp.getModifiedProperty());
		addProperty(metaDataSetBuilder, REVISION_NUMBER, cp.getRevisionProperty());
		addProperty(metaDataSetBuilder, SUBJECT, cp.getSubjectProperty());
		addProperty(metaDataSetBuilder, TITLE, cp.getTitleProperty());
		addProperty(metaDataSetBuilder, VERSION, cp.getVersionProperty());

		// Extended Metadatas
		final CTProperties extendedProperties = extractor.getExtendedProperties().getUnderlyingProperties();
		if (extendedProperties.isSetApplication()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.APPLICATION, extendedProperties.getApplication());
		}
		if (extendedProperties.isSetAppVersion()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.APP_VERSION, extendedProperties.getAppVersion());
		}
		if (extendedProperties.isSetCharacters()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.CHARACTERS, extendedProperties.getCharacters());
		}
		if (extendedProperties.isSetCharactersWithSpaces()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.CHARACTERS_WITH_SPACES, extendedProperties.getCharactersWithSpaces());
		}
		if (extendedProperties.isSetCompany()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.COMPANY, extendedProperties.getCompany());
		}
		if (extendedProperties.isSetSlides()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.HIDDEN_SLIDES, extendedProperties.getHiddenSlides());
		}
		if (extendedProperties.isSetLines()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.LINES, extendedProperties.getLines());
		}
		if (extendedProperties.isSetManager()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.MANAGER, extendedProperties.getManager());
		}
		if (extendedProperties.isSetSlides()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.MMCLIPS, extendedProperties.getMMClips());
		}
		if (extendedProperties.isSetNotes()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.NOTES, extendedProperties.getNotes());
		}
		if (extendedProperties.isSetPages()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.PAGES, extendedProperties.getPages());
		}
		if (extendedProperties.isSetParagraphs()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.PARAGRAPHS, extendedProperties.getParagraphs());
		}
		if (extendedProperties.isSetPresentationFormat()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.PRESENTATION_FORMAT, extendedProperties.getPresentationFormat());
		}
		if (extendedProperties.isSetSlides()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.SLIDES, extendedProperties.getSlides());
		}
		if (extendedProperties.isSetTemplate()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.TEMPLATE, extendedProperties.getTemplate());
		}
		if (extendedProperties.isSetTotalTime()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.TOTAL_TIME, extendedProperties.getTotalTime());
		}
		if (extendedProperties.isSetWords()) {
			addProperty(metaDataSetBuilder, OOXMLExtendedMetaData.WORDS, extendedProperties.getWords());
		}

		// Custom Metadatas
		//extractor.getCustomProperties();
		return metaDataSetBuilder.build();

	}

	private static void addProperty(final MetaDataSetBuilder metaDataSetBuilder, final OOXMLMetaData metaData, final String value) {
		metaDataSetBuilder.addMetaData(metaData, value);
	}

	private static void addProperty(final MetaDataSetBuilder metaDataSetBuilder, final OOXMLMetaData metaData, final int value) {
		metaDataSetBuilder.addMetaData(metaData, value);
	}

	private static void addProperty(final MetaDataSetBuilder metaDataSetBuilder, final OOXMLMetaData metaData, final Nullable<String> nullable) {
		if (nullable.hasValue()) {
			addProperty(metaDataSetBuilder, metaData, nullable.getValue());
		}
	}

	private static void addDateProperty(final MetaDataSetBuilder metaDataSetBuilder, final OOXMLMetaData metaData, final Nullable<Date> nullable) {
		if (nullable.hasValue()) {
			metaDataSetBuilder.addMetaData(metaData, nullable.getValue());
		}
	}

	/*
		protected final void addProperty(final MetaDataContainer2 metaDataContainer, final MetaData2 metaData, Object value) throws KSystemException {
			if (value instanceof Nullable<?>) {
				final Nullable<?> nullable = (Nullable<?>) value;
				value = nullable.hasValue() ? nullable.getValue() : null;
			}

			value = convertValue(value, metaData);
			if (value != null) {
				metaDataContainer.setValue(metaData, convertValue(value, metaData));
			}
		}

		protected Object convertValue(final MetaDataType metaDataType, final Object value) {1
			if (value == null) {
				return null;
			}

	    	switch (metaDataType) {
				case INTEGER :
					if (value instanceof Long) {
						return ((Long) value).intValue();
					}
					if (value instanceof Integer) {
						return value;
					}
					if (value instanceof String) {
						return Integer.parseInt((String) value);
					}
					return null;

				case LONG :
					if (value instanceof Long) {
						return value;
					}
					if (value instanceof Integer) {
						return ((Integer) value).longValue();
					}
					if (value instanceof String) {
						return Long.parseLong((String) value);
					}
					return null;

	    		case DATE :
	    			if (value instanceof Date) {
	    				return value;
	    			}
	    			if (value instanceof Calendar) {
	    				return ((Calendar) value).getTime();
	    			}
	    			if (value instanceof String) {
	    				try {
	    					return DateFormat.getDateInstance().parse((String) value);
	    				} catch (final ParseException e) {
	    					try {
	    						return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) value);
	    					} catch (final ParseException e2) {
	    						return null;
	    					}
	    				}
	    			}
	    			return null;

	    		case DURATION :
					try {
						return new SimpleDateFormat("'P''T'HH'H'mm'M'ss'S'").parse((String) value).getTime();
					} catch (final ParseException e2) {
						return null;
					}

	    		case CALENDAR :
	    			if (value instanceof Calendar) {
	    				return value;
	    			}
	    			if (value instanceof Date) {
	    				final Calendar calendar = Calendar.getInstance();
					    calendar.setTime((Date) value);

	    				return calendar;
	    			}
	    			if (value instanceof String) {
	    				try {
						    final Date date = DateFormat.getDateInstance().parse((String) value);
						    final Calendar calendar = Calendar.getInstance();
						    calendar.setTime(date);

		    				return calendar;
						} catch (final ParseException e) {
						return null;
						}
	    			}
	    			return null;

	    		case STRING :
	    		case UNKNOWN :
				default :
					return value.toString();
	    	}
		}
	 */
	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		final String fileExtension = FileUtil.getFileExtension(file.getFileName());
		return "pptx".equalsIgnoreCase(fileExtension)
				|| "docx".equalsIgnoreCase(fileExtension)
				|| "xlsx".equalsIgnoreCase(fileExtension);

	}
}
