package br.ufrn.imd.vdc.io;

import com.github.pires.obd.commands.ObdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by elton on 12/05/17.
 */

public class ObdCommandAdapter implements ICommand {
    ObdCommand obdCommand;

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
    public String getCalculatedResult() {
        return obdCommand.getCalculatedResult();
    }

    @Override
    public boolean useImperialUnits() {
        return obdCommand.useImperialUnits();
    }

    @Override
    public String getResultUnit() {
        return obdCommand.getResultUnit();
    }

    @Override
    public void useImperialUnits(boolean isImperial) {
        obdCommand.useImperialUnits(isImperial);
    }

    @Override
    public String getName() {
        return obdCommand.getName();
    }

    @Override
    public Long getResponseTimeDelay() {
        return obdCommand.getResponseTimeDelay();
    }

    @Override
    public void setResponseTimeDelay(Long responseDelayInMs) {
        obdCommand.setResponseTimeDelay(responseDelayInMs);
    }

    @Override
    public long getStart() {
        return obdCommand.getStart();
    }

    @Override
    public void setStart(long start) {
        obdCommand.setStart(start);
    }

    @Override
    public long getEnd() {
        return obdCommand.getEnd();
    }

    @Override
    public void setEnd(long end) {
        obdCommand.setEnd(end);
    }

    @Override
    public String getCommandPID() {
        return obdCommand.getCommandPID();
    }
}
