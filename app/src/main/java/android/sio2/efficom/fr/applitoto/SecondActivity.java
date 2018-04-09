package android.sio2.efficom.fr.applitoto;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sio2.efficom.fr.applitoto.adapters.InterventionAdapter;
import android.sio2.efficom.fr.applitoto.model.Intervention;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yassine on 06/02/2018.
 */

public class SecondActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    String apiURL = "https://pins.area42.fr/api.php";

    TextView textView;
    RecyclerView recyclerView;
    private View.OnClickListener adapterClicListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);


        recyclerView = findViewById(R.id.recyclerView);

        //affichage des données sous forme d'une liste
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
//        recyclerView.setLayoutManager(gridLayoutManager);

        adapterClicListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intervention.idInterventionTotal.Liste_int item = (Intervention.idInterventionTotal.Liste_int) view.getTag();
                Intent intent = new Intent(view.getContext(), ItemActivity.class);
                intent.putExtra("IDINTER", item.id_inter);
                intent.putExtra("LASTNAME", item.lastname);
                intent.putExtra("FIRSTNAME", item.firstname);
                intent.putExtra("COMPANY", item.company);
                intent.putExtra("DATE", item.date_inter);
                intent.putExtra("ADDRESS1", item.address1);
                intent.putExtra("ADDRESS2", item.address2);
                intent.putExtra("CITY", item.city);
                intent.putExtra("ZIPCODE", item.zipcode);
                intent.putExtra("PHONE", item.phone);
                intent.putExtra("MOTIVE", item.motive);
                intent.putExtra("PENDING", item.pending);
                intent.putExtra("REPORT", item.report);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        //création de l'async Task
        MonAsyncTask monAsyncTask = new MonAsyncTask();
        //exécution de l'async task sans bloquer le main thread
        monAsyncTask.execute(apiURL);
    }

    String run(String url) throws IOException {
        //pour lancer l'async task
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    class MonAsyncTask extends AsyncTask<String, Void, Intervention> {

        @Override
        protected Intervention doInBackground(String... strings) {
            //on n'est pas dans le main thread ici
            try {
                String jsonBody = run(strings[0]);

                Gson gson = new Gson();
                Intervention intervention = gson.fromJson(jsonBody, Intervention.class);

                return intervention;
            } catch (IOException e) {
                Log.e("SIO2", e.getStackTrace().toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Intervention intervention) {
            //la variable 's' contient le résultat du doInBackground
            if (intervention == null) {
                return;
            }

            //création de l'adapter avec la list d'items dont il va gérer l'affichage
            InterventionAdapter adapter = new InterventionAdapter(intervention.id_intervention_total.liste_int, adapterClicListener);

            //fait le lien entre le recycleview et l'adapter
            recyclerView.setAdapter(adapter);
        }
    }
}
