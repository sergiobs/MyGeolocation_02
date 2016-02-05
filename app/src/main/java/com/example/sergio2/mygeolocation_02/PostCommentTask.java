package com.example.sergio2.mygeolocation_02;

import android.os.AsyncTask;

import com.sergio2.auxiliares.Depuracion;
import com.sergio2.auxiliares.statusCodeTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sergio2 on 25/11/2015.
 */

//Las tres variables de entrada que posee se refieren a los Parámetros, Unidades de Progreso y Resultados
//public class PostCommentTask extends AsyncTask<URL, Void, List<String>> {
public class PostCommentTask extends AsyncTask<String, Void, List<String>> {
    HttpURLConnection con = null;
    String commandQuery = "query";
    String commandTables = "tables";
    String accesToken = "?access_token=";
    String urlFusionT = "https://www.googleapis.com/fusiontables/v2/";
    URL url;



    String tableId="1CBP-Ht9blF_PuPXNWjAajjRfEZUbNeCxuQ5WvMbJ";
    String key ="AIzaSyB-C74mt75P7YcE9_RxXyDCNekBAQ-tGMk";
    String key2 ="AIzaSyBiZRmFXpNJ2A8na8b0lRGqITs-6U8POL4";
    String key3 ="AIzaSyDXgqOwipSCHNlCMevBwVnlRgXkhV0a1DY";


    String Query_03 = "SELECT Text, Number, Location, Date FROM tableID".replaceAll(" ", "%20").replaceAll("tableID", tableId);
    String Query_04 = "INSERT INTO tableID (Text, Number, Location, Date) VALUES ('f', '6', '40.0, 41.0', '2015-11-24 00:01:02')".replaceAll(" ", "%20").replaceAll("tableID", tableId);
    String Query_05 = "DELETE FROM tableID  WHERE ROWID = '1001'".replaceAll(" ", "%20").replaceAll("tableID", tableId);

    List comments = null;


    @Override
    //protected List doInBackground(URL... urls) {
    protected List doInBackground(String... idToken) {
        //String urlParameters  = "sql="+Query_04;
        //String urlParameters  = "sql="+Query_04+"&key="+key;
        //String urlParameters  = "sql="+Query_04+"&key="+key+"&Authorization: Bearer="+idToken[0];
        //String urlParameters  = "sql="+Query_04+"&key="+key+"&access_token="+idToken[0];
        //String urlParameters  = "sql="+Query_03+"&key="+key;
        String urlParameters  = "access_token="+idToken[0];
        try {
            url = new URL(urlFusionT + "tables?key=" + key);
            //url = new URL(urlFusionT + commandQuery+"?"  + urlParameters);
            //url = new URL(urlFusionT + commandQuery);
            //url = new URL(urlFusionT + commandTables );
        } catch (IOException e) {
            e.printStackTrace();
            Depuracion.traza(this.getClass().getName() + " Excepcion e: " + e);
        }


        byte[] postData       = urlParameters.getBytes();

        try {
            //con = (HttpURLConnection)urls[0].openConnection();
            con = (HttpURLConnection)url.openConnection();
            //con.setRequestMethod("POST");
            con.setRequestMethod("GET");

            //con.setDoOutput(true);  // HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has been called.
                                    // Other HTTP methods (OPTIONS, HEAD, PUT, DELETE and TRACE) can be used with setRequestMethod(String)

            //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //con.setRequestProperty("charset", "utf-8");

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(urlParameters);
            writer.flush();
            writer.close();
            os.close();
            int statusCode = con.getResponseCode();
            Depuracion.traza("ResponseMessage(): " + con.getResponseMessage());
            Depuracion.traza("StatusCode "+ statusCode + ": " +statusCodeTools.code2str(statusCode));

            if((statusCode!=200)&&(statusCode!=201)&&(statusCode!=400)&&(statusCode!=401)) {
                comments = new ArrayList();
                comments.add("ERROR: el recurso no está disponible");
                return comments;
            }
            else{
                InputStream in = con.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(in);
                int read = 0;
                int bufSize = 512;
                StringBuffer response2 = new StringBuffer();
                byte[] buffer = new byte[bufSize];
                while(true){
                    read = bis.read(buffer);
                    String sTemp = new String(buffer);
                    if(read==-1){
                        break;
                    }
                    response2.append(sTemp);
                }

                JSONObject jObj;
                try {
                    jObj = new JSONObject(response2.toString());
                    readJSONObj_2(jObj);
                    readJSONObj(jObj);
                    parseJson(jObj);

                } catch (JSONException e) {
                    Depuracion.traza("JSONException en ... " + e);
                }
            }

        } catch (Exception e) {
            Depuracion.traza(this.getClass().getName()+" Excepcion e: "+ e );
            e.printStackTrace();
        }


        finally {
            con.disconnect();
        }
        return comments;

    }

    @Override
    protected void onPostExecute(List s) {

        /* Se crea un adaptador con el el resultado del parsing
        que se realizó al arreglo JSON
         */
    }


    public void readJSONObj_2 (JSONObject obj) {
        Depuracion.traza("obj.toString(): " + obj.toString());
        try {
            for(int i = 0; i<obj.length(); i++){

                Depuracion.traza("Key = " + obj.names().getString(i) + " - " + obj.get(obj.names().getString(i)));
            }

        } catch (JSONException e) {
            Depuracion.traza(this.getClass().getName()+" Excepcion e: "+ e );
        }
    }

    public void readJSONObj (JSONObject obj) {
        try {
            for(int i = 0; i<obj.length(); i++){
                Depuracion.traza("Key = " + obj.names().getString(i) + " - " + obj.get(obj.names().getString(i)));
            }

        } catch (JSONException e) {
            Depuracion.traza(this.getClass().getName()+" Excepcion e: "+ e );
        }
    }

    private void parseJson(JSONObject data) {

        if (data != null) {
            Iterator<String> it = data.keys();
            Depuracion.traza("Analisis de parseJson");
            while (it.hasNext()) {
                String key = it.next();

                try {
                    if (data.get(key) instanceof JSONArray) {
                        JSONArray arry = data.getJSONArray(key);
                        int size = arry.length();
                        for (int i = 0; i < size; i++) {
                            parseJson(arry.getJSONObject(i));
                        }
                    } else if (data.get(key) instanceof JSONObject) {
                        parseJson(data.getJSONObject(key));
                    } else {
                        Depuracion.traza("" + key + " : " + data.optString(key));
                    }
                } catch (Throwable e) {
                    Depuracion.traza(this.getClass().getName()+" Excepcion e: "+ e );
                    Depuracion.traza("" + key + " : " + data.optString(key));
                    e.printStackTrace();

                }
            }
        }
    }


}


