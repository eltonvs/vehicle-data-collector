package br.ufrn.imd.vdc.obd;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by elton on 12/05/17.
 */

public class ObdCommandAdapter2 implements ICommand {
    private final ObdCommand obdCommand;

    public ObdCommandAdapter2(ObdCommand command) {
        this.obdCommand = command;
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        obdCommand.run(in, out);
    }

    @Override
    public String getResult() {
        return obdCommand.getResult();
    }

    @Override
    public String getFormattedResult() {
        return obdCommand.getFormattedResult();
    }

    @Override
    public String getResultUnit() {
        return obdCommand.getResultUnit();
    }

    @Override
    public String getName() {
        return obdCommand.getName();
    }

    @Override
    public String getPID() {
        return obdCommand.getCommandPID();
    }
}
