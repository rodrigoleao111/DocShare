package com.example.docshare.outros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.docshare.R;


public class HistoricoAdapter extends BaseAdapter {

    Context context;
    String titulos[];
    String datas[];
    LayoutInflater inflater;

    public HistoricoAdapter(Context c, String [] titulos, String[] datas) {
        this.context = c;
        this.titulos = titulos;
        this.datas = datas;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return titulos.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.line_listview_custom,null);
        TextView textView = convertView.findViewById(R.id.tituloHistorico);
        TextView textView2 = convertView.findViewById(R.id.txtData);
        textView.setText(titulos[position]);
        textView2.setText(datas[position]);
        return convertView;
    }
}
