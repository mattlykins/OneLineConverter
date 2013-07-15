package com.mattlykins.onelineconverter;

import android.provider.BaseColumns;

public final class dbContract
{
	public dbContract()
	{
	}

	/* Inner class that defines the table contents */
	public static abstract class dBase implements BaseColumns
	{
		public static final String TABLE_NAME = "convtable";
		public static final String COLUMN_NAME_FROMSYMBOL = "fromsymbol";
		public static final String COLUMN_NAME_FROMTEXT = "fromtext";
		public static final String COLUMN_NAME_TOSYMBOL = "tosymbol";
		public static final String COLUMN_NAME_TOTEXT = "totext";
		public static final String COLUMN_NAME_MULTIBY = "multiby";
	}
}
