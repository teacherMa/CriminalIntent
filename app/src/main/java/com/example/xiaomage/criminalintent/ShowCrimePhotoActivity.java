package com.example.xiaomage.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by xiaomage on 2017/1/27.
 */

public class ShowCrimePhotoActivity extends AppCompatActivity {

    private static final String CRIME_PHOTO_LOCATION = "crime_photo_location";


    public static Intent newIntent(Context context, String photoLocation){
        Intent intent = new Intent(context,ShowCrimePhotoActivity.class);
        intent.putExtra(CRIME_PHOTO_LOCATION,photoLocation);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {

        Log.e("scp", "onCreate:");

        super.onCreate(savedInstanceState, persistentState);

        String mPhotoFile = getIntent().getStringExtra(CRIME_PHOTO_LOCATION);

        setContentView(R.layout.activity_show_photo);

        FragmentManager fragmentManager = getSupportFragmentManager();
        ShowCrimePhotoFragment fragment =(ShowCrimePhotoFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragment = ShowCrimePhotoFragment.getNewInstance(mPhotoFile);
        }
        fragmentManager.beginTransaction().replace(R.id.fragment_show_photo_container,fragment).commit();

    }
}
