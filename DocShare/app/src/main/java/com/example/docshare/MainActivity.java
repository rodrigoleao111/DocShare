package com.example.docshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private ImageView bt_nova_os_manutencao, bt_historico_de_atividades, bt_configuracoes;
    private TextView boas_vindas;
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID, ola;

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

        bt_nova_os_manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFormOsActivity = new Intent(getApplicationContext(), FormularioOSManutencaoCorretiva.class);
                startActivity(goToFormOsActivity);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);

        // Texto de Boas Vindas
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    ola = "Olá, " + documentSnapshot.getString("nome");
                    boas_vindas.setText(ola);
                }
            }
        });
    }

    private void IniciarComponentes(){
        bt_nova_os_manutencao = findViewById(R.id.image_bt_nova_os);
        bt_historico_de_atividades = findViewById(R.id.image_historico);
        bt_configuracoes = findViewById(R.id.image_configuracoes);

        boas_vindas = findViewById(R.id.txt_boas_vindas);

    }
}