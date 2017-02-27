package io.vertigo.folio.metadata;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vertigo.app.AutoCloseableApp;
import io.vertigo.app.config.AppConfig;
import io.vertigo.core.component.di.injector.DIInjector;
import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.folio.impl.metadata.FileInfoMetaData;
import io.vertigo.folio.plugins.metadata.microsoft.MSMetaData;
import io.vertigo.folio.plugins.metadata.odf.ODFMetaData;
import io.vertigo.folio.plugins.metadata.ooxml.OOXMLCoreMetaData;
import io.vertigo.folio.plugins.metadata.pdf.PDFMetaData;
import io.vertigo.folio.plugins.metadata.tika.AutoTikaMetaData;
import io.vertigo.folio.plugins.metadata.txt.TxtMetaData;
import io.vertigo.lang.Assertion;

/**
 * Test de l'impl�mentation standard.
 *
 * @author pchretien
 * @version $Id: MetaDataManagerTest.java,v 1.5 2014/07/16 13:26:33 pchretien Exp $
 */
public final class MetaDataManagerTest {
	private static final Logger LOG = Logger.getLogger(MetaDataManagerTest.class);
	@Inject
	private MetaDataManager metaDataManager;
	@Inject
	private FileManager fileManager;

	private AutoCloseableApp app;

	@Before
	public final void setUp() throws Exception {
		app = new AutoCloseableApp(buildAppConfig());
		DIInjector.injectMembers(this, app.getComponentSpace());
	}

	@Before
	public final void tearDown() throws Exception {
		if (app == null) {
			app.close();
		}
	}

	private AppConfig buildAppConfig() {
		return MetaDataConfig.build();
	}

	private MetaDataSet buildMetaDataSet(final String fileName) {
		try {
			final URL fileURL = MetaDataManagerTest.class.getResource(fileName);
			Assertion.checkNotNull(fileURL, "File not found : {0}", fileName);
			final URI fileURI = MetaDataManagerTest.class.getResource(fileName).toURI();
			final VFile file = fileManager.createFile(new File(fileURI));
			return metaDataManager.extractMetaData(file);
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	//---------------------------doc, docx, odt--------------------------------

	/** Test DOC. */
	@Test
	public void testDoc() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/microsoft/doc/klee.doc");
		Assert.assertEquals("kleegroup", metaDataSet.getValue(MSMetaData.AUTHOR));
	}

	/** Test DOCX. */
	@Test
	public void testDocX() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/ooxml/docx/klee.docx");
		printMetaData(metaDataSet);
		Assert.assertEquals("kleegroup", metaDataSet.getValue(OOXMLCoreMetaData.CREATOR));
		//		Assert.assertEquals("Documentation Interface CRM --> Agresso", metaDataContainer.getValue(OOXMLCoreMetaData.TITLE));
		//		Assert.assertEquals("Amine Kouddane", metaDataContainer.getValue(OOXMLCoreMetaData.CREATOR));
		//		Assert.assertEquals("KleeGroup", metaDataContainer.getValue(OOXMLExtendedMetaData.COMPANY));
		//		Assert.assertEquals("133", metaDataContainer.getValue(OOXMLCoreMetaData.REVISION_NUMBER));
		//		Assert.assertEquals(14, metaDataContainer.getValue(OOXMLExtendedMetaData.PAGES));
	}

