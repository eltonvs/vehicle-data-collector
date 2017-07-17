package br.ufrn.imd.vdc.activities;

import android.support.v7.app.AppCompatActivity;

import br.ufrn.imd.vdc.obd.CommandTask;
import br.ufrn.imd.vdc.services.ObdGatewayServiceManager;

/**
 * Created by elton on 15/05/17.
 */

public abstract class TaskProgressListener extends AppCompatActivity {
    public abstract void updateState(final CommandTask task);

    public abstract void updateState(ObdGatewayServiceManager.State state);
}
