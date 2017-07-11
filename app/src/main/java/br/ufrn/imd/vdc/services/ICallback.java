package br.ufrn.imd.vdc.services;

import br.ufrn.imd.vdc.obd.ICommand;

/**
 * Created by elton on 02/06/17.
 */

public interface ICallback {
    void callback(ICommand cmd);
}
