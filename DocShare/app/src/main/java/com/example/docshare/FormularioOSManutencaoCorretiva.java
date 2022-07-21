package com.example.docshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FormularioOSManutencaoCorretiva extends AppCompatActivity {

    Bundle formularioOS = new Bundle();
    private Button bt_visualizarOS;
    private EditText edtNome, edtRG, edtCPF, edtSetor, edtCargo, edtTelefone, edtEmail;  // Edt referente as informaçoes do colaborador
    private EditText edtEquipamento, edtModelo, edtEquipID;                              // Edt referente ao Equipamento | Ativo
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

    // PREENCHIMENTO COM OS DADOS DO USUÁRIO
    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);

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

        bt_visualizarOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColetarInformacoes();
            }
        });
    }

    
    private void ColetarInformacoes() {
        formularioOS.putString("nome", edtNome.toString());
        formularioOS.putString("rg", edtRG.toString());
        formularioOS.putString("cpf", edtCPF.toString());
        formularioOS.putString("setor", edtSetor.toString());
        formularioOS.putString("cargo", edtCargo.toString());
        formularioOS.putString("email", edtEmail.toString());

        formularioOS.putString("locacao", formLocacao.toString());
        formularioOS.putString("equipamento", edtEquipamento.toString());
        formularioOS.putString("modelo", edtModelo.toString());
        formularioOS.putString("equipID", edtEquipID.toString());
    }


    private void IniciarComponentes() {
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

        bt_visualizarOS = findViewById(R.id.bt_visualizarOS);
    }
}