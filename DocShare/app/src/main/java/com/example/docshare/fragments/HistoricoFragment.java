package com.example.docshare.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.docshare.R;

public class HistoricoFragment extends Fragment {

    private ListView lista;
    private String[] itens = {"OS Exemplo 1","OS Exemplo 2","OS Exemplo 3","OS Exemplo 4","OS Exemplo 5",
            "OS Exemplo 6","OS Exemplo 7","OS Exemplo 8","OS Exemplo 9","OS Exemplo 10","OS Exemplo 11",
    };

    private String[] datas = {"Hoje","Ontem","Segunda","26/08/22","25/08/22", "24/08/22","10/08/22",
            "08/08/22","01/08/22","31/07/22","28/07/22",
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_historico, container, false);

       lista = view.findViewById(R.id.listView);

       //Criar adaptador para a lista
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_2, android.R.id.text1, itens);

        //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
               // android.R.layout.simple_list_item_2, android.R.id.text2, datas);

        //Adiciona adaptador para a lista
        lista.setAdapter(adapter);
        //lista.setAdapter(adapter2);

        //Adicionando clique na lista
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String valorSelecionado = lista.getItemAtPosition(position).toString();
            }
        });


        return view;
    }
}