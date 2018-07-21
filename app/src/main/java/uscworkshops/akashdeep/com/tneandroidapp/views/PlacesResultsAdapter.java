package uscworkshops.akashdeep.com.tneandroidapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uscworkshops.akashdeep.com.tneandroidapp.R;

/**
 * Created by akash on 4/18/2018.
 */

public class PlacesResultsAdapter extends RecyclerView.Adapter<PlacesResultsAdapter.PlaceResultViewHolder> {

    private Context mContext;
    private ArrayList<PlaceResultItem> placeResultList;
    public PlaceResultsListener onClickListener;



    public interface PlaceResultsListener{

        void itemOnClick(View v,int position);

        void favouriteOnClick(View v,int position);
    }








    public  PlacesResultsAdapter(Context mContext,ArrayList<PlaceResultItem> list, PlaceResultsListener listener)
    {
            this.mContext = mContext;
            this.placeResultList = list;
            this.onClickListener = listener;
    }

    @Override
    public PlaceResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_list_item,parent,false);
        return  new PlaceResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceResultViewHolder holder, int position) {
        PlaceResultItem currentItem = placeResultList.get(position);
        String imageUrl = currentItem.getImageUrl();
        String placeName = currentItem.getPlaceName();
        String placeAddress = currentItem.getPlaceAddress();
        boolean isFavourite = currentItem.isFavourite();

        holder.mPlaceName.setText(placeName);
        holder.mPlaceAddress.setText(placeAddress);
        //Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext).load(imageUrl).fit().centerCrop().into(holder.mImageView);
        if(isFavourite)
        {
            holder.mIsFavourite.setImageResource(R.drawable.heart_fill_red);
        }
        else
        {
            holder.mIsFavourite.setImageResource(R.drawable.heart_outline_black);
        }
    }



    @Override
    public int getItemCount() {
        return placeResultList.size();
    }

    public class PlaceResultViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mPlaceName;
        public TextView mPlaceAddress;
        public ImageView mIsFavourite;


        public PlaceResultViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.result_image_view);
            mPlaceName = (TextView) itemView.findViewById(R.id.place_name);
            mPlaceAddress = (TextView) itemView.findViewById(R.id.place_address);
            mIsFavourite = (ImageView) itemView.findViewById(R.id.view_favorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemOnClick(v,getAdapterPosition());
                }
            });

            mIsFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.favouriteOnClick(v,getAdapterPosition());
                }
            });

        }



    }
}
