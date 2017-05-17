package br.ufrn.imd.vdc.io;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by elton on 15/05/17.
 */

public abstract class TaskProgressListener extends AppCompatActivity {
    public abstract void updateState(final CommandTask task);

    public abstract void updateState(ObdServiceManager.Status status);
}
