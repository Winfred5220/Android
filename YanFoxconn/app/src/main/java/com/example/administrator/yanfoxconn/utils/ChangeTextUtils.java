package com.example.administrator.yanfoxconn.utils;

import java.io.IOException;

import taobe.tec.jcc.JChineseConvertor;

/**
 * 繁簡體轉換
 * Created by song on 2017/8/30.
 */

public class ChangeTextUtils {

    //簡體轉換成繁體
    public String simToTra(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.s2t(changeText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

    //繁體轉換成簡體
    public String traToSim(String changeText) {
        try {
            JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
            changeText = jChineseConvertor.t2s(changeText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return changeText;
    }

}
