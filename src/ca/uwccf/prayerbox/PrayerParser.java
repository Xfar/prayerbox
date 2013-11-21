package ca.uwccf.prayerbox;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.Html;

public class PrayerParser {
	private XmlPullParserFactory factory;
	private String m_content;

	public PrayerParser(String content) {
		m_content = content;

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, String> parseLogin() {
		try {
			XmlPullParser parser = null;
			parser = factory.newPullParser();
			parser.setInput(new StringReader(m_content));
			int eventType = 0;
			HashMap<String, String> accountInfo = new HashMap<String, String>();
			eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// at start of document: START_DOCUMENT

				// at start of a tag: START_TAG
				case XmlPullParser.START_TAG:
					// get tag name
					String tagName = parser.getName();
					if (tagName.equalsIgnoreCase("error")) {
						accountInfo.put("error", parser.nextText());
					}
					if (tagName.equalsIgnoreCase("user")) {
						accountInfo.put("user", parser.nextText());
					}
					if (tagName.equalsIgnoreCase("email")) {
						accountInfo.put("email", parser.nextText());
					}
					if (tagName.equalsIgnoreCase("displayname")) {
						accountInfo.put("displayname", parser.nextText());
					}
					if (tagName.equalsIgnoreCase("session_id")) {
						accountInfo.put("session_id", parser.nextText());
					}
				}
				eventType = parser.next();
			}
			return accountInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public ArrayList<Prayer> parsePrayerList() {
		try {
			XmlPullParser parser = null;
			parser = factory.newPullParser();
			parser.setInput(new StringReader(m_content));
			ArrayList<Prayer> prayer_list = new ArrayList<Prayer>();
			int eventType = 0;
			eventType = parser.getEventType();
			Prayer prayer = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// at start of document: START_DOCUMENT

				// at start of a tag: START_TAG
				case XmlPullParser.START_TAG:
					// get tag name
					String tagName = parser.getName();
					// if <study>, get attribute: 'id'
					if (tagName.equalsIgnoreCase("prayer")) {
						prayer = new Prayer();
						break;
					} else if (tagName.equalsIgnoreCase("subject")) {
						prayer.subject = parser.nextText();
					} else if(tagName.equalsIgnoreCase("isStarred")) {
						if(parser.nextText().equalsIgnoreCase("true")){
							prayer.isStarred = true;
						}else {
							prayer.isStarred = false;
						}
					} else if (tagName.equalsIgnoreCase("request")) {
						prayer.request = Html.fromHtml(parser.nextText())
								.toString();
					} else if (tagName.equalsIgnoreCase("name")) {
						prayer.author = parser.nextText();
					} else if (tagName.equalsIgnoreCase("prayer_id")) {
						prayer.prayer_id = parser.nextText();
					} else if (tagName.equalsIgnoreCase("date")) {
						prayer.date = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equalsIgnoreCase("prayer")) {
						prayer_list.add(prayer);
						prayer = null;
					}
				}
				eventType = parser.next();
			}
			return prayer_list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
