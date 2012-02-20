/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.cyanogenmod.settings.device;

import java.io.IOException;
import android.content.Context;
import android.util.AttributeSet;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

public class mDNIeNegative extends ListPreference implements OnPreferenceChangeListener {

    public mDNIeNegative(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnPreferenceChangeListener(this);
    }

    private static final String FILE = "/sys/class/mdnie/mdnie/negative";

    public static boolean isSupported() {
        return Utils.fileExists(FILE);
    }

    /**
     * Restore mdnie user mode setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Utils.writeValue(FILE, sharedPrefs.getString(DeviceSettings.KEY_MDNIE_NEGATIVE, "0"));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // negative is owned by root... wtf?
        try {
            Process p = Runtime.getRuntime().exec("chmod 777 " + FILE);
        } catch (IOException ex) { ex.printStackTrace(); }

        Utils.writeValue(FILE, (String) newValue);

        // restore file perms
        try {
            Process p = Runtime.getRuntime().exec("chmod 644 " + FILE);
        } catch (IOException ex) { ex.printStackTrace(); }

        return true;
    }

}
