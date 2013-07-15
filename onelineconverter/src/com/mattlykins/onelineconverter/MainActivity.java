package com.mattlykins.onelineconverter;

import com.mattlykins.onelineconverter.dbContract.dBase;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
	String sEntry,sValue,sFromUnit,sToUnit;
	String smtocm[] = {"m","cm","100"};
	String skmtom[] = {"km","m","1000"};
	Context context = this;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etTextEntry = (EditText)findViewById(R.id.etTextEntry);
		bConvert = (Button)findViewById(R.id.bConvert);
		tvOutput = (TextView)findViewById(R.id.tvOutput);
		
		bConvert.setOnClickListener(this);
		
		dbHelper mydbHelper = new dbHelper(context);
		SQLiteDatabase mydB = mydbHelper.getWritableDatabase();		
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
		
		if( sTokens.length == 3)
		{
			sValue = sTokens[0].trim();
			sFromUnit = sTokens[1].trim();
			sToUnit = sTokens[2].trim();
			
			if( sFromUnit.contentEquals(smtocm[0]) && sToUnit.contentEquals(smtocm[1]) )
			{
				double result = 0;
				result = Double.valueOf(sValue)*Double.valueOf(smtocm[2]);
				tvOutput.setText(String.valueOf(result));
			}
			
			
		}
		else
		{
		
		}
		
		
	}

}
