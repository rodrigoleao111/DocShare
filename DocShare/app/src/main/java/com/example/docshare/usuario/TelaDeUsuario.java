package com.example.docshare.usuario;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docshare.R;
import com.example.docshare.formularios.FormOSManutencaoCorretiva;
import com.example.docshare.metodos.ImageHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;

public class TelaDeUsuario extends AppCompatActivity implements ImageHelper {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private ImageView bt_nova_os_manutencao, bt_historico_de_atividades, bt_configuracoes, profilePic;
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

        // Seguir para FormOS
        bt_nova_os_manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent goToFormOsActivity = new Intent(getApplicationContext(), FormOSManutencaoCorretiva.class);
                    startActivity(goToFormOsActivity);
                } else {
                    requestPermission();
                }

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
                    String[] nomeUser = documentSnapshot.getString("nome").split("\\s");
                    ola = "Olá, " + nomeUser[0];
                    boas_vindas.setText(ola);
                    try {
                        Bitmap profilePicBitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver() ,
                                Uri.parse(documentSnapshot.getString("profilePicUri")));
                        profilePic.setImageBitmap(ImageHelper.getRoundedCornerBitmap(profilePicBitmap, 10));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //profilePic.setImageURI(Uri.parse(documentSnapshot.getString("profilePicUri")));
                }
            }
        });
    }

    private void IniciarComponentes(){
        bt_nova_os_manutencao = findViewById(R.id.image_bt_nova_os);
        bt_historico_de_atividades = findViewById(R.id.image_historico);
        bt_configuracoes = findViewById(R.id.image_configuracoes);
        profilePic = findViewById(R.id.profilePic);
        boas_vindas = findViewById(R.id.txt_boas_vindas);

    }

    // Checagem de permissão para ler e escrever no armazenamento externo
    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    // Solicitar permissão
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // Alterar condição da permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}