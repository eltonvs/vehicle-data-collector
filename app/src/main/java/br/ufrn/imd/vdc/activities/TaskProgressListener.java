package br.ufrn.imd.vdc.activities;

import android.support.v7.app.AppCompatActivity;

import br.ufrn.imd.vdc.helpers.ObdServiceManager;
import br.ufrn.imd.vdc.services.tasks.CommandTask;

/**
 * Created by elton on 15/05/17.
 */

public abstract class TaskProgressListener extends AppCompatActivity {
    public abstract void updateState(final CommandTask task);

    public abstract void updateState(ObdServiceManager.Status status);
}
