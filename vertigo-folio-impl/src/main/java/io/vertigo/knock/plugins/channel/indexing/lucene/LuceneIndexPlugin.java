package io.vertigo.knock.plugins.channel.indexing.lucene;

import io.vertigo.folio.document.model.Document;
import io.vertigo.folio.document.model.DocumentBuilder;
import io.vertigo.folio.document.model.DocumentVersion;
import io.vertigo.folio.document.model.DocumentVersionBuilder;
import io.vertigo.lang.Assertion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LegacyIntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * Created by pchretien
 */
public class LuceneIndexPlugin {
	private final Directory directory = new RAMDirectory();
	private final Analyzer analyzer;

	public LuceneIndexPlugin() {
		analyzer = new StandardAnalyzer();
	}

	//	@Override
	public List<Document> getAllDocuments() {
		try (IndexReader reader = DirectoryReader.open(directory)) {
			final List<Document> documents = new ArrayList<>();
			for (int docId = 0; docId < reader.numDocs(); docId++) {
				final org.apache.lucene.document.Document d = reader.document(docId);
				final Document document = buildDocument(d);
				documents.add(document);
			}
			return documents;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	//	@Override
	public void pushDocument(final Document document) {
		Assertion.checkNotNull(document);
		//-----
		final List<Document> candidates = search(document.getName());
		for (final Document candidate : candidates) {
			if (candidate.getDocumentVersion().equals(document.getDocumentVersion())) {
				return;
			}
		}

		// Document does not exist in the index if we got here
		final org.apache.lucene.document.Document luceneDocument = buildLuceneDocument(document);

		//-----
		try (IndexWriter indexWriter = buildIndexWriter()) {
			indexWriter.addDocument(luceneDocument);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	//	@Override
	public void dropIndex() {
		try (IndexWriter indexWriter = buildIndexWriter()) {
			indexWriter.deleteAll();
			indexWriter.commit();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Query buildQuery(final String querystr, final Analyzer analyzer) throws ParseException {
		final QueryParser parser = new QueryParser("name", analyzer);
		parser.setAllowLeadingWildcard(true);
		return parser.parse("*" + querystr + "*");
	}

	private IndexWriter buildIndexWriter() throws IOException {
		final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		return new IndexWriter(directory, indexWriterConfig);
	}

	//	@Override
	public List<Document> search(final String querystr) {
		try {
			final Query query = buildQuery(querystr, analyzer);
			//-----
			final int hitsPerPage = 10;
			try (final IndexReader reader = DirectoryReader.open(directory)) {
				final IndexSearcher searcher = new IndexSearcher(reader);
				final TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
				searcher.search(query, collector);
				final ScoreDoc[] hits = collector.topDocs().scoreDocs;

				final List<Document> documents = new ArrayList<>();
				for (final ScoreDoc hit : hits) {
					final int docId = hit.doc;
					final org.apache.lucene.document.Document luceneDocument = searcher.doc(docId);
					final Document document = buildDocument(luceneDocument);
					documents.add(document);
				}
				return documents;
			}
		} catch (final IOException | ParseException e) {
			return Collections.EMPTY_LIST;
		}
	}

	//	@Override
	public Document getCategoryDescription(final String categoryUrl) throws Exception {
		final QueryParser parser = new QueryParser("sourceUrl", analyzer);
		parser.setAllowLeadingWildcard(true);
		final Query query = parser.parse(categoryUrl + "readme.md");
		try (final IndexReader reader = DirectoryReader.open(directory)) {
			final IndexSearcher searcher = new IndexSearcher(reader);
			final TopScoreDocCollector collector = TopScoreDocCollector.create(1);
			searcher.search(query, collector);
			final ScoreDoc result = collector.topDocs().scoreDocs[0];
			final int docId = result.doc;
			final org.apache.lucene.document.Document luceneDocument = searcher.doc(docId);
			final Document document = buildDocument(luceneDocument);
			return document;
		}
	}

	private static Document buildDocument(final org.apache.lucene.document.Document luceneDocument) {
		final DocumentVersion documentVersion = new DocumentVersionBuilder()
				.withDataSourceId(luceneDocument.get("dataSourceId"))
				.withLastModified(new Date())
				.withSourceUrl(luceneDocument.get("sourceUrl"))
				.build();

		return new DocumentBuilder(documentVersion)
				.withName(luceneDocument.get("name"))
				.withSize(Integer.parseInt(luceneDocument.get("size")))
				.withType(luceneDocument.get("type"))
				.build();
	}

	private static org.apache.lucene.document.Document buildLuceneDocument(final Document document) {
		final org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();

		luceneDocument.add(new TextField("dataSourceId", document.getDocumentVersion().getDataSourceId(), Field.Store.YES));
		luceneDocument.add(new TextField("sourceUrl", document.getDocumentVersion().getUrl(), Field.Store.YES));
		luceneDocument.add(new TextField("name", document.getName(), Field.Store.YES));
		luceneDocument.add(new TextField("content", document.getContent(), Field.Store.NO)); //<<<<<<<<
		luceneDocument.add(new TextField("type", document.getType(), Field.Store.YES));
		luceneDocument.add(new LegacyIntField("size", document.getSize(), Field.Store.YES));

		return luceneDocument;
	}
}
