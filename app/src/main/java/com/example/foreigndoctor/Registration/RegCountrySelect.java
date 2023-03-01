package com.example.foreigndoctor.Registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.foreigndoctor.R;

public class RegCountrySelect extends AppCompatActivity {

    Spinner Country;

    String countryS = "null";

    private CardView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_country_select);

        Country = findViewById(R.id.select_country);

        next = findViewById(R.id.nextB);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });

        final String country [] = {"Select Country","India","Singapore","Malaysia","England"};

        final String India [] = {"Select Hospital","AIIMS","CMC","Apollo Hospitals","Fortis Hospitals","NIMHANS"};
        final String Malaysia [] = {"Select Hospital","Gleneagles Medical"," KPJ Specialist"," Prince Court"," Sunway Medical Centre","Assunta Hospital"};
        final String England [] = {"Select Hospital","Freeman Hospital","Birmingham Children's","Bedford Trust Hospital","BMI Healthcare Institute","Royal Marsden Hospital"};
        final String Singapore [] = {"Select Hospital","Raffles Medical Hospital"," Mount Elizabeth","Tan Tock Seng","Singapore General"," Ng Teng Fong General"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_select_model2,country);

        Country.setAdapter(adapter);

        Country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    countryS = Country.getItemAtPosition(i).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void nextPage() {

        if (countryS.equals("null")){
            Toast.makeText(this, "must select country......!", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(RegCountrySelect.this,Registration.class);
            intent.putExtra("country",countryS);
            startActivity(intent);
            finish();

        }

    }
}
