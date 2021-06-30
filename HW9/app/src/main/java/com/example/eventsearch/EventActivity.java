package com.example.eventsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.eventsearch.databinding.ActivityEventBinding;
import com.example.eventsearch.ui.main.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity {

    private ActivityEventBinding binding;
    static JSONObject jsonObject = null;
    static String name = null;
    String date = null;
    String venue = null;
    String imageURL = null;
    String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);



        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("EventJSON"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Put in favorite button and others here, on creation go to event tab
        ImageButton backButton = findViewById(R.id.backArrow);

        // When back button is clicked, go to search
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton favoriteButton = findViewById(R.id.eventFavoriteButton);


        try {
            name = jsonObject.getString("name");
            date = jsonObject.getJSONObject("dates").getJSONObject("start")
                    .getString("localDate");
            venue = jsonObject.getJSONObject("_embedded").getJSONArray("venues")
                    .getJSONObject(0).getString("name");
            imageURL = jsonObject.getJSONArray("images").getJSONObject(0)
                    .getString("url");
            id = jsonObject.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(FavoritesFragment.eventIsFavorited(name, venue, date)){
            // Event is already favorited, so set that accordingly
            favoriteButton.setContentDescription("Filled");
            favoriteButton.setImageResource(R.drawable.filled_star);
        }



        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void  onClick(View v) {

                // Event being favorited
                Log.d("ContentDescription", favoriteButton.getContentDescription().toString());
                if(favoriteButton.getContentDescription().toString().equals("Unfilled")){
                    favoriteButton.setContentDescription("Filled");
                    favoriteButton.setImageResource(R.drawable.filled_star);
                    Log.d("Favorite", "Favoriting event with name " + name);
                    FavoritesFragment.addEvent(name, venue, date, imageURL, id);
                }
                // Event being unfavorited
                else{
                    favoriteButton.setContentDescription("Unfilled");
                    favoriteButton.setImageResource(R.drawable.unfilled_star);
                    FavoritesFragment.removeEvent(name, venue, date, imageURL);
                }
            }
        });

        ImageButton twitterButton = findViewById(R.id.twitterButton);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "https://twitter.com/intent/tweet?text=Check out " + name + " at " + venue;
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(URL));
                startActivity(viewIntent);
            }
        });

        Intent intent = new Intent(getApplicationContext(),EventInfoFragment.class);
        intent.putExtra("EventJSON", jsonObject.toString());
    }
}