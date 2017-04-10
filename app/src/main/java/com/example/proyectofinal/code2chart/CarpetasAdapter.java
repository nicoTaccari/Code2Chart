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
import java.util.List;

/**
 * Created by nico on 10/04/17.
 */

public class CarpetasAdapter extends BaseAdapter implements Filterable {
    private List<CarpetaOArchivo> originalData = null;
    private List<CarpetaOArchivo> filteredData = null;
    private LayoutInflater mInflater;
    private CarpetaOArchivoFilter item = new CarpetaOArchivoFilter();

    public CarpetasAdapter(Context unContexto, List<CarpetaOArchivo> datos){
        this.filteredData = datos;
        this.originalData = datos;
        mInflater = LayoutInflater.from(unContexto);
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int posicion, View convertView, ViewGroup parent){

        CarpetasHolder holder = null;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.item_folder,null);

            holder = new CarpetasHolder();
            holder.imagenItem = (ImageView) convertView.findViewById(R.id.icono);
            holder.textoItem = (TextView) convertView.findViewById(R.id.texto);
            convertView.setTag(holder);
        }else{
            holder = (CarpetasHolder)convertView.getTag();
        }

        holder.textoItem.setText(filteredData.get(posicion).getTitulo());
        holder.imagenItem.setImageResource(filteredData.get(posicion).getIcono());

        return convertView;
    }

    static class CarpetasHolder {
        ImageView imagenItem;
        TextView textoItem;
    }

    @Override
    public Filter getFilter() {
        return item;
    }

    private class CarpetaOArchivoFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<CarpetaOArchivo> list = originalData;
            int count = list.size();
            final ArrayList<CarpetaOArchivo> nlist = new ArrayList<CarpetaOArchivo>();
            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getTitulo();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<CarpetaOArchivo>) results.values;
            notifyDataSetChanged();
        }
    }
}