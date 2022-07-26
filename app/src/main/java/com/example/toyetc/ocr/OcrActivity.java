package com.example.toyetc.ocr;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.toyetc.R;
import com.example.toyetc.databinding.ActivityOcrBinding;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OcrActivity extends AppCompatActivity {

    ActivityOcrBinding binding;
    OcrViewModel ocrViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ocr);

        ocrViewModel = new OcrViewModel(this);

        TessBaseAPI tessBaseAPI = ocrViewModel.getTessBaseAPI();

        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
            }
        });

        // ocr
        binding.btnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.imgOcr.getDrawable();
                if (binding.imgOcr.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(),"이미지를 선택해", Toast.LENGTH_LONG).show();
                    return;
                }

                String OCRresult = null;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                tessBaseAPI.setImage(bitmap);
                OCRresult = tessBaseAPI.getUTF8Text();
                binding.textOcr.setText(OCRresult);
            }
        });

        binding.btnTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"미구현...", Toast.LENGTH_LONG).show();
            }
        });

    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    Uri uri = intent.getData();
                    Glide.with(OcrActivity.this)
                            .load(uri)
                            .into(binding.imgOcr);
                }
            }
        });

}