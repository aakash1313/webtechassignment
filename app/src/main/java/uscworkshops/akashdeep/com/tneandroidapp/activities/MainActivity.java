package uscworkshops.akashdeep.com.tneandroidapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import uscworkshops.akashdeep.com.tneandroidapp.R;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceLatLong;
import uscworkshops.akashdeep.com.tneandroidapp.views.MainActivityViewPager;

public class MainActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static String currLat = null;
    public static String currLon = null;
    private final String TAG = "TNE_Tags";
    ViewPager mViewPager;
    PlaceLatLong mcurrentLatlon;
    TabLayout mTabLayout;
    MainActivityViewPager mAdapter;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    public static void reduceMarginsInTabs(TabLayout tabLayout, int marginOffset) {

        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            for (int i = 0; i < ((ViewGroup) tabStrip).getChildCount(); i++) {
                View tabView = tabStripGroup.getChildAt(i);
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) tabView.getLayoutParams()).leftMargin = marginOffset;
                    ((ViewGroup.MarginLayoutParams) tabView.getLayoutParams()).rightMargin = marginOffset;
                }
            }

            tabLayout.requestLayout();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_main_tab_layout);
        mAdapter = new MainActivityViewPager(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        getSupportActionBar().setElevation(0);
        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main_custom_tab_item, null);
        TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
        tabContent.setText("Search");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
        mTabLayout.getTabAt(0).setCustomView(tabContent);
        LinearLayout tabLinearLayout1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.main_custom_tab_item, null);
        TextView tabContent1 = (TextView) tabLinearLayout1.findViewById(R.id.tabContent);
        tabContent1.setText("Favourites");
        tabContent1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill_white, 0, 0, 0);
        mTabLayout.getTabAt(1).setCustomView(tabContent1);
        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.DKGRAY);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);
        reduceMarginsInTabs(mTabLayout, 110);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mcurrentLatlon = PlaceLatLong.getInstance();


    }

    private Location getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        try {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            return loc;
        } catch (SecurityException ex) {
            return null;
        }

    }

    @Override
    protected void onStart() {


        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currLat = String.valueOf(location.getLatitude());
            currLon = String.valueOf(location.getLongitude());
            mcurrentLatlon.setCurrentLat(currLat);
            mcurrentLatlon.setCurrentLong(currLon);


        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        Location loc = getLocation();
        if (loc != null) {
            currLat = String.valueOf(loc.getLatitude());
            currLon = String.valueOf(loc.getLongitude());
            mcurrentLatlon.setCurrentLat(currLat);
            mcurrentLatlon.setCurrentLong(currLon);
        }
        Log.d(TAG, "Last Known location is" + currLat + " " + currLon);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,
                            "permission was granted");

                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, this);
                    } catch (SecurityException e) {
                        Log.d(TAG,
                                "SecurityException:\n" + e.toString());
                    }
                } else {
                   Log.d(TAG,
                            "permission denied, ...:(");
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,
                "onConnectionFailed: \n" + connectionResult.toString());
    }
}


