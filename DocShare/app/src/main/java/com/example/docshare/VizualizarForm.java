package com.example.docshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class VizualizarForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizar_form);

        Intent intentReceberForm = getIntent();
        Bundle dadosOS = intentReceberForm.getExtras();
    }
}