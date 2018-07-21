package uscworkshops.akashdeep.com.tneandroidapp.views;

/**
 * Created by akash on 4/18/2018.
 */

public class PlaceResultItem {
    String imageUrl;
    String placeName;
    String placeAddress;
    String placeId;
    boolean isFavourite;




    public PlaceResultItem(String imageUrl, String placeName, String placeAddress, String placeId, boolean isFavourite)
    {
        this.imageUrl = imageUrl;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeId = placeId;
        this.isFavourite = isFavourite;
    }
    public String getImageUrl() {
        return imageUrl;
    }



    public String getPlaceName() {
        return placeName;
    }



    public String getPlaceAddress() {
        return placeAddress;
    }



    public String getPlaceId() {
        return placeId;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
