package br.ufrn.imd.vdc.helpers;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import br.ufrn.imd.vdc.activities.SettingsActivity;
import br.ufrn.imd.vdc.services.AbstractGatewayService;
import br.ufrn.imd.vdc.services.ObdGatewayService;
import br.ufrn.imd.vdc.services.tasks.ICommand;
import br.ufrn.imd.vdc.services.tasks.ObdCommandTask;
import br.ufrn.imd.vdc.activities.TaskProgressListener;

/**
 * Created by elton on 5/15/17.
 */

public class ObdServiceManager {
    private static final String TAG = ObdServiceManager.class.getName();
    private final ServiceConnection serviceConnection;
    private final TaskProgressListener context;
    private AbstractGatewayService service;
    private volatile Status currentState;

    public ObdServiceManager(final TaskProgressListener context) {
        this.context = context;
        currentState = Status.DISCONNECTED;
        this.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(TAG, "onServiceConnected: Service is bound");
                if (verifyPreRequisites()) {
                    service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
                    service.setContext(context);
                    try {
                        Log.d(TAG, "onServiceConnected: Trying to start Service");
                        service.startService();
                        Log.d(TAG, "onServiceConnected: Bluetooth device is connected");
                        setCurrentState(Status.CONNECTED);
                    } catch (IOException e) {
                        Log.e(TAG, "onServiceConnected: Service failed to start", e);
                        doUnbindService();
                    }
                } else {
                    Log.e(TAG, "onServiceConnected: Pre requisites not satisfied");
                    setCurrentState(Status.DISCONNECTED);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected: Service is unbound");
                setCurrentState(Status.DISCONNECTED);
            }
        };
    }

    public void doBindService() {
        if (currentState != Status.CONNECTED) {
            Log.d(TAG, "doBindService: Binding service");
            setCurrentState(Status.CONNECTING);
            if (verifyPreRequisites()) {
                Log.d(TAG, "doBindService: Creating Service");
                Intent intentService = new Intent(context, ObdGatewayService.class);
                if (context.bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE)) {
                    Log.d(TAG, "doBindService: Service is bound");
                } else {
                    Log.e(TAG, "doBindService: Error binding service");
                    setCurrentState(Status.DISCONNECTED);
                }
            } else {
                Log.e(TAG, "doBindService: Pre requisites not satisfied");
                setCurrentState(Status.DISCONNECTED);
            }
        }
    }

    public void doUnbindService() {
        if (currentState != Status.DISCONNECTED) {
            setCurrentState(Status.DISCONNECTING);
            if (service.isRunning()) {
                service.stopService();
            }
            Log.d(TAG, "doUnbindService: Unbinding service");
            context.unbindService(serviceConnection);
            Log.d(TAG, "doUnbindService: Service Disconnected");
            setCurrentState(Status.DISCONNECTED);
        }
    }

    public void enqueueDefaultCommands() {
        if (currentState == Status.CONNECTED) {
            Log.d(TAG, "enqueueDefaultCommands: Enqueuing Commands");
            for (ICommand cmd : ObdCommandList.getInstance().getCommands())
                service.enqueueTask(new ObdCommandTask(cmd));
        } else {
            Log.e(TAG, "enqueueDefaultCommands: Service isn't connected");
        }
    }

    private void setCurrentState(Status state) {
        Log.d(TAG, "setCurrentState: Updating State...");
        currentState = state;
        context.updateState(state);
    }

    private boolean verifyPreRequisites() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Log.e(TAG, "verifyPreRequisites: Bluetooth is not enabled");
            return false;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String btDeviceMAC = prefs.getString(SettingsActivity.BLUETOOTH_DEVICES, "-1");
        if (btDeviceMAC.equals("-1")) {
            Log.e(TAG, "verifyPreRequisites: No Bluetooth device is set");
            return false;
        }

        return true;
    }

    public enum Status {
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
        DISCONNECTING
    }
}
