package br.ufrn.imd.vehicle_data_collector.net;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Definition of REST service available in OBD Server.
 */

public interface ObdService {
    @POST("/")
    Response uploadReading(@Body ObdReading reading);
}
