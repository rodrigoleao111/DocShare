package com.example.docshare.formularios;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.docshare.metodos.CropImage;
import com.example.docshare.usuario.FormLogin;
import com.example.docshare.R;
import com.example.docshare.metodos.FileGenerator;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends FileGenerator {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;

    private EditText email_user, senha_user, confirmar_senha_user;
    private EditText nome_user, cpf_user, rg_user, telefone_user;
    private Spinner cargo_user, setor_user;
    private Button bt_cadastrar;
    private ImageView foto_perfil, loadingBg;
    private Uri profilePicUri;

    private ProgressBar loadingPb;

    private final FirebaseFirestore db_cadastros = FirebaseFirestore.getInstance();
//    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//    private final FirebaseStorage storage = FirebaseStorage.getInstance("gs://docshare-db561.appspot.com/ProfilePictures");

    String[] cameraPermission;
    String[] storagePermission;

    String[] mensagens = {"Erro: Preencha todos os campos", "Cadastro realizado", "Erro: Campos de senha diferentes"};
    String usuarioID;

    String email, senha, confirma_senha;
    Map<String,Object> dados_usuario = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();

        Intent profilePic = getIntent();
        boolean check = profilePic.getBooleanExtra("check", false);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Adicionar foto de perfil
        foto_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        // Caso já tenha adicionado uma foto de perfil:
        if(check){
            foto_perfil.setImageURI(Uri.parse(profilePic.getStringExtra("uri")));
            profilePicUri = Uri.parse(profilePic.getStringExtra("uri"));
        }

        // Botão Finalizar Cadastro
        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColetarInformacoes();

                if(email.isEmpty() || senha.isEmpty() || confirma_senha.isEmpty()){
                    Toast.makeText(getApplicationContext(), mensagens[0], Toast.LENGTH_LONG).show();
                } else {
                    if(senha.equals(confirma_senha)){
                        loadingBg.setVisibility(View.VISIBLE);
                        loadingPb.setVisibility(View.VISIBLE);
                        CadastrarUsuario(email, senha, dados_usuario);
                    } else Toast.makeText(getApplicationContext(), mensagens[2], Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void ColetarInformacoes() {
        // Coletar informações inseridas
        email = email_user.getText().toString();
        senha = senha_user.getText().toString();
        confirma_senha = confirmar_senha_user.getText().toString();

        // Informações do documento usuário
        dados_usuario.put("nome", nome_user.getText().toString());
        dados_usuario.put("cpf", cpf_user.getText().toString());
        dados_usuario.put("rg", rg_user.getText().toString());
        dados_usuario.put("telefone", telefone_user.getText().toString());
        dados_usuario.put("cargo", cargo_user.getSelectedItem().toString());
        dados_usuario.put("setor", setor_user.getSelectedItem().toString());
        if(profilePicUri != null)
            dados_usuario.put("profilePicUri", profilePicUri.toString());
        else
            Toast.makeText(getApplicationContext(),mensagens[0], Toast.LENGTH_SHORT).show();

//        // Salvar e coletar imagem de perfil
//        StorageReference profilePicRef = storageRef.child(profilePicUri.getPath());
//        UploadTask uploadTask = profilePicRef.putFile(profilePicUri);
//        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                return profilePicRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    dados_usuario.put("profilePic", downloadUri.toString());
//                } else {
//                    //Toast de falha
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        goToFormLogin();
        super.onBackPressed();
    }


    /***
     * Realizar comunicação com Firebase para autenticar novo usuário e salvar informações no DB
     * Utiliza as ferramentas: Google Authentication
     * @param email inserido pelo usuário
     * @param senha inserido pelo usuário
     * @param dados_usuario informações a serem adicionadas ao DB
     */
    private void CadastrarUsuario(String email, String senha, Map<String, Object> dados_usuario){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SalvarDadosCadastrais(dados_usuario);

                    Toast.makeText(getApplicationContext(), mensagens[1], Toast.LENGTH_LONG).show();
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        erro = "A senha precisa ter no mínimo 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e){
                        erro = "Essa conta já foi cadastrada";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail inválido";
                    } catch (Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }
                    Toast.makeText(getApplicationContext(), erro, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /***
     * Salvar informações cadastrais no banco de dados
     * Utiliza as ferramentas: Google Firestone
     * @param cadastro informações(K, V)
     */
    private void SalvarDadosCadastrais(Map<String, Object> cadastro){
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db_cadastros.collection("Usuarios").document(usuarioID);

        documentReference.set(cadastro).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar os dados");
                goToFormLogin();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Erro ao salvar os dados: " + e.toString());
            }
        });
    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            Intent goToCropActivity = new Intent(getApplicationContext(), CropImage.class);
            goToCropActivity.putExtra("uri", result);
            goToCropActivity.putExtra("class", FormCadastro.class);
            startActivity(goToCropActivity);
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
                            foto_perfil.setImageBitmap(cameraPic);
                        }
                    }
                }
            });



    // Checagem de permissão: armazenamento externo
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requisição de permissão: galeria
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // Checagem de permissão: camera
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requisição de permissão: camera
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // Adicionar condição para quando a permissão for negada











    public void IniciarComponentes(){
        email_user = findViewById(R.id.edit_cadastro_email);
        senha_user = findViewById(R.id.edit_cadastro_senha);
        confirmar_senha_user = findViewById(R.id.edit_confirmar_senha);

        nome_user = findViewById(R.id.edit_cadastro_nome);
        cpf_user = findViewById(R.id.edit_cadastro_cpf);
        rg_user = findViewById(R.id.edit_cadastro_RG);
        telefone_user = findViewById(R.id.edit_cadastro_telefone);
        cargo_user = findViewById(R.id.edit_cargo);
        setor_user = findViewById(R.id.edit_setor);

        bt_cadastrar = findViewById(R.id.button_cadastrar);

        foto_perfil = findViewById(R.id.profile_picture);

        loadingBg = findViewById(R.id.loagingBg);
        loadingPb = findViewById(R.id.loadingPb);
    }


    private boolean ValidacaoPreenchimento(String edt_valor, String nome_do_item){
        boolean validacao = false;
        if(edt_valor.isEmpty())         // Colocar validação isBlank
            Toast.makeText(getApplicationContext(), "Preencha o campo do " + nome_do_item, Toast.LENGTH_LONG).show();
        else
            validacao = true;

        return validacao;
    }


    private void goToFormLogin(){
        Intent intent = new Intent(getApplicationContext(), FormLogin.class);
        startActivity(intent);
        finish();
    }
}