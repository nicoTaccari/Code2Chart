package com.example.proyectofinal.code2chart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class ArchivosAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Archivo> misDatos;
    private ArrayList<Archivo> filterListMisDatos;
    private ArchivoFilter filter;
    private Context miContexto;

    public ArchivosAdapter(Context unContexto, ArrayList<Archivo> archivos){
        this.miContexto = unContexto;
        this.misDatos = archivos;
        this.filterListMisDatos = archivos;
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

    /*--------------------------------------------------------------------------------------*/
    public View getView(int posicion, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) miContexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_folder,null);
        }

        TextView titulo = (TextView) convertView.findViewById(R.id.titulo);
        TextView autor = (TextView) convertView.findViewById(R.id.autor);

        //SET DATA
        titulo.setText(misDatos.get(posicion).getTitulo());
        autor.setText(misDatos.get(posicion).getAutor());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new ArchivoFilter();
        }
        return filter;
    }

    class ArchivoFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();
                ArrayList<Archivo> filters = new ArrayList<>();

                //TODO aplicar collections para un codigo mas limpio
                for (Archivo arch: filterListMisDatos){
                    if (arch.getTitulo().toUpperCase().contains(constraint) && arch.getClass().isAssignableFrom(Archivo.class)){
                        Archivo archivo = new Archivo(arch.getTitulo(), arch.getAutor());
                        filters.add(archivo);
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
            misDatos = (ArrayList<Archivo>) results.values;
            notifyDataSetChanged();
        }
    }
}