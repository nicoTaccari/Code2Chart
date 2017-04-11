package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CarpetasAdapter extends BaseAdapter implements Filterable {

    private ArrayList<CarpetaOArchivo> misDatos;
    private ArrayList<CarpetaOArchivo> filterListMisDatos;
    private CarpetaOArchivoFilter filter;
    private Context miContexto;

    public CarpetasAdapter(Context unContexto, ArrayList<CarpetaOArchivo> datos){
        this.miContexto = unContexto;
        this.misDatos = datos;
        this.filterListMisDatos = datos;
    }

    @Override
    public int getCount() {
        return misDatos.size();
    }

    @Override
    public Object getItem(int position) {
        return misDatos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return misDatos.indexOf(getItem(position));
    }

    public View getView(int posicion, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) miContexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_folder,null);
        }

        TextView titulo = (TextView) convertView.findViewById(R.id.texto);
        ImageView icono = (ImageView) convertView.findViewById(R.id.icono);

        //SET DATA
        titulo.setText(misDatos.get(posicion).getTitulo());
        icono.setImageResource(misDatos.get(posicion).getIcono());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new CarpetaOArchivoFilter();
        }
        return filter;
    }

    class CarpetaOArchivoFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();
                ArrayList<CarpetaOArchivo> filters = new ArrayList<CarpetaOArchivo>();

                for (int i = 0; i < filterListMisDatos.size(); i++) {
                    if (filterListMisDatos.get(i).getTitulo().toUpperCase().contains(constraint)) {
                        CarpetaOArchivo file = new CarpetaOArchivo(filterListMisDatos.get(i).getTitulo(),filterListMisDatos.get(i).getIcono());
                        filters.add(file);
                    }
                }

                results.count = filters.size();
                results.values = filters;
            }else{
                results.count = filterListMisDatos.size();
                results.values = filterListMisDatos;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            misDatos = (ArrayList<CarpetaOArchivo>) results.values;
            notifyDataSetChanged();
        }
    }
}