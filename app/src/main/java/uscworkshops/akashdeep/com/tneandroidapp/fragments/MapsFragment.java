package uscworkshops.akashdeep.com.tneandroidapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uscworkshops.akashdeep.com.tneandroidapp.R;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceInfoData;
import uscworkshops.akashdeep.com.tneandroidapp.views.PlaceAutocompleteAdapter;


/**
 * Created by akash on 4/20/2018.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static final LatLngBounds BOUNDS_WORLD = new LatLngBounds(
            new LatLng(-90, -180), new LatLng(90, 180));
    protected GeoDataClient mGeoDataClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private Spinner travelModeSpinner;
    private LinearLayout parent;
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private Context mContext;
    private GoogleMap mMap;
    private String fromLat = "";
    private String fromLon = "";
    private String selectedFrom = "";
    private String fromFormattedAddress = "";
    private PlaceInfoData mPlaceInfo;
    TextWatcher mOtherTextWatcher;
    String modes[];
    String selectedMode = "Driving";
    TravelMode mode = TravelMode.DRIVING;
    private static final int overview = 0;
    private static final String TAG = "MAPFRAGMENT";

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.map_autocomplete_search_btn);
        mGeoDataClient = Places.getGeoDataClient(mContext, null);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(mContext, mGeoDataClient, BOUNDS_WORLD, null);
        autoCompleteTextView.setAdapter(placeAutocompleteAdapter);
        mPlaceInfo = PlaceInfoData.getInstance();
        travelModeSpinner = (Spinner) view.findViewById(R.id.travel_mode_spinner);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
                selectedFrom = item.getFullText(STYLE_BOLD).toString();
                repopulateMap();


            }
        });


        modes = getResources().getStringArray(R.array.travelmode_array);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.travelmode_array, android.R.layout.simple_spinner_item);
        travelModeSpinner.setAdapter(adapter);
        travelModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = parent.getSelectedItemPosition();
                selectedMode = modes[index];
                repopulateMap();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    void repopulateMap() {


        switch (selectedMode) {
            case "Driving":
                mode = TravelMode.DRIVING;
                break;
            case "Bicycling":
                mode = TravelMode.BICYCLING;
                break;
            case "Transit":
                mode = TravelMode.TRANSIT;
                break;
            case "Walking":
                mode = TravelMode.WALKING;
                break;


        }
        selectedFrom = autoCompleteTextView.getText().toString();
        String url = "http://rjarvis123.us-east-2.elasticbeanstalk.com/getPlaceLatlon/" + selectedFrom;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response is" + response);
                        try {
                            JSONObject r1 = new JSONObject(response);
                            JSONArray results = r1.getJSONArray("results");
                            JSONObject r2 = results.getJSONObject(0);
                            String formattedAddress = r2.getString("formatted_address");
                            Log.d(TAG, formattedAddress);
                            DirectionsResult r5 = getDirectionsDetails(formattedAddress, mPlaceInfo.getAddress(), mode);
                            if (r5 != null) {
                                mMap.clear();
                                addPolyline(r5, mMap);
                                positionCamera(r5.routes[overview], mMap,r5);
                                addMarkersToMap(r5, mMap);
                            }


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




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng destination = new LatLng(Double.parseDouble(mPlaceInfo.getPlaceLatitude()), Double.parseDouble(mPlaceInfo.getPlaceLongitude()));
        mMap.addMarker(new MarkerOptions().position(destination).title(mPlaceInfo.getPlaceName())).showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 10.0f));

    }


    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        Marker source = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng))
                .title(selectedFrom));
        Marker destination = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng))
                .title(mPlaceInfo.getPlaceName()));
        source.showInfoWindow();

    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap,DirectionsResult results) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng source = new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng);
        LatLng destination = new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng);
        builder.include(source);
        builder.include(destination);
        LatLngBounds latLngBounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.zoomOut());
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.parseColor("#0000FF")));
    }



    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey("AIzaSyDxDC3eagIh2894g0N1ic0iLfLp7V7uR-Q")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
