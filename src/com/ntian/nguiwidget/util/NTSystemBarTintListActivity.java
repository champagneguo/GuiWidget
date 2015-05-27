/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntian.nguiwidget.util;

import com.ntian.nguiwidget.R;
import com.ntian.nguiwidget.util.SystemBarTintManager;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * A common superclass that keeps track of whether an {@link Activity} has saved its state yet or
 * not.
 */
public abstract class NTSystemBarTintListActivity extends ListActivity {

    protected SystemBarTintManager mTintManager;
    protected boolean mIgnoreTint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIgnoreTint) return;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mTintManager = new SystemBarTintManager(this);
        // Here we can set the background of system status bar to any color you want, typically same color as action bar.
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.nt_actionbar_bg_color);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIgnoreTint) return;
        mTintManager.setStatusBarDarkMode(true, this);
    }
}
