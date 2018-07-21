package uscworkshops.akashdeep.com.tneandroidapp.views;

/**
 * Created by akash on 4/24/2018.
 */

public class FavoriteResultItem {
    String place_id;
    String place_name;
    String place_address;
    String place_icon;

    public FavoriteResultItem()
    {
        resetParam();
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getPlace_icon() {
        return place_icon;
    }

    public void setPlace_icon(String place_icon) {
        this.place_icon = place_icon;
    }



    void resetParam()
    {
        place_id = null;
        place_name = null;
        place_address = null;
        place_icon = null;
    }
}
