package com.example.xiaomage.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by xiaomage on 2017/1/27.
 */

public class ShowCrimePhotoFragment extends DialogFragment {

    ImageView mCrimePhotoView;
    String mFileLocation;
    private static final String PHOTO_LOCATION = "crime_photo_location";

    public static ShowCrimePhotoFragment getNewInstance(String fileLocation){

        Bundle args = new Bundle();
        args.putString(PHOTO_LOCATION,fileLocation);
        ShowCrimePhotoFragment fragment = new ShowCrimePhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mFileLocation = getArguments().getString(PHOTO_LOCATION);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_show_crime_photo,null);
        mCrimePhotoView = (ImageView)v.findViewById(R.id.show_crime_photo_ImageView);
        Bitmap crimePhoto = PictureUtils.getScaledBitMap(mFileLocation,getActivity());
        mCrimePhotoView.setImageBitmap(crimePhoto);

        Dialog dialog =new  AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.Dark);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return dialog;
    }
}
