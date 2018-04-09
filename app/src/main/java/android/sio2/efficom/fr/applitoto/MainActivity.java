package android.sio2.efficom.fr.applitoto;

import android.content.Intent;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


// A faire:
// - envoyer le rapport à l'api
// - envoyer un id à l'api
// - ajouter une couleur/bouton pour dire si une intervention a été terminée

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        Button monBouton = findViewById(R.id.button);
        monBouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Hello", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(view.getContext(), SecondActivity.class);
                startActivity(intent);



                EditText editLogin = findViewById(R.id.editLogin);


                String login = editLogin.getText().toString();


                //création de l'async Task
                RequeteAsyncTask monAsyncTask= new RequeteAsyncTask();
                //exécution de l'async task sans bloquer le main thread



                monAsyncTask.execute(apiURL,login);


            }
        });
    }

    OkHttpClient client = new OkHttpClient(); // classe qui permet d'envoyer et récupérer des données
    String apiURL = "https://pins.area42.fr/api.php";



    class RequeteAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            //le traitement qui va se faire en async

            String url = strings[0];
            String login = strings[1];

            //methode post
            //on consitute le contenu du post (un endroit où on pourra mettre les elements du post)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("lastname", login)
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

            Log.d("SIO2", "" + response.isSuccessful());


            return null;
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
}
