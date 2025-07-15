package com.example.examen2parcial.Config;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.examen2parcial.MainActivity;
import com.example.examen2parcial.R;

import java.util.ArrayList;
import java.util.List;

public class ContactoAdaptador extends RecyclerView.Adapter<ContactoAdaptador.VistaContacto> {

    private List<Contactos> listaOriginal;
    private List<Contactos> listaFiltrada;
    private Context ctx;

    public ContactoAdaptador(Context contexto, List<Contactos> contactos) {
        this.ctx = contexto;
        this.listaOriginal = new ArrayList<>(contactos);
        this.listaFiltrada = new ArrayList<>(contactos);
    }

    @NonNull
    @Override
    public VistaContacto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(ctx).inflate(R.layout.activity_item_contacto, parent, false);
        return new VistaContacto(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull VistaContacto holder, int posicion) {
        Contactos c = listaFiltrada.get(posicion);
        holder.textoNombre.setText(c.getNombre());
        holder.textoTelefono.setText(c.getTelefono());

        // Mostrar la firma si existe
        if (c.getFirmaBase64() != null && !c.getFirmaBase64().isEmpty()) {
            byte[] imagenBytes = Base64.decode(c.getFirmaBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            holder.imagenFirma.setImageBitmap(bitmap);
        } else {
            holder.imagenFirma.setImageBitmap(null);  // Imagen por defecto opcional
        }

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(ctx)
                    .setTitle("Opciones para " + c.getNombre())
                    .setItems(new CharSequence[]{"Editar", "Eliminar"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent i = new Intent(ctx, MainActivity.class);
                            i.putExtra("modo", "editar");
                            i.putExtra("id", c.getId());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(i);
                        } else {
                            eliminarContacto(c.getId(), posicion);
                        }
                    })
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public void actualizarLista(List<Contactos> nuevaLista) {
        this.listaOriginal = nuevaLista;
        this.listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    public void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            for (Contactos c : listaOriginal) {
                if (c.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class VistaContacto extends RecyclerView.ViewHolder {
        TextView textoNombre, textoTelefono;
        ImageView imagenFirma;

        public VistaContacto(@NonNull View itemView) {
            super(itemView);
            textoNombre = itemView.findViewById(R.id.texto_nombre);
            textoTelefono = itemView.findViewById(R.id.texto_telefono);
            imagenFirma = itemView.findViewById(R.id.imagen_firma); // Imagen
        }
    }

    private void eliminarContacto(int id, int posicion) {
        String url = "http://10.0.2.2/crud-exam/DeleteContacto.php?id=" + id;

        StringRequest solicitud = new StringRequest(Request.Method.DELETE, url,
                respuesta -> {
                    listaOriginal.remove(posicion);
                    filtrar(""); // refrescar lista
                    Toast.makeText(ctx, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(ctx, "Error al eliminar: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(ctx).add(solicitud);
    }
}
