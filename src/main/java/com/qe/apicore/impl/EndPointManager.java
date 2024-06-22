package com.qe.apicore.impl;

public class EndPointManager {
    public static String reformatEndPoint(String endPoint){
        if(endPoint!=null && !endPoint.startsWith("/")){
            endPoint="/"+endPoint;
            return endPoint;
        }
        return endPoint;
    }
}
