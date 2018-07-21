package uscworkshops.akashdeep.com.tneandroidapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import uscworkshops.akashdeep.com.tneandroidapp.R;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceInfoData;
import uscworkshops.akashdeep.com.tneandroidapp.views.PlaceInfoViewPager;

public class PlaceInfoActivity extends AppCompatActivity {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    PlaceInfoViewPager mAdapter;
    private final String TAG = "TNE_Tags";
    SharedPreferences preferences;
    PlaceInfoData mPlaceInfoData;
    SharedPreferences.Editor editor;
    private Menu menu;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        progressBar = new ProgressDialog(PlaceInfoActivity.this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Fetching details");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        final Runnable r = new Runnable() {
            public void run() {
                progressBar.dismiss();
            }
        };
        new Handler().postDelayed(r, 1000);
        Intent intent = getIntent();
        String place_name = intent.getStringExtra("place_name");
        mViewPager = (ViewPager) findViewById(R.id.activity_placeinfo_view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_placeinfo_tab_layout);
        mAdapter = new PlaceInfoViewPager(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        initTabs();
        setTitle(place_name);
        mViewPager = (ViewPager) findViewById(R.id.activity_placeinfo_view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_placeinfo_tab_layout);
        mAdapter = new PlaceInfoViewPager(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mPlaceInfoData = PlaceInfoData.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        initTabs();


    }

    private void initTabs()
    {
        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.placeinfo_custom_tab_item, null);
        TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabInfoContent);
        tabContent.setText("Info");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_outline, 0, 0, 0);
        mTabLayout.getTabAt(0).setCustomView(tabContent);
        LinearLayout tabLinearLayout2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.placeinfo_custom_tab_item, null);
        TextView tabContent2 = (TextView) tabLinearLayout2.findViewById(R.id.tabInfoContent);
        tabContent2.setText("Photos");
        tabContent2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photos, 0, 0, 0);
        mTabLayout.getTabAt(1).setCustomView(tabContent2);
        LinearLayout tabLinearLayout3 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.placeinfo_custom_tab_item, null);
        TextView tabContent3 = (TextView) tabLinearLayout3.findViewById(R.id.tabInfoContent);
        tabContent3.setText("Maps");
        tabContent3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maps, 0, 0, 0);
        mTabLayout.getTabAt(2).setCustomView(tabContent3);
        LinearLayout tabLinearLayout4 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.placeinfo_custom_tab_item, null);
        TextView tabContent4 = (TextView) tabLinearLayout4.findViewById(R.id.tabInfoContent);
        tabContent4.setText("Reviews");
        tabContent4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.review, 0, 0, 0);
        mTabLayout.getTabAt(3).setCustomView(tabContent4);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        String id = mPlaceInfoData.getPlaceId();
        if(preferences.contains(id))
        {
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_fill_white));
        }
        else {

            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_outline_white));

        }
        this.menu = menu;
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.place_info_heart_btn:
                String id = mPlaceInfoData.getPlaceId();
                if(preferences.contains(id))
                {
                    Toast.makeText(this,mPlaceInfoData.getPlaceName()+" was removed from favorites",Toast.LENGTH_SHORT).show();
                    editor.remove(id);
                    editor.apply();
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_outline_white));
                }
                else
                {
                    JSONObject placeDataJSON = new JSONObject();
                    String placeString = null;
                    try {
                        placeDataJSON.put("place_name", mPlaceInfoData.getPlaceName());
                        placeDataJSON.put("place_address",mPlaceInfoData.getVicinity());
                        placeDataJSON.put("place_icon",mPlaceInfoData.getPlaceIconUrl());
                        placeDataJSON.put("place_id",mPlaceInfoData.getPlaceId());
                        placeString = placeDataJSON.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putString(id,placeString);
                    editor.apply();
                    Toast.makeText(this,mPlaceInfoData.getPlaceName()+" was added to favorites",Toast.LENGTH_SHORT).show();
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_fill_white));
                }
                return true;
            case R.id.place_info_share_btn:
                String tweetText = "Checkout "+mPlaceInfoData.getPlaceName()+" located at" + mPlaceInfoData.getAddress() + ". Website:"+mPlaceInfoData.getWebsiteUrl();
                Intent i2=new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet/?text="+tweetText));
                startActivity(i2);
                return true;
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
