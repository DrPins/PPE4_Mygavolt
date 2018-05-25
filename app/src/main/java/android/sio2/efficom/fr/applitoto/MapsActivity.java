package android.sio2.efficom.fr.applitoto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity  {

    private static final String TAG = " ";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;
    private double startLat = 0;
    private double startLong = 0;
    private double endLat = 0;
    private double endLong = 0;
    private boolean endBool = false;
    private boolean startBool = false;
    private String lastname;
    private String sLatLong;
    private LatLng latLng;


    private Boolean mLocationPermissonGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;



    //URL qui permettra d'avoir l'itineraire, elle sera envoyée à googleMaps
    //Utilisez la bonne clé d'API geocode
    //https://developers.google.com/maps/documentation/geocoding/get-api-key
    private static final String GEOCODE_API_KEY = "AIzaSyBwfwvFSBptmIlrf8fL0GsJgieqAG-znTo";
    //Utilisez la bonne clé d'API
    private static final String BASE_LOCATION_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=" +
            GEOCODE_API_KEY +"&address=";
    private   String locationURL = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // on récupère les données passées en paramètre
        String date = getIntent().getStringExtra("DATE");
        String address1 = getIntent().getStringExtra("ADDRESS1");
        String address2 = getIntent().getStringExtra("ADDRESS2");
        String city = getIntent().getStringExtra("CITY");
        String zipcode = getIntent().getStringExtra("ZIPCODE");
        String idInter = getIntent().getStringExtra("IDINTER");

        // permet de faire passer des données d'un script à un autre sans passer
        //par les extras.
        SharedPreferences mSharedPreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        // initialisation de lastname en shared preferences
        lastname = mSharedPreferences.getString("lastname", null);

        //concatenation des éléments d'adresse
        String fullAddress = address1 +" " + address2 + " "+ city + " "+zipcode;

        //concaténation de la base de l'url (pour l'itineraire) et de l'adresse
        locationURL = BASE_LOCATION_URL + fullAddress;

        //avant de pouvoir continuer, il va falloir vérifier les permissions
        getLocationPermission();
    }

    private void getLocationPermission(){
        String[] permissions ={FINE_LOCATION, COARSE_LOCATION};
        Log.d(TAG, "Getting location permission");
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                //si on a les permissions de géolocalisation
                //on passe la variable à true
                mLocationPermissonGranted = true;
                //on lance l'initialisation de la carte
                initMap();
            }else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "onMapReady : initisalization map");
        //Prépare la map
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(MapsActivity.this, "maps is ready", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onMapReady : map is ready ");
                mMap = googleMap;

                if (mLocationPermissonGranted) {
                    getDeviceLocation();
                    //Dans le cas où on a un pb de permission
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }


                //ASYNC TASK
                new DataLongOperationAsynchTask().execute();
            }
        });


        //test pour récupérer régulièrement les coordonnées
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // dans le cas où le network provider fonctionne

        //création de l'async Task
        final RequeteAsync geolocAsyncTask = new RequeteAsync();

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 150000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //récupération de la lattitude
                    double  latitude = location.getLatitude();


                    //récupération de la longitude
                    double longitude = location.getLongitude();

                    //on converti en String (des fois que..)
                    String sLng = Double.toString(longitude);
                    String sLat = Double.toString(latitude);

                    latLng = new LatLng(latitude,longitude);
                    sLatLong = latLng.toString();


                    RequeteAsync geolocAsyncTask = new RequeteAsync();
                    //exécution de l'async task sans bloquer le main thread

                    geolocAsyncTask.execute(apiURL,lastname, sLatLong);
                    //new geolocAsyncTask().execute(apiURL,lastname, sLatLong);

                    //Toast.makeText(MapsActivity.this, "maps is ready"+sLat+" "+sLng, Toast.LENGTH_LONG).show();
                    Log.i("pins", "coordonnée "+sLat+" "+sLng);
                    Log.i("pins", "Latng "+sLatLong);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }
        // dans le cas où le network provider ne fonctionne pas, on va utiliser le GPS
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //récupération de la lattitude
                    double  latitude = location.getLatitude();


                    //récupération de la longitude
                    double longitude = location.getLongitude();

                    //on converti en String (des fois que..)
                    String sLng = Double.toString(longitude);
                    String sLat = Double.toString(latitude);

                    latLng = new LatLng(latitude,longitude);
                    sLatLong = latLng.toString();


                    RequeteAsync geolocAsyncTask = new RequeteAsync();
                    //exécution de l'async task sans bloquer le main thread

                    geolocAsyncTask.execute(apiURL,lastname, sLatLong);
                    //new geolocAsyncTask().execute(apiURL,lastname, sLatLong);

                    //Toast.makeText(MapsActivity.this, "maps is ready"+sLat+" "+sLng, Toast.LENGTH_LONG).show();
                    Log.i("pins", "coordonnée "+sLat+" "+sLng);
                    Log.i("pins", "Latng "+sLatLong);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }




    }

    OkHttpClient client = new OkHttpClient(); // classe qui permet d'envoyer et récupérer des données
    String apiURL = "https://pins.area42.fr/updategeoloc.php";



    class RequeteAsync extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            //le traitement qui va se faire en async

            String url = strings[0];

            //methode post
            //on consitute le contenu du post (un endroit où on pourra mettre les elements du post)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("lastname", strings[1])
                    .addFormDataPart("lat", strings[2])
                    .build();

            // on envoye la requete au serveur et va construire la nouvelle url
            Request request = new Request.Builder()
                    .url(url) // url de base
                    .post(requestBody) //la partie post
                    .build();

            Response response;
            try {
                response = client.newCall(request).execute();

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            Log.d("pins", "" + response.isSuccessful());


            return null;
        }
    }


    //ASYNC TASK
    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
        ProgressDialog dialog = new ProgressDialog(MapsActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                response = getLatLongByURL(locationURL);
                Log.d("response",""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");
                // ici on récupère la lattitude du device
                Log.d("latitude", "" + lat);
                Log.d("longitude", "" + lng);

                // Object qui représente les coordonnées
                LatLng coordinate = new LatLng(lat, lng);
                //Ajout d'un marqueur
                mMap.addMarker(new MarkerOptions().position(coordinate).title(""));
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 12);
                mMap.animateCamera(yourLocation);

                endLat = lat;
                endLong = lng;


                // on vérifie si la tache asynchrone permettant de récupérer les coordonnées de départ
                // a bien fini
                if(startLat != 0 && startLong != 0){
                    // dans ce cas, on affiche le bouton et on lance le onclick
                    FloatingActionButton fabMap = findViewById(R.id.fabGetItinerary);
                    fabMap.setVisibility(View.VISIBLE);
                    fabMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String format = "http://maps.google.com/maps?saddr=" + startLat + ","
                                    + startLong + "&daddr=" + endLat + "," + endLong + "(Ma destination)";
                            Uri uri = Uri.parse(format);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : getting the device current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissonGranted) {
                final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getDeviceLocation : location found");
                            Location currentLocation = task.getResult();

                            startLat = currentLocation.getLatitude();
                            startLong = currentLocation.getLongitude();

                            Log.d(TAG, "getDeviceLocation : location found");

                            if(endLat != 0 && endLong != 0){
                                // dans ce cas, on affiche le bouton et on lance le onclick
                                FloatingActionButton fabMap = findViewById(R.id.fabGetItinerary);
                                fabMap.setVisibility(View.VISIBLE);
                                fabMap.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        String format = "http://maps.google.com/maps?saddr=" + startLat + ","
                                                + startLong + "&daddr=" + endLat + "," + endLong + "(Ma destination)";
                                        Uri uri = Uri.parse(format);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });

                            }

                            // moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "getDeviceLocation : current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : SecurityException" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera : moving the camera to lat : " + latLng.latitude + " lng : " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }







    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    //




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissonGranted =false;
        Log.d(TAG, "onRequestPermissionsResult : called");
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 ){
                    for(int i =0 ; i< grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissonGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult : permission failed ");
                            return;
                        }
                    }
                    mLocationPermissonGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult : permission granted ");
                    //initialisation de la map (toutes les permissions ont été vérifiées)
                    initMap();
                }

            }
        }
    }

}

