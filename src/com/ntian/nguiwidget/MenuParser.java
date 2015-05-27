package com.ntian.nguiwidget;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.ActionProvider;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class MenuParser {
	/*
	private static final String LOG_TAG = "MenuInflater";

     
    private static final String XML_MENU = "menu";
    
    
    private static final String XML_GROUP = "group";
    
   
    private static final String XML_ITEM = "item";
    
	public void inflate(Context mContext, int menuRes, Menu menu) {
        XmlResourceParser parser = null;
        try {
            parser = mContext.getResources().getLayout(menuRes);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            
            parseMenu(parser, attrs, menu);
        } catch (XmlPullParserException e) {
            throw new InflateException("Error inflating menu XML", e);
        } catch (IOException e) {
            throw new InflateException("Error inflating menu XML", e);
        } finally {
            if (parser != null) parser.close();
        }
    }
	
	 private void parseMenu(XmlPullParser parser, AttributeSet attrs, Menu menu)
	            throws XmlPullParserException, IOException {
	        MenuState menuState = new MenuState(menu);

	        int eventType = parser.getEventType();
	        String tagName;
	        boolean lookingForEndOfUnknownTag = false;
	        String unknownTagName = null;

	        // This loop will skip to the menu start tag
	        do {
	            if (eventType == XmlPullParser.START_TAG) {
	                tagName = parser.getName();
	                if (tagName.equals(XML_MENU)) {
	                    // Go to next tag
	                    eventType = parser.next();
	                    break;
	                }
	                
	                throw new RuntimeException("Expecting menu, got " + tagName);
	            }
	            eventType = parser.next();
	        } while (eventType != XmlPullParser.END_DOCUMENT);
	        
	        boolean reachedEndOfMenu = false;
	        while (!reachedEndOfMenu) {
	            switch (eventType) {
	                case XmlPullParser.START_TAG:
	                    if (lookingForEndOfUnknownTag) {
	                        break;
	                    }
	                    
	                    tagName = parser.getName();
	                    if (tagName.equals(XML_GROUP)) {
	                        menuState.readGroup(attrs);
	                    } else if (tagName.equals(XML_ITEM)) {
	                        menuState.readItem(attrs);
	                    } else if (tagName.equals(XML_MENU)) {
	                        // A menu start tag denotes a submenu for an item
	                        SubMenu subMenu = menuState.addSubMenuItem();

	                        // Parse the submenu into returned SubMenu
	                        parseMenu(parser, attrs, subMenu);
	                    } else {
	                        lookingForEndOfUnknownTag = true;
	                        unknownTagName = tagName;
	                    }
	                    break;
	                    
	                case XmlPullParser.END_TAG:
	                    tagName = parser.getName();
	                    if (lookingForEndOfUnknownTag && tagName.equals(unknownTagName)) {
	                        lookingForEndOfUnknownTag = false;
	                        unknownTagName = null;
	                    } else if (tagName.equals(XML_GROUP)) {
	                        menuState.resetGroup();
	                    } else if (tagName.equals(XML_ITEM)) {
	                        // Add the item if it hasn't been added (if the item was
	                        // a submenu, it would have been added already)
	                        if (!menuState.hasAddedItem()) {
	                            if (menuState.itemActionProvider != null &&
	                                    menuState.itemActionProvider.hasSubMenu()) {
	                                menuState.addSubMenuItem();
	                            } else {
	                                menuState.addItem();
	                            }
	                        }
	                    } else if (tagName.equals(XML_MENU)) {
	                        reachedEndOfMenu = true;
	                    }
	                    break;
	                    
	                case XmlPullParser.END_DOCUMENT:
	                    throw new RuntimeException("Unexpected end of document");
	            }
	            
	            eventType = parser.next();
	        }
	    } */
}