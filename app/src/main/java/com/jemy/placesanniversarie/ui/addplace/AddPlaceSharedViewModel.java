package com.jemy.placesanniversarie.ui.addplace;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddPlaceSharedViewModel extends ViewModel {


    MutableLiveData<Double> latitude;
    MutableLiveData<Double> longitude;
}
