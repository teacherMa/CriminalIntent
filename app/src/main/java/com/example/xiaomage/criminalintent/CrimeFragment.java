package com.example.xiaomage.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by xiaomage on 2016/12/8.
 */

public class CrimeFragment extends Fragment {

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_SHOW_PHOTO = 4;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POSITION = "crime_position";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_DELETE = "DialogDelete";
    private static final String DIALOG_SHOW_PICTURE = "DialogShowPicture";
    private static final String STORE_DATE = "date";
    private static final String STORE_TIME = "time";

    private Crime mCrime;
    private CrimeFragment.Callbacks mCallbacks;
    private Date mDate;//储存年月日
    private Date mTime;//储存时分
    private int mPosition;//储存本fragment在ViewPager中的位置
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSuspectButton;
    private Button mReportButton;
    private Button mCallingButton;
    private ImageButton mPhotoButton;
    private CheckBox mSolvedCheckBox;
    private Toolbar mToolbar;
    private ImageView mPhotoView;

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
            mPosition = getArguments().getInt(ARG_CRIME_POSITION);
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
            mDate = mCrime.getDate();
            mTime = mCrime.getDate();
            mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        }

        if (savedInstanceState != null) {
            mDate = (Date) savedInstanceState.getSerializable(STORE_DATE);
            mTime = (Date) savedInstanceState.getSerializable(STORE_TIME);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime,
                container, false);


        mToolbar = (Toolbar)v.findViewById(R.id.fragment_crime_toolbar);
        mToolbar.inflateMenu(R.menu.fragment_crime);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });
        /*如果不把Toolbar设置为Actionbar,onCreateOptionsMenu()
        和onOptionsItemSelected()方法会无效，失效的同时可以采用
        下面这种方法监听菜单。而且此处实例化ToolBar时若采用Crime
        -ListFragment中实例化ActionBar的方法会导致菜单栏显示不正
        常*/
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete_crime:
                        DialogFragment dialogFragment = new CrimeDeleteFragment();
                        dialogFragment.show(getActivity().getSupportFragmentManager(),DIALOG_DELETE);
                        return true;
                    default:
                        return true;
                }
            }
        });


        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto =
                mPhotoFile != null && captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
                updateCrime();
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.crime_photo);
        if(mPhotoView.getDrawable() == null){
            mPhotoView.setClickable(false);
        }else{
            mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*问题代码
                * 下面两行无法启动Activity，且不报错*/
//                Intent intent = ShowCrimePhotoActivity.newIntent(getActivity(),mPhotoFile.toString());
//                getActivity().startActivity(intent);

                /*使用下面的方法，ShowCrimePhotoFragment不会显示，原因未知
                *猜测是CrimePagerActivity中已经托管了ViewPager有关 */
//                FragmentManager manager = getFragmentManager();
//                ShowCrimePhotoFragment fragment = ShowCrimePhotoFragment.getNewInstance(mPhotoFile.toString());
//                manager.beginTransaction().add(R.id.fragment_crime_pager,fragment).commit();
//                manager.beginTransaction().hide(CrimeFragment.this);

                /*Dialog实现*/
                    FragmentManager manager = getFragmentManager();
                    ShowCrimePhotoFragment fragment = ShowCrimePhotoFragment.getNewInstance(mPhotoFile.toString());
                    fragment.show(manager,DIALOG_SHOW_PICTURE);
                    fragment.setTargetFragment(CrimeFragment.this,REQUEST_SHOW_PHOTO);
                }
            });
        }

        updatePhotoView();

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(mCrime.getTitle()))
                    /*这里这样写，是因为在ViewPager中，左右滑动会造成fragment的视图不断的销毁、创建
                    * 每次创建时，都会调用setText方法，从而onTextChanged方法也会被调用，如果不加判断，
                    * 就会直接将数组相应位置的元素设置为true，虽然这种情况下不会造成程序的结果错误，但是
                    * 是一个bug*/
                    ;
                else {
                    mCrime.setTitle(s.toString());
                    CrimeLab.setModifyArray(mPosition);
                    updateCrime();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getFormatDay());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newIntent(mCrime.getDate());
                dialog.show(manager, DIALOG_DATE);
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                CrimeLab.setModifyArray(mPosition);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mTimeButton.setText(mCrime.getFormatTime());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newIntent(mCrime.getDate());
                dialog.show(manager, DIALOG_TIME);
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                CrimeLab.setModifyArray(mPosition);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                CrimeLab.setModifyArray(mPosition);
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*创建intent的两种写法之一*/
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .createChooserIntent();
                startActivity(intent);
                /*创建intent的两种写法之二
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i.createChooser(i, getString(R.string.send_report));
                startActivity(i);*/
            }
        });

        mCallingButton = (Button) v.findViewById(R.id.crime_call);
        mCallingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                String[] queryFields = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                /*queryFields为返回的想查询的内容*/

                String selectClause = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
                /*这里要使用DISPLAY_NAME,而不是使用_ID*/

                /*或者可以写成下面这样
                * String selectClause = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = mCrime.getSuspect()";
                * query中相应的selectParams则为空
                * */

                String [] selectParams ={ mCrime.getSuspect()};

                Cursor cursor = getActivity().getContentResolver().query(
                        uri,
                        queryFields,
                        selectClause,
                        selectParams,
                        null);

                if(cursor!= null && cursor.getCount()!= 0 ){
                    try{
                        cursor.moveToFirst();
                        String number = cursor.getString(0);

                        /*血案发生地点*/
                        Uri numberUri = Uri.parse("tel:" + number);//这里一定要要加 "tel:"
                        /*血案发生地点*/

                        Intent intent = new Intent(Intent.ACTION_DIAL, numberUri);
                        startActivity(intent);
                    }finally {
                        cursor.close();
                    }
                }
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        /*为了确定设备上是否安装了联系人应用*/
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                packageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(STORE_DATE, mDate);
        savedInstanceState.putSerializable(STORE_TIME, mTime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            mDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(mDate);
            mDateButton.setText(mCrime.getFormatDay());
            updateCrime();
        }
        if (requestCode == REQUEST_TIME) {
            mTime = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(mTime);
            mTimeButton.setText(mCrime.getFormatTime());
            updateCrime();
        }
        if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String [] queryField = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryField,null,null,null);
            try{
                if(c.getCount() == 0){
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                updateCrime();
            }finally {
                c.close();
            }
        }
        if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
            updateCrime();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (CrimeFragment.Callbacks)context;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.e("onStart"+mCrime.getTitle(),"000");
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e("onResume"+mCrime.getTitle(),"000");
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
//        Log.e("onPause"+mCrime.getTitle(),"000");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.e("onStop"+mCrime.getTitle(),"000");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.e("onDestroy"+mCrime.getTitle(),"000");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId, int position) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        args.putInt(ARG_CRIME_POSITION, position);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public String getCrimeReport(){

        String solvedString;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_solved_label);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = mCrime.getFormatDay() + mCrime.getFormatTime();

        String suspectString = mCrime.getSuspect();
        if(suspectString == null){
            suspectString = getString(R.string.crime_report_no_suspect);
        }else {
            suspectString = getString(R.string.crime_report_no_suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspectString);

        return report;
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitMap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    public int getPosition(){
        return mPosition;
    }

    public void updateCrime(){
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

}
