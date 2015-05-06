package com.remedeo.remedeoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Utils {
	
	public static final String AVIARY_SECRET="f1136432fd0b6d18";
	
	private static final String SHARED_PREF = "Remideo_pref";
	
	public static void setPreference(Context context, String key, String value) {
		SharedPreferences preference = context.getSharedPreferences(
				SHARED_PREF, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getPreference(Context context, String key) {
		SharedPreferences preference = context.getSharedPreferences(
				SHARED_PREF, Context.MODE_PRIVATE);
		return preference.getString(key, null);
	}
	
	public static int dpToPx(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
	
}
