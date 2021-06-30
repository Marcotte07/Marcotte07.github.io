package com.example.eventsearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable
    private static ArrayList<String> nameList = new ArrayList<String>();
    private static ArrayList<String> venueList = new ArrayList<String>();
    private static ArrayList<String> dateList = new ArrayList<String>();
    private static ArrayList<String> imageList = new ArrayList<String>();
    static ArrayList<String> eventIDList = new ArrayList<String>();
    private static FavoritesAdapter adapter;
    private static TextView noFavorites;

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.favorite_fragment_layout, container, false);

        if(nameList.size() == 0){
            noFavorites = view.findViewById(R.id.noFavoritesTextView);
            noFavorites.setVisibility(View.VISIBLE);
        }
        else{
            noFavorites = view.findViewById(R.id.noFavoritesTextView);
            noFavorites.setVisibility(View.INVISIBLE);
        }

        adapter = new FavoritesAdapter(getActivity().getApplicationContext(), nameList, venueList, dateList, imageList);

        ListView myListView = view.findViewById(R.id.myListView);
        myListView.setAdapter(adapter);


        myListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Make second call here and then navigate to event info activity
                Intent intent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                String url = "https://app.ticketmaster.com/discovery/v2/events/" + eventIDList.get(position) + "?apikey=f2DRraG6ARgAnKE2Vc70MT18I7mGrOAp";
                Log.d("EventURL", url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("EVENTIDRESPONSE", response.toString());
                                intent.putExtra("EventJSON", response.toString());
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error

                            }
                        });
                requestQueue.add(jsonObjectRequest);
            }
        });



        return view;
    }

    public static void refresh(){
        adapter.notifyDataSetChanged();
        if(nameList.size() == 0) {
            noFavorites.setVisibility(View.VISIBLE);
        }
    }

    public static void addEvent(String name, String venue, String date, String image, String id){
        nameList.add(name);
        venueList.add(venue);
        dateList.add(date);
        imageList.add(image);
        eventIDList.add(id);
    }

    public static void removeEvent(String name, String venue, String date, String image){
        // Need to find index in arrayLists that
        for(int i = 0; i < nameList.size(); i++){
            if(nameList.get(i).equals(name) && venueList.get(i).equals(venue) && dateList.get(i).equals(date)){
                Log.d("REMOVING FAVORITE", "Removing index " + i + " from favorites");
                nameList.remove(i);
                venueList.remove(i);
                dateList.remove(i);
                imageList.remove(i);
                eventIDList.remove(i);
                return;
            }
        }
        return;
    }


    // Check if item is already in favorites list, this should be used OUTSIDE of this class
    public static boolean eventIsFavorited(String name, String venue, String date){
        boolean inList = false;
        for(int i = 0; i < nameList.size(); i++){
            if(nameList.get(i).equals(name) && venueList.get(i).equals(venue) && dateList.get(i).equals(date)){
                inList = true;
            }
        }
        return inList;
    }
}
