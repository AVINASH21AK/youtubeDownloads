package com.yudownloader.ui.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yudownloader.R;
import com.yudownloader.common.App;
import com.yudownloader.get.DownloadManager;
import com.yudownloader.service.DownloadManagerService;
import com.yudownloader.ui.adapter.NavigationAdapter;
import com.yudownloader.ui.common.FloatingActionButton;
import com.yudownloader.ui.common.ToolbarActivity;
import com.yudownloader.ui.fragment.AllMissionsFragment;
import com.yudownloader.ui.fragment.MissionsFragment;
import com.yudownloader.ui.settings.SettingsActivity;
import com.yudownloader.util.CrashHandler;
import com.yudownloader.util.Utility;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public static final String INTENT_DOWNLOAD = "intent.DOWNLOAD";

    String TAG = "MainActivity";
    String mVideoUrl, mVideoTitle, mVideoFullPath;

    private MissionsFragment mFragment;
    private DrawerLayout mDrawer;
    private ListView mList;
    private NavigationAdapter mAdapter;
    //private ActionBarDrawerToggle mToggle;
    private DownloadManager mManager;
    private DownloadManagerService.DMBinder mBinder;

    private String mPendingUrl;
    private SharedPreferences mPrefs;
    private int mSelection = 0;

    String combine = "";
    String urlDownloadlink = "";//declare variable to hold final URL

    String strFrom = "";

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName p1, IBinder binder) {
            mBinder = (DownloadManagerService.DMBinder) binder;
            mManager = mBinder.getDownloadManager();
        }

        @Override
        public void onServiceDisconnected(ComponentName p1) {

        }


    };

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        try {
            CrashHandler.init(this);
            CrashHandler.register();

            getActivityIntent();

            // Service
            Intent i = new Intent();
            i.setClass(this, DownloadManagerService.class);
            startService(i);
            bindService(i, mConnection, Context.BIND_AUTO_CREATE);

            super.onCreate(savedInstanceState);
            //111getSupportActionBar().setDisplayUseLogoEnabled(false);

            setContentView(R.layout.main);

            mPrefs = getSharedPreferences("threads", Context.MODE_WORLD_READABLE);


            // Drawer
            mDrawer = Utility.findViewById(this, R.id.drawer);
            //111mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, 0, 0);
            //111mToggle.setDrawerIndicatorEnabled(true);
            //111mDrawer.setDrawerListener(mToggle);

            if (Build.VERSION.SDK_INT >= 21) {
                findViewById(R.id.nav).setElevation(20.0f);
            } else {
                mDrawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
            }

            mList = Utility.findViewById(this, R.id.nav_list);
            mAdapter = new NavigationAdapter(this, R.array.drawer_items, R.array.drawer_icons);
            mList.setAdapter(mAdapter);


            // FAB
            new FloatingActionButton.Builder(this)
                    .withButtonColor(getResources().getColor(R.color.blue))
                    .withButtonSize(80)
                    .withDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp))
                    .withGravity(Gravity.RIGHT | Gravity.BOTTOM)
                    .withPaddings(0, 0, 10, 10)
                    .create()
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mFragment != null) {
                                showUrlDialog();
                            }
                        }
                    });

            // Fragment
            getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updateFragments();
                    getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
            mList.setOnItemClickListener(this);

            // Intent
            if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(INTENT_DOWNLOAD)) {
                mPendingUrl = getIntent().getData().toString();
            }


            if(strFrom !=null && strFrom.length() > 0)
            {
              new Handler().postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      if (mFragment != null) {
                          showUrlDialog();
                      }
                  }
              },1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getActivityIntent(){
        try{

            if(getIntent() !=null && getIntent().hasExtra("from"))
            {
                strFrom  = getIntent().getStringExtra("from");
                mVideoUrl = getIntent().getStringExtra("VideoUrl");
                mVideoTitle = getIntent().getStringExtra("VideoTitle");
                mVideoFullPath = getIntent().getStringExtra("VideoFullPath");

                App.showLog(TAG, "mVideoUrl: "+mVideoUrl);
                App.showLog(TAG, "mVideoTitle: "+mVideoTitle);
                App.showLog(TAG, "mVideoFullPath: "+mVideoFullPath);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    private void setDataOtherDownloadActivity() {
        try {
            Log.i("-setdata-", "------>11111111111111");
            if (getIntent().getData() != null) {//check if intent is not null
                Uri data = getIntent().getData();//set a variable for the Intent
                String scheme = data.getScheme();//get the scheme (http,https)
                String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments
                Log.i("-fullPath-", "------>fullPath-0-" + fullPath);
                if (fullPath.contains("//")) {
                    combine = scheme + ":" + fullPath; //combine to get a full URI
                } else {
                    combine = scheme + "://" + fullPath; //combine to get a full URI
                }

            }

            urlDownloadlink = null;//declare variable to hold final URL
            if (combine != null) {//if combine variable is not empty then navigate to that full path
                urlDownloadlink = combine;// URLDecoder.decode(combine, "UTF-8");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        if (mFragment != null && urlDownloadlink != null && urlDownloadlink.length() > 5) {

                            try {
                                urlDownloadlink = combine; // URLDecoder.decode(combine, "UTF-8");
                                showUrlDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, 200);


            } else {//else open main page
                urlDownloadlink = "-1";
            }

            Log.i("-setDataOtherDownloadActivity-", "==Download url 111111==" + urlDownloadlink);




			/*StringBuilder text = new StringBuilder();

			Uri data = getIntent().getData();
			if(data != null){
				text.append("Path:\n");
				text.append(data.getPath());

				text.append("\n\nScheme:\n");
				text.append(data.getScheme());

				text.append("\n\nHost:\n");
				text.append(data.getHost());

				text.append("\n\nPath segments:\n");
				text.append(Arrays.toString(data.getPathSegments().toArray()));
			} else {
				text.append("Uri is null");
			}

			Log.i("-setDataOtherDownloadActivity-","==Download url=="+text);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction().equals(INTENT_DOWNLOAD)) {
            mPendingUrl = intent.getData().toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataOtherDownloadActivity();
        if (mPendingUrl != null && mFragment != null) {
            showUrlDialog();
            mPendingUrl = null;
        }
    }
/*
	@Override
    protected int getLayoutResource() {
		return R.layout.main;
	}*/

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //111mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //111mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDrawer.closeDrawer(Gravity.LEFT);
        if (position < 1) {
            if (position != mSelection) {
                mSelection = position;
                updateFragments();
            }
        } else if (position == 2) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setClass(this, SettingsActivity.class);
            startActivity(i);
        }

    }

    private void updateFragments() {
        switch (mSelection) {
            case 0:
                mFragment = new AllMissionsFragment();
                break;

        }
        getFragmentManager().beginTransaction()
                .replace(R.id.frame, mFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        for (int i = 0; i < 1; i++) {
            View v = mList.getChildAt(i);

            ImageView icon = Utility.findViewById(v, R.id.drawer_icon);
            TextView text = Utility.findViewById(v, R.id.drawer_text);

            if (i == mSelection) {
                v.setBackgroundResource(R.color.light_gray);
                icon.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
                text.setTextColor(getResources().getColor(R.color.blue));
            } else {
                v.setBackgroundResource(android.R.color.transparent);
                icon.setColorFilter(null);
                text.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }

    private void showUrlDialog() {
        // Create the view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_url, null);
        final EditText text = Utility.findViewById(v, R.id.url);
        final EditText name = Utility.findViewById(v, R.id.file_name);
        final TextView tCount = Utility.findViewById(v, R.id.threads_count);
        final SeekBar threads = Utility.findViewById(v, R.id.threads);
        final Toolbar toolbar = Utility.findViewById(v, R.id.toolbar);
        final Button fetch = Utility.findViewById(v, R.id.fetch_name);
        final Button btnDownload = Utility.findViewById(v, R.id.btnDownload);

        if (urlDownloadlink != null && urlDownloadlink.length() > 5) {
            text.setText(urlDownloadlink);
            urlDownloadlink = null;
            combine = null;
        }


        if(strFrom != null)
        {
            if(mVideoUrl !=null && mVideoUrl.length() >0)
                text.setText(""+mVideoUrl);

            if(mVideoFullPath !=null && mVideoFullPath.length() >0)
            name.setText(""+mVideoFullPath);
        }

        threads.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                tCount.setText(String.valueOf(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {

            }

        });

        int def = mPrefs.getInt("threads", 4);
        threads.setProgress(def - 1);
        tCount.setText(String.valueOf(def));

        text.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

                String url = text.getText().toString().trim();

                if (!url.equals("")) {
                    int index = url.lastIndexOf("/");

                    if (index > 0) {
                        int end = url.lastIndexOf("?");

                        if (end < index) {
                            end = url.length();
                        }

                        name.setText(url.substring(index + 1, end));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable txt) {

            }
        });

        if (mPendingUrl != null) {
            text.setText(mPendingUrl);
        }

        toolbar.setTitle(R.string.add);
        toolbar.setNavigationIcon(R.drawable.ic_add_white_24dp);
        //toolbar.inflateMenu(R.menu.dialog_url);

        // Show the dialog
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(v)
                .create();

        dialog.show();

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NameFetcherTask().execute(text, name);
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    {
                        String url = text.getText().toString().trim();
                        String fName = name.getText().toString().trim();

                        File f = new File(mManager.getLocation() + "/" + fName);

                        if (f.exists()) {
                            Toast.makeText(MainActivity.this, R.string.msg_exists, Toast.LENGTH_SHORT).show();
                        } else if (!checkURL(url)) {
                            Toast.makeText(MainActivity.this, R.string.msg_url_malform, Toast.LENGTH_SHORT).show();
                        } else {

                            while (mBinder == null);

                            int res = mManager.startMission(url, fName, threads.getProgress() + 1);
                            mBinder.onMissionAdded(mManager.getMission(res));
                            mFragment.notifyChange();

                            mPrefs.edit().putInt("threads", threads.getProgress() + 1).commit();
                            dialog.dismiss();
                        }

                        //return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.okay) {
                    String url = text.getText().toString().trim();
                    String fName = name.getText().toString().trim();

                    File f = new File(mManager.getLocation() + "/" + fName);

                    if (f.exists()) {
                        Toast.makeText(MainActivity.this, R.string.msg_exists, Toast.LENGTH_SHORT).show();
                    } else if (!checkURL(url)) {
                        Toast.makeText(MainActivity.this, R.string.msg_url_malform, Toast.LENGTH_SHORT).show();
                    } else {

                        while (mBinder == null) ;

                        int res = mManager.startMission(url, fName, threads.getProgress() + 1);
                        mBinder.onMissionAdded(mManager.getMission(res));
                        mFragment.notifyChange();

                        mPrefs.edit().putInt("threads", threads.getProgress() + 1).commit();
                        dialog.dismiss();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    private boolean checkURL(String url) {
        try {
            URL u = new URL(url);
            u.openConnection();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private class NameFetcherTask extends AsyncTask<View, Void, Object[]> {

        @Override
        protected Object[] doInBackground(View[] params) {
            try {
                URL url = new URL(((EditText) params[0]).getText().toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String header = conn.getHeaderField("Content-Disposition");

                if (header != null && header.indexOf("=") != -1) {
                    return new Object[]{params[1], header.split("=")[1].replace("\"", "")};
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            super.onPostExecute(result);

            if (result != null) {
                ((EditText) result[0]).setText(result[1].toString());
            }
        }
    }

}
