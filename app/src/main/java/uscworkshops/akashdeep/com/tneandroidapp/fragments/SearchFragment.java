package uscworkshops.akashdeep.com.tneandroidapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uscworkshops.akashdeep.com.tneandroidapp.R;
import uscworkshops.akashdeep.com.tneandroidapp.activities.MainActivity;
import uscworkshops.akashdeep.com.tneandroidapp.activities.NoSearchResults;
import uscworkshops.akashdeep.com.tneandroidapp.activities.SearchResultsActivity;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceLatLong;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.SearchRequestParams;
import uscworkshops.akashdeep.com.tneandroidapp.views.PlaceAutocompleteAdapter;

/**
 * Created by akash on 4/14/2018.
 */

public class SearchFragment extends Fragment implements View.OnClickListener {


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    TextView errorKeyword;
    TextView errorAutoComplete;
    View rootView;
    Button search_btn;
    Button clear_btn;
    Spinner cateogorySpinner;
    String otherLocation = "";
    RadioGroup locationRadio;
    private AutoCompleteTextView autoCompleteTextView;
    private final String TAG = "TNE_Tags";
    private static String currentToken = "";
    EditText keywordEditText;
    String catSelected;
    SearchRequestParams searchQuery;
    private static final LatLngBounds BOUNDS_WORLD = new LatLngBounds(
            new LatLng(-90, -180), new LatLng(90, 180));
    protected GeoDataClient mGeoDataClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private RadioButton mCurrentLoc, mOtherLoc;
    String distance;
    private ProgressDialog progressBar;
    RequestQueue queue;
    private EditText mDistanceEdit;
    PlaceLatLong placeLatLong;
    MainActivity activityContext = null;
    TextWatcher mKeywordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            String keywordText = keywordEditText.getText().toString();


            if (keywordText == null || keywordText.trim().length() == 0) {
                errorKeyword.setVisibility(View.VISIBLE);

            } else {
                errorKeyword.setVisibility(View.GONE);
            }
        }
    };
    TextWatcher mAutoCompleteTextWat = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            otherLocation = autoCompleteTextView.getText().toString();


            if (otherLocation == null || otherLocation.trim().length() == 0) {
                errorAutoComplete.setVisibility(View.VISIBLE);

            } else {
                errorAutoComplete.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        return rootView;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = (MainActivity) context;
        queue = Volley.newRequestQueue(activityContext);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        errorKeyword = (TextView) view.findViewById(R.id.error_keyword);
        mCurrentLoc = (RadioButton) view.findViewById(R.id.currLocRadio);
        mOtherLoc = (RadioButton) view.findViewById(R.id.otherLocRadio);
        locationRadio = (RadioGroup) view.findViewById(R.id.locationRadioGroup);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.place_autocomplete_search_btn);
        cateogorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        mDistanceEdit = (EditText) view.findViewById(R.id.distance_input_text);
        errorAutoComplete = (TextView) view.findViewById(R.id.error_autocomplete);
        searchQuery = SearchRequestParams.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activityContext,
                R.array.category_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        cateogorySpinner.setAdapter(adapter);
        mGeoDataClient = Places.getGeoDataClient(activityContext, null);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(activityContext, mGeoDataClient, BOUNDS_WORLD, null);
        autoCompleteTextView.setAdapter(placeAutocompleteAdapter);
        placeLatLong = PlaceLatLong.getInstance();
        keywordEditText = (EditText) view.findViewById(R.id.edit_text);
        keywordEditText.addTextChangedListener(mKeywordTextWatcher);
        autoCompleteTextView.addTextChangedListener(mAutoCompleteTextWat);
        clear_btn = (Button) view.findViewById(R.id.clear_btn);
        clear_btn.setOnClickListener(this);
        search_btn = (Button) view.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);
        if(haveNetworkConnection())
        {

            search_btn.setEnabled(true);
        }
        else {
            Toast.makeText(activityContext, "Internet is not working",Toast.LENGTH_SHORT).show();
            search_btn.setEnabled(false);
        }
        locationRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.currLocRadio:
                                            autoCompleteTextView.setEnabled(false);
                                            break;
                    case R.id.otherLocRadio:
                                            autoCompleteTextView.setEnabled(true);
                                            break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.search_btn:
                boolean formValidResult = validateForm();
                if (formValidResult == true) {
                    catSelected = cateogorySpinner.getItemAtPosition(cateogorySpinner.getSelectedItemPosition()).toString();
                    Log.d(TAG,"Activity selected is"+catSelected);
                    //show progress and get the results using volley
                    progressBar = new ProgressDialog(v.getContext());
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Fetching results");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    distance = mDistanceEdit.getText().toString();
                    if(distance == null || distance.length() == 0)
                    {
                        distance = "16090";
                    }
                    else
                    {
                        distance = String.valueOf(Integer.parseInt(distance) * 1609);
                    }
                    searchQuery.resetQueryParams();
                    if(mCurrentLoc.isChecked())
                    {
                        String keywordText = keywordEditText.getText().toString();
                        searchQuery.setLatitude(placeLatLong.getCurrentLat());
                        searchQuery.setLongitude(placeLatLong.getCurrentLong());
                        searchQuery.setKeyword(keywordText);
                        searchQuery.setCategory(catSelected);
                        searchQuery.setOtherLocation(null);
                        searchQuery.setDistance(distance);
                        // Instantiate the RequestQueue.
                        String url ="http://rjarvis123.us-east-2.elasticbeanstalk.com/queryLat/"+searchQuery.getLatitude()+"/queryLong/"+searchQuery.getLongitude()+"/distance/"+searchQuery.getDistance()+"/keyword/"+searchQuery.getKeyword()+"/category/"+searchQuery.getCategory();
                        Log.d(TAG,url);
// Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                        Log.d(TAG,"Response is"+response);
                                        try{
                                            JSONObject res = new JSONObject(response);
                                            JSONArray result = res.getJSONArray("results");
                                            if(result.length() > 0)
                                            {
                                                searchQuery.addToPlaceSearchResults(response);
                                                progressBar.dismiss();
                                                Intent searchInt = new Intent(activityContext, SearchResultsActivity.class);
                                                startActivity(searchInt);
                                            }
                                            else
                                            {
                                                progressBar.dismiss();
                                                Intent searchInt = new Intent(activityContext, NoSearchResults.class);
                                                startActivity(searchInt);
                                            }


                                        }
                                        catch (JSONException ex)
                                        {
                                            Log.d(TAG,"Error occured while fetching data");
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar.dismiss();
                                Intent searchInt = new Intent(activityContext, NoSearchResults.class);
                                startActivity(searchInt);
                            }
                        });

