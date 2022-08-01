package com.example.docshare;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import com.example.docshare.formulario.Form;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FormOSManutencaoCorretiva extends Form {

    private Button bt_visualizarOS;
    private EditText edtNome, edtRG, edtCPF, edtSetor, edtCargo, edtTelefone, edtEmail;  // Edt referente as informaçoes do colaborador
    private EditText edtEquipamento, edtModelo, edtEquipID;                              // Edt referente ao Equipamento | Ativo
    private EditText edtDiagnostico, edtSolucao, edtPecasTrocadas, edtObservacoes;
    //private ImageView foto;
    private Spinner formLocacao;
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_osmanutencao_corretiva);

        getSupportActionBar().hide();
        IniciarComponentes();

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

        // BOTÃO FINALIZAR FORMULÁRIO
        bt_visualizarOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GerarPDF(ColetarInformacoes());
            }
        });
    }

    // INICIALIZAR COMPONENTES DA CLASSE
    @Override
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

        //foto.findViewById(R.id.addFoto);

        bt_visualizarOS = findViewById(R.id.bt_visualizarOS);
    }

    // MÉTODO PARA COLETAR AS INFORMAÇÕES INSERIDAS PELO USUÁRIO
    @Override
    public Bundle ColetarInformacoes() {
        Bundle formularioOS = new Bundle();

        Date formID = new Date();
        // Colocar validação para caso o item esteja em vazio

        formularioOS.putInt("formType", 1);     // Tipo do formulário

        formularioOS.putString("formID", String.valueOf(formID.getTime()));

        formularioOS.putString("nome", edtNome.getText().toString());
        formularioOS.putString("rg", edtRG.getText().toString());
        formularioOS.putString("cpf", edtCPF.getText().toString());
        formularioOS.putString("setor", edtSetor.getText().toString());
        formularioOS.putString("cargo", edtCargo.getText().toString());
        formularioOS.putString("telefone", edtTelefone.getText().toString());
        formularioOS.putString("email", edtEmail.getText().toString());

        formularioOS.putString("locacao", formLocacao.getSelectedItem().toString());
        formularioOS.putString("equipamento", edtEquipamento.getText().toString());
        formularioOS.putString("modelo", edtModelo.getText().toString());
        formularioOS.putString("equipID", edtEquipID.getText().toString());

        formularioOS.putString("diagnostico", edtDiagnostico.getText().toString());
        formularioOS.putString("solucao", edtSolucao.getText().toString());
        formularioOS.putString("troca", edtPecasTrocadas.getText().toString());
        formularioOS.putString("obs", edtObservacoes.getText().toString());

        return formularioOS;
    }

}