package br.ufrn.imd.vdc.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.ufrn.imd.vdc.R;
import br.ufrn.imd.vdc.obd.CommandTask;
import br.ufrn.imd.vdc.services.ObdGatewayServiceManager;
import br.ufrn.imd.vdc.services.TaskBroadcastReceiver;

public class MainActivity extends TaskProgressListener implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();

    private Button btnStartService;
    private Button btnStopService;
    private Button btnEnqueueCommands;
    private Button btnStartBluetooth;
    private Button btnStopBluetooth;

    private TaskBroadcastReceiver receiver = new TaskBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TaskBroadcastReceiver.TASK_RESPONSE);

        registerReceiver(receiver, intentFilter);

        setup();
    }

    private void setup() {
        // Start/Stop bluetooth
        btnStartBluetooth = (Button) findViewById(R.id.btn_connect_bluetooth);
        btnStopBluetooth = (Button) findViewById(R.id.btn_disconnect_bluetooth);

        btnStartBluetooth.setOnClickListener(this);
        btnStopBluetooth.setOnClickListener(this);

        // Start/Stop Service and Enqueue Commands
        btnStartService = (Button) findViewById(R.id.btn_start_service);
        btnStopService = (Button) findViewById(R.id.btn_stop_service);
        btnEnqueueCommands = (Button) findViewById(R.id.btn_enqueue_commands);

        btnStartService.setOnClickListener(this);
        btnEnqueueCommands.setOnClickListener(this);
        btnStopService.setOnClickListener(this);

        // Clear log button
        Button btnClearLog = (Button) findViewById(R.id.btn_clear_log);
        btnClearLog.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateState(ObdGatewayServiceManager.State.DISCONNECTED);
    }

    private void setActionBarSubtitle(String subtitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
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

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect_bluetooth:
                Log.d(TAG, "onClick: Creating bluetooth connection");

                // Get MAC Address from Preferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String btDeviceMAC = prefs.getString(SettingsActivity.BLUETOOTH_DEVICES, "-1");

                if (!btDeviceMAC.equals("-1") &&
                    ObdGatewayServiceManager.getInstance().setUpDevice(btDeviceMAC)) {
                    updateState(ObdGatewayServiceManager.State.CONNECTED);
                }
                break;
            case R.id.btn_disconnect_bluetooth:
                Log.d(TAG, "onClick: disconnecting bluetooth");
                updateState(ObdGatewayServiceManager.State.DISCONNECTING);
                ObdGatewayServiceManager.getInstance().disconnect();
                updateState(ObdGatewayServiceManager.State.DISCONNECTED);
                break;
            case R.id.btn_start_service:
                Log.d(TAG, "onClick: Calling doBindService()");
                ObdGatewayServiceManager.getInstance().enqueueInitialCommands(this);
                ObdGatewayServiceManager.getInstance().startAlarm(this, 60 * 1000);  // 1 min
                break;
            case R.id.btn_stop_service:
                Log.d(TAG, "onClick: Calling doUnbindService()");
                ObdGatewayServiceManager.getInstance().stopAlarm(this);
                break;
            case R.id.btn_enqueue_commands:
                Log.d(TAG, "onClick: Enqueuing commands");
                ObdGatewayServiceManager.getInstance().enqueueDefaultCommands(this);
                break;
            case R.id.btn_clear_log:
                Log.d(TAG, "onClick: Clearing OBD Commands Log");
                clearObdLog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister receiver on Activity Destroy
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void clearObdLog() {
        TextView tvResultsLog = (TextView) findViewById(R.id.tv_results);
        tvResultsLog.setText("");
    }

    @Override
    public void updateState(CommandTask task) {
        TextView tvResultsLog = (TextView) findViewById(R.id.tv_results);
        tvResultsLog.append(task.getCommand().toString());
    }

    @Override
    public void updateState(ObdGatewayServiceManager.State state) {
        switch (state) {
            case CONNECTED:
                setActionBarSubtitle(ObdGatewayServiceManager.getInstance().getDeviceString());
                btnStartBluetooth.setEnabled(false);
                btnStopBluetooth.setEnabled(true);
                btnStartService.setEnabled(true);
                btnStopService.setEnabled(true);
                btnEnqueueCommands.setEnabled(true);
                break;
            case DISCONNECTED:
                setActionBarSubtitle(getString(R.string.disconnected));
                btnStartBluetooth.setEnabled(true);
                btnStopBluetooth.setEnabled(false);
                btnStartService.setEnabled(false);
                btnStopService.setEnabled(false);
                btnEnqueueCommands.setEnabled(false);
                break;
            case CONNECTING:
                setActionBarSubtitle(getString(R.string.connecting));
                btnStartBluetooth.setEnabled(false);
                btnStopBluetooth.setEnabled(false);
                btnStartService.setEnabled(false);
                btnStopService.setEnabled(false);
                btnEnqueueCommands.setEnabled(false);
                break;
            case DISCONNECTING:
                setActionBarSubtitle(getString(R.string.disconnecting));
                btnStartBluetooth.setEnabled(false);
                btnStopBluetooth.setEnabled(false);
                btnStartService.setEnabled(false);
                btnStopService.setEnabled(false);
                btnEnqueueCommands.setEnabled(false);
                break;
            default:
                Log.d(TAG, "updateState: Unhandled option");
                break;
        }
    }
}
