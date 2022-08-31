package com.example.docshare.usuario;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.docshare.R;
import com.example.docshare.metodos.CropImage;
import com.example.docshare.metodos.ImageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfiguracoesDeUsuario extends AppCompatActivity implements ImageHelper {

    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);

    private EditText edtnome, edtcpf, edtrg, edttelefone;
    private TextView tvcargo, tvsetor, tvUserId;
    private Button btAlterarDados, btMudarSenha;
    private ImageView changeProfilePic;

    // Códigos de requisição
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private String[] cameraPermission;
    private String[] storagePermission;

    private Map<String,Object> newProfilePicUri = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_de_usuario);

        getSupportActionBar().hide();
        IniciarComponentes();

        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        // Recebendo dados de CropImage
        Intent receberProfilePic = getIntent();
        if(receberProfilePic.getBooleanExtra("check", false)){
            newProfilePicUri.put("profilePicUri", receberProfilePic.getStringExtra("path"));
            updateProfilePic(newProfilePicUri);
        }


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

        changeProfilePic = findViewById(R.id.changeProfilePic);

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
                        changeProfilePic.setImageURI(Uri.parse(documentSnapshot.getString("profilePicUri")));
                    }
                }
            }
        });
    }

    public void showImagePicDialog() {
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String[] options = {"Camera", "Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracoesDeUsuario.this);
        builder.setTitle("Selecione a fonte da imagem.");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraResultLauncher.launch(camera);
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        mGetContent.launch("image/*");
                    }
                }
            }
        });
        builder.create().show();
    }


    // Coletar imagem da galeria
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            // Enviar bitmap para CropImage
            Intent sendToCropImageActivity = new Intent(getApplicationContext(), CropImage.class);
            sendToCropImageActivity.putExtra("uri", result);
            sendToCropImageActivity.putExtra("call", 0);
            sendToCropImageActivity.putExtra("source", 1);
            startActivity(sendToCropImageActivity);
        }
    });


    // Coletar imagem da camera
    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bitmap cameraPic = (Bitmap)(data.getExtras().get("data"));

                            // Enviar bitmap para CropImage
                            Intent sendToCropImageActivity = new Intent(getApplicationContext(), CropImage.class);
                            sendToCropImageActivity.putExtra("byteArray", ImageHelper.bitmapToByteArray(cameraPic));
                            sendToCropImageActivity.putExtra("call", 0);
                            sendToCropImageActivity.putExtra("source", 0);
                            startActivity(sendToCropImageActivity);
                        }
                    }
                }
            });



    // Checagem de permissão: armazenamento externo
    public Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requisição de permissão: galeria
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // Checagem de permissão: camera
    public Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requisição de permissão: camera
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }



    // Update profile picture
    private void updateProfilePic(Map<String,Object> newProfilePicUri){
        documentReference.update(newProfilePicUri).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Imagem de perfil atualizada");
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if(documentSnapshot != null){
                            changeProfilePic.setImageURI(Uri.parse(documentSnapshot.getString("profilePicUri")));
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Erro ao atualizar imagem de perfil: " + e.toString());
            }
        });
    }
    // Adicionar condição para quando a permissão for negada


    
    @Override
    public void onBackPressed() {
        Intent userScreen = new Intent(getApplicationContext(), TelaDeUsuario.class);
        startActivity(userScreen);
    }


}