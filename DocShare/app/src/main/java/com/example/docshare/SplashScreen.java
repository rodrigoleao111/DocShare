package com.example.docshare;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.example.docshare.metodos.UserInfo;
import com.example.docshare.usuario.FormLogin;
import com.example.docshare.usuario.TelaDeUsuario;
import com.example.docshare.usuario.TelaUsuario2Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent;

                if(usuarioAtual != null) {
                    intent = new Intent(getApplicationContext(), TelaUsuario2Activity.class);
                    InstanciarFirebase();
                }
                else
                    intent = new Intent(SplashScreen.this, FormLogin.class);

                //checkAppDir();
                startActivity(intent);
                finish();
            }
        },1000);

    }

    private void InstanciarFirebase() {
        FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);
        UserInfo.setUserCredentials(documentReference, userID);
    }

    /*
    private void checkAppDir() {
        if (checkPermission()) {
            String folderName = "DocShare";
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName);
            if(!folder.exists()){
                if(folder.mkdir())
                    paths.putString("rootDir", folder.getAbsolutePath());
                Toast.makeText(getApplicationContext(), "sucesso ao criar pasta", Toast.LENGTH_SHORT).show();
                CriarPastasDoApp(folder);
            } else {
                Toast.makeText(getApplicationContext(), folder.getAbsolutePath(), Toast.LENGTH_LONG).show();
                paths.putString("rootDir", folder.getAbsolutePath());
                //paths.putString("userDir", userFolder.getAbsolutePath());
                //paths.putString("osDir", osFolder.getAbsolutePath());
                //paths.putString("imagesDir", imagesFolder.getAbsolutePath());
            }
        } else {
            requestPermission();
        }

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

     */

}