package com.example.xiaomage.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by xiaomage on 2016/12/12.
 */

public class CrimePagerActivity extends AppCompatActivity implements CrimeDeleteFragment.SetDelete,CrimeFragment.Callbacks{
    private static final String EXTRA_CRIME_ID = "com.example.xiaomage.criminalintent.crime_id";
    private static final String TAG = "CrimePagerActivity";
    private static final String EXTRA_CRIME_DELETE = "crime delete";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private boolean mIsDelete;

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    public void setDelete(boolean isDelete) {
        this.mIsDelete = isDelete;
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        /*空实现*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_crime_pager_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId(),position);
            }
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        for(int i = 0;i<mCrimes.size();i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        /*一定要屏蔽掉super.onBackPressed()!!!*/
        Intent intent = new Intent();
        if(this.mIsDelete){
            intent.putExtra(EXTRA_CRIME_DELETE,mIsDelete);
            CrimeLab.get(this).removeCrime(mViewPager.getCurrentItem());
        }else{
            intent.putExtra(EXTRA_CRIME_DELETE,false);
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onStart(){
        super.onStart();
//        Log.d(TAG,"onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
//        Log.d(TAG,"onResume() called");
    }

    @Override
    public void onStop(){
        super.onStop();
//        Log.d(TAG,"onStop() called");
    }

    @Override
    public void onPause(){
        super.onPause();
//        Log.d(TAG,"onPause() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy() called");
    }

    public static boolean getDelete(Intent intent){
        return intent.getBooleanExtra(EXTRA_CRIME_DELETE,true);
    }

}
