package br.ufrn.imd.vdc.obd;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by elton on 12/05/17.
 */

public class ObdCommandAdapter implements ICommand {
    private final ObdCommand obdCommand;

    public ObdCommandAdapter(ObdCommand command) {
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

    @Override
    public Map<String, String> getMap() {
        Map<String, String> retMap = new HashMap<>();
        retMap.put(getName(), getResult());
        return retMap;
    }

    @Override
    public String toString() {
        return getName() + " = " + getFormattedResult() + "\n";
    }
}
