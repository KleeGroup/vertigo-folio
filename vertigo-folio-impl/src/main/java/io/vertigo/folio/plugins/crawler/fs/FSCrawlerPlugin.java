package io.vertigo.folio.plugins.crawler.fs;

import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentBuilder;
import io.vertigo.folio.document.model.DocumentCategory;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.folio.document.model.DocumentVersionBuilder;
import io.vertigo.folio.impl.crawler.CrawlerPlugin;
import io.vertigo.folio.impl.metadata.FileInfoMetaData;
import io.vertigo.folio.metadata.MetaData;
import io.vertigo.folio.metadata.MetaDataManager;
import io.vertigo.folio.metadata.MetaDataSet;
import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;
import io.vertigo.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Crawler de fichier synchrone.
 * @author npiedeloup
 * @version $Id: FSCrawlerPlugin.java,v 1.10 2014/02/17 17:55:57 npiedeloup Exp $
 */
public final class FSCrawlerPlugin implements CrawlerPlugin {
	private final static String categoryUrlRegex = "^\\/([^(\\/)]*\\/){0,3}";

	@Inject
	private MetaDataManager metaDataManager;
	@Inject
	private FileManager fileManager;

	private final String dataSourceId;
	private final File directory;
	private final Integer maxFiles;
	private final List<Pattern> excludePatterns;

	/**
	 * Constructeur.
	 * @param dataSourceId dataSourceId
	 * @param strDirectory Repertoire source
	 * @param maxFiles Nombre de fichier maximum � crawler, ou null pour aucune limite
	 * @param strExcludePatterns Patterns d'exclusion de fichier
	 */
	@Inject
	public FSCrawlerPlugin(@Named("dataSourceId") final String dataSourceId,
			@Named("directory") final String strDirectory,
			@Named("maxFiles") final Integer maxFiles,
			@Named("excludePatterns") final String strExcludePatterns) {
		Assertion.checkArgNotEmpty(dataSourceId);
		Assertion.checkArgNotEmpty(strDirectory);
		Assertion.checkArgument(maxFiles == null || maxFiles > 0, "maxFiles est null ou positif");
		//-----
		this.dataSourceId = dataSourceId;
		directory = new File(strDirectory);
		this.maxFiles = maxFiles;
		if (strExcludePatterns != null && !strExcludePatterns.isEmpty()) {
			excludePatterns = new ArrayList<>();
			for (final String strExcludePattern : strExcludePatterns.split(";")) {
				excludePatterns.add(Pattern.compile(strExcludePattern));
			}
		} else {
			excludePatterns = Collections.emptyList();
		}
		//-----
		Assertion.checkState(directory.isDirectory(), "Le directory ({0}) doit �tre un directory", strDirectory);
	}

	/**
	 * @param startAtUrl Point de d�part du crawling
	 * @return Iterateur de DocumentVersion
	 */
	private Iterator<File> createFileIterator(final String startAtUrl) {
		//<param name="fileReaderClassName" value="kasperimpl.crawler.plugins.fs.FSFileInputPipe"/>
		//final Object[] parameters = new Object[] { directory, startAtUrl, maxFiles };
		//return ClassUtil.newInstance(fileReaderConstructor, parameters);
		return new FSFileIterator(directory, startAtUrl, maxFiles, excludePatterns);
	}

	private static final File getFile(final DocumentVersion documentVersion, final String dataSourceId, final String basePath) {
		//Il s'agit de la m�thode inverse de getRelativeUrl
		Assertion.checkArgument(dataSourceId.equals(documentVersion.getDataSourceId()), "Ce crawler n'est pas sur la m�me dataSource que le document (crawler:{0} document:{1})", dataSourceId, documentVersion.getDataSourceId());
		return new File(basePath + "/" + documentVersion.getUrl());
	}

	/**
	 * @param file Fichier source
	 * @param basePath Chemin de la racine.
	 * @return Url relative du fichier
	 */
	private static String getRelativeUrl(final File file, final String basePath) {
		//Il s'agit de la m�thode inverse de getFile
		final String fullPath = getNormalizedAbsolutePath(file);
		Assertion.checkArgument(fullPath.startsWith(basePath), "Le chemin du fichier {0} ne commence pas par la racine du channel {1}", fullPath, basePath);
		return fullPath.substring(basePath.length());
	}

	/** {@inheritDoc} */
	@Override
	public Document readDocument(final DocumentVersion documentVersion) {
		final File file = getFile(documentVersion, dataSourceId, getNormalizedAbsolutePath(directory));
		return createDocumentFromFile(documentVersion, file);
	}

	//
	//	/** {@inheritDoc} */
	//	@Override
	//	public String getBaseDownloadUrl() {
	//		return downloadUrl;
	//	}

	private static String getNormalizedAbsolutePath(final File file) {
		//normalization pour eviter les disctinctions Windows vs Linux
		return file.getAbsolutePath().replace('\\', '/');
	}

	private static Date getNormalizedLastModified(final File file) {
		//normalization pour eviter les disctinctions Windows vs Linux
		return new Date(file.lastModified() / 1000 * 1000);//on tronque � la seconde
	}

