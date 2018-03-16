package com.yudownloader.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.yudownloader.R;
import com.yudownloader.ui.common.ToolbarActivity;
import com.yudownloader.util.Utility;

public class SettingsActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);*/

		setContentView(R.layout.settings);
		
		getFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
	}
/*
	@Override
	protected int getLayoutResource() {
		return R.layout.settings;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;	
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
		Utility.processDirectoryChange(requestCode, resultCode, data, this);
	}
	

}
