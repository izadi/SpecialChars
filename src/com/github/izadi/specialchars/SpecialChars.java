package com.github.izadi.specialchars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class SpecialChars {
	private final static String NAMESPACE = "http://schemas.android.com/apk/res/com.github.izadi.specialchars";
	private final static String TAG_SPECIAL_CHARS = "SpecialChars";
	private final static String TAG_GROUP = "Group";
	private final static String TAG_CHAR = "Char";
	private final static String ATTR_GROUP_LABEL  = "label";
	private final static String ATTR_CHAR_LABEL = "label";
	private final static String ATTR_CHAR_VALUE = "value";

	@SuppressWarnings("serial")
	public static class XmlParseException extends Exception {
		public XmlParseException() {
			super();
		}
		public XmlParseException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}
		public XmlParseException(String detailMessage) {
			super(detailMessage);
		}
		public XmlParseException(Throwable throwable) {
			super(throwable);
		}
	}
	
	public static class Char {
		private String label;
		private String value;
		
		public Char(String label, String value) {
			this.label = label;
			this.value = value;
		}
		public Char(XmlResourceParser parser) throws XmlParseException {
			label = parser.getAttributeValue(NAMESPACE, ATTR_CHAR_LABEL);
			if (label == null)
				throw new XmlParseException();
			value = parser.getAttributeValue(NAMESPACE, ATTR_CHAR_VALUE);
			if (value == null)
				throw new XmlParseException();
			try {
				for (int event = parser.next(); event != XmlPullParser.END_TAG; event = parser.next()) {
					if (event == XmlPullParser.START_TAG)
						SkipElement(parser);
					else
						throw new XmlParseException();
				}
				if (!parser.getName().equals(TAG_CHAR))
					throw new XmlParseException();
			} catch (XmlPullParserException e) {
				throw new XmlParseException();
			} catch (IOException e) {
				throw new XmlParseException();
			}
		}
		
		public String getLabel() {
			return label;
		}
		public String getValue() {
			return value;
		}
	}
	
	public static class Group {
		private String label;
		private List<Char> chars;
		
		public Group(String label, List<Char> chars) {
			this.label = label;
			this.chars = chars;
		}
		public Group(XmlResourceParser parser) throws XmlParseException {
			label = parser.getAttributeValue(NAMESPACE, ATTR_GROUP_LABEL);
			if (label == null)
				throw new XmlParseException();
			try {
				chars = new ArrayList<SpecialChars.Char>();
				for (int event = parser.next(); event != XmlPullParser.END_TAG; event = parser.next()) {
					if (event == XmlPullParser.START_TAG) {
						if (parser.getName().equals(TAG_CHAR)) {
							Char c = new Char(parser);
							chars.add(c);
						} else
							SkipElement(parser);
					}
					else
						throw new XmlParseException();
				}
				if (!parser.getName().equals(TAG_GROUP))
					throw new XmlParseException();
			} catch (XmlPullParserException e) {
				throw new XmlParseException();
			} catch (IOException e) {
				throw new XmlParseException();
			}
		}

		public String getLabel() {
			return label;
		}
		public List<Char> getChars() {
			return chars;
		}
	}
	
	private List<Group> groups;

	public SpecialChars(List<Group> groups) {
		this.groups = groups;
	}
	public SpecialChars(XmlResourceParser parser) throws XmlParseException {
		try {
			groups = new ArrayList<SpecialChars.Group>();
			for (int event = parser.next(); event != XmlPullParser.END_TAG; event = parser.next()) {
				if (event == XmlPullParser.START_TAG) {
					if (parser.getName().equals(TAG_GROUP)) {
						Group g = new Group(parser);
						groups.add(g);
					} else
						SkipElement(parser);
				} else
					throw new XmlParseException();
			}
			if (!parser.getName().equals(TAG_SPECIAL_CHARS))
				throw new XmlParseException();
		} catch (XmlPullParserException e) {
			throw new XmlParseException();
		} catch (IOException e) {
			throw new XmlParseException();
		}
	}

	private static void SkipElement(XmlResourceParser parser) throws XmlParseException {
		final Stack<String> tags = new Stack<String>();
		try {
			tags.push(parser.getName());
			while (!tags.empty()) {
				int event;
				event = parser.next();
				if (event == XmlPullParser.START_TAG)
					tags.push(parser.getName());
				else if (event == XmlPullParser.END_TAG) {
					if (tags.pop() != parser.getName())
						throw new XmlParseException();
				} else
					throw new XmlParseException();
			}
		} catch (XmlPullParserException e) {
			throw new XmlParseException();
		} catch (IOException e) {
			throw new XmlParseException();
		}
	}
	public static SpecialChars load(Context context, int resourceId) throws XmlParseException {
		Resources res = context.getResources();
		XmlResourceParser parser = res.getXml(resourceId);
		try {
			parser.next();
			for (int event = parser.next(); event == XmlPullParser.START_TAG; event = parser.next()) {
				if (parser.getName().equals(TAG_SPECIAL_CHARS)) {
					SpecialChars result = new SpecialChars(parser);
					return result;
				} else
					SkipElement(parser);
			}
			throw new XmlParseException();
		} catch (XmlPullParserException e) {
			throw new XmlParseException();
		} catch (IOException e) {
			throw new XmlParseException(e);
		} finally {
			parser.close();
		}
	}

	public List<Group> getGroups() {
		return groups;
	}
}
