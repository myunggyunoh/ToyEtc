package com.example.toyetc.sms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SmsViewModel extends ViewModel {

    private SmsRepo smsRepo;
    private MutableLiveData<List<SmsModel>> mutableLiveData;
//    private MutableLiveData<Integer> checkedSum;

    public SmsViewModel(){
        smsRepo = new SmsRepo();
    }

    public LiveData<List<SmsModel>> getSms(Context context, int year, int month,
                                           String callNumber, String moneyStandard,
                                           String productStandardStart, String productStandardEnd) {
        mutableLiveData = smsRepo.requestSms(context, year, month, callNumber, moneyStandard, productStandardStart, productStandardEnd);
        return mutableLiveData;
    }

    public void requestPermission(Context context) {
        String[] permissions = {
                Manifest.permission.READ_SMS,
        };

        ActivityCompat.requestPermissions((Activity)context, permissions, 2001);
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
