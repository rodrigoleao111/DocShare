package com.example.docshare.metodos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileGenerator extends AppCompatActivity {

    // Construtor vazio
    public FileGenerator(){}


    /***
     * Criar arquivo PDF com as informações inseridas pelo usuário
     * @param coletarInformacoes : bundle com as informações inseridas na activity
     */
    public void GerarPDF(Bundle coletarInformacoes, Bitmap bitmap) {

        /*

        // Separar criação de formulário para diferentes tipos.

        switch (coletarInformacoes.getInt("formType")){
            case 0:
                // cadastro
                break;
            case 1:
                // FormOSManutencaoCorretiva
                break;
            case 2:
                // FormRelatorioDefeitos
                break;
            default:
                // TO DO
        }

         */

        String[] infoColaborador = {"Nome", "RG", "CPF", "Setor", "Cargo", "Telefone", "E-mail"};
        String[] infoEquipamento = {"Locação", "Equipamento", "Modelo", "ID"};
        String[] infoManutencao = {"Diagnóstico", "Solução", "Peças trocadas", "Observações"};
        String[] chavesColaborador = {"nome", "rg", "cpf", "setor", "cargo", "telefone", "email"};
        String[] chavesEquipamento = {"locacao", "equipamento", "modelo", "equipID"};
        String[] chavesManutencao = {"diagnostico", "solucao", "troca", "obs"};

        PdfDocument pdfRelatorio = new PdfDocument();
        Paint myPaint = new Paint();

        // Informações da página
        int pageWidth = 400, pageHeight = 600;  // Pixels
        int y = 0, marginLeft = 10, center = pageWidth/2;
        PdfDocument.PageInfo infoRelatorio = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page pagRelatorio = pdfRelatorio.startPage(infoRelatorio);
        Canvas canvas = pagRelatorio.getCanvas();

        // Layout da página
        // Título
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(12.0f);
        y += 30;
        canvas.drawText("Ordem de Serviço - " + coletarInformacoes.getString("formID"), center, y, myPaint);

        // Subtítulo
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.rgb(112, 119, 119));
        y += 10;
        canvas.drawText("Manutenção corretiva", center, y, myPaint);

        // Informações do usuário
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(6.0f);
        y += 30;
        canvas.drawText("Informações do colaborador", marginLeft, y, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.BLACK);

        int marginRigth = pageWidth - 10;
        y += 20;
        for (int i = 0; i < infoColaborador.length; i++) {
            canvas.drawText(infoColaborador[i], marginLeft, y, myPaint);
            canvas.drawText(coletarInformacoes.getString(chavesColaborador[i]), marginLeft + 80, y, myPaint);
            canvas.drawLine(marginLeft, y + 3, marginRigth, y + 3, myPaint);
            y += 10;
        }

        y += 30;

        // Informações do equipamento
        myPaint.setColor(Color.rgb(112, 119, 119));
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(6.0f);
        canvas.drawText("Equipamento | Ativo", 10, y, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.BLACK);

        y += 20;

        for (int i = 0; i < infoEquipamento.length; i++) {
            canvas.drawText(infoEquipamento[i], marginLeft, y, myPaint);
            canvas.drawText(coletarInformacoes.getString(chavesEquipamento[i]), marginLeft + 80, y, myPaint);
            canvas.drawLine(marginLeft, y + 3, marginRigth, y + 3, myPaint);
            y += 10;
        }

        y += 30;

        // Informações de Manutenção
        myPaint.setColor(Color.rgb(112, 119, 119));
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(6.0f);
        canvas.drawText("Informações de Manutenção", 10, y, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.BLACK);

        y += 20;

        for (int i = 0; i < infoManutencao.length; i++) {
            canvas.drawText(infoManutencao[i], marginLeft, y, myPaint);
            canvas.drawText(coletarInformacoes.getString(chavesManutencao[i]), marginLeft + 80, y, myPaint);
            canvas.drawLine(marginLeft, y + 3, marginRigth, y + 3, myPaint);
            y += 10;
        }

        y += 30;

        if(bitmap != null){
            Rect rect = new Rect( marginLeft, y, marginRigth, y+bitmap.getHeight());
            canvas.drawBitmap(bitmap, null, rect, myPaint);
        }

        pdfRelatorio.finishPage(pagRelatorio);

        String nomeArquivo = "OSManutencao_" + coletarInformacoes.getString("formID") + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nomeArquivo);

        try {
            pdfRelatorio.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(), "arquivo gerado", Toast.LENGTH_SHORT).show();
            CompartilharRelatorio(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfRelatorio.close();
    }

    /*
    public void ColetarImagem() {

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    foto_perfil.setImageURI(result);
                }
            }
        });

        mGetContent.launch("image/*");
    }
    */


    public void CompartilharRelatorio(File file) {

        Uri pathUri = FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.docshare.provider",
                file);

        if(file.exists()){
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("application/pdf");
            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathUri.toString()));
            startActivity(Intent.createChooser(intentShare, "Share file"));
        } else {
            Toast.makeText(getApplicationContext(), "Arquivo não encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    public Bundle ColetarInformacoes() {
        Bundle formularioOS = new Bundle();
        return formularioOS;
    }

    public void IniciarComponentes(){}

}
