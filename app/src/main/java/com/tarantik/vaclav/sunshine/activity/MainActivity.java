package com.tarantik.vaclav.sunshine.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tarantik.vaclav.sunshine.R;
import com.tarantik.vaclav.sunshine.fragment.DetailFragment;
import com.tarantik.vaclav.sunshine.fragment.ForecastFragment;
import com.tarantik.vaclav.sunshine.helper.Utility;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean twoPane;

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            twoPane = true;
            if(savedInstanceState== null){
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.weather_detail_container,new DetailFragment())
                        .commit();
            }else{
                twoPane = false;
            }
        }

        mLocation = Utility.getPreferredLocation(this);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if(mLocation.equals(Utility.getPreferredLocation(this))){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            ff.onLocationChanged();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mLocation = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }else if(id == R.id.action_show_location){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String preferredLocation = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri.Builder builder = new Uri.Builder();
            builder.path("geo:0,0?").appendQueryParameter("q", preferredLocation);
            Uri uri = Uri.parse("geo:0,0?q="+preferredLocation);
            intent.setData(uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }else{
                Log.d(TAG, "Something is wrong, desired path: " + uri.toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
