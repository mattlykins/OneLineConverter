package com.mattlykins.onelineconverter;

import java.util.ArrayList;
import java.util.List;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class DBFunctions
{
	public Context context;
	public dbHelper mydbHelper;

	public DBFunctions(Context context, dbHelper mydbHelper)
	{
		this.context = context;
		this.mydbHelper = mydbHelper;
	}

	List<Convs> AddToList(Cursor cursor)
	{
		List<Convs> List1 = new ArrayList<Convs>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			if (cursor != null)
			{
				boolean lgAddToList = true;
				Convs conv = new Convs(cursor.getString(dBase.NDEX_ID), cursor.getString(dBase.NDEX_FROMSYMBOL), cursor.getString(dBase.NDEX_FROMTEXT),
						cursor.getString(dBase.NDEX_TOSYMBOL), cursor.getString(dBase.NDEX_TOTEXT), cursor.getString(dBase.NDEX_MULTIBY));

				if (lgAddToList)
				{
					Log.d("FERRET",
							cursor.getString(dBase.NDEX_ID) + " " + cursor.getString(dBase.NDEX_FROMSYMBOL) + " " + cursor.getString(dBase.NDEX_FROMTEXT) + " "
									+ cursor.getString(dBase.NDEX_TOSYMBOL) + " " + cursor.getString(dBase.NDEX_TOTEXT) + " "
									+ cursor.getString(dBase.NDEX_MULTIBY) + "\n");
					List1.add(conv);
				}
			}

		}
		return List1;
	}

	public void IntegrityTest(List<Convs> UnMatchedConvs, List<Convs> WrongValueConvs)
	{
		// Create a list of all of the conversions
		List<Convs> AllConvs = new ArrayList<Convs>();
		AllConvs = mydbHelper.getAllConvs();

		// Loop through each conversion and verify that an inverse conversion
		// exists with the correct factor
		boolean lgFound = false;
		for (Convs C : AllConvs)
		{
			lgFound = false;
			for (Convs C2 : AllConvs)
			{
				if (C.getFromSymbol().equals(C2.getToSymbol()) && C.getToSymbol().equals(C2.getFromSymbol()))
				{
					Double BaseMult = Double.parseDouble(C.getMultiBy());
					Double ComparMult = 1 / Double.parseDouble(C2.getMultiBy());
					Double DiffAvg = 2 * Math.abs(BaseMult - ComparMult) / (BaseMult + ComparMult);
					if (DiffAvg < 0.01)
					{
						// Values check out
						Log.d("TAG", C.getFromSymbol() + " to " + C.getToSymbol() + ": Verified");
						lgFound = true;
						break;
					}
					else
					{
						// Values are bad
						Log.d("TAG", C.getFromSymbol() + " to " + C.getToSymbol() + ": BAD");
						Log.d("TAG", C.getFromSymbol() + " " + C.getToSymbol() + " " + C.getMultiBy() + " vs " + C2.getFromSymbol() + " " + C2.getToSymbol()
								+ " " + C2.getMultiBy());
						WrongValueConvs.add(C2);
						break;
					}
				}
			}
			if (!lgFound)
			{
				// No match found
				UnMatchedConvs.add(C);
			}
		}
	}

	public boolean boolIntegrityCheck(List<Convs> UnMatchedConvs, List<Convs> WrongValueConvs, Boolean lgShowVerified)
	{
		// Set up alert dialog for all three cases (Verified, UnMatched, and WrongValue)
		AlertDialog.Builder adb = new AlertDialog.Builder(context);		
		adb.setCancelable(false);
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// here you can add functions
				dialog.cancel();
			}
		});
		
		//Determine which of the three cases
		if (UnMatchedConvs.isEmpty() && WrongValueConvs.isEmpty())
		{
			// All DB entries verified
			if (lgShowVerified)
			{				
				adb.setTitle("Database Integrity Verified");
				adb.setMessage("Database Integrity Verified");				
				AlertDialog ad = adb.create();
				ad.show();
			}
			return true;
		}
		else if( !UnMatchedConvs.isEmpty() && WrongValueConvs.isEmpty() )
		{
			// There are unmatched conversions
			adb.setTitle("Database Integrity Compromised!");
			String line = "The following conversions are unmatched:\n\n";
			
			for( Convs C: UnMatchedConvs)
			{
				line = line + C.getFromSymbol() + " to " + C.getToSymbol() + "\n";
			}		
			
			adb.setMessage(line);				
			AlertDialog ad = adb.create();
			ad.show();			
			return false;
		}
		else
		{
			//There are conversions with the wrong value and possibly unmatched
			adb.setTitle("Database Integrity Compromised!");
			String line = "The following conversions have the wrong conversion or are unmatched:\n\n";
			
			for( Convs C: WrongValueConvs)
			{
				for( Convs C2: UnMatchedConvs)
				{
					if( C.getFromSymbol().equals(C2.getToSymbol()) && C.getToSymbol().equals(C2.getFromSymbol()))
					{
						line = line + C.getFromSymbol() + " to " + C.getToSymbol() + " by " + C.getMultiBy() + " != " +
								C2.getFromSymbol() + " to " + C2.getToSymbol() + " by " + C2.getMultiBy() + "\n";
					}
					else
					{
						line = line + C2.getFromSymbol() + " to " + C2.getToSymbol() + "\n";
					}
				}
			}		
			
			adb.setMessage(line);				
			AlertDialog ad = adb.create();
			ad.show();			
			return false;
		}
	}

	double FindFactorWrapper(MainActivity act, String sFromUnit, String sToUnit)
	{
		// Verify the unit conversion is potentially possible
		Cursor testFF = mydbHelper.searchFrom(sFromUnit, null);
		Cursor testTF = mydbHelper.searchTo(sFromUnit, null);
		if (testFF == null && testTF == null)
		{
			Toast toast = Toast.makeText(act, "Invalid From Unit", Toast.LENGTH_SHORT);
			toast.show();
			return 0;
		}

		Cursor testFT = mydbHelper.searchFrom(sToUnit, null);
		Cursor testTT = mydbHelper.searchTo(sToUnit, null);
		if (testFT == null && testTT == null)
		{
			Toast toast = Toast.makeText(act, "Invalid To Unit", Toast.LENGTH_SHORT);
			toast.show();
			return 0;
		}

		Log.d("FERRET", "Calling junk with " + sFromUnit + " " + sToUnit + "\n");
		return (FindFactor(act, sFromUnit, sToUnit));
	}

	double FindFactor(MainActivity act, String sFromUnit, String sToUnit)
	{
		// Create a list of convs where the From unit in the db query is the
		// user entered from unit
		List<Convs> FF = new ArrayList<Convs>();
		Cursor cursorFF = mydbHelper.searchFrom(sFromUnit, null);
		if (cursorFF != null)
		{
			FF = AddToList(cursorFF);
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
			TT = AddToList(cursorTT);
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
				FF2 = AddToList(cursorFF2);
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
				FF2 = AddToList(cursorFF2);
				cursorFF2.close();
				for (Convs R : TT)
				{
					List<Convs> TT2 = new ArrayList<Convs>();
					Cursor cursorTT2 = mydbHelper.searchTo(R.getFromSymbol(), null);
					if (cursorTT2 != null)
					{
						TT2 = AddToList(cursorTT2);
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
				FF2 = AddToList(cursorFF2);
				cursorFF2.close();
				for (Convs R : TT)
				{
					List<Convs> TT2 = new ArrayList<Convs>();
					Cursor cursorTT2 = mydbHelper.searchTo(R.getFromSymbol(), null);
					if (cursorTT2 != null)
					{
						TT2 = AddToList(cursorTT2);
						cursorTT2.close();
						for (Convs L2 : FF2)
						{
							List<Convs> FF3 = new ArrayList<Convs>();
							Cursor cursorFF3 = mydbHelper.searchFrom(L2.getToSymbol(), null);
							if (cursorFF3 != null)
							{
								FF3 = AddToList(cursorFF3);
								cursorFF3.close();

								for (Convs R2 : TT2)
								{
									for (Convs L3 : FF3)
									{

										if (L.getToSymbol().equals(L2.getFromSymbol()) && R.getFromSymbol().equals(R2.getToSymbol())
												&& L2.getToSymbol().equals(L3.getFromSymbol()) && L3.getToSymbol().equals(R2.getFromSymbol()))
										{
											Log.d("FIVESTEP",
													L.getFromSymbol() + " " + L.getFromText() + " " + L.getToSymbol() + " " + L.getToText() + " "
															+ L.getMultiBy() + "\n");
											Log.d("FIVESTEP", L2.getFromSymbol() + " " + L2.getFromText() + " " + L2.getToSymbol() + " " + L2.getToText() + " "
													+ L2.getMultiBy() + "\n");
											Log.d("FIVESTEP", L3.getFromSymbol() + " " + L3.getFromText() + " " + L3.getToSymbol() + " " + L3.getToText() + " "
													+ L3.getMultiBy() + "\n");
											Log.d("FIVESTEP", R2.getFromSymbol() + " " + R2.getFromText() + " " + R2.getToSymbol() + " " + R2.getToText() + " "
													+ R2.getMultiBy() + "\n");
											Log.d("FIVESTEP",
													R.getFromSymbol() + " " + R.getFromText() + " " + R.getToSymbol() + " " + R.getToText() + " "
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