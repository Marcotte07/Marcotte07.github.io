package com.example.eventsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventInfoFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable

    String date = null;
    static String venue = null;
    String category = null;
    String priceRange = null;
    String ticketStatus = null;
    String ticketLink = null;
    String mapLink = null;
    static String artists_or_teams = null;
    @Override
    public android.view.View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // Need Artists/Teams, Venue, Date, Category,
        // Price Range, Ticket Status, TicketMaster Link, Seat Map link
        JSONObject json = EventActivity.jsonObject;
        try {
            date = json.getJSONObject("dates").getJSONObject("start").getString("localDate");
            venue = json.getJSONObject("_embedded").getJSONArray("venues")
                    .getJSONObject(0).getString("name");
            ArrayList<String> categories = new ArrayList<String>();

            JSONObject sc = json.getJSONArray("classifications").getJSONObject(0);
            if(sc.has("genre")){
                if(sc.getJSONObject("genre").getString("name") != "Undefined"){
                    categories.add(sc.getJSONObject("genre").getString("name"));
                }
            }
            if(sc.has("segment")){
                if(sc.getJSONObject("segment").getString("name") != "Undefined"){
                    categories.add(sc.getJSONObject("segment").getString("name"));
                }
            }
            if(sc.has("subGenre")){
                if(sc.getJSONObject("subGenre").getString("name") != "Undefined"){
                    categories.add(sc.getJSONObject("subGenre").getString("name"));
                }
            }
            if(sc.has("subType")){
                if(sc.getJSONObject("subType").getString("name") != "Undefined"){
                    categories.add(sc.getJSONObject("subType").getString("name"));
                }
            }
            if(sc.has("type")){
                if(sc.getJSONObject("type").getString("name") != "Undefined"){
                    categories.add(sc.getJSONObject("type").getString("name"));
                }
            }
            category = TextUtils.join(" | ", categories);
            Log.d("Category", category);

            // price range
            if(json.has("priceRanges")){
                String min = json.getJSONArray("priceRanges").getJSONObject(0).getString("min");
                String max = json.getJSONArray("priceRanges").getJSONObject(0).getString("max");
                priceRange = min + " - " + max + " USD";
            }
            Log.d("PriceRange", "" + priceRange);

            ticketStatus = json.getJSONObject("dates").getJSONObject("status").getString("code");
            Log.d("Ticket Status", ticketStatus);

            ticketLink = json.getString("url");
            Log.d("Ticket link", ticketLink);

            mapLink = json.getJSONObject("seatmap").getString("staticUrl");
            Log.d("Map Link", mapLink);

            JSONArray attractionsArray = json.getJSONObject("_embedded").getJSONArray("attractions");
            if(attractionsArray.length() == 1){
                artists_or_teams = attractionsArray.getJSONObject(0).getString("name");
            }
            else{
                artists_or_teams = attractionsArray.getJSONObject(0).getString("name") + " | " +
                        attractionsArray.getJSONObject(1).getString("name");
            }


            // NOW SET ALL TEXT VIEWS HERE and onClick events for the map and ticket link

        } catch (JSONException e) {
            e.printStackTrace();
        }


        View view =  inflater.inflate(R.layout.event_info_layout, container, false);

        // Artist
        TextView artistTextView = view.findViewById(R.id.ArtistTextView);
        if(artists_or_teams != null) {
            artistTextView.setText(artists_or_teams);
        }
        else{
            artistTextView.setText("N/A");
        }
        // Venue
        TextView venueTextView = view.findViewById(R.id.venueTextView);
        if(venue != null) {
            venueTextView.setText(venue);
        }
        else{
            venueTextView.setText("N/A");
        }

        TextView dateTextView = view.findViewById(R.id.dateTextView2);
        if(date != null) {
            dateTextView.setText(date);
        }
        else{
            dateTextView.setText("N/A");
        }

        TextView categoryTextView = view.findViewById(R.id.categoryTextView);
        if(category != null) {
            categoryTextView.setText(category);
        }
        else{
            categoryTextView.setText("N/A");
        }

        TextView priceRangeTextView = view.findViewById(R.id.priceRangeTextView);
        if(priceRange != null) {
            priceRangeTextView.setText(priceRange);
        }
        else{
            priceRangeTextView.setText("N/A");
        }
        TextView ticketStatusTextView = view.findViewById(R.id.ticketStatusTextView);
        if(ticketStatus != null) {
            ticketStatusTextView.setText(ticketStatus);
        }
        else{
            ticketStatusTextView.setText("N/A");
        }
        TextView ticketLinkTextView = view.findViewById(R.id.ticketLinkTextView);
        // Set onClick Method here, redirects to url
        ticketLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(ticketLink));
                startActivity(viewIntent);
            }
        });
        TextView seatMapTextView = view.findViewById(R.id.seatMapTextView);
        // Set onClick Method here, redirects to url
        seatMapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(mapLink));
                startActivity(viewIntent);
            }
        });

        return view;
    }
}

