package android.sio2.efficom.fr.applitoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView itemDate = findViewById(R.id.itemDetailDateTextView);
        TextView itemFirstName = findViewById(R.id.itemDetailFirstNameTextView);
        //TextView itemLastName= findViewById(R.id.itemDetailLastNameTextView);
        TextView itemCompany= findViewById(R.id.itemDetailCompanyTextView);
        TextView itemAddress1= findViewById(R.id.itemDetailAddress1TextView);
        //TextView itemAddress2= findViewById(R.id.itemDetailAddress2TextView);
        TextView itemCity= findViewById(R.id.itemDetailCityTextView);
        //TextView itemZip= findViewById(R.id.itemDetailZipTextView);
        TextView itemPhone= findViewById(R.id.itemDetailPhoneTextView);
        TextView itemReport= findViewById(R.id.itemDetailReportTextView);
        TextView itemMotive= findViewById(R.id.itemDetailMotiveTextView);


        final String idInter = getIntent().getStringExtra("IDINTER");
        String lastname = getIntent().getStringExtra("LASTNAME");
        String firstname = getIntent().getStringExtra("FIRSTNAME");
        String company = getIntent().getStringExtra("COMPANY");
        final String date = getIntent().getStringExtra("DATE");
        final String address1 = getIntent().getStringExtra("ADDRESS1");
        final String address2 = getIntent().getStringExtra("ADDRESS2");
        final String city = getIntent().getStringExtra("CITY");
        final String zipcode = getIntent().getStringExtra("ZIPCODE");
        String phone = getIntent().getStringExtra("PHONE");
        String motive = getIntent().getStringExtra("MOTIVE");
        String pending = getIntent().getStringExtra("PENDING");
        final String report = getIntent().getStringExtra("REPORT");

        itemDate.setText(date);
        itemFirstName.setText(firstname +' '+ lastname);
        //itemLastName.setText(lastname);
        itemCompany.setText(company);
        itemAddress1.setText(address1 +' '+ address2);
        //itemAddress2.setText(address2);
        itemCity.setText(city +' '+ zipcode);
        //itemZip.setText(zipcode);
        itemPhone.setText(phone);
        itemReport.setText(report);
        itemMotive.setText(motive);

        FloatingActionButton fab = findViewById(R.id.fabAddReport);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReportActivity.class);

                intent.putExtra("IDINTER", idInter);
                intent.putExtra("REPORT", report);
                startActivity(intent);
            }
        });

        FloatingActionButton fabMap = findViewById(R.id.fabGoToMap);
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);

                intent.putExtra("IDINTER", idInter);
                intent.putExtra("DATE", date);
                intent.putExtra("ADDRESS1", address1);
                intent.putExtra("ADDRESS2",address2);
                intent.putExtra("CITY", city);
                intent.putExtra("ZIPCODE", zipcode);
                startActivity(intent);
            }
        });


    }

}
