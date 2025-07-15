package com.example.examen2parcial.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class Contactos {
    private int id;
    private String nombre;
    private String telefono;
    private double latitud;
    private double longitud;
    private String firmaBase64;

    public Contactos() {}

    // Constructor completo
    public Contactos(int id, String nombre, String telefono, double latitud, double longitud, String firmaBase64) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.firmaBase64 = firmaBase64;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFirmaBase64() {
        return firmaBase64;
    }

    public void setFirmaBase64(String firmaBase64) {
        this.firmaBase64 = firmaBase64;
    }


    public String toJsonString() {
        JSONObject o = new JSONObject();
        try {
            o.put("id", id);
            o.put("nombre", nombre);
            o.put("telefono", telefono);
            o.put("latitud", latitud);
            o.put("longitud", longitud);
            o.put("firmaBase64", firmaBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o.toString();
    }


    public static Contactos fromJsonString(String json) {
        Contactos c = new Contactos();
        try {
            JSONObject o = new JSONObject(json);
            c.setId(o.getInt("id"));
            c.setNombre(o.getString("nombre"));
            c.setTelefono(o.getString("telefono"));
            c.setLatitud(o.getDouble("latitud"));
            c.setLongitud(o.getDouble("longitud"));
            c.setFirmaBase64(o.getString("firmaBase64"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }
}
