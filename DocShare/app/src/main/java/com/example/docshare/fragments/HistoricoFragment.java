package com.example.docshare.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.docshare.adapters.HistoricoAdapter;
import com.example.docshare.R;
import com.example.docshare.metodos.RequestPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoricoFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<File> pdfList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historico, container, false);
        
        permissoes();
        recyclerView = view.findViewById(R.id.recyclerView);
        HistoricoAdapter adapter = new HistoricoAdapter(getContext(), pdfList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration( new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void permissoes() {
        if(RequestPermissions.checkPermission(getActivity())){
            displayPdf();
        } else {
            RequestPermissions.requestPermission(getActivity());
        }
    }

    public ArrayList<File> findPdf (File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(findPdf(singleFile));
            } else {
                if (singleFile.getName().endsWith(".pdf")){
                    arrayList.add(singleFile);
                }
            }
        } return arrayList;
    }


    private void displayPdf() {

        pdfList = new ArrayList<>();
        pdfList.addAll(findPdf(Environment.getExternalStorageDirectory()));
    }

}