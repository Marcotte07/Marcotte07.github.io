package com.example.eventsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesAdapter extends BaseAdapter {

    ArrayList<String> nameList;
    ArrayList<String> venueList;
    ArrayList<String> dateList;
    ArrayList<String> imageList;
    LayoutInflater mInflater;
    public FavoritesAdapter(Context c, ArrayList<String> nameList, ArrayList<String> venueList, ArrayList<String> dateList, ArrayList<String> imageList){
        this.nameList = nameList;
        this.venueList = venueList;
        this.imageList = imageList;
        this.dateList = dateList;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.my_listview_detail, null);

        TextView nameTextView = v.findViewById(R.id.eventNameTextView);
        TextView venueTextView = v.findViewById(R.id.eventVenueTextView);
        TextView dateTextView = v.findViewById(R.id.dateTextView);

        // Set everything in the view here
        String name = nameList.get(position);
        if(name.length() > 20){
            nameTextView.setText(name.substring(0,20) + "...");
        }
        else{
            nameTextView.setText(name);
        }
        String venue = venueList.get(position);
        if(venue.length() > 20){
            venueTextView.setText(venue.substring(0,20) + "...");
        }
        else{
            venueTextView.setText(venue);
        }
        String date = dateList.get(position);
        dateTextView.setText(date);

        String image = imageList.get(position);
        ImageButton button = v.findViewById(R.id.eventFavoriteButton);
        ImageView imageView = v.findViewById(R.id.imageView);

        Picasso.get().load(image).fit().centerCrop().into(imageView);

        if(FavoritesFragment.eventIsFavorited(name, venue, date)){
            // Event is already favorited, so set that accordingly
            button.setContentDescription("Filled");
            button.setImageResource(R.drawable.filled_star);
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void  onClick(View v) {
                // Remove from favorites
                FavoritesFragment.removeEvent(name, venue, date, image);
                // Call some function that refreshes the fragment!
                FavoritesFragment.refresh();
            }
        });

        return v;
    }
}
