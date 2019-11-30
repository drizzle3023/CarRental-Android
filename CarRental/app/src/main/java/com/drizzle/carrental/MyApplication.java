package com.drizzle.carrental;

import android.app.Application;

import lombok.Getter;
import lombok.Setter;

public class MyApplication extends Application {


    @Getter
    @Setter
    private boolean isLoggedIn = false;


}
