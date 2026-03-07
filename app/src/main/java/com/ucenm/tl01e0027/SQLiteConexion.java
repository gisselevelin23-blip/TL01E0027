package com.ucenm.tl01e0027;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class SQLiteConexion extends SQLiteOpenHelper {

    public static final String NameDatabase = "DB_Contactos";
    public static final int version = 1;

    public static final String CREATE_TABLE_CONTACTOS = "CREATE TABLE contactos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "pais TEXT, " +
            "nombre TEXT, " +
            "telefono TEXT, " +
            "nota TEXT, " +
            "imagen BLOB)";

    public SQLiteConexion(@Nullable Context context) {
        super(context, NameDatabase, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contactos");
        onCreate(db);
    }
}
