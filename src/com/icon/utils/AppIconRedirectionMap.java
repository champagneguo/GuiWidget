package com.icon.utils;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.util.XmlUtils;
import com.ntian.nguiwidget.R;

// @KCHEN, to support app icon redirection feature
public class AppIconRedirectionMap {
    private static final String TAG = "AppIconRedirectionMap";

    private static final int ICON_REDIRECTION_MAP_ID = R.xml.shortcut_redirections;
    private static final int INITIAL_ICON_REDIRECTION_MAP_CAPACITY = 40;
    private static final String ROOT_TAG = "shortcut-redirections";

    private final XmlPullParser mParser;
    private static AppIconRedirectionMap sSingleton = null;

    private Context mContext;
    private Resources mResources;
    private final HashMap<String, Integer> mIconMap = new HashMap<String, Integer>(
            INITIAL_ICON_REDIRECTION_MAP_CAPACITY);

    public synchronized static AppIconRedirectionMap getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new AppIconRedirectionMap(context);
        }
        return sSingleton;
    }

    private AppIconRedirectionMap(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mParser = mResources.getXml(ICON_REDIRECTION_MAP_ID);
        mIconMap.clear();

        syncMap();
    }

    private void syncMap() {
        XmlPullParser parser = mParser;
        int type;

        try {
            while ((type = parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
                ;
            }

            String tagName = parser.getName();
            if (parser.getName().equals(ROOT_TAG)) {
                processRootTag();
            } else {
                Log.w(TAG,
                        "Unknown root element: " + tagName + " " + parser.getPositionDescription());
            }
        } catch (XmlPullParserException e) {
            Log.w(TAG, "Malformed redirection meta");
        } catch (IOException e) {
            Log.w(TAG, "Unknown error reading redirection meta");
        }
    }

    private void processRootTag() throws XmlPullParserException, IOException {
        XmlPullParser parser = mParser;
        int type;
        final int innerDepth = parser.getDepth();

        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG || parser.getDepth() > innerDepth)) {
            if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                continue;
            }

            String tagName = parser.getName();
            if (tagName.equals("item")) {
                processItemTag();
            } else {
                Log.w(TAG,
                        "Unknown element under root tag: " + tagName + " "
                                + parser.getPositionDescription());
                XmlUtils.skipCurrentTag(parser);
                continue;
            }
        }
    }

    private void processItemTag() throws XmlPullParserException, IOException {
        XmlPullParser parser = mParser;

        String packageName = parser.getAttributeValue(null, "packageName");
        String className = parser.getAttributeValue(null, "className");
        String resName = parser.getAttributeValue(null, "icon");

        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)
                || TextUtils.isEmpty(resName)) {
            Log.w(TAG,
                    "Missing android:name attribute on <item> tag "
                            + parser.getPositionDescription());
            return;
        }

        int resIdent = mResources.getIdentifier(resName, null, mContext.getPackageName());
        if (resIdent == 0) {
            Log.w(TAG, "No such resource found for " + resName);
            return;
        }

        ComponentName componentName = new ComponentName(packageName, className);
        mIconMap.put(getComponentNameKey(componentName), resIdent);
    }

    private String getComponentNameKey(ComponentName componentName) {
        return componentName == null ? null : componentName.toShortString();
    }

    public Integer get(ComponentName componentName) {
        return mIconMap.get(getComponentNameKey(componentName));
    }
}
