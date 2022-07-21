package com.example.docshare;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public interface Formulario {

    default void GerarPDF(Bundle info){
        int pageHeight = 1120;
        int pageWidth = 792;
        //Bitmap bmp, scaledbmp;

        PdfDocument pdfFormulario = new PdfDocument();

        Paint paint = new Paint();
        Paint title = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfFormulario.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();
        //canvas.drawBitmap(scaledbmp, 56, 40, paint);

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(212121);     // <------- Voltar aqui

        canvas.drawText("Ordem de Serviço", 209, 100, title);
        canvas.drawText("Manutenção Corretiva", 209, 80, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(212121);     // <------- Voltar aqui
        title.setTextSize(15);

        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Teste de formatação de dados em arquivo pdf.", 396, 560, title);

        pdfFormulario.finishPage(myPage);

        // Criar/Nomear arquivo
        File file = new File(Environment.getExternalStorageDirectory(), "Teste_Formulario.pdf");

        try {
            pdfFormulario.writeTo(new FileOutputStream(file));
            //Toast.makeText(this.getClass(), "Arquivo gerado com sucesso", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfFormulario.close();
    }
}
