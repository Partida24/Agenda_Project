package com.example.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AgendaBD extends SQLiteOpenHelper {

    private static final String DB_NAME = "agenda.db";
    private static final int DB_VERSION = 1;

    public AgendaBD(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contactes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT, " +
                "cognom TEXT, " +
                "numero TEXT, " +
                "dataNaix TEXT, " +
                "adreca TEXT, " +
                "email TEXT)");

        // Contactes de prova inicials
        db.execSQL("INSERT INTO contactes (nom, cognom, numero, dataNaix, adreca, email) VALUES " +
                "('Adam', 'Partida', '612345678', '12/05/2000', 'Barcelona', 'adam@gmail.com'), " +
                "('Adrian', 'Herrera', '623456789', '20/08/1999', 'Sabadell', 'adrian@gmail.com'), " +
                "('Ivan', 'Vera', '611111771', '10/10/1997', 'Sant Quirze', 'ivan@gmail.com')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contactes");
        onCreate(db);
    }

    public ArrayList<Contacte> obtenerContactos() {
        ArrayList<Contacte> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, nom, cognom, numero, dataNaix, adreca, email FROM contactes ORDER BY nom, cognom", null);
        try {
            while (c.moveToNext()) {
                lista.add(new Contacte(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6)
                ));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public Contacte obtenerContacto(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, nom, cognom, numero, dataNaix, adreca, email FROM contactes WHERE id=?", new String[]{String.valueOf(id)});
        try {
            if (c.moveToFirst()) {
                return new Contacte(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6)
                );
            }
        } finally {
            c.close();
        }
        return null;
    }

    public long guardar(Contacte contacte) {
        ContentValues v = valores(contacte);
        SQLiteDatabase db = getWritableDatabase();
        if (contacte.getId() == -1) {
            long id = db.insert("contactes", null, v);
            if (id != -1) {
                contacte.setId((int) id);
            }
            return id;
        } else {
            return db.update("contactes", v, "id=?", new String[]{String.valueOf(contacte.getId())});
        }
    }

    public void eliminar(int id) {
        getWritableDatabase().delete("contactes", "id=?", new String[]{String.valueOf(id)});
    }

    private ContentValues valores(Contacte c) {
        ContentValues v = new ContentValues();
        v.put("nom", c.getNom());
        v.put("cognom", c.getCognom());
        v.put("numero", c.getNumero());
        v.put("dataNaix", c.getDataNaix());
        v.put("adreca", c.getAdreca());
        v.put("email", c.getEmail());
        return v;
    }
}
