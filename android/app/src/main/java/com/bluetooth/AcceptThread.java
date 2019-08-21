package com.bluetooth;

import java.lang.Thread;
import java.util.UUID;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import com.facebook.react.bridge.Promise;
import android.bluetooth.BluetoothAdapter;
import com.facebook.react.bridge.Callback;
import android.bluetooth.BluetoothServerSocket;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread(BluetoothAdapter bluetoothAdapter, String name, UUID uuid, Callback errorCallback, Callback successCallback) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
            successCallback.invoke();
        } catch (IOException e) {
            errorCallback.invoke("Socket's listen() method failed");
        }
        mmServerSocket = tmp;
    }

    public void run(Callback errorCallback, Callback successCallback) {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
                successCallback.invoke();
            } catch (IOException e) {
                errorCallback.invoke("Socket's accept() method failed");
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket);
                mmServerSocket.close();
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel(Callback errorCallback, Callback successCallback) {
        try {
            promise.resolve();
            mmServerSocket.close();
            successCallback.invoke();
        } catch (IOException e) {
            errorCallback.invoke("Could not close the connect socket");
        }
    }
}