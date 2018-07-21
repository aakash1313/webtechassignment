package uscworkshops.akashdeep.com.tneandroidapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import uscworkshops.akashdeep.com.tneandroidapp.R;

/**
 * Created by akash on 4/20/2018.
 */

public class PhotosResultsAdapter extends RecyclerView.Adapter<PhotosResultsAdapter.PhotoResultViewHolder>   {


    private Context mContext;
    private GeoDataClient geoDataClient;
    private ArrayList<PlacePhotoMetadata> photoResultList;
    public  PhotosResultsAdapter(Context mContext, List<PlacePhotoMetadata> list, GeoDataClient client)
    {
        this.mContext = mContext;
        this.photoResultList = (ArrayList<PlacePhotoMetadata>) list;
        this.geoDataClient = client;

    }
    @Override
    public PhotosResultsAdapter.PhotoResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photos_list_item,parent,false);
        return  new PhotosResultsAdapter.PhotoResultViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final PhotosResultsAdapter.PhotoResultViewHolder holder, int position) {
        PlacePhotoMetadata currentItem = photoResultList.get(position);
        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(currentItem);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();


                holder.mImageView.invalidate();
                holder.mImageView.setImageBitmap(photoBitmap);
            }
        });


    }
    @Override
    public int getItemCount() {
        return photoResultList.size();
    }

    public class PhotoResultViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;



        public PhotoResultViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.photo_list_item);
        }



    }

}
