package com.example.docshare.formularios;

import android.content.Intent;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import com.example.docshare.R;
import com.example.docshare.VizualizarForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FormOSManutencaoCorretiva extends AppCompatActivity {

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
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_osmanutencao_corretiva);

        getSupportActionBar().hide();
        IniciarComponentes();

        // Adicionar foto de perfil
        addFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

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
                if(bitmap != null)
                    goToVizualizationActivity.putExtra("BitmapImage", bitmap);
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

    // ALTERAR VISIBILIDADE DO BLOCO DA IMAGEM
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result != null){
                preview.setImageURI(result);
                preview.buildDrawingCache();
                bitmap = preview.getDrawingCache();
                preview.setVisibility(View.VISIBLE);
                edtDescricao.setVisibility(View.VISIBLE);
                hitDescricao.setVisibility(View.VISIBLE);
                vwConteiner.setVisibility(View.VISIBLE);
            }
        }
    });

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