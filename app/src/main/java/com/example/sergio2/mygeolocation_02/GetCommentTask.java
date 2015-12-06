package com.example.sergio2.mygeolocation_02;

import android.os.AsyncTask;
import com.sergio2.auxiliares.Depuracion;
import com.sergio2.auxiliares.statusCodeTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sergio2 on 25/11/2015.
 */

//Las tres variables de entrada que posee se refieren a los Parámetros, Unidades de Progreso y Resultados
public class GetCommentTask extends AsyncTask<URL, Void, List<String>> {
    HttpURLConnection con = null;

    @Override
    protected List doInBackground(URL... urls) {
        List comments = null;
        try {
            // Establecer la conexión
            con = (HttpURLConnection)urls[0].openConnection();
            con.setRequestMethod("GET");
            // Obtener el estado del recurso y saca mensaje
            int statusCode = con.getResponseCode();
            Depuracion.traza("StatusCode "+ statusCode + ": " + statusCodeTools.code2str(statusCode));

            if((statusCode!=200)&&(statusCode!=201)) {
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

                    readJSONObj(jObj);
                    parseJson(jObj);

                    /*JSONArray json_array = jObj.optJSONArray("items");
                    for (int i = 0; i < json_array.length(); i++) {
                        Depuracion.traza(" - "+ json_array.getJSONObject(i).getString("kind")
                                + " " + json_array.getJSONObject(i).getString("columnId")
                                + " " + json_array.getJSONObject(i).getString("name")
                                + " " + json_array.getJSONObject(i).getString("type")
                                + " " + json_array.getJSONObject(i).getString("formatPattern")
                                + " " + json_array.getJSONObject(i).getString("validateData"));
                    }*/
                } catch (JSONException e) {
                    Depuracion.traza("JSONException en ... " + e);
                }
            }

        } catch (Exception e) {
            Depuracion.traza(this.getClass().getName()+" Excepcion e: "+ e );
            e.printStackTrace();

        }finally {
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


