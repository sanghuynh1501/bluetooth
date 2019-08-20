package com.bluetooth;

import android.util.Log;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import java.util.UUID;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.bluetooth.*;

public class WavProcess extends ReactContextBaseJavaModule {
    
    private BluetoothServerSocket mmServerSocket;
    private BluetoothAdapter mAdapter;
    private BluetoothDevice remoteDevice;

    private static final String TAG = "WavProcess";
    private ReactApplicationContext reactContext;
    private static UUID MY_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");

    public WavProcess(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "Wav";
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @ReactMethod
    public void writeWav(String audioData) {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "audioData");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        Log.i(TAG, audioData);
        String [] audioDataSet = audioData.split(",");
        File dstFile = new File(folder.getAbsolutePath(), "/dst.wav");
        if (!dstFile.exists()) {
            try {
                dstFile.createNewFile();
            } catch(IOException ex) {
                Log.v(TAG, "khong tao duoc file");
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(dstFile);
            try {
                for(int i = 0; i < audioDataSet.length; i++) {
                    byte[] b = hexStringToByteArray(audioDataSet[i]);
                    out.write(b, 0, b.length);
                }
                out.close();
            } catch(IOException ex) {
                Log.v(TAG, "file loi");
            }
        }
        catch (FileNotFoundException ex) {
            Log.v(TAG, "file khong ton tai");
        }
    }
}