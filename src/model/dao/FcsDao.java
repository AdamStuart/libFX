package model.dao;
/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;


public class FcsDao {

	private static FCSFileReader reader;
	private static FCSFileWriter writer;

	public static SortedMap<Number, Number> importFcs(String filePath) {
		try {
			reader = new FCSFileReader(new File(filePath));
			return reader.getDataTreeMap();
		} 
		catch (IOException e) 		{			System.err.println(e.getMessage());		}
		return Collections.emptySortedMap();
	}

	public static void exportFcs(HashMap<String, String> text, TableData data, String filePath) {
		try {
			writer = new FCSFileWriter(new File(filePath));
			writer.writeAll(text, data);  
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
