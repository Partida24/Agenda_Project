
package com.example.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Códigos para saber si creamos o editamos
    private static final int REQUEST_CREAR = 1;
    private static final int REQUEST_EDITAR = 2;

    private ArrayList<Contacte> contactes;
    private AdaptadorContactes adaptador;

    private EditText inputBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Relacionamos las variables con el XML
        ImageButton addPerson = findViewById(R.id.addPerson);
        inputBuscar = findViewById(R.id.inputBuscar);
        ListView llistaContactes = findViewById(R.id.myListView);

        // Recuperamos los contactos guardados
        contactes = carregarContactes();

        // Si no hay contactos guardados, añadimos los de prueba
        if (contactes.isEmpty()) {
            afegirContactesProva();
            guardarContactes();
        }

        // Preparamos el adaptador
        adaptador = new AdaptadorContactes(this, contactes);
        llistaContactes.setAdapter(adaptador);

        // Abrimos el formulario para crear
        addPerson.setOnClickListener(v -> obrirNouContacte());

        // Abrimos el contacto seleccionado
        llistaContactes.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Contacte contacte = adaptador.getItem(position);

                    if (contacte != null) {
                        obrirContactePerEditar(contacte);
                    }
                }
        );

        // Filtramos mientras escribimos
        inputBuscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                adaptador.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Añadimos contactos de prueba
    private void afegirContactesProva() {

        contactes.add(new Contacte(
                "Adam",
                "Partida",
                "612345678",
                "12/05/2000",
                "Barcelona",
                "adam@gmail.com"
        ));

        contactes.add(new Contacte(
                "Adrian",
                "Herrera",
                "623456789",
                "20/08/1999",
                "Sabadell",
                "adrian@gmail.com"
        ));

        contactes.add(new Contacte(
                "Ivan",
                "Vera",
                "611111771",
                "10/10/1997",
                "Sant Quirze",
                "ivan@gmail.com"
        ));
    }

    // GUARDAR CONTACTOS
    private void guardarContactes() {

        SharedPreferences prefs = getSharedPreferences(
                "AgendaPrefs",
                Context.MODE_PRIVATE
        );

        JSONArray jsonArray = new JSONArray();

        for (Contacte contacte : contactes) {

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("nom", contacte.getNom());
                jsonObject.put("cognom", contacte.getCognom());
                jsonObject.put("adreca", contacte.getAdreca());
                jsonObject.put("telefon", contacte.getNumero());
                jsonObject.put("mail", contacte.getEmail());
                jsonObject.put("dataNaixement", contacte.getDataNaix());

                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        prefs.edit()
                .putString("contactes_json", jsonArray.toString())
                .apply();
    }

    // CARGAMOS LOS CONTACTOS
    private ArrayList<Contacte> carregarContactes() {

        ArrayList<Contacte> llista = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences(
                "AgendaPrefs",
                Context.MODE_PRIVATE
        );

        String json = prefs.getString("contactes_json", null);

        // Si no hay datos guardados, devolvemos la lista vacía
        if (json == null) {
            return llista;
        }

        try {

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String nom = jsonObject.getString("nom");
                String cognom = jsonObject.getString("cognom");
                String numero = jsonObject.getString("telefon");
                String dataNaix = jsonObject.getString("dataNaixement");
                String adreca = jsonObject.getString("adreca");
                String email = jsonObject.getString("mail");

                Contacte contacte = new Contacte(
                        nom,
                        cognom,
                        numero,
                        dataNaix,
                        adreca,
                        email
                );

                llista.add(contacte);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return llista;
    }

    private void obrirNouContacte() {

        Intent intent = new Intent(
                MainActivity.this,
                ContactActivity.class
        );

        // Indicamos que queremos crear
        intent.putExtra("modo", "crear");

        startActivityForResult(intent, REQUEST_CREAR);
    }

    private void obrirContactePerEditar(Contacte contacte) {

        Intent intent = new Intent(
                MainActivity.this,
                ContactActivity.class
        );

        // Indicamos que queremos editar
        intent.putExtra("modo", "editar");

        // Enviamos los datos del contacto
        intent.putExtra("nom", contacte.getNom());
        intent.putExtra("cognom", contacte.getCognom());
        intent.putExtra("numero", contacte.getNumero());
        intent.putExtra("dataNaix", contacte.getDataNaix());
        intent.putExtra("adreca", contacte.getAdreca());
        intent.putExtra("email", contacte.getEmail());

        // Guardamos su posición real
        intent.putExtra(
                "position",
                contactes.indexOf(contacte)
        );

        startActivityForResult(intent, REQUEST_EDITAR);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobamos que se han guardado los datos
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        // Recogemos los datos recibidos
        String nom = data.getStringExtra("nom");
        String cognom = data.getStringExtra("cognom");
        String numero = data.getStringExtra("numero");
        String dataNaix = data.getStringExtra("dataNaix");
        String adreca = data.getStringExtra("adreca");
        String email = data.getStringExtra("email");

        if (requestCode == REQUEST_CREAR) {

            crearContacte(
                    nom,
                    cognom,
                    numero,
                    dataNaix,
                    adreca,
                    email
            );

        } else if (requestCode == REQUEST_EDITAR) {

            int position = data.getIntExtra("position", -1);

            editarContacte(
                    position,
                    nom,
                    cognom,
                    numero,
                    dataNaix,
                    adreca,
                    email
            );
        }

        // Actualizamos la lista manteniendo la búsqueda
        actualitzarLlista();
    }

    private void crearContacte(
            String nom,
            String cognom,
            String numero,
            String dataNaix,
            String adreca,
            String email
    ) {

        Contacte nouContacte = new Contacte(
                nom,
                cognom,
                numero,
                dataNaix,
                adreca,
                email
        );

        contactes.add(nouContacte);

        // Guardamos los contactos
        guardarContactes();
    }

    private void editarContacte(
            int position,
            String nom,
            String cognom,
            String numero,
            String dataNaix,
            String adreca,
            String email
    ) {

        // Comprobamos que la posición exista
        if (position < 0 || position >= contactes.size()) {
            return;
        }

        Contacte contacte = contactes.get(position);

        // Modificamos el contacto
        contacte.setNom(nom);
        contacte.setCognom(cognom);
        contacte.setNumero(numero);
        contacte.setDataNaix(dataNaix);
        contacte.setAdreca(adreca);
        contacte.setEmail(email);

        // Guardamos los contactos modificados
        guardarContactes();
    }

    private void actualitzarLlista() {

        // Aplicamos otra vez la búsqueda actual
        String cercaActual = inputBuscar.getText().toString();

        adaptador.getFilter().filter(cercaActual);
    }

    // Guardamos cuando salimos de la actividad
    @Override
    protected void onPause() {
        super.onPause();

        guardarContactes();
    }
}
