package com.example.xiaomage.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by xiaomage on 2016/12/14.
 */

public class TimePickerFragment extends DialogFragment {

    public static final String ARG_DATE = "date";
    public static final String EXTRA_TIME = "com.example.xiaomage.criminalintent.time";

    private TimePicker mTimePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        Date date =(Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);

        mTimePicker = (TimePicker)v.findViewById(R.id.dialog_time_time_picker);
        if(Build.VERSION.SDK_INT<23){
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }else{
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }
        mTimePicker.setIs24HourView(true);
        mTimePicker.setEnabled(true);

        return new AlertDialog.Builder(getActivity()).
                setView(v).
                setTitle("选择违规时间").
                setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface,int which){
                            int hour,minute;
                            if(Build.VERSION.SDK_INT<23){
                                hour = mTimePicker.getCurrentHour();
                                minute = mTimePicker.getCurrentMinute();
                            }
                            else{
                                hour = mTimePicker.getHour();
                                minute = mTimePicker.getMinute();
                            }
                            Date date =new GregorianCalendar(0,0,0,hour,minute).getTime();
                            sendResult(Activity.RESULT_OK,date);
                        }}).
                create();
    }

    public static TimePickerFragment newIntent(Date date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE,date);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return  timePickerFragment;
    }

    private void sendResult(int resultCode,Date date){
        if(getTargetFragment() ==null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
