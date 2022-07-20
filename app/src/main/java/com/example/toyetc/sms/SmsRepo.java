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

//문자 데이터 형식
//_id:11776 thread_id:484 address:15447200 person:null date:1651915384567 date_sent:1651915383000 protocol:0
// read:1 status:-1 type:1 reply_path_present:0 subject:null
// body:[Web발신] 신한카드(5403)승인 노*규 6,900원(일시불)05/07 18:23 로카모빌리티 누적1,329,306원
// service_center:+821020911432 locked:0 error_code:-1 sub_id:1 creator:com.samsung.android.messaging
// seen:1 deletable:0 sim_slot:0 sim_imsi:450050757713274 hidden:0 group_id:null
// group_type:null delivery_date:null app_id:0 msg_id:0 callback_number:null
// reserved:0 pri:0 teleservice_id:0 link_url:null svc_cmd:0 svc_cmd_content:null
// roam_pending:0 spam_report:0 secret_mode:0 safe_message:0 favorite:0 d_rpt_cnt:0
// using_mode:0 from_address:null announcements_subtype:0 announcements_scenario_id:null
// device_name:null correlation_tag:22bd4bc0f5a868e6 object_id:null cmc_prop:null bin_info:0
// re_original_body:null re_body:null re_original_key:null re_recipient_address:null
// re_content_uri:null re_content_type:null re_file_name:null re_type:0 re_count_info:null



public class SmsRepo {
    private final String TAG = getClass().getSimpleName();

    public MutableLiveData<List<SmsModel>> requestSms(Context context, int year, int month) {
        Log.d("mvvm", "requestSms");

        String[] date = getDate(year, month);
        String[] what = new String[]{ "address","date", "body"};
        String where = "address" + "='15447200'" + " AND date>'"+ date[0] +"' " + "AND date<'"+ date[1] +"'";

        List<SmsModel> list = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
                what,
                where,
                null,
                "date ASC");//ASC || DESC
        Log.d("mvvm","cursor size : " + cursor.getCount());

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                SmsModel s = new SmsModel();
                s.setAddress(cursor.getString(0));
                s.setDate(getDateFormat(cursor.getString(1)));
                String strMoney = getMoney(cursor.getString(2));
                s.setBody(strMoney);
                s.setMoney(getMoneyToInt(strMoney));
                s.setProduct(getProduct(cursor.getString(2)));
                list.add(s);
            } while (cursor.moveToNext());
        } else {
            Log.d("TTTAG","empty box, no SMS");
        }


        final MutableLiveData<List<SmsModel>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(list);

        return mutableLiveData;
    }

    private int getMoneyToInt(String money) {
        Log.d("mvvm","money : " + money);
        String strMoney = "";
        for (int i=0; i<money.length(); i++) {
            Log.d("mvvm","i : " + money.charAt(i));
            if (money.charAt(i) >= 48 && money.charAt(i) <= 57) {
                strMoney += money.charAt(i);
            }
        }
        Log.d("mvvm","strMoney : " + strMoney);
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

        Log.d("mvvm","start : " + start);

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
        Log.d("mvvm","start date : " +  sdf1.format(calendar.getTime()));
        Log.d("mvvm","end date : " +  sdf1.format(calendar2.getTime()));

        return new String[]{start + "", end + ""};
    }


    private String getProduct(String body) {
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

        int start = body.indexOf(":");
        int end = body.indexOf("누적");
        for (int i = start + 3; i < end; i++) {
            product += body.charAt(i);
        }

        return product;
    }

    private String getDateFormat(String date) {
        @SuppressLint("SimpleDateFormat")
        DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        return sdf1.format(calendar.getTime());
    }

    private String getMoney(String body) {
        //신한카드(2*3) 승인 노*규 4,500원(일시불)07/03 09:21 GS25 인덕원  누적557,000원
        //[Web발신] [신한체크승인] 노*규(2739) 07/08 13:07 (금액)10,000원 이니시스-정기과금
        //[Web발신] [신한체크취소] 노*규(2739) 07/08 13:37 (금액)10,000원 이니시스-정기과금
        //[Web발신] [신한체크거절] 노*규(2739) 07/08 10:30 10,000원 예금잔액부족
//b발신] 신한체크해외승인 노*규(2739) 03/03 13:26 SAR 5.00         (SA)WORLD TRAV(승인)
        String money = "";

        int start = body.indexOf("노*규");
        for (int i=start+3; i<body.length(); i++) {
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
    public void requestPermission(Context context) {

        String[] permissions = {
            Manifest.permission.READ_SMS,
        };

        ActivityCompat.requestPermissions((Activity)context, permissions, 2001);

//        if(ActivityCompat.checkSelfPermission((Activity)context,android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("mvvm","권한없음");
////            permissionRetry(context);
//        }
    }


    public void retryPermission(Context context) {
        AlertDialog.Builder
        localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle("권한 설정")
                .setMessage("권한 거절로 인해 일부기능이 제한됩니다.")
                .setPositiveButton("권한 설정하러 가기", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:" + context.getPackageName()));
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            context.startActivity(intent);
                        }
                    }})
                .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        Toast.makeText(context,"권한없음!",Toast.LENGTH_SHORT).show();
                    }})
                .create()
                .show();
    }


}
