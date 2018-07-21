package uscworkshops.akashdeep.com.tneandroidapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uscworkshops.akashdeep.com.tneandroidapp.R;

/**
 * Created by akash on 4/24/2018.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavouriteResultViewHolder> {

    private Context mContext;
    private ArrayList<FavoriteResultItem> favouriteResultList;
    public FavouriteItemClick onClickListener;



    public interface FavouriteItemClick{

        void favouriteOnClick(View v, int position);

        void itemOnClick(View v, int position);
    }

    public  FavoritesAdapter(Context mContext,ArrayList<FavoriteResultItem> list, FavouriteItemClick listener)
    {
        this.mContext = mContext;
        this.favouriteResultList = list;
        this.onClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return favouriteResultList.size();
    }
    @Override
    public FavouriteResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favourite_list_item,parent,false);
        return  new FavouriteResultViewHolder(view);
    }
    @Override
    public void onBindViewHolder(FavouriteResultViewHolder holder, int position) {
        FavoriteResultItem currentItem = favouriteResultList.get(position);
        String imageUrl = currentItem.getPlace_icon();
        String placeName = currentItem.getPlace_name();
        String placeAddress = currentItem.getPlace_address();
        holder.mPlaceName.setText(placeName);
        holder.mPlaceAddress.setText(placeAddress);
        //Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext).load(imageUrl).fit().centerCrop().into(holder.mImageView);
        holder.mIsFavourite.setImageResource(R.drawable.heart_fill_red);

    }

    public class FavouriteResultViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mPlaceName;
        public TextView mPlaceAddress;
        public ImageView mIsFavourite;


        public FavouriteResultViewHolder(final View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.result_image_view_fav);
            mPlaceName = (TextView) itemView.findViewById(R.id.place_name_fav1);
            mPlaceAddress = (TextView) itemView.findViewById(R.id.place_address_fav1);
            mIsFavourite = (ImageView) itemView.findViewById(R.id.view_favorite_fav1);

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
