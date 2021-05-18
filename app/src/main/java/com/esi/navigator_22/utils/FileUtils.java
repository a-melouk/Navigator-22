package com.esi.navigator_22.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    @SuppressLint("StaticFieldLeak")
    public static Activity baseActivity;
    public static String TARGET_BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/";

    public static void copyMapFilesToSdCard(Activity baseActivity, FileTransferListener listener) {
        FileUtils.baseActivity = baseActivity;
        if (copyMapFilesToSdCard("Sba.sqlite")) {
            listener.onLoadSuccess();
            Log.d("Offline", "success");
        } else {
            listener.onLoadFailed();
            Log.d("Offline", "fail");
        }
    }

    public interface FileTransferListener {
        void onLoadFailed();

        void onLoadSuccess();
    }

    public static boolean copyMapFilesToSdCard(String path) {
        AssetManager assetManager = baseActivity.getAssets();
        String[] assets;
        try {
            assets = assetManager.list(path);
            if (assets != null) {
                if (assets.length == 0) {
                    copyFile(path);
                    Log.d("Offline3", path);
                } else {
                    String fullPath = TARGET_BASE_PATH + path;
                    File dir = new File(fullPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    for (String asset : assets) {
                        String p;
                        if (path.equals(""))
                            p = "";
                        else
                            p = path + "/";
                        copyMapFilesToSdCard(p + asset);
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean isMapFileExists() {
        File file = new File(TARGET_BASE_PATH + "Sba.sqlite");
        Log.d("Offline1", String.valueOf(file.exists()));
        return file.exists();
    }

    public static boolean isMapPathExists() {
        File file = new File(TARGET_BASE_PATH);
        Log.d("Offline2", String.valueOf(file.exists()));
        return file.exists();
    }

    public static void createMapPath() {
        File makeMapPath = new File(TARGET_BASE_PATH);
        makeMapPath.mkdirs();
    }

    public static void copyFile(String filename) throws Exception {
        AssetManager assetManager = baseActivity.getAssets();
        InputStream in;
        OutputStream out;
        String newFileName;
        in = assetManager.open(filename);
        if (!isMapPathExists()) {
            createMapPath();
        }
        newFileName = TARGET_BASE_PATH + filename;
        out = new FileOutputStream(newFileName);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
    }
}
