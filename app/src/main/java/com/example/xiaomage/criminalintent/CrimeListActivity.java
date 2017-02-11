package com.example.xiaomage.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by xiaomage on 2016/12/9.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks ,CrimeFragment.Callbacks{

    private static final int REQUEST_CRIME = 1;

//    private static final String TAG = "CrimeListActivity";/*用于日志打印*/

    /*Inherited from SingleFragmentActivity*/
    @Override
    protected Fragment createCrimeListFragment(){
        return new CrimeListFragment();
    }

    /*Inherited from SingleFragmentActivity*/
    @Override
    protected Fragment createCrimeCreateFragment() {
        return new CrimeCreateFragment();
    }

    /*Inherited from SingleFragmentActivity*/
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    /*Inherited from CrimeListFragment.Callbacks*/
    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this,crime.getId());
            startActivityForResult(intent,REQUEST_CRIME);
        }else{
            int mPosition = 0;
            for(;mPosition < CrimeLab.MAX_CRIMEA_MOUNT;mPosition++){
                if(CrimeLab.get(this).getCrimes().get(mPosition).getId().equals(crime.getId())){
                    break;
                }
            }
            Fragment fragment = CrimeFragment.newInstance(crime.getId(),mPosition);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,fragment).commit();
        }
    }

    /*Inherited from CrimeFragment.Callbacks*/
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
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
    public void onDestroy(){
        super.onDestroy();
//        Log.e(TAG,"onDestroy() called");
    }

    /** 这里使用书上的方法，书上并不需要处理返回值
     * 但在这里需要，所以在CrimeListActivity中调用
     * onActivityResult()*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CRIME){
            if(data ==null){
                return;
            }else {
                if(getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container) == null){
                    ((CrimeListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).
                            setDeleteCrime(CrimePagerActivity.getDelete(data));
                }
            }
        }
    }
}
