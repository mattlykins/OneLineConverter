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
		
		public static final int NDEX_ID = 0;
		public static final int NDEX_FROMSYMBOL = 1;
		public static final int NDEX_FROMTEXT = 2;
		public static final int NDEX_TOSYMBOL = 3;
		public static final int NDEX_TOTEXT = 4;
		public static final int NDEX_MULTIBY = 5;
		
	}
}
