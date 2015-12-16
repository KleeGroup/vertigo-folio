package io.vertigo.folio.plugins.metadata.image;

//import java.io.InputStream;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.folio.impl.metadata.MetaDataExtractorPlugin;
import io.vertigo.folio.metadata.MetaDataContainer;
import io.vertigo.lang.Assertion;

//import com.drew.imaging.jpeg.JpegMetadataReader;
//import com.drew.metadata.Metadata;

/**
 * @author pchretien
 * @version $Id: JpegMetaDataExtractorPlugin.java,v 1.4 2014/01/28 18:49:34 pchretien Exp $
 */
public final class JpegMetaDataExtractorPlugin implements MetaDataExtractorPlugin {

	/** {@inheritDoc} */
	@Override
	public MetaDataContainer extractMetaData(final VFile file) throws Exception {
		throw new UnsupportedOperationException();
		/*		Assertion.notNull(fileInfo);
		//-----
		final MetaDataContainer2<JpegMetaData> metaDataContainer = new MetaDataContainer2Impl<JpegMetaData>();
		//-----
		// http://www.drewnoakes.com/code/exif/sampleUsage.html
		final InputStream inputStream = fileInfo.createInputStream();
		final Metadata metadata;
		try {
			metadata = JpegMetadataReader.readMetadata(inputStream);
		} finally {
			inputStream.close();
		}
		 */
		// for (final Iterator directories = metadata.getDirectoryIterator();
		// directories
		// .hasNext();) {
		// final Directory directory = (Directory) directories.next();
		// for (final Iterator tags = directory.getTagIterator(); tags
		// .hasNext();) {
		// final Tag tag = (Tag) tags.next();
		// // use Tag.toString()
		// if (tag.getDescription() != null) {
		// throw new UnsupportedOperationException();
		// // ????????????????????
		// // ????????????????????
		// // ????Mettre les tags sous forme de m�tadonn�es
		// // ????????????????????
		// // ????????????????????
		// // document.add(createField(tag));
		// }
		// }
		// }

		//-----
		//return metaDataContainer;
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		throw new UnsupportedOperationException();
	}
}
