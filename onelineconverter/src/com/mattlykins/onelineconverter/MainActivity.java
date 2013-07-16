package com.mattlykins.onelineconverter;

import java.util.ArrayList;
import java.util.List;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

			boolean lgLinkFound = false;
			int i = 0;

			dbHelper mydbHelper = new dbHelper(this);

			// List<Convs> convList = new ArrayList<Convs>();
			// convList = mydbHelper.getAllConvs();
			//
			// for( Convs con:convList )
			// {
			// String FS = con.getFromSymbol();
			// String TS = con.getToSymbol();
			// }

			Cursor cursor = mydbHelper.searchFrom(sFromUnit, null);
//			if (cursor != null)
//			{
//				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
//				{
//					if (cursor.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(sToUnit))
//					{
//						Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY));
//						tvOutput.setText(String.valueOf(output));
//						lgLinkFound = true;
//						break;
//					}
//				}
//			}
//
//			if (!lgLinkFound)
//			{
//
//				Cursor cursorend = mydbHelper.searchTo(sToUnit, null);
//				if (cursor != null)
//				{
//					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
//					{
//						for (cursorend.moveToFirst(); !cursorend.isAfterLast(); cursorend.moveToNext())
//						{
//							if (cursor.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(cursorend.getString(dBase.NDEX_FROMSYMBOL)))
//							{
//								Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY))
//										* Double.valueOf(cursorend.getString(dBase.NDEX_MULTIBY));
//								tvOutput.setText(String.valueOf(output));
//								lgLinkFound = true;
//								break;
//							}
//						}
//					}
//				}
//			}

			if (!lgLinkFound)
			{
				outerloop:
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
				{
					// Check to see if ToSymbol is sToUnit
					if (cursor.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(sToUnit))
					{
						Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY));
						tvOutput.setText(String.valueOf(output));
						lgLinkFound = true;
						break outerloop;
					}
					Cursor cursor2 = mydbHelper.searchFrom(cursor.getString(dBase.NDEX_TOSYMBOL), null);
					for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext())
					{
						// Check to see if ToSymbol is sToUnit
						if (cursor2.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(sToUnit))
						{
							Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY))
									* Double.valueOf(cursor2.getString(dBase.NDEX_MULTIBY));
							tvOutput.setText(String.valueOf(output));
							lgLinkFound = true;
							break outerloop;
						}
						Cursor cursor3 = mydbHelper.searchFrom(cursor2.getString(dBase.NDEX_TOSYMBOL), null);
						for (cursor3.moveToFirst(); !cursor3.isAfterLast(); cursor3.moveToNext())
						{
							// Check to see if ToSymbol is sToUnit
							if (cursor3.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(sToUnit))
							{
								Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY))
										* Double.valueOf(cursor2.getString(dBase.NDEX_MULTIBY)) * Double.valueOf(cursor3.getString(dBase.NDEX_MULTIBY));
								tvOutput.setText(String.valueOf(output));
								lgLinkFound = true;
								break outerloop;
							}
							Cursor cursor4 = mydbHelper.searchFrom(cursor3.getString(dBase.NDEX_TOSYMBOL), null);
							for (cursor4.moveToFirst(); !cursor4.isAfterLast(); cursor4.moveToNext())
							{
								if (cursor4.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(sToUnit))
								{
									Double output = Double.valueOf(sValue) * Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY))
											* Double.valueOf(cursor2.getString(dBase.NDEX_MULTIBY)) * Double.valueOf(cursor3.getString(dBase.NDEX_MULTIBY))
											* Double.valueOf(cursor4.getString(dBase.NDEX_MULTIBY));
									tvOutput.setText(String.valueOf(output));
									lgLinkFound = true;
									break outerloop;
								}
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
