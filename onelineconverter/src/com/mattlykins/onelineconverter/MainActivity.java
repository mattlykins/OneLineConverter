package com.mattlykins.onelineconverter;

import java.util.ArrayList;
import java.util.List;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

	dbHelper mydbHelper = new dbHelper(this);

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
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_addtodb:
				Intent intent = new Intent(this, AddToDB.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
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

			double Value = Double.valueOf(sValue);

			boolean lgLinkFound = false;

			// Verify the unit conversion is potentially possible
			Cursor testFF = mydbHelper.searchFrom(sFromUnit, null);
			Cursor testTF = mydbHelper.searchTo(sFromUnit, null);
			if (testFF == null && testTF == null)
			{
				Toast toast = Toast.makeText(this, "Invalid From Unit", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			Cursor testFT = mydbHelper.searchFrom(sToUnit, null);
			Cursor testTT = mydbHelper.searchTo(sToUnit, null);
			if (testFT == null && testTT == null)
			{
				Toast toast = Toast.makeText(this, "Invalid To Unit", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			List<Integer> IDS = new ArrayList<Integer>();

			Double factor = junk(IDS);

			Double ConvertedValue = factor * Double.parseDouble(sValue);

			tvOutput.setText(ConvertedValue + "");

			String out = "FERRET: ";
			for (int i : IDS)
			{
				out = out + i + " ";
			}
			Toast toast = Toast.makeText(this, out, Toast.LENGTH_LONG);
			toast.show();

		}
		else
		{

		}

	}

	private List<Convs> AddToList(Cursor cursor, List<Integer> IDS)
	{
		List<Convs> List1 = new ArrayList<Convs>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			if (cursor != null)
			{
				boolean lgAddToList = true;
				Convs conv = new Convs(cursor.getString(dBase.NDEX_ID), cursor.getString(dBase.NDEX_FROMSYMBOL), cursor.getString(dBase.NDEX_FROMTEXT),
						cursor.getString(dBase.NDEX_TOSYMBOL), cursor.getString(dBase.NDEX_TOTEXT), cursor.getString(dBase.NDEX_MULTIBY));
				Log.d("FERRET", cursor.getString(dBase.NDEX_ID) + " " + cursor.getString(dBase.NDEX_FROMSYMBOL) + " " + cursor.getString(dBase.NDEX_FROMTEXT)
						+ " " + cursor.getString(dBase.NDEX_TOSYMBOL) + " " + cursor.getString(dBase.NDEX_TOTEXT) + " " + cursor.getString(dBase.NDEX_MULTIBY)
						+ "\n");
				for (int i : IDS)
				{
					if (i == Integer.parseInt(cursor.getString(dBase.NDEX_ID)))
					{
						lgAddToList = false;
						break;
					}
				}
				if (lgAddToList)
				{
					List1.add(conv);
					int id = Integer.parseInt(cursor.getString(dBase.NDEX_ID));
					IDS.add(id);
				}
			}

		}
		return List1;
	}

	private double scanOne(String sToUnit, boolean lgLinkFound)
	{
		Cursor cursor = mydbHelper.searchFrom(sFromUnit, null);
		if (cursor != null)
		{
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
			{
				if (cursor.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(sToUnit))
				{
					lgLinkFound = true;
					cursor.close();
					return Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY));
				}
			}
		}
		Cursor Bcursor = mydbHelper.searchTo(sFromUnit, null);
		if (Bcursor != null)
		{
			for (Bcursor.moveToFirst(); !Bcursor.isAfterLast(); Bcursor.moveToNext())
			{
				if (Bcursor.getString(dBase.NDEX_FROMSYMBOL).equalsIgnoreCase(sToUnit))
				{
					lgLinkFound = true;
					Bcursor.close();
					return (1 / Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY)));
				}
			}
		}

		return 0;
	}

	private double scanTwo(String sToUnit, boolean lgLinkFound)
	{
		Cursor cursor = mydbHelper.searchFrom(sFromUnit, null);
		Cursor cursorend = mydbHelper.searchTo(sToUnit, null);
		if (cursor != null && cursorend != null)
		{
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
			{
				for (cursorend.moveToFirst(); !cursorend.isAfterLast(); cursorend.moveToNext())
				{
					if (cursor.getString(dBase.NDEX_TOSYMBOL).equalsIgnoreCase(cursorend.getString(dBase.NDEX_FROMSYMBOL)))
					{
						lgLinkFound = true;
						return Double.valueOf(cursor.getString(dBase.NDEX_MULTIBY)) * Double.valueOf(cursorend.getString(dBase.NDEX_MULTIBY));

					}
				}
			}
		}
		return 0;
	}

	private double junk(List<Integer> IDS)
	{
		// List<Integer> IDS = new ArrayList<Integer>();

		// Create a list of convs where the From unit in the db query is the
		// user entered from unit
		List<Convs> FF = new ArrayList<Convs>();
		Cursor cursorFF = mydbHelper.searchFrom(sFromUnit, null);
		if (cursorFF != null)
		{
			FF = AddToList(cursorFF, IDS);

			// Check the array of convs to see if any of the dB To units equal
			// the user entered To Unit
			for (Convs C : FF)
			{
				if (C.getToSymbol().equals(sToUnit))
				{
					return Double.parseDouble(C.getMultiBy());
				}

			}
		}

		// Query the user To unit from the To side of db
		List<Convs> TT = new ArrayList<Convs>();
		Cursor cursorTT = mydbHelper.searchTo(sToUnit, null);
		if (cursorTT != null)
		{
			TT = AddToList(cursorTT, IDS);

			for (Convs L : FF)
			{
				for (Convs R : TT)
				{
					if (L.getToSymbol().equals(R.getFromSymbol()))
					{

						return Double.parseDouble(L.getMultiBy()) * Double.parseDouble(R.getMultiBy());
					}

				}
			}
		}

		// The three step
		for (Convs L : FF)
		{
			List<Convs> FF2 = new ArrayList<Convs>();
			Cursor cursorFF2 = mydbHelper.searchFrom(L.getToSymbol(), null);
			if (cursorFF2 != null)
			{
				FF2 = AddToList(cursorFF2, IDS);
				for (Convs R : TT)
				{
					for (Convs L2 : FF2)
					{
						if (L.getToSymbol().equals(L2.getFromSymbol()) && R.getFromSymbol().equals(L2.getToSymbol()))
						{
							Log.d("FERRET2", L.getFromSymbol() + " " + L.getFromText() + " " + L.getToSymbol() + " " + L.getToText() + " " + L.getMultiBy()	+ "\n");
							Log.d("FERRET2", R.getFromSymbol() + " " + R.getFromText() + " " + R.getToSymbol() + " " + R.getToText() + " " + R.getMultiBy()	+ "\n");
							Log.d("FERRET2", L2.getFromSymbol() + " " + L2.getFromText() + " " + L2.getToSymbol() + " " + L2.getToText() + " " + L2.getMultiBy() + "\n");

							return Double.parseDouble(L.getMultiBy()) * Double.parseDouble(R.getMultiBy()) * Double.parseDouble(L2.getMultiBy());
						}
					}
				}
			}
		}

		return 0;

	}
}
