package br.ufrn.imd.vehicle_data_collector.io;

public interface ObdProgressListener {
    void stateUpdate(final ObdCommandJob job);
}
