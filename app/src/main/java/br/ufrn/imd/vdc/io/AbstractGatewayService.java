package br.ufrn.imd.vdc.io;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by elton on 12/05/17.
 */

public abstract class AbstractGatewayService extends Service {
    private static final String TAG = AbstractGatewayService.class.getName();
    private final IBinder binder = new AbstractGatewayServiceBinder();
    protected Context context;
    protected BlockingQueue<CommandTask> taskQueue;
    protected Long queueCounter = 0L;
    protected boolean isRunning;

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            // Template Method Implementation
            executeTask();
        }
    });

    AbstractGatewayService(Context context) {
        super();
        this.context = context;
        this.taskQueue = new LinkedBlockingQueue<>();
    }

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

    public void queueTask(CommandTask task) {
        queueCounter++;
        Log.d(TAG, "queueTask: Trying to add task[" + queueCounter + "] to queue..");

        task.setId(queueCounter);
        try {
            taskQueue.put(task);
            Log.d(TAG, "queueTask: Task queued successfully.");
        } catch (InterruptedException e) {
            task.setState(CommandTask.CommandTaskState.QUEUE_ERROR);
            Log.e(TAG, "queueTask: Failed to queue task.");
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
