package br.ufrn.imd.vdc.io;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by elton on 5/15/17.
 */

public class ObdServiceManager {
    private static final String TAG = ObdServiceManager.class.getName();
    private final ServiceConnection serviceConnection;
    private TaskProgressListener context;
    private boolean preRequisites = true;
    private AbstractGatewayService service;
    private Status currentState;

    public ObdServiceManager(final TaskProgressListener context) {
        this.context = context;
        currentState = Status.DISCONNECTED;
        this.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(TAG, "onServiceConnected: Service is bound");
                service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
                service.setContext(context);
                try {
                    Log.d(TAG, "onServiceConnected: Starting Service");
                    service.startService();
                    if (preRequisites) {
                        Log.d(TAG, "onServiceConnected: Bluetooth device is connected");
                        setCurrentState(Status.CONNECTED);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "onServiceConnected: Service failed to start (startService() failed)", e);
                    doUnbindService();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected: Service is unbound");
                setCurrentState(Status.DISCONNECTED);
            }
        };
    }

    private void setCurrentState(Status state) {
        Log.d(TAG, "setCurrentState: Updating State...");
        currentState = state;
        context.updateState(state);
    }

    public void doBindService() {
        if (currentState != Status.CONNECTED) {
            Log.d(TAG, "doBindService: Binding service");
            setCurrentState(Status.CONNECTING);
            if (preRequisites) {
                Log.d(TAG, "doBindService: Creating Service");
                Intent intentService = new Intent(context, ObdGatewayService.class);
                if (context.bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE)) {
                    Log.d(TAG, "doBindService: Service is bound");
                } else {
                    Log.e(TAG, "doBindService: Error binding service");
                    setCurrentState(Status.DISCONNECTED);
                }
            } else {
                Log.e(TAG, "doBindService: Error Creating Service");
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

    public enum Status {
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
        DISCONNECTING
    }
}
