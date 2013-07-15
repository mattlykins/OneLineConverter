package com.mattlykins.onelineconverter;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddToDB extends Activity implements OnClickListener
{

	EditText etFromSymbol, etFromText, etToSymbol, etToText, etMultiBy;
	Button bSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_to_db);

		etFromSymbol = (EditText) findViewById(R.id.etFromSymbol);
		etFromText = (EditText) findViewById(R.id.etFromText);
		etToSymbol = (EditText) findViewById(R.id.etToSymbol);
		etToText = (EditText) findViewById(R.id.etToText);
		etMultiBy = (EditText) findViewById(R.id.etMultiBy);
		bSubmit = (Button) findViewById(R.id.bSubmit);

		bSubmit.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_to_db, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.bSubmit:
				String tFS,tFT,tTS,tTT,tMB;
				tFS = etFromSymbol.getText().toString();
				tFT = etFromText.getText().toString();
				tTS = etToSymbol.getText().toString();
				tTT = etToText.getText().toString();
				tMB = etMultiBy.getText().toString();
				
				if( tFS.equals(null) || tFS.equals("") ||
					tFT.equals(null) || tFT.equals("") ||
					tTS.equals(null) || tTS.equals("") ||
					tTT.equals(null) || tTT.equals("") ||
					tMB.equals(null) || tMB.equals("") )
				{
					Toast toast = Toast.makeText(this, "Fill in all blanks", Toast.LENGTH_LONG);
					toast.show();					
				}
				else
				{
					dbHelper mydbHelper = new dbHelper(this);
					SQLiteDatabase mydB = mydbHelper.getWritableDatabase();

					ContentValues values = new ContentValues();
					values.put(dBase.COLUMN_NAME_FROMSYMBOL, tFS);
					values.put(dBase.COLUMN_NAME_FROMTEXT, tFT);
					values.put(dBase.COLUMN_NAME_TOSYMBOL, tTS);
					values.put(dBase.COLUMN_NAME_TOTEXT, tTT);
					values.put(dBase.COLUMN_NAME_MULTIBY, tMB);
					long newRowId = mydB.insert(dBase.TABLE_NAME, null, values);	
					
					
					etFromSymbol.setText("");
					etFromText.setText("");
					etToSymbol.setText("");
					etToText.setText("");
					etMultiBy.setText("");
					
				}
				break;
		}

	}
}
