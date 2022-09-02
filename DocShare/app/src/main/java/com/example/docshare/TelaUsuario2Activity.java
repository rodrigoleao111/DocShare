package com.example.docshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.docshare.fragments.ConfiguracoesFragment;
import com.example.docshare.fragments.HistoricoFragment;
import com.example.docshare.fragments.InicioFragment;
import com.example.docshare.usuario.FormLogin;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class TelaUsuario2Activity extends AppCompatActivity {

    private InicioFragment inicioFragment = new InicioFragment();
    private HistoricoFragment historicoFragment = new HistoricoFragment();
    private ConfiguracoesFragment configuracoesFragment = new ConfiguracoesFragment();
    private BottomNavigationView bottomNavigationView;
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    private Button buttonSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_usuario2);

        getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, inicioFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch ((item.getItemId())){
                    case R.id.inicio:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, inicioFragment).commit();
                        break;
                    case R.id.historico:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, historicoFragment).commit();
                        break;
                    case R.id.configuracoes:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, configuracoesFragment).commit();
                        break;
                }

                return true;
            }
        });


    }

}