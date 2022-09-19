package com.example.docshare.formularios;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import com.example.docshare.R;
import com.example.docshare.VizualizarForm;
import com.example.docshare.metodos.CropImage;
import com.example.docshare.metodos.ImageHelper;
import com.example.docshare.usuario.ConfiguracoesDeUsuario;
import com.example.docshare.usuario.TelaDeUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FormOSManutencaoCorretiva extends AppCompatActivity implements ImageHelper {

    private Button bt_visualizarOS;
    private EditText edtNome, edtRG, edtCPF, edtSetor, edtCargo, edtTelefone, edtEmail;  // Edt referente as informaçoes do colaborador
    private EditText edtEquipamento, edtModelo, edtEquipID;                              // Edt referente ao Equipamento | Ativo
    private EditText edtDiagnostico, edtSolucao, edtPecasTrocadas, edtObservacoes;       // Edt referente a manutenção
    private EditText edtDescricao;
    private ImageView addFoto, preview;
    private Spinner formLocacao;
    private View vwConteiner;
    private TextView hitDescricao;
    Bitmap bitmap;
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID, bitmapPath = null;

    // Códigos de requisição
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private String[] cameraPermission;
    private String[] storagePermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_osmanutencao_corretiva);

        getSupportActionBar().hide();
        IniciarComponentes();

        // Adicionar imagem
        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        // Recebendo dados de CropImage
        Intent receberProfilePic = getIntent();
        if(receberProfilePic.getBooleanExtra("check", false)){
            preview.setVisibility(View.VISIBLE);
            bitmapPath = receberProfilePic.getStringExtra("path");
            preview.setImageURI(Uri.parse(bitmapPath));
            edtDescricao.setVisibility(View.VISIBLE);
            hitDescricao.setVisibility(View.VISIBLE);
            vwConteiner.setVisibility(View.VISIBLE);
            bitmap = BitmapFactory.decodeFile(bitmapPath);
        }

    }

    public void showImagePicDialog() {
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String[] options = {"Camera", "Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(FormOSManutencaoCorretiva.this);
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

                            // ASSIM ESTOU MODIFICANDO A TUMBNAIL (ESTICANDO A IMAGEM)

                            Uri tempUri = ImageHelper.getUriFromTumbnailBitmap(getApplicationContext(), cameraPic);
                            File finalFile = new File(ImageHelper.getRealPathFromURI(tempUri, getApplicationContext()));

                            // Enviar bitmap para CropImage
                            Intent sendToCropImageActivity = new Intent(getApplicationContext(), CropImage.class);
                            sendToCropImageActivity.putExtra("uri", tempUri);
                            sendToCropImageActivity.putExtra("call", 1);
                            sendToCropImageActivity.putExtra("source", 1);
                            startActivity(sendToCropImageActivity);

                        }
                    }
                }
            });

    // Coletar imagem da galeria
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    // Enviar bitmap para CropImage
                    Intent sendToCropImageActivity = new Intent(getApplicationContext(), CropImage.class);
                    //sendToCropImageActivity.putExtras(paths);
                    sendToCropImageActivity.putExtra("uri", result);
                    sendToCropImageActivity.putExtra("call", 1);
                    sendToCropImageActivity.putExtra("source", 1);
                    startActivity(sendToCropImageActivity);
                }
            });

    @Override
    public void onBackPressed() {
        Intent backToUserScreen = new Intent(this, TelaDeUsuario.class);
        startActivity(backToUserScreen);
    }

    // PREENCHIMENTO AUTOMÁTICO COM OS DADOS DO USUÁRIO
    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);

        // Recuperação de dados pessoais
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    edtNome.setText(documentSnapshot.getString("nome"));
                    edtRG.setText(documentSnapshot.getString("rg"));
                    edtCPF.setText(documentSnapshot.getString("cpf"));
                    edtSetor.setText(documentSnapshot.getString("setor"));
                    edtCargo.setText(documentSnapshot.getString("cargo"));
                    edtTelefone.setText(documentSnapshot.getString("telefone"));
                    edtEmail.setText(email);
                }
            }
        });

        // BOTÃO VIZUALIZAR FORMULÁRIO
        bt_visualizarOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToVizualizationActivity = new Intent(getApplicationContext(), VizualizarForm.class);
                goToVizualizationActivity.putExtras(ColetarInformacoes());
                if(bitmap != null) {
                    goToVizualizationActivity.putExtra("BitmapImage", bitmapPath);
                }
                startActivity(goToVizualizationActivity);
            }
        });
    }

    // INICIALIZAR COMPONENTES DA CLASSE
    public void IniciarComponentes() {
        // Dados usuário
        edtNome = findViewById(R.id.userName);
        edtRG = findViewById(R.id.userRG);
        edtCPF = findViewById(R.id.userCPF);
        edtSetor = findViewById(R.id.userSetor);
        edtCargo = findViewById(R.id.userCargo);
        edtTelefone = findViewById(R.id.userTelefone);
        edtEmail = findViewById(R.id.userEmail);

        // Formulário
        formLocacao = findViewById(R.id.edtFormOSLocacao);
        edtEquipamento = findViewById(R.id.edtFormOSEquipamento);
        edtModelo =findViewById(R.id.edtFormOSModelo);
        edtEquipID = findViewById(R.id.edtFormOSIDEquipamento);

        edtDiagnostico = findViewById(R.id.edtFormOSDiagnostico);
        edtSolucao = findViewById(R.id.edtFormOSSolucao);
        edtPecasTrocadas = findViewById(R.id.edtFormOSTroca);
        edtObservacoes = findViewById(R.id.edtFormOSObservacoes);

        addFoto = findViewById(R.id.addFoto);
        preview = findViewById(R.id.preview);

        edtDescricao = findViewById(R.id.edtFormOSDescricao);
        vwConteiner = findViewById(R.id.containerHist4);
        hitDescricao = findViewById(R.id.hintDescricao);

        bt_visualizarOS = findViewById(R.id.bt_visualizarOS);
    }


    // MÉTODO PARA COLETAR AS INFORMAÇÕES INSERIDAS PELO USUÁRIO
    public Bundle ColetarInformacoes() {
        Bundle formularioOS = new Bundle();

        Date formID = new Date();
        // Colocar validação para caso o item esteja em vazio

        formularioOS.putInt("formType", 1);     // Tipo do formulário
        formularioOS.putString("formID", String.valueOf(formID.getTime()));

        String[] chaves = {"nome", "rg", "cpf", "setor", "cargo", "telefone", "email", "equipamento",
                "modelo", "equipID", "diagnostico", "solucao", "troca", "obs", "descricaoImg"};

        EditText[] dadosDoForm = {edtNome, edtRG, edtCPF, edtSetor, edtCargo, edtTelefone, edtEmail,
                edtEquipamento, edtModelo, edtEquipID, edtDiagnostico, edtSolucao, edtPecasTrocadas, edtObservacoes, edtDescricao};

        for(int i = 0; i < chaves.length; i++) {
            formularioOS.putString(chaves[i], dadosDoForm[i].getText().toString());
        }

        formularioOS.putString("locacao", formLocacao.getSelectedItem().toString());

        return formularioOS;
    }

}