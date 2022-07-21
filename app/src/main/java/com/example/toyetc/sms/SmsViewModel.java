package com.example.toyetc.sms;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SmsViewModel extends ViewModel {

    private SmsRepo smsRepo;
    private MutableLiveData<List<SmsModel>> mutableLiveData;
    private MutableLiveData<Integer> liveSum;

    public SmsViewModel(){
        smsRepo = new SmsRepo();
    }

    public LiveData<List<SmsModel>> getSms(Context context, int year, int month,
                                           String callNumber, String moneyStandard,
                                           String productStandardStart, String productStandardEnd) {
//        if(mutableLiveData==null){
            mutableLiveData = smsRepo.requestSms(context, year, month, callNumber, moneyStandard, productStandardStart, productStandardEnd);
//        }
        return mutableLiveData;
    }
//    Context context, int year, int month, String callNumber, String moneyStandard, String productStandardStart, String productStandardEnd
    public void requestPermission(Context context) {
        smsRepo.requestPermission(context);
    }

    public void retryPermission(Context context){
        smsRepo.retryPermission(context);
    }
}
