package com.example.docshare.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

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

import java.util.Objects;


public class InicioFragment extends Fragment {

    private Bundle userCredentials = UserInfo.getUserCredentials();
    private TextView boasvindas;
    private ImageView profilePic;
    private Button bt_novaOSManutencao, button_novaOS;
    private TextView textVerTodas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        IniciarComponentes(view);
        AtribuirCredenciais();

        bt_novaOSManutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfo.CriarDiretoriosDoApp(RequestPermissions.checkPermission(getContext()))) {
                    Intent goToFormOsActivity = new Intent(getContext(), FormOSManutencaoCorretiva.class);
                    startActivity(goToFormOsActivity);
                } else {
                    Activity activity = new Activity();
                    RequestPermissions.requestPermission(activity);
                }
            }
        });


        button_novaOS = view.findViewById(R.id.button_novaOS);
        textVerTodas = view.findViewById(R.id.textVerTodas);

        button_novaOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FormOSManutencaoCorretiva.class);
                startActivity(intent);
            }
        });


        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }


    private void AtribuirCredenciais() {
        // Mensagem boas-vindas
        String[] nomeUser = userCredentials.getString("nome").split("\\s");
        String ola = "Olá, " + nomeUser[0];
        boasvindas.setText(ola);

        // Imagem de perfil
        if(!Objects.equals(userCredentials.getString("profilePicUri"), "void")) {
            Bitmap profilePicBitmap = BitmapFactory.decodeFile(userCredentials.getString("profilePicUri"));
            if (profilePicBitmap != null)
                profilePic.setImageBitmap(ImageHelper.getRoundedCornerBitmap(profilePicBitmap, 10000));
        }

    }

    private void IniciarComponentes(View view) {
        boasvindas = view.findViewById(R.id.txt_boas_vindas2);
        profilePic = view.findViewById(R.id.profilePicInit);
        //bt_novaOSManutencao = view.findViewById(R.id.bt_nova_os_manutencao);
    }







}

