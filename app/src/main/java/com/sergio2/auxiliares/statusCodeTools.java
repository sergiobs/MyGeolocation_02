package com.sergio2.auxiliares;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by sergio2 on 12/11/2015.
 */


public class statusCodeTools {
    static String[] statusCodes = {
            "200", "200 OK. The request has succeeded" ,
            "201", "201 Created. The request has been fulfilled and resulted in a new resource being created.",
            "400", "400 Client Error. Bad Request.",
            "401", "401 Client Error. Unauthorized",
            "403", "403 Client Error. Forbidden",
            "404", "404 Client Error. Not found.",
            "405", "405 Client Error. Method not allowed.",
            "406", "406 Client Error. Not acceptable.",
            "501", "501 Server Error. Not implemented. The server does not support the functionality required to fulfill the request.",
            "503", "503 Server Error. Service Unavailable. The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.",
            "504", "504 Server Error. Gateway TimeOut."
    };



    public static String code2str(int statusCodeNum) {

        String statusCodeString = "";
        int statusCode_index = Arrays.binarySearch(statusCodes, Integer.toString(statusCodeNum))+1;
        if (statusCode_index>=0) {
            statusCodeString = statusCodes[statusCode_index].substring(4);
        }
        else {
            statusCodeString = "Unknown code number";
        }
        return statusCodeString;
    }

}