	/** {@inheritDoc} */
	@Override
	public Iterable<DocumentVersion> crawl(final String startAtUrl) {
		return new Iterable<DocumentVersion>() {

			@Override
			public Iterator<DocumentVersion> iterator() {
				return new DocumentVersionIterator(directory, dataSourceId, createFileIterator(startAtUrl));
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final String testDataSourceId) {
		return dataSourceId.equals(testDataSourceId);
	}

	private static class DocumentVersionIterator implements Iterator<DocumentVersion> {
		private final String basePath;
		private final String dataSourceId;
		private final Iterator<File> fileIterator;

		/**
		 * Constructeur.
		 * @param baseDirectory Repertoire de base
		 * @param dataSourceId Id de la DataSource
		 * @param fileIterator Source de fichier
		 */
		DocumentVersionIterator(final File baseDirectory, final String dataSourceId, final Iterator<File> fileIterator) {
			Assertion.checkNotNull(baseDirectory);
			Assertion.checkArgNotEmpty(dataSourceId);
			Assertion.checkNotNull(fileIterator);
			//-----
			basePath = getNormalizedAbsolutePath(baseDirectory);
			this.dataSourceId = dataSourceId;
			this.fileIterator = fileIterator;
		}

		@Override
		public boolean hasNext() {
			return fileIterator.hasNext();
		}

		@Override
		public DocumentVersion next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			final File file = fileIterator.next();
			return new DocumentVersionBuilder()
					.withSourceUrl(getRelativeUrl(file, basePath))
					.withDataSourceId(dataSourceId)
					.withLastModified(getNormalizedLastModified(file))
					.build();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private Document createDocumentFromFile(final DocumentVersion documentVersion, final File file) {
		Assertion.checkNotNull(documentVersion);
		Assertion.checkNotNull(file);
		//-----
		final VFile kFile = fileManager.createFile(file);

		//On extrait les MetaDatas
		final MetaDataSet mdc = metaDataManager.extractMetaData(kFile);
		//final DocumentCategory category = extractCategory(documentVersion);
		return createDocument(documentVersion, mdc);
	}

	private static Document createDocument(final DocumentVersion documentVersion, final MetaDataSet extractedMdc) {
		//On cr�e le document
		//System.out.println("createDocument :" + fileDownloadUrl);
		final DocumentBuilder documentBuilder = new DocumentBuilder(documentVersion);
		populateDocument(documentBuilder, extractedMdc);
		documentBuilder.withCategory(extractCategory(documentVersion));
		return documentBuilder.build();
	}

	private static void populateDocument(final DocumentBuilder documentBuilder, final MetaDataSet metaDataSet) {
		final List<MetaData> excludedMetaData = new ArrayList<>(4);
		excludedMetaData.add(FileInfoMetaData.FILE_NAME);
		excludedMetaData.add(FileInfoMetaData.SIZE);
		excludedMetaData.add(FileInfoMetaData.FILE_EXTENSION);
		excludedMetaData.add(FileInfoMetaData.LAST_MODIFIED);

		final MetaDataSetBuilder metaDataSetBuilder = new MetaDataSetBuilder();
		final String type = (String) metaDataSet.getValue(FileInfoMetaData.FILE_EXTENSION);

		documentBuilder
				.withName((String) metaDataSet.getValue(FileInfoMetaData.FILE_NAME))
				.withSize((Integer) metaDataSet.getValue(FileInfoMetaData.SIZE))
				.withType(StringUtil.isEmpty(type) ? "<aucun>" : type)
				.withContent("");//vide par defaut

		//documentBuilder.setLastModified((Date) mdc.getValue(FileInfoMetaData.LAST_MODIFIED));
		boolean contentSet = false;
		for (final MetaData metaData : metaDataSet.getMetaDatas()) {
			if ("CONTENT".equals(metaData.toString())) {
				Assertion.checkArgument(!contentSet, "Le contenu � d�j� �t� trouv�, que faire de {0}.CONTENT ?", metaData.getClass().getName());
				documentBuilder.withContent((String) metaDataSet.getValue(metaData));
				contentSet = true;
			} else if (!excludedMetaData.contains(metaData)) {
				metaDataSetBuilder.addMetaData(metaData, metaDataSet.getValue(metaData));
			}
		}
		documentBuilder.withSourceMetaDataSet(metaDataSetBuilder.build());
	}

	private static DocumentCategory extractCategory(final DocumentVersion documentVersion) {
		final String url = documentVersion.getUrl();

		final Pattern urlPattern = Pattern.compile(categoryUrlRegex);
		final Matcher urlMatcher = urlPattern.matcher(url);
		urlMatcher.find();
		final String categoryUrl = urlMatcher.group();

		final String categoryName;
		if (categoryUrl.equals("/")) {
			categoryName = "";
		} else {
			categoryName = categoryUrl.substring(categoryUrl.substring(0, categoryUrl.length() - 1).lastIndexOf("/") + 1, categoryUrl.length() - 1);
		}

		return new DocumentCategory(categoryName, categoryUrl);
	}
}