	/** Test ODT. */
	@Test
	public void testOdt() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/odf/klee.odt");
		printMetaData(metaDataSet);
		Assert.assertEquals("kleegroup", metaDataSet.getValue(ODFMetaData.INITIAL_CREATOR));
		//		Assert.assertEquals("E. Paumier", metaDataContainer.getValue(ODFMetaData.INITIAL_CREATOR));
		//		Assert.assertEquals("Edouard Paumier", metaDataContainer.getValue(ODFMetaData.CREATOR));
		//		Assert.assertEquals(10, metaDataContainer.getValue(ODFMetaData.EDITING_CYCLES));
		//		Assert.assertEquals("Moteur d'�dition - Manuel de l'admistrateur", metaDataContainer.getValue(ODFMetaData.TITLE));
		//		Assert.assertEquals("Spark Archives", metaDataContainer.getValue(ODFMetaData.SUBJECT));
		//		Assert.assertEquals(19, metaDataContainer.getValue(ODFMetaData.PAGE_COUNT));
		//		Assert.assertEquals(460, metaDataContainer.getValue(ODFMetaData.PARAGRAPH_COUNT));
		//		Assert.assertEquals(24, metaDataContainer.getValue(ODFMetaData.TABLE_COUNT));
		//		Assert.assertEquals(21242, metaDataContainer.getValue(ODFMetaData.CHARACTER_COUNT));
	}

	//-----------------------------txt, pdf, pdf-a-----------------------------
	/** Test TXT. */
	@Test
	public void testTxt() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/txt/lautreamont.txt");
		final String content = (String) metaDataSet.getValue(TxtMetaData.CONTENT);
		Assert.assertTrue(content.contains("cantiques"));
	}

	/** Test PDF. */
	@Test
	public void testPdf() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/pdf/recette.pdf");
		Assert.assertEquals("Agglo", metaDataSet.getValue(PDFMetaData.AUTHOR));
	}

	/** Test PDF/A. */
	@Test
	public void testPdfaValid() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/pdf/VERCINGETORIX-pdfa.pdf");
		Assert.assertEquals("true", metaDataSet.getValue(PDFMetaData.PDFA));
		Assert.assertEquals("VALID", metaDataSet.getValue(PDFMetaData.PDFA_VALIDATION_MSG));
	}

	/** Test PDF/A. */
	@Test
	public void testPdfaInvalid() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/pdf/recette.pdf");
		Assert.assertEquals("false", metaDataSet.getValue(PDFMetaData.PDFA));
		Assert.assertNotSame("VALID", metaDataSet.getValue(PDFMetaData.PDFA_VALIDATION_MSG));
	}

	//-----------------------------ppt, pptx-----------------------------------

	/**Test PPT. */
	@Test
	public void testPpt() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/microsoft/ppt/Architecture.ppt");
		Assert.assertEquals("kleegroup", metaDataSet.getValue(MSMetaData.AUTHOR));
	}

	/** Test PPTX. */
	@Test
	public void testPptX() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/ooxml/pptx/Architecture.pptx");
		printMetaData(metaDataSet);
		Assert.assertEquals("kleegroup", metaDataSet.getValue(OOXMLCoreMetaData.CREATOR));
		//		Assert.assertEquals("Waveform", metaDataContainer.getValue(OOXMLExtendedMetaData.TEMPLATE));
		//		Assert.assertEquals(1, metaDataContainer.getValue(OOXMLExtendedMetaData.MMCLIPS));
	}

	//-----------------------------xxxxx-----------------------------------

	/**
	 * Test XLS.
	 */
	@Test
	public void testXls() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/microsoft/xls/132-2_comptages_2004.xls");
		Assert.assertEquals("PARC DU MERCANTOUR", metaDataSet.getValue(MSMetaData.AUTHOR));
	}

	//	/**
	//	 */
	//	public void testMp3()   {
	//		final MetaDataContainer metaDataContainer = buildMDC("data/mp3/198 One Two Three Four.mp3");
	//		Assert.assertEquals("Feist", metaDataContainer.getValue(Mp3MetaData.ARTIST));
	//	}

	/**
	 * Test XLSX.
	 */
	@Test
	public void testXlsX() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/ooxml/xlsx/Champs.xlsx");
		printMetaData(metaDataSet);
		Assert.assertEquals("epaumier", metaDataSet.getValue(OOXMLCoreMetaData.CREATOR));
	}

	/**
	 * Test XLSX.
	 */
	@Test
	public void testUndefinded() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/undefined/undefined.zip");
		//Le type de fichier zip n'�tant pas d�clar�, on  ne r�cup�re que les donn�es relatives au fichier
		printMetaData(metaDataSet);
		//MetaData de fichier + le content que tika r�cup�re en ouvrant le zip
		Assert.assertEquals(FileInfoMetaData.values().length + 1, metaDataSet.getMetaDatas().size());
	}

	/**
	 * Test MBOX.
	 */
	@Test
	public void testMbox() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/autoparse/complex.mbox");
		printMetaData(metaDataSet);
	}

	/**
	 * Test TIKA.
	 */
	@Test
	public void testTikaDetector() {
		final MetaDataSet metaDataSet = buildMetaDataSet("data/autodetect/klee.mysterious");
		printMetaData(metaDataSet);
		Assert.assertEquals("kleegroup", metaDataSet.getValue(AutoTikaMetaData.CREATOR));
	}

	private void printMetaData(final MetaDataSet metaDataSet) {
		if (LOG.isDebugEnabled()) {
			for (final MetaData md : metaDataSet.getMetaDatas()) {
				LOG.debug(md.toString() + " = " + metaDataSet.getValue(md));
			}
		}
	}
}
