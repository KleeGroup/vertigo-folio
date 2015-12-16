package io.vertigo.folio.plugins.metadata.microsoft;

import io.vertigo.folio.metadata.MetaDataContainerBuilder;
import io.vertigo.lang.Assertion;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

final class POIFSReaderListenerImpl implements POIFSReaderListener {
	private final MetaDataContainerBuilder metaDataContainerBuilder;

	POIFSReaderListenerImpl(final MetaDataContainerBuilder metaDataContainerBuilder) {
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
					.withMetaData(MSMetaData.TITLE, si.getTitle())
					.withMetaData(MSMetaData.AUTHOR, si.getAuthor())
					.withMetaData(MSMetaData.SUBJECT, si.getSubject())
					.withMetaData(MSMetaData.COMMENTS, si.getComments())
					.withMetaData(MSMetaData.KEYWORDS, si.getKeywords());
		} catch (final Exception ex) {
			throw new RuntimeException("processPOIFSReaderEvent", ex);
		}
	}
}
