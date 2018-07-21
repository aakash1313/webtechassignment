package uscworkshops.akashdeep.com.tneandroidapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import uscworkshops.akashdeep.com.tneandroidapp.R;
import uscworkshops.akashdeep.com.tneandroidapp.helpers.PlaceInfoData;

/**
 * Created by akash on 4/20/2018.
 */

public class PhotosFragment extends Fragment {

    PlaceInfoData placeInfoData;
    public static final String TAG = "PlacePhotosFragment";
    Context mContext;
    TextView noPhoto;
    private List<PlacePhotoMetadata> photosDataList;
    ScrollView mScrollViewLayout;
    private GeoDataClient geoDataClient;
    LinearLayout mLinearPhotos;
    private int id = 1;

    public static PhotosFragment newInstance()
    {
        return new PhotosFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photos,container,false);
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
        placeInfoData = PlaceInfoData.getInstance();
        geoDataClient = Places.getGeoDataClient(mContext, null);
        mScrollViewLayout = (ScrollView) view.findViewById(R.id.scollable_photo_view);
        mLinearPhotos = (LinearLayout) view.findViewById(R.id.scrollable_linear_layoutpic);
        noPhoto = (TextView) view.findViewById(R.id.no_photo_found);
        getPhotos(placeInfoData.getPlaceId());


    }
    // Request photos and metadata for the specified place.
    private void getPhotos(String placeID) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = geoDataClient.getPlacePhotos(placeID);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if(photoMetadataBuffer.getCount()> 0)
                {
                    noPhoto.setVisibility(View.GONE);
                    for(int i = 0; i < photoMetadataBuffer.getCount(); i++)
                    {
                        // Get the first photo in the list.
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                        // Get a full-size bitmap for the photo.
                        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                Bitmap bitmap = photo.getBitmap();
                                ImageView image = new ImageView(mContext);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700);
                                layoutParams.setMargins(2,2,2,10);

                                image.setLayoutParams(layoutParams);
                                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                image.setPadding(5,5,5,5);
                                mLinearPhotos.addView(image);
                                image.invalidate();
                                image.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
                else
                {

                        noPhoto.setVisibility(View.VISIBLE);
                }


            }
        });
    }

}
