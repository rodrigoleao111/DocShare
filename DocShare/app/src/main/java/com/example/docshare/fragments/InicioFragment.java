package com.example.docshare.fragments;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.docshare.R;
import com.example.docshare.formularios.FormOSManutencaoCorretiva;


public class InicioFragment extends Fragment {

    private TextView textVerTodas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        Button button_novaOS = view.findViewById(R.id.button_novaOS);
        textVerTodas = view.findViewById(R.id.textVerTodas);

        button_novaOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FormOSManutencaoCorretiva.class);
                startActivity(intent);
            }
        });


        return view;


    }

}
