package com.example.docshare.usuario;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.docshare.R;
import com.example.docshare.metodos.ImageHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.Objects;

public class ConfiguracoesDeUsuario extends AppCompatActivity {

    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);

    private EditText edtnome, edtcpf, edtrg, edttelefone;
    private TextView tvcargo, tvsetor, tvUserId;
    private Button btAlterarDados, btMudarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_de_usuario);

        getSupportActionBar().hide();
        IniciarComponentes();


    }

    private void IniciarComponentes() {
        edtnome = findViewById(R.id.edtNomeConfig);
        edtcpf = findViewById(R.id.edtCpfConfig);
        edtrg = findViewById(R.id.edtRgConfig);
        edttelefone = findViewById(R.id.edtTelefoneConfig);
        tvcargo = findViewById(R.id.edtCargoConfig);
        tvsetor = findViewById(R.id.edtSetorConfig);
        btAlterarDados = findViewById(R.id.btAlterarDados);
        btMudarSenha = findViewById(R.id.btMudarSenha);

        tvUserId = findViewById(R.id.tvUserIdConfig);
        tvUserId.setText(userID);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    edtnome.setText(documentSnapshot.getString("nome"));
                    edtcpf.setText(documentSnapshot.getString("cpf"));
                    edtrg.setText(documentSnapshot.getString("rg"));
                    edttelefone.setText(documentSnapshot.getString("telefone"));
                    tvcargo.setText(documentSnapshot.getString("cargo"));
                    tvsetor.setText(documentSnapshot.getString("setor"));

                    if(!Objects.equals(documentSnapshot.getString("profilePicUri"), "void")){
                        // Atribuir uri (string) para imageview
                    }
                }
            }
        });
    }
}