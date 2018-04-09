package android.sio2.efficom.fr.applitoto;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toolbar idToolbar = findViewById(R.id.toolbar);
        final EditText reportEditText  = findViewById(R.id.reportEditText);

        String id = getIntent().getStringExtra("IDINTER");
        String report = getIntent().getStringExtra("REPORT");

        setSupportActionBar(idToolbar);
        getSupportActionBar().setTitle("Numéro d'intervention : "+id);

        // rempli le rapport si il a deja été rempli
        if (report != null){
        reportEditText.setText(report);}

        //Envoyer le rapport
        Button sendReportBtn = findViewById(R.id.sendReportBtn);
        sendReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reportEdit = reportEditText.getText().toString();

                //création de l'async Task
                RequeteAsyncTask monAsyncTask= new RequeteAsyncTask();
                //exécution de l'async task sans bloquer le main thread



                monAsyncTask.execute(apiURL,reportEdit);

                Toast.makeText(view.getContext(), "Rapport envoyé", Toast.LENGTH_LONG).show();

               // Intent intent = new Intent(view.getContext(), SecondActivity.class);
               // startActivity(intent);


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

            //methode post
            //on consitute le contenu du post (un endroit où on pourra mettre les elements du post)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("report", strings[1])
                    .addFormDataPart("duration", "00:00:00.0000000")
                    .addFormDataPart("id_intervention", "1004")
                    .addFormDataPart("action", "fin")
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

}
