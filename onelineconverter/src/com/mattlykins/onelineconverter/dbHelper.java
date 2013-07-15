package com.mattlykins.onelineconverter;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper
{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ConversionFactors.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + dBase.TABLE_NAME + " (" +
	    dBase._ID + " INTEGER PRIMARY KEY," +
	    dBase.COLUMN_NAME_FROMSYMBOL + TEXT_TYPE + COMMA_SEP +
	    dBase.COLUMN_NAME_FROMTEXT + TEXT_TYPE + COMMA_SEP +
	    dBase.COLUMN_NAME_TOSYMBOL + TEXT_TYPE + COMMA_SEP +
	    dBase.COLUMN_NAME_TOTEXT + TEXT_TYPE + COMMA_SEP +
	    dBase.COLUMN_NAME_MULTIBY + TEXT_TYPE + " )";
	
	private static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + dBase.TABLE_NAME;
	
	
	public dbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		
	}

}
