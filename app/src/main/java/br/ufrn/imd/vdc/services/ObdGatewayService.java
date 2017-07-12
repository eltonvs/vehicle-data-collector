package br.ufrn.imd.vdc.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.io.IOException;

import br.ufrn.imd.vdc.activities.SettingsActivity;
import br.ufrn.imd.vdc.helpers.BluetoothManager;
import br.ufrn.imd.vdc.obd.CommandTask;
import br.ufrn.imd.vdc.obd.ObdCommandAdapter;
import br.ufrn.imd.vdc.obd.ObdCommandList;
import br.ufrn.imd.vdc.obd.ObdCommandTask;

public class ObdGatewayService extends AbstractGatewayService {
    private static final String TAG = ObdGatewayService.class.getName();
    private BluetoothDevice device;
    private BluetoothSocket btSocket;

    @Override
    protected void executeTask() {
        Log.d(TAG, "executeTask: Executing queue...");

        while (!Thread.currentThread().isInterrupted()) {
            CommandTask task = null;
            try {
                task = taskQueue.take();

                Log.d(TAG, "executeTask: Taking task[" + task.getId() + "] from queue...");
                if (task.getState().equals(CommandTask.CommandTaskState.NEW)) {
                    Log.d(TAG, "executeTask: Task state is NEW. Run it...");
                    task.setState(CommandTask.CommandTaskState.RUNNING);
                    if (btSocket.isConnected()) {
                        task.getCommand().run(btSocket.getInputStream(),
                            btSocket.getOutputStream());
                        task.setState(CommandTask.CommandTaskState.FINISHED);
                    } else {
                        task.setState(CommandTask.CommandTaskState.EXECUTION_ERROR);
                        Log.e(TAG, "executeTask: Can't run command on a closed socket.");
                    }
                } else {
                    Log.e(TAG, "executeTask: That's a bug, it shouldn't happen...");
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "executeTask: InterruptedException on thread", e);
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException e) {
                Log.e(TAG, "executeTask: UnsupportedCommandException", e);
                if (task != null) {
                    task.setState(CommandTask.CommandTaskState.NOT_SUPPORTED);
                }
            } catch (IOException e) {
                Log.e(TAG, "executeTask: IOException", e);
                task.setState(e.getMessage().contains(
                    "Broken pipe") ? CommandTask.CommandTaskState.BROKEN_PIPE : CommandTask
                                  .CommandTaskState.EXECUTION_ERROR);
            } catch (Exception e) {
                Log.e(TAG, "executeTask: Some error occurred", e);
                if (task != null) {
                    task.setState(CommandTask.CommandTaskState.EXECUTION_ERROR);
                }
            }

            if (task != null) {
                Log.d(TAG,
                    "executeTask: task: " + task.getCommand().getName() + " | state: " + task
                        .getState() + " | value: " + task.getCommand().getResult());
            }

            if (task != null && task.getState().equals(CommandTask.CommandTaskState.FINISHED)) {
                final CommandTask returnedTask = task;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update GUI with changed data
                        context.updateState(returnedTask);
                    }
                });
            }
        }
    }

    @Override
    public void startService() throws IOException {
        Log.d(TAG, "startService: Starting Service...");

        setupBluetoothDevice();

        try {
            startObdConnection();
        } catch (IOException e) {
            Log.e(TAG, "startService: Error while establishing a connection", e);
            stopService();
            throw e;
        }
    }

    private void setupBluetoothDevice() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String btDeviceMAC = prefs.getString(SettingsActivity.BLUETOOTH_DEVICES, "-1");

        if (!btDeviceMAC.equals("-1")) {
            Log.d(TAG, "Creating a Bluetooth Device");
            device = btAdapter.getRemoteDevice(btDeviceMAC);
            Log.d(TAG, "Device Name: " + device.getName());

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActionBar actionBar = context.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setSubtitle(device.getName() + " - " + btDeviceMAC);
                    }
                }
            });

            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG, "Device is bonded, starting connection");
            }
        }
    }

    private void startObdConnection() throws IOException {
        Log.d(TAG, "startObdConnection: String OBD Connection....");

        isRunning = true;
        try {
            btSocket = BluetoothManager.connect(device);
        } catch (IOException e) {
            Log.e(TAG, "startObdConnection: Error occurred when starting a bluetooth connection",
                e);
            throw e;
        }

        if (!btSocket.isConnected()) {
            Log.e(TAG, "startObdConnection: Bluetooth Socket isn't connected");
            throw new IOException();
        }

        obdSetup();
    }

    private void obdSetup() throws IOException {
        try {
            enqueueTask(new ObdCommandTask(new ObdCommandAdapter(new ObdResetCommand())));

            // Sleep while OBD device is being resetting.
            Thread.sleep(500);

            enqueueTask(new ObdCommandTask(ObdCommandList.getInstance().setupDevice()));
        } catch (InterruptedException e) {
            Log.e(TAG, "obdSetup: An error occurred (InterruptedException)", e);
            Thread.currentThread().interrupt();
            throw new IOException();
        }
    }

    @Override
    public void stopService() {
        Log.d(TAG, "stopService: Stopping Service...");

        taskQueue.clear();
        isRunning = false;

        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "stopService: Error while closing bluetooth socket", e);
            }
        }

        stopSelf();
    }
}
