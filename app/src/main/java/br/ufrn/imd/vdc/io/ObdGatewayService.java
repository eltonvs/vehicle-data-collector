package br.ufrn.imd.vdc.io;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.io.IOException;

public class ObdGatewayService extends AbstractGatewayService {
    private static final String TAG = ObdGatewayService.class.getName();
    private BluetoothDevice device;
    private BluetoothSocket btSocket;

    public ObdGatewayService(Context context, BluetoothDevice device) {
        super(context);
        this.device = device;
    }

    @Override
    public void startService() throws IOException {
        Log.d(TAG, "startService: Starting Service...");

        try {
            startObdConnection();
        } catch (IOException e) {
            Log.e(TAG, "startService: Error while establishing a connection", e);
            stopService();
            throw e;
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

    private void startObdConnection() throws IOException {
        Log.d(TAG, "startObdConnection: String OBD Connection....");

        isRunning = true;
        try {
            btSocket = BluetoothManager.connect(device);
        } catch (IOException e) {
            Log.e(TAG, "startObdConnection: Error occurred when starting a bluetooth connection", e);
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
            queueTask(new ObdCommandTask(new ObdCommandAdapter(new ObdResetCommand())));

            // Sleep while OBD device is being resetting.
            Thread.sleep(500);

            queueTask(new ObdCommandTask(new ObdCommandAdapter(new EchoOffCommand())));
            queueTask(new ObdCommandTask(new ObdCommandAdapter(new LineFeedOffCommand())));
            queueTask(new ObdCommandTask(new ObdCommandAdapter(new TimeoutCommand(62))));

            // TODO: use protocol defined on settings
            queueTask(new ObdCommandTask(new ObdCommandAdapter(new SelectProtocolCommand(ObdProtocols.AUTO))));
        } catch (InterruptedException e) {
            Log.e(TAG, "obdSetup: An error occurred (InterruptedException)", e);
            throw new IOException();
        }
    }

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
                        task.getCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
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
                task.setState(e.getMessage().contains("Broken pipe") ? CommandTask.CommandTaskState.BROKEN_PIPE : CommandTask.CommandTaskState.EXECUTION_ERROR);
            } catch (Exception e) {
                Log.e(TAG, "executeTask: Some error occurred", e);
                if (task != null) {
                    task.setState(CommandTask.CommandTaskState.EXECUTION_ERROR);
                }
            }

            /*
            if (task != null) {
                final CommandTask returnedTask = task;
                ((BluetoothActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update GUI with changed data
                    }
                });
            }
            */
        }
    }
}
