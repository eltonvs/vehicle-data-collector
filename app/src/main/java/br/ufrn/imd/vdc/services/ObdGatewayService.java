package br.ufrn.imd.vdc.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import br.ufrn.imd.vdc.helpers.BluetoothManager;
import br.ufrn.imd.vdc.obd.CommandTask;
import br.ufrn.imd.vdc.obd.ObdReading;

public class ObdGatewayService extends IntentService {
    public static final String ACTION_SEND_OBD_COMMAND = "br.ufrn.imd.vdc.services.action" +
                                                         ".ACTION_SEND_OBD_COMMAND";

    private static final String TAG = ObdGatewayService.class.getName();

    private static final BlockingQueue<CommandTask> tasks = new LinkedBlockingQueue<>();
    private final BluetoothManager btManager = BluetoothManager.getInstance();

    public ObdGatewayService() {
        super("ObdGatewayService");
    }

    static void enqueueTask(CommandTask task) {
        tasks.add(task);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_OBD_COMMAND.equals(action)) {
                try {
                    CommandTask cmdTask = tasks.take();
                    if (!btManager.isConnected()) {
                        Log.d(TAG, "onHandleIntent: BT Socket isn't connected. Connecting...");
                        btManager.connect();
                    }

                    executeTask(cmdTask);
                } catch (InterruptedException e) {
                    Log.e(TAG, "onHandleIntent: InterruptedException Error", e);
                    Thread.currentThread().interrupt();
                } catch (BluetoothManager.DeviceNotSetException e) {
                    Log.e(TAG, "onHandleIntent: Bluetooth Device is not set", e);
                } catch (IOException e) {
                    Log.e(TAG, "onHandleIntent: Error when starting a bluetooth connection", e);
                }
            }
        }
    }

    /**
     * Execute Task in the provided background thread with the provided parameters.
     */
    private void executeTask(CommandTask task) {
        Log.d(TAG, "executeTask: Taking task[" + task.getId() + "] from queue...");
        if (task.getState().equals(CommandTask.CommandTaskState.NEW)) {
            Log.d(TAG, "executeTask: Task state is NEW. Run it...");
            task.setState(CommandTask.CommandTaskState.RUNNING);
            if (btManager.isConnected()) {
                try {
                    task.getCommand()
                        .run(btManager.getSocket().getInputStream(),
                            btManager.getSocket().getOutputStream());
                } catch (IOException e) {
                    Log.e(TAG, "executeTask: IOException on run command", e);
                } catch (InterruptedException e) {
                    Log.e(TAG, "executeTask: InterruptedException on run command", e);
                    Thread.currentThread().interrupt();
                }
                task.setState(CommandTask.CommandTaskState.FINISHED);
            } else {
                task.setState(CommandTask.CommandTaskState.EXECUTION_ERROR);
                Log.e(TAG, "executeTask: Can't run command on a closed socket.");
            }
        } else {
            Log.e(TAG, "executeTask: That's a bug, it shouldn't happen...");
        }

        Log.d(TAG, "executeTask: task: " + task.getCommand().getName() + " | state: " +
                   task.getState() + " | value: " + task.getCommand().getResult() + "\n" +
                   task.getCommand().toString());
        sendTaskBroadcast(task);
    }

    private void sendTaskBroadcast(CommandTask task) {
        Intent broadcastIntent = new Intent();

        broadcastIntent.setAction(TaskBroadcastReceiver.TASK_RESPONSE);
        broadcastIntent.putExtra(TaskBroadcastReceiver.TASK_STRING, task.getCommand().toString());

        Map<String, String> readingsMap = task.getCommand().getMap();

        ObdReading reading = new ObdReading(0, 0, "", 0, 0, 0, readingsMap);
        broadcastIntent.setAction(TaskBroadcastReceiver.OBD_READING);
        broadcastIntent.putExtra(TaskBroadcastReceiver.OBD_READING, reading);

        Log.d(TAG, "sendTaskBroadcast: sending broadcast");
        sendBroadcast(broadcastIntent);
    }
}
