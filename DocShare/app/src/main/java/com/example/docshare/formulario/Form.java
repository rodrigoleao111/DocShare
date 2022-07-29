package com.example.docshare.formulario;

import android.content.Intent;
import android.graphics.Canvas;
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


        PdfDocument pdfRelatorio = new PdfDocument();
        Paint myPaint = new Paint();

        PdfDocument.PageInfo infoRelatorio = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
        PdfDocument.Page pagRelatorio = pdfRelatorio.startPage(infoRelatorio);

        Canvas canvas = pagRelatorio.getCanvas();
        canvas.drawText("OS Manutenção", 40, 50, myPaint);
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

}
