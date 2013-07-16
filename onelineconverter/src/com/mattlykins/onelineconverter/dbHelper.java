package com.mattlykins.onelineconverter;

import java.util.ArrayList;
import java.util.List;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class dbHelper extends SQLiteOpenHelper
{

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "ConversionFactors.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + dBase.TABLE_NAME + " (" + dBase._ID + " INTEGER PRIMARY KEY,"
			+ dBase.COLUMN_NAME_FROMSYMBOL + TEXT_TYPE + COMMA_SEP + dBase.COLUMN_NAME_FROMTEXT + TEXT_TYPE + COMMA_SEP + dBase.COLUMN_NAME_TOSYMBOL
			+ TEXT_TYPE + COMMA_SEP + dBase.COLUMN_NAME_TOTEXT + TEXT_TYPE + COMMA_SEP + dBase.COLUMN_NAME_MULTIBY + TEXT_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + dBase.TABLE_NAME;

	public dbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public Cursor searchFrom(String sQuery, String[] columns)
	{
		String selection = dBase.COLUMN_NAME_FROMSYMBOL + "=?";
		String[] selectionArgs = new String[]
		{ sQuery };

		return query(selection, selectionArgs, columns);
	}
	
	public Cursor searchTo(String sQuery, String[] columns)
	{
		String selection = dBase.COLUMN_NAME_TOSYMBOL + "=?";
		String[] selectionArgs = new String[]
		{ sQuery };

		return query(selection, selectionArgs, columns);
	}

	private Cursor query(String selection, String[] selectionArgs, String[] columns)
	{

		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(dBase.TABLE_NAME,columns, selection, selectionArgs, null, null, null);

		if (cursor == null)
		{
			return null;
		}
		else if (!cursor.moveToFirst())
		{
			cursor.close();
			return null;
		}
		return cursor;
	}
	
	  public List<Convs> getAllConvs() {
	        List<Convs> convList = new ArrayList<Convs>();
	        // Select All Query
	        String selectQuery = "SELECT  * FROM " + dBase.TABLE_NAME;
	 
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	 
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                Convs conv = new Convs();
	                conv.setFromSymbol(cursor.getString(dBase.NDEX_FROMSYMBOL));
	                conv.setFromText (cursor.getString(dBase.NDEX_FROMTEXT));
	                conv.setToSymbol(cursor.getString(dBase.NDEX_TOSYMBOL));
	                conv.setToText(cursor.getString(dBase.NDEX_TOTEXT));
	                conv.setMultiBy(cursor.getString(dBase.NDEX_MULTIBY));
	                convList.add(conv);
	            } while (cursor.moveToNext());
	        }
	 
	        // return conv list
	        return convList;
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
