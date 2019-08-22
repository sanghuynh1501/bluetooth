package com.bluetooth;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothAdapter;
import android.os.ParcelUuid;

import java.util.Set;
import java.util.UUID;
import java.util.Arrays;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.bluetooth.*;

public class ReactBluetoothAdapter extends ReactContextBaseJavaModule {
    
    private AcceptThread bluetoothService;
    private ReactApplicationContext reactContext;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                WritableMap deviceInfo = Arguments.createMap();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null) {
                    deviceInfo.putString("name", deviceName);
                    deviceInfo.putString("address", deviceHardwareAddress);
                    Log.i("Device Name: " , "device " + deviceName);
                    Log.i("deviceHardwareAddress " , "hard"  + deviceHardwareAddress);
                    sendEvent(reactContext, "deviceInfo", deviceInfo);
                }
            }
        }
    };

    private void sendEvent(ReactContext reactContext,
                       String eventName,
                       WritableMap params) {
        reactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(eventName, params);
    }

    public ReactBluetoothAdapter(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    // Create a BroadcastReceiver for ACTION_FOUND.

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
    public void bluetoothServerInitial(String address, String uuid, Callback errorCallback, Callback successCallback) {
        bluetoothAdapter.cancelDiscovery();
        bluetoothService = new AcceptThread(bluetoothAdapter, address, UUID.fromString(uuid), errorCallback, successCallback);
    }

    @ReactMethod
    public void bluetoothServerStart(Callback errorCallback, Callback successCallback) {
        bluetoothService.run(errorCallback, successCallback);
    }

    @ReactMethod
    public void bluetoothServerCancel(Callback errorCallback, Callback successCallback) {
        bluetoothService.cancel(errorCallback, successCallback);
    }

    @ReactMethod
    public void bluetoothServerScan(Callback errorCallback, Callback successCallback) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.reactContext.registerReceiver(mReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }
}