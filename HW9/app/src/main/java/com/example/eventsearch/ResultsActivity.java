package com.example.eventsearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    JSONObject jsonObject;
    static ArrayList<String> nameList = new ArrayList<String>();
    static ArrayList<String> venueList = new ArrayList<String>();
    static ArrayList<String> dateList = new ArrayList<String>();
    static ArrayList<String> imageList = new ArrayList<String>();
    static ArrayList<String> eventIDList = new ArrayList<String>();
    private static TextView noResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_layout);


        ListView myListView = findViewById(R.id.myListView);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });



        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("results"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray eventArr = jsonObject.getJSONObject("_embedded").getJSONArray("events");
            nameList.clear();
            venueList.clear();
            imageList.clear();
            dateList.clear();
            eventIDList.clear();
            for(int i = 0; i < eventArr.length(); i++){
                // Add name, venue, date, and image icon url to arrays
                String name = eventArr.getJSONObject(i).getString("name");
                String date = eventArr.getJSONObject(i).getJSONObject("dates").getJSONObject("start")
                .getString("localDate");
                String venue = eventArr.getJSONObject(i).getJSONObject("_embedded").getJSONArray("venues")
                        .getJSONObject(0).getString("name");
                String imageURL = eventArr.getJSONObject(i).getJSONArray("images").getJSONObject(0)
                        .getString("url");
                String id = eventArr.getJSONObject(i).getString("id");
                Log.d("Results","" + name + " " + date + " " + venue + " " + imageURL);
                nameList.add(name);
                venueList.add(venue);
                imageList.add(imageURL);
                dateList.add(date);
                eventIDList.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // NEED ICON, NAME, VENUE, DATE, LIKE BUTTON

        noResults = findViewById(R.id.noResultsTextView);

        if(nameList.size() == 0){
            noResults.setVisibility(View.VISIBLE);
        }
        else{
            noResults.setVisibility(View.INVISIBLE);
        }


        ResultsAdapter adapter = new ResultsAdapter(this, nameList, venueList, imageList, dateList, eventIDList);

        myListView.setAdapter(adapter);
        myListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Make second call here and then navigate to event info activity
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                // Make call to HW6 backend
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

    }
}