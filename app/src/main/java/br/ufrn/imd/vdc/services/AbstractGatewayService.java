package br.ufrn.imd.vdc.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import br.ufrn.imd.vdc.activities.TaskProgressListener;
import br.ufrn.imd.vdc.obd.CommandTask;

/**
 * Created by elton on 12/05/17.
 */

public abstract class AbstractGatewayService extends Service {
    private static final String TAG = AbstractGatewayService.class.getName();
    protected final BlockingQueue<CommandTask> taskQueue = new LinkedBlockingQueue<>();
    private final IBinder binder = new AbstractGatewayServiceBinder();
    private final Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            // A Template Method to implement task runner
            executeTask();
        }
    });
    protected TaskProgressListener context;
    protected Long queueCounter = 0L;
    protected boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Creating Service...");
        t.start();
        Log.d(TAG, "Service Created.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying service...");
        t.interrupt();
        Log.d(TAG, "Service Destroyed.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    public void setContext(TaskProgressListener context) {
        this.context = context;
    }

    public void enqueueTask(CommandTask task) {
        queueCounter++;
        Log.d(TAG, "enqueueTask: Trying to add task[" + queueCounter + "] to queue..");

        task.setId(queueCounter);
        try {
            taskQueue.put(task);
            Log.d(TAG, "enqueueTask: Task queued successfully.");
        } catch (InterruptedException e) {
            task.setState(CommandTask.CommandTaskState.QUEUE_ERROR);
            Log.e(TAG, "enqueueTask: Failed to queue task.");
        }
    }

    public boolean queueEmpty() {
        return taskQueue.isEmpty();
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected abstract void executeTask();

    public abstract void startService() throws IOException;

    public abstract void stopService();

    public class AbstractGatewayServiceBinder extends Binder {
        public AbstractGatewayService getService() {
            return AbstractGatewayService.this;
        }
    }
}
