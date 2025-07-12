package com.example.examen2parcial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btncontactos, btnsalvar, btnfirma;

    EditText nombre, telefono, latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btncontactos = (Button) findViewById(R.id.btncontactos);
        btnsalvar = (Button) findViewById(R.id.btnsalvar);
        btnfirma = (Button) findViewById(R.id.btnfirma);

        nombre = (EditText) findViewById(R.id.txtnombre);
        telefono = (EditText) findViewById(R.id.txttelefono);
        latitud = (EditText) findViewById(R.id.txtlatitud);
        longitud = (EditText) findViewById(R.id.txtlongitud);

        btncontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactosView.class);
                startActivity(intent);
            }
        });

    }
}