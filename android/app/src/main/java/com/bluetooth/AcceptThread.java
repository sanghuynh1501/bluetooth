package com.bluetooth;

import java.lang.Thread;
import java.util.UUID;
import java.io.IOException;

import android.os.ParcelUuid;

import android.util.Log;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;

public class AcceptThread extends Thread {
    private final BluetoothSocket mmSocket;
    private static final String TAG = "WavProcess";

    public AcceptThread(BluetoothAdapter bluetoothAdapter, String address, UUID uuid, Callback errorCallback, Callback successCallback) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothSocket tmp = null;
        try {
            BluetoothDevice mmDevice = bluetoothAdapter.getRemoteDevice(address);
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
            successCallback.invoke();
        } catch (IOException e) {
            Log.v(TAG, "Socket's listen() method failed");
            errorCallback.invoke("Socket's listen() method failed");
        }
        mmSocket = tmp;
    }

    public void run(Callback errorCallback, Callback successCallback) {
        Log.v(TAG, "run run run");
        try {
            mmSocket.connect();
            successCallback.invoke();
            Log.v(TAG, "run run run 1");
        } catch (IOException e) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }
        BluetoothService service = new BluetoothService(mmSocket);
        service.readData();
    }
    
    // Closes the connect socket and causes the thread to finish.
    public void cancel(Callback errorCallback, Callback successCallback) {
        try {
            mmSocket.close();
            successCallback.invoke();
        } catch (IOException e) {
            errorCallback.invoke("Could not close the connect socket");
        }
    }
}