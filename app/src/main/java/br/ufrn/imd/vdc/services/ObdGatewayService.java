package br.ufrn.imd.vdc.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import br.ufrn.imd.vdc.helpers.BluetoothManager;
import br.ufrn.imd.vdc.obd.CommandTask;

public class ObdGatewayService extends IntentService {
    private static final String ACTION_SEND_OBD_COMMAND = "br.ufrn.imd.vdc.services.action" +
                                                          ".ACTION_SEND_OBD_COMMAND";

    private static final String TAG = ObdGatewayService.class.getName();

    private static Queue<CommandTask> tasks = new LinkedBlockingQueue<>();

    public ObdGatewayService() {
        super("ObdGatewayService");
    }

    public static void enqueueTask(Context context, CommandTask task) {
        Intent intent = new Intent(context, ObdGatewayService.class);
        intent.setAction(ACTION_SEND_OBD_COMMAND);
        tasks.add(task);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_OBD_COMMAND.equals(action)) {
                CommandTask cmdTask = tasks.poll();

                try {
                    Log.d(TAG, "onHandleIntent: Connecting to bluetooth device");
                    BluetoothManager.getInstance().connect();
                } catch (IOException e) {
                    Log.e(TAG, "startObdConnection: Error occurred when starting a bluetooth " +
                               "connection",
                        e);
                    return;
                }

                if (!BluetoothManager.getInstance().isConnected()) {
                    Log.e(TAG, "startObdConnection: Bluetooth Socket isn't connected");
                }

                executeTask(cmdTask);
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
            if (BluetoothManager.getInstance().isConnected()) {
                try {
                    task.getCommand()
                        .run(BluetoothManager.getInstance().getSocket().getInputStream(),
                            BluetoothManager.getInstance().getSocket().getOutputStream());
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
    }
}
