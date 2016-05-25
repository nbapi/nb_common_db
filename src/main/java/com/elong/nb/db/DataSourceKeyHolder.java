package com.elong.nb.db;

public class DataSourceKeyHolder {

	private static final ThreadLocal<String> ContextKeyHolder = new ThreadLocal<String>();

	static void setDataSourceKey(String dataSourceKey) {
		ContextKeyHolder.set(dataSourceKey);
	}

	static String getDataSourceKey() {
		return ContextKeyHolder.get();
	}

	public static void clearDataSourceKey() {
		ContextKeyHolder.remove();
	}

}
