package ru.etysoft.cute.lang;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.HashMap;

import ru.etysoft.cute.exceptions.LanguageParsingException;
import ru.etysoft.cute.exceptions.NoSuchValueException;

public class StringsRepository {

    private static HashMap<String, String> values = new HashMap<>();

    public static void applyXml(String xmlString) throws LanguageParsingException {
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));
            int eventType = xmlPullParser.getEventType();

            String key = null;
            String value = null;
            boolean lastStartTag = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xmlPullParser.getAttributeCount() > 0) {
                        key = xmlPullParser.getAttributeValue(0);
                        lastStartTag = true;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (lastStartTag) {
                        lastStartTag = false;
                        value = xmlPullParser.getText();
                    }
                }
                if (key != null && value != null) {
                    Log.d("LANG", "Added " + key + " with " + value);
                    values.put(key, value);
                    key = null;
                    value = null;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LanguageParsingException();
        }
    }

    public static String getValue(String key) throws NoSuchValueException {
        if (!values.containsKey(key)) throw new NoSuchValueException();
        return values.get(key);
    }
}