package com.example.xiaomage.criminalintent;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by xiaomage on 2016/12/9.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createCrimeListFragment();
    protected abstract Fragment createCrimeCreateFragment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment= fm.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            if(CrimeLab.get(this).getCrimes().size() == 0){
                fragment = createCrimeCreateFragment();
            }else{
                fragment = createCrimeListFragment();
            }
        }
        fm.beginTransaction().add(R.id.fragment_container, fragment)
                .commit();
    }
}
