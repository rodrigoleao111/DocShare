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

import com.edmodo.cropper.CropImageView;
import com.example.docshare.formularios.FormCadastro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CropImage extends AppCompatActivity {

    private Button btCrop;
    private CropImageView mCropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        getSupportActionBar().hide();
        IniciarComponentes();

        Intent intentReceberForm = getIntent();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), intentReceberForm.getParcelableExtra("uri"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCropImageView.setImageBitmap(bitmap);

        // Enviar imagem cortada
        btCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criar nova imagem
                Bitmap auxBitmap = Bitmap.createScaledBitmap(mCropImageView.getCroppedImage(),
                        mCropImageView.getCroppedImage().getWidth(),
                        mCropImageView.getCroppedImage().getHeight(),
                        true);

                // Convert to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                auxBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Voltar para activity anterior
                Intent goBack = new Intent(getApplicationContext(), FormCadastro.class);
                goBack.putExtra("image",byteArray);
                startActivity(goBack);
            }
        });
    }

    private void IniciarComponentes() {
        mCropImageView = findViewById(R.id.mCropImageView);
        btCrop = findViewById(R.id.btCrop);
    }
}