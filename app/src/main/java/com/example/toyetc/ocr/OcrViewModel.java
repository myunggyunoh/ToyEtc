package com.example.toyetc.ocr;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.lifecycle.ViewModel;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OcrViewModel extends ViewModel {
    private OcrRepo ocrRepo;
    private TessBaseAPI tessBaseAPI; //Tess API reference

    public OcrViewModel(Context context){
        init(context);
        ocrRepo = new OcrRepo();
    }

    public TessBaseAPI getTessBaseAPI() {
        return tessBaseAPI;
    }

    private void init(Context context) {
        tessBaseAPI = new TessBaseAPI();
        String dir = context.getFilesDir() + "/tesseract";
        if(checkLanguageFile(context, dir+"/tessdata")){
            tessBaseAPI.init(dir, "eng");
        }
    }

    boolean checkLanguageFile(Context context, String dir)
    {
        File file = new File(dir);
        if(!file.exists() && file.mkdirs())
            createFiles(context, dir);
        else if(file.exists()){
            String filePath = dir + "/eng.traineddata";
            File langDataFile = new File(filePath);
            if(!langDataFile.exists())
                createFiles(context, dir);
        }
        return true;
    }
    private void createFiles(Context context, String dir)
    {
        AssetManager assetMgr = context.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetMgr.open("eng.traineddata");

            String destFile = dir + "/eng.traineddata";

            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
