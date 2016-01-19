package io.vertigo.folio.plugins.metadata.pdf;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.impl.metadata.MetaDataExtractorPlugin;
import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.folio.metadata.MetaDataContainerBuilder;
import io.vertigo.lang.Assertion;
import io.vertigo.util.ListBuilder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.activation.DataSource;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.apache.pdfbox.preflight.utils.ByteArrayDataSource;
import org.apache.pdfbox.util.PDFTextStripper;

import sun.misc.BASE64Encoder;

/**
 * @author  pchretien
 * @version $Id: PDFMetaDataExtractorPlugin.java,v 1.5 2014/02/27 10:22:04 pchretien Exp $
 */
public final class PDFMetaDataExtractorPlugin implements MetaDataExtractorPlugin {
	private static final Logger LOGGER = Logger.getLogger(PDFMetaDataExtractorPlugin.class);
	private static final String PDFA_VALID = "VALID";
	private static final String PDFA_INVALID = "INVALID";

	private static PDDocument createPDDocument(final VFile file) throws IOException {
		try (final InputStream inputStream = file.createInputStream()) {
			return PDDocument.load(inputStream);
		}
	}

	/** {@inheritDoc} */
	@Override
	public MetaDataContainer extractMetaData(final VFile file) throws Exception {
		Assertion.checkNotNull(file);
		//-----
		//Extraction de TOUT le contenu d'un pdf
		try (final PDDocument pdd = createPDDocument(file)) {
			final PDFTextStripper stripper = new PDFTextStripper();
			final String content = stripper.getText(pdd);
			final String pdfaValidationMsg = getPdfA1bValidation(file);
			final List<String> thumbnails = extractBase64Thumbnails(pdd);
			//-----
			//Metadata
			final PDDocumentInformation documentInformation = pdd.getDocumentInformation();
			final MetaDataContainerBuilder metaDataContainerBuilder = new MetaDataContainerBuilder()
					.withMetaData(PDFMetaData.AUTHOR, documentInformation.getAuthor())
					.withMetaData(PDFMetaData.KEYWORDS, documentInformation.getKeywords())
					.withMetaData(PDFMetaData.SUBJECT, documentInformation.getSubject())
					.withMetaData(PDFMetaData.TITLE, documentInformation.getTitle())
					.withMetaData(PDFMetaData.CONTENT, content)
					.withMetaData(PDFMetaData.PRODUCER, documentInformation.getProducer())
					.withMetaData(PDFMetaData.PDFA, String.valueOf(PDFA_VALID.equals(pdfaValidationMsg)))
					.withMetaData(PDFMetaData.PDFA_VALIDATION_MSG, pdfaValidationMsg);
			if (thumbnails.size() > 0) {
				metaDataContainerBuilder.withMetaData(PDFMetaData.THUMBNAIL_PAGE_1, thumbnails.get(0));
				if (thumbnails.size() > 1) {
					metaDataContainerBuilder.withMetaData(PDFMetaData.THUMBNAIL_PAGE_2, thumbnails.get(1));
					if (thumbnails.size() > 2) {
						metaDataContainerBuilder.withMetaData(PDFMetaData.THUMBNAIL_PAGE_3, thumbnails.get(2));
						if (thumbnails.size() > 3) {
							metaDataContainerBuilder.withMetaData(PDFMetaData.THUMBNAIL_PAGE_4, thumbnails.get(3));
						}
					}
				}
			}
			return metaDataContainerBuilder.build();
		}
	}

	private static String getPdfA1bValidation(final VFile file) throws IOException {
		ValidationResult result;
		try (final InputStream inputStream = file.createInputStream()) {
			final DataSource ds = new ByteArrayDataSource(inputStream);
			final PreflightParser parser = new PreflightParser(ds);
			try {
				/* Parse the PDF file with PreflightParser that inherits from the NonSequentialParser.
				 * Some additional controls are present to check a set of PDF/A requirements.
				 * (Stream length consistency, EOL after some Keyword...)
				 */
				parser.parse();
				/* Once the syntax validation is done,
				 * the parser can provide a PreflightDocument
				 * (that inherits from PDDocument)
				 * This document process the end of PDF/A validation.
				 */
				try (final PreflightDocument document = parser.getPreflightDocument()) {
					try {
						document.validate();
					} catch (final NullPointerException e) {
						//bug connu https://issues.apache.org/jira/browse/PDFBOX-3024
						LOGGER.warn("PdfBox preflight PDF/A validation error", e);
					}
					// Get validation result
					result = document.getResult();
				}
			} catch (final SyntaxValidationException e) {
				/* the parse method can throw a SyntaxValidationException
				 * if the PDF file can't be parsed.
				 * final In this case, the exception contains an instance of ValidationResult
				 */
				result = e.getResult();
			}
			// display validation result
			if (result.isValid()) {
				return PDFA_VALID;
			}
			final StringBuilder sb = new StringBuilder(PDFA_INVALID).append(" : ");
			for (final ValidationError error : result.getErrorsList()) {
				sb.append("\n").append(error.getErrorCode()).append(" : ").append(error.getDetails()).append(",");
			}
			return sb.toString();
		}
	}

	private static List<String> extractBase64Thumbnails(final PDDocument pdd) throws IOException {
		final List<PDPage> pages = pdd.getDocumentCatalog().getAllPages();
		final ListBuilder<String> stringListBuilder = new ListBuilder<>();
		// Get the pages one by one, testing if it exists for each one
		int nbThumbnails = 0;
		for (final PDPage page : pages) {
			stringListBuilder.add(encodeToString(page.convertToImage(BufferedImage.TYPE_INT_RGB, 48), "png"));
			if (nbThumbnails++ > 5) {
				break; //pas plus de 5 pages
			}
		}
		return stringListBuilder.build();
	}

	public static String encodeToString(final BufferedImage image, final String type) throws IOException {
		try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			ImageIO.write(image, type, bos);
			final byte[] imageBytes = bos.toByteArray();
			final BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(imageBytes);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		final String fileExtension = FileUtil.getFileExtension(file.getFileName());
		return "pdf".equalsIgnoreCase(fileExtension);
	}
}
