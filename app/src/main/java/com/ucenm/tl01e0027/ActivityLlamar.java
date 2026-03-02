package com.ucenm.tl01e0027;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityLlamar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        TextView tvNumero = findViewById(R.id.tvNumeroLlamar);
        String nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");

        if (telefono != null) {
            tvNumero.setText(telefono);
        }

        findViewById(R.id.btnAtrasLlamar).setOnClickListener(v -> finish());

        findViewById(R.id.btnIconoLlamar).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar Llamada")
                    .setMessage("¿Desea llamar a " + (nombre != null ? nombre : "este número") + "?")
                    .setPositiveButton("Si", (dialog, which) -> {
                        try {
                            // Marcar utilizando el intent ACTION_CALL
                            Intent intentCall = new Intent(Intent.ACTION_CALL);
                            intentCall.setData(Uri.parse("tel:" + telefono));
                            startActivity(intentCall);
                        } catch (SecurityException e) {
                            Toast.makeText(this, "Debe otorgar permisos de llamada en configuración", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}