package com.tarantik.vaclav.sunshine.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tarantik.vaclav.sunshine.R;
import com.tarantik.vaclav.sunshine.fragment.DetailFragment;
import com.tarantik.vaclav.sunshine.fragment.ForecastFragment;
import com.tarantik.vaclav.sunshine.helper.Utility;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean twoPane;

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            Log.d(TAG,"Two pane");
            twoPane = true;
            if(savedInstanceState== null){
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.weather_detail_container,new DetailFragment())
                        .commit();
            }else{
                Log.d(TAG,"One pane");
                twoPane = false;
                getSupportActionBar().setElevation(0);
            }
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            forecastFragment.setUseTodayLayout(!twoPane);
        }

        mLocation = Utility.getPreferredLocation(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String location = Utility.getPreferredLocation(this);

        if(location!= null && !location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(ff !=null){
                ff.onLocationChanged();
            }

            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(df != null){
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if (twoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dateUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }
    }
}
