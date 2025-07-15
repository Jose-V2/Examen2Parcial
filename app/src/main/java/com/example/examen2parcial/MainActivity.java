package com.example.examen2parcial;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.examen2parcial.Config.Contactos;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 100;

    SignaturePad signaturePad;
    Button btncontactos, btnsalvar, btnfirma;
    EditText nombre, telefono, latitud, longitud;
    FusedLocationProviderClient fusedLocationClient;
    String currentPhotoPath;
    Bitmap signatureBitmap;
    boolean modoEditar = false;
    int contactoIdEditar = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        signaturePad = findViewById(R.id.signaturePad);
        btncontactos = findViewById(R.id.btncontactos);
        btnsalvar = findViewById(R.id.btnsalvar);
        btnfirma = findViewById(R.id.btnfirma);
        nombre = findViewById(R.id.txtnombre);
        telefono = findViewById(R.id.txttelefono);
        latitud = findViewById(R.id.txtlatitud);
        longitud = findViewById(R.id.txtlongitud);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        if (intent != null && "editar".equals(intent.getStringExtra("modo"))) {
            modoEditar = true;
            contactoIdEditar = intent.getIntExtra("id", -1);

            if (contactoIdEditar != -1) {
                String url = "http://10.0.2.2/crud-exam/GetContactoById.php?id=" + contactoIdEditar;

                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest req = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        response -> {
                            try {
                                nombre.setText(response.getString("nombre"));
                                telefono.setText(response.getString("telefono"));
                                latitud.setText(String.valueOf(response.getDouble("latitud")));
                                longitud.setText(String.valueOf(response.getDouble("longitud")));

                                String firmaBase64 = response.getString("firmaBase64");
                                if (firmaBase64 != null && !firmaBase64.isEmpty()) {
                                    byte[] bytes = Base64.decode(firmaBase64, Base64.DEFAULT);
                                    signatureBitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    signaturePad.setSignatureBitmap(signatureBitmap);
                                }

                            } catch (JSONException e) {
                                Toast.makeText(this, "Error al cargar datos del contacto", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        },
                        error -> Toast.makeText(this, "Error en conexión: " + error.getMessage(), Toast.LENGTH_LONG).show()
                );

                queue.add(req);
            }
        }

        btnfirma.setOnClickListener(v -> {
            if (signaturePad.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, firme primero", Toast.LENGTH_SHORT).show();
                return;
            }
            signatureBitmap = signaturePad.getSignatureBitmap();
            guardarfirma();
        });

        btnsalvar.setOnClickListener(v -> {
            if (validarInputs()) {
                checkLocationPermissionAndFetch();
            }
        });

        btncontactos.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), ContactoList.class);
            startActivity(intent1);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        signatureBitmap = null;
    }

    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            fetchLocationAndSave();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndSave();
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchLocationAndSave() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitud.setText(String.valueOf(location.getLatitude()));
                        longitud.setText(String.valueOf(location.getLongitude()));
                        guardarContacto();
                    } else {
                        Toast.makeText(this, "Ubicación no disponible, intentando fallback...", Toast.LENGTH_SHORT).show();
                        requestLocationUpdatesFallback();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error de ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void requestLocationUpdatesFallback() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                Location loc = result.getLastLocation();
                if (loc != null) {
                    latitud.setText(String.valueOf(loc.getLatitude()));
                    longitud.setText(String.valueOf(loc.getLongitude()));
                    guardarContacto();
                } else {
                    Toast.makeText(MainActivity.this, "Ubicación aún no disponible", Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());
    }

    private void guardarContacto() {
        if (signatureBitmap == null && !signaturePad.isEmpty()) {
            signatureBitmap = signaturePad.getSignatureBitmap();
        }

        if (signatureBitmap == null) {
            Toast.makeText(this, "Debe proporcionar una firma", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = modoEditar
                ? "http://10.0.2.2/crud-exam/updateContacto.php?id=" + contactoIdEditar
                : "http://10.0.2.2/crud-exam/PostContacto.php";

        int metodo = modoEditar ? Request.Method.PUT : Request.Method.POST;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nombre", nombre.getText().toString().trim());
            jsonBody.put("telefono", telefono.getText().toString().trim());
            jsonBody.put("latitud", Double.parseDouble(latitud.getText().toString()));
            jsonBody.put("longitud", Double.parseDouble(longitud.getText().toString()));

            // Convertir firma a Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
            String firmaBase64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
            jsonBody.put("firmaBase64", firmaBase64);
        } catch (JSONException | NumberFormatException e) {
            Toast.makeText(this, "Error creando datos del contacto", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                metodo,
                url,
                jsonBody,
                response -> {
                    Toast.makeText(this, modoEditar ? "Contacto actualizado" : "Contacto guardado", Toast.LENGTH_SHORT).show();
                    nombre.setText("");
                    telefono.setText("");
                    latitud.setText("");
                    longitud.setText("");
                    signaturePad.clear();
                    signatureBitmap = null;
                },
                error -> Toast.makeText(this, "Error al guardar: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }

    private boolean validarInputs() {
        if (nombre.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingrese el Nombre", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (telefono.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingrese un teléfono", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void guardarfirma() {
        try {
            File file = crearImagen();
            FileOutputStream fos = new FileOutputStream(file);
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            currentPhotoPath = file.getAbsolutePath();
            Toast.makeText(this, "Firma guardada", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la firma", Toast.LENGTH_SHORT).show();
        }
    }

    private File crearImagen() throws IOException {
        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreImg = "Firma_" + fecha + ".png";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, nombreImg);
    }


}
