package android.sio2.efficom.fr.applitoto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.sio2.efficom.fr.applitoto.adapters.InterventionAdapter;
import android.sio2.efficom.fr.applitoto.model.Intervention;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PreviousInterActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    String apiURL = "https://pins.area42.fr/previousInterventions.php";


    TextView textView;
    RecyclerView recyclerView;
    private View.OnClickListener adapterClicListener;
    private String lastname;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_inter);

        //création des shared préférences (semblabe aux variables de sessions
        // permet de faire passer des données d'un script à un autre sans passer
        //par les extras.
        SharedPreferences mSharedPreferences = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        // initialisation de lastname en shared preferences
        lastname = mSharedPreferences.getString("lastname", null);

        recyclerView = findViewById(R.id.recyclerView);

        //affichage des données sous forme d'une liste
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //on fait passer à l'adapteur les données récupérée par l'api
        adapterClicListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intervention.Liste_int item = (Intervention.Liste_int) view.getTag();
                Intent intent = new Intent(view.getContext(), ItemActivity.class);
                intent.putExtra("IDINTER", item.id_inter);
                intent.putExtra("LASTNAME", item.lastname);
                intent.putExtra("FIRSTNAME", item.firstname);
                intent.putExtra("COMPANY", item.company);
                intent.putExtra("DATE", item.date_inter);
                intent.putExtra("HOUR", item.time_inter);
                intent.putExtra("ADDRESS1", item.address1);
                intent.putExtra("ADDRESS2", item.address2);
                intent.putExtra("CITY", item.city);
                intent.putExtra("ZIPCODE", item.zipcode);
                intent.putExtra("PHONE", item.phone);
                intent.putExtra("MOTIVE", item.motive);
                intent.putExtra("PENDING", item.pending);
                intent.putExtra("DURATION", item.duration);
                intent.putExtra("REPORT", item.report);
                startActivity(intent);
            }
        };

        FloatingActionButton fabMap = findViewById(R.id.fabGoBack);
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SecondActivity.class);
                // bouton qui permet de passer vers la carte
                //intent.putExtra("IDINTER", idInter);
                //intent.putExtra("DATE", date);
                //intent.putExtra("ADDRESS1", address1);
                //intent.putExtra("ADDRESS2",address2);
                //intent.putExtra("CITY", city);
                //intent.putExtra("ZIPCODE", zipcode);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //création de l'async Task
        MonAsyncTask monAsyncTask = new MonAsyncTask();
        //exécution de l'async task sans bloquer le main thread (api et lastname sont les
        //paramètres passés à l'asynctask (on peut faire passer autant de paramètres
        //que l'on veut
        //NB: ici on fait passer lastname qui servira à la requète de récupération des
        //interventions (récupération des intervention de lastname
        monAsyncTask.execute(apiURL, lastname);



    }

    class MonAsyncTask extends AsyncTask<String, Void, Intervention> {

        @Override
        protected Intervention doInBackground(String... strings) {
            //on n'est pas dans le main thread ici
            //le traitement qui va se faire en async

            //ici on récupère les données passées en paramètre de l'asyctask
            //NB: le premier paramètre sera strings[0], le 2ème strings[1], ...
            String apiURL = strings[0];
            String lastname = strings[1];


            //methode post
            //on consitute le contenu du post (un endroit où on pourra mettre les elements du post)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("lastname", lastname)
                    .build();

            // on envoie la requete au serveur et va construire la nouvelle url
            Request request = new Request.Builder()
                    .url(apiURL) // url de base
                    .post(requestBody) //la partie post
                    .build();

            try {
                // exécution de la requete POST et récupération du résultat dans response
                Response response = client.newCall(request).execute();
                //parse du json
                Gson gson = new Gson();
                return gson.fromJson(response.body().string(), Intervention.class);

            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Intervention intervention) {
            //on passe l'objet intervention en paramètre
            //si l'intervention est vide on arrete le traitement
            if (intervention == null) {
                return;
            }
            //sinon
            //création de l'adapter avec la list d'items dont il va gérer l'affichage
            InterventionAdapter adapter = new InterventionAdapter(intervention.liste_int, adapterClicListener);


            //fait le lien entre le recycleview et l'adapter
            recyclerView.setAdapter(adapter);
        }
    }
}
