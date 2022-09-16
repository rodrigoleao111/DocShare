package com.example.docshare.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.docshare.R;
import com.example.docshare.formularios.FormOSManutencaoCorretiva;
import com.example.docshare.metodos.ImageHelper;
import com.example.docshare.metodos.RequestPermissions;
import com.example.docshare.metodos.UserInfo;
import com.example.docshare.usuario.TelaUsuario2Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;


public class InicioFragment extends Fragment {

    private TextView boasvindas;
    private ImageView profilePic;
    private Button button_novaOS;
    private TextView textVerTodas;
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), ola;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        IniciarComponentes(view);

        button_novaOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfo.CriarDiretoriosDoApp(RequestPermissions.checkPermission(getContext()))) {
                    Intent goToFormOsActivity = new Intent(getContext(), FormOSManutencaoCorretiva.class);
                    startActivity(goToFormOsActivity);
                } else {
                    Activity activity = getActivity();
                    RequestPermissions.requestPermission(activity);
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
}

