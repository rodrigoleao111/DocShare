package com.example.docshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.MyviewHolder> {

    private Context context;
    private List<File> pdfFiles;

    public HistoricoAdapter(Context context, List<File> pdfFiles) {
        this.context = context;
        this.pdfFiles = pdfFiles;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_listview_custom, parent, false);
        return new MyviewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        holder.txtTitulo.setText(pdfFiles.get(position).getName());
        holder.txtTitulo.setSelected(true);
    }

    @Override
    public int getItemCount() {return pdfFiles.size();}

    public static class MyviewHolder extends RecyclerView.ViewHolder{
            TextView txtTitulo;
            ConstraintLayout conteiner;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitulo = itemView.findViewById(R.id.tituloHistorico);
            conteiner = itemView.findViewById(R.id.line_historico);
        }
    }
}
