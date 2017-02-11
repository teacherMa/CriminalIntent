package com.example.xiaomage.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by xiaomage on 2016/12/26.
 */

public class CrimeCreateFragment extends DialogFragment {

    ImageButton mImageButtonOk;
    ImageButton mImageButtonCancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

//        Log.e("CrimeCreateFragment","onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        Log.e("CrimeCreateFragment","onCreateView");

        final View view = inflater.inflate(R.layout.fragment_crime_create,container,false);

        mImageButtonOk =(ImageButton) view.findViewById(R.id.fragment_crime_create_ok_button);
        mImageButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                view.setVisibility(View.INVISIBLE);
            }
        });

        mImageButtonCancel = (ImageButton) view.findViewById(R.id.fragment_crime_create_cancel_button);
        mImageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                Fragment fragment = new CrimeListFragment();
                fragmentManager.beginTransaction().add(R.id.fragment_container,fragment).commit();
                /*这里如果不设置成不可见，直接使用replace也能达到相同的效果*/
                view.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
//        Log.e("0","0");
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        if(CrimeLab.get(getActivity()).getCrimes().size() > 0){
            FragmentManager fm = getFragmentManager();
            Fragment fragment = new CrimeListFragment();
            /*这里不能用add方法，因为add之后，fragment栈顶部还是CrimeCreateFragment，只是不可见了而已，所以每次都会在栈中新增一个
            * CrimeListFragment，导致CrimeListFragment越来越多，从而多次在ToolBar中增加菜单选项*/
            fm.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            /*如果不在ok按钮的监听方法中将view设置为invisible，就可以采用下面的方法设置，效果相同
            * fm.findFragmentById(R.id.fragment_container).getView().setVisibility(View.INVISIBLE);*/
        }
        super.onResume();
    }
}
