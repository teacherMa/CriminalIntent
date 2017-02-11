package com.example.xiaomage.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xiaomage on 2016/12/9.
 */

public class CrimeListFragment extends Fragment implements CrimeDeleteFragment.SetDelete{

//    private static final int REQUEST_CRIME = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String SAVED_CRIME_DELETE = "crime delete";

    private RecyclerView mCrimeRecyclerView;
    private Toolbar mToolbar;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible = false;
    private boolean mIsDeleteCrime = false;
    private boolean mIsAddCrime = false;
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView)itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                }
            });
        }

        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getFormatDate());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View view){
            CrimeLab.Initialize();
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder,int position){
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount(){
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
    }

    @Override
    public void setDelete(boolean isDelete) {
        mIsDeleteCrime = isDelete;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
            mIsDeleteCrime = savedInstanceState.getBoolean(SAVED_CRIME_DELETE);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mToolbar = (Toolbar)view.findViewById(R.id.fragment_crime_list_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mCrimeRecyclerView = (RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
        savedInstanceState.putBoolean(SAVED_CRIME_DELETE,mIsDeleteCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_it_show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                mCallbacks.onCrimeSelected(crime);
                mIsAddCrime = true;
                updateUI();
                return true;
            case R.id.menu_it_show_subtitle:
                mSubtitleVisible =!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return true;
        }
    }

    public void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setCrimes(crimes);
            /*更新全屏幕的几种情况：有删减，有新增*/
            if(this.isDeleteCrime()){
                mAdapter.notifyDataSetChanged();
            }else {
                if(mIsAddCrime){
                    mAdapter.notifyDataSetChanged();
                }
            }
            for(int i =0;i<CrimeLab.get(getActivity()).getCrimes().size();i++){
                boolean [] a = CrimeLab.getModifyArray();
                if(a[i] == true){
                    mAdapter.notifyItemChanged(i);
                }
            }
        }
        updateSubtitle();
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format,Integer.toString(crimeCount));

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    public boolean isDeleteCrime() {
        return mIsDeleteCrime;
    }

    public void setDeleteCrime(boolean deleteCrime) {
        mIsDeleteCrime = deleteCrime;
    }

}
