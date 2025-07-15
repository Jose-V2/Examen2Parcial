package com.example.examen2parcial;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.examen2parcial.Config.Contactos;
import com.example.examen2parcial.Config.ContactoAdaptador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactoList extends AppCompatActivity {

    private RecyclerView listaContactos;
    private ContactoAdaptador adaptador;
    private ArrayList<Contactos> datos = new ArrayList<>();

    private androidx.appcompat.widget.SearchView barraBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto_list);

        listaContactos = findViewById(R.id.lista_contactos);
        barraBusqueda = findViewById(R.id.barra_busqueda);

        listaContactos.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new ContactoAdaptador(this, datos);
        listaContactos.setAdapter(adaptador);

        cargarContactos();

        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String consulta) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                adaptador.filtrar(texto);
                return true;
            }
        });
    }

    private void cargarContactos() {
        String url = "http://10.0.2.2/crud-exam/GetContacto.php";

        JsonArrayRequest solicitud = new JsonArrayRequest(url,
                (JSONArray respuesta) -> {
                    datos.clear();
                    for (int i = 0; i < respuesta.length(); i++) {
                        try {
                            JSONObject obj = respuesta.getJSONObject(i);
                            Contactos c = new Contactos();
                            c.setId(obj.getInt("id"));
                            c.setNombre(obj.getString("nombre"));
                            c.setTelefono(obj.getString("telefono"));
                            c.setLatitud(obj.getDouble("latitud"));
                            c.setLongitud(obj.getDouble("longitud"));

                            if (obj.has("firmaBase64")) {
                                c.setFirmaBase64(obj.getString("firmaBase64"));
                            }

                            datos.add(c);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error al procesar contacto #" + (i + 1), Toast.LENGTH_SHORT).show();
                        }
                    }
                    adaptador.actualizarLista(datos);
                },
                error -> Toast.makeText(this, "Error al cargar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(solicitud);
    }
}
