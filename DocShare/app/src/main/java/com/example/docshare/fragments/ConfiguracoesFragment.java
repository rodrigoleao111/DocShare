package com.example.docshare.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docshare.R;
import com.example.docshare.usuario.ConfiguracoesDeUsuario;
import com.example.docshare.usuario.FormLogin;
import com.google.firebase.auth.FirebaseAuth;


public class ConfiguracoesFragment extends Fragment {

    private Button buttonSair;
    private TextView mudarSenha, editarPerfil, sobre;
    Bundle paths = new Bundle();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuracoes, container, false);
        buttonSair = view.findViewById(R.id.buttonSair);

        editarPerfil = view.findViewById(R.id.EditarPerfil);
        mudarSenha = view.findViewById(R.id.MudarSenha);
        sobre = view.findViewById(R.id.Sobre);

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

        return view;
    }

}