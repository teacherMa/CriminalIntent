package com.example.xiaomage.criminalintent;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by xiaomage on 2016/12/8.
 */

public class Crime {

    private UUID mId;
    private Date mDate;
    private String mTitle;
    private String mSuspect;
    private String mFormatDate;
    private String mFormatDay;
    private String mFormatTime;
    private Boolean mSolved;

    public static final String DATE_FORMAT = "yyyy-MM-dd  HH:mm";
    public static final String DAY_FORMAT = "yyyy-MM-dd ";
    public static final String TIME_FORMAT = "HH:mm";

    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mId = id;
        mDate = new Date();
        mSolved = false;
        mSuspect = null;
        mFormatDate = makeFormatDate(mDate);
        mFormatDay = makeFormatDay(mDate);
        mFormatTime = makeFormatTime(mDate);
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public Boolean isSolved(){
        return mSolved;
    }

    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if(year ==2 || month ==0 || day==0){
            /*如果传入的date只包含时分秒，则只更新时分秒*/
            Calendar innerCalendar = Calendar.getInstance();
            innerCalendar.setTime(mDate);
            int innerYear = innerCalendar.get(Calendar.YEAR);
            int innerMonth = innerCalendar.get(Calendar.MONTH);
            int innerDay = innerCalendar.get(Calendar.DAY_OF_MONTH);
            mDate = new GregorianCalendar(innerYear,innerMonth,innerDay,hour,minute).getTime();
        }else{
            if(hour==0 || minute==0){
                /*如果传入的date只包含年月日，则只更新年月日*/
                Calendar innerCalendar = Calendar.getInstance();
                innerCalendar.setTime(mDate);
                int innerHour = innerCalendar.get(Calendar.HOUR_OF_DAY);
                int innerMinute = innerCalendar.get(Calendar.MINUTE);
                mDate = new GregorianCalendar(year,month,day,innerHour,innerMinute).getTime();
            }else{
                mDate = date;
            }
        }
        mFormatDate = makeFormatDate(mDate);
        mFormatDay = makeFormatDay(mDate);
        mFormatTime = makeFormatTime(mDate);
    }

    public void setSolved(Boolean solved) {
        mSolved = solved;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getFormatDate(){
        return mFormatDate;
    }

    public String getFormatDay(){
        return mFormatDay;
    }

    public String getFormatTime(){
        return  mFormatTime;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    private String makeFormatDate(Date date){
        return DateFormat.format(DATE_FORMAT,date).toString();
    }

    private String makeFormatDay(Date date){
        return DateFormat.format(DAY_FORMAT,date).toString();
    }

    private String makeFormatTime(Date date){
        return DateFormat.format(TIME_FORMAT,date).toString();
    }
}
