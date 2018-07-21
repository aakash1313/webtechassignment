package uscworkshops.akashdeep.com.tneandroidapp.helpers;

import java.util.ArrayList;

/**
 * Created by akash on 4/17/2018.
 */

public class SearchRequestParams {
    String latitude;
    String longitude;
    String keyword;
    String category;
    String distance;
    String otherLocation;
    ArrayList<String> placeSearchResult = new ArrayList<>();
    private static SearchRequestParams single_instance = null;
    private SearchRequestParams()
    {

    }

    // static method to create instance of Singleton class
    public static SearchRequestParams getInstance()
    {
        if (single_instance == null)
            single_instance = new SearchRequestParams();

        return single_instance;
    }

    public  void resetQueryParams()
    {
        latitude = null;
        longitude = null;
        keyword = null;
        category = null;
        distance = null;
        otherLocation = null;
        placeSearchResult.clear();
    }
    public void addToPlaceSearchResults(String result)
    {
        placeSearchResult.add(result);
    }

    public ArrayList<String> getPlaceSearchResults()
    {
        return placeSearchResult;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getOtherLocation() {
        return otherLocation;
    }

    public void setOtherLocation(String otherLocation) {
        this.otherLocation = otherLocation;
    }
}
