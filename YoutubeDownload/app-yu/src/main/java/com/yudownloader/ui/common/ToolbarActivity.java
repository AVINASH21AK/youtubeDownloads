package com.yudownloader.ui.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yudownloader.R;
import com.yudownloader.util.Utility;

public abstract class ToolbarActivity extends AppCompatActivity
{
	protected Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		
		mToolbar = Utility.findViewById(this, R.id.toolbar);
		
		//setSupportActionBar(mToolbar);

	}
	
	protected abstract int getLayoutResource();
}
