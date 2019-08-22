package com.bluetooth;

import java.lang.Thread;
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
import java.util.logging.Handler;
import java.util.Arrays;

import android.util.Log;

import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;

public class BluetoothService {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private static final String TAG = "WavProcess";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public BluetoothService(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        
        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
        
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        Log.e(TAG, "readData");
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void readData() {
        mmBuffer = new byte[10];
        int numBytes = 0;
        CRC16 crc16 = new CRC16();
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "audioData");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
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
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    int crc = crc16.checkCRC(Arrays.copyOfRange(mmBuffer, 2, numBytes - 4)); 
                    String crcBuffer = bytesToHex(Arrays.copyOfRange(mmBuffer, 2, numBytes - 4));
                    String hexBuffer = bytesToHex(mmBuffer);
                    Log.e("buffer ", hexBuffer);
                    Log.e("crc buffer ", crcBuffer);
                    Log.e("crc ", Integer.toHexString(crc));
                    // out.write(mmBuffer, 0, numBytes);
                    // Send the obtained bytes to the UI activity.
                    // Message readMsg = handler.obtainMessage(
                    //         MessageConstants.MESSAGE_READ, numBytes, -1,
                    //         mmBuffer);
                    // readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }
        catch (FileNotFoundException ex) {
            Log.v(TAG, "file khong ton tai");
        }    
    }

        // // Call this from the main activity to send data to the remote device.
        // public void write(byte[] bytes) {
        //     try {
        //         mmOutStream.write(bytes);

        //         // Share the sent message with the UI activity.
        //         Message writtenMsg = handler.obtainMessage(
        //                 MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
        //         writtenMsg.sendToTarget();
        //     } catch (IOException e) {
        //         Log.e(TAG, "Error occurred when sending data", e);

        //         // Send a failure message back to the activity.
        //         Message writeErrorMsg =
        //                 handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
        //         Bundle bundle = new Bundle();
        //         bundle.putString("toast",
        //                 "Couldn't send data to the other device");
        //         writeErrorMsg.setData(bundle);
        //         handler.sendMessage(writeErrorMsg);
        //     }
        // }

        // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}