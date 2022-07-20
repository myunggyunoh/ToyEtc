package com.example.toyetc.sms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.toyetc.R;
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

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SmsAdapter();
        binding.recyclerView.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();

        binding.editYear.setText(calendar.get(Calendar.YEAR)+"");
        binding.editMonth.setText((calendar.get(Calendar.MONTH)+1)+"");

        smsViewModel = new SmsViewModel();


        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            smsViewModel.getPermission(this);
        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSms();
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

        smsViewModel.getSms(this, year, month).observe(this, new Observer<List<SmsModel>>() {
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