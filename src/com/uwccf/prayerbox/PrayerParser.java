package com.uwccf.prayerbox;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.Html;

public class PrayerParser {
	private XmlPullParserFactory factory;
	private String m_content;
	
	public PrayerParser(String content){
		m_content = content;
		
		try {
			factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<Prayer> parse()
	{
		try
		{
	        XmlPullParser parser = null;
			parser = factory.newPullParser();
			parser.setInput(new StringReader (m_content));
			ArrayList<Prayer> prayer_list = new ArrayList<Prayer>();
			ArrayList<String> string_list = new ArrayList<String>();
			int eventType = 0;
			eventType = parser.getEventType();
			Prayer prayer = null;
		    while (eventType != XmlPullParser.END_DOCUMENT) {
		    	switch(eventType) {
				// at start of document: START_DOCUMENT

				// at start of a tag: START_TAG
				case XmlPullParser.START_TAG:
					// get tag name
					String tagName = parser.getName();
					// if <study>, get attribute: 'id'
					if(tagName.equalsIgnoreCase("prayer")) {
						prayer = new Prayer();
						break;
					}
					else if(tagName.equalsIgnoreCase("subject")) {
		//				string_list.add(parser.nextText());
						prayer.subject = parser.nextText();
					}
					else if(tagName.equalsIgnoreCase("request")) {
						prayer.request = Html.fromHtml(parser.nextText()).toString();
					}
					else if(tagName.equalsIgnoreCase("name")){
						prayer.author = parser.nextText();
					}
					else if(tagName.equalsIgnoreCase("date")){
						prayer.date = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if(parser.getName().equalsIgnoreCase("prayer"))
					{
						prayer_list.add(prayer);
						prayer = null;
					}
		    	}
		     eventType = parser.next();
		    }
		    return prayer_list;
		}catch (Exception e){
			 e.printStackTrace();
			 return null;
		}
	}
}
