package br.ufrn.imd.vdc.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by elton on 12/05/17.
 */


public interface ICommand {
    void run(InputStream in, OutputStream out) throws IOException, InterruptedException;

    String getResult();

    String getFormattedResult();

    String getCalculatedResult();

    boolean useImperialUnits();

    String getResultUnit();

    void useImperialUnits(boolean isImperial);

    String getName();

    Long getResponseTimeDelay();

    void setResponseTimeDelay(Long responseDelayInMs);

    long getStart();

    void setStart(long start);

    long getEnd();

    void setEnd(long end);

    String getCommandPID();
}
