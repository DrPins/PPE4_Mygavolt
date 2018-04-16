package android.sio2.efficom.fr.applitoto;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.sio2.efficom.fr.applitoto.model.Intervention;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import okhttp3.Connection;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// AIzaSyA3YmPp32lRhvrqyq5g9KejH0U0_tlHdZk
// A faire:


public class MainActivity extends AppCompatActivity {

    //vérification de l'accès aux maps
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //on vérifie tout de suite si on a accès aux google services
        if (isServicesOk()){
            // si c'est bon on lance l'initialisation
            init();
        }

    }

    private void init(){
        Button monBouton = findViewById(R.id.button);
        monBouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // a l'initialisation, on commence par récupérer le login et le mot de passe
                // qu'on stocke dans des variables
                EditText editLogin = findViewById(R.id.editLogin);
                EditText editPassword = findViewById(R.id.editPassword);

                String login = editLogin.getText().toString();
                String pwd = editPassword.getText().toString();


                //création de l'async Task
                RequeteAsyncTask monAsyncTask= new RequeteAsyncTask();
                //exécution de l'async task sans bloquer le main thread
                //avec le login et le mot de passe en paramètre
                monAsyncTask.execute(apiURL,login, pwd);


            }
        });
    }

    OkHttpClient client = new OkHttpClient(); // classe qui permet d'envoyer et récupérer des données

    // Api permetant de vérifier le login (login et mot de passe correspondent à un employé)
    String apiURL = "https://pins.area42.fr/login.php";

    class RequeteAsyncTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            //le traitement qui va se faire en async
            //le do in background va renvoyer un boolean qui sera ensuite utilisé par le post execute

            //on récupère les paramètres passé au lancement de l'asynctask
            String url = strings[0];
            String login = strings[1];
            String pwd = strings[2];

            //methode post
            //on consitute le contenu du post (un endroit où on pourra mettre les elements du post)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("lastname", login)
                    .addFormDataPart("pwd", pwd)
                    .build();

            // on envoye la requete au serveur et va construire la nouvelle url
            Request request = new Request.Builder()
                    .url(url) // url de base
                    .post(requestBody) //la partie post
                    .build();

            Response response;
            try {
                response = client.newCall(request).execute();
                //on utilise 2 façons différents pour vérifier si le login + mdp est correct
                //mais on ne peut en utiliser d'une des 2
                //la variable s représente ce que va renvoyer l'api
                //NB: si on trouve l'employé en base l'api va renvoyer 'OK' et le code retour 200
                //dans le cas inverser on récupérera 'ko'
                String s = response.body().string();
                //récupère le code retour
                if(response.code() == 200 || "ok".equals(s)){
                    Log.d("SIO2", "code retour " + response.isSuccessful());
                    Log.d("API1", "body " + response.isSuccessful());
                    return true;
                }
                else{
                    Log.d("API1", "error login "+ s + " code " + response.code());
                    return false;
                }

            } catch (Exception e) {

                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Boolean b) {
            //ici le post execture prend un boolean en paramètre
            // true si le compte existe / false dans le cas inverse

            if(b) {
                //si le paramètre est à true, on crée une variable de "session"
                SharedPreferences mSharedPreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();

                // création de l'intent permetant de passer à une autre vue
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                // récupération du login qui correspond au nom de famille de l'employé
                EditText editLogin = findViewById(R.id.editLogin);
                String login = editLogin.getText().toString();

                // on enregiste le nom de famille dans la shared preference
                mEditor.putString("lastname", login).apply();
                // on passe à la vue suivante
                startActivity(intent);
            }

            else{
                //Dans le cas où le login ou le mot de passe est erroné, on affiche un toast
                //pour prévenir l'utilisateur
                Toast.makeText(MainActivity.this.getApplicationContext(), "Login ou mot de passe invalide", Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public boolean isServicesOk (){
        // vérification des google services, renvoie true ou false
        Log.d(TAG, "isServicesOk : checking Google services version" );
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is ok
            Log.d(TAG, "Google Play Services is working" );
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // si on peut résoudre l'erreur
            Log.d(TAG, "An error occured but we can fix it" );
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,  available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            // si on ne peut pas résoudre l'erreur
            Toast.makeText(this, "You  can't make map requests",Toast.LENGTH_LONG ).show();
        }
        return false;
    }
}
