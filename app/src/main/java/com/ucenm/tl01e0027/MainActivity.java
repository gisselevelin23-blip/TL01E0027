package com.ucenm.tl01e0027;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner comboPais;
    EditText txtNombre, txtTelefono, txtNota;
    Button btnSalvar, btnContactos;
    ImageView imageView;
    ImageButton btnAgregarPais; // Nuevo botón

    byte[] imagenEnBytes;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Variables para países dinámicos
    ArrayList<String> listaPaises;
    ArrayAdapter<String> adaptadorPaises;

    // Variables para actualización
    int idRecibido = -1;
    boolean esEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de controles
        comboPais = findViewById(R.id.comboPais);
        txtNombre = findViewById(R.id.nombres);
        txtTelefono = findViewById(R.id.telefono);
        txtNota = findViewById(R.id.notas);
        btnSalvar = findViewById(R.id.btnsalvarcontacto);
        btnContactos = findViewById(R.id.btncontactossalvados);
        imageView = findViewById(R.id.imageViewContacto);
        btnAgregarPais = findViewById(R.id.btnAgregarPais); //

        // --- CONFIGURACIÓN DE LISTA DE PAÍSES ---
        listaPaises = new ArrayList<>();
        listaPaises.add("+504 - Honduras");
        listaPaises.add("+502 - Guatemala");
        listaPaises.add("+503 - El Salvador");
        listaPaises.add("+505 - Nicaragua");
        listaPaises.add("+506 - Costa Rica");
        listaPaises.add("+507 - Panamá");
        listaPaises.add("+501 - Belice");

        adaptadorPaises = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaPaises);
        adaptadorPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboPais.setAdapter(adaptadorPaises);

        // --- RECIBIR DATOS PARA ACTUALIZAR ---
        if (getIntent().hasExtra("id")) {
            esEdicion = true;
            idRecibido = getIntent().getIntExtra("id", -1);
            txtNombre.setText(getIntent().getStringExtra("nombre"));
            txtTelefono.setText(getIntent().getStringExtra("telefono"));
            txtNota.setText(getIntent().getStringExtra("nota"));

            imagenEnBytes = getIntent().getByteArrayExtra("imagen");
            if(imagenEnBytes != null){
                Bitmap bmp = BitmapFactory.decodeByteArray(imagenEnBytes, 0, imagenEnBytes.length);
                imageView.setImageBitmap(bmp);
            }
            btnSalvar.setText("Actualizar Contacto");
        }

        // --- EVENTOS ---

        // Botón para agregar país nuevo
        btnAgregarPais.setOnClickListener(v -> mostrarDialogoNuevoPais());

        imageView.setOnClickListener(v -> abrirCamara());

        btnSalvar.setOnClickListener(v -> validarDatos());

        btnContactos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityLista.class);
            startActivity(intent);
        });
    }

    private void mostrarDialogoNuevoPais() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo País");

        final EditText input = new EditText(this);
        input.setHint("Ej: +506 - Costa Rica");
        builder.setView(input);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String nuevo = input.getText().toString().trim();
            if (!nuevo.isEmpty()) {
                listaPaises.add(nuevo);
                adaptadorPaises.notifyDataSetChanged();
                comboPais.setSelection(listaPaises.size() - 1);
                Toast.makeText(this, "País agregado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void abrirCamara() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imagenEnBytes = stream.toByteArray();
        }
    }

    private void validarDatos() {
        if (txtNombre.getText().toString().isEmpty()) {
            txtNombre.setError("Escriba un nombre");
        } else if (txtTelefono.getText().toString().isEmpty()) {
            txtTelefono.setError("Escriba un teléfono");
        } else if (imagenEnBytes == null) {
            Toast.makeText(this, "Tome una fotografía", Toast.LENGTH_SHORT).show();
        } else {
            guardarContacto();
        }
    }

    private void guardarContacto() {
        try {
            SQLiteConexion conexion = new SQLiteConexion(this);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put("pais", comboPais.getSelectedItem().toString());
            valores.put("nombre", txtNombre.getText().toString());
            valores.put("telefono", txtTelefono.getText().toString());
            valores.put("nota", txtNota.getText().toString());
            valores.put("imagen", imagenEnBytes);

            if (esEdicion) {
                db.update("contactos", valores, "id=?", new String[]{String.valueOf(idRecibido)});
                Toast.makeText(this, "Contacto Actualizado", Toast.LENGTH_LONG).show();
                finish();
            } else {
                db.insert("contactos", null, valores);
                Toast.makeText(this, "Contacto Salvado", Toast.LENGTH_LONG).show();
                limpiarPantalla();
            }
            db.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarPantalla() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
        imageView.setImageResource(android.R.drawable.ic_menu_camera);
        imagenEnBytes = null;
        comboPais.setSelection(0);
        esEdicion = false;
        idRecibido = -1;
        btnSalvar.setText("Salvar Contacto");
    }
}