package br.ufrn.imd.vdc.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import br.ufrn.imd.vdc.R;
import br.ufrn.imd.vdc.io.AbstractGatewayService;
import br.ufrn.imd.vdc.io.CommandTask;
import br.ufrn.imd.vdc.io.ObdGatewayService;
import br.ufrn.imd.vdc.io.TaskProgressListener;

public class MainActivity extends TaskProgressListener implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();

    private boolean isServiceBound;
    private boolean preRequisites = true;
    private AbstractGatewayService service;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected: Service is bound");
            isServiceBound = true;
            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(MainActivity.this);
            try {
                Log.d(TAG, "onServiceConnected: Starting Service");
                service.startService();
                if (preRequisites) {
                    Log.d(TAG, "onServiceConnected: Bluetooth device is connected");
                }
            } catch (IOException e) {
                Log.e(TAG, "onServiceConnected: startService() failed", e);
                Log.e(TAG, "onServiceConnected: Service failed to start");
                doUnbindService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: Service is unbound");
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }

    private void setup() {
        Button btnStartService = (Button) findViewById(R.id.btn_start_service);
        Button btnStopService = (Button) findViewById(R.id.btn_stop_service);
        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        resetActionBarSubtitle();
    }

    private void resetActionBarSubtitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle("Disconnected");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_service:
                Log.d(TAG, "onClick: Calling doBindService()");
                doBindService();
                break;
            case R.id.btn_stop_service:
                Log.d(TAG, "onClick: Calling doUnbindService()");
                doUnbindService();
                break;
            default:
                break;
        }
    }

    @Override
    public void updateState(CommandTask task) {
        TextView tvResults = (TextView) findViewById(R.id.tv_results);
        tvResults.setText(tvResults.getText() + task.getCommand().getName() + " = " + task.getCommand().getFormattedResult() + "\n");
    }

    @Override
    protected void doBindService() {
        if (!isServiceBound) {
            Log.d(TAG, "doBindService: Binding service");
            if (preRequisites) {
                Log.d(TAG, "doBindService: Creating Service");
                Intent intentService = new Intent(MainActivity.this, ObdGatewayService.class);
                if (bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE)) {
                    Log.d(TAG, "doBindService: Service is bound");
                } else {
                    Log.e(TAG, "doBindService: Error binding service");
                }
            } else {
                Log.e(TAG, "doBindService: Error Creating Service");
            }
        }
    }

    @Override
    protected void doUnbindService() {
        if (isServiceBound) {
            if (service.isRunning()) {
                service.stopService();
            }
            Log.d(TAG, "doUnbindService: Unbinding service");
            unbindService(serviceConnection);
            isServiceBound = false;
            Log.d(TAG, "doUnbindService: Service Disconnected");
            resetActionBarSubtitle();
        }
    }
}
