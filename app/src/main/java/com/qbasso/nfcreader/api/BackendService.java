package com.qbasso.nfcreader.api;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface BackendService {

    @POST("/locks/5124/access")
    @Headers("Accept: */*")
    Observable<Response<Void>> unlock(@Header("Authorization") String auth);

}