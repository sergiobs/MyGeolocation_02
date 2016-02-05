package com.example.sergio2.mygeolocation_02;


//AIzaSyDXgqOwipSCHNlCMevBwVnlRgXkhV0a1DY apy key de depuracion (Android key 1)


//AIzaSyA4s027KNXRNhff5YAzVWleLNb1GpmEtug api key de release (keysbs.jks_20151105)


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sergio2.auxiliares.Depuracion;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

//public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
public class MainActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    //public class MainActivity extends FragmentActivity implements View.OnClickListener  {
    // CONSTANTES
    int T_MIN_INICIAL = 2;
    int DIST_MIN_INICIAL = 15;
    float ZOOM_INICIAL = 17;

    // pongo esto de SignInActivity.java
    int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";


    // Display
    int display_res_Ancho = 0;
    int display_res_Alto = 0;
    int display_densidad = 0;

    // Entorno GRAFICO
    TextView textLat, textLong, textPrec;
    TextView textSpeed;
    TextView textUser;
    EditText editText_T_Min, editText_Dist_Min;
    TextView textDepurador, textDepuradorMap;

    ToggleButton btnActivar_loc;
    Button btnTab_Dep, btnTab_GPS, btnTab_Aux, btnTab_Frame;
    Button btnClearMarker;

    SignInButton signInButton;
    Button signOutButton;

    ImageView image_status_gps, image_status_network, image_status_passive;

    ToggleButton btnPosition;

    TableLayout contentTable;

    LinearLayout layoutTabla;
    LinearLayout layoutDepurador;
    LinearLayout layoutMap;
    MapFragment mapFragment;

    //VARIALBLES
    boolean enabledProviderGPS = false;
    boolean enabledProviderNetwork = false;
    boolean enabledProviderPassive = false;
    boolean avaliableProviderGPS = false;
    boolean avaliableProviderNetwork = false;
    boolean avaliableProviderPassive = false;
    boolean tabDep = true;
    boolean tabGPS = false;
    boolean tabAux = false;
    boolean tabFrame = false;
    boolean GpsStatusListener_Enabled = false;
    boolean status_Activado = false;

    LatLng CasaNueva;
    float zoomActual = ZOOM_INICIAL;

    GoogleMap mapaSBS;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;

    LocationManager lm;
    LocationListener locationListenerPassive, locationListenerGPS, locationListenerNetwork;
    GpsStatus gpsStatus = null;
    Iterable<GpsSatellite> satellites;
    Iterator<GpsSatellite> sat;
    GpsStatus.Listener gpsStatusListener = null;
    List<String> listProviders;

    PostCommentTask tareaPost2;
    URL url2, url_query_01, url_query_02, url_query_03, url_query_04, url_query_05;
    String idToken;

    PowerManager.WakeLock wl;

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        Depuracion.traza("onStart().opr.isDone(): " + opr.isDone(), textDepurador);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        Depuracion.traza("onStop()", textDepurador);
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (GpsStatusListener_Enabled) {
            lm.removeGpsStatusListener(gpsStatusListener);
            GpsStatusListener_Enabled = false;
            Depuracion.traza("onDestroy().GpsStatus_listener eliminado", textDepurador);
        }
        if (status_Activado) {
            wl.release();
            try {
                lm.removeUpdates(locationListenerPassive);
            } catch (SecurityException e) {
                Depuracion.traza("onDestroy().Security Excepcion en provider PASSIVE_PROVIDER al hacer removeUpdates: " + e);
            }
            try {
                lm.removeUpdates(locationListenerGPS);
            } catch (SecurityException e) {
                Depuracion.traza("onDestroy().Security Excepcion en provider GPS_PROVIDER al hacer removeUpdates: " + e);
            }
            try {
                lm.removeUpdates(locationListenerNetwork);
            } catch (SecurityException e) {
                Depuracion.traza("onDestroy().Security Excepcion en provider NETWORK_PROVIDER al hacer removeUpdates: " + e);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Depuracion.traza("server_client_id " + getString(R.string.server_client_id));

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                //.requestScopes(SCOPE.)
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                //.addScope()
                .build();

        iniciaGUI();

        // Conexion a internet, fusiontable
        // --------------------------------------------------------
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); //las características actuales de la conexión
        if (networkInfo != null && networkInfo.isConnected()) {
            Depuracion.traza("onCreate(). Hay conexion a internet ", textDepurador);
        } else {
            Depuracion.traza("onCreate(). ERROR, no hay conexion  a internetv ", textDepurador);
        }

        String urlFusionT = "https://www.googleapis.com/fusiontables/v2/";
        String urlFusionT_v1 = "https://www.googleapis.com/fusiontables/v1/";
        String key = "AIzaSyB-C74mt75P7YcE9_RxXyDCNekBAQ-tGMk";
        String tableId = "1CBP-Ht9blF_PuPXNWjAajjRfEZUbNeCxuQ5WvMbJ";
        String resourceID = "tables";
        String commandQuery_SQL = "query?sql=";
        String commandQuery = "query";
        String Query_01 = "SELECT * FROM tableID".replaceAll(" ", "%20").replaceAll("tableID", tableId);
        String Query_02 = "SELECT ROWID FROM tableID".replaceAll(" ", "%20").replaceAll("tableID", tableId);
        String Query_03 = "SELECT Text, Number, Location, Date FROM tableID".replaceAll(" ", "%20").replaceAll("tableID", tableId);
        String Query_04 = "INSERT INTO tableID (Text, Number) VALUES ('f', '6')".replaceAll(" ", " ").replaceAll("tableID", tableId);

        GetCommentTask tareaGet2 = new GetCommentTask();
        //PostCommentTask tareaPost2 = new PostCommentTask();
        tareaPost2 = new PostCommentTask();

        try {
            url2 = new URL(urlFusionT + resourceID + "/" + tableId + "/" + "columns?key=" + key);
            url_query_01 = new URL(urlFusionT + commandQuery_SQL + Query_01 + "&key=" + key);
            url_query_02 = new URL(urlFusionT + commandQuery_SQL + Query_02 + "&key=" + key);
            url_query_03 = new URL(urlFusionT + commandQuery_SQL + Query_03 + "&key=" + key);
            url_query_04 = new URL(urlFusionT + commandQuery);
            url_query_05 = new URL(urlFusionT + resourceID+"?key="+key);
            //tareaGet2.execute(url_query_05);

            //tareaPost2.execute("xxxxx idToken");

        } catch (IOException e) {
            e.printStackTrace();
            Depuracion.traza(this.getClass().getName() + " Excepcion e: " + e);
        } finally {
        }

        // --------------------------------------------------------

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ArrayList<String[]> content = new ArrayList<String[]>();
        gpsStatusListener = new myGpsStatusListener(textDepurador, content, this);

        String[] cabecera = {" PRN ", " InFix ", " SNR ", "  Azim  ", "  Elev  "};

        TableRow rowCabecera = new TableRow(this);
        //for (int j = 0; j < content.get(0).length; j++) {
        for (int j = 0; j < cabecera.length; j++) {
            TextView rowCell = new TextView(this);
            rowCell.setText(cabecera[j]);
            rowCell.setTextColor(Color.parseColor("#CCCCCC"));
            rowCabecera.addView(rowCell);
        }
        contentTable.addView(rowCabecera);

        for (int i = 0; i < content.size(); i++) {
            TableRow row = new TableRow(this);

            for (int j = 0; j < content.get(0).length; j++) {
                TextView rowCell = new TextView(this);
                rowCell.setText(content.get(i)[j]);
                rowCell.setTextColor(Color.parseColor("#999999"));
                row.addView(rowCell);
                //Depuracion.traza(rowCell.getText()+"");
            }
            contentTable.addView(row);
        }

        locationListenerGPS = new mylocationlistener();
        locationListenerNetwork = new mylocationlistener();
        locationListenerPassive = new mylocationlistener();

        ProvidersToolsSBS.printInfoProviders2(lm, textDepurador);
        checkInitialAvailableProvider();
        checkEnabledProvider();
        actualizaGUI_Providers();
        actualizaGUI();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggleActivar_loc:
                status_Activado = btnActivar_loc.isChecked();
                if (status_Activado) {
                    wl.acquire();
                    long t_min = 1000 * Long.parseLong(String.valueOf(editText_T_Min.getText()).toString());
                    float dist_min = Float.parseFloat(String.valueOf(editText_Dist_Min.getText()).toString());

                    try {
                        GpsStatusListener_Enabled = lm.addGpsStatusListener(gpsStatusListener);
                        if (GpsStatusListener_Enabled)
                            Depuracion.traza("añade el gpsStatusListener", textDepurador);
                    }catch (SecurityException e) {
                        GpsStatusListener_Enabled = false;
                        Depuracion.traza("Casca el gpsStatusListener", textDepurador);
                    }

                    if (avaliableProviderPassive) {
                        try {
                            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, t_min, dist_min, locationListenerPassive);     //nos subscribimos al proveedor para que nos de información
                        } catch (SecurityException e) {
                            Depuracion.traza("Security Excepcion en provider PASSIVE_PROVIDER al hacer requestLocationUpdates: " + e);
                        }
                    }
                    if (avaliableProviderGPS) {
                        try {
                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, t_min, dist_min, locationListenerGPS);     //nos subscribimos al proveedor para que nos de información
                        } catch (SecurityException e) {
                            Depuracion.traza("Security Excepcion en provider GPS_PROVIDER al hacer requestLocationUpdates: " + e);
                        }
                    }
                    if (avaliableProviderNetwork) {
                        try {
                            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, t_min, dist_min, locationListenerNetwork);     //nos subscribimos al proveedor para que nos de información
                        } catch (SecurityException e) {
                            Depuracion.traza("Security Excepcion en provider NETWORK_PROVIDER al hacer requestLocationUpdates: " + e);
                        }
                    }
                } else  {
                    wl.release();
                    if (GpsStatusListener_Enabled) {
                        lm.removeGpsStatusListener(gpsStatusListener);
                        GpsStatusListener_Enabled = false;
                        Depuracion.traza("GpsStatus_listener eliminado", textDepurador);
                    }

                    if (avaliableProviderPassive) {
                        try {
                            lm.removeUpdates(locationListenerPassive);
                        } catch (SecurityException e) {
                            Depuracion.traza("Security Excepcion en provider PASSIVE_PROVIDER al hacer removeUpdates: " + e);
                        }
                    }
                    if (avaliableProviderGPS) {
                        try {
                            lm.removeUpdates(locationListenerGPS);
                        } catch (SecurityException e) {
                            Depuracion.traza("Security Excepcion en provider GPS_PROVIDER al hacer removeUpdates: " + e);
                        }
                    }
                    if (avaliableProviderNetwork) {
                        try {
                            lm.removeUpdates(locationListenerNetwork);
                        } catch (SecurityException e) {
                            Depuracion.traza("Security Excepcion en provider NETWORK_PROVIDER al hacer removeUpdates: " + e);
                        }
                    }
                }
                checkEnabledProvider();
                actualizaGUI_Providers();
                break;

            case R.id.buttonDep:
                if (!tabDep) {
                    tabDep = true;
                    tabGPS = false;
                    tabAux = false;
                    tabFrame = false;
                }
                actualizaGUI();
                break;
            case R.id.buttonGPS:
                if (!tabGPS) {
                    tabDep = false;
                    tabGPS = true;
                    tabAux = false;
                    tabFrame = false;
                }
                actualizaGUI();
                break;
            case R.id.buttonAux:
                //prueba con FT
                Depuracion.traza("Probando escritura a FT desde AUX", textDepurador);

               // tareaPost2.execute(url_query_04);



                break;
            case R.id.buttonFrame:
                if (!tabFrame) {
                    tabDep = false;
                    tabGPS = false;
                    tabAux = false;
                    tabFrame = true;
                }
                actualizaGUI();
                break;

            case R.id.sign_out_button:
                signOut();
                break;

            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.buttonClrMarker:
                if (mapaSBS!=null) {
                    mapaSBS.clear();
                }
                break;
            case R.id.togglePosition:
                if (mapaSBS!=null) {
                    if (btnPosition.isChecked()) {
                        mapaSBS.setMyLocationEnabled(true);
                    }else {
                        mapaSBS.setMyLocationEnabled(false);
                    }
                }
                break;
        }
    }


    private void signIn() {
        Depuracion.traza("signin()", textDepurador);
        //Starting the intent prompts the user to select a Google account to sign in with.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // [START signOut]
    private void signOut() {
        Depuracion.traza("signout() mGoogleApiClient.isConnected:" + mGoogleApiClient.isConnected(), textDepurador);
        if (mGoogleApiClient.isConnected() ) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            updateUI(false);
                            // [END_EXCLUDE]
                        }
                    });

        } else {

        }
        Depuracion.traza("signout() mGoogleApiClient.isConnected despues:" + mGoogleApiClient.isConnected(), textDepurador);

    }
    // [END signOut]

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                        // ...
                    }
                });
    }
