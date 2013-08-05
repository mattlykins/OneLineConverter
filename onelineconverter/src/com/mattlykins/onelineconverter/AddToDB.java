package com.mattlykins.onelineconverter;

import java.text.DecimalFormat;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddToDB extends Activity implements OnClickListener
{

	EditText etFromSymbol, etFromText, etToSymbol, etToText, etMultiBy;
	Button bSubmit, bE;
	Boolean lgEdit;
	int UpdateID;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_to_db);

		Bundle extras = getIntent().getExtras();
		if (extras == null)
		{
			lgEdit = false;
			UpdateID = 0;
		}
		else
		{
			lgEdit = true;
			UpdateID = extras.getInt("index");
		}

		etFromSymbol = (EditText) findViewById(R.id.etFromSymbol);
		etFromText = (EditText) findViewById(R.id.etFromText);
		etToSymbol = (EditText) findViewById(R.id.etToSymbol);
		etToText = (EditText) findViewById(R.id.etToText);
		etMultiBy = (EditText) findViewById(R.id.etMultiBy);
		bSubmit = (Button) findViewById(R.id.bSubmit);
		bE = (Button) findViewById(R.id.bE);

		bSubmit.setOnClickListener(this);
		bE.setOnClickListener(this);

		// If extras exist, plug in the text values
		if (lgEdit)
		{
			etFromSymbol.setText(extras.getString("sFrom"));
			etFromText.setText(extras.getString("sFromText"));
			etToSymbol.setText(extras.getString("sTo"));
			etToText.setText(extras.getString("sFromText"));
			etMultiBy.setText(extras.getString("sMultiBy"));		
			
			
			Button bDelete = (Button)findViewById(R.id.bDelete);
			//Make button visible
			bDelete.setVisibility(0);
			bDelete.setOnClickListener(this);
		}
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
				String tFS,
				tFT,
				tTS,
				tTT,
				tMB;
				tFS = etFromSymbol.getText().toString();
				tFT = etFromText.getText().toString();
				tTS = etToSymbol.getText().toString();
				tTT = etToText.getText().toString();
				tMB = etMultiBy.getText().toString();

				if (tFS.equals(null) || tFS.equals("") || tFT.equals(null) || tFT.equals("") || tTS.equals(null) || tTS.equals("") || tTT.equals(null)
						|| tTT.equals("") || tMB.equals(null) || tMB.equals(""))
				{
					Toast toast = Toast.makeText(this, "Fill in all blanks", Toast.LENGTH_LONG);
					toast.show();
				}
				else
				{
					DecimalFormat dfSigFig = new DecimalFormat("0.####E0");
					
					if (lgEdit)
					{
						dbHelper mydbHelper = new dbHelper(this);
						String sFrom, sFromText, sTo, sToText, sMultiBy;
						sFrom = etFromSymbol.getText().toString();
						sFromText = etFromText.getText().toString();
						sTo = etToSymbol.getText().toString();
						sToText = etToText.getText().toString();
						sMultiBy = etMultiBy.getText().toString();
						
						//Set the format
						String newsMultiBy = dfSigFig.format(Double.parseDouble(sMultiBy));
						
						
						mydbHelper.Update_ByID(UpdateID, sFrom, sFromText, sTo, sToText, newsMultiBy);						
					}
					else
					{

						dbHelper mydbHelper = new dbHelper(this);
						SQLiteDatabase mydB = mydbHelper.getWritableDatabase();
						
						String newsMultiBy = dfSigFig.format(Double.parseDouble(tMB));

						ContentValues values = new ContentValues();
						values.put(dBase.COLUMN_NAME_FROMSYMBOL, tFS);
						values.put(dBase.COLUMN_NAME_FROMTEXT, tFT);
						values.put(dBase.COLUMN_NAME_TOSYMBOL, tTS);
						values.put(dBase.COLUMN_NAME_TOTEXT, tTT);
						values.put(dBase.COLUMN_NAME_MULTIBY, newsMultiBy);
						long newRowId = mydB.insert(dBase.TABLE_NAME, null, values);

						// Set up the inverse conversion
						Double dIMB = 1 / Double.parseDouble(tMB);
						String sIMB = dfSigFig.format(dIMB);

						ContentValues Ivalues = new ContentValues();
						Ivalues.put(dBase.COLUMN_NAME_FROMSYMBOL, tTS);
						Ivalues.put(dBase.COLUMN_NAME_FROMTEXT, tTT);
						Ivalues.put(dBase.COLUMN_NAME_TOSYMBOL, tFS);
						Ivalues.put(dBase.COLUMN_NAME_TOTEXT, tFT);
						Ivalues.put(dBase.COLUMN_NAME_MULTIBY, sIMB);
						long InewRowId = mydB.insert(dBase.TABLE_NAME, null, Ivalues);

						etFromSymbol.setText("");
						etFromText.setText("");
						etToSymbol.setText("");
						etToText.setText("");
						etMultiBy.setText("");
					}

				}
				break;
			case R.id.bE:
				etMultiBy.setText(etMultiBy.getText() + "E");
				break;
			case R.id.bDelete:
				if (lgEdit)
				{
					dbHelper mydbHelper = new dbHelper(this);
					mydbHelper.Delete_ByID(UpdateID);					
				}
				else
				{
					Log.wtf("WTF", "BAD STUFF");
				}
				break;
		}

	}
}
