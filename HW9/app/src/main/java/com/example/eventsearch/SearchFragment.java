package com.example.eventsearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    @Nullable
    @org.jetbrains.annotations.Nullable

    String units = null;
    String lat = "34.0224";
    String lng = "-118.2851";
    String keyword = null;
    String category = null;
    String distance = null;


    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.search_fragment_layout, container, false);
        //get the spinner from the xml.
        Spinner categoryDropdown = (Spinner) view.findViewById(R.id.categoryDropdown);
        //create a list of items for the spinner.
        String[] items = {"All", "Music", "Sports", "Arts & Theatre", "Film", "Miscellaneous"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                items);
        //set the spinners adapter to the previously created one.
        categoryDropdown.setAdapter(adapter);

        Spinner distanceDropdown = (Spinner) view.findViewById(R.id.distanceDropdown);
        //create a list of items for the spinner.
        String[] unit = {"Miles", "Kilometers"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                unit);
        //set the spinners adapter to the previously created one.
        distanceDropdown.setAdapter(distanceAdapter);

        EditText otherLocationEditText = (EditText) view.findViewById(R.id.otherLocationEditText);

        RadioButton currButton = (RadioButton) view.findViewById(R.id.currentRadioButton);
        RadioButton otherButton = (RadioButton) view.findViewById(R.id.otherRadioButton);

        EditText keywordEditText = (EditText) view.findViewById(R.id.keywordEditText);
        EditText distanceEditText = (EditText) view.findViewById(R.id.distanceEditText);

        currButton.setOnClickListener(new View.OnClickListener() {
            public void  onClick(View v) {
                Log.d("ENABLE", "Other Location is being enabled");
                otherLocationEditText.setFocusableInTouchMode(false);
                otherLocationEditText.setFocusable(false);
                otherLocationEditText.setEnabled(false);
                otherLocationEditText.setError(null);
                otherLocationEditText.setText("");
            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("DISABLE", "Other Location is being disabled");
                otherLocationEditText.setFocusableInTouchMode(true);
                otherLocationEditText.setFocusable(true);
                otherLocationEditText.setEnabled(true);
            }
        });


        // onClick handler for the clear button
        Button clearButton = (Button) view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("CLEARBUTTON", "Resetting all fields");
                // Reset all fields to base
                categoryDropdown.setSelection(0,true);
                distanceDropdown.setSelection(0,true);
                keywordEditText.setText("");
                keywordEditText.setError(null);
                currButton.setChecked(true);
                otherLocationEditText.setText("");
                otherLocationEditText.setFocusableInTouchMode(false);
                otherLocationEditText.setFocusable(false);
                otherLocationEditText.setEnabled(false);
                otherLocationEditText.setError(null);
                distanceEditText.setText("10");
            }
        });

        Button searchButton = (Button) view.findViewById(R.id.submitButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Need to first validate that all fields are ok
                boolean valid = validateFields(view);
                if(!valid){
                    return;
                }

                // Get ALL VALUES NOW
                keyword = keywordEditText.getText().toString().toLowerCase();
                category = categoryDropdown.getSelectedItem().toString().toLowerCase();
                if(category == "arts & theatre"){
                    category = "arts&theatre";
                }
                distance = distanceEditText.getText().toString();
                if(isWhiteSpace(distance)){
                    distance = "10";
                }
                units = distanceDropdown.getSelectedItem().toString().toLowerCase();


                getData(view);
            }
        });



        return view;
    }

    private void getData(View view){
        // All fields are valid, so now get proper lat and lng and then
        EditText otherLocationEditText = view.findViewById(R.id.otherLocationEditText);
        RadioButton otherRadioButton = view.findViewById(R.id.otherRadioButton);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        TextView resultTextView = (TextView) view.findViewById(R.id.resultTextView);

        String address = otherLocationEditText.getText().toString();
        // Make call to API here
        if(otherRadioButton.isChecked()){
            Log.d("INFO", "Other Location is Checked and calling API NOW");
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    address +
                    "&key=AIzaSyBUVIzR6tIwztkSldSmGFP7X6C_-9QmQtw";
            Log.d("URL", "URL is " + url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("results");

                                Double newLat = jsonArray.getJSONObject(0).getJSONObject("geometry")
                                        .getJSONObject("location").getDouble("lat");
                                Double newLng = jsonArray.getJSONObject(0).getJSONObject("geometry")
                                        .getJSONObject("location").getDouble("lng");

                                // get Lat lng data from previous call and make main data call here

                                String newURL = "https://csci-571-hw8-nick-marcotte.uc.r.appspot.com/retrieveSearchResults?keyword=" + keyword + "&distance=" + distance +
                                        "&units=" + units + "&lat=" + newLat + "&lng=" + newLng + "&event=" + category;

                                Log.d("Backend call", "URL is " + newURL);

                                // SECOND REQUEST HERE, ONLY GOES ONCE FIRST REQUEST FINISHES
                                JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest
                                        (Request.Method.GET, newURL, null, new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    newActivity(response);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // TODO: Handle error

                                            }
                                        });
                                queue.add(jsonObjectRequest2);


                            }catch (JSONException err){
                                Log.d("Error", err.toString());
                            }



                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERROR", "Error Retrieving lat and lng from google");
                        }
                    });
            queue.add(jsonObjectRequest);
        }

        else{
            String newURL = "https://csci-571-hw8-nick-marcotte.uc.r.appspot.com/retrieveSearchResults?keyword=" + keyword + "&distance=" + distance +
                    "&units=" + units + "&lat=" + lat + "&lng=" + lng + "&event=" + category;

            Log.d("Backend call", "URL is " + newURL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, newURL, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                newActivity(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error

                        }
                    });
            queue.add(jsonObjectRequest);

        }

    }


    // Loads results activity with JSON from search
    public void newActivity(JSONObject response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response.toString());
        Log.d("EventResult", "JSON is " + jsonObject);


        Intent startIntent = new Intent(getActivity().getApplicationContext(), ResultsActivity.class);
        startIntent.putExtra("results", jsonObject.toString());
        startActivity(startIntent);
    }




    public boolean validateFields(View view){
        EditText keywordEditText = view.findViewById(R.id.keywordEditText);
        EditText locationEditText = view.findViewById(R.id.otherLocationEditText);
        RadioButton otherRadioButton = view.findViewById(R.id.otherRadioButton);

        boolean isValid = true;
        // Returns true if edit text is INVALID
        if(isWhiteSpace(keywordEditText.getText().toString())){
            keywordEditText.setError("Please Enter a Valid Keyword");
            isValid = false;
        }
        if(otherRadioButton.isChecked()){
            if(isWhiteSpace(locationEditText.getText().toString())){
                locationEditText.setError("Please Enter a Valid Location");
                isValid = false;
            }
        }

        return isValid;
    }

    public boolean isWhiteSpace(String s){
        if(s == null){
            return true;
        }
        else{
            boolean nonSpaceChar = false;
            for(int i = 0; i < s.length(); i++){
                if(s.charAt(i) != ' '){
                    nonSpaceChar = true;
                }
            }
            return !nonSpaceChar;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
