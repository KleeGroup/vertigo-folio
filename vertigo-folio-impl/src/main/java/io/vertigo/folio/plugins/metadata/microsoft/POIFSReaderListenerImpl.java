package io.vertigo.folio.plugins.metadata.microsoft;

import io.vertigo.folio.metadata.MetaDataSetBuilder;
import io.vertigo.lang.Assertion;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

final class POIFSReaderListenerImpl implements POIFSReaderListener {
	private final MetaDataSetBuilder metaDataContainerBuilder;

	POIFSReaderListenerImpl(final MetaDataSetBuilder metaDataContainerBuilder) {
		Assertion.checkNotNull(metaDataContainerBuilder);
		//---------------------------------------------------------------------
		this.metaDataContainerBuilder = metaDataContainerBuilder;
	}

	/** {@inheritDoc} */
	@Override
	public void processPOIFSReaderEvent(final POIFSReaderEvent event) {
		try {
			final SummaryInformation si = (SummaryInformation) PropertySetFactory.create(event.getStream());
			metaDataContainerBuilder
					.addMetaData(MSMetaData.TITLE, si.getTitle())
					.addMetaData(MSMetaData.AUTHOR, si.getAuthor())
					.addMetaData(MSMetaData.SUBJECT, si.getSubject())
					.addMetaData(MSMetaData.COMMENTS, si.getComments())
					.addMetaData(MSMetaData.KEYWORDS, si.getKeywords());
		} catch (final Exception ex) {
			throw new RuntimeException("processPOIFSReaderEvent", ex);
		}
	}
}
