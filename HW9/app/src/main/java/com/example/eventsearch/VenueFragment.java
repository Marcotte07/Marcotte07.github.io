package com.example.eventsearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class VenueFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable


    @Override
    public android.view.View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {





        View view =  inflater.inflate(R.layout.venue_layout, container, false);

        return view;
    }
}
