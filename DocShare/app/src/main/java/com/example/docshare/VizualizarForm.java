package com.example.docshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.docshare.formularios.FormOSManutencaoCorretiva;
import com.example.docshare.metodos.FileGenerator;

public class VizualizarForm extends FileGenerator {

    private TextView nome, rg, cpf, setor, cargo, telefone, email, locacao, equipamento, modelo, equipID, diagnostico, solucao, troca, obs;
    TextView[] dadosDoForm = {nome, rg, cpf, setor, cargo, telefone, email, locacao, equipamento, modelo, equipID, diagnostico, solucao, troca, obs};
    String[] chavesForm = {"nome", "rg", "cpf", "setor", "cargo", "telefone", "email", "locacao", "equipamento", "modelo", "equipID", "diagnostico", "solucao", "troca", "obs"};
    Button btVoltar, btCompartilhar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizar_form);
        getSupportActionBar().hide();
        Intent intentReceberForm = getIntent();
        Bundle dadosOS = intentReceberForm.getExtras();
        InicializarComponentes();
        AtribuirTextos(dadosOS);

        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ESTÁ VOLTANDO E LIMPANDO TODAS AS INFORMAÇÕES QUE ESTAVAM INSERIDAS (NÃO QUERO ISSO)
                Intent voltar = new Intent(getApplicationContext(), FormOSManutencaoCorretiva.class);
                startActivity(voltar);
            }
        });

        btCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GerarPDF(dadosOS);
            }
        });
    }


    private void AtribuirTextos(Bundle dadosOS) {
        for(int i = 0; i < dadosDoForm.length; i++){
            if (dadosOS.getString(chavesForm[i]) != null) {
                dadosDoForm[i].setText(dadosOS.getString(chavesForm[i]));
            } else {
                dadosOS.putString(chavesForm[i], "xxxxxxx");
                dadosDoForm[i].setText(dadosOS.getString(chavesForm[i]));
            }
        }
    }

    private void InicializarComponentes() {
        dadosDoForm[0] = findViewById(R.id.txtNome);
        dadosDoForm[1] = findViewById(R.id.txtRG);
        dadosDoForm[2] = findViewById(R.id.txtCPF);
        dadosDoForm[3] = findViewById(R.id.txtSetor);
        dadosDoForm[4] = findViewById(R.id.txtCargo);
        dadosDoForm[5] = findViewById(R.id.txtTelefone);
        dadosDoForm[6] = findViewById(R.id.txtEmail);
        dadosDoForm[7] = findViewById(R.id.txtLocacao);
        dadosDoForm[8] = findViewById(R.id.txtEquipamento);
        dadosDoForm[9] = findViewById(R.id.txtModelo);
        dadosDoForm[10] = findViewById(R.id.txtEquipID);
        dadosDoForm[11] = findViewById(R.id.txtDiag);
        dadosDoForm[12] = findViewById(R.id.txtSolucao);
        dadosDoForm[13] = findViewById(R.id.txtTroca);
        dadosDoForm[14] = findViewById(R.id.txtOBS);


        btVoltar = findViewById(R.id.btVoltar);
        btCompartilhar = findViewById(R.id.btSaveShare);
    }
}