package com.example.docshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private ImageView bt_nova_os_manutencao, bt_historico_de_atividades, bt_configuracoes;
    private TextView txt_nome_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        IniciarComponentes();

        // Por enquanto vou usar com a função de deslogar
        bt_configuracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Usuário deslogado", Toast.LENGTH_LONG).show();
                Intent back_to_login = new Intent(getApplicationContext(), FormLogin.class);
                startActivity(back_to_login);
            }
        });


    }


    private void IniciarComponentes(){
        bt_nova_os_manutencao = findViewById(R.id.image_bt_nova_os);
        bt_historico_de_atividades = findViewById(R.id.image_historico);
        bt_configuracoes = findViewById(R.id.image_configuracoes);

        txt_nome_user = findViewById(R.id.txt_boas_vindas);

    }
}