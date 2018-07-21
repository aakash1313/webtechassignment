package uscworkshops.akashdeep.com.tneandroidapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import uscworkshops.akashdeep.com.tneandroidapp.R;
import uscworkshops.akashdeep.com.tneandroidapp.activities.PlaceInfoActivity;
import uscworkshops.akashdeep.com.tneandroidapp.activities.SearchResultsActivity;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceInfoData;
import uscworkshops.akashdeep.com.tneandroidapp.views.FavoriteResultItem;
import uscworkshops.akashdeep.com.tneandroidapp.views.FavoritesAdapter;
import uscworkshops.akashdeep.com.tneandroidapp.views.ReviewResultItem;

/**
 * Created by akash on 4/14/2018.
 */

public class FavoritesFragment extends Fragment {

    Context mContext;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private FavoritesAdapter mResultsAdapter;
    private TextView mNoFav;
    FavoriteResultItem item;
    RecyclerView mRecyclerView;
    PlaceInfoData placeInfoData;
    private static final String TAG = "FavoritesFragment";
    private ArrayList<FavoriteResultItem> mFavouriteResultList;

    public static FavoritesFragment newInstance()
    {
        return new FavoritesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites,container,false);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favourites_recyclerview);
        mNoFav = (TextView) view.findViewById(R.id.no_favourites_found);
        editor = preferences.edit();



    }

    @Override
    public void onResume() {
        super.onResume();
        mFavouriteResultList = new ArrayList<FavoriteResultItem>();
        parseSharedPrefData();
        mResultsAdapter = new FavoritesAdapter(mContext, mFavouriteResultList, new FavoritesAdapter.FavouriteItemClick() {
            @Override
            public void favouriteOnClick(View v, int position) {
                item = mFavouriteResultList.get(position);
                if(preferences.contains(item.getPlace_id()))
                {
                    Toast.makeText(mContext,item.getPlace_name()+"was removed from favorites",Toast.LENGTH_SHORT).show();
                    editor.remove(item.getPlace_id());
                    editor.apply();
                    mFavouriteResultList.remove(item);

                    mRecyclerView.getAdapter().notifyItemRemoved(position);
                    if(mFavouriteResultList.size() <= 0)
                    {
                        mNoFav.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void itemOnClick(View v, int position) {
                item = mFavouriteResultList.get(position);
                updatePlaceData(item.getPlace_id());
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mResultsAdapter);

    }

    public void parseSharedPrefData()
    {
        mFavouriteResultList.clear();
        if(preferences.getAll().size() > 0)
        {
            mNoFav.setVisibility(View.GONE);
            Map<String, ?> allEntries = preferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                FavoriteResultItem fav = new FavoriteResultItem();
                String placeId = entry.getKey();
                String data = (String) entry.getValue();
                try{
                    JSONObject jsonObj = new JSONObject(data);
                    String placeName = jsonObj.getString("place_name");
                    String placeAddress = jsonObj.getString("place_address");
                    String placeIcon = jsonObj.getString("place_icon");
                    fav.setPlace_address(placeAddress);
                    fav.setPlace_name(placeName);
                    fav.setPlace_icon(placeIcon);
                    fav.setPlace_id(placeId);
                    mFavouriteResultList.add(fav);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        else
        {
            mNoFav.setVisibility(View.VISIBLE);
            //show no records
        }
    }
    private void updatePlaceData(String place_id) {
        if(place_id != null || place_id != "")
        {
            placeInfoData = PlaceInfoData.getInstance();
            ReviewsFragment.currentSelectedReview = "Google reviews";
            ReviewsFragment.currentSelectedOrder = "Default order";
            placeInfoData.resetParams();
            String url ="http://jarvisandroid.us-east-2.elasticbeanstalk.com/placeId/"+place_id;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG,"Response is"+response);
                            placeInfoData.resetParams();
                            placeInfoData.setPlaceId(item.getPlace_id());
                            placeInfoData.setPlaceName(item.getPlace_name());
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
                                String yelpURL = "http://jarvisandroid.us-east-2.elasticbeanstalk.com/address1/"+addressForYelp+"/city/"+cityForYelp+"/name/"+item.getPlace_name()+"/postal_code/"+postCodeForYelp+"/state/"+stateForYelp+"/country/"+countryForYelp;
                                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, yelpURL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.d(TAG, "Response is" + response);
                                                if(response != null && !response.equals("-1") && !response.equals(""))
                                                {

                                                    String businessId = response;
                                                    String yelp_reviews_url = "http://jarvisandroid.us-east-2.elasticbeanstalk.com/yelpBusinessId/"+businessId;
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
                                                                    Log.d(TAG,"Item was clicked"+item.getPlace_id());
                                                                    Intent i = new Intent(mContext, PlaceInfoActivity.class);
                                                                    i.putExtra("place_id", item.getPlace_id());
                                                                    i.putExtra("place_name",item.getPlace_name());
                                                                    startActivity(i);




                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                        }
                                                    });
                                                    Volley.newRequestQueue(mContext).add(stringRequest3);
                                                }
                                                else
                                                {
                                                    Intent i = new Intent(mContext, PlaceInfoActivity.class);
                                                    i.putExtra("place_id", item.getPlace_id());
                                                    i.putExtra("place_name",item.getPlace_name());
                                                    startActivity(i);
                                                }



                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                Volley.newRequestQueue(mContext).add(stringRequest2);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Volley.newRequestQueue(mContext).add(stringRequest);
        }
    }
}
