package br.ufrn.imd.vdc.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import br.ufrn.imd.vdc.R;
import br.ufrn.imd.vdc.io.BluetoothManager;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = BluetoothActivity.class.getName();
    private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        setup();
    }

    private void setup() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        TextView tv = (TextView) findViewById(R.id.tvBluetooth);
        String btDeviceAddr = prefs.getString(SettingsActivity.BLUETOOTH_DEVICES, "-1");
        tv.setText(btDeviceAddr);

        if (!btDeviceAddr.equals("-1")) {
            Log.d(TAG, "Creating a Bluetooth Device");
            BluetoothDevice device = btAdapter.getRemoteDevice(btDeviceAddr);
            Log.d(TAG, "Device Name: " + device.getName());

            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG, "Device is bonded, starting connection");
                // TODO: Put this running in a service
                obdConnection(device);
            }
        }
    }

    private void obdConnection(BluetoothDevice device) {
        try {
            btSocket = BluetoothManager.connect(device);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when starting a bluetooth connection", e);
        }

        if (btSocket == null || !btSocket.isConnected()) {
            Log.e(TAG, "Bluetooth Socket isn't connected");
            return;
        }

        Log.d(TAG, "Bluetooth Socket connected");
        runObdCommands();
        try {
            btSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth Socket", e);
        }
    }

    private void runObdCommands() {
        obdSetup();

        TextView obdResults = (TextView) findViewById(R.id.tvResults);
        obdResults.setText("");

        // TODO: Create this list based on car's commands availability
        ArrayList<ObdCommand> obdCommands = new ArrayList<>();
        obdCommands.add(new RPMCommand());
        obdCommands.add(new ThrottlePositionCommand());
        obdCommands.add(new AmbientAirTemperatureCommand());
        obdCommands.add(new SpeedCommand());
        obdCommands.add(new VinCommand());

        try {
            InputStream socketIS = btSocket.getInputStream();
            OutputStream socketOS = btSocket.getOutputStream();

            for (ObdCommand cmd : obdCommands) {
                cmd.run(socketIS, socketOS);
                String formattedOutput = cmd.getName() + " = " + cmd.getFormattedResult();
                Log.d(TAG, formattedOutput);
                obdResults.setText(obdResults.getText() + formattedOutput + "\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "Some error occurred", e);
        }
    }

    private void obdSetup() {
        try {
            new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            new TimeoutCommand(125).run(btSocket.getInputStream(), btSocket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (Exception e) {
            Log.e(TAG, "Some error occurred", e);
        }
    }
}
