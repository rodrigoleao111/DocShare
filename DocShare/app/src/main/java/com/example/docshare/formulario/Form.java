package com.example.docshare.formulario;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.util.Date;

public class Form extends AppCompatActivity {

    // GERAR ARQUIVO EM PDF
    public void GerarPDF(Bundle coletarInformacoes) {

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
        String[] chaves = {"nome", "rg", "cpf", "setor", "cargo", "telefone", "email"};

        PdfDocument pdfRelatorio = new PdfDocument();
        Paint myPaint = new Paint();

        // Informações da página
        PdfDocument.PageInfo infoRelatorio = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
        PdfDocument.Page pagRelatorio = pdfRelatorio.startPage(infoRelatorio);
        Canvas canvas = pagRelatorio.getCanvas();

        // Layout da página
            // Título
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(12.0f);
        canvas.drawText("Ordem de Serviço - " + coletarInformacoes.getString("formID"), infoRelatorio.getPageWidth()/2, 30, myPaint);
            // Subtítulo
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.rgb(112,119,119));
        canvas.drawText("Manutenção corretiva", infoRelatorio.getPageWidth()/2, 40, myPaint);
            // Informações do usuário
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(6.0f);
        canvas.drawText("Informações do colaborador", 10, 70, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.BLACK);

        int startXPosition = 10, endXPosition = infoRelatorio.getPageWidth()-10, startYPosition = 100;
        for(int i=0;i<infoColaborador.length;i++){
            canvas.drawText(infoColaborador[i], startXPosition, startYPosition, myPaint);
            canvas.drawText(coletarInformacoes.getString(chaves[i]), startXPosition+80, startYPosition, myPaint);
            canvas.drawLine(startXPosition, startYPosition+3, endXPosition, startYPosition+3, myPaint);
            startYPosition += 10;
        }

        canvas.drawLine(80,92,80,190,myPaint);

        pdfRelatorio.finishPage(pagRelatorio);

        String nomeArquivo = "OSManutencao_" + coletarInformacoes.getString("formID") + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nomeArquivo);

        try {
            pdfRelatorio.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(), "arquivo gerado", Toast.LENGTH_SHORT).show();
            CompartilharRelatorio(file);
        } catch (IOException e){
            e.printStackTrace();
        }

        pdfRelatorio.close();
    }


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
