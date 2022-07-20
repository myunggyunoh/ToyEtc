package com.example.toyetc.sms;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyetc.R;
import com.example.toyetc.databinding.ItemSmsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SnsHolder> {

    private List<SmsModel> snsList;

    private int sumMoney = 0;

    private SmsItemClickListener mListener;

    public SmsAdapter() {
        snsList = new ArrayList<>();
    }

    public void addSmsList(List<SmsModel> snsList) {
        sumMoney = 0;//데이터가 바뀌면 (월을 바꾸면) 초기화
        mListener.onClick(sumMoney);
        this.snsList = snsList;
    }

    @Override
    public int getItemCount() {
        return snsList != null ? snsList.size() : 0;
    }

    public void setListener(SmsItemClickListener listener) {
        mListener = listener;
    }

    @NotNull
    @Override
    public SnsHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemSmsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_sms, parent, false);

        return new SnsHolder(binding);
    }

    static class SnsHolder extends RecyclerView.ViewHolder {
        private ItemSmsBinding binding;

        SnsHolder(ItemSmsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onBindViewHolder(final SnsHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.binding.setModel( snsList.get(position) );
        holder.binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sumMoney += snsList.get(position).getMoney();
                } else {
                    sumMoney -= snsList.get(position).getMoney();
                }
                mListener.onClick(sumMoney);
                Log.d("mvvm", "onChecked... " + sumMoney);
            }
        });
    }

}