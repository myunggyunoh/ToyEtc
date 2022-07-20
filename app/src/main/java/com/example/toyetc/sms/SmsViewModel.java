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

    public LiveData<List<SmsModel>> getSms(Context context, int year, int month) {
//        if(mutableLiveData==null){
            mutableLiveData = smsRepo.requestSms(context, year, month);
//        }
        return mutableLiveData;
    }

    public void getPermission(Context context) {
        smsRepo.requestPermission(context);
    }

    public void retryPermission(Context context){
        smsRepo.retryPermission(context);
    }
}
