package com.example.docshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.example.docshare.formularios.FormCadastro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CropImage extends AppCompatActivity {

    private TextView btCrop, btCancel;
    private ImageView btRotate;
    private CropImageView mCropImageView;
    float rotation = 0.0f;
    Matrix matrix = new Matrix();
    Bitmap bitmap, finalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        getSupportActionBar().hide();
        IniciarComponentes();

        // Recebendo e atribuindo uri da imagem selecionada
        Intent intentReceberForm = getIntent();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                    intentReceberForm.getParcelableExtra("uri"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Inicialização da imagem
        mCropImageView.setImageBitmap(bitmap);
        mCropImageView.setRotation(rotation);

        // Rotacionar imagem
        btRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotation += 90.0f;
                mCropImageView.setRotation(rotation);

            }
        });

        // Enviar imagem cortada
        btCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criar nova imagem
                matrix.setRotate(mCropImageView.getRotation());
                finalBitmap = Bitmap.createScaledBitmap(
                        mCropImageView.getCroppedImage(),
                        mCropImageView.getCroppedImage().getWidth(),
                        mCropImageView.getCroppedImage().getHeight(),
                        false);

                finalBitmap = Bitmap.createBitmap(finalBitmap, 0, 0, finalBitmap.getWidth(),
                        finalBitmap.getHeight(), matrix, false);

                // Convert to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Voltar para activity anterior
                Intent goBack = new Intent(getApplicationContext(), FormCadastro.class);
                goBack.putExtra("image",byteArray);
                startActivity(goBack);
            }
        });

        // Cancelar processo
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void IniciarComponentes() {
        mCropImageView = findViewById(R.id.mCropImageView);
        btCrop = findViewById(R.id.btCrop);
        btCancel = findViewById(R.id.btCancel);
        btRotate = findViewById(R.id.btRotate);
    }
}