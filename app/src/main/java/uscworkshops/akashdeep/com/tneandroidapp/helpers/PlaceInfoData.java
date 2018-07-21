package uscworkshops.akashdeep.com.tneandroidapp.helpers;

import com.google.android.gms.location.places.PlacePhotoMetadata;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by akash on 4/20/2018.
 */

public class PlaceInfoData {
    String address;
    String placeId;
    String phoneNumber;
    String ratingLevel;
    String pricingLevel;
    String googlePage;
    String websiteUrl;
    String placeLatitude;
    String placeLongitude;
    String vicinity;
    String placeIconUrl;
    JSONArray googleReviewArray;
    JSONArray yelpReviewArray;


    public JSONArray getGoogleReviewArray() {
        return googleReviewArray;
    }

    public void setGoogleReviewArray(JSONArray googleReviewArray) {
        this.googleReviewArray = googleReviewArray;
    }

    public void setPlaceIconUrl(String url)
    {
        this.placeIconUrl = url;
    }

    public String getPlaceIconUrl()
    {
        return placeIconUrl;
    }

    public JSONArray getYelpReviewArray() {
        return yelpReviewArray;
    }

    public void setYelpReviewArray(JSONArray yelpReviewArray) {
        this.yelpReviewArray = yelpReviewArray;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    String placeName;

    public String getPlaceInfoResult() {
        return placeInfoResult;
    }

    public void setPlaceInfoResult(String placeInfoResult) {
        this.placeInfoResult = placeInfoResult;
    }

    public String getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(String placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public String getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(String placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    String placeInfoResult;

    private static PlaceInfoData single_instance = null;

    public static PlaceInfoData getInstance()
    {
        if (single_instance == null)
            single_instance = new PlaceInfoData();

        return single_instance;
    }
    public void resetParams()
    {
        placeId = null;
        address = null;
        phoneNumber = null;
        ratingLevel = null;
        pricingLevel = null;
        googlePage = null;
        websiteUrl = null;
        placeInfoResult = null;
        googleReviewArray = null;
        yelpReviewArray = null;
        vicinity = null;
    }

    public String getVicinity()
    {
       return vicinity;
    }
    public void setVicinity(String vicinity)
    {
        this.vicinity = vicinity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRatingLevel() {
        return ratingLevel;
    }

    public void setRatingLevel(String ratingLevel) {
        this.ratingLevel = ratingLevel;
    }

    public String getPricingLevel() {
        return pricingLevel;
    }

    public void setPricingLevel(String pricingLevel) {
        this.pricingLevel = pricingLevel;
    }

    public String getGooglePage() {
        return googlePage;
    }

    public void setGooglePage(String googlePage) {
        this.googlePage = googlePage;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
