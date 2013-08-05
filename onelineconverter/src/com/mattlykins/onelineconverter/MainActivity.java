package com.mattlykins.onelineconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
				
			case R.id.action_viewdb:
				Intent intent1 = new Intent(this, ViewDB.class);
				startActivity(intent1);
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
			
			//Split From Unit into tokens
			StringTokenizer tokFrom = new StringTokenizer(sFromUnit, "[*/]", true);
			String[] FromTokens = new String[tokFrom.countTokens()+1];
			int FromDex = 0;		
			
			
			while(tokFrom.hasMoreElements())
			{
				FromTokens[FromDex] = tokFrom.nextToken();
				Log.d("TOKENIZER",FromTokens[FromDex]+ " " + String.valueOf(tokFrom.countTokens()) + "\n");
				FromDex += 1;
			}
			
			//Split To Unit into tokens
			StringTokenizer tokTo = new StringTokenizer(sToUnit, "[*/]", true);
			String[] ToTokens = new String[tokTo.countTokens()+1];
			int ToDex = 0;
			
			while(tokTo.hasMoreElements())
			{
				ToTokens[ToDex] = tokTo.nextToken();
				Log.d("TOKENIZER",ToTokens[ToDex]+ " " + String.valueOf(tokTo.countTokens()) + "\n");
				ToDex += 1;
			}			
			
			
			//Run conversion on each part of the From Unit
			Double TotalConvFactor = 1.0;
			
			//Verify dimensional compatibility
			if( FromDex == ToDex )
			{				
				for( int j = 0; j < FromDex; j++ )
				{
					if( !FromTokens[j].equals("*") && !FromTokens[j].equals("/")
							&& !ToTokens[j].equals("*") && !ToTokens[j].equals("/"))
					{
						//Call conversion and return conversion factor
						
						Log.d("FERRET","Calling junkwrapper with " + sFromUnit + " " + sToUnit + "\n");
						Double result =	FindFactorWrapper(FromTokens[j],ToTokens[j]);
						Log.d("FERRET","Junkwrapper returned " + result + "\n");
						
						if( j != 0 && FromTokens[j-1].equals("/") && ToTokens[j-1].equals("/"))
						{
							Log.d("FERRET","TotalConvFactor = " + TotalConvFactor + "  result=" + result + "\n");
							TotalConvFactor /= result;
						}
						else
						{
							TotalConvFactor *= result;
						}
						
					}
				}				
			}
			else
			{
				//Bad Stuff
			}
			
			String ConvertedValue = String.valueOf(Double.parseDouble(sValue)*TotalConvFactor);
			tvOutput.setText(ConvertedValue + "");
		}
		else
		{

		}

	}
	
	private double FindFactorWrapper(String sFromUnit,String sToUnit)
	{
		// Verify the unit conversion is potentially possible
		Cursor testFF = mydbHelper.searchFrom(sFromUnit, null);
		Cursor testTF = mydbHelper.searchTo(sFromUnit, null);
		if (testFF == null && testTF == null)
		{
			Toast toast = Toast.makeText(this, "Invalid From Unit", Toast.LENGTH_SHORT);
			toast.show();
			return 0;
		}

		Cursor testFT = mydbHelper.searchFrom(sToUnit, null);
		Cursor testTT = mydbHelper.searchTo(sToUnit, null);
		if (testFT == null && testTT == null)
		{
			Toast toast = Toast.makeText(this, "Invalid To Unit", Toast.LENGTH_SHORT);
			toast.show();
			return 0;
		}

		List<Integer> IDS = new ArrayList<Integer>();
		
		
		Log.d("FERRET","Calling junk with " + sFromUnit + " " + sToUnit + "\n");
		return(FindFactor(sFromUnit,sToUnit,IDS));	
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
//				for (int i : IDS)
//				{
//					if (i == Integer.parseInt(cursor.getString(dBase.NDEX_ID)))
//					{
//						lgAddToList = false;
//						break;
//					}
//				}
				//Investigate IDS system - maybe remove
				if (lgAddToList)
				{
					Log.d("FERRET", cursor.getString(dBase.NDEX_ID) + " " + cursor.getString(dBase.NDEX_FROMSYMBOL) + " " + cursor.getString(dBase.NDEX_FROMTEXT)
							+ " " + cursor.getString(dBase.NDEX_TOSYMBOL) + " " + cursor.getString(dBase.NDEX_TOTEXT) + " " + cursor.getString(dBase.NDEX_MULTIBY)
							+ "\n");
					List1.add(conv);
					int id = Integer.parseInt(cursor.getString(dBase.NDEX_ID));
					IDS.add(id);
				}
			}

		}
		return List1;
	}

	private double FindFactor(String sFromUnit, String sToUnit, List<Integer> IDS)
	{
		// List<Integer> IDS = new ArrayList<Integer>();

		// Create a list of convs where the From unit in the db query is the
		// user entered from unit
		List<Convs> FF = new ArrayList<Convs>();
		Cursor cursorFF = mydbHelper.searchFrom(sFromUnit, null);
		if (cursorFF != null)
		{
			FF = AddToList(cursorFF, IDS);
			cursorFF.close();

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
			cursorTT.close();

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
				cursorFF2.close();
				for (Convs R : TT)
				{
					for (Convs L2 : FF2)
					{
						if (L.getToSymbol().equals(L2.getFromSymbol()) && R.getFromSymbol().equals(L2.getToSymbol()))
						{
							Log.d("THREESTEP", L.getFromSymbol() + " " + L.getFromText() + " " + L.getToSymbol() + " " + L.getToText() + " " + L.getMultiBy()
									+ "\n");							
							Log.d("THREESTEP",
									L2.getFromSymbol() + " " + L2.getFromText() + " " + L2.getToSymbol() + " " + L2.getToText() + " " + L2.getMultiBy() + "\n");
							Log.d("THREESTEP", R.getFromSymbol() + " " + R.getFromText() + " " + R.getToSymbol() + " " + R.getToText() + " " + R.getMultiBy()
									+ "\n");

							return Double.parseDouble(L.getMultiBy()) * Double.parseDouble(R.getMultiBy()) * Double.parseDouble(L2.getMultiBy());
						}
					}
				}
			}
		}

		// The four step
		for (Convs L : FF)
		{
			List<Convs> FF2 = new ArrayList<Convs>();
			Cursor cursorFF2 = mydbHelper.searchFrom(L.getToSymbol(), null);
			if (cursorFF2 != null)
			{
				FF2 = AddToList(cursorFF2, IDS);
				cursorFF2.close();
				for (Convs R : TT)
				{
					List<Convs> TT2 = new ArrayList<Convs>();
					Cursor cursorTT2 = mydbHelper.searchTo(R.getFromSymbol(), null);
					if (cursorTT2 != null)
					{
						TT2 = AddToList(cursorTT2, IDS);
						cursorTT2.close();
						for (Convs L2 : FF2)
						{
							for (Convs R2 : TT2)
							{
								if (L.getToSymbol().equals(L2.getFromSymbol()) && R.getFromSymbol().equals(R2.getToSymbol())
										&& L2.getToSymbol().equals(R2.getFromSymbol()))
								{
									Log.d("FOURSTEP",
											L.getFromSymbol() + " " + L.getFromText() + " " + L.getToSymbol() + " " + L.getToText() + " " + L.getMultiBy()
													+ "\n");
									Log.d("FOURSTEP",
											L2.getFromSymbol() + " " + L2.getFromText() + " " + L2.getToSymbol() + " " + L2.getToText() + " " + L2.getMultiBy()
													+ "\n");
									Log.d("FOURSTEP",
											R2.getFromSymbol() + " " + R2.getFromText() + " " + R2.getToSymbol() + " " + R2.getToText() + " " + R2.getMultiBy()
													+ "\n");
									Log.d("FOURSTEP",
											R.getFromSymbol() + " " + R.getFromText() + " " + R.getToSymbol() + " " + R.getToText() + " " + R.getMultiBy()
													+ "\n");									

									return Double.parseDouble(L.getMultiBy()) * Double.parseDouble(R.getMultiBy()) * Double.parseDouble(L2.getMultiBy())
											* Double.parseDouble(R2.getMultiBy());
								}
							}
						}
					}
				}
			}
		}

		// The Five Step
		for (Convs L : FF)
		{
			List<Convs> FF2 = new ArrayList<Convs>();
			Cursor cursorFF2 = mydbHelper.searchFrom(L.getToSymbol(), null);
			if (cursorFF2 != null)
			{
				FF2 = AddToList(cursorFF2, IDS);
				cursorFF2.close();
				for (Convs R : TT)
				{
					List<Convs> TT2 = new ArrayList<Convs>();
					Cursor cursorTT2 = mydbHelper.searchTo(R.getFromSymbol(), null);
					if (cursorTT2 != null)
					{
						TT2 = AddToList(cursorTT2, IDS);
						cursorTT2.close();
						for (Convs L2 : FF2)
						{
							List<Convs> FF3 = new ArrayList<Convs>();
							Cursor cursorFF3 = mydbHelper.searchFrom(L2.getToSymbol(), null);
							if (cursorFF3 != null)
							{
								FF3 = AddToList(cursorFF3, IDS);
								cursorFF3.close();

								for (Convs R2 : TT2)
								{
									for (Convs L3 : FF3)
									{

										if (L.getToSymbol().equals(L2.getFromSymbol()) && R.getFromSymbol().equals(R2.getToSymbol())
												&& L2.getToSymbol().equals(L3.getFromSymbol()) && L3.getToSymbol().equals(R2.getFromSymbol()))
										{
											Log.d("FIVESTEP", L.getFromSymbol() + " " + L.getFromText() + " " + L.getToSymbol() + " " + L.getToText() + " "
													+ L.getMultiBy() + "\n");
											Log.d("FIVESTEP", L2.getFromSymbol() + " " + L2.getFromText() + " " + L2.getToSymbol() + " " + L2.getToText() + " "
													+ L2.getMultiBy() + "\n");
											Log.d("FIVESTEP", L3.getFromSymbol() + " " + L3.getFromText() + " " + L3.getToSymbol() + " " + L3.getToText() + " "
													+ L3.getMultiBy() + "\n");
											Log.d("FIVESTEP", R2.getFromSymbol() + " " + R2.getFromText() + " " + R2.getToSymbol() + " " + R2.getToText() + " "
													+ R2.getMultiBy() + "\n");
											Log.d("FIVESTEP", R.getFromSymbol() + " " + R.getFromText() + " " + R.getToSymbol() + " " + R.getToText() + " "
													+ R.getMultiBy() + "\n");											

											return Double.parseDouble(L.getMultiBy()) * Double.parseDouble(R.getMultiBy())
													* Double.parseDouble(L2.getMultiBy()) * Double.parseDouble(R2.getMultiBy())
													* Double.parseDouble(L3.getMultiBy());
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return 0;

	}
}
