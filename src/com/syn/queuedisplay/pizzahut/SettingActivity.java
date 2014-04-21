package com.syn.queuedisplay.pizzahut;

import java.util.List;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingActivity extends PreferenceActivity{
	public static final String PREF_SHOP_ID = "pref_shop_id";
	public static final String PREF_URL = "pref_url";
	public static final String PREF_REFRESH = "pref_refresh";
	public static final String PREF_VDO_DIR = "pref_vdo_dir";
	public static final String PREF_QUEUE_SPEAK_DIR = "pref_queue_speak_dir";
	public static final String PREF_INFO_TEXT = "pref_info_text";
	public static final String PREF_SPEAK_TIMES = "pref_speak_time";
	public static final String PREF_QUEUE_COLUMNS = "pref_queue_columns";

	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setupSimplePreferencesScreen();
	}
	
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}
		
		addPreferencesFromResource(R.xml.pref_conn);
		addPreferencesFromResource(R.xml.pref_general);
		addPreferencesFromResource(R.xml.pref_resource);
		bindPreferenceSummaryToValue(findPreference(PREF_SHOP_ID));
		bindPreferenceSummaryToValue(findPreference(PREF_URL));
		bindPreferenceSummaryToValue(findPreference(PREF_REFRESH));
		bindPreferenceSummaryToValue(findPreference(PREF_VDO_DIR));
		bindPreferenceSummaryToValue(findPreference(PREF_QUEUE_SPEAK_DIR));
		bindPreferenceSummaryToValue(findPreference(PREF_INFO_TEXT));
		bindPreferenceSummaryToValue(findPreference(PREF_SPEAK_TIMES));
		bindPreferenceSummaryToValue(findPreference(PREF_QUEUE_COLUMNS));
	}

	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			}else {
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	private static void bindPreferenceSummaryToValue(Preference preference) {
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch(item.getItemId()){
		case android.R.id.home:
			intent = new Intent(SettingActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			return true;
		default :
			return super.onOptionsItemSelected(item);
		}
	}
	
	public static class GeneralFragment extends PreferenceFragment{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			bindPreferenceSummaryToValue(findPreference(PREF_QUEUE_COLUMNS));
		}
	}
	
	public static class ResourceFragment extends PreferenceFragment{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_resource);
			bindPreferenceSummaryToValue(findPreference(PREF_VDO_DIR));
			bindPreferenceSummaryToValue(findPreference(PREF_QUEUE_SPEAK_DIR));
			bindPreferenceSummaryToValue(findPreference(PREF_INFO_TEXT));
			bindPreferenceSummaryToValue(findPreference(PREF_SPEAK_TIMES));
		}
	}
	
	public static class ConnectionFragment extends PreferenceFragment{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_conn);
			bindPreferenceSummaryToValue(findPreference(PREF_SHOP_ID));
			bindPreferenceSummaryToValue(findPreference(PREF_URL));
			bindPreferenceSummaryToValue(findPreference(PREF_REFRESH));
		}

	}
}
