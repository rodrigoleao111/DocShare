package com.example.docshare.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docshare.R;
import com.example.docshare.formularios.FormOSManutencaoCorretiva;
import com.example.docshare.metodos.ImageHelper;
import com.example.docshare.metodos.RequestPermissions;
import com.example.docshare.metodos.UserInfo;
import com.example.docshare.usuario.TelaDeUsuario;
import com.example.docshare.usuario.TelaUsuario2Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.util.Objects;


public class InicioFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private TextView boasvindas;
    private ImageView profilePic;
    private Button button_novaOS;
    private TextView textVerTodas;
    Bundle paths = new Bundle();
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), ola;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        IniciarComponentes(view);

        // CRIAÇÃO DE PASTAS
        if (RequestPermissions.checkPermission(getContext())) {
            String folderName = "DocShare";
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName);
            if(!folder.exists()){
                if(folder.mkdir())
                    paths.putString("rootDir", folder.getAbsolutePath());
                Toast.makeText(getContext(), "sucesso ao criar pasta", Toast.LENGTH_SHORT).show();
                CriarPastasDoApp(folder);
            } else {
                //Toast.makeText(getApplicationContext(), folder.getAbsolutePath(), Toast.LENGTH_LONG).show();
                paths.putString("rootDir", folder.getAbsolutePath());
                //paths.putString("userDir", userFolder.getAbsolutePath());
                //paths.putString("osDir", osFolder.getAbsolutePath());
                //paths.putString("imagesDir", imagesFolder.getAbsolutePath());
            }
        } else {
            RequestPermissions.requestPermission(getActivity());
        }



        button_novaOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RequestPermissions.checkPermission(getContext())) {
                    Intent goToFormOsActivity = new Intent(getContext(), FormOSManutencaoCorretiva.class);
                    goToFormOsActivity.putExtras(paths);
                    startActivity(goToFormOsActivity);
                } else {
                    RequestPermissions.requestPermission(getActivity());
                }
            }
        });


        return view;
    }


    private void IniciarComponentes(View view) {
        boasvindas = view.findViewById(R.id.txt_boas_vindas2);
        profilePic = view.findViewById(R.id.profilePicInit);
        button_novaOS = view.findViewById(R.id.button_novaOS);
        textVerTodas = view.findViewById(R.id.textVerTodas);
    }

    @Override
    public void onStart() {
        super.onStart();

        DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);

        // Texto de Boas Vindas
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    String[] nomeUser = documentSnapshot.getString("nome").split("\\s");
                    ola = "Olá, " + nomeUser[0];
                    boasvindas.setText(ola);
                    if(!Objects.equals(documentSnapshot.getString("profilePicUri"), "void")) {
                        Bitmap profilePicBitmap = BitmapFactory.decodeFile(documentSnapshot.getString("profilePicUri"));
                        if(profilePicBitmap !=null)
                            profilePic.setImageBitmap(ImageHelper.getRoundedCornerBitmap(profilePicBitmap, profilePicBitmap.getHeight()));
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(getContext(), "Permission Granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Permission Denined.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void CriarPastasDoApp(File folder) {
        String folderUserName = userID, folderImages = "DocShare_images", folderOS = "DocShare_os_files";
        File userFolder = new File(folder, folderUserName);
        if(!userFolder.exists()){
            userFolder.mkdir();
            paths.putString("userDir", userFolder.getAbsolutePath());
            File imagesFolder = new File(userFolder, folderImages);
            File osFolder = new File(userFolder, folderOS);
            if(!imagesFolder.exists()&&!osFolder.exists()){
                imagesFolder.mkdir();
                paths.putString("imagesDir", imagesFolder.getAbsolutePath());
                if(osFolder.mkdir()) {
                    paths.putString("osDir", osFolder.getAbsolutePath());
                    Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(),"Folder Already Exists",Toast.LENGTH_SHORT).show();
        }
    }

}

