package com.example.docshare.metodos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.docshare.formularios.FormCadastro;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public interface ImageHelper {

    /***
     * Arredondar cantos de bitmap
     * @param bitmap - imagem a ser modificada
     * @param pixels - raio do arredondamento
     * @return - bitmap de imagem arredondada
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /***
     * Transformar bitmap em file e retornar path
     * @param bitmap - imagem recebida
     * @return - String path file criado
     */
    public static String bitmapToUri(Bitmap bitmap, Context context, String filename) throws IOException {

        boolean finish = false;
        StringBuilder filenameBuilder = new StringBuilder(filename);
        File file = null;

        while (!finish) {
            file = new File(context.getCacheDir(), filename);

            if (file.createNewFile()) {
                // Atribuir bitmap ao file
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                finish = true;
            } else {
                // Modificar o filename
                Random r = new Random();
                char c = (char)(r.nextInt(26) + 'a');
                filenameBuilder.insert(0, c);
                filename = filenameBuilder.toString();
            }
        }


        return file.getAbsolutePath();
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bitmap.recycle();
        return bos.toByteArray();
    }
}
