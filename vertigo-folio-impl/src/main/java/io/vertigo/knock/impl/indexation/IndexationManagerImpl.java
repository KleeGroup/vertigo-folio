package io.vertigo.knock.impl.indexation;

import io.vertigo.app.Home;
import io.vertigo.dynamo.collections.ListFilter;
import io.vertigo.dynamo.domain.metamodel.DtDefinition;
import io.vertigo.dynamo.domain.model.URI;
import io.vertigo.dynamo.search.SearchManager;
import io.vertigo.dynamo.search.metamodel.SearchIndexDefinition;
import io.vertigo.dynamo.search.model.SearchIndex;
import io.vertigo.folio.document.model.Document;
import io.vertigo.knock.channel.ChannelDefinition;
import io.vertigo.knock.indexation.IndexationManager;

import java.util.Collection;

import javax.inject.Inject;

import org.dom4j.io.DocumentResult;

/**
 * Created by sbernard on 28/05/2015.
 */
public class IndexationManagerImpl implements IndexationManager {
	private static final String SOURCE_ID = "SOURCE_ID:";

	@Inject
	private SearchManager searchManager;

	@Override
	public void pushDocument(final Document document) {
		final DocumentIndex documentIndex = document.toDocumentIndex();
		final DocumentResult documentResult = document.toDocumentResult();
		final Collection<DtDefinition> dtDefinitions = Home.getDefinitionSpace().getAll(DtDefinition.class);
		final SearchIndexDefinition searchIndexDefinition = getIndexDefinition();
		final URI<DocumentIndex> uri = new URI<>(searchIndexDefinition.getIndexDtDefinition(), documentIndex.getId());
		final SearchIndex<DocumentIndex, DocumentResult> searchIndex = SearchIndex.createIndex(searchIndexDefinition, uri, documentIndex, documentResult);
		searchManager.put(searchIndexDefinition, searchIndex);
	}

	@Override
	public void dropIndex(final ChannelDefinition channelDefinition) {
		final String dataSourceName = channelDefinition.getDataSourceName();
		final ListFilter listFilter = new ListFilter(SOURCE_ID + "\"" + dataSourceName + "\"");
		searchManager.removeAll(getIndexDefinition(), listFilter);
	}

	private SearchIndexDefinition getIndexDefinition() {
		return Home.getApp().getDefinitionSpace().resolve(getIndexDefinitionName(), SearchIndexDefinition.class);
	}

	protected String getIndexDefinitionName() {
		return "IDX_DOCUMENT";
	}

}
