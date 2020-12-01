package com.example.administrator.yanfoxconn.controller;

/**
 * Created by song
 */
public class BaseResponse {

    public int errorCode;
    public String errorMessage;
    public Result result;

    public enum Result {
        SUCCESS,
        ERROR,
        TIME_OUT
    }
}
