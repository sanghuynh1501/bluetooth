package com.bluetooth;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothAdapter;

import java.util.Set;
import java.util.UUID;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import android.bluetooth.*;

public class ReactBluetoothAdapter extends ReactContextBaseJavaModule {
    
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    AcceptThread bluetoothService;
    // private final BroadcastReceiver receiver = new BroadcastReceiver() {
    //     public void onReceive(Context context, Intent intent) {
    //         String action = intent.getAction();
    //         if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    //             BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    //             String deviceName = device.getName();
    //             String deviceHardwareAddress = device.getAddress(); // MAC address
    //         }
    //     }
    // };

    public ReactBluetoothAdapter(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "BluetoothAdapter";
    }

    @ReactMethod
    public void getBluetoothAdapter(Promise promise) {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                promise.resolve("isEnable");
            } else {
                promise.resolve("isDisable");
            }
        } else {
            promise.reject("no adapter");
        }
    }
    
    @ReactMethod
    public void getPairedDevices(Promise promise) {
        WritableMap devices = Arguments.createMap();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                devices.putString(deviceName, deviceHardwareAddress);
            }
        }

        promise.resolve(devices);
    }
    
    @ReactMethod
    public void bluetoothServerInitial(String name, String uuid, Callback errorCallback, Callback successCallback) {
        bluetoothService = new AcceptTheard(bluetoothAdapter, name, UUID.fromString(uuid), errorCallback, successCallback);
    }

    @ReactMethod
    public void bluetoothServerStart(Callback errorCallback, Callback successCallback) {
        bluetoothService.run(errorCallback, successCallback);
    }

    @ReactMethod
    public void bluetoothServerCancel(Callback errorCallback, Callback successCallback) {
        bluetoothService.cancel(errorCallback, successCallback);
    }
}