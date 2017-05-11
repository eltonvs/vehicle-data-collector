package br.ufrn.imd.vdc.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.ufrn.imd.vdc.R;
import br.ufrn.imd.vdc.io.ObdAsyncTask;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = BluetoothActivity.class.getName();
    private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        setup();
    }

    private void setup() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String btDeviceMAC = prefs.getString(SettingsActivity.BLUETOOTH_DEVICES, "-1");

        if (!btDeviceMAC.equals("-1")) {
            Log.d(TAG, "Creating a Bluetooth Device");
            BluetoothDevice device = btAdapter.getRemoteDevice(btDeviceMAC);
            Log.d(TAG, "Device Name: " + device.getName());

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(device.getName() + " - " + btDeviceMAC);
            }

            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG, "Device is bonded, starting connection");
                new ObdAsyncTask(this, device).execute();
            }
        }
    }
}