//
//    @Override
//    public void onConnected(Bundle connectionHint) {
//        // Connected to Google Play services!
//        // The good stuff goes here.
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        // The connection has been interrupted.
//        // Disable any UI components that depend on Google APIs
//        // until onConnected() is called.
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Depuracion.traza("onConnectionFailed:" + connectionResult, textDepurador);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        Depuracion.traza("onActivityResult().requestCode: " + requestCode, textDepurador);
        Depuracion.traza("onActivityResult().resultCode: " + resultCode, textDepurador);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            // findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            textUser.setText("signed out");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Depuracion.traza("handleSignInResult.result.isSuccess(): " + result.isSuccess(), textDepurador);

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Depuracion.traza("handleSignInResult: " + acct.getEmail()
                    + ", " + acct.getDisplayName()
                    + ", " + acct.getId()

                    , textDepurador);

            idToken = acct.getIdToken();

            Depuracion.traza("mGoogleApiClient.isConnected(): "+ mGoogleApiClient.isConnected(), textDepurador);


                    Depuracion.traza("acct.getServerAuthCode(): " + acct.getServerAuthCode());

            Depuracion.traza("ID Token: " + idToken, textDepurador);
///            tareaPost2.execute(idToken);
            // TODO(user): send token to server and validate server-side
            textUser.setText(acct.getDisplayName());
            updateUI(true);
        } else {
            textUser.setText("KO");
            Depuracion.traza("ID Token: null", textDepurador);
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapaSBS = googleMap;
        mapaSBS.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CasaNueva = new LatLng(40.382137, -80.053961);
//        Bombardier = new LatLng(40.347280, -79.957230);
//
//        mapaSBS.addMarker(new MarkerOptions().position(Bombardier).title("Example Marker in Bombardier")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//        mapaSBS.addMarker(new MarkerOptions().position(CasaNueva).title("Example Marker in CasaNueva")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLng(CasaNueva);
        mapaSBS.moveCamera(camUpd1);

        CameraPosition camPos1 = new CameraPosition.Builder()
                .target(CasaNueva)
                .zoom(ZOOM_INICIAL)
                .bearing(0)
                .tilt(0)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos1);
        mapaSBS.animateCamera(camUpd3);

        mapaSBS.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                //Projection proj = mapaSBS.getProjection();
                //Point coord = proj.toScreenLocation(point);
                Depuracion.traza("Click: " + point.latitude + ", " + point.longitude, textDepurador, textDepuradorMap);
            }
        });

        mapaSBS.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {

            }
        });



        mapaSBS.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                String sLat = Double.toString(position.target.latitude);
                String sLong = Double.toString(position.target.longitude);
                zoomActual = position.zoom;

                if (sLong.length() > 10) sLong = sLong.substring(0,10);
                if (sLat.length() > 10) sLat = sLat.substring(0,10);


                //Depuracion.traza("Cambio: " + sLat + ", " + sLong + ", " +
                //        position.zoom + ", " + position.bearing + ", " + position.tilt, textDepurador, textDepuradorMap);
            }
        });

        mapaSBS.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                Depuracion.traza("Marcador pulsado: " +
                        marker.getTitle(), textDepurador, textDepuradorMap);
                return false;
            }
        });

        //mostrarLineas();
    }

    class myGpsStatusListener implements GpsStatus.Listener {
        TextView tv_Fuera;
        ArrayList<String[]> respuestaListener = null;
        Context contexto_dentro;
        String[] sEvent = { "GPS_EVENT_STARTED", "GPS_EVENT_STOPPED", "GPS_EVENT_FIRST_FIX", "GPS_EVENT_SATELLITE_STATUS" };
        int TABLE_TEXT_SIZE = 18;


        //ArrayList<String[]> respuestaListener = new ArrayList<String[]>();
        public myGpsStatusListener(TextView tv, ArrayList respuesta, Context contexto) {
            textDepurador.setText("dentro del constructor del myGpsStatusListener");
            String[] _fila1 = { "xB1", "xB2", "xB3", "xB4", "xB5"  };
            contexto_dentro = contexto;
            respuesta.add(_fila1);
        }

        public void onGpsStatusChanged(int event) {

            gpsStatus = lm.getGpsStatus(gpsStatus);
            satellites = gpsStatus.getSatellites();

            sat = satellites.iterator();
            contentTable.removeAllViews();

            int i=0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                TableRow row = new TableRow(contexto_dentro);
                TextView rowCell1 = new TextView(contexto_dentro);
                TextView rowCell2 = new TextView(contexto_dentro);
                TextView rowCell3 = new TextView(contexto_dentro);
                TextView rowCell4 = new TextView(contexto_dentro);
                TextView rowCell5 = new TextView(contexto_dentro);

                rowCell1.setText(satellite.getPrn() + "   ");
                rowCell1.setTextColor(Color.parseColor("#CCCCCC"));
                rowCell1.setTextSize(TABLE_TEXT_SIZE);
                row.addView(rowCell1);
                rowCell2.setText(satellite.usedInFix() + "   ");
                rowCell2.setTextColor(Color.parseColor("#CCCCCC"));
                rowCell2.setTextSize(TABLE_TEXT_SIZE);
                row.addView(rowCell2);
                rowCell3.setText(satellite.getSnr() + "   (");
                rowCell3.setTextColor(Color.parseColor("#CCCCCC"));
                rowCell3.setTextSize(TABLE_TEXT_SIZE);
                row.addView(rowCell3);
                rowCell4.setText(satellite.getAzimuth() + ", ");
                rowCell4.setTextColor(Color.parseColor("#CCCCCC"));
                rowCell4.setTextSize(TABLE_TEXT_SIZE);
                row.addView(rowCell4);
                rowCell5.setText(satellite.getElevation() + ")");
                rowCell5.setTextColor(Color.parseColor("#CCCCCC"));
                rowCell5.setTextSize(TABLE_TEXT_SIZE);
                row.addView(rowCell5);
                contentTable.addView(row);
                i++;
            }

            switch (event) {
                case 1: //GPS_EVENT_STARTED
                    Depuracion.traza("onGpsStatusChanged." + sEvent[event-1], textDepurador);
                    break;
                case 2: //GPS_EVENT_STOPPED
                    Depuracion.traza("onGpsStatusChanged." + sEvent[event-1], textDepurador);
                    break;
                case 3: //GPS_EVENT_FIRST_FIX
                    Depuracion.traza("onGpsStatusChanged." + sEvent[event-1]+" getTimeToFirstFix: "+ gpsStatus.getTimeToFirstFix(), textDepurador);
                    break;
                case 4: //GPS_EVENT_SATELLITE_STATUS<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    break;
            }
        }
    }

    public void iniciaGUI() {
        textLat = (TextView) findViewById(R.id.textLat);
        textLong = (TextView) findViewById(R.id.textLong);
        textPrec = (TextView) findViewById(R.id.textPrecision);
        textSpeed = (TextView) findViewById(R.id.textSpeed);

        textUser = (TextView) findViewById(R.id.textUser);

        textDepurador = (TextView) findViewById(R.id.textDepurador);
        textDepurador.setMovementMethod(new ScrollingMovementMethod());
        textDepuradorMap = (TextView) findViewById(R.id.textDepuradorMap);
        textDepuradorMap.setMovementMethod(new ScrollingMovementMethod());

        image_status_gps = (ImageView) findViewById(R.id.image_status_gps);
        image_status_network = (ImageView) findViewById(R.id.image_status_network);
        image_status_passive = (ImageView) findViewById(R.id.image_status_passive);

        editText_T_Min = (EditText) findViewById(R.id.editText_T_min);
        editText_Dist_Min = (EditText) findViewById(R.id.editText_Dist_min);
        editText_T_Min.setText(Integer.toString(T_MIN_INICIAL));
        editText_Dist_Min.setText(Integer.toString(DIST_MIN_INICIAL));
        editText_T_Min.clearFocus();
        editText_Dist_Min.clearFocus();

        btnActivar_loc = (ToggleButton) findViewById(R.id.toggleActivar_loc);
        btnTab_Dep = (Button) findViewById(R.id.buttonDep);
        btnTab_GPS = (Button) findViewById(R.id.buttonGPS);
        btnTab_Aux = (Button) findViewById(R.id.buttonAux);
        btnTab_Frame = (Button) findViewById(R.id.buttonFrame);
        btnClearMarker = (Button) findViewById(R.id.buttonClrMarker);
        btnPosition = (ToggleButton) findViewById(R.id.togglePosition);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setText("Sign out");
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //signInButton.setSize(SignInButton.SIZE_WIDE);
        //signInButton.setSize(SignInButton.SIZE_ICON_ONLY);
        signInButton.setScopes(gso.getScopeArray());

        btnTab_Dep.setOnClickListener(this);
        btnTab_GPS.setOnClickListener(this);
        btnTab_Aux.setOnClickListener(this);
        btnTab_Frame.setOnClickListener(this);
        btnActivar_loc.setOnClickListener(this);
        btnClearMarker.setOnClickListener(this);
        btnPosition.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        contentTable = (TableLayout)findViewById(R.id.contentTable);

        layoutDepurador = (LinearLayout)findViewById(R.id.layoutDepurador);
        layoutTabla = (LinearLayout)findViewById(R.id.layoutTabla);
        layoutMap = (LinearLayout)findViewById(R.id.layoutMap);

        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Obtiene datos de la pantalla. FORMA 1 ---------------------------------------------
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display_res_Ancho = display.getWidth();
        display_res_Alto  = display.getHeight();
        display_densidad  = getResources().getDisplayMetrics().densityDpi;

        Depuracion.traza("Screen Width " + Integer.toString(display_res_Ancho), textDepurador);
        Depuracion.traza("Screen High: " + Integer.toString(display_res_Alto), textDepurador);
        Depuracion.traza("Screen Density (dpi): " + display_densidad, textDepurador);
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        Depuracion.traza("Scale " + Float.toString(getApplicationContext()
                .getResources().getDisplayMetrics().density), textDepurador);

        // buscando los pixeles a partir de dips con la densidad
        int dips = 200;
        float pixelBoton = 0;
        float scaleDensity = 0;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        switch(metrics.densityDpi)
        {
            case DisplayMetrics.DENSITY_HIGH: //HDPI
                Depuracion.traza("DENSITY_HIGH", textDepurador);
                scaleDensity = scale * 240;
                pixelBoton = dips * (scaleDensity / 240);
                break;
            case DisplayMetrics.DENSITY_MEDIUM: //MDPI
                Depuracion.traza("DENSITY_MEDIUM", textDepurador);
                scaleDensity = scale * 160;
                pixelBoton = dips * (scaleDensity / 160);
                break;

            case DisplayMetrics.DENSITY_LOW:  //LDPI
                Depuracion.traza("DENSITY_LOW", textDepurador);
                scaleDensity = scale * 120;
                pixelBoton = dips * (scaleDensity / 120);
                break;
        }
        Depuracion.traza("scaleDensity: " + scaleDensity, textDepurador);
        Depuracion.traza("pixelBoton: " + pixelBoton, textDepurador);
        Log.d(getClass().getSimpleName(), "pixels:" + Float.toString(pixelBoton));
        /////////////////////////////////////////////////////////////////////////////////////

        // Obtiene datos de la pantalla. FORMA 2 ---------------------------------------------
        Resources resources = getResources();
        int dips2 = 200;
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips2, resources.getDisplayMetrics());
        Depuracion.traza("Forma 2. Pixels: " + pixels, textDepurador);
        ///////////////////////////////////////////


        // hago alguna adaptación en base a la resolución
        float GUI_TEXTO_VISOR_GRANDE = 25f;
        float GUI_TEXTO_DEP_GRANDE = 15f;
        float GUI_TEXTO_BOT_GRANDE = 20f;
        //float GUI_TEXTO_LABEL_L3_GRANDE = 18f;

        float GUI_TEXTO_VISOR_PEQ = 15f;
        float GUI_TEXTO_DEP_PEQ = 11f;
        float GUI_TEXTO_BOT_PEQ = 15f;
        //float GUI_TEXTO_LABEL_L3_PEQ = 11f;



        if (display_res_Ancho>=800) {
            editText_T_Min.setTextSize(GUI_TEXTO_VISOR_GRANDE);
            editText_Dist_Min.setTextSize(GUI_TEXTO_VISOR_GRANDE);
            textLat.setTextSize(GUI_TEXTO_VISOR_GRANDE);
            textLong.setTextSize(GUI_TEXTO_VISOR_GRANDE);
            textPrec.setTextSize(GUI_TEXTO_VISOR_GRANDE);
            textSpeed.setTextSize(GUI_TEXTO_VISOR_GRANDE);

            textDepurador.setTextSize(GUI_TEXTO_DEP_GRANDE);
            textDepuradorMap.setTextSize(GUI_TEXTO_DEP_GRANDE);

            btnActivar_loc.setTextSize(GUI_TEXTO_BOT_GRANDE);
            btnTab_Aux.setTextSize(GUI_TEXTO_BOT_GRANDE);
            btnTab_Frame.setTextSize(GUI_TEXTO_BOT_GRANDE);
            btnTab_Dep.setTextSize(GUI_TEXTO_BOT_GRANDE);
            btnTab_GPS.setTextSize(GUI_TEXTO_BOT_GRANDE);
            btnPosition.setTextSize(GUI_TEXTO_BOT_GRANDE);
            btnClearMarker.setTextSize(GUI_TEXTO_BOT_GRANDE);

            Depuracion.traza("Resolucion W >= 800: " + pixels, textDepurador);
        }

        else if (display_res_Ancho<800) {
            editText_T_Min.setTextSize(GUI_TEXTO_VISOR_PEQ);
            editText_Dist_Min.setTextSize(GUI_TEXTO_VISOR_PEQ);
            textLat.setTextSize(GUI_TEXTO_VISOR_PEQ);
            textLong.setTextSize(GUI_TEXTO_VISOR_PEQ);
            textPrec.setTextSize(GUI_TEXTO_VISOR_PEQ);
            textSpeed.setTextSize(GUI_TEXTO_VISOR_PEQ);

            textDepurador.setTextSize(GUI_TEXTO_DEP_PEQ);
            textDepuradorMap.setTextSize(GUI_TEXTO_DEP_PEQ);

            btnActivar_loc.setTextSize(GUI_TEXTO_BOT_PEQ);
            btnTab_Aux.setTextSize(GUI_TEXTO_BOT_PEQ);
            btnTab_Frame.setTextSize(GUI_TEXTO_BOT_PEQ);
            btnTab_Dep.setTextSize(GUI_TEXTO_BOT_PEQ);
            btnTab_GPS.setTextSize(GUI_TEXTO_BOT_PEQ);
            btnPosition.setTextSize(GUI_TEXTO_BOT_PEQ);
            btnClearMarker.setTextSize(GUI_TEXTO_BOT_PEQ);
            Depuracion.traza("Resolucion W < 800: " + pixels, textDepurador);
        }
    }

    class mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(location !=null){
                DecimalFormat df_vel = new DecimalFormat("###");
                DecimalFormat df_latlong = new DecimalFormat("##.#####");
                String markerText="";
                String labelColor="WHITE";
                double pLong = location.getLongitude();
                double pLat = location.getLatitude();
                double pPrec = location.getAccuracy();
                float pSpeed = location.getSpeed();
                String pProvider = location.getProvider(); //gps, passive, network

                String sLong = df_latlong.format(pLat);
                String sLat = df_latlong.format(pLong);
                String sPrec = df_vel.format(pPrec);
                String sSpeed = df_vel.format(pSpeed * 2.2369f);   // en mph

                String sProvider = pProvider.substring(0,1);

                switch(pProvider) {
                    case "gps":
                        markerText = DateFormat.getTimeInstance().format(new Date())+": "+ sPrec+ "v: "+sSpeed+" "+sProvider;
                        labelColor = "GREEN";
                        break;
                    case "network":
                        markerText = DateFormat.getTimeInstance().format(new Date())+": "+ sPrec+ "v: "+sSpeed+" "+sProvider;
                        labelColor = "YELLOW";;
                        sSpeed="-1.0";
                        break;
                    case "passive":
                        markerText = DateFormat.getTimeInstance().format(new Date())+": "+ sPrec+ "v: "+sSpeed+" "+sProvider;
                        labelColor = "BLUE";
                        sSpeed="-1.0";
                        break;
                }

                textLat.setText(sLong);
                textLong.setText(sLat);
                textPrec.setText(sPrec);
                textSpeed.setText(sSpeed);

                textLat.setTextColor(Color.parseColor(labelColor));
                textLong.setTextColor(Color.parseColor(labelColor));
                textPrec.setTextColor(Color.parseColor(labelColor));
                textSpeed.setTextColor(Color.parseColor(labelColor));

                Depuracion.traza("onLocationChanged: " + sLong + "," + sLat + ", " + sPrec + ", " +sSpeed +", " + sProvider, textDepurador);

                if (mapaSBS!=null) {
                    LatLng Actual = new LatLng(pLat, pLong);

                    CameraPosition camPos_actual = new CameraPosition.Builder().target(Actual)
                            .zoom(zoomActual)
                            .bearing(0).tilt(0).build();
                    CameraUpdate camUpd32 = CameraUpdateFactory.newCameraPosition(camPos_actual);
                    mapaSBS.moveCamera(camUpd32);

                    switch(pProvider) {
                        case "gps":
                            mapaSBS.addMarker(new MarkerOptions().position(Actual).title(markerText)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            break;
                        case "network":
                            mapaSBS.addMarker(new MarkerOptions().position(Actual).title(markerText)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            break;
                        case "passive":
                            mapaSBS.addMarker(new MarkerOptions().position(Actual).title(markerText)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            break;
                    }

                } else {
                    Depuracion.traza("onLocationChanged. mapaSBS = null", textDepurador);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Depuracion.traza("onStatusChanged: provider=" + provider + ", status=" + status, textDepurador);
            actualizaGUI_Providers();
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (provider.equals("gps")) {
                enabledProviderGPS = true;
            } else if (provider.equals("network")) {
                enabledProviderNetwork = true;
            } else if (provider.equals("passive")){
                enabledProviderPassive = true;
            }
            Depuracion.traza("onProviderEnabled() "+ provider, textDepurador);
            actualizaGUI_Providers();
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (provider.equals("gps")) {
                enabledProviderGPS = false;
            } else if (provider.equals("network")) {
                enabledProviderNetwork = false;
            } else if (provider.equals("passive")) {
                enabledProviderPassive = false;
            }
            Depuracion.traza("onProviderDisabled() "+ provider, textDepurador);
            actualizaGUI_Providers();
        }
    }

    public void checkEnabledProvider() {
        //sólo se chequea en el inicio ya que luego se actualizan los estasdos de los boolean
        //con onProviderEnabled y onProviderDisabled
        if (avaliableProviderGPS)
            enabledProviderGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (avaliableProviderNetwork)
            enabledProviderNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (avaliableProviderPassive)
            enabledProviderPassive = lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
    }

    public void checkInitialAvailableProvider() {
        // para saber si existe el HW de cada uno de los tres posibles provider
        listProviders = lm.getAllProviders();
        avaliableProviderGPS = listProviders.contains("gps");
        avaliableProviderNetwork = listProviders.contains("network");
        avaliableProviderPassive = listProviders.contains("passive");
        //Depuracion.traza("xxx: " + avaliableProviderGPS + " " + avaliableProviderNetwork + " " + avaliableProviderPassive);
    }



    public void actualizaGUI_Providers() {
//
        if (!avaliableProviderGPS) {
            image_status_gps.setImageResource(R.drawable.gps_24_grey_crossed);
        } else if ( (avaliableProviderGPS)&&(!status_Activado) ||
                    (avaliableProviderGPS)&&(status_Activado)&&(!enabledProviderGPS)
                ) {
            image_status_gps.setImageResource(R.drawable.gps_24_grey);
        } else if ((avaliableProviderGPS)&&(status_Activado)&&(enabledProviderGPS)) {
            image_status_gps.setImageResource(R.drawable.gps_24_green);
        } else {
            image_status_gps.setImageResource(R.drawable.icon_dot_red_small);
        }

        if (!avaliableProviderPassive) {
            image_status_passive.setImageResource(R.drawable.radio_24_grey_crossed);
        } else if ( (avaliableProviderPassive)&&(!status_Activado) ||
                    (avaliableProviderPassive)&&(status_Activado)&&(!enabledProviderPassive)
                ) {
            image_status_passive.setImageResource(R.drawable.radio_24_grey);
        } else if ((avaliableProviderPassive)&&(status_Activado)&&(enabledProviderPassive)) {
            image_status_passive.setImageResource(R.drawable.radio_24_green);
        } else {
            image_status_passive.setImageResource(R.drawable.icon_dot_red_small);
        }

        if (!avaliableProviderNetwork) {
            image_status_network.setImageResource(R.drawable.wifi_24_grey_crossed);
        } else if ( (avaliableProviderNetwork)&&(!status_Activado) ||
                    (avaliableProviderNetwork)&&(status_Activado)&&(!enabledProviderNetwork)
                ) {
            image_status_network.setImageResource(R.drawable.wifi_24_grey);
        } else if ((avaliableProviderNetwork)&&(status_Activado)&&(enabledProviderNetwork)) {
            image_status_network.setImageResource(R.drawable.wifi_24_green);
        } else {
            image_status_network.setImageResource(R.drawable.icon_dot_red_small);
        }



    }

    public void actualizaGUI() {
        editText_T_Min.clearFocus();
        editText_Dist_Min.clearFocus();

        if (tabGPS) {
            btnTab_GPS.setBackgroundColor(Color.parseColor("#0000FF"));
            layoutTabla.setVisibility(View.VISIBLE);
        }else {
            btnTab_GPS.setBackgroundColor(Color.parseColor("#000066"));
            layoutTabla.setVisibility(View.INVISIBLE);
        }
        if (tabDep) {
            btnTab_Dep.setBackgroundColor(Color.parseColor("#0000FF"));
            layoutDepurador.setVisibility(View.VISIBLE);
        }else {
            btnTab_Dep.setBackgroundColor(Color.parseColor("#000066"));
            layoutDepurador.setVisibility(View.INVISIBLE);
        }
        if (tabAux) {
            btnTab_Aux.setBackgroundColor(Color.parseColor("#0000FF"));

        }else {
            btnTab_Aux.setBackgroundColor(Color.parseColor("#000066"));
        }
        if (tabFrame) {
            btnTab_Frame.setBackgroundColor(Color.parseColor("#0000FF"));
            layoutMap.setVisibility(View.VISIBLE);

        }else {
            btnTab_Frame.setBackgroundColor(Color.parseColor("#000066"));
            layoutMap.setVisibility(View.INVISIBLE);
        }



        if (status_Activado) {
            editText_T_Min.setEnabled (false);
            editText_Dist_Min.setEnabled(false);
            editText_T_Min.setTextColor(Color.parseColor("#777777"));
            editText_Dist_Min.setTextColor(Color.parseColor("#777777"));
        } else {
            editText_T_Min.setEnabled (true);
            editText_Dist_Min.setEnabled(true);
            editText_T_Min.setTextColor(Color.parseColor("#FFFFFF"));
            editText_Dist_Min.setTextColor(Color.parseColor("#FFFFFF"));            
        }
    }


}
