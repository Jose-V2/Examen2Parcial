package com.example.examen2parcial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactosView extends AppCompatActivity {

    Button btnatras, btneliminar, btnactulizar;

    ListView listcontactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contactos_view);

        btnatras = (Button) findViewById(R.id.btnatras);
        btneliminar = (Button) findViewById(R.id.btneliminar);
        btnactulizar = (Button) findViewById(R.id.btnactualizar);

        listcontactos = (ListView) findViewById(R.id.listcontactos);

        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}