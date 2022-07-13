package com.example.docshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FormLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        getSupportActionBar().hide();   // Esconder barra de ação


    }

    public void goToCadastro(View view){
        Intent goToFormCadastroActivity = new Intent(getApplicationContext(), FormCadastro.class);
        startActivity(goToFormCadastroActivity);
    }
}