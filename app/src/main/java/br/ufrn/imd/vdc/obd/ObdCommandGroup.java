package br.ufrn.imd.vdc.obd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elton on 02/06/17.
 */

public class ObdCommandGroup implements ICommand {
    private final ArrayList<ICommand> commands;

    public ObdCommandGroup() {
        commands = new ArrayList<>();
    }

    public void add(ICommand command) {
        commands.add(command);
    }

    public void remove(ICommand command) {
        commands.remove(command);
    }

    @Override
    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        for (ICommand command : commands) {
            command.run(in, out);
        }
    }

    @Override
    public List<String> getResult() {
        ArrayList<String> results = new ArrayList<>();
        for (ICommand command : commands) {
            results.add((String) command.getResult());
        }
        return results;
    }

    @Override
    public List<Object> getFormattedResult() {
        ArrayList<Object> results = new ArrayList<>();
        for (ICommand command : commands) {
            results.add(command.getFormattedResult());
        }
        return results;
    }

    @Override
    public List<String> getResultUnit() {
        ArrayList<String> results = new ArrayList<>();
        for (ICommand command : commands) {
            results.add((String) command.getResultUnit());
        }
        return results;
    }

    @Override
    public List<String> getName() {
        ArrayList<String> results = new ArrayList<>();
        for (ICommand command : commands) {
            results.add((String) command.getName());
        }
        return results;
    }

    @Override
    public List<String> getPID() {
        ArrayList<String> results = new ArrayList<>();
        for (ICommand command : commands) {
            results.add((String) command.getPID());
        }
        return results;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ICommand command : commands) {
            sb.append(command.toString());
        }
        return sb.toString();
    }
}
