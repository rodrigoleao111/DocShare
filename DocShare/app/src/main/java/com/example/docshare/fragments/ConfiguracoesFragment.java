package com.example.docshare.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docshare.R;
import com.example.docshare.usuario.FormLogin;
import com.google.firebase.auth.FirebaseAuth;


public class ConfiguracoesFragment extends Fragment {

    private Button buttonSair;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuracoes, container, false);
        buttonSair = view.findViewById(R.id.buttonSair);
        buttonSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent back_to_login = new Intent(getContext(), FormLogin.class);
                Toast.makeText(getContext(), "Usuário deslogado", Toast.LENGTH_LONG).show();
                startActivity(back_to_login);
            }
        });

        return view;
    }

}