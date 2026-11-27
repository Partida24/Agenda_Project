package com.example.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CREAR = 1;
    private static final int REQUEST_EDITAR = 2;

    private AgendaBD bdHelper;
    private ArrayList<Contacte> contactes;
    private AdaptadorContactes adaptador;
    private EditText inputBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bdHelper = new AgendaBD(this);
        contactes = bdHelper.obtenerContactos();
        adaptador = new AdaptadorContactes(this, contactes);

        ListView llistaContactes = findViewById(R.id.myListView);
        llistaContactes.setAdapter(adaptador);

        inputBuscar = findViewById(R.id.inputBuscar);
        findViewById(R.id.addPerson).setOnClickListener(v -> obrirNouContacte());

        llistaContactes.setOnItemClickListener((parent, view, position, id) -> {
            Contacte contacte = adaptador.getItem(position);
            if (contacte != null) obrirContactePerEditar(contacte);
        });

        llistaContactes.setOnItemLongClickListener((parent, view, position, id) -> {
            Contacte contacte = adaptador.getItem(position);
            if (contacte != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar contacte")
                        .setMessage("Vols eliminar a " + contacte.getNom() + " " + contacte.getCognom() + "?")
                        .setNegativeButton("Cancel·lar", null)
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            bdHelper.eliminar(contacte.getId());
                            actualitzarLlista();
                        })
                        .show();
            }
            return true;
        });

        inputBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { adaptador.getFilter().filter(s); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void obrirNouContacte() {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("modo", "crear");
        startActivityForResult(intent, REQUEST_CREAR);
    }

    private void obrirContactePerEditar(Contacte contacte) {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("modo", "editar");
        intent.putExtra("nom", contacte.getNom());
        intent.putExtra("cognom", contacte.getCognom());
        intent.putExtra("numero", contacte.getNumero());
        intent.putExtra("dataNaix", contacte.getDataNaix());
        intent.putExtra("adreca", contacte.getAdreca());
        intent.putExtra("email", contacte.getEmail());
        intent.putExtra("position", contactes.indexOf(contacte));
        startActivityForResult(intent, REQUEST_EDITAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        String nom = data.getStringExtra("nom");
        String cognom = data.getStringExtra("cognom");
        String numero = data.getStringExtra("numero");
        String dataNaix = data.getStringExtra("dataNaix");
        String adreca = data.getStringExtra("adreca");
        String email = data.getStringExtra("email");

        if (requestCode == REQUEST_CREAR) {
            bdHelper.guardar(new Contacte(nom, cognom, numero, dataNaix, adreca, email));
        } else if (requestCode == REQUEST_EDITAR) {
            int position = data.getIntExtra("position", -1);
            if (position >= 0 && position < contactes.size()) {
                Contacte c = contactes.get(position);
                c.setNom(nom);
                c.setCognom(cognom);
                c.setNumero(numero);
                c.setDataNaix(dataNaix);
                c.setAdreca(adreca);
                c.setEmail(email);
                bdHelper.guardar(c);
            }
        }
        actualitzarLlista();
    }

    private void actualitzarLlista() {
        contactes.clear();
        contactes.addAll(bdHelper.obtenerContactos());
        adaptador.getFilter().filter(inputBuscar.getText().toString());
    }

    @Override
    protected void onDestroy() {
        if (bdHelper != null) bdHelper.close();
        super.onDestroy();
    }
}
