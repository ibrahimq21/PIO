package com.example.pio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainPageActivity extends AppCompatActivity {


    private Button displayRoute, displayMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);


        displayRoute = findViewById(R.id.bus_route);

        displayMap = findViewById(R.id.display_uni_map);

        displayRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainPageActivity.this, MainActivity.class);

                MainPageActivity.this.startActivity(intent);


            }
        });

        displayMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainPageActivity.this, DisplayMapActivity.class);

                MainPageActivity.this.startActivity(intent);

            }
        });


    }
}
