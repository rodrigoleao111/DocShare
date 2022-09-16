package com.example.docshare.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docshare.R;
import com.example.docshare.metodos.ImageHelper;
import com.example.docshare.usuario.ConfiguracoesDeUsuario;
import com.example.docshare.usuario.FormLogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;


public class ConfiguracoesFragment extends Fragment {

    private Button buttonSair;
    private TextView mudarSenha, editarPerfil, sobre;   // Botões
    private TextView nome, cargo, email, telefone;
    private ImageView profilePic;
    Bundle paths = new Bundle();
    FirebaseFirestore db_dados_usuario = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracoes, container, false);
        IniciarComponentes(view);


        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToUserConfig = new Intent( getContext(), ConfiguracoesDeUsuario.class);
                goToUserConfig.putExtras(paths);
                startActivity(goToUserConfig);
            }
        });

        buttonSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Você será desconectado");
                dialog.setMessage("Deseja continuar?");
                dialog.setCancelable(false);

                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent back_to_login = new Intent(getContext(), FormLogin.class);
                        Toast.makeText(getContext(), "Usuário deslogado", Toast.LENGTH_LONG).show();
                        startActivity(back_to_login);
                    }
                });

                dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                dialog.create();
                dialog.show();

            }
        });

        mudarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogSenha = new AlertDialog.Builder(getContext());
                View viewMudarSenha = inflater.inflate(R.layout.dialog_mudar_senha, null);
                dialogSenha.setView(viewMudarSenha);
                dialogSenha.create();
                dialogSenha.show();


            }
        });

        return view;
    }

    private void IniciarComponentes(View view) {
        buttonSair = view.findViewById(R.id.buttonSair);
        editarPerfil = view.findViewById(R.id.EditarPerfil);
        mudarSenha = view.findViewById(R.id.MudarSenha);
        sobre = view.findViewById(R.id.Sobre);
        nome = view.findViewById(R.id.textView6);
        cargo = view.findViewById(R.id.textView7);
        email = view.findViewById(R.id.textView);
        telefone = view.findViewById(R.id.textView4);
        profilePic = view.findViewById(R.id.imageView2);
    }

    @Override
    public void onStart() {
        super.onStart();

        DocumentReference documentReference = db_dados_usuario.collection("Usuarios").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nome.setText(documentSnapshot.getString("nome"));
                    cargo.setText(documentSnapshot.getString("cargo"));
                    telefone.setText(documentSnapshot.getString("telefone"));
                    email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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