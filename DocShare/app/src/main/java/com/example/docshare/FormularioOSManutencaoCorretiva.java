package com.example.docshare;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

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
                GerarPDF(ColetarInformacoes());
            }
        });
    }

    private void GerarPDF(Bundle coletarInformacoes) {
        PdfDocument pdfRelatorio = new PdfDocument();
        Paint myPaint = new Paint();

        PdfDocument.PageInfo infoRelatorio = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
        PdfDocument.Page pagRelatorio = pdfRelatorio.startPage(infoRelatorio);

        Canvas canvas = pagRelatorio.getCanvas();
        canvas.drawText("OS Manutenção", 40, 50, myPaint);
        pdfRelatorio.finishPage(pagRelatorio);

        String nomeArquivo = "OSManutenção_" + coletarInformacoes.getString("formID") + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nomeArquivo);

        try {
            pdfRelatorio.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(), "arquivo gerado", Toast.LENGTH_SHORT).show();
            //String pathUri = "content://com.example.docshare.fileprovider/Downloads/" + nomeArquivo;
            String pathUri = Environment.getExternalStorageDirectory().getPath() + File.separator + nomeArquivo;
            CompartilharRelatorio(pathUri);
        } catch (IOException e){
            e.printStackTrace();
        }

        pdfRelatorio.close();
    }

    private void CompartilharRelatorio(String pathUri) {
        File file = new File(pathUri);
        if(file.exists()){
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("application/pdf");
            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathUri));
            startActivity(Intent.createChooser(intentShare, "Share file"));
        } else {
            Toast.makeText(getApplicationContext(), "Arquivo não encontrado", Toast.LENGTH_SHORT).show();
        }
    }


    private Bundle ColetarInformacoes() {

        Date formID = new Date();
        // Colocar validação para caso o item esteja em vazio

        formularioOS.putInt("formType", 0);     // Tipo do formulário

        formularioOS.putString("formID", String.valueOf(formID.getTime()));

        formularioOS.putString("nome", edtNome.toString());
        formularioOS.putString("rg", edtRG.toString());
        formularioOS.putString("cpf", edtCPF.toString());
        formularioOS.putString("setor", edtSetor.toString());
        formularioOS.putString("cargo", edtCargo.toString());
        formularioOS.putString("email", edtEmail.toString());

        formularioOS.putString("locacao", formLocacao.getSelectedItem().toString());
        formularioOS.putString("equipamento", edtEquipamento.toString());
        formularioOS.putString("modelo", edtModelo.toString());
        formularioOS.putString("equipID", edtEquipID.toString());

        return formularioOS;
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