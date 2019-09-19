package com.aiop.lda.util;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;



public class SQLUtils {
	private static Logger LOG=LogManager.getLogger(SQLUtils.class.getName());
	
	private static final String SQL_FILE_NAME = "sql.xml";
	
	
	
	public static Map<String, String> getSQLList() {
		Document doc = null;
		Map<String, String> result = new LinkedHashMap<String, String>();
		InputStream sqlInputStream = null;
		try {
			sqlInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SQL_FILE_NAME);
			List<String> list = IOUtils.readLines(sqlInputStream, "UTF-8");
			StringBuilder buf = new StringBuilder();
			for (String e : list) {
				buf.append(e);
			}
			doc = DocumentHelper.parseText(buf.toString());
			Element root = doc.getRootElement();
			Iterator<Element> iter = root.elementIterator("sql");
			while (iter.hasNext()) {
				Element sql = (Element) iter.next();
				String id = sql.attributeValue("id");
				String sqlText = sql.getTextTrim();
				result.put(id, sqlText);
			}
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		} finally {
			if (sqlInputStream != null) {
				IOUtils.closeQuietly(sqlInputStream);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		Map<String, String>sqls=SQLUtils.getSQLList();
		System.out.println(sqls);
	}
	
	
	
}
