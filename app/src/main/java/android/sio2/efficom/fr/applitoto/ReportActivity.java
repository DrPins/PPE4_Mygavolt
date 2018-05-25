package android.sio2.efficom.fr.applitoto;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    //time picker test
    TextView timeTextView;
    Button timePickerBtn;
    TimePickerDialog timePickerDialog;
    //


    //une fois l'heure fixée dans le time picker, on l'insert dans le textview
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeTextView .setText( hourOfDay+ ":" + minute);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //time picker test
        // récupération des objets graphiques
        timeTextView = findViewById(R.id.timeTextView);
        timePickerBtn = findViewById(R.id.timePickerBtn);

        //création d'un on click listener qui va lancer le timepicker une fois le bouton pressé
        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // voir timepickerfragment.java
                android.support.v4.app.DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");

            }
        });

        //


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toolbar idToolbar = findViewById(R.id.toolbar);
        final EditText reportEditText  = findViewById(R.id.reportEditText);

        final String id = getIntent().getStringExtra("IDINTER");
        String report = getIntent().getStringExtra("REPORT");
        String duration = getIntent().getStringExtra("DURATION");
        String timeStart = getIntent().getStringExtra("HOUR");

        setSupportActionBar(idToolbar);
        getSupportActionBar().setTitle("Numéro d'intervention : "+id);

        // rempli le rapport si il a deja été rempli
        if (report != null){
            reportEditText.setText(report);}

        // rempli la durée si elle a deja été remplie
        // le substring ici permet de n'afficher que les heures et minutes (sans les sec et millisec)
        if (duration != null){
            timeTextView.setText(duration.substring(0,5));}

        //Récupération en format heure de l'heure de fin
        String timeEnd = timeTextView.getText().toString();
        DateFormat sdf = new SimpleDateFormat("hh:mm");
        try {
            Date dateEnd = sdf.parse(timeEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Récupération en format heure de l'heure de début (passée en extra)
        try {
            Date dateStart = sdf.parse(timeStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        //Envoyer le rapport
        Button sendReportBtn = findViewById(R.id.sendReportBtn);
        sendReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // récupération dans des variables du rapport et de l'heure
                String reportEdit = reportEditText.getText().toString();
                String timeEdit = timeTextView.getText().toString();


                //création de l'async Task
                RequeteAsyncTask monAsyncTask= new RequeteAsyncTask();
                //exécution de l'async task sans bloquer le main thread


                // execution de l'asyncTask qui va appeler l'api de mise à jour des intervention
                // avec pour paramètre l'id de l'intervention, le rapport et l'heure de fin
                monAsyncTask.execute(apiURL,reportEdit,id, timeEdit);

                Toast.makeText(view.getContext(), "Rapport envoyé", Toast.LENGTH_LONG).show();

                //retour à l'écran affichant toutes les interventions
                Intent intent = new Intent(view.getContext(), SecondActivity.class);
                startActivity(intent);


            }
        });


    }

    OkHttpClient client = new OkHttpClient(); // classe qui permet d'envoyer et récupérer des données
    String apiURL = "https://pins.area42.fr/update.php"; // url de l'api



    public class RequeteAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            //le traitement qui va se faire en async

            String url = strings[0];

            //methode post
            //on consitute le contenu du post (un endroit où on pourra mettre les elements du post)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("report", strings[1])
                    .addFormDataPart("duration", strings[3])
                    .addFormDataPart("id_intervention", strings[2])
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

