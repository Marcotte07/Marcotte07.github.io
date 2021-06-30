package com.example.eventsearch;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ArtistFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable


    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.artist_layout, container, false);

        JSONObject json = EventActivity.jsonObject;
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // BQCupJ6E8Y5jO-Fm4RzbwWDiLYmKMNz2cktFPf9-
        // v2155AnTB0cBoAt4ANK1nvNdRn-WT7bGHt9pWwzfkn40E90rzJ9xvKulJrghl
        // 2SyKo2FkP8wVQC3dsR2Dv-jck3GRKRY4GjaM_n0u1rjR68PHB7BvA
        String artist = null;
        try {
            artist = json.getJSONObject("_embedded").getJSONArray("attractions").getJSONObject(0).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://api.spotify.com/v1/search?q=" + artist + "&type=artist";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Spotify", response.toString());
                        JSONObject json;
                        try {
                            json = response.getJSONObject("artists");
                            int num = json.getInt("total");
                            Log.d("NUM Records", " " + num);

                            TextView artistName = view.findViewById(R.id.artistName);
                            TextView artistFollowers = view.findViewById(R.id.artistFollowers);
                            TextView artistPopularity = view.findViewById(R.id.artistPopularity);
                            TextView artistLink = view.findViewById(R.id.artistLink);
                            if(num == 0){
                                // SHOW TEAMS AND ERROR HERE
                                Log.d("Test", "In if");
                                artistName.setText(EventInfoFragment.artists_or_teams);
                                artistFollowers.setText("N/A");
                                artistPopularity.setText("N/A");
                                artistLink.setText("N/A");
                                artistLink.setTextColor(Color.parseColor("gray"));
                            }

                            else {
                                artistLink.setTextColor(Color.parseColor("#3F51B5"));
                                Log.d("Test", "In else");
                                String name = json.getJSONArray("items").getJSONObject(0).getString("name");
                                String followers = json.getJSONArray("items").getJSONObject(0).getJSONObject("followers").getString("total");
                                // followers = followers.toString();
                                String popularity = json.getJSONArray("items").getJSONObject(0).getString("popularity");
                                String link = json.getJSONArray("items").getJSONObject(0).getJSONObject("external_urls").getString("spotify");

                                artistName.setText(name);
                                artistFollowers.setText(followers);
                                artistPopularity.setText(popularity);

                                artistLink.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String URL = link;
                                        Intent viewIntent =
                                                new Intent("android.intent.action.VIEW",
                                                        Uri.parse(URL));
                                        startActivity(viewIntent);
                                    }
                                });

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Handle logic here

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer BQBB_MhuwDSWBSoBxDXs_fuQbsFr4-Ed_Pdw1TcTBNOYzxrfdEXJYlsqQNZiwfxH-u6aYMx_nQQeXZ0Dn_ln6-bUS2P4fuyDg4B4aPm0luIIx4V6KiNJDShifT3jJ9Ypr6IndSVQMLd_J6BobY3qEst86Q");

                return params;
            }};

        queue.add(jsonObjectRequest);
        return view;
    }
}

