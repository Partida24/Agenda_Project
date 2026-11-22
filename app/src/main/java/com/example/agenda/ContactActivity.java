package com.example.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private TextView textTitle;
    private EditText inputNom;
    private EditText inputCognom;
    private EditText inputNumero;
    private EditText inputData;
    private EditText inputAdreca;
    private EditText inputEmail;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Relacionamos las variables con el XML
        textTitle = findViewById(R.id.textTitle);
        inputNom = findViewById(R.id.inputNom);
        inputCognom = findViewById(R.id.inputCognom);
        inputNumero = findViewById(R.id.inputNumero);
        inputData = findViewById(R.id.inputData);
        inputAdreca = findViewById(R.id.inputAdreca);
        inputEmail = findViewById(R.id.inputEmail);

        Button btnAcceptar = findViewById(R.id.btnAcceptar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // Miramos si estamos creando o editando
        String modo = getIntent().getStringExtra("modo");

        // Guardamos la posición del contacto
        position = getIntent().getIntExtra("position", -1);

        if ("editar".equals(modo)) {
            prepararEdicio();
        } else {
            textTitle.setText("NOU CONTACTE");
        }

        // Guardamos los datos
        btnAcceptar.setOnClickListener(v -> guardarContacte());

        // Cerramos sin guardar
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void prepararEdicio() {

        textTitle.setText("EDITAR CONTACTE");

        // Mostramos los datos recibidos
        inputNom.setText(getIntent().getStringExtra("nom"));
        inputCognom.setText(getIntent().getStringExtra("cognom"));
        inputNumero.setText(getIntent().getStringExtra("numero"));
        inputData.setText(getIntent().getStringExtra("dataNaix"));
        inputAdreca.setText(getIntent().getStringExtra("adreca"));
        inputEmail.setText(getIntent().getStringExtra("email"));
    }

    private void guardarContacte() {

        // Recogemos los datos del formulario
        String nom = inputNom.getText().toString().trim();
        String cognom = inputCognom.getText().toString().trim();
        String numero = inputNumero.getText().toString().trim();
        String dataNaix = inputData.getText().toString().trim();
        String adreca = inputAdreca.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();

        // Comprobamos los campos
        if (!validarDades(nom, cognom, numero, email)) {
            return;
        }

        // Preparamos los datos para MainActivity
        Intent resultat = new Intent();

        resultat.putExtra("nom", nom);
        resultat.putExtra("cognom", cognom);
        resultat.putExtra("numero", numero);
        resultat.putExtra("dataNaix", dataNaix);
        resultat.putExtra("adreca", adreca);
        resultat.putExtra("email", email);
        resultat.putExtra("position", position);

        setResult(Activity.RESULT_OK, resultat);
        finish();
    }

    private boolean validarDades(
            String nom,
            String cognom,
            String numero,
            String email
    ) {

        boolean dadesCorrectes = true;

        // Borramos errores anteriores
        inputNom.setError(null);
        inputCognom.setError(null);
        inputNumero.setError(null);
        inputEmail.setError(null);

        if (nom.isEmpty()) {
            inputNom.setError("El nom és obligatori");
            dadesCorrectes = false;
        }

        if (cognom.isEmpty()) {
            inputCognom.setError("El cognom és obligatori");
            dadesCorrectes = false;
        }

        if (numero.isEmpty()) {
            inputNumero.setError("El telèfon és obligatori");
            dadesCorrectes = false;
        }

        // El email es opcional, pero si se escribe debe ser válido
        if (!email.isEmpty()
                && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            inputEmail.setError("El correu electrònic no és vàlid");
            dadesCorrectes = false;
        }

        return dadesCorrectes;
    }
}