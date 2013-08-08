package com.mattlykins.onelineconverter;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ViewDB extends Activity implements OnItemClickListener
{

	ListView list;
	Context context;
	final static String[] arrayColumns = new String[]
	{ dBase.COLUMN_NAME_FROMSYMBOL, dBase.COLUMN_NAME_TOSYMBOL, dBase.COLUMN_NAME_MULTIBY };
	final static int[] arrayViewIDs = new int[]
	{ R.id.tvListFrom, R.id.tvListTo, R.id.tvListMultiplyBy };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_db);
		context = this;
		list = (ListView) findViewById(R.id.list);

	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

		dbHelper mydbHelper = new dbHelper(this);

		Cursor cursor = mydbHelper.GetAllRows();

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, arrayColumns, arrayViewIDs,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		list.setAdapter(adapter);

		list.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_db, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		Cursor c = (Cursor) arg0.getItemAtPosition(arg2);
		Intent intent = new Intent(context, AddToDB.class);

		int index = c.getInt(dBase.NDEX_ID);
		String sFrom = c.getString(dBase.NDEX_FROMSYMBOL);
		String sFromText = c.getString(dBase.NDEX_FROMTEXT);
		String sTo = c.getString(dBase.NDEX_TOSYMBOL);
		String sToText = c.getString(dBase.NDEX_TOTEXT);
		String sMultiBy = c.getString(dBase.NDEX_MULTIBY);

		intent.putExtra("index", index);
		intent.putExtra("sFrom", sFrom);
		intent.putExtra("sFromText", sFromText);
		intent.putExtra("sTo", sTo);
		intent.putExtra("sToText", sToText);
		intent.putExtra("sMultiBy", sMultiBy);

		startActivity(intent);
	}

}
