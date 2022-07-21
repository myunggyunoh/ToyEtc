package com.example.toyetc.sms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.toyetc.R;
import com.example.toyetc.common.PreferenceUtil;
import com.example.toyetc.databinding.ActivitySmsBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmsActivity extends AppCompatActivity {
    final String TAG = getClass().getSimpleName();

    ActivitySmsBinding binding;
    SmsAdapter adapter;
    SmsViewModel smsViewModel;
    SmsItemClickListener smsItemClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sms);

        init();

        smsViewModel = new SmsViewModel();

        //권한
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            smsViewModel.requestPermission(this);
        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSms();
            }
        });

        binding.buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String copyText = adapter.getCopyText();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", copyText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SmsActivity.this,"복사됐어",Toast.LENGTH_LONG).show();
            }
        });

        smsItemClickListener = new SmsItemClickListener() {
            @Override
            public void onClick(int sum) {
                binding.tvSum.setText(sum+"");
            }
        };
        adapter.setListener(smsItemClickListener);

    }

    private void init() {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SmsAdapter();
        binding.recyclerView.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();

        binding.editYear.setText(calendar.get(Calendar.YEAR)+"");
        binding.editMonth.setText((calendar.get(Calendar.MONTH)+1)+"");

        binding.editCallNumber.setText(PreferenceUtil.getInstance(this).getCallNumber());
        binding.editMoneyStandard.setText(PreferenceUtil.getInstance(this).getMoneyStandard());
        binding.editProductStandardStart.setText(PreferenceUtil.getInstance(this).getProductStandardStart());
        binding.editProductStandardEnd.setText(PreferenceUtil.getInstance(this).getProductStandardEnd());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2001) {
            if (grantResults[0] != 0) {
                Log.d(" myunggyu", "권한 안승인");
                smsViewModel.retryPermission(this);
            } else {
                Log.d("myunggyu","sms데이터 재요청");
                getSms();
            }
        }
    }

    private void getSms() {
        int year = Integer.parseInt(binding.editYear.getText().toString());
        int month = Integer.parseInt(binding.editMonth.getText().toString());
        String callNumber = binding.editCallNumber.getText().toString();
        String moneyStandard = binding.editMoneyStandard.getText().toString();
        String productStandardStart = binding.editProductStandardStart.getText().toString();
        String productStandardEnd = binding.editProductStandardEnd.getText().toString();

        PreferenceUtil.getInstance(this).setCallNumber(callNumber);
        PreferenceUtil.getInstance(this).setmoneyStandard(moneyStandard);
        PreferenceUtil.getInstance(this).setProductStandardStart(productStandardStart);
        PreferenceUtil.getInstance(this).setProductStandardEnd(productStandardEnd);


        smsViewModel.getSms(this, year, month, callNumber, moneyStandard, productStandardStart, productStandardEnd)
                .observe(this, new Observer<List<SmsModel>>() {
            @Override
            public void onChanged(List<SmsModel> smsModels) {
                if (smsModels != null && !smsModels.isEmpty()) {
                    Log.e("myunggyu", "observe onChanged()=" + smsModels.size());
                    adapter.addSmsList(smsModels);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}