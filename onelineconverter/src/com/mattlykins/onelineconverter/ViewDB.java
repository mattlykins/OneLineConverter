package com.mattlykins.onelineconverter;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ViewDB extends Activity
{

	ListView list;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_db);
		context = this;
		list = (ListView)findViewById(R.id.list);
		
		String[] arrayColumns = new String[]{dBase.COLUMN_NAME_FROMSYMBOL,dBase.COLUMN_NAME_TOSYMBOL,dBase.COLUMN_NAME_MULTIBY};
		int[] arrayViewIDs = new int[]{R.id.tvListFrom,R.id.tvListTo,R.id.tvListMultiplyBy};
		
		dbHelper mydbHelper = new dbHelper(this);
		
		Cursor cursor = mydbHelper.GetAllRows();
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, arrayColumns, arrayViewIDs,SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        list.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_db, menu);
		return true;
	}

}
