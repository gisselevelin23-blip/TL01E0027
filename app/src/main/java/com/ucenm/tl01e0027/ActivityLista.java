package com.ucenm.tl01e0027;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ActivityLista extends AppCompatActivity {
    SQLiteConexion conexion;
    ListView listView;
    ArrayList<Contactos> listaContactos;
    ArrayList<String> listaConcatenada;
    Contactos contactoSeleccionado;
    int index = -1;
    ArrayAdapter<String> adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        conexion = new SQLiteConexion(this);
        listView = findViewById(R.id.listViewContactos);
        EditText txtBuscar = findViewById(R.id.txtBuscar);

        obtenerLista();

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adp != null) adp.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            index = i;
            String itemSeleccionado = adp.getItem(i);
            for(Contactos c : listaContactos){
                if((c.getNombre() + " | " + c.getTelefono()).equals(itemSeleccionado)){
                    contactoSeleccionado = c;
                    break;
                }
            }
            mostrarDetallesContacto();
        });

        findViewById(R.id.btnCompartir).setOnClickListener(v -> compartir());
        findViewById(R.id.btnVerImagen).setOnClickListener(v -> verFoto());
        findViewById(R.id.btnEliminar).setOnClickListener(v -> eliminar());

        findViewById(R.id.btnActualizar).setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("id", contactoSeleccionado.getId());
            i.putExtra("nombre", contactoSeleccionado.getNombre());
            i.putExtra("telefono", contactoSeleccionado.getTelefono());
            i.putExtra("nota", contactoSeleccionado.getNota());
            i.putExtra("pais", contactoSeleccionado.getPais());
            i.putExtra("imagen", contactoSeleccionado.getImagen());
            startActivity(i);
        });

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
    }

    private void mostrarDetallesContacto() {
        if (contactoSeleccionado == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(contactoSeleccionado.getNombre());

        String info = "Teléfono: " + contactoSeleccionado.getTelefono() +
                "\nPaís: " + contactoSeleccionado.getPais() +
                "\nNota: " + contactoSeleccionado.getNota();
        builder.setMessage(info);

        if (contactoSeleccionado.getImagen() != null) {
            byte[] img = contactoSeleccionado.getImagen();
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            ImageView iv = new ImageView(this);
            iv.setPadding(0, 30, 0, 0);
            iv.setImageBitmap(bmp);
            builder.setView(iv);
        }

        builder.setPositiveButton("Llamar", (d, w) -> mostrarDialogoLlamada());
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }

    // MÉTODO ACTUALIZADO: Cumple con Intent ACTION_CALL y AlertDialog
    private void mostrarDialogoLlamada() {
        if (contactoSeleccionado == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Llamada")
                .setMessage("¿Desea llamar a " + contactoSeleccionado.getNombre() + "?")
                .setPositiveButton("Si", (d, w) -> {
                    try {
                        // Requisito: ACTION_CALL
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + contactoSeleccionado.getTelefono()));
                        startActivity(intentCall);
                    } catch (SecurityException e) {
                        // Si falla el permiso, usamos ACTION_DIAL como respaldo
                        Toast.makeText(this, "Falta permiso de llamadas", Toast.LENGTH_SHORT).show();
                        Intent intentDial = new Intent(Intent.ACTION_DIAL);
                        intentDial.setData(Uri.parse("tel:" + contactoSeleccionado.getTelefono()));
                        startActivity(intentDial);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void verFoto() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contactoSeleccionado.getImagen() != null) {
            byte[] img = contactoSeleccionado.getImagen();
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            ImageView iv = new ImageView(this);
            iv.setImageBitmap(bmp);
            new AlertDialog.Builder(this).setTitle("Foto de " + contactoSeleccionado.getNombre())
                    .setView(iv).setPositiveButton("Cerrar", null).show();
        } else {
            Toast.makeText(this, "No hay imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void compartir() {
        if (contactoSeleccionado == null) return;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "Contacto: " + contactoSeleccionado.getNombre() + "\nTel: " + contactoSeleccionado.getTelefono());
        startActivity(Intent.createChooser(i, "Compartir vía"));
    }

    private void eliminar() {
        if (contactoSeleccionado == null) return;
        new AlertDialog.Builder(this).setTitle("Eliminar")
                .setMessage("¿Seguro que desea eliminar a " + contactoSeleccionado.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    SQLiteDatabase db = conexion.getWritableDatabase();
                    db.delete("contactos", "id=?", new String[]{String.valueOf(contactoSeleccionado.getId())});
                    db.close();
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    contactoSeleccionado = null;
                    obtenerLista();
                }).setNegativeButton("Cancelar", null).show();
    }

    private void obtenerLista() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        listaContactos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM contactos", null);

        while (cursor.moveToNext()) {
            Contactos c = new Contactos();
            c.setId(cursor.getInt(0));
            c.setPais(cursor.getString(1));
            c.setNombre(cursor.getString(2));
            c.setTelefono(cursor.getString(3));
            c.setNota(cursor.getString(4));
            c.setImagen(cursor.getBlob(5));
            listaContactos.add(c);
        }
        cursor.close();
        llenarListView();
    }

    private void llenarListView() {
        listaConcatenada = new ArrayList<>();
        for (Contactos c : listaContactos) {
            listaConcatenada.add(c.getNombre() + " | " + c.getTelefono());
        }
        adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaConcatenada);
        listView.setAdapter(adp);
        index = -1;
    }
}