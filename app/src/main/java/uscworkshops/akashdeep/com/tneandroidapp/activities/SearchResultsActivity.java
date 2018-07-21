package uscworkshops.akashdeep.com.tneandroidapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uscworkshops.akashdeep.com.tneandroidapp.R;
import uscworkshops.akashdeep.com.tneandroidapp.fragments.ReviewsFragment;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceInfoData;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.SearchRequestParams;
import uscworkshops.akashdeep.com.tneandroidapp.views.PlaceResultItem;
import uscworkshops.akashdeep.com.tneandroidapp.views.PlacesResultsAdapter;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private PlacesResultsAdapter mResultsAdapter;
    private ArrayList<PlaceResultItem> mplaceResultList;
    private Button mNextBtn;
    private GeoDataClient geoDataClient;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Button mPrevBtn;
    PlaceResultItem item;
    PlaceInfoData placeInfoData;
    private ProgressDialog progressBar;

    private final String TAG = "TNE_Tags";
    SearchRequestParams searchRequestParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.search_results_recyclerview);
        mNextBtn = (Button) findViewById(R.id.next_result_btn);
        mPrevBtn = (Button) findViewById(R.id.prev_result_btn);
        mRecyclerView.setHasFixedSize(true);
        placeInfoData = PlaceInfoData.getInstance();
        progressBar = new ProgressDialog(this);
        searchRequestParams = SearchRequestParams.getInstance();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mplaceResultList = new ArrayList<PlaceResultItem>();
        geoDataClient = Places.getGeoDataClient(this, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        parseJSON();
        mResultsAdapter = new PlacesResultsAdapter(SearchResultsActivity.this, mplaceResultList, new PlacesResultsAdapter.PlaceResultsListener() {
            @Override
            public void itemOnClick(View v, int position) {
                item = mplaceResultList.get(position);
                updatePlaceData(item.getPlaceId());


            }

            @Override
            public void favouriteOnClick(View v, int position) {
                item = mplaceResultList.get(position);
                if(preferences.contains(item.getPlaceId()))
                {
                    Toast.makeText(SearchResultsActivity.this,item.getPlaceName()+" was removed from favorites",Toast.LENGTH_SHORT).show();
                    editor.remove(item.getPlaceId());
                    editor.apply();
                    item.setFavourite(false);
                    mRecyclerView.getAdapter().notifyItemChanged(position);

                }
                else
                {
                    JSONObject placeDataJSON = new JSONObject();
                    String placeString = null;
                    try {
                        placeDataJSON.put("place_name", item.getPlaceName());
                        placeDataJSON.put("place_address",item.getPlaceAddress());
                        placeDataJSON.put("place_icon",item.getImageUrl());
                        placeDataJSON.put("place_id",item.getPlaceId());
                        placeString = placeDataJSON.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putString(item.getPlaceId(),placeString);
                    editor.apply();
                    Toast.makeText(SearchResultsActivity.this,item.getPlaceName()+" was added to favorites",Toast.LENGTH_SHORT).show();
                    item.setFavourite(true);
                    mRecyclerView.getAdapter().notifyItemChanged(position);
                }

            }
        });
        mRecyclerView.setAdapter(mResultsAdapter);
        mNextBtn.setOnClickListener(this);
        mPrevBtn.setOnClickListener(this);

    }

    private void updatePlaceData(String place_id) {
        if(place_id != null || place_id != "")
        {
            ReviewsFragment.currentSelectedReview = "Google reviews";
            ReviewsFragment.currentSelectedOrder = "Default order";
            placeInfoData.resetParams();
            String url ="http://rjarvis123.us-east-2.elasticbeanstalk.com/placeId/"+place_id;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG,"Response is"+response);
                            placeInfoData.resetParams();
                            placeInfoData.setPlaceId(item.getPlaceId());
                            placeInfoData.setPlaceName(item.getPlaceName());
                            Log.d(TAG,"Place details data is"+response);
                            //parseJSON();
                            placeInfoData.setPlaceInfoResult(response);
                            try {
                                JSONObject result = new JSONObject(response);
                                JSONObject r1 = result.getJSONObject("result");
                                JSONObject geometry = r1.getJSONObject("geometry");
                                JSONObject loc = geometry.getJSONObject("location");
                                if(r1.has("reviews"))
                                {
                                    JSONArray googleReview = r1.getJSONArray("reviews");
                                    placeInfoData.setGoogleReviewArray(googleReview);
                                }
                                String formattedAdress = r1.getString("formatted_address");
                                placeInfoData.setAddress(formattedAdress);
                                String vicinity = r1.getString("vicinity");
                                placeInfoData.setVicinity(vicinity);
                                String icon_url = r1.getString("icon");
                                placeInfoData.setPlaceIconUrl(icon_url);
                                String latitude = loc.getString("lat");
                                String longitude = loc.getString("lng");
                                placeInfoData.setPlaceLatitude(latitude);
                                placeInfoData.setPlaceLongitude(longitude);
                                String addressForYelp = "";
                                String stateForYelp = null;
                                String countryForYelp = null;
                                String postCodeForYelp = null;
                                String cityForYelp = null;
                                String placeForYelp = placeInfoData.getPlaceName();
                                JSONArray address_components = r1.getJSONArray("address_components");
                                for(int i =0; i < address_components.length(); i++)
                                {
                                    JSONArray type = address_components.getJSONObject(i).getJSONArray("types");
                                    String  abc = type.getString(0);
                                    if(abc.equals("postal_code"))
                                    {
                                        postCodeForYelp = address_components.getJSONObject(i).getString("short_name");
                                    }
                                    if(abc.equals("country"))
                                    {
                                        countryForYelp = address_components.getJSONObject(i).getString("short_name");
                                    }
                                    if(abc.equals("locality"))
                                    {
                                        cityForYelp = address_components.getJSONObject(i).getString("short_name");
                                    }
                                    if(abc.equals("street_number"))
                                    {
                                        addressForYelp += address_components.getJSONObject(i).getString("short_name")+ " ";
                                    }
                                    if(abc.equals("route"))
                                    {
                                        addressForYelp += address_components.getJSONObject(i).getString("short_name");
                                    }
                                    if(abc.equals("administrative_area_level_1"))
                                    {
                                        stateForYelp = address_components.getJSONObject(i).getString("short_name");
                                    }
                                }
                                if(addressForYelp == "")
                                {
                                    addressForYelp = null;
                                }
                                String yelpURL = "http://rjarvis123.us-east-2.elasticbeanstalk.com/address1/"+addressForYelp+"/city/"+cityForYelp+"/name/"+item.getPlaceName()+"/postal_code/"+postCodeForYelp+"/state/"+stateForYelp+"/country/"+countryForYelp;
                                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, yelpURL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.d(TAG, "Response is" + response);
                                                if(response != null && !response.equals("-1") && !response.equals(""))
                                                {

                                                    String businessId = response;
                                                    String yelp_reviews_url = "http://rjarvis123.us-east-2.elasticbeanstalk.com/yelpBusinessId/"+businessId;
                                                    StringRequest stringRequest3 = new StringRequest(Request.Method.GET, yelp_reviews_url,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    Log.d(TAG, "Response is" + response);
                                                                    try {
                                                                        JSONArray yelpArray = new JSONArray(response);
                                                                        placeInfoData.setYelpReviewArray(yelpArray);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    Log.d(TAG,"Item was clicked"+item.getPlaceId());
                                                                    Intent i = new Intent(SearchResultsActivity.this, PlaceInfoActivity.class);
                                                                    i.putExtra("place_id", item.getPlaceId());
                                                                    i.putExtra("place_name",item.getPlaceName());
                                                                    startActivity(i);




                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                        }
                                                    });
                                                    Volley.newRequestQueue(SearchResultsActivity.this).add(stringRequest3);
                                                }
                                                else
                                                {
                                                    Intent i = new Intent(SearchResultsActivity.this, PlaceInfoActivity.class);
                                                    i.putExtra("place_id", item.getPlaceId());
                                                    i.putExtra("place_name",item.getPlaceName());
                                                    startActivity(i);
                                                }



                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                Volley.newRequestQueue(SearchResultsActivity.this).add(stringRequest2);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Volley.newRequestQueue(SearchResultsActivity.this).add(stringRequest);
        }
    }


    private void parseJSON() {
        try{
            mplaceResultList.clear();
            ArrayList<String> placeResults = searchRequestParams.getPlaceSearchResults();
            if(placeResults.size() > 0)
            {
                String mPlacesResults = placeResults.get(placeResults.size() - 1);
                JSONObject obj = new JSONObject(mPlacesResults);
                JSONArray resultArray = obj.getJSONArray("results");
                for(int i = 0; i < resultArray.length(); i++)
                {
                    JSONObject row = resultArray.getJSONObject(i);
                    String placeName = row.getString("name");
                    String placeAddress = row.getString("vicinity");
                    String placeId = row.getString("place_id");
                    String iconUrl = row.getString("icon");
                    //Check whether it is favourite in shared preferences
                    boolean isFavourite;
                    if(preferences.contains(placeId))
                    {
                        isFavourite = true;
                    }
                    else
                    {
                        isFavourite = false;
                    }
                    mplaceResultList.add(new PlaceResultItem(iconUrl,placeName,placeAddress,placeId,isFavourite));

                }
                if(obj.has("next_page_token"))
                {
                    mNextBtn.setEnabled(true);
                }
                else
                {
                    mNextBtn.setEnabled(false);
                }
                if(placeResults.size() >1)
                {
                    mPrevBtn.setEnabled(true);
                }
                else
                {
                    mPrevBtn.setEnabled(false);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void clearData() {
        mplaceResultList.clear(); //clear list
        mResultsAdapter.notifyDataSetChanged(); //let your adapter know about the changes and reload view.
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.next_result_btn:
                final ArrayList<String> placeResults = searchRequestParams.getPlaceSearchResults();
                if(placeResults.size() > 0)
                {
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Fetching next page");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    String mPlacesResults = placeResults.get(placeResults.size() - 1);
                    try {
                        JSONObject obj = new JSONObject(mPlacesResults);
                        if(obj.has("next_page_token"))
                        {

                            String tokenId = obj.getString("next_page_token");
                            String url ="http://rjarvis123.us-east-2.elasticbeanstalk.com/token/"+tokenId;
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d(TAG,"Response is"+response);
                                            searchRequestParams.addToPlaceSearchResults(response);
                                            clearData();
                                            parseJSON();
                                            Log.d(TAG,"New list size is"+mplaceResultList.size());
                                            mResultsAdapter.notifyDataSetChanged();
                                            progressBar.dismiss();


                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(SearchResultsActivity.this,"Error occured while fetching next 20 values",Toast.LENGTH_SHORT).show();
                                    progressBar.dismiss();

                                }
                            });
                            Volley.newRequestQueue(SearchResultsActivity.this).add(stringRequest);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

                break;
            case R.id.prev_result_btn:
                                        ArrayList<String> placeResults1 = searchRequestParams.getPlaceSearchResults();
                                        if(placeResults1.size() > 0)
                                        {

                                            placeResults1.remove(placeResults1.size() -1);
                                            clearData();
                                            parseJSON();
                                            mResultsAdapter.notifyDataSetChanged();
                                        }

                                        Log.d(TAG,"Prev was clicked");
                                        break;
        }
    }
}
