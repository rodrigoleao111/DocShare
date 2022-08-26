package com.example.docshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.docshare.formularios.FormOSManutencaoCorretiva;
import com.example.docshare.metodos.FileGenerator;

public class VizualizarForm extends FileGenerator {

    private TextView nome, rg, cpf, setor, cargo, telefone, email, locacao, equipamento, modelo, equipID, diagnostico, solucao, troca, obs, descricao;
    private String[] chavesForm = {"nome", "rg", "cpf", "setor", "cargo", "telefone", "email", "locacao", "equipamento", "modelo", "equipID", "diagnostico", "solucao", "troca", "obs", "descricaoImg"};
    private TextView txtImage;
    private Button btVoltar, btCompartilhar;
    private ImageView preview;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizar_form);
        getSupportActionBar().hide();

        Intent intentReceberForm = getIntent();
        Bitmap bitmap = (Bitmap) intentReceberForm.getParcelableExtra("BitmapImage");
        Bundle dadosOS = intentReceberForm.getExtras();

        InicializarComponentes();
        AtribuirValores(dadosOS, bitmap);

        // BOTÃO VOLTAR
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // BOTÃO COMPARTILHAR
        btCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GerarPDF(dadosOS, bitmap);
            }
        });
    }

    /***
     * Impressão de dados na tela
     * Atribui os valores que foram inseridos pelo usuário na Activity Formulário
     * @param dadosOS - Dados de texto inseridos pelo usuário
     * @param bitmap - Imagem anexada pelo usuário
     */
    private void AtribuirValores(Bundle dadosOS, Bitmap bitmap) {
        TextView[] dadosDoForm = {nome, rg, cpf, setor, cargo, telefone, email, locacao, equipamento, modelo, equipID, diagnostico, solucao, troca, obs, descricao};
        for(int i = 0; i < dadosDoForm.length; i++){
            if (dadosOS.getString(chavesForm[i]) != null) {
                dadosDoForm[i].setText(dadosOS.getString(chavesForm[i]));
            } else {
                dadosOS.putString(chavesForm[i], "xxxxxxx");
                dadosDoForm[i].setText(dadosOS.getString(chavesForm[i]));
            }
        }

        if(bitmap != null) {
            preview.setImageBitmap(bitmap);
            preview.setVisibility(View.VISIBLE);
            descricao.setVisibility(View.VISIBLE);
            txtImage.setVisibility(View.VISIBLE);
        }
    }

    private void InicializarComponentes() {
        nome = findViewById(R.id.txtNome);
        rg = findViewById(R.id.txtRG);
        cpf = findViewById(R.id.txtCPF);
        setor = findViewById(R.id.txtSetor);
        cargo = findViewById(R.id.txtCargo);
        telefone = findViewById(R.id.txtTelefone);
        email = findViewById(R.id.txtEmail);
        locacao = findViewById(R.id.txtLocacao);
        equipamento = findViewById(R.id.txtEquipamento);
        modelo = findViewById(R.id.txtModelo);
        equipID = findViewById(R.id.txtEquipID);
        diagnostico = findViewById(R.id.txtDiag);
        solucao = findViewById(R.id.txtSolucao);
        troca = findViewById(R.id.txtTroca);
        obs = findViewById(R.id.txtOBS);
        descricao = findViewById(R.id.txtDescricao);

        preview = findViewById(R.id.imagePreview);
        txtImage = findViewById(R.id.vwInfoImagem);

        btVoltar = findViewById(R.id.btVoltar);
        btCompartilhar = findViewById(R.id.btSaveShare);
    }
}