package uscworkshops.akashdeep.com.tneandroidapp.helpers;

/**
 * Created by akash on 4/17/2018.
 */

public class PlaceLatLong {


    String currentLat;

    String currentLong;

    private static PlaceLatLong single_instance = null;

    public static PlaceLatLong getInstance()
    {
        if (single_instance == null)
            single_instance = new PlaceLatLong();

        return single_instance;
    }

    public String getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(String currentLat) {
        this.currentLat = currentLat;
    }

    public String getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(String currentLong) {
        this.currentLong = currentLong;
    }
}