// Add the request to the RequestQueue.
                        Volley.newRequestQueue(activityContext).add(stringRequest);
                    }
                    else {
                        if(mOtherLoc.isChecked())
                        {
                            String url = "http://rjarvis123.us-east-2.elasticbeanstalk.com/getPlaceLatlon/" + otherLocation;
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d(TAG, "Response is" + response);
                                            try {
                                                String keywordText = keywordEditText.getText().toString();
                                                JSONObject res = new JSONObject(response);
                                                JSONArray r1 = res.getJSONArray("results");
                                                JSONObject geometry = r1.getJSONObject(0).getJSONObject("geometry");
                                                JSONObject loc = geometry.getJSONObject("location");
                                                String lat = loc.getString("lat");
                                                String lng = loc.getString("lng");
                                                searchQuery.setCategory(catSelected);
                                                searchQuery.setOtherLocation(null);
                                                searchQuery.setDistance(distance);
                                                searchQuery.setLatitude(lat);
                                                searchQuery.setLongitude(lng);
                                                searchQuery.setKeyword(keywordText);
                                                String url ="http://rjarvis123.us-east-2.elasticbeanstalk.com/queryLat/"+searchQuery.getLatitude()+"/queryLong/"+searchQuery.getLongitude()+"/distance/"+searchQuery.getDistance()+"/keyword/"+searchQuery.getKeyword()+"/category/"+searchQuery.getCategory();
                                                Log.d(TAG,url);
// Request a string response from the provided URL.
                                                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                // Display the first 500 characters of the response string.
                                                                try{
                                                                    Log.d(TAG, "Response is" + response);
                                                                    JSONObject res = new JSONObject(response);
                                                                    JSONArray result = res.getJSONArray("results");
                                                                    if(result.length() > 0)
                                                                    {
                                                                        searchQuery.addToPlaceSearchResults(response);
                                                                        progressBar.dismiss();
                                                                        Intent searchInt = new Intent(activityContext, SearchResultsActivity.class);
                                                                        startActivity(searchInt);
                                                                    }
                                                                    else
                                                                    {
                                                                        progressBar.dismiss();
                                                                        Intent searchInt = new Intent(activityContext, NoSearchResults.class);
                                                                        startActivity(searchInt);
                                                                    }


                                                                }
                                                                catch (JSONException ex)
                                                                {
                                                                    Log.d(TAG,"Error occured while fetching data");
                                                                }

                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                });

// Add the request to the RequestQueue.
                                                Volley.newRequestQueue(activityContext).add(stringRequest2);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressBar.dismiss();
                                    Intent searchInt = new Intent(activityContext, NoSearchResults.class);
                                    startActivity(searchInt);
                                }
                            });
                            Volley.newRequestQueue(activityContext).add(stringRequest);

                        }
                    }

                }
                else
                {
                   Toast t1 = Toast.makeText(activityContext,"Please fix all fields with errors",Toast.LENGTH_SHORT);
                    t1.show();
                }
                break;
            case R.id.clear_btn:
                                keywordEditText.setText("");
                                errorAutoComplete.setText("");
                                mOtherLoc.setChecked(false);
                                mCurrentLoc.setChecked(true);
                                mDistanceEdit.setText("");
                                errorKeyword.setVisibility(View.GONE);
                                autoCompleteTextView.setText("");
                                errorAutoComplete.setVisibility(View.GONE);
                                break;
        }
    }






    private boolean validateForm() {
        String keywordText = keywordEditText.getText().toString();


        if (keywordText == null || keywordText.trim().length() == 0 || keywordText.equals("")) {
            errorKeyword.setVisibility(View.VISIBLE);
            if (mOtherLoc.isChecked()) {
                if(otherLocation.equals("") || otherLocation.length() == 0 || otherLocation == null)
                {
                    errorAutoComplete.setVisibility(View.VISIBLE);


                }
            }
            return false;
        } else {
            Log.d(TAG, "Keyword is" + keywordText);
        }
        if (mOtherLoc.isChecked()) {
            boolean status = true;
            if(otherLocation.equals("") || otherLocation.length() == 0 || otherLocation == null)
            {
                errorAutoComplete.setVisibility(View.VISIBLE);
                status = false;

            }
            return  status;
        }
        return true;


    }


}
