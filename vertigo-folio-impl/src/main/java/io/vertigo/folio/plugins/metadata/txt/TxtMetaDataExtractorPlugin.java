package io.vertigo.folio.plugins.metadata.txt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.impl.metadata.MetaDataExtractorPlugin;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

/**
 * Gestion des documents de type txt.
 *
 * @author pchretien
 * @version $Id: TxtMetaDataExtractorPlugin.java,v 1.5 2014/02/27 10:22:04 pchretien Exp $
 */
public final class TxtMetaDataExtractorPlugin implements MetaDataExtractorPlugin {
	private static final int MAX_CONTENT_LENGTH = 25 * 1024 * 1024; //inutil de faire trop grand : 25Mo max

	private final List<String> extensions;

	/**
	 * Constructeur.
	 */
	@Inject
	public TxtMetaDataExtractorPlugin(final @Named("extensions") String extensions) {
		Assertion.checkNotNull(extensions);
		//-----
		this.extensions = Arrays.asList(extensions.split(","));
	}

	private static String getContent(final VFile file) throws IOException {

		try (final InputStream inputStream = file.createInputStream();
				final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			final int length = file.getLength().intValue();
			final StringBuilder content = new StringBuilder("");
			content.ensureCapacity(Math.min(length, MAX_CONTENT_LENGTH));
			String line = reader.readLine();
			while (line != null && content.length() + line.length() <= MAX_CONTENT_LENGTH) {
				content.append(line);
				line = reader.readLine();
			}
			return content.toString();
		}
	}

	/** {@inheritDoc}  */
	@Override
	public MetaDataSet extractMetaDataSet(final VFile file) throws IOException {
		Assertion.checkNotNull(file);
		//-----
		final String content = getContent(file);
		final MetaDataSetBuilder metaDataSetBuilder = new MetaDataSetBuilder()
				.addMetaData(TxtMetaData.CONTENT, content);
		if ("MD".equalsIgnoreCase(getExtension(file))) {
			metaDataSetBuilder.addMetaData(TxtMetaData.MARKDOWN_CONTENT, content);
		}
		return metaDataSetBuilder.build();
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		return extensions
				.stream()
				.anyMatch(extension -> extension.trim().equalsIgnoreCase(getExtension(file)));
	}

	private static String getExtension(final VFile file) {
		final String fileExtension = FileUtil.getFileExtension(file.getFileName());
		return fileExtension.trim();
	}
}
