package com.example.examen2parcial;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.examen2parcial.Config.ContactoAdaptador;
import com.example.examen2parcial.Config.Contactos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactosView extends AppCompatActivity {

    private RecyclerView recyclerContactos;
    private SearchView buscador;
    private ContactoAdaptador adaptador;
    private ArrayList<Contactos> listaContactos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos_view);

        recyclerContactos = findViewById(R.id.recyclerContactos); // RecyclerView nuevo en el XML
        buscador = findViewById(R.id.searchbar); // Asegúrate que sigue en tu layout

        recyclerContactos.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new ContactoAdaptador(ContactosView.this, listaContactos);
        recyclerContactos.setAdapter(adaptador);

        cargarContactos();

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nuevoTexto) {
                adaptador.filtrar(nuevoTexto); // método de filtrado personalizado
                return true;
            }
        });
    }

    private void cargarContactos() {
        String url = "http://10.0.2.2/crud-exam/GetContacto.php";

        JsonArrayRequest req = new JsonArrayRequest(url,
                (JSONArray arr) -> {
                    listaContactos.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        try {
                            JSONObject o = arr.getJSONObject(i);

                            Contactos c = new Contactos();
                            c.setId(o.getInt("id"));
                            c.setNombre(o.getString("nombre"));
                            c.setTelefono(o.getString("telefono"));
                            c.setLatitud(o.getDouble("latitud"));
                            c.setLongitud(o.getDouble("longitud"));

                            if (o.has("firmaBase64")) {
                                c.setFirmaBase64(o.getString("firmaBase64"));
                            }

                            listaContactos.add(c);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error en contacto #" + (i + 1), Toast.LENGTH_SHORT).show();
                        }
                    }
                    adaptador.actualizarLista(listaContactos);
                },
                error -> Toast.makeText(this, "Error al cargar contactos: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(req);
    }
}