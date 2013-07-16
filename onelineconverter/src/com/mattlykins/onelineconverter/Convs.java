package com.mattlykins.onelineconverter;

public class Convs
{
	private String FromSymbol,FromText,ToSymbol,ToText,MultiBy;

	public String getFromSymbol()
	{
		return FromSymbol;
	}

	public void setFromSymbol(String fromSymbol)
	{
		FromSymbol = fromSymbol;
	}

	public String getFromText()
	{
		return FromText;
	}

	public void setFromText(String fromText)
	{
		FromText = fromText;
	}

	public String getToSymbol()
	{
		return ToSymbol;
	}

	public void setToSymbol(String toSymbol)
	{
		ToSymbol = toSymbol;
	}

	public String getToText()
	{
		return ToText;
	}

	public void setToText(String toText)
	{
		ToText = toText;
	}

	public String getMultiBy()
	{
		return MultiBy;
	}

	public void setMultiBy(String multiBy)
	{
		MultiBy = multiBy;
	}
	
	public Convs()
	{
		super();
		FromSymbol = null;
		FromText = null;
		ToSymbol = null;
		ToText = null;
		MultiBy = null;
		
	}
	
	public Convs(String fromSymbol, String fromText, String toSymbol, String toText, String multiBy)
	{
		super();
		FromSymbol = fromSymbol;
		FromText = fromText;
		ToSymbol = toSymbol;
		ToText = toText;
		MultiBy = multiBy;
	}
	
	

}
