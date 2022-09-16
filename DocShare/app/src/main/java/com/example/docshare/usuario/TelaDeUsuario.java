package com.example.docshare.usuario;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docshare.R;
import com.example.docshare.formularios.FormOSManutencaoCorretiva;
import com.example.docshare.metodos.ImageHelper;
import com.example.docshare.metodos.RequestPermissions;
import com.example.docshare.metodos.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.util.Objects;

public class TelaDeUsuario extends AppCompatActivity implements ImageHelper, RequestPermissions {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private ImageView bt_nova_os_manutencao, bt_historico_de_atividades, bt_configuracoes, profilePic;
    private TextView boas_vindas;
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), ola;
    Bundle paths = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        IniciarComponentes();


        // intent recebedora
        Intent intentRecebida = getIntent();
        boolean firstAccess = intentRecebida.getBooleanExtra("FirstAccess", false);


        // CRIAÇÃO DE PASTAS
        if (RequestPermissions.checkPermission(getApplicationContext())) {
            String folderName = "DocShare";
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName);
            if(!folder.exists()){
                if(folder.mkdir())
                    paths.putString("rootDir", folder.getAbsolutePath());
                    Toast.makeText(getApplicationContext(), "sucesso ao criar pasta", Toast.LENGTH_SHORT).show();
                CriarPastasDoApp(folder);
            } else {
                //Toast.makeText(getApplicationContext(), folder.getAbsolutePath(), Toast.LENGTH_LONG).show();
                paths.putString("rootDir", folder.getAbsolutePath());
                //paths.putString("userDir", userFolder.getAbsolutePath());
                //paths.putString("osDir", osFolder.getAbsolutePath());
                //paths.putString("imagesDir", imagesFolder.getAbsolutePath());
            }
        } else {
            RequestPermissions.requestPermission(TelaDeUsuario.this);
        }




        bt_configuracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Configurações de usuário", "Trocar de conta", "Sair"};
                AlertDialog.Builder builder = new AlertDialog.Builder(TelaDeUsuario.this);
                builder.setTitle("Opções de configuração");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent goToUserConfig = new Intent( getApplicationContext(), ConfiguracoesDeUsuario.class);
                            goToUserConfig.putExtras(paths);
                            startActivity(goToUserConfig);
                        } else if (which == 1) {
                            FirebaseAuth.getInstance().signOut();
                            Intent back_to_login = new Intent(getApplicationContext(), FormLogin.class);
                            Toast.makeText(getApplicationContext(), "Usuário deslogado", Toast.LENGTH_LONG).show();
                            startActivity(back_to_login);
                        } else if (which == 2) {
                            finishAndRemoveTask();
                        }
                    }
                });
                builder.create().show();

            }
        });

        // Seguir para FormOS
        bt_nova_os_manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RequestPermissions.checkPermission(getApplicationContext())) {
                    Intent goToFormOsActivity = new Intent(getApplicationContext(), FormOSManutencaoCorretiva.class);
                    goToFormOsActivity.putExtras(paths);
                    startActivity(goToFormOsActivity);
                } else {
                    RequestPermissions.requestPermission(TelaDeUsuario.this);
                }

            }
        });
    }


    private void CriarPastasDoApp(File folder) {
        String folderUserName = userID, folderImages = "DocShare_images", folderOS = "DocShare_os_files";
        File userFolder = new File(folder, folderUserName);
        if(!userFolder.exists()){
            userFolder.mkdir();
            paths.putString("userDir", userFolder.getAbsolutePath());
            File imagesFolder = new File(userFolder, folderImages);
            File osFolder = new File(userFolder, folderOS);
            if(!imagesFolder.exists()&&!osFolder.exists()){
                imagesFolder.mkdir();
                paths.putString("imagesDir", imagesFolder.getAbsolutePath());
                if(osFolder.mkdir()) {
                    paths.putString("osDir", osFolder.getAbsolutePath());
                    Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Fail",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Folder Already Exists",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(UserInfo.getUserCredentials() != null) {
            //String[] nomeUser = UserInfo.getUserCredentials().getString("nome").split("\\s");
            String nomeUser = UserInfo.getUserCredentials().getString("nome");   // NÃO FAÇO IDEIA PQ AQUI DA NULL
            ola = "Olá, " + nomeUser;
            //boas_vindas.setText(ola);
        }

        DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);

        // Texto de Boas Vindas
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    String[] nomeUser = UserInfo.getUserCredentials().getString("nome").split("\\s");
                    ola = "Olá, " + nomeUser[0];
                    boas_vindas.setText(ola);
                    if(!Objects.equals(UserInfo.getUserCredentials().getString("profilePicUri"), "void")) {
                        Bitmap profilePicBitmap = BitmapFactory.decodeFile(UserInfo.getUserCredentials().getString("profilePicUri"));
                        if(profilePicBitmap !=null)
                            profilePic.setImageBitmap(ImageHelper.getRoundedCornerBitmap(profilePicBitmap, profilePicBitmap.getHeight()));
                    }
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

//    // Checagem de permissão para ler e escrever no armazenamento externo
//    private boolean checkPermission() {
//        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
//        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
//        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
//    }
//
//    // Solicitar permissão
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//    }

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