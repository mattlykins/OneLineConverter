package com.mattlykins.onelineconverter;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	DBFunctions dbFunctions = new DBFunctions(this, new dbHelper(this));

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

			// Split From Unit into tokens
			StringTokenizer tokFrom = new StringTokenizer(sFromUnit, "[*/]", true);
			String[] FromTokens = new String[tokFrom.countTokens() + 1];
			int FromDex = 0;

			while (tokFrom.hasMoreElements())
			{
				FromTokens[FromDex] = tokFrom.nextToken();
				Log.d("TOKENIZER", FromTokens[FromDex] + " " + String.valueOf(tokFrom.countTokens()) + "\n");
				FromDex += 1;
			}

			// Split To Unit into tokens
			StringTokenizer tokTo = new StringTokenizer(sToUnit, "[*/]", true);
			String[] ToTokens = new String[tokTo.countTokens() + 1];
			int ToDex = 0;

			while (tokTo.hasMoreElements())
			{
				ToTokens[ToDex] = tokTo.nextToken();
				Log.d("TOKENIZER", ToTokens[ToDex] + " " + String.valueOf(tokTo.countTokens()) + "\n");
				ToDex += 1;
			}

			// Run conversion on each part of the From Unit
			Double TotalConvFactor = 1.0;

			// Verify dimensional compatibility
			if (FromDex == ToDex)
			{
				for (int j = 0; j < FromDex; j++)
				{
					if (!FromTokens[j].equals("*") && !FromTokens[j].equals("/") && !ToTokens[j].equals("*") && !ToTokens[j].equals("/"))
					{
						// Call conversion and return conversion factor

						Log.d("FERRET", "Calling junkwrapper with " + sFromUnit + " " + sToUnit + "\n");
						Double result = dbFunctions.FindFactorWrapper(this, FromTokens[j], ToTokens[j]);
						Log.d("FERRET", "Junkwrapper returned " + result + "\n");

						if (j != 0 && FromTokens[j - 1].equals("/") && ToTokens[j - 1].equals("/"))
						{
							Log.d("FERRET", "TotalConvFactor = " + TotalConvFactor + "  result=" + result + "\n");
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
				// Bad Stuff
			}

			DecimalFormat dfSigFig = new DecimalFormat("0.####E0");
			String ConvertedValue = dfSigFig.format(Double.parseDouble(sValue) * TotalConvFactor);
			tvOutput.setText(ConvertedValue + "");
		}
		else
		{

		}

	}
}
