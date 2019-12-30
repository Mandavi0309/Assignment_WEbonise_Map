package map.develop.com.mapdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import map.develop.com.mapdemo.FullScreenActivity;
import map.develop.com.mapdemo.R;
import map.develop.com.mapdemo.model.Restaurant;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

  private ArrayList<Restaurant> restaurants;
  private Context context;


  public RestaurantAdapter(ArrayList<Restaurant> restaurants, Context context) {
    this.restaurants = restaurants;
    this.context = context;

  }


  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View listItem = layoutInflater.inflate(R.layout.data_item, parent, false);
    ViewHolder viewHolder = new ViewHolder(listItem);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


    holder.tvDesc.setText(restaurants.get(position).getAddress());

    holder.tvTitle.setText(restaurants.get(position).getName());
    holder.tvRatings.setText(restaurants.get(position).getRatings());

    Glide.with(context)
        .load(restaurants.get(position).getImage())
        .into(holder.ivNewsImage);

    holder.mainLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String url = getUrl(restaurants.get(position).getPhotoReference());
        Intent in = new Intent(context, FullScreenActivity.class);
        in.putExtra("Photo", url);
        context.startActivity(in);

      }
    });


  }


  @Override
  public int getItemCount() {
    return restaurants.size();
  }


  public static class ViewHolder extends RecyclerView.ViewHolder {

    final TextView tvTitle, tvDesc, tvRatings;
    final ImageView ivNewsImage;
    final View mView;
    final LinearLayout mainLayout;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      tvTitle = view.findViewById(R.id.tv_title);
      tvDesc = view.findViewById(R.id.tv_description);
      tvRatings = view.findViewById(R.id.tv_ratings);
      ivNewsImage = view.findViewById(R.id.image);
      mainLayout = view.findViewById(R.id.main_rl);

    }
  }

  private String getUrl(String photoReference) {
    StringBuilder googlePlacesUrl = new StringBuilder(
        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400");
    googlePlacesUrl.append("&photoreference=" + photoReference);
    googlePlacesUrl.append("&key=" + "AIzaSyCq179894LXc7HlDo_Umnv9sXbgom6R0EY");
    Log.d("getUrl", googlePlacesUrl.toString());
    return (googlePlacesUrl.toString());
  }


}


