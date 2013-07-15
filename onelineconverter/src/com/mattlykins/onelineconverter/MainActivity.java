package com.mattlykins.onelineconverter;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{

	EditText etTextEntry;
	Button bConvert;
	TextView tvOutput;
	String sEntry, sValue, sFromUnit, sToUnit;
	String smtocm[] =
	{ "m", "cm", "100" };
	String skmtom[] =
	{ "km", "m", "1000" };
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etTextEntry = (EditText) findViewById(R.id.etTextEntry);
		bConvert = (Button) findViewById(R.id.bConvert);
		tvOutput = (TextView) findViewById(R.id.tvOutput);

		bConvert.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		sEntry = etTextEntry.getText().toString();
		String[] sTokens = sEntry.split(" ");

		if (sTokens.length == 3)
		{
			sValue = sTokens[0].trim();
			sFromUnit = sTokens[1].trim();
			sToUnit = sTokens[2].trim();

			dbHelper mydbHelper = new dbHelper(context);
			SQLiteDatabase mydB = mydbHelper.getReadableDatabase();

			boolean firsttime = false;
			Cursor cursor = mydB.query(dBase.TABLE_NAME, null, dBase.COLUMN_NAME_FROMSYMBOL + "=?", new String[]
			{ sFromUnit }, null, null, null, null);
			if (cursor != null)
			{
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
				{
					if (cursor.getString(3).equalsIgnoreCase(sToUnit))
					{
						Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(5));
						tvOutput.setText(String.valueOf(output));
						firsttime = true;
						break;
					}
				}
			}
			if (!firsttime)
			{
				boolean secondtime = false;
				Cursor cursor2 = mydB.query(dBase.TABLE_NAME, null, dBase.COLUMN_NAME_TOSYMBOL + "=?", new String[]
				{ sToUnit }, null, null, null, null);
				if (cursor2 != null)
				{
					for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
					{
						for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
						{
							if (cursor.getString(3).equalsIgnoreCase(cursor2.getString(1)))
							{
								Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(5))*Double.valueOf(cursor2.getString(5));;
								tvOutput.setText(String.valueOf(output));
								secondtime = true;
								break;
							}
						}
					}
				}
			}

		}
		else
		{

		}

	}

}
