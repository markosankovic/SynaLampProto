package com.synapticon.synalampproto;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter mBluetoothAdapter;

    private int REQUEST_ENABLE_BLUETOOTH = 1;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i(TAG, "Bluetooth discovery started.");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "Bluetooth discovery finished");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i(TAG, "Bluetooth device found.");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, device.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "Device does not support Bluetooth.");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                queryPairedDevices();
            } else {
                Log.i(TAG, "Bluetooth is not enabled. Issue a request to enable Bluetooth through the system settings.");
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Bluetooth enabled.");
                queryPairedDevices();
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "Bluetooth was not enabled due to an error or the user responded \"No\".");
            }
        }
    }

    private void queryPairedDevices() {
        Log.i(TAG, "Query paired devices.");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Log.i(TAG, "There are paired devices.");
            for (BluetoothDevice device : pairedDevices) {
                Log.i(TAG, device.toString());
            }
        } else {
            Log.i(TAG, "There are no paired devices.");
            this.discoverDevices();
        }
    }

    public void onDiscoverDevices(View view) {
        discoverDevices();
    }

    private void discoverDevices() {
        Log.i(TAG, "Discover devices.");
        mBluetoothAdapter.startDiscovery();
    }
}
