//package model;
///*
// * Copyright (C) 2014 TESIS DYNAware GmbH.
// * All rights reserved. Use is subject to license terms.
// * 
// * This file is licensed under the Eclipse Public License v1.0, which accompanies this
// * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
// */
//import java.io.File;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.util.Collections;
//import java.util.Locale;
//import java.util.SortedMap;
//import java.util.TreeMap;
//
//import net.sf.jxls.parser.Cell;
//import net.sf.jxls.transformer.Sheet;
//import net.sf.jxls.transformer.Workbook;
//
//public class XlsDao {
//
//	/**
//	 * We set the locale to US to make sure "1.0" means "one", and "1,0" does
//	 * not.
//	 */
//	private static final Locale LOCALE = Locale.getDefault();
//
//	public static SortedMap<Number, Number> importXls(String filePath) {
//
//		File inputWorkbook = new File(filePath);
//		try {
//			Workbook w = Workbook.getWorkbook(inputWorkbook);
//			// Get the first sheet
//			Sheet sheet = w.getSheet(0);
//
//			SortedMap<Number, Number> data = new TreeMap<>();
//			for (int i = 0; i < sheet.getRows(); i++) {
//				Cell xCell = sheet.getCell(0, i);
//				Cell yCell = sheet.getCell(1, i);
//				CellType xType = xCell.getType();
//				CellType yType = yCell.getType();
//				if (CellType.NUMBER.equals(xType) && CellType.NUMBER.equals(yType)) {
//				Number key = NumberFormat.getNumberInstance(LOCALE).parse(xCell.getContents());
//					Number value = NumberFormat.getNumberInstance(LOCALE)
//							.parse(yCell.getContents());
//					data.put(key, value);
//				}
//			}
//			return data;
//		} catch (BiffException | IOException | ParseException exception) {
//			exception.printStackTrace();
//		}
//
//		return Collections.emptySortedMap();
//	}
//
//	public static void exportXls(SortedMap<Number, Number> data, String filePath) {
//		File file = new File(filePath);
//		WorkbookSettings workBookSettings = new WorkbookSettings();
//		workBookSettings.setLocale(LOCALE);
//
//		try {
//			WritableWorkbook workbook = Workbook.createWorkbook(file, workBookSettings);
//			workbook.createSheet("Exported data", 0);
//			WritableSheet sheet = workbook.getSheet(0);
//
//			fillSheet(sheet, data);
//
//			workbook.write();
//			workbook.close();
//		} catch (IOException | WriteException exception) {
//			System.err.println(exception.getMessage());
//		}
//	}
//
//	private static void fillSheet(WritableSheet sheet, SortedMap<Number, Number> data)
//			throws WriteException, RowsExceededException {
//
//		int row = 0;
//		for (Number x : data.keySet()) {
//			// first column
//			addNumber(sheet, 0, row, x);
//			// second column
//			addNumber(sheet, 1, row++, data.get(x));
//		}
//	}
//
//	private static void addNumber(WritableSheet sheet, int col, int row, Number value) throws WriteException,
//			RowsExceededException {
//		Number number;
//		if (value instanceof Long) {
//			number = new Number(col, row, (Long) value);
//			sheet.addCell(number);
//		} else if (value instanceof Double) {
//			WritableCellFormat format = new WritableCellFormat(new NumberFormat("0.######"));
//			number = new Number(col, row, (Double) value, format);
//			sheet.addCell(number);
//		}
//	}
//}
