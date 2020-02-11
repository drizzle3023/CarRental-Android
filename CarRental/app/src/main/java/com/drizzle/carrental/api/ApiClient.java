package com.drizzle.carrental.api;

import android.content.Context;

import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.Utils;


import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    static Retrofit retrofit = null;
    private static Retrofit retrofit_address = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        String server_api_url =
                Constants.SERVER_HTTP_URL + "/";

        retrofit = new Retrofit.Builder()
                .baseUrl(server_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static void uploadFile(Context context, String apiName, String filePath) {
        MultipartUploadRequest multipartUploadRequest = null;

        multipartUploadRequest = new MultipartUploadRequest(context, Constants.SERVER_HTTP_URL + "/api/" + apiName);

        multipartUploadRequest.setMethod("POST");
        try {
            multipartUploadRequest.addFileToUpload(filePath, "video-vehicle");
            multipartUploadRequest.addParameter("coverage_id", Globals.coverage.getId().toString());
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
                e.printStackTrace();
        }

        multipartUploadRequest.startUpload();
    }

}
