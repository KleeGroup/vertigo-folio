package io.vertigo.folio.plugins.metadata.microsoft.excel;

import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.file.util.FileUtil;
import io.vertigo.folio.plugins.metadata.microsoft.AbstractMSMetaDataExtractorPlugin;
import io.vertigo.lang.Assertion;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author pchretien
 * @version $Id: MSExcelMetaDataExtractorPlugin.java,v 1.5 2014/02/27 10:21:46 pchretien Exp $
 */
public final class MSExcelMetaDataExtractorPlugin extends AbstractMSMetaDataExtractorPlugin {

	/** {@inheritDoc} */
	@Override
	protected String extractContent(final VFile file) throws Exception {
		if (file.getFileName().endsWith(".xls") && file.getLength() > 50 * 1024 * 1024) {
			return "TOO_BIG : " + file.getLength() / 1024 + "Ko";
			//throw new java.lang.UnsupportedOperationException("Fichier XLS trop gros pour �tre trait� : " + file.getLength() / 1024 + "Ko");
		}

		final StringBuilder sb = new StringBuilder();

		final POIFSFileSystem fs;
		try (final InputStream inputStream = file.createInputStream()) {
			fs = new POIFSFileSystem(inputStream);
		}

		final HSSFWorkbook wb = new HSSFWorkbook(fs);

		for (int k = 0; k < wb.getNumberOfSheets(); k++) {
			final HSSFSheet sheet = wb.getSheetAt(k);
			for (final Iterator<Row> rows = sheet.rowIterator(); rows.hasNext();) {
				final HSSFRow row = (HSSFRow) rows.next();
				final int c1 = row.getFirstCellNum();
				final int c2 = row.getLastCellNum();
				for (int c = c1; c < c2; c++) {
					final HSSFCell cell = row.getCell(c);
					if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
						sb.append(cell.getRichStringCellValue().getString()).append(' ');
					}
				}
			}
		}
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final VFile file) {
		Assertion.checkNotNull(file);
		//-----
		final String fileExtension = FileUtil.getFileExtension(file.getFileName());
		return "xls".equalsIgnoreCase(fileExtension);
	}
}
