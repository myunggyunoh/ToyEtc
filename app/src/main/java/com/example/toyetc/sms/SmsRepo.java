package com.example.toyetc.sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.toyetc.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SmsRepo {
    private final String TAG = getClass().getSimpleName();

    public MutableLiveData<List<SmsModel>> requestSms(Context context, int year, int month,
                                                      String callNumber, String moneyStandard,
                                                      String productStandardStart, String productStandardEnd) {
        Log.d("myunggyu", "requestSms");

        String[] date = getDate(year, month);
        String[] what = new String[]{ "address","date", "body"};
        String where = "address=" + callNumber + " AND date>"+ date[0] +" AND date<"+ date[1];

        List<SmsModel> list = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
                what,
                where,
                null,
                "date ASC");//ASC || DESC
        Log.d("myunggyu","cursor size : " + cursor.getCount());

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                SmsModel s = new SmsModel();
                s.setAddress(cursor.getString(0));
                s.setDate(getDateFormat(cursor.getString(1)));

                String body = cursor.getString(2);
                s.setBody(body);
                String strMoney = getMoneyToString(body, moneyStandard);
                s.setMoneyStr(strMoney);
                s.setMoney(getMoneyToInt(strMoney));
                s.setProduct(getProduct(body, productStandardStart, productStandardEnd));
                list.add(s);
            } while (cursor.moveToNext());
        } else {
            Log.d("myunggyu","empty box, no SMS");
        }

        final MutableLiveData<List<SmsModel>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(list);

        return mutableLiveData;
    }

    private int getMoneyToInt(String money) {
        String strMoney = "";
        for (int i=0; i<money.length(); i++) {
            if (money.charAt(i) >= 48 && money.charAt(i) <= 57) {
                strMoney += money.charAt(i);
            }
        }
        Log.d("myunggyu","strMoney : " + strMoney);
        if (strMoney.length() == 0) {
            strMoney = "0";
        }
        return Integer.parseInt(strMoney);
    }

    private String[] getDate(int year, int month) {
        int plusMonth = month + 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        long start = calendar.getTimeInMillis();

        Log.d("myunggyu","start : " + start);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, year);
        calendar2.set(Calendar.MONTH, plusMonth-1);
        calendar2.set(Calendar.DAY_OF_MONTH, 1);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);

        long end = calendar2.getTimeInMillis();

        //로그를 위해..
        @SuppressLint("SimpleDateFormat")
        DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("myunggyu","start date : " +  sdf1.format(calendar.getTime()));
        Log.d("myunggyu","end date : " +  sdf1.format(calendar2.getTime()));

        return new String[]{start + "", end + ""};
    }


    private String getProduct(String body, String start, String end) {
        String product = "";

        if (body.indexOf("신한체크거절") > 0) {
            return "거절";
        } else if(body.indexOf("신한체크취소") > 0) {
            return "취소";
        } else if (body.indexOf("신한체크해외") > 0){
            return "해외";
        } else {
//            money = money + "(승인)";
        }

        int startIdx = body.indexOf(start); //":"
        int endIdx = body.indexOf(end); //"누적"
        if (start.equals(":")) {
            startIdx = startIdx + 2;
        }
        for (int i = startIdx + start.length(); i < endIdx; i++) {
            product += body.charAt(i);
        }

        return product;
    }

    private String getDateFormat(String date) {
        @SuppressLint("SimpleDateFormat")
        DateFormat sdf1 = new SimpleDateFormat("MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        return sdf1.format(calendar.getTime());
    }

    private String getMoneyToString(String body, String standard) {
        String money = "";
        int start = body.indexOf(standard);
        for (int i = start + standard.length(); i<body.length(); i++) {
            if (String.valueOf(body.charAt(i)).equals("(")) {
                money = "체크카드";
                break;
            } else if (String.valueOf(body.charAt(i)).equals(" ")) {
                continue;
            } else {
                money = money + body.charAt(i);
            }

            if (String.valueOf(body.charAt(i)).equals("원")) {
                break;
            }
        }

        //위엔 신용카드
        if (money.equals("체크카드")) {
            money = "";
            int start2 = body.indexOf("(금액)");
            for (int i=start2+4; i<body.length(); i++) {
                money = money + body.charAt(i);
                if (String.valueOf(body.charAt(i)).equals("원")) {
                    break;
                }
            }
        }

        if (body.indexOf("신한체크거절") > 0) {
            return "거절";
        } else if (body.indexOf("신한체크취소") > 0) {
            money = money + "(취소)";
        } else if (body.indexOf("신한체크해외") > 0){
            return "해외";
        } else {
            money = money + "(승인)";
        }

        return money;
    }

}
