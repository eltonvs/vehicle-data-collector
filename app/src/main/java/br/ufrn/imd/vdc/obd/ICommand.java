package br.ufrn.imd.vdc.obd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by elton on 02/06/17.
 */

public interface ICommand {
    void run(InputStream in, OutputStream out) throws IOException, InterruptedException;

    Object getResult();

    Object getFormattedResult();

    Object getResultUnit();

    Object getName();

    Object getPID();

    Map<String, String> getMap();

    String toString();
}
