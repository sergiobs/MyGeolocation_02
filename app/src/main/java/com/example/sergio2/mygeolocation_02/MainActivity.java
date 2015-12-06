package com.example.sergio2.mygeolocation_02;


//AIzaSyDXgqOwipSCHNlCMevBwVnlRgXkhV0a1DY apy key de depuracion (Android key 1)


//AIzaSyA4s027KNXRNhff5YAzVWleLNb1GpmEtug api key de release (keysbs.jks_20151105)





import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
public class MainActivity extends Activity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
//public class MainActivity extends FragmentActivity implements View.OnClickListener  {
    // CONSTANTES
    int T_MIN_INICIAL = 2;
    int DIST_MIN_INICIAL = 15;
    float ZOOM_INICIAL = 17;

    // pongo esto de SignInActivity.java
    int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";


    // Display
    int display_res_Ancho=0;
    int display_res_Alto=0;
    int display_densidad=0;

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

    //signInButton.setScopes(gso.getScopeArray());



    ImageView image_avail_gps, image_avail_network, image_avail_passive;

    ToggleButton btnPosition;

    TableLayout contentTable;

    LinearLayout layoutTabla;
    LinearLayout layoutDepurador;
    LinearLayout layoutMap;
    MapFragment  mapFragment;

    //VARIALBLES
    boolean available_GPS       = false;
    boolean available_Network   = false;
    boolean available_Passive   = false;
    boolean tabDep              = true;
    boolean tabGPS              = false;
    boolean tabAux              = false;
    boolean tabFrame            = false;
    boolean locationListener_activo = false;
    boolean status_Activado = false;

    LatLng CasaNueva;
    LatLng Bombardier;
    float zoomActual = ZOOM_INICIAL;

    GoogleMap mapaSBS;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    LocationManager lm;
    LocationProvider lp0, lp1, lp2;
    LocationListener ll_2, ll_0, ll_1;
    List<String> listProviders;
    GpsStatus gpsStatus = null;
    Iterable<GpsSatellite> satellites;
    Iterator<GpsSatellite> sat;
    GpsStatus.Listener gpsStatus_listener=null;

    PowerManager.WakeLock wl;

    @Override
    protected void onStart() {
        super.onStart();
        Depuracion.traza("onStart()", textDepurador);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Depuracion.traza("onStart--> opr.isDone", textDepurador);
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            Depuracion.traza("onStart--> opr.is NOT Done", textDepurador);
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
           // showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                  //  hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
/*
    protected void onStop() {
        Depuracion.traza("onStop()", textDepurador);
    }*/

 /*   private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            //mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setMessage("... loading");
            mProgressDialog.setIndeterminate(true);
        }
        Depuracion.traza("showProgressDialog", textDepurador);
        mProgressDialog.show();
        Depuracion.traza("showProgressDialog, despues de", textDepurador);
    }

    private void hideProgressDialog() {
        Depuracion.traza("hideProgressDialog", textDepurador);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            Depuracion.traza("hideProgressDialog dentro del if", textDepurador);
            mProgressDialog.hide();
        }
    }*/

    protected void onDestroy(){
        super.onDestroy();

        Depuracion.traza("onDestroy()");
        if (locationListener_activo) {
            lm.removeGpsStatusListener(gpsStatus_listener);
            locationListener_activo = false;
            Depuracion.traza("GpsStatus_listener eliminado!!", textDepurador);
        }
        if (status_Activado) {
            wl.release();
            lm.removeUpdates(ll_2);
            lm.removeUpdates(ll_0);
            lm.removeUpdates(ll_1);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        iniciaGUI();

        // Conexion a internet, fusiontable
        // --------------------------------------------------------
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); //las características actuales de la conexión
        if (networkInfo != null && networkInfo.isConnected()) {
            Depuracion.traza("Hay conexion a internet ", textDepurador);
        } else {
            Depuracion.traza("ERROR : No hay conexion  a internet", textDepurador);
        }

        String urlFusionT = "https://www.googleapis.com/fusiontables/v2/";
        String urlFusionT_v1 = "https://www.googleapis.com/fusiontables/v1/";
        String key ="AIzaSyB-C74mt75P7YcE9_RxXyDCNekBAQ-tGMk";
        String tableId="1CBP-Ht9blF_PuPXNWjAajjRfEZUbNeCxuQ5WvMbJ";
        String resourceID = "tables";
        String commandQuery_SQL = "query?sql=";
        String commandQuery     = "query";
        String Query_01 = "SELECT * FROM tableID".replaceAll(" ", "%20").replaceAll("tableID", tableId);
        String Query_02 = "SELECT ROWID FROM tableID".replaceAll(" ", "%20").replaceAll("tableID", tableId);
        String Query_03 = "SELECT Text, Number, Location, Date FROM tableID".replaceAll(" ", "%20").replaceAll("tableID", tableId);
        String Query_04 = "INSERT INTO tableID (Text, Number) VALUES ('f', '6')".replaceAll(" ", " ").replaceAll("tableID", tableId);

        //"INSERT INTO <table_id> (<column_name> {, <column_name>}*) VALUES (<value> {, <value>}*)\n"

        GetCommentTask tareaGet2 = new GetCommentTask();
        PostCommentTask tareaPost2 = new PostCommentTask();

        URL url2, url_query_01, url_query_02, url_query_03, url_query_04;

        try {
            url2 = new URL (urlFusionT+resourceID+"/"+tableId+"/"+"columns?key="+key);
            url_query_01 = new URL (urlFusionT+commandQuery_SQL+Query_01+"&key="+key);
            url_query_02 = new URL (urlFusionT + commandQuery_SQL+Query_02+"&key="+key);
            url_query_03 = new URL (urlFusionT+commandQuery_SQL+Query_03+"&key="+key);
            url_query_04 = new URL (urlFusionT+commandQuery    );
            //tareaGet2.execute(url_query_03);

          //  tareaPost2.execute(url_query_04);

        }
        catch (IOException e) {
            e.printStackTrace();
            Depuracion.traza(this.getClass().getName()+" Excepcion e: "+ e );
        }
        finally {
        }

        // --------------------------------------------------------

        PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        wl=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ArrayList<String[]> content = new ArrayList<String[]>();
        gpsStatus_listener     = new myGpsStatusListener(textDepurador, content, this);

        String[] cabecera = { " PRN ", " InFix ", " SNR ", "  Azim  ", "  Elev  " };

        TableRow rowCabecera = new TableRow(this);
        //for (int j = 0; j < content.get(0).length; j++) {
        for (int j = 0; j < cabecera.length; j++) {
            TextView rowCell = new TextView(this);
            rowCell.setText(cabecera[j]);
            rowCell.setTextColor(Color.parseColor("#CCCCCC"));
            rowCabecera.addView(rowCell);
        }
        contentTable.addView(rowCabecera);

        for (int i = 0; i < content.size(); i++){
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

        ll_0 = new mylocationlistener();
        ll_1 = new mylocationlistener();
        ll_2 = new mylocationlistener();


        obtieneInfoProviders();
        chequeaEstado();
        actualizaGUI();



    }





    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggleActivar_loc:
                if (btnActivar_loc.isChecked()) {
                    status_Activado = true;
                    //Depuracion.traza("true", textDepurador);
                }else {
                    status_Activado = false;
                    //Depuracion.traza("false", textDepurador);
                }
                //status_Activado = !status_Activado;
                actualizaGUI();

                if (status_Activado) {
                    chequeaEstado();

                    if(lm.addGpsStatusListener(gpsStatus_listener)) {
                        Depuracion.traza("añade el gpsStatus_listener", textDepurador);
                        locationListener_activo = true;
                    } else {
                        Depuracion.traza("Casca el gpsStatus_listener", textDepurador);
                        locationListener_activo = false;
                    }

                    wl.acquire();
                    long t_min = 1000 * Long.parseLong(String.valueOf(editText_T_Min.getText()).toString());
                    float dist_min = Float.parseFloat(String.valueOf(editText_Dist_Min.getText()).toString());
                    Depuracion.traza("dist_min " + dist_min, textDepurador);
                    lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, t_min, dist_min, ll_2);     //nos subscribimos al proveedor para que nos de información
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, t_min, dist_min, ll_0);             //nos subscribimos al proveedor para que nos de información
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, t_min, dist_min, ll_1);

                } else  {
                    if (locationListener_activo) {
                        lm.removeGpsStatusListener(gpsStatus_listener);
                        locationListener_activo = false;
                        Depuracion.traza("GpsStatus_listener eliminado!!", textDepurador);
                    }

                    lm.removeUpdates(ll_2);
                    lm.removeUpdates(ll_0);
                    lm.removeUpdates(ll_1);
                    //onLine_Device = 0;

                    wl.release();
                }
                actualizaGUI();
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
            /*case R.id.buttonAux:
                if (!tabAux) {
                    tabDep = false;
                    tabGPS = false;
                    tabAux = true;
                    tabFrame = false;
                }
                actualizaGUI();
                break;*/
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
                Depuracion.traza("signout", textDepurador);
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

        Depuracion.traza("EStamos en sign-in()", textDepurador);

        //Starting the intent prompts the user to select a Google account to sign in with.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // [START signOut]
    private void signOut() {
        Depuracion.traza("EStamos en sign-outn()", textDepurador);
        if (mGoogleApiClient.isConnected() ) {
            Depuracion.traza("mGoogleApiClient esta conectado", textDepurador);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            updateUI(false);
                            // [END_EXCLUDE]
                        }
                    });
        } else
            Depuracion.traza("mGoogleApiClient NO esta conectado", textDepurador);


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
        Depuracion.traza("Estamos en onActivityResult()", textDepurador);
        Depuracion.traza("requestCode: " + requestCode, textDepurador);
        Depuracion.traza("resultCode: " + resultCode, textDepurador);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //
            // findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            textUser.setText("signed out");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Depuracion.traza("handleSignInResult:" + result.isSuccess());
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Depuracion.traza("handleSignInResult: " + acct.getEmail()
                    + ", " + acct.getDisplayName()
                    + ", " + acct.getId()
                    , textDepurador);

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()))
            textUser.setText(acct.getDisplayName());
            Depuracion.traza("Login result.isSuccess()");

            updateUI(true);
        } else {
            Depuracion.traza("Login result.KO!!");
            textUser.setText("KO");
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapaSBS = googleMap;
        mapaSBS.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mapaSBS.setMyLocationEnabled(true);

        CasaNueva = new LatLng(40.382137, -80.053961);
        Bombardier = new LatLng(40.347280, -79.957230);

        mapaSBS.addMarker(new MarkerOptions().position(Bombardier).title("Example Marker in Bombardier")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mapaSBS.addMarker(new MarkerOptions().position(CasaNueva).title("Example Marker in CasaNueva")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLng(Bombardier);
        mapaSBS.moveCamera(camUpd1);

        CameraPosition camPos1 = new CameraPosition.Builder()
                .target(Bombardier)
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

    private void mostrarLineas()
    {
        //Dibujo con Lineas


        PolylineOptions lineas = new PolylineOptions()
                .add(Bombardier)
                .add(CasaNueva);

        lineas.width(8);
        lineas.color(Color.RED);

        mapaSBS.addPolyline(lineas);
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

            //text_available_GPS.setText((i) + " / " + gpsStatus.getTimeToFirstFix());


            switch (event) {
                case 1: //GPS_EVENT_STARTED
                    Depuracion.traza(sEvent[event-1], textDepurador);
                    break;
                case 2: //GPS_EVENT_STOPPED
                    Depuracion.traza(sEvent[event-1], textDepurador);
                    break;
                case 3: //GPS_EVENT_FIRST_FIX
                    Depuracion.traza(sEvent[event-1]+" getTimeToFirstFix: "+ gpsStatus.getTimeToFirstFix(), textDepurador);
                    //Depuracion.traza("time: " + gpsStatus.getTimeToFirstFix() , textDepurador);
                    break;
                case 4: //GPS_EVENT_SATELLITE_STATUS
                    //Depuracion.traza(sEvent[event-1], textDepurador);
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

        image_avail_gps = (ImageView) findViewById(R.id.image_avail_gps);
        image_avail_network = (ImageView) findViewById(R.id.image_avail_network);
        image_avail_passive = (ImageView) findViewById(R.id.image_avail_passive);

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
        float GUI_TEXTO_LABEL_L3_GRANDE = 18f;

        float GUI_TEXTO_VISOR_PEQ = 15f;
        float GUI_TEXTO_DEP_PEQ = 11f;
        float GUI_TEXTO_BOT_PEQ = 15f;
        float GUI_TEXTO_LABEL_L3_PEQ = 11f;



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

//                String sLong = df_latlong.format(Double.toString(pLat));
//                String sLat = df_latlong.format(Double.toString(pLong));
//                String sPrec = df_vel.format(Double.toString(pPrec));
//                String sSpeed = df_vel.format(Float.toString(pSpeed * 2.2369f));   // en mph

                String sLong = df_latlong.format(pLat);
                String sLat = df_latlong.format(pLong);
                String sPrec = df_vel.format(pPrec);
                String sSpeed = df_vel.format(pSpeed * 2.2369f);   // en mph

                String sProvider = pProvider.substring(0,1);

//                if (sLong.length() > 9) sLong = sLong.substring(0,9);
//                if (sLat.length() > 9) sLat = sLat.substring(0,9);
//                if (sPrec.length() > 4) sPrec = sPrec.substring(0,4);
//                if (sSpeed.length() > 5) sSpeed = sSpeed.substring(0,5);

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
                            sSpeed="-1.0";
                            break;
                        case "passive":
                            mapaSBS.addMarker(new MarkerOptions().position(Actual).title(markerText)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            sSpeed="-1.0";
                            break;
                    }



                } else {
                    Depuracion.traza("mapaSBS = null", textDepurador);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Depuracion.traza("onStatusChanged: provider=" + provider + ", status=" + status, textDepurador);
        }

        @Override

        public void onProviderEnabled(String provider) {
            if (provider.equals("gps")) {
                available_GPS = true;
            } else if (provider.equals("network")) {
                available_Network = true;
            } else if (provider.equals("passive")){
                available_Passive = true;
            }

            Depuracion.traza(provider + " ON", textDepurador);
            actualizaGUI();
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (provider.equals("gps")) {
                available_GPS = false;
            } else if (provider.equals("network")) {
                available_Network = false;
            } else if (provider.equals("passive")) {
                available_Passive = false;
            }

            Depuracion.traza(provider + " OFF", textDepurador);
            actualizaGUI();
        }
    }

    public void chequeaEstado() {
        //onLine_Device = 0;

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            available_GPS   = true;
        } else {
            available_GPS = false;
        }

        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            available_Network = true;
        } else {
            available_Network = false;
        }

        if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
        {
            available_Passive = true;
            if (status_Activado) {
            }

        } else {
            available_Passive = false;
        }

    }

    public void obtieneInfoProviders() {

        listProviders = lm.getAllProviders();
        lp0      = lm.getProvider(listProviders.get(0));
        lp1      = lm.getProvider(listProviders.get(1));
        lp2      = lm.getProvider(listProviders.get(2));

        int lp0_acc                     = lp0.getAccuracy();
        boolean lp0_alt_av              = lp0.supportsAltitude();
        int lp0_pr                      = lp0.getPowerRequirement();
        boolean lp0_reqCell             = lp0.requiresCell();
        boolean lp0_speed               = lp0.supportsSpeed();
        int lp1_acc                     = lp1.getAccuracy();
        boolean lp1_alt_av              = lp1.supportsAltitude();
        int lp1_pr                      = lp1.getPowerRequirement();
        boolean lp1_reqCell             = lp1.requiresCell();
        boolean lp1_speed               = lp1.supportsSpeed();
        int lp2_acc                     = lp2.getAccuracy();
        boolean lp2_alt_av              = lp2.supportsAltitude();
        int lp2_pr                      = lp2.getPowerRequirement();
        boolean lp2_reqCell             = lp2.requiresCell();
        boolean lp2_speed               = lp2.supportsSpeed();

        Depuracion.traza(listProviders.get(0) + ": Acc=" + lp0_acc + ", altitudeAv=" + lp0_alt_av
                + ", PowReq=" + lp0_pr + ", reqCell: " + lp0_reqCell + " supportSpeed: " + lp0_speed, textDepurador);
        Depuracion.traza(listProviders.get(1) + ": Acc=" + lp1_acc + ", altitudeAv=" + lp1_alt_av
                + ", PowReq=" + lp1_pr + ", reqCell: " + lp1_reqCell + " supportSpeed: " + lp1_speed, textDepurador);
        Depuracion.traza(listProviders.get(2) + ": Acc=" + lp2_acc + ", altitudeAv=" + lp2_alt_av
                + ", PowReq=" + lp2_pr + ", reqCell: " + lp2_reqCell + " supportSpeed: " + lp2_speed, textDepurador);
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

        if (available_Passive) {
            image_avail_passive.setImageResource(R.drawable.radio_24_green);
        } else {
            image_avail_passive.setImageResource(R.drawable.radio_24_grey);
        }

        if (available_GPS) {
            image_avail_gps.setImageResource(R.drawable.gps_24_green);
        } else {
            image_avail_gps.setImageResource(R.drawable.gps_24_grey);
        }

        if (available_Network) {
            image_avail_network.setImageResource(R.drawable.wifi_24_green);
        } else {
            image_avail_network.setImageResource(R.drawable.wifi_24_grey);
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
            //text_available_GPS.setText("");
        }
    }


}
